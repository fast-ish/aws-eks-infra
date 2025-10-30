# AI Context: aws-eks-infra

> **Purpose**: This document helps AI assistants quickly understand the aws-eks-infra codebase architecture, patterns, and conventions.

## What is aws-eks-infra?

A **production-ready AWS EKS (Elastic Kubernetes Service) infrastructure** built with AWS CDK that provides:
- Complete EKS cluster with managed and custom addons
- Multi-AZ VPC with public/private subnets
- Advanced autoscaling with Karpenter
- Comprehensive observability with Grafana Cloud
- Security-focused with Pod Identity, KMS encryption, and RBAC
- Helm chart-based addon management

**Key Technologies**: Java 21, AWS CDK 2.219.0, cdk-common library, Kubernetes 1.33, Karpenter, Grafana

## Architecture Overview

### Nested Stack Pattern

```
DeploymentStack (main)
├── NetworkNestedStack      # VPC, subnets, NAT gateways
└── EksNestedStack          # EKS cluster, node groups, addons
```

**Infrastructure Flow**:
1. Network stack creates VPC with 3 AZs (public/private subnets)
2. EKS stack creates cluster in the VPC
3. AWS managed addons installed (VPC CNI, EBS CSI, CoreDNS, etc.)
4. Helm chart addons installed (Karpenter, Cert-Manager, etc.)
5. Node groups provisioned with Bottlerocket AMI

### Project Structure

```
aws-eks-infra/
├── src/
│   ├── main/
│   │   ├── java/fasti/sh/eks/
│   │   │   ├── Launch.java               # CDK App entry point
│   │   │   └── stack/
│   │   │       ├── DeploymentConf.java   # Configuration record
│   │   │       └── DeploymentStack.java  # Main orchestration stack
│   │   └── resources/prototype/v1/       # Configuration templates
│   │       ├── conf.mustache             # Main deployment config
│   │       ├── synthesizer.mustache      # CDK synthesizer config
│   │       ├── eks/                      # EKS cluster configs
│   │       │   ├── cluster.yaml
│   │       │   ├── node-group.yaml
│   │       │   └── addons/              # Addon configurations
│   │       ├── helm/                     # Helm chart values
│   │       │   ├── karpenter/
│   │       │   ├── cert-manager/
│   │       │   ├── aws-load-balancer-controller/
│   │       │   └── grafana-k8s-monitoring/
│   │       ├── policy/                   # IAM policy templates
│   │       └── vpc/                      # VPC configurations
│   └── test/
│       ├── java/fasti/sh/eks/
│       │   ├── LaunchTest.java           # App entry point tests (34 tests)
│       │   └── stack/
│       │       └── DeploymentConfTest.java # Config validation tests (103 tests)
│       └── resources/                    # Test configuration files
│
├── .github/
│   ├── dependabot.yml                    # Dependency update automation
│   ├── workflows/                        # GitHub Actions CI/CD
│   └── AI_CONTEXT.md                     # This file
│
├── docs/                                 # Comprehensive documentation
│   ├── ARCHITECTURE.md                   # Detailed architecture guide
│   ├── DEVELOPMENT.md                    # Development workflows
│   ├── OPERATIONS.md                     # Operational runbooks
│   ├── SECURITY.md                       # Security guidelines
│   ├── TEMPLATE_REFERENCE.md             # Template documentation
│   └── ADDONS_DEEP_DIVE.md              # Addon configuration details
│
├── cdk.json                              # CDK app configuration
├── cdk.context.template.json             # Context template for deployment
└── pom.xml                              # Maven build configuration
```

## Core Concepts

### 1. Configuration-Driven Infrastructure

All infrastructure is defined through YAML configuration files processed by Mustache templates:

```yaml
# resources/prototype/v1/eks/cluster.yaml
name: "{{hosted:name}}-eks"
version: "1.33"
logging:
  api: true
  audit: true
  authenticator: true
  controllerManager: true
  scheduler: true
```

**Template Variables**:
- `{{host:*}}` - AWS account/region context (e.g., `host:account`, `host:region`)
- `{{hosted:*}}` - Application context (e.g., `hosted:name`, `hosted:environment`)
- Service-specific: `{{eks:*}}`, `{{vpc:*}}`, etc.

### 2. cdk-common Dependency

