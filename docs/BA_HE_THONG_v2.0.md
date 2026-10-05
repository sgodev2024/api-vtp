# BA hệ thống quản lý bán hàng AICRM — phiên bản 2.0

Ngày lập: 05/10/2026. Trạng thái: bản dự thảo đầy đủ để rà soát nghiệp vụ và phân công triển khai. Nền tảng: Java Core Platform hiện tại. Đối tượng: cửa hàng, hộ kinh doanh, doanh nghiệp thương mại nhỏ và chuỗi điểm bán.

Tài liệu này mô tả **quy trình vận hành chuẩn cần đạt**, đối chiếu với giao diện và mã nguồn hiện tại. Đây không phải biên bản xác nhận toàn bộ chức năng đã hoàn thành. Chi tiết từng trường nằm trong [Phụ lục dữ liệu và màn hình](./BA_TRUONG_DU_LIEU_v2.0.md). Khi nội dung khác tài liệu BA/SRS 1.0, các quyết định mới của người dùng và quy tắc ghi rõ trong bản 2.0 là căn cứ cập nhật SRS tiếp theo.

## 1. Mục tiêu và phạm vi

Người mới có thể thiết lập cửa hàng, nhập hàng, bán, thu tiền và tra cứu tồn/nợ theo cùng một quy trình. Một nghiệp vụ chỉ nhập một lần; tồn kho, tiền và công nợ phát sinh từ chứng từ đã ghi. Người dùng không phải hiểu cấu trúc kỹ thuật của nền tảng để bán hàng.

| Phạm vi giai đoạn đầu | Nội dung |
|---|---|
| Sản phẩm | Danh mục dùng chung, SKU, đơn vị cơ sở/quy đổi, nhóm, thương hiệu, thuộc tính, lô/hạn hoặc serial |
| Mua hàng | Nhập trực tiếp; tùy chọn đề nghị → đơn mua → nhận nhiều lần; nhà cung cấp; trả mua |
| Bán hàng | Bán quầy, báo giá/đơn bán, đơn online nhập tay, bán sỉ/CTV, đặt cọc, bán công nợ |
| Kho | Tồn và lịch sử, nhập/xuất khác, chuyển hai đầu, kiểm kê, đầu kỳ |
| CRM | Hồ sơ khách/CTV, lịch sử mua, ghi chú, nhắc chăm sóc |
| Kế toán nội bộ | Thu chi, phải thu/phải trả, ứng trước, phân bổ, COD, hoàn tiền, hoa hồng thủ công |
| Vận hành chung | Đổi trả/bảo hành, duyệt, thiết lập, nhập và xuất dữ liệu |
| Quản trị | Tài khoản, quyền, phạm vi dữ liệu, tổ chức, nhật ký và cấu hình Core |

Chưa đưa vào giai đoạn này: bán khi mất Internet; mở/đóng ca và kiểm quỹ; tích hợp hãng vận chuyển/sàn; hóa đơn điện tử, sổ kế toán pháp định và khai thuế; sản xuất; giá vốn bổ sung sau nhập/bán; tự động tách khách SaaS sang máy chủ riêng. Vẫn ghi nhận phương thức tiền mặt trong thu chi; bỏ quản lý ca không đồng nghĩa bỏ dữ liệu tiền mặt.

## 2. Cách đọc hiện trạng

| Nhãn | Ý nghĩa |
|---|---|
| **Có** | Đã có màn hình/API hoặc quy tắc trong mã nguồn; chưa thay thế nghiệm thu người dùng |
| **Bổ sung** | Yêu cầu chuẩn trong BA nhưng chưa đủ ở hệ thống hiện tại |
| **Sau** | Phát triển khi có nhu cầu đặc thù hoặc giai đoạn sau |
| **Chờ chốt** | Đề xuất tạm thời, chưa được người dùng xác nhận riêng |

Đã xác nhận trong lượt xây dựng này: **lần đầu sử dụng phải có hướng dẫn từng bước**, **kiểm kê chỉ khóa sản phẩm đang đếm tại kho kiểm kê** và **sản phẩm có biến thể chọn thuộc tính/giá trị đã cấu hình sẵn**. Chuẩn BA dùng sản phẩm cha và SKU theo tổ hợp biến thể; mô hình mã nguồn hiện tại vẫn là SKU độc lập với mô tả màu/size bằng chữ, cần nâng cấp.

## 3. Cấu trúc menu và trách nhiệm dữ liệu

| Menu cấp cao | Chức năng con | Vai trò chính |
|---|---|---|
| Trang chủ | Trang chủ nền tảng | Điểm vào hệ thống; cần ưu tiên công việc kinh doanh thay chỉ số kỹ thuật |
| Tổng quan | Tab Mua hàng / Bán hàng / Kho / Sản phẩm / CRM / Kế toán | Theo dõi kết quả và việc cần làm |
| Bán hàng | Bán tại quầy; Đơn bán hàng | Giao dịch bán; không tạo sản phẩm tại quầy |
| Mua hàng | Mua hàng; Nhà cung cấp | Giao dịch mua và hồ sơ NCC hỗ trợ mua |
| Kho | Kho & Tồn; Chuyển kho; Cấu hình kho | Giao dịch tồn và chính sách tồn |
| Sản phẩm | Danh sách sản phẩm; Nhóm sản phẩm; Thương hiệu; Đơn vị tính; Quy đổi đơn vị tính; Thuộc tính | Dữ liệu gốc của hàng hóa |
| CRM | Khách hàng | Hồ sơ khách/CTV và chăm sóc |
| Kế toán | Thu chi; Công nợ | Sổ tiền và sổ nợ tách riêng |
| Vận hành chung | Đổi trả & bảo hành; Cần duyệt; Thiết lập ban đầu | Công việc xuyên module |
| Báo cáo | Báo cáo nghiệp vụ và xuất dữ liệu | Đối chiếu kết quả |
| Quản trị hệ thống | Các chức năng Core tại mục 17 | Tài khoản và vận hành nền tảng |

Tổng quan và Báo cáo nằm riêng, cùng cấp các menu cha. Cấu hình kho chỉ giữ cài đặt tồn kho; không chứa các tab đơn vị, biến thể, thương hiệu hoặc thuộc tính. Danh mục không tăng tồn; phiếu nhập đã ghi mới tăng tồn. Khách/NCC không có số dư tự nhập trên hồ sơ; số dư bắt nguồn từ phiếu và phân bổ.

## 4. Người dùng, quyền và phạm vi

Một người có thể kiêm nhiều vai trò. Bảng dưới là mẫu cấu hình nghiệp vụ cần cung cấp; Core đã có quyền, nhưng chưa mặc định đầy đủ các mẫu vai trò này.

| Vai trò | Công việc | Hạn chế mặc định |
|---|---|---|
| Chủ doanh nghiệp | Thiết lập, phân quyền, xem toàn công ty, duyệt, xem vốn/báo cáo | Không tự duyệt phiếu mình tạo |
| Quản lý điểm bán | Theo dõi điểm bán, duyệt nghiệp vụ được giao | Trong chi nhánh/kho được giao |
| Bán hàng | Quầy, báo giá/đơn bán, khách, trả/bảo hành | Không xem vốn nếu chưa được cấp; không duyệt mình |
| Thu ngân | Ghi thu, phân bổ, tra phiếu bán | Quyền tiền riêng; không mặc định điều chỉnh tồn |
| Thủ kho | Tra tồn, nhận/xuất/chuyển/kiểm kê | Kho được giao; không mặc định xem vốn |
| Mua hàng | NCC, đề nghị/đơn/nhập/trả mua | Không mặc định tự duyệt mua |
| Chăm sóc khách | Hồ sơ, lịch sử và nhắc việc | Dữ liệu giao dịch đúng phạm vi |
| Kế toán | Thu chi, nợ, COD, hoàn/hoa hồng, đối chiếu | Xem vốn và duyệt chỉ khi cấp thêm |

Quyền hiện có: đọc nghiệp vụ, bán, mua, kho, CRM, tài chính, báo cáo, duyệt, thiết lập, xem giá vốn, xuất dữ liệu, toàn bộ chi nhánh. Phạm vi kho và chi nhánh phải áp dụng ở danh sách, chi tiết, API, báo cáo, nhập/xuất; ẩn menu không thay thế kiểm quyền.

