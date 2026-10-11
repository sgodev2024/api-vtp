# Rà soát hệ thống AICRM và backlog giao dev — 09/10/2026
Cập nhật tài liệu: BA/SRS hiện hành 3.0 và runbook đã được lập trong docs; xem README.md của thư mục docs. DOC-01 chuyển sang chờ review trong JSON task. Các nhận xét audit bên dưới giữ bối cảnh snapshot trước thay tài liệu; task Word là bản lịch sử audit.

Cập nhật tài liệu: BA/SRS hiện hành 3.0 và runbook đã được lập trong docs; xem README.md của thư mục docs. DOC-01 chuyển sang chờ review trong JSON task. Các nhận xét audit bên dưới giữ bối cảnh snapshot trước thay tài liệu; task Word là bản lịch sử audit.


Phạm vi: source local aicrm-java, BA/SRS hiện tại, migration, API, menu/frontend, phân quyền, kiểm thử/CI và cơ chế module. Baseline Git: main ae59355; có thay đổi Đợt 3 chưa commit. Không coi source local là bản đang chạy trên server. Lần rà soát này không sửa nghiệp vụ, không push/deploy và không tạo chứng từ kiểm thử trên database vận hành.

## 1. Kết luận để giao việc

Hệ thống đã có nền tảng và luồng nhập–duyệt–bán–thu tiền–tồn kho. Tiếp tục hoàn thiện trên source này, không dựng lại từ đầu. Các kết luận dưới đây được tách thành: **đã có**, **có một phần/chênh SRS**, **thiếu theo đặc tả**, và **cần kiểm chứng bằng test**. Task kiểm chứng không đồng nghĩa chức năng bị lỗi.

Ưu tiên trước nghiệm thu: test/CI, an toàn tiền–tồn, tính đầy đủ của danh sách và xuất dữ liệu, mô hình biến thể đã chốt. Nhóm cải tiến gồm CRM, mobile thực tế, in phiếu và giảm code dùng chung. Không khôi phục trường phí mua vốn hóa/phân bổ/phí chưa biết, giảm dòng trong nhập mua/đơn bán, COD/phí khách trả trong form tạo đơn bán mà người dùng đã bỏ. COD vẫn là chức năng giao/đối soát nhập tay riêng.

## 2. Hiện trạng từng nhóm

| Nhóm | Frontend | Backend | Kết luận/giao tiếp |
|---|---|---|---|
| Core đăng nhập/Trang chủ/quản trị | Có | Có | Giữ core; test hồi quy và phân quyền, không xây lại |
| Core tệp/audit/resources | Có console | Có file/audit/dynamic-resource | Có; kiểm quyền download, tenant, audit và hồi quy |
| Core jobs/outbox/webhook | Có console kỹ thuật | Có queue/event/webhook | Có; kiểm retry/auth và quan sát vận hành, không đưa vào luồng SME |
| Vai trò/phân quyền/gán user | Có list + popup checkbox | Có capability, scope, API role core | Đã có; cần ma trận negative tests; admin ngoại lệ tự duyệt đã được code |
| Thiết lập ban đầu | Có wizard 5 bước | Có company/location/warehouse/default/progress | Đã triển khai; kiểm doanh nghiệp mới và tiếp tục tiến độ |
| Địa điểm/Chi nhánh, Kho | Có màn quản lý riêng | Có quản lý/default/trạng thái/phạm vi | Có; giữ Địa điểm trong Vận hành chung, Kho trong Kho |
| Sản phẩm đơn giản | Có tạo và list | Có SKU/tracking/price/policy | Có; thiếu chỉnh hồ sơ/version và liên kết nhóm/brand đầy đủ |
| Thuộc tính/biến thể | Có danh mục, sản phẩm vẫn ô text | Có warehouse_config, sku.variant text | Chưa đáp ứng mô hình Product cha + tổ hợp ID cấu hình sẵn |
| UOM/quy đổi | Có | Có bảng UOM/conversion + trigger đồng bộ JSON | Đã có; cần test snapshot, không xây bảng lại |
| Customer Master/NCC/CRM khách | Có | Có partner chung nhiều vai trò/version | Đã có; kiểm trường/ngân hàng và lịch sử phạm vi |
| Giá/khuyến mãi | Có cấu hình/preview/giá tự động | Có ưu tiên và stackable | Có; nghiệm thu UOM/giá0/snapshot/ngoại lệ |
| POS | Có giao diện quầy | Có post và kiểm tồn/serial/FEFO | Có; tồn UI chưa loại lô hết hạn, phần thiếu chưa tách rõ |
| Báo giá/đơn bán/xuất nhiều lần | Có | Có source/progress/deposit/AR | Có; thêm sửa nháp, yêu cầu đóng có duyệt và progress giao từng dòng |
| Mua/Nhập trực tiếp/nhận nhiều lần | Có | Có document family/approval/AP | Có; kiểm sai lệch; không bổ sung các trường đã bỏ |
| Trả mua/tiền NCC hoàn | Có phiếu trả và loại SUPPLIER_REFUND | Có giảm AP + refundCredit nhưng chưa sổ claim có phân bổ đầy đủ | Cần hoàn thiện đối chiếu và chặn thu vượt |
| Kho/tồn/lô/serial | Có | Có movement/balance/tracking/quarantine | Có; cần paging, xử lý cách ly và đối chiếu |
| Chuyển kho | Có xuất/nhận từng phần | Có transit/progress/nhận hỏng | Có một phần; UI tình trạng toàn lần nhận, chưa xử lý mất còn chuyển |
| Kiểm kê | Có | Có khóa SKU/kho, snapshot/duyệt | Đã có; hiện chặn SKU có cách ly nên cần xử lý riêng |
| Thu chi/công nợ/cọc | Có tách màn | Có payment/allocation/open_item | Có; limit 1000, thiếu luồng đảo khoản tiền |
| Trả bán/hoàn/đổi | Có trả/hoàn, liên kết nguồn | Có creditReturn/REFUND/exchangeReturnId | Trả/hoàn có; đối trừ đổi hàng chưa là sổ quyền lợi hoàn chỉnh |
| Giao hàng/COD nhập tay | Có | Có trạng thái/settle/fee | Chưa có deliverylines; COD create kiểm từng đợt, chưa tổng đã gán |
| Bảo hành | Có màn/form | Có source/serial/status | Có; thiếu tổng lượng giữ hàng không serial và metadata bàn giao |
| CRM nhắc chăm sóc | Có | Có note/due/done | Cần assignee/result/completedAt; reminder chỉ20 mẫu |
| Hoa hồng | Có thủ công/kỳ/chi | Có ledger + remaining | Có; cần lọc kỳ/paging/test chống trùng |
| Tổng quan | Có6 tab | Có summary nhưng FE tải toàn phiếu | Có; tổng nợ/CRM tính trên mẫu bị cắt; cần aggregate server |
| Báo cáo/xuất/in | Có | Có sales/purchase/inventory/report/export | Có một phần; export tiền/movement lấy list limit 1000; thiếu bộ lọc đồng nhất |
| Module import/bật tắt | Có entry/registry và màn bật tắt | Có port/dependency/gate/source bundle | Đợt3 local; import bằng CLI build/restart, chưa hot upload; còn engine dùng chung |

