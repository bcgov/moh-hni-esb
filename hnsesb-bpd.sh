
#!/bin/bash
# Manual feature build + deploy for hnsesb
#
# Three modes:
#   build:  Maven + podman build (local only, nothing pushed)
#   push:   Push local image to a cluster's tools namespace
#   deploy: Tag image from tools -> target namespace (fixed-tag deployment)
#
# Usage:
#   ./hnsesb-feature-deploy.sh build <image-tag>
#   ./hnsesb-feature-deploy.sh push <cluster> <image-tag>
#   ./hnsesb-feature-deploy.sh deploy <environment> <full-image-tag>
#
#   Clusters: silver, gold, gold-dr
#   Environments: silver-prod, gold-trn, gold-vc1, gold-prod, gold-dr
#   Image tag: descriptive name (e.g. java_17_upgrade)
#   Full image tag: <image-tag>-<SHORT_SHA> (output by build mode)
#
# Examples:
#   # 1. Build locally
#   ./hnsesb-feature-deploy.sh build java_17_upgrade
#   # Output: === Registry tag: java_17_upgrade-a1b2c3d4e5f6 ===
#
#   # 2. Push to Silver tools, deploy to Silver prod
#   ./hnsesb-feature-deploy.sh push silver java_17_upgrade-a1b2c3d4e5f6
#   ./hnsesb-feature-deploy.sh deploy silver-prod java_17_upgrade-a1b2c3d4e5f6
#
#   # 3. Push to Gold tools
#   ./hnsesb-feature-deploy.sh push gold java_17_upgrade-a1b2c3d4e5f6
#
#   # 4. Deploy to Gold test environments
#   ./hnsesb-feature-deploy.sh deploy gold-trn java_17_upgrade-a1b2c3d4e5f6
#   ./hnsesb-feature-deploy.sh deploy gold-vc1 java_17_upgrade-a1b2c3d4e5f6
#
#   # 5. Change window — deploy to Gold prod
#   ./hnsesb-feature-deploy.sh deploy gold-prod java_17_upgrade-a1b2c3d4e5f6
#
#   # 6. Push to Gold DR tools, then deploy
#   ./hnsesb-feature-deploy.sh push gold-dr java_17_upgrade-a1b2c3d4e5f6
#   ./hnsesb-feature-deploy.sh deploy gold-dr java_17_upgrade-a1b2c3d4e5f6
#
# Prerequisites: podman, oc, mvn, git (WSL2)
# Must be logged into the correct OpenShift cluster before running push or deploy.
set -eu

IMAGE_NAME="hnsesb"
SILVER_REGISTRY="image-registry.apps.silver.devops.gov.bc.ca"
GOLD_REGISTRY="image-registry.apps.gold.devops.gov.bc.ca"
GOLDDR_REGISTRY="image-registry.apps.golddr.devops.gov.bc.ca"
INTERNAL_REGISTRY="image-registry.openshift-image-registry.svc:5000"

# Returns: registry tools-namespace expected-server
resolve_cluster() {
  case "$1" in
    silver)  echo "${SILVER_REGISTRY} c5839f-tools https://api.silver.devops.gov.bc.ca:6443" ;;
    gold)    echo "${GOLD_REGISTRY} c8d6f2-tools https://api.gold.devops.gov.bc.ca:6443" ;;
    gold-dr) echo "${GOLDDR_REGISTRY} c8d6f2-tools https://api.golddr.devops.gov.bc.ca:6443" ;;
    *)
      echo "Unknown cluster: $1" >&2
      echo "Valid: silver, gold, gold-dr" >&2
      exit 1
      ;;
  esac
}

# Returns: namespace tools-namespace imagestream env-tag container-name expected-server
resolve_env() {
  case "$1" in
    silver-prod) echo "c5839f-prod c5839f-tools hnsesb prod hnsesb https://api.silver.devops.gov.bc.ca:6443" ;;
    gold-trn)    echo "c8d6f2-test c8d6f2-tools hnsesb-trn trn hnsesb https://api.gold.devops.gov.bc.ca:6443" ;;
    gold-vc1)    echo "c8d6f2-test c8d6f2-tools hnsesb-vc1 vc1 hnsesb https://api.gold.devops.gov.bc.ca:6443" ;;
    gold-prod)   echo "c8d6f2-prod c8d6f2-tools hnsesb prod hnsesb https://api.gold.devops.gov.bc.ca:6443" ;;
    gold-dr)     echo "c8d6f2-prod c8d6f2-tools hnsesb prod hnsesb https://api.golddr.devops.gov.bc.ca:6443" ;;
    *)
      echo "Unknown environment: $1" >&2
      echo "Valid: silver-prod, gold-trn, gold-vc1, gold-prod, gold-dr" >&2
      exit 1
      ;;
  esac
}

# Verify the user is logged into the expected cluster
verify_cluster() {
  local expected_server="$1"
  local current_server
  current_server=$(oc whoami --show-server 2>/dev/null) || {
    echo "ERROR: Not logged in to OpenShift." >&2
    echo "Run: oc login --token=<token> --server=${expected_server}" >&2
    echo "Get your token at: https://oauth-openshift.$(echo "${expected_server}" | sed 's|https://api\.||;s|:6443||')/oauth/token/request" >&2
    exit 1
  }
  if [ "${current_server}" != "${expected_server}" ]; then
    echo "ERROR: Logged into ${current_server} but expected ${expected_server}" >&2
    echo "Run: oc login --token=<token> --server=${expected_server}" >&2
    exit 1
  fi
  echo "Verified: logged into ${current_server} as $(oc whoami)"
}

MODE="${1:?Usage: $0 <build|push|deploy> ...}"