Danh mục sản phẩm/đối tác dùng chung trong công ty. Lịch sử, tồn, tiền/nợ theo phạm vi được cấp. Tra phiếu bán gốc để trả khác chi nhánh chỉ cho phép tìm bằng **đúng số phiếu**, trả dữ liệu cần thiết đối chiếu; không mở quyền xem toàn bộ doanh số chi nhánh khác.

**Ràng buộc vận hành:** nghiệp vụ bắt buộc duyệt cần ít nhất hai tài khoản có quyền và phạm vi phù hợp. Bản hiện tại còn kiểm quyền nghiệp vụ mua/kho/tài chính ngoài quyền duyệt; mẫu quản lý phải cấp đủ quyền tương ứng. Với cửa hàng một người, không tự tạo cơ chế bỏ duyệt. Nếu muốn chế độ một người phải có quyết định thay đổi riêng.

## 5. Luồng bắt đầu từ đăng nhập lần đầu

### F-01. Đăng nhập

1. Người dùng mở ứng dụng, nhập email và mật khẩu; chọn ghi nhớ phiên nếu cần.
2. Kiểm tra email hợp lệ, mật khẩu tối thiểu 8 ký tự, tài khoản còn hoạt động.
3. Xác thực thành công, tải tên người dùng, menu/quyền và phạm vi kho.
4. Quản trị chưa hoàn thành thiết lập → vào hướng dẫn lần đầu. Nhân viên → chọn điểm làm việc được giao, vào chức năng công việc.
5. Không có kho/phạm vi → thông báo nhờ quản trị cấp; không cho tạo giao dịch ở kho bất kỳ.
6. Các lần sau vào Tổng quan hoặc màn hình gần nhất hợp lệ. Hết phiên → đăng nhập lại; lỗi kết nối → thông báo và thử lại.

**Có:** đăng nhập email, nhớ phiên, hồ sơ, đổi mật khẩu. Chính sách local đã tắt MFA, không ép đổi mật khẩu lần đầu, mật khẩu từ 8 ký tự không ép độ phức tạp. **Bổ sung:** định tuyến theo tiến độ thiết lập, điểm làm việc mặc định và thông báo chưa có phạm vi. Chính sách local không tự được coi là cấu hình SaaS đã nghiệm thu.

### F-02. Hướng dẫn thiết lập từng bước — đã chốt, cần bổ sung giao diện wizard

| Bước | Người thực hiện và thao tác | Điều kiện hoàn thành |
|---|---|---|
| 1. Thông tin cửa hàng | Chủ nhập tên hiển thị, liên hệ, địa chỉ, logo tùy chọn; VND và giờ Việt Nam mặc định | Tên cửa hàng có dữ liệu; các trường hồ sơ/in phiếu cần bổ sung |
| 2. Điểm bán và kho | Tạo chi nhánh đầu tiên, kho đầu tiên; thêm điểm khác nếu có | Ít nhất một chi nhánh và một kho hoạt động |
| 3. Người dùng | Tạo tài khoản, vai trò, kho phụ trách, người duyệt khác người lập | Chủ có quyền làm việc; có người duyệt cho luồng bắt buộc |
| 4. Danh mục | Tạo/nhập đơn vị, hàng hóa, khách, NCC; thiết lập đổi trả/bảo hành, giá/ưu đãi | Có sản phẩm hợp lệ; có thể chọn “Nhập sau” khi chưa phát sinh nghiệp vụ |
| 5. Số dư đầu kỳ | Chọn ngày bắt đầu; nhập tồn theo kho/lô/serial và nợ theo đối tác; xem lỗi, gửi duyệt | Phiếu đầu kỳ đã ghi hoặc xác nhận không có số dư; ngày hiệu lực cần bổ sung |
| 6. Chính sách | Mức giảm tự quyết, theo dõi tồn mặc định; kiểm tra quy tắc không âm và duyệt | Quy tắc được lưu; không bắt nhập cấu hình nâng cao |
| 7. Kiểm tra và bắt đầu | Xem danh sách đã đủ/chưa đủ, đối chiếu tồn/nợ, chọn điểm làm việc | Lưu mốc hoàn thành, mở Tổng quan và hướng dẫn nhập/bán |

Lưu tiến độ sau mỗi bước; có thể quay lại sửa khi chưa ghi giao dịch. “Nhập sau” không xóa dữ liệu đã có. Bỏ qua số dư nghĩa là người dùng xác nhận số dư bằng 0, không tự kết luận cửa hàng không có tồn/nợ. Một công ty chỉ có một bộ số dư bắt đầu cho cùng phạm vi/ngày; bổ sung sau bằng chứng từ điều chỉnh có lý do.

Hiện tại màn hình Thiết lập ban đầu có các chức năng rời (chi nhánh/kho, danh mục, quyền kho, giá, nhập đầu kỳ); chưa có bộ hướng dẫn, lưu tiến độ và checklist hoàn thành.

## 6. Luồng vận hành hằng ngày

### F-03. Ngày làm việc tiêu chuẩn

1. Đăng nhập → xác nhận chi nhánh/kho → xem tab Tổng quan phù hợp.
2. Kiểm tra cần duyệt, đơn chưa xuất, hàng đang chuyển, nhắc khách và COD chưa đối soát.
3. Hàng về: tạo phiếu nhập trực tiếp hoặc nhận từ đơn mua → người khác duyệt → tồn/NCC phải trả cập nhật.
4. Khách tại quầy: chọn hàng → xem giá → thu toàn bộ hoặc một phần → xác nhận → in phiếu.
5. Khách online/sỉ: tạo đơn tay → ghi cọc nếu có → xuất từng phần khi đủ hàng → theo dõi giao → thu/đối soát COD.
6. Thu tiền nợ hoặc trả NCC: ghi khoản tiền → phân bổ đúng phiếu → kiểm tra số dư.
7. Phát sinh trả/bảo hành/chuyển kho: dùng phiếu gốc và luồng tương ứng; không sửa tồn thủ công.
8. Cuối ngày xem bán–trả, nhập–trả mua, thu–chi, tồn và nợ; kiểm tra ngoại lệ còn treo, xuất dữ liệu khi cần.

Không mở ca, không kiểm đếm tiền đầu/cuối ca. Không chạy offline. Các bước không có phát sinh được bỏ qua.

### F-04. Quy tắc giao dịch chung

Nháp không tác động tồn/tiền/nợ. Chờ duyệt không phải đã ghi. Chỉ **Đã ghi** làm phát sinh sổ. Đơn/báo giá đã xác nhận không làm tăng doanh thu. Giao hàng đã giao không ghi doanh thu lần hai. Thu tiền không làm tăng doanh thu lần hai.

Phiếu đã ghi giữ nguyên; sửa sai bằng trả/hoàn/điều chỉnh liên kết nguồn. Nháp cần cho sửa, thêm/bỏ dòng trước gửi (hiện tại chủ yếu tạo và hủy nháp, còn thiếu chỉnh sửa đầy đủ). Mọi từ chối/hủy/đóng/duyệt ngoại lệ cần lý do và người thực hiện. Không xóa vật lý chứng từ đã ghi.

Nếu nhiều người cùng thao tác, máy chủ kiểm tra lại phiên bản phiếu/tồn/serial; dữ liệu thay đổi thì yêu cầu tải lại. Cần bổ sung khóa chống tạo trùng khi gửi lại sau timeout; phiên bản phiếu hiện có chưa đủ để bảo đảm mọi yêu cầu tạo mới không trùng.

## 7. Module Sản phẩm — dữ liệu gốc

### F-05. Tạo và sử dụng sản phẩm

1. Vào Danh sách sản phẩm → Thêm sản phẩm; nhập mã, tên, đơn vị cơ sở, giá; chọn theo dõi thường/lô/serial.
2. Chọn sản phẩm không có biến thể hoặc có biến thể. Nếu có, chọn thuộc tính và giá trị từ danh mục đã cấu hình, ví dụ Màu = Đỏ/Đen, Size = M/L; không nhập màu/size tự do. Chọn các tổ hợp thực tế kinh doanh, tạo SKU cho từng tổ hợp. Nhập barcode, VAT nội bộ, số ngày trả/bảo hành, cho trả khi mở bao bì, trọng lượng nếu cần.
3. Với lô: nhập/xuất yêu cầu theo lô và hạn. Với serial: từng đơn vị cơ sở phải có đúng một serial.
4. Thêm quy đổi đơn vị, ví dụ 1 thùng = 24 chai; tạo sản phẩm không sinh tồn.
5. Tìm theo mã/tên/barcode; ngừng sử dụng khi không giao dịch tiếp. Lịch sử cũ vẫn xem được.