## 3. Phát hiện có căn cứ trong source

| ID | Phát hiện | Ý nghĩa |
|---|---|---|
| E01 | RetailCatalog:117/119 ghi sku.variant text; chưa Product cha và liên kết attribute-value | Chênh quyết định biến thể; PRD-01..04 |
| E02 | RetailFinance:50, RetailInventory:84 giới hạn1000; RetailController export dùng lại hai danh sách | Có thể thiếu dòng và tổng khi dữ liệu tăng; FIN-01/REP-03 |
| E03 | Overview:16 limit20 reminders; OverviewDashboard dùng reminders.length, debt từ finance.openItems | Dashboard có thể sai tổng; CRM-02/REP-01 |
| E04 | RetailOperations:createDelivery kiểm cod<=debt của từng request, không trừ COD đã gán các đợt khác | Có thể gán COD vượt nghĩa vụ còn lại; SAL-08 |
| E05 | warranty chỉ qty<=base_qty từng phiếu; hàng không serial chưa cộng phiếu đang giữ | Có thể nhận bảo hành vượt lượng; WAR-01 |
| E06 | RetailDocuments:104 close ghi CLOSED trực tiếp với APPROVE_CANCEL; không có bước request/pendingclose riêng | Quyền duyệt đã có, nhưng quy trình đề nghị–người khác duyệt chưa đủ; SAL-06 |
| E07 | AccountingController không có API đảo; exchangeReturnId hiện chỉ lưu liên kết, chưa tiêu quyền lợi có sổ | FIN-02/RET-02 là hoàn thiện theo SRS, không coi các khoản thu hiện có đều sai |
| E08 | createDelivery/delivery chỉ trạng thái; migration delivery không có bảng dòng giao thực tế | Chưa theo dõi lượng giao nhiều đợt; SAL-07 |
| E09 | COUNT submit trả COUNT_QUARANTINE; transfer chưa có action xử lý mất; UI damaged dùng chung lần nhận | STK-02..06 |
| E10 | Import preview kiểm một phần field, commit gọi validator tạo SKU đầy đủ hơn | Preview báo hợp lệ nhưng commit có thể thất bại; OPS-03 |
| E11 | backend-ci gọi package -DskipTests dù tên job integration tests | CI chưa bảo vệ hồi quy backend; QA-02 |
| E12 | Chạy3 frontend test chọn lọc:1qua/2fail do cấu trúc shell sau tách module | QA-01; không suy ra UI thực tế bị lỗi |
| E13 | SRS dẫn TASK_TRIEN_KHAI_v2.0.md/TASK_INTERN_v2.0.md không có trong repo; DEC-07/OPEN-04 cũ trái ngoại lệ admin đã chốt | DOC-01; dùng backlog mới bên dưới để tránh giao task lịch sử không còn đúng |
| E14 | GET catalog tải toàn SKU/master và gọi defaults.ensure; dashboard lặp hết trang chứng từ | PERF-01/REP-01; cần benchmark dataset, không kết luận tốc độ production bằng dataset demo |

## 4. Quy tắc ưu tiên và phân công

P1: hoàn thành trước nghiệm thu luồng liên quan. P2: cải tiến sau khi tiền/tồn an toàn. Không gắn P0 vì chưa có bằng chứng sự cố mất dữ liệu đang xảy ra. Mức FE/BE trong task là phần tương ứng đã có, không phải phần dev phải xây từ số0.

Đội3 intern fullstack: **A** phụ trách Bán hàng/khách hàng/CRM/mobile/quyền; **B** Sản phẩm/Mua/Kho/thiết lập/import; **C** Kế toán/Báo cáo/kiểm thử/module/release. Đây là phân công đề xuất theo vùng code, chưa có người hướng dẫn được xác nhận. Cần người có kinh nghiệm review schema, quyền và sổ tiền/tồn. Intern không tự chốt chính sách nghiệp vụ hoặc merge thay đổi tiền/tồn thiếu review.

Ước lượng dưới đây là ngày công cho người đã nắm repo, chưa gồm review/rework; task2 ngày là lát cắt đầu tiên, không hứa hoàn thành cả epic phức tạp trong2 ngày. Khi bắt đầu task schema/đảo/đổi/cách ly, tách PR migration, API, UI và test; reviewer chốt hợp đồng trước. Không để3 người cùng sửa SharedWorkspace/RetailDocuments: giao một người tích hợp cho mỗi đợt.

## 5. Danh sách công việc cụ thể

Có **60 task**: 52 P1, 8 P2. Trạng thái ban đầu Todo nghĩa chưa triển khai trong đợt backlog này; không phủ định code đã có. Những task bắt đầu bằng Test/Hồi quy/Nghiệm thu là kiểm chứng trước, chỉ sửa nếu test chỉ ra lỗi.

### Bảng giao việc nhanh

