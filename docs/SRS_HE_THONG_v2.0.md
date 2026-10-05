# SRS AICRM 2.0 — đặc tả triển khai bản chuẩn

Ngày: 05/10/2026. Trạng thái: dự thảo triển khai theo BA 2.0 và các quyết định đã xác nhận. Không phải biên bản nghiệm thu hoặc lệnh triển khai toàn bộ code.

Căn cứ: [BA 2.0](./BA_HE_THONG_v2.0.md), [Từ điển trường dữ liệu](./BA_TRUONG_DU_LIEU_v2.0.md). Danh sách công việc giao thành viên: [Backlog task 2.0](./TASK_TRIEN_KHAI_v2.0.md). Các mã `FR`, `NFR`, `AC`, `T` dùng để truy vết yêu cầu → công việc → kiểm thử. Tài liệu SRS 1.0 được giữ làm lịch sử; quy tắc trái với quyết định mới phải cập nhật theo bản này.

Bảng giao việc cho đội 3 intern backend/frontend: [Phân công 80 task nhỏ](./TASK_INTERN_v2.0.md). Bảng này phân rã công việc, không thay các yêu cầu, quyền và điều kiện nghiệm thu SRS.

## 1. Mục tiêu, giới hạn và nền tảng

Hoàn thiện hệ thống SME thương mại dùng hàng ngày: thiết lập → danh mục/đầu kỳ → nhập → bán → thu/trả → chuyển/kiểm kê → đối chiếu. Kế thừa Java Core, menu và API đang có; chỉ bổ sung hoặc sửa phần thiếu, không dựng lại nền tảng hay dùng dữ liệu giả cho luồng chính.

Một công ty có nhiều điểm bán/kho; sản phẩm/đối tác dùng chung, giao dịch đúng phạm vi. Bản chuẩn chạy online và local trước; nền dữ liệu phải cô lập theo tenant để có thể nâng SaaS. Chưa làm quản lý ca, offline, carrier API/sàn, kế toán pháp định/hóa đơn điện tử, phí mua đến muộn, SaaS thuê bao/tự tách source/database. Không commit/push/deploy trong nhiệm vụ lập tài liệu này.

### 1.1. Quyết định bắt buộc

| DEC | Quy tắc |
|---|---|
| DEC-01 | Đăng nhập local: email/mật khẩu ≥8 ký tự, MFA tắt, không ép đổi lần đầu; giữ đổi mật khẩu tự nguyện |
| DEC-02 | Lần đầu có wizard lưu tiến độ; nhân viên chưa có kho được thông báo, không giao dịch tùy ý |
| DEC-03 | Hàng có biến thể chọn thuộc tính/giá trị cấu hình sẵn; mỗi tổ hợp một SKU, cha không có tồn/giao dịch; không nhập màu/size tự do |
| DEC-04 | Kiểm kê chỉ khóa SKU đang đếm tại kho, kể cả chờ duyệt; các SKU/kho khác hoạt động |
| DEC-05 | Bán công nợ trong quyền bán không cần duyệt riêng, không áp hạn mức/chặn quá hạn; còn nợ phải chọn khách |
| DEC-06 | Bán thường theo chính sách xác nhận ngay; giảm ngoài mức/bán dưới vốn cần người khác duyệt |
| DEC-07 | Mua, chuyển, điều chỉnh/đầu kỳ, trả và hoàn có người khác duyệt; người tạo không tự duyệt |
| DEC-08 | Không âm; FEFO; hạn trước ngày hiện tại chặn bán, hạn bằng hôm nay còn dùng theo quy tắc hiện tại; không bán cách ly |
| DEC-09 | VND nguyên đồng, lượng/đơn giá tối đa 4 số lẻ, HALF_UP; giờ Việt Nam; snapshot giữ dữ liệu lịch sử |
| DEC-10 | CTV tính thủ công theo phiếu + khoản bổ sung tháng + điều chỉnh; không ghi lại tổng tháng để tính trùng |

### 1.2. Quyết định đang mở và cách quản lý task

| OPEN | Nội dung | Được làm ngay | Chưa được chốt triển khai |
|---|---|---|---|
| OPEN-01 / BA Q-03 | Trả hàng đã sử dụng | Thiết kế chính sách/tình trạng và ca kiểm; giữ quy tắc đang chạy | Cho USED trả hay luôn bảo hành; mặc định/cấp duyệt |
| OPEN-02 / BA Q-05 | Bên chịu/nhận phí mua–giao, hoàn phí | Cấu trúc chi phí tách tiền hàng/phí, màn đối chiếu | Tự động tăng nợ cho bên nào, hoàn phí khi trả |
| OPEN-03 / BA Q-06 | Ngày bắt đầu, hạn thanh toán/ngày hiệu lực | Mô hình ngày, hợp đồng API và mockup | Quyền ghi lùi ngày, ngày khóa, thay mặc định 30 ngày |
| OPEN-04 / BA Q-04 | Cửa hàng chỉ một người | Checklist yêu cầu người duyệt thứ hai | Không được tự bật chế độ tự duyệt |

Task đánh dấu `Conditional` được phân tích nhưng phần phụ thuộc quyết định không chuyển `Ready` trước khi PO chốt. Các phần độc lập vẫn triển khai. Không coi một câu hỏi chưa trả lời là cho phép thay đổi chính sách.

## 2. Quy ước và ranh giới module

`FR`: chức năng; `NFR`: phi chức năng; `AC`: nghiệm thu; `T`: task. `Kế thừa + hoàn thiện` nghĩa có source, phải kiểm khoảng thiếu trước sửa; không nghĩa đã hoàn thành. Owner trong backlog là vai trò, người cụ thể phân sau. P1 = trước nghiệm thu luồng liên quan; P2 = sau luồng tiền/tồn ổn định nhưng vẫn nằm backlog R1 bổ sung; R1.1 = ngoài bản chuẩn.

Sản phẩm quản lý master; Mua quản lý giao dịch mua/NCC; Kho quản lý movement/tồn. Bán không tạo sản phẩm tại quầy. Thu chi khác công nợ. Menu Tổng quan/Báo cáo độc lập. Cấu hình kho không chứa danh mục UOM/brand/thuộc tính. Phạm vi Core tổ chức không đồng nhất chi nhánh bán; không gọi đơn vị tổ chức là khách SaaS.

