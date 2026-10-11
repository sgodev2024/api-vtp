# SRS hiện hành — AICRM

Phiên bản 3.0 — 09/10/2026. Baseline: main ae59355 cộng thay đổi local đợt 3 chưa commit. Server chưa được xác minh cùng snapshot. Nguồn: frontend/backend, manifest, migration V1–V33 và audit chỉ đọc 09/10. Docker đang tắt lúc viết nên không query live schema.

## 1. Mục đích và quy ước

BA_HIEN_HANH.md định nghĩa nghiệp vụ; SRS xác định dữ liệu/hợp đồng/FR và AT; RUNBOOK_SU_DUNG.md hướng dẫn vận hành. “Hiện có” khác “Hạn chế/backlog”. AT là ca cần chạy khi nghiệm thu, không tuyên bố đã chạy tất cả trong lần lập tài liệu. Khai báo quyền không chứng minh action đã có đầy đủ UI/API. Task dev tiếp tục dùng DEV_TASKS_2026-10-09.json, không tạo backlog cạnh tranh.

## 2. Kiến trúc và module

Spring Boot/Java, Next.js, PostgreSQL/Flyway. Core namespace vn.coreplatform; business vn.aicrm/shared, products, partners, operations, inventory, sales, purchase, crm, accounting, overview, reports; frontend entry từng gói. RetailDocuments vẫn là engine chung, không phải microservice/database riêng.

Core gồm kernel, permission, local-identity, audit-store, event-outbox, job-queue, webhook, dynamic-resource, file-management, control-plane. Migration core V1–V23, business V24–V33. Manifest module-platform.json; core API 1.1.0/import contract 2. Registry frontend được sinh, Boot AutoConfiguration backend phát hiện. Giữ migration lịch sử khi tắt gói, không sửa migration đã chạy.

## 3. Mô hình dữ liệu

UUID dùng cho ID; tenant lấy auth, không cho payload chọn tenant. STT chỉ hiển thị. Danh mục chung tenant, giao dịch scope địa điểm/kho. Các FK ghép tenant và kiểm tra service/scope đều phải giữ. Command camelCase, response đọc DB có snake_case; không giả mọi endpoint có cùng vỏ response.

| Thực thể logic | Quan hệ | Dữ liệu |
|---|---|---|
| branch / warehouse | Tenant; warehouse thuộc branch | Mã, tên, active, kind, default, liên hệ, manager |
| company_profile (onboarding_step) | Tenant | Công ty và bước wizard |
| access_scope | Account + warehouse + tenant | Kho phụ trách, branch suy ra |
| sku | Tenant/code và barcode unique | Units, tracking, giá/VAT, policies, version |
| uom / uom_conversion | SKU–UOM | Hệ số, đồng bộ trigger V29 |
| warehouse_config | Tenant/kind/code | INVENTORY, UNIT, VARIANT, BRAND, ATTRIBUTE, GROUP |
| partner | Tenant/code unique | Roles JSON, hồ sơ, bank, version |
| price_rule / promotion | SKU và điều kiện | Giá/ngưỡng/priority/hiệu lực/stackable |
| document / line | Source document/line | Trạng thái, tiền, snapshot, tiến độ |
| balance / lot / serial / movement | Warehouse/SKU/tenant | Tồn, cách ly, hạn, serial, ledger |
| open_item / payment / allocation | Partner/source | AR/AP, thu chi, phân bổ |
| delivery / warranty / care | Phiếu/dòng gốc hoặc partner | Giao, bảo hành, chăm sóc |
| commission | Partner/period | Khoản hoa hồng |
| import_batch / command | Tenant/actor/kind/key | Preview/commit và chống lặp |

Tên và cột vật lý kiểm tra migration phụ lục; bảng logic này không thay dump database.

### 3.1. Công ty và kho

CompanyName bắt buộc 160; phone/email/address/taxCode optional 60/160/2000/60. Branch code/name bắt buộc 60/160, unique tenant; phone/email/address/note optional; managerId là account active cùng tenant. Warehouse branchId active/trong scope, code/name required, kind SALEABLE/QUARANTINE, default boolean. Kho cũ không đổi branchId. Không ngừng default trước khi chuyển default; validate dữ liệu liên quan khi ngừng.

### 3.2. SKU, cấu hình và conversion

code 60 có thể sinh, name 160 required, barcode 100 optional unique, variant 160 text, unit 40 required, units JSON factor>0≤4 số lẻ; tracking NONE/LOT/SERIAL; price≥0≤4 số lẻ; VAT0..100; weight≥0; returnDays/warrantyDays nguyên≥0; allowOpened/active boolean. Không có parent-product/combinations chuẩn trong snapshot.

Conversion command productId/uomId UUID, factor>0≤4 số lẻ; UOM cùng tenant, khác baseUnit; khóa SKU rồi cập nhật units JSON, trigger V29 sync table. Base factor 1; line ratio giữ snapshot.

Config kind enum, code 60 có thể sinh, name 160, value 1000, active. INVENTORY/defaultTracking value chỉ NONE/LOT/SERIAL. PriceRule skuId required, partner/group/branch/channel optional, minQty>0, price≥0, priority nguyên, validFrom/To ngày và To≥From. Promotion percent>0≤100, priority, stackable, khoảng ngày.

### 3.3. Partner

code/name, entityType PERSON/BUSINESS, phone/email/address; companyName/companyAddress/taxCode/contactName/contactTitle/contactPhone/ownerId; shippingAddress/customerGroup/saleTerms/purchaseTerms; saleDays/purchaseDays nullable 0..3650; bankName/bankAccount/bankHolder; roles/active/version. NCC6 trường chung bắt buộc, bổ sung/bank optional. Bank phải có grant riêng; owner cùng tenant. Sửa kiểm version. Quick-create customer cố định CUSTOMER; không cho tự nâng role qua payload nhanh.

### 3.4. Chứng từ

