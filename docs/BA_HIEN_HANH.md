# BA hiện hành — AICRM

Phiên bản 3.0 — 09/10/2026. Mốc source: main ae59355 và thay đổi đợt 3 đang có tại local, chưa commit. Đây là mô tả hiện trạng, không xác nhận server đã cập nhật cùng phiên bản.

Cơ sở: source frontend/backend, manifest module, migration V1–V33 và kết quả rà soát chỉ đọc ngày 09/10/2026. Trong lần viết tài liệu này không truy vấn lại database sống vì Docker Desktop đang tắt. “Hiện có” là hành vi xác định trong source; “Hạn chế” là phần chưa hoàn chỉnh; “Mở rộng” chỉ là backlog. Quyền được khai báo không đồng nghĩa mọi thao tác đã có đủ giao diện/API.

## 1. Mục tiêu và phạm vi

Quản lý bán hàng cho hộ kinh doanh, cửa hàng nhỏ 1–3 điểm bán và chuỗi nhiều địa điểm. Luồng hằng ngày: thiết lập → danh mục → nhập hàng → bán hàng → tồn và công nợ → đối chiếu. Sản phẩm là Master Data; Mua hàng là giao dịch mua; Kho là giao dịch tồn. Kế toán hiện là kế toán nội bộ.

Java Core Platform cung cấp xác thực/quản trị; web desktop và mobile dạng app cần Internet. Có mô hình tenant/công ty và phân quyền dữ liệu. Có đóng gói source module cho core tương thích; chưa có SaaS thương mại hoàn chỉnh gồm đăng ký thuê bao, tính phí và tự tách database khách hàng.

Ngoài phạm vi hiện tại: bán offline, ca bán và kiểm đếm tiền mặt, API vận chuyển/sàn, hóa đơn điện tử, kế toán pháp định, tự động hoa hồng và phân bổ chi phí mua phát sinh muộn sau bán. Trường legacy trong API không có nghĩa phải đưa lại vào form đã tinh giản.

## 2. Vai trò, công ty và tổ chức

| Vai trò nghiệp vụ | Công việc | Phạm vi |
|---|---|---|
| Chủ doanh nghiệp / admin | Thiết lập, quản trị, duyệt, đối chiếu | Toàn công ty khi có ALL_BRANCHES |
| Quản lý | Điều phối và duyệt | Kho/địa điểm được cấp |
| Bán hàng / thu ngân | Báo giá, đơn bán, POS, thu tiền | Kho bán được giao |
| Mua hàng | NCC, đề nghị, đơn mua, nhập mua | Kho mua được giao |
| Thủ kho | Nhập/xuất, chuyển, kiểm kê | Kho được giao |
| Chăm sóc | Thông tin, lịch sử, ghi chú, lịch hẹn | Theo quyền và phạm vi |
| Kế toán nội bộ | Thu chi, nợ, hoa hồng, đối soát | Theo phạm vi và quyền nhạy cảm |

Vai trò được tạo và gán, không cố định theo chức danh; một người có nhiều vai trò. Người quản lý trên hồ sơ địa điểm/đối tác chỉ là thông tin phụ trách, không tự cấp quyền.

Mỗi công ty có ít nhất một địa điểm và một kho. Hệ thống tự tạo địa điểm mặc định và kho chính; công ty một điểm dùng ngay. Admin là người dùng, không phải thực thể công ty/kho. Chỉ hiện lựa chọn nơi/kho khi cần phân biệt nhiều nơi có quyền. Kho luôn thuộc địa điểm, loại SALEABLE hoặc QUARANTINE; kho cách ly không bán.

## 3. Bản đồ hệ thống

