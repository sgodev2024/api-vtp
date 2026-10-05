# Phân công task cho intern — AICRM SRS 2.0

Ngày: 05/10/2026. Căn cứ: [SRS](./SRS_HE_THONG_v2.0.md), [72 task cấp tính năng](./TASK_TRIEN_KHAI_v2.0.md), [trường thông tin](./BA_TRUONG_DU_LIEU_v2.0.md).

Tài liệu chia việc thành **80 task nhỏ**: 40 frontend (`IF`), 24 backend (`IB`), 16 kiểm thử/tài liệu (`IQ`). Các task này là phần việc con của backlog T-01…T-72, không thay thế toàn bộ phần việc của người hướng dẫn. Một tính năng chỉ hoàn tất khi các phần việc intern, backend nghiệp vụ, tích hợp và QA cùng đạt.

Người dùng xác nhận đội có **3 intern đều làm backend và frontend**. Dưới đây phân công bằng ký hiệu Intern 1/2/3, chưa có tên và chưa xác định người hướng dẫn/review. Mỗi task nhắm một đầu ra có thể review trong khoảng 2–6 giờ làm việc tập trung, không tính thời gian học, chờ API và sửa theo review. Các khoảng giờ là **gợi ý để chia nhỏ**, không là cam kết tiến độ. Nếu làm thử thấy vượt một ngày, người hướng dẫn tách tiếp task thay giao cả module cho intern.

## 1. Nhóm phân công và người review

| Thành viên | Phạm vi chính | Task triển khai | Task kiểm thử chéo |
|---|---|---|---|
| Intern 1 | Thành phần dùng chung; danh mục, sản phẩm, biến thể, UOM | IF-01…04, IF-11…20; IB-04…13 | IQ-03, IQ-05, IQ-10, IQ-11, IQ-13, IQ-15 — kiểm thiết lập/mua và kho–tiền/dịch vụ của bạn khác |
| Intern 2 | Wizard, NCC, mua hàng, giá, POS và đơn bán | IF-05…10, IF-21…29; IB-01…03, IB-14…16 | IQ-04, IQ-08, IQ-09, IQ-12, IQ-14 — kiểm sản phẩm và các luồng kho/trả/CRM |
| Intern 3 | Kho, chuyển/kiểm kê, kế toán, giao/COD, trả/bảo hành, nhập/báo cáo | IF-30…40; IB-17…24 | IQ-01, IQ-02, IQ-06, IQ-07, IQ-16 — truy vết, fixture, bán/cọc, đồng thời và tổng hợp nghiệm thu |

Tổng phân công: Intern 1 có 30 task (24 triển khai + 6 kiểm); Intern 2 có 26 task (21 triển khai + 5 kiểm); Intern 3 có 24 task (19 triển khai + 5 kiểm). Đếm task không phải đo tải: nhóm Intern 3 có nhiều tích hợp phức tạp, có thể cần điều chỉnh sau lượt đầu. Mỗi người chỉ có **một task đang làm**; ưu tiên hoàn thành cả lát cắt backend–frontend được giao, không chuyển sang nhiều màn còn dở.

IF là loại việc frontend, IB là backend, IQ là kiểm thử, không phải ba vị trí chuyên môn tách biệt. Mỗi intern đều có cả IF và IB. Kiểm thử chéo không thay review thiết kế của người có kinh nghiệm. Người review cần được chỉ định trước khi task ghi dữ liệu/quyền chuyển Ready; chưa có mentor thì làm đọc hiểu, DTO, mockup hoặc thiết kế ca kiểm và giữ phần tích hợp lõi chờ hướng dẫn.

## 2. Công việc người hướng dẫn phải chuẩn bị

Đây là điều kiện bàn giao, không phải câu hỏi xin phép lặp lại. Cột “cổng” dùng trong phụ thuộc các task phía dưới.

| Cổng | Người phụ trách | Đầu ra để intern nhận việc | Liên quan task lớn |
|---|---|---|---|
| G-01 | TL + BA | Chốt field/API/error/version và quy tắc đã duyệt, route và component cần dùng; ví dụ request/response, ca đúng/sai | T-01 |
| G-02 | Mentor FE | Tách vùng dùng chung của workspace thành component/hooks đủ nhỏ, thống nhất tên/thư mục; giữ hành vi hiện tại. Phân người sở hữu từng file, đọc AGENTS và guide Next hiện có | T-02 |
| G-03 | Mentor BE | Migration, entity/DTO contract Product/Attribute/UOM, tenant FK, unique constraints và khung service/controller; dữ liệu test isolated | T-08/09/11/12/13/16 |
| G-04 | Mentor BE | Backend nghiệp vụ cho task đang giao đã có hoặc có hợp đồng rõ: transaction, scope, duyệt, giá, tồn/nợ, chống ghi trùng. Intern chỉ nối UI/DTO/test trong phạm vi ticket | T-03/04/17/19…55 theo tính năng |
| G-05 | OPS + QA | Database/app kiểm thử riêng, tài khoản test theo vai trò, cách reset fixture và build/test; không dùng DB cửa hàng đang vận hành | T-68/69 |
| G-06 | BA/PO + mentor | Chốt quyết định OPEN tương ứng trước kích hoạt: USED, bên chịu/nhận phí, ngày hiệu lực/khóa; giữ hai người duyệt | Các task Conditional trong backlog |

G-04 không có nghĩa phải đợi toàn bộ backend xong. Mentor đánh dấu `G-04: mua`, `G-04: POS`, `G-04: chuyển`… cho đúng slice. UI có thể làm mockup từ G-01, nhưng **không nghiệm thu tích hợp** bằng mock. G-03 cũng được bàn giao từng catalog/API.

### Phần mentor chủ trì, intern không tự quyết

