# Khởi động và dữ liệu local AICRM

Tham chiếu [BA](docs/BA_HIEN_HANH.md), [SRS](docs/SRS_HIEN_HANH.md), [Runbook](docs/RUNBOOK_SU_DUNG.md). Tài liệu này cho dev/quản trị kỹ thuật.

## Khởi động

Docker Desktop phải chạy. Clone mới tạo .env từ .env.example và đặt secrets riêng. Không chia sẻ cấu hình production hoặc commit .env.

```powershell
docker compose up -d --build
docker compose ps
docker compose logs --tail 100 backend frontend
```

Web http://localhost:3205/; readiness http://localhost:8185/actuator/health/readiness; PostgreSQL localhost:55437. Admin local admin@core.local, mật khẩu từ AICRM_BOOTSTRAP_PASSWORD trong .env. Local tối thiểu8 ký tự, không ép độ phức tạp/MFA/đổi lần đầu; hash/throttle giữ nguyên. Chính sách production cần kiểm tra riêng.

## Đồng bộ code và database

Không cần cài lại DB khi lấy code mới. Giữ .env và volume, backup trước, build/start để Flyway chạy migration mới. Không sửa V1–V33 đã chạy, không xóa volume và không restore dump khởi tạo chồng DB có giao dịch.

Backup DB core_platform với service postgres/user core_admin:

```powershell
New-Item -ItemType Directory -Force -Path backups/database | Out-Null
docker compose exec -T postgres pg_dump -U core_admin -d core_platform -Fc -f /tmp/aicrm-before-upgrade.dump
docker compose cp postgres:/tmp/aicrm-before-upgrade.dump backups/database/aicrm-before-upgrade.dump
```

Dùng tên riêng mỗi lần, kiểm tra exit code/file khác0byte và thử restore DB cách ly. Dump chứa dữ liệu nhạy cảm, không Git. Storage tệp/cấu hình cần backup riêng. Restore DB thật cần kế hoạch và backup hiện trạng, không phải bước đồng bộ thường ngày.

## Thiết lập và module

Đăng nhập→Doanh nghiệp & Địa điểm→Kho chính→Danh mục→Đầu kỳ→Hoàn tất. Mặc định đã tạo. Import đầu kỳ là nháp cần gửi/duyệt; admin đủ điều kiện tự duyệt, staff theo kho cấp. Kiểm kê chỉ khóa SKU trong kho đang đếm.

[Hướng dẫn module đợt3](docs/BUSINESS_MODULES_PHASE3.md): pack/export-core/dry-run/import/start. Core API1.1.0/contract2; source import không mang data/secrets; lỗi startup/migration không tự rollback DB. Module tắt vẫn giữ dữ liệu/migration.

Failed to fetch: xem compose ps/logs/readiness, proxy/baseURL/session và bản chạy. Timeout kiểm phiếu trước khi tạo lại; retry cùng Idempotency-Key/payload. Không xóa volume chữa kết nối.

Snapshot tài liệu09/10: main ae59355 cộng local chưa commit, chưa xác minh server cùng bản. Evidence lịch sử trong docs/audit và BUSINESS_MODULES_PHASE3_PROGRESS.md không thay regression release mới.
