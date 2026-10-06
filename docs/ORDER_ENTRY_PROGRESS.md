# Tạo đơn bán — tiến độ frontend/backend

Cập nhật 06/10/2026; triển khai local.

| Hạng mục | Frontend | Backend |
|---|---|---|
| Chọn khách hàng, tạo nhanh và tự chọn | Icon + trong ô Khách hàng; giữ giỏ và địa chỉ giao | `/customers/quick-create`; READ+SELL; master chung, chỉ CUSTOMER; idempotency/audit |
| Tìm tên/SKU/barcode dạng danh sách xổ xuống | Combobox; tìm, phím lên/xuống/Enter/Esc, đóng khi rời ô; barcode Enter thêm mã khớp duy nhất | Catalog theo quyền hiện có |
| Bảng hàng gọn như báo giá | STT, sản phẩm, ĐVT, lượng, giá, VAT, thành tiền; tổng tạm tính; lô/serial thu gọn | Tổng nháp tiếp tục do máy chủ tính |
| Kiểm tra bố cục cuối | Build production, TypeScript, ESLint đạt; mobile viewport/root đều 390px, bảng cuộn và sửa được; barcode/VAT/tổng tiền đạt | Fixture không ghi dữ liệu vận hành; bằng chứng `docs/order-layout-check-result.json`, script `scripts/order-layout-check.mjs` |
| Địa chỉ giao và ghi chú cùng bảng | Đặt dưới dòng hàng trong cùng panel; bỏ tiêu đề Giao hàng, thuế và ghi chú | Giữ trường address/note/taxInclusive của API |
| Bỏ COD/phí khách trả/giảm dòng | Đã triển khai trên SALE_ORDER | Payload tạo đơn từ UI không thêm phí/COD hoặc giảm thủ công; vẫn áp ưu đãi tự động |
| Đơn giá theo khách/kho/kênh/lượng/đơn vị | Tự tải, khóa giá có sẵn; nhập tay khi thiếu giá | `/price` trả priceAvailable; máy chủ tính lại khi lưu |
| Chưa có giá: chặn lưu khi chưa nhập | Đã triển khai, cho phép số 0 nhập rõ | SALE_PRICE_REQUIRED; kiểm số âm/độ chính xác |
| Quyền tạo nhanh | Nút theo SELL; dữ liệu liên hệ đơn giản | Không mở SETUP/FINANCE; lọc payload, không cho gán vai trò khác |
| Kiểm chứng tạo đơn | 8 nhóm API/UI đạt; desktop và mobile 390px không tràn ngang, không lỗi runtime | Database tạm aicrm-order-check; gồm giá/UOM/ưu đãi, giữ giá báo giá nguồn, quyền tạo khách và idempotency |
| Hồi quy POS/đặt trước | 12 nhóm kiểm tra đạt | Các lệnh ghi được chặn/chuyển vào fixture; 0 retail write vào DB vận hành |
| Hồi quy Customer Master | API 12 kiểm tra đạt | Cùng master ở Bán hàng/Mua hàng/CRM; ngân hàng và vai trò vẫn theo quyền |
| Build | Next production build + TypeScript + ESLint Sales đạt | Maven package đạt; build không chạy toàn bộ unit test Core |

Tài liệu quy tắc: BA F-13 và SRS FR-12. Script `scripts/order-entry-check.mjs`; bằng chứng `docs/order-entry-check-result.json`. Đơn từ báo giá/phiếu xuất giữ giá dòng nguồn. API giá tự động trên SKU chưa có giá danh mục (>0) và chưa có rule trả priceAvailable=false; rule giá 0 vẫn là giá cấu hình. Giá chưa có nhập tay chỉ dùng trên giao dịch, không tự sửa giá trong danh mục sản phẩm.

Tra giá chờ 200ms sau thay đổi, bỏ kết quả cũ khi người dùng đổi điều kiện; nút lưu chờ kết quả giá mới, có nút tra lại nếu API lỗi. Thử phản hồi qty=3 chậm hơn qty=4 không ghi đè giá mới. Thử tạo khách bằng tài khoản READ+SELL không có SETUP; không thể dùng endpoint tạo nhanh để gán SUPPLIER hoặc ngân hàng. Chưa commit/push các thay đổi lượt này.
