# CRM — Tiềm năng và Cơ hội

Ngày 12/10/2026. Đã triển khai local AICRM http://localhost:3205; chưa commit/push/deploy server. Bản crm-dai-thang không được sửa trong đợt này.

## 1. Menu và luồng

CRM gồm Khách hàng, Khách hàng tiềm năng, Cơ hội. Khách hàng tiếp tục dùng Customer Master. Tiềm năng là hồ sơ trước khi trở thành khách hàng, không tự thêm vào Customer Master khi tạo.

Thêm tiềm năng → liên hệ/đánh giá → tạo cơ hội (chọn tiềm năng trên form Cơ hội) → chuyển tiềm năng thành khách hàng mới hoặc liên kết khách hàng hiện có. Chuyển đổi cập nhật liên kết khách hàng của các cơ hội cùng tiềm năng, giữ hồ sơ nguồn để đối chiếu. Không tự tạo cơ hội khi chuyển đổi. Không tự tạo báo giá/đơn bán khi cơ hội thành công.

Giá trị cơ hội chỉ là dự kiến, không ghi doanh thu/tồn kho/công nợ. Ghi chú nội bộ ở hồ sơ; lịch chăm sóc vẫn tại tab Chăm sóc của khách hàng hiện hành. Chưa có email campaign, scoring tự động, pipeline tùy biến hoặc xóa hồ sơ từ UI.

## 2. Trường và quy tắc

| Tiềm năng | Quy tắc |
|---|---|
| Mã | Optional khi tạo, tự sinh TN- + UUID prefix; unique tenant, tối đa60; không sửa sau tạo |
| Tên / người liên hệ | Bắt buộc, tối đa160 |
| Địa điểm | Bắt buộc, active và trong phạm vi; không đổi khi đã có cơ hội |
| Người phụ trách | Optional, mặc định người tạo; account active cùng tenant; không tự cấp quyền |
| Công ty, điện thoại, email | Optional, giới hạn160/60/160; email phải hợp lệ nếu nhập |
| Địa chỉ, nguồn, ghi chú | Optional, giới hạn2000/100/4000 |
| Trạng thái | NEW/Mới; CONTACTED/Đang liên hệ; QUALIFIED/Đủ điều kiện; DISQUALIFIED/Không phù hợp; CONVERTED/Đã chuyển đổi |
| Khách hàng liên kết | Chỉ đặt qua chuyển đổi; không chỉnh bằng form tiềm năng |

| Cơ hội | Quy tắc |
|---|---|
| Mã/tên/địa điểm/người phụ trách | Như tiềm năng; mã tự sinh CH- |
| Tiềm năng hoặc khách hàng | Ít nhất một; tiềm năng cùng địa điểm và không DISQUALIFIED; khách hàng active có vai trò CUSTOMER |
| Tiềm năng đã chuyển đổi | Khách hàng phải khớp hồ sơ chuyển đổi; UI tự chọn và khóa liên kết |
| Giá trị dự kiến | VND nguyên, không âm; mặc định0 |
| Ngày chốt dự kiến | Optional, ngày hợp lệ |
| Giai đoạn | NEW/Mới; CONTACTED/Đang trao đổi; QUOTED/Báo giá; NEGOTIATION/Đàm phán; WON/Thành công; LOST/Không thành công |
| Lý do không thành công | Bắt buộc khi LOST, tối đa1000 |
| Ghi chú | Optional, tối đa4000 |

Giai đoạn được cập nhật thủ công; không bắt buộc đi tuần tự. WON/LOST vẫn sửa được với quyền UPDATE, có audit/version. Trạng thái CONVERTED chỉ qua action; không sửa tiềm năng đã chuyển đổi, cập nhật thông tin khách hàng ở Customer Master. Tiềm năng DISQUALIFIED không chuyển đổi. Chọn hồ sơ khách hàng hiện hữu nếu đã có; không tự ghép theo tên/điện thoại.

## 3. Phân quyền và dữ liệu