| Menu | Chức năng | Ghi chú |
|---|---|---|
| Trang chủ | Trang chủ core | Khác Tổng quan nghiệp vụ |
| Tổng quan | Tab Bán/Mua/Kho/Sản phẩm/CRM/Kế toán | Theo quyền |
| Bán hàng | Bán tại quầy, Đơn bán hàng, Khách hàng | Master chung |
| Mua hàng | Mua hàng, Nhà cung cấp | Nhập trực tiếp hoặc theo đơn |
| Kho | Kho & Tồn, Danh sách kho, Chuyển kho, Cấu hình kho | Giao dịch tồn |
| Sản phẩm | Danh sách, Nhóm, Thương hiệu, Đơn vị, Quy đổi, Thuộc tính | Dữ liệu chung |
| Khách hàng / Đối tác | Khách hàng, Đối tác | Một hồ sơ nhiều vai trò |
| CRM | Khách hàng, lịch sử, chăm sóc | Cùng master |
| Kế toán | Thu chi, Công nợ | Hai màn hình riêng |
| Vận hành chung | Địa điểm/Chi nhánh, Đổi trả/Bảo hành, Cần duyệt, Thiết lập | Điều phối |
| Báo cáo | Báo cáo và xuất dữ liệu | Theo quyền |
| Quản trị hệ thống | Người dùng, Tổ chức, Phân quyền, Module, Tài nguyên, Nhật ký, Tệp, Cài đặt | Core và quyền nghiệp vụ |

## 4. Quy tắc chung

- BR-01: mọi hồ sơ thuộc tenant/công ty. Giao dịch gắn kho; địa điểm suy ra từ kho. Không truy cập tenant khác.
- BR-02: nháp chưa ghi tồn/tiền/nợ. Chỉ ghi tác động khi được ghi sổ; chuyển kho có thời điểm xuất nguồn và nhận đích riêng.
- BR-03: người lập thường không duyệt/từ chối phiếu mình. admin@core.local đang hoạt động và có platform-admin được tự duyệt; chỉ trùng email không đủ điều kiện.
- BR-04: mua hàng, đầu kỳ, chuyển, kiểm kê, trả/hoàn cần duyệt. Bán thường theo chính sách; ngoại lệ giảm giá/vốn chờ duyệt. Bán chịu không bị chặn bởi hạn mức/quá hạn.
- BR-05: không tồn âm. Đơn đặt trước không trừ tồn và chưa tự giữ hàng. POS thiếu tồn phải bổ sung hoặc chuyển đặt trước.
- BR-06: xuất bán LOT ưu tiên hạn sớm; lô hết hạn bị chặn. Ngày hạn bằng hôm nay còn hợp lệ theo kiểm tra hiện có.
- BR-07: tiền VND làm tròn đồng; lượng/quy đổi tối đa 4 số thập phân; giá vốn nội bộ chính xác hơn.
- BR-08: lưu snapshot sản phẩm, quy đổi, giá/thuế và chính sách trên dòng; không viết lại lịch sử khi danh mục đổi.
- BR-09: không xóa phiếu đã ghi sổ. Hủy nháp, đóng phần còn lại, trả hàng và hoàn tiền là thao tác khác nhau.
- BR-10: đối chiếu số/dòng phiếu gốc và serial/lô. Tiền thu/chi và phân bổ nợ là dữ liệu riêng.

## 5. Bắt đầu sử dụng

Đăng nhập quản trị → Doanh nghiệp & Địa điểm → Kho chính → Danh mục → Dữ liệu đầu kỳ → Hoàn tất. Nhân viên không có quyền setup không bị buộc thực hiện wizard quản trị.

| Bước | Thông tin / việc cần làm | Kết quả |
|---|---|---|
| 1 | Tên công ty; mã/tên địa điểm; liên hệ/địa chỉ/MST | Sửa địa điểm mặc định sẵn có |
| 2 | Mã/tên kho chính, địa điểm, loại, phụ trách | Hoàn thiện kho mặc định |
| 3 | Đơn vị, sản phẩm, khách/NCC; nhập file nếu cần | Danh mục dùng chung |
| 4 | Tồn, phải thu, phải trả đầu kỳ hoặc không có | Phiếu cần gửi và duyệt |
| 5 | Đối chiếu, hoàn tất | Bắt đầu vận hành |

Hạn chế: wizard lưu tiến độ nhưng chưa kiểm tra chặt tất cả danh mục/phiếu đầu kỳ đã duyệt trước Hoàn tất. Quản trị phải thực hiện checklist trong runbook.

## 6. Địa điểm và kho

