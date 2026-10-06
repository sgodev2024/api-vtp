# Đề xuất phân quyền theo module — đã duyệt

Ngày: 06/10/2026. Trạng thái: ĐÃ DUYỆT; triển khai trên local.
Phạm vi: AICRM hiện tại, tái sử dụng tài khoản, vai trò, chính sách và nhật ký của Java Core. Phương án đã được người dùng duyệt. Tiến độ, hướng dẫn và giới hạn triển khai thực tế được ghi tại MODULE_PERMISSION_LOCAL.md.

## 1. Hiện trạng và mục tiêu

RetailAccess đang kiểm quyền trên resource aicrm-retail với READ, SELL, PURCHASE, STOCK, CRM, FINANCE, REPORT, APPROVE, SETUP, COST, EXPORT, ALL_BRANCHES. Core đã có vai trò/chính sách ALLOW–DENY, mặc định từ chối, DENY ưu tiên, cache theo permission revision. Kho/chi nhánh được giới hạn bằng retail.access_scope. Menu lấy quyền từ RetailModule.

Quyền READ đang dùng chung cho nhiều màn và danh sách chứng từ; SETUP gom sửa nhiều danh mục; APPROVE và EXPORT chưa chia theo nghiệp vụ. Một người được xem màn bán chưa có nghĩa chỉ được xem chứng từ bán. Cần kiểm cả danh sách, chi tiết, API dùng chung, báo cáo, export và dữ liệu trả về, không chỉ ẩn menu.

Mục tiêu: quản trị viên chọn vai trò → đánh dấu các chức năng và thao tác được phép → chọn chi nhánh/kho → xem trước quyền hiệu lực → lưu. Nhân viên chỉ thấy đúng công việc, dữ liệu và nút được cấp.

## 2. Quy tắc chung đề xuất

- Mỗi quyền = chức năng con + thao tác. Menu cha hiện khi có ít nhất một chức năng con được xem. Quyền Xem không mặc định cấp Thêm, Sửa, Duyệt hoặc Xuất dữ liệu.
- Không cấp quyền thì từ chối. Nhiều vai trò cộng quyền ALLOW; DENY rõ ràng ưu tiên. Giao diện hiển thị nguồn quyền và xung đột.
- Quyền thao tác và phạm vi dữ liệu là hai điều kiện độc lập, phải đồng thời thỏa mãn. Tất cả truy vấn có tenant; không xuyên công ty.
- Thêm/Sửa/Gửi duyệt/Duyệt bắt buộc có Xem. UI tự đánh dấu quyền phụ thuộc, backend từ chối cấu hình thiếu phụ thuộc. Xuất dữ liệu bắt buộc có Xem dữ liệu tương ứng.
- Đã ghi sổ không sửa/xóa trực tiếp; dùng trả hàng, điều chỉnh hoặc hủy theo quy trình. Quyền Sửa chỉ dùng khi chức năng và trạng thái hiện hỗ trợ; không tự bổ sung khả năng sửa nháp trong lần phân quyền này.
- Không xóa chứng từ đã phát sinh. Danh mục đã tham chiếu chỉ ngừng hoạt động; không thêm nút xóa hàng loạt.
- Duyệt, từ chối, hoàn tiền và hủy có lý do, người thực hiện, thời điểm và phiên bản. Người tạo không tự duyệt phiếu của mình, kể cả chủ doanh nghiệp; không đổi quy tắc này.
- Không thay luồng duyệt: bán thường theo chính sách duyệt sẵn, ngoại lệ mới xin duyệt. Bán công nợ theo quyền bán hiện hành, không bổ sung chặn hạn mức hoặc duyệt riêng vì còn nợ.
- Quyền xem giá vốn/lợi nhuận là riêng. Không có quyền thì API bỏ dữ liệu, UI và export không hiển thị; không chỉ che bằng CSS. Giá mua được cấp riêng theo chức năng mua, không mặc định cho sale/thủ kho.
- Thu hồi quyền có hiệu lực từ yêu cầu API tiếp theo sau khi lưu và tăng revision. Đang mở biểu mẫu phải kiểm lại khi ghi; không tiếp tục bằng quyền cũ. Tác vụ export/import đang chờ phải kiểm lại trước thực thi và tải kết quả.

## 3. Danh mục chức năng và thao tác