- Thiết kế/chạy migration và backfill trên DB thật; bộ đếm số phiếu; giữ nguyên SKU và snapshot lịch sử.
- Thay quyền, tenant/scope, auth, session, secret và cấu hình môi trường.
- Ghi movement/giá vốn, AP/AR, thu chi/phân bổ/đảo, COD/hoàn/bù tiền và xử lý mất hàng đang chuyển.
- Row lock, transaction, rollback, idempotency và quyết định trạng thái được phép.
- Chốt chính sách chưa được PO xác nhận, release, backup/restore và chuyển dữ liệu khách.

Intern có thể viết DTO/test/đoạn triển khai trong khung mentor giao; mentor review toàn bộ thay đổi ảnh hưởng các phần trên. Không tạo engine duyệt, auth hay tính tiền riêng ở frontend. Source đã có `RetailCommands` và header chống gửi trùng: khảo sát và kế thừa trước bổ sung, không viết một lớp chống trùng khác chỉ vì ticket có từ “idempotency”.

## 3. Bản đồ file để nhận task

Đường dẫn dưới tính từ thư mục `aicrm-java`. Ký hiệu file trong bảng là điểm đọc/sửa dự kiến, không bắt sửa tất cả. File mới phải do mentor chốt tên và vị trí tại G-02/G-03.

| Ký hiệu | File hiện có / vị trí dự kiến |
|---|---|
| F-WS | `frontend/app/retail/workspace.tsx` — màn nghiệp vụ dùng chung |
| F-CSS | `frontend/app/retail/retail.css` |
| F-DASH | `frontend/app/retail/overview-dashboard.tsx` |
| F-UOM | `frontend/app/retail/uom-conversions.tsx` |
| F-CONFIG | `frontend/app/retail/warehouse-config.tsx` |
| F-CORE | `frontend/app/page.tsx` — giao diện Core/login/quản trị |
| F-NEW | Component/module con mới tại `frontend/app/retail/`, tên do mentor chốt; **chưa có** |
| B-CTRL | `backend/src/main/java/vn/aicrm/retail/RetailController.java` |
| B-CAT | `backend/src/main/java/vn/aicrm/retail/RetailCatalog.java` |
| B-DOC | `backend/src/main/java/vn/aicrm/retail/RetailDocuments.java` |
| B-INV | `backend/src/main/java/vn/aicrm/retail/RetailInventory.java` |
| B-FIN | `backend/src/main/java/vn/aicrm/retail/RetailFinance.java` |
| B-OPS | `backend/src/main/java/vn/aicrm/retail/RetailOperations.java` |
| B-IMP | `backend/src/main/java/vn/aicrm/retail/RetailImport.java` |
| B-COM | `backend/src/main/java/vn/aicrm/retail/RetailCommissions.java` |
| B-CMD | `backend/src/main/java/vn/aicrm/retail/RetailCommands.java` |
| B-SEC | `backend/src/main/java/vn/aicrm/retail/RetailAccess.java` — đọc để kế thừa scope, không tự sửa quyền |
| B-NEW | DTO/validator/mapper/service mới trong package retail, mentor chốt cấu trúc; **chưa có** |
| Q-NEW | Ca kiểm mới dưới `backend/src/test/java/vn/aicrm/retail/` hoặc vị trí QA chốt; **chưa có** |
| Q-EXIST | `scripts/retail-smoke.mjs`, `scripts/uom-smoke.mjs` — tham khảo, không chạy trên DB đang dùng vì có tạo dữ liệu |
| DOC | `docs/` — tài liệu/biên bản task; không ghi password/token |

`workspace.tsx` và `page.tsx` hiện chứa nhiều phần dùng chung. Không cho hai intern đồng thời sửa cùng vùng file. Mentor tách component trước hoặc xếp việc lần lượt; việc phân module không đồng nghĩa phân file an toàn. Không thay toàn bộ CSS chung để sửa một màn.

## 4. Ticket frontend — thành phần chung, wizard, sản phẩm

Mỗi dòng là một ticket riêng. Trường/giới hạn lấy từ BA D tương ứng. Các task lưu dữ liệu chỉ hoàn tất sau API thật/permission/error/version do mentor bàn giao.