Tạo địa điểm khi mở thêm điểm bán; có thể tạo kho cùng lúc nếu có quyền. Tạo kho từ Danh sách kho, chọn địa điểm hoạt động. Sửa liên hệ/phụ trách/mặc định; chuyển mặc định sang nơi khác trước khi ngừng nơi mặc định.

| Hồ sơ | Bắt buộc | Bổ sung |
|---|---|---|
| Địa điểm | Mã 60, tên 160 | Phone 60, email 160, địa chỉ/ghi chú 2000, manager, default, active |
| Kho | Địa điểm, mã 60, tên 160, loại | Liên hệ, địa chỉ, ghi chú, manager, default tại địa điểm, active |

Mã unique trong công ty; người phụ trách là account hoạt động cùng tenant. Kho hiện có không sửa sang branchId khác. Khi ngừng kho kiểm tra tồn, giá trị, phiếu chưa xong, transit, serial, giao và bảo hành liên quan; không ngừng/xóa để bỏ lịch sử. Phạm vi kho nhân viên được gán riêng.

## 7. Sản phẩm, cấu hình và quy đổi

Luồng: đơn vị → SKU → đơn vị phụ/quy đổi → giao dịch. Một SKU là hàng giao dịch độc lập. Nhóm/thương hiệu/thuộc tính có danh mục nhưng liên kết chuẩn với SKU và sinh tổ hợp biến thể chưa hoàn chỉnh.

| Trường SKU | Quy tắc |
|---|---|
| Mã / tên | Mã unique tối đa 60, có thể sinh; tên bắt buộc, tối đa 160 |
| Barcode | Không bắt buộc, tối đa 100, unique tenant |
| Biến thể | Chuỗi tối đa 160 hiện tại, chưa chọn tổ hợp từ cấu hình |
| Đơn vị cơ sở | Bắt buộc40, tỷ lệ1 |
| Đơn vị phụ / tỷ lệ | Theo SKU, dương, ≤4 số thập phân |
| Theo dõi | NONE / LOT / SERIAL, một kiểu mỗi SKU |
| Giá bán / VAT | Giá ≥0, ≤4 số lẻ; VAT0..100 |
| Trọng lượng | ≥0, không bắt buộc dùng hằng ngày |
| Ngày trả / bảo hành | Nguyên ≥0; 0 không có quyền thời hạn tương ứng |
| Cho mở bao bì | Boolean; không đồng nghĩa cho hàng đã dùng |
| Hoạt động | Hàng active được chọn giao dịch mới |

Product–UOM–Conversion: 1 thùng=24 cái, nhập 2 thùng=48 cái. Đơn vị cơ sở luôn 1; phiếu cũ giữ tỷ lệ snapshot. Cấu hình kho chỉ tập trung cài đặt tồn; UOM/brand/thuộc tính ở Sản phẩm.

Hiện có tạo/ngừng SKU; chưa có luồng update SKU đầy đủ dù có quyền UPDATE. Yêu cầu chọn biến thể đã cấu hình vẫn nằm backlog, không coi đã làm.

## 8. Master khách hàng / đối tác / NCC

Một partner master cho Bán/Mua/CRM; vai trò CUSTOMER/SUPPLIER/COLLABORATOR có thể cùng hồ sơ. Menu từng module lọc role; sửa thông tin dùng chung cùng ID. Tìm mã/điện thoại trước tạo tránh trùng.

| Nhóm | Trường |
|---|---|
| Chung | Mã, tên, loại PERSON/BUSINESS, phone, email, địa chỉ |
| Bổ sung | Tên/địa chỉ công ty, MST, liên hệ, manager |
| Thanh toán | Bank, số tài khoản, chủ tài khoản, ngày công nợ |
| Khách hàng | Nhóm, địa chỉ giao, điều kiện thanh toán, liên hệ |
| Hệ thống | Roles, active, version, lịch sử |

NCC popup có 3 tab: tất cả 6 trường Chung bắt buộc; Bổ sung và Thanh toán optional; email hợp lệ, ngày công nợ 0..3650 khi nhập. VIEW_BANK / UPDATE_BANK riêng. Quick-create khách từ icon+ trong đơn bán chọn hồ sơ mới; chưa thu đủ phải gắn khách để theo dõi, không vãng lai. Owner hồ sơ không tự cấp ACL.

