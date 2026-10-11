# Tách module nghiệp vụ và cài trên core mới — Đợt 3

## Phạm vi

Triển khai trên local, giữ luồng nghiệp vụ và database hiện tại. Không tự commit, push hoặc deploy server trong đợt này.

Core giữ đăng nhập, người dùng/vai trò, permission engine, tổ chức kỹ thuật, module/resource/navigation registry, audit, outbox, jobs, tài nguyên động, files và cấu hình hệ thống. Core không import Java class hoặc giao diện nghiệp vụ cụ thể.

Các gói nghiệp vụ có descriptor, menu, API boundary và frontend entry riêng:

| Gói | Nội dung | Phụ thuộc nghiệp vụ |
|---|---|---|
| aicrm-retail | Hợp đồng dịch vụ, giao dịch chung, phân quyền nghiệp vụ, schema/migration lịch sử, widget dùng chung | Không |
| aicrm-products | Sản phẩm, nhóm, thương hiệu, đơn vị, thuộc tính, quy đổi, danh mục/giá | Nền tảng nghiệp vụ |
| aicrm-partners | Customer Master / Đối tác | Nền tảng, Sản phẩm |
| aicrm-operations | Địa điểm, thiết lập, import đầu kỳ, phạm vi, màn hình cần duyệt | Nền tảng, Sản phẩm |
| aicrm-accounting | Thu chi, công nợ, hoa hồng | Nền tảng, Đối tác, Vận hành |
| aicrm-stock | Kho, tồn, chuyển kho, kiểm kê, tracking | Nền tảng, Sản phẩm, Vận hành, Kế toán |
| aicrm-sales | POS, đơn bán, giao hàng, đổi trả, bảo hành | Nền tảng, Kho, Đối tác, Kế toán |
| aicrm-purchase | Mua, nhận hàng, trả mua, nhà cung cấp | Nền tảng, Kho, Đối tác, Kế toán |
| aicrm-crm | Lịch sử, ghi chú, nhắc chăm sóc | Nền tảng, Sản phẩm, Đối tác, Vận hành |
| aicrm-overview | Dashboard nghiệp vụ | Nền tảng, Sản phẩm, Kế toán |
| aicrm-reports | Báo cáo và xuất báo cáo | Nền tảng, Sản phẩm, Vận hành |

Tất cả vẫn dùng chung dữ liệu master và một PostgreSQL. Chưa tách thành microservice/database riêng. Gói nền tảng giữ schema lịch sử để không sửa checksum Flyway; widget/biểu mẫu dùng chung tiếp tục tái sử dụng. Tách theo package/hợp đồng và điểm đăng ký, không sao chép luồng nghiệp vụ giữa các module.

## Cách cài đặt core mới

Yêu cầu: Node.js >=22.13, Docker Desktop/Engine với Compose v2; môi trường build cần tải Maven/npm dependencies và Docker images lần đầu. Core mới cần dùng starter được xuất từ source này, có import contract v2 và core API 1.1.0. Core cũ từ repository khác chưa có hợp đồng này không thể import trực tiếp.

Tại source AICRM:

```powershell
node scripts/module-manager.mjs pack --out backups/business-modules/aicrm-suite.module.json
node scripts/module-manager.mjs export-core --target ../aicrm-core-new
```

Starter không chứa code nghiệp vụ và chỉ có 23 migration core. Tạo `.env` theo `.env.example` tại starter, dùng mật khẩu riêng. Chọn tên Docker project và các port riêng nếu chạy cùng hệ thống hiện tại. Không sao chép `.env` production vào gói.

Có thể chạy core trống trước bằng `node scripts/module-manager.mjs start --target ../aicrm-core-new`.

Import toàn bộ nghiệp vụ và vận hành:

```powershell
node scripts/module-manager.mjs import --file backups/business-modules/aicrm-suite.module.json --target ../aicrm-core-new --dry-run
node scripts/module-manager.mjs import --file backups/business-modules/aicrm-suite.module.json --target ../aicrm-core-new --start
```

Import riêng chức năng và tự lấy phụ thuộc trong cùng gói:

