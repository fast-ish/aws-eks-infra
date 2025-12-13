# aws-eks-infra

Standalone EKS cluster infrastructure. Java 21, AWS CDK 2.219.0.

**Depends on**: `cdk-common` (install first with `mvn install -DskipTests`)

## Architecture

```
EksStack (main)
  ├── NetworkNestedStack        # VPC, subnets, NAT
  ├── EksNestedStack            # EKS cluster, managed addons, node groups
  ├── CoreAddonsNestedStack     # cert-manager, karpenter, external-dns, etc.
  └── ObservabilityAddonsStack  # grafana k8s-monitoring
```

**No Backstage, no Argo** - pure Kubernetes infrastructure only.

## Directory Layout

```
src/main/java/fasti/sh/eks/
  Launch.java                 # CDK app entry point
  stack/
    EksStack.java             # Main orchestration stack
    EksReleaseConf.java       # Configuration record

src/main/resources/production/v1/
  conf.mustache               # Main config
  eks/addons.mustache         # All addon configs
  helm/*.mustache             # Helm chart values
  policy/*.mustache           # IAM policy templates
```

## Key Addons

| Addon | Purpose |
|-------|---------|
| cert-manager | TLS certificates |
| aws-secrets-store | AWS Secrets CSI driver |
| karpenter | Node autoscaling with NodePools |
| aws-load-balancer | ALB/NLB controller |
| metrics-server | Kubernetes metrics API |
| external-dns | Route53 DNS records |
| external-secrets | Secrets Manager operator |
| reloader | ConfigMap/Secret reload watcher |
| grafana k8s-monitoring | Observability |

## Commands

```bash
mvn compile -q                 # Compile
mvn spotless:apply             # Format code
cdk synth                      # Synthesize CloudFormation
cdk deploy                     # Deploy to AWS
```

## Key Files

- `stack/EksStack.java` - Main stack using cdk-common constructs
- `resources/production/v1/eks/addons.mustache` - All addon config
- `resources/production/v1/helm/*.mustache` - Helm chart values
- `resources/production/v1/policy/*.mustache` - IAM policies

## Configuration

Edit `cdk.context.json`:
- AWS account/region
- Domain name
- Administrator IAM roles
- Grafana Cloud secret ARN

## Differences from aws-idp-infra

| Feature | aws-eks-infra | aws-idp-infra |
|---------|---------------|---------------|
| Backstage | No | Yes |
| ArgoCD | No | Yes |
| Argo Workflows | No | Yes |
| Kyverno | No | Yes |
| Core Addons | Yes | Yes |
| Observability | Yes | Yes |

## Don't

- Deploy without installing cdk-common first
- Add Argo or Backstage here - use aws-idp-infra
- Forget to update policy templates when adding addons
