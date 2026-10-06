# Phụ lục BA 2.0 — trường dữ liệu và màn hình

Ngày 05/10/2026. Đi cùng [BA hệ thống v2.0](./BA_HE_THONG_v2.0.md). Bảng mô tả **thiết kế nghiệp vụ chuẩn**, không khẳng định mọi trường đã xuất hiện trên giao diện. `Có` = nguồn hiện tại có trường/quy tắc; `Bổ sung` = cần phát triển. Trường kỹ thuật nêu để nhóm thực hiện đối chiếu, không yêu cầu người bán nhập.

Quy ước: **B** bắt buộc; **ĐK** bắt buộc khi có điều kiện; **K** tùy chọn; **T** hệ thống tự sinh/tính, chỉ đọc. Tên Việt là nhãn cần dùng trên màn hình. Trường Có trong API nhưng chưa có UI được ghi riêng. Giới hạn không nêu số cụ thể phải được chốt trong SRS; không tự suy từ độ dài cột database.

## D-01. Trường chung và danh sách

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| ID | T; định danh nội bộ; không bắt người dùng nhập để tìm nguồn | Có |
| Công ty/không gian dữ liệu | T theo phiên đăng nhập; không cho chọn công ty khác trong form | Có |
| Chi nhánh | ĐK theo nghiệp vụ; phiếu kho tự lấy từ kho thực hiện | Có |
| Kho thực hiện | B với chứng từ hàng và cả phiếu đầu kỳ/hoàn hiện tại; trong phạm vi/đang hoạt động | Có; phiếu tiền độc lập chỉ cần chi nhánh |
| Ngày tạo, người tạo | T, thời điểm thật | Có |
| Ngày nghiệp vụ | B, mặc định hôm nay; chỉ nhập trong phạm vi ngày cho phép | Bổ sung; hiện phát sinh chủ yếu dùng thời điểm ghi |
| Trạng thái | T, tiếng Việt theo BA mục 14 | Có |
| Phiên bản | T; kiểm thay đổi đồng thời, không cho sửa | Có với phiếu/giao/bảo hành |
| Người/thời điểm duyệt | T sau duyệt; người khác người lập | Người duyệt có; hiển thị đầy đủ lịch sử cần chuẩn hóa |
| Ngày ghi sổ | T khi đã ghi | Có |
| Ghi chú | K; không thay trường bắt buộc hoặc lý do ngoại lệ | Có |
| Lý do | ĐK gửi duyệt/từ chối/hủy/đóng/ngoại lệ; tối đa 1.000 ký tự ở các lệnh kiểm hiện tại | Có; duyệt hủy riêng cần bổ sung |
| Tham chiếu nguồn | ĐK; chọn số phiếu/dòng, không nhập ID kỹ thuật | Có cấu trúc; chọn nguồn bảo hành còn thiếu UX |
| STT | T theo vị trí trang/danh sách; không lưu mã nghiệp vụ | Có ở bảng nghiệp vụ dùng thành phần chung |
| Tìm kiếm/bộ lọc | Mã/tên/số phiếu, trạng thái, loại, thời gian, chi nhánh/kho/đối tác phù hợp | Có từng phần; chưa đầy đủ mọi màn |

Các bảng chuẩn có STT và hành động xem chi tiết; cột vốn chỉ hiện khi có quyền. Bộ lọc và tổng số phải ứng với cùng phạm vi, không đếm toàn công ty rồi chỉ hiển thị một kho.

## D-02. Đăng nhập, hồ sơ và hướng dẫn đầu tiên

| Trường | Loại / bắt buộc / kiểm tra | Hiện trạng |
|---|---|---|
| Email đăng nhập | Email, B; định dạng hợp lệ | Có |
| Mật khẩu | Bí mật, B; tối thiểu 8 ký tự theo chính sách local | Có |
| Ghi nhớ phiên | Boolean, K; mặc định không | Có |
| Họ tên hiển thị | Chữ, T từ tài khoản | Có |
| Vai trò/phạm vi | T; tải theo tài khoản | Có |
| Mật khẩu hiện tại/mới/xác nhận | B khi tự đổi; mới ≥8, xác nhận khớp | Có; không ép thay lần đầu local |
| Tên cửa hàng/công ty | Chữ, B khi setup | Bổ sung hồ sơ nghiệp vụ |
| Điện thoại cửa hàng | Chữ, K, không lưu kiểu số mất số 0 đầu | Bổ sung |
| Địa chỉ cửa hàng | Chữ nhiều dòng, K | Bổ sung |
| Logo | Tệp ảnh, K; định dạng/kích thước chốt SRS | Bổ sung liên kết hồ sơ |
| Tiền tệ/múi giờ | T; VND / Asia/Ho_Chi_Minh giai đoạn đầu | Quy tắc có; UI hồ sơ bổ sung |
| Ngày bắt đầu số dư | Ngày, B khi nhập đầu kỳ | Bổ sung |
| Kho làm việc mặc định | Danh sách, B trước giao dịch; kho được cấp | Có chọn kho; lưu mặc định theo người bổ sung |
| Bước thiết lập/đã hoàn thành | T; lưu tiến độ và người xác nhận từng bước | Bổ sung wizard |
| Xác nhận không có số dư | Boolean, ĐK khi bỏ nhập tồn/nợ | Bổ sung |

## D-03. Chi nhánh, kho và phạm vi người dùng

| Đối tượng / trường | Quy tắc | Hiện trạng |
|---|---|---|
| Chi nhánh — mã | Chữ, B, tối đa 60, duy nhất trong công ty | Có |
| Chi nhánh — tên | Chữ, B, tối đa 160 | Có |
| Chi nhánh — hoạt động | Boolean, T/K quản trị; không xóa lịch sử | Có tạo/ngừng; bật lại cần chuẩn hóa |
| Chi nhánh — địa chỉ/điện thoại/người phụ trách | K; thông tin điểm bán/in phiếu | Bổ sung |
| Kho — mã/tên | B; mã 60, tên 160, duy nhất theo quy tắc tenant | Có |
| Kho — chi nhánh | B; chọn chi nhánh hoạt động | Có |
| Kho — địa chỉ/loại/người phụ trách | K; mặc định kho bán hàng, loại cách ly chỉ nếu thiết kế riêng | Bổ sung |
| Kho — hoạt động | Boolean; ngừng không làm mất tồn/lịch sử | Có; cần kiểm điều kiện ngừng |
| Phạm vi — tài khoản | B; tài khoản thuộc công ty | Có |
| Phạm vi — kho được giao | B; được cấp một hoặc nhiều kho | Có thêm từng kho; thu hồi/sửa bổ sung |
| Phạm vi — toàn chi nhánh | Quyền riêng; không mặc định cấp nhân viên | Có |
| Phạm vi — đơn vị tổ chức liên kết | K; không tự đồng nhất với chi nhánh/kho | Bổ sung liên kết nếu cần |

