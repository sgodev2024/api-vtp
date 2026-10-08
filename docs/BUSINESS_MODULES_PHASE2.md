# Chuẩn hóa module nghiệp vụ — Đợt 2

Trạng thái: đã nghiệm thu local; phát hành lên main và triển khai server theo yêu cầu ngày 08/10/2026.

## Các gói
| Module runtime | Package Java / frontend | Phụ thuộc |
|---|---|---|
| aicrm-retail — Nghiệp vụ dùng chung | vn.aicrm.shared / retail/shared-workspace và các master | kernel, permission, audit-store, event-outbox |
| aicrm-stock — Kho | vn.aicrm.inventory / retail/inventory | aicrm-retail |
| aicrm-sales — Bán hàng | vn.aicrm.sales / retail/sales | aicrm-retail, aicrm-stock |
| aicrm-purchase — Mua hàng | vn.aicrm.purchase / retail/purchase | aicrm-retail, aicrm-stock |

Sản phẩm, đơn vị tính, Customer Master, công nợ và cơ chế chứng từ/duyệt/idempotency vẫn dùng chung để giữ nghiệp vụ hiện tại. InventoryPort là hợp đồng của dùng chung; triển khai ghi tồn chỉ nằm trong gói Kho. Bán hàng/Mua hàng không import implementation của Kho. Các form cũ của Mua hàng/Kho dùng adapter tới shared-workspace; không viết lại thuật toán nghiệp vụ trong đợt này.

## Quản lý phụ thuộc và API
- Trong Quản lý modules hiển thị phụ thuộc và các module đang dùng module đó.
- Bật một module: tất cả phụ thuộc phải đang bật. Trạng thái ATTENTION vẫn hoạt động theo quy tắc cũ.
- Tắt module: từ chối nếu còn module phụ thuộc đang bật; không tự tắt dây chuyền.
- Tắt Kho: tắt Bán hàng và Mua hàng trước. Bật: dùng chung → Kho → Bán hàng/Mua hàng.
- Không cho tắt module nền tảng trong runtime.
- Trạng thái áp dụng toàn deployment, không phải theo tenant hoặc theo user. Quyền tài khoản là lớp kiểm tra riêng; admin cũng không vượt trạng thái module.
- Tắt module không xóa dữ liệu, phiếu hoặc cấu hình; bật lại tiếp tục dùng dữ liệu.
- API của controller thuộc module tắt trả 403 MODULE_DISABLED. API chứng từ dùng chung vẫn tồn tại: lọc loại phiếu đã tắt khỏi danh sách, chặn xem/sửa/duyệt loại đó kể cả gọi URL trực tiếp và gửi lại Idempotency-Key cũ.
- Kiểm tra trạng thái trước replay idempotency; giao dịch ghi giữ shared lock trên trạng thái module, thao tác bật/tắt giữ exclusive lock, tránh tắt chen vào giữa ghi phiếu.
- Snapshot trạng thái chỉ tồn tại trong request; sau bật/tắt và trước command được làm mới. Menu và capability phản ánh trạng thái hiện tại.
- Các resource_type, route cũ, dữ liệu và policy quyền được giữ. Startup đổi owner của resource nghiệp vụ từ owner cũ sang owner mới có kiểm soát; không đổi schema nghiệp vụ.

## API theo gói
Ngoài các URL hiện tại để tương thích, bổ sung:
- /api/v1/retail/sales/documents: QUOTE, SALE_ORDER, POS, SALE_RETURN.
- /api/v1/retail/purchase/documents: PURCHASE_REQUEST, PURCHASE_ORDER, PURCHASE_RECEIPT, PURCHASE_RETURN.
- /api/v1/retail/inventory/documents: ISSUE, TRANSFER, COUNT, OTHER_RECEIPT, OTHER_ISSUE, OPENING_STOCK.

GET danh sách truyền type; GET /{id}; POST tạo; POST /{id}/{action}. Không cho gửi loại phiếu thuộc gói khác. Quyền, phạm vi kho, duyệt và version vẫn do engine hiện tại kiểm tra.

## Đóng gói
Chạy backend `mvn package` tạo application JAR và bốn classifier JAR:
- core-platform-runtime-1.0.0-SNAPSHOT-aicrm-shared.jar
- core-platform-runtime-1.0.0-SNAPSHOT-aicrm-stock.jar
- core-platform-runtime-1.0.0-SNAPSHOT-aicrm-sales.jar
- core-platform-runtime-1.0.0-SNAPSHOT-aicrm-purchase.jar

Chạy `scripts/package-business-modules.ps1` tạo ZIP trong backups/business-modules; gồm JAR, source Java, source frontend, module.json ghi phụ thuộc/checksum và hướng dẫn. Gói shared chứa migration dùng chung; giữ nguyên lịch sử Flyway, không chạy lại bằng cách đổi số migration. Không kèm .env, mật khẩu hay dump dữ liệu.