| Task | Ưu tiên | Nhóm | Owner | Trạng thái | Hạng mục |
|---|---|---|---|---|---|
| DOC-01 | P1 | Tài liệu | Intern A | Todo | Sửa BA/SRS theo quyết định mới: admin tự duyệt, wizard 5 bước, module đã tách; khôi phục liên kết task bị thiếu |
| CORE-01 | P1 | Core | Intern A | Todo | Hồi quy login/profile/users/org/settings/menu và core trống không nghiệp vụ |
| CORE-02 | P1 | Core | Intern C | Todo | Test tệp/audit/dynamic resource/jobs/outbox/webhook theo tenant và quyền |
| QA-01 | P1 | Kiểm thử | Intern C | Todo | Sửa 2 test frontend đang thất bại do kiểm chuỗi cấu trúc cũ |
| QA-02 | P1 | Kiểm thử | Intern C | Todo | Bật chạy kiểm thử backend trong CI, hiện package -DskipTests |
| QA-03 | P1 | Kiểm thử | Intern C | Todo | Đưa smoke test nghiệp vụ vào bộ test Git, fixture DB riêng |
| QA-04 | P1 | Kiểm thử | Intern C | Todo | Thêm test đồng thời và idempotency cho bán/nhận/trả/phân bổ |
| ACL-01 | P1 | Phân quyền | Intern A | Todo | Lập và chạy ma trận quyền theo chức năng, tenant, kho, trường nhạy cảm |
| ACL-02 | P1 | Phân quyền | Intern A | Todo | Test gán vai trò người dùng và ngoại lệ admin tự duyệt |
| OPS-01 | P1 | Thiết lập | Intern B | Todo | Nghiệm thu wizard với doanh nghiệp mới và tiếp tục lần đăng nhập sau |
| OPS-02 | P1 | Thiết lập | Intern B | Todo | Test đổi kho mặc định, ngừng địa điểm/kho và phạm vi nhân viên |
| OPS-03 | P1 | Import | Intern B | Todo | Dùng cùng validator cho preview và commit import |
| OPS-04 | P1 | Import | Intern B | Todo | Chống nhập trùng và hiển thị phiếu đầu kỳ sau commit |
| PRD-01 | P1 | Sản phẩm | Intern B | Todo | Thiết kế migration Product cha và liên kết SKU, backfill giữ ID cũ |
| PRD-02 | P1 | Sản phẩm | Intern B | Todo | API thuộc tính và giá trị cấu hình, unique và ngừng sử dụng |
| PRD-03 | P1 | Sản phẩm | Intern B | Todo | API tạo tổ hợp SKU từ thuộc tính đã chọn |
| PRD-04 | P1 | Sản phẩm | Intern B | Todo | Form sản phẩm đơn giản/có biến thể và danh sách SKU con |
| PRD-05 | P1 | Sản phẩm | Intern B | Todo | API cập nhật sản phẩm có version và liên kết nhóm/thương hiệu |
| PRD-06 | P1 | Sản phẩm | Intern B | Todo | Test quy đổi UOM và giữ snapshot hệ số trên chứng từ |
| PRD-07 | P2 | Sản phẩm | Intern B | Todo | Danh sách sản phẩm có lọc nhóm/brand/tracking/trạng thái và sửa hồ sơ |
| PRD-08 | P1 | Giá bán | Intern A | Todo | Test ưu tiên giá, UOM, khuyến mãi và giá 0 nhập rõ |
| PAR-01 | P1 | Customer Master | Intern A | Todo | Hồi quy dữ liệu chung giữa master, NCC, CRM và bán |
| PAR-02 | P2 | Customer Master | Intern A | Todo | Phân trang lịch sử khách hàng và lịch chăm sóc |
| SAL-01 | P1 | Chứng từ | Intern A | Todo | API sửa bản nháp có optimistic version, không sửa phiếu ghi sổ |
| SAL-02 | P1 | Chứng từ | Intern A | Todo | Nút sửa nháp/nhân bản có nguồn và xử lý xung đột |
| SAL-03 | P1 | POS | Intern A | Todo | Đồng bộ tồn bán được với FEFO, hết hạn và cách ly |
| SAL-04 | P1 | POS | Intern A | Todo | Tách lượng đủ và lượng thiếu thành đặt trước có xác nhận |
| SAL-05 | P1 | Đơn bán | Intern A | Todo | Hồi quy nhiều kho/nhiều xuất/cọc/thanh toán nhiều lần |
| SAL-06 | P1 | Hủy đơn | Intern A | Todo | Tách yêu cầu đóng đơn và người duyệt; giữ phần đã thực hiện |
| SAL-07 | P1 | Giao hàng | Intern A | Todo | API dòng giao và lượng thực giao cho từng đợt |
| SAL-08 | P1 | COD | Intern A | Todo | Chặn tổng COD gán vượt công nợ qua nhiều đợt |
| SAL-09 | P1 | COD | Intern A | Todo | Màn đối soát nhập tay có bảng kê, tiền gross/fee/net |
| PUR-01 | P1 | Mua hàng | Intern B | Todo | Nghiệm thu nhập trực tiếp/NCC/công nợ và chờ duyệt |
| PUR-02 | P1 | Mua hàng | Intern B | Todo | Test nhận6+4, thiếu/thừa/khác giá và quyền xác nhận |
| PUR-03 | P1 | Trả NCC | Intern C | Todo | Sổ tiền NCC phải hoàn sau trả mua vượt AP còn lại |
| STK-01 | P1 | Kho | Intern B | Todo | Test nhận chuyển ở kho đích với quyền chỉ kho đích |
| STK-02 | P1 | Chuyển kho | Intern B | Todo | Nhận cùng dòng hàng tốt/hỏng riêng trong một lần |
| STK-03 | P1 | Chuyển kho | Intern B | Todo | Luồng đề nghị/duyệt xử lý mất lượng đang chuyển |
| STK-04 | P1 | Cách ly | Intern B | Todo | API giải phóng/hủy/trả NCC hàng cách ly với duyệt |
| STK-05 | P1 | Kiểm kê | Intern B | Todo | Hồi quy khóa SKU theo kho và mở khóa khi kết thúc/hủy |
| STK-06 | P1 | Kiểm kê | Intern B | Todo | Tách số đếm tồn tốt và cách ly để SKU có cách ly kiểm được |
| FIN-01 | P1 | Công nợ | Intern C | Todo | Phân trang và tổng server cho thu chi/công nợ/ứng trước |
| FIN-02 | P1 | Thu chi | Intern C | Todo | Luồng đề nghị/duyệt đảo khoản thu chi đã phân bổ |
| RET-01 | P1 | Đổi trả | Intern C | Todo | Test trả nhiều lần/khác chi nhánh/serial/lô/hoàn |
| RET-02 | P1 | Đổi trả | Intern C | Todo | Sổ đối trừ tiền đổi hàng thay vì chỉ lưu exchangeReturnId |
| WAR-01 | P1 | Bảo hành | Intern A | Todo | Chặn tổng lượng bảo hành đang giữ vượt lượng bán |
| WAR-02 | P2 | Bảo hành | Intern A | Todo | Form chọn hóa đơn/dòng, người xử lý/hạn/kết quả/bàn giao |
| CRM-01 | P2 | CRM | Intern A | Todo | Nhắc chăm sóc có người phụ trách và kết quả hoàn thành |
| CRM-02 | P1 | Dashboard CRM | Intern C | Todo | API phân trang nhắc lịch và count tổng/quá hạn độc lập |
| FIN-03 | P2 | Hoa hồng | Intern C | Todo | Test và bổ sung lọc kỳ/danh sách hoa hồng có phân trang |
| REP-01 | P1 | Báo cáo | Intern C | Todo | API chỉ tiêu tổng hợp và lọc ngày/địa điểm/kho cho 6 tab |
| REP-02 | P1 | Báo cáo | Intern C | Todo | Báo cáo đối chiếu bán/mua/tồn/tiền/nợ, số thuần có dấu |
| REP-03 | P1 | Xuất dữ liệu | Intern C | Todo | Xuất đủ dữ liệu và cùng bộ lọc, tránh limit 1000 |
| REP-04 | P2 | In phiếu | Intern A | Todo | Mẫu phiếu bán80mm/A4 và nhãn tiền đã thu/còn nợ |
| PERF-01 | P1 | Hiệu năng | Intern C | Todo | API tìm/paging catalog, giảm tải lặp capabilities và defaults.ensure |
| MOD-01 | P1 | Tách module | Intern C | Todo | Test và kiểm manifest phụ thuộc FE/BE/source bundle đồng nhất trong CI |
| MOD-02 | P2 | Tách module | Intern A | Todo | Giảm engine/form dùng chung: tách handler và UI theo module có contract |
| MOD-03 | P1 | Bàn giao | Intern C | Todo | Diễn tập cài core trống/import/nâng bản/backup restore trên DB test |
| MOB-01 | P2 | Mobile | Intern A | Todo | Checklist toàn màn/modal/bàn phím trên390/360px và iOS Safari thật |
| REL-01 | P1 | Vận hành | Intern C | Todo | Lập release manifest và checklist source/local/server, log sức khỏe |