Danh sách chi nhánh: STT, mã, tên, trạng thái, thao tác. Kho: STT, mã, tên, chi nhánh, trạng thái. Phạm vi: nhân viên, vai trò, chi nhánh/kho được giao, hành động cấp/thu hồi.

## D-04. Sản phẩm/SKU

| Trường | Loại / bắt buộc / mặc định / kiểm tra | Hiện trạng |
|---|---|---|
| Mã SKU | Chữ, B, ≤60, duy nhất trong công ty | Có |
| Tên hàng | Chữ, B, ≤160 | Có |
| Barcode | Chữ, K, ≤100; nếu có không trùng | Có |
| Có biến thể | Boolean, B, mặc định không; nếu có phải chọn thuộc tính/giá trị cấu hình sẵn | Bổ sung theo quyết định đã chốt |
| Màu/size/biến thể | ĐK có biến thể: danh sách chọn, không nhập tự do; mô tả hiển thị tự ghép từ tổ hợp | Hiện chỉ có chữ mô tả; cần bộ chọn cấu trúc |
| Đơn vị cơ sở | B, tên ≤40; chọn/chuẩn hóa về UOM | Có dữ liệu UOM; form hiện nhập tên |
| Giá bán mặc định | Số, B, ≥0, tối đa 4 chữ số lẻ | Có |
| VAT nội bộ | Số, K, mặc định 0, từ 0 đến 100% | Có |
| Kiểu theo dõi | B; thông thường / lô–hạn / serial; mặc định cấu hình kho | Có; chưa hỗ trợ đồng thời lô và serial cho một SKU |
| Số ngày đổi trả | Số nguyên ≥0, B theo BA; 0 = không áp dụng | Có; cần bắt nhập/hiển thị rõ mặc định |
| Số ngày bảo hành | Số nguyên ≥0, B theo BA; 0 = không áp dụng | Có |
| Cho trả khi mở bao bì | Boolean, K, mặc định không | Có |
| Cho trả khi đã sử dụng | Boolean, K; điều kiện và duyệt theo Q-03 | Bổ sung/chờ chốt; hiện USED bị chặn trả |
| Trọng lượng cơ sở (kg) | Số ≥0, K; ĐK nếu phân bổ phí theo trọng lượng | Có |
| Đang sử dụng | Boolean, T khi tạo = có; ngừng theo quyền | Có |
| Nhóm sản phẩm | Danh mục, K hoặc B nếu chủ chọn chính sách; liên kết ID | Bổ sung gán vào SKU |
| Thương hiệu | Danh mục, K | Bổ sung gán vào SKU |
| Thuộc tính và giá trị | ĐK có biến thể, chọn giá trị đang hoạt động; vd màu đỏ, size M | Bổ sung liên kết cấu trúc; đã chốt |
| Sản phẩm cha | ĐK SKU biến thể; không giữ tồn ở cha | Bổ sung; đã chốt Q-01 |
| Tổ hợp biến thể | ĐK, duy nhất trong sản phẩm cha; chỉ tạo tổ hợp cửa hàng chọn kinh doanh | Bổ sung |
| Hình ảnh/mô tả | K, không cần để bán nhanh | Bổ sung |

Danh sách chuẩn: STT, mã, tên, biến thể, nhóm, thương hiệu, đơn vị cơ sở, giá, tracking, trạng thái; cột chưa liên kết phải phát triển trước sử dụng. Form không có trường “tồn hiện tại” để nhập trực tiếp; mở tồn kho qua liên kết.

## D-05. Danh mục sản phẩm và quy đổi

| Đối tượng / trường | Quy tắc | Hiện trạng |
|---|---|---|
| Nhóm/Thương hiệu/Thuộc tính — mã | B, ≤60, không trùng trong loại danh mục | Có qua cấu hình danh mục |
| Nhóm/Thương hiệu/Thuộc tính — tên | B, ≤160 | Có |
| Giá trị/mô tả cấu hình | K, chữ ≤1.000; không coi là giá trị thuộc tính SKU đã chuẩn hóa | Có |
| Đang sử dụng | Boolean, mặc định có; có thể cập nhật cùng mã | Có |
| Nhóm cha | K; tránh vòng lặp | Bổ sung nếu cần phân cấp, R1 có thể nhóm phẳng |
| Thuộc tính — dùng tạo biến thể | Boolean; thuộc tính tạo biến thể dùng tập giá trị chọn sẵn | Bổ sung |
| Giá trị thuộc tính — mã/tên/thuộc tính cha | B; mã không trùng trong thuộc tính, tên hiển thị rõ | Bổ sung bảng giá trị cấu trúc |
| Giá trị thuộc tính — hoạt động/thứ tự | Boolean/số, K; ngừng không xóa tham chiếu đã dùng | Bổ sung |
| Đơn vị — tên | B, ≤40, duy nhất trong công ty | Có `uom` |
| Đơn vị — mã/hoạt động | Mã ở danh mục cấu hình, không mặc nhiên là mã trong bảng UOM | Có cấu hình; chuẩn hóa đồng bộ khi đổi tên cần bổ sung |
| Quy đổi — sản phẩm/SKU | B; chọn SKU đang dùng | Có |
| Quy đổi — đơn vị phụ | B; khác đơn vị cơ sở, UOM tồn tại | Có |
| Quy đổi — hệ số | B, >0, tối đa 4 chữ số lẻ; một cặp SKU/UOM một hệ số | Có |
| Quy đổi — cách diễn giải | T: 1 đơn vị phụ = hệ số × đơn vị cơ sở | Có thể hiển thị; không nhập công thức tùy ý |
| Hệ số đơn vị cơ sở | T = 1; không tạo dòng quy đổi tự thân | Có |

Chỉnh hệ số là cập nhật cho tương lai; hệ số trên phiếu cũ giữ nguyên. Không có chức năng xóa quy đổi đã dùng hoàn chỉnh; bản chuẩn cần ngừng sử dụng thay xóa lịch sử.

## D-06. Customer Master — Khách hàng / Đối tác và chăm sóc