## 3. Mô hình dữ liệu cần nâng cấp

### 3.1. Dữ liệu gốc

| Đối tượng | Trường/quan hệ tối thiểu | Ràng buộc |
|---|---|---|
| BusinessProfile | tenant, tên, liên hệ, địa chỉ, logoFileId, currency, timezone, openingDate, version | Một hồ sơ/tenant; logo có quyền tệp, không trộn environment.name |
| Onboarding | tenant, step, progress, completedAt/by, confirmations, version | Resume sau đăng xuất; hoàn thành khi điều kiện đủ |
| Product | id, tenant, code/name, hasVariants, groupId, brandId, active, version, defaults | Cha gom SKU, không chứa số dư; không biến thể có đúng một SKU hoạt động |
| SKU hiện có | giữ id/code/name/barcode, thêm productId, variantKey, version, group/brand qua Product | Unique tenant+code, barcode theo quy tắc hiện có; phiếu cũ giữ SKU ID |
| Attribute | id, tenant, code/name, useForVariant, active, sortOrder, version | Mã duy nhất/tenant; thuộc tính biến thể dùng tập giá trị |
| AttributeValue | id, tenant, attributeId, code/name, active, sortOrder, version | Unique tenant+attribute+code; không xóa giá trị đã dùng |
| SkuAttributeValue | tenant, skuId, attributeId, valueId | Một giá trị/thuộc tính/SKU; value phải thuộc attribute |
| ProductVariantAttribute | tenant, productId, attributeId | Bộ thuộc tính bắt buộc để định nghĩa tổ hợp của cha |
| Group/Brand | id, tenant, code/name, active, version | Chuẩn hóa catalog hiện có; không tạo bản sao khác ID nếu đang có tham chiếu |
| UOM/Conversion | Giữ UOM hiện có và conversion SKU–UOM–factor | Factor >0 ≤4 số lẻ; cơ sở ngầm 1; một cặp một hệ số |
| Partner | giữ hiện có; thêm liên hệ/MST tùy chọn, terms khi chốt | Không tự đổi một loại đối tác thành nhiều vai trò |
| Scope/RoleTemplate | tenant, account, quyền, kho/phạm vi; cấu hình mẫu vai trò | Tenant FK, quyền thao tác riêng phạm vi; thu hồi hiệu lực ngay |

`variantKey` chuẩn hóa bằng ID thuộc tính/giá trị sắp theo ID thuộc tính, không dùng tên hiển thị: đổi “Đỏ” thành “Đỏ đậm” không tạo SKU thứ hai. Unique tenant+product+variantKey, kiểm cùng transaction khi hai người tạo cùng tổ hợp. Chỉ tạo các tổ hợp người dùng chọn. Thay bộ thuộc tính của sản phẩm đã có giao dịch phải giữ SKU cũ và thêm phiên bản/tổ hợp mới; không gán lại SKU đã bán sang màu/size khác.

**Migration hiện trạng:** tạo một Product không biến thể cho mỗi SKU cũ, giữ ID SKU, tồn, UOM, serial/lô và tham chiếu dòng phiếu. Mô tả biến thể cũ giữ trong dữ liệu lịch sử; không tự đoán màu/size thành thuộc tính. Công cụ gom SKU cũ về cha mới cần preview, mapping người dùng xác nhận, không gộp sổ tồn hay sửa snapshot phiếu. Backfill tenant đầy đủ, kiểm số lượng/tồn/nợ trước–sau; không seed demo.

### 3.2. Giao dịch và sổ

| Đối tượng | Bổ sung/chuẩn hóa | Bất biến |
|---|---|---|
| Document/Line | businessDate, version, số phiếu qua counter, snapshot SKU/biến thể; draft update | Đã ghi không sửa hàng/giá/nguồn; sourceLine đúng SKU |
| CommandRequest | tenant, actor, operation, idempotencyKey, requestHash, result, status | Cùng khóa/payload trả cùng kết quả; khác payload báo conflict |
| ApprovalRequest | target/action, creator, reason, expectedVersion, payloadHash, decision/history | Duyệt không hiệu lực khi dữ liệu đổi; khác creator |
| OrderProgress | ordered/fulfilled/closed/backordered; delivery/payment tách riêng | Không tự giảm tồn khi đặt; lượng đã xuất không bị đóng/xóa |
| DeliveryLine/Settlement | issueLine, qty, serial/lot refs; bảng kê, phí, COD, receipt refs | Tổng giao không vượt xuất; COD đa đợt không vượt nợ hợp lệ |
| PurchaseCost | receipt, amount, allocationBasis, payer/payee, includedInSupplierDebt, allocated lines | Tổng vốn hóa đúng tổng phí; nghĩa vụ trả riêng tiền hàng |
| SupplierRefundClaim | purchaseReturn, partner, branch, amount, settled, receipt links | Không coi thu rời là tất toán claim nếu chưa phân bổ |
| TransferVariance | sourceTransfer/line, thiếu/hỏng/mất, qty/value, reason, approval | Nhận + xử lý mất = đã gửi; không xóa hàng đang chuyển |
| QuarantineDisposition | warehouse/SKU/lot/serial, qty, action, reason/approval | Không tăng khả dụng trước duyệt; không giải phóng vượt cách ly |
| PaymentReversal/AllocationReversal | payment/allocation nguồn, amount, reason/approval | Đảo bằng dòng mới, không sửa tiền/nợ đã ghi; một phần/full theo thiết kế chốt |
| ExchangeSettlement | return/sale/payment refs, refund/offset/additional | Tổng bù + hoàn không vượt quyền lợi trả; không thu hai lần |
| Care/Warranty | assignee, due/result/completedAt; warranty bàn giao/held items | Bảo hành không tự tăng tồn bán, người nhận vật phẩm có dấu vết |

Các tên bảng trên là **khái niệm thiết kế**, không mặc định phải tạo đúng tên hoặc engine riêng. Tech Lead chọn migration/tái dùng cấu trúc có sẵn, ghi mapping và đánh giá transaction trước code. Vẫn giữ inventory ledger là nơi ghi tồn, finance ledger là nơi ghi tiền/nợ; không thêm field total editable để thay sổ.

