# Resource policies for Kubernetes manifests
# Ensures proper resource limits and requests are defined

package main

import future.keywords.in

# Warn when resource requests are not defined (not enforced, just warning)
warn[msg] {
    input.kind in ["Deployment", "DaemonSet", "StatefulSet"]
    container := input.spec.template.spec.containers[_]
    not container.resources.requests
    not is_exempt_resources(input.metadata.name)
    msg := sprintf("Container '%s' in %s '%s' has no resource requests defined", [container.name, input.kind, input.metadata.name])
}

# Warn when resource limits are not defined
warn[msg] {
    input.kind in ["Deployment", "DaemonSet", "StatefulSet"]
    container := input.spec.template.spec.containers[_]
    not container.resources.limits
    not is_exempt_resources(input.metadata.name)
    msg := sprintf("Container '%s' in %s '%s' has no resource limits defined", [container.name, input.kind, input.metadata.name])
}

# Exempt certain workloads from resource requirements
# These are typically system components managed by operators
is_exempt_resources(name) {
    exempt_names := [
        "karpenter",
        "cilium",
        "coredns",
        "aws-node",
        "kube-proxy",
        "metrics-server",
        "hubble"
    ]
    some exempt_name in exempt_names
    contains(name, exempt_name)
}

# Warn on CPU limits for certain workloads (controversial, as CPU limits can cause throttling)
# This is informational only
warn[msg] {
    input.kind in ["Deployment", "StatefulSet"]
    container := input.spec.template.spec.containers[_]
    container.resources.limits.cpu
    input.metadata.labels["app.kubernetes.io/component"] == "controller"
    msg := sprintf("Container '%s' in '%s' has CPU limits set - consider if this may cause throttling", [container.name, input.metadata.name])
}

# Warn when memory request is significantly lower than limit (potential OOM risk)
warn[msg] {
    input.kind in ["Deployment", "DaemonSet", "StatefulSet"]
    container := input.spec.template.spec.containers[_]
    request := parse_memory(container.resources.requests.memory)
    limit := parse_memory(container.resources.limits.memory)
    request > 0
    limit > 0
    ratio := request / limit
    ratio < 0.5
    msg := sprintf("Container '%s' in '%s': memory request (%v) is less than 50%% of limit (%v) - potential OOM risk", [container.name, input.metadata.name, container.resources.requests.memory, container.resources.limits.memory])
}

# Helper to parse memory values (simplified - handles common units)
parse_memory(value) = bytes {
    endswith(value, "Gi")
    num := to_number(trim_suffix(value, "Gi"))
    bytes := num * 1073741824
}

parse_memory(value) = bytes {
    endswith(value, "Mi")
    num := to_number(trim_suffix(value, "Mi"))
    bytes := num * 1048576
}

parse_memory(value) = bytes {
    endswith(value, "Ki")
    num := to_number(trim_suffix(value, "Ki"))
    bytes := num * 1024
}

parse_memory(value) = bytes {
    endswith(value, "G")
    num := to_number(trim_suffix(value, "G"))
    bytes := num * 1000000000
}

parse_memory(value) = bytes {
    endswith(value, "M")
    num := to_number(trim_suffix(value, "M"))
    bytes := num * 1000000
}

parse_memory(value) = bytes {
    not endswith(value, "Gi")
    not endswith(value, "Mi")
    not endswith(value, "Ki")
    not endswith(value, "G")
    not endswith(value, "M")
    bytes := to_number(value)
}