Các thao tác chỉ áp dụng nếu chức năng hiện có hỗ trợ. Xác nhận/Ghi nhận thực hiện chuyển trạng thái đúng nghiệp vụ, không thay quyền Duyệt.

| Module / chức năng | Quyền đề xuất |
|---|---|
| Trang chủ | Xem Trang chủ Core; mỗi widget/dữ liệu vẫn theo quyền Core hiện có |
| Tổng quan | Xem riêng tab Bán hàng, Mua hàng, Kho, Sản phẩm, CRM, Kế toán; chỉ số theo phạm vi và quyền dữ liệu nguồn |
| Bán hàng / Bán tại quầy | Xem, Lập phiếu, Xác nhận bán; Ghi thu ngay tại POS cấp riêng; Yêu cầu hủy; In phiếu; Xuất dữ liệu |
| Bán hàng / Báo giá và đơn bán | Xem, Tạo, Sửa khi được hỗ trợ, Gửi/Xác nhận, Duyệt ngoại lệ, Từ chối, Yêu cầu hủy/đóng phần còn, Duyệt hủy, In, Xuất dữ liệu |
| Bán hàng / Khách hàng | Xem hồ sơ khách, Thêm nhanh, Sửa thông tin liên hệ; không mặc định đổi vai trò hoặc thông tin ngân hàng |
| Bán hàng / Giao hàng | Xem, Tạo giao hàng, Cập nhật trạng thái; thuộc luồng đơn bán, không thêm module mới |
| Mua hàng / Chứng từ mua | Xem, Tạo đề nghị/đơn mua, Gửi duyệt, Duyệt/Từ chối mua, Yêu cầu hủy/đóng, Duyệt hủy, In, Xuất dữ liệu |
| Mua hàng / Nhập mua | Xem, Tạo nhập, Gửi duyệt, Duyệt nhập, Xác nhận chênh lệch thừa/thiếu/giá; thực nhận và ghi tồn dùng quyền nhận kho |
| Mua hàng / Trả mua | Xem, Tạo, Gửi duyệt, Duyệt; thực xuất dùng quyền xuất kho; không tự hoàn tiền |
| Mua hàng / Nhà cung cấp | Xem, Thêm NCC, Sửa thông tin liên hệ/công ty; Xem/Sửa thanh toán cấp riêng; Ngừng vai trò NCC qua master |
| Kho / Kho & Tồn | Xem tồn, Xem lịch sử nhập xuất, Xem lô/serial, Tạo nhập/xuất khác, Gửi duyệt, Duyệt nhập/xuất; Xác nhận nhận hàng, Xác nhận xuất hàng; In, Xuất dữ liệu |
| Kho / Kiểm kê, điều chỉnh | Xem, Lập kiểm kê, Nhập số đếm, Gửi duyệt, Duyệt chênh lệch, Kết thúc kiểm kê; khóa sản phẩm đang đếm theo BA |
| Kho / Chuyển kho | Xem, Đề nghị, Gửi duyệt, Duyệt/Từ chối, Xuất kho nguồn, Nhận kho đích, Xác nhận thiếu/hỏng, Yêu cầu hủy, Duyệt hủy, In, Xuất dữ liệu |
| Kho / Cấu hình kho | Xem, Sửa cài đặt tồn kho; tách khỏi quyền thao tác kho |
| Sản phẩm / Danh sách | Xem, Thêm, Sửa, Ngừng hoạt động, Nhập Excel, Xuất dữ liệu; Xem/Sửa giá bán cấp riêng |
| Sản phẩm / Nhóm sản phẩm | Xem, Thêm, Sửa, Ngừng hoạt động |
| Sản phẩm / Thương hiệu | Xem, Thêm, Sửa, Ngừng hoạt động |
| Sản phẩm / Đơn vị tính | Xem, Thêm, Sửa, Ngừng hoạt động |
| Sản phẩm / Quy đổi đơn vị | Xem, Thêm/Sửa, Ngừng áp dụng theo ràng buộc giao dịch |
| Sản phẩm / Thuộc tính | Xem, Thêm/Sửa thuộc tính và giá trị, Ngừng hoạt động |
| Khách hàng / Đối tác / Master | Xem theo vai trò CUSTOMER/SUPPLIER/COLLABORATOR được cấp, Thêm, Sửa liên hệ/công ty, Quản lý vai trò, Ngừng từng vai trò, Nhập Excel, Xuất dữ liệu |
| CRM / Khách hàng | Xem hồ sơ, Thêm nhanh, Sửa liên hệ; Xem lịch sử mua, Thêm ghi chú, Tạo/Sửa nhắc chăm sóc, Hoàn thành nhắc |
| Kế toán / Thu chi | Xem thu/chi riêng, Lập thu, Lập chi, Gửi duyệt, Duyệt chi/hoàn tiền, Phân bổ thanh toán, Yêu cầu hủy, Duyệt hủy, In, Xuất dữ liệu |
| Kế toán / Công nợ | Xem phải thu/phải trả riêng, Phân bổ, Xem/Ghi công nợ đầu kỳ, Xuất dữ liệu; không cho sửa số dư trực tiếp |
| Kế toán / Hoa hồng | Xem, Ghi khoản theo đơn/bổ sung tháng, Duyệt theo luồng hỗ trợ; không tính trùng khoản bổ sung |
| Vận hành / Đổi trả | Xem giao dịch gốc theo phạm vi cần thiết, Lập trả, Gửi duyệt, Duyệt trả; Nhận hàng trả, Hoàn tiền, Duyệt hoàn tiền là quyền riêng |
| Vận hành / Bảo hành | Xem, Tiếp nhận, Cập nhật xử lý; không mặc định hoàn tiền hay sửa tồn |
| Vận hành / Cần duyệt | Xem danh sách thuộc nhóm được duyệt; Duyệt/Từ chối theo đúng loại phiếu, không có quyền duyệt tất cả ngầm định |
| Vận hành / Thiết lập ban đầu | Xem hướng dẫn, Quản lý chi nhánh/kho, Nhập thử danh mục; ghi dữ liệu cần quyền chức năng đích và quyền nhập; tồn/công nợ đầu kỳ cần quyền riêng |
| Báo cáo | Xem/Xuất riêng Bán hàng, Mua hàng, Kho, Sản phẩm, CRM, Kế toán; Giá vốn/Lợi nhuận riêng |
| Quản trị hệ thống | Giữ các quyền Core; tách Quản lý người dùng, Vai trò/quyền, Phạm vi dữ liệu, Xem nhật ký; quản trị kỹ thuật không mặc định được xem số liệu kinh doanh |