### Tài liệu

**DOC-01 — Sửa BA/SRS theo quyết định mới: admin tự duyệt, wizard 5 bước, module đã tách; khôi phục liên kết task bị thiếu**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Tài liệu không còn OPEN-04 cho admin; không phục hồi COD/giảm dòng/phí mua đã bỏ khỏi form.
- Điểm bắt đầu: `docs/SRS_HIEN_HANH.md` (bản thay thế hiện hành); phụ thuộc: không.

### Core

**CORE-01 — Hồi quy login/profile/users/org/settings/menu và core trống không nghiệp vụ**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Đổi vai trò đúng revision; nhân viên không mở admin; home core khác Tổng quan; login local không MFA/ép đổi lần đầu.
- Điểm bắt đầu: `backend/src/main/java/vn/coreplatform/controlplane/ControlPlaneController.java`; phụ thuộc: QA-01, QA-02.

**CORE-02 — Test tệp/audit/dynamic resource/jobs/outbox/webhook theo tenant và quyền**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Không đọc tệp/audit tenant khác; audit chỉ đọc; job/outbox retry có dấu vết; webhook xác thực/replay; không lộ token.
- Điểm bắt đầu: `backend/src/test/java/vn/coreplatform/permission/TenantIsolationTest.java`; phụ thuộc: QA-02, ACL-01.

### Kiểm thử

**QA-01 — Sửa 2 test frontend đang thất bại do kiểm chuỗi cấu trúc cũ**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Test kiểm hành vi menu/module và demo thay vì bắt core phải import demo; npm test qua.
- Điểm bắt đầu: `frontend/tests/rendered-html.test.mjs:63`; phụ thuộc: không.

**QA-02 — Bật chạy kiểm thử backend trong CI, hiện package -DskipTests**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **—**, backend **Có**.
- Nghiệm thu: CI chạy verify với PostgreSQL/Testcontainers; test fail làm job fail; có surefire report.
- Điểm bắt đầu: `.github/workflows/backend-ci.yml:30`; phụ thuộc: không.

**QA-03 — Đưa smoke test nghiệp vụ vào bộ test Git, fixture DB riêng**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Mua 10 duyệt, bán 2 còn 8; chưa duyệt không đổi tồn/nợ; không ghi DB vận hành.
- Điểm bắt đầu: `backend/src/test/java/vn/aicrm/BusinessBoundaryTest.java`; phụ thuộc: QA-02.

**QA-04 — Thêm test đồng thời và idempotency cho bán/nhận/trả/phân bổ**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Bán chiếc cuối chỉ 1 thành công; replay 1 hiệu ứng; cùng key khác body 409; rollback đủ tồn/tiền/nợ.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailCommands.java:20`; phụ thuộc: QA-03.

### Phân quyền

**ACL-01 — Lập và chạy ma trận quyền theo chức năng, tenant, kho, trường nhạy cảm**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: 2 tenant và 2 kho; thu hồi có hiệu lực phiên cũ; API/detail/export/print không lộ giá vốn/ngân hàng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailPermissions.java`; phụ thuộc: QA-02.

