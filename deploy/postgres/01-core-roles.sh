#!/bin/sh
set -e
: "${AICRM_DB_PASSWORD:?Runtime database password is required}"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --set=runtime_password="$AICRM_DB_PASSWORD" <<'SQL'
CREATE ROLE core_app WITH LOGIN PASSWORD :'runtime_password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOBYPASSRLS;
GRANT CONNECT ON DATABASE core_platform TO core_app;
SQL
