# Backlog triển khai AICRM — SRS 2.0

Ngày 05/10/2026. Căn cứ: [SRS 2.0](./SRS_HE_THONG_v2.0.md), [BA 2.0](./BA_HE_THONG_v2.0.md), [trường dữ liệu](./BA_TRUONG_DU_LIEU_v2.0.md).

**72 task để phân công**, chia theo nhóm phụ thuộc. Đây là backlog công việc, chưa phải kết quả code/kiểm thử. Owner là vai trò chủ trì: TL = Tech Lead, BE = Backend Java, FE = Frontend, QA = kiểm thử, OPS = vận hành, BA/PO = nghiệp vụ/người duyệt sản phẩm. Mỗi task có một người chịu trách nhiệm khi assign; phối hợp không thay owner. Không có yêu cầu thuê đủ 6 người: thành viên có thể kiêm vai trò, nhưng cần reviewer phù hợp và người duyệt nghiệp vụ khác người tạo.

Đội đã xác nhận có 3 intern làm cả backend/frontend. Xem [80 ticket nhỏ và phân công Intern 1/2/3](./TASK_INTERN_v2.0.md); task T trong tài liệu này vẫn là cấp tính năng để quản lý nghiệm thu, không mặc định giao nguyên task lõi cho intern.

## 1. Quy tắc quản lý task

Trạng thái ban đầu tất cả là **Todo**; task có quyết định chưa chốt đánh dấu **Conditional** trong nội dung. Workflow: Todo → Ready (contract/phụ thuộc/quyết định đủ) → In progress → Review → QA → Done. Blocked phải có lý do và task/quyết định đang chờ. Không tự coi source hiện có là Done.

Thông tin bổ sung trên công cụ quản lý công việc khi assign: người phụ trách, reviewer, sprint/mốc, estimate, ngày bắt đầu/mục tiêu, link thay đổi, evidence test và lỗi liên quan. Estimate chỉ lập sau T-01 khảo sát phạm vi code; không định ngày giao giả khi chưa biết đội ngũ.

Các cột “nghiệm thu” là tiêu chí tối thiểu riêng từng task, đi cùng AC SRS. BE có test phù hợp dữ liệu/quyền/transaction; FE có tích hợp thật và trạng thái lỗi; QA xác minh khi FR kết thúc. Được thiết kế FE từ contract trước khi BE xong, nhưng dependency trong bảng là điều kiện **hoàn tất tích hợp**, không cấm làm mockup trước. Không đưa dữ liệu giả vào DB cửa hàng để demo.

## 2. Nền tảng, quyền và lần đầu sử dụng — M0/M1

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-01 | TL + BA / P1 | Lập baseline mã nguồn/API/migration/menu; đối chiếu từng FR Có/Bổ sung; ghi các OPEN, chốt field/API/error contract và phương án tái dùng Core. Đầu ra: design note, OpenAPI mục tiêu, danh sách thay đổi/bằng chứng hiện trạng, estimate và kế hoạch sprint do đội điền | Không | Mọi FR-01…24 có owner/module/contract; DEC-03/04 khớp BA; OPEN không bị coi đã duyệt; không refactor ngoài phạm vi |
| T-02 | FE / P1 | Chuẩn hóa thành phần bảng/form/dialog: STT, tìm/lọc/phân trang, loading/empty/error/retry, tiền/ngày Việt; chọn SKU/UOM và action theo status. Tạo hướng dẫn dùng thành phần cho module mới | T-01 | Bảng hai trang STT đúng; lỗi giữ form; số tiền không dùng float để ghi sổ; kiểm desktop/mobile và bàn phím; NFR-06 |
| T-03 | BE / P1 | Cấu hình mẫu vai trò 8 nhóm; cấp quyền theo action và COST/EXPORT riêng, gán nhiều role; kế thừa tạo/tắt/reset user, không dựng auth mới. Đầu ra: role template, permission matrix, endpoint gán/sửa role có audit | T-01 | Sales không tự có COST/APPROVE; quản lý có quyền duyệt type được giao; tài khoản tắt không đăng nhập; AC-14/35, FR-03 |
| T-04 | BE / P1 | Hoàn thiện gán/thu hồi scope kho, kiểm tenant/branch trên list/detail/actions/export; phạm vi cũ bị thu hồi hiệu lực request tiếp. Đầu ra: scope API và bộ test quyền cơ sở | T-03 | Nhân viên kho A không đọc/ghi kho B; catalog shared nhưng history scoped; tenant khác bị chặn; AC-14, NFR-01 |
| T-05 | BE / P1 | Hồ sơ cửa hàng, logo tham chiếu Core, tiến độ onboarding và kho mặc định theo người; version và validation từng bước. Ngày bắt đầu chỉ kích hoạt theo OPEN-03; mặc định VND/giờ Việt Nam | T-01, T-04 | Một profile/tenant, quyền logo đúng; lưu/resume tiến độ; default warehouse mất scope phải bỏ; AC-01, FR-01/02 |
| T-06 | BE / P1 | API checklist 7 bước, complete kiểm chi nhánh/kho/quyền/người duyệt, batch/phiếu đầu kỳ và xác nhận zero; tính missingItems cho FE. Không tạo giao dịch thử | T-05, T-03 | Complete thiếu điều kiện bị chặn bằng lỗi cụ thể; commit import chưa posted không báo đầu kỳ đã ghi; AC-01/15 |
| T-07 | FE / P1 | Định tuyến login owner→wizard, employee→working warehouse; xây 7 bước, lưu/quay lại/resume/checklist, gán role/scope, nhãn hoàn thành. Tích hợp người dùng/phạm vi đủ trước mở bán | T-02, T-03, T-04, T-06 | Đăng xuất bước 3 vào lại đúng tiến độ; không có phạm vi thấy hướng dẫn quản trị; không ép đổi mật khẩu/MFA local; AC-01/14/35 |