**ACL-02 — Test gán vai trò người dùng và ngoại lệ admin tự duyệt**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: admin@core.local + platform-admin tự duyệt được; user thường không; stale revision và tự sửa vai trò bị chặn.
- Điểm bắt đầu: `backend/src/main/java/vn/coreplatform/permission/UserRoleController.java`; phụ thuộc: ACL-01.

### Thiết lập

**OPS-01 — Nghiệm thu wizard với doanh nghiệp mới và tiếp tục lần đăng nhập sau**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Bước 1 công ty/địa chỉ/địa điểm; có 1 kho; chỉ hiện chọn khi nhiều; không sinh hàng/giao dịch demo.
- Điểm bắt đầu: `frontend/app/retail/location-workspace.tsx`; phụ thuộc: DOC-01.

**OPS-02 — Test đổi kho mặc định, ngừng địa điểm/kho và phạm vi nhân viên**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Không ngừng điểm/kho mặc định cuối; kho ngừng không tạo phiếu mới; lịch sử vẫn truy cập theo quyền.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/operations/RetailLocations.java`; phụ thuộc: OPS-01.

### Import

**OPS-03 — Dùng cùng validator cho preview và commit import**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: SKU tracking sai/đơn vị sai/VAT âm/serial thiếu báo lỗi ngay preview; commit lỗi rollback cả batch.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/operations/RetailImport.java:21`; phụ thuộc: QA-03.

**OPS-04 — Chống nhập trùng và hiển thị phiếu đầu kỳ sau commit**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Checksum gồm kind/tenant/kho; replay không nhân đôi; dẫn đến phiếu nháp để duyệt; file 500 dòng/2MB đúng giới hạn.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/operations/RetailImport.java:21`; phụ thuộc: OPS-03.

### Sản phẩm

**PRD-01 — Thiết kế migration Product cha và liên kết SKU, backfill giữ ID cũ**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: Không đổi ID SKU/movement/line; sản phẩm thường dùng được; cha có biến thể không có tồn/giao dịch.
- Điểm bắt đầu: `backend/src/main/resources/db/migration/V24__aicrm_retail.sql`; phụ thuộc: DOC-01.

**PRD-02 — API thuộc tính và giá trị cấu hình, unique và ngừng sử dụng**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Màu có Đỏ/Xanh bằng ID; giá trị sai thuộc tính hoặc ngừng bị chặn khi tạo biến thể; không sửa lịch sử.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/products/RetailWarehouseConfigService.java:51`; phụ thuộc: PRD-01.

**PRD-03 — API tạo tổ hợp SKU từ thuộc tính đã chọn**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: Đỏ + M chỉ 1 SKU kể cả 2 request đồng thời; chặn SKU cha tại API mua/bán/kho.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/products/RetailCatalog.java:90`; phụ thuộc: PRD-02.

**PRD-04 — Form sản phẩm đơn giản/có biến thể và danh sách SKU con**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Theo PRD-03**.
- Nghiệm thu: Chọn giá trị sẵn, xem trước tổ hợp; bỏ nhập màu/size tự do cho sản phẩm mới; giá/tồn theo SKU.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx:167`; phụ thuộc: PRD-03.

**PRD-05 — API cập nhật sản phẩm có version và liên kết nhóm/thương hiệu**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Chưa**.
- Nghiệm thu: Đổi tên/giá/chính sách không đổi snapshot phiếu cũ; không đổi tracking/đơn vị gốc khi có tồn không qua quy trình.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/products/RetailCatalog.java`; phụ thuộc: PRD-01.

**PRD-06 — Test quy đổi UOM và giữ snapshot hệ số trên chứng từ**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: 1 thùng=24 chai, bán 3 còn21; hệ số mới30 không đổi phiếu đã ghi; đối chiếu JSON và uom_conversion.
- Điểm bắt đầu: `backend/src/main/resources/db/migration/V29__product_uom_conversion.sql:9`; phụ thuộc: QA-03.

**PRD-07 — Danh sách sản phẩm có lọc nhóm/brand/tracking/trạng thái và sửa hồ sơ**

- Ưu tiên: P2; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Theo PRD-05**.
- Nghiệm thu: Lọc/phân trang phía server, STT đúng page; không nạp toàn catalog khi chỉ mở danh sách.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx`; phụ thuộc: PRD-05, PERF-01.

### Giá bán

**PRD-08 — Test ưu tiên giá, UOM, khuyến mãi và giá 0 nhập rõ**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Giá khách ưu tiên nhóm; rule giá0 hợp lệ; thiếu giá phải nhập; source giữ giá; không ghi phản hồi cũ.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/products/RetailCatalog.java:218`; phụ thuộc: QA-03, PRD-06.

### Customer Master

**PAR-01 — Hồi quy dữ liệu chung giữa master, NCC, CRM và bán**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Sửa 1 hồ sơ phản ánh các màn; nhiều vai trò 1 ID; khách tạo nhanh giữ giỏ, không cấp quyền sửa ngân hàng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/partners/RetailPartners.java`; phụ thuộc: ACL-01.

**PAR-02 — Phân trang lịch sử khách hàng và lịch chăm sóc**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Hơn500 giao dịch vẫn tìm được; tổng không tính trên mẫu bị cắt; không vượt quyền chi nhánh.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/partners/RetailPartners.java:22`; phụ thuộc: PERF-01.

### Chứng từ

**SAL-01 — API sửa bản nháp có optimistic version, không sửa phiếu ghi sổ**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: 2 người sửa: người sau409; chưa ghi tồn/tiền; nội dung thay đổi phải qua xác nhận mới.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:93`; phụ thuộc: QA-04.

**SAL-02 — Nút sửa nháp/nhân bản có nguồn và xử lý xung đột**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Theo SAL-01**.
- Nghiệm thu: Sửa khách, kho, dòng; giữ lỗi trên form; không có nút sửa nội dung phiếu đã ghi; bản sao có mã mới.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx:123`; phụ thuộc: SAL-01.

### POS

**SAL-03 — Đồng bộ tồn bán được với FEFO, hết hạn và cách ly**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Có10 nhưng8 hết hạn thì bán được2; UI/API cùng kết quả; server vẫn kiểm lại tại post.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/inventory/RetailInventory.java:23`; phụ thuộc: QA-03.