Cập nhật 06/10/2026 theo BA F-21. Đây là hồ sơ gốc dùng chung Bán hàng, Mua hàng và CRM trong một tenant. Nhà cung cấp là góc nhìn theo vai trò, không có bảng hồ sơ riêng. Các thông tin theo vai trò chỉ xuất hiện khi vai trò được chọn; nhóm trường tùy chọn có thể thu gọn khi bắt đầu sử dụng.

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Mã đối tác | B, ≤60, duy nhất trong công ty | Có |
| Tên đối tác | B, ≤160 | Có |
| ID đối tác / tenant | T, bất biến; các module cùng tham chiếu một ID | Có định danh chung |
| Loại đối tượng | B: Cá nhân / Doanh nghiệp; mặc định Cá nhân | Bổ sung; khác vai trò kinh doanh |
| Vai trò | B, chọn một hoặc nhiều: Khách hàng / Nhà cung cấp / CTV | Có trên local V30; kind cũ giữ tương thích |
| Trạng thái từng vai trò | B, mặc định hoạt động; ngừng riêng không ngừng vai trò khác | Bổ sung |
| Điện thoại | K, chữ ≤60 | Có |
| Email | K, ≤160; cần thống nhất kiểm định dạng khi nhập | Có trường |
| Địa chỉ | K, chữ | Có |
| Địa chỉ giao/nhận mặc định | K; mặc định dùng địa chỉ chung; giao dịch được sửa riêng | Bổ sung cấu trúc; không ghi địa chỉ giao riêng ngược vào master |
| Nhóm khách | K, chữ ≤80; dùng chọn giá | Có; chưa có danh mục riêng |
| Trạng thái hoạt động | T/K quản trị | Có tạo/ngừng |
| Mã số thuế | K, với doanh nghiệp/NCC; chuẩn hóa trước so trùng trong tenant | Bổ sung; nếu đã có chọn hồ sơ cũ |
| Người liên hệ/chức danh/điện thoại liên hệ | K; một liên hệ chính giai đoạn đầu | Bổ sung |
| Điều khoản bán / số ngày thanh toán bán | K, dành vai trò Khách hàng; bỏ trống nghĩa chưa thỏa thuận, không tự chặn bán | Bổ sung form; bỏ mặc định 30 ngày chưa được chốt |
| Điều khoản mua / số ngày thanh toán mua | K, dành vai trò Nhà cung cấp; độc lập điều khoản bán | Bổ sung |
| Ngân hàng/số tài khoản/tên chủ tài khoản | K; chỉ hiển thị/sửa theo quyền thông tin thanh toán | Bổ sung; không bắt buộc khi tạo master |
| Người phụ trách chăm sóc | K, tài khoản đang hoạt động trong công ty | Bổ sung |
| Phải thu / phải trả / cọc khách / ứng trước NCC | T từ sổ tương ứng, hiển thị riêng theo quyền và phạm vi | Có qua finance từng phần; không nhập hoặc tự bù trừ tại master |
| Phiên bản, người/ngày tạo/sửa, nhật ký đổi vai trò | T; kiểm sửa đồng thời và audit | Bổ sung chuẩn hóa master |
| Ghi chú chăm sóc — đối tác | B, cùng ID master; chọn vai trò/ngữ cảnh chăm sóc phù hợp | Có partner chung; cần chuẩn hóa quyền/ngữ cảnh |
| Ghi chú — chi nhánh | B, trong phạm vi | Có |
| Ghi chú — nội dung | B, tối đa 4.000 | Có |
| Lịch nhắc | K, ngày giờ hợp lệ; lưu thời điểm, hiển thị giờ Việt Nam | Có |
| Hoàn thành nhắc | Boolean, T sau thao tác | Có |
| Người phụ trách/kết quả/thời điểm hoàn thành | ĐK khi dùng nhắc việc chuẩn | Bổ sung |

Khách: STT, mã, tên, loại, điện thoại, nhóm, trạng thái; mở lịch sử/ghi chú/nhắc. NCC: STT, mã, tên, liên hệ, địa chỉ, trạng thái; liên kết mua/nợ. Lịch sử: số, loại, ngày, trạng thái, tổng, liên kết phiếu; ghi rõ phạm vi.

## D-07. Bảng giá, khuyến mãi và chính sách

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Bảng giá — SKU | B | Có |
| Bảng giá — giá | B, ≥0, tối đa 4 chữ số lẻ; làm rõ giá theo đơn vị cơ sở | Có; trả giá giao dịch theo hệ số |
| Khách cụ thể | K; ưu tiên cao nhất nếu khớp | Có |
| Nhóm khách | K; khớp chuỗi nhóm trên đối tác | Có |
| Chi nhánh | K; trống là chung | Có |
| Kênh | K: quầy/online/sỉ/CTV | API có kênh; form bảng giá hiện thiếu lựa chọn CTV |
| Từ số lượng cơ sở | B, >0; không vượt lượng đang bán khi chọn rule | Có |
| Độ ưu tiên | Số, K; mặc định do service; chuẩn cần hòa ổn định | Có |
| Từ ngày/đến ngày | K, ngày đúng; đến không trước từ | Có |
| Khuyến mãi — SKU/tên | B, tên ≤160 | Có |
| Khuyến mãi — giảm % | B, >0 và ≤100 | Có |
| Khuyến mãi — cộng dồn | Boolean, K mặc định không | Có |
| Khuyến mãi — ưu tiên/thời gian | Theo quy tắc trên | Có |
| Mức giảm thêm tự quyết | K, 0–100%, cấu hình công ty | Có |
| Giá đã áp và căn cứ | T; lưu snapshot giá/rule/chương trình trên dòng | Có |

Không có bảng giá theo nhiều tiền tệ hoặc mọi loại khuyến mãi quà tặng/combo; thuộc yêu cầu mở rộng. Không cho nhân viên đổi bảng giá để né duyệt giảm.

## D-08. Đầu chứng từ dùng chung

Áp dụng báo giá, đơn mua/bán, nhập/xuất/bán, trả, chuyển, kiểm kê, đầu kỳ và hoàn; trường hiện theo điều kiện, không dựng một form bắt nhập tất cả.

