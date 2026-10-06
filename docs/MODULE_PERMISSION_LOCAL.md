# Phân quyền module — triển khai local

Phương án được người dùng duyệt ngày 06/10/2026. Tài liệu gốc: DE_XUAT_PHAN_QUYEN_MODULE_v1.0.md. Chỉ triển khai local; chưa commit/push.

## Cách dùng

1. Quản trị hệ thống → Phân quyền module (`/business/retail/permissions`).
2. Màn hình chính hiển thị danh sách vai trò với STT, Mã vai trò, Tên vai trò. Chọn + Thêm vai trò để nhập mã/tên và quyền; bấm tên vai trò để xem/chỉnh sửa.
3. Cấp quyền chức năng theo Cho phép/Từ chối/Chưa cấp. Chọn thao tác tự chọn Xem; bỏ Xem sẽ bỏ các ALLOW phụ thuộc. Có lý do và danh sách trước/sau trước lưu.
4. Chọn người dùng ở Xem trước quyền hiệu lực; kiểm nguồn ALLOW/DENY và kho được giao.
5. Mở Gán vai trò và Giao kho phụ trách để cập nhật người dùng, nhập lý do. Quyền toàn công ty nằm trong vai trò, không suy ra từ số kho được chọn.

Application User có thể cấu hình quyền nghiệp vụ. Platform Administrator và các vai trò hệ thống khác được bảo vệ; không sửa vai trò hoặc phạm vi của chính người quản trị. Các chính sách Core/custom giữ nguyên. DENY ưu tiên mọi ALLOW, gồm wildcard. Không dùng quyền retail cũ làm fallback.

## Hiện trạng chuyển đổi

Local trước cập nhật chỉ có platform-admin: 1 tài khoản; application-user: 0; chính sách *:*:ALLOW. Không có tài khoản nhân viên hay chính sách aicrm-retail cần mapping. Giữ tài khoản quản trị hiện tại, không tự tạo/gán quyền cho nhân sự thực. Vai trò mẫu được tạo khi quản trị viên chọn. Database test riêng aicrm-permission-check dùng để kiểm chứng, không nhập demo vào vận hành.

## Backend

- Resource/action đăng ký tại RetailPermissions; Core PermissionService quyết định ALLOW/DENY và revision.
- RetailCommands kiểm quyền và phạm vi trước đọc kết quả idempotency. Loại chứng từ lấy từ database khi thao tác; list/count/detail/export lọc đúng loại được cấp.
- Customer Master kiểm vai trò và trường ngân hàng; quick-create chỉ CUSTOMER. Lookup giao dịch trả dữ liệu tối thiểu; không mở quyền sửa master.
- Thu/chi và phải thu/phải trả lọc riêng. Giá vốn và bank bị loại khỏi dữ liệu khi thiếu quyền; snapshot chứng từ cũng được lọc.
- Duyệt độc lập không bắt buộc CREATE; không tự duyệt chứng từ mình. Bán công nợ giữ quy tắc hiện hành, không thêm chặn hạn mức.
- Kho/chuyển/kiểm kê/giao hàng/bảo hành/import/export có quyền riêng. Import kiểm lại cả quyền nhập và ghi loại đích; export chứng từ hỗn hợp lọc VIEW+EXPORT từng loại.
- Core navigation dùng NavigationCapabilityProvider do module đóng góp, không phụ thuộc trực tiếp nghiệp vụ Retail. Menu tổng hợp chỉ hiện nếu có quyền tại chức năng liên quan; trang frontend có boundary chặn URL ngoài quyền.
- Migration V32 chỉ bổ sung DELETE trên retail.access_scope để thay danh sách kho được giao; không cấp DELETE cho chứng từ/sổ nghiệp vụ. Không thay dữ liệu nghiệp vụ.

## Kiểm chứng và phạm vi thực tế

Bằng chứng API: module-permission-check-result.json; UI: module-permission-ui-check-result.json. Các script ghi vào backend/database tạm cổng 8186. Ảnh ma trận: docs/screenshots/permissions.

Các quyền chỉ thực thi trên thao tác hiện có. Phân quyền không tự bổ sung API sửa nháp, xóa chứng từ, workflow hủy phần đã duyệt, hoặc quy trình duyệt phiếu chi độc lập chưa có trong hệ thống. Nút yêu cầu hủy phần chưa xuất vẫn theo tình trạng hỗ trợ hiện hành. Không tuyên bố quyền In ngăn người đã được Xem chụp/lưu nội dung. Core quyền người dùng/vai trò/policy vẫn quản lý như trước, ma trận nghiệp vụ không thay toàn bộ Core Access UI.

