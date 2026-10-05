# Tiến độ module Bán hàng — nguồn theo dõi duy nhất

Cập nhật: 05/10/2026. Căn cứ [BA 2.0](./BA_HE_THONG_v2.0.md), [SRS 2.0](./SRS_HE_THONG_v2.0.md). Phạm vi: Bán tại quầy và Đơn bán hàng. Không coi giao diện có nút là nghiệp vụ đã hoàn thành. “Có backend” dưới đây là đối chiếu mã nguồn, chưa thay UAT. Các chức năng còn thiếu được ghi cụ thể để intern nhận đúng phần việc.

## 1. Tổng trạng thái

- Frontend đã tách riêng `sales-workspace.tsx` và `sales.css`, được route qua RetailWorkspace. Các module ngoài Bán hàng tiếp tục dùng màn hiện tại.
- Dùng API thật hiện có: danh mục, tồn, giá, chứng từ/nguồn/submit, cọc/thu/phân bổ, giao hàng và đối soát COD. Không tạo dữ liệu demo vào DB vận hành.
- Tổng tiền/thuế xác nhận lấy từ **phiếu nháp máy chủ**, không lấy phép tính ước lượng frontend làm số ghi sổ. Quy trình hiện tại là chọn hàng → lưu nháp kiểm tra tiền → xác nhận/gửi duyệt.
- Backend đã có `RetailCommands` với Idempotency-Key, lưu kết quả và transaction/advisory lock theo tenant. Frontend kế thừa; không tuyên bố chưa có chống ghi trùng. Khi mất phản hồi lúc tạo, khóa nội dung lần gửi và cho thử lại cùng request/key.
- Chưa đạt nghiệm thu toàn SRS: mô hình sản phẩm cha–biến thể, sửa nháp, duyệt yêu cầu hủy, lượng giao từng dòng và bù tiền đổi còn thiếu backend. Không bật đóng đơn trực tiếp để thay luồng duyệt hủy.

## 2. Ma trận frontend / backend

