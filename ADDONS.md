# Addons Reference

This document describes the Kubernetes addons deployed with aws-eks-infra.

## AWS Managed Addons

These addons are installed via EKS managed addon API.

| Addon | Version | Purpose |
|-------|---------|---------|
| vpc-cni | Latest | Pod networking with AWS VPC |
| coredns | Latest | Cluster DNS resolution |
| kube-proxy | Latest | Network proxy |
| aws-ebs-csi-driver | Latest | EBS persistent volumes |
| eks-pod-identity-agent | Latest | IAM roles for service accounts |

### VPC CNI

Provides native VPC networking for pods.

**Configuration**:
- ENI-based pod networking
- Security groups for pods
- Custom networking mode supported

### CoreDNS

Cluster DNS service for service discovery.

**Configuration**:
- Deployed on managed node group
- Horizontal pod autoscaling enabled

### EBS CSI Driver

Enables EBS volume provisioning for persistent storage.

**Configuration**:
- Default storage class: gp3
- Encryption enabled via KMS
- Volume expansion supported

### Pod Identity Agent

Enables IAM roles for Kubernetes service accounts.

**Configuration**:
- Replaces IRSA for new deployments
- Automatic token refresh
- Cross-account access supported

## Core Addons (CoreAddonsNestedStack)

These infrastructure-level addons are installed via Helm charts.

### cert-manager

**Purpose**: TLS certificate management

**Version**: Latest

**Configuration**:
```yaml
installCRDs: true
prometheus:
  enabled: true
```

**Usage**:
- Automatic certificate issuance
- Let's Encrypt integration
- Certificate rotation

### AWS Load Balancer Controller

**Purpose**: Manage AWS ALB/NLB for Kubernetes services

**Version**: Latest

**Configuration**:
```yaml
clusterName: <cluster-name>
serviceAccount:
  create: true
  annotations:
    eks.amazonaws.com/role-arn: <role-arn>
```

**Usage**:
- Ingress creates ALB
- Service type LoadBalancer creates NLB
- Target group binding

### Secrets Store CSI Driver

**Purpose**: Mount AWS Secrets Manager secrets as volumes

**Version**: Latest

**Configuration**:
```yaml
syncSecret:
  enabled: true
enableSecretRotation: true
```

**Usage**:
- SecretProviderClass resources
- Environment variables from secrets
- Automatic rotation support

### Karpenter

**Purpose**: Kubernetes node autoscaling

**Version**: Latest

**Configuration**:
```yaml
settings:
  clusterName: <cluster-name>
  interruptionQueue: <queue-name>
serviceAccount:
  annotations:
    eks.amazonaws.com/role-arn: <role-arn>
```

**Components**:
- **NodePool**: Defines instance requirements
- **EC2NodeClass**: Defines AMI, subnets, security groups

### Metrics Server

**Purpose**: Kubernetes metrics API for HPA/VPA

**Version**: Latest

**Configuration**:
```yaml
args:
  - --kubelet-insecure-tls
```

**Usage**:
- Required for `kubectl top` commands
- Enables Horizontal Pod Autoscaling
- Enables Vertical Pod Autoscaling
- Provides metrics for Karpenter decisions

### External DNS

**Purpose**: Auto-manage Route53 records from Ingress/Service

**Version**: Latest

**Configuration**:
```yaml
provider: aws
aws:
  region: <region>
txtOwnerId: <cluster-id>
policy: sync
serviceAccount:
  create: true
  annotations:
    eks.amazonaws.com/role-arn: <role-arn>
```

**Usage**:
- Automatically creates DNS records for Ingress resources
- Supports A and CNAME records
- Cleans up records when resources are deleted

### External Secrets Operator

**Purpose**: Sync AWS Secrets Manager to K8s Secrets

**Version**: Latest

**Configuration**:
```yaml
serviceAccount:
  create: true
  annotations:
    eks.amazonaws.com/role-arn: <role-arn>
```