```powershell
node scripts/module-manager.mjs import --file backups/business-modules/aicrm-suite.module.json --target ../aicrm-core-new --modules aicrm-sales,aicrm-crm --start
```

Sau import, công cụ tự tạo frontend registry; Spring Boot tự phát hiện cấu hình nghiệp vụ; Flyway áp dụng V24–V33; registry đồng bộ module, resource và menu. Đăng nhập admin rồi làm hướng dẫn thiết lập công ty/địa điểm/kho, danh mục và dữ liệu đầu kỳ. Gói không chứa dữ liệu khách hàng hay chứng từ; chuyển dữ liệu cần quy trình backup/restore hoặc import dữ liệu riêng.

## Import và vận hành

- Đây là import source và build/restart tự động. Không phải hot upload JAR vào ứng dụng đang chạy. Nút bật/tắt trong Quản lý module áp dụng cho module đã cài; chưa có nút upload gói trên trình duyệt.
- `--dry-run` kiểm tra gói mà chưa ghi file. SHA-256 phát hiện sai lệch nội dung, không thay thế chữ ký nhà phát hành; chỉ dùng gói từ nguồn quản trị tin cậy vì code được chạy khi build/start.
- Kiểm tra format/version, key, đường dẫn, giới hạn kích thước, checksum, file trùng, phụ thuộc thiếu/vòng lặp. Gói không được ghi đè code core hoặc `.env`.
- File đã custom được bảo vệ: bản import mới chỉ thay file còn trùng bản đã cài; xung đột dừng trước khi ghi. `module-install.lock.json` lưu owner/checksum.
- File trước import được lưu trong `backups/module-import/<id>`. Nếu lỗi ghi source, công cụ khôi phục các file đã ghi trong lần đó. Build thất bại thì container cũ chưa được thay; source đã import và bản backup được giữ để dev xử lý hoặc phục hồi.
- Build backend/frontend hoàn tất trước `compose up --wait`. Nếu startup/migration thất bại, phải xử lý theo log; không tự rollback database.
- Với hệ thống đang có dữ liệu, backup database trước nâng cấp. Không xóa volume, không cài lại DB, không sửa V1–V33, không import lại dump khởi tạo. Không bỏ migration lịch sử khi tắt module.
- Tắt module: menu và capabilities bị lọc; API kiểm tra module cả với admin. Không tắt core; không tắt phụ thuộc còn module đang sử dụng; bật theo thứ tự phụ thuộc. Thuộc tính module không thay quyền và phạm vi chi nhánh/kho của người dùng.

## Phân công mở rộng

Dev thêm chức năng vào package sở hữu; giao tiếp qua interface trong gói nền tảng. Java core dùng SPI, không import `vn.aicrm`. Frontend core chỉ đọc `InstalledFrontendModule`; package manager tạo literal dynamic import để Next.js build được. Manifest `module-platform.json` là danh sách cài đặt; metadata frontend bổ sung entry/mobile/bootstrap. Khi thêm module mới phải mở rộng danh mục gói được hỗ trợ và kiểm thử import, dependency, API/menu, core trống.

## Kiểm thử

`node --test scripts/module-manager.test.mjs`: kiểm tra gói, đường dẫn, checksum, graph, export core trống, import chọn chức năng, import lặp, bảo vệ custom và version không tương thích.

`BusinessBoundaryTest`: core không phụ thuộc code nghiệp vụ; nền tảng không phụ thuộc implementation; các package nghiệp vụ chỉ dùng hợp đồng chung. Backend build, frontend build và kiểm thử chạy thực tế được ghi bổ sung trong báo cáo tiến độ.
Kết quả nghiệm thu local: 20 Java test, 8 package-manager test, 32 API/giao dịch sau import, 7 API gán vai trò core, 6 kiểm tra cài một phần và 12 màn hình trình duyệt đều qua. Core trống và core sau import đều đã build/chạy; local chính healthy, Flyway giữ phiên bản 33. Chi tiết trong `BUSINESS_MODULES_PHASE3_PROGRESS.md`.
