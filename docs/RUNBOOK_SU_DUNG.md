# Runbook hướng dẫn sử dụng — AICRM

Phiên bản 3.0 — 09/10/2026. Theo source local main ae59355 và thay đổi đợt 3 chưa commit. Phiên bản server chưa xác minh lại. Chỉ hướng dẫn chức năng hiện có; hạn chế được ghi rõ, không thay bằng yêu cầu chưa triển khai. Tham chiếu BA_HIEN_HANH.md và SRS_HIEN_HANH.md.

## 1. Chuẩn bị và đăng nhập

Dùng trình duyệt desktop/mobile và Internet. Local http://localhost:3205/; server đã triển khai trước đây https://erp.aicrm.vn/ — quản trị xác nhận phiên bản server trước áp dụng. Đăng nhập bằng tài khoản được cấp. Admin local admin@core.local, mật khẩu lấy cấu hình riêng, không có mật khẩu mặc định trong tài liệu.

Không thấy menu: kiểm tra vai trò, scope kho và module. Không chia sẻ admin cho nhân viên. Mobile dùng điều hướng app/menu; chưa bán offline. Nếu ô login làm zoom, kiểm tra bản mới/cache và báo dev.

## 2. Thiết lập lần đầu

1. Quản trị vào Vận hành chung → Thiết lập ban đầu nếu wizard chưa tự hiện.
2. Doanh nghiệp & Địa điểm: nhập tên công ty, mã/tên địa điểm, địa chỉ/liên hệ/MST. Sửa mặc định đã có; một cửa hàng không cần thêm chi nhánh giả.
3. Kho chính: mã/tên kho mặc định, địa điểm, loại SALEABLE cho kho bán. QUARANTINE dành cho hàng cách ly.
4. Danh mục: đơn vị, SKU, khách/NCC; có thể nhập file mẫu. Kiểm tra quy đổi/tracking trước nhập tồn.
5. Dữ liệu đầu kỳ: tồn thực và giá trị, phải thu/phải trả theo đối tượng. Nếu không có ghi nhận nội bộ là số 0, không tạo phiếu giả.
6. Mở các phiếu đầu kỳ, gửi rồi duyệt; admin đủ điều kiện tự duyệt, user thường cần người khác.
7. Đối chiếu Kho & Tồn/Công nợ, sau đó Hoàn tất.

Checklist: thông tin công ty đúng, kho đúng, không trùng master, đơn vị/factor đúng, tồn/nợ đã duyệt. Wizard chưa ép đủ kiểm tra; bấm Hoàn tất không có nghĩa đầu kỳ đã ghi sổ.

## 3. Người dùng và quyền

1. Quản trị hệ thống → Phân quyền: danh sách STT/mã/tên vai trò.
2. Thêm vai trò: mã chữ thường bắt đầu bằng chữ, dài 3–100, có số/gạch ngang; tên và lý do.
3. Tích quyền từng chức năng/hành động. Chọn tất cả chỉ khi cần toàn bộ; Bỏ chọn tất cả để làm lại. VIEW không tự bao gồm CREATE/APPROVE.
4. Lưu, mở lại kiểm tra checkbox. Role hệ thống/bảo vệ có thể không sửa.
5. Người dùng → mở user → chọn một/nhiều vai trò → lưu. Không đổi vai trò của chính mình.
6. Ở quản trị phạm vi chọn kho phụ trách và lý do; user một kho không cần ALL_BRANCHES.
7. Đăng nhập user nhân viên thử đúng màn hình/kho. Manager hồ sơ không tự có quyền.

409/phân quyền đã thay đổi: tải lại, kiểm tra thay đổi mới rồi lưu lại; không ghi đè bản cũ. admin@core.local active + platform-admin được tự duyệt, email đơn lẻ không đủ.

## 4. Địa điểm và kho

Vận hành chung → Địa điểm / Chi nhánh → thêm → mã/tên/liên hệ/phụ trách → lưu; tạo kho cùng lúc nếu có quyền. Kho → Danh sách kho → thêm → địa điểm/mã/tên/loại → lưu. Chọn mặc định và cấp scope cho nhân viên.

Ngừng kho: kiểm tồn/giá trị/phiếu chờ/transit/serial/giao/bảo hành; chuyển mặc định trước. Bị chặn thì xử lý dữ liệu còn mở, không xóa SQL. Kho cũ không đổi địa điểm tùy ý; nơi mới tạo kho mới rồi chuyển hàng.