| Trường header command | Bắt buộc / điều kiện | Kiểm tra |
|---|---|---|
| type | Required | 17 loại hỗ trợ |
| warehouseId | Required | Active, tenant, scope; branch suy ra |
| partnerId | Theo loại | NCC mua, khách đơn/công nợ; POS/ISSUE chưa đủ tiền cần khách |
| sourceId | Theo luồng | ISSUE/trả/hoàn cần đúng nguồn |
| destinationId | TRANSFER | Khác nguồn, active, scope |
| channel | Default COUNTER | COUNTER/ONLINE/WHOLESALE/COLLABORATOR |
| note/reason | Theo action | Reason cho hủy/từ chối/ngoại lệ |
| paid/method | Có thanh toán | Paid nguyên0..total; không dùng tiền đưa vượt tổng |
| taxInclusive | Form/line | Giá gồm/chưa VAT |
| validUntil/deliveryAddress | Metadata | Hạn báo giá và địa chỉ, expiry guard chưa đủ |
| fee/costPending/allocationMethod | Legacy API | Không đưa lại form nhập mua/đơn bán |
| exchangeReturnId | Optional | Chỉ tham chiếu, chưa bút toán đối trừ |

Line tối đa 200: skuId; sourceLineId khi từ nguồn; qty>0≤4 số lẻ; unit hợp lệ; ratio nguồn/SKU; baseQty=qty×ratio; price≥0; discount nguyên 0..gross; VAT0..100; LOT lot/expiry; SERIAL array đúng lượng; condition; COUNT countedQty≥0, không trùng SKU. Qty kỹ thuật COUNT được chuẩn hóa, số thực dùng countedQty.

Snapshot sku/rule/ratio/giá/policy; net/tax/total/cost/progress/closed_qty/segments do server. Không nhận tenant/creator/approver/cost tự gán từ client. Số phiếu tự cấp theo loại/năm dưới khóa tenant.

### 3.5. Tiền và thuế

Gross=ROUND(qty×price, HALF_UP,0). Discounted=gross−discount. Chưa VAT: net=discounted, tax=ROUND(net×vat/100,0). Gồm VAT: net=ROUND(discounted/(1+vat/100),0), tax=discounted−net. Total dòng=net+tax. Ví dụ 110000 gồm 10%: net 100000/tax 10000. Phiếu tổng theo dòng và fee hợp lệ nếu có.

Dòng nguồn phân bổ net/tax theo lượng, giữ phần dư lần cuối khi trả nhiều lần. Lượng numeric(20,4), tiền numeric(20,0), vốn numeric(24,8). Frontend JS chỉ hiển thị, không quyết định số ghi sổ.

## 4. Vòng đời và tác động

Danh sách type chính thức: QUOTE, SALE_ORDER, POS, ISSUE, PURCHASE_REQUEST, PURCHASE_ORDER, PURCHASE_RECEIPT, PURCHASE_RETURN, SALE_RETURN, TRANSFER, COUNT, OTHER_RECEIPT, OTHER_ISSUE, REFUND, OPENING_STOCK, OPENING_AR, OPENING_AP.

| Loại | Luồng thường | Ghi nhận |
|---|---|---|
| QUOTE | DRAFT → APPROVED → chuyển/đóng | Không tồn/nợ |
| SALE_ORDER | DRAFT → APPROVED → PARTIAL/COMPLETE/CLOSED | Tiến độ, chưa reservation |
| POS/ISSUE | DRAFT → POSTED hoặc PENDING → POSTED | Xuất, AR, thu/phân bổ |
| PURCHASE_REQUEST/ORDER | DRAFT → PENDING → APPROVED → PARTIAL/COMPLETE/CLOSED | Tiến độ, chưa tăng tồn |
| PURCHASE_RECEIPT | DRAFT → PENDING → POSTED | Nhập/vốn/AP/chi |
| PURCHASE_RETURN | DRAFT → PENDING → POSTED | Xuất trả/điều chỉnh nợ |
| SALE_RETURN | DRAFT → PENDING → POSTED | Nhập/cách ly/giảm AR/tín dụng hoàn |
| REFUND | DRAFT → PENDING → POSTED | Tiền hoàn hợp lệ |
| TRANSFER | DRAFT → PENDING → APPROVED → TRANSIT → PARTIAL → COMPLETE | Xuất nguồn/nhận đích |
| COUNT | DRAFT → COUNTING → PENDING → POSTED | Khóa SKU/kho, chỉnh chênh |
| OTHER_RECEIPT/ISSUE | DRAFT → gửi/duyệt theo loại→POSTED | Nhập/xuất khác |
| OPENING_STOCK/AR/AP | DRAFT → PENDING → POSTED | Tồn/nợ đầu kỳ |

PENDING có thể REJECTED. DRAFT / COUNTING có CANCELLED theo quyền; không đảo POSTED. QUOTE/SALE_ORDER/PURCHASE_REQUEST/ORDER APPROVED/PARTIAL được CLOSED phần còn theo APPROVE_CANCEL, hiện ngay lập tức chưa request approval riêng. Action tăng version. Nguồn hợp lệ: QUOTE → SALE_ORDER; PURCHASE_REQUEST → PURCHASE_ORDER; SALE_ORDER→ISSUE; PURCHASE_ORDER→RECEIPT; POS đã POSTED/ISSUE→SALE_RETURN; phiếu nhập đã POSTED→PURCHASE_RETURN; trả bán/đơn đóng→REFUND theo điều kiện tiền.

## 5. Yêu cầu chức năng

Mỗi FR có đầu vào/xử lý/kết quả, lỗi và AT cần kiểm tra. Không coi AT là pass nếu chưa có evidence.

### FR-01 Đăng nhập và ngữ cảnh

Hành vi hiện tại: Email, mật khẩu và ngữ cảnh tenant của core. Cấu hình local cho mật khẩu 8–128 ký tự, không ép độ phức tạp/MFA/đổi lần đầu; vẫn hash và giới hạn thử sai. Account phải hoạt động; không nhận tenant tùy ý từ payload.

AT-01: Đăng nhập đúng/sai; tài khoản bị khóa; giới hạn thử sai; thay tenant trong payload không truy cập được công ty khác.

### FR-02 Wizard và mặc định

Hành vi hiện tại: Admin có setup VIEW/UPDATE và ALL_BRANCHES vào wizard khi chưa xong. ensure dưới khóa tenant tạo địa điểm/kho mặc định, lưu tiến độ 5 bước. Bước hoàn tất hiện chưa kiểm tra đầy đủ danh mục và phiếu đầu kỳ đã duyệt.

