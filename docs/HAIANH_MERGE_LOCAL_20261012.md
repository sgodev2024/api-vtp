# Đồng bộ haianh_dev vào main local — 12/10/2026

Nguồn: origin/haianh_dev 9821e17. Main trước merge: ed26cd33ddbdbf384086f1551fe89a1670fcb465. Merge không reset/rebase, giữ cả hai lịch sử. Mốc phục hồi local: codex/before-haianh-merge-20261012.

Giải quyết xung đột:

- docker-compose.yml giữ cấu hình cổng AICRM_DATABASE_PORT mặc định55437; không đổi sang15432. Database/volume không thay đổi.
- workspace.tsx giữ dispatcher module hiện hành. Chuyển các cải tiến UI và MoneyInput tương thích từ workspace cũ của dev sang shared-workspace.tsx; không khôi phục kiến trúc monolith hoặc logic tải dữ liệu cũ.
- Giữ CRM Tiềm năng/Cơ hội, menu nhóm nghiệp vụ, phân quyền và module packaging của main.

Đã nhận cải tiến CSS, giao diện địa điểm/đối tác/bán hàng, MoneyInput định dạng tiền. Frontend production và TypeScript đạt; 25 kiểm tra UI gồm desktop15route/mobile8route, MoneyInput số nguyên và thập phân, không lỗi JavaScript. 9 kiểm tra module manager đạt. Kiểm thử chặn thao tác ghi nghiệp vụ; API tra giá chỉ đọc được cho phép.

Đã build và khởi động riêng frontend localhost3205. Không rebuild backend, không migrate database, không push Git/server trong đợt đồng bộ local này. Evidence tại backups/haianh-merge-20261012/ui-results.json (ignored).