Không dùng quyền In để xuất Excel/CSV. Mỗi API xuất/nhập phải xác định loại dữ liệu; export chứng từ hỗn hợp chỉ chứa các loại được xem và xuất. Các chức năng hoa hồng/kiểm kê/giao hàng nằm trong luồng hiện tại, không tự thêm menu mới.

## 4. Customer Master và dữ liệu dùng chung

Master vẫn dùng một ID. Quyền truy cập từ Bán hàng, Mua hàng hoặc CRM là các quyền được phép trên cùng master, không yêu cầu mở menu master độc lập.

- Sale được chọn khách và tạo nhanh khách nếu có quyền tương ứng; không được tạo NCC hoặc đổi vai trò bằng payload.
- Mua hàng được chọn/tạo NCC; không tự đổi vai trò CUSTOMER/CTV. Ngân hàng/tài khoản chỉ cho người có quyền thanh toán đối tác.
- CSKH được xem liên hệ, lịch sử mua theo chi nhánh và ghi chú chăm sóc; không mặc định được xem công nợ/giá vốn/tài khoản ngân hàng.
- Danh mục sản phẩm/đối tác chung toàn công ty. Bộ chọn trả dữ liệu tối thiểu phục vụ giao dịch: ID, mã, tên, ĐVT/biến thể, liên hệ/địa chỉ cần thiết; không cần quyền quản lý danh mục. Xem/sửa hồ sơ đầy đủ và export cần quyền riêng.
- Hồ sơ nhiều vai trò: người có quyền khách chỉ thấy góc nhìn CUSTOMER; quyền NCC không mở lịch sử bán. Ngừng một vai trò không làm mất vai trò khác.
- Master chung không kéo theo quyền lịch sử toàn công ty. Số dư công nợ toàn công ty, nếu được xem, cần quyền tổng hợp công ty riêng; không mở chi tiết chứng từ chi nhánh ngoài phạm vi.

## 5. Phạm vi dữ liệu

