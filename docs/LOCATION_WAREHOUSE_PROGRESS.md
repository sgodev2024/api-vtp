# Địa điểm / Chi nhánh, kho và thiết lập ban đầu

Cập nhật: 07/10/2026. Đã triển khai và kiểm tra trên local. Menu Địa điểm / Chi nhánh nằm trong Vận hành chung; nhóm phân quyền tương ứng đã đồng bộ. Thông tin phát hành Git/server được xác nhận khi triển khai.

| Hạng mục | Frontend | Backend | Kết quả |
|---|---|---|---|
| Bước 1: Doanh nghiệp & Địa điểm | Hoàn thành | company_profile + cập nhật địa điểm có sẵn | Lưu tên doanh nghiệp, MST, liên hệ, địa chỉ, quản lý; không tạo trùng |
| Bước 2: Kho chính | Hoàn thành | Cập nhật kho có sẵn | Xác nhận kho thuộc địa điểm đầu tiên |
| Bước 3: Danh mục | Hoàn thành | Dùng API danh mục hiện có | Mở danh mục để bổ sung, quay lại tiếp tục |
| Bước 4: Dữ liệu đầu kỳ | Hoàn thành | Dùng chứng từ/Excel hiện có | Nhập tồn, phải thu/phải trả; hoàn tất không tự duyệt phiếu |
| Bước 5: Hoàn tất | Hoàn thành | Lưu tiến độ 1–5 | Mở Tổng quan; quay lại bước đã hoàn thành để sửa |
| Mở wizard từ Trang chủ khi chưa hoàn tất | Hoàn thành | Kiểm UPDATE setup + ALL_BRANCHES | Áp dụng cho người quản trị có quyền, giữ route mở trực tiếp module |
| Địa điểm / Chi nhánh | Hoàn thành | List/create/update/deactivate/export | Nằm trong Vận hành chung |
| Danh sách kho | Hoàn thành | List/create/update/deactivate/export | Nằm trong Kho, tách Kho & tồn và Cấu hình kho |
| Mặc định, tạo kho kèm địa điểm | Hoàn thành | Giao dịch nguyên tử + unique index | Địa điểm mặc định toàn công ty, kho mặc định theo địa điểm |
| Phân quyền từng màn hình | Hoàn thành | VIEW/CREATE/UPDATE/DEACTIVATE/EXPORT + scope | Manager không tự cấp quyền; kiểm phạm vi cả xuất và ngừng |
| Chặn ngừng hồ sơ | Hoàn thành | Kiểm mặc định, tồn/giá trị, serial, phiếu, giao hàng, bảo hành, kho con | Không xóa lịch sử |
| Kho cách ly / hàng lỗi | Hoàn thành | Chặn POS, đơn bán, báo giá và xuất bán | Vẫn dùng cho xử lý tồn phù hợp |

## Kiểm tra

- Build frontend/backend thành công; TypeScript và ESLint của component mới đạt.
- 11 nhóm kiểm tra API: `location-onboarding-check-result.json`.
- 6 nhóm kiểm tra UI: `location-onboarding-ui-result.json`, gồm desktop/mobile, lưu form và mở wizard từ Trang chủ local.
- 23 kiểm tra hồi quy phân quyền: `module-permission-check-result.json`.
- Dữ liệu thử ghi vào project Docker riêng `aicrm-permission-check`; không ghi dữ liệu thử vào database vận hành.
- Migration V33 đã áp dụng thành công trên local, các dịch vụ healthy. Dữ liệu nghiệp vụ hiện có giữ nguyên: 2 SKU, 4 chứng từ.
- Sao lưu trước migration: `backups/locations/before-v33.sql` (gitignored). Ảnh kiểm tra: `backups/locations/ui/` (gitignored).

## Mở màn hình local

- Thiết lập: http://localhost:3205/business/retail/setup
- Địa điểm / Chi nhánh: http://localhost:3205/business/retail/locations
- Danh sách kho: http://localhost:3205/business/retail/warehouses

## Phạm vi còn lại của backlog cũ

Wizard hiện là luồng cơ bản đã duyệt. Ngày hiệu lực số dư, logo in phiếu, kho mặc định theo từng tài khoản và checklist đối chiếu nâng cao chưa được đánh dấu hoàn tất. Hồ sơ doanh nghiệp mới cần nhập tên doanh nghiệp khi xác nhận bước đầu; không suy đoán tên từ tài khoản admin và không đổi tên chi nhánh/kho đã có.

Chuyển menu ngày 07/10/2026: đã kiểm tra API navigation và danh mục quyền trên local; Địa điểm / Chi nhánh thuộc `retail-general` (Vận hành chung).
