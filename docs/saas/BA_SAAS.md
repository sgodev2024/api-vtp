# BA SaaS AICRM — phiên bản 1.0

Ngày 11/10/2026. Kiến trúc đã chốt, đặc tả trình duyệt trước triển khai. Tài liệu mở rộng BA hiện hành; không thay nghiệp vụ Bán hàng/Kho/Mua hàng/CRM/Kế toán nội bộ. Chưa có triển khai hoặc test nghiệm thu SaaS trong đợt này.

## 1. Mục tiêu và quyết định

Phục vụ hộ kinh doanh, cửa hàng 1–3 điểm và chuỗi. Mỗi doanh nghiệp là một tenant, có database và credential riêng; dùng chung source chuẩn. Chi nhánh/kho nằm trong tenant, không phải tenant riêng. Hỗ trợ subdomain và tên miền riêng. Khách nhận source hoặc tùy chỉnh sâu chuyển sang ứng dụng, database, tệp và cấu hình độc lập.

Source bàn giao là phiên bản source chuẩn cùng các module được thỏa thuận; không bao gồm bí mật hạ tầng hoặc dữ liệu khách khác. Bản tùy chỉnh sâu không cam kết nâng cấp tự động, cần hợp nhất và review.

## 2. Phạm vi

Cấp mới qua quản trị SaaS, gói/module/giới hạn, tên miền, trạng thái dịch vụ, backup/restore, phiên hỗ trợ, nâng cấp, chuyển dữ liệu hiện có và bàn giao độc lập. Chưa làm đăng ký công khai, thu tiền thuê bao tự động, hóa đơn thuê bao, SSO xuyên tên miền hoặc source tùy chỉnh trong runtime dùng chung. Luồng nghiệp vụ và wizard hiện hành giữ nguyên; không đưa lại ca bán/offline/API vận chuyển.

## 3. Vai trò và quyền

| Vai trò | Công việc |
|---|---|
| Quản trị SaaS | Doanh nghiệp, gói, domain, yêu cầu vận hành |
| Vận hành SaaS | Backup, restore, migration theo quyền riêng |
| Hỗ trợ SaaS | Metadata; nghiệp vụ chỉ qua phiên được doanh nghiệp cho phép |
| Admin doanh nghiệp | Toàn quyền trong doanh nghiệp theo quy tắc hiện hành |
| Nhân viên | Theo role/module/kho hiện hành |

Admin doanh nghiệp không tự thành admin SaaS vì email admin@core.local. Quyền restore, xuất source, truy cập hỗ trợ và quản lý secret tách riêng. Mọi hành động nhạy cảm có audit actor thật, tenant và lý do.

## 4. Màn hình và trường thông tin

| Màn hình | Thông tin và điều kiện |
|---|---|
| Danh sách doanh nghiệp | STT, mã, tên, gói, trạng thái, domain chính, phiên bản app/schema, thao tác; tìm kiếm/phân trang |
| Thêm/sửa doanh nghiệp | Mã/tên/gói/email liên hệ bắt buộc; tên pháp lý, MST, địa chỉ, người liên hệ, điện thoại optional; mã không đổi sau cấp mới |
| Gói | Mã/tên, module được cấp, giới hạn user hoạt động/địa điểm/kho/dung lượng; null không giới hạn, 0 không cho tạo |
| Tên miền | Hostname, loại mặc định/riêng, chính/phụ, xác minh/DNS/TLS, hết hạn TLS, lỗi gần nhất; hướng dẫn TXT/CNAME |
| Tác vụ | Mã, loại, tenant, người yêu cầu, trạng thái, bước, thời gian, lỗi đã che bí mật, evidence |
| Sao lưu | Mốc dữ liệu, phiên bản, DB+tệp, checksum, trạng thái, hạn lưu |
| Hỗ trợ | Người hỗ trợ, lý do, phạm vi, người doanh nghiệp duyệt, thời hạn, thu hồi |
| Bàn giao | Phiên bản source/module, backup cuối, checksum, đối chiếu, thời điểm chuyển đổi |

Không hiển thị mật khẩu DB. Trang chi tiết có tab Tổng quan/Tên miền/Gói và module/Tác vụ/Sao lưu/Nhật ký/Bàn giao. Khu vực SaaS tách khỏi menu nghiệp vụ; doanh nghiệp chỉ thấy gói, giới hạn, domain và phiên hỗ trợ của mình theo quyền.

## 5. Cấp mới và lần đăng nhập đầu

Nhập doanh nghiệp/gói → kiểm tra mã/subdomain → PROVISIONING → tạo DB/credential → migration đúng phiên bản → admin/module → địa điểm chính/kho mặc định → health check → ACTIVE → cung cấp liên kết kích hoạt một lần.

Không dữ liệu demo, không mật khẩu mặc định chung. Admin đặt mật khẩu riêng rồi chạy wizard: thông tin doanh nghiệp/địa điểm → kho → danh mục → tồn/công nợ đầu kỳ. Lỗi vào PROVISION_FAILED, retry từ checkpoint; không tạo DB/admin/kho trùng, không tự xóa DB có dữ liệu. Chỉ đăng nhập khi cấp mới hoàn tất.