| ID / task lớn | File / giờ gợi ý | Các bước intern thực hiện | Kết quả nghiệm thu tối thiểu | Chờ trước tích hợp |
|---|---|---|---|---|
| IF-01 / T-02 | F-NEW, F-CSS / 2–4h | Tạo trạng thái đang tải, danh sách rỗng, lỗi và nút thử lại; thay ở một màn được giao | Rỗng khác lỗi; thử lại gọi handler đúng, không tạo POST mới | G-01, G-02 |
| IF-02 / T-02 | F-NEW / 2–4h | Hoàn thiện cột STT theo page/size; tổng số và trước/sau trang; thử 2 trang | Trang size50 thứ hai bắt đầu 51; không coi STT là ID; giữ bộ lọc khi đổi trang | G-02 |
| IF-03 / T-02 | F-NEW / 2–4h | FormField nhãn/lỗi/bắt buộc; dialog lưu–đang lưu; giữ giá trị khi lỗi | Không submit đôi; lỗi gắn đúng trường; đóng dialog không làm ghi dữ liệu | G-02 |
| IF-04 / T-02 | F-NEW / 2–3h | Hiển thị VND, lượng 4 số lẻ, ngày giờ Việt Nam; dùng giá trị API | Không tự tính sổ bằng float; 0 khác trống; thời điểm UTC hiển thị đúng giờ Việt Nam | G-01, G-02 |
| IF-05 / T-07 | F-NEW / 3–5h | Vẽ wizard 7 bước/tiến độ; nút quay lại/tiếp; render missingItems | Resume hiển thị bước API trả, không tự báo hoàn thành | G-01, G-02 |
| IF-06 / T-05/07 | F-NEW, F-CORE / 3–5h | Form tên/liên hệ/địa chỉ; logo dùng fileId Core; gửi/lấy lại profile | Reload còn dữ liệu; field sai giữ form; không đổi tên environment thay hồ sơ | IF-03, G-04: setup |
| IF-07 / T-07 | F-NEW / 3–5h | Bước tạo chi nhánh/kho bằng API; chọn chi nhánh rồi kho; hiển thị lỗi | Kho thuộc chi nhánh, mã trùng báo đúng; không tự cấp ALL_BRANCHES | IF-05, G-04: setup |
| IF-08 / T-07 | F-NEW / 3–5h | Bước người dùng: danh sách/gán role/kho, trạng thái thiếu người duyệt | Không có quyền thì chỉ hướng dẫn; tự duyệt không được coi hợp lệ | IF-05, G-04: quyền |
| IF-09 / T-07/60 | F-NEW / 2–4h | Bước danh mục/đầu kỳ nối link nhập; hiển thị batch và phiếu nháp/đã ghi; nút xác nhận không số dư | Batch nhập xong chưa ghi không hiện hoàn thành đầu kỳ | IF-05, G-04: onboarding/import |
| IF-10 / T-07 | F-CORE, F-NEW / 3–5h | Định tuyến sau login, lựa chọn kho làm việc và thông báo chưa có scope; lưu mặc định qua API | Chủ chưa đủ vào wizard; nhân viên chỉ chọn kho được trả; scope mất yêu cầu chọn lại | IF-05, G-04: auth/setup |
| IF-11 / T-10 | F-NEW / 3–5h | Danh sách Nhóm sản phẩm, form mã/tên/active; nối create/update, version/error | STT/tìm/sửa xung đột/ngừng giữ lịch sử; không tạo catalog thứ hai | IF-01…03, G-03: group |
| IF-12 / T-10 | F-NEW / 2–4h | Thương hiệu dùng thành phần danh mục; form/list/search/ngừng | Validation tên/mã, API lỗi hiển thị, không xóa tham chiếu đã dùng | IF-11, G-03: brand |
| IF-13 / T-10 | F-NEW / 3–5h | Danh sách thuộc tính, form mã/tên/dùng tạo biến thể/thứ tự/active | Tạo Màu/Size; ngừng không xuất hiện chọn mới; không chuyển về Cấu hình kho | IF-01…03, G-03: attribute |
| IF-14 / T-10 | F-NEW / 3–5h | Trong thuộc tính mở bảng giá trị mã/tên/thứ tự/active; thêm/sửa/ngừng | Giá trị thuộc đúng thuộc tính; lỗi trùng giữ form; không một ô text thay toàn tập giá trị | IF-13, G-03: values |
| IF-15 / T-14 | F-NEW / 3–5h | Form sản phẩm cơ bản: mã/tên/UOM/group/brand/giá/tracking/return/warranty | Không có ô nhập tồn; số ngày0 rõ “không áp dụng”; mã/giá lỗi tại chỗ | IF-11/12, G-03: product |
| IF-16 / T-14 | F-NEW / 3–5h | Công tắc có biến thể; bộ chọn attribute/values active từ API; link bổ sung danh mục | Không nhập màu/size tự do, không dùng giá trị ngoài attribute; tắt chọn không mất dữ liệu âm thầm | IF-14/15 |
| IF-17 / T-14 | F-NEW / 3–5h | Hiển thị preview tổ hợp API, checkbox chọn tổ hợp, nhập mã/barcode/giá từng dòng | Đỏ/M và Đen/L chỉ tạo 2 khi chọn 2; lỗi theo dòng; không tự ép tạo toàn bộ | IF-16, G-04: variants |
| IF-18 / T-14 | F-NEW / 3–5h | List sản phẩm cha, mở SKU, search/status, nhóm/brand/tên tổ hợp; edit trong giới hạn API | SKU có phiếu không đổi tracking/UOM/tổ hợp; ngừng xem lịch sử được | IF-15/17, G-04: product |
| IF-19 / T-15 | F-NEW / 3–5h | Bộ chọn sản phẩm→SKU, search/barcode; nhãn tổ hợp; tích hợp vào một form mẫu | Chọn/scan đúng SKU, cha không thành dòng giao dịch; giữ snapshot SKU ngừng trên phiếu cũ | IF-18, G-04: SKU search |
| IF-20 / T-16 | F-UOM, F-CONFIG / 3–5h | Chọn SKU/UOM/hệ số, nhãn “1 thùng = 24 chai”, lỗi/version; kiểm menu inventory-only | Hệ số>0, không cùng baseUOM; đổi hệ số không đổi phiếu cũ; không thêm tab catalog vào config | IF-19, G-04: UOM |

## 5. Ticket frontend — các màn nghiệp vụ

Intern chịu trách nhiệm nhập/hiển thị và gọi đúng command, **không chịu trách nhiệm tự viết phép tính nợ/vốn/hoàn ở FE**. Nếu ticket gồm hai form và vượt một ngày, mentor tách suffix `.a/.b`.

