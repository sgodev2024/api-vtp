# Phiếu giao task SaaS cho đội dev

Ngày 11/10/2026. Tất cả task chưa bắt đầu. Mỗi phiếu nêu đầu vào, bước làm và bàn giao. Backend/frontend=NA nghĩa task không yêu cầu thay đổi lớp đó; không có nghĩa hệ thống SaaS đã hoàn thành. Task hỗn hợp cần cập nhật riêng API/UI.

## SAAS-01 — Audit core và sequence host/DB/auth

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: Không.

Đầu vào: Đọc BA_SAAS.md và FR-S14 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ source hiện hành.

Backend: NA; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: Inventory auth/datasource/worker và sơ đồ điểm sửa, giữ SINGLE.

Nghiệm thu: AT-S02; đối chiếu FR-S14. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-02 — Schema control plane, API/state machine

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-01.

Đầu vào: Đọc BA_SAAS.md và FR-S01, FR-S16 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-01.

Backend: NA; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: DDL mới/OpenAPI/compatibility; không sửa migration lịch sử.

Nghiệm thu: AT-S10; đối chiếu FR-S01, FR-S16. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-03 — Fixture local control plane và hai DB

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-02.

Đầu vào: Đọc BA_SAAS.md và FR-S15 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-02.

Backend: NA; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: Compose riêng; credential/seed test riêng; không production secrets.

Nghiệm thu: AT-S01; đối chiếu FR-S15. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-04 — API CRUD doanh nghiệp

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-02.

Đầu vào: Đọc BA_SAAS.md và FR-S01 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-02.

Backend: NOT_STARTED; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Validation unique/version/quyền; không trả secret.

Nghiệm thu: AT-S03; đối chiếu FR-S01. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-05 — UI danh sách/form doanh nghiệp

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-04.

Đầu vào: Đọc BA_SAAS.md và FR-S01 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-04.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: STT/filter/page/form/mobile/error states.

Nghiệm thu: AT-S14; đối chiếu FR-S01. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-06 — Host resolver và trusted proxy

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-02.

Đầu vào: Đọc BA_SAAS.md và FR-S14 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-02.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Normalize/unknown-host/header spoof tests; trước auth.

Nghiệm thu: AT-S02; đối chiếu FR-S14. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-07 — Dynamic pool và RLS routing

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-03, SAAS-06.

Đầu vào: Đọc BA_SAAS.md và FR-S14, FR-S16 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-03, SAAS-06.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Bounded pools/eviction/transaction scope; CP datasource tách.

Nghiệm thu: AT-S08; đối chiếu FR-S14, FR-S16. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-08 — Auth session/token ràng buộc tenant host

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-07.

Đầu vào: Đọc BA_SAAS.md và FR-S14 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-07.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Cross-host deny/cookie/CSRF/login lookup/revocation.

Nghiệm thu: AT-S02; đối chiếu FR-S14. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-09 — Frontend API same-origin/context/login

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-08.

Đầu vào: Đọc BA_SAAS.md và FR-S13 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-08.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Không hardcode API host; minimal context; trang host lỗi.

Nghiệm thu: AT-S14; đối chiếu FR-S13. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-10 — Operation/idempotency/checkpoint API

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-02.

Đầu vào: Đọc BA_SAAS.md và FR-S07 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-02.

Backend: NOT_STARTED; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: 202/status/key+hash/409/lock/retry validation.

Nghiệm thu: AT-S03; đối chiếu FR-S07. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-11 — Provision DB/secret/migration executor

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-07, SAAS-10.

Đầu vào: Đọc BA_SAAS.md và FR-S15, FR-S16 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-07, SAAS-10.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Least privilege/checkpoint/error; không delete dữ liệu.

Nghiệm thu: AT-S03; đối chiếu FR-S15, FR-S16. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-12 — Bootstrap/admin activation/default location/kho

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-11.

Đầu vào: Đọc BA_SAAS.md và FR-S15 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-11.

Backend: NOT_STARTED; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Exactly once/no demo/hash token/TTL; lead review auth.

Nghiệm thu: AT-S03; đối chiếu FR-S15. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-13 — UI provision status/retry

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-10, SAAS-12.

Đầu vào: Đọc BA_SAAS.md và FR-S02, FR-S07 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-10, SAAS-12.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Step/error/state; refresh không duplicate task.

Nghiệm thu: AT-S03; đối chiếu FR-S02, FR-S07. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-14 — Suspend/resume/maintenance/drain

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-08, SAAS-10.

Đầu vào: Đọc BA_SAAS.md và FR-S03 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-08, SAAS-10.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Invalidate session/cache; chặn ghi/jobA; B không ảnh hưởng.