## 4. Đặc tả chức năng

Mỗi FR dưới có actor, đầu vào, xử lý, kết quả và lỗi. Toàn bộ trường và cột màn hình của BA D-01…D-20 là phần bắt buộc của SRS, không phải tham khảo tùy chọn. Nhóm phát triển phải ghi field mapping UI ↔ API ↔ DB trong task contract.

### FR-01. Đăng nhập và điểm làm việc

Actor: mọi người dùng. Dữ liệu D-02/03. Kế thừa auth Core; sau xác thực tải menu/quyền/scope. Chủ setup chưa xong vào wizard; nhân viên chọn kho hợp lệ, có thể lưu mặc định. Kho bị thu hồi/ngừng thì xóa lựa chọn cũ và yêu cầu chọn lại. Chưa có kho chỉ được hồ sơ/đăng xuất và chức năng được cấp, không tự cấp scope. Lỗi phân biệt xác thực, tài khoản tắt, thiếu quyền, kết nối; không log mật khẩu. AC-01/14; T-03–07.

### FR-02. Wizard và đầu kỳ

Actor: chủ/người SETUP. 7 bước theo F-02: hồ sơ → chi nhánh/kho → người/quyền/người duyệt → danh mục → đầu kỳ → chính sách → kiểm tra. GET tiến độ, PATCH từng bước có version; nút tiếp chỉ khi điều kiện hợp lệ; bỏ qua đầu kỳ phải xác nhận số dư 0. Màn cuối phân biệt batch nhập xong với phiếu đã ghi. Hoàn thành không tạo thu/chi/bán thử. Chỉ chủ sửa tiến độ, nhân viên không bị chặn làm việc vì wizard của chủ còn bước tùy chọn. Ngày hiệu lực phụ thuộc OPEN-03. AC-01/15; T-05–07/57–60.

### FR-03. Tài khoản, quyền và phạm vi

Actor: quản trị. Tạo/sửa/bật tắt/reset người; gán nhiều vai trò và kho, thu hồi scope; mẫu chủ, quản lý, sales, thu ngân, kho, mua, CRM, kế toán. Quyền APPROVE không tự cấp COST/FINANCE; quyết định mua cần đủ quyền mua/duyệt/phạm vi hoặc permission riêng được Tech Lead đặc tả. Không cấp toàn chi nhánh mặc định. Tải lại quyền khi đổi, phiên cũ không vượt API. Nhận chuyển kho dùng scope đích mà không bắt thủ kho đích thấy toàn nguồn. Dữ liệu D-03/19. AC-14/19; T-03/04/36/66.

### FR-04. Thuộc tính, nhóm, thương hiệu

Actor SETUP. CRUD có version/ngừng; giá trị thuộc tính có mã/tên/cha/active/order. Select chỉ lấy active; phiếu/SKU cũ vẫn hiển thị tên snapshot/giá trị ngừng. Trùng mã hoặc xóa đã tham chiếu bị chặn. Không lưu tập biến thể trong một chuỗi value tự do. D-05; AC-20; T-08–10.

### FR-05. Sản phẩm cha và SKU biến thể

Actor SETUP, đọc theo READ. Chọn hasVariants; không biến thể sinh một SKU. Có biến thể chọn attribute/values cấu hình sẵn, preview các tổ hợp, chọn tổ hợp sẽ kinh doanh, nhập mã/barcode/giá từng SKU rồi lưu nguyên tử. Trùng tổ hợp/SKU/barcode, sai attribute hoặc giá trị ngừng → lỗi đúng dòng, không lưu một phần. Bộ chọn giao dịch theo product → SKU, không cho bán cha. SKU đã có sổ không đổi baseUOM/tracking/tổ hợp; ngừng không mất lịch sử. Danh sách nhóm cha và mở SKU có STT/tìm/lọc. Migration theo mục 3.1. D-04/05; AC-02/20/21; T-11–15.

### FR-06. UOM, giá và chính sách bán

Actor SETUP; SELL xem giá. Conversion giữ theo SKU, cơ sở1, factor dương4 lẻ; đổi hệ số không đổi phiếu cũ. Rule giá khách > nhóm > branch/channel > default; minQty cao nhất phù hợp → priority desc → ID asc. Có exclusive promotion phù hợp: lấy exclusive priority cao nhất, bỏ mọi promo khác; không có exclusive: tuần tự các stackable. Preview và post cùng engine và snapshot. Qty rule tính theo cơ sở; đơn giá trả về đúng UOM. Ngày rule hợp lệ và từ≤đến; UI đủ CTV, active/ngừng rule. Giảm vượt discountLimit/below cost gửi duyệt; credit không phải ngoại lệ. D-05/07; AC-02/22; T-16–18.

### FR-07. Khung chứng từ và duyệt

Actor theo type. Tạo/sửa nháp version; số phiếu counter unique không reuse; submit/approve/reject/post/cancel/close đúng trạng thái. Soạn bỏ nháp chưa gửi không làm đảo sổ. Phiếu có hiệu lực cần yêu cầu duyệt hủy/đóng. Người khác duyệt snapshot đúng version; nội dung đổi → hết hiệu lực duyệt. Posted immutable; reject có cách sao chép thành nháp mới có source reference. Idempotency áp tạo và command ảnh hưởng sổ, không chỉ disable nút. Lỗi rollback nguyên tử document/movement/value/openItem/payment/allocation/audit. D-01/08/09/16; AC-08/14/17/23; T-19–22.

### FR-08. NCC và nhập trực tiếp

Actor PURCHASE tạo; APPROVE phù hợp duyệt. NCC B, kho B, hàng/qty/price, lô/serial conditional; fee/paid optional. Nháp/chờ không tăng tồn. Duyệt ghi nhập, vốn bình quân, AP tiền hàng và khoản chi/phân bổ đã trả. Không tăng AP NCC hàng bằng phí trả hãng khác; phí cấu trúc và nghĩa vụ chờ OPEN-02. Tách phí vốn hóa và tổng phải trả rõ ở UI. Tổng AP/vốn/tiền trả phải đối chiếu. D-06/08–10; AC-03/24; T-23–25.

