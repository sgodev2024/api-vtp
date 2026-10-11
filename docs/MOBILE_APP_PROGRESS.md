# Tiến độ giao diện mobile — Đợt 1

Cập nhật: 07/10/2026. Trạng thái: đã triển khai và chạy trên local http://localhost:3205.

## Phạm vi hoàn thành
- Đăng nhập: email và mật khẩu 16px trên mobile, bỏ tự focus email, hiện/ẩn mật khẩu; giữ cơ chế xác thực và chính sách mật khẩu hiện tại. Không vô hiệu hóa zoom của người dùng.
- Thanh điều hướng dưới theo menu đã được cấp quyền: Tổng quan, Bán hàng, Kho, Mua hàng, Thêm. Thêm mở menu đầy đủ.
- Giao diện mobile: tiêu đề cố định, vùng chạm lớn hơn, biểu mẫu một cột, danh sách nghiệp vụ dạng thẻ có nhãn trường, popup toàn màn hình, hỗ trợ safe area và điều chỉnh thanh dưới theo bàn phím.
- POS mobile: Chọn hàng → Giỏ hàng → Thanh toán. Giữ API, tra giá, kiểm tra tồn, lưu nháp, xác nhận và duyệt hiện tại.
- Manifest, icon và hướng dẫn thêm vào màn hình chính; vẫn cần Internet. Không bổ sung xử lý bán offline.

## Backend và dữ liệu
Không thay đổi backend, cấu trúc database, phân quyền hoặc quy tắc nghiệp vụ. Dùng các API hiện có. Kiểm tra UI không lưu phiếu bán hay khách hàng mới.

## Kiểm tra
- Next.js production build / TypeScript: đạt.
- ESLint các component mới, layout, manifest và POS: đạt.
- 390px: đăng nhập 16px, hiện/ẩn mật khẩu, sáu màn hình chính, POS ba bước, manifest standalone, không có lỗi JavaScript.
- 320px: 36 màn hình được cấp cho admin không tràn ngang toàn trang.
- Tạo đơn: chọn sản phẩm vào dòng hàng; popup thêm khách hàng rộng 390px, cao 844px, không lưu dữ liệu.
- 1440px: thanh mobile được ẩn.
- Ảnh kiểm tra và kết quả cục bộ: backups/mobile-app/ (không đưa dữ liệu kiểm tra lên Git).

## Giới hạn kiểm tra
Kiểm tra bằng Microsoft Edge/Chromium với viewport và thiết bị mô phỏng. Cần nghiệm thu thêm Safari iPhone thật về bàn phím, focus không phóng to và thêm vào màn hình chính; không khẳng định đã kiểm tra thiết bị thật. Việc cài ứng dụng tùy hỗ trợ trình duyệt và HTTPS khi triển khai.

## Chưa triển khai trong đợt này
Tách/chuyển các module sang core khác chưa triển khai. Người dùng đã duyệt commit main và triển khai server với nội dung “Tối ưu mobile app”; kết quả triển khai được ghi riêng sau kiểm tra.


## Cập nhật 10/10/2026 — Làm gọn Trang chủ và menu mobile

Trạng thái: đã triển khai, build và chạy trên local http://localhost:3205. Chưa commit, push Git hoặc triển khai server trong đợt này.

- Thanh đầu: bỏ hamburger trên mobile; tên màn hình 17px, thông báo và avatar gọn.
- Thanh dưới: Tổng quan → Bán hàng → Kho → Mua → Thêm; nhãn một dòng, lọc theo Navigation Registry đã cấp quyền. Màn hình ngoài bốn nhóm chính đánh dấu Thêm. Không đánh dấu Trang chủ core thành Tổng quan kinh doanh.
- Thêm: danh mục toàn màn hình nền sáng, tìm kiếm không dấu, nhóm Nghiệp vụ / Vận hành / Báo cáo & Quản trị, mở nhóm rồi chọn chức năng. Ẩn thanh dưới trong lúc mở; khóa cuộn nền, hỗ trợ Escape/focus và trả focus về nút Thêm khi đóng. Yêu thích desktop giữ nguyên.
- Trang chủ: giữ chức năng core, lời chào ngắn, Việt hóa chỉ số, bốn thẻ gọn, thông tin kỹ thuật mở rộng, lối tắt lọc theo quyền. Tạo tài nguyên thực hiện tại module Tài nguyên.
- Backend, database và nghiệp vụ không sửa trong đợt này. Kiểm tra giao diện không lưu chứng từ hay master; yêu cầu lưu tùy chọn điều hướng được mô phỏng trong kiểm tra.
- Production Docker build và TypeScript đạt. ESLint hai file TypeScript thay đổi: 0 lỗi; page.tsx còn 10 cảnh báo có sẵn về hook, ảnh và component chưa dùng.
- Production UI: 320/390/430px và desktop1440px không cuộn ngang, đúng thứ tự tab, tìm Địa điểm bằng `dia diem`, đóng menu và chuyển tab đúng. Tám màn hình nghiệp vụ không tràn ngang hoặc lỗi JavaScript. Kiểm tra focus, Escape, tìm không có kết quả và registry bị giới hạn; đăng nhập email/password giữ 16px.
- Evidence và ảnh: backups/mobile-refresh-20261010/ui-check.json, smoke-check.json, home-390.png, directory-390.png. Giới hạn: Edge/Chromium mô phỏng; chưa nghiệm thu Safari iPhone hoặc Android thật. Không xác nhận bán hàng/ghi sổ bằng kiểm tra giao diện này.

## Phát hành 11/10/2026

Người dùng đã duyệt đồng bộ toàn bộ source hiện tại lên main và server erp.aicrm.vn. Bản phát hành gồm tách module đợt 3, tài liệu BA/SRS/runbook hiện hành và giao diện mobile hoàn thiện ngày 10/10. Build frontend và kiểm tra giao diện đã qua trước phát hành; thông tin commit và trạng thái deployment được xác nhận theo release sau khi triển khai.
