# Security policies for Kubernetes manifests
# These policies enforce security best practices

package main

import future.keywords.in

# Deny privileged containers except for exempt components
deny[msg] {
    input.kind == "Pod"
    container := input.spec.containers[_]
    container.securityContext.privileged == true
    not is_exempt_privileged(input.metadata.name)
    msg := sprintf("Privileged container '%s' in Pod '%s' is not allowed", [container.name, input.metadata.name])
}

deny[msg] {
    input.kind == "Deployment"
    container := input.spec.template.spec.containers[_]
    container.securityContext.privileged == true
    not is_exempt_privileged(input.metadata.name)
    msg := sprintf("Privileged container '%s' in Deployment '%s' is not allowed", [container.name, input.metadata.name])
}

deny[msg] {
    input.kind == "DaemonSet"
    container := input.spec.template.spec.containers[_]
    container.securityContext.privileged == true
    not is_exempt_privileged(input.metadata.name)
    msg := sprintf("Privileged container '%s' in DaemonSet '%s' is not allowed", [container.name, input.metadata.name])
}

deny[msg] {
    input.kind == "StatefulSet"
    container := input.spec.template.spec.containers[_]
    container.securityContext.privileged == true
    not is_exempt_privileged(input.metadata.name)
    msg := sprintf("Privileged container '%s' in StatefulSet '%s' is not allowed", [container.name, input.metadata.name])
}

# Exempt components that legitimately need privileged access
is_exempt_privileged(name) {
    exempt_names := [
        "cilium",
        "cilium-agent",
        "cilium-operator",
        "falco",
        "falco-driver-loader",
        "aws-node",
        "kube-proxy",
        "aws-node-termination-handler",
        "node-termination-handler",
        "ebs-csi-node"
    ]
    some exempt_name in exempt_names
    contains(name, exempt_name)
}

# Deny containers without image registry allowlist
deny[msg] {
    input.kind in ["Pod", "Deployment", "DaemonSet", "StatefulSet", "Job", "CronJob"]
    container := get_containers(input)[_]
    image := container.image
    not is_allowed_registry(image)
    msg := sprintf("Container '%s' uses image '%s' from non-allowed registry", [container.name, image])
}

# Allowed image registries
is_allowed_registry(image) {
    allowed_registries := [
        "docker.io",
        "ghcr.io",
        "quay.io",
        "gcr.io",
        "registry.k8s.io",
        "public.ecr.aws",
        ".ecr.",
        ".dkr.ecr.",
        "amazon/",
        "grafana/",
        "prom/",
        "bitnami/",
        "stakater/",
        "jetstack/",
        "kyverno/",
        "velero/",
        "fairwinds/",
        "external-secrets/"
    ]
    some registry in allowed_registries
    contains(image, registry)
}

# Also allow images without explicit registry (DockerHub shorthand)
is_allowed_registry(image) {
    not contains(image, "/")
}

is_allowed_registry(image) {
    # Single slash means DockerHub org/image format
    count(split(image, "/")) == 2
    not contains(image, ".")
}

# Warn on hostNetwork usage except for exempt components
warn[msg] {
    input.kind in ["Pod", "Deployment", "DaemonSet", "StatefulSet"]
    spec := get_pod_spec(input)
    spec.hostNetwork == true
    not is_exempt_host_network(input.metadata.name)
    msg := sprintf("hostNetwork is enabled in '%s' - verify this is required", [input.metadata.name])
}

# Components that legitimately need hostNetwork
is_exempt_host_network(name) {
    exempt_names := [
        "cilium",
        "aws-node",
        "kube-proxy",
        "metrics-server",
        "node-termination-handler",
        "aws-node-termination-handler"
    ]
    some exempt_name in exempt_names
    contains(name, exempt_name)
}

# Helper to get containers from different resource kinds
get_containers(resource) = containers {
    resource.kind == "Pod"
    containers := resource.spec.containers
}

get_containers(resource) = containers {
    resource.kind in ["Deployment", "DaemonSet", "StatefulSet", "Job"]
    containers := resource.spec.template.spec.containers
}

get_containers(resource) = containers {
    resource.kind == "CronJob"
    containers := resource.spec.jobTemplate.spec.template.spec.containers
}

# Helper to get pod spec from different resource kinds
get_pod_spec(resource) = spec {
    resource.kind == "Pod"
    spec := resource.spec
}

get_pod_spec(resource) = spec {
    resource.kind in ["Deployment", "DaemonSet", "StatefulSet", "Job"]
    spec := resource.spec.template.spec
}

get_pod_spec(resource) = spec {
    resource.kind == "CronJob"
    spec := resource.spec.jobTemplate.spec.template.spec
}