| ID | Chức năng / SRS | Frontend trong lượt này | Backend hiện tại | API / service | Việc còn lại |
|---|---|---|---|---|---|
| SALES-01 | Màn quầy / FR-11 | Có bố cục chọn sản phẩm + giỏ, search/barcode Enter, SKU/variant label | Có danh mục SKU độc lập | GET catalog / RetailCatalog | Tìm product cha→SKU sau khi T-11…13 hoàn thành; backend chưa có tổ hợp cấu hình sẵn |
| SALES-02 | Kho/khách/kênh/UOM | Có, chỉ lựa chọn active; customer bắt buộc đơn, conditional còn nợ; không thêm hàng tại quầy | Có catalog/scope/UOM normalized | catalog, stock, uom_conversion | Kho làm việc mặc định theo tài khoản chưa có backend riêng; số tồn khả dụng còn bao gồm lượng lô hết hạn |
| SALES-03 | Lô/hạn/serial / FR-11/14 | Trường conditional LOT/SERIAL, FEFO khi để lô trống | Có validation FEFO/expiry/serial khi post | RetailInventory / documents | Picker lô/serial nâng trải nghiệm sau; không dùng số available làm cam kết hàng còn hạn |
| SALES-04 | Giá/ưu đãi/VAT / FR-06 | Tra giá cơ sở theo chính sách, tùy chọn giá/giảm/VAT/taxInclusive; tổng chuẩn xem trên nháp | Có engine/snapshot/tính tiền | POST price; POST documents | Chưa có endpoint preview tổng phiếu không lưu nháp; giữ 2 bước, không tự tính tổng ghi sổ |
| SALES-05 | Lưu nháp/xác nhận quầy | Có, khóa form sau lưu; reload list/detail; xử lý mất phản hồi tạo cùng key | Có create/submit/idempotency | documents; RetailCommands | Sửa nháp chưa có PATCH; hủy nháp rồi lập lại hoặc giữ nháp |
| SALES-06 | Giảm ngoài chính sách/below cost | Có lý do, pending/selfapproval disabled, approve/reject nếu quyền đủ | Có backend kiểm ngoại lệ/approve | documents/{id}/submit/approve/reject | UAT vai trò; backend đang kiểm SELL + APPROVE cho duyệt bán |
| SALES-07 | Bán chịu/thu ngay / FR-11/17 | Có paid/method, chọn khách nếu thiếu; nợ exact từ openItems trên detail | Có AR/payment/allocation, không creditlimit block | RetailDocuments / RetailFinance | Frontend validation paid≤total dựa kiểm server khi tạo; tiền thối không ghi thành thu |
| SALES-08 | Đặt trước / FR-11/12 | Có chuyển toàn giỏ thành SALE_ORDER có xác nhận; paid reset0, giữ hàng; cọc sau xác nhận | Có đơn không trừ tồn | documents type SALE_ORDER | Chưa tự tách thiếu thành sale phần đủ + preorder phần thiếu trong một command |
| SALES-09 | Danh sách / FR-12 | Tab order/quote/issue/POS; q/state; paging50/STT/tổng; empty/loading/error | Có filter type/status/q/page | GET documents | Backend chưa filter branch/kho/ngày; UI ghi rõ toàn scope. Không tạo filter giả chỉ áp trên trang |
| SALES-10 | Báo giá→đơn | Có form/hiệu lực/source, chuyển approved quote thành order | Có source/snapshot | documents QUOTE→SALE_ORDER | Backend chưa chặn chuyển quote hết hạn đầy đủ; form ngày chỉ hỗ trợ nhập |
| SALES-11 | Đơn online/sỉ/CTV | Có khách/kênh/address/COD/fee/note | Có metadata và kênh | documents SALE_ORDER | Tách người mua và CTV giới thiệu/commission link chưa có; không tự tính hoa hồng |
| SALES-12 | Xuất nhiều lần/nhiều kho | Có chọn kho thực xuất, qty remaining, 0/bỏ dòng xuất sau; bảng phiếu xuất liên quan | Có sourceLine/progress/snapshot/warehouse | documents ISSUE | Backend chưa GET children riêng: frontend đọc các trang ISSUE rồi lọc source_id; cần API quan hệ nguồn khi tăng dữ liệu |
| SALES-13 | Cọc và thu nợ | Có dialog RECEIPT gắn đơn/phiếu, phương thức/reference; auto allocation openItem khi thu nợ | Có advance/order allocation và finance | payments; finance; document.openItems | Khoản tiền cùng đơn/phiếu lấy finance giới hạn1.000; không coi list cắt là tổng toàn bộ; depositrefund separate finance flow |
| SALES-14 | Tiến độ đơn | Detail lượng đã xuất/còn theo dòng, khoản tiền riêng, phiếu xuất và đợt giao riêng | Có progress nhưng không đủ summaryđơn | lines.progress; payments; deliveries | Chưa có summary tổng trạng thái tiền/giao toàn đơn đầy đủ; UI không bịa nhãn “đã thanh toán” dựa paid cũ |
| SALES-15 | Giao manual/COD | Có tạo carrier/tracking/cod/fee, status validnext, settle gross/fee/net/method | Có deliveries/status/settle | RetailOperations | Giới hạn1.000 deliveries, chưa qty giao từng dòng, chưa kiểm tổng COD gán nhiều đợt đầy đủ/bảng kê |
| SALES-16 | Hủy/đóng / FR-13 | Hủy nháp có lý do; nút yêu cầu hủy đơn có hiệu lực disabled và giải thích | Có cancel nháp/close trực tiếp, **chưa duyệt close request** | documents/cancel/close | T-34/35 backend approval action; không gọi close trực tiếp từ frontend mới |
| SALES-17 | Đổi trả/bảo hành | Link sang module hiện tại từ phiếu posted, số phiếu nguồn ở detail | Có return/warranty core; thiếu exchangeledger/USEDpolicy | retail-returns / RetailOperations | Chưa tạo frontend đổi trả mới trong module Bán; T-51…55, chính sách USED chờ chốt |
| SALES-18 | In phiếu | Có print detail, SKU/tracking/fee/tổng/đã phân bổ/nợ hiện tại; CSS loại sidebar và panel khác | Có documentdetail data | window.print + detail | Chưa chuẩn80mm/brandbusinessprofile; cần QA máy in và template T-65 |
| SALES-19 | Xuất dữ liệu | Chưa tích hợp nút xuất ở frontend Bán mới | Có export CSV documents toàn scope, **chưa filter đúng tab/query/status** | GET export?kind=documents | Bổ sung export bán có filter/audit, không xuất tất cả nghiệp vụ rồi báo theo tab; T-65 |
| SALES-20 | READ/SELL/FINANCE/APPROVE | Nút theo quyền; list/detail đọc scope server, không tải finance khi thiếu FINANCE | Có serverpermission/scope | RetailAccess | UAT hai tenant/kho và revoke; toàn bộscope/roletemplate upgrade T-03/04 riêng |