This project uses the `cdk-common` library which provides:
- High-level AWS constructs (`VpcConstruct`, `EksConstruct`, etc.)
- Template processing (`Template.java`, `Mapper.java`)
- Common models (`Common`, `NetworkConf`, `KubernetesConf`)
- Naming conventions (`Format.java`)

**All nested stacks use cdk-common constructs internally.**

### 3. Deployment Pattern

```
CDK Context → Template Processing → YAML/JSON → Java Records → CDK Constructs → AWS Resources
```

**Key Classes**:
- `Launch.java` - Entry point, loads configuration and creates stacks
- `DeploymentConf` - Record holding all configuration paths
- `DeploymentStack` - Orchestrates nested stack creation
- `Template.parse()` - Processes Mustache templates with context
- `Mapper.get()` - Jackson YAML/JSON parser

### 4. Nested Stack Architecture

Each nested stack:
- Extends `software.amazon.awscdk.NestedStack`
- Uses cdk-common constructs for resource creation
- Receives `Common` metadata and specific configuration
- Returns resources via getter methods

Example pattern:
```java
public class EksNestedStack extends NestedStack {
  @Getter
  private final Cluster cluster;

  public EksNestedStack(Construct scope, Common common,
                       KubernetesConf conf, Vpc vpc, NestedStackProps props) {
    super(scope, Format.id("eks", common.id()), props);

    // Create EKS cluster using cdk-common construct
    var eksConstruct = new EksConstruct(this, common, conf, vpc);
    this.cluster = eksConstruct.cluster();
  }
}
```

## Key Components

### 1. Network Stack

**VPC Configuration**:
- CIDR: 10.0.0.0/16 (configurable)
- 3 Availability Zones for high availability
- Public subnets: /24 per AZ (NAT Gateways, Load Balancers)
- Private subnets: /24 per AZ (EKS worker nodes)
- 2 NAT Gateways for redundancy

**Pattern**: Uses `VpcConstruct` from cdk-common with configuration loaded from `resources/prototype/v1/vpc/`

### 2. EKS Stack

**Cluster Features**:
- Kubernetes version: 1.33
- Endpoint access: Public + Private (hybrid mode)
- Logging: All log types enabled
- Authentication: IAM + RBAC integration
- Access entries: Administrator and user roles

**Node Groups**:
- AMI: Bottlerocket (security-focused container OS)
- Instance type: m5a.large (configurable)
- Capacity: 2-6 nodes (min-max-desired)
- Deployment: Private subnets only
- Management: SSM for secure access

### 3. AWS Managed Addons

Installed automatically with proper configuration:

| Addon | Purpose | Configuration |
|-------|---------|--------------|
| VPC CNI | Pod networking | IAM role with service account |
| EBS CSI Driver | Persistent storage | KMS encryption, custom storage class |
| CoreDNS | Cluster DNS | Default configuration |
| Kube Proxy | Network proxy | Default configuration |
| Pod Identity Agent | IRSA support | Enables IAM roles for pods |
| Container Insights | Monitoring | CloudWatch integration |

**Pattern**: Configured via YAML files in `resources/prototype/v1/eks/addons/`

### 4. Helm Chart Addons

Installed via CDK Helm chart construct:

| Chart | Namespace | Purpose |
|-------|-----------|---------|
| cert-manager | cert-manager | TLS certificate automation |
| CSI Secrets Store | aws-secrets-store | Secrets/Parameter Store integration |
| Karpenter | kube-system | Advanced node autoscaling |
| AWS LB Controller | aws-load-balancer | ALB/NLB management |
| Alloy Operator | alloy-system | Grafana Alloy operator (CRDs) |
| K8s Monitoring | monitoring | Grafana Cloud observability |

**Pattern**: Values configured in `resources/prototype/v1/helm/{chart-name}/values.yaml`

### 5. Karpenter Autoscaling

**Key Features**:
- Just-in-time node provisioning using Pod Identity
- Cost optimization with right-sized instances
- SQS integration for spot instance interruption handling
- Multi-AZ distribution
- Node consolidation and deprovisioning

**Configuration**:
- SQS Queue: `{{hosted:id}}-karpenter`
- Service Account: Linked to IAM role with EC2 permissions
- EC2NodeClass: Defines AMI, security groups, subnets
- NodePool: Defines instance requirements and limits

### 6. Observability Stack

**Grafana Cloud Integration**:
- Metrics: Prometheus-compatible metrics collection
- Logs: Structured JSON logs via Fluent Bit
- Traces: OpenTelemetry/X-Ray distributed tracing
- Dashboards: Pre-built Kubernetes dashboards