## 9. Bán hàng

### 9.1. POS

Chọn kho → tìm tên/SKU hoặc quét barcode → thêm hàng → lượng/đơn vị → khách nếu cần → kiểm tra giá/VAT/thu → lưu nháp → xác nhận. Đủ điều kiện thì ghi xuất tồn, phải thu, tiền/phân bổ theo thực thu. Ngoại lệ giá chờ duyệt; PENDING chưa là bán thành công.

POS hiển thị tiền khách đưa/trả lại; số thanh toán phía backend không vượt tổng phiếu. Bán chịu cho phép, cần khách khi chưa thu đủ. Nháp đã lưu hiện chưa sửa như giỏ đang soạn; phải hủy/lập lại.

Thiếu tồn: không cố xác nhận. Bổ sung tồn hợp lệ hoặc “Hủy nháp & chuyển đặt trước”; hiện chuyển toàn giỏ, chưa tự tách phần đủ bán ngay/phần thiếu đặt trước. Đặt trước chưa xuất kho.

### 9.2. Báo giá, đơn bán và xuất nhiều lần

Báo giá → đơn bán → phiếu xuất bán → đợt giao → thu tiền. Có thể bắt đầu trực tiếp đơn bán. Báo giá lưu hạn hiệu lực nhưng tự chặn hết hạn chưa hoàn chỉnh, người bán kiểm tra trước chuyển đơn.

Form gồm kho, khách, kênh, bảng dòng gọn, địa chỉ giao/ghi chú và lựa chọn giá gồm VAT. Tìm hàng dạng xổ xuống, thêm khách icon+. Giá theo chính sách, chưa có nhập tay. Không có COD/phí giao khách trả/giảm dòng trên form đơn bán; COD ở đợt giao thủ công.

Đơn duyệt không tạo tồn/nợ. Tạo ISSUE từ nguồn, chọn lượng còn và kho xuất. Nhiều kho dùng nhiều phiếu; không vượt phần còn lại dòng nguồn. ISSUE ghi sổ mới giảm tồn/tạo nợ. Cọc trước xuất là khoản thu chưa phân bổ; sau có phải thu mới phân bổ.

Đóng đơn theo khách yêu cầu chỉ đóng phần chưa giao. Phiếu đã xuất/tiền thu cần trả/hoàn phù hợp. Hiện APPROVE_CANCEL đóng ngay, chưa có yêu cầu đóng→người khác duyệt; không mô tả là workflow hủy đã hoàn chỉnh.

### 9.3. Giá và ưu đãi

Xét khách cụ thể, nhóm, địa điểm/kênh, lượng và hiệu lực; mặc định giá SKU nếu không rule. Cùng tầng ưu tiên xét ngưỡng lượng/priority. Nonstackable lấy ưu tiên; stackable tính nối tiếp trên giá còn lại, không cộng phần trăm đơn giản. Máy chủ chụp rule vào phiếu. SALE_ORDER có giá policy không tự sửa thấp tùy ý; POS có giá tay và ngoại lệ. Giá vốn/lợi nhuận là quyền riêng.

### 9.4. Giao/COD thủ công

Từ POS/ISSUE đã ghi sổ: tạo đợt, hãng, tracking, COD, phí, note. Trạng thái chuẩn bị/đang giao/giao phần/đã giao/thất bại; chưa API hãng. Đối soát nhập tiền trước phí và phí thực tế; tạo thu/phân bổ AR và chi tương ứng.

Hạn chế: kiểm COD từng đợt chưa kiểm tổng gán nhiều đợt; đợt chưa quản lý dòng/lượng thực giao. Người vận hành đối chiếu thủ công tổng COD/biên nhận, không suy lượng giao từ trạng thái giao phần.

## 10. Mua hàng

Nhập trực tiếp: NCC/kho → hàng/UOM/lượng/giá/VAT/lô/serial → số thực trả → nháp → gửi → duyệt → ghi sổ. Tồn/vốn tăng, AP ghi nhận, trả ngay được phân bổ. Chờ duyệt chưa có hàng để bán.

Popup nhập mua bỏ phí vốn hóa, phân bổ phí, phí chưa biết và giảm dòng. Không dùng API legacy làm cách nhập thường ngày.