**Có:** SKU độc lập, tạo/tìm/ngừng, mô tả biến thể, cấu hình theo dõi. **Bổ sung theo quyết định đã chốt:** chỉnh sửa có kiểm soát, gán nhóm/thương hiệu/thuộc tính, quản lý sản phẩm cha và chọn tổ hợp biến thể cấu hình sẵn. Không cho đổi đơn vị cơ sở hoặc kiểu theo dõi khi đã có tồn/phiếu mà không có quy trình chuyển đổi riêng.

Sản phẩm không có biến thể có một SKU giao dịch. Sản phẩm có biến thể là hồ sơ cha để gom nhóm; không giữ tồn hoặc phát sinh giao dịch ở cha. Mỗi tổ hợp được chọn có mã SKU duy nhất, barcode nếu dùng, giá và tồn riêng; không cho trùng tổ hợp trong cùng sản phẩm. Có thể kế thừa đơn vị/chính sách từ cha khi tạo SKU, nhưng giao dịch lưu chính sách thực tế của SKU. Trong bán/mua/chuyển/kiểm kê phải chọn đúng SKU, ví dụ Áo A — Đỏ — M. Không buộc tạo toàn bộ tổ hợp nếu cửa hàng chỉ kinh doanh một số tổ hợp.

### F-06. Danh mục và quy đổi

Tạo nhóm, thương hiệu, đơn vị, thuộc tính tại menu Sản phẩm. Trong Thuộc tính, cấu hình mã/tên thuộc tính và các giá trị được phép (ví dụ Màu: Đỏ, Đen; Size: M, L). Khi tạo biến thể chỉ chọn giá trị đang hoạt động; thiếu giá trị thì người có quyền bổ sung danh mục rồi quay lại chọn. Danh mục không dùng nữa chuyển ngừng; không xóa giá trị đã tham chiếu. Hiện các danh mục nhóm/thương hiệu/thuộc tính lưu dạng mã–tên–giá trị chung, **chưa có liên kết hoàn chỉnh vào SKU hoặc bộ chọn tổ hợp biến thể**. Không đưa cấu hình này trở lại Cấu hình kho.

Quy đổi: chọn SKU → chọn đơn vị khác đơn vị cơ sở → nhập hệ số dương tối đa 4 chữ số lẻ → lưu. Mỗi SKU/đơn vị có một hệ số; đơn vị cơ sở ngầm bằng 1. Cập nhật hệ số chỉ áp dụng phiếu mới; phiếu cũ giữ hệ số đã lưu. Giá trên dòng theo đơn vị giao dịch, tồn theo đơn vị cơ sở. Không mặc định coi hai SKU cùng tên là cùng hàng hoặc cùng hệ số.

### F-07. Bảng giá và khuyến mãi

Trong Thiết lập ban đầu: chọn SKU → quy tắc giá theo khách/nhóm khách/chi nhánh/kênh, ngưỡng số lượng cơ sở và thời gian hiệu lực; khuyến mãi theo SKU, phần trăm, ưu tiên, cộng dồn hay không. Xem giá trước xác nhận; lưu kết quả và căn cứ giá trên phiếu.

Giá ưu tiên: khách riêng → nhóm khách → chi nhánh/kênh → giá mặc định. Trong cùng cấp chọn ngưỡng lượng phù hợp lớn nhất, rồi độ ưu tiên giảm dần, rồi ID tăng dần để hòa ổn định. Với khuyến mãi hiện tại, nếu có chương trình không cộng dồn phù hợp thì chọn chương trình không cộng dồn có ưu tiên cao nhất và bỏ các chương trình khác; nếu không có, áp lần lượt các chương trình cộng dồn theo ưu tiên rồi ID. Giảm thêm thủ công vượt mức được cấp hoặc bán dưới vốn → gửi duyệt. Đây là quy tắc nguồn đang chạy; cần cập nhật SRS cũ và ca kiểm chồng chương trình để mọi nơi tính giống nhau.

## 8. Module Mua hàng

### F-08. Nhà cung cấp

Tạo hồ sơ mã/tên, liên hệ và địa chỉ → chọn trong đơn/phiếu nhập → tra phải trả và chi tiền tại Kế toán → ngừng khi không dùng. Không nhập nợ trực tiếp trên hồ sơ. Các trường mã số thuế, người liên hệ, điều khoản trả là đề xuất bổ sung, không bắt buộc người bán nhỏ nhập.

### F-09. Nhập hàng trực tiếp — luồng mặc định

1. Chọn kho nhận và NCC, ngày tham chiếu nếu bổ sung, ghi chú/chứng từ NCC nếu có.
2. Chọn SKU/đơn vị/lượng/giá mua/giảm/VAT. Lô phải có mã và hạn; serial phải đủ và không trùng.
3. Nhập phí mua đã biết, cách phân bổ; nhập đã trả ngay và phương thức nếu có.
4. Lưu nháp → kiểm tra → gửi duyệt kèm lý do.
5. Người khác có quyền xem giá/lượng/thông tin tracking → duyệt hoặc từ chối.
6. Duyệt thành công: tăng tồn kho nhận, cập nhật giá vốn, phát sinh phải trả theo tổng phiếu; tiền trả ngay tạo khoản chi và phân bổ. Xem số còn nợ NCC.

Nhập thiếu lô/serial, sai hệ số hoặc giá trị âm bị chặn. NCC bắt buộc. Không tự tạo sản phẩm từ form nhập; hướng dẫn sang Sản phẩm, lưu nháp rồi quay lại.

Phí mua vốn hóa hiện có nhưng khoản phí có thể trả bên vận chuyển khác NCC hàng: phải tách người nhận tiền và nghĩa vụ trả, tránh tự cộng cả phí vào nợ NCC hàng. Đây là phần cần chuẩn hóa, không mặc định dữ liệu nợ hiện tại đã phân tách phí đầy đủ. Cờ “giá vốn tạm thời” chỉ là thông tin; chưa tự điều chỉnh phí đến muộn.

### F-10. Mua theo kế hoạch — tùy chọn

Đề nghị mua → gửi/duyệt → tạo đơn mua từ đề nghị hoặc lập đơn riêng → gửi/duyệt → nhận hàng từ đơn → duyệt từng phiếu nhập → đơn nhận một phần hoặc hoàn thành.

Một đơn nhận nhiều lần, mỗi phiếu nhận có kho/lô/serial/giá/tiền riêng. Chưa nhận đủ thì số còn nhận vẫn mở. Nhận thừa hoặc khác giá đặt phải hiển thị chênh lệch và được người có quyền xác nhận, có lý do. Không tự nhận thêm dòng hàng ngoài đơn; lập bổ sung hoặc nhập trực tiếp. Đóng phần chưa nhận phải có lý do; không làm giảm hàng đã nhập.

**Có:** nhận nhiều lần, xác nhận chênh lệch qua duyệt. **Bổ sung:** bảng đặt–đã nhận–lần này–còn lại dễ đọc, quyền đóng/hủy theo chính sách và liên kết vòng đời đề nghị/báo giá nguồn. Việc sử dụng lại nguồn nhiều lần cần chống vượt hoặc chỉ rõ nguồn tham khảo.

### F-11. Trả nhà cung cấp

Chọn phiếu nhập đã ghi → chọn dòng/lượng trả còn được trả → kiểm tra tồn/lô/serial đúng → gửi/duyệt → giảm tồn và ghi giảm phải trả. Nếu đã thanh toán, phần NCC phải hoàn theo dõi riêng và ghi “NCC hoàn tiền” khi thực nhận.

**Có:** trả mua, giảm nợ còn mở, khoản tiền NCC hoàn. **Bổ sung bắt buộc:** sổ theo dõi khoản NCC còn phải hoàn và phân bổ đúng khoản trả; một khoản thu rời không chứng minh đã tất toán hoàn NCC. Không trả vượt lượng nguồn sau các lần trả. Hàng cách ly cần quy trình xử lý riêng trước khi xuất trả.

## 9. Module Bán hàng