| Trường | Quy tắc và điều kiện | Hiện trạng |
|---|---|---|
| Loại phiếu | B, theo màn hình; không đổi sau tạo | Có |
| Số phiếu | T, duy nhất, không dùng lại | Có; cơ chế sinh cần nâng cấp |
| Kho | B trong API chứng từ, đang hoạt động/đúng phạm vi | Có |
| Chi nhánh | T từ kho | Có |
| Đối tác | B đơn bán, nhập/đơn/trả mua, đầu kỳ nợ; ĐK bán còn nợ; K bán trả đủ/đề nghị mua | Có |
| Phiếu nguồn | B xuất bán/trả bán/trả mua/hoàn; K đơn từ báo giá/nhận từ đơn/đơn từ đề nghị | Có |
| Kho đích | B chuyển kho, khác nguồn | Có |
| Kênh bán | Quầy/online/sỉ/CTV, mặc định quầy | Có |
| Hiệu lực báo giá | Ngày, K, mặc định hôm nay +7 | Có dữ liệu; chưa chặn/nhắc chuyển báo giá hết hiệu lực đầy đủ |
| Địa chỉ giao | ĐK đơn cần giao, K quầy | Có trên báo giá/đơn; snapshot giao cần chuẩn hóa |
| COD dự kiến | K, ≥0; thông tin kế hoạch, không thu tiền ở bước đơn | Có metadata |
| Phí giao khách trả | K, ≥0, chỉ bán/báo giá/đơn | Có `fee`; cần phân biệt phí khách và phí hãng |
| Phí mua vốn hóa | K, ≥0 ở nhập | Có `fee`; tách bên nhận/nghĩa vụ cần bổ sung |
| Phân bổ phí mua | Giá trị / số lượng cơ sở / trọng lượng, mặc định giá trị | Có |
| Giá vốn tạm thời | Boolean, K; chỉ nhãn chưa đủ phí | Có; không tự điều chỉnh muộn |
| Tiền trả ngay | K, ≥0, không vượt tổng; ở POS/nhập mua | Có |
| Phương thức | Tiền mặt/chuyển khoản/thẻ/khác; mặc định tiền mặt | Có |
| Giá đã gồm VAT | Boolean, K, mặc định không; tại bán/báo giá/đơn mới | Có |
| Tổng hàng sau giảm/thuế/tổng | T theo dòng và phí áp dụng | Có |
| Còn phải thu/trả | T theo tiền/phân bổ; không chỉ tổng trừ paid tĩnh sau nhiều lần thu | Có sổ; hiển thị cần chuẩn hóa |
| Số tiền | B >0 cho hoàn/đầu kỳ nợ, không có dòng hàng | Có |
| Ghi chú/lý do | Theo D-01 | Có |
| Số phiếu NCC/ngày chứng từ NCC | K, giúp chống nhập trùng/đối chiếu | Bổ sung |
| CTV giới thiệu | K, tách người mua khỏi CTV nhận hoa hồng | Bổ sung liên kết trên đơn; hiện commission chọn CTV độc lập |
| Phiếu trả của giao dịch đổi | K, chỉ POS/đơn bán, đã ghi/cùng khách | Có liên kết; bù tiền chưa đầy đủ |
| Tệp tham chiếu | K ảnh chứng từ/biên bản | Bổ sung UI/liên kết |

**Lưu ý tổng nhập mua hiện tại:** mã tạo phiếu chỉ cộng `fee` vào tổng các phiếu bán, trong khi phí mua vẫn vốn hóa vào tồn. Vì vậy không mô tả tổng AP đã bao gồm mọi phí mua. Bản chuẩn phải tách tiền hàng NCC và chi phí mua theo đúng bên được trả; kiểm toán luồng này trước nghiệm thu.

## D-09. Dòng hàng dùng chung

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Sản phẩm/SKU | B, còn sử dụng lúc tạo; nguồn trả/xuất chọn từ phiếu nguồn | Có |
| Dòng nguồn | ĐK nếu phát sinh từ nguồn; SKU phải khớp | Có |
| Tên/barcode/biến thể/chính sách lúc lập | T snapshot, không sửa theo danh mục mới | Có |
| Đơn vị giao dịch | B, cơ sở hoặc quy đổi hợp lệ | Có |
| Hệ số | T tại thời điểm lập; giữ từ nguồn khi theo phiếu | Có |
| Số lượng | B, >0, tối đa 4 chữ số lẻ; kiểm kê dùng số đếm riêng | Có |
| Số lượng cơ sở | T = lượng × hệ số, tối đa 4 chữ số lẻ | Có |
| Đơn giá | B, ≥0, tối đa 4 chữ số lẻ, theo đơn vị dòng | Có |
| Giảm dòng (VND) | K, ≥0, không vượt tiền hàng; số tiền, không nhầm tỷ lệ | Có |
| VAT (%) | K, 0–100 | Có |
| Tiền sau giảm/thuế/thành tiền | T, làm tròn đồng theo BA | Có |
| Mã lô | ĐK nhập/trả hàng LOT; xuất để trống chọn FEFO | Có, ≤100 theo xử lý lô |
| Hạn sử dụng | ĐK hàng LOT; cùng lô/kho/SKU không khác hạn | Có |
| Serial | ĐK SERIAL; riêng biệt, ≤100/serial, số serial bằng lượng cơ sở nguyên | Có; UI ngăn bằng `|` |
| Tình trạng trả | ĐK SALE_RETURN: tốt/mở bao bì/chờ kiểm; USED theo Q-03 | Có tốt/mở/cách ly; USED/FAULTY chuyển bảo hành |
| Phân đoạn lô thực xuất | T; FEFO có thể xuất nhiều lô trên một dòng | Có |
| Vốn dòng | T theo sổ vốn/nguồn; ẩn khi không có quyền | Có |
| Đã nhận/đã xuất/đóng phần còn | T trên dòng đơn, theo đơn vị cơ sở | Có `progress/closed_qty`; UX cần rõ |

Phiếu hàng có 1–200 dòng hiện tại; hoàn/đầu kỳ nợ không có dòng. Không tạo nhiều dòng cùng SKU trên phiếu kiểm kê. Trả một phần theo giá/thuế đã bán, không áp khuyến mãi hiện tại. Số lượng cùng nguồn phải kiểm tổng tất cả dòng và lần ghi trước.

## D-10. Trường riêng từng loại giao dịch và cột danh sách

| Chức năng | Trường riêng/điều kiện | Danh sách chuẩn |
|---|---|---|
| Báo giá | Khách, hiệu lực, kênh, dòng hàng/giá, phí; không tạo nợ | STT, số, ngày, khách, hiệu lực, tổng, trạng thái |
| Đơn bán | Khách B, địa chỉ ĐK, cọc xem riêng, dòng chờ xuất/đặt trước | STT, số, ngày, khách, kênh, tổng, tiến độ xuất/giao/tiền |
| POS | Kho B, khách ĐK còn nợ, thu ngay/phương thức | STT, số, ngày giờ, kho, khách, tổng, đã thu/còn nợ, trạng thái |
| Xuất bán | Đơn và dòng nguồn B, kho thực xuất, lượng còn xuất | STT, số, đơn nguồn, kho, khách, tổng, trạng thái |
| Đề nghị mua | Kho, dòng hàng, lượng, ghi chú nhu cầu; NCC K | STT, số, ngày, kho, người đề nghị, trạng thái |
| Đơn mua | NCC B, nguồn đề nghị K, lượng/giá đặt | STT, số, NCC, tổng, đã nhận/còn nhận, trạng thái |
| Nhập mua | NCC B, kho/lô/serial, phí, đã trả; đơn nguồn K | STT, số, NCC, nguồn, kho, tổng hàng, phí riêng, nợ, trạng thái |
| Trả mua | Phiếu nhập/dòng nguồn B, lượng còn trả, lý do | STT, số, nguồn, NCC, kho, tổng trả, giảm nợ/còn hoàn |
| Trả bán | Phiếu bán B, kho nhận, điều kiện, serial/lô, quá hạn nếu có | STT, số, nguồn/chi nhánh bán, kho nhận, khách, tổng, trạng thái |
| Nhập/xuất khác | Mục đích/lý do B; đối tác không bắt buộc | STT, số, ngày, kho, loại, lý do, trạng thái |
| Hoàn khách | Nguồn trả/đơn đã đóng B, tiền được hoàn, phương thức, lý do | STT, số, nguồn, khách, tiền, phương thức, trạng thái |

