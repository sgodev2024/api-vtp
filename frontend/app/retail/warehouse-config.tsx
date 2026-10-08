"use client";

import { useEffect, useState } from "react";

type Entry = {
  kind: string;
  code: string;
  name: string;
  value: string;
  active: boolean;
};

type EntryPayload = Omit<Entry, "code"> & { code: string | null };

const tabs = [
  ["INVENTORY", "Cài đặt tồn kho"],
  ["UNIT", "Đơn vị tính"],
  ["VARIANT", "Cấu hình biến thể"],
  ["BRAND", "Thương hiệu"],
  ["ATTRIBUTE", "Thuộc tính"],
  ["GROUP", "Nhóm sản phẩm"],
];

export default function WarehouseConfig({
  request,
  kind = "INVENTORY",
  editable = true,
}: {
  editable?: boolean;
  kind?: string;
  request: <T>(path: string, init?: RequestInit) => Promise<T>;
}) {
  const [tab, setTab] = useState(kind);
  const [rows, setRows] = useState<Entry[]>([]);
  const [form, setForm] = useState({ code: "", name: "", value: "" });  // code ẩn
  const [editing, setEditing] = useState(false);
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState("");

  const load = async () =>
    setRows(await request<Entry[]>("/api/v1/retail/warehouse-config"));

  useEffect(() => {
    load().catch((e) => setNotice(e.message));
  }, []);

  const save = async (entry: EntryPayload) => {
    setBusy(true);
    setNotice("");

    try {
      const res = await request<{ ok: boolean; code?: string }>(
        "/api/v1/retail/warehouse-config",
        {
          method: "POST",
          headers: { "Idempotency-Key": crypto.randomUUID() },
          body: JSON.stringify(entry),
        }
      );

      await load();
      setEditing(false);
      setForm({ code: "", name: "", value: "" });

      if (res.code && !entry.code) {
        setNotice(`Đã tạo mới với mã: ${res.code}`);
      } else {
        setNotice("Đã lưu cấu hình");
      }
    } catch (e) {
      setNotice(e instanceof Error ? e.message : "Không thể lưu");
    } finally {
      setBusy(false);
    }
  };

  const label = tabs.find((t) => t[0] === tab)?.[1];

  return (
    <section className="p-panel">
      <div className="p-modal-body" role="tabpanel" aria-label={label}>
        {notice && <p role="status">{notice}</p>}

        {tab === "INVENTORY" ? (
          <>
            <h2>Cài đặt tồn kho</h2>
            <p>
              Không ghi nhận tồn âm; phần thiếu chuyển sang đặt hàng trước. Hàng
              hết hạn bị chặn xuất bán. Lô được ưu tiên theo hạn sử dụng sớm
              nhất (FEFO).
            </p>

            <label>
              Kiểu theo dõi mặc định khi thêm sản phẩm
              <select
                aria-label="Kiểu theo dõi mặc định"
                value={
                  form.value ||
                  rows.find(
                    (r) =>
                      r.kind === "INVENTORY" && r.code === "defaultTracking"
                  )?.value ||
                  "NONE"
                }
                onChange={(e) => setForm({ ...form, value: e.target.value })}
              >
                <option value="NONE">Thông thường</option>
                <option value="LOT">Lô và hạn sử dụng</option>
                <option value="SERIAL">Serial</option>
              </select>
            </label>

            <button
              className="p-button primary"
              disabled={busy || !editable}
              onClick={() =>
                save({
                  kind: tab,
                  code: "defaultTracking",
                  name: "Kiểu theo dõi mặc định",
                  value:
                    form.value ||
                    rows.find((r) => r.kind === "INVENTORY")?.value ||
                    "NONE",
                  active: true,
                })
              }
            >
              Lưu cài đặt
            </button>
          </>
        ) : (
          <>
            <div className="p-panel-head">
              <h2>{label}</h2>
                <button
                className="p-button primary"
                disabled={!editable || editing}
                onClick={() => {
                    setEditing(true);
                    setForm({ code: "", name: "", value: "" });
                }}
                >
                {editing ? "Đang thêm..." : "Thêm mới"}
                </button>
            </div>

            <p>
              {tab === "UNIT"
                ? "Quản lý tên đơn vị. Tỷ lệ quy đổi được khai báo riêng trên từng sản phẩm."
                : tab === "VARIANT"
                ? "Khai báo chiều biến thể và các giá trị, ví dụ Màu: Đỏ, Xanh; Size: S, M, L."
                : tab === "GROUP"
                ? "Quản lý các nhóm để phân loại sản phẩm."
                : tab === "ATTRIBUTE"
                ? "Khai báo thuộc tính và các giá trị gợi ý, ví dụ Chất liệu: Cotton, Da."
                : "Danh sách thương hiệu dùng chung cho sản phẩm."}
            </p>

            {editing && (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  void save({
                    ...form,
                    code: form.code || null,  
                    kind: tab,
                    active: true,
                  });
                }}
              >
                <div className="p-form-two">
                  {[
                    ["name", "Tên"],
                    [
                      "value",
                      tab === "VARIANT" || tab === "ATTRIBUTE"
                        ? "Các giá trị (cách nhau bằng dấu phẩy)"
                        : "Ghi chú",
                    ],
                  ].map(([k, l]) => (
                    <label key={k}>
                      {l}
                      <input
                        aria-label={l}
                        required={k === "name"}
                        maxLength={k === "name" ? 160 : 1000}
                        value={form[k as keyof typeof form]}
                        onChange={(e) =>
                          setForm({ ...form, [k]: e.target.value })
                        }
                      />
                    </label>
                  ))}
                </div>

                <div className="r-actions">
                  <button
                    type="submit"
                    className="p-button primary"
                    disabled={busy || !editable}
                  >
                    Lưu
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setEditing(false);
                      setForm({ code: "", name: "", value: "" });
                    }}
                  >
                    Đóng
                  </button>
                </div>
              </form>
            )}

            <div className="warehouse-config-table-wrap"><table className="warehouse-config-table">
              <thead>
                <tr>
                  {[
                    "STT",
                    "Mã",
                    "Tên",
                    "Giá trị / Ghi chú",
                    "Trạng thái",
                    "Thao tác",
                  ].map((h) => (
                    <th key={h} scope="col">{h}</th>
                  ))}
                </tr>
              </thead>

              <tbody>
                {rows
                  .filter((r) => r.kind === tab)
                  .map((r, i) => (
                    <tr key={r.code}>
                      <td>{i + 1}</td>
                      <td>{r.code}</td>
                      <td>{r.name}</td>
                      <td>{r.value || "—"}</td>
                      <td>{r.active ? "Đang dùng" : "Ngừng dùng"}</td>
                      <td><div className="warehouse-config-actions">
                        <button
                          disabled={busy || !editable}
                          onClick={() => {
                            setForm({
                              code: r.code,     
                              name: r.name,
                              value: r.value,
                            });
                            setEditing(true);
                          }}
                        >
                          Sửa
                        </button>

                        <button
                          disabled={busy || !editable}
                          onClick={() => save({ ...r, active: !r.active })}
                        >
                          {r.active ? "Ngừng dùng" : "Kích hoạt"}
                        </button></div>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table></div>

            {!rows.some((r) => r.kind === tab) && (
              <p>Chưa có dữ liệu. Bấm Thêm mới để bắt đầu.</p>
            )}
          </>
        )}
      </div>
    </section>
  );
}