#!/usr/bin/env bash
# Install validation tools for Kubernetes manifest validation
# Tools: kubeconform, kube-linter, pluto, conftest

set -euo pipefail

# Tool versions
KUBECONFORM_VERSION="${KUBECONFORM_VERSION:-0.6.4}"
KUBE_LINTER_VERSION="${KUBE_LINTER_VERSION:-0.6.8}"
PLUTO_VERSION="${PLUTO_VERSION:-5.19.4}"
CONFTEST_VERSION="${CONFTEST_VERSION:-0.54.0}"

# Installation directory
INSTALL_DIR="${INSTALL_DIR:-$HOME/.local/bin}"
mkdir -p "${INSTALL_DIR}"

# Detect OS and architecture
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case "${ARCH}" in
  x86_64) ARCH="amd64" ;;
  aarch64|arm64) ARCH="arm64" ;;
  *) echo "Unsupported architecture: ${ARCH}"; exit 1 ;;
esac

echo "Installing validation tools to ${INSTALL_DIR}"
echo "OS: ${OS}, Architecture: ${ARCH}"
echo ""

# Function to check if command exists and has correct version
check_tool() {
  local tool=$1
  local expected_version=$2
  local version_cmd=$3

  if command -v "${tool}" &> /dev/null; then
    local current_version
    current_version=$(eval "${version_cmd}" 2>/dev/null || echo "unknown")
    if [[ "${current_version}" == *"${expected_version}"* ]]; then
      echo "✓ ${tool} v${expected_version} already installed"
      return 0
    fi
  fi
  return 1
}

# Install kubeconform
install_kubeconform() {
  echo "Installing kubeconform v${KUBECONFORM_VERSION}..."

  local url="https://github.com/yannh/kubeconform/releases/download/v${KUBECONFORM_VERSION}/kubeconform-${OS}-${ARCH}.tar.gz"
  curl -sL "${url}" | tar xz -C "${INSTALL_DIR}" kubeconform
  chmod +x "${INSTALL_DIR}/kubeconform"
  echo "✓ kubeconform installed"
}

# Install kube-linter
install_kube_linter() {
  echo "Installing kube-linter v${KUBE_LINTER_VERSION}..."

  local os_name="${OS}"
  [[ "${OS}" == "darwin" ]] && os_name="darwin"

  local url="https://github.com/stackrox/kube-linter/releases/download/v${KUBE_LINTER_VERSION}/kube-linter-${os_name}_${ARCH}.tar.gz"
  curl -sL "${url}" | tar xz -C "${INSTALL_DIR}" kube-linter
  chmod +x "${INSTALL_DIR}/kube-linter"
  echo "✓ kube-linter installed"
}

# Install pluto
install_pluto() {
  echo "Installing pluto v${PLUTO_VERSION}..."

  local os_name="${OS}"
  [[ "${OS}" == "darwin" ]] && os_name="darwin"

  local url="https://github.com/FairwindsOps/pluto/releases/download/v${PLUTO_VERSION}/pluto_${PLUTO_VERSION}_${os_name}_${ARCH}.tar.gz"
  curl -sL "${url}" | tar xz -C "${INSTALL_DIR}" pluto
  chmod +x "${INSTALL_DIR}/pluto"
  echo "✓ pluto installed"
}

# Install conftest
install_conftest() {
  echo "Installing conftest v${CONFTEST_VERSION}..."

  local os_name="${OS}"
  [[ "${OS}" == "darwin" ]] && os_name="Darwin"
  [[ "${OS}" == "linux" ]] && os_name="Linux"

  local arch_name="${ARCH}"
  [[ "${ARCH}" == "amd64" ]] && arch_name="x86_64"

  local url="https://github.com/open-policy-agent/conftest/releases/download/v${CONFTEST_VERSION}/conftest_${CONFTEST_VERSION}_${os_name}_${arch_name}.tar.gz"
  curl -sL "${url}" | tar xz -C "${INSTALL_DIR}" conftest
  chmod +x "${INSTALL_DIR}/conftest"
  echo "✓ conftest installed"
}

# Install tools if not present
check_tool "kubeconform" "${KUBECONFORM_VERSION}" "kubeconform -v" || install_kubeconform
check_tool "kube-linter" "${KUBE_LINTER_VERSION}" "kube-linter version" || install_kube_linter
check_tool "pluto" "${PLUTO_VERSION}" "pluto version" || install_pluto
check_tool "conftest" "${CONFTEST_VERSION}" "conftest --version" || install_conftest

echo ""
echo "All tools installed successfully!"
echo ""
echo "Installed versions:"
"${INSTALL_DIR}/kubeconform" -v
"${INSTALL_DIR}/kube-linter" version
"${INSTALL_DIR}/pluto" version
"${INSTALL_DIR}/conftest" --version

echo ""
echo "Add ${INSTALL_DIR} to your PATH if not already:"
echo "  export PATH=\"${INSTALL_DIR}:\$PATH\""