**CloudWatch Container Insights**:
- Container-level metrics
- Application performance data
- Enhanced observability metrics

## Configuration Records

All configuration is defined as Java records (immutable):

```java
public record DeploymentConf(
  String vpc,              // Path to VPC config
  String eks,              // Path to EKS config
  String nodeGroup,        // Path to node group config
  // ... addon paths
) {}
```

**Record Pattern**:
- All fields are `String` paths to template files
- Loaded via `Template.load()` method
- Validated in constructors
- Immutable by design

## Testing Strategy

### Configuration Tests (103 tests)

High-value tests validating configuration loading and parsing:
- `DeploymentConfTest`: 103 tests for all config paths
- Tests YAML parsing, Mustache processing, record creation
- Validates required fields, optional fields, defaults
- Tests error handling for missing/invalid configs

### Launch Tests (34 tests)

Tests for CDK app initialization:
- `LaunchTest`: 34 tests for app entry point
- Template processing with context variables
- Configuration record creation
- Synthesizer configuration

**Total Tests**: 137 tests, all passing ✅

### What We Don't Test

Integration/CDK tests were removed:
- Stack instantiation tests (brittle, low value)
- Mock-heavy construct tests (maintenance burden)
- CDK synth tests (covered by deployment)

**Rationale**: Focus on configuration correctness; actual AWS resource creation validated during deployment.

## Code Conventions

### Naming Conventions

**Stack Names**: `{Service}NestedStack` (e.g., `EksNestedStack`, `NetworkNestedStack`)
**Config Records**: `{Service}Conf` (e.g., `KubernetesConf`, `NetworkConf`)
**Resource IDs**: Use `Format.id()` from cdk-common
**Resource Names**: Use `Format.name()` from cdk-common

### Common Pattern

Every construct receives `Common` metadata:
```java
public record Common(
  String id, account, region, organization,
  String name, alias, environment, version, domain,
  Map<String, String> tags
)
```

Usage:
```java
var stack = new EksNestedStack(this, common, eksConf, vpc, props);
// common used for naming, tagging, context
```

### Logging Pattern

```java
@Slf4j
public class DeploymentStack extends Stack {
  public DeploymentStack(...) {
    log.debug("DeploymentStack [common: {} conf: {}]", common, conf);
  }
}
```

### Template Loading Pattern

```java
// From CDK context
var conf = Template.parse(app, "conf.mustache", contextVars);

// From resource file
var clusterConf = Template.load(ClusterConf.class,
    "prototype/v1/eks/cluster.yaml", ctx());
```

## Common Tasks

### Adding a New Addon

1. Add addon configuration to `resources/prototype/v1/eks/addons/` or `resources/prototype/v1/helm/`
2. Update `DeploymentConf` if needed
3. Configure in `EksConstruct` (for AWS addons) or add Helm chart in `EksNestedStack`
4. Add tests in `DeploymentConfTest`

### Modifying Node Group Configuration

1. Edit `resources/prototype/v1/eks/node-group.yaml`
2. Adjust instance type, capacity, or AMI
3. Update tags or labels as needed
4. Test with `cdk synth` and deploy

### Adding RBAC Access

1. Update `cdk.context.json` with new IAM role ARN:
```json
{
  "hosted:eks:administrators": [
    {
      "username": "new-admin",
      "role": "arn:aws:iam::ACCOUNT:role/ROLE-NAME",
      "email": "admin@example.com"
    }
  ]
}
```
2. Configuration automatically creates access entries

### Customizing Helm Chart Values

1. Edit `resources/prototype/v1/helm/{chart-name}/values.yaml`
2. Follow Helm chart documentation for available values
3. Test locally with `helm template` if possible
4. Deploy with `cdk deploy`

## Deployment Workflow

### 1. Prerequisites

```bash
# Install tools
java 21+, maven, aws-cli, cdk-cli, gh-cli

# Bootstrap CDK
cdk bootstrap aws://ACCOUNT-ID/REGION

# Build cdk-common dependency
mvn -f cdk-common/pom.xml clean install
```

### 2. Configure

```bash
# Copy template
cp cdk.context.template.json cdk.context.json

# Edit with your settings:
# - AWS account and region
# - Domain name
# - Administrator/user IAM roles
# - Environment and version
```

### 3. Build & Deploy

