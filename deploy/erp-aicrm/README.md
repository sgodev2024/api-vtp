# ERP AICRM production

Server: 123.31.31.39. Source: `/usr/share/nginx/html/erp-java-aicrm`.
Domain: https://erp.aicrm.vn. Nginx proxies Next.js on 127.0.0.1:3205 and
Java API on 127.0.0.1:8185; source files are never served as static files.

Keep `.env` private (mode 600). Set database credentials, bootstrap password
and `NEXT_PUBLIC_CORE_API_URL=https://erp.aicrm.vn` before building.

```sh
docker compose -p erp-java-aicrm -f docker-compose.yml -f deploy/erp-aicrm/compose.production.yml up -d --build
docker compose -p erp-java-aicrm -f docker-compose.yml -f deploy/erp-aicrm/compose.production.yml ps
```

PostgreSQL has no published port. Its named volume persists across upgrades;
never use `down -v` on production. Back up the database before upgrades.
The initial installation creates a new database through migrations and retains
the separately configured production admin password; local business data is not copied.

Install `nginx.conf` as `/etc/nginx/conf.d/erp.aicrm.vn.conf`, run `nginx -t`
before reloading, and issue a certificate with Certbot's nginx plugin.

If the Cloudflare zone uses Flexible SSL for older applications, create a
Configuration Rule matching only `(http.host eq "erp.aicrm.vn")` and set
SSL to Full (strict). Do not change the SSL mode of the entire zone.
Flexible mode cannot work with the origin's HTTP-to-HTTPS redirect.