Các classifier là thư viện, không phải ứng dụng tự chạy. Core đích cần Kernel API tương thích, security, audit, event-outbox, resource/navigation registry, migration của retail và InventoryPort provider. Host hiện tại vẫn lắp ráp các gói ở build-time và giữ engine dùng chung. Chưa có tính năng tải ZIP để cài/gỡ nóng hoặc tự chuyển database sang hệ thống khác; đó là phạm vi đợt 3. Khi tắt module, bean vẫn tồn tại nhưng truy cập nghiệp vụ bị chặn.

## Kiểm tra bắt buộc
1. Build Java/Next.js; kiểm thử ArchUnit không import implementation chéo.
2. Chặn tắt Kho khi Bán hàng/Mua hàng đang bật; chặn bật con khi Kho tắt; chặn tắt core.
3. Tắt từng module: menu/capabilities đổi, GET/POST trực tiếp bị chặn, master chung còn dùng được.
4. API cũ và theo gói, loại chứng từ sai, idempotency cũ khi module tắt.
5. Bật lại phục hồi quyền/menu; không thay đổi số lượng dữ liệu nghiệp vụ.
6. Kiểm tra màn hình tổng quan/kế toán khi Bán hàng và Kho tắt.

Kết quả local được ghi trong backups/module-phase2; nhật ký nghiệm thu cập nhật sau khi chạy thực tế.
Gói shared còn kèm core-patch (ModuleRuntime, ResourceRegistry, ControlPlaneController và composition root). Core đích phải có các API bổ sung này; chỉ trùng số phiên bản core chưa đủ để lắp gói. Review/ghép patch tại build-time và import RetailConfiguration, không thay file core hiện có một cách tự động.
## Kết quả nghiệm thu local — 07/10/2026
- Production build frontend, TypeScript và lint các điểm lắp ráp mới: đạt.
- Java: 18 kiểm thử khác nhau đã đạt; 7 kiểm thử ranh giới/runtime được chạy lại trên bản cuối, gồm module còn trong database nhưng không có descriptor/code.
- 17 kiểm tra API: phụ thuộc bật/tắt, core bắt buộc, admin bị chặn khi tắt, API cũ/mới, loại phiếu khác gói, menu/capability và bật lại: đạt.
- 12 kiểm tra UI: thông báo phụ thuộc, POS/đơn bán/mua/kho/chuyển kho/sản phẩm/NCC; Tổng quan, Thu chi, Sản phẩm, Customer Master khi ba module tắt: đạt; không có lỗi JavaScript hoặc API bị gọi nhầm từ các màn hình dùng chung.
- Đối chiếu số dòng 16 bảng nghiệp vụ trước/sau: không thay đổi. Kiểm thử không tạo phiếu mới; trạng thái module đã khôi phục như trước.
- Bốn classifier JAR đã kiểm tra chỉ chứa package sở hữu; đã xuất bốn ZIP tại backups/business-modules.
- Backend, frontend và PostgreSQL local healthy. Server/Git chưa cập nhật cho đợt 2.


## Đồng bộ main ngày 08/10/2026
- Main local đã cập nhật từ origin/main tại commit 498286a (đã merge warehouse và haianh_dev).
- Giữ toàn bộ source Đợt 2 ở trạng thái chưa commit; main theo dõi origin/main.
- Dịch vụ danh mục/sinh mã mới được chuyển vào vn.aicrm.shared; controller tiếp tục dùng InventoryPort, command giữ khóa trạng thái module và kiểm tra trước replay.
- Giao diện sản phẩm mới được ghép vào shared-workspace, giao diện Bán hàng/CSS nằm trong retail/sales; giữ bộ điều phối module riêng.
- Snapshot trước đồng bộ được giữ trong Git stash với tên phase2-before-sync-main-20261008; mã snapshot lưu local tại backups/git-sync/phase2-stash.txt.
- Chỉ đồng bộ nội dung đã nằm trên main. Các commit phát sinh sau đó trên nhánh dev chưa merge vào main không được đưa vào bản này.
- Kiểm tra sau đồng bộ: frontend build/TypeScript đạt; 22 test backend đạt qua các lượt kiểm tra; 17 kiểm tra API và 12 kiểm tra UI đạt. Backend/frontend local đã cập nhật, các bảng nghiệp vụ có số lượng bản ghi không thay đổi. Không commit/push hoặc triển khai server.

## Phát hành 08/10/2026
- Được yêu cầu commit/push main và cập nhật server. Các ghi chú chưa commit/server ở phần nghiệm thu trước là trạng thái lịch sử.
- Database hiện có không cần cài lại/import; không thay migration. Xem LOCAL_SYNC_DATABASE_PHASE2.md để đồng bộ máy dev.