## 5. Sản phẩm

1. Sản phẩm → Đơn vị tính: tạo cái/thùng hoặc đơn vị cần dùng.
2. Danh sách sản phẩm → thêm: mã/tên/barcode, đơn vị cơ sở, tracking, giá/VAT, ngày trả/bảo hành và cho mở bao bì.
3. NONE hàng thường; LOT lô/hạn; SERIAL mỗi chiếc một serial.
4. Quy đổi đơn vị tính: chọn SKU/UOM phụ/factor. 1 thùng=24 cái, 2 thùng=48 cái.
5. Kho → Cấu hình kho: theo dõi mặc định nếu cần. Brand/thuộc tính/UOM ở Sản phẩm.

Biến thể hiện là text, chưa tổ hợp chọn từ thuộc tính. Mỗi hàng khác nhau dùng SKU riêng rõ mã, không coi đã có variant liên kết. Chưa có sửa SKU đầy đủ; báo quản trị/dev khi cần, không sửa SQL. Phiếu cũ giữ tỷ lệ snapshot.

## 6. Khách hàng và NCC

Khách hàng / Đối tác → Khách hàng → thêm; tìm mã/phone trước. Đơn bán có icon+ trong ô Khách hàng, lưu rồi chọn hồ sơ mới. CRM/Bán dùng cùng master.

Mua hàng → Nhà cung cấp → Thêm NCC:

- Thông tin chung: đủ Mã NCC, Tên NCC, Loại đối tượng, Điện thoại, Email, Địa chỉ.
- Bổ sung optional: tên/địa chỉ công ty, MST, liên hệ, quản lý.
- Thanh toán optional: bank/số tài khoản/chủ tài khoản/ngày nợ. Bank cần quyền riêng.

Một người vừa khách vừa NCC: thêm role cho cùng hồ sơ trong Đối tác, không tạo hai master. Ngày công nợ không tự chặn bán quá hạn.

## 7. Nhập mua trực tiếp

1. Mua hàng → Mua hàng → Nhập mua.
2. Kho nhận/NCC; chưa có NCC tạo trước.
3. SKU/UOM/lượng thực nhận/giá/VAT. LOT mã/hạn; SERIAL đúng danh sách/số lượng.
4. Số đã trả/phương thức đúng thực tế; chưa trả 0; kiểm tổng.
5. Lưu nháp hoặc gửi theo nút đang hiện. Chờ duyệt chưa có tồn.
6. Người duyệt: Vận hành chung → Cần duyệt hoặc mở phiếu từ Mua hàng.
7. Kiểm rồi Duyệt/Từ chối với lý do khi yêu cầu; user thường khác người lập.
8. Đã ghi sổ: đối chiếu tồn và AP của NCC.

Popup đã bỏ phí vốn hóa/phân bổ/phí chưa biết và giảm dòng. Phí biết sau chưa có luồng điều chỉnh vốn sau bán; báo kế toán, không nhập thêm hàng giả để tăng vốn.

## 8. Mua theo đơn và trả mua

Đề nghị khi cần→gửi/duyệt→đơn mua từ nguồn→gửi/duyệt→nhập từng lần. Nhập đúng thực nhận, xem tiến độ sau post; chênh lượng/giá cần xác nhận có quyền. Không nhận tiếp thì đóng phần còn lại; không hủy phiếu nhập đã ghi.

Mở phiếu nhập đã ghi sổ→Trả mua→dòng nguồn/lượng/lô/serial thực trả→gửi/duyệt→đối chiếu tồn/nợ. Không vượt đã nhận chưa trả. Tiền NCC hoàn cần kế toán đối chiếu riêng, chưa có lifecycle hoàn/đối trừ đầy đủ.

## 9. POS

1. Bán hàng → Bán tại quầy; chọn kho nếu nhiều kho.
2. Tìm tên/SKU hoặc barcode; chọn hàng, lượng/UOM.
3. Chọn khách cho history/bán chịu; không vãng lai nếu còn nợ.
4. Giá mặc định, chưa có nhập tay; VAT/khuyến mãi/tổng.
5. Thực thu/phương thức; tiền khách đưa vượt tổng xem tiền trả lại, không ghi paid vượt phiếu.
6. Lưu nháp/xác nhận; chỉ coi bán thành công khi ghi sổ. PENDING cần duyệt trước.
7. In phiếu theo quyền và kiểm khổ giấy trình duyệt.