## 3. Sản phẩm, biến thể cấu hình sẵn và giá — M1

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-08 | BE / P1 | Chuẩn hóa Nhóm/Thương hiệu thành danh mục có ID/code/name/active/version; CRUD có kiểm mã trùng và tham chiếu. Mapping warehouse_config hiện tại, tránh tạo catalog song song | T-01 | Sửa có version; ngừng còn đọc lịch sử; xóa dùng rồi bị chặn; dữ liệu hiện có không mất; FR-04, D-05 |
| T-09 | BE / P1 | Attribute + AttributeValue + useForVariant/order/active; API list/create/update/ngừng giá trị; kiểm parent/tenant, unique code trong thuộc tính | T-01 | Màu Đỏ và Size M chọn được, value của Size không gán cho Màu; ngừng không vào select mới; AC-20 |
| T-10 | FE / P1 | Các màn Nhóm/Brand/UOM/Thuộc tính dưới Sản phẩm; tab/chi tiết giá trị ngay trong Thuộc tính, form mã/tên/active/order, ngừng có giải thích. Không đặt lại catalog trong Cấu hình kho | T-02, T-08, T-09 | Tạo Màu→Đỏ/Đen, Size→M/L bằng UI; STT/search/error trùng đúng; menu giữ cùng cấp; FR-04 |
| T-11 | BE / P1 | Migration Product + SKU.productId + cấu trúc tổ hợp, productvariantattributes, unique variantKey; thiết kế defaults và field inheritance. Giữ SKU ID, khóa ngoại theo tenant và ledger FK | T-08, T-09 | DB chặn duplicate tổ hợp tenant/product; cha không có balance; SKU không đổi ID; AC-20/21, FR-05 |
| T-12 | BE + QA / P1 | Backfill một cha không biến thể/SKU cũ; công cụ preview mapping gom cha do người dùng xác nhận, không tự suy text màu/size. Viết migration verification/report và rollback bằng backup | T-11 | Count SKU, balance qty/value, UOM, lots/serials, open items, refs trước–sau bằng nhau; text lịch sử giữ; AC-21 |
| T-13 | BE / P1 | Product CRUD/version; variants preview/commit các tổ hợp được chọn; mã SKU/barcode/giá riêng, tính canonical key từ ID; không bán cha; khóa đổi baseUOM/tracking/tổ hợp khi đã giao dịch | T-12 | Concurrent cùng tổ hợp chỉ một SKU; toàn bộ batch variants lỗi thì rollback; chỉ tổ hợp người dùng chọn được tạo; AC-20 |
| T-14 | FE / P1 | Form tạo/sửa sản phẩm với Có biến thể; chọn attribute/values đã cấu hình → preview grid → chọn tổ hợp → nhập mã/barcode/giá → lưu. Danh sách cha/mở SKU, nhóm/brand/đổi trả/bảo hành; hỗ trợ thiếu value qua link cấu hình | T-10, T-13 | Không ô màu/size tự do trong form chuẩn; Áo→Đỏ/M và Đen/L tạo hai SKU, không ép bốn tổ hợp; lỗi theo dòng; AC-20 |
| T-15 | FE / P1 | Bộ chọn product→SKU dùng chung cho mua/bán/chuyển/kiểm/trả; tìm barcode vào đúng SKU; hiển thị tên + tổ hợp, loại SKU inactive; snapshot phiếu cũ vẫn hiển thị được | T-14 | Không chọn cha làm dòng giao dịch; scan đúng Đỏ/M không lấy Đỏ/L; SKU ngừng vẫn xem lịch sử; AC-07/20 |
| T-16 | BE + FE / P1 | Hoàn thiện UOM/Conversion per SKU, edit/ngừng có version, validate positive4decimals/basefactor1; adapter sku.units legacy, labels “1 thùng = 24 chai” | T-12, T-02 | Factorchange không đổi line cũ; baseUOM cannot self-convert; SKU A/B khác factor độc lập; AC-02, FR-06 |
| T-17 | BE / P1 | Đồng nhất price preview/post engine; ưu tiên khách/nhóm/branch/channel/minQty/priority/ID, exclusive/stackable; validate hiệu lực, channel, decimals; snapshot rules và VAT exact rounding | T-13, T-16 | Fixture giá90k overrides92k/95k/100k; exclusive vs stackable đúng SRS; partial-return rounding căn nguồn; AC-22 |
| T-18 | FE / P1 | Form bảng giá/khuyến mãi/quyền giảm, đủ kênh CTV và active; xem giá/giảm/tax-inclusive cùng engine, giải thích rule và cảnh báo ngoại lệ | T-02, T-17 | Preview và phiếu ghi không chênh; date/rule lỗi giữ form; user không SETUP không sửa policy; AC-22 |

