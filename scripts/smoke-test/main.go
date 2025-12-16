package main

import (
	"context"
	"fmt"
	"os"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
)

const (
	colorReset  = "\033[0m"
	colorRed    = "\033[31m"
	colorGreen  = "\033[32m"
	colorYellow = "\033[33m"
	colorBlue   = "\033[34m"
	colorCyan   = "\033[36m"
)

type TestResult struct {
	Name    string
	Passed  bool
	Warning bool
	Message string
}

type TestSuite struct {
	clientset     *kubernetes.Clientset
	dynamicClient dynamic.Interface
	results       []TestResult
	passed        int
	failed        int
	warnings      int
}

func main() {
	printBanner()

	suite, err := NewTestSuite()
	if err != nil {
		fmt.Printf("%s✗ Failed to initialize: %v%s\n", colorRed, err, colorReset)
		os.Exit(1)
	}

	suite.RunAll()
	suite.PrintSummary()

	if suite.failed > 0 {
		os.Exit(1)
	}
}

func printBanner() {
	fmt.Printf("%s", colorCyan)
	fmt.Println("╔═══════════════════════════════════════════════════════════════╗")
	fmt.Println("║           EKS SMOKE TEST - Infrastructure Validation          ║")
	fmt.Println("╚═══════════════════════════════════════════════════════════════╝")
	fmt.Printf("%s\n", colorReset)
}

func NewTestSuite() (*TestSuite, error) {
	loadingRules := clientcmd.NewDefaultClientConfigLoadingRules()
	configOverrides := &clientcmd.ConfigOverrides{}
	kubeConfig := clientcmd.NewNonInteractiveDeferredLoadingClientConfig(loadingRules, configOverrides)

	config, err := kubeConfig.ClientConfig()
	if err != nil {
		return nil, fmt.Errorf("failed to load kubeconfig: %w", err)
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("failed to create clientset: %w", err)
	}

	dynamicClient, err := dynamic.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("failed to create dynamic client: %w", err)
	}

	return &TestSuite{
		clientset:     clientset,
		dynamicClient: dynamicClient,
	}, nil
}

func (s *TestSuite) RunAll() {
	s.testEKSCluster()
	s.testCoreAddons()
	s.testSecurity()
	s.testNetworking()
	s.testBackupAndRecovery()
	s.testResourceOptimization()
	s.testObservability()
}

func (s *TestSuite) printHeader(title string) {
	fmt.Printf("\n%s━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%s\n", colorBlue, colorReset)
	fmt.Printf("%s  %s%s\n", colorBlue, title, colorReset)
	fmt.Printf("%s━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%s\n", colorBlue, colorReset)
}

func (s *TestSuite) printSection(title string) {
	fmt.Printf("\n%s▶ %s%s\n", colorYellow, title, colorReset)
}

func (s *TestSuite) pass(name string) {
	fmt.Printf("  %s✓%s %s\n", colorGreen, colorReset, name)
	s.passed++
	s.results = append(s.results, TestResult{Name: name, Passed: true})
}

func (s *TestSuite) fail(name string) {
	fmt.Printf("  %s✗%s %s\n", colorRed, colorReset, name)
	s.failed++
	s.results = append(s.results, TestResult{Name: name, Passed: false})
}

func (s *TestSuite) warn(name string) {
	fmt.Printf("  %s⚠%s %s\n", colorYellow, colorReset, name)
	s.warnings++
	s.results = append(s.results, TestResult{Name: name, Warning: true})
}