Theo đơn: đề nghị mua nếu cần → duyệt đơn mua → nhận nhiều lần → thanh toán. Nhập gắn dòng đơn gốc, cập nhật tiến độ; chênh lượng/giá cần quyền xác nhận sai lệch. Không xác nhận lượng chưa thực nhận. Khi không nhận tiếp, đóng phần còn lại, giữ phiếu đã nhận.

Trả mua từ phiếu nhập gốc, không vượt lượng nhận chưa trả; xuất hàng và điều chỉnh nợ theo xử lý source. Vòng đời tiền NCC hoàn/đối trừ chưa đầy đủ, kế toán đối chiếu riêng; không coi phiếu thu chung là quy trình hoàn trả mua hoàn chỉnh.

## 11. Kho

Kho & Tồn hiển thị lượng / cách ly / khả dụng / giá vốn theo quyền. Available tổng hiện qty−quarantine, chưa loại lô quá hạn; backend vẫn kiểm lô/serial khi post nên bảng tổng có hàng chưa chắc bán được.

Vốn bình quân theo kho/SKU; nhập cập nhật qty/value, xuất dùng bình quân và ghi movement/segments. Chưa có điều chỉnh phí mua biết muộn sau bán. Nhập/xuất khác cần lý do/quyền/duyệt phù hợp, không thay nhập mua để tránh AP.

Chuyển: đề nghị → duyệt → xuất nguồn → transit → nhận đích từng phần → complete khi hết. Dispatch giảm nguồn, chưa tăng đích; nhận không vượt transit còn. Hỏng vào cách ly, ghi thiếu/hỏng. UI chọn tình trạng chung mỗi lần nhận, chưa chi tiết tốt/hỏng cùng SKU. Phần thiếu chưa tự kết thúc mất mát; còn transit để xử lý. Chưa API giải phóng/tiêu hủy cách ly đầy đủ.

Kiểm kê: chọn kho/SKU → nháp → bắt đầu đếm → thực đếm → kết thúc gửi duyệt → duyệt chênh. Khóa SKU đang đếm trong kho đó, kể cả chờ duyệt; SKU/kho khác tiếp tục. Hiện từ chối SKU có quarantine, cần kế hoạch riêng, không biến cách ly thành tồn bán.

## 12. Trả, đổi và bảo hành

Tìm số phiếu gốc → dòng/số còn/thời hạn/serial → kho nhận → trả → duyệt → nhập tồn và giảm nợ → hoàn phần tiền hợp lệ. Bắt buộc nguồn trong hệ thống, cho nhận khác địa điểm; lưu nguồn và kho nhận. Serial phải đúng đã bán. Dùng policy snapshot phiếu gốc.

GOOD được nhận; OPENED cần allowOpened; USED/FAULTY hiện chuyển bảo hành, chưa cấu hình trả hàng đã dùng. Quá hạn trả được đánh dấu cho người duyệt kiểm tra. Giảm AR chưa thu trước, không hoàn phần khách chưa trả. Phương thức hoàn theo gốc hoặc khác có lý do; không vượt phần còn được hoàn. Hoàn cọc từ đơn CLOSED dựa tiền chưa phân bổ.

Đổi: phiếu trả + đơn bán mới, có tham chiếu nhưng chưa tự đối trừ tiền/tín dụng. Không suy đã thanh toán từ exchangeReturnId.

Bảo hành: RECEIVED→PROCESSING→READY→HANDED_OVER; lưu dòng gốc/kho/qty/serial/tình trạng. Form còn ID dòng gốc, chưa picker thân thiện; không tự chuyển tồn. Chặn serial đang bảo hành trùng; tổng lượng cộng dồn hàng không serial chưa đầy đủ, phải đối chiếu thủ công.

## 13. CRM và hoa hồng

CRM dùng master chung, xem history theo quyền/scope, note≤4000, dueAt tùy chọn, đánh dấu done. Chưa owner/result/completedAt chi tiết hay SMS/email tự động.

