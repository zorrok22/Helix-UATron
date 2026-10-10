#!/bin/bash
set -e

CONFIG_FILE="properties.config"

POSTGRES_URL_DEFAULT="jdbc:postgresql://registry.example.com:5000/"
POSTGRES_USER_DEFAULT="postgres"
POSTGRES_DATABASE_DEFAULT="solqa_dashboard"
POSTGRES_TABLE_DEFAULT="solqa"

POSTGRES_URL="${POSTGRES_URL:-$POSTGRES_URL_DEFAULT}"
POSTGRES_USER="${POSTGRES_USER:-$POSTGRES_USER_DEFAULT}"
POSTGRES_DATABASE="${POSTGRES_DATABASE:-$POSTGRES_DATABASE_DEFAULT}"
POSTGRES_TABLE="${POSTGRES_TABLE:-$POSTGRES_TABLE_DEFAULT}"

sed_escape_replacement() {
  local s="$1"
  s="${s//\\/\\\\}"
  s="${s//&/\\&}"
  s="${s//|/\\|}"
  printf '%s' "$s"
}

# Validate configuration file exists
if [ ! -f "$CONFIG_FILE" ]; then
  echo "Error: $CONFIG_FILE not found!" >&2
  echo "Please ensure $CONFIG_FILE exists before running this script." >&2
  exit 1
fi

echo "=== Configuring Environment ==="
echo "Product Type:      $PRODUCT_TYPE"
echo "Stack Name:        $STACK_NAME"
echo "Host Domain:       $HOST_DOMAIN"
echo "DWPC URL:          ${DWPC_URL:-default}"
echo "Admin Username:    $ADMIN_USERNAME"
if [ ! -z "$ADMIN_PASSWORD" ]; then
	echo "Admin Password:    ****"
fi
echo "Product Version:   $PRODUCT_VERSION"
echo "AR Server Alias:   $ARSERVER_ALIAS"
echo "MSP Enabled:       $ISMSP_ENABLED"
echo "ADE Platform URL:  $ADE_PLATFORM_URL"
echo "ADE Username:      $ADE_USERNAME"
if [ ! -z "$ADE_PASSWORD" ]; then
	echo "ADE Password:      ****"
fi
echo "Smart Graph URL:   $ADE_SMART_GRAPH_URL"
echo "CMDB URL:          $CMDBURL"
echo "Database URL:      $POSTGRES_URL"
echo "Database Name:     $POSTGRES_DATABASE"
echo "Database Table:    $POSTGRES_TABLE"
echo "Database User:     $POSTGRES_USER"
if [ ! -z "$POSTGRES_PWD" ]; then
	echo "Database Password: ****"
else
	echo "Database Password: Not set"
fi


replace_placeholder() {
  local placeholder="$1"
  local value="$2"
  local escaped
  escaped="$(sed_escape_replacement "$value")"
  if [ -n "$value" ]; then
    sed -i "s|${placeholder}|${escaped}|g" "$CONFIG_FILE"
  else
    echo "↷ ${placeholder} is empty; skipping"
  fi
}

if [ -n "${POSTGRES_PWD:-}" ]; then
	echo "=== Configuring Database Settings ==="

	declare -A database_replace_map=(
		["__postgresUrl__"]="$POSTGRES_URL"
		["__postgresDatabase__"]="$POSTGRES_DATABASE"
		["__postgresTableName__"]="$POSTGRES_TABLE"
		["__postgresUser__"]="$POSTGRES_USER"
		["__postgresPwd__"]="$POSTGRES_PWD"
	)

	for key in "${!database_replace_map[@]}"; do
		replace_placeholder "$key" "${database_replace_map[$key]}"
	done

	echo "✅ PostgreSQL database configuration complete"

else
	echo "=== Skipping Database Settings (POSTGRES_PWD not set) ==="
fi

RUN_COUNTER="${RUN_COUNTER:-0}"
DEPLOYMENT_JOB_ID="${DEPLOYMENT_JOB_ID:-}"

declare -A replace_map=(
	["__STACK_NAME__"]="$STACK_NAME"
	["__HOST_DOMAIN__"]="$HOST_DOMAIN"
	["__productVersion__"]="$PRODUCT_VERSION"
	["__ARServerAlias__"]="$ARSERVER_ALIAS"
	["__isMSPEnabled__"]="$ISMSP_ENABLED"
	["__adminUserName__"]="$ADMIN_USERNAME"
	["__adminPassword__"]="$ADMIN_PASSWORD"
	["__DWPC_URL__"]="$DWPC_URL"
	["__runcounter__"]="$RUN_COUNTER"
	["__deployment_job_id__"]="$DEPLOYMENT_JOB_ID"
	["__ADEPortalURL__"]="$ADE_PLATFORM_URL"
	["__adetenantadminuser__"]="$ADE_USERNAME"
	["__adetenantadminpassword__"]="$ADE_PASSWORD"
	["__ADESmartGraphURL__"]="$ADE_SMART_GRAPH_URL"
	["__CMDBURL__"]="$CMDBURL"
)

for key in "${!replace_map[@]}"; do
	replace_placeholder "$key" "${replace_map[$key]}"
done

echo "✅ Configuration complete. Starting tests..."
echo ""

# If no command provided or default mvn command, use TEST_SUITE variable
if [ "$1" = "mvn" ] && [ "$2" = "test" ] && [ -z "$3" ]; then
	exec mvn test -Dsurefire.suiteXmlFiles="${TEST_SUITE}"
else
	# Execute the provided command
	exec "$@"
fi
