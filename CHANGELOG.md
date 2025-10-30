# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial release of AWS EKS Infrastructure with AWS CDK
- Production-ready EKS cluster (Kubernetes 1.34) with complete observability stack
- Multi-AZ VPC with public/private subnets
- Comprehensive CI/CD with GitHub Actions workflows
- EKS managed addons (VPC CNI, EBS CSI, CoreDNS, Kube Proxy, Pod Identity Agent, CloudWatch Observability)
- Helm-based addon management (Karpenter, Cert-Manager, AWS Load Balancer Controller, Grafana K8s Monitoring)
- Advanced autoscaling with Karpenter and spot instance support
- Complete observability with Grafana Cloud integration
- Security-focused configuration (KMS encryption, RBAC, Pod Identity)
- Bottlerocket AMI for worker nodes
- Configuration-driven infrastructure with Mustache templating
- Comprehensive test suite (137 tests) for configuration validation
- Static analysis integration (Checkstyle, PMD, SpotBugs, OWASP Dependency-Check)
- AI_CONTEXT.md documentation for AI assistants
- Dependabot configuration with grouped dependency updates
- Complete documentation suite (Architecture, Development, Operations, Security guides)

---

*Note: For detailed release notes of each version, see [GitHub Releases](https://github.com/fast-ish/cdk-common/releases)*