AT-02: Gọi lặp không tạo trùng mặc định; công ty/địa điểm trước kho; staff không bị buộc thiết lập; ghi nhận thiếu kiểm tra đầu kỳ là gap.

### FR-03 Địa điểm và kho

Hành vi hiện tại: List theo scope; create/update kiểm mã, liên hệ, manager và active. Chuyển mặc định trong transaction; deactivate kiểm mặc định/tồn/phiếu/transit/serial liên quan. Không đổi branchId của kho hiện có.

AT-03: Mã trùng hoặc manager khác tenant bị chặn; đổi mặc định đúng; ngừng kho có hàng bị chặn; một kho không hiện selector khi không cần.

### FR-04 SKU cấu hình và UOM

Hành vi hiện tại: Tạo/ngừng SKU; danh mục cấu hình và conversion. Update SKU đầy đủ và tổ hợp biến thể chưa có. Ghi conversion khóa SKU, trigger V29 đồng bộ dữ liệu. Snapshot trên phiếu cũ được giữ.

AT-04: 2 thùng × 24 = 48 cái; factor 0 hoặc quá 4 số lẻ bị chặn; đơn vị cơ sở luôn 1; barcode trùng bị chặn; thay quy đổi không đổi phiếu cũ.

### FR-05 Master đối tác

Hành vi hiện tại: Một ID có nhiều roles; menu lọc vai trò. Tạo nhanh khách cố định CUSTOMER; sửa kiểm version; bank có quyền riêng. NCC bắt buộc sáu trường chung.

AT-05: NCC thiếu trường không lưu; thêm role customer cho NCC không tạo hồ sơ thứ hai; sửa tên thấy ở bán/CRM; thiếu VIEW_BANK không lộ bank.

### FR-06 Định giá

Hành vi hiện tại: Backend xét partner/group/location/channel/qty/time; xét minQty/priority khi cùng tầng. Promotion không cộng dồn hoặc cộng dồn nối tiếp. Lưu rule snapshot; SALE_ORDER có giá policy không tự ghi đè thấp tùy ý.

AT-06: Khách cụ thể trước nhóm; ngoài hiệu lực không áp dụng; giảm 10% rồi 10% tương đương 19%; server kiểm giá client gửi.

### FR-07 Engine chứng từ

Hành vi hiện tại: Create DRAFT validate loại, nguồn, dòng, tracking, paid. Submit theo chính sách hoặc PENDING; approve/reject khác creator, trừ admin đủ điều kiện. Cancel/close theo trạng thái; cập nhật version/audit/idempotency. Close hiện trực tiếp, chưa có yêu cầu rồi duyệt riêng.

AT-07: Cùng key/body không tạo trùng; version cũ 409; staff tự duyệt 403, admin đặc biệt được; POSTED không hủy trực tiếp; đóng không đảo phần đã xuất.

### FR-08 POS

Hành vi hiện tại: Tìm SKU/barcode; khách, đơn vị, lượng, giá/VAT và thực thu. Lưu/xác nhận; thiếu tồn không ghi sổ. Chuyển đặt trước hủy nháp và chuyển toàn giỏ, chưa tách phần đủ/thiếu. Paid không vượt tổng; còn nợ cần khách.

AT-08: Tồn 10 bán 3 còn 7; gửi lặp không xuất hai lần; thiếu tồn không ghi tiền; chuyển đặt trước không trừ tồn; tiền thừa không làm paid vượt tổng.

### FR-09 Đơn và xuất bán

Hành vi hiện tại: QUOTE → SALE_ORDER → ISSUE. Nguồn giữ ratio/pricing; lượng còn = baseQty − progress − closedQty. Nhiều kho dùng nhiều ISSUE. Không tự giữ tồn; cọc chưa phân bổ tới khi AR phát sinh.

AT-09: Đơn 10 xuất 4 và 6 tại hai kho; xuất thêm 1 bị chặn; đơn chưa xuất không tạo AR; đóng còn lại không đảo đã xuất; cọc không phân bổ quá nợ.

### FR-10 Mua hàng

Hành vi hiện tại: Nhập trực tiếp hoặc đề nghị/đơn/nhận nhiều lần. Chờ duyệt chưa tăng tồn/AP; ghi sổ mới có. Sai lệch lượng/giá cần quyền xác nhận. Trả không vượt lượng đã nhận chưa trả. Form không phí vốn hóa/costPending/discount.

AT-10: Pending tồn chưa đổi; duyệt nhận 10 tăng tồn và AP; nhận 4 rồi 6 đúng tiến độ; sai lệch không quyền bị chặn; trả vượt bị chặn.

### FR-11 Tồn lô và serial

Hành vi hiện tại: Giá vốn bình quân theo kho/SKU; quarantine không vượt qty. FEFO lô còn hạn; serial duy nhất, đúng SKU/kho và AVAILABLE; qty nguyên bằng số serial. Available UI chưa trừ expired. COUNT_OUT/PURCHASE_RETURN/OTHER_ISSUE xử lý expired khác xuất bán.

AT-11: Lô sớm xuất trước; expired bị chặn POS/ISSUE; serial sai/đã bán bị chặn; lệnh cạnh tranh không âm; qty/value khớp ledger.

### FR-12 Chuyển kho

Hành vi hiện tại: Dispatch APPROVED giảm nguồn; receive TRANSIT/PARTIAL tăng đích theo thực nhận, không vượt còn lại. Hỏng vào cách ly; còn thiếu giữ transit. Chưa có bước tất toán hàng mất riêng.

AT-12: Chuyển 10: nguồn −10, đích chưa tăng; nhận 6, còn chuyển 4; nhận tiếp 5 bị chặn; lặp key không trùng; hỏng không khả dụng bán.

### FR-13 Kiểm kê

Hành vi hiện tại: COUNTING/PENDING khóa SKU trong warehouse. record-count dùng version; finish-count sang pending, approve ghi chênh. SKU có quarantine hiện không được kiểm kê theo luồng này.

AT-13: SKU đang đếm không nhập/xuất/bán tại kho đó; SKU khác và kho khác hoạt động; stale 409; sổ 10 đếm 8 tạo chuyển động −2.

### FR-14 Trả và hoàn

