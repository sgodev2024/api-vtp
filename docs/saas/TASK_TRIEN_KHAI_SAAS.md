# Task triển khai SaaS 1.0

Ngày 11/10/2026. 35 task, tất cả NOT_STARTED. [SAAS_TASKS.json](SAAS_TASKS.json) là nguồn cập nhật trạng thái SaaS, không đổi backlog audit nghiệp vụ hiện hành.

## 1. Đội triển khai

3 intern fullstack: Intern1 metadata/API/operation/gói/benchmark; Intern2 admin UI/tích hợp/regression; Intern3 domain/files/backup UI/standalone runbook. Lead/senior là vai trò cần bố trí, chưa giả định đã có người. Lead phụ trách routing/auth/DB/secret/provisioner/migration/restore/cutover. Intern tham gia các phần này dưới review, không tự thao tác production. Task UI chưa Done nếu chỉ mock API.

Task intern mục tiêu 1–3 ngày tập trung khi đủ dependency; chưa cam kết lịch tổng. Nếu vượt3ngày, lead chia API/UI/test thành PR nhỏ trước khi giao. Không phân công nhiều intern cùng sửa auth/datasource đồng thời.

## 2. Đợt và cổng nghiệm thu

1. Nền tảng local:01–14,21–23. Gate host/auth/DB/file/job isolation hai tenant, SINGLE cũ chạy.
2. Gói/module/subdomain:15–17. Gate API direct/dependency/quota concurrent.
3. Tên miền riêng:18–20. Gate ownership/DNS/TLS/primary/renewal; chưa DNS production.
4. Vận hành/bàn giao:24–32. Gate backup/restore/migration/support/SINGLE/rehearsal dữ liệu cũ.
5. Kiểm định:33–35. Test âm/cross-tenant viết ngay từng task, không đợi đợt5. Chỉ production sau evidence và duyệt.

## 3. Task cụ thể