### F-12. Bán tại quầy — luồng nhanh

1. Chọn kho làm việc; tìm/quét hàng có sẵn trong danh mục.
2. Thêm dòng, số lượng/đơn vị; chọn khách nếu muốn lưu lịch sử hoặc bán còn nợ. Khách vãng lai được bỏ trống khi trả đủ.
3. Xem giá theo chính sách, khuyến mãi, giảm và VAT; chọn serial; lô để trống cho FEFO hoặc chọn lô hợp lệ.
4. Nhập tiền nhận và phương thức. Không ghi tiền khách đưa vượt tổng phiếu vào khoản thu; tiền thối chỉ hiển thị hỗ trợ, không phải doanh thu.
5. Xác nhận: trong chính sách → ghi phiếu ngay; ngoại lệ → chờ người khác duyệt. Không giao hàng khi phiếu còn chờ duyệt.
6. Đã ghi → trừ tồn, lưu vốn, phát sinh phải thu; tạo thu/phân bổ nếu khách trả; in phiếu bán.

Thiếu tồn/serial/lô hợp lệ → chặn xuất; đề nghị chuyển phần thiếu sang đơn đặt trước, không ghi tồn âm. **Hiện chưa tự tách phần thiếu thành đơn**; nhân viên lập đơn bán thủ công cho phần thiếu. Bán công nợ thuộc quyền bán, không kiểm hạn mức và không xin duyệt chỉ vì còn nợ; nhưng khách phải được chọn. Ngưỡng giảm/bán dưới vốn vẫn áp dụng.

### F-13. Báo giá → đơn bán → xuất → giao → thanh toán

1. Báo giá tùy chọn: khách, kho, hàng, giá, hiệu lực, phí dự kiến → xác nhận/gửi duyệt ngoại lệ. Không ảnh hưởng tồn/nợ.
2. Tạo đơn từ báo giá hoặc trực tiếp; chọn kênh online/sỉ/quầy/CTV, khách, địa chỉ giao và hàng; xác nhận.
3. Có cọc → Kế toán ghi thu gắn đơn; cọc là tiền ứng trước, chưa phải doanh thu/nợ đã xuất.
4. Kho xem lượng chưa xuất và tồn → tạo phiếu xuất từ dòng đơn. Mỗi phiếu một kho; nhiều phiếu có thể lấy từ nhiều kho.
5. Ghi phiếu xuất đủ điều kiện hoặc duyệt ngoại lệ → trừ tồn, phát sinh doanh thu nội bộ/giá vốn/nợ; phân bổ cọc vào phiếu đã xuất.
6. Tạo đợt giao từ phiếu xuất đã ghi → cập nhật tay chuẩn bị/đang giao/giao một phần/đã giao/thất bại.
7. Thu thêm, đối soát COD hoặc để công nợ → phân bổ vào khoản phải thu. Đơn hoàn thành về xuất khi toàn bộ lượng đã xuất; trạng thái tiền/giao cần hiển thị độc lập.

Đơn online nhập thủ công, hãng vận chuyển/mã vận đơn/phí nhập tay. Một đơn có thể nhiều kho, nhiều lần xuất, nhiều đợt giao và nhiều lần thu. Hiện trạng thái giao “một phần” chưa lưu lượng giao theo dòng; cần bổ sung để biết chính xác lượng giao, thất bại và còn giao. Phí giao trên đơn xuất phân bổ theo phần hàng đã xuất; không cộng lại ở mỗi đợt.

### F-14. Hủy theo yêu cầu khách

Nháp chưa phát sinh → hủy kèm lý do. Đơn xác nhận/đã xuất một phần → yêu cầu đóng phần chưa xuất; ghi người yêu cầu/lý do, áp dụng duyệt hủy. Có cọc chưa phân bổ → lập hoàn cọc, duyệt và chi trả. Phần đã xuất phải trả hàng theo phiếu bán gốc; không xóa xuất/thu cũ. Giao thất bại không tự động hủy, không tự cộng hàng vào tồn.

**Khoảng thiếu hiện tại:** hủy nháp/đóng phần còn lại trực tiếp chưa có yêu cầu duyệt riêng. Bản chuẩn yêu cầu người khác duyệt việc hủy có ý nghĩa nghiệp vụ; bỏ một nháp chưa gửi có thể xử lý như bỏ bản soạn, không tạo giao dịch đảo.

### F-15. COD và phí vận chuyển

Tạo đợt giao với COD không vượt nợ phiếu xuất; phí dự kiến không âm → ghi giao thành công/một phần → kế toán nhập **COD gộp**, phí thực tế bị trừ, phương thức nhận → kiểm tra → ghi thu COD gộp và phân bổ nợ, ghi chi phí bị trừ riêng. Thực nhận = COD gộp − phí lần đối soát.

Ví dụ: COD 500.000, hãng trừ 25.000, ngân hàng nhận 475.000. Ghi giảm nợ khách 500.000; chi phí 25.000; thực nhận 475.000. Không ghi giảm nợ chỉ 475.000. Đối soát nhiều lần không vượt COD/nợ còn mở.

**Bổ sung:** phát hiện COD trùng giữa nhiều đợt cùng phiếu, tham chiếu bảng kê hãng, đối soát phí khi COD = 0 và phân biệt phí cửa hàng chịu/phí khách trả. Chưa có kết nối API hãng vận chuyển.

## 10. Module Kho

### F-16. Tra tồn và lịch sử

Chọn kho/SKU → xem tổng tồn, cách ly, khả dụng, đơn vị cơ sở; mở lô/hạn hoặc serial; xem lịch sử phiếu làm tăng/giảm. Người có quyền vốn xem giá trị và vốn bình quân. Tồn khả dụng = tồn thực ghi − cách ly; hiện chưa có cơ chế giữ hàng cho đơn. Đơn chưa xuất không tự làm giảm khả dụng.

Ngày hết hạn bằng hôm nay hiện vẫn xuất được; ngày trước hôm nay bị chặn bán. Hàng không hết hạn và bán được chọn FEFO (hạn sớm trước, hòa theo mã lô). Hàng cách ly/hết hạn phải xử lý riêng, không tính là hàng sẵn bán chỉ vì tổng tồn dương. Cần bổ sung cảnh báo sắp hết hạn và tồn thấp theo ngưỡng; chưa có đủ ngưỡng cấu hình.

### F-17. Chuyển kho

Đề nghị chuyển: kho nguồn khác kho đích, hàng/lượng/tracking → gửi → người khác duyệt → kho nguồn xuất → hàng đang chuyển → kho đích nhận từng phần → hoàn thành khi đủ.

Xuất nguồn giảm tồn ngay, giữ giá trị vốn chuyển; chưa tăng tồn đích. Nhận đích tăng tồn theo lượng nhận và vốn chuyển, không dùng giá mua mới. Serial/lô phải thuộc phần đã xuất. Hàng hỏng nhận vào cách ly và có lý do. Nếu nhận thiếu, phần thiếu vẫn đang chuyển; không tự mất khỏi hệ thống.

**Có:** duyệt, xuất, đang chuyển, nhận một phần, hỏng vào cách ly. **Bổ sung:** biên bản và duyệt xử lý thiếu/mất để đóng phần chuyển treo; nhận từng dòng tốt/hỏng trong cùng lần nhận (UI hiện chọn tình trạng chung); quyền hai đầu cho thủ kho đích; thông báo kho đích.

### F-18. Kiểm kê

Chọn kho và danh sách SKU → lập kỳ → bắt đầu đếm và chụp tồn → dừng giao dịch làm thay đổi tồn trong phạm vi kiểm kê → nhập số thực tế theo đơn vị cơ sở, đủ serial/lô (lô không còn phải ghi 0) → kết thúc đếm, ghi nguyên nhân → người khác duyệt → ghi chênh lệch tăng/giảm → mở lại giao dịch.

**Quy tắc đã chốt:** chỉ khóa sản phẩm đang đếm tại kho kiểm kê; bán/nhập/xuất sản phẩm khác tại cùng kho và giao dịch ở kho khác vẫn được. Khóa từ bắt đầu đếm đến khi ghi hoặc hủy kỳ, bao gồm lúc chờ duyệt. Quy tắc này khớp phạm vi khóa SKU trong mã nguồn và SRS 1.0. Có hàng cách ly hiện bị chặn mở kiểm kê SKU đó; phải bổ sung quy trình tách/kiểm hàng cách ly.