Hành vi hiện tại: Lookup số gốc POSTED, đúng dòng/serial; policy snapshot và lượng trả cộng dồn. Khác địa điểm nhận đúng kho. GOOD/OPENED theo chính sách/cách ly; USED/FAULTY trả WARRANTY_REQUIRED. Giảm AR trước phần credit hoàn; refund không vượt tiền hợp lệ, phương thức khác cần lý do. Exchange mới chỉ link.

AT-14: Bán 5 trả 3 rồi 3 bị chặn; serial sai bị chặn; khác chi nhánh giữ nguồn/kho đúng; nợ chưa thu không hoàn như tiền đã thu; hoàn lặp không vượt credit.

### FR-15 Giao và COD

Hành vi hiện tại: Tạo từ POS/ISSUE POSTED; carrier/tracking/COD/fee. Trạng thái theo service và version. Đối soát PARTIAL/DELIVERED với gross không vượt COD còn, fee không vượt gross; tạo thu gross/phân bổ AR và chi fee OFFSET. Chưa có dòng giao và giới hạn tổng COD gán nhiều đợt.

AT-15: Gross 100, fee 10 tạo thu 100/chi 10; vượt dư bị chặn; lặp không ghi tiền lại; ca nhiều đợt gán COD quá nợ là gap cần sửa.

### FR-16 Bảo hành

Hành vi hiện tại: SourceLine, kho, qty/serial, note và hạn snapshot. Serial đang mở bảo hành không nhận trùng. RECEIVED/PROCESSING/READY/HANDED_OVER với version; chưa chuyển tồn tự động, picker và cap lượng hàng không serial đầy đủ.

AT-16: Serial khác/hết hạn bị chặn; version cũ 409; serial đang mở bị chặn; kiểm tổng lượng không serial như gap.

### FR-17 CRM

Hành vi hiện tại: History partner theo scope/grant; care note tối đa 4000, branch, dueAt ISOInstant tùy chọn. Complete đánh dấu done; chưa có owner/result/completedAt đầy đủ.

AT-17: Cùng master ID; lịch sử đúng scope; thiếu COMPLETE trả 403; care không tạo tiền/đơn; không hứa SMS tự động.

### FR-18 Thu chi và nợ

Hành vi hiện tại: Payment branch/partner/document, kind RECEIPT/PAYMENT/COMMISSION/SUPPLIER_REFUND, amount nguyên dương, method tối đa 40, reference. RECEIPT→AR, PAYMENT→AP cùng partner/scope và không vượt tiền/nợ. Chưa đảo tiền và allocation hoàn NCC đầy đủ.

AT-18: Thu 100 phân bổ 60 còn 40; nợ 50 không phân bổ 60; sai đối tượng/phạm vi bị chặn; key lặp không tạo trùng.

### FR-19 Hoa hồng

Hành vi hiện tại: ORDER theo nguồn bán POSTED; MONTHLY_ADDITION chỉ bổ sung; ADJUSTMENT âm cần reason. Kỳ YYYY-MM-01, đối tượng COLLABORATOR; chi không vượt còn.

AT-19: 100 theo đơn + 20 bổ sung = 120; chi 130 bị chặn; điều chỉnh âm không lý do bị chặn.

### FR-20 Import

Hành vi hiện tại: SKUS/PARTNERS/OPENING_STOCK/OPENING_DEBT; file tối đa 2 MB/500 dòng, CSV UTF-8 hoặc XLSX sheet đầu không công thức. Preview/errors lưu batch; commit toàn bộ hoặc rollback. Master ghi trực tiếp, opening DRAFT. Validation preview chưa ngang commit.

AT-20: Preview lỗi không tạo tồn; commit lỗi rollback; commit đầu kỳ chưa ghi sổ; gửi/duyệt mới ảnh hưởng; công thức bị chặn.

### FR-21 Tổng quan báo cáo xuất và in

Hành vi hiện tại: Sáu tab theo quyền; report from/to tối đa 3 năm, nguồn bán/mua đã ghi sổ và movement. CSV BOM/escape/chống công thức. Finance/movements 1000, care 20 là hạn chế; print qua permission/trình duyệt.

AT-21: Nháp không tính doanh thu; posted đúng kỳ; cost ẩn; công thức thành text; kiểm >1000 như gap; export không mặc nhiên giống bộ lọc UI.

### FR-22 Phân quyền và core

Hành vi hiện tại: Role code/name/reason/grants/revision; bảo vệ role hệ thống/role đang dùng theo controller. User roles qua Core Access GET/POST cùng tenant, không tự đổi; warehouse scopes có revision. Quyền core account/org/resource/policy/audit/file/settings riêng.

AT-22: Checkbox lưu/đọc lại; user có role đúng; revision cũ 409; tự đổi bị chặn; khác tenant bị chặn; staff kho A không xem kho B; test admin không đủ chứng minh scope.

### FR-23 Module package và import

Hành vi hiện tại: Kiểm dependency trước toggle/import; disabled 403, absent 404 hoặc MODULE_NOT_INSTALLED tại proxy; core được bảo vệ. Pack source/export-core/import dry-run/checksum/path/version/custom conflict/build/restart. Gói không có dữ liệu.

AT-23: Tắt sales chặn menu/API cả admin; không tắt stock khi sales đang dùng; import sales lấy đủ deps; core trống không business; .env/custom không bị ghi đè.

## 6. API, lỗi và chống lặp

Base /api/v 1/retail, auth và scope. Phụ lục sinh từ controller snapshot. GET documents type/status/q/page, page 0,50 dòng; partners thêm role/state/group. Create/detail/transition trả theo service, không giả định một vỏ response data chung. Không PATCH/PUT cập nhật nháp update tổng quát.

Command khai báo Idempotency-Key yêu cầu 8–120 ký tự [A-Za-z 0-9._:-], khóa theo tenant/actor/key/operation, hashpayload. Timeout retry cùng key/body, kiểm tra phiếu trước key mới. Endpointprice/role revision không mặc nhiên có header này. Hash theo JSON biểu diễn, chưa cam kết đổi thứ tự trường JSON tương đương.

