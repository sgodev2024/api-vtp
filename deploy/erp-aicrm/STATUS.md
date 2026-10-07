# Deployment status

Deployment checked on 2026-10-07 (Asia/Saigon).

- Server: 123.31.31.39, accessed with existing AICRM root SSH key.
- Source directory: /usr/share/nginx/html/erp-java-aicrm.
- Base source commit: bec20098e1468c998f406b3a760da7857ecab13b.
- Additional local/server change: environment-configurable CORS whitelist.
- Stack: erp-java-aicrm; PostgreSQL, Java backend, Next.js frontend healthy.
- Origin HTTPS: HTTP 200; Let's Encrypt certificate installed with renewal timer.
- Seven origin browser/API checks passed: admin login, default catalog,
  home, sales, POS, suppliers, no browser runtime errors.
- Database initialized through migrations; no local business data imported.
- Production admin credentials retained in ignored local backups/erp-deploy/login.txt.
- Deployment and performance changes are included in the main branch update; see Git history.

## Public domain verification completed

After the user configured a hostname-specific SSL rule, public HTTPS returned
HTTP 200. All seven public browser/API checks passed, including admin login,
catalog/default location and warehouse, home, sales, POS and suppliers.
No browser runtime errors were detected. Evidence is retained in the ignored
local file backups/erp-deploy/result.json.

The user reported selecting Full SSL. The exact Cloudflare setting was not
inspected directly. Full (strict) remains the recommended hostname-specific
setting because the origin has a valid Let's Encrypt certificate. Keep the
existing SSL mode of other hostnames unchanged.

## Module performance update — 2026-10-07

Applied environment-configurable CORS, request-local permission revision
snapshots, shared permission condition parsing, immediate module loading
headers and module-specific/parallel data requests to the running local and
server application. Data and the current server admin password were preserved.
Both environments passed 34 screen navigation checks plus delayed/denied
permission checks. Two backend unit tests and 23 isolated permission API checks
passed. See docs/performance/module-load-2026-10-07.md for measurements.
Deployment/performance source and evidence are included in the main branch update.