### F-19. Nhập/xuất khác và hàng cách ly

Nhập/xuất khác dùng cho tăng/giảm ngoài mua/bán, ghi mục đích và lý do → gửi/duyệt → cập nhật tồn, không tạo doanh thu mua/bán. Cần chọn loại rõ: tặng, sử dụng nội bộ, hủy hết hạn, phát hiện dư/thiếu; tránh dùng thay phiếu nhập mua hoặc trả bán.

Hàng chờ kiểm nhận vào cách ly. Quy trình cần bổ sung: kiểm tra → bán lại được (chuyển cách ly sang khả dụng) / trả NCC / hủy → người có quyền xác nhận → lưu lịch sử và giá trị. Hiện có số lượng cách ly nhưng chưa đủ chức năng giải phóng/hủy cách ly; không hứa người dùng xử lý trọn vòng ngay.

### F-20. Cấu hình kho

Chủ/quản lý thiết lập kiểu theo dõi mặc định khi tạo SKU (thường/lô/serial). Mặc định không âm, FEFO và chặn bán hàng hết hạn là quy tắc hệ thống; hiện chưa phải các công tắc cấu hình. Thay mặc định không đổi hàng đã tạo. Chính sách kiểm kê/tồn thấp/sắp hết hạn là phần bổ sung.

## 11. Module CRM

### F-21. Khách và cộng tác viên

Tạo mã/tên, loại khách hoặc CTV, điện thoại/email/địa chỉ/nhóm khách → chọn trên giao dịch → tra lịch sử đúng phạm vi → ghi chú chăm sóc → đặt lịch gọi → đánh dấu hoàn thành sau khi xử lý. Nhóm khách hiện là chữ trên hồ sơ, chưa có danh mục nhóm khách riêng.

Lịch sử bao gồm loại, số, ngày, trạng thái, tổng tiền phiếu; phải phân biệt nháp và đã ghi. Nợ tra từ sổ nợ; không sửa nợ trên hồ sơ. Ghi chú bắt buộc; lịch nhắc tùy chọn. **Có:** lịch/hoàn thành. **Bổ sung:** người phụ trách, kết quả gọi, thời gian hoàn thành, lịch sử sửa, nhắc trên ứng dụng; không tự hứa email/SMS. Quyền ẩn thông tin cá nhân hoặc chỉ hiện khách của nhân viên là cấu hình đặc thù sau.

## 12. Module Kế toán nội bộ

### F-22. Thu chi

Chọn thu khách/chi NCC/thu NCC hoàn/chi hoa hồng → chi nhánh, đối tác nếu liên quan, phiếu/đơn, số tiền nguyên VND, phương thức và tham chiếu → ghi → phân bổ ngay hoặc sau. Thu/chi không có đối tác chỉ cho mục đích nội bộ có nội dung rõ. Chi phí cần nhóm thu/chi và ngày hiệu lực ở bản chuẩn; hiện chủ yếu nội dung tự do.

Không ghi hoàn khách bằng khoản chi rời; dùng phiếu hoàn có duyệt. Khoản tiền đã ghi cần bất biến, sửa sai bằng nghiệp vụ đảo có liên kết; hiện chưa có luồng đảo khoản thu/chi đầy đủ. Tiền chuyển giữa các quỹ/tài khoản chưa phải chức năng hoàn thiện.

### F-23. Công nợ và ứng trước

Chọn phải thu/phải trả → xem đối tác, phiếu, phát sinh, đã giảm do trả, đã phân bổ, còn mở → chọn khoản tiền chưa phân bổ → phân bổ không vượt cả tiền dư và nợ → cập nhật số dư. Một khoản tiền chia nhiều phiếu; một phiếu nhận nhiều khoản. Đối tác và chiều tiền phải khớp; phân bổ giữa các chi nhánh cần người có quyền cả hai.

Nợ còn mở = phát sinh − giảm do trả − phân bổ. Ứng trước = khoản thu/chi chưa phân bổ còn hợp lệ, phải trừ phần hoàn/đảo. Hiện hạn nợ tự đặt 30 ngày, chưa có form điều khoản/ngày đến hạn; **không dùng hạn/quá hạn để chặn bán**. Chuẩn cần cho chọn ngày đến hạn và cảnh báo tham khảo.

### F-24. Hoàn tiền và đổi hàng

Trả đã ghi giảm nợ chưa thu trước. Chỉ phần tiền đã thu hợp lệ cho lượng trả mới được hoàn. Lập phiếu hoàn gắn phiếu trả → chọn phương thức gốc hoặc phương thức khác có lý do → gửi/duyệt → tạo khoản hoàn → tra tổng đã hoàn không vượt phần được hoàn.

Đổi hàng = trả hàng cũ + bán hàng mới, hai chứng từ có tham chiếu. Không sửa phiếu cũ. Hệ thống cần hiển thị giá trị được bù, khách trả thêm hoặc cửa hàng hoàn thêm; bù tiền phải có dữ liệu phân bổ, không chỉ ghi chú. Hiện có liên kết đổi, chưa có luồng bù tiền đổi hoàn chỉnh.

### F-25. Hoa hồng thủ công

Kế toán chọn CTV/chi nhánh/kỳ tháng → ghi khoản theo phiếu bán đã ghi; cuối tháng chỉ ghi **phần bổ sung**, không ghi lại tổng tháng → trả/hủy phát sinh ghi điều chỉnh tăng/giảm có lý do → chi hoa hồng không vượt số còn phải trả của kỳ.

Ví dụ theo đơn đã ghi 300.000, chính sách cuối tháng tổng 500.000 → ghi bổ sung 200.000; tổng phải trả 500.000. Không tự tính tỷ lệ, không tự ghi đảo khi hàng trả; người có quyền tự ghi điều chỉnh. Phải có bảng chi tiết đã ghi/đã trả/còn trả theo kỳ; hiện đã có cấu trúc và kiểm mức chi, cần hoàn thiện tra cứu nhiều kỳ.

## 13. Đổi trả và bảo hành

### F-26. Trả hàng, có thể khác chi nhánh

1. Nhập đúng số phiếu bán gốc đã ghi → hệ thống lấy dòng hàng/serial/lô/chính sách tại thời điểm bán.
2. Chọn kho nhận tại chi nhánh trả và lượng còn được trả.
3. Kiểm tình trạng, thời hạn theo sản phẩm, đúng serial/lô đã bán; ghi lý do. Serial sai luôn chặn, không duyệt để bỏ qua.
4. Hàng tốt hoặc mở bao bì đủ điều kiện → trả; hàng chờ kiểm → cách ly; hàng lỗi → bảo hành nếu đủ điều kiện.
5. Gửi người khác duyệt, quá hạn phải nêu ngoại lệ → đã ghi tăng tồn kho nhận, ghi giảm doanh thu nội bộ, hoàn vốn theo phiếu gốc và giảm nợ nguồn.
6. Có tiền cần hoàn/đổi → dùng F-24. Phiếu trả ghi rõ chi nhánh nhận và phiếu/chi nhánh bán ban đầu.

**Điểm cần xác nhận lại:** yêu cầu trước cho phép hàng đã sử dụng được trả, nhưng SRS 1.0/mã hiện tại chuyển USED sang bảo hành. Bản chuẩn đề xuất trường “cho trả hàng đã sử dụng” theo sản phẩm, kiểm tra tình trạng và duyệt; chỉ áp dụng sau khi người dùng chốt, chưa tự đổi quy tắc đang chạy. Hàng lỗi không mặc định đưa vào tồn bán được. Phí giao hàng có hoàn hay không phải cấu hình riêng; mặc định đề xuất không hoàn phí dịch vụ đã thực hiện.

### F-27. Bảo hành

Tìm phiếu bán → chọn dòng/số lượng/serial và kho nhận → kiểm còn thời hạn bảo hành theo chính sách đã bán → ghi tình trạng → tiếp nhận → đang xử lý → sẵn bàn giao → đã bàn giao. Serial đúng và không có phiếu bảo hành đang mở khác. Bảo hành không tự tăng tồn hàng bán/giảm nợ/hoàn tiền.