## 6. Truy cập và tên miền

Mặc định `daithang.erp.aicrm.vn`: hostname → mapping active → tenant → database → đăng nhập → quyền nghiệp vụ. Unknown host không rơi về doanh nghiệp mặc định. Token/phiên phải khớp tenant từ host; mã tenant trong payload không được chọn DB.

Tên miền riêng `erp.daithang.vn`: khai báo → TXT xác minh sở hữu và CNAME trỏ đích → kiểm DNS → cấp HTTPS → active → chọn địa chỉ chính. Đợt đầu hỗ trợ hostname con; apex xem xét riêng. Một hostname chỉ gán một tenant. Trạng thái: PENDING_VERIFY/PENDING_DNS/PENDING_TLS/ACTIVE/ERROR/DISABLED.

Chuyển địa chỉ chính chỉ redirect GET/HEAD, không redirect POST đăng nhập; người dùng đăng nhập lại, cookie không dùng chung tên miền. DNS/TLS lỗi vẫn giữ subdomain mặc định. Không xóa domain chính nếu chưa chọn địa chỉ hợp lệ khác. Cấu hình TLS theo hostname, không đổi SSL toàn zone aicrm.vn.

## 7. Module và giới hạn

Gói đặt trần module; admin doanh nghiệp bật/tắt trong trần theo dependency hiện có. Backend chặn API, frontend ẩn chức năng theo quyền. Không xóa dữ liệu khi tắt, không tắt module có phụ thuộc đang dùng. Hạ quota không xóa tài khoản/kho; chặn tạo/kích hoạt thêm đến khi phù hợp. Nhiều yêu cầu đồng thời không được vượt quota.

## 8. Trạng thái doanh nghiệp

DRAFT → PROVISIONING → ACTIVE hoặc PROVISION_FAILED → retry PROVISIONING. ACTIVE ↔ SUSPENDED; ACTIVE → MAINTENANCE → ACTIVE. ACTIVE/SUSPENDED → EXPORTING; thành công trở về trạng thái trước để kiểm thử, chỉ ARCHIVED sau cắt chuyển độc lập được duyệt. Export lỗi không đổi dữ liệu nguồn.

SUSPENDED chặn phiên và job ghi, vẫn cho vận hành backup/export theo quyền. MAINTENANCE drain transaction/job, chặn ghi trước restore/migrate/cắt chuyển. ARCHIVED giữ theo retention; đợt đầu không có xóa vĩnh viễn từ UI.

## 9. Backup, phục hồi, nâng cấp

Backup riêng gồm DB+tệp+manifest phiên bản và checksum, phải có mốc nhất quán. Restore vào vùng tạm, đối chiếu trước khi duyệt đổi routing; không overwrite DB đang ghi hoặc DB khách khác. Đề xuất backup mỗi ngày, giữ 30 ngày, RPO24 giờ/RTO4 giờ: cần xác nhận tài nguyên và diễn tập, chưa là SLA cam kết.

Nâng cấp: tenant thử nghiệm → backup/migration/test → nhóm nhỏ → mở rộng; ghi version theo tenant. Lỗi migration dừng batch. Rollback app chỉ nếu schema tương thích; restore cần xem xét giao dịch phát sinh sau backup. Không cam kết zero downtime.

## 10. Chuyển dữ liệu hiện tại

Tạo tenant mapping giữ ID doanh nghiệp hiện có → backup → thử bản sao → đối chiếu account/role/phiếu/tồn/nợ/tệp → duyệt bảo trì → khóa ghi/drain → backup cuối → đổi routing → smoke → mở ghi. Không reset dữ liệu/mật khẩu. Trước mở ghi có thể quay về nguồn; sau mở ghi phải xử lý dữ liệu mới trước rollback.

## 11. Bàn giao độc lập

Chọn release/module → xuất source+DB+tệp+config mẫu+license inventory+runbook → dựng môi trường riêng → đối chiếu → duyệt cắt chuyển → khóa ghi SaaS/đồng bộ cuối → DNS → mở bản độc lập → archive nguồn sau xác nhận. Bản độc lập hoạt động không cần control-plane hoặc network SaaS. Không chứa credential production, secret SaaS hoặc dữ liệu tenant khác.

## 12. Quy tắc và nghiệm thu

BR-S01 DB/credential riêng. BR-S02 source chuẩn chung. BR-S03 host xác định tenant trước auth. BR-S04 branch/kho trong tenant. BR-S05 file/cache/job/export có scope tenant. BR-S06 quyền SaaS khác quyền công ty. BR-S07 API kiểm module/role/scope. BR-S08 tác vụ chống lặp/audit. BR-S09 tắt/ngừng không xóa dữ liệu. BR-S10 độc lập không phụ thuộc SaaS.

Nghiệm thu phải chứng minh cách ly hai tenant, wizard và nghiệp vụ hoạt động, domain riêng HTTPS, restore một tenant không ảnh hưởng tenant khác, bản bàn giao chạy trên máy sạch. Chi tiết test tại SRS. Chưa bắt đầu code cho đến khi chủ sản phẩm duyệt bộ đặc tả và task.