| Tình huống | HTTP / hợp đồng | Frontend |
|---|---|---|
| Chưa login/hết phiên | Auth core 401/redirect | Login, không gửi lặp |
| Thiếu quyền/scope/module tắt | 403 | Hiện lỗi, không giả list rỗng |
| ID/route không có | 404 | Kiểm tra ID/module/phạm vi |
| Đầu vào / số lượng / đơn vị / theo dõi sai | 400 ApiProblem | Hiện detail, sửa đầu vào |
| Version/revision/key và payload khác | 409 | Reload, không ghi đè hoặc tạo key khác để né lỗi |
| Tồn thiếu | Lỗi service | Không post; bổ sung/đặt trước |
| Module service thiếu | MODULE_NOT_INSTALLED | Kiểm tra dependency/config |
| Mất kết nối / 5xx | Kết quả có thể chưa rõ | Tra phiếu, retry cùng key |

Giữ các mã VERSION_CONFLICT, PERMISSION_DENIED, INVALID_UOM, BASE_UOM, INVALID_DOCUMENT_TYPE, ROLE_READ_ONLY, SELF_PERMISSION_CHANGE, WARRANTY_REQUIRED theo source. Parser code/detail/status của ApiProblem/core, không chỉ chuỗi tiếng Việt.

## 7. Transaction và bảo mật

RetailCommands/idempotency/advisory khóa tenant serialize nghiệp vụ; khóa bản ghi tồn/nguồn/tiền/revision trước ghi. Post/ledger trong transaction, lỗi rollback. Vẫn cần test concurrency, không lấy việc có lock làm evidence đã kiểm tra đạt. Không sửa balance/payment trực tiếp SQL trong luồng người dùng.

API kiểm quyền + scope + module; Ẩn menu không thay thế bảo mật API. VIEW_COST / VIEW_PROFIT, VIEW_BANK / UPDATE_BANK kiểm soát field, segments/cost/bank. Secret env không Git/log/gói. MFA local tắt không áp đặt mọi môi trường. Input đăng nhập 16px/mobile safe-area, chỉ hoạt động online.

## 8. Phi chức năng và nghiệm thu

UI nên hiện khung loading trước dữ liệu và lỗi rõ. Hiện còn tổng hợp nhiều trang ở browser, nằm trong backlog. Chưa chốt SLO/năng lực tải và chưa kiểm thử tải. Mẫu GET 19–40 ms trên dữ liệu nhỏ không chứng minh mọi quy mô; các lần mở UI có chờ nhân tạo không phải phép đo độ trễ thực.

Backup trước nâng cấp, Flyway áp dụng migration theo chiều tiến. Không xóa volume hoặc cài lại DB để đồng bộ code. Build thất bại giữ source/backup; lỗi startup/migration không tự rollback DB. Import gói không chuyển data và không hot upload JAR.

Audit 09/10 ghi 25 API HTTP 200, 27 màn hình trên desktop/mobile, tổng 54 lần mở không có pageerror/alert/overflow trong mẫu admin. Chưa kiểm toàn bộ workflow, staff scope, multi-tenant, cạnh tranh hoặc dữ liệu lớn. Selected frontend tests có 3 ca: 1 đạt, 2 lỗi do giả định source cũ. Backend CI package với -DskipTests, chưa có integration gate. Kết quả ngày 08/10 trong BUSINESS_MODULES_PHASE3_PROGRESS.md là bằng chứng lịch sử, chưa chạy lại trong lần viết này.

Trước release: chạy AT phần đổi và regression quyền/scope, chống lặp/cạnh tranh, migration DB cũ, core trống/import chọn module. Lưu evidence/commit/reviewer; không đánh dấu đạt khi chưa chạy.

## 9. Truy vết và task

BR-01/03→FR-01/22; BR-02/08/09→FR-07/09/10/14; BR-05/06→FR-08/11/12/13; BR-07→mục 3.5; BR-10→FR-14/18. Runbook theo cùng module/thuật ngữ. File task ngày 09/10 là nguồn phân công 3 intern; DOC-01 cập nhật tài liệu, các task triển khai giữ trạng thái thực.

| Nhóm | Hiện có | Backlog tiếp tục |
|---|---|---|
| Product | SKU/UOM/conversion/config | Parent/variant, linkgroup/brand, update |
| Documents | Create/submit/duyệt/source/post | Draftedit, cancelrequest, reservation |
| Stock | Ledger/FEFO/serial/transit/count | Quarantine/mất, nhận tốt/hỏng |
| Master/CRM | Sharedroles/history/care | Owner/result/complete time, paging |
| Finance | Payment/allocation/refund/commission | Reverse/exchangecredit/NCCrefund |
| Delivery/warranty | Manualstatus/COD/warranty | Lines/CODsum/giới hạn hàng không serial |
| Report/import | Endpoint/mẫu | Totals/paging/export/parity |
| Core/modules | Sourceimport/chặn API/roles | CI/security/migrate/regression |

## Phụ lục A Danh mục API nghiệp vụ

Sinh từ annotation Controller của snapshot, không phải kết quả gọi API. Action tùy đường dẫn được service giới hạn.