// -----------------------------------------------------------------------------
// EKS Cluster Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testEKSCluster() {
	s.printHeader("EKS CLUSTER HEALTH")
	ctx := context.Background()

	s.printSection("Cluster Connectivity")
	version, err := s.clientset.Discovery().ServerVersion()
	if err != nil {
		s.fail("Cluster connectivity")
	} else {
		s.pass(fmt.Sprintf("Cluster connectivity (Kubernetes %s)", version.GitVersion))
	}

	s.printSection("Node Health")
	nodes, err := s.clientset.CoreV1().Nodes().List(ctx, metav1.ListOptions{})
	if err != nil {
		s.fail("List nodes")
	} else {
		ready := 0
		notReady := 0
		for _, node := range nodes.Items {
			for _, cond := range node.Status.Conditions {
				if cond.Type == "Ready" {
					if cond.Status == "True" {
						ready++
					} else {
						notReady++
					}
				}
			}
		}
		s.pass(fmt.Sprintf("Nodes ready: %d", ready))
		if notReady > 0 {
			s.warn(fmt.Sprintf("Nodes not ready: %d", notReady))
		}
	}

	s.printSection("System Pods")
	s.checkPodsRunning(ctx, "kube-system", "k8s-app=kube-dns", "CoreDNS")
	s.checkPodsRunning(ctx, "kube-system", "k8s-app=kube-proxy", "kube-proxy")
	s.checkPodsRunning(ctx, "kube-system", "k8s-app=aws-node", "AWS VPC CNI")

	s.printSection("Karpenter")
	s.checkPodsRunning(ctx, "kube-system", "app.kubernetes.io/name=karpenter", "Karpenter controller")
	s.checkCRDExists(ctx, "nodepools.karpenter.sh", "NodePool CRD")
	s.checkCRDExists(ctx, "ec2nodeclasses.karpenter.k8s.aws", "EC2NodeClass CRD")

	// Check NodePools
	nodePoolGVR := schema.GroupVersionResource{Group: "karpenter.sh", Version: "v1", Resource: "nodepools"}
	nodePools, err := s.dynamicClient.Resource(nodePoolGVR).List(ctx, metav1.ListOptions{})
	if err == nil && len(nodePools.Items) > 0 {
		s.pass(fmt.Sprintf("NodePools configured: %d", len(nodePools.Items)))
	} else {
		s.warn("No NodePools configured")
	}

	// Check EC2NodeClasses
	ec2NodeClassGVR := schema.GroupVersionResource{Group: "karpenter.k8s.aws", Version: "v1", Resource: "ec2nodeclasses"}
	ec2NodeClasses, err := s.dynamicClient.Resource(ec2NodeClassGVR).List(ctx, metav1.ListOptions{})
	if err == nil && len(ec2NodeClasses.Items) > 0 {
		s.pass(fmt.Sprintf("EC2NodeClasses configured: %d", len(ec2NodeClasses.Items)))
	} else {
		s.warn("No EC2NodeClasses configured")
	}

	s.printSection("Node Termination Handler")
	s.checkPodsRunning(ctx, "kube-system", "app.kubernetes.io/name=aws-node-termination-handler", "Node Termination Handler")
}

// -----------------------------------------------------------------------------
// Core Addons Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testCoreAddons() {
	s.printHeader("CORE ADDONS")
	ctx := context.Background()

	s.printSection("CRDs Installed")
	crds := []struct{ name, display string }{
		{"externalsecrets.external-secrets.io", "External Secrets"},
		{"clustersecretstores.external-secrets.io", "ClusterSecretStore"},
		{"certificates.cert-manager.io", "Cert Manager Certificates"},
		{"issuers.cert-manager.io", "Cert Manager Issuers"},
		{"clusterissuers.cert-manager.io", "Cert Manager ClusterIssuers"},
		{"clusterpolicies.kyverno.io", "Kyverno ClusterPolicies"},
		{"nodepools.karpenter.sh", "Karpenter NodePools"},
		{"ec2nodeclasses.karpenter.k8s.aws", "Karpenter EC2NodeClasses"},
		{"backups.velero.io", "Velero Backups"},
		{"restores.velero.io", "Velero Restores"},
		{"schedules.velero.io", "Velero Schedules"},
	}
	for _, crd := range crds {
		s.checkCRDExists(ctx, crd.name, crd.display)
	}

	s.printSection("Addon Deployments")
	addons := []struct{ ns, label, name string }{
		{"cert-manager", "app.kubernetes.io/name=cert-manager", "Cert Manager"},
		{"cert-manager", "app.kubernetes.io/name=cainjector", "Cert Manager CA Injector"},
		{"cert-manager", "app.kubernetes.io/name=webhook", "Cert Manager Webhook"},
		{"external-secrets", "app.kubernetes.io/name=external-secrets", "External Secrets Operator"},
		{"external-secrets", "app.kubernetes.io/name=external-secrets-webhook", "External Secrets Webhook"},
		{"external-secrets", "app.kubernetes.io/name=external-secrets-cert-controller", "External Secrets Cert Controller"},
		{"kyverno", "app.kubernetes.io/component=admission-controller", "Kyverno Admission Controller"},
		{"aws-load-balancer", "app.kubernetes.io/name=aws-load-balancer-controller", "AWS Load Balancer Controller"},
		{"external-dns", "app.kubernetes.io/name=external-dns", "External DNS"},
		{"reloader", "app.kubernetes.io/name=reloader", "Reloader"},
		{"kube-system", "app.kubernetes.io/name=metrics-server", "Metrics Server"},
	}
	for _, addon := range addons {
		s.checkPodsRunning(ctx, addon.ns, addon.label, addon.name)
	}
}

