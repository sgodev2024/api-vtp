# SRS SaaS AICRM — phiên bản 1.0

Ngày 11/10/2026. Đặc tả mục tiêu chưa triển khai; tham chiếu BA_SAAS.md và SRS_HIEN_HANH.md. AT là yêu cầu nghiệm thu, chưa chạy.

## 1. Baseline và kiến trúc

Source hiện có TenantContextFilter lấy tenant từ phiên xác thực; TenantAwareDataSource đặt/reset core.tenant_id trên connection cho RLS; compose trỏ một database core_platform. Đây chưa phải routing database theo host. Rà soát auth/bootstrap, datasource, migration, file/jobs/outbox/webhooks và module gate trước sửa.

Control plane có DB, credential, host/admin session riêng; data plane cùng artifact chuẩn, database riêng/tenant. MODE=SAAS hoặc SINGLE: SINGLE dùng tenant/DB cố định server-side, vẫn giữ auth/quyền/RLS và không cần control plane. Frontend API same-origin qua proxy, không hardcode domain API trong build.

Resolve host trước truy vấn login → tenant registry → trạng thái/version → datasource → auth → so tenant token/host → nghiệp vụ. Host/body/query không tự chọn DB; chỉ tin forwarded header từ proxy cấu hình, proxy loại header client giả. Clear context trong finally và scope riêng cho async. Không đổi datasource trong transaction. Wrapper RLS chỉ áp dụng data plane, không wrap vô điều kiện DB quản trị.

Pool lazy/bounded, idle eviction, đóng pool khi secret rotate. Tham số thử đề xuất: max5 connection/tenant, idle10 phút, tổng100; benchmark mới chốt production. Runtime DB user chỉ quyền DB mình, migration/provisioner quyền riêng. Không dùng runtime credential để tạo DB.

## 2. Dữ liệu quản trị

UUID, timestamp UTC, optimistic version, không plaintext secret. Giữ tenant_id/FK/RLS trong DB nghiệp vụ; registry mapping khớp ID tenant hiện có. Mỗi DB nghiệp vụ vận hành một doanh nghiệp.

| Entity | Trường | Ràng buộc |
|---|---|---|
| Tenant | id/code60/name160/contactEmail254/plan/status/appVersion/schemaVersion/version | code unique immutable; email valid; state enum |
| TenantDatabase | tenantId/serverRef/dbName63/runtimeSecretRef/migrationSecretRef/status | 1 tenant/DB mapping; tên DB hệ thống sinh; secret store |
| Domain | id/tenantId/hostname253/type/primary/status/verifyHash/expiresAt/tlsRef/tlsExpiry/version | lowercase IDNA ASCII unique global; 1 primary active |
| Plan | id/code60/name160/limits/enabled/version | limits integer>=0, null unlimited |
| Entitlement | tenantId/moduleKey/allowed/effectiveFrom | unique tenant/module; dependency closure |
| Operation | id/tenantId/type/idempotencyKey/payloadHash/state/step/attempt/actor/error/timestamps | unique actor/tenant/type/key; mismatch payload409 |
| Backup | id/tenantId/dbRef/filesRef/appVersion/schemaVersion/checksums/state/retentionUntil | READY chỉ khi nhất quán và đủ artifacts |
| SupportGrant | id/tenantId/requester/approver/scope/reason/expiry/revokedAt | tenant approval; TTL tối đa60phút đề xuất |
| ReleaseExecution | tenantId/releaseId/state/backupId/evidence | unique tenant/release |

Control plane migrations namespace riêng; không chỉnh migration cũ. Trong BA doanh nghiệp điện thoại/MST optional, form validation nhất quán API. Secrets chỉ secretRef, không trả JDBC/password trong API/log/export.

## 3. API mục tiêu và FR

Prefix đề xuất /saas-admin/api/v1 trên host quản trị riêng; cần đối chiếu core trước code. JSON camelCase; lỗi code/message/requestId/fieldErrors. 400 validation,401 auth,403 quyền/module,404 tài nguyên/unknown host,409 version/duplicate,423 suspended/maintenance,503 tenant DB unavailable. List page1/pageSize20/max100/sort whitelist. Không lộ tồn tại tài nguyên tenant khác. Async202 có operationId; mutation nhạy cảm audit và Idempotency-Key.