| Method | Route | Source |
|---|---|---|
| GET | /api/v1/retail/commissions | AccountingController.java |
| POST | /api/v1/retail/commissions | AccountingController.java |
| GET | /api/v1/retail/finance | AccountingController.java |
| POST | /api/v1/retail/payments | AccountingController.java |
| POST | /api/v1/retail/allocations | AccountingController.java |
| GET | /api/v1/retail/customers/{id}/history | CrmController.java |
| POST | /api/v1/retail/care | CrmController.java |
| POST | /api/v1/retail/care/{id}/complete | CrmController.java |
| GET | /api/v1/retail/stock | InventoryController.java |
| GET | /api/v1/retail/movements | InventoryController.java |
| GET | /api/v1/retail/tracking | InventoryController.java |
| GET | /api/v1/retail/inventory/documents | StockDocuments.java |
| GET | /api/v1/retail/inventory/documents/{id} | StockDocuments.java |
| POST | /api/v1/retail/inventory/documents | StockDocuments.java |
| POST | /api/v1/retail/inventory/documents/{id}/{action} | StockDocuments.java |
| GET | /api/v1/retail/locations/{kind} | OperationsController.java |
| POST | /api/v1/retail/locations/{kind} | OperationsController.java |
| POST | /api/v1/retail/locations/{kind}/{id} | OperationsController.java |
| GET | /api/v1/retail/onboarding | OperationsController.java |
| GET | /api/v1/retail/location-owners | OperationsController.java |
| GET | /api/v1/retail/locations/{kind}/export | OperationsController.java |
| POST | /api/v1/retail/onboarding | OperationsController.java |
| POST | /api/v1/retail/imports | OperationsController.java |
| POST | /api/v1/retail/imports/file | OperationsController.java |
| POST | /api/v1/retail/imports/{id}/commit | OperationsController.java |
| GET | /api/v1/retail/imports | OperationsController.java |
| GET | /api/v1/retail/scopes | OperationsController.java |
| GET | /api/v1/retail/summary | OverviewController.java |
| GET | /api/v1/retail/partner-owners | PartnersController.java |
| GET | /api/v1/retail/partners | PartnersController.java |
| GET | /api/v1/retail/partners/{id} | PartnersController.java |
| POST | /api/v1/retail/partners | PartnersController.java |
| POST | /api/v1/retail/customers/quick-create | PartnersController.java |
| POST | /api/v1/retail/partners/{id} | PartnersController.java |
| GET | /api/v1/retail/uom-conversions | ProductsController.java |
| POST | /api/v1/retail/uom-conversions | ProductsController.java |
| GET | /api/v1/retail/warehouse-config | ProductsController.java |
| POST | /api/v1/retail/warehouse-config | ProductsController.java |
| GET | /api/v1/retail/purchase/documents | PurchaseDocuments.java |
| GET | /api/v1/retail/purchase/documents/{id} | PurchaseDocuments.java |
| POST | /api/v1/retail/purchase/documents | PurchaseDocuments.java |
| POST | /api/v1/retail/purchase/documents/{id}/{action} | PurchaseDocuments.java |
| GET | /api/v1/retail/reports | ReportsController.java |
| POST | /api/v1/retail/price | SalesController.java |
| GET | /api/v1/retail/deliveries | SalesController.java |
| POST | /api/v1/retail/deliveries | SalesController.java |
| POST | /api/v1/retail/deliveries/{id}/{action} | SalesController.java |
| GET | /api/v1/retail/original-sale | SalesController.java |
| GET | /api/v1/retail/warranties | SalesController.java |
| POST | /api/v1/retail/warranties | SalesController.java |
| POST | /api/v1/retail/warranties/{id}/status | SalesController.java |
| GET | /api/v1/retail/sales/documents | SalesDocuments.java |
| GET | /api/v1/retail/sales/documents/{id} | SalesDocuments.java |
| POST | /api/v1/retail/sales/documents | SalesDocuments.java |
| POST | /api/v1/retail/sales/documents/{id}/{action} | SalesDocuments.java |
| GET | /api/v1/retail/catalog | RetailController.java |
| POST | /api/v1/retail/catalog/{kind} | RetailController.java |
| POST | /api/v1/retail/catalog/{kind}/{id}/deactivate | RetailController.java |
| GET | /api/v1/retail/documents | RetailController.java |
| GET | /api/v1/retail/documents/{id}/print | RetailController.java |
| GET | /api/v1/retail/documents/{id} | RetailController.java |
| POST | /api/v1/retail/documents | RetailController.java |
| POST | /api/v1/retail/documents/{id}/{action} | RetailController.java |
| GET | /api/v1/retail/export | RetailController.java |
| GET | /api/v1/retail/permissions/capabilities | RetailPermissionController.java |
| GET | /api/v1/retail/permissions | RetailPermissionController.java |
| POST | /api/v1/retail/permissions/roles | RetailPermissionController.java |
| POST | /api/v1/retail/permissions/template | RetailPermissionController.java |
| GET | /api/v1/retail/permissions/roles/{id} | RetailPermissionController.java |
| GET | /api/v1/retail/permissions/users/{id}/preview | RetailPermissionController.java |
| POST | /api/v1/retail/permissions/users/{id}/roles | RetailPermissionController.java |
| POST | /api/v1/retail/permissions/users/{id}/scopes | RetailPermissionController.java |
| POST | /api/v1/retail/permissions/roles/{id} | RetailPermissionController.java |

## Phụ lục B Danh mục quyền

Nguồn RetailPermissions.FEATURES, 49 feature. Đây là capability catalog; không xác nhận mọi action đã có đủ UI/API.