// -----------------------------------------------------------------------------
// Security Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testSecurity() {
	s.printHeader("SECURITY CONFIGURATION")
	ctx := context.Background()

	s.printSection("Secrets Management")
	// Check ClusterSecretStore
	gvr := schema.GroupVersionResource{Group: "external-secrets.io", Version: "v1", Resource: "clustersecretstores"}
	_, err := s.dynamicClient.Resource(gvr).Get(ctx, "aws-secrets-manager", metav1.GetOptions{})
	if err != nil {
		// Try v1beta1
		gvr = schema.GroupVersionResource{Group: "external-secrets.io", Version: "v1beta1", Resource: "clustersecretstores"}
		_, err = s.dynamicClient.Resource(gvr).Get(ctx, "aws-secrets-manager", metav1.GetOptions{})
	}
	if err != nil {
		s.fail("ClusterSecretStore 'aws-secrets-manager'")
	} else {
		s.pass("ClusterSecretStore 'aws-secrets-manager'")
	}

	// Check ExternalSecrets sync status (v1 API)
	esGVR := schema.GroupVersionResource{Group: "external-secrets.io", Version: "v1", Resource: "externalsecrets"}
	esList, err := s.dynamicClient.Resource(esGVR).Namespace("").List(ctx, metav1.ListOptions{})
	if err == nil {
		synced := 0
		total := len(esList.Items)
		for _, es := range esList.Items {
			conditions, found, _ := unstructured.NestedSlice(es.Object, "status", "conditions")
			if found {
				for _, c := range conditions {
					cond := c.(map[string]interface{})
					if cond["type"] == "Ready" && cond["status"] == "True" {
						synced++
						break
					}
				}
			}
		}
		if total > 0 {
			if synced == total {
				s.pass(fmt.Sprintf("ExternalSecrets synced: %d/%d", synced, total))
			} else {
				s.warn(fmt.Sprintf("ExternalSecrets synced: %d/%d", synced, total))
			}
		} else {
			s.pass("No ExternalSecrets configured (OK)")
		}
	}

	s.printSection("Kyverno Policies")
	policyGVR := schema.GroupVersionResource{Group: "kyverno.io", Version: "v1", Resource: "clusterpolicies"}
	policies, err := s.dynamicClient.Resource(policyGVR).List(ctx, metav1.ListOptions{})
	if err != nil {
		s.warn("Could not list Kyverno policies")
	} else {
		ready := 0
		for _, p := range policies.Items {
			cond, found, _ := unstructured.NestedBool(p.Object, "status", "ready")
			if found && cond {
				ready++
			}
		}
		s.pass(fmt.Sprintf("Kyverno policies: %d total, %d ready", len(policies.Items), ready))
	}

	s.printSection("TLS/Certificates")
	s.checkPodsRunning(ctx, "cert-manager", "app.kubernetes.io/name=cert-manager", "Cert Manager")

	// Check for ClusterIssuers
	issuerGVR := schema.GroupVersionResource{Group: "cert-manager.io", Version: "v1", Resource: "clusterissuers"}
	issuers, err := s.dynamicClient.Resource(issuerGVR).List(ctx, metav1.ListOptions{})
	if err == nil && len(issuers.Items) > 0 {
		s.pass(fmt.Sprintf("ClusterIssuers configured: %d", len(issuers.Items)))
	} else {
		s.warn("No ClusterIssuers configured")
	}

	// Check for issued certificates
	certGVR := schema.GroupVersionResource{Group: "cert-manager.io", Version: "v1", Resource: "certificates"}
	certs, err := s.dynamicClient.Resource(certGVR).Namespace("").List(ctx, metav1.ListOptions{})
	if err == nil {
		ready := 0
		for _, cert := range certs.Items {
			conditions, found, _ := unstructured.NestedSlice(cert.Object, "status", "conditions")
			if found {
				for _, c := range conditions {
					cond := c.(map[string]interface{})
					if cond["type"] == "Ready" && cond["status"] == "True" {
						ready++
						break
					}
				}
			}
		}
		if len(certs.Items) > 0 {
			s.pass(fmt.Sprintf("Certificates: %d total, %d ready", len(certs.Items), ready))
		}
	}
}