| Đối tượng | Quy tắc |
|---|---|
| Chứng từ, tồn, thu chi | Trong tenant và tập kho được giao; thu chi không có kho phải gắn chi nhánh và kiểm phạm vi chi nhánh |
| Dashboard/báo cáo | Tính lại từ dữ liệu được phép; không trả tổng toàn công ty rồi lọc trên frontend |
| Danh mục chung | Bộ chọn dùng chung; quyền master quyết định trường đầy đủ và thao tác sửa |
| Chuyển kho | Nguồn chỉ xuất khi có quyền tại nguồn; đích chỉ nhận khi có quyền tại đích. Được thấy phiếu liên quan kho mình và thông tin đầu còn lại tối thiểu, không mở toàn bộ dữ liệu kho kia |
| Trả khác chi nhánh | Nhận trả tại kho được giao; tra giao dịch gốc theo số phiếu/khách/serial, chỉ trả dữ liệu tối thiểu để kiểm điều kiện trả; không cho duyệt hoặc hoàn tiền ngoài quyền |
| Toàn công ty | Chỉ cấp rõ quyền phạm vi toàn công ty; cấp quyền module không tự cấp toàn bộ kho |

Giai đoạn này không thêm phạm vi “chỉ phiếu tôi tạo” cho giao dịch nghiệp vụ, để tránh phức tạp. Nếu cần sau sẽ thiết kế riêng. Trường người quản lý khách chưa mặc định dùng để giới hạn truy cập khách.

## 6. Vai trò mẫu để áp dụng nhanh

Các mẫu là điểm khởi đầu, quản trị viên chỉnh được. Không tự áp vào tài khoản đang dùng trước khi duyệt mapping.

| Vai trò | Cấp mặc định | Không cấp mặc định |
|---|---|---|
| Chủ doanh nghiệp | Mọi module kinh doanh, duyệt, giá vốn/lợi nhuận, tổng hợp công ty, quản trị người dùng/quyền | Tự duyệt phiếu mình; truy cập tenant khác |
| Quản lý chi nhánh | Bán/mua/kho/CRM trong phạm vi; duyệt bán/mua/kho, báo cáo chi nhánh | Quản trị quyền, toàn công ty, ngân hàng, duyệt hoàn tiền, giá vốn/lợi nhuận |
| Nhân viên bán hàng | POS/đơn bán, chọn/thêm nhanh khách, lịch sử bán phạm vi, in phiếu, giao hàng | Duyệt, giá mua/vốn, Excel hàng loạt, thu chi độc lập, sửa master toàn diện |
| Thu ngân | Tra bán, thu tiền POS/phiếu thu, phân bổ phải thu trong phạm vi, in biên nhận | Lập chi/hoàn tiền, duyệt, điều chỉnh kho, toàn bộ phải trả |
| Thủ kho | Tồn, lô/serial, thực nhận/xuất theo phiếu hợp lệ, chuyển/kiểm kê, chọn sản phẩm | Duyệt phiếu, giá mua/vốn, thu chi, sửa sản phẩm |
| Nhân viên mua hàng | Đề nghị/đơn/nhập/trả mua, xem giá mua, chọn/thêm/sửa liên hệ NCC | Duyệt, thực xuất/nhận nếu chưa cấp thêm quyền kho, thanh toán NCC, giá vốn |
| CSKH | CRM, liên hệ khách, lịch sử mua trong phạm vi, ghi chú/nhắc/bảo hành | Duyệt, tài chính, thay vai trò đối tác, export hàng loạt |
| Kế toán | Thu/chi, phải thu/phải trả, phân bổ, thông tin thanh toán, giá vốn, báo cáo kế toán phạm vi | Duyệt phiếu mình, quyền kho, quản trị người dùng; duyệt chi/hoàn tiền phải cấp riêng |
| Quản trị kỹ thuật | Quản trị Core được giao | Không tự cấp dữ liệu kinh doanh; không tự nâng quyền bản thân |

Sale muốn thu tiền tại quầy phải được cấp thêm Ghi thu POS, có thể gán đồng thời vai trò Thu ngân. Khả năng bán và thu ngay vẫn được giữ, nhưng không mặc định mở toàn bộ Kế toán.

## 7. Màn cấu hình quyền đề xuất