| ID | Công việc | Phụ trách | Phụ thuộc | FR / AT | Sản phẩm và tiêu chí Done |
|---|---|---|---|---|---|
| SAAS-01 | Audit core và sequence host/DB/auth | Lead | Không | FR-S14 / AT-S02 | Inventory auth/datasource/worker và sơ đồ điểm sửa, giữ SINGLE |
| SAAS-02 | Schema control plane, API/state machine | Lead | SAAS-01 | FR-S01, FR-S16 / AT-S10 | DDL mới/OpenAPI/compatibility; không sửa migration lịch sử |
| SAAS-03 | Fixture local control plane và hai DB | Intern 1 | SAAS-02 | FR-S15 / AT-S01 | Compose riêng; credential/seed test riêng; không production secrets |
| SAAS-04 | API CRUD doanh nghiệp | Intern 1 | SAAS-02 | FR-S01 / AT-S03 | Validation unique/version/quyền; không trả secret |
| SAAS-05 | UI danh sách/form doanh nghiệp | Intern 2 | SAAS-04 | FR-S01 / AT-S14 | STT/filter/page/form/mobile/error states |
| SAAS-06 | Host resolver và trusted proxy | Lead | SAAS-02 | FR-S14 / AT-S02 | Normalize/unknown-host/header spoof tests; trước auth |
| SAAS-07 | Dynamic pool và RLS routing | Lead | SAAS-03, SAAS-06 | FR-S14, FR-S16 / AT-S08 | Bounded pools/eviction/transaction scope; CP datasource tách |
| SAAS-08 | Auth session/token ràng buộc tenant host | Lead | SAAS-07 | FR-S14 / AT-S02 | Cross-host deny/cookie/CSRF/login lookup/revocation |
| SAAS-09 | Frontend API same-origin/context/login | Intern 2 | SAAS-08 | FR-S13 / AT-S14 | Không hardcode API host; minimal context; trang host lỗi |
| SAAS-10 | Operation/idempotency/checkpoint API | Intern 1 | SAAS-02 | FR-S07 / AT-S03 | 202/status/key+hash/409/lock/retry validation |
| SAAS-11 | Provision DB/secret/migration executor | Lead | SAAS-07, SAAS-10 | FR-S15, FR-S16 / AT-S03 | Least privilege/checkpoint/error; không delete dữ liệu |
| SAAS-12 | Bootstrap/admin activation/default location/kho | Intern 1 | SAAS-11 | FR-S15 / AT-S03 | Exactly once/no demo/hash token/TTL; lead review auth |
| SAAS-13 | UI provision status/retry | Intern 2 | SAAS-10, SAAS-12 | FR-S02, FR-S07 / AT-S03 | Step/error/state; refresh không duplicate task |
| SAAS-14 | Suspend/resume/maintenance/drain | Lead | SAAS-08, SAAS-10 | FR-S03 / AT-S05 | Invalidate session/cache; chặn ghi/jobA; B không ảnh hưởng |
| SAAS-15 | API gói/entitlement/dependency | Intern 1 | SAAS-04 | FR-S04 / AT-S06 | Limits null/0/version/closure; hạ quota không xóa |
| SAAS-16 | Backend gate module và atomic quota | Lead | SAAS-14, SAAS-15 | FR-S18 / AT-S06 | API direct deny/concurrent quota/scope |
| SAAS-17 | UI gói/module/limits | Intern 2 | SAAS-15, SAAS-16 | FR-S04 / AT-S06 | Effective permissions/dependency/errors; tenant summary |
| SAAS-18 | Domain CRUD/UI TXT/CNAME | Intern 3 | SAAS-04, SAAS-06 | FR-S05 / AT-S04 | Hostname unique/IDNA/instructions/token không log |
| SAAS-19 | DNS ownership/TLS adapter | Lead | SAAS-18 | FR-S17 / AT-S04 | Nonce expiry/verify/TLS gate/renewal provider contract |
| SAAS-20 | Primary/remove/renewal UI và tác vụ | Intern 3 | SAAS-19 | FR-S06, FR-S17 / AT-S04 | Còn default route/GET redirect/invalidation/expiry |
| SAAS-21 | Tenant scope job/outbox/webhook/cache | Lead | SAAS-07, SAAS-14 | FR-S19 / AT-S07 | Context finally/dedupe/fairness/retry/suspend gates |
| SAAS-22 | File namespace/auth/quota | Intern 3 | SAAS-08, SAAS-16 | FR-S20 / AT-S07 | Traversal/download scope/signed link/staging quota race |
| SAAS-23 | Audit/metrics redaction và UI operation | Intern 2 | SAAS-10, SAAS-14 | FR-S23 / AT-S12 | Actor/requestId/scope/error masking; không secret |
| SAAS-24 | Backup consistency/manifest/encryption | Lead | SAAS-21, SAAS-22 | FR-S21 / AT-S09 | DB+tệp cùngmốc/checksum/retention/READY conditions |
| SAAS-25 | UI backup và restore validation | Intern 3 | SAAS-24 | FR-S08, FR-S09 / AT-S09 | Version/evidence/quyền/xác nhận đúngtenant; không overwrite |
| SAAS-26 | Restore temp/cutover rehearsal | Lead | SAAS-24, SAAS-25 | FR-S21 / AT-S09 | Đối chiếu/drain/pool/atomic routing; fail không đổi nguồn |
| SAAS-27 | Migration batch/version/pilot | Lead | SAAS-11, SAAS-24 | FR-S16, FR-S10 / AT-S10 | Compatibility/pause/backup/rollback xét giao dịch |
| SAAS-28 | Support grant approve/revoke/expiry API | Lead | SAAS-08, SAAS-23 | FR-S12 / AT-S12 | Tenant approve/timebound/actor thật; backend deny khi expired |
| SAAS-29 | UI phiên hỗ trợ/audit | Intern 2 | SAAS-28 | FR-S12 / AT-S12 | Request/approve/revoke theo quyền; scope/lýdo/expiry |
| SAAS-30 | MODE SINGLE và source package/export | Lead | SAAS-16, SAAS-24 | FR-S22, FR-S11 / AT-S11 | No CP dependency/secret; release/modules/licenses/config |
| SAAS-31 | Runbook standalone và test máy sạch | Intern 3 | SAAS-30 | FR-S22, FR-S11 / AT-S11 | Compose/install/backup/restore; network SaaS blocked smoke |
| SAAS-32 | Rehearsal dữ liệu hiện tại | Lead | SAAS-26, SAAS-27, SAAS-30 | FR-S21, FR-S22 / AT-S13 | Đối chiếu IDs/account/role/tồn/nợ/phiếu/tệp; cutover plan |
| SAAS-33 | Regression hai tenant/mobile | Intern 2 | SAAS-13, SAAS-17, SAAS-20, SAAS-22, SAAS-29 | FR-S14, FR-S18 / AT-S01, AT-S02, AT-S06, AT-S14 | Fixture API/UI/cross-tenant/screenshot 320/390/430px |
| SAAS-34 | Load test và failure isolation | Intern 1 | SAAS-21, SAAS-33 | FR-S19 / AT-S08, AT-S14 | 20 tenant/100 sessions; p95/poolmax/report; lead phân tích |
| SAAS-35 | Pilot checklist/nghiệm thu | Lead | SAAS-26, SAAS-27, SAAS-31, SAAS-32, SAAS-33, SAAS-34 | FR-S01, FR-S22 / AT-S01, AT-S11, AT-S13 | AT-S01..14 evidence; backup/rollback; chủ sản phẩm duyệt |

## 4. Definition of Ready / Done

Ready: đọc BA/SRS, đủ dependency, schema/API/fixture/test được lead review, giải quyết câu hỏi bảo mật. Done: implementation/API/UI trong phạm vi, validation/error/loading states, test tích cực/âm/cross-tenant, migration impact, lead review, evidence và commit/PR trong tracker. Backend xong/UI chưa xong ghi riêng, không đánh task toàn bộ DONE. Task UI cần tích hợp API thật. Task security cần lead nghiệm thu, không chỉ build pass.

Tracker: status NOT_STARTED/IN_PROGRESS/IN_REVIEW/BLOCKED/DONE; backend/frontend NOT_STARTED/IN_PROGRESS/DONE/NA; TO_BE_SCOPED phải xác định NA hay có UI trước nhận task. Thêm owner thực tế/reviewer/date/commit/evidence/blocker khi bắt đầu. Không secrets trong evidence. Mỗi PR một task hoặc phần có liên kết. Không commit/push/deploy trong phạm vi lập tài liệu này.

## 5. Traceability và duyệt

Mỗi task có FR/AT, SRS mô tả điều kiện nghiệm thu; BA quy định luồng. AT-S01..14 phải có evidence trước pilot sign-off. Chủ sản phẩm cần duyệt bộ tài liệu/task và bố trí lead. Backup30 ngày/RPO24 giờ/RTO4 giờ/pool sizing/load target là đề xuất cần đo và duyệt tài nguyên trước production. Domain vendor/CA và host quản trị được lead đề xuất ở đợt hạ tầng; không tự đổi SSL toàn zone. Chưa triển khai code hoặc cắt chuyển hệ thống hiện có.