### FR-09. Đề nghị/đơn mua/nhận từng phần

Actor PURCHASE/APPROVE. Đề nghị optional, đơn trực tiếp hoặc nguồn; mỗi phiếu nhận sourceLine và remaining. Bảng ordered/received/current/remaining; thừa/khác giá hiển thị diff và lý do người có quyền duyệt. Nhận thiếu để còn mở, đóng phần còn cần duyệt. Nhận từ đơn đã đóng/hoàn thành bị chặn; nhiều người nhận cùng lúc kiểm phần còn lại dưới lock. Nguồn đề nghị/báo giá chỉ tham khảo hay bị giới hạn phải thống nhất; baseline đề xuất nguồn tham khảo không thay lượng của đơn đã xác nhận. D-08–10; AC-04/23; T-26/27.

### FR-10. Trả mua và NCC hoàn tiền

Actor PURCHASE, FINANCE, APPROVE. Nguồn receipt posted; qty/lot/serial đúng và còn trả, đủ tồn xuất. Duyệt giảm tồn/vốn, cấn AP còn nợ trước; phần đã trả lập SupplierRefundClaim. Ghi thu NCC hoàn + phân bổ vào claim không vượt dư; nhận nhiều lần. Không phân bổ claim vào AR bán hàng; không giảm AP hai lần. D-10/13; AC-25; T-28/29.

### FR-11. POS

Actor SELL. SKU/qty/UOM, customer conditional debt, payment/method; scan barcode và keyboard; lô FEFO hoặc chọn hợp lệ, serial đủ. Preview rồi confirm atomic; trong policy post, ngoại lệ pending và không giao hàng. Paid≤total; tiền đưa/thối hiển thị riêng nếu UI cần, không ghi thu vượt. Nợ tự do nhưng chọn khách. Thiếu tồn giữ phần bán hợp lệ và có hành động lập đơn phần thiếu **có xác nhận của người dùng**, không tự tạo phiếu bán một phần không báo. Ngăn phát sinh đôi khi thao tác này gồm hai chứng từ. D-08–10; AC-05/07/08; T-30/31.

### FR-12. Báo giá, đơn bán và nhiều lần xuất

Actor SELL. Quote optional, đơn manualonline/sỉ/CTV/quầy; customer B. Theo nguồn giữ giá/chính sách nguồn, báo giá hết hiệu lực cảnh báo và xin xác nhận lại theo contract, không âm thầm thay giá. Đơn không ghi tồn/AR; cọc tiền trước. Mỗi issue một kho, sourceLine, xuất≤remaining, nhiều kho/multipleissues. Đã xuất/đóng/chờ hàng riêng, progress tiền/giao độc lập; chưa giữ hàng tự động. D-08–10; AC-06/07/26; T-32/33.

### FR-13. Hủy/đóng và hoàn cọc

Actor SELL/PURCHASE yêu cầu, APPROVE duyệt; FINANCE hoàn. Khách yêu cầu hủy thì ghi yêu cầu/reason; approved/partial chỉ đóng remaining, không đảo đã xuất/nhận. Lock để phiếu xuất đang ghi không chạy song song vượt phần đóng. Deposit available = thu cọc chưa phân bổ chưa hoàn/đảo; refund sau close không vượt available. Không hủy bởi carrier status. D-08/10/15/16; AC-17/23; T-34/35.

### FR-14. Tồn, tracking và chuyển kho

Actor STOCK; READ xem tồn, COST xem value. Giữ movement immutable, transaction balance/value, khóa SKU–kho chống âm. FEFO ghi segments; serial state unique. Transfer request→approve→dispatch→transit→partial receive→complete; receiver có quyền đích, chỉ thấy nguồn tối thiểu cần đối chiếu. Receive theo dòng good/damaged qty riêng; serial/lô thuộc dispatched. Thiếu giữ transit; claim missing có reason/approval, ghi mất theo transitledger hoặc tài khoản tồn đang chuyển, không giảm nguồn lần hai. Đối chiếu sent=received+remaining+settledLoss, value tương tự. D-11; AC-07/08/12/19/27; T-36–39.

### FR-15. Kiểm kê/cách ly/nhập xuất khác

Actor STOCK/APPROVE. Count lock SKU tại kho từ bắt đầu kiểm kê đến posted/cancel; snapshot không đổi; lotsqty includingzero/serialsqty. Cách ly kiểm riêng hoặc cùng đợt có hai số dư tách, không bị bỏ khỏi đối chiếu. Nhập/xuất khác mục đích/giá trị rõ và duyệt. Disposition release/destroy/returnSupplier được kiểm qty/tracking và vốn; dùng movement/ledger có dấu vết, không update quarantine thủ công. Hủy cách ly giảm qty/value đúng, release chỉ đổi status/available không tạo tổng lượng mới. D-11; AC-13/27; T-40–43.

### FR-16. Giao và COD nhập tay

Actor SELL tạo/cập nhật giao, FINANCE settle. Mỗi đợt deliverylines đối chiếu issue, trạng thái chuẩn; giao một phần có lượng thực từng dòng. Failed không nhập trả/hủy tự động. COD assigned đa đợt≤debt hợp lệ, completed qty không đếm hai lần; nhiều settle có idempotency + statementref. Gross thu giảm nợ, fee ghi chi riêng, net thực nhận gross−fee; zeroCOD phí riêng không giả thu khách. Fee khách chịu/hãng charge/fee refunded phụ thuộc OPEN-02. D-12; AC-09/26/28; T-44–46.

### FR-17. Thu chi, nợ, cọc và đảo

Actor FINANCE; APPROVE nếu đảo/hoàn. Payment immutable, amountinteger>0, partner/doc/branchmatch; allocations đúng chiều/partner và không vượt tiền dư/nợ, lock cả hai tránh phân bổ đôi. Cọc order phân bổ khi issue/receipt tạo debt; phần dư còn advance. SupplierRefund phân bổ claim. DueDate/businessDate theo OPEN-03; không creditlimit blocking. Đảo liên kết payment/allocations, reverse mở lại nợ/advance phù hợp và có approval; không xóa record. Summary/report không cộng paymentreversal như khoản tiền mới. D-13; AC-05/06/25/29/30; T-47–50.