**Có:** hồ sơ, kiểm hạn/serial, trạng thái. **Bổ sung:** chọn dòng qua giao diện thay nhập ID, người xử lý, ngày hẹn, nơi gửi bảo hành, kết quả/chi phí/biên bản bàn giao và sổ vật phẩm giữ hộ. Quá hạn hiện bị chặn; dịch vụ sửa chữa có phí hoặc ngoại lệ ngoài hạn thuộc yêu cầu riêng.

## 14. Duyệt và trạng thái

### F-28. Duyệt

Người lập gửi lý do → danh sách Cần duyệt đúng phạm vi → người khác mở phiếu, đối chiếu hàng/giá/tồn/tiền/tracking → duyệt hoặc từ chối có lý do → hệ thống kiểm lại điều kiện → cập nhật trạng thái và sổ nếu là phiếu ghi. Không cho người tạo tự quyết định. Từ chối không thay đổi sổ; cần sao chép/lập lại hoặc luồng chỉnh nháp mới.

| Nghiệp vụ | Quy tắc chuẩn | Hiện trạng |
|---|---|---|
| Bán trong bảng giá/chính sách | Xác nhận trực tiếp | Có |
| Giảm vượt mức; dưới vốn | Người khác duyệt | Có; vốn kiểm lại khi ghi |
| Bán còn nợ đúng quyền bán | Không duyệt riêng vì nợ | Có |
| Đề nghị/đơn/nhập/trả mua | Người khác duyệt | Có |
| Xuất bán thường theo chính sách | Cho phép trực tiếp; ngoại lệ duyệt | Có; khác yêu cầu ban đầu “mọi xuất đều duyệt”, đã theo cơ chế chính sách |
| Chuyển kho; nhập/xuất khác; đầu kỳ; kiểm kê | Người khác duyệt | Có |
| Trả bán; hoàn tiền | Người khác duyệt | Có |
| Hủy/đóng đơn có hiệu lực | Người khác duyệt trước đóng/đảo | Cần bổ sung |

Không cho chỉnh nội dung trọng yếu sau duyệt rồi thực hiện theo duyệt cũ. Giao diện cần tóm tắt người tạo, thời điểm, lý do và tác động dự kiến; không buộc chủ cửa hàng hiểu mã trạng thái tiếng Anh.

| Nhóm | Trạng thái và chuyển chính | Tác động |
|---|---|---|
| Phiếu nhập/bán/trả/khác/đầu kỳ/hoàn | Nháp → Chờ duyệt → Đã ghi; hoặc Nháp → Đã ghi nếu chính sách cho phép; từ chối/hủy nháp | Chỉ đã ghi tác động sổ |
| Báo giá/đề nghị | Nháp → Xác nhận hoặc chờ duyệt → Đã duyệt → Đóng | Không tác động tồn/tiền |
| Đơn mua/bán | Nháp → Đã duyệt → Một phần → Hoàn thành; đóng phần chưa thực hiện | Tiến độ theo phiếu nhận/xuất, không theo tiền |
| Chuyển kho | Nháp → Chờ duyệt → Đã duyệt → Đang chuyển → Một phần → Hoàn thành | Giảm nguồn khi xuất, tăng đích khi nhận |
| Kiểm kê | Nháp → Đang đếm → Chờ duyệt → Đã ghi; hủy khi đang đếm theo quyền | Khóa trong khi đếm/chờ; chênh lệch khi ghi |
| Đợt giao | Chuẩn bị → Đang giao → Giao một phần/Đã giao/Thất bại; một phần có thể giao tiếp | Không tự thay tồn/nợ |
| Bảo hành | Tiếp nhận → Đang xử lý → Sẵn bàn giao → Đã bàn giao | Theo dõi vật phẩm, không ghi bán |

## 15. Tổng quan, báo cáo và nhập/xuất

### F-29. Tổng quan

Sáu tab theo quyền: Mua (nhập hôm nay, đơn còn nhận, NCC); Bán (bán trừ trả hôm nay, đơn còn xuất, phiếu cần duyệt); Kho (tồn, chuyển chưa nhận, kiểm kê); Sản phẩm (SKU hoạt động/lô/serial); CRM (khách, nhắc việc); Kế toán (nợ, thu/chi liên quan).

**Hiện có sáu tab.** Số liệu hiện cộng toàn phạm vi được cấp, không tự theo kho đang chọn ở thanh làm việc. Bản chuẩn cần bộ lọc kỳ/chi nhánh/kho, nhãn phạm vi và liên kết từ chỉ số sang danh sách. CRM hiện chỉ lấy tối đa 20 lịch nhắc trên summary; không dùng số đó làm tổng việc toàn hệ thống. Thu/chi hôm nay phải ghi rõ nhóm khoản tiền đang được tính, tránh hiểu là toàn bộ dòng tiền.

### F-30. Báo cáo

Chọn thời gian/phạm vi → xem doanh số và trả, nhập và trả mua, tồn đầu–nhập–xuất–cuối, nợ/thu chi → mở phiếu nguồn → xuất theo quyền. Chỉ phiếu đã ghi vào báo cáo phát sinh. Giá vốn/lợi nhuận chỉ cho người có quyền. Báo cáo phải có tổng hợp và chi tiết đủ đối chiếu, không chỉ hiển thị 1.000 dòng rồi gọi là toàn bộ dữ liệu.

Hiện có báo cáo phiếu theo kỳ, tồn theo vận động và xuất CSV; các báo cáo tiền/nợ/hoa hồng/COD và lợi nhuận chuẩn cần bổ sung/chuẩn hóa phạm vi và cách tính. Khoảng truy vấn hiện tối đa 3 năm; không phải bảo đảm dữ liệu đã được phân trang đầy đủ.

### F-31. Nhập dữ liệu

Chọn hàng hóa/đối tác/tồn đầu/nợ đầu → kho/phạm vi → tải mẫu → nhập Excel/CSV hoặc gửi dữ liệu qua API nhập chung → xem trước lỗi theo dòng → sửa → xác nhận nhập → theo dõi batch và phiếu đầu kỳ → gửi/duyệt phiếu → đối chiếu.

Hiện tệp tối đa 2 MB, 500 dòng/lần; Excel lấy sheet đầu, không nhận công thức; ngày YYYY-MM-DD; serial ngăn bằng `|`. Hàng/đối tác ghi danh mục khi commit; tồn/nợ tạo **phiếu nháp**, cần gửi duyệt/ghi tiếp. “Đã nhập batch” không đồng nghĩa “đã tăng tồn/đã ghi nợ”. Kiểm dữ liệu và phạm vi cả preview lẫn commit; chống nhập lại cùng dữ liệu. Chưa có kết nối kéo dữ liệu tự động từ hệ thống ngoài.

### F-32. Xuất và in

Xuất hàng/NCC/tồn/lịch sử/phiếu/tiền/nợ/báo cáo thành CSV theo quyền, có ghi nhật ký; cần chuẩn hóa lọc và số dòng thực xuất. In phiếu bán nội bộ, không gắn nhãn hóa đơn điện tử. Mẫu cần có số/ngày/điểm bán, người bán, khách, hàng/đơn vị/lượng/giá/giảm/VAT/tổng, đã thu/còn nợ, phương thức, serial/lô khi có và điều kiện đổi trả. Khổ 80 mm cho quầy, A4 cho phiếu khác là mục tiêu cần QA máy in; không xác nhận mẫu in đã đạt chỉ từ BA.

## 16. Quy tắc dữ liệu và tính toán thống nhất