Tại Quản trị hệ thống → Vai trò & phân quyền, mở chi tiết vai trò:
1. Thông tin: mã, tên, mô tả, đang hoạt động.
2. Quyền module: cây module/chức năng, các cột Xem/Thêm/Sửa/Gửi–Xác nhận/Duyệt/Hủy/In/Nhập/Xuất. Các quyền đặc thù hiển thị tại đúng chức năng; ô không áp dụng không chọn được. Chọn tất cả chỉ trong chức năng, không chọn mọi quyền toàn hệ thống vô tình.
3. Phạm vi: tại hồ sơ người dùng chọn chi nhánh và kho; toàn công ty là lựa chọn riêng có quyền cấp. Vai trò mô tả thao tác, phạm vi gắn người dùng để tránh nhân bản vai trò theo từng kho.
4. Xem trước: chọn người dùng, hiển thị quyền hiệu lực từ các vai trò, quyền bị DENY, kho được giao, menu và dữ liệu nhạy cảm được xem.
5. Lưu: liệt kê thay đổi trước/sau, lý do; kiểm quyền người cấp và chống tự nâng quyền; tăng revision, ghi audit.

Không bổ sung cấp quyền riêng trực tiếp cho từng người trong giai đoạn đầu; dùng nhiều vai trò để giải quyết ngoại lệ. Không đặt giới hạn tiền phê duyệt mới trong lần này.

## 8. Thiết kế kỹ thuật để triển khai sau duyệt

Tái sử dụng PermissionService/Core, không xây engine quyền mới. Đăng ký resource theo chức năng, ví dụ aicrm-sales-order, aicrm-sales-pos, aicrm-purchase-receipt, aicrm-stock-transfer, aicrm-product, aicrm-partner-customer, aicrm-partner-supplier, aicrm-finance-payment, aicrm-report-sales. Action dùng VIEW/CREATE/UPDATE/SUBMIT/APPROVE/REJECT/REQUEST_CANCEL/APPROVE_CANCEL/PRINT/IMPORT/EXPORT; quyền đặc thù như RECEIVE, DISPATCH, VIEW_COST, COLLECT_POS, VIEW_BANK, UPDATE_BANK đăng ký rõ.

- API /documents kiểm type thực từ database cho chi tiết/action; không tin type/module frontend gửi. List rỗng type phải lọc theo tập loại phiếu được xem, count cũng cùng bộ lọc.
- /catalog trả lookup phục vụ nghiệp vụ theo quyền và bỏ trường nhạy cảm; API quản lý danh mục kiểm đúng resource. /warehouse-config phân kind cho sản phẩm hoặc cấu hình kho.
- /partners kiểm vai trò, trường được sửa và quyền đổi vai trò; quick-create chỉ CUSTOMER. Lịch sử/công nợ/trường ngân hàng kiểm riêng.
- /finance tách read model thu chi/công nợ, phải thu/phải trả. /summary và /reports lọc đúng nguồn quyền. /export kiểm kind và cột; /imports kiểm quyền xem trước và quyền ghi từng loại.
- Duyệt nhập mua cần APPROVE trên nhập mua; không bắt buộc người duyệt đồng thời có quyền tạo nhập hoặc quyền thủ kho. Một thao tác duyệt có ghi tồn là lệnh workflow đã được duyệt; không gọi thêm quyền người tạo trong đường post nội bộ. Thực nhận/xuất độc lập phải có RECEIVE/DISPATCH và phiếu nguồn hợp lệ.
- Idempotency, version, audit và giao dịch database giữ nguyên. In qua API/preview theo quyền, không chỉ khóa nút; không hứa ngăn người dùng chụp/lưu nội dung đã được quyền xem.
- Điều hướng và frontend lấy capability từ backend, không tự suy vai trò theo tên. Truy cập URL trực tiếp/API vẫn bị từ chối; trang thiếu quyền có thông báo dễ hiểu.

## 9. Chuyển đổi quyền hiện có

Không tự mở rộng READ thành Xem mọi module, SETUP thành quản trị quyền hoặc APPROVE thành Duyệt mọi loại. Lập bảng so sánh từng tài khoản/vai trò đang có: quyền cũ → quyền mới → chi nhánh/kho; người quản trị duyệt trước khi chạy migration. Tài khoản chủ được xác định rõ; giữ ít nhất một người quản lý phân quyền, và bố trí người duyệt độc lập.

