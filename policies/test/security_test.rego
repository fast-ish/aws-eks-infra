# Unit tests for security policies

package main

import future.keywords.in

# Test: Privileged container should be denied
test_deny_privileged_container {
    deny["Privileged container 'test-container' in Deployment 'test-app' is not allowed"] with input as {
        "kind": "Deployment",
        "metadata": {"name": "test-app"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "test-container",
                        "image": "nginx",
                        "securityContext": {"privileged": true}
                    }]
                }
            }
        }
    }
}

# Test: Cilium should be exempt from privileged check
test_allow_cilium_privileged {
    count(deny) == 0 with input as {
        "kind": "DaemonSet",
        "metadata": {"name": "cilium-agent"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "cilium-agent",
                        "image": "quay.io/cilium/cilium:v1.15.0",
                        "securityContext": {"privileged": true}
                    }]
                }
            }
        }
    }
}

# Test: Falco should be exempt from privileged check
test_allow_falco_privileged {
    count(deny) == 0 with input as {
        "kind": "DaemonSet",
        "metadata": {"name": "falco"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "falco",
                        "image": "docker.io/falcosecurity/falco:0.37.1",
                        "securityContext": {"privileged": true}
                    }]
                }
            }
        }
    }
}

# Test: Non-privileged container should pass
test_allow_non_privileged {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test-app"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "test-container",
                        "image": "nginx",
                        "securityContext": {"privileged": false}
                    }]
                }
            }
        }
    }
}

# Test: Allowed registry should pass
test_allow_registry_dockerhub {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "docker.io/library/nginx:latest"
                    }]
                }
            }
        }
    }
}

# Test: Allowed registry (ECR) should pass
test_allow_registry_ecr {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "123456789012.dkr.ecr.us-west-2.amazonaws.com/my-app:v1"
                    }]
                }
            }
        }
    }
}

# Test: Public ECR should be allowed
test_allow_registry_public_ecr {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "public.ecr.aws/karpenter/karpenter:v0.35.0"
                    }]
                }
            }
        }
    }
}

# Test: Quay.io should be allowed
test_allow_registry_quay {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "quay.io/cilium/cilium:v1.15.0"
                    }]
                }
            }
        }
    }
}

# Test: ghcr.io should be allowed
test_allow_registry_ghcr {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "ghcr.io/external-secrets/external-secrets:v0.9.0"
                    }]
                }
            }
        }
    }
}

# Test: Simple image name (DockerHub shorthand) should be allowed
test_allow_simple_image_name {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "nginx:latest"
                    }]
                }
            }
        }
    }
}

# Test: DockerHub org/image format should be allowed
test_allow_dockerhub_org_image {
    count(deny) == 0 with input as {
        "kind": "Deployment",
        "metadata": {"name": "test"},
        "spec": {
            "template": {
                "spec": {
                    "containers": [{
                        "name": "app",
                        "image": "grafana/grafana:10.0.0"
                    }]
                }
            }
        }
    }
}

# Test: hostNetwork warning for non-exempt workload
test_warn_host_network {
    warn["hostNetwork is enabled in 'custom-app' - verify this is required"] with input as {
        "kind": "Deployment",
        "metadata": {"name": "custom-app"},
        "spec": {
            "template": {
                "spec": {
                    "hostNetwork": true,
                    "containers": [{
                        "name": "app",
                        "image": "nginx"
                    }]
                }
            }
        }
    }
}

# Test: hostNetwork should not warn for cilium
test_no_warn_host_network_cilium {
    count(warn) == 0 with input as {
        "kind": "DaemonSet",
        "metadata": {"name": "cilium"},
        "spec": {
            "template": {
                "spec": {
                    "hostNetwork": true,
                    "containers": [{
                        "name": "cilium-agent",
                        "image": "quay.io/cilium/cilium:v1.15.0"
                    }]
                }
            }
        }
    }
}
