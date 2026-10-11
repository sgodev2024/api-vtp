# Tiến độ tách module Đợt 3

Trạng thái: đã triển khai và kiểm chứng trên local. Bản phát hành ngày 11/10/2026 bao gồm toàn bộ gói đợt 3; commit và kết quả triển khai được xác nhận theo từng release trên Git/main và server.

| Hạng mục | Backend | Frontend | Trạng thái |
|---|---|---|---|
| Nền tảng nghiệp vụ / hợp đồng dịch vụ | Có | Có widget dùng chung và entry phân quyền | Hoàn thành |
| Bán hàng | Có | Có entry POS / đơn bán / khách hàng bán / đổi trả | Hoàn thành |
| Kho | Có | Có entry tồn / chuyển / quản lý kho / cấu hình | Hoàn thành |
| Mua hàng | Có | Có entry mua hàng / nhà cung cấp | Hoàn thành |
| Sản phẩm | Có | Có entry danh sách và danh mục sản phẩm | Hoàn thành |
| Khách hàng / Đối tác | Có | Có entry Customer Master | Hoàn thành |
| CRM | Có | Có entry khách hàng / chăm sóc | Hoàn thành |
| Kế toán | Có | Có entry thu chi / công nợ | Hoàn thành |
| Vận hành chung | Có | Có entry địa điểm / thiết lập / cần duyệt | Hoàn thành |
| Tổng quan | Có | Có entry dashboard | Hoàn thành |
| Báo cáo | Có | Có entry báo cáo | Hoàn thành |
| API đổi vai trò người dùng | Thuộc core, không phụ thuộc nghiệp vụ | Người dùng gọi API core | Hoàn thành |
| Core khởi động không có nghiệp vụ | Auto-configuration theo gói được cài | Registry rỗng hợp lệ | Đã kiểm chứng |
| Công cụ đóng gói / export-core / import | Source bundle + dependency/checksum checks | Tự sinh literal dynamic-import registry | Hoàn thành |

## Bằng chứng kiểm tra

- Backend build Java 21 và frontend build Next.js thành công; backend có 11 classifier artifact theo package.
- 20 kiểm thử Java đã qua: 19 kiểm thử module registry/runtime/ràng buộc package và 1 kiểm thử API chưa cài trả 404.
- 8 kiểm thử package manager qua: gói hợp lệ, path traversal/ghi core, checksum/trùng, dependency thiếu/vòng, core trống, import riêng/lặp, custom conflict, version sai.
- Core trống chạy với database mới: đăng nhập thành công, 10 core module, menu Trang chủ và Quản trị hệ thống; 23 migration, chưa có schema retail.
- Core sau import: tự migrate 23 → 33, có đủ 11 module nghiệp vụ; 32 kiểm tra API/giao dịch qua. Mua nhận 10 → duyệt → POS bán 2 → tồn 8; tạo/làm xong chăm sóc; API 403 và menu ẩn khi tắt từng module, chặn tắt phụ thuộc và core.
- 7 kiểm tra API đổi vai trò core qua: đọc/gán, stale revision, role không hợp lệ, tự sửa bị chặn, hoạt động khi tắt toàn bộ nghiệp vụ.
- 12 màn hình nghiệp vụ render trên local, không có lỗi browser hay API.
- 6 kiểm tra import một phần đã qua: core chỉ có nền tảng + Sản phẩm, tạo/đọc được sản phẩm, menu đúng gói đã cài; API Kho chưa cài trả 404.

Các bằng chứng kỹ thuật nằm trong `backups/module-phase3` (local, không đưa lên Git). Hướng dẫn cài đặt: `docs/BUSINESS_MODULES_PHASE3.md`.

## Quy tắc và giới hạn

- Các module sử dụng cùng database và dữ liệu master. Foundation giữ migration lịch sử và các widget/engine chung; việc tắt module không xóa dữ liệu.
- Import gói source cần build/restart. Chưa có hot reload JAR hoặc nút upload package trên trình duyệt; giao diện Quản lý module bật/tắt các gói đã cài.
- Core cài mới phải theo starter có import contract v2; không hỗ trợ import nguyên trạng vào một core cũ bất kỳ.
- Phụ thuộc vận hành gồm địa điểm/phạm vi phải được cài cho CRM/kế toán/báo cáo; package manager tự lấy trong cùng bundle.
- Không cài lại PostgreSQL hoặc xóa volume của hệ thống hiện tại. Backup DB trước khi nâng cấp deployment có dữ liệu.