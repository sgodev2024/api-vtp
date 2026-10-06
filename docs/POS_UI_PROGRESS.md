# Bán tại quầy — giao diện POS

Cập nhật: 06/10/2026. Phạm vi: frontend chạy local trên Java Core; dùng các API bán hàng hiện có.

| Hạng mục | Frontend | Backend |
|---|---|---|
| Danh mục dạng thẻ; tên, SKU, biến thể, giá niêm yết | Đã triển khai; biểu tượng khi chưa có ảnh sản phẩm | Có: `GET /api/v1/retail/catalog` |
| Tìm tên/SKU/barcode; Enter thêm đúng mã, F2 tìm hàng | Đã triển khai; mã trùng yêu cầu chọn mặt hàng | Dùng danh mục được tải theo quyền |
| Lọc tất cả/còn hàng/hết hàng tại kho đang chọn | Đã triển khai | Có: `GET /api/v1/retail/stock`; tồn hiển thị có thể gồm lô hết hạn |
| Thiếu tồn, nhiều dòng cùng SKU, đơn vị quy đổi | Cảnh báo cần/còn/thiếu theo đơn vị cơ sở; chặn lưu bán khi thiếu, tải tồn mới trước lưu/xác nhận | Dùng stock API; backend vẫn khóa tồn và chặn xuất âm |
| Giỏ hàng, STT, tăng/giảm số lượng, đổi đơn vị | Đã triển khai | Có: tạo phiếu kiểm tra đơn vị quy đổi |
| Khách hàng dùng chung Customer Master; F4 chọn khách | Đã triển khai | Có: khách theo vai trò CUSTOMER trong catalog |
| Giá theo khách, kho, kênh, lượng, ưu đãi | Tự tra khi thay đổi điều kiện; hiển thị tổng tạm tính | Có: `POST /api/v1/retail/price`; tổng chính xác do API chứng từ tính |
| Giá nhập tay, giảm dòng, VAT, lô/hạn/serial | Đã triển khai trong phần chi tiết dòng; serial mở sẵn | Có; kiểm tra serial/lô/FEFO khi ghi phiếu |
| Tiền mặt/chuyển khoản/thẻ/khác; thu đủ hoặc thu một phần | Đã triển khai; cảnh báo chọn khách nếu còn nợ | Có: paid/method trong chứng từ; tiền thu không vượt tổng |
| Kiểm tra thanh toán → xác nhận bán | Đã triển khai; khóa nội dung sau lưu nháp và hiển thị tổng máy chủ | Có: `POST /documents`, `POST /documents/{id}/submit` |
| Lưu bị gián đoạn, thử lại không tạo trùng | Giữ biểu mẫu, khóa sửa và giữ cùng payload/idempotency key | Có: cơ chế idempotency hiện tại |
| Đặt trước từ giỏ khi thiếu hàng | Giữ thao tác chuyển sang biểu mẫu đơn bán hiện có | Có: SALE_ORDER; không tự xuất và không tự tách phần thiếu |
| Lịch sử, chi tiết, in phiếu | Lịch sử mở theo nhu cầu; chi tiết xuất hiện sau xác nhận hoặc chọn phiếu | Có: danh sách/chi tiết chứng từ; in tại trình duyệt |
| Desktop/tablet/mobile | Bố cục hai cột trên desktop; xếp dọc ở màn hình hẹp | Không cần API mới |

Luồng thao tác: chọn kho → tìm/quét/chọn hàng → chỉnh lượng, đơn vị, lô/serial nếu cần → chọn khách và tiền thu → **Kiểm tra thanh toán** → xem tổng máy chủ → **Xác nhận bán hàng**. Phiếu thông thường ghi bán; phiếu ngoại lệ chờ duyệt. Chưa giao hàng khi còn chờ duyệt.

Nháp đã lưu chưa hỗ trợ sửa nội dung: hủy nháp rồi lập lại nếu cần. Giao diện không tạo danh mục sản phẩm tại quầy. Không phát sinh ca bán hàng hoặc chế độ offline.

Nếu tồn giảm sau khi lưu nháp, POS giữ nháp và báo phần thiếu. Người dùng có thể nhập hàng/tồn đầu kỳ đã được duyệt, bấm Làm mới rồi xác nhận; hoặc bấm **Hủy nháp & chuyển giỏ đặt trước**. Thao tác này hủy nháp POS theo version/idempotency, giữ giỏ và chuyển sang biểu mẫu SALE_ORDER; chưa tự tạo đơn hoặc ghi cọc. Toàn bộ giỏ được chuyển, không tự tách phần thiếu. Tạo sản phẩm chỉ thêm danh mục, không tự sinh tồn.

## Kiểm chứng

- Build production và TypeScript: thành công.
- ESLint các component POS/bán hàng và kiểm tra tồn: thành công, không cảnh báo.
- Kiểm thử UI: 12 kiểm tra thành công, không lỗi runtime; kết quả tại `sales-ui-check-result.json`. Bao gồm quét mã, lượng/đơn vị, lô/serial, tổng máy chủ, công nợ, retry cùng idempotency key, lịch sử, tồn bằng 0, tồn giảm sau lưu nháp và chuyển giỏ sang đặt trước.
- Kiểm tra tính lượng thiếu: nhiều đơn vị cùng SKU, lượng thập phân, các dòng serial cùng SKU, không cộng tồn kho khác, không có dòng tồn và bán vừa đủ lượng khả dụng đều thành công.
- Ảnh kiểm chứng: `screenshots/sales/pos-local.png`, `pos-fixture-cart-desktop.png` và `pos-fixture-cart-mobile.png`; ảnh fixture dùng dữ liệu giả lập trong trình duyệt.
- Dữ liệu kiểm thử được chặn và giả lập tại trình duyệt; không nhập dữ liệu demo vào database vận hành.

File chính: `frontend/app/retail/pos-editor.tsx`, `pos.css`, `sales-workspace.tsx`. Script kiểm tra: `scripts/sales-ui-check.mjs`.