| Feature | Nhóm và tên | Actions |
|---|---|---|
| locations | Vận hành chung: Địa điểm / Chi nhánh | VIEW, CREATE, UPDATE, DEACTIVATE, EXPORT |
| warehouses | Kho: Danh sách kho | VIEW, CREATE, UPDATE, DEACTIVATE, EXPORT |
| pos | Bán hàng: Bán tại quầy | VIEW, CREATE, SUBMIT, COLLECT_POS, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| orders | Bán hàng: Báo giá / Đơn bán | VIEW, CREATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| issue | Kho: Xuất bán | VIEW, CREATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| delivery | Bán hàng: Giao hàng | VIEW, CREATE, UPDATE, SETTLE, EXPORT |
| purchase-order | Mua hàng: Đề nghị / Đơn mua | VIEW, CREATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| purchase-receipt | Mua hàng: Nhập mua | VIEW, CREATE, SUBMIT, APPROVE, REJECT, CONFIRM_DISCREPANCY, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| purchase-return | Mua hàng: Trả mua | VIEW, CREATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| stock | Kho: Kho & Tồn / Nhập xuất khác | VIEW, CREATE, SUBMIT, APPROVE, REJECT, RECEIVE, DISPATCH, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| count | Kho: Kiểm kê | VIEW, CREATE, UPDATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| transfer | Kho: Chuyển kho | VIEW, CREATE, SUBMIT, APPROVE, REJECT, DISPATCH, RECEIVE, CONFIRM_DISCREPANCY, REQUEST_CANCEL, APPROVE_CANCEL, PRINT, EXPORT |
| warehouse-config | Kho: Cấu hình kho | VIEW, UPDATE |
| products | Sản phẩm: Danh sách sản phẩm | VIEW, CREATE, UPDATE, DEACTIVATE, VIEW_PRICE, UPDATE_PRICE, IMPORT, EXPORT |
| product-groups | Sản phẩm: Nhóm sản phẩm | VIEW, CREATE, UPDATE, DEACTIVATE |
| product-brands | Sản phẩm: Thương hiệu | VIEW, CREATE, UPDATE, DEACTIVATE |
| product-units | Sản phẩm: Đơn vị tính | VIEW, CREATE, UPDATE, DEACTIVATE |
| uom-conversions | Sản phẩm: Quy đổi đơn vị tính | VIEW, CREATE, UPDATE, DEACTIVATE |
| product-attributes | Sản phẩm: Thuộc tính / Biến thể | VIEW, CREATE, UPDATE, DEACTIVATE |
| customer | Khách hàng / Đối tác: Khách hàng dùng chung | VIEW, CREATE, UPDATE, DEACTIVATE, IMPORT, EXPORT |
| supplier | Mua hàng: Nhà cung cấp dùng chung | VIEW, CREATE, UPDATE, DEACTIVATE, IMPORT, EXPORT |
| partners | Khách hàng / Đối tác: Đối tác / Vai trò | VIEW, CREATE, UPDATE, MANAGE_ROLES, DEACTIVATE, VIEW_BANK, UPDATE_BANK, IMPORT, EXPORT |
| crm | CRM: Chăm sóc khách hàng | VIEW, CREATE, UPDATE, COMPLETE, VIEW_HISTORY, EXPORT |
| receipt | Kế toán: Phiếu thu | VIEW, CREATE, ALLOCATE, PRINT, EXPORT |
| payment | Kế toán: Phiếu chi | VIEW, CREATE, ALLOCATE, PRINT, EXPORT |
| receivables | Kế toán: Phải thu | VIEW, ALLOCATE, EXPORT |
| payables | Kế toán: Phải trả | VIEW, ALLOCATE, EXPORT |
| opening-stock | Vận hành chung: Tồn đầu kỳ | VIEW, CREATE, SUBMIT, APPROVE, REJECT, IMPORT, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| opening-ar | Kế toán: Phải thu đầu kỳ | VIEW, CREATE, SUBMIT, APPROVE, REJECT, IMPORT, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| opening-ap | Kế toán: Phải trả đầu kỳ | VIEW, CREATE, SUBMIT, APPROVE, REJECT, IMPORT, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| commission | Kế toán: Hoa hồng | VIEW, CREATE, EXPORT |
| returns | Vận hành chung: Trả hàng | VIEW, CREATE, SUBMIT, APPROVE, REJECT, RECEIVE, LOOKUP_ORIGINAL, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| warranty | Vận hành chung: Bảo hành | VIEW, CREATE, UPDATE, LOOKUP_ORIGINAL, EXPORT |
| refund | Kế toán: Hoàn tiền | VIEW, CREATE, SUBMIT, APPROVE, REJECT, REQUEST_CANCEL, APPROVE_CANCEL, EXPORT |
| setup | Vận hành chung: Thiết lập / Chi nhánh kho | VIEW, CREATE, UPDATE, DEACTIVATE |
| scope | Quản trị hệ thống: Phạm vi chi nhánh / kho | VIEW, MANAGE, ALL_BRANCHES |
| sensitive | Kế toán: Giá vốn / Lợi nhuận | VIEW, VIEW_COST, VIEW_PROFIT |
| overview-sales | Tổng quan: Bán hàng | VIEW |
| overview-purchase | Tổng quan: Mua hàng | VIEW |
| overview-stock | Tổng quan: Kho | VIEW |
| overview-products | Tổng quan: Sản phẩm | VIEW |
| overview-crm | Tổng quan: CRM | VIEW |
| overview-finance | Tổng quan: Kế toán | VIEW |
| report-sales | Báo cáo: Bán hàng | VIEW, EXPORT |
| report-purchase | Báo cáo: Mua hàng | VIEW, EXPORT |
| report-stock | Báo cáo: Kho | VIEW, EXPORT |
| report-products | Báo cáo: Sản phẩm | VIEW, EXPORT |
| report-crm | Báo cáo: CRM | VIEW, EXPORT |
| report-finance | Báo cáo: Kế toán | VIEW, EXPORT |

## Phụ lục C Gói module và phụ thuộc

| Module | Version | Dependencies |
|---|---|---|
| aicrm-retail | 1.2.0 | Không |
| aicrm-products | 1.0.0 | aicrm-retail |
| aicrm-partners | 1.0.0 | aicrm-products, aicrm-retail |
| aicrm-operations | 1.0.0 | aicrm-products, aicrm-retail |
| aicrm-accounting | 1.0.0 | aicrm-operations, aicrm-retail, aicrm-partners |
| aicrm-stock | 1.0.0 | aicrm-retail, aicrm-products, aicrm-operations, aicrm-accounting |
| aicrm-sales | 1.0.0 | aicrm-retail, aicrm-stock, aicrm-partners, aicrm-accounting |
| aicrm-purchase | 1.0.0 | aicrm-retail, aicrm-stock, aicrm-partners, aicrm-accounting |
| aicrm-crm | 1.0.0 | aicrm-operations, aicrm-products, aicrm-retail, aicrm-partners |
| aicrm-overview | 1.0.0 | aicrm-retail, aicrm-accounting, aicrm-products |
| aicrm-reports | 1.0.0 | aicrm-operations, aicrm-products, aicrm-retail |

## Phụ lục D Migration nghiệp vụ

Đọc file SQL để biết kiểu cột, default và FK đầy đủ. Chưa kiểm tra lại information_schema DB chạy. Không đưa SQL seed/secret vào tài liệu.

- V24__aicrm_retail.sql: tạo retail.branch, retail.warehouse, retail.access_scope, retail.sku, retail.partner, retail.price_rule, retail.promotion, retail.setting, retail.document, retail.line, retail.balance, retail.lot, retail.serial, retail.movement, retail.open_item, retail.payment, retail.allocation, retail.delivery, retail.care, retail.warranty, retail.command, retail.import_batch; điều chỉnh retail.line, retail.serial, retail..
- V25__aicrm_cod_actual_fee.sql: tạo —; điều chỉnh retail.delivery.
- V26__aicrm_commission_ledger.sql: tạo retail.commission; điều chỉnh retail.payment, retail.commission.
- V27__warehouse_configuration.sql: tạo retail.warehouse_config; điều chỉnh retail.warehouse_config.
- V28__product_groups.sql: tạo —; điều chỉnh retail.warehouse_config.
- V29__product_uom_conversion.sql: tạo retail.uom, retail.uom_conversion; điều chỉnh retail.sku, retail..
- V30__shared_partner_master.sql: tạo —; điều chỉnh retail.partner.
- V31__supplier_company_profile.sql: tạo —; điều chỉnh retail.partner.
- V32__module_permission_scope_grants.sql: tạo —; điều chỉnh —.
- V33__locations_warehouses_onboarding.sql: tạo retail.company_profile; điều chỉnh retail.branch, retail.warehouse, retail.company_profile.