// -----------------------------------------------------------------------------
// Networking Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testNetworking() {
	s.printHeader("NETWORKING")
	ctx := context.Background()

	s.printSection("AWS Load Balancer Controller")
	s.checkPodsRunning(ctx, "aws-load-balancer", "app.kubernetes.io/name=aws-load-balancer-controller", "AWS LB Controller")

	// Check for IngressClasses
	ingressClasses, err := s.clientset.NetworkingV1().IngressClasses().List(ctx, metav1.ListOptions{})
	if err == nil && len(ingressClasses.Items) > 0 {
		for _, ic := range ingressClasses.Items {
			s.pass(fmt.Sprintf("IngressClass: %s (controller: %s)", ic.Name, ic.Spec.Controller))
		}
	} else {
		s.warn("No IngressClasses found")
	}

	s.printSection("External DNS")
	s.checkPodsRunning(ctx, "external-dns", "app.kubernetes.io/name=external-dns", "External DNS")

	// Check service account has IRSA
	sa, err := s.clientset.CoreV1().ServiceAccounts("external-dns").Get(ctx, "external-dns", metav1.GetOptions{})
	if err == nil && sa.Annotations != nil {
		if roleArn, ok := sa.Annotations["eks.amazonaws.com/role-arn"]; ok && roleArn != "" {
			s.pass("External DNS has IRSA configured")
		} else {
			s.warn("External DNS missing IRSA annotation")
		}
	}

	s.printSection("Ingresses")
	ingresses, err := s.clientset.NetworkingV1().Ingresses("").List(ctx, metav1.ListOptions{})
	if err == nil {
		withLB := 0
		for _, ing := range ingresses.Items {
			if len(ing.Status.LoadBalancer.Ingress) > 0 {
				withLB++
			}
		}
		s.pass(fmt.Sprintf("Ingresses: %d total, %d with LoadBalancer", len(ingresses.Items), withLB))
	}

	s.printSection("Services")
	services, err := s.clientset.CoreV1().Services("").List(ctx, metav1.ListOptions{})
	if err == nil {
		lbCount := 0
		for _, svc := range services.Items {
			if svc.Spec.Type == "LoadBalancer" {
				lbCount++
			}
		}
		s.pass(fmt.Sprintf("LoadBalancer services: %d", lbCount))
	}
}

// -----------------------------------------------------------------------------
// Backup and Recovery Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testBackupAndRecovery() {
	s.printHeader("BACKUP AND RECOVERY")
	ctx := context.Background()

	s.printSection("Velero")
	s.checkPodsRunning(ctx, "velero", "app.kubernetes.io/name=velero", "Velero Server")

	// Check Velero service account has IRSA
	sa, err := s.clientset.CoreV1().ServiceAccounts("velero").Get(ctx, "velero", metav1.GetOptions{})
	if err == nil && sa.Annotations != nil {
		if roleArn, ok := sa.Annotations["eks.amazonaws.com/role-arn"]; ok && roleArn != "" {
			s.pass("Velero has IRSA configured")
		} else {
			s.warn("Velero missing IRSA annotation")
		}
	}

	// Check backup storage locations
	bslGVR := schema.GroupVersionResource{Group: "velero.io", Version: "v1", Resource: "backupstoragelocations"}
	bsls, err := s.dynamicClient.Resource(bslGVR).Namespace("velero").List(ctx, metav1.ListOptions{})
	if err == nil && len(bsls.Items) > 0 {
		available := 0
		for _, bsl := range bsls.Items {
			phase, found, _ := unstructured.NestedString(bsl.Object, "status", "phase")
			if found && phase == "Available" {
				available++
			}
		}
		s.pass(fmt.Sprintf("BackupStorageLocations: %d total, %d available", len(bsls.Items), available))
	} else {
		s.warn("No BackupStorageLocations configured")
	}

	// Check scheduled backups
	scheduleGVR := schema.GroupVersionResource{Group: "velero.io", Version: "v1", Resource: "schedules"}
	schedules, err := s.dynamicClient.Resource(scheduleGVR).Namespace("velero").List(ctx, metav1.ListOptions{})
	if err == nil {
		if len(schedules.Items) > 0 {
			s.pass(fmt.Sprintf("Backup schedules: %d", len(schedules.Items)))
		} else {
			s.warn("No backup schedules configured")
		}
	}

	// Check recent backups
	backupGVR := schema.GroupVersionResource{Group: "velero.io", Version: "v1", Resource: "backups"}
	backups, err := s.dynamicClient.Resource(backupGVR).Namespace("velero").List(ctx, metav1.ListOptions{})
	if err == nil && len(backups.Items) > 0 {
		completed := 0
		for _, backup := range backups.Items {
			phase, found, _ := unstructured.NestedString(backup.Object, "status", "phase")
			if found && phase == "Completed" {
				completed++
			}
		}
		s.pass(fmt.Sprintf("Backups: %d total, %d completed", len(backups.Items), completed))
	}
}