| Mã | Quy tắc |
|---|---|
| R-01 | Một công ty trong một không gian dữ liệu; nhiều chi nhánh/kho. Cơ cấu tổ chức Core không mặc nhiên đồng nhất chi nhánh bán hàng; cần liên kết rõ |
| R-02 | VND, tiền ghi sổ nguyên đồng; lượng và đơn giá tối đa 4 chữ số lẻ; giờ Việt Nam, hiển thị dd/MM/yyyy, mã ngày nhập API YYYY-MM-DD |
| R-03 | Lượng cơ sở = lượng giao dịch × hệ số; serial đòi lượng cơ sở nguyên và đủ serial riêng biệt |
| R-04 | Giảm trước thuế; giá chưa VAT mặc định, chọn giá gồm VAT thì tách trước tính. Tiền làm tròn từng dòng HALF_UP, tổng bằng tổng dòng; phần dư phân bổ vào dòng cuối ổn định |
| R-05 | Không âm, không bán hàng cách ly/hết hạn; kiểm lại tồn và tracking khi ghi, không chỉ khi mở form |
| R-06 | Bình quân di động SKU/kho; vốn nhập = hàng sau giảm chưa VAT + phí vốn hóa đủ điều kiện. VAT không tự coi là vốn. Nếu cần VAT không khấu trừ tính vốn phải phát triển/chốt riêng |
| R-07 | Phí biết lúc nhập phân bổ theo giá trị mặc định, hoặc lượng cơ sở/trọng lượng; tổng cơ sở bằng 0 thì không phân bổ. Phí đến muộn thuộc giai đoạn sau |
| R-08 | Xuất bán ghi doanh thu nội bộ/giá vốn/nợ; thu và giao không ghi lần hai. Trả lấy tiền/thuế/vốn nguồn theo lượng, không giá hiện tại |
| R-09 | Sổ bất biến có phiếu nguồn; snapshot giữ tên/đơn vị/hệ số/giá/chính sách lúc giao dịch; đổi danh mục không đổi lịch sử |
| R-10 | Bán công nợ tự quyết theo quyền bán; không áp hạn mức hoặc chặn quá hạn. Quá hạn chỉ nhắc theo thông tin hạn thanh toán |
| R-11 | Đổi trả/bảo hành theo số ngày sản phẩm tại thời điểm bán; 0 là không áp dụng; tìm phiếu gốc, không chấp nhận serial khác |
| R-12 | Ngừng danh mục không xóa lịch sử. Mã trong công ty không trùng; STT danh sách chỉ là thứ tự hiển thị, không phải mã nghiệp vụ |
| R-13 | Người tạo không tự duyệt. Không dùng tài khoản chung để thay người duyệt; ghi rõ thời gian/người/lý do/tác động |
| R-14 | Số phiếu duy nhất, không tái sử dụng khi hủy; cần thay cách sinh dựa số lượng phiếu hiện tại bằng bộ đếm an toàn khi hoàn thiện |

## 17. Quản trị hệ thống và hồ sơ

| Chức năng | Luồng chuẩn và hiện trạng |
|---|---|
| Người dùng | Tạo họ tên/email/mật khẩu/vai trò/đơn vị → gán kho → kiểm quyền → nhân viên đăng nhập. Có tạo, bật/tắt, reset; cần giao diện đổi vai trò/phạm vi đầy đủ và các mẫu nghiệp vụ |
| Cơ cấu tổ chức | Tạo mã/tên/đơn vị cha; phục vụ tổ chức nhân sự. Không dùng tạo thêm tenant khách SaaS |
| Vai trò & phân quyền | Quản trị tạo vai trò/quy tắc, gán người, kiểm truy cập. Giao diện hiện mang thuật ngữ kỹ thuật; cần mẫu vai trò và thao tác cấp/quản lý quyền dễ hiểu, không chỉ tạo policy rời |
| Modules/Resources | Quản trị kỹ thuật xem trạng thái module/tài nguyên; thao tác bật/tắt theo quyền. Ẩn khỏi người bán; không coi là menu hàng hóa |
| Activity/Events & Jobs | Theo dõi xử lý nền, chạy lại/hủy theo trạng thái; dành quản trị kỹ thuật |
| Nhật ký | Tra người/thời gian/hành động/kết quả/đối tượng; không cho nhân viên sửa hoặc xóa nhật ký |
| Tệp | Theo dõi/tải tệp theo quyền; cần liên kết chứng từ nếu dùng ảnh hàng/chứng từ NCC. Có Core quản lý tệp không đồng nghĩa các form bán/mua đã gắn tệp |
| Cài đặt | Cấu hình nền tảng theo quyền; tách hồ sơ cửa hàng khỏi tùy chọn kỹ thuật |
| Hồ sơ cá nhân | Xem tên/email/vai trò, tự đổi mật khẩu hiện tại–mới–xác nhận; đăng xuất |

Các trường và giới hạn chi tiết ở phụ lục. Không yêu cầu nhân viên hiểu tenant, policy, outbox hoặc schema để thao tác bán/nhập hàng.

## 18. Chuẩn giao diện dễ dùng

Form cơ bản chỉ hiện trường thiết yếu, phần lô/serial chỉ xuất hiện theo hàng, phần giao hàng chỉ cho đơn cần giao. Mỗi trường có tên Việt, ví dụ, đơn vị và lỗi tại chỗ. Trường tự tính không cho sửa. Số tiền có phân cách nghìn; hiển thị số dư sau giao dịch dự kiến.

Mọi danh sách nghiệp vụ có STT, tìm kiếm, bộ lọc phù hợp, tổng số, trạng thái rỗng/tải/lỗi, mở chi tiết và hành động theo quyền/trạng thái. Danh sách lớn phải phân trang; số dòng giới hạn phải ghi rõ. Lưu nháp khác Xác nhận, Gửi duyệt khác Đã ghi. Các thao tác tiền/tồn có màn xác nhận tóm tắt tác động.

Hiển thị lỗi thiếu quyền/kho, thiếu tồn, serial sai, hết hạn, phiếu đã thay đổi bằng hướng dẫn xử lý cụ thể. Lỗi mạng sau gửi: cho tra lại phiếu theo tham chiếu, không hướng dẫn bấm liên tục. Không tạo dữ liệu demo vào dữ liệu vận hành.

## 19. Danh sách việc cần hoàn thiện trước nghiệm thu

| ID | Hạng mục | Ưu tiên | Luồng |
|---|---|---|---|
| GAP-01 | Wizard và tiến độ, hồ sơ cửa hàng/ngày bắt đầu/điểm làm việc | P1 | F-01/02 |
| GAP-02 | Mẫu vai trò, gán–thu hồi quyền/phạm vi, người duyệt, quyền hai đầu chuyển | P1 | 4, F-17/28 |
| GAP-03 | Chỉnh nháp, chống tạo trùng/đồng thời, số phiếu an toàn | P1 | F-04, R-14 |
| GAP-04 | Liên kết nhóm/thương hiệu/thuộc tính; sản phẩm cha–SKU, chọn biến thể từ thuộc tính/giá trị cấu hình sẵn | P1 | F-05/06 |
| GAP-05 | Phần thiếu đặt trước rõ ràng, tiến độ xuất/giao/tiền riêng | P1 | F-12/13 |
| GAP-06 | Duyệt hủy/đóng, hoàn cọc và bù tiền đổi có sổ đầy đủ | P1 | F-14/24 |
| GAP-07 | Phí mua theo bên chịu/bên nhận, nợ và thu hoàn NCC đầy đủ | P1 | F-09/11 |
| GAP-08 | COD đa đợt không trùng, phí khách/cửa hàng, bảng kê đối soát | P1 | F-15 |
| GAP-09 | Xử lý thiếu chuyển kho, cách ly; giữ khóa kiểm kê theo SKU đã chốt | P1 | F-17/18/19 |
| GAP-10 | Chốt hàng USED trả/bảo hành, chọn nguồn bảo hành dễ dùng | P1 | F-26/27 |
| GAP-11 | Ngày đến hạn/hiệu lực, đảo thu chi, sổ ứng trước đúng | P1 | F-22/23 |
| GAP-12 | Báo cáo đầy đủ, đối chiếu, phân trang, lọc dashboard, mẫu in | P1 | F-29/30/32 |
| GAP-13 | Nhắc việc có người phụ trách/kết quả, hoa hồng tra nhiều kỳ | P2 | F-21/25 |
| GAP-14 | Cảnh báo tồn thấp/hết hạn, nâng trải nghiệm nhóm danh mục | P2 | F-06/16 |
| GAP-15 | Phí muộn/COGS, carrier API, SaaS tự tách source/data | Sau | Ngoài R1 |

P1: đóng trước nghiệm thu vận hành luồng liên quan; P2: sau khi luồng tiền/tồn ổn định. Bảng không phải đánh giá lỗi tất cả chức năng; là khoảng cách giữa chuẩn BA và hiện trạng. Khi cập nhật SRS cần gắn mỗi GAP vào yêu cầu, API/UI, người phụ trách và ca nghiệm thu.

## 20. Kịch bản nghiệm thu nghiệp vụ