Mỗi chức năng dùng chính sách riêng; cờ READ/SELL/SETUP trong payload catalog chỉ là tương thích UI, được suy từ quyền mới, không cấp quyền backend. Các nhóm CRM/khách, sale/khách, mua/NCC sử dụng một master. Khi cấu hình vai trò tùy chỉnh, cấp quyền dữ liệu khách/NCC và quyền giao dịch phù hợp như các mẫu đã cung cấp.

Backup trước thay đổi có tại backups/dev-handoff-20261006-155958 (V31). Sau cập nhật local dùng V32; bản database đã bàn giao trước đó chưa bao gồm V32 hoặc quyền mới.

## Kết quả triển khai ngày 06/10/2026

- Đã cập nhật backend và frontend local tại http://localhost:3205; backend 8185 healthy. Migration V32 áp dụng thành công.
- 47 chức năng có danh mục quyền theo thao tác. 19 kiểm tra API trên database riêng đạt; 4 kiểm tra UI desktop/mobile đạt; 7 kiểm thử Core navigation/ranh giới module đạt.
- Smoke local xác nhận đăng nhập quản trị, trang phân quyền và navigation; vẫn đọc được 2 sản phẩm và 4 chứng từ hiện có. Không ghi dữ liệu nghiệp vụ thử vào local.
- Kết quả smoke: module-permission-local-smoke-result.json. Build backend/frontend thành công. Chưa commit, chưa push Git.
- Bộ lọc xuất báo cáo tách quyền EXPORT bán/mua theo loại từng dòng; commit import kiểm kho trước đọc kết quả idempotency.
- 12 kiểm tra hồi quy Bán hàng/POS đạt sau cập nhật local; chỉ đọc local và chặn ghi nghiệp vụ qua fixture trình duyệt. Bằng chứng: sales-ui-check-result.json.

## Cập nhật giao diện checkbox theo mẫu

Quyền thao tác hiển thị dạng checkbox, ba cột trên desktop, hai/một cột theo màn hình. Mỗi nhóm module có Chọn tất cả/Bỏ chọn tất cả, áp dụng các chức năng đang hiển thị theo bộ lọc. Gán vai trò người dùng cũng dùng lưới checkbox và thao tác chọn hàng loạt. Quyền DENY nằm trong phần Từ chối rõ ràng (nâng cao); chọn ALLOW sẽ thay DENY tương ứng và vẫn yêu cầu xem lại thay đổi/lý do trước lưu. Bỏ chọn không tạo DENY; quyền từ vai trò khác hoặc Core/custom vẫn có thể hiệu lực. Backend và quy tắc phân quyền không thay đổi trong lần chỉnh giao diện này.
- Giao diện checkbox đã cập nhật local; build/TypeScript và ESLint đạt, 6 kiểm tra UI đạt (chọn/bỏ chọn quyền và vai trò, lưu quyền, desktop/mobile không tràn, không lỗi runtime). Chưa commit/push.

## Sửa tình trạng không tích chọn được

Checkbox Application User được mở để cấp quyền nghiệp vụ và vai trò này được chọn mặc định khi vào màn hình. Backend trả editable/editBlockReason theo vai trò; frontend khóa đúng trường hợp bảo vệ hoặc tự sửa quyền của mình, kèm thông báo cụ thể. Không còn khóa toàn bộ vai trò có system_role=true. Không thay chính sách quyền đang có khi mở trang.
- Bản sửa đã chạy local: 20 kiểm tra API, 6 kiểm tra UI, TypeScript/ESLint và build đạt. Smoke trên backend 8185 xác nhận Application User editable, không ghi thay đổi quyền vào database vận hành. Chưa commit/push.

## Danh sách vai trò và biểu mẫu thêm/chỉnh sửa