// -----------------------------------------------------------------------------
// Resource Optimization Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testResourceOptimization() {
	s.printHeader("RESOURCE OPTIMIZATION")
	ctx := context.Background()

	s.printSection("Goldilocks (VPA Recommendations)")
	s.checkPodsRunning(ctx, "goldilocks", "app.kubernetes.io/name=goldilocks", "Goldilocks Controller")

	// Check VPA CRD exists
	s.checkCRDExists(ctx, "verticalpodautoscalers.autoscaling.k8s.io", "VPA CRD")

	// Check for VPA resources
	vpaGVR := schema.GroupVersionResource{Group: "autoscaling.k8s.io", Version: "v1", Resource: "verticalpodautoscalers"}
	vpas, err := s.dynamicClient.Resource(vpaGVR).Namespace("").List(ctx, metav1.ListOptions{})
	if err == nil {
		if len(vpas.Items) > 0 {
			s.pass(fmt.Sprintf("VPAs configured: %d", len(vpas.Items)))
		} else {
			s.warn("No VPAs configured (Goldilocks may create them automatically)")
		}
	}

	// Check goldilocks dashboard service
	_, err = s.clientset.CoreV1().Services("goldilocks").Get(ctx, "goldilocks-dashboard", metav1.GetOptions{})
	if err == nil {
		s.pass("Goldilocks dashboard service exists")
	} else {
		s.warn("Goldilocks dashboard service not found")
	}

	s.printSection("Reloader (ConfigMap/Secret Watcher)")
	s.checkPodsRunning(ctx, "reloader", "app.kubernetes.io/name=reloader", "Reloader")
}

// -----------------------------------------------------------------------------
// Observability Tests
// -----------------------------------------------------------------------------