## 3. File và trách nhiệm

| File | Vai trò |
|---|---|
| frontend/app/retail/sales-workspace.tsx | Frontend Bán riêng, gọi backend thật, xử lý giỏ/nguồn/commands/related |
| frontend/app/retail/sales.css | CSS riêng theo nền Core, responsive và print |
| frontend/app/retail/workspace.tsx | Router chọn SalesWorkspace cho retail-pos/retail-orders; legacy giữ các module khác |
| frontend/app/page.tsx | ApiProblemError bổ sung HTTP status cho phân biệt lỗi validation và mất phản hồi; không thay auth policy |
| backend RetailCatalog/Documents/Inventory/Finance/Operations/Commands | Kế thừa source hiện tại; chưa sửa nghiệp vụ backend trong lượt frontend này |

## 4. Kiểm tra và bằng chứng

| Check | Trạng thái | Bằng chứng/giới hạn |
|---|---|---|
| TypeScript | Đạt | Next production build kiểm TypeScript thành công |
| ESLint | Đạt, 0 lỗi | File frontend mới không warning; 11 warning sẵn có ở page.tsx/workspace.tsx |
| Production build | Đạt | Docker build frontend / Next production build thành công |
| Kiểm trình duyệt | Đạt 6 kiểm tra | Playwright + Edge: POS thật chỉ đọc; fixture kiểm retry cùng key/payload, xác nhận công nợ, đặt trước, tab danh sách và mobile 390px; không lỗi runtime |
| API hiện trạng | Đã đọc source | Không đồng nghĩa UAT mọi role/tình huống |
| Test dữ liệu tiền/tồn mới | Fixture trình duyệt đạt | Request ghi retail được intercept; 0 lần ghi nghiệp vụ vào DB vận hành. Không thay UAT backend tiền/tồn |
| Local3205 | Đã cập nhật | Chỉ rebuild/restart frontend, không reset DB/backend |

Bằng chứng: [kịch bản kiểm tra](../scripts/sales-ui-check.mjs), [kết quả](./sales-ui-check-result.json), [POS local](./screenshots/sales/pos-local.png), [POS fixture desktop](./screenshots/sales/pos-fixture-desktop.png), [đơn fixture mobile](./screenshots/sales/orders-fixture-mobile.png). Ảnh fixture là dữ liệu giả trong trình duyệt, không phải chứng từ trong DB. `git diff --check` đạt. Đồng bộ source vào repository `sgodev2024/java-aicrm` ngày 06/10/2026; vẫn chỉ vận hành local, chưa triển khai server.

Mỗi lần hoàn thiện cập nhật hàng SALES tương ứng, ghi file/check/API và lỗi/giới hạn. Không nâng “Có backend” thành “Hoàn thành” nếu FR liên quan còn thiếu. Các quyết định Q/OPEN ở BA/SRS vẫn được giữ; file này chỉ theo dõi trạng thái thực hiện.

## 5. Backlog tiếp theo ưu tiên

1. Backend sửa nháp, preview tổng không cần lưu; source children/filter/export sales.
2. Sản phẩm cha/biến thể chọn từ cấu hình → thay picker khi backend sẵn.
3. Duyệt hủy/đóng, cọc hoàn và bù tiền đổi có ledger.
4. DeliveryLines/CODassigned/statementref, summary tiền/giao/đơn đầy đủ.
5. In80mm/A4 có hồ sơ cửa hàng và UAT role/thiết bị; không đánh dấu toàn SRS hoàn tất trước các phần này.