**SAL-04 — Tách lượng đủ và lượng thiếu thành đặt trước có xác nhận**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Đặt5 tồn3: khách chọn bán3 + đặt2; liên kết phiếu; không tồn âm/ghi nợ phần chưa xuất.
- Điểm bắt đầu: `frontend/app/retail/sales/pos-editor.tsx`; phụ thuộc: SAL-03.

### Đơn bán

**SAL-05 — Hồi quy nhiều kho/nhiều xuất/cọc/thanh toán nhiều lần**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Đơn1tr cọc200k, xuất600k+400k: doanh thu1tr, còn800k; không dùng lại cọc; progress đúng từng dòng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:117`; phụ thuộc: QA-04.

### Hủy đơn

**SAL-06 — Tách yêu cầu đóng đơn và người duyệt; giữ phần đã thực hiện**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Sale chỉ yêu cầu/lý do; người có quyền duyệt mới đóng remaining; admin ngoại lệ; hoàn cọc chỉ phần dư.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:104`; phụ thuộc: ACL-02, SAL-05.

### Giao hàng

**SAL-07 — API dòng giao và lượng thực giao cho từng đợt**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Xuất10 giao4+6; vượt lượng bị chặn; thất bại không tự hủy/trả; không ghi doanh thu/nợ lại.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/sales/RetailOperations.java:17`; phụ thuộc: SAL-05.

### COD

**SAL-08 — Chặn tổng COD gán vượt công nợ qua nhiều đợt**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Nợ500k: gán300k rồi250k bị chặn; concurrent/replay an toàn; giảm nợ sau đối soát đúng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/sales/RetailOperations.java:17`; phụ thuộc: QA-04.

**SAL-09 — Màn đối soát nhập tay có bảng kê, tiền gross/fee/net**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: COD500k phí25k: giảm nợ500k, thu ròng475k, chi25k; không settle lần hai; không tích hợp hãng.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx:198`; phụ thuộc: SAL-08.

### Mua hàng

**PUR-01 — Nghiệm thu nhập trực tiếp/NCC/công nợ và chờ duyệt**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: 10x100k chưa trả: chờ chưa tăng tồn/nợ; duyệt tồn10/AP1tr; giữ form không phí mua/giảm dòng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/purchase/PurchaseDocuments.java`; phụ thuộc: QA-03, PAR-01.

**PUR-02 — Test nhận6+4, thiếu/thừa/khác giá và quyền xác nhận**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Nhận vượt khi thiếu CONFIRM_DISCREPANCY bị chặn; đúng quyền có lý do; không vượt khi concurrent.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:117`; phụ thuộc: QA-04.

### Trả NCC

**PUR-03 — Sổ tiền NCC phải hoàn sau trả mua vượt AP còn lại**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Trả300k còn nợ100k: giảm AP100k, claim200k; thu150k+50k tất toán; vượt200k bị chặn.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/accounting/RetailFinance.java:44`; phụ thuộc: FIN-01, PUR-01.

### Kho

**STK-01 — Test nhận chuyển ở kho đích với quyền chỉ kho đích**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Đích nhận được, không xuất nguồn/đọc tồn nguồn khác; chỉ nguồn không nhận đích; tenant khác bị chặn.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailPermissions.java:90`; phụ thuộc: ACL-01.

### Chuyển kho

**STK-02 — Nhận cùng dòng hàng tốt/hỏng riêng trong một lần**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Có một phần**.
- Nghiệm thu: Gửi10 nhận6 gồm5 tốt1 hỏng: đíchqty6 cáchly1, transit4; mỗi serial/lô đúng nguồn.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx:162`; phụ thuộc: STK-01.

**STK-03 — Luồng đề nghị/duyệt xử lý mất lượng đang chuyển**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: Gửi10 nhận6, mất4 chỉ đóng sau duyệt; không giảm nguồn lần hai; lượng/giá trị đối chiếu đủ.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:162`; phụ thuộc: STK-02.

### Cách ly

**STK-04 — API giải phóng/hủy/trả NCC hàng cách ly với duyệt**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: Release không tăng tổng lượng; hủy giảm lượng/vốn đúng; giữ serial/lô và audit; chặn vượt cáchly.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/inventory/RetailInventory.java`; phụ thuộc: QA-04.

### Kiểm kê

**STK-05 — Hồi quy khóa SKU theo kho và mở khóa khi kết thúc/hủy**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: X/A khóa cả PENDING; Y/A và X/B chạy; duyệt10->8 ghi-2; lô0/serial thiếu đối chiếu đúng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/inventory/RetailInventory.java:21`; phụ thuộc: QA-04.

**STK-06 — Tách số đếm tồn tốt và cách ly để SKU có cách ly kiểm được**

- Ưu tiên: P1; phụ trách: Intern B; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Hiện COUNT_QUARANTINE chặn; sau sửa tổng=good+quarantine, chênh đúng trạng thái; không bán hàng cáchly.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:97`; phụ thuộc: STK-04, STK-05.

### Công nợ

**FIN-01 — Phân trang và tổng server cho thu chi/công nợ/ứng trước**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: 1501 dòng đọc đủ qua page; totals toàn bộ filter; loại tiền/partner/phạm vi đúng; không vượtallocated.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/accounting/RetailFinance.java:50`; phụ thuộc: QA-04.

### Thu chi

**FIN-02 — Luồng đề nghị/duyệt đảo khoản thu chi đã phân bổ**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Chưa**, backend **Chưa**.
- Nghiệm thu: Đảo thu150k mởAR150k; dòng đảo liên kết gốc; không xóa nguồn; đảo trùng bị chặn.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/accounting/AccountingController.java:21`; phụ thuộc: FIN-01, ACL-02.

### Đổi trả

**RET-01 — Test trả nhiều lần/khác chi nhánh/serial/lô/hoàn**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Trả300k đã thu200k: giảmnợ100k hoàn<=200k; serial sai/trả vượt chặn; kho nhận và nguồn đối chiếu.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:132`; phụ thuộc: QA-04.

**RET-02 — Sổ đối trừ tiền đổi hàng thay vì chỉ lưu exchangeReturnId**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Trả200k mua250k thu50k; chỉ1 lần dùngcredit; hoàn và đổi không cùng tiêu một quyền lợi; không thu giả200k.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailDocuments.java:44`; phụ thuộc: RET-01, FIN-01.

