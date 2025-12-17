'use client';

import { motion } from 'framer-motion';
import React, { useState } from 'react';
import {
  EKSIcon,
  VPCIcon,
  KubernetesIcon,
  KarpenterIcon,
  BottlerocketIcon,
  IAMIcon,
  ALBIcon,
  CloudWatchIcon,
  GrafanaIcon,
  CertManagerIcon,
  HelmIcon,
  CDKIcon,
  SecretsManagerIcon,
  ExternalSecretsIcon,
  TLSIcon,
  SQSIcon,
  EBSIcon,
  CoreDNSIcon,
  VPCCNIIcon,
  PodIdentityIcon,
  MetricsServerIcon,
  ExternalDNSIcon,
  ReloaderIcon,
  NodeGroupIcon,
  ControlPlaneIcon,
  OpenTelemetryIcon,
  PrometheusIcon,
  LokiIcon,
  TempoIcon,
  GoldilocksIcon,
  VeleroIcon,
  KyvernoIcon,
  NodeTerminationHandlerIcon,
} from '../icons/aws-icons';
import { ServiceCard, Layer, DataFlowArrow } from '../ui/service-card';

type ViewMode = 'infrastructure' | 'addons' | 'observability' | 'deployment';

export const EksArchitecture: React.FC = () => {
  const [activeView, setActiveView] = useState<ViewMode>('infrastructure');

  const views: { id: ViewMode; label: string; description: string }[] = [
    { id: 'infrastructure', label: 'Infrastructure', description: 'VPC, EKS Cluster & Nodes' },
    { id: 'addons', label: 'Addons', description: 'AWS Managed & Helm Charts' },
    { id: 'observability', label: 'Observability', description: 'Grafana Cloud & Monitoring' },
    { id: 'deployment', label: 'Deployment', description: 'CDK Stack Architecture' },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950">
      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="border-b border-slate-800/50 bg-slate-900/50 backdrop-blur-xl sticky top-0 z-50"
      >
        <div className="max-w-[1800px] mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-3">
                <motion.div
                  animate={{ rotate: [0, 360] }}
                  transition={{ duration: 20, repeat: Infinity, ease: 'linear' }}
                >
                  <EKSIcon size={40} />
                </motion.div>
                <div>
                  <h1 className="text-xl font-bold text-white">Amazon EKS on AWS</h1>
                  <p className="text-slate-400 text-sm">Production Kubernetes Infrastructure</p>
                </div>
              </div>
            </div>

            {/* View Switcher */}
            <div className="flex items-center gap-2 bg-slate-800/50 rounded-xl p-1">
              {views.map((view) => (
                <button
                  key={view.id}
                  onClick={() => setActiveView(view.id)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all duration-300 cursor-pointer
                    ${activeView === view.id
                      ? 'bg-orange-600 text-white shadow-lg shadow-orange-500/25'
                      : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
                    }`}
                >
                  {view.label}
                </button>
              ))}
            </div>

            {/* Legend */}
            <div className="flex items-center gap-4 text-xs">
              <div className="flex items-center gap-2 cursor-help" title="Services that are running">
                <span className="w-3 h-3 rounded-full bg-green-500"></span>
                <span className="text-slate-400">Active</span>
              </div>
              <div className="flex items-center gap-2 cursor-help" title="AWS managed services">
                <span className="w-3 h-3 rounded-full bg-orange-500"></span>
                <span className="text-slate-400">AWS Managed</span>
              </div>
              <div className="flex items-center gap-2 cursor-help" title="Helm deployed">
                <span className="w-3 h-3 rounded-full bg-blue-500"></span>
                <span className="text-slate-400">Helm Chart</span>
              </div>
            </div>
          </div>
        </div>
      </motion.header>

      {/* Main Content */}
      <main className="max-w-[1800px] mx-auto px-6 py-8">
        {/* View Description */}
        <motion.div
          key={activeView}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8 text-center"
        >
          <h2 className="text-2xl font-bold text-white mb-2">
            {views.find(v => v.id === activeView)?.label} View
          </h2>
          <p className="text-slate-400">
            {views.find(v => v.id === activeView)?.description}
          </p>
        </motion.div>

        {/* Architecture Diagram */}
        {activeView === 'infrastructure' && <InfrastructureView />}
        {activeView === 'addons' && <AddonsView />}
        {activeView === 'observability' && <ObservabilityView />}
        {activeView === 'deployment' && <DeploymentView />}
      </main>

      {/* Footer Stats */}
      <motion.footer
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1 }}
        className="border-t border-slate-800/50 bg-slate-900/30 backdrop-blur-sm"
      >
        <div className="max-w-[1800px] mx-auto px-6 py-6">
          <div className="grid grid-cols-6 gap-6">
            {[
              { label: 'K8s Version', value: '1.33', icon: <KubernetesIcon size={24} /> },
              { label: 'CDK Stacks', value: '5', icon: <CDKIcon size={24} /> },
              { label: 'AWS Addons', value: '5', icon: <EKSIcon size={24} /> },
              { label: 'Helm Charts', value: '12+', icon: <HelmIcon size={24} /> },
              { label: 'Security Layers', value: '4', icon: <TLSIcon size={24} /> },
              { label: 'Availability Zones', value: '3', icon: <VPCIcon size={24} /> },
            ].map((stat, idx) => (
              <motion.div
                key={stat.label}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 1.2 + idx * 0.1 }}
                className="text-center cursor-pointer hover:bg-slate-800/30 rounded-lg p-2 transition-colors"
              >
                <div className="flex items-center justify-center gap-2 mb-2">
                  {stat.icon}
                  <span className="text-2xl font-bold text-white">{stat.value}</span>
                </div>
                <span className="text-slate-500 text-sm">{stat.label}</span>
              </motion.div>
            ))}
          </div>
        </div>
      </motion.footer>
    </div>
  );
};

const InfrastructureView: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* AWS Cloud Container */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="relative border-2 border-dashed border-orange-500/30 rounded-3xl p-8 bg-gradient-to-br from-orange-950/10 to-slate-950"
      >
        <div className="absolute -top-4 left-8 bg-slate-950 px-4 py-1 rounded-full border border-orange-500/30 cursor-pointer hover:border-orange-500/60 hover:bg-slate-900 transition-all">
          <span className="text-orange-400 text-sm font-semibold flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-orange-500 animate-pulse"></span>
            AWS Cloud
          </span>
        </div>

        {/* VPC Layer */}
        <Layer
          title="VPC Network"
          subtitle="10.0.0.0/16 - Multi-AZ - Private & Public Subnets"
          color="from-purple-900/20 to-slate-900/50"
          delay={0.1}
          className="mb-6"
        >
          <div className="grid grid-cols-3 gap-4">
            {['us-west-2a', 'us-west-2b', 'us-west-2c'].map((az, idx) => (
              <motion.div
                key={az}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.2 + idx * 0.1 }}
                className="bg-slate-800/30 rounded-xl p-4 border border-slate-700/30 cursor-pointer hover:border-purple-500/50 hover:bg-slate-800/50 transition-all"
              >
                <div className="text-xs text-slate-500 mb-3">Availability Zone: {az}</div>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-xs cursor-help" title="NAT Gateway attached">
                    <span className="w-2 h-2 rounded-full bg-green-500"></span>
                    <span className="text-slate-400">Public Subnet (10.0.{idx + 1}.0/24)</span>
                  </div>
                  <div className="flex items-center gap-2 text-xs cursor-help" title="EKS worker nodes">
                    <span className="w-2 h-2 rounded-full bg-orange-500"></span>
                    <span className="text-slate-400">Private Subnet (10.0.{idx + 4}.0/24)</span>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>

          <div className="flex justify-center gap-8 mt-6">
            <ServiceCard
              icon={<VPCIcon size={36} />}
              title="NAT Gateway"
              description="Egress for private subnets"
              details={['High availability', 'Auto-scaling bandwidth', '2 NAT Gateways']}
              delay={0.4}
              size="sm"
              status="active"
              metrics={[
                { label: 'Gateways', value: '2' },
                { label: 'Elastic IPs', value: '2' },
              ]}
              tags={['networking', 'egress', 'ha']}
              docsUrl="https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat-gateway.html"
            />
            <ServiceCard
              icon={<ALBIcon size={36} />}
              title="Internet Gateway"
              description="Public internet access"
              details={['Ingress routing', 'Redundant by default']}
              delay={0.5}
              size="sm"
              status="active"
              tags={['networking', 'ingress']}
              docsUrl="https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Internet_Gateway.html"
            />
          </div>
        </Layer>

        {/* EKS Control Plane */}
        <Layer
          title="Amazon EKS Cluster"
          subtitle="Kubernetes 1.33 - Managed Control Plane"
          color="from-orange-900/20 to-slate-900/50"
          delay={0.3}
          className="mb-6"
        >
          <div className="grid grid-cols-2 gap-6 mb-6">
            <ServiceCard
              icon={<ControlPlaneIcon size={40} />}
              title="Control Plane"
              description="AWS Managed Kubernetes API"
              details={['Multi-AZ deployment', 'OIDC provider enabled', 'Encrypted etcd', 'Pod Identity support', 'All logging enabled']}
              delay={0.4}
              size="lg"
              status="active"
              metrics={[
                { label: 'Version', value: '1.33' },
                { label: 'Uptime SLA', value: '99.95%' },
              ]}
              tags={['kubernetes', 'managed', 'control-plane']}
              docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/clusters.html"
            />
            <ServiceCard
              icon={<NodeGroupIcon size={40} />}
              title="Managed Node Group"
              description="Bottlerocket-based worker nodes"
              details={['m5a.large instances', 'On-demand capacity', 'Auto-scaling (2-6 nodes)', 'Private subnets only', 'SSM managed']}
              delay={0.5}
              size="lg"
              status="active"
              metrics={[
                { label: 'Min', value: '2' },
                { label: 'Max', value: '6' },
                { label: 'Desired', value: '2' },
              ]}
              tags={['compute', 'nodes', 'bottlerocket']}
              docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/managed-node-groups.html"
            />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <ServiceCard
              icon={<KarpenterIcon size={36} />}
              title="Karpenter"
              description="Node auto-provisioning"
              details={['Just-in-time provisioning', 'Spot instance support', 'Right-sizing', 'Cost optimization']}
              delay={0.6}
              status="active"
              metrics={[
                { label: 'Provisioner', value: 'Default' },
                { label: 'Interruption', value: 'SQS' },
              ]}
              tags={['autoscaling', 'spot', 'cost']}
              docsUrl="https://karpenter.sh/docs/"
            />
            <ServiceCard
              icon={<BottlerocketIcon size={36} />}
              title="Bottlerocket AMI"
              description="Security-focused container OS"
              details={['Minimal attack surface', 'Immutable filesystem', 'Automatic updates', 'SELinux enforcing']}
              delay={0.7}
              status="active"
              metrics={[
                { label: 'Type', value: 'x86_64' },
                { label: 'Updates', value: 'Auto' },
              ]}
              tags={['security', 'os', 'container']}
              docsUrl="https://aws.amazon.com/bottlerocket/"
            />
            <ServiceCard
              icon={<SQSIcon size={36} />}
              title="Interruption Queue"
              description="Karpenter node termination"
              details={['Spot interruption notices', 'Rebalance recommendations', 'Graceful node draining']}
              delay={0.8}
              status="active"
              tags={['messaging', 'spot', 'graceful']}
              docsUrl="https://karpenter.sh/docs/concepts/disruption/"
            />
          </div>
        </Layer>

        {/* Security Layer */}
        <Layer
          title="Security & Identity"
          subtitle="IAM - RBAC - Encryption"
          color="from-red-900/20 to-slate-900/50"
          delay={0.5}
        >
          <div className="grid grid-cols-4 gap-4">
            <ServiceCard
              icon={<PodIdentityIcon size={36} />}
              title="Pod Identity"
              description="IAM for service accounts"
              details={['Fine-grained permissions', 'No IRSA annotation needed', 'Cross-account support']}
              delay={0.6}
              status="active"
              tags={['iam', 'security', 'identity']}
              docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/pod-identities.html"
            />
            <ServiceCard
              icon={<IAMIcon size={36} />}
              title="RBAC Integration"
              description="AWS IAM to K8s RBAC"
              details={['Access entries', 'Admin/User roles', 'SSO integration']}
              delay={0.7}
              status="active"
              tags={['rbac', 'access', 'sso']}
              docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/access-entries.html"
            />
            <ServiceCard
              icon={<TLSIcon size={36} />}
              title="KMS Encryption"
              description="Data encryption at rest"
              details={['EBS volume encryption', 'Secrets encryption', 'Customer managed keys']}
              delay={0.8}
              status="active"
              tags={['encryption', 'kms', 'security']}
              docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/enable-kms.html"
            />
            <ServiceCard
              icon={<SecretsManagerIcon size={36} />}
              title="Secrets Manager"
              description="Centralized secrets"
              details={['Automatic rotation', 'Fine-grained access', 'Audit logging']}
              delay={0.9}
              status="active"
              tags={['secrets', 'vault', 'rotation']}
              docsUrl="https://docs.aws.amazon.com/secretsmanager/"
            />
          </div>
        </Layer>
      </motion.div>
    </div>
  );
};

const AddonsView: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* AWS Managed Addons */}
      <Layer
        title="AWS Managed EKS Addons"
        subtitle="Installed via EKS addon API"
        color="from-orange-900/20 to-slate-900/50"
      >
        <div className="grid grid-cols-5 gap-4">
          <ServiceCard
            icon={<VPCCNIIcon size={36} />}
            title="VPC CNI"
            description="Pod networking"
            details={['Native VPC IPs for pods', 'Security groups for pods', 'ENI-based networking']}
            delay={0.1}
            status="active"
            metrics={[{ label: 'Version', value: 'Latest' }]}
            tags={['networking', 'cni', 'vpc']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/managing-vpc-cni.html"
          />
          <ServiceCard
            icon={<CoreDNSIcon size={36} />}
            title="CoreDNS"
            description="Cluster DNS"
            details={['Service discovery', 'DNS caching', 'Custom DNS entries']}
            delay={0.2}
            status="active"
            metrics={[{ label: 'Version', value: 'Latest' }]}
            tags={['dns', 'discovery', 'core']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/managing-coredns.html"
          />
          <ServiceCard
            icon={<KubernetesIcon size={36} />}
            title="Kube Proxy"
            description="Network proxy"
            details={['Service load balancing', 'iptables/IPVS mode', 'Connection tracking']}
            delay={0.3}
            status="active"
            metrics={[{ label: 'Version', value: 'Latest' }]}
            tags={['networking', 'proxy', 'services']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/managing-kube-proxy.html"
          />
          <ServiceCard
            icon={<EBSIcon size={36} />}
            title="EBS CSI Driver"
            description="Persistent volumes"
            details={['GP3 storage class', 'KMS encryption', 'Volume expansion']}
            delay={0.4}
            status="active"
            metrics={[{ label: 'Default', value: 'gp3' }]}
            tags={['storage', 'csi', 'ebs']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/ebs-csi.html"
          />
          <ServiceCard
            icon={<PodIdentityIcon size={36} />}
            title="Pod Identity Agent"
            description="IRSA replacement"
            details={['Automatic token refresh', 'Cross-account access', 'No annotation needed']}
            delay={0.5}
            status="active"
            metrics={[{ label: 'Version', value: 'Latest' }]}
            tags={['identity', 'iam', 'security']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/pod-identities.html"
          />
        </div>
      </Layer>

      {/* Core Helm Addons */}
      <Layer
        title="Core Addons (Helm Charts)"
        subtitle="Infrastructure-level components"
        color="from-blue-900/20 to-slate-900/50"
        delay={0.3}
      >
        <div className="grid grid-cols-4 gap-4 mb-6">
          <ServiceCard
            icon={<CertManagerIcon size={36} />}
            title="cert-manager"
            description="TLS certificate automation"
            details={['Let\'s Encrypt integration', 'Certificate rotation', 'Prometheus metrics']}
            delay={0.4}
            status="active"
            metrics={[{ label: 'CRDs', value: 'Installed' }]}
            tags={['tls', 'certificates', 'automation']}
            docsUrl="https://cert-manager.io/docs/"
          />
          <ServiceCard
            icon={<ALBIcon size={36} />}
            title="AWS LB Controller"
            description="Ingress management"
            details={['ALB/NLB provisioning', 'Target group binding', 'TLS termination']}
            delay={0.5}
            status="active"
            metrics={[{ label: 'Type', value: 'ALB/NLB' }]}
            tags={['ingress', 'load-balancer', 'tls']}
            docsUrl="https://kubernetes-sigs.github.io/aws-load-balancer-controller/"
          />
          <ServiceCard
            icon={<KarpenterIcon size={36} />}
            title="Karpenter"
            description="Node autoscaling"
            details={['Just-in-time nodes', 'Spot support', 'Right-sizing', 'Consolidation']}
            delay={0.6}
            status="active"
            metrics={[
              { label: 'Queue', value: 'SQS' },
              { label: 'Mode', value: 'Interrupt' },
            ]}
            tags={['autoscaling', 'nodes', 'spot']}
            docsUrl="https://karpenter.sh/docs/"
          />
          <ServiceCard
            icon={<SecretsManagerIcon size={36} />}
            title="CSI Secrets Store"
            description="Mount secrets as volumes"
            details={['AWS Secrets Manager', 'Parameter Store', 'Secret rotation']}
            delay={0.7}
            status="active"
            tags={['secrets', 'csi', 'mount']}
            docsUrl="https://secrets-store-csi-driver.sigs.k8s.io/"
          />
        </div>

        <div className="grid grid-cols-4 gap-4">
          <ServiceCard
            icon={<MetricsServerIcon size={36} />}
            title="Metrics Server"
            description="Resource metrics API"
            details={['HPA/VPA support', 'kubectl top', 'Resource monitoring']}
            delay={0.8}
            status="active"
            tags={['metrics', 'hpa', 'vpa']}
            docsUrl="https://github.com/kubernetes-sigs/metrics-server"
          />
          <ServiceCard
            icon={<ExternalDNSIcon size={36} />}
            title="External DNS"
            description="Route53 automation"
            details={['Auto DNS records', 'Ingress integration', 'Cleanup on delete']}
            delay={0.9}
            status="active"
            metrics={[{ label: 'Provider', value: 'AWS' }]}
            tags={['dns', 'route53', 'automation']}
            docsUrl="https://github.com/kubernetes-sigs/external-dns"
          />
          <ServiceCard
            icon={<ExternalSecretsIcon size={36} />}
            title="External Secrets"
            description="Sync AWS secrets to K8s"
            details={['Secrets Manager sync', 'Scheduled refresh', 'Templating support']}
            delay={1.0}
            status="active"
            metrics={[{ label: 'Refresh', value: '1h' }]}
            tags={['secrets', 'sync', 'operator']}
            docsUrl="https://external-secrets.io/"
          />
          <ServiceCard
            icon={<ReloaderIcon size={36} />}
            title="Reloader"
            description="ConfigMap/Secret reload"
            details={['Auto pod restart', 'Watch globally', 'Annotation-based']}
            delay={1.1}
            status="active"
            tags={['reload', 'config', 'restart']}
            docsUrl="https://github.com/stakater/Reloader"
          />
        </div>
      </Layer>

      {/* Operational Addons */}
      <Layer
        title="Operational Addons (Helm Charts)"
        subtitle="Cluster operations and maintenance"
        color="from-green-900/20 to-slate-900/50"
        delay={0.5}
      >
        <div className="grid grid-cols-4 gap-4">
          <ServiceCard
            icon={<GoldilocksIcon size={36} />}
            title="Goldilocks"
            description="Resource recommendations"
            details={['VPA-based analysis', 'Right-sizing suggestions', 'Cost optimization', 'Dashboard UI']}
            delay={0.6}
            status="active"
            metrics={[{ label: 'Mode', value: 'VPA' }]}
            tags={['resources', 'optimization', 'vpa']}
            docsUrl="https://goldilocks.docs.fairwinds.com/"
          />
          <ServiceCard
            icon={<KyvernoIcon size={36} />}
            title="Kyverno"
            description="Policy enforcement"
            details={['Admission control', 'Policy validation', 'Mutation rules', 'Audit logging']}
            delay={0.7}
            status="active"
            metrics={[{ label: 'Mode', value: 'Enforce' }]}
            tags={['policy', 'security', 'admission']}
            docsUrl="https://kyverno.io/docs/"
          />
          <ServiceCard
            icon={<NodeTerminationHandlerIcon size={36} />}
            title="Node Termination Handler"
            description="Graceful node shutdown"
            details={['Spot interruptions', 'Scheduled events', 'Cordon & drain', 'Pod eviction']}
            delay={0.8}
            status="active"
            metrics={[{ label: 'Events', value: 'SQS' }]}
            tags={['spot', 'termination', 'graceful']}
            docsUrl="https://github.com/aws/aws-node-termination-handler"
          />
          <ServiceCard
            icon={<VeleroIcon size={36} />}
            title="Velero"
            description="Cluster backup & restore"
            details={['Cluster state backup', 'Disaster recovery', 'S3 storage', 'Scheduled backups']}
            delay={0.9}
            status="active"
            metrics={[{ label: 'Storage', value: 'S3' }]}
            tags={['backup', 'disaster-recovery', 'restore']}
            docsUrl="https://velero.io/docs/"
          />
        </div>
      </Layer>

      {/* Addon Dependencies */}
      <div className="bg-slate-800/20 rounded-xl p-4 border border-slate-700/20">
        <h4 className="text-sm font-semibold text-white mb-3">Addon Deployment Order</h4>
        <div className="flex items-center justify-between text-xs">
          {['EKS Cluster', 'Node Groups', 'Managed Addons', 'Core Addons', 'Operational Addons', 'Observability'].map((step, idx) => (
            <React.Fragment key={step}>
              <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.8 + idx * 0.1 }}
                className="flex flex-col items-center cursor-pointer hover:scale-110 transition-transform"
              >
                <span className="w-6 h-6 rounded-full bg-orange-600 flex items-center justify-center text-white font-bold mb-1 text-xs">
                  {idx + 1}
                </span>
                <span className="text-slate-400 text-center text-[10px]">{step}</span>
              </motion.div>
              {idx < 5 && (
                <motion.div
                  initial={{ scaleX: 0 }}
                  animate={{ scaleX: 1 }}
                  transition={{ delay: 0.9 + idx * 0.1 }}
                  className="flex-1 h-0.5 bg-gradient-to-r from-orange-500 to-orange-400 mx-2"
                />
              )}
            </React.Fragment>
          ))}
        </div>
      </div>
    </div>
  );
};

const ObservabilityView: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Grafana Cloud Integration */}
      <Layer
        title="Grafana Cloud Integration"
        subtitle="Full-stack observability platform"
        color="from-orange-900/20 to-slate-900/50"
      >
        <div className="grid grid-cols-4 gap-6">
          <ServiceCard
            icon={<PrometheusIcon size={40} />}
            title="Prometheus (Mimir)"
            description="Metrics collection"
            details={['Container metrics', 'Application metrics', 'Custom metrics', 'Long-term storage']}
            delay={0.1}
            size="lg"
            status="active"
            metrics={[
              { label: 'Backend', value: 'Grafana Cloud' },
              { label: 'Retention', value: '13 months' },
            ]}
            tags={['metrics', 'prometheus', 'mimir']}
            docsUrl="https://grafana.com/oss/mimir/"
          />
          <ServiceCard
            icon={<LokiIcon size={40} />}
            title="Loki"
            description="Log aggregation"
            details={['Pod logs', 'Node logs', 'Application logs', 'Label-based queries']}
            delay={0.2}
            size="lg"
            status="active"
            metrics={[
              { label: 'Backend', value: 'Grafana Cloud' },
              { label: 'Query', value: 'LogQL' },
            ]}
            tags={['logs', 'loki', 'aggregation']}
            docsUrl="https://grafana.com/oss/loki/"
          />
          <ServiceCard
            icon={<TempoIcon size={40} />}
            title="Tempo"
            description="Distributed tracing"
            details={['Request tracing', 'Span collection', 'Service maps', 'Trace search']}
            delay={0.3}
            size="lg"
            status="active"
            metrics={[
              { label: 'Backend', value: 'Grafana Cloud' },
              { label: 'Protocol', value: 'OTLP' },
            ]}
            tags={['tracing', 'tempo', 'distributed']}
            docsUrl="https://grafana.com/oss/tempo/"
          />
          <ServiceCard
            icon={<GrafanaIcon size={40} />}
            title="Pyroscope"
            description="Continuous profiling"
            details={['CPU profiling', 'Memory profiling', 'Flame graphs', 'Performance insights']}
            delay={0.4}
            size="lg"
            status="active"
            metrics={[
              { label: 'Backend', value: 'Grafana Cloud' },
              { label: 'Language', value: 'Go/Java' },
            ]}
            tags={['profiling', 'pyroscope', 'performance']}
            docsUrl="https://grafana.com/oss/pyroscope/"
          />
        </div>
      </Layer>

      {/* Telemetry Collection */}
      <Layer
        title="Telemetry Collection"
        subtitle="Grafana Alloy - OpenTelemetry Collector"
        color="from-blue-900/20 to-slate-900/50"
        delay={0.2}
      >
        <div className="flex items-center justify-between gap-4">
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 }}
            className="flex flex-col items-center cursor-pointer hover:scale-105 transition-transform"
          >
            <div className="w-16 h-16 rounded-full bg-slate-800 border-2 border-orange-500 flex items-center justify-center mb-2">
              <KubernetesIcon size={32} />
            </div>
            <span className="text-xs text-slate-400">K8s Workloads</span>
          </motion.div>

          <DataFlowArrow direction="right" label="Metrics/Logs/Traces" />

          <ServiceCard
            icon={<OpenTelemetryIcon size={36} />}
            title="Grafana Alloy"
            description="Unified telemetry collector"
            details={['OTLP ingestion', 'Prometheus scraping', 'Log collection', 'Trace forwarding']}
            delay={0.4}
            status="active"
            metrics={[
              { label: 'Mode', value: 'DaemonSet' },
              { label: 'Protocol', value: 'OTLP' },
            ]}
            tags={['otel', 'alloy', 'collector']}
            docsUrl="https://grafana.com/docs/alloy/latest/"
          />

          <DataFlowArrow direction="right" label="Export" />

          <ServiceCard
            icon={<GrafanaIcon size={36} />}
            title="Grafana Cloud"
            description="Observability platform"
            details={['Dashboards', 'Alerting', 'Explore', 'Service discovery']}
            delay={0.5}
            status="active"
            tags={['grafana', 'dashboards', 'alerting']}
            docsUrl="https://grafana.com/products/cloud/"
          />
        </div>
      </Layer>

      {/* AWS Native Monitoring */}
      <Layer
        title="AWS Native Monitoring"
        subtitle="CloudWatch Container Insights"
        color="from-pink-900/20 to-slate-900/50"
        delay={0.4}
      >
        <div className="grid grid-cols-3 gap-4">
          <ServiceCard
            icon={<CloudWatchIcon size={36} />}
            title="Container Insights"
            description="EKS monitoring"
            details={['Container metrics', 'Node metrics', 'Pod metrics', 'Pre-built dashboards']}
            delay={0.5}
            status="active"
            metrics={[
              { label: 'Agent', value: 'CloudWatch' },
              { label: 'Logs', value: 'Enabled' },
            ]}
            tags={['cloudwatch', 'insights', 'aws']}
            docsUrl="https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/Container-Insights-EKS.html"
          />
          <ServiceCard
            icon={<CloudWatchIcon size={36} />}
            title="Control Plane Logs"
            description="EKS audit & system logs"
            details={['API server logs', 'Audit logs', 'Authenticator logs', 'Controller logs', 'Scheduler logs']}
            delay={0.6}
            status="active"
            metrics={[{ label: 'Log Types', value: '5' }]}
            tags={['logs', 'audit', 'control-plane']}
            docsUrl="https://docs.aws.amazon.com/eks/latest/userguide/control-plane-logs.html"
          />
          <ServiceCard
            icon={<CloudWatchIcon size={36} />}
            title="CloudWatch Alarms"
            description="Alerting & notifications"
            details={['Metric alarms', 'Anomaly detection', 'SNS integration', 'Auto-scaling triggers']}
            delay={0.7}
            status="active"
            tags={['alarms', 'alerts', 'notifications']}
            docsUrl="https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html"
          />
        </div>
      </Layer>

      {/* Data Flow Diagram */}
      <div className="bg-slate-800/20 rounded-xl p-6 border border-slate-700/20">
        <h4 className="text-sm font-semibold text-white mb-4">Observability Data Flow</h4>
        <div className="flex items-center justify-between gap-4 text-xs">
          <div className="flex flex-col items-center">
            <span className="px-3 py-1 bg-orange-500/20 text-orange-400 rounded-full mb-2">Source</span>
            <span className="text-slate-400">Pods, Nodes, Apps</span>
          </div>
          <motion.div className="flex-1 h-px bg-gradient-to-r from-orange-500/50 to-blue-500/50" />
          <div className="flex flex-col items-center">
            <span className="px-3 py-1 bg-blue-500/20 text-blue-400 rounded-full mb-2">Collect</span>
            <span className="text-slate-400">Grafana Alloy</span>
          </div>
          <motion.div className="flex-1 h-px bg-gradient-to-r from-blue-500/50 to-purple-500/50" />
          <div className="flex flex-col items-center">
            <span className="px-3 py-1 bg-purple-500/20 text-purple-400 rounded-full mb-2">Process</span>
            <span className="text-slate-400">Filter, Transform</span>
          </div>
          <motion.div className="flex-1 h-px bg-gradient-to-r from-purple-500/50 to-green-500/50" />
          <div className="flex flex-col items-center">
            <span className="px-3 py-1 bg-green-500/20 text-green-400 rounded-full mb-2">Store</span>
            <span className="text-slate-400">Grafana Cloud</span>
          </div>
          <motion.div className="flex-1 h-px bg-gradient-to-r from-green-500/50 to-teal-500/50" />
          <div className="flex flex-col items-center">
            <span className="px-3 py-1 bg-teal-500/20 text-teal-400 rounded-full mb-2">Visualize</span>
            <span className="text-slate-400">Dashboards, Alerts</span>
          </div>
        </div>
      </div>
    </div>
  );
};

const DeploymentView: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* CDK Stack Hierarchy */}
      <Layer
        title="CDK Stack Architecture"
        subtitle="Infrastructure as Code - Java CDK"
        color="from-yellow-900/20 to-slate-900/50"
      >
        <div className="relative">
          {/* Main Stack */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-slate-800/50 rounded-xl p-6 border border-yellow-500/30 mb-6"
          >
            <div className="flex items-center gap-3 mb-4">
              <CDKIcon size={40} />
              <div>
                <h3 className="text-white font-bold">EksStack (Main)</h3>
                <p className="text-slate-400 text-xs">Orchestrates all nested stacks</p>
              </div>
            </div>

            {/* Nested Stacks Grid */}
            <div className="grid grid-cols-5 gap-4">
              {[
                { name: 'NetworkNestedStack', desc: 'VPC, Subnets, NAT', icon: <VPCIcon size={28} />, dep: 'None' },
                { name: 'EksNestedStack', desc: 'EKS Cluster, Nodes, AWS Addons', icon: <EKSIcon size={28} />, dep: 'Network' },
                { name: 'AddonsNestedStack', desc: 'Core Helm Charts', icon: <HelmIcon size={28} />, dep: 'EKS' },
                { name: 'ObservabilityAddonsStack', desc: 'Grafana Alloy', icon: <GrafanaIcon size={28} />, dep: 'EKS' },
                { name: 'ObservabilityNestedStack', desc: 'CloudWatch Config', icon: <CloudWatchIcon size={28} />, dep: 'EKS' },
              ].map((stack, idx) => (
                <motion.div
                  key={stack.name}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.3 + idx * 0.05 }}
                  className="bg-slate-900/70 rounded-lg p-3 border border-slate-700/50 hover:border-yellow-500/50 transition-colors cursor-pointer group"
                >
                  <div className="flex items-center gap-2 mb-2">
                    {stack.icon}
                    <span className="text-xs font-medium text-white truncate group-hover:text-yellow-400 transition-colors">
                      {stack.name.replace('NestedStack', '').replace('Stack', '')}
                    </span>
                  </div>
                  <p className="text-[10px] text-slate-500">{stack.desc}</p>
                  <div className="mt-2 flex items-center gap-1">
                    <span className="text-[9px] text-slate-600">Depends:</span>
                    <span className="text-[9px] text-orange-400">{stack.dep}</span>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>

          {/* Deployment Order */}
          <div className="bg-slate-800/30 rounded-xl p-4 border border-slate-700/20">
            <h4 className="text-sm font-semibold text-white mb-3">Stack Dependencies</h4>
            <div className="flex items-center justify-between text-xs">
              {['Network', 'EKS', 'Core Addons', 'Observability Addons', 'Observability'].map((step, idx) => (
                <React.Fragment key={step}>
                  <motion.div
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 0.8 + idx * 0.1 }}
                    className="flex flex-col items-center cursor-pointer hover:scale-110 transition-transform"
                    title={`Step ${idx + 1}`}
                  >
                    <span className="w-8 h-8 rounded-full bg-orange-600 flex items-center justify-center text-white font-bold mb-1 hover:bg-orange-500 transition-colors">
                      {idx + 1}
                    </span>
                    <span className="text-slate-400 text-center whitespace-nowrap">{step}</span>
                  </motion.div>
                  {idx < 4 && (
                    <motion.div
                      initial={{ scaleX: 0 }}
                      animate={{ scaleX: 1 }}
                      transition={{ delay: 0.9 + idx * 0.1 }}
                      className="flex-1 h-0.5 bg-gradient-to-r from-orange-500 to-orange-400 mx-2"
                    />
                  )}
                </React.Fragment>
              ))}
            </div>
          </div>
        </div>
      </Layer>

      {/* Configuration Management */}
      <Layer
        title="Configuration Management"
        subtitle="Mustache Templates & CDK Context"
        color="from-blue-900/20 to-slate-900/50"
        delay={0.3}
      >
        <div className="flex items-center justify-between gap-6">
          <ServiceCard
            icon={<CDKIcon size={36} />}
            title="cdk.context.json"
            description="Deployment parameters"
            details={['Account & region', 'Environment name', 'Grafana Cloud config', 'IAM role mappings']}
            delay={0.4}
            status="active"
            metrics={[
              { label: 'Format', value: 'JSON' },
              { label: 'Type', value: 'Context' },
            ]}
            tags={['config', 'cdk', 'context']}
          />

          <DataFlowArrow direction="right" label="Inject" />

          <ServiceCard
            icon={<HelmIcon size={36} />}
            title="Mustache Templates"
            description="Parameterized configs"
            details={['Helm values', 'IAM policies', 'K8s manifests', 'Addon configs']}
            delay={0.5}
            status="active"
            metrics={[
              { label: 'Templates', value: '15+' },
              { label: 'Engine', value: 'Mustache' },
            ]}
            tags={['templates', 'mustache', 'config']}
          />

          <DataFlowArrow direction="right" label="Generate" />

          <ServiceCard
            icon={<KubernetesIcon size={36} />}
            title="K8s Resources"
            description="Final manifests"
            details={['Helm releases', 'ConfigMaps', 'Secrets', 'IAM roles']}
            delay={0.6}
            status="active"
            tags={['kubernetes', 'manifests', 'resources']}
          />

          <DataFlowArrow direction="right" label="Deploy" />

          <ServiceCard
            icon={<EKSIcon size={36} />}
            title="EKS Cluster"
            description="Running infrastructure"
            details={['Pods running', 'Services active', 'Ingress configured']}
            delay={0.7}
            status="active"
            metrics={[
              { label: 'Status', value: 'Active' },
              { label: 'Health', value: 'Healthy' },
            ]}
            tags={['eks', 'cluster', 'running']}
          />
        </div>
      </Layer>

      {/* Template Structure */}
      <Layer
        title="Resource Template Structure"
        subtitle="src/main/resources directory layout"
        color="from-purple-900/20 to-slate-900/50"
        delay={0.5}
      >
        <div className="grid grid-cols-3 gap-4">
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
            className="bg-slate-800/40 rounded-lg p-4 border border-slate-700/30"
          >
            <h5 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-orange-500"></span>
              eks/
            </h5>
            <ul className="space-y-1.5 text-xs">
              {['cluster.mustache', 'addons.mustache', 'nodegroups.mustache', 'rbac.mustache', 'storage-class.yaml'].map((file) => (
                <li key={file} className="text-slate-400 flex items-center gap-2">
                  <span className="text-[10px] text-slate-600">└</span> {file}
                </li>
              ))}
            </ul>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
            className="bg-slate-800/40 rounded-lg p-4 border border-slate-700/30"
          >
            <h5 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-blue-500"></span>
              helm/
            </h5>
            <ul className="space-y-1.5 text-xs">
              {['karpenter.mustache', 'cert-manager.mustache', 'aws-load-balancer.mustache', 'grafana.mustache', 'metrics-server.mustache', 'external-dns.mustache'].map((file) => (
                <li key={file} className="text-slate-400 flex items-center gap-2">
                  <span className="text-[10px] text-slate-600">└</span> {file}
                </li>
              ))}
            </ul>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8 }}
            className="bg-slate-800/40 rounded-lg p-4 border border-slate-700/30"
          >
            <h5 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-red-500"></span>
              policy/
            </h5>
            <ul className="space-y-1.5 text-xs">
              {['karpenter.mustache', 'karpenter-interrupt.mustache', 'aws-load-balancer-controller.mustache', 'secret-access.mustache', 'kms-eks-ebs-encryption.mustache'].map((file) => (
                <li key={file} className="text-slate-400 flex items-center gap-2">
                  <span className="text-[10px] text-slate-600">└</span> {file}
                </li>
              ))}
            </ul>
          </motion.div>
        </div>
      </Layer>

      {/* Deployment Commands */}
      <div className="bg-slate-800/20 rounded-xl p-4 border border-slate-700/20">
        <h4 className="text-sm font-semibold text-white mb-3">Deployment Commands</h4>
        <div className="grid grid-cols-3 gap-4 text-xs">
          <div className="bg-slate-900/50 rounded-lg p-3">
            <div className="text-orange-400 font-mono mb-1">cdk synth</div>
            <div className="text-slate-500">Generate CloudFormation templates</div>
          </div>
          <div className="bg-slate-900/50 rounded-lg p-3">
            <div className="text-orange-400 font-mono mb-1">cdk diff</div>
            <div className="text-slate-500">Preview infrastructure changes</div>
          </div>
          <div className="bg-slate-900/50 rounded-lg p-3">
            <div className="text-orange-400 font-mono mb-1">cdk deploy</div>
            <div className="text-slate-500">Deploy all stacks to AWS</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EksArchitecture;