“Tồn khả dụng không đủ”: đúng kho? nhập đã duyệt? lô hết hạn/cách ly/serial/kiểm kê? Tổng tồn không chắc đủ bán. Bổ sung tồn hợp lệ hoặc Hủy nháp & chuyển đặt trước, có khách. Hiện chuyển cả giỏ, chưa tách phần đủ/thiếu. Nháp đã lưu chưa sửa như giỏ soạn; cần hủy/lập lại. Timeout kiểm phiếu trước khi tạo lại.

## 10. Đơn bán và cọc

1. Bán hàng → Đơn bán hàng → Tạo đơn bán hoặc báo giá→đơn từ nguồn.
2. Khách/kho/kênh, tìm hàng xổ xuống; bảng gọn lượng/UOM/giá/VAT.
3. Địa chỉ/ghi chú trong cùng nhóm; không COD/phí khách trả/giảm dòng.
4. Lưu nháp & kiểm tra tiền rồi gửi. Đặt trước không xuất/không reservation.
5. Có hàng mở đơn→Xuất bán→kho/lượng lần này; nhiều kho lập phiếu riêng.
6. Gửi/duyệt theo policy; post kiểm lượng đã xuất/còn trên đơn.
7. Thu tiền ở Thu chi; cọc trước xuất chưa phân bổ, sau có AR mới allocate.

Đơn 10 xuất 4 rồi 6, không tạo đơn 10 mới lần hai. Khách hủy 6 còn thì đóng phần đó có lý do;4 đã xuất chỉ trả khi thực tế trả. Hạn báo giá người bán kiểm trước chuyển, chưa dựa tự chặn.

## 11. Giao/COD thủ công

Mở POS/ISSUE đã ghi sổ→Tạo đợt giao→hãng/tracking/COD/phí/note. Cập nhật theo thực tế, không tự đồng bộ hãng. Đã giao/giao phần: đối soát tiền trước phí và actual fee. COD thu 100000/phí 15000: gross 100000/fee 15000→thu 100000,chi 15000,ròng 85000. Không nhậpgross 85000 rồi trừ phí lần nữa.

Nhiều đợt: kiểm tổng COD≤nợ, hệ thống chưa chặn cộng dồn đầy đủ. Chưa dòng giao lượng, dùng phiếu xuất/biên nhận. FAILED không tự hủy/nhập hoàn; hàng về xử lý đúng nguồn/thực tế.

## 12. Kho, chuyển và kiểm kê

Kho & Tồn xem lượng / cách ly / khả dụng, lô/serial/movement; cost theo quyền. Tổng available chưa loại lô hết hạn, cần xem lô khi bán.

Chuyển kho:

1. Kho → Chuyển kho → thêm kho nguồn/đích khác nhau, SKU/lượng.
2. Gửi→duyệt→Xuất kho nguồn. Hàng transit, chưa vào đích.
3. Kho đích mở phiếu→Nhận kho đích, lượng thực/serial.
4. Tốt hoặc hỏng/cách ly, biên bản thiếu/hỏng→Ghi nhận thực nhận.
5. Nhận tiếp phần còn; không nhận đủ giả nếu thiếu.

UI tình trạng chung lần nhận; tốt/hỏng cùng SKU cần tách lần nhận phù hợp, kiểm qty còn và ghi chú. Phần mất chưa có hoàn tất riêng, báo quản lý để theo dõi, không nhận giả rồi xuất xóa.

Kiểm kê:

1. Kho & Tồn→tạo kiểm kê→kho/SKU cần đếm.
2. Bắt đầu, khóa SKU ở kho đó; hàng khác/kho khác tiếp tục.
3. Số thực đếm→lưu; kiểm đơn vị/lô/serial.
4. Kết thúc/gửi duyệt; vẫn khóa lúc chờ.
5. Duyệt chênh/lý do→đối chiếu tồn/movement.

Có cách ly hiện không kiểm kê SKU theo luồng này; không biến cách ly thành hàng bán. Chưa API giải phóng/tiêu hủy cách ly hoàn chỉnh, báo quản lý/dev.

## 13. Trả/đổi/bảo hành/hoàn