# =============================================================================
# BUILD — Maven + podman build, local only
# =============================================================================
if [ "${MODE}" = "build" ]; then
  IMAGE_TAG="${2:?Usage: $0 build <image-tag>}"

  BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD)
  SHORT_SHA=$(git rev-parse --short=12 HEAD)
  FULL_TAG="${IMAGE_TAG}-${SHORT_SHA}"

  echo "=== BUILD ==="
  echo "=== Branch: ${BRANCH_NAME} | Tag: ${IMAGE_TAG} | SHA: ${SHORT_SHA} ==="
  echo "=== Registry tag: ${FULL_TAG} ==="

  # Maven build
  cd hnsecure
  mvn install -Dimpl-version="${IMAGE_TAG}" -Dgit-commit="${SHORT_SHA}"
  cd ..

  # Podman build (local image stays 'latest' to avoid accumulation)
  podman build -t "${IMAGE_NAME}:latest" -f hnsecure/Dockerfile hnsecure/

  echo ""
  echo "=== Build complete ==="
  echo "=== Local image: ${IMAGE_NAME}:latest ==="
  echo "=== Registry tag: ${FULL_TAG} ==="
  echo ""
  echo "Next steps:"
  echo "  $0 push silver ${FULL_TAG}"
  echo "  $0 push gold ${FULL_TAG}"
  echo "  $0 push gold-dr ${FULL_TAG}"
  exit 0
fi

# =============================================================================
# PUSH — Push local image to a cluster's tools namespace
# =============================================================================
if [ "${MODE}" = "push" ]; then
  CLUSTER="${2:?Usage: $0 push <silver|gold|gold-dr> <full-image-tag>}"
  FULL_TAG="${3:?Usage: $0 push <silver|gold|gold-dr> <full-image-tag>}"

  RESOLVED=$(resolve_cluster "${CLUSTER}")
  REGISTRY=$(echo "${RESOLVED}" | awk '{print $1}')
  TOOLS_NS=$(echo "${RESOLVED}" | awk '{print $2}')
  EXPECTED_SERVER=$(echo "${RESOLVED}" | awk '{print $3}')

  verify_cluster "${EXPECTED_SERVER}"

  echo "=== PUSH: ${FULL_TAG} -> ${CLUSTER} (${TOOLS_NS}) ==="

  oc whoami -t | podman login "${REGISTRY}" -u "$(oc whoami)" --password-stdin

  podman tag "${IMAGE_NAME}:latest" "${REGISTRY}/${TOOLS_NS}/${IMAGE_NAME}:${FULL_TAG}"
  podman push "${REGISTRY}/${TOOLS_NS}/${IMAGE_NAME}:${FULL_TAG}"

  echo ""
  echo "=== Push complete: ${FULL_TAG} in ${TOOLS_NS} ==="
  echo ""
  echo "Next steps:"
  case "${CLUSTER}" in
    silver)
      echo "  $0 deploy silver-prod ${FULL_TAG}"
      ;;
    gold)
      echo "  $0 deploy gold-trn ${FULL_TAG}"
      echo "  $0 deploy gold-vc1 ${FULL_TAG}"
      echo "  $0 deploy gold-prod ${FULL_TAG}"
      ;;
    gold-dr)
      echo "  $0 deploy gold-dr ${FULL_TAG}"
      ;;
  esac
  exit 0
fi

# =============================================================================
# DEPLOY — Tag from tools -> target namespace (fixed-tag deployment)
# =============================================================================
if [ "${MODE}" = "deploy" ]; then
  ENV="${2:?Usage: $0 deploy <environment> <full-image-tag>}"
  FULL_TAG="${3:?Usage: $0 deploy <environment> <full-image-tag>}"

  RESOLVED=$(resolve_env "${ENV}")
  TARGET_NS=$(echo "${RESOLVED}" | awk '{print $1}')
  TOOLS_NS=$(echo "${RESOLVED}" | awk '{print $2}')
  TARGET_IS=$(echo "${RESOLVED}" | awk '{print $3}')
  ENV_TAG=$(echo "${RESOLVED}" | awk '{print $4}')
  CONTAINER_NAME=$(echo "${RESOLVED}" | awk '{print $5}')
  EXPECTED_SERVER=$(echo "${RESOLVED}" | awk '{print $6}')

  verify_cluster "${EXPECTED_SERVER}"

  # make sure we have permission to deploy the image from tools to target namespace
  oc policy add-role-to-user system:image-puller system:serviceaccount:${TARGET_NS}:default -n "${TOOLS_NS}"

  echo "=== DEPLOY: ${FULL_TAG} -> ${ENV} (${TARGET_IS}:${ENV_TAG}) ==="

  oc tag "${TOOLS_NS}/${IMAGE_NAME}:${FULL_TAG}" "${TARGET_NS}/${TARGET_IS}:${ENV_TAG}"
  oc set image "deployment/${TARGET_IS}" "${CONTAINER_NAME}=${INTERNAL_REGISTRY}/${TOOLS_NS}/${IMAGE_NAME}:${FULL_TAG}" -n "${TARGET_NS}"

  echo ""
  echo "=== Deploy complete: ${FULL_TAG} -> ${TARGET_NS}/${TARGET_IS}:${ENV_TAG} ==="
  exit 0
fi

# =============================================================================
# Unknown mode
# =============================================================================
echo "Unknown mode: ${MODE}" >&2
echo "Usage:" >&2
echo "  $0 build <image-tag>" >&2
echo "  $0 push <silver|gold|gold-dr> <full-image-tag>" >&2
echo "  $0 deploy <silver-prod|gold-trn|gold-vc1|gold-prod|gold-dr> <full-image-tag>" >&2
exit 1