Nghiệm thu: AT-S05; đối chiếu FR-S03. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-15 — API gói/entitlement/dependency

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-04.

Đầu vào: Đọc BA_SAAS.md và FR-S04 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-04.

Backend: NOT_STARTED; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Limits null/0/version/closure; hạ quota không xóa.

Nghiệm thu: AT-S06; đối chiếu FR-S04. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-16 — Backend gate module và atomic quota

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-14, SAAS-15.

Đầu vào: Đọc BA_SAAS.md và FR-S18 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-14, SAAS-15.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: API direct deny/concurrent quota/scope.

Nghiệm thu: AT-S06; đối chiếu FR-S18. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-17 — UI gói/module/limits

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-15, SAAS-16.

Đầu vào: Đọc BA_SAAS.md và FR-S04 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-15, SAAS-16.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Effective permissions/dependency/errors; tenant summary.

Nghiệm thu: AT-S06; đối chiếu FR-S04. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-18 — Domain CRUD/UI TXT/CNAME

Phụ trách: Intern 3. Review: Lead/senior. Phụ thuộc: SAAS-04, SAAS-06.

Đầu vào: Đọc BA_SAAS.md và FR-S05 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-04, SAAS-06.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Hostname unique/IDNA/instructions/token không log.

Nghiệm thu: AT-S04; đối chiếu FR-S05. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-19 — DNS ownership/TLS adapter

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-18.

Đầu vào: Đọc BA_SAAS.md và FR-S17 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-18.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Nonce expiry/verify/TLS gate/renewal provider contract.

Nghiệm thu: AT-S04; đối chiếu FR-S17. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-20 — Primary/remove/renewal UI và tác vụ

Phụ trách: Intern 3. Review: Lead/senior. Phụ thuộc: SAAS-19.

Đầu vào: Đọc BA_SAAS.md và FR-S06, FR-S17 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-19.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Còn default route/GET redirect/invalidation/expiry.

Nghiệm thu: AT-S04; đối chiếu FR-S06, FR-S17. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-21 — Tenant scope job/outbox/webhook/cache

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-07, SAAS-14.

Đầu vào: Đọc BA_SAAS.md và FR-S19 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-07, SAAS-14.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Context finally/dedupe/fairness/retry/suspend gates.

Nghiệm thu: AT-S07; đối chiếu FR-S19. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-22 — File namespace/auth/quota

Phụ trách: Intern 3. Review: Lead/senior. Phụ thuộc: SAAS-08, SAAS-16.

Đầu vào: Đọc BA_SAAS.md và FR-S20 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-08, SAAS-16.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Traversal/download scope/signed link/staging quota race.

Nghiệm thu: AT-S07; đối chiếu FR-S20. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-23 — Audit/metrics redaction và UI operation

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-10, SAAS-14.

Đầu vào: Đọc BA_SAAS.md và FR-S23 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-10, SAAS-14.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Actor/requestId/scope/error masking; không secret.

Nghiệm thu: AT-S12; đối chiếu FR-S23. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-24 — Backup consistency/manifest/encryption

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-21, SAAS-22.

Đầu vào: Đọc BA_SAAS.md và FR-S21 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-21, SAAS-22.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: DB+tệp cùngmốc/checksum/retention/READY conditions.

Nghiệm thu: AT-S09; đối chiếu FR-S21. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-25 — UI backup và restore validation

Phụ trách: Intern 3. Review: Lead/senior. Phụ thuộc: SAAS-24.

Đầu vào: Đọc BA_SAAS.md và FR-S08, FR-S09 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-24.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Version/evidence/quyền/xác nhận đúngtenant; không overwrite.

Nghiệm thu: AT-S09; đối chiếu FR-S08, FR-S09. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-26 — Restore temp/cutover rehearsal

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-24, SAAS-25.

Đầu vào: Đọc BA_SAAS.md và FR-S21 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-24, SAAS-25.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Đối chiếu/drain/pool/atomic routing; fail không đổi nguồn.

Nghiệm thu: AT-S09; đối chiếu FR-S21. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-27 — Migration batch/version/pilot

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-11, SAAS-24.

Đầu vào: Đọc BA_SAAS.md và FR-S16, FR-S10 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-11, SAAS-24.

Backend: NOT_STARTED; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Compatibility/pause/backup/rollback xét giao dịch.

Nghiệm thu: AT-S10; đối chiếu FR-S16, FR-S10. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-28 — Support grant approve/revoke/expiry API

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-08, SAAS-23.

Đầu vào: Đọc BA_SAAS.md và FR-S12 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-08, SAAS-23.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: Tenant approve/timebound/actor thật; backend deny khi expired.

Nghiệm thu: AT-S12; đối chiếu FR-S12. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-29 — UI phiên hỗ trợ/audit

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-28.

