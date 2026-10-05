# AICRM — ứng dụng local trên Java Core

Dự án độc lập tại `aicrm-java`, nền Core ghi trong `CORE_BASELINE`. Repository đồng bộ: https://github.com/sgodev2024/java-aicrm. Chỉ vận hành local, chưa triển khai server. BA/SRS nằm trong `docs/`.

## Khởi động

Yêu cầu Docker Desktop đang chạy. Khi clone mới, sao chép `.env.example` thành `.env` và đặt ba mật khẩu riêng trước khi chạy. `.env` không được đưa lên Git. Máy local hiện tại đã được giữ cấu hình trong `.env`. Tại thư mục dự án:

```powershell
docker compose up -d --build
docker compose ps
```

| Thành phần | Địa chỉ |
|---|---|
| Ứng dụng | http://localhost:3205/business/retail/overview |
| Đăng nhập | http://localhost:3205/ |
| Backend readiness | http://localhost:8185/actuator/health/readiness |
| PostgreSQL | localhost:55437 |

Chỉ bind loopback. Tài khoản local: `admin@core.local`; mật khẩu lấy từ `AICRM_BOOTSTRAP_PASSWORD` trong `.env`. Theo yêu cầu dự án: MFA mặc định tắt, không buộc đổi mật khẩu lần đầu/sau reset, tối thiểu 8 ký tự, không bắt buộc chữ hoa/số/ký tự đặc biệt. Hash và giới hạn thử sai vẫn sử dụng Core.

`/prototype` chuyển đến ứng dụng có đăng nhập. Màn hình nghiệp vụ dùng API và lưu PostgreSQL; tải lại trang giữ giao dịch. Dữ liệu có tên “QA / kiểm thử” là dữ liệu kiểm thử local.

## Bắt đầu vận hành

1. Đăng nhập, mở **Thiết lập ban đầu**. Tạo chi nhánh, kho, hàng hóa/SKU và khách/NCC. Khai báo đơn vị phụ, serial/lô và chính sách đổi trả/bảo hành theo SKU.
2. Trong quản trị Core, tạo người dùng và gán quyền nghiệp vụ. Trong Thiết lập, gán kho phụ trách. Quyền thuộc resource `aicrm-retail`; người không có `ALL_BRANCHES` phải được gán kho. Quản trị mặc định có quyền toàn công ty.
3. Tạo người duyệt khác người lập: nhập mua, trả hàng, chuyển kho, kiểm kê, tồn/công nợ đầu kỳ, hoàn tiền và bán ngoại lệ cần người khác xác nhận.
4. Nhập mua trực tiếp hoặc lập đơn mua. Chờ duyệt chưa tăng tồn/nợ. Đã ghi sổ tạo tồn, giá vốn bình quân và phải trả NCC.
5. Bán tại quầy: chọn kho, tìm/quét hàng, chọn khách nếu ghi nợ, lượng/đơn vị và tiền trả. Đơn online nhập tay tại Đơn bán hàng; xuất từng phần khi có tồn. Không xuất âm.
6. Trả hàng từ phiếu gốc, kiểm đúng serial/lô và thời hạn. Nhận khác kho được; hàng chờ kiểm cách ly. Hoàn tiền dùng chứng từ riêng cần duyệt. Đổi hàng lập bán mới liên kết phiếu trả.
7. Chuyển kho: đề nghị → duyệt → xuất nguồn → đang chuyển → nhận đích từng phần. Kiểm kê: mở kỳ → ghi số đếm → hoàn tất → người khác duyệt. Hàng trong kỳ bị khóa xuất nhập.
8. Thu chi/công nợ: nhập khoản tiền, phân bổ vào khoản phải thu/phải trả. Cọc liên kết đơn chỉ phân bổ khi xuất/nhập. COD và vận chuyển nhập tay.

## Kiểm chứng

```powershell
$env:AICRM_ADMIN_PASSWORD='<mật khẩu local trong .env>'
node scripts/retail-smoke.mjs
```

Script tạo dữ liệu QA riêng bằng mã ngẫu nhiên, chạy giao dịch thật và vô hiệu hóa tài khoản QA sau khi xong; dữ liệu chứng từ được giữ để đối chiếu. Không chạy script trên dữ liệu khách hàng thật.

Kết quả và giới hạn nghiệm thu: `docs/LOCAL_VERIFICATION.md`, `docs/IMPLEMENTATION_STATUS.md`. Kết quả máy đọc: `docs/retail-smoke-result.json`.

## Lưu dữ liệu và khôi phục

Dừng giữ volume: `docker compose down`. Không dùng `down -v` khi cần giữ dữ liệu.

```powershell
docker compose exec -T postgres pg_dump -U core_admin -d core_platform --no-owner --file=/tmp/aicrm.sql
docker compose cp postgres:/tmp/aicrm.sql ./backups/aicrm.sql
```

Tạo thư mục `backups` trước. Bản kiểm thử đã lưu tại `docs/local-backup/aicrm-local-verification.sql`, được bỏ qua bởi Git. Khi khôi phục, tạo database trống có tên mới, bảo đảm role `core_app` tồn tại, chép dump vào container rồi chạy `psql -v ON_ERROR_STOP=1 -U core_admin -d <database_mới> -f <dump>`. Chuyển DB_URL sau khi đối chiếu và tắt ứng dụng ghi dữ liệu trong thời điểm cutover. Sao lưu riêng volume file Core, cấu hình và khóa ứng dụng; dump database không thay thế các bản sao này.

## Kiến trúc tách khách hàng

Nghiệp vụ nằm trong `vn.aicrm.retail`, schema `retail`; Core cấp đăng nhập, quyền, navigation, audit, outbox và tenant context. 23 bảng retail có forced RLS theo tenant; API áp dụng thêm phạm vi kho/chi nhánh. Đây là nền để nâng cấp SaaS, chưa phải gói vận hành SaaS hoàn chỉnh. Chạy riêng có thể dùng cùng source với database/config riêng. Tách dữ liệu một khách từ database SaaS chung, provisioning, subscription và vận hành SaaS còn cần triển khai và nghiệm thu riêng.
