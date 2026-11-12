"use client";

import { useCallback, useEffect, useState } from "react";

// --- Định nghĩa kiểu dữ liệu ---
type OrderSummary = {
  id: string;
  orderCode: string;
  status: string;
  receiverName: string;
  receiverPhone: string;
  totalAmount: number;
  vtpOrderNumber: string | null;
  createdAt: string;
};

type OrderDetail = OrderSummary & {
  tenantId: string;
  shippingProvidersId: number;
  productName: string;
  receiverAddress: string;
  senderFullName: string;
  senderPhone: string;
  senderAddress: string;
  vtpServiceId: string;
  vtpOrderStatus: number | null; // <-- THÊM TRƯỜNG NÀY
  moneyCollection: number;
  moneyTotal: number;
  moneyTotalFee: number;
  moneyFee: number;
  moneyCollectionFee: number;
  moneyVAT: number;
  kpiHt: number;
  exchangeWeight: number;
  receiverProvince: number;
  receiverDistrict: number;
  receiverWard: number;
  sortCode: string;
  requestPayload: string;
  responsePayload: string;
  updatedAt: string;
};

type PageResponse<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
};

export default function OrderManagementPage() {
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [pageData, setPageData] = useState({ page: 1, totalPages: 1, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // State cho Modal Chi tiết
  const [selectedOrder, setSelectedOrder] = useState<OrderDetail | null>(null);

  const API_URL = process.env.NEXT_PUBLIC_CORE_API_URL ?? "http://localhost:8185";

  const apiRequest = async (path: string, options: RequestInit = {}) => {
    const token = typeof window !== "undefined" 
      ? localStorage.getItem("core-access-token") || sessionStorage.getItem("core-access-token") 
      : "";
    const res = await fetch(`${API_URL}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
      },
    });
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.detail || "Có lỗi xảy ra khi gọi API");
    }
    return res.json();
  };

  // --- Load danh sách đơn hàng ---
  const loadOrders = useCallback(async (page: number = 1) => {
    setLoading(true);
    setError("");
    try {
      const res = await apiRequest(`/api/v1/orders?page=${page}&size=10`);
      const data = res as PageResponse<OrderSummary>;
      setOrders(data.content);
      setPageData({ page: data.pageNumber, totalPages: data.totalPages, totalElements: data.totalElements });
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadOrders(1);
  }, [loadOrders]);

  // --- Xem chi tiết đơn hàng ---
  const viewOrderDetail = async (orderId: string) => {
    try {
      const res = await apiRequest(`/api/v1/orders/${orderId}`);
      setSelectedOrder(res as OrderDetail);
    } catch (err: any) {
      alert("Lỗi tải chi tiết: " + err.message);
    }
  };

  const formatMoney = (amount: number | null | undefined) => {
    if (amount == null) return "0 ₫";
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);
  };

  const getStatusBadge = (status: string) => {
    const styles: Record<string, { bg: string, color: string, label: string }> = {
      PENDING: { bg: "#fef9c3", color: "#854d0e", label: "Chờ xử lý" },
      INITED: { bg: "#dbeafe", color: "#1eaf23", label: "Đã tạo đơn" },
      SHIPPING: { bg: "#dbeafe", color: "#1e40af", label: "Đang giao" },
      DELIVERED: { bg: "#dcfce7", color: "#166534", label: "Thành công" },
      CANCELED: { bg: "#fee2e2", color: "#991b1b", label: "Đã hủy" },
      RETURNED: { bg: "#f3e8ff", color: "#6b21a8", label: "Hoàn hàng" },
    };
    const s = styles[status] || { bg: "#f1f5f9", color: "#475569", label: status };
    return <span style={{ padding: "4px 8px", borderRadius: "12px", fontSize: "12px", fontWeight: "500", background: s.bg, color: s.color }}>{s.label}</span>;
  };

  // --- Hàm map mã trạng thái VTP ra Tiếng Việt ---
  const getVtpStatusLabel = (statusCode: number | null) => {
    if (statusCode == null) return "Chưa có cập nhật";
    
    const vtpStatusMap: Record<number, string> = {
      100: "Chờ lấy hàng",
      101: "Đã tiếp nhận",
      102: "Đang lấy hàng",
      103: "Đã lấy hàng",
      104: "Đang nhập kho",
      105: "Đã nhập kho",
      200: "Đang luân chuyển",
      201: "Đã giao bưu tá",
      202: "Đang giao hàng",
      300: "Giao hàng thành công (1 phần)",
      400: "Chờ xử lý",
      500: "Đang phát",
      501: "Giao hàng thành công",
      503: "Hủy vận đơn",
      504: "Chuyển hoàn",
      505: "Đã chuyển hoàn",
      506: "Đã nhận chuyển hoàn",
      508: "Phát lại",
      509: "Chờ chuyển hoàn",
      550: "Đang chuyển hoàn",
      [-15]: "Từ chối nhận",
    };

    return vtpStatusMap[statusCode] || `Mã VTP: ${statusCode}`;
  };

  return (
    <div style={{ padding: "24px", maxWidth: "1200px", margin: "0 auto" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
        <div>
          <p style={{ fontSize: "14px", fontWeight: "bold", color: "#666", margin: 0, textTransform: "uppercase" }}>AICRM · Bán hàng</p>
          <h1 style={{ margin: "8px 0 0 0", fontSize: "24px" }}>Quản lý Đơn hàng</h1>
        </div>
        <button onClick={() => loadOrders(pageData.page)} style={{ padding: "8px 16px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}>
          ↻ Làm mới
        </button>
      </div>

      {error && <div style={{ color: "red", marginBottom: "16px", padding: "12px", background: "#fef2f2", border: "1px solid #f87171", borderRadius: "4px" }}>{error}</div>}

      {loading ? (
        <p>Đang tải dữ liệu...</p>
      ) : orders.length === 0 ? (
        <p style={{ color: "#666" }}>Chưa có đơn hàng nào.</p>
      ) : (
        <>
          <div style={{ overflowX: "auto", background: "white", borderRadius: "8px", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left", fontSize: "14px" }}>
              <thead>
                <tr style={{ background: "#f8fafc", borderBottom: "1px solid #e2e8f0" }}>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Mã đơn</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Khách nhận</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>SĐT</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Tổng tiền</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Mã VTP</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Trạng thái</th>
                  <th style={{ padding: "16px", fontWeight: "600", color: "#475569", textAlign: "right" }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((o) => (
                  <tr key={o.id} style={{ borderBottom: "1px solid #e2e8f0" }}>
                    <td style={{ padding: "16px", fontWeight: "bold" }}>{o.orderCode}</td>
                    <td style={{ padding: "16px" }}>{o.receiverName}</td>
                    <td style={{ padding: "16px" }}>{o.receiverPhone}</td>
                    <td style={{ padding: "16px", fontWeight: "500" }}>{formatMoney(o.totalAmount)}</td>
                    <td style={{ padding: "16px" }}>{o.vtpOrderNumber || <span style={{ color: "#999" }}>Chưa có</span>}</td>
                    <td style={{ padding: "16px" }}>{getStatusBadge(o.status)}</td>
                    <td style={{ padding: "16px", textAlign: "right" }}>
                      <button onClick={() => viewOrderDetail(o.id)} style={{ padding: "6px 12px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}>
                        Xem chi tiết
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: "16px" }}>
            <p style={{ fontSize: "14px", color: "#64748b", margin: 0 }}>
              Trang {pageData.page} / {pageData.totalPages} (Tổng {pageData.totalElements} đơn)
            </p>
            <div style={{ display: "flex", gap: "8px" }}>
              <button 
                disabled={pageData.page <= 1} 
                onClick={() => loadOrders(pageData.page - 1)}
                style={{ padding: "6px 12px", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: pageData.page <= 1 ? "not-allowed" : "pointer", background: "white" }}
              >
                Trước
              </button>
              <button 
                disabled={pageData.page >= pageData.totalPages} 
                onClick={() => loadOrders(pageData.page + 1)}
                style={{ padding: "6px 12px", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: pageData.page >= pageData.totalPages ? "not-allowed" : "pointer", background: "white" }}
              >
                Sau
              </button>
            </div>
          </div>
        </>
      )}

      {/* Modal Chi tiết đơn hàng */}
      {selectedOrder && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 50 }}>
          <div style={{ background: "white", padding: "24px", borderRadius: "8px", width: "100%", maxWidth: "800px", maxHeight: "90vh", overflowY: "auto" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px", borderBottom: "1px solid #e2e8f0", paddingBottom: "16px" }}>
              <h2 style={{ margin: 0, fontSize: "20px" }}>Chi tiết đơn hàng: {selectedOrder.orderCode}</h2>
              <button onClick={() => setSelectedOrder(null)} style={{ background: "none", border: "none", fontSize: "24px", cursor: "pointer", color: "#64748b" }}>×</button>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "24px" }}>
              <div>
                <h3 style={{ fontSize: "16px", borderBottom: "2px solid #3b82f6", paddingBottom: "4px", display: "inline-block" }}>Người gửi</h3>
                <p><strong>Tên:</strong> {selectedOrder.senderFullName || "N/A"}</p>
                <p><strong>SĐT:</strong> {selectedOrder.senderPhone || "N/A"}</p>
                <p><strong>Địa chỉ:</strong> {selectedOrder.senderAddress || "N/A"}</p>
              </div>
              <div>
                <h3 style={{ fontSize: "16px", borderBottom: "2px solid #10b981", paddingBottom: "4px", display: "inline-block" }}>Người nhận</h3>
                <p><strong>Tên:</strong> {selectedOrder.receiverName}</p>
                <p><strong>SĐT:</strong> {selectedOrder.receiverPhone}</p>
                <p><strong>Địa chỉ:</strong> {selectedOrder.receiverAddress}</p>
              </div>
            </div>

            <div style={{ marginTop: "24px" }}>
              <h3 style={{ fontSize: "16px", borderBottom: "2px solid #f59e0b", paddingBottom: "4px", display: "inline-block" }}>Thông tin vận chuyển</h3>
              {/* Đã sửa thành 4 cột để chứa thêm trạng thái VTP */}
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr 1fr", gap: "16px", background: "#f8fafc", padding: "16px", borderRadius: "8px", marginTop: "8px" }}>
                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Mã vận đơn VTP:</span><br/><strong>{selectedOrder.vtpOrderNumber || "Chưa có"}</strong></div>
                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Mã dịch vụ:</span><br/><strong>{selectedOrder.vtpServiceId || "N/A"}</strong></div>
                
                {/* TRẠNG THÁI HỆ THỐNG */}
                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Trạng thái (Hệ thống):</span><br/>{getStatusBadge(selectedOrder.status)}</div>
                
                {/* TRẠNG THÁI VTP */}
                <div>
                  <span style={{ color: "#64748b", fontSize: "13px" }}>Trạng thái (VTP):</span><br/>
                  <strong style={{ color: selectedOrder.vtpOrderStatus ? "#0f766e" : "#64748b" }}>
                    {getVtpStatusLabel(selectedOrder.vtpOrderStatus)}
                  </strong>
                </div>

                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Tiền thu hộ (COD):</span><br/><strong>{formatMoney(selectedOrder.moneyCollection)}</strong></div>
                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Tổng cước phí:</span><br/><strong>{formatMoney(selectedOrder.moneyTotalFee)}</strong></div>
                <div><span style={{ color: "#64748b", fontSize: "13px" }}>Trọng lượng quy đổi:</span><br/><strong>{selectedOrder.exchangeWeight} g</strong></div>
              </div>
            </div>

            <div style={{ marginTop: "24px", display: "flex", justifyContent: "flex-end" }}>
              <button onClick={() => setSelectedOrder(null)} style={{ padding: "8px 24px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}>Đóng</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}