- leads: VIEW/CREATE/UPDATE/CONVERT; opportunities: VIEW/CREATE/UPDATE.
- Chuyển khách hàng mới cần thêm customer CREATE; chọn khách hàng cũ cần customer VIEW. Các action đều cần VIEW theo quy tắc core.
- Cơ hội tham chiếu tiềm năng cần leads VIEW; tham chiếu khách hàng cần customer VIEW.
- Danh sách/sửa theo tenant và địa điểm phụ trách. Admin hiện hành full quyền; các role khác không được cấp ngầm quyền mới.
- API kiểm module CRM và dependencies; tắt module chặn cả đọc/ghi. Tệp/Customer Master không được nhân bản thành kho khách hàng CRM riêng.
- Ghi qua RetailCommands: transaction, Idempotency-Key, audit/outbox; version chống ghi đè stale. Replay cùng key/payload trả kết quả cũ, nội dung khác409.

## 4. API và migration

GET /api/v1/retail/crm/options: địa điểm trong scope, account active tenant, customers nếu customer VIEW, tối đa500 tiềm năng gần nhất trong scope nếu leads VIEW; capabilities.

GET /api/v1/retail/crm/leads và /opportunities: q theo mã/tên, state, page từ0,20dòng; trả items/total/page/pageSize. POST cùng path tạo; POST /{id} sửa với version. POST /leads/{id}/convert gồm version và partnerId optional, Idempotency-Key bắt buộc.

V34__crm_leads_opportunities.sql thêm retail.crm_lead và crm_opportunity, tenant/composite FK branch/partner/lead, RLS, index và grants. Không chỉnh V1–V33. V34 thuộc source package aicrm-crm; module manager pack/import giữ migration cùng CRM. Bản standalone/tenant SaaS khi nâng cấp phải chạy migration riêng theo quy trình của mình.

## 5. Hướng dẫn sử dụng

1. CRM → Khách hàng tiềm năng → Thêm tiềm năng; nhập tên, chọn địa điểm, thêm liên hệ/nguồn nếu có; Lưu.
2. Sửa để cập nhật trạng thái, người phụ trách và ghi chú.
3. CRM → Cơ hội → Thêm cơ hội; chọn tiềm năng hoặc khách hàng, nhập nhu cầu/tên, giá trị/ngày dự kiến/giai đoạn; Lưu.
4. Khi đủ điều kiện, tại tiềm năng chọn Chuyển khách hàng; chọn hồ sơ hiện hữu hoặc tạo mới; Xác nhận. Dữ liệu xuất hiện trong Customer Master dùng chung.
5. Cập nhật cơ hội WON/LOST; khi LOST nhập lý do. Nghiệp vụ báo giá/đơn bán thực hiện trong Bán hàng riêng.

Danh sách có STT, tìm kiếm/trạng thái,20dòng/trang, mã/tên, liên hệ hoặc đối tượng, địa điểm/người phụ trách, thao tác. Mobile danh sách dạng thẻ và form toàn màn hình, đủ mã/ngày/phụ trách.

## 6. Xác minh và vận hành local

Backend package và 3 BusinessBoundaryTest đạt; frontend production/TypeScript đạt; 9 module-manager tests đạt (gồm import CRM với V34 vào core mới). 17 nhóm API check trên DB cách ly gồm CRUD, idempotency/conflict, duplicatecode/email/amount/lostreason, conversion mới/cũ, converted protection, branchscope, cross-tenant và module disabled. UI tạo tiềm năng/cơ hội qua backend test, mobile320/390/430px, menu3mục, không lỗi JavaScript.

Backup trước V34: backups/crm-pipeline-20261012/before-v34.dump (ignored, nhạy cảm). DB thử aicrm_crm_test_20261012 chứa bản sao và fixtures, không phải DB vận hành; backend test8188 dừng sau kiểm tra. Báo cáo/screenshots ở cùng thư mục backup. Database chính core_platform: V34 success; tại nghiệm thu leads/opportunities0dòng. Không tạo phiếu hoặc đổi dữ liệu nghiệp vụ cũ trong đợt này.

Build local dùng Maven cache offline và runtime image từ JAR đã đóng gói; Dockerfile chuẩn trong source giữ nguyên cho lần build sau. Backend image trước thay đổi được giữ tag before-crm-v34. Không rollback migration bằng xóa bảng khi đã có dữ liệu; cần backup/đối chiếu kế hoạch riêng.