| ID / task lớn | File / giờ gợi ý | Các bước intern thực hiện | Kết quả nghiệm thu tối thiểu | Chờ trước tích hợp |
|---|---|---|---|---|
| IF-21 / T-18 | F-NEW / 3–5h | Form bảng giá/khuyến mãi; đủ kênh CTV, từ/đến/priority/stackable; hiện preview rule | Giá sau ưu đãi lấy API; không tự đổi thuật toán; lỗi thời gian đúng | IF-03/19, G-04: price |
| IF-22 / T-23 | F-NEW / 3–5h | NCC list/create/edit/ngừng; contact/MST tùy chọn, link mua/nợ | Không ô sửa nợ trên profile; scope đúng; không tự nâng quyền người mua | IF-01…03, G-04: supplier |
| IF-23 / T-25 | F-NEW / 3–5h | Header receipt: kho/NCC/nguồn K/note; chọn SKU và UOM dùng chung | Kho/NCC bắt buộc, lỗi giữ nháp; không nút thêm sản phẩm trong form nhập | IF-19/20/22, G-04: purchase |
| IF-24 / T-25 | F-NEW / 3–5h | Dòng qty/price/discount/VAT, lô/hạn/serial; hiển thị tổng, paid và phí từ API | LOT/SERIAL field đúng điều kiện; phí vốn/tổng phải trả không trộn; không ghi pending thành đã nhập | IF-23, G-04: receipt; G-06 nếu phí |
| IF-25 / T-27 | F-NEW / 3–5h | Đơn mua detail ordered/received/current/remaining và chênh giá; chọn dòng tạo receipt | Nhận nhiều lần, không dùng đơn đóng; thừa/khác giá nêu ngoại lệ | IF-24, G-04: PO |
| IF-26 / T-31 | F-NEW / 3–5h | POS search/scan, giỏ SKU, qty/UOM, khách và giá preview; thao tác bàn phím | Không thêm sản phẩm tại quầy; scan đúng variant; tổng lấy API | IF-19/20/21, G-04: POS |
| IF-27 / T-31 | F-NEW / 3–5h | Form tiền/phương thức, confirm theo key chuẩn, lỗi thiếu tồn và nút đặt trước có xác nhận | Nợ yêu cầu khách nhưng không duyệt riêng; retry không sinh key mới tùy ý; không giao khi pending | IF-26, G-04: POS/preorder |
| IF-28 / T-33 | F-NEW / 3–5h | Đơn bán list/detail: kênh/khách/địa chỉ, fulfillment/delivery/settlement riêng; sourcequote | Không gọi đơn đã xuất là đã thu/giao; cọc nối khoản tiền, không thêm doanh thu | IF-19, G-04: orders |
| IF-29 / T-33/35 | F-NEW / 3–5h | Dialog xuất phần còn theo kho; dialog yêu cầu đóng với lý do/remaining/cọc, trạng thái duyệt | Chỉ lượng chưa làm đóng; phần đã xuất giữ; không auto cancel vì giao thất bại | IF-28, G-04: issue/close |
| IF-30 / T-43 | F-NEW / 3–5h | Tồn list/filter/tracking/history; mở phiếu nguồn; giá trị chỉ hiện theo quyền API | Tổng/cách ly/khả dụng phân biệt; không thấy cost trong response không cố tính; không ô sửa tồn | IF-01/02/19, G-04: stock |
| IF-31 / T-39 | F-NEW / 3–5h | Transfer list/detail/send action, nhận qty tốt/hỏng/SN từng dòng; biên bản thiếu request | Hai đầu đúng action; thiếu còn chuyển tới khi xử lý; không cộng tồn client | IF-19/30, G-04: transfer |
| IF-32 / T-41 | F-NEW / 3–5h | Count list/record form: snapshot/read-only, counted qty/lots including0/SNs/reason, diff từ API | Chỉ SKU đếm khóa, hàng khác hoạt động; submit khác post; không sửa snapshot | IF-19/30, G-04: count |
| IF-33 / T-46 | F-NEW / 3–5h | Đợt giao items/recipient/carrier/tracking/manualstatus; form gross/fee/net và bảng settle | Partial ghi qty; net lấy API; FINANCE mới settle; no carrier API | IF-28, G-04: delivery/COD; G-06 phí |
| IF-34 / T-50 | F-NEW / 3–5h | Thu chi list/create/linkdoc và allocation form; công nợ bảng riêng, nợ/advance lấy API | Số dư không dựa paid tĩnh; sai partner rõ; không ghi refund khách bằng chi rời | IF-01…03, G-04: finance |
| IF-35 / T-53 | F-NEW / 3–5h | Trả search số gốc→dòng/qty/tracking/condition/kho nhận; hiển thị refundable/credit và đổi/hoàn link | Không nhập ID kỹ thuật; serial sai không bypass; return/paid/cost nguồn không thay giá hiện tại | IF-19/34, G-04: returns/exchange; G-06 USED |
| IF-36 / T-55 | F-NEW / 3–5h | Warranty sourcepicker, serial/qty/note/assignee/due, status và recipient bàn giao | Đúng source, vật phẩm giữ hộ không tăng stock; không nhập sourceLineId thủ công | IF-35, G-04: warranty |
| IF-37 / T-62/61 | F-NEW / 3–5h | CRM note/reminder/result/assignee/done; kỳ hoa hồng chọn tháng, list theo kỳ | Reminder total không bằng top20; tháng chỉ ghi bổ sung, không tự tính commissionrate | IF-03/34, G-04: CRM/commission |
| IF-38 / T-60 | F-NEW / 3–5h | Import loại/file/mẫu/preview lỗi theo dòng/commit, batch status/link đầu kỳ; chặn khi errors | Commit batch khác phiếu đã ghi; không gửi commit nếu còn lỗi; không upload DB thật để test | IF-09, G-04: import |
| IF-39 / T-64 | F-DASH, F-CSS / 3–5h | Tab theo quyền, kỳ/branch/warehouse labels, chỉ số→filteredlist; report paging/totals | Filter và totals cùng phạm vi; không đổi CSS toàn hệ thống; không top20count làm tổng | IF-01/02, G-04: report/summary |
| IF-40 / T-22/65 | F-NEW / 3–5h | Approval summary/decisionreason; export theo filter và print view từ payload đã ghi | Selfapprove hidden/serverdeny; file đủscope/COST; receipt numbers đúng, no “hóa đơn VAT” | IF-03/39, G-04: approvals/export/print |

## 6. Ticket backend — DTO, validation, đọc dữ liệu và danh mục

Mentor bàn giao schema, service mẫu dùng chung và cách gọi `RetailAccess`/`RetailCommands`. Ticket dưới không cho tự bỏ kiểm tenant/scope hoặc tự viết SQL ghi sổ. Validation mới phải dùng cả API và import nếu cùng quy tắc.