### Bảo hành

**WAR-01 — Chặn tổng lượng bảo hành đang giữ vượt lượng bán**

- Ưu tiên: P1; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Bán2, nhận2 chưa bàn giao: không nhận thêm1; serial qty luôn1; serial đúng bán; tenant/nguồn hợp lệ.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/sales/RetailOperations.java:27`; phụ thuộc: QA-04.

**WAR-02 — Form chọn hóa đơn/dòng, người xử lý/hạn/kết quả/bàn giao**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Không nhập UUID; nhận->xử lý->sẵn sàng->bàn giao lưu ai/khi nào; không tăng tồn bán.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx:198`; phụ thuộc: WAR-01.

### CRM

**CRM-01 — Nhắc chăm sóc có người phụ trách và kết quả hoàn thành**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Lưu assignee/result/completedAt, sửa lịch version; giờ Việt Nam; giữ lịch sử; đúng phạm vi.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/crm/CustomerCare.java:17`; phụ thuộc: PAR-01.

### Dashboard CRM

**CRM-02 — API phân trang nhắc lịch và count tổng/quá hạn độc lập**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: Có25 nhắc thì count25, không20; lọc người phụ trách/chi nhánh/kho; không dựa reminders.length.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/overview/Overview.java:16`; phụ thuộc: CRM-01.

### Hoa hồng

**FIN-03 — Test và bổ sung lọc kỳ/danh sách hoa hồng có phân trang**

- Ưu tiên: P2; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Theo đơn300k+bổ sung200k=500k, chi<=500k; điều chỉnh âm đúng kỳ; không ghi lại tổng tháng.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/accounting/RetailCommissions.java:31`; phụ thuộc: FIN-01.

### Báo cáo

**REP-01 — API chỉ tiêu tổng hợp và lọc ngày/địa điểm/kho cho 6 tab**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có một phần**.
- Nghiệm thu: Dashboard không tải toàn lịch sử phiếu; totals theo server và quyền; drilldown giữ filter; nợ không bị1000cắt.
- Điểm bắt đầu: `frontend/app/retail/overview-dashboard.tsx:12`; phụ thuộc: FIN-01, CRM-02.

**REP-02 — Báo cáo đối chiếu bán/mua/tồn/tiền/nợ, số thuần có dấu**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Một phần**.
- Nghiệm thu: Sales net trừ trả; AP/AR khớp sổ; opening+in-out=closing theo ngày VN; không cộng reversal như khoản mới.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/reports/Reports.java:17`; phụ thuộc: FIN-02, RET-02.

### Xuất dữ liệu

**REP-03 — Xuất đủ dữ liệu và cùng bộ lọc, tránh limit 1000**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Một phần**.
- Nghiệm thu: 1501 thu chi/movement xuất1501; filter/permission COST đúng; report tồn có xuất riêng; CSV an toàn công thức.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailController.java:29`; phụ thuộc: FIN-01, REP-02.

### In phiếu

**REP-04 — Mẫu phiếu bán80mm/A4 và nhãn tiền đã thu/còn nợ**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Một phần**, backend **Có**.
- Nghiệm thu: In từ posted snapshot; phân biệt lúc bán/hiện tại; không in sidebar; giữ quyền PRINT.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/shared/RetailController.java:24`; phụ thuộc: RET-01.

### Hiệu năng

**PERF-01 — API tìm/paging catalog, giảm tải lặp capabilities và defaults.ensure**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: TìmSKU/barcode/khách trên server; không tải toàn master mỗi màn; không bỏ scope/module gate; đo 10kSKU/5kđối tác.
- Điểm bắt đầu: `backend/src/main/java/vn/aicrm/products/RetailCatalog.java:43`; phụ thuộc: ACL-01.

### Tách module

**MOD-01 — Test và kiểm manifest phụ thuộc FE/BE/source bundle đồng nhất trong CI**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Core trống login; products riêng chạy; thiếu/vòng/chặn tắt/API403; test package manager chạy khi scripts đổi.
- Điểm bắt đầu: `scripts/module-manager.test.mjs`; phụ thuộc: QA-02.

**MOD-02 — Giảm engine/form dùng chung: tách handler và UI theo module có contract**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có nhưng dùng chung**, backend **Có nhưng dùng chung**.
- Nghiệm thu: Core không import business; SharedWorkspace không ôm tất cả màn; test giữ hành vi; không đổi API khi chưa có version.
- Điểm bắt đầu: `frontend/app/retail/shared-workspace.tsx`; phụ thuộc: MOD-01.

### Bàn giao

**MOD-03 — Diễn tập cài core trống/import/nâng bản/backup restore trên DB test**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 2 ngày; trạng thái: Todo.
- Hiện có: frontend **Có CLI**, backend **Có CLI**.
- Nghiệm thu: Build/restart theo tài liệu; migrate không mất tồn/nợ; restore cả DB/files; xung đột custom được báo, không ghi đè.
- Điểm bắt đầu: `scripts/module-manager.mjs`; phụ thuộc: MOD-01, QA-03.

### Mobile

**MOB-01 — Checklist toàn màn/modal/bàn phím trên390/360px và iOS Safari thật**

- Ưu tiên: P2; phụ trách: Intern A; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **—**.
- Nghiệm thu: Không zoom ô login; nút lưu thấy khi bàn phím mở; bảng cuộn trong vùng; focus/dialog/labels dùng được.
- Điểm bắt đầu: `frontend/app/components/mobile-app-chrome.tsx`; phụ thuộc: QA-01.

### Vận hành

**REL-01 — Lập release manifest và checklist source/local/server, log sức khỏe**

- Ưu tiên: P1; phụ trách: Intern C; dự kiến lát cắt: 1 ngày; trạng thái: Todo.
- Hiện có: frontend **Có**, backend **Có**.
- Nghiệm thu: Ghi commit/image/migration/moduleversions; health + smoke sau release; secrets không vàoGit; có rollback; chỉ deploy khi được yêu cầu.
- Điểm bắt đầu: `docs/BUSINESS_MODULES_PHASE3_PROGRESS.md`; phụ thuộc: MOD-03.