Hoa hồng nhập ORDER hoặc MONTHLY_ADDITION chỉ phần bổ sung để tránh trùng; ADJUSTMENT cần reason, có thể âm. Theo cộng tác viên/kỳ, chi không vượt còn được chi; chưa tự tính tỷ lệ doanh thu.

## 14. Thu chi và công nợ

Phiếu thu/chi có địa điểm, đối tượng nếu có, số thực tiền, method/reference/note, có thể gắn chứng từ. Phân bổ riêng: tiền còn dư→nợ cùng đối tượng, không vượt dư/nợ và trong scope. Thu trước chưa phân bổ không tự tất toán nợ.

AR từ bán ghi sổ, AP từ nhập; nợ đầu kỳ bằng phiếu. Hạn theo ngày công nợ snapshot partner; không chặn bán vượt hạn/quá hạn. Chưa đảo thu chi; sai tiền báo quản trị, không xóa/ghi tiền giả.

Danh sách finance/movements hiện max 1000; export dùng các nguồn này cũng giới hạn. Chưa xem như báo cáo đầy đủ mọi quy mô. Hoàn NCC, exchange credit và báo cáo nợ nâng cao cần làm tiếp.

## 15. Import, tổng quan và báo cáo

XLSX/CSV UTF-8 nhập hàng/đối tác/tồn/nợ đầu kỳ; một sheet đầu, không công thức, ≤2MB/500 dòng. Preview→sửa lỗi→commit. Preview không tạo master/tồn; commit master tạo hồ sơ, commit opening tạo DRAFT cần gửi/duyệt. Không nhập lại đầu kỳ gây nhân đôi. Preview chưa phủ hết validation commit.

Tổng quan 6 tab theo quyền, không thay Trang chủ. Một số tổng lấy dữ liệu giới hạn: care 20, finance 1000; chưa chứng minh đầy đủ với dữ liệu lớn. Báo cáo ngày theo API bán/mua/tồn hiện có, CSV và in trình duyệt; không pháp định/eInvoice. Export không mặc nhiên trùng bộ lọc UI.

## 16. Quản trị core và module

Người dùng: quản lý tài khoản/trạng thái, gán lại nhiều role. Phân quyền: list STT/mã/tên, thêm→mã/tên/lý do→checkbox/chọn hoặc bỏ tất cả→lưu. Userroles ở Người dùng; scope kho quản trị riêng. Revision bảo vệ cập nhật; không đổi role/scope của chính mình, role hệ thống bảo vệ.

Core quản lý tổ chức, policy/resource, nhật ký, tệp, settings, job/outbox/webhook theo grant; không phải giao dịch kho.

Module: bật/tắt đã cài, không tắt core hoặc phụ thuộc còn dùng. API chặn khi tắt kể cả admin; không xóa data. Import source bằng công cụ, build/restart, chưa upload trên web; core đích cần API 1.1.0/import contract 2. Gói không chuyển database.

## 17. Nghiệm thu và mở rộng

Checklist vận hành: địa điểm/kho đúng, master không trùng, nhập đã duyệt, tồn không âm, tiền/nợ khớp, trả có nguồn, scope đúng, backup/log. Ví dụ nhập 2 thùng× 24 cái rồi bán 3 cái thì còn 45 cái khi không có giao dịch khác.

Các gap trong SYSTEM_AUDIT_DEV_BACKLOG_2026-10-09.md / DEV_TASKS_2026-10-09.json: tổ hợp variant; sửa SKU/nháp; giữ tồn; yêu cầu đóng/hủy; giao dòng/tổng COD; đổi tiền/đảo thu chi; warranty; quarantine/mất hàng; paginated report/export; import parity; CI/quyền. Không coi các mục đã hoàn thành.

SRS_HIEN_HANH.md đặc tả kỹ thuật và AT; RUNBOOK_SU_DUNG.md hướng dẫn thao tác. BA này không thay đổi code/nghiệp vụ.

## Cập nhật local 12/10/2026 — CRM

CRM bổ sung Khách hàng tiềm năng và Cơ hội, cùng Khách hàng dùng Customer Master. Luồng, trường và quy tắc chuyển đổi theo [đặc tả CRM local](CRM_PIPELINE_LOCAL.md). Đây là phần mở rộng sau snapshot09/10; không xác nhận server đã cập nhật.