Màn hình chính có bảng STT / Mã vai trò / Tên vai trò, tìm theo mã hoặc tên và nút + Thêm vai trò. Popup thêm gồm mã/tên bắt buộc và checkbox quyền theo module; chọn/bỏ chọn tất cả theo nhóm, kiểm tra thay đổi trước lưu. Popup chi tiết nạp các quyền đã lưu; vai trò được bảo vệ có lý do khóa. API POST /api/v1/retail/permissions/roles tạo vai trò và chính sách trong một transaction; mã trùng bị từ chối, lỗi quyền làm rollback cả vai trò. Vai trò mới chưa tự gán cho tài khoản nào. Phần xem quyền hiệu lực/gán vai trò/giao kho vẫn nằm dưới danh sách.
- Đã cập nhật bản danh sách/popup trên local: 21 kiểm tra API (gồm rollback tạo vai trò) và 7 kiểm tra UI đạt; build backend/frontend, TypeScript, ESLint đạt. Các endpoint ghi có transaction được khai báo public để ranh giới giao dịch rõ ràng. Chưa commit/push.

## Chọn lại vai trò trong Module Người dùng

Tại Quản trị hệ thống → Người dùng, mỗi dòng có Chọn lại vai trò. Popup tải roleIds hiện tại, dùng danh sách vai trò chung, cho chọn nhiều vai trò và nhập lý do trước lưu. Dùng API preview/users roles hiện có với revision để cập nhật và làm mới danh sách. Backend giữ bảo vệ không tự sửa vai trò của chính mình; không thay kho được giao.
- Frontend đã build/TypeScript thành công và cập nhật local. 3 kiểm tra giao diện giả lập đạt: tải vai trò hiện có, lưu đúng roleIds/revision/lý do và làm mới, mở lại đúng lựa chọn mới. Không ghi quyền vào tài khoản vận hành; chưa commit/push.

## Ngoại lệ tài khoản quản trị

Theo yêu cầu, admin@core.local có vai trò platform-admin được phép tự lập và tự duyệt/từ chối chứng từ. Nhận diện bằng tài khoản database trong tenant hiện hành, còn enabled và được gán platform-admin; không dựa vào email từ frontend. Catalog trả canSelfApprove để mở nút đúng tài khoản. Vẫn kiểm quyền APPROVE/REJECT, trạng thái/version và lý do, ghi nhật ký như trước. Các tài khoản khác giữ quy tắc không tự duyệt. Tài khoản admin dùng chính sách wildcard hiện có để full quyền; không bỏ kiểm dữ liệu/tồn kho/quy tắc chứng từ.
- Local đã xác nhận admin có đủ mọi capability đã đăng ký và canSelfApprove=true, không ghi phiếu thử vào vận hành. 23 kiểm tra API trên database riêng đạt, gồm admin tự duyệt và nhân viên dù có APPROVE vẫn không tự duyệt. Build backend/frontend thành công; chưa commit/push.
- 12 kiểm tra hồi quy giao diện Bán hàng/POS đạt sau cập nhật.

## Địa điểm và kho mặc định

Mỗi công ty/tenant có ít nhất một địa điểm và một kho hoạt động. Khi khởi động và lần đầu truy cập catalog, hệ thống tự bổ sung Trụ sở chính nếu chưa có địa điểm hoạt động, rồi Kho chính nếu chưa có kho hoạt động thuộc địa điểm hoạt động. Không đổi tên hoặc tạo trùng danh mục sẵn có; dùng khóa giao dịch theo tenant. Admin có toàn quyền nên sử dụng kho mặc định; nhân viên vẫn phải được giao kho. Không coi tài khoản admin là công ty/kho.

Trường chọn kho/địa điểm của biểu mẫu giao dịch được ẩn khi chỉ có một lựa chọn hợp lệ và dữ liệu được tự chọn; có nhiều lựa chọn thì hiển thị. Chuyển kho vẫn cần chọn kho đích. Chặn ngừng kho hoặc địa điểm hoạt động cuối cùng; không ngừng địa điểm còn kho hoạt động.
- Local đã cập nhật backend/frontend; giữ nguyên Chi Nhánh 1/KHO-1, 2 sản phẩm và 4 chứng từ, catalog đọc lặp không tạo trùng. 4 kiểm tra defaults trên database mới và 23 kiểm tra phân quyền đạt. Build/TypeScript đạt; chưa commit/push.
- 13 kiểm tra hồi quy Bán hàng/POS đạt, gồm tự chọn và ẩn selector kho duy nhất. Kết quả defaults: retail-defaults-check-result.json; smoke local: retail-defaults-local-result.json.