1. Vận hành chung → Đổi trả / Bảo hành hoặc mở phiếu bán đã ghi → Trả / đổi hàng.
2. Tìm đúng số phiếu gốc; không tìm được nguồn chưa dùng luồng chuẩn này.
3. Dòng/lượng còn trả/kho nhận; khác chi nhánh được khi có quyền.
4. Policy snapshot/hạn/serial gốc. GOOD; OPENED khi cho phép; USED/FAULTY dùng bảo hành hiện tại.
5. Gửi/duyệt→đối chiếu tồn nhận/cách ly và AR giảm.
6. Tiền đã thu còn được hoàn: REFUND nguồn hợp lệ/phương thức gốc hoặc khác có reason→gửi/duyệt→đối chiếu chi.

Đổi: trả + bán mới có liên kết nhưng chưa tự đối trừ. Kế toán đối chiếu tiền thực, không tạo thu/chi giả để số dư đẹp. Hoàn cọc dùng đơn đã đóng và số tiền chưa phân bổ hợp lệ.

Warranty: sourceLine / qty / serial/tình trạng/kho, tiếp nhận→xử lý→sẵn sàng→đã giao. Form còn “ID dòng phiếu bán gốc”, nhờ quản trị tra trong chi tiết, không đoán ID. Chưa picker/chuyển tồn tự động và giới hạn hàng không serial đầy đủ; đối chiếu lịch sử/thực nhận.

## 14. Tiền, nợ và hoa hồng

Kế toán → Thu chi→thu/chi→địa điểm/partner/phiếu nếu có/amount thực/method/reference→kiểm→lưu. Tiền khác hàng.

Kế toán → Công nợ→AR/AP/partner→gốc/đã trả/còn/hạn. Allocate chọn tiền dư và nợ cùng đối tượng, không vượt cả hai. Thu chưa phân bổ chưa tất toán.

Nợ 100000 thu 60000 và phân bổ 60000 còn 40000. Thu 120000 chỉ phân bổ 100000,20000 dư, không tăng doanh thu phiếu cũ. Sai phiếu tiền báo quản trị/kế toán; chưa có đảo phiếu tiền, không SQL/tiền âm/phiếu giả.

Commission chọn collaborator/kỳ, nhập theo đơn hoặc bổ sung tháng. Đơn 100000 cuối tháng thêm 20000 thì MONTHLY_ADDITION 20000, không 120000. Điều chỉnh reason; chi≤còn.

## 15. CRM

CRM → Khách hàng→tìm→history/thông tin→note/thời gian hẹn→lưu→done sau xử lý. Sửa hồ sơ cùng master, không tạo khách CRM khác. Nhắc lịch chỉ thời gian/done, chưa owner/result/cuộc gọi đầy đủ hay gửi SMS/email; theo dõi trên màn hình.

## 16. Nhập file và báo cáo

Thiết lập ban đầu/nhập dữ liệu→loại→mẫu→kho đầu kỳ→XLSX/CSV→preview→sửa lỗi→commit. ≤500 dòng/2MB, sheet đầu, không công thức; ngày YYYY-MM-DD, serial ngăn cách bằng |. Nhập riêng loại.

| Loại | Cột mẫu |
|---|---|
| Hàng | code,name,unit,price,tracking,variant,units,returnDays,warrantyDays,allowOpened |
| Đối tác | code,name,kind,phone,email,address,customerGroup |
| Tồn | skuCode,qty,price,lot,expiry,serials |
| Nợ | partnerCode,kind AR/AP,amount |

Dùng mẫu tải từ UI để trường như units đúng định dạng; không tự đoán JSON. Preview chưa bắt hết lỗi khi commit. Commit lỗi kiểm batch/thông báo, không lập lại tay trước biết kết quả. Commit đầu kỳ vẫn DRAFT, cần gửi/duyệt.

Tổng quan chọn 6 tab theo quyền. Báo cáo chọn ngày/xem/CSV; Excel UTF-8, in kiểm khổ giấy. Finance/movements/export một số nguồn tối đa 1000, care 20. Export không mặc nhiên giữ bộ lọc UI; đối chiếu phạm vi/kỳ/số dòng. Chưa pháp định/dữ liệu lớn được kiểm chứng.

## 17. Đầu/cuối ngày

Đầu: user/kho/Internet đúng; nhập chờ duyệt xử lý; hàng hết hạn/cách ly/transit và lịch care.

Cuối: đơn chưa xuất/đặt trước, phiếu chờ, thu/chi/allocation, COD chưa đối soát, trả/refund/warranty, transit thiếu, count chờ, xuất dữ liệu cần lưu. Không ca bán/kiểm tiền trong giai đoạn này.