### FR-18. Trả bán, hoàn và đổi

Actor SELL nhận, APPROVE duyệt, FINANCE hoàn. Exact originalnumber lookup có giới hạn trả dữ liệu; correct sold serial/lot, cumulative qty≤sold, product return snapshot. Crossbranch receive+credit source branch traceable. Credit AR trước, paid portion refundcredit; không hoàn vượt sau các lần refund/exchange. Đổi = return+sale linked, offsetvalue explicit, chênh thu/hoàn có ledger; không dùng payment mới giả cho tiền bù. Opened theo policy, overdue qua ngoại lệ; USED OPEN-01; lỗi warranty. D-09/15; AC-10/11/31; T-51–53.

### FR-19. Bảo hành

Actor SELL/quyền dịch vụ theo scope. UI searchoriginal→selectline; không nhập ID kỹ thuật. Qty≤remaining eligible, serialexact, không có warranty active trùng; nonserial tổng vật phẩm nhận chưa bàn giao≤qtysold phù hợp nghiệp vụ. Theo received→processing→ready→handedover, assignee/due/note/result/cost/recipient timestamps. Custody list giữ hộ không vào stock bán. Outofwarranty/sửa có phí riêng không tự mở policy. D-15; AC-32; T-54/55.

### FR-20. CRM/nhắc việc và CTV

Actor CRM profile/history/care, FINANCE commission. Customer commonmaster, transactionalhistoryscope; notes≤4000, due, assignee, result/completedAt. Reminder list serverpaginated, countfulltotal not20sample. CTV ORDER/monthaddition/signedadjustment; period month, correctphiếu bán đã ghi/crossreturn adjustments, paid≤remaining; không auto commission rate. D-06/14; AC-16/33; T-56/61/62.

### FR-21. Import

Actor SETUP plus permissions on openingdoc. XLSX/CSV/API samevalidator: 2MB/500rows hiện tại, noformulas, firstsheet, headers/4decimal/date/tracking/type/scope. Preview validatefully như commit; rowerrors không ghi một phần; checksum/idempotency incl targetscope tránh nhầm batch cùng dữ liệu ở kho khác. New productimport supporting attributes phải có template chuẩn, không infer free text. Commitcatalog writes; openingdraft→submit→approve→post; wizard hiển thị riêng. Ngăn đầu kỳ trùng cùng ngày/phạm vi, không tạo nợ âm cho advance. D-17; AC-15/21; T-57–60.

### FR-22. Dashboard/báo cáo/export/in

Actor READ/REPORT/EXPORT/COST. Sixtabs quyền, period/branchwarehousefilter/named scope; indicator→filteredlist; nợ/CRMcount full not sample. Report salesnet, purchasesnet, inventory, AR/AP, payments/advances, CODclaims, commissions; đúng sources and dates. Serverpaging/exportallpages streaming/bounded; no silent1000truncate. PermissionCOST checked outputfile as API. Receipt80mm/A4 based exactposteddata, printdate vs transactiondate distinguish, paid/open current vs atsale labeled. D-18; AC-18/34; T-63–65.

### FR-23. Core admin và tệp

Actor admin. Regression auth/users/org/roles/modules/resources/events/jobs/files/settings/profile. Giữ technicalmenus away retailstaff, Vietnamese labels, no accidentalMFAchange. Tệp gắn retaildoc/product theo role/tenant, size/contenttype limits, no crossscope download; nhật ký filterreadonly. Profile uses corelogo byreference, not duplicateblob. D-19; AC-14/35; T-66/67.

### FR-24. Bàn giao vận hành và đường nâng SaaS

Actor TechLead/DevOps/QA. Fullsource runlocal, envtemplate không chứa bí mật, migrationsclean/upgradedb, backuprestore onlytenant/files correct, deploymentdoc, test reconciliation. No auto tenant exporter/selfservicebilling in R1; cô lập dữ liệu là điều kiện baseline. Source bàn giao procedure thực hành trên môi trường kiểm thử; không push/deploy customerlive trong task docs. BA mục23; AC-36; T-68–72.

## 5. Hợp đồng API/UI và lỗi

### 5.1. Kế thừa và bổ sung

API hiện tại bắt đầu `/api/v1/retail`; giữ các endpoint legacy đang dùng và responseadapter khi refactor. Các path dưới là **đề xuất contract cần chốt T-01 trước code**, không phải endpoint đã tồn tại.