func (s *TestSuite) testObservability() {
	s.printHeader("OBSERVABILITY")
	ctx := context.Background()

	s.printSection("Metrics Server")
	s.checkPodsRunning(ctx, "kube-system", "app.kubernetes.io/name=metrics-server", "Metrics Server")

	// Check metrics API
	_, err := s.clientset.Discovery().ServerResourcesForGroupVersion("metrics.k8s.io/v1beta1")
	if err == nil {
		s.pass("Metrics API available")
	} else {
		s.warn("Metrics API not available")
	}

	// Test node metrics
	nodeMetricsGVR := schema.GroupVersionResource{Group: "metrics.k8s.io", Version: "v1beta1", Resource: "nodes"}
	nodeMetrics, err := s.dynamicClient.Resource(nodeMetricsGVR).List(ctx, metav1.ListOptions{})
	if err == nil && len(nodeMetrics.Items) > 0 {
		s.pass(fmt.Sprintf("Node metrics available: %d nodes", len(nodeMetrics.Items)))
	} else {
		s.warn("Node metrics not available")
	}

	s.printSection("Container Insights")
	// Check CloudWatch agent
	s.checkPodsRunning(ctx, "amazon-cloudwatch", "app.kubernetes.io/name=cloudwatch-agent", "CloudWatch Agent")

	// Check Fluent Bit (if used)
	pods, err := s.clientset.CoreV1().Pods("amazon-cloudwatch").List(ctx, metav1.ListOptions{
		LabelSelector: "app.kubernetes.io/name=fluent-bit",
	})
	if err == nil && len(pods.Items) > 0 {
		running := 0
		for _, pod := range pods.Items {
			if pod.Status.Phase == "Running" {
				running++
			}
		}
		if running > 0 {
			s.pass(fmt.Sprintf("Fluent Bit: %d running", running))
		}
	}

	s.printSection("Grafana Cloud Monitoring (k8s-monitoring)")
	s.checkPodsRunning(ctx, "monitoring", "app.kubernetes.io/name=alloy-logs", "Alloy Logs")
	s.checkPodsRunning(ctx, "monitoring", "app.kubernetes.io/name=alloy-metrics", "Alloy Metrics")
	s.checkPodsRunning(ctx, "monitoring", "app.kubernetes.io/name=alloy-singleton", "Alloy Singleton")
	s.checkPodsRunning(ctx, "monitoring", "app.kubernetes.io/name=kube-state-metrics", "Kube State Metrics")
	s.checkPodsRunning(ctx, "monitoring", "app.kubernetes.io/name=node-exporter", "Node Exporter")

	// Check monitoring namespace pods summary
	pods, err = s.clientset.CoreV1().Pods("monitoring").List(ctx, metav1.ListOptions{})
	if err == nil && len(pods.Items) > 0 {
		running := 0
		for _, pod := range pods.Items {
			if pod.Status.Phase == "Running" {
				running++
			}
		}
		if running > 0 {
			s.pass(fmt.Sprintf("Monitoring namespace pods: %d running", running))
		} else {
			s.warn("Monitoring pods: none running")
		}
	} else {
		s.warn("Monitoring namespace/pods not found")
	}

	// Check for Grafana ingress
	ingresses, err := s.clientset.NetworkingV1().Ingresses("monitoring").List(ctx, metav1.ListOptions{})
	if err == nil {
		for _, ing := range ingresses.Items {
			if len(ing.Spec.Rules) > 0 {
				host := ing.Spec.Rules[0].Host
				if host != "" {
					s.pass(fmt.Sprintf("Grafana ingress: %s", host))
				}
			}
		}
	}
}

// -----------------------------------------------------------------------------
// Helper Functions
// -----------------------------------------------------------------------------

func (s *TestSuite) checkPodsRunning(ctx context.Context, namespace, labelSelector, name string) {
	pods, err := s.clientset.CoreV1().Pods(namespace).List(ctx, metav1.ListOptions{
		LabelSelector: labelSelector,
	})
	if err != nil {
		s.fail(fmt.Sprintf("%s (error listing)", name))
		return
	}
	running := 0
	for _, pod := range pods.Items {
		if pod.Status.Phase == "Running" {
			running++
		}
	}
	if running > 0 {
		s.pass(fmt.Sprintf("%s: %d running", name, running))
	} else {
		s.fail(fmt.Sprintf("%s: no pods running", name))
	}
}

func (s *TestSuite) checkCRDExists(ctx context.Context, name, display string) {
	gvr := schema.GroupVersionResource{Group: "apiextensions.k8s.io", Version: "v1", Resource: "customresourcedefinitions"}
	_, err := s.dynamicClient.Resource(gvr).Get(ctx, name, metav1.GetOptions{})
	if err != nil {
		s.fail(display)
	} else {
		s.pass(display)
	}
}

func (s *TestSuite) PrintSummary() {
	s.printHeader("TEST SUMMARY")

	total := s.passed + s.failed + s.warnings
	fmt.Printf("\n  %s✓ Passed:%s   %d\n", colorGreen, colorReset, s.passed)
	fmt.Printf("  %s✗ Failed:%s   %d\n", colorRed, colorReset, s.failed)
	fmt.Printf("  %s⚠ Warnings:%s %d\n", colorYellow, colorReset, s.warnings)
	fmt.Printf("  ─────────────────\n")
	fmt.Printf("  Total:     %d\n", total)

	if s.failed == 0 {
		fmt.Printf("\n%s✓ All critical checks passed!%s\n\n", colorGreen, colorReset)
	} else {
		fmt.Printf("\n%s✗ Some checks failed. Review output above.%s\n\n", colorRed, colorReset)
	}
}