## 4. Chứng từ, đồng thời và duyệt — M2

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-19 | BE / P1 | Draft PATCH có version, validate header/lines/source; chỉ DRAFT edit; rejected copy thành nháp có trace; giữ posted immutable; schema field map theo D-08/09 | T-01, T-04, T-13 | Hai lần sửa cùng version một conflict; post rồi edit bị chặn; nguồn/SKU khác bị chặn; AC-23, FR-07 |
| T-20 | BE / P1 | Bộ đếm số phiếu an toàn tenant/type/year và unique DB; loại cơ chế đếm dòng hiện tại; migration không đổi số đã có, không tái sử dụng số hủy | T-19 | Hai người tạo không trùng số; hủy/tạo lại không reuse; rollback cho phép gap nhưng không trùng; FR-07/R-14 |
| T-21 | BE / P1 | Idempotency trên create/submit/approve/receive/settle/payment/allocation/refund; requestHash/result atomic và retry strategy. Kiểm row locks/thứ tự khóa, rollback ledger/audit | T-19, T-20 | Same key/same payload một effect và trả cùng result; different payload409; timeout tra lại; AC-08/23/29, NFR-02/03 |
| T-22 | BE + FE / P1 | Approval action có version/payload hash, approve/reject history, self-deny; mở rộng cần duyệt cho close/cancel/variance/reversal; UI tóm tắt tác động/lý do/ngoại lệ | T-03, T-04, T-19, T-21, T-02 | Creator selfapproval bị chặn API; stale approval không thực hiện; pending noledger; UI không bấm duyệt phiếu mình; AC-14/17/23 |

## 5. Mua hàng và nhà cung cấp — M2/M3

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-23 | BE + FE / P1 | Hoàn thiện NCC create/edit/ngừng/search/export, optional contact/MST; scope sharedcatalog, link mua/nợ. Không nhập công nợ trực tiếp vào profile | T-02, T-04, T-19 | NCC required đúng kind, ngừng không mất phiếu/nợ; chủ sửa, nhân viên mua không tự có SETUP; FR-08, D-06 |
| T-24 | BE / P1 | Nhập trực tiếp, lô/serial, vốn bình quân, AP/paid/allocation atomic; tách PurchaseCost/value allocations khỏi tiền hàng, thiết kế payable/payee. **Conditional OPEN-02** phần phí/bên chịu | T-16, T-19, T-21, T-22, T-47, T-48 | Pending0stock/AP; approved nhập10/AP1m; phí100k tăng vốn1.1m không tăng nhầm AP NCC hàng; AC-03/24 |
| T-25 | FE / P1 | Form/list/detail nhập trực tiếp với nguồn K, phí/alloc, paid/phương thức, tổng hàng/thuế/phí/tổng nghĩa vụ riêng; lô/serial condition; edit draft, gửi/duyệt theo quyền | T-15, T-18, T-23, T-24 | Thấy tồn/nợ sau ghi, lý do chờ/từ chối rõ; không tạo sản phẩm tại form mua; D-08–10, AC-03/24 |
| T-26 | BE / P1 | Đề nghị/PO/partial receipts, progress/closedqty, chênh thừa/khác giá approvalreason; lock sourceLine khi receive, nguồn đã đóng blocked; trace nguồn không làm double receipt | T-24, T-22 | PO10 receive6+4 complete; changedprice exception; simultaneous nhận remaining không vượt tùy ý; AC-04/23 |
| T-27 | FE / P1 | Màn Mua: loại phiếu request/order/receipt/return, nguồn theo lựa chọn; bảng đặt/nhận/lần này/còn và chênh giá; tạo phiếu nhận nhiều lần, close request theo quyền | T-25, T-26 | Thực hiện đủ chuỗi và nhập trực tiếp đều ít bước; nhận thiếu để open, đóng còn lại không giảm tồn đã nhận; AC-04 |
| T-28 | BE + FE / P1 | Trả mua từ receipt posted với qty/SN/lot cap, outbound/value và APcredit; hiển thị phần NCC còn phải hoàn, không giả thu để giảm nợ | T-24, T-48, T-22 | Return300k cònAP100k: credit100k + refundclaim200k; serial gốc/qty đúng; AC-25 |
| T-29 | BE + FE / P1 | SupplierRefundClaim list/detail/openremaining; supplier refundreceipt allocations nhiều lần; bảo vệ amount/partner/branch, link return và payment | T-28, T-48 | Thu150k+50k→claim200k closed; over200k blocked; không đẩy vào AR bán; report trace nguồn; AC-25 |

