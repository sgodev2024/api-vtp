"use client";
import { useEffect, useState } from "react";

export default function ApiKeyPage() {
  const [keys, setKeys] = useState<any[]>([]);
  const [newKeyName, setNewKeyName] = useState("");
  const [rawKeyDisplay, setRawKeyDisplay] = useState<string | null>(null);
  const API_URL = process.env.NEXT_PUBLIC_CORE_API_URL ?? "http://localhost:8185";

  const apiRequest = async (path: string, options: RequestInit = {}) => {
    const token = localStorage.getItem("core-access-token");
    const res = await fetch(`${API_URL}${path}`, {
      ...options,
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Lỗi kết nối");
    return res.json();
  };

  const load = () => apiRequest("/api/v1/api-keys").then(setKeys);
  useEffect(() => { load(); }, []);

  const handleCreate = async () => {
    if (!newKeyName.trim()) return alert("Vui lòng nhập tên key");
    try {
      const res = await apiRequest("/api/v1/api-keys", {
        method: "POST",
        body: JSON.stringify({ name: newKeyName, expiresInDays: 365 }),
      });
      setRawKeyDisplay(res.apiKey); // Hiển thị Modal chứa Key
      setNewKeyName("");
      load();
    } catch (e) { alert("Tạo key thất bại"); }
  };

  const handleRevoke = async (id: string) => {
    if (!confirm("Bạn chắc chắn muốn thu hồi key này? Hệ thống bên ngoài sẽ không thể gọi API nữa.")) return;
    await apiRequest(`/api/v1/api-keys/${id}/revoke`, { method: "PATCH" });
    load();
  };

  return (
    <div style={{ padding: "24px", maxWidth: "800px", margin: "0 auto" }}>
      <h1>Quản lý API Key</h1>
      <p>Dùng API Key này để hệ thống bên ngoài gọi API vận chuyển của công ty bạn.</p>

      <div style={{ display: "flex", gap: "8px", margin: "16px 0" }}>
        <input 
          value={newKeyName} onChange={(e) => setNewKeyName(e.target.value)} 
          placeholder="Tên gợi nhớ (VD: Web bán hàng)" 
          style={{ flex: 1, padding: "8px", border: "1px solid #ccc", borderRadius: "4px" }}
        />
        <button onClick={handleCreate} style={{ padding: "8px 16px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px" }}>
          + Tạo Key mới
        </button>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse", background: "white" }}>
        <thead>
          <tr style={{ background: "#f8fafc", textAlign: "left" }}>
            <th style={{ padding: "12px" }}>Tên</th>
            <th style={{ padding: "12px" }}>Prefix</th>
            <th style={{ padding: "12px" }}>Trạng thái</th>
            <th style={{ padding: "12px" }}>Ngày tạo</th>
            <th style={{ padding: "12px" }}>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {keys.map(k => (
            <tr key={k.id} style={{ borderBottom: "1px solid #eee" }}>
              <td style={{ padding: "12px" }}>{k.name}</td>
              <td style={{ padding: "12px", fontFamily: "monospace" }}>{k.prefix}****</td>
              <td style={{ padding: "12px" }}>{k.status}</td>
              <td style={{ padding: "12px" }}>{new Date(k.created_at).toLocaleDateString("vi-VN")}</td>
              <td style={{ padding: "12px" }}>
                {k.status === "ACTIVE" && (
                  <button onClick={() => handleRevoke(k.id)} style={{ color: "red", cursor: "pointer", border: "none", background: "none" }}>
                    Thu hồi
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal hiển thị API Key 1 lần duy nhất */}
      {rawKeyDisplay && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.6)", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <div style={{ background: "white", padding: "24px", borderRadius: "8px", maxWidth: "500px", width: "100%" }}>
            <h2 style={{ color: "#0f766e", marginTop: 0 }}>Tạo API Key thành công!</h2>
            <p style={{ color: "red", fontWeight: "bold" }}>Hãy sao chép và lưu trữ Key này ngay lập tức. Bạn sẽ KHÔNG THỂ xem lại nó sau khi đóng cửa sổ này.</p>
            <div style={{ background: "#f1f5f9", padding: "12px", borderRadius: "4px", fontFamily: "monospace", wordBreak: "break-all", marginBottom: "16px" }}>
              {rawKeyDisplay}
            </div>
            <button 
              onClick={() => { navigator.clipboard.writeText(rawKeyDisplay); alert("Đã sao chép!"); }}
              style={{ padding: "8px 16px", background: "#0f766e", color: "white", border: "none", borderRadius: "4px", marginRight: "8px" }}
            >
              Sao chép
            </button>
            <button onClick={() => setRawKeyDisplay(null)} style={{ padding: "8px 16px", background: "white", border: "1px solid #cbd5e1", borderRadius: "4px" }}>
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
}