Cột trạng thái tiền/giao tách riêng là mục tiêu bổ sung, không gộp một nhãn “hoàn thành” cho mọi thứ. Hiện nhiều loại dùng bảng chứng từ chung: số/type/đối tác/kho/tổng/trạng thái; cần mở rộng theo mục đích màn.

## D-11. Tồn, chuyển kho, kiểm kê và cấu hình kho

| Chức năng / trường | Quy tắc | Hiện trạng |
|---|---|---|
| Tồn — kho/SKU/đơn vị | T từ balance/danh mục | Có |
| Tồn — lượng tổng/cách ly/khả dụng | T; khả dụng = tổng − cách ly | Có |
| Tồn — giá trị/vốn bình quân | T, chỉ quyền COST | Giá trị có; vốn tính theo qty/value |
| Tồn — lô/hạn/serial/vị trí | T từ tracking; không sửa trên bảng tồn | Có |
| Tồn — mức tối thiểu/ngày cảnh báo hạn | K cấu hình theo SKU/kho | Bổ sung |
| Lịch sử — ngày/số phiếu/loại/kho/SKU/lượng/giá trị | T; tăng dương, giảm âm; truy phiếu | Có; giá trị theo quyền |
| Chuyển — kho nguồn/đích | B, khác nhau; đúng quyền thao tác hai đầu | Có dữ liệu; quyền nhận hai đầu cần hoàn thiện |
| Chuyển — lượng gửi/tracking/vốn | B/T theo dòng và thực xuất | Có |
| Nhận chuyển — dòng phiếu | B thuộc phiếu chuyển | Có |
| Nhận chuyển — lượng thực nhận | B >0, không vượt đang chuyển; đơn vị dòng | Có |
| Nhận chuyển — serial thực nhận | ĐK SERIAL; đúng serial đang chuyển | Có |
| Nhận chuyển — hỏng/lý do | ĐK hỏng thì cách ly/lý do B | Có; UI chọn tình trạng chung |
| Chuyển — lượng thiếu/mất và biên bản chốt | ĐK chốt thiếu; cần duyệt và movement phù hợp | Bổ sung |
| Kiểm kê — kho/phạm vi SKU | B; đã chốt chỉ khóa SKU đang đếm tại kho, đến khi ghi/hủy | Có, khớp quyết định |
| Kiểm kê — tồn chụp lúc bắt đầu | T | Có |
| Kiểm kê — số đếm | B, ≥0, đơn vị cơ sở, tối đa 4 chữ số lẻ | Có |
| Kiểm kê — lô đếm/hạn/lượng từng lô | ĐK LOT; tổng bằng đếm, lô mất ghi 0 | Có |
| Kiểm kê — serial đếm | ĐK SERIAL; số serial bằng lượng nguyên | Có |
| Kiểm kê — chênh lệch/nguyên nhân | T/B; chỉ ghi khi duyệt | Có |
| Kiểm kê — vốn khi tồn bằng 0 | ĐK có dư mà chưa có bình quân; giá nhập tham chiếu | Có |
| Cấu hình — mã/tên/giá trị/hoạt động | INVENTORY; mã ≤60, tên ≤160, giá trị ≤1.000 | Có |
| Cấu hình — theo dõi mặc định | Thường/lô/serial; không đổi SKU đã tạo | Có |
| Cấu hình — không âm/FEFO/chặn hết hạn | T quy tắc chuẩn; không dựng công tắc tùy tiện | Có quy tắc, không có form công tắc |

Kho & Tồn: STT, kho, mã/tên/biến thể, đơn vị, tổng, cách ly, khả dụng, giá trị theo quyền, xem tracking/lịch sử. Chuyển: STT, số, nguồn, đích, ngày, gửi/nhận/còn chuyển, trạng thái. Kiểm kê: số, kho, phạm vi, ngày, người đếm, chênh lệch, trạng thái; chi tiết theo SKU/lô/serial.

## D-12. Giao hàng và đối soát COD

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Phiếu xuất/bán liên quan | B, POS/ISSUE đã ghi | Có |
| Hãng vận chuyển | Chữ, K; ĐK có gửi ngoài theo BA | Có nhập tay |
| Mã vận đơn | Chữ, K; ĐK có vận đơn; kiểm trùng hãng/mã cần bổ sung | Có |
| COD dự kiến | ≥0, không vượt nợ nguồn tại tạo | Có; tổng nhiều đợt cần bổ sung |
| Phí dự kiến | ≥0; hiện khi COD>0 không vượt COD | Có; cần mở rộng trường hợp phí vượt COD/không COD |
| Địa chỉ/người nhận/điện thoại nhận | ĐK giao ngoài; snapshot theo đợt | Địa chỉ có trên đơn; đợt chưa đủ trường riêng |
| Trạng thái giao | T/K thao tác theo sơ đồ BA | Có |
| Dòng/lượng giao | ĐK nhiều lần/một phần, không vượt xuất | Bổ sung; hiện trạng thái không có lượng giao chi tiết |
| Ghi chú giao/thất bại | ĐK có lỗi/lý do; không tự hủy đơn | Có note; yêu cầu lý do cần chuẩn hóa |
| COD gộp đối soát | B >0, không vượt COD/nợ còn lại | Có |
| Phí thực tế lần đối soát | ≥0, không vượt COD gộp lần đó | Có |
| Thực nhận | T = COD gộp − phí | Có trong cách ghi; cần cột rõ |
| Phương thức nhận | B, mặc định chuyển khoản ở service | Có; form hiện nhập chữ |
| Ngày/bảng kê/tham chiếu nhận | B/K theo đối soát, chống lặp | Thời điểm hệ thống có; tham chiếu bảng kê bổ sung |
| COD đã đối soát/phí thực tế cộng dồn | T | Có |