## 6. Bán quầy, đơn và hủy — M2/M3

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-30 | BE / P1 | POS confirm pipeline price/stock/value/AR/payment/allocation/approval cùng transaction; nợ customerrequired/no creditlimit; insufficientstockresponse line-level; dưới vốn recheck | T-17, T-19, T-21, T-22, T-47, T-48 | Sale200k paid150k→debt50k; unpaidnocustomer blocked; exceptionpending no stock/thu; lastunit concurrent AC-05/07/08 |
| T-31 | FE / P1 | POS scan/search SKU, UOM/qty, tax/discount, customer, receive/payment, preview, giữ draft; explicit preorderaction phần thiếu và xác nhận sale+order; print link. Không nút thêm sản phẩm | T-02, T-15, T-18, T-30, T-32 | Dùng bàn phím/scan, nợ không bị bắt duyệt riêng; thiếu chọn chờ hay sửa, không tự bán một phần; retry key đúng; AC-05/07/08 |
| T-32 | BE / P1 | Quote/order/issue từ nguồn, nhiều kho/lần, giá snapshot, dueprogress/backorder/closed; separate fulfillment/delivery/payment status, tính advance/AR đúng; quoteexpiry contract | T-17, T-19, T-21, T-22, T-47, T-48 | Cọc200k/đơn1m, issue600+400→revenue1m AR800k; order no stock/AR; multiwarehouse capsource; AC-06/26 |
| T-33 | FE / P1 | Danh sách đơn với kênh, khách, xuất/giao/tiền riêng; manualonline/address/COD; quote→order, depositlink, issue remaining theo kho; trạng thái chờ hàng rõ | T-15, T-18, T-32, T-50 | Một đơn có hai kho/hai issue và nhiều payment truy được; tổng không cộng trùng; chưa có reservation hiển thị đúng; AC-06/07/26 |
| T-34 | BE / P1 | Yêu cầu cancel/close khách→approval→closedremaining; lock close/issue race; requestmetadata khách/reason; calculate depositrefundavailable đã trừ allocation/refund; carrierfailed không tự close | T-22, T-26, T-32, T-48 | Closepartial retainsissued; creator cannot approve; depositrefund≤unused, replay oneeffect; AC-17/23 |
| T-35 | FE / P1 | Dialog đóng/hủy nêu remaining/đã làm/cọc dư và ảnh hưởng, trạng thái requestduyệt; refundcọc entry/link, không nút xóa posted | T-02, T-34, T-50 | Người bán hiểu phần nào hủy/không hủy; failedshipping không hiện cancelledorder tự động; AC-17 |

