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

