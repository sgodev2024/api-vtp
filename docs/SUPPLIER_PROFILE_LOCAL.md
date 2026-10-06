# Nhà cung cấp — popup nhập thông tin

Cập nhật 06/10/2026. Triển khai local; NCC tiếp tục dùng `retail.partner` và cùng ID với Customer Master.

| Hạng mục | Trạng thái |
|---|---|
| Nút Thêm NCC và popup ba tab | Đã triển khai frontend |
| Bỏ nút Mở Customer Master tại Nhà cung cấp | Đã triển khai; các màn Khách hàng giữ điều hướng hiện có |
| 6 trường Thông tin chung bắt buộc | Kiểm tra frontend và API khi gửi supplierProfile=true |
| 5 trường Thông tin bổ sung tùy chọn | companyName / companyAddress mới; taxCode / contactName / ownerId dùng master chung |
| 4 trường Thông tin thanh toán tùy chọn | bankName / bankAccount / bankHolder theo quyền FINANCE; purchaseDays là số ngày 0–3650 |
| Migration V31 | Thêm company_name / company_address với mặc định rỗng; bảo toàn dữ liệu có sẵn |
| Sửa từ các module khác / import | Các trường công ty không gửi được giữ nguyên; cùng ID, không tạo bản sao NCC |
| Snapshot trên chứng từ mới | Bổ sung thông tin công ty; phiếu đã có không bị viết lại |
| Build backend / frontend | Maven package và Next production build đạt; TypeScript đạt |
| Kiểm thử NCC API/UI | 9 nhóm kiểm tra đạt; không có runtime error; kết quả tại supplier-profile-check-result.json |
| Hồi quy Customer Master | 12 kiểm tra API và 5 kiểm tra UI đạt trên database riêng aicrm-master-check; Bán hàng/Mua hàng/CRM dùng cùng hồ sơ |

Form NCC mới gợi ý mã NCC-…, mặc định Cá nhân và vai trò SUPPLIER. Khi sửa, giữ vai trò khách hàng/CTV, dữ liệu giao hàng và điều khoản khác của master. Các trường ngoài ba tab không bị xóa vì popup chuyên cho NCC. Người quản lý là tài khoản đang hoạt động trong cùng công ty. Địa chỉ tại tab bổ sung lưu `company_address`, độc lập địa chỉ liên hệ tại tab chung.

Kiểm thử API/UI ghi vào database tạm `aicrm-supplier-check` trên 8186; deployment vận hành chỉ đăng nhập/đọc. Script: `scripts/supplier-profile-check.mjs`. Popup: `frontend/app/retail/supplier-dialog.tsx`.

Đã xem ảnh desktop của tab Thông tin chung/Thông tin bổ sung và mobile 390px của tab Thông tin thanh toán tại `docs/screenshots/suppliers/`. Kiểm tra gồm lưu chỉ sáu trường bắt buộc, cập nhật đủ trường tùy chọn và mở lại, giữ số tài khoản có số 0 đầu, hai địa chỉ độc lập, quyền ngân hàng, email/ngày công nợ/người quản lý không hợp lệ, version và replay cùng Idempotency-Key. Không thêm retail data kiểm thử vào database vận hành. Deployment kiểm thử NCC đã được xóa sau kiểm chứng.