| FR | API/chức năng | Điều kiện |
|---|---|---|
| FR-S01 | GET/POST tenants; GET/PATCH tenants/{id} | tenant.read/manage; version sửa; tạo DRAFT |
| FR-S02 | POST tenants/{id}/provision | provision quyền riêng; async/checkpoint/health |
| FR-S03 | POST tenants/{id}/suspend,resume,maintenance | lifecycle; lý do; invalidate session/cache; drain ghi |
| FR-S04 | CRUD plans; PUT tenants/{id}/entitlements | dependency/quota/version; backend gate |
| FR-S05 | GET/POST tenants/{id}/domains | hostname validation/unique; TXT/CNAME hướng dẫn |
| FR-S06 | POST domains/{id}/verify,activate,set-primary; DELETE domain | ownership+DNS+TLS; còn đường truy cập hợp lệ |
| FR-S07 | GET operations/{id}; POST operations/{id}/retry | đọc/retry riêng; retry chỉ step phù hợp; lock tenant |
| FR-S08 | POST/GET tenants/{id}/backups | async; encrypted artifacts; checksum |
| FR-S09 | POST restore-validations; POST validations/{id}/cutover | restore vùng tạm, approval/maintenance trước cutover |
| FR-S10 | POST releases/{id}/executions | batch/version/backup/pilot; lỗi dừng batch |
| FR-S11 | POST tenants/{id}/independent-exports | quyền xuất source; manifest/config không secrets |
| FR-S12 | Support grant request/approve/revoke; audit list | approve tại API tenant; hết hạn/revoke chặn ngay |
| FR-S13 | GET /api/saas/context | public minimal tên/trạng thái, không DB/contact/secret; host-keyed cache |

FR-S14 Auth/routing: email có thể trùng hai DB; token/session tenant khớp host, cookie host-only Secure/HttpOnly/SameSite=Lax; CSRF với cookie auth; revocation dựa core, không auth stack thứ hai; returnUrl relative whitelist. Activation token hash, một lần, TTL24 giờ đề xuất, không log token. Admin doanh nghiệp full quyền hiện có nhưng không cross-tenant.

FR-S15 Provision: state/checkpoint/idempotency; credential ngẫu nhiên riêng, migrate đúng release, bootstrap không demo, exactly-once admin/location/kho; lỗi không delete DB; ACTIVE chỉ khi health đạt. Không tự email người khác nếu chưa có quy trình gửi được duyệt.

FR-S16 Migration: mỗi DB Flyway history, compatibility matrix app/schema; migration credential riêng; không startup migrate hàng loạt tự động. Tenant lỗi không làm mất tenant đã nâng cấp; dừng batch mặc định lỗi đầu. Rollback image kiểm schema, restore xét giao dịch sau backup.

FR-S17 Domain: chỉ hostname không IP/path/protocol/port/wildcard tùy ý; TXT nonce hết hạn24 giờ đề xuất; CNAME target cấu hình; TLS wildcard hệ thống và TLS custom quản lý riêng. Vendor DNS/CA chọn ở hạ tầng sau. Không fetch URL vào nội mạng; domain chỉ active sau verify/TLS. Renewal cảnh báo30/7/1ngày, không fallback HTTP. Primary redirect GET/HEAD, POST không redirect; host-only sessions phải login lại.

FR-S18 Gate: effective quyền = manifest/dependency ∩ entitlement ∩ enabled ∩ role/action ∩ kho scope; API trực tiếp chặn; quota transaction tránh race; giảm quota không delete. Tenant không upload/import code tùy ý vào runtime chung.

FR-S19 Background/cache: job/outbox/webhook có tenant/version; check state, open/clear scope; dedupe tenant-scoped, fairness/retry; cache key tenant/version. Domain cache TTL60s đề xuất và event invalidation. Không dùng context request cũ cho worker.