| ID / task lớn | File / giờ gợi ý | Các bước intern thực hiện | Kết quả nghiệm thu tối thiểu | Chờ trước tích hợp |
|---|---|---|---|---|
| IB-01 / T-01 | DOC, B-CTRL / 2–3h | Liệt kê route thực tế, request fields, permission và response theo một module; đánh dấu đề xuất chưa có | Mentor so với source, không ghi API mới như đang hoạt động; có ví dụ không token | G-01 |
| IB-02 / T-05 | B-NEW / 2–4h | DTO profile theo D-02, required tên, optionalcontact/logo, version; unit test validation | Không map environment vào tên cửa hàng; field sai có code/field; chưa ghi tenant từ request | G-03: profile |
| IB-03 / T-06 | B-NEW / 2–4h | Mapper checklist/step/missingItems từ service mentor, serialize null/complete flags; không tự tính đủ quyền | Batchdone chưa post hiển thị missingOpening; completedAt đúng DTO | IB-02, G-04: setup |
| IB-04 / T-08 | B-NEW, B-CAT / 3–5h | DTO group/brand code/name/active/version, mapping lỗi trùng theo framework | Required/maxlength/version đúng; không duplicate catalog source | G-03: group/brand |
| IB-05 / T-08 | B-NEW / 3–5h | Endpoint list/get group/brand theo query mẫu mentor, activefilter/paging/totals | Tenant lấy từ session; param SQL bind; tổng theo filter, không leak inactive lịch sử | IB-04, G-03 |
| IB-06 / T-08 | B-NEW / 3–5h | CRUD group/brand trong service mẫu đã review, ngừng thay xóa có tham chiếu | Trùng mã/xung đột phiên bản audit; không đổi migration ngoài ticket | IB-05, G-03; mentor review ghi master |
| IB-07 / T-09 | B-NEW / 3–5h | Attribute DTO + list/get, sortOrder/active/useForVariant; đọc scope tenant mẫu | Không lọc sai thuộc tính sang tenant khác; mã/tên required | G-03: attribute |
| IB-08 / T-09 | B-NEW / 3–5h | AttributeValue DTO và CRUD mẫu, kiểm parent/tenant/active/unique; ngừng giữ tham chiếu | Value Size không thuộc Màu; parent ngoài tenant bị chặn; không SQLdynamic từ input | IB-07, G-03; mentor review ghi master |
| IB-09 / T-13 | B-NEW / 3–5h | Product create/update DTO fields/defaults hasVariants; validator scalarprice/days/UOM refs qua service | 0 days hợp lệ, negativeblocked; không tự tạo cha/SKU hoặc backfill | G-03: product |
| IB-10 / T-13 | B-NEW / 3–5h | Validator variants payload: SKUcode/barcode, valueId thuộc attribute, đủ attribute bộ cha; map field-indexerror | Mã trùng payload/sai giá trị báo variants[i]; DBunique/commit do mentor | IB-08/09, G-04: variants |
| IB-11 / T-13 | B-NEW / 2–4h | Mapper productdetail→SKU/tổ hợp/active/version; snapshotname từ data đã lưu | Không ghi tên mới đè phiếu cũ; cha không trả qty cộng gộp sai đơn vị | IB-09, G-04: product |
| IB-12 / T-16 | B-CAT, B-NEW / 2–4h | Validator factor positive max4decimals và UOM khác base; test helper; giữ adapter approved | 0/negative/5lẻ/cùngUOM bị chặn; factor1base tự có; không sửa snapshot | G-04: UOM |
| IB-13 / T-17/18 | B-NEW / 2–4h | DTO rule/promo, channelenum có CTV, datefrom≤to, qty>0/percentvalid; error mapping | Chỉ validation, không thay giáengine/rounding; ca boundary | G-04: price |
| IB-14 / T-19 | B-NEW, B-DOC / 3–5h | DTO draft input theo D-08/09, required/maxlength/arraylimit/basicqty; mapper header/dòng | Không sửa state machine, approval hoặc post; Posted inputedit do service chặn | G-04: draft |
| IB-15 / T-23 | B-CAT, B-NEW / 3–5h | Supplier DTO/contact optional + listfilter SUPPLIER, details mapping, reuse master service | Không chỉnh AP trong profile, partnerkind/scope error đúng; create/edit mentorreview | G-04: supplier |
| IB-16 / T-26/32 | B-NEW / 2–4h | Mapper ordered/progress/closed/remaining từ service, labelunit/warehouse/source | Không tự đổi lượng nguồn; remaining đúng cơ sở, DTO serialize decimalsexact | G-04: orderprogress |
| IB-17 / T-43 | B-INV, B-NEW / 3–5h | DTO/read-only filter tồn/tracking/history qua query đã review, COST field removal | Không viết movement; response không cost với user không quyền; boundparams | G-04: stock |
| IB-18 / T-44 | B-NEW, B-OPS / 3–5h | Delivery DTO/recipient/carrier/tracking/qty validator và mapper; totals service cung cấp | Không tự chuyển failed→cancel/return; shipmentline refs checked service | G-04: delivery |
| IB-19 / T-47/50 | B-NEW, B-FIN / 2–4h | Payment/allocation request scalarvalidator, integerVND>0/method/reason; mapper nợ/advance | Không tính remaining mới hay ghi allocation; mentor giữ locks/payment engine | G-04: finance |
| IB-20 / T-56 | B-NEW, B-OPS / 3–5h | DTO care assignee/due/result; parse thời gian ISO/map VN, maxnote; list query scoped được review | Test lịch UTC hiển thị giờ Việt Nam; completion timestamp lấy service, không cho input | G-04: CRM |
| IB-21 / T-57/58 | B-IMP, B-NEW / 3–5h | Chuẩn hóa lỗi import row/field/code/message và columnaliases theo template mentor; validator số/ngày | Lỗi cùng trường API/import giống nhau; không tự seed/commit/backfill trong parser | G-04: import |
| IB-22 / T-63 | B-NEW, B-OPS / 3–5h | Bộ parse filter ngày/page/size/branchwarehouse; mapper items/total/scope theo query mentor | Param SQL bind, maxrange rõ, total không pagecount; không tự cộng lại sổ bằng Javafloat | G-04: report |
| IB-23 / T-65 | B-NEW / 3–5h | CSV encoder theo contract, UTF-8/cột/escape công thức/quote/newline; test encoder | Ô =/+/−/@ xử lý theo chuẩnexport; dữ liệu số thật không mất nghĩa; quyền/totals do service | G-04: export |
| IB-24 / T-69 | Q-NEW / 3–5h | Viết test tham số cho validators/mappers đã làm: đúng/sai/boundary/null; fixture nhỏ trong test | Test phát hiện dữ liệu invalid, không chỉ assert implementation; không cần DB thật | IB-02…23 theo task được kiểm, G-05 |