```bash
# Build project
mvn clean install

# Synthesize CloudFormation
cdk synth

# Deploy to AWS
cdk deploy
```

### 4. Post-Deployment

```bash
# Update kubeconfig
aws eks update-kubeconfig --name CLUSTER-NAME --region REGION

# Verify cluster
kubectl get nodes
kubectl get pods -A
```

## Dependencies

### Core Dependencies
- **AWS CDK**: 2.219.0 (cdk-common dependency)
- **Java**: 21+
- **cdk-common**: 1.0.0-SNAPSHOT (local dependency)
- **Jackson**: 2.20.0 (YAML/JSON processing)
- **Lombok**: 1.18.42 (annotations)

### Test Dependencies
- **JUnit 5**: 5.14.0
- **Mockito**: 5.20.0

### Maven Plugins (Latest Versions)
- **compiler**: 3.14.0
- **exec**: 3.6.2
- **surefire**: 3.5.4
- **spotless**: 3.0.0
- **spotbugs**: 4.9.8.1
- **pmd**: 3.28.0
- **jacoco**: 0.8.14
- **checkstyle**: 3.6.0
- **dependency-check**: 12.1.8
- **versions**: 2.19.1

## Troubleshooting

### CDK Synth Fails

```bash
# Clean and rebuild
mvn clean install
rm -rf cdk.out
cdk synth
```

### Template Processing Errors

- Check `cdk.context.json` has all required variables
- Verify template paths match `resources/` structure
- Ensure Mustache syntax is correct: `{{variable}}`

### EKS Cluster Access Issues

- Verify IAM role ARNs in `cdk.context.json`
- Check access entries created in cluster
- Update kubeconfig with correct cluster name/region
- Verify AWS credentials have necessary permissions

### Addon Installation Failures

- Check addon version compatibility with EKS version
- Verify IAM roles have necessary permissions
- Review CloudFormation stack events for errors
- Check addon-specific configuration in YAML files

### Karpenter Not Scaling

- Verify SQS queue exists and has correct permissions
- Check Karpenter logs: `kubectl logs -n kube-system -l app.kubernetes.io/name=karpenter`
- Ensure EC2 permissions are correct in IAM role
- Verify NodePool and EC2NodeClass configurations

## Key Differences from Other Projects

| Aspect | cdk-common | aws-webapp-infra | aws-eks-infra |
|--------|------------|------------------|---------------|
| Purpose | Library of reusable constructs | Web app infrastructure | EKS cluster infrastructure |
| Structure | Flat construct library | Nested stacks (API, Auth, DB) | Nested stacks (Network, EKS) |
| Complexity | Medium (library) | Medium (serverless) | High (Kubernetes) |
| Testing | Construct + model tests | Model tests only | Model + launch tests |
| Deployment | N/A (library) | Full serverless stack | Full EKS cluster |

## Resources

- [README.md](../README.md) - Overview and quickstart
- [ARCHITECTURE.md](../docs/ARCHITECTURE.md) - Detailed architecture
- [DEVELOPMENT.md](../docs/DEVELOPMENT.md) - Development guide
- [OPERATIONS.md](../docs/OPERATIONS.md) - Operational runbooks
- [ADDONS.md](../ADDONS.md) - Addon reference
- [cdk-common](https://github.com/stxkxs/cdk-common) - Dependency library

## Version Info

- **Java**: 21+
- **AWS CDK**: 2.219.0
- **Kubernetes**: 1.33
- **Maven**: 3.8+
- **Package**: `fasti.sh.eks`
- **Current Version**: 1.0.0-SNAPSHOT

## Dependency Update Strategy

### Dependabot Configuration

Dependabot groups updates into three categories:
1. **maven-plugins**: All Maven plugins grouped together
2. **test-dependencies**: JUnit and Mockito updates grouped
3. **production-dependencies**: Jackson and other production deps grouped

This minimizes PR noise while keeping dependencies current.

### Latest Versions (as of 2025-10-29)

**Libraries**:
- Jackson: 2.20.0
- JUnit: 5.14.0
- Mockito: 5.20.0

**Plugins**:
- exec-maven-plugin: 3.6.2
- spotbugs: 4.9.8.1
- pmd: 3.28.0
- jacoco: 0.8.14
- spotless: 3.0.0
- dependency-check: 12.1.8
- versions: 2.19.1

---

**Last Updated**: 2025-10-29
**Test Status**: 137/137 passing ✅
**Build Status**: All dependencies updated to latest versions