| AC | Tình huống | Kết quả phải chứng minh |
|---|---|---|
| BA2-01 | Chủ lần đầu, chưa có điểm bán | Wizard lưu tiến độ; tạo chi nhánh/kho/người duyệt; vào vận hành đúng quyền |
| BA2-02 | Nhập 1 thùng = 24 chai, bán 3 chai | Tồn tăng 24 rồi còn 21; phiếu nhập vẫn giữ hệ số khi quy đổi mới đổi |
| BA2-03 | Nhập trực tiếp 10 × 100.000, chưa trả | Chờ duyệt chưa tăng tồn/nợ; duyệt tăng 10 và AP 1.000.000 |
| BA2-04 | Đơn mua 10, nhận 6 rồi 4; khác giá | Hai phiếu, một phần rồi hoàn thành; lệch giá cần lý do/người khác duyệt |
| BA2-05 | Bán 200.000 thu 150.000 cho khách có hồ sơ | Xuất đúng lượng, thu 150.000, nợ 50.000; không chặn vì bán chịu |
| BA2-06 | Đơn 1.000.000 cọc 200.000, xuất 600.000 rồi 400.000 | Trước xuất chưa doanh thu/AR; cọc phân bổ tối đa 200.000; nợ cuối 800.000 nếu chưa thu thêm |
| BA2-07 | Kho thiếu, lô hết hạn, serial đã bán | Không xuất âm/không xuất lô hết hạn/không bán lại serial; phần thiếu nằm đơn chờ |
| BA2-08 | Hai người bán chiếc cuối | Chỉ một lần xuất thành công; người còn lại nhận lỗi và tra lại tồn |
| BA2-09 | COD 500.000, phí 25.000 | Nợ giảm 500.000; thực nhận 475.000; phí 25.000; không đối soát lặp |
| BA2-10 | Trả phiếu 300.000 đã thu 200.000, trả hết ở chi nhánh khác | Nợ giảm 100.000; được hoàn tối đa 200.000; tăng tồn kho nhận; nguồn truy được |
| BA2-11 | Trả sai serial hoặc quá lượng nguồn | Chặn dù có quyền duyệt; không thay tồn/tiền |
| BA2-12 | Chuyển 10, nhận 6, còn 4; nhận hỏng 1 | Nguồn giảm 10; đích tăng 6; còn chuyển 4; phần hỏng không khả dụng; vốn đối chiếu |
| BA2-13 | Kiểm kê 10 đếm 8 | Chặn theo phạm vi đã chốt; trước duyệt tồn 10; sau duyệt 8; mở khóa |
| BA2-14 | Tự duyệt hoặc truy kho ngoài quyền | Bị chặn API/list/detail/export; không tiết lộ số liệu khác phạm vi |
| BA2-15 | Import lỗi/trùng và batch đầu kỳ | Lỗi đúng dòng; không ghi một phần; commit đầu kỳ tạo nháp, chỉ duyệt mới ghi số dư |
| BA2-16 | CTV theo đơn 300.000, bổ sung 200.000 | Tổng phải trả 500.000; chi không vượt; trả hàng dùng điều chỉnh có lý do |
| BA2-17 | Hủy phần đơn đã có cọc/đã xuất một phần | Duyệt đóng phần chưa xuất; hoàn cọc dư; không đảo/xóa phần xuất cũ |
| BA2-18 | Đối chiếu cuối ngày và in/xuất | Tồn theo movement, nợ theo phiếu/credit/allocation; bán/thu không tính trùng; quyền vốn đúng |

Các số trên là dữ liệu minh họa nghiệm thu, không được nạp tự động vào cửa hàng đang dùng. Cần UAT người dùng theo vai trò và loại hàng thường/lô/serial trước đánh dấu hoàn tất. Bản BA này được đối chiếu mã nguồn, chưa phải kết quả chạy UAT mới.

## 21. Quyết định còn cần chốt

| Q | Nội dung | Đề xuất mặc định và tác động |
|---|---|---|
| Q-01 — Đã chốt | Mô hình sản phẩm có biến thể | Chọn thuộc tính/giá trị cấu hình sẵn, tổ hợp tạo SKU riêng; giá/tồn/giao dịch theo SKU, hồ sơ cha chỉ gom nhóm |
| Q-02 — Đã chốt | Kiểm kê dừng toàn kho hay chỉ SKU? | Người dùng xác nhận chỉ khóa sản phẩm đang đếm tại kho; các sản phẩm/kho khác hoạt động |
| Q-03 | Hàng đã sử dụng có trả được? | Đề xuất công tắc theo sản phẩm và duyệt tình trạng; yêu cầu trước cho phép nhưng hiện chưa có, cần khớp lại |
| Q-04 | Hộ một người có vẫn duyệt hai người? | Giữ quy tắc đã chốt khác người; cần tối thiểu hai tài khoản; không tự bỏ duyệt |
| Q-05 | Ai chịu phí mua/giao và phí trả? | Mỗi phiếu chọn cửa hàng/khách/NCC; phí dịch vụ đã thực hiện đề xuất không tự hoàn; cần chốt xử lý nghĩa vụ trả |
| Q-06 | Ngày bắt đầu và ngày đến hạn? | Đề xuất chọn ngày bắt đầu khi setup; nợ mặc định 30 ngày nhưng được đổi, không chặn bán |

Các quyết định này chưa phải phê duyệt triển khai thay đổi ứng dụng. BA vẫn đủ phần còn lại để phát triển; nhiệm vụ phụ thuộc Q phải cập nhật sau câu trả lời.

## 22. Căn cứ đối chiếu và quản lý phiên bản

Đối chiếu ngày 05/10/2026 với menu `RetailModule`, các service `RetailCatalog`, `RetailDocuments`, `RetailInventory`, `RetailFinance`, `RetailOperations`, `RetailImport`, `RetailCommissions`; giao diện `retail/workspace`, `warehouse-config`, `uom-conversions`, `overview-dashboard` và các màn Core; tài liệu MODULE_BOUNDARIES, PRODUCT_UOM_MODEL, IMPLEMENTATION_STATUS và SRS 1.0. Các lớp nguồn là căn cứ kỹ thuật nội bộ, không phải tài liệu cần người bán đọc.

Không commit/push/deploy khi dựng BA. Không sửa/xóa dữ liệu vận hành. Sau khi chốt Q, tăng phiên bản BA, cập nhật SRS theo cùng quy tắc, giao GAP và chạy kịch bản BA2 trước phát hành.

## 23. Định hướng bản chuẩn và SaaS

Bản chuẩn chạy một công ty và nhiều điểm bán trước. Cửa hàng thêm điểm bán không tạo khách SaaS mới. Khi nâng SaaS, mỗi doanh nghiệp là không gian dữ liệu độc lập, có hồ sơ/quyền/danh mục/chính sách riêng; các luồng mua–bán–kho giữ nguyên. Việc đăng ký khách SaaS, gói thuê bao, thanh toán thuê bao, hạn sử dụng và quản trị nhà cung cấp SaaS là gói riêng, chưa phải chức năng hiện tại.

Khi khách nhận mã nguồn và chạy riêng, gói bàn giao phải có toàn bộ source, hướng dẫn build/chạy, migration, cấu hình mẫu không chứa bí mật, dữ liệu/file của đúng khách và biên bản đối chiếu. Bản local hiện tại không chứng minh đã có công cụ tự tách nhiều khách. Trước production cần kiểm cô lập dữ liệu, sao lưu/khôi phục và chạy từ source sạch; không dùng việc có cột tenant làm bằng chứng SaaS hoàn chỉnh.

## 24. Bảng tra cứu module → luồng → dữ liệu

| Module | Luồng BA | Nhóm trường trong phụ lục |
|---|---|---|
| Đăng nhập/thiết lập | F-01–04 | D-01–03, D-17 |
| Sản phẩm | F-05–07 | D-04/05/07 |
| Mua/NCC | F-08–11 | D-06/08–10 |
| Bán | F-12–15 | D-08–10/12 |
| Kho | F-16–20 | D-03/08/09/11 |
| CRM | F-21 | D-06 |
| Kế toán | F-22–25 | D-13/14/15 |
| Trả/bảo hành | F-26/27 | D-09/10/15 |
| Duyệt | F-28 | D-16 |
| Tổng quan/Báo cáo | F-29/30/32 | D-18 |
| Nhập ban đầu | F-31 | D-17 |
| Quản trị/Hồ sơ | Mục 17 | D-19 |
