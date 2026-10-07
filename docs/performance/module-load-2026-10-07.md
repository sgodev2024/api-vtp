# Kiểm tra và tối ưu mở module — 07/10/2026

Đã áp dụng trên local (`http://localhost:3205`) và server
(`https://erp.aicrm.vn`). Mã nguồn và kết quả kiểm tra được đưa vào cập nhật nhánh main.

## Nguyên nhân

1. Màn hình chờ kiểm tra quyền trước khi hiển thị tiêu đề. Một số màn hình
   tiếp tục chờ danh mục, tồn kho và chứng từ trước khi mở giao diện.
2. Màn hình nghiệp vụ dùng chung gọi các API không cần thiết: mở danh sách
   sản phẩm hoặc mua hàng vẫn tải tổng hợp, tồn kho, giao hàng, bảo hành,
   tài chính. Một số yêu cầu thực hiện nối tiếp.
3. Mỗi lần đánh giá quyền lại đọc revision từ database. Một API danh mục/
   capabilities đánh giá nhiều quyền, gây nhiều truy vấn lặp dù dữ liệu ít.

Server có 0 sản phẩm, 0 đối tác và 0 chứng từ tại thời điểm kiểm tra.
HTML tại origin phản hồi khoảng 27–35 ms; qua Cloudflare khoảng 203–239 ms.
Các số này đo phản hồi trang, không đại diện cho thời gian tải toàn bộ module.

## Đã thay đổi

- Hiển thị tiêu đề module và trạng thái tải ngay khi điều hướng.
- Chỉ mount phần nội dung và gọi API dữ liệu sau khi quyền được xác nhận.
- Mỗi module chỉ tải nhóm dữ liệu cần dùng. Các yêu cầu độc lập chạy song song.
- Bán hàng tải danh mục, tồn và chứng từ song song; danh mục không phải chờ
  toàn bộ chứng từ/tồn mới được đưa vào state.
- Backend đọc revision một lần trong một yêu cầu GET/HEAD. Yêu cầu tiếp theo
  đọc lại revision; POST/PUT và các thao tác ghi không dùng snapshot này.
- Tái sử dụng ObjectMapper để đọc điều kiện quyền.
- Chuẩn hóa script `.sh` về LF để khởi tạo role PostgreSQL trên database mới.

## Kết quả

API local, sau khi cache đã làm nóng:

| API | Trước | Sau |
|---|---:|---:|
| Quyền capabilities | khoảng 753–1.311 ms | 34–43 ms |
| Danh mục catalog | khoảng 781–1.190 ms | 122–192 ms |

Lượt capabilities đầu sau khi backend khởi động vẫn cần làm nóng cache:
khoảng 715 ms local và 473 ms qua tên miền server trong bài đo.

Hiển thị tiêu đề khi tải lại trang trên local:

| Module | Trước | Sau |
|---|---:|---:|
| Đơn bán hàng | 3.762 ms | 1.612 ms |
| Sản phẩm | 3.877 ms | 810 ms |
| Mua hàng | 3.291 ms | 500 ms |
| Nhà cung cấp | 2.282 ms | 651 ms |
| Kho & tồn | 2.693 ms | 582 ms |

Đây là số đo tham khảo trên máy dev, mỗi trang một lượt. Tải lại trang bao gồm
tải frontend và xác thực; không giống chuyển module trong phiên đã mở.

Chuyển đúng đường dẫn của 34 màn hình (25 nghiệp vụ và 9 màn hình còn lại)
trong phiên đã đăng nhập, qua bộ tìm
chức năng, đo từ click đến hai frame render:

| Môi trường | Trung bình | Nhanh nhất | Chậm nhất |
|---|---:|---:|---:|
| Local | 120 ms | 87 ms | 244 ms |
| Server | 109 ms | 71 ms | 398 ms |

Các thời gian trên đo hiển thị màn hình, không phải hoàn tất tất cả API.
Server qua Cloudflare: capabilities và catalog sau làm nóng khoảng
151–249 ms; API chứng từ/tồn/tài chính khoảng 130–141 ms.

## Kiểm chứng

- Build frontend production và kiểm tra TypeScript đạt.
- Build backend đạt; 2 unit test snapshot quyền đạt.
- 23 kiểm tra API phân quyền trên database độc lập đạt, gồm thu hồi quyền,
  giới hạn dữ liệu và chặn nhân viên tự duyệt. Database test đã được dọn.
- Đúng 34 màn hình trên mỗi môi trường mở được, bao gồm nghiệp vụ, Trang chủ
  và quản trị, không có lỗi runtime JavaScript.
- Khi cố ý làm chậm API quyền, tiêu đề module vẫn xuất hiện; API danh mục
  và nút thao tác được bảo vệ chưa được hiển thị trước khi có quyền.
- Khi capabilities bị từ chối, nội dung được bảo vệ không mount và không
  phát sinh yêu cầu catalog.
- Các dịch vụ vận hành được giữ nguyên database, không nhập/xóa dữ liệu nghiệp vụ.

Số đo được lưu trong `local-before.json`, `local-after.json`,
`server-after.json`, `local-ui-check.json`, `server-ui-check.json` cùng thư mục.
Trường `settledMs` trong baseline cũ có bao gồm yêu cầu logo chưa kết thúc nên
không dùng để so sánh hiệu năng; bảng trên chỉ dùng `shellMs`.
Các thời gian phụ thuộc trạng thái cache, tải máy và mạng, không phải cam kết SLA.
