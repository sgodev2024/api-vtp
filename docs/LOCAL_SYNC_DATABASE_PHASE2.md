# Đồng bộ local và database — Đợt 2 (08/10/2026)

## Máy đã có database
Không phải cài lại PostgreSQL, tạo lại database hoặc import dump mới. Đợt này giữ nguyên 33 migration và schema nghiệp vụ; backend tự đăng ký module, chuyển owner resource sang package mới khi khởi động. Dữ liệu, quyền theo resource key và tài khoản hiện có được giữ.

Giữ nguyên file .env và Docker Compose project/volume đang sử dụng. Chạy `docker compose ls` để xác định project; ví dụ dưới dùng aicrm-java. Nếu máy đang dùng aicrm-dev, thay aicrm-java bằng aicrm-dev trong mọi lệnh. Đổi project sẽ tạo volume khác và có thể khiến ứng dụng trông như mất dữ liệu.

### 1. Sao lưu (PowerShell, tại thư mục dự án)
```powershell
$stamp = Get-Date -Format yyyyMMdd-HHmmss
New-Item -ItemType Directory -Force backups/db | Out-Null
docker compose -p aicrm-java exec -T postgres pg_dump -U core_admin -d core_platform -Fc -f /tmp/aicrm-before-update.dump
docker compose -p aicrm-java cp postgres:/tmp/aicrm-before-update.dump "backups/db/aicrm-$stamp.dump"
```
Dừng nếu pg_dump/cp báo lỗi. Dump chứa dữ liệu nhạy cảm; không commit/push hoặc gửi cùng source.

### 2. Đồng bộ source
```powershell
git status
git switch main
git pull --ff-only origin main
```
Nếu có code local chưa commit, lưu thành commit riêng hoặc stash (kèm untracked) và ghép lại có kiểm tra. Không dùng reset --hard để bỏ qua xung đột. Bản này đã di chuyển package Java và frontend; không chép đè một vài file lên source cũ vì có thể giữ API/bean trùng.

### 3. Build và chạy
```powershell
docker compose -p aicrm-java up -d --build
docker compose -p aicrm-java ps
docker compose -p aicrm-java logs --tail=100 backend
```
Truy cập http://localhost:3205. Backend http://localhost:8185. Backend chạy Flyway validate/migrate tự động trước khi phục vụ API. Không chỉnh các migration đã chạy; nếu checksum lỗi, đối chiếu code và database với người phụ trách thay vì chạy repair hoặc xóa schema.

### 4. Kiểm tra
Đăng nhập bằng tài khoản hiện có; mở Sản phẩm, Bán hàng, Kho, Mua hàng. Trong Quản lý modules kiểm tra aicrm-retail, aicrm-stock, aicrm-sales, aicrm-purchase. Khi cần bật: dùng chung → Kho → Bán hàng/Mua hàng. Khi tắt Kho: tắt Bán hàng/Mua hàng trước. Các module đã tắt vẫn giữ dữ liệu; admin cũng bị chặn API nghiệp vụ của module tắt.

Không dùng `docker compose down -v`, xóa volume, hoặc import dump ghi đè để cập nhật phiên bản này. Không sao chép .env từ máy khác vì thông tin truy cập DB có thể khác.

## Máy mới, chưa có database
Cài Git và Docker Desktop (Linux containers), clone repository rồi cấu hình .env:
```powershell
git clone https://github.com/sgodev2024/aicrm-java.git
Set-Location aicrm-java
Copy-Item .env.example .env
```
Sửa ba giá trị AICRM_POSTGRES_PASSWORD, AICRM_DB_PASSWORD, AICRM_BOOTSTRAP_PASSWORD trong .env thành mật khẩu local; chỉ copy .env.example khi chưa có .env. Sau đó chạy `docker compose -p aicrm-java up -d --build`. PostgreSQL tạo database/role qua deploy/postgres; Flyway tạo schema tự động. Tài khoản đầu tiên admin@core.local dùng AICRM_BOOTSTRAP_PASSWORD. Database mới không chứa dữ liệu của máy khác.

Nếu cần dùng bộ dữ liệu dev đã có, xin dump đã được phép chia sẻ từ người phụ trách và phục hồi vào database dev riêng. Không bắt buộc import dump để chạy bản mới.