**Usage**:
- Creates Kubernetes secrets from AWS Secrets Manager
- Supports scheduled sync (`refreshInterval`)
- Templating support for combining/transforming secrets
- Works with Reloader for auto-restart on secret changes

### Reloader

**Purpose**: Restart pods when ConfigMaps/Secrets change

**Version**: Latest

**Configuration**:
```yaml
reloader:
  watchGlobally: true
```

**Usage in Deployments**:
```yaml
metadata:
  annotations:
    reloader.stakater.com/auto: "true"
    # Or specific:
    # secret.reloader.stakater.com/reload: "my-secret"
    # configmap.reloader.stakater.com/reload: "my-config"
```

## Observability Addons (ObservabilityAddonsNestedStack)

These addons are installed for monitoring and observability.

### K8s Monitoring (Grafana)

**Purpose**: Full-stack Kubernetes monitoring

**Version**: Latest

**Configuration**:
```yaml
cluster:
  name: <cluster-name>
externalServices:
  prometheus:
    host: <grafana-cloud-host>
  loki:
    host: <grafana-cloud-host>
```

**Features**:
- Pre-built dashboards
- Kubernetes metrics
- Application logs
- Container insights
- Grafana Alloy telemetry collector (deployed as part of the chart)

## Addon Dependencies

```
EksNestedStack (base)
    │
    ├── ManagedAddonsConstruct (AWS managed addons)
    │
    └── NodeGroupsConstruct (worker nodes)

CoreAddonsNestedStack (depends on EksNestedStack)
    │
    ├── cert-manager
    ├── aws-secrets-store (CSI driver + AWS provider)
    ├── karpenter
    ├── aws-load-balancer-controller
    ├── metrics-server
    ├── external-dns
    ├── external-secrets
    └── reloader

ObservabilityAddonsNestedStack (depends on EksNestedStack)
    │
    └── grafana (k8s-monitoring with Alloy)
```

## Updating Addons

### AWS Managed Addons

```bash
# Check available versions
aws eks describe-addon-versions \
  --addon-name vpc-cni \
  --kubernetes-version 1.31

# Update addon
aws eks update-addon \
  --cluster-name <cluster-name> \
  --addon-name vpc-cni \
  --addon-version v1.x.x
```

### Helm Chart Addons

```bash
# Update Helm repo
helm repo update

# Check for updates
helm search repo cert-manager

# Upgrade chart
helm upgrade cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --version vX.Y.Z
```

## Troubleshooting

### Addon Pod Issues

```bash
# Check addon pods
kubectl get pods -n kube-system
kubectl get pods -n cert-manager
kubectl get pods -n karpenter
kubectl get pods -n external-dns
kubectl get pods -n external-secrets
kubectl get pods -n reloader

# Check logs
kubectl logs -n kube-system -l app.kubernetes.io/name=aws-load-balancer-controller
```

### Certificate Issues

```bash
# Check cert-manager
kubectl get certificates -A
kubectl describe certificate <name> -n <namespace>

# Check cert-manager logs
kubectl logs -n cert-manager -l app=cert-manager
```

### Karpenter Issues

```bash
# Check NodePool status
kubectl get nodepool
kubectl describe nodepool default

# Check Karpenter logs
kubectl logs -n karpenter -l app.kubernetes.io/name=karpenter
```

### Secrets Issues

```bash
# CSI Secrets Store
kubectl get secretproviderclass -A
kubectl describe secretproviderclass <name>
kubectl get pods -n kube-system -l app=secrets-store-csi-driver

# External Secrets
kubectl get clustersecretstores
kubectl get externalsecrets -A
kubectl describe externalsecret <name> -n <namespace>
```

### External DNS Issues

```bash
# Check External DNS logs
kubectl logs -n external-dns -l app.kubernetes.io/name=external-dns

# Check Route53 for records
aws route53 list-resource-record-sets --hosted-zone-id <zone-id>
```

### Metrics Server Issues

```bash
# Verify metrics-server is running
kubectl get pods -n kube-system -l k8s-app=metrics-server

# Test metrics API
kubectl top nodes
kubectl top pods -A
```