## Phụ lục E Trường dữ liệu các nghiệp vụ phụ

| Chức năng | Đầu vào | Điều kiện và kết quả |
|---|---|---|
| Thu / chi | branchId, partnerId/documentId tùy chọn; kind; amount; method; reference; allocations tùy chọn | Amount nguyên dương; phiếu gắn phải cùng đối tượng/địa điểm; phân bổ không vượt tiền/nợ |
| Phân bổ | paymentId, openItemId, amount | Cùng tenant/partner; RECEIPT→AR, PAYMENT→AP; số dương không vượt hai phần dư |
| Hoa hồng | branchId, partnerId, kind, documentId, period, amount, reason | COLLABORATOR; ngày đầu tháng; ORDER nguồn hợp lệ; ADJUSTMENT âm cần lý do |
| Chăm sóc | partnerId, branchId, note, dueAt | Note tối đa 4000, dueAt ISOInstant hoặc không có; complete chỉ done |
| Đợt giao | issueId, carrier, tracking, cod, fee, note | Phiếu bán POSTED; COD không âm, không vượt nợ theo từng yêu cầu; fee hợp lệ |
| Cập nhật giao | version, status, note | Trạng thái hợp lệ theo service, không ghi đè version cũ |
| Đối soát | version, amount, fee, method | Amount dương ≤COD còn; fee≤amount; trạng thái giao hợp lệ; thu gross và chi fee |
| Bảo hành | sourceLineId, warehouseId, qty, serial, note | Gốc bán POSTED, hạn theo snapshot; serial đúng; còn hạn; không chuyển tồn tự động |
| Cập nhật bảo hành | version, status, note | RECEIVED/PROCESSING/READY/HANDED_OVER theo service |
| Nhận chuyển | version, lines gồm lineId/qty/serials/damaged, reason | Lượng thực nhận ≤transit còn; damaged vào cách ly |
| Thực đếm | version, dữ liệu dòng/count và reason theo action | Đơn vị cơ sở, SKU không trùng; giữ khóa trong COUNTING/PENDING |

## Phụ lục F Chức năng core và hợp đồng quản trị

### FR-24 Người dùng tổ chức và hồ sơ

Core Access tạo user với email hợp lệ, displayName bắt buộc tối đa160, password 8–128, roleIds ít nhất một, orgId tùy chọn. Reset password 8–128 theo quyền; không trả hash. Tổ chức có code dạng chữ thường/số/gạch ngang bắt đầu chữ dài2..80, name tối đa 160 và parentCode tùy chọn. Đây là cây tổ chức core, không thay branch/kho nghiệp vụ.

Core UserRoleController GET/POST /api/v1/access/users/{id}/roles dùng roleIds, revision, reason; tenant và UUID hợp lệ, không tự đổi role chính mình; ghi audit/tăng revision. Mẫu request xác định theo controller, không dùng endpoint role summary của control-plane làm gán quyền thật.

AT-24: create user thiếu role từ chối; reset không lộ password/hash trong log; org parent hợp lệ; tài khoản disabled bị chặn; đổi role dùng revision mới; không truy cập cross-tenant.

### FR-25 Trang chủ nhật ký và hoạt động

Control-plane bootstrap cung cấp summary/modules/resources/activities/roles/files/audit/settings theo quyền quản trị deployment. Trang chủ core không hiển thị dashboard bán hàng thay thế. Nhật ký có actorEmail/action/resourceType/resourceId/result/correlationId/occurredAt, mặc định 50, tối đa 200; không coi là xuất toàn bộ audit.

Jobs có status và thao tác retry/cancel theo trạng thái/quyền; outbox có eventType/tenantKey/status/attempts/availableAt/lastError và replay. Các thao tác có thể thực thi lại xử lý, chỉ quản trị dùng sau kiểm tra nguyên nhân. Không hứa retry sẽ đảo tác động bên ngoài đã thực hiện.

AT-25: non-admin bị chặn control-plane; Trang chủ khác Tổng quan; retry/replay sai trạng thái bị chặn; audit lưu người và kết quả, không secret. Cần kiểm tra phạm vi deployment trước cung cấp SaaS, không mặc nhiên coi control-plane đã tenant-isolated như nghiệp vụ.

### FR-26 Tài nguyên tệp cài đặt và webhook

ResourceCreate: name tối đa 160, storageMode DOMAIN/DYNAMIC, ownerModule tối đa 80 đã đăng ký, schemaVersion tối đa 20. Dynamic resource/record có schema / fields/classification; update record dùng If-Match version và quyền của resource. Schema quản trị không thay SKU hoặc chứng từ đã ghi sổ.

Tệp: name/mediaType/classification INTERNAL/CONFIDENTIAL/RESTRICTED, resourceType/resourceId tùy chọn. Upload session STAGING→SCANNING→ACTIVE khi scan sạch; nhiễm chuyển QUARANTINED. List page 0/size 25 tối đa100, owner scope theo FILE READ. Download/delete theo quyền và trạng thái. Logo hệ thống có luồng riêng, không mọi tệp đều là logo.

Settings key bắt buộc, tối đa 100, value bắt buộc, tối đa 500, chỉ quản trị thay; cấu hình MFA riêng theo core. Webhook gồm URL/eventTypes, tạo/vô hiệu hóa theo WEBHOOK quyền; không đồng nghĩa có API hãng vận chuyển. Cần kiểm tra SSRF/secret/delivery theo service trước thêm nhà cung cấp thực.

AT-26: resource owner chưa đăng ký bị chặn; update If-Match cũ409; tệp chưa sạch không ACTIVE; owner-only không đọc file người khác; settings/webhook thiếu quyền 403.