Danh sách: STT, phiếu xuất/đơn, khách, hãng, vận đơn, trạng thái, COD, đã đối soát, còn COD, phí dự kiến/thực tế; mở lịch sử các lần đối soát. Tạo đợt không ghi thu; đổi trạng thái không tự nhập trả hàng.

## D-13. Thu chi, công nợ và phân bổ

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Loại khoản tiền | B: thu, chi, NCC hoàn, chi hoa hồng; hoàn khách từ phiếu duyệt | Có |
| Chi nhánh | B, đúng phạm vi | Có |
| Đối tác | ĐK gắn nợ/cọc/CTV; loại phải phù hợp mục đích | Có; kiểm loại từng mục cần chuẩn hóa |
| Phiếu/đơn liên quan | K/ĐK thu cọc hoặc nợ xác định; khớp đối tác/chi nhánh | Có |
| Số tiền | B, nguyên VND >0 | Có |
| Phương thức | B, chữ ≤40 theo service; chuẩn chọn tiền mặt/bank/card/khác | Có |
| Nội dung/tham chiếu | K hiện tại; B cho thu/chi khác theo BA | Có; cần chuẩn hóa bắt buộc |
| Nhóm thu/chi, quỹ/tài khoản | K/ĐK báo cáo dòng tiền, tên đơn giản | Bổ sung |
| Ngày hiệu lực/đảo khoản tiền/lý do đảo | ĐK sửa sai, không sửa khoản đã ghi | Bổ sung |
| Công nợ — loại | T phải thu AR/phải trả AP | Có |
| Công nợ — đối tác/phiếu/chi nhánh | T nguồn | Có |
| Công nợ — phát sinh | T tổng nghĩa vụ của phiếu | Có |
| Công nợ — giảm do trả | T credit | Có |
| Công nợ — đã phân bổ/còn mở | T; còn = phát sinh − credit − allocated | Có |
| Ngày đến hạn | B theo chính sách; hiện tự +30 ngày | Có dữ liệu; chọn/sửa có kiểm soát bổ sung |
| Quá hạn | T theo ngày/remaining>0; chỉ nhắc, không chặn bán | Cần hiển thị/cảnh báo |
| Phân bổ — khoản tiền | B, còn dư, cùng đối tác/chiều tiền | Có |
| Phân bổ — khoản nợ | B, còn mở, đúng quyền phạm vi | Có |
| Phân bổ — số tiền | B >0 nguyên đồng; ≤tiền dư và ≤nợ mở | Có |
| Phân bổ — ngày/người/tham chiếu | T; bảo đảm truy vết | Có một phần; UI lịch sử bổ sung |
| Ứng trước còn dư | T; không gồm tiền đã hoàn/đảo | Có phần chưa allocated; cần chuẩn hóa sổ/hiển thị |
| NCC còn phải hoàn | T từ trả mua sau cấn AP | Bổ sung sổ riêng/liên kết thu hoàn |

Thu chi: STT, ngày, loại, chi nhánh, đối tác, phiếu, phương thức, số tiền, đã phân bổ, còn dư, nội dung. Công nợ: STT, loại, đối tác, phiếu, phát sinh, giảm, đã thanh toán, còn nợ, hạn, tình trạng. Không gộp Thu chi và Công nợ thành một module con.

## D-14. Hoa hồng

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Chi nhánh/CTV | B, loại COLLABORATOR | Có |
| Loại ghi | B theo đơn / bổ sung tháng / điều chỉnh | Có |
| Phiếu bán/trả liên quan | B khoản theo đơn; K điều chỉnh có thể gắn trả; phải đã ghi/cùng chi nhánh | Có |
| Kỳ | B, ngày đầu tháng; UI nên chọn tháng thay nhập ngày | Có kiểm dữ liệu; UX cần đổi |
| Số tiền | B nguyên VND khác 0; theo đơn/bổ sung >0, điều chỉnh có dấu | Có |
| Lý do | B, ≤1.000 | Có |
| Đã trả/còn phải trả | T theo CTV/chi nhánh/kỳ | Có aggregate |
| Chi hoa hồng — kỳ/số tiền/phương thức | B; ≤số còn phải trả | Có |

Không có trường tỷ lệ tự tính mặc định. Danh sách: STT, CTV, chi nhánh, kỳ, loại, phiếu, số tiền, lý do; bảng tổng hợp kỳ: phải trả, đã trả, còn trả. Điều chỉnh ghi dòng mới, không sửa khoản đã ghi.

## D-15. Trả hàng, hoàn và bảo hành

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Số phiếu bán gốc | B, đúng số, đã ghi; có thể ở chi nhánh khác | Có tra cứu chính xác |
| Dòng/lượng nguồn còn trả | T để chọn; không vượt đã bán trừ trả trước | Có kiểm quy tắc |
| Kho nhận trả | B, trong phạm vi | Có |
| Tình trạng/lý do trả | B theo D-09 và chính sách | Có điều kiện; USED chờ chốt |
| Ngày bán/số ngày trả/hạn bảo hành | T từ snapshot bán | Có |
| Quá hạn/lý do ngoại lệ | T/B nếu quá hạn cần duyệt | Có cờ quá hạn; UX/lý do chuyên biệt cần chuẩn hóa |
| Serial/lô/hạn gốc | ĐK, phải đúng hàng đã bán | Có |
| Được giảm nợ/được hoàn/đã hoàn | T; hoàn không vượt tiền thực thu hợp lệ | Có tính refundCredit; trình bày cần bổ sung |
| Phương thức hoàn | B; khác nguồn cần lý do | Có |
| Bảo hành — dòng bán gốc | B, POS/ISSUE đã ghi; chọn qua phiếu và hàng | Có dữ liệu, UI hiện nhập ID |
| Bảo hành — kho nhận/serial/lượng | B/ĐK; serial đúng, lượng >0 ≤nguồn | Có |
| Bảo hành — tình trạng ghi nhận | B, ≤4.000 | Có |
| Bảo hành — trạng thái/phiên bản | T/theo thao tác hợp lệ | Có |
| Bảo hành — người xử lý/ngày hẹn/nơi gửi/kết quả/chi phí | ĐK theo công việc thực tế | Bổ sung |
| Bảo hành — người nhận lại/ngày bàn giao/biên nhận | ĐK khi bàn giao | Bổ sung; hiện lưu note trạng thái |

Danh sách bảo hành: STT, phiếu gốc, khách, hàng, serial, lượng, kho nhận, ngày nhận/hẹn, trạng thái, xử lý. Vật phẩm bảo hành là giữ hộ, không tự đưa vào tồn bán. Với hàng không serial cần kiểm tổng hồ sơ bảo hành mở để tránh nhận vượt; mã hiện tại chủ yếu chặn trùng serial, cần bổ sung tổng lượng.

## D-16. Duyệt

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Phiếu/loại/trạng thái | T, chỉ chờ duyệt/phạm vi được cấp | Có |
| Người tạo/ngày tạo/chi nhánh/kho/đối tác | T từ phiếu | Có dữ liệu |
| Tổng hàng/tiền/lý do xin duyệt | T xem trước tác động | Có; tóm tắt cần chuẩn hóa |
| Dòng ngoại lệ | T giảm vượt, dưới vốn, lệch mua, quá hạn trả... | Có cờ/dữ liệu; giải thích từng dòng cần bổ sung |
| Quyết định | B duyệt/từ chối; không tự quyết phiếu mình | Có |
| Nội dung quyết định | B ≤1.000 | Có |
| Người/thời điểm quyết định | T; đầy đủ lịch sử | Người có; chuẩn hóa thời điểm/lịch sử |
| Yêu cầu hủy/đóng/chốt thiếu | ĐK; không làm trực tiếp nếu bắt buộc duyệt | Bổ sung |

Danh sách: STT, số, loại, ngày, người lập, kho/chi nhánh, đối tác, tổng, lý do; mở chi tiết rồi quyết định. Không có nút Duyệt cho chính người tạo.

## D-17. Nhập đầu kỳ và nhập danh mục

| Trường | Quy tắc | Hiện trạng |
|---|---|---|
| Loại nhập | B: hàng hóa / đối tác / tồn đầu / nợ đầu | Có |
| Kho/phạm vi | B trong API preview hiện tại kể cả danh mục | Có; wizard danh mục cần giải thích/chọn mặc định |
| Tệp/dữ liệu API | B một nguồn, tối đa 2 MB, 1–500 dòng/lần | Có CSV/xlsx/JSON |
| Mã batch/checksum/người/thời gian | T | Có |
| Dòng/số lỗi/thông báo lỗi | T, không commit khi còn lỗi | Có; kiểm đầy đủ sớm ở preview cần hoàn thiện |
| Trạng thái batch | T xem trước/đã nhập; tách trạng thái phiếu đầu kỳ | Có |
| ID/số/trạng thái phiếu sinh | T để tiếp tục gửi duyệt | Có nguồn kết quả; UX cần rõ |
| Ngày hiệu lực đầu kỳ | B theo D-02 | Bổ sung |

| Loại / cột nhập hiện tại | Bắt buộc/điều kiện |
|---|---|
| Hàng: `code,name,unit,price` | B; mã/tên/đơn vị hợp lệ, giá ≥0; mã không trùng trong tệp hoặc đã có |
| Hàng: `tracking,barcode,variant,returnDays,warrantyDays,weight` | K/ĐK; chính sách đổi trả/bảo hành cần đầy đủ theo BA; cấu trúc đơn vị phụ có thể nhận qua JSON nhưng mẫu đơn giản chưa bao phủ mọi trường |
| Đối tác: `code,name,roles` | B; roles gồm một hoặc nhiều CUSTOMER/SUPPLIER/COLLABORATOR. Có local; hỗ trợ roles JSON object hoặc chuỗi phân cách |; kind đơn còn tương thích |
| Đối tác: `phone,email,address,customerGroup` | K |
| Tồn: `skuCode,qty,price` | B; SKU có sẵn, qty >0, giá ≥0; kho lấy từ batch |
| Tồn: `lot,expiry,serials` | ĐK theo tracking; ngày YYYY-MM-DD, serial `SN1|SN2` |
| Nợ: `partnerCode,kind,amount` | B; đối tác có sẵn, AR/AP phù hợp, tiền >0; mỗi dòng sinh phiếu đầu kỳ nợ |

Mẫu Excel lấy sheet đầu, không nhận công thức. Hàng đầu là tên cột đúng; không nhập chỉ tiêu tổng cộng vào dòng dữ liệu. Không nhận nợ âm để thể hiện ứng trước; cần luồng đầu kỳ ứng trước riêng nếu khách có nhu cầu.

## D-18. Tổng quan, báo cáo, in và xuất

| Chức năng / trường | Quy tắc | Hiện trạng |
|---|---|---|
| Tổng quan — tab | T theo quyền Mua/Bán/Kho/Sản phẩm/CRM/Kế toán | Có |
| Tổng quan — kỳ/chi nhánh/kho/phạm vi | B/T nhãn rõ; chọn đúng phạm vi | Phạm vi cấp quyền có; bộ lọc riêng chưa đủ |
| Chỉ số doanh số | T bán đã ghi − trả đã ghi, theo ngày Việt Nam | Có summary |
| Chỉ số mua | T nhập đã ghi − trả mua đã ghi trong phạm vi | Có tab |
| Chỉ số tồn | T theo đơn vị cơ sở/tracking, không gọi tổng các đơn vị khác nhau là một lượng hàng | Có từng dòng; chuẩn hóa chỉ số |
| Nợ phải thu/phải trả | T tổng còn mở | Có |
| Nhắc việc/quá hạn | T; tổng phải tách danh sách tối đa 20 | Có danh sách giới hạn; tổng đầy đủ bổ sung |
| Báo cáo — từ/đến | B, đến ≥từ, hiện tối đa 3 năm | Có |
| Báo cáo — chi nhánh/kho/kênh/đối tác/hàng | K trong phạm vi | Cần mở rộng filter |
| Tồn đầu/nhập/xuất/cuối | T theo ngày movement; cuối = đầu+nhập−xuất | Có |
| Doanh thu/giảm/thuế/vốn/lợi nhuận | T; vốn/lãi theo quyền và công thức thống nhất | Dữ liệu phiếu/vốn có; báo cáo lợi nhuận hoàn chỉnh bổ sung |
| Tổng hợp thu chi/nợ/COD/hoa hồng | T từ đúng sổ, theo kỳ/phạm vi | Bổ sung báo cáo chuẩn |
| Xuất — loại/phạm vi/thời gian | B/K theo loại; kiểm quyền EXPORT và quyền đọc dữ liệu | Có CSV; đầy đủ lọc/phân trang cần kiểm |
| Xuất — người/thời điểm/số dòng | T nhật ký và thông báo kết quả | Audit có; số dòng/chất lượng UX bổ sung |
| Phiếu in — thông tin cửa hàng | T từ hồ sơ, số/ngày/khách/người bán | Cần hoàn thiện hồ sơ/mẫu và QA |
| Phiếu in — dòng/tổng/đã thu/còn nợ/tracking/chính sách | T từ giao dịch đã ghi và số dư đúng | Cần QA nội dung/máy in, không lấy BA làm bằng chứng đã đạt |