## 7. Kho, chuyển, kiểm kê và cách ly — M3

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-36 | BE / P1 | Tách authorize dispatch(source) và receive(destination), list transfer theo hai đầu, expose nguồn tối thiểu; account chỉ kho đích nhận được phiếu authorized | T-04, T-19 | Receiverdest-only nhận đúng phiếu, không xem movement/source stock khác; senderkhông quyềndest không receive; AC-19 |
| T-37 | BE / P1 | Hoàn thiện transfer dispatch/value segments và partialreceipt tốt/hỏng từng dòng, lock qtyremaining/SN, tracking snapshot; transit quantity/value đối chiếu | T-16, T-21, T-22, T-36 | Dispatch10 receive6 gồm hỏng1, remaining4, availdest5; serial/lô/value đúng, repeatedreceive không double; AC-12 |
| T-38 | BE / P1 | TransferVariance thiếu/mất, biên bản + approval, settle remaining và costloss ledger; missing không giảm nguồn lần hai; damagedreceipt link | T-22, T-37 | Received6+lost4 approved→remaining0, sumvaluecorrect; chưa duyệt vẫn transit4; overvariance blocked; AC-27 |
| T-39 | FE / P1 | Transfer list/source/dest/remaining, dispatch/receive theo quyền; mỗi dòng nhận tốt/hỏng/SN, biên bản thiếu và request/decision; lịch sử từng nhận | T-02, T-15, T-37, T-38 | Phân biệt thiếu treo và đã xử lý; nhậnpartial/zero skipped, lỗi từng dòng, dest-only UI hoạt động; AC-12/19/27 |
| T-40 | BE / P1 | Kiểm kê giữ SKU/kho lock từ bắt đầu kiểm kê qua pending; countSN/lots including0, snapshotconflict; chuẩn hóa vốn chênh và cách đếm quarantine tách rõ | T-19, T-21, T-22, T-42 | Count10→8 approved−2; X/A locked, Y/A X/B allowed; cancel/reject xử lý lock đúng contract; AC-13 |
| T-41 | FE / P1 | UI kỳ kiểm kê, SKU chọn/tracking/snapshot/countdiff/reason, theo dõi lock/duyệt/hủy; nhập lô có bảng thay textarea thô, quét serialđếm | T-02, T-15, T-40 | Người dùng nhập lô0, tổng lô bằng đếm; nhãn “chỉ hàng đang đếm bị khóa”; không sửa snapshot; AC-13 |
| T-42 | BE / P1 | Quarantine release/destroy/returnSupplier và otherreceipt/issue mục đích/reason/value; approval và movement audit; không trực tiếp update qty/quarantine từ UI | T-19, T-21, T-22, T-37 | Release qtytotal unchanged/available↑, destroy qty/value↓; SNstate đúng; overisolationblocked; AC-27 |
| T-43 | FE / P1 | Kho & Tồn có bộ lọc/kho/SKU/tracking, history và cách ly actions; cấu hình chỉ inventory defaults, optional thresholds cảnh báo P2; otherin/out theo mục đích | T-02, T-15, T-42 | Giá trị chỉ COST; no catalogtabs/addproduct; stockhistoryrefs đúng; cảnh báo không gọi expired là availabletosell; FR-14/15, AC-07/27 |

## 8. Giao hàng, COD và kế toán — M3/M4

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-44 | BE / P1 | DeliveryLine theo issue, recipient snapshot và partialdeliveryquantities; state transitions; tổng giao≤xuất; không stock/revenue tự động khi delivered/failed | T-32, T-21 | Issue10 giao4+6, không delivery trùngqty; failed không tự nhập trả; AC-26 |
| T-45 | BE / P1 | CODassigned đa đợt có lock/debtcap, settlementgross/fee/net, statementref và idempotency; receiptallocation+expense atomic; **Conditional OPEN-02** fee0COD/customercharge | T-44, T-47, T-48, T-21 | Gross500 fee25→net475 AR−500; multi-settle/dup/overfee bị chặn; debtthu khác trước settle xử lý conflict; AC-09/28 |
| T-46 | FE / P1 | Giao hàng trong đơn: create/recipient/items/carrier/tracking manual; partial/statushistory; bảng CODpending/settled/net/fee và form statementsettle | T-02, T-44, T-45 | Đợt giao không bị hiểu đã thu; nhậpgross vàfee riêng, previewnet; userFINANCE settle, SELL chỉ cập nhật giao; AC-09/26/28 |
| T-47 | BE / P1 | Chuẩn hóa finance contracts/openItem/payment/advance, dates/due fields, type/partner validations; groupincomeexpense và accountoptional. **Conditional OPEN-03** ngày hiệu lực/khóa/lùi; giữ nợ không chặn bán | T-19, T-22, T-04 | Ledgerremaining gốc−credit−allocated; cọc chưa xuất không revenue/AR; accountnull rõ, VNDexact; FR-17/AC-05/06 |
| T-48 | BE / P1 | Allocation atomic/locks crossbranchscopes/partner/direction; auto depositallocation order→post, explicitadvanceremaining; cap sau credit/refund và concurrent | T-47, T-21 | Mộtpayment chia nhiềuopenItem, mộtopenItem nhận nhiều; concurrent≤amount/remaining; deposit200k allocatedonce; AC-06/29 |
| T-49 | BE / P1 | Payment/allocation reversal request→approval→linkedopposite ledger, reopen debt/advance; cap/duplicate protection; audit lý do. Không dùng delete hoặc sửa payment đã ghi | T-22, T-48 | Reverse150k reopenAR150k, bản ghi gốc bất biến; report không double count reversal; rejectednoeffect; AC-30 |
| T-50 | FE / P1 | Hai màn riêng Thu chi/Công nợ, list/filter/totals, recordmoney và phân bổ ngay/sau, advances, due/read-onlyremaining, reverse request/history; cảnh báo no debtblocking | T-02, T-47, T-48, T-49 | Người dùng thu1kho/phân bổnợ đúng, chọn saiđối tác bị lỗi; không dùng sốpaid tĩnh làm nợ hiện tại; AC-05/06/29/30 |