## 6. Thứ tự triển khai cho3 intern

| Đợt | Intern A | Intern B | Intern C | Điều kiện ra đợt |
|---|---|---|---|---|
| 1: baseline/test | DOC-01, ACL-01/02, PAR-01 | OPS-01/02, PUR-01, PRD-06 | QA-01/02/03, MOD-01 | CI có test, luồng nhập–bán–tồn smoke qua, tài liệu không mâu thuẫn |
| 2: sửa thiếu hiện hữu | SAL-03, SAL-08, WAR-01, SAL-05 | OPS-03/04, PUR-02, STK-01/05 | QA-04, FIN-01, PERF-01 | Không âm/không thu vượt/không cắt export sau khi REP-03 hoàn tất |
| 3: master/chứng từ | SAL-01/02/06 | PRD-01..05, STK-02 | FIN-02, PUR-03, RET-01/02 | Migration và sổ có reviewer duyệt; hồi quy giá/UOM/nguồn |
| 4: luồng mở rộng chuẩn | SAL-04/07/09, WAR-02, CRM-01 | STK-03/04/06, PRD-07 | CRM-02, REP-01/02/03, FIN-03 | Đối chiếu mua/bán/tồn/tiền/nợ cùng dataset |
| 5: hoàn thiện bàn giao | PRD-08, REP-04, MOB-01, MOD-02 | Test chéo sản phẩm/mua/kho và import | MOD-03, REL-01 | Build clean, restore và regression đủ; release theo yêu cầu riêng |

Đây là các đợt theo phụ thuộc, không phải5 tuần cố định. Không giao tất cả task trong một sprint. Đợt1 nên chốt ngay reviewer và8–10 task sẵn sàng; các task thuộc tiền/tồn có thể cần lập trình viên có kinh nghiệm cùng làm.

## 7. Tiêu chuẩn hoàn thành và file theo dõi

Mỗi task phải có: FE/BE đã làm, migration nếu có, quyền/module gate, xử lý loading/empty/error, testcase có dữ liệu/kết quả mong đợi, bằng chứng review, commit/PR và trạng thái. Chứng từ đã ghi giữ snapshot; dùng BigDecimal VND/quantity phía backend; không dùng cập nhật DB tay thay sổ. Test tạo dữ liệu chỉ ở DB test, không DB local vận hành/server. Migration mới dùng Flyway phiên bản tiếp theo, không sửa checksum migration cũ.

File máy đọc: [DEV_TASKS_2026-10-09.json](./DEV_TASKS_2026-10-09.json). Cập nhật từng task status: Todo → InProgress → Review → Done, hoặc Blocked kèm lý do. Các trường commit/testEvidence/completedAt để trống lúc khởi tạo. File này là backlog mới, không tự cập nhật trạng thái theo tên progress cũ.

## 8. Quyết định còn mở và ngoài phạm vi

- Hàng đã sử dụng: BA Q-03 còn cần chính sách theo sản phẩm/ngoại lệ. Có thể thiết kế fields/ca test, chưa tự đổi mặc định nhận trả. Không yêu cầu lại ngoại lệ tự duyệt admin vì đã xác nhận và đã code.
- Ngày nghiệp vụ/ghi lùi/khóa kỳ: chốt nếu cần bổ sung; không tự thêm quy trình ERP phức tạp cho SME. Hạn thanh toán trên partner đã có.
- Bên chịu phí mua/giao/hoàn phí: chốt trước khi đổi sổ phí; không đưa các trường phí đã bỏ về form. COD nhập tay hiện có được kiểm đúng chính sách hiện hữu.
- Chưa giao task triển khai: offline/ca, API hãng vận chuyển/sàn, kế toán pháp định/hóa đơn điện tử, phí mua đến muộn, SaaS thuê bao/tự tách DB, hotreload JAR.
- Tách module đã có source bundle+CLI; không mô tả là import vào bất kỳ core cũ hoặc upload nóng qua web.

## 9. Bằng chứng và giới hạn kiểm tra

Source đã đối chiếu các controller/service nghiệp vụ, migration V24–V33, registry/module manager, các frontend entry/editor/sharedworkspace và workflow CI. Bằng chứng trước08/10 nằm trong BUSINESS_MODULES_PHASE3_PROGRESS.md, không tính là test chạy mới hôm nay.

### Kết quả chạy mới trên local

- 25/25 API GET trả200 với admin; thời gian một lượt 19–40ms trên dữ liệu hiện có. Đây không phải benchmark tải lớn hoặc p95.
- 54 lượt mở:27màn nghiệp vụ/phân quyền × desktop1440px và mobile390px. Có heading đúng từng màn, không ghi nhận pageerror, role=alert hay tràn ngang toàn trang trong smoke. Chưa nghiệm thu tất cả nút/form và iOS thật.
- Smoke UI dùng khoảng chờ cố định1,5giây; durationMs của UI gồm thời gian chờ này, không dùng làm thời gian tải sản phẩm.
- Test frontend chọn lọc3ca:1qua,2fail (demo import + cấu trúc menu cũ). Không chạy lại toàn bộ npm test hoặc toàn bộ Maven tests trong lần audit này.
- Không gửi API ghi nghiệp vụ từ browser/không lập chứng từ; chỉ đăng nhập tạo phiên và đọc dữ liệu. GET catalog có cơ chế ensure mặc định trong source, vì vậy đây không phải chứng nhận database tuyệt đối không có bất kỳ write nào.

Kết quả smoke đọc local09/10 được lưu tại [audit/SYSTEM_AUDIT_READONLY_2026-10-09.json](./audit/SYSTEM_AUDIT_READONLY_2026-10-09.json). Smoke GET/admin và render màn hình không thay thế test giao dịch, concurrency, multi-tenant hay iOS Safari thật; các việc đó được giao QA-03/04/ACL-01/MOB-01. Không kiểm tốc độ production hoặc version server trong lần rà soát này. Không kết luận tất cả luồng đã nghiệm thu vì màn hình mở được.
