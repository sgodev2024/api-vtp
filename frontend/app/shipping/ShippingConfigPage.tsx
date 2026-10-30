"use client";

import { useCallback, useEffect, useState } from "react";

// Định nghĩa kiểu dữ liệu khớp với Backend trả về
type ShippingProvider = {
  id: number;
  code: string;
  name: string;
  token: string | null;
  isEnable: boolean;
  senderName: string | null;
  senderPhone: string | null;
  senderAddress: string | null;
  token_expires_at: string | null;
};

export default function ShippingConfigPage() {
  const [providers, setProviders] = useState<ShippingProvider[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  // State cho Modal
  const [modalType, setModalType] = useState<"add" | "token" | "address" | null>(null);
  const [selectedProvider, setSelectedProvider] = useState<ShippingProvider | null>(null);
  
  // State cho form Thêm mới
  const [newCode, setNewCode] = useState("");
  const [newName, setNewName] = useState("");
  const [newSenderName, setNewSenderName] = useState("");
  const [newSenderPhone, setNewSenderPhone] = useState("");
  const [newSenderAddress, setNewSenderAddress] = useState("");

  // State cho form Sửa
  const [inputToken, setInputToken] = useState("");
  const [senderName, setSenderName] = useState("");
  const [senderPhone, setSenderPhone] = useState("");
  const [senderAddress, setSenderAddress] = useState("");

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
    if (res.status === 204) return null;
    return res.json();
  };

  const loadProviders = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await apiRequest("/api/v1/shipping-providers");
      setProviders(res as ShippingProvider[]);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadProviders();
  }, [loadProviders]);

  const handleToggleStatus = async (p: ShippingProvider) => {
    try {
      setError("");
      await apiRequest(`/api/v1/shipping-providers/${p.id}/status`, {
        method: "PATCH",
        body: JSON.stringify({ isEnabled: !p.isEnable }),
      });
      setSuccessMsg(`Đã cập nhật trạng thái cho ${p.name}.`);
      void loadProviders();
    } catch (err: any) {
      setError(err.message);
    }
  };

  // Xử lý Thêm mới Nhà cung cấp
  const handleAddProvider = async () => {
    if (!newCode.trim() || !newName.trim()) {
      setError("Mã và Tên nhà cung cấp không được để trống.");
      return;
    }
    try {
      setError("");
      await apiRequest(`/api/v1/shipping-providers`, {
        method: "POST",
        body: JSON.stringify({
          code: newCode.trim().toUpperCase(),
          name: newName.trim(),
          senderName: newSenderName.trim(),
          senderPhone: newSenderPhone.trim(),
          senderAddress: newSenderAddress.trim(),
        }),
      });
      setSuccessMsg(`Đã thêm nhà cung cấp ${newName}.`);
      setModalType(null);
      // Reset form
      setNewCode(""); setNewName(""); setNewSenderName(""); setNewSenderPhone(""); setNewSenderAddress("");
      void loadProviders();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleSaveToken = async () => {
    if (!selectedProvider) return;
    try {
      setError("");
      await apiRequest(`/api/v1/shipping-providers/${selectedProvider.id}/token`, {
        method: "PUT",
        body: JSON.stringify({ token: inputToken }),
      });
      setSuccessMsg(`Đã cập nhật token cho ${selectedProvider.name}.`);
      setModalType(null);
      void loadProviders();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleSaveAddress = async () => {
    if (!selectedProvider) return;
    try {
      setError("");
      await apiRequest(`/api/v1/shipping-providers/${selectedProvider.id}/address`, {
        method: "PUT",
        body: JSON.stringify({ senderName, senderPhone, senderAddress }),
      });
      setSuccessMsg(`Đã cập nhật thông tin gửi hàng cho ${selectedProvider.name}.`);
      setModalType(null);
      void loadProviders();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const openModal = (type: "add" | "token" | "address", p?: ShippingProvider) => {
    if (type === "add") {
      setModalType("add");
      return;
    }
    if (!p) return;
    setSelectedProvider(p);
    setModalType(type);
    if (type === "token") setInputToken(p.token || "");
    if (type === "address") {
      setSenderName(p.senderName || "");
      setSenderPhone(p.senderPhone || "");
      setSenderAddress(p.senderAddress || "");
    }
  };

  return (
    <div style={{ padding: "24px", maxWidth: "1200px", margin: "0 auto" }}>
      {/* Header */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
        <div>
          <p style={{ fontSize: "14px", fontWeight: "bold", color: "#666", margin: 0, textTransform: "uppercase" }}>AICRM · Cấu hình</p>
          <h1 style={{ margin: "8px 0 0 0", fontSize: "24px" }}>Quản lý Vận chuyển</h1>
        </div>
        <div style={{ display: "flex", gap: "12px" }}>
          <button 
            onClick={() => openModal("add")} 
            style={{ padding: "8px 16px", background: "#0284c7", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}
          >
            + Thêm nhà cung cấp
          </button>
          <button 
            onClick={() => loadProviders()} 
            style={{ padding: "8px 16px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}
          >
            ↻ Làm mới
          </button>
        </div>
      </div>

      {/* Alerts */}
      {error && <div style={{ color: "red", marginBottom: "16px", padding: "12px", background: "#fef2f2", border: "1px solid #f87171", borderRadius: "4px" }}>{error}</div>}
      {successMsg && <div style={{ color: "green", marginBottom: "16px", padding: "12px", background: "#f0fdf4", border: "1px solid #4ade80", borderRadius: "4px" }}>{successMsg}</div>}

      {/* Bảng Dữ liệu */}
      {loading ? (
        <p>Đang tải dữ liệu...</p>
      ) : providers.length === 0 ? (
        <p style={{ color: "#666" }}>Chưa có nhà cung cấp nào. Hãy bấm "Thêm nhà cung cấp".</p>
      ) : (
        <div style={{ overflowX: "auto", background: "white", borderRadius: "8px", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left", fontSize: "14px" }}>
            <thead>
              <tr style={{ background: "#f8fafc", borderBottom: "1px solid #e2e8f0" }}>
                <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Mã</th>
                <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Tên nhà cung cấp</th>
                <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Thông tin người gửi</th>
                <th style={{ padding: "16px", fontWeight: "600", color: "#475569" }}>Trạng thái</th>
                <th style={{ padding: "16px", fontWeight: "600", color: "#475569", textAlign: "right" }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {providers.map((p) => (
                <tr key={p.id} style={{ borderBottom: "1px solid #e2e8f0" }}>
                  <td style={{ padding: "16px", fontWeight: "bold" }}>{p.code}</td>
                  <td style={{ padding: "16px" }}>{p.name}</td>
                  <td style={{ padding: "16px", fontSize: "13px", color: "#64748b" }}>
                    {p.senderName ? (
                      <>
                        <div>{p.senderName} - {p.senderPhone}</div>
                        <div>{p.senderAddress}</div>
                      </>
                    ) : (
                      <span style={{ color: "#f59e0b" }}>Chưa cấu hình</span>
                    )}
                  </td>
                  <td style={{ padding: "16px" }}>
                    <span style={{ 
                      padding: "4px 8px", borderRadius: "12px", fontSize: "12px", fontWeight: "500",
                      background: p.isEnable ? "#dcfce7" : "#fef9c3",
                      color: p.isEnable ? "#166534" : "#854d0e"
                    }}>
                      {p.isEnable ? "Hoạt động" : "Tạm dừng"}
                    </span>
                  </td>
                  <td style={{ padding: "16px", textAlign: "right" }}>
                    <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
                      <button onClick={() => openModal("address", p)} style={{ padding: "6px 12px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}>Sửa thông tin gửi</button>
                      <button onClick={() => openModal("token", p)} style={{ padding: "6px 12px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}>Cập nhật Token</button>
                      <button onClick={() => handleToggleStatus(p)} style={{ padding: "6px 12px", background: p.isEnable ? "#ef4444" : "#10b981", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontSize: "13px" }}>
                        {p.isEnable ? "Tạm dừng" : "Kích hoạt"}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal Thêm mới */}
      {modalType === "add" && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 50 }}>
          <div style={{ background: "white", padding: "24px", borderRadius: "8px", width: "100%", maxWidth: "500px" }}>
            <h2 style={{ marginTop: 0, fontSize: "20px", marginBottom: "16px" }}>Thêm nhà cung cấp</h2>
            <div style={{ display: "flex", flexDirection: "column", gap: "16px", marginBottom: "24px" }}>
              <div>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Mã nhà cung cấp (VD: VIETTEL_POST) *</label>
                <input type="text" value={newCode} onChange={(e) => setNewCode(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
              </div>
              <div>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Tên nhà cung cấp *</label>
                <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
              </div>
              <hr style={{ border: "none", borderTop: "1px solid #e2e8f0", margin: "8px 0" }} />
              <p style={{ margin: 0, fontSize: "13px", color: "#64748b", fontStyle: "italic" }}>Thông tin người gửi mặc định (có thể cập nhật sau)</p>
              <div>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Tên người gửi</label>
                <input type="text" value={newSenderName} onChange={(e) => setNewSenderName(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
              </div>
              <div>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Số điện thoại</label>
                <input type="text" value={newSenderPhone} onChange={(e) => setNewSenderPhone(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
              </div>
              <div>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Địa chỉ lấy hàng</label>
                <textarea value={newSenderAddress} onChange={(e) => setNewSenderAddress(e.target.value)} rows={2} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
              </div>
            </div>
            <div style={{ display: "flex", justifyContent: "flex-end", gap: "12px" }}>
              <button onClick={() => setModalType(null)} style={{ padding: "8px 16px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: "pointer" }}>Hủy</button>
              <button onClick={handleAddProvider} style={{ padding: "8px 16px", background: "#0284c7", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}>Thêm mới</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Sửa Token / Địa chỉ */}
      {(modalType === "token" || modalType === "address") && selectedProvider && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 50 }}>
          <div style={{ background: "white", padding: "24px", borderRadius: "8px", width: "100%", maxWidth: "500px" }}>
            <h2 style={{ marginTop: 0, fontSize: "20px", marginBottom: "16px" }}>
              {modalType === "token" ? `Cập nhật Token - ${selectedProvider.name}` : `Thông tin gửi hàng - ${selectedProvider.name}`}
            </h2>
            
            {modalType === "token" ? (
              <div style={{ marginBottom: "24px" }}>
                <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Token API (Viettel Post)</label>
                <textarea value={inputToken} onChange={(e) => setInputToken(e.target.value)} placeholder="Dán chuỗi Token vào đây..." rows={4} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px", fontFamily: "monospace" }} />
              </div>
            ) : (
              <div style={{ display: "flex", flexDirection: "column", gap: "16px", marginBottom: "24px" }}>
                <div>
                  <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Tên người gửi</label>
                  <input type="text" value={senderName} onChange={(e) => setSenderName(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
                </div>
                <div>
                  <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Số điện thoại</label>
                  <input type="text" value={senderPhone} onChange={(e) => setSenderPhone(e.target.value)} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
                </div>
                <div>
                  <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>Địa chỉ lấy hàng</label>
                  <textarea value={senderAddress} onChange={(e) => setSenderAddress(e.target.value)} rows={3} style={{ width: "100%", padding: "8px", border: "1px solid #cbd5e1", borderRadius: "4px" }} />
                </div>
              </div>
            )}

            <div style={{ display: "flex", justifyContent: "flex-end", gap: "12px" }}>
              <button onClick={() => setModalType(null)} style={{ padding: "8px 16px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px", cursor: "pointer" }}>Hủy</button>
              <button onClick={modalType === "token" ? handleSaveToken : handleSaveAddress} style={{ padding: "8px 16px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "500" }}>Lưu thay đổi</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}