## 9. Đổi trả, bảo hành và CRM — M4

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-51 | BE / P1 | CrossbranchsaleReturn originalexactlookup, totals/serial/lot caps, price/cost snapshot, ARcredit/refundable cap; opened/overdue rule. **Conditional OPEN-01** USED; không tự bật allowUsed | T-30, T-32, T-48, T-22 | Return300 paid200 debt100→credit100/refundmax200, recipientstocksourceARtrace; wrongSNalwaysblocked; AC-10/11 |
| T-52 | BE / P1 | Refundsale/cọc + ExchangeSettlement ledger refs; returncredit→saleoffset + collect/refunddifference; amount/paymentmethodchange reason/approval; không tạo khoản cash giả để bù | T-51, T-34, T-48, T-22 | Return200/new250→additional50, no cash200; cheapernew refunddifference≤available; replays no doublebenefit; AC-17/31 |
| T-53 | FE / P1 | Tìm phiếu gốc bằng số→dòng/lượng/tracking/condition, kho nhận, submit; đổi→chọn SKU mới→bù/chênh, refund methodreason; historynguồn | T-02, T-15, T-51, T-52, T-50 | Không nhập ID; crossbranchhiển thị chi nhánh bán/nhận; lỗi wrongserial và overqty giữ form; AC-10/11/31 |
| T-54 | BE / P1 | Warranty giữ hộ fields/assignee/due/result/recipient, bộ chọn phiếu/dòng nguồn, SNdup và nonseriallượng đang tiếp nhận caps; statemachine/bàn giao audit, no salestock | T-51, T-04, T-21 | Sourcewarranty snapshotdays, wrongSN/expiredblocked; nonserialqtyactive cap; handedoverclearsgiữ hộ; AC-32 |
| T-55 | FE / P1 | Warranty form searchsale/selectline thay ID nhập; tiếp nhận/sửa/thông tin xử lý/ngày hẹn/người nhận bàn giao, listlọc/nhắc và giữ hộ | T-02, T-54 | Đủ bốn trạng thái; phiếu giữ hộ không hiện hàng bán tăng tồn; lỗi quyền/serial rõ; AC-32 |
| T-56 | BE / P2 | Customer/CTV profile edit/version, lịch sử scoped, careassignee/due/result/completedAt; reminder pagination + totalserver, thao tác complete audit | T-04, T-19 | Nhắc>20 totalđúng, lịch hẹn theo giờ Việt Nam, assignmentphạm vi hợp lệ; lịch sử không lộ chi nhánh ngoài quyền; AC-33 |

## 10. Import, hoa hồng và nhắc việc — M1/M4

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-57 | BE / P1 | Sharedvalidator CSV/xlsx/API preview và commit dùng cùng quy tắc, headers/type/dates/decimals/tracking/scopes, 2MB/500rows, không nhận công thức; checksum includeskind/warehouse/openingDate | T-04, T-13, T-16, T-21 | Lỗi đúng dòng/field; commitkhông ghi một phần; cùng dữ liệu ở kho khác không bị nhận nhầm batchcũ; AC-15 |
| T-58 | BE / P1 | Template/import Product–SKU biến thể đã cấu hình, attribute/valuecodesresolveIDs, group/brand/UOMrefs; atomicproductrows and uniquevariantchecks; dữ liệu legacytemplateadapter | T-57, T-13 | Không freevarianttext tạo attributes tự ý; foreign/inactivevalue blocked, replaynotdoubleSKU; AC-20/21 |
| T-59 | BE / P1 | Opening stock/debt theo batch tạo drafts và mapping đầy đủ; duplicateopeningguard, submitapprovepipeline, reconcilezero/userconfirm; **Conditional OPEN-03** ngày hiệu lực | T-57, T-47, T-48, T-22 | Commitkhôngupdatebalance; approvecreatesqty/debt exactlyonce; không dùng AP âm thay ứng trước; AC-15 |
| T-60 | FE / P1 | Mẫu nhập/tải file/preview rowerrors/tóm tắt/commit/batchlist; sourceAPI hướng dẫn cùng schema; openingdocslinks gửi/duyệt và checklistwizard | T-02, T-58, T-59, T-06 | Người dùng biết batchcommitted≠postedstock; sửa và nhập lại không tạo trùng; no hiddenpartialcommit; AC-01/15 |
| T-61 | BE + FE / P2 | Commission ORDER/monthaddition/adjustment, monthpicker/list/totals multi-period, paymentcap và trace sale/return; immutableentries, negativeadjustcorrect | T-32, T-48, T-50 | 300+200=500 không monthlytotaldouble; paid>remainingblocked; period/branch/CTVtrace; AC-16 |
| T-62 | FE / P2 | CRM màn customerhistory/note/reminderassignee/due/result/done, overduefilters/total, linkreminder từ overview; ngừng khách vẫn lịch sử | T-02, T-56 | Khách/ghi chú dùng dễ, reminder>20 không mất total, hoàn thành có result/time; AC-33 |

