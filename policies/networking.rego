# Networking policies for Kubernetes manifests
# Ensures proper network security configurations

package main

import future.keywords.in

# Warn when Services expose NodePort (prefer LoadBalancer or ClusterIP)
warn[msg] {
    input.kind == "Service"
    input.spec.type == "NodePort"
    not is_exempt_nodeport(input.metadata.name)
    msg := sprintf("Service '%s' uses NodePort type - consider using LoadBalancer or ClusterIP instead", [input.metadata.name])
}

# Exempt certain services from NodePort warning
is_exempt_nodeport(name) {
    exempt_names := [
        "webhook",
        "metrics"
    ]
    some exempt_name in exempt_names
    contains(name, exempt_name)
}

# Warn on hostPort usage
warn[msg] {
    input.kind in ["Pod", "Deployment", "DaemonSet", "StatefulSet"]
    spec := get_pod_spec(input)
    container := spec.containers[_]
    port := container.ports[_]
    port.hostPort
    not is_exempt_host_port(input.metadata.name)
    msg := sprintf("Container '%s' in '%s' uses hostPort %d - verify this is required", [container.name, input.metadata.name, port.hostPort])
}

# Components that legitimately need hostPort
is_exempt_host_port(name) {
    exempt_names := [
        "cilium",
        "aws-node",
        "kube-proxy",
        "hubble"
    ]
    some exempt_name in exempt_names
    contains(name, exempt_name)
}

# Warn when Ingress doesn't have TLS configured
warn[msg] {
    input.kind == "Ingress"
    not input.spec.tls
    msg := sprintf("Ingress '%s' does not have TLS configured - consider enabling HTTPS", [input.metadata.name])
}

# Warn when NetworkPolicy is overly permissive
warn[msg] {
    input.kind == "NetworkPolicy"
    input.spec.ingress[_].from == []
    msg := sprintf("NetworkPolicy '%s' allows ingress from all sources - verify this is intended", [input.metadata.name])
}

warn[msg] {
    input.kind == "NetworkPolicy"
    input.spec.egress[_].to == []
    msg := sprintf("NetworkPolicy '%s' allows egress to all destinations - verify this is intended", [input.metadata.name])
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