Đầu vào: Đọc BA_SAAS.md và FR-S12 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-28.

Backend: NA; frontend: NOT_STARTED. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Xác định route và tái sử dụng component/style core hiện hành.
2. Dựng form/list/detail theo task; validation và loading/empty/error states.
3. Tích hợp API thật; kiểm quyền và trạng thái; không chỉ mock.
4. Chạy desktop/mobile và ca âm theo AT; lưu screenshot/report.

Bàn giao: Request/approve/revoke theo quyền; scope/lýdo/expiry.

Nghiệm thu: AT-S12; đối chiếu FR-S12. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-30 — MODE SINGLE và source package/export

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-16, SAAS-24.

Đầu vào: Đọc BA_SAAS.md và FR-S22, FR-S11 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-16, SAAS-24.

Backend: NOT_STARTED; frontend: NOT_STARTED. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chốt DTO/schema/service boundary và quyền theo FR.
2. Triển khai validation, transaction/state và tenant scope trong phạm vi task.
3. Viết test thành công, lỗi, chạy lặp/concurrent/cross-tenant khi liên quan.
4. Tích hợp fixture hai tenant; chạy AT tương ứng, lưu report và gửi lead review.

Bàn giao: No CP dependency/secret; release/modules/licenses/config.

Nghiệm thu: AT-S11; đối chiếu FR-S22, FR-S11. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-31 — Runbook standalone và test máy sạch

Phụ trách: Intern 3. Review: Lead/senior. Phụ thuộc: SAAS-30.

Đầu vào: Đọc BA_SAAS.md và FR-S22 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-30.

Backend: NA; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: Compose/install/backup/restore; network SaaS blocked smoke.

Nghiệm thu: AT-S11; đối chiếu FR-S22. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-32 — Rehearsal dữ liệu hiện tại

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-26, SAAS-27, SAAS-30.

Đầu vào: Đọc BA_SAAS.md và FR-S21, FR-S22 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-26, SAAS-27, SAAS-30.

Backend: NA; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: Đối chiếu IDs/account/role/tồn/nợ/phiếu/tệp; cutover plan.

Nghiệm thu: AT-S13; đối chiếu FR-S21, FR-S22. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-33 — Regression hai tenant/mobile

Phụ trách: Intern 2. Review: Lead/senior. Phụ thuộc: SAAS-13, SAAS-17, SAAS-20, SAAS-22, SAAS-29.

Đầu vào: Đọc BA_SAAS.md và FR-S14, FR-S18 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-13, SAAS-17, SAAS-20, SAAS-22, SAAS-29.

Backend: NA; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: Fixture API/UI/cross-tenant/screenshot 320/390/430px.

Nghiệm thu: AT-S01, AT-S02, AT-S06, AT-S14; đối chiếu FR-S14, FR-S18. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-34 — Load test và failure isolation

Phụ trách: Intern 1. Review: Lead/senior. Phụ thuộc: SAAS-21, SAAS-33.

Đầu vào: Đọc BA_SAAS.md và FR-S19 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-21, SAAS-33.

Backend: NA; frontend: NA. Ước lượng: 1–3 ngày tập trung sau khi đủ phụ thuộc; chia nhỏ nếu vượt 3 ngày.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: 20 tenant/100 sessions; p95/poolmax/report; lead phân tích.

Nghiệm thu: AT-S08, AT-S14; đối chiếu FR-S19. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

## SAAS-35 — Pilot checklist/nghiệm thu

Phụ trách: Lead. Review: Lead/senior. Phụ thuộc: SAAS-26, SAAS-27, SAAS-31, SAAS-32, SAAS-33, SAAS-34.

Đầu vào: Đọc BA_SAAS.md và FR-S01, FR-S22 trong SRS_SAAS.md; Đối chiếu hợp đồng/fixture từ SAAS-26, SAAS-27, SAAS-31, SAAS-32, SAAS-33, SAAS-34.

Backend: NA; frontend: NA. Ước lượng: Lead ước lượng sau SAAS-01; chưa cam kết lịch.

1. Chuẩn bị inventory hoặc fixture/phương án trong phạm vi task.
2. Thực hiện rà soát/diễn tập/kiểm thử trên local hoặc bản sao theo phụ thuộc.
3. Đối chiếu tiêu chí và ghi kết quả/evidence; không tác động dữ liệu live.
4. Lead review kết quả và chốt đầu vào cho task kế tiếp.

Bàn giao: AT-S01..14 evidence; backup/rollback; chủ sản phẩm duyệt.

Nghiệm thu: AT-S01, AT-S11, AT-S13; đối chiếu FR-S01, FR-S22. Ghi evidence, reviewer, commit/PR và ngày cập nhật vào tracker trước DONE.

