# AICRM Java Core Platform

Ứng dụng bán hàng, mua hàng, kho, sản phẩm, customer master, CRM và kế toán nội bộ trên Java Core. Source local đang có thay đổi tách module đợt 3 chưa commit.

## Tài liệu hiện hành

- [Bộ tài liệu](docs/README.md)
- [BA](docs/BA_HIEN_HANH.md)
- [SRS](docs/SRS_HIEN_HANH.md)
- [Runbook sử dụng](docs/RUNBOOK_SU_DUNG.md)
- [Khởi động local](AICRM_LOCAL.md)
- [Task dev và bằng chứng audit](docs/SYSTEM_AUDIT_DEV_BACKLOG_2026-10-09.md)
- [Import module vào core mới](docs/BUSINESS_MODULES_PHASE3.md)

BA/SRS cũ đã rút khỏi docs; bản sao phục hồi tại backups/documentation-20261009 không dùng làm yêu cầu hiện hành. Báo cáo tiến độ khác là lịch sử.

## Chạy local

Docker Desktop phải hoạt động. Clone mới tạo .env từ .env.example và đặt secrets riêng, không commit .env. Chạy từ thư mục dự án:

```powershell
docker compose up -d --build
docker compose ps
```

Web http://localhost:3205/; backend readiness http://localhost:8185/actuator/health/readiness; PostgreSQL localhost:55437. Admin local admin@core.local; mật khẩu lấy AICRM_BOOTSTRAP_PASSWORD trong .env.

Lấy code mới không cần cài lại database. Backup trước, build/start để Flyway áp dụng migration mới. Không xóa volume hoặc import lại dump đầu kỳ vào DB có dữ liệu.

## Module và kiểm thử

Core vn.coreplatform, nghiệp vụ vn.aicrm và frontend app/retail. Gói import source/build/restart, không chuyển DB. Core đích cần core API 1.1.0/import contract 2. API chặn khi tắt module; core và dependency có bảo vệ.

AT trong SRS là ca cần chạy. Backend CI hiện package -DskipTests, chưa có integration gate. Audit chỉ đọc ngày09/10 là mẫu admin/API/UI, chưa full regression, tải lớn hoặc multi-tenant. Xem task/evidence trước release.