## 7. Ticket kiểm thử chéo và bằng chứng

IQ có thể bắt đầu viết ca trước API/UI hoàn thành, nhưng chỉ ghi Passed sau chạy đúng bản build và dữ liệu. Người hướng dẫn chạy hoặc theo dõi các ca đồng thời/rollback/migration; intern không tự tác động DB vận hành.

| ID / task lớn | File / giờ gợi ý | Các bước intern thực hiện | Kết quả nghiệm thu tối thiểu | Chờ trước chạy |
|---|---|---|---|---|
| IQ-01 / T-01/69 | DOC / 2–4h | Bảng FR→T→IF/IB→AC→case; ghi còn chờ API/policy, không bỏ AC chưa gán | 36 AC có case/owner/evidence placeholder, phạm vi conditional rõ | G-01 |
| IQ-02 / T-68/69 | DOC, Q-NEW / 3–5h | Cùng mentor tạo fixture riêng: hai tenant, khoA/B, role, hàng thường/LOT/SERIAL/variants, giá/nợ; reset hướng dẫn | Nêu rõ DB test, không password/token trong report; không dùng SKU thật trong test phá dữ liệu | G-05 |
| IQ-03 / T-07/71 | DOC / 2–4h | Ca login/wizard/resume/noscope/zeroopening, chụp kết quả và request IDs | AC-01/15/35; UI không báo đầu kỳ ghi khi mới commit | IF-05…10/38, IQ-02 |
| IQ-04 / T-14/69 | DOC, Q-NEW / 3–5h | Ca Màu/Size/valueinactive, chọn 2/4 tổ hợp, duplicateSKU, không nhập tự do, scan đúng variant, UOMoldfactor | AC-02/20/21; backfill chỉ mentor chạy, intern ghi đối chiếu | IF-11…20, IB-08/10/12, IQ-02 |
| IQ-05 / T-25/27/71 | DOC / 3–5h | Nhập 10 × 100.000 ở trạng thái chờ/duyệt; PO nhận 6+4; khác giá/thừa; kiểm tồn/AP/đã trả | AC-03/04/24; đối chiếu sổ, phí chờ chính sách | IF-22…25, G-04: purchase, G-06 phí |
| IQ-06 / T-31/33/71 | DOC / 3–5h | Bán 200.000 thu 150.000; thiếu khách khi còn nợ; đơn 1.000.000 cọc 200.000 xuất 600.000+400.000; thiếu hàng/đặt trước | AC-05/06/07/22; vốn/AR không lặp thu/giao | IF-26…29/34, IQ-02 |
| IQ-07 / T-69 | Q-NEW, DOC / 3–5h | Dùng công cụ mentor cấp: bán chiếc cuối đồng thời; cùng khóa cùng/khác dữ liệu; xung đột phiên bản nháp | AC-08/23; chỉ một tác động, không bấm tay rồi đoán đã kiểm đồng thời | G-04: commands, G-05, IQ-02 |
| IQ-08 / T-04/36/66/69 | DOC, Q-NEW / 3–5h | Kiểm ngoài tenant/kho, xem vốn/export, tự duyệt, thu hồi quyền, nhận chỉ quyền kho đích, các màn Core | AC-14/19/35; thử API trực tiếp, không chỉ nút ẩn; không lộ bí mật | G-04: scope/approval, IQ-02 |
| IQ-09 / T-39/41/71 | DOC / 3–5h | Chuyển 10 nhận 6 gồm 1 hỏng, xử lý thiếu 4; kiểm kê X10→8, Y/A và X/B hoạt động; giải phóng/hủy cách ly | AC-12/13/27; đối chiếu lượng/giá trị nguồn/đích/đang chuyển | IF-30…32, G-04: stock, IQ-02 |
| IQ-10 / T-46/69 | DOC / 3–5h | Giao 10 chia 4+6; COD 500.000 phí 25.000; nhiều lần/trùng/thu nợ trước COD/giao thất bại/không COD | AC-09/26/28; nợ giảm 500.000, thực nhận 475.000, phí 25.000; không tự hủy | IF-33, G-04: COD, G-06 phí |
| IQ-11 / T-50/29/69 | DOC, Q-NEW / 3–5h | Phân bổ nhiều lần, sai khách/vượt tiền, công cụ đồng thời; đảo 150.000; trả mua 300.000 còn AP 100.000, NCC hoàn 200.000 thu 150.000+50.000 | AC-25/29/30; nợ/khoản phải hoàn/ứng trước đúng, nguồn không xóa | IF-34, G-04: finance/supplierrefund, IQ-02 |
| IQ-12 / T-53/55/69 | DOC / 3–5h | Trả phiếu 300.000 đã thu 200.000 ở kho khác; sai serial/vượt lượng; trả 200.000 đổi 250.000; bảo hành và bàn giao | AC-10/11/31/32; hoàn tối đa 200.000/thu thêm 50.000; tồn bán khác giữ hộ | IF-35/36, G-04: returns/warranty, G-06 USED |
| IQ-13 / T-60/69 | DOC / 3–5h | Import xlsx/csv/API: dòng sai/tracking/công thức/trùng/biên 500 dòng và 2MB; đầu kỳ nháp→duyệt | AC-15/21; lỗi dòng/trường, không ghi một phần hoặc nhập lặp | IF-38, IB-21, IQ-02 |
| IQ-14 / T-61/62/69 | DOC / 2–4h | Hơn 20 nhắc việc/người phụ trách/hẹn/kết quả; CTV 300.000+bổ sung 200.000=500.000, điều chỉnh âm/chi vượt | AC-16/33; giờ đúng, tổng đủ, không ghi lại tổng tháng | IF-37, G-04: CRM/commission |
| IQ-15 / T-64/65/71 | DOC / 3–5h | Bộ lọc/đối chiếu báo cáo/xuất hơn 1.000 dòng/quyền vốn; in 80mm/A4/bàn phím/mobile; lỗi CSS theo màn | AC-18/34; tổng đầy đủ đúng phạm vi, in số/ngày/đã thu/còn nợ đúng | IF-39/40, G-04: report/export, IQ-02 |
| IQ-16 / T-69…72 | DOC / 3–5h | Tổng hợp lỗi/kiểm lại/bằng chứng 36AC; đóng phần chưa xuất/hoàn cọc; theo dõi restore/build mentor chạy; checklist bàn giao và giới hạn | AC-17/36 và AC thiếu; chính sách chưa chốt không Passed; mentor xác nhận restore/release | IQ-01…15 theo phạm vi, G-04: close/refund, G-05 |