## 11. Báo cáo, Core và bàn giao — M4/M5

| Task | Owner / P | Công việc cụ thể và đầu ra | Phụ thuộc | Nghiệm thu / truy vết |
|---|---|---|---|---|
| T-63 | BE / P1 | Query/report definitions theo phạm vi/thời gian: bán−trả, nhập−trả, tồn open/in/out/close, AR/AP/advance/thu chi/refundclaims/COD/commission; phân trang trên máy chủ/totals/COST control và reconcile queries | T-24, T-29, T-32, T-38, T-45, T-49, T-52 | Total không giới hạn1000, ledger mathđúng/ngày Việt Nam, negative/reversal đúngsign, không lộ giá vốn; AC-18/34; commissionreport full sau T-61 |
| T-64 | FE / P1 | Sixtab dashboard rolefilter, period/branch/kho labels, indicator drilldown; reportsfilter/tables/totals/pagination; reusedloading/error; CRM total sau T-56 | T-02, T-63 | Chọnkho dashboard phản ánhfilter hoặc nhãn toànscope rõ, không dùng top20count làm total; buildCSSpass; AC-18/34 |
| T-65 | BE + FE / P1 | ExportCSV đủ bộ lọc/tất cả trang với audit/formulaescape/COST; receipt80mm và A4, profilebranding, snapshot và số dư nhãn rõ, repeatprintcùng ID; export/printAPIcontract | T-05, T-63, T-50 | >1000rowstệp đủ dòng, scope/COSTđúng; receiptitems/totals/currentoutstanding đúng, QAthiết bị in; AC-18/34 |
| T-66 | BE + FE / P1 | Core regression users/org/roles/modules/resources/activity/files/settings/profile; auditreadonlyfilters, đúng technicalrole; scopes/role UI sửa thu hồi, preserve loginpolicy | T-03, T-04, T-02 | Module hiện tại không mất menu/quyền; reset≥8 local, no MFAre-enable/firstPWchangeforced; audit no password/token; AC-14/35 |
| T-67 | BE + FE / P2 | Gắn file ảnh hàng/chứng từ/biên bản với Corefileid; size/MIME/quyền/tenant/detaildownload; optionalfields không ép upload để bán | T-05, T-13, T-19, T-66 | Userngoàiscope không download bằng URL/ID; lịch sử liên kết đúng, uploaderror không halfposted; FR-23/NFR-01 |
| T-68 | OPS + TL / P1 | Môi trường test tách database vận hành, cleanbuild/migration/envtemplate, runlocal scripts/health, không tự nạp demo; backup trước migration và bản rollback | T-01, T-12 | Checkout/sourcecopy clean chạy được không secrets; database kiểm thử khác đang dùng, migrationsclean/upgrade pass; AC-21/36 |
| T-69 | QA / P1 | Bộ test AC-01…36 với fixtureisolated/roles/tenants/lô/SN/variants; concurrent/replay/rollback/permissions/reportreconcile. Ghi matrix FR→task→case→evidence/bug; OPENcasesconditional | T-68, T-21 | Kịch bản đạt với số liệu cụ thể; failedcases mở bug theo owner; tiền/tồn/phạm vi chưa đạt không nghiệm thu; NFR-01…08; chạy từng FR khi task tương ứng hoàn tất |
| T-70 | OPS + QA / P1 | Diễn tập backuprestore DB+files, compare qty/value/debt/refs; benchmark POS/stock/report/import trên dataset mô tả, ghi p95 và RPO/RTO đo; chốt target với PO | T-68, T-63, T-65 | Restore boot/login/openinvoice/fileread đúng, no cross-tenantfiles; có biên bản đo thay hứa hiệu năng; AC-36/NFR-09/10 |
| T-71 | QA + BA/PO / P1 | UAT hành trình firstlogin→catalog/opening→purchase→POS/order→money→return/transfer/count→report; vai trò/thường/lô/SN/variant; keyboard/mobile/printer; đối chiếu backlog thiếu | T-07, T-14, T-25, T-27, T-31, T-33, T-35, T-39, T-41, T-46, T-50, T-53, T-55, T-60, T-64, T-65, T-66, T-69 | AC bắt buộc có evidence/PO review; điều kiện OPEN chốt trước ca tương ứng; P2 chưa xong ghi hạn chế, không gọi đủBA; AC-01…36 thuộc phạm vi |
| T-72 | TL + OPS / P1 | Đóng gói source/runbook/migrations/envsamples/userguide/knownlimits/testreport; quy trình nhận fullsource và restore riêng, danh sách việc SaaS sau; releasechecklist chốt | T-70, T-71 | Người khác build/runlocal/restore được từ gói bàn giao, không secrets/DBdemo; chỉ xuất/push/deploy khi nhiệm vụ sau được yêu cầu; AC-36 |

