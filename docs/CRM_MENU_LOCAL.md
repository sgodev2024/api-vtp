# CRM menu — local 12/10/2026

Nguyên nhân: frontend renderSection tự bỏ nhãn workspace khi chỉ có một page. CRM HEALTHY, API navigation có CRM → Khách hàng, nhưng desktop hiển thị riêng Khách hàng.

Đã sửa frontend/app/page.tsx: chỉ home, retail-overview, retail-reports được render trực tiếp khi có một page; các workspace nghiệp vụ giữ tên nhóm. Không đổi API, quyền, database hoặc Customer Master. Mobile hiện có giữ nhãn CRM và nhãn con Khách hàng trên một mục trong danh mục chức năng.

Đã build production frontend và khởi động lại riêng frontend local http://localhost:3205. Kiểm tra trình duyệt Edge tự động:

- Desktop1440px: CRM → Khách hàng mở đúng /business/retail/customers.
- Trang chủ/Tổng quan/Báo cáo tiếp tục là trang độc lập.
- Mobile390px: Thêm → CRM mở được, không tràn ngang.
- Mock response navigation chỉ còn một page Bán hàng và một page CRM: giữ tên nhóm. Đây là kiểm thử UI, không thay quyền tài khoản thực tế.
- Không có lỗi JavaScript.

Không tạo/sửa dữ liệu nghiệp vụ, không commit/push hoặc deploy server. Backend và database không rebuild trong đợt này. Script kiểm tra local: tmp/verify-crm-menu-fix.cjs ở workspace cha.