Quyền mới là nguồn quyết định sau chuyển đổi. Không dùng fallback “quyền cũ OR quyền mới” vì sẽ mở lại quyền đã thu hồi. Chuyển đổi giao diện/API/mapping cùng phiên bản, thử trên database tạm; có backup và rollback code + quyền, không rollback chứng từ đã ghi. Khách có custom vai trò phải được review riêng.

## 10. Task và tiêu chí nghiệm thu

| Task | Phụ trách đề xuất | Kết quả |
|---|---|---|
| P01 Chốt catalog quyền và mapping chứng từ/API | Người hướng dẫn | Danh sách resource/action, phụ thuộc, bảng tài khoản cũ/mới để duyệt |
| P02 UI ma trận vai trò, xem trước | Intern 1 | Cây module, quyền đặc thù, phụ thuộc, tổng quyền; không tự áp quyền |
| P03 Backend catalog/capabilities/menu | Intern 2, người hướng dẫn review | Đăng ký resource, API trả capability, kiểm revision và Core integration |
| P04 API nghiệp vụ và dữ liệu nhạy cảm | Người hướng dẫn chính; Intern 2 hỗ trợ | Gate list/detail/actions/catalog/master/finance/report/import/export |
| P05 UI nghiệp vụ theo capability | Intern 3 | Ẩn nút/tab đúng quyền, chặn URL, giữ form khi 403; không dùng UI thay backend |
| P06 Scope đặc biệt và migration | Người hướng dẫn chính | Chuyển kho, trả khác chi nhánh, tổng công ty, mapping và rollback |
| P07 Kiểm thử tích hợp | 3 intern, người hướng dẫn nghiệm thu | Bộ test API+UI, số dư/tồn không đổi khi bị từ chối, bằng chứng |

Thứ tự: P01 duyệt → P02/P03 → P04/P05 → P06 → P07 → duyệt kết quả local → triển khai theo yêu cầu. Intern không tự quyết thay đổi luồng duyệt hoặc phân quyền wildcard.

Nghiệm thu bắt buộc:
- Tài khoản chỉ được bán không đọc được chứng từ mua qua list/detail/export/URL; list/count/report không lộ dữ liệu loại khác.
- VIEW không tạo/sửa/duyệt; CREATE không duyệt; người tạo không tự duyệt; người có quyền duyệt đúng loại duyệt được mà không cần quyền tạo.
- Phạm vi kho/chi nhánh kiểm ở cả đọc và ghi; chuyển kho và trả khác chi nhánh hoạt động theo mục 5.
- Sale tạo khách không tạo NCC/gán vai trò/sửa ngân hàng qua payload; thông tin chung cập nhật một master, lịch sử không vượt phạm vi.
- Giá vốn/lợi nhuận/ngân hàng bị loại khỏi JSON, báo cáo và export khi thiếu quyền; lookup tối thiểu không làm mở menu danh mục.
- Ghi thu POS chỉ được cấp riêng, không mở phải trả/lập chi; hoàn tiền cần đúng quyền và workflow.
- Import thiếu quyền loại đích không ghi dữ liệu; export hỗn hợp không lẫn loại/cột bị cấm.
- Thu hồi quyền khi đang mở biểu mẫu, thử lại idempotency hoặc có export/import đang chờ đều không dùng quyền cũ để thực thi/tải dữ liệu.
- Multi-role ALLOW/DENY, tài khoản không scope, tenant khác, URL trực tiếp và payload giả đều được kiểm tra.
- Chủ/thu ngân/thủ kho/sale/mua hàng/kế toán có thể vận hành các luồng hợp lệ; không phát sinh thiếu quyền do API lookup dùng chung.

## 11. Các điểm xin duyệt

1. Phân quyền tới chức năng con và thao tác, dùng vai trò mẫu tùy chỉnh; quyền chưa cấp mặc định từ chối.
2. Tách duyệt theo loại nghiệp vụ, quyền thu POS/hoàn tiền và dữ liệu nhạy cảm; không tự duyệt phiếu mình.
3. Danh mục dùng chung, lịch sử theo kho/chi nhánh; không thêm owner-only trong giai đoạn này.
4. Mapping quyền cũ theo từng vai trò/tài khoản phải được duyệt, không tự chuyển quyền rộng.

Chỉ sau khi người dùng duyệt đề xuất mới sửa code/database và triển khai local. Commit/push hoặc triển khai server thực hiện theo yêu cầu riêng.