| Nhóm | Kế thừa | Bổ sung đề xuất và dữ liệu cốt lõi |
|---|---|---|
| Auth/Core | auth/login, access/*, settings, files | Không tạo authengine mới; workingwarehouse/profilerefs theo quyền |
| Setup | catalog branches/warehouses/scopes/settings | GET/PATCH business-profile; GET/PATCH onboarding, complete với version |
| Product | catalog/skus, warehouse-config, uom-conversions | products, attributes/{id}/values, products/{id}/variants/preview, variants/commit; nestedattribute IDs |
| Catalog | catalog/partners, price-rules/promotions | PATCH/* với version, activefilter, roletemplates/scoperevoke |
| Document | documents/create/detail/actions | PATCH documents/{id} draft; closureapproval; sourceprogressview; Idempotency-Key cho create/commands |
| Stock | stock/movements/tracking, transfer actions | transfer variance, quarantine actions và trackingrefs |
| Delivery | deliveries/create/status/settle | deliverylines, shipmentremaining, statementref, settlementlist |
| Finance | finance/payments/allocations/commissions | supplierrefundclaims+allocations, approvedreversals, exchangesettlements |
| CRM/Service | care/history/warranties | assignee/result/completedAt, bộ chọn phiếu/dòng nguồn, giữ hộ/bàn giao |
| Data | imports/preview/commit, exports/reports | templates, pagination/totals, filteredexport, print payload |

GET list chuẩn trả items/page/size/total/filterScope; totals không bị cắt bởi page. POST/PATCH trả id/number/status/version và references tác động; không buộc FE tự cộng/trừ sổ. Các amount trong API dùng representation thập phân chính xác thống nhất, không để JavaScript float quyết định số tiền. Date/time và null/empty optional phải có ví dụ.

### 5.2. Ví dụ hợp đồng sản phẩm biến thể

Ví dụ đề xuất cho `POST /api/v1/retail/products/{productId}/variants/commit`; `productId`, `attributeId`, `valueId` và `baseUomId` là UUID đã tồn tại trong cùng tenant. Không truyền tên “Đỏ/M” để tự tạo danh mục. Giá gửi dưới dạng chuỗi thập phân. Cần chốt contract tại T-01, đây chưa phải endpoint hoạt động.

```json
{
  "expectedProductVersion": 1,
  "variants": [
    {
      "code": "AO-A-DO-M",
      "barcode": "893000000001",
      "attributeValues": [
        {"attributeId": "<uuid-mau>", "valueId": "<uuid-do>"},
        {"attributeId": "<uuid-size>", "valueId": "<uuid-m>"}
      ],
      "baseUomId": "<uuid-cai>",
      "price": "150000.0000",
      "tracking": "NONE",
      "returnDays": 7,
      "warrantyDays": 0,
      "allowOpened": false
    }
  ]
}
```

Các giá trị trong dấu `<…>` chỉ là tham chiếu minh họa, phải thay bằng UUID thật trong test. Header `Idempotency-Key` bắt buộc cho command commit. Backend trả các SKU đã tạo, phiên bản sản phẩm và tổng tổ hợp; SKU trùng báo lỗi theo `variants[i]`, không lưu một phần. Cùng khóa và payload trả kết quả cũ; đổi payload cùng khóa báo conflict. Với sản phẩm không có biến thể, contract tạo sản phẩm phải sinh một SKU trong cùng transaction.

Response danh sách chuẩn đề xuất:

```json
{
  "items": [],
  "page": 0,
  "size": 50,
  "total": 0,
  "filterScope": {"branchIds": [], "warehouseIds": []},
  "totals": {}
}
```

`page` bắt đầu từ 0; `totals` chỉ có chỉ số của loại màn hình và tính trên toàn bộ kết quả lọc, không chỉ trang hiện tại. Danh mục shared dùng scope tenant; danh sách giao dịch có branch/warehouse được phép. Không trả ID kho ngoài quyền trong `filterScope`.

### 5.3. Lỗi cần phân biệt

| Mã nghiệp vụ | Tình huống / hướng xử lý UI |
|---|---|
| INVALID_FIELD | Lỗi trường, có field/row/message; giữ dữ liệu nhập |
| SCOPE_DENIED / FORBIDDEN | Thiếu scope/quyền; không tiết lộ số liệu ngoài quyền |
| VERSION_CONFLICT | Phiếu thay đổi; tải lại, không ghi đè |
| IDEMPOTENCY_CONFLICT | Khóa đã dùng payload khác; yêu cầu mới không thực thi |
| INSUFFICIENT_STOCK | Lượng khả dụng thiếu; đề nghị đơn chờ, không ghi tồn âm |
| COUNT_LOCKED | SKU tại kho đang đếm/chờ duyệt; chỉ ra kỳ kiểm kê được phép xem |
| INVALID_SERIAL / WRONG_SOLD_SERIAL | Serial sai/không đủ/không đúng nguồn; sửa lựa chọn |
| EXPIRED_OR_UNAVAILABLE_LOT | Hết hạn/thiếu lô bán được; không cho bypass duyệt |
| SOURCE_CLOSED / QUANTITY_EXCEEDED | Đơn nguồn đóng/đã đủ/qtyvượt; tải progress mới |
| SELF_APPROVAL_DENIED / APPROVAL_STALE | Người tạo tự duyệt hoặc snapshotduyệt cũ |
| ALLOCATION_EXCEEDED / REFUND_EXCEEDED | Vượt tiền/nợ/quyền hoàn còn lại; không ghi một phần |
| INVALID_STATE | Thao tác không đúng trạng thái; refresh hợp lệ |

HTTP mapping phải công bố rõ trong OpenAPI: 401 auth, 403 quyền, 404 đối tượng không thể truy cập theo thiết kế không tiết lộ, 409 conflict, 422 validation mục tiêu; legacycode adapter cần mapping khi hiện tại trả khác. FE đọc code/detail, không suy từ lỗi fetch chung. 5xx/network không khẳng định phiếu đã thất bại; tra lại bằng key trước retry.

## 6. Trạng thái và tác động sổ

| Loại | State/command chuẩn | Ghi sổ |
|---|---|---|
| POS/issue/receipt/return/other/opening/refund | DRAFT→PENDING→POSTED; ordinary sale DRAFT→POSTED; pending reject→REJECTED | Post tác động tồn/vốn/nợ/tiền theo type trong một transaction |
| Quote/request | DRAFT→APPROVED hoặc PENDING→APPROVED; closed theo requestduyệt | Không tự ghi tồn/nợ |
| Sale/purchase order | DRAFT→APPROVED→PARTIAL→COMPLETE; remaining close qua duyệt→CLOSED | Nhận/xuất child mới ghi; cọc riêng payment |
| Transfer | DRAFT→PENDING→APPROVED→TRANSIT→PARTIAL→COMPLETE | Dispatchgiảm nguồn, receive tăng đích, variancesettle ghi mất transit |
| Count | DRAFT→COUNTING→PENDING→POSTED; cancelhợp lệ mở khóa | Snapshotlock từcounting; ghi chênh lệch, không cập nhật lượng lúc đếm |
| Delivery | PREPARING→SHIPPING→PARTIAL/DELIVERED/FAILED; partial→shipping/delivered/failed | Không tự nhập/thu; CODsettle command riêng |
| Warranty | RECEIVED→PROCESSING→READY→HANDED_OVER | Giữ hộ, không bán/tồn khả dụng |
| Approvalaction | REQUESTED→APPROVED/REJECTED/STALE | Approve action xử lý nguyên tử với targetversion |

Không thêm một state “Đã thanh toán” vào status đơn để mất tiến độ xuất. Fulfillment/delivery/settlementlà dimension riêng. Status target đối chiếu BA và contract thực tế trước refactor; tên mới cần migrationadapter.

## 7. Yêu cầu phi chức năng

| NFR | Yêu cầu / bằng chứng nghiệm thu | Task |
|---|---|---|
| NFR-01 | Cô lập tenant/scope/COST ở list/detail/search/create/action/import/export/file; test ít nhất hai tenant, nhiều kho và quyền thu hồi | T-04/36/66/69 |
| NFR-02 | Transaction nguyên tử, rollback chèn lỗi giữa tồn/tiền/nợ/audit; không halfposted | T-19/21/69 |
| NFR-03 | Concurrent lastunit/partialreceive/returnqty/allocation; chỉ một kết quả hợp lệ; idempotentreplaysamepayload oneeffect | T-20/21/69 |
| NFR-04 | Audit actor/time/action/source/reason/result/correlation, không log password/token; không cho sửa audit | T-22/66/69 |
| NFR-05 | Cleanmigrate+upgrade giữ SKU/stock/debt/postedrefs; rollback runbook/backup và reconcile | T-11/12/68/70 |
| NFR-06 | FE loading/empty/error/retry, labelsViệt/STT/responsive; bàn phím quầy và barcode trên thiết bị mẫu | T-02/31/64/71 |
| NFR-07 | Paging/totals/export không cắt; báo cáo cùng filteredphạm vi/thời gian, exactdecimal acrossBE/FE | T-17/63–65/69 |
| NFR-08 | Lưu ngày nghiệp vụ và timestamp tách, ngày Việt Nam không lệch UTC; khóa kỳ/ngày lùi theo OPEN-03 | T-47/63/69 |
| NFR-09 | Backuprestore DB/files và rebuildfromsource có biên bản; RPO/RTO chốt trước production | T-68/70/72 |
| NFR-10 | Benchmark trên dataset có lượng/chi nhánh rõ; ghi p95 POS/stock/report/import, chốt ngưỡng với PO thay vì tự hứa | T-70 |
| NFR-11 | No mock/không nạp demo mặc định vào database vận hành; QA isolatedDB, dọn fixture có scope | T-01/68/69/71 |

Không cần kiểm lại toàn bộ hệ thống cho mỗi thay đổi nhỏ; kiểm phù hợp ảnh hưởng, nhưng release phải qua bộ E2E/sổ/quyền. Toàn bộ mục này là yêu cầu, không tuyên bố kiểm thử hiện tại đã đạt.

## 8. Tiêu chí nghiệm thu truy vết

| AC | Yêu cầu / kết quả kiểm cụ thể | BA / FR |
|---|---|---|
| AC-01 | Wizard lưu và mở lại đúng bước; chủ chưa đủ thiết lập vào wizard, nhân viên vào kho được cấp; hoàn tất không tạo giao dịch demo | BA2-01 / FR-01/02 |
| AC-02 | Nhập 1 thùng = 24 chai, bán 3 còn 21; đổi hệ số thành 30 chỉ ảnh hưởng phiếu mới | BA2-02 / FR-05/06 |
| AC-03 | Nhập 10 × 100.000 chưa trả: chờ duyệt chưa tăng tồn/nợ; duyệt tăng tồn 10 và phải trả 1.000.000 | BA2-03 / FR-08 |
| AC-04 | Đơn mua 10 nhận 6 rồi 4; khác giá có lý do/duyệt; nhận đồng thời không vượt phần còn khi chưa xác nhận thừa | BA2-04 / FR-09 |
| AC-05 | Bán 200.000, thu 150.000, chọn khách → nợ 50.000 không cần duyệt vì nợ; thiếu khách khi bán chịu bị chặn | BA2-05 / FR-11/17 |
| AC-06 | Đơn 1.000.000, cọc 200.000, xuất 600.000 rồi 400.000 → doanh thu 1.000.000, phân bổ cọc đúng một lần, còn nợ 800.000 | BA2-06 / FR-12/17 |
| AC-07 | Thiếu tồn/lô hết hạn/serial đã bán bị chặn; chuyển phần thiếu sang đặt trước có xác nhận; không giao dịch ở sản phẩm cha | BA2-07 / FR-11/12/14 |
| AC-08 | Hai người bán chiếc cuối chỉ một phiếu ghi; gửi lại cùng khóa tạo chỉ một bản ghi | BA2-08 / FR-07/11/14 |
| AC-09 | COD 500.000, phí 25.000 → nợ giảm 500.000, thực nhận 475.000, chi phí 25.000; gửi lại không đối soát lần hai | BA2-09 / FR-16 |
| AC-10 | Trả hết phiếu 300.000 đã thu 200.000 tại chi nhánh khác → giảm nợ 100.000, hoàn tối đa 200.000, tăng tồn kho nhận, đối chiếu chi nhánh nguồn | BA2-10 / FR-18 |
| AC-11 | Serial sai/trả vượt đã bán luôn bị chặn; trả nhiều lần giữ tổng tiền/thuế nguồn, lần cuối nhận phần dư làm tròn | BA2-11 / FR-18 |
| AC-12 | Gửi 10, nhận 6 gồm 1 hỏng → nguồn giảm 10, đích tăng 6/cách ly 1, còn chuyển 4; tổng giá trị chuyển đối chiếu không lệch | BA2-12 / FR-14 |
| AC-13 | Đếm SKU X ở A: tồn 10 đếm 8; X/A khóa, Y/A và X/B vẫn hoạt động; duyệt ghi -2 và mở khóa | BA2-13 / FR-15 |
| AC-14 | Tự duyệt, ngoài phạm vi hoặc xem vốn không quyền bị chặn cả API/export; thu hồi có hiệu lực với phiên cũ | BA2-14 / FR-03/07/23 |
| AC-15 | Import lỗi không ghi; gửi lại không tạo trùng; nhập batch đầu kỳ tạo nháp, duyệt mới tăng tồn/nợ | BA2-15 / FR-02/21 |
| AC-16 | Hoa hồng 300.000 + bổ sung 200.000 = 500.000; chi vượt bị chặn; điều chỉnh âm đúng kỳ | BA2-16 / FR-20 |
| AC-17 | Đóng phần chưa xuất cần duyệt; hoàn cọc không vượt chưa sử dụng; giữ phần đã xuất; hãng giao thất bại không tự hủy đơn | BA2-17 / FR-13 |
| AC-18 | Bán, tồn, nợ, tiền và báo cáo đối chiếu được; xuất đủ bộ lọc; phiếu in ghi số dư có nhãn rõ | BA2-18 / FR-22 |
| AC-19 | Người chỉ có kho đích nhận phiếu chuyển được giao; không xem tồn nguồn ngoài quyền; người chỉ có nguồn không nhận đích | FR-03/14 |
| AC-20 | Chọn màu/size cấu hình sẵn tạo SKU tổ hợp duy nhất; giá trị ngừng/sai thuộc tính bị chặn; tạo đồng thời cùng tổ hợp chỉ một SKU | FR-04/05 |
| AC-21 | Backfill SKU cũ giữ nguyên ID, lượng/giá trị tồn, nợ và tham chiếu phiếu; mô tả biến thể không tự tạo thuộc tính; import kiểm ID thuộc tính/giá trị | FR-05/21 |
| AC-22 | Giá khách 90.000 ưu tiên nhóm 92.000; khuyến mãi không cộng dồn chọn ưu tiên cao nhất trong nhóm đó; lượng quy đổi/thuế/snapshot đúng | FR-06 |
| AC-23 | Xung đột phiên bản nháp không ghi đè; sửa nội dung làm duyệt cũ mất hiệu lực; đóng/xuất đồng thời không vượt lượng nguồn | FR-07/09/13 |
| AC-24 | Hàng 1.000.000, phí hãng 100.000 → vốn 1.100.000, AP hàng 1.000.000 và phí riêng theo OPEN-02; không ghi nghĩa vụ phí hai lần | FR-08 |
| AC-25 | Trả mua 300.000, còn nợ 100.000 → giảm AP 100.000, NCC phải hoàn 200.000; thu 150.000 rồi 50.000 tất toán, thu vượt bị chặn | FR-10/17 |
| AC-26 | Xuất 10, giao 4 rồi 6 → thực giao 10; không ghi doanh thu/nợ lần hai, không gán COD trùng | FR-12/16 |
| AC-27 | Thiếu 4 đang chuyển: duyệt xử lý mất mới đóng lượng/giá trị đang chuyển; giải phóng cách ly không tăng tổng tồn; hủy giảm lượng và giá trị đúng | FR-14/15 |
| AC-28 | Giao không COD vẫn xử lý phí theo OPEN-02; thực nhận/phí khách trả/hoàn phí nhất quán với chính sách đã chốt | FR-16 |
| AC-29 | Hai lần phân bổ đồng thời cùng khoản tiền không vượt tiền dư; ứng trước/nợ còn lại đúng sau hoàn một phần | FR-17 |
| AC-30 | Đảo khoản thu đã phân bổ 150.000 → mở lại AR 150.000, ghi dòng đảo trái dấu, không xóa nguồn; đảo trùng bị chặn | FR-17 |
| AC-31 | Quyền lợi trả 200.000 bù bán mới 250.000 → thu thêm 50.000, không tạo thu tiền mặt giả 200.000; bán mới thấp hơn thì hoàn chênh có duyệt | FR-18 |
| AC-32 | Bảo hành đúng serial, hàng không serial kiểm lượng đang nhận; bàn giao có người/thời gian; không tạo vận động tồn bán | FR-19 |
| AC-33 | Người chăm sóc đúng phạm vi, giờ Việt Nam, hoàn thành có kết quả/thời gian; hơn 20 nhắc vẫn đếm đủ, lịch sử không lộ ngoài quyền | FR-20 |
| AC-34 | Hơn 1.000 dòng tồn/tiền báo cáo và export đủ; tổng theo bộ lọc đúng; không lộ vốn khi không quyền | FR-22 |
| AC-35 | Hồi quy Core login/profile/users/files/settings/jobs/modules; không vô ý bật MFA hoặc ép đổi mật khẩu local | FR-23 |
| AC-36 | Build từ source sạch, nâng DB, sao lưu/khôi phục tệp; đối chiếu đúng dữ liệu tenant, chạy local theo hướng dẫn không chứa bí mật | FR-24 |

Số liệu chỉ dùng fixture isolated; task nào phụ thuộc OPEN dùng AC conditional, không đánh dấu Passed trước chốt. AC hoàn thiện là bằng chứng test/biên bản, không chỉ ảnh UI.

## 9. Kế hoạch giao việc và Definition of Done

Backlog riêng có 72 task. Mỗi task có owner vai trò, priority, dependency, thao tác/đầu ra và AC. Trước assign ghi người thực hiện, estimate sau T-01 khảo sát, sprint/mốc, reviewer và trạng thái. Không gán ngày hoàn thành giả khi chưa biết số người/năng lực.

Đường chính: nền contract/quyền → productmigration/variant/UOM → khungchứngtừ/sổ → nhập/POS/đơn → chuyển/trả/tiền/COD → báo cáo/UAT/restore. Gói FE bắt đầu khi contract đã review, tích hợp thật khi BE sẵn; QA viết ca theo AC trước demo. P2 đi sau P1, không làm trì hoãn fix tiền/tồn.

Một task Done khi: phần việc được mô tả đầy đủ, review; migration/backfill nếu có; API contract/quyền/scope/error đúng; UI gọi thật và đủ trạng thái; test AC gắn evidence; không mock đường chính; nhật ký/transaction nếu tác động sổ; tài liệu vận hành cập nhật. Task FE/BE riêng được Done theo deliverable riêng, nhưng FR chỉ Done sau integration+QA, không lấy “xongBE” thay tính năng hoàn chỉnh.

Milestone nghiệm thu: M0 contract/quyết định → M1 bắt đầu sử dụng/sản phẩm → M2 nhập/quầy → M3 đơn/tiền/chuyển/kiểm → M4 trả/COD/service/report → M5 UAT/restore/bàn giao. Releasegate: P1 done hoặc PO duyệt phạm vi loại trừ rõ, quyết định conditional liên quan chốt, AC bắt buộc đạt, không lỗi nghiêm trọng tiền/tồn/quyền; P2 chưa done phải ghi hạn chế. Không gọi release fullBA nếu còn thiếuFR được cam kết.
