# Customer Master — triển khai local

Cập nhật 06/10/2026. Đã được người dùng cho phép commit và đồng bộ source lên repository `sgodev2024/aicrm-java`; ứng dụng chỉ chạy local. Căn cứ BA 2.0 F-21 và phụ lục D-06.

## Đã triển khai

- Module cấp cao **Khách hàng / Đối tác** có hai mục **Khách hàng** (CUSTOMER) và **Đối tác** (tất cả hồ sơ, lọc vai trò). CRM và Bán hàng mỗi module có mục **Khách hàng** riêng (CUSTOMER), dùng cùng API/dữ liệu master; Nhà cung cấp tiếp tục là góc nhìn SUPPLIER tại Mua hàng.
- Danh sách có STT, tìm mã/tên/điện thoại/email/MST, lọc trạng thái/nhóm khách, phân trang 50; gợi ý mã khi thêm, sửa và các tab hồ sơ. Các màn Khách hàng/NCC cố định bộ lọc vai trò; màn Đối tác cho chọn vai trò.
- Một ID có nhiều vai trò Khách hàng, Nhà cung cấp, CTV; hoạt động/ngừng từng vai trò và cả hồ sơ. Màn Nhà cung cấp trong Mua hàng dùng chính master; thêm/sửa bằng popup ba tab riêng cho NCC, giữ các vai trò và thông tin master còn lại.
- Bán/Mua chọn theo vai trò đang hoạt động; Bán tự điền địa chỉ giao mặc định từ master; CTV dùng chung master cho nghiệp vụ hoa hồng. Dashboard đếm theo vai trò thay vì kind đơn.
- Hồ sơ có loại cá nhân/doanh nghiệp, liên hệ, địa chỉ giao mặc định, nhóm khách, MST, điều khoản/số ngày bán và mua riêng, ngân hàng và người phụ trách. Popup NCC bắt buộc mã, tên, loại đối tượng, điện thoại, email, địa chỉ; thông tin bổ sung/thanh toán tùy chọn. Tên và địa chỉ công ty lưu riêng với tên và địa chỉ NCC. Chi tiết cập nhật local: [SUPPLIER_PROFILE_LOCAL.md](./SUPPLIER_PROFILE_LOCAL.md).
- Kiểm trùng mã/MST trong tenant; chuẩn hóa MST; trùng điện thoại/email báo cảnh báo trên chi tiết, không tự gộp.
- Quyền READ xem master, SETUP sửa; FINANCE xem/sửa ngân hàng và số dư; CRM xem/ghi chăm sóc. Lịch sử giới hạn quyền module và scope; số dư AR/AP tách riêng theo scope.
- Dùng Idempotency-Key của Core, version chống ghi đè, audit/outbox hiện có. Không bỏ/ngừng vai trò còn đơn mở. Hồ sơ ngừng không tạo giao dịch mới; thu nợ và nghiệp vụ có nguồn vẫn được xử lý theo quyền.
- Phiếu mới lưu bản chụp thông tin đối tác trong metadata; sửa master không đổi tên trên danh sách/chi tiết phiếu đã tạo. Công nợ dùng số ngày mua/bán từ bản chụp; trường chưa thỏa thuận để trống ngày đến hạn. Phiếu cũ không có trường điều khoản giữ cách tính trước đây.
- Import PARTNERS cập nhật theo mã trong tenant, thêm vai trò không tạo bản sao, giữ trường không gửi; nhận roles dạng JSON object hoặc chuỗi CUSTOMER|SUPPLIER|COLLABORATOR từ Excel/CSV. Preview kiểm lỗi, commit trong transaction chung.
- Migration V30 bảo toàn ID, suy vai trò từ kind cũ; không xóa dữ liệu. Snapshot phiếu tồn tại được lấy từ master tại thời điểm migration: không thể khôi phục tên lịch sử trước đó nếu trước đây chưa lưu snapshot.

## Kiểm chứng

| Kiểm tra | Kết quả |
|---|---|
| Backend Maven package / frontend Next production build | Đạt; build có kiểm TypeScript, không đồng nghĩa chạy toàn bộ unit test Core |
| ESLint các file mới, Sales và Overview sửa | 0 lỗi/cảnh báo |
| API integration | 12 kiểm tra đạt trong database riêng `aicrm-master-check` trên 8186 |
| Trình duyệt Edge/Playwright | 5 kiểm tra đạt: local chỉ đọc; tạo hai vai trò, sửa từ NCC, POS lấy tên mới, desktop/mobile 390px |
| Runtime frontend | Không có lỗi trong kịch bản UI |
| Ghi dữ liệu kiểm thử vào DB vận hành | 0; chỉ đăng nhập/đọc ở deployment thật, tất cả retail writes của UI chuyển đến database test |
| Git | Đồng bộ source và tài liệu lên origin/main theo yêu cầu ngày 06/10/2026; chưa triển khai server |

Bằng chứng: [API](./customer-master-check-result.json), [UI](./customer-master-ui-check-result.json), [ảnh local](./screenshots/customer-master/master-local.png), [ảnh desktop test](./screenshots/customer-master/master-desktop.png), [ảnh mobile test](./screenshots/customer-master/master-mobile.png). Ảnh test sử dụng dữ liệu trong database tạm.

## Giới hạn còn lại

- Lịch sử/ghi chú hiển thị tối đa 500 gần nhất; chưa có trang nhật ký sửa master riêng. Nhật ký ghi tại audit Core.
- Tab công nợ hiển thị phải thu/phải trả; cọc và ứng trước tra trong Kế toán, chưa có chỉ số tổng hợp riêng trên hồ sơ.
- Chưa có gộp hồ sơ hoặc chính sách ẩn từng trường thông tin cá nhân. API history CRM cũ và endpoint partners cùng áp dụng quyền loại giao dịch.
- Outbox Retail hiện có cảnh báo định dạng tên sự kiện từ mã Core/retail kế thừa; không coi chuyển tiếp sự kiện ra hệ thống khác đã nghiệm thu trong lượt này. Ghi hồ sơ, audit và idempotency vẫn hoạt động.

Không thay đổi môi trường SaaS/server hoặc repository remote.

## Cấu trúc menu local cập nhật

| Menu cha | Menu con | Route |
|---|---|---|
| Khách hàng / Đối tác | Khách hàng | /business/retail/master-customers |
| Khách hàng / Đối tác | Đối tác | /business/retail/master-partners |
| CRM | Khách hàng | /business/retail/customers (giữ route cũ) |
| Bán hàng | Khách hàng | /business/retail/sales-customers |
| Mua hàng | Nhà cung cấp | /business/retail/suppliers |

Các màn đều tham chiếu `retail.partner`; không có bảng khách CRM hoặc khách Bán hàng riêng. Kiểm tra menu/route đạt 6 kiểm tra: cấu trúc navigation API, năm route thật, bộ lọc vai trò, sửa chung từ Bán hàng (fixture trình duyệt), liên kết về master và mobile. Không phát sinh retail writes vào DB vận hành. Kết quả: [customer-master-menu-check-result.json](./customer-master-menu-check-result.json); [ảnh menu local](./screenshots/customer-master/master-menu-local.png). Build backend/frontend và ESLint PartnerMaster đạt sau thay đổi menu. Đồng bộ source theo yêu cầu người dùng ngày 06/10/2026.