Admin kiểm log/job/backup môi trường; không chia sẻ password/dump/CSV khách công khai. Nhân viên nghỉ: core đổi role/scope/disabled, lưu reason.

## 18. Xử lý lỗi

| Lỗi | Kiểm tra | Cách xử lý |
|---|---|---|
| Failed to fetch | Backend/proxy/Internet/session | Không spam lưu; tra phiếu; quản trị log |
| Chờ duyệt chưa tồn | Chưa POSTED | Cần duyệt/chi tiết nhập mua |
| Không tick/lưu quyền | Protected/grant/revision | Lý do/reload409; không sửa tự mình |
| Không kho/module | Phạm vi / VIEW / đã cài / đã bật | Quản trị cấp đúng/bật dependency |
| Có tồn vẫn bán lỗi | Expired/quarantine/serial/count | Xem lô/serial/nhập, không nhập giả |
| Mã trùng | SKU/barcode/master | Tìm lại, thêm role không tạo trùng |
| Nhận kho lỗi | Transit còn/serial | Đúng thực nhận, biên bản |
| Không hủy POSTED | Ghi sổ rồi | Trả/hoàn đúng nguồn, báo quản trị |
| Thu rồi nợ còn | Allocation | Allocate cùng partner/scope |
| Save timeout | Có thể đã commit | Kiểm tra phiếu, thử lại cùng key |

Báo lỗi gồm thời gian/môi trường/email người dùng/module/số phiếu/thao tác/lỗi/ảnh che dữ liệu. Không password/token/.env. Chức năng chưa hoàn chỉnh như đảo tiền/variant/quarantine ghi yêu cầu riêng.

## 19. Quản trị kỹ thuật

AICRM_LOCAL.md hướng dẫn chạy/backup; BUSINESS_MODULES_PHASE3.md hướng dẫn import source, SRS phụ lục deps. Nhân viên không chạy SQL/module-manager.

Tắt module không xóa data, kiểmdependency. Core đích tương thích API/import contract, dryrun/backup/build/restart rồi kiểm tra lại. Gói source không mang DB; importcode chưa có nghĩa chuyển data xong. Chưa tự tách DB thuê bao.

## 20. Hướng dẫn quản trị core

Người dùng: tạo email/tên hiển thị/mật khẩu/role, tổ chức nếu cần; khóa tài khoản khi ngừng dùng. Đặt lại mật khẩu qua chức năng core và bàn giao riêng, không ghi vào ghi chú/audit. Hồ sơ người dùng dùng cho thông tin cá nhân/đổi mật khẩu; chính sách phụ thuộc môi trường.

Tổ chức: tạo mã/tên/tổ chức cha cho cấu trúc nhân sự. Không dùng Tổ chức để thay Địa điểm/Chi nhánh hoặc tạo kho.

Trang chủ: xem thông tin nền tảng. Hoạt động/Nhật ký: tra thời gian, user, action, resource, kết quả/correlation; khi lỗi báo kỹ thuật với ID tương quan. Retry job/replay outbox chỉ người quản trị hiểu tác động, tránh chạy lại nhiều lần.

Tài nguyên: khai báo resource owner/storage/schema theo đội kỹ thuật; không sửa schema để thay phiếu bán hoặc tồn. Tệp: tải lên theo chức năng hiện có, đợi xử lý/quét; không sử dụng file cách ly; tải/xóa theo quyền. Cài đặt: xem giá trị hiện hành, ghi nhận trước khi đổi; logo/MFA là cấu hình riêng, không tùy ý đổi ảnh hưởng nhân viên.

Quản lý module: xem dependencies và active dependents; tắt module sẽ mất menu/API nhưng giữ dữ liệu. Không tắt core hoặc gói đang được gói khác dùng. Bật theo phụ thuộc rồi đăng nhập kiểm tra lại. Chưa có upload gói trực tiếp trên màn hình, chuyển đội kỹ thuật thực hiện source import.

Webhook/API kỹ thuật chưa phải kênh bán hay tích hợp vận chuyển hoàn chỉnh; chỉ cấu hình khi có thiết kế/kiểm thử. Khi vận hành nhiều tenant, quản trị deployment có quyền rộng hơn nhân viên công ty; không cấp control-plane cho khách SaaS như quyền bán hàng thông thường.