CSV được bảo vệ ô có thể bị diễn giải công thức. Chỉ xuất dữ liệu được phép; không thêm vốn ẩn vào file. Báo cáo gộp phí khách thu hộ/chi hãng phải giải thích, không lấy tổng phiếu làm lợi nhuận.

## D-19. Quản trị Core

Phần này dành quản trị; không đưa form kỹ thuật vào luồng bán hằng ngày. Trường mô tả được đối chiếu giao diện hiện tại; các giới hạn chưa xác minh để SRS/API contract chốt.

| Chức năng | Trường và quy tắc | Hiện trạng |
|---|---|---|
| Người dùng — tạo | Họ tên B, email B, mật khẩu B ≥8 local, vai trò B, đơn vị tổ chức K | Có |
| Người dùng — danh sách | T: ID, họ tên, email, trạng thái, vai trò, ngày tạo; bật/tắt/reset theo quyền | Có |
| Người dùng — reset | Chọn người B, mật khẩu mới B ≥8; thao tác quản trị có nhật ký | Có |
| Người dùng — vai trò nhiều/scope | B theo công việc; không cấp quyền chỉ bằng tên role | Dữ liệu role list có; UI chỉnh/gán đủ quyền và phạm vi cần hoàn thiện |
| Tổ chức | Mã B, tên B, đơn vị cha K; ID/trạng thái/ngày tạo T; kiểm không vòng lặp | Có tạo/danh sách |
| Vai trò | Mã B, tên B; ID/vai trò hệ thống T | Có tạo/danh sách |
| Quy tắc quyền | Mã B, loại tài nguyên B, hành động B, cho phép/từ chối B; điều kiện/phiên bản/hiệu lực T/K quản trị | Có tạo cơ bản; UI hiện chủ yếu READ/CREATE/UPDATE/DELETE/MANAGE, cần hỗ trợ đủ hành động retail hoặc mẫu cấu hình |
| Gán quyền cho vai trò | Vai trò + tập quyền B; hiệu lực theo server; kiểm xung đột | Cần UI nghiệp vụ dễ dùng và kiểm mẫu |
| Modules | T: mã/tên/phiên bản/trạng thái; bật/tắt theo quyền, kiểm phụ thuộc | Có màn quản trị |
| Resources | T: tên, chế độ lưu, module sở hữu, số bản ghi, phiên bản schema, ngày cập nhật | Có |
| Dynamic resource | B: khóa/tên/schema/phân loại khi tạo; phiên bản/trạng thái T; dành kỹ thuật | Có console, không dùng làm form sản phẩm chuẩn |
| Jobs | T: ID/loại/không gian dữ liệu/trạng thái/số lần/lỗi; chạy lại/hủy khi hợp lệ | Có |
| Outbox | T: ID/loại/đối tượng/trạng thái/số lần/lỗi; phát lại khi hợp lệ | Có |
| Nhật ký | T: người, hành động, loại/ID đối tượng, kết quả, thời gian, mã liên hệ request | Có dữ liệu; filter/export theo quyền cần đối chiếu khi hoàn thiện |
| Tệp | T: ID, tên, MIME, dung lượng, phân loại, trạng thái, ngày cập nhật; chọn tệp để tải lên, tải xuống theo quyền | Có UI tải lên/xuống; liên kết retail, chia sẻ/xóa nếu cần phải đặc tả thêm |
| Cài đặt môi trường | Tên môi trường, cấp dịch vụ (pilot/standard/critical), khu vực, URL công khai; lưu có nhật ký | Có; không phải tên/địa chỉ cửa hàng |
| Logo hệ thống | Chọn ảnh, tối đa 5 MB; tên/MIME/dung lượng T; thay logo cũ | Có Core; tái dùng cho hồ sơ/in phiếu cần liên kết rõ |
| MFA | Bật/tắt theo quyền quản trị; local đang tắt; thay đổi áp lần đăng nhập sau | Có; không bật lại trong nhiệm vụ BA |
| Hồ sơ cá nhân | T họ tên/email/vai trò/ID; B bộ mật khẩu khi tự đổi; đăng xuất | Có |

Không mô tả quyền kỹ thuật là quyền được cấp sẵn cho mọi chủ cửa hàng. Menu/định nghĩa có dữ liệu không chứng minh CRUD đầy đủ; mỗi gói việc phải kiểm API thực thi tương ứng.

## D-20. Ma trận form tối thiểu theo thao tác

| Form | Luôn hiện | Hiện khi có điều kiện | Hệ thống tính/hiển thị |
|---|---|---|---|
| Thêm sản phẩm | Mã, tên, đơn vị cơ sở, giá, tracking, ngày trả/bảo hành | Barcode, biến thể, trọng lượng, mở bao bì; nhóm/brand/thuộc tính khi bổ sung | Trạng thái, không có ô nhập tồn |
| Bán quầy | Kho, hàng, lượng/đơn vị, giá/tổng, thu/phương thức | Khách khi nợ, serial/lô, giảm/VAT | Khả dụng, giá áp, tiền còn nợ, trạng thái |
| Đơn bán | Kho, khách, kênh, dòng/tổng | Địa chỉ, phí, COD, cọc qua khoản thu | Đã xuất/còn xuất, đã thu/cọc/còn nợ, giao |
| Nhập mua | Kho, NCC, hàng, lượng, giá | Đơn nguồn, lô/serial, phí/phân bổ, đã trả | Chênh đơn, vốn dự kiến, nợ NCC |
| Chuyển | Nguồn/đích, hàng/lượng | Lô/serial | Đang chuyển, đã nhận/còn nhận |
| Kiểm kê | Kho/phạm vi, số đếm, lý do | Lô/hạn/serial | Snapshot/chênh lệch, khóa |
| Trả | Phiếu gốc, kho nhận, dòng/lượng/tình trạng/lý do | Tracking, quá hạn, đổi/hoàn | Còn được trả, giảm nợ/được hoàn |
| Thu chi | Chi nhánh, loại, tiền, phương thức/nội dung | Đối tác, phiếu/đơn, phân bổ, kỳ CTV | Còn dư, nợ còn mở |
| Chăm sóc | Khách/chi nhánh/nội dung | Lịch, người phụ trách khi bổ sung | Lịch sử, hoàn thành |
| Duyệt | Phiếu, lý do, quyết định | Thông tin ngoại lệ | Tác động và người lập |

Các dòng “Bổ sung” trong tài liệu phải thành yêu cầu SRS có tiêu chí nghiệm thu; không thêm trường tự do rồi coi đã đủ nghiệp vụ. Q-01 đã chốt biến thể chọn từ thuộc tính/giá trị cấu hình sẵn, Q-02 đã xác nhận khóa kiểm kê theo SKU. Chính sách trả hàng đã sử dụng Q-03 còn chờ quyết định.