FR-S20 Files: UUID tenant namespace; path normalization/traversal/symlink checks; authorization download cả khi đoán ID; upload staging cũng tính quota để tránh race; export/report/signed URL tenant-bound và TTL ngắn cấu hình.

FR-S21 Backup/restore: mã hóa/access audit/checksum; DB+tệp có mốc nhất quán bằng maintenance hoặc snapshot strategy. pg_dump riêng không đủ chứng minh đồng bộ tệp. Restore tạm kiểm tenant/schema/count/ledger/file hashes; drain/pool close/atomic route switch; failure không đổi DB nguồn.

FR-S22 Standalone: cùng release MODE=SINGLE, config example/compose/runbook/license inventory/module closure; loại secrets/control-plane bindings; login và nghiệp vụ không gọi SaaS. Entitlement bàn giao nằm manifest theo thỏa thuận.

FR-S23 Audit/metrics: actor thật, tenant, action, outcome, requestId, before/after redacted; không password/token/JDBC; support ghi cả grant và hành động; metrics giới hạn cardinality; tenant không đọc logs của nhau.

## 4. UI và chất lượng

Admin list/form/detail tabs theo BA; async không spinner khóa toàn trang; lỗi có requestId và retry theo state. Xác nhận nhạy cảm nêu đúng tenant/tác động/backup. Tenant wizard giữ nguyên. Mobile320/390/430px không tràn, input>=16px/touch>=44px. Menu mở với skeleton, không chờ tải toàn bộ dữ liệu.

Mục tiêu thử:20 tenant/100 session; p95 warm host routing<=50ms; p95 API đọc thông thường<=1s với bộ fixture được công bố. Chưa là SLA. Đo pool saturation và DB failure tenant riêng; timeout/retry hữu hạn theo tenant. Retention30 ngày/RPO24 giờ/RTO4 giờ đề xuất cần đo và duyệt tài nguyên.

## 5. Bộ nghiệm thu

| AT | Kịch bản và kết quả cần chứng minh |
|---|---|
| AT-S01 | A/B cùng email/SKU; phiếu riêng; UI/API/file/export không truy cập chéo |
| AT-S02 | TokenA/hostB, header giả, unknown host: fail closed; không fallback DB |
| AT-S03 | Provision lỗi từng bước/retry: một DB/admin/địa điểm/kho; không demo |
| AT-S04 | Domain chưa verify không active; trùng409; TLS/login/primary/remove/expiry đúng |
| AT-S05 | SuspendA chặn phiên/job ghi; B hoạt động; resume không mất dữ liệu |
| AT-S06 | Module tắt/quota/dependency/API trực tiếp/concurrent request đều đúng |
| AT-S07 | Job/cache/webhook/file trộn tenant bị chặn; context clear kể cả exception |
| AT-S08 | Credential rotate/pool eviction/DBA down không làm lỗiB; pool bounded |
| AT-S09 | Backup/restoreA temp/đối chiếu/cutover; B không đổi; restore lỗi không đổi routing |
| AT-S10 | Migration lỗi dừng batch; version đúng; rollback kiểm compatibility |
| AT-S11 | SINGLE máy sạch, chặn network SaaS vẫn login/bán/mua/kho/backup; secret scan sạch |
| AT-S12 | Support thiếu approve/expired/revoke bị chặn; audit actor thật |
| AT-S13 | Chuyển bản sao dữ liệu cũ, đối chiếu account/role/phiếu/tồn/nợ/tệp; không reset |
| AT-S14 | Load/mobile/cookie/CSRF/returnUrl; report/evidence đầy đủ |

## 6. Release và phê duyệt

Local có control plane và hai DB test riêng; staging/pilot trước production. Single hiện tại tiếp tục vận hành trong chuyển đổi. Code chỉ bắt đầu sau duyệt đặc tả/task. Cắt chuyển live, DNS/restore production phải có kế hoạch reviewable, backup/cửa sổ bảo trì và duyệt riêng. Không coi test được liệt kê là đã đạt.