## 8. Cách giao một task để intern làm được

Mỗi ticket phải có ví dụ đầu vào và kết quả kỳ vọng, file cho sửa, API mẫu và người hỏi khi vướng. Không giao “làm module Kho” hoặc “hoàn thiện backend” mà không giới hạn đầu ra.

### Ví dụ ticket IF-17 — bảng chọn tổ hợp biến thể

- **Người làm:** Intern 1; **reviewer:** người hướng dẫn frontend, backend xác nhận payload; **kiểm thử chéo:** Intern 2 qua IQ-04.
- **Đọc trước:** BA F-05/F-06, D-04/D-05; SRS FR-05; IF-16 đã hoàn thành.
- **Dữ liệu test:** sản phẩm Áo A; Màu gồm Đỏ/Đen, Size gồm M/L; giá trị được cấp ID từ API test, không tự tạo ID giả để commit.
- **File:** component con mới do mentor chỉ định trong F-NEW; chỉ CSS thuộc vùng component. Không sửa auth, SQL hoặc logic tính giá.
- **Bước 1:** nhận danh sách preview 4 tổ hợp từ API, hiện bảng checkbox/tổ hợp/mã/barcode/giá.
- **Bước 2:** chọn Đỏ/M và Đen/L; sửa mã SKU và giá; validation field cơ bản và lỗi trả từ server theo index.
- **Bước 3:** gửi đúng 2 dòng đã chọn qua command helper; giữ khóa retry theo contract; disable khi gửi nhưng không coi đây là chống trùng duy nhất.
- **Bước 4:** thành công tải lại list SKU; lỗi giữ dòng đã nhập, đưa focus tới lỗi đầu.
- **Nghiệm thu:** chỉ tạo 2 SKU; không freeinput màu/size; giá âm/mã trùng không lưu một phần; retry cùng request không tạo2 lần; ảnh và request mẫu đã bỏ token.
- **Bàn giao:** component + danh sách file sửa + bước test + kết quả lint/build phù hợp + điều còn thiếu; mentor kiểm payload/schema/API thật trước Done.

### Ví dụ ticket IB-08 — giá trị thuộc tính

Mentor bàn giao table schema và service mẫu gọi scope/audit/idempotency. Intern làm DTO code/name/parent/version, CRUD trong khung đó và ca trùng/sai parent/ngừng. Không tự tạo migration, tự lấy tenant từ body, bỏ scope hoặc viết unique bằng “SELECT trước INSERT” thay DB constraint. Done khi mentor review và test parenttenant/duplicate/version/active đã đạt.

### Ví dụ ticket IQ-06 — bán và cọc

Ca A: kho test có2SKU, chọn khách, bán200.000 thu150.000 → phiếu đã ghi, tồn giảm, tiền thu150.000 và AR còn50.000. Ca B: đơn1.000.000 cọc200.000 → trước xuất chưaAR/doanhthu; xuất600.000 rồi400.000 → doanhthu1.000.000, cọc phân bổ200.000 một lần, AR800.000. Ghi số phiếu/payments/openitems và ảnh/API được phép; không chỉ đánh dấu “bấm thành công”.

## 9. Kiểm tra trước khi gửi review

| Loại task | Checklist |
|---|---|
| FE | Field đúng D; nhãn Việt; loading/empty/error; giữ form; keyboard; API/quyền/version; không tính sổ lại; không sửa CSS/auth ngoài phạm vi |
| BE | DTO/validation theo contract; tenant từ session; bind SQL; gọi service đã review; lỗi có field/code; không secrets; không đổi ledger/migration ngoài ticket |
| QA | Build/DB/role rõ; case bước/expected/actual; bằng chứng số phiếu/sổ; lỗi có tái hiện; conditional không Passed; không fixture DB vận hành |
| Chung | Một ticket/một đầu ra; liệt kê file sửa; chỉ rõ còn chờ API/quyết định; reviewer đã xem; task cha chưa tự coi Done |

Frontend hiện có npm scripts `lint` và `build`. Sau thay đổi có thể chạy từ thư mục `frontend`: `npm run lint`, `npm run build` theo hướng dẫn môi trường mentor. Đây là lệnh kiểm, không thực hiện trong nhiệm vụ phân task này. Trước sửa code đọc `frontend/AGENTS.md` và guide Next cài tại dự án. Backend dùng Java21/Maven; mentor đưa lệnh test đúng lớp, database/testcontainers riêng khi cần. Không chạy smoke/import/cleanup trên database vận hành để “kiểm nhanh”.

