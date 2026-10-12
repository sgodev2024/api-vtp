# Ma trận truy vết và checklist duyệt SaaS

Ngày 11/10/2026. Kiến trúc đã chốt; các mục chi tiết dưới đây trình duyệt trước code.

## Truy vết yêu cầu

| Yêu cầu | Task chịu trách nhiệm |
|---|---|
| FR-S01 | SAAS-02, SAAS-04, SAAS-05, SAAS-35 |
| FR-S02 | SAAS-13 |
| FR-S03 | SAAS-14 |
| FR-S04 | SAAS-15, SAAS-17 |
| FR-S05 | SAAS-18 |
| FR-S06 | SAAS-20 |
| FR-S07 | SAAS-10, SAAS-13 |
| FR-S08 | SAAS-25 |
| FR-S09 | SAAS-25 |
| FR-S10 | SAAS-27 |
| FR-S11 | SAAS-30 |
| FR-S12 | SAAS-28, SAAS-29 |
| FR-S13 | SAAS-09 |
| FR-S14 | SAAS-01, SAAS-06, SAAS-07, SAAS-08, SAAS-33 |
| FR-S15 | SAAS-03, SAAS-11, SAAS-12 |
| FR-S16 | SAAS-02, SAAS-07, SAAS-11, SAAS-27 |
| FR-S17 | SAAS-19, SAAS-20 |
| FR-S18 | SAAS-16, SAAS-33 |
| FR-S19 | SAAS-21, SAAS-34 |
| FR-S20 | SAAS-22 |
| FR-S21 | SAAS-24, SAAS-26, SAAS-32 |
| FR-S22 | SAAS-30, SAAS-31, SAAS-32, SAAS-35 |
| FR-S23 | SAAS-23 |
| AT-S01 | SAAS-03, SAAS-33, SAAS-35 |
| AT-S02 | SAAS-01, SAAS-06, SAAS-08, SAAS-33 |
| AT-S03 | SAAS-04, SAAS-10, SAAS-11, SAAS-12, SAAS-13 |
| AT-S04 | SAAS-18, SAAS-19, SAAS-20 |
| AT-S05 | SAAS-14 |
| AT-S06 | SAAS-15, SAAS-16, SAAS-17, SAAS-33 |
| AT-S07 | SAAS-21, SAAS-22 |
| AT-S08 | SAAS-07, SAAS-34 |
| AT-S09 | SAAS-24, SAAS-25, SAAS-26 |
| AT-S10 | SAAS-02, SAAS-27 |
| AT-S11 | SAAS-30, SAAS-31, SAAS-35 |
| AT-S12 | SAAS-23, SAAS-28, SAAS-29 |
| AT-S13 | SAAS-32, SAAS-35 |
| AT-S14 | SAAS-05, SAAS-09, SAAS-33, SAAS-34 |

## Checklist quyết định

| Nội dung | Trạng thái |
|---|---|
| Database riêng, source chuẩn chung, subdomain/custom domain, tách độc lập | Đã chốt bởi chủ sản phẩm |
| Cấp mới do quản trị, không public signup/thanh toán tự động | Đề xuất trong BA, chờ duyệt chi tiết |
| Host quản trị riêng và hỗ trợ phải doanh nghiệp cho phép | Đề xuất, chờ duyệt chi tiết |
| Domain con; apex chưa trong đợt đầu | Đề xuất, chờ duyệt chi tiết |
| Không tự xóa dữ liệu, restore tạm trước cutover | Quy tắc an toàn triển khai trong SRS, chờ duyệt bộ |
| Lead/senior review routing/auth/DB/migration/restore | Cần bố trí người thực tế trước giao task nhạy cảm |
| Retention 30 ngày, RPO24 giờ/RTO4 giờ | Mục tiêu đề xuất, cần tài nguyên và diễn tập trước cam kết |
| Sizing pool/load target và nhà cung cấp DNS/TLS | Lead đánh giá trước production, không khóa vendor trong BA |
| Bắt đầu code local | Chưa được duyệt trong đợt lập tài liệu |
| Commit/push/server/DNS/cắt chuyển live | Ngoài phạm vi đợt tài liệu; cần yêu cầu triển khai cụ thể |

Không dùng thời gian 1–3 ngày/task để cộng thành lịch cam kết: có phụ thuộc và vai trò lead chưa bố trí. Sau audit SAAS-01/02 chốt lịch và tách task lớn. BA/SRS hiện hành vẫn là baseline nghiệp vụ; tracker SaaS không đổi trạng thái backlog cũ.