## 12. Thứ tự thực hiện và điểm bàn giao

| Mốc | Nhóm việc | Điều kiện ra mốc |
|---|---|---|
| M0 — Chốt nền | T-01; thiết kế T-03/04/05/08/09/11/19/47; chuẩn UI T-02 | Contract/decision register/phạm vi code rõ; estimate đội xác nhận |
| M1 — Bắt đầu và sản phẩm | T-03…18; T-57…60 triển khai theo dependency; môi trường T-68 làm sớm | Setup resume, role/scope, SKU variants/UOM và opening thực chạy |
| M2 — Nhập/quầy | T-19…25, T-30/31, T-47/48; T-32 đủ cho preorder | Mua ghi tồn/AP, POS ghi stock/AR/payment, replay/đồng thời không trùng |
| M3 — Đơn/kho/tiền | T-26…29, T-32…43, T-49/50 | Partialfulfillment/transfer/count/quarantine/claims/closure đối chiếu được |
| M4 — Dịch vụ và báo cáo | T-44…55, T-63…67; P2 T-56/61/62 | COD/trả/đổi/bảo hành/report/export/in phù hợp quyết định chốt |
| M5 — Nghiệm thu/bàn giao | T-69…72 | AC/scope/restore/UAT đủ; công bố giới hạn nếu P2/conditional chưa xong |

Mốc là trình tự logic, không phải deadline. T-69 thiết kế ca ngay từ M0 và chạy khi từng slice sẵn, không đợi M5 mới tìm lỗi. T-68 triển khai sớm trước mọi migration trên DB dùng thật. T-07 có thể demo wizard không số dư trước, nhưng đầu kỳ chỉ nghiệm thu sau T-60.

### Bàn giao giữa vai trò

BE → FE: OpenAPI/field map/example/error/status/permissions và dữ liệu kiểm thử isolated. FE → QA: route/role/action/checklist/screenshots nếu cần + commit/build tham chiếu theo quy trình thực hiện sau. QA → owner: lỗi có bước tái hiện/dữ liệu/quyền/kết quả kỳ vọng; không chỉ “Failed to fetch”. OPS → team: runbook/environment/restore/evidence. BA/PO: chốt OPEN và xác nhận kết quả nghiệp vụ, không duyệt kỹ thuật thay kiểm thử.

## 13. Gói sau bản chuẩn, không trộn vào 72 task

| Gói | Phạm vi |
|---|---|
| NEXT-01 | Phí mua đến muộn phân bổ tồn/COGS kỳ mở, không sửa phiếu bán cũ |
| NEXT-02 | Carrier API/sàn: adapter, webhook/idempotency và đối soát hãng |
| NEXT-03 | SaaS đăng ký/gói thuê bao/vòng đời khách, vận hành/cô lập production |
| NEXT-04 | Công cụ tách tenant/file/config/source sang môi trường riêng, cutover/reconcile |
| NEXT-05 | ERP lớn/kế toán pháp định/tích hợp hóa đơn theo nhu cầu và quy định được xác minh khi làm |

Không tự tạo task offline/ca hoặc chức năng ngoài BA. R1 có quy trình bàn giao source thủ công và kiểm restore; NEXT-04 là công cụ tự động, hai mức không được đánh đồng.

## 14. Mẫu ticket để đưa lên công cụ quản lý

```text
ID / Tên: T-xx — nội dung trong bảng
FR / BA flow / Data section / AC:
Owner cụ thể / Reviewer / Sprint / Estimate:
Trạng thái: Todo | Ready | In progress | Review | QA | Done | Blocked
Phụ thuộc task / quyết định:
Hiện trạng code cần kế thừa:
Phạm vi sửa: DB/migration, API, UI, quyền, audit, test, tài liệu
Đầu ra: liên kết contract/source/runbook
Điều kiện nghiệm thu: từng bước và giá trị kỳ vọng
Evidence: test result/UAT/screens/đối chiếu sổ
Ngoài phạm vi và giới hạn:
```

Ticket có thể tách thêm khi estimate quá lớn, dùng `T-xx.a/b` và giữ task cha làm tiêu chí integration; không đổi ID gây mất truy vết. Mọi AC chưa đạt phải có bug/ticket liên quan, không đóng bằng ghi chú “sẽ làm sau” nếu vẫn nằm phạm vi cam kết.