Hồ sơ bàn giao không chứa token/password. Mỗi ngày intern báo: đã làm gì, kết quả chạy, đang vướng API/file/quy tắc nào, cần mentor xử lý gì. Vướng thiết kế hoặc quy tắc nghiệp vụ thì dừng phần phụ thuộc và hỏi mentor, vẫn làm phần độc lập được giao.

## 10. Kế hoạch nhận việc theo lượt

| Lượt | Intern 1 | Intern 2 | Intern 3 | Điều kiện bàn giao |
|---|---|---|---|---|
| 0 — làm quen | Đọc BA sản phẩm và F-WS/B-CAT, nhận IF-01 | IB-01: kiểm kê API mua/bán, đọc wizard | IQ-01 và chuẩn bị IQ-02 cùng mentor | G-01, G-05; chưa ghi dữ liệu thật |
| 1 — ít rủi ro | IF-01…04; IB-04…08 rồi IF-11…14 | IB-02/03; IF-05…10 theo API setup, IF-22 theo API NCC | IB-17/19; IF-30 mockup/tích hợp tồn đọc; cùng mentor fixture | G-02/03; một file một người; scope/DTO có review |
| 2 — sản phẩm và nhập | IB-09…13; IF-15…20; IQ-03/05 theo build sẵn | IB-14…16; IF-21…25; IQ-04 kiểm sản phẩm | IB-21 và IF-38; IB-22; thiết kế IQ-06/07 | Product/receipt/import APIs có từng slice; mentor migration xong |
| 3 — giao dịch | Kiểm chéo IQ-10/11/13; sửa lỗi sản phẩm/UOM | IF-26…29; kiểm IQ-08/09/12 | IB-18; IF-31…36; IB-23; IF-39/40 theo API | Tồn/tiền/approval do mentor cung cấp; chính sách liên quan chốt |
| 4 — bổ sung/nghiệm thu | IQ-15, hồi quy/khả dụng | IQ-14 và lỗi mua/bán | IB-20/24, IF-37; IQ-06/07/16 tổng hợp | P2 sau P1; review/UAT/restore do người hướng dẫn và PO |

Đây là lượt nhận việc cho 3 intern, không phải kế hoạch tuần. Task có thể đổi lượt theo API sẵn và năng lực, nhưng không bỏ dependency. Mentor ghi rõ owner theo bảng mục 1, chỉ chia song song khi component/file/API tách rõ. Lượt đầu đánh giá thời gian thực rồi cân lại tải, không giao30/26/24task một lần.

### Ba ticket giao ngay sau khi có môi trường và mentor

1. **Intern 1 → IF-01:** trạng thái tải/rỗng/lỗi/thử lại của một danh sách; không sửa logic ghi sổ. Đầu ra component và demo trạng thái, reviewer FE.
2. **Intern 2 → IB-01:** lập bảng API thực tế của Mua hàng và Bán hàng, trường/quyền/lỗi; không sửa code. Đầu ra tài liệu để mentor chốt contract.
3. **Intern 3 → IQ-01:** bảng truy vết36 AC và thiết kế ca IQ-02 trên môi trường test riêng; không tự seed database vận hành. Đầu ra matrix và danh sách fixture cần mentor cấp.

## 11. Truy vết về 72 task lớn và phần việc còn lại

| Nhóm backlog cha | Việc intern hỗ trợ | Mentor vẫn sở hữu |
|---|---|---|
| T-01…07 | IF-01…10, IB-01…03, IQ-01/03/08 | Contract, role/scope, auth/checklist đúng, tiến độ và profile API |
| T-08…18 | IF-11…21, IB-04…13, IQ-04 | Model/migration/backfill/atomicvariants, UOM/price engine |
| T-19…22 | IF-03/40, IB-14, IQ-07/08 | Draft/version, số phiếu, idempotency/locks, approval/history |
| T-23…29 | IF-22…25/34, IB-15/16/19, IQ-05/11 | Receipt/value/AP, partialPO/trả mua/refundclaim và phân bổ |
| T-30…35 | IF-26…29/34, IB-16/19, IQ-06/07/16 | POS/posting, sourceprogress, closure/depositrefund/preorder atomic |
| T-36…43 | IF-30…32, IB-17, IQ-08/09 | Hai đầu scope, transfer/value/loss/quarantine/count locks |
| T-44…50 | IF-33/34, IB-18/19, IQ-10/11 | COD/debt/fee, allocation/advance/reversal transactions |
| T-51…56 | IF-35…37, IB-20, IQ-12/14 | Trả/hoàn/đổi ledger, warranty giữ hộ/caps, CRM API |
| T-57…62 | IF-09/37/38, IB-21, IQ-13/14 | Importatomic/checksum/opening/variantsresolve, commissionremaining |
| T-63…67 | IF-39/40, IB-22/23, IQ-08/15 | Báo cáo query/totals/scope, Core regression, file authorization; T-67 attachments giao sau bằng ticket riêng |
| T-68…72 | IB-24, IQ-01…16 | Môi trường, automation/rollback/concurrency/migration/restore/benchmark, UAT/packaging/release |

80 task trên ưu tiên phần intern có thể nhận rõ và tạo giá trị; không tuyên bố80task đủ để hoàn thành72task cha nếu thiếu mentor thực hiện phần lõi. Phần attachments T-67/P2, backend engine hoặc công cụ kỹ thuật cần ticket bổ sung sau khi thiết kế, không giao mơ hồ cho intern. PO chốt USED/fees/dates trước test và bật rule mới; intern không được mặc định quyết định thay.

Trạng thái ban đầu toàn bộ task: Todo. Không tạo nhánh, commit, push, deploy hoặc thay DB trong nhiệm vụ lập bảng phân công này. Khi bước sang triển khai, follow quy trình review và phạm vi đã được giao.
