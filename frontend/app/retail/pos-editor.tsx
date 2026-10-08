"use client";

import { useEffect, useRef, useState } from "react";
import { AppIcon } from "../components/app-icon";
import MoneyInput from "./money-input";
import type { Catalog, Doc, Header, Line, Row } from "./sales-workspace";
import type { StockShortage } from "./pos-stock";
import "./pos.css";

type Request = <T>(path: string, init?: RequestInit) => Promise<T>;
type Props = {
  catalog: Catalog; customers: Row[]; header: Header; lines: Line[]; saved: Doc | null;
  busy: boolean; uncertain: boolean; request: Request; available: (id: string) => number;
  updateHeader: <K extends keyof Header>(key: K, value: Header[K]) => void;
  updateLine: (key: string, field: keyof Line, value: string) => void;
  addSku: (sku: Row) => void; removeLine: (key: string) => void;
  save: () => void; confirm: () => void; cancel: () => void; preorder: () => void;
  shortages: StockShortage[]; recoverPreorder: () => void;
};
const text = (row: Row, key: string) => String(row[key] ?? "");
const money = (value: number) => new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 0 }).format(value) + " ₫";
const quantity = (value: number) => new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 4 }).format(value);
const methods = [["CASH", "Tiền mặt", "database"], ["BANK", "Chuyển khoản", "refresh"], ["CARD", "Thẻ", "layers"], ["OTHER", "Khác", "apps"]];
const round = (value: number) => Math.round(value);

export default function PosEditor({ catalog, customers, header, lines, saved, busy, uncertain, request, available, updateHeader, updateLine, addSku, removeLine, save, confirm, cancel, preorder, shortages, recoverPreorder }: Props) {
  const [mobileStep,setMobileStep]=useState("products");
  const [query, setQuery] = useState(""), [filter, setFilter] = useState("all"), [scanMessage, setScanMessage] = useState("");
  const [prices, setPrices] = useState<{ key: string; values: Record<string, Row> }>({ key: "", values: {} }), [pricing, setPricing] = useState(false), [priceError, setPriceError] = useState("");
  const searchRef = useRef<HTMLInputElement>(null), customerRef = useRef<HTMLSelectElement>(null);
  const locked = busy || !!saved || uncertain;
  const priceKey = JSON.stringify({ warehouseId: header.warehouseId, partnerId: header.partnerId, channel: header.channel, lines: lines.map(l => ({ key: l.key, skuId: l.skuId, qty: l.qty, unit: l.unit })) });

  useEffect(() => {
    const handler = (event: KeyboardEvent) => {
      if (locked || document.querySelector('[role="dialog"]')) return;
      if (event.key === "F2" || event.key === "F4") { event.preventDefault(); (event.key === "F2" ? searchRef : customerRef).current?.focus(); }
    };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [locked]);

  useEffect(() => {
    let active = true;
    const context = JSON.parse(priceKey) as { warehouseId: string; partnerId: string; channel: string; lines: { key: string; skuId: string; qty: string; unit: string }[] };
    if (saved || !context.warehouseId || !context.lines.length) return;
    const timer = setTimeout(async () => {
      setPricing(true); setPriceError("");
      try {
        const result = await Promise.all(context.lines.map(async l => {
          const sku = catalog.skus.find(s => text(s, "id") === l.skuId) ?? {};
          const ratio = l.unit === text(sku, "unit") ? 1 : Number(((sku.units ?? {}) as Row)[l.unit]);
          if (!(Number(l.qty) > 0) || !(ratio > 0)) return [l.key, {}] as const;
          return [l.key, await request<Row>("/api/v1/retail/price", { method: "POST", body: JSON.stringify({ skuId: l.skuId, warehouseId: context.warehouseId, partnerId: context.partnerId || undefined, channel: context.channel, qty: Number(l.qty) * ratio }) })] as const;
        }));
        if (active) setPrices({ key: priceKey, values: Object.fromEntries(result) });
      } catch { if (active) setPriceError("Chưa tra được giá chính sách. Tổng chính xác sẽ được kiểm tra khi lưu phiếu."); }
      finally { if (active) setPricing(false); }
    }, 250);
    return () => { active = false; clearTimeout(timer); };
  }, [priceKey, saved, request, catalog.skus]);

  const activeProducts = catalog.skus.filter(s => s.active);
  const matches = activeProducts.filter(s => ["name", "code", "barcode", "variant"].some(k => text(s, k).toLowerCase().includes(query.trim().toLowerCase())) && (filter === "all" || (filter === "stock" ? available(text(s, "id")) > 0 : available(text(s, "id")) <= 0)));
  const estimates = lines.map((l, index) => {
    const savedLine = saved?.lines[index];
    if (savedLine) return { key: l.key, price: Number(savedLine.price), gross: round(Number(savedLine.qty) * Number(savedLine.price)), discount: Number(savedLine.discount), tax: Number(savedLine.tax), total: Number(savedLine.total) };
    const sku = catalog.skus.find(s => text(s, "id") === l.skuId) ?? {};
    const ratio = l.unit === text(sku, "unit") ? 1 : Number(((sku.units ?? {}) as Row)[l.unit] ?? 1);
    const policy = prices.key === priceKey ? prices.values[l.key] ?? {} : {};
    const basePrice = Number(policy.price ?? sku.price ?? 0) * ratio;
    const price = l.price === "" ? basePrice : Number(l.price);
    const gross = round(Number(l.qty || 0) * price);
    const discount = l.discount === "" ? round(Number(l.qty || 0) * (Number(policy.price ?? sku.price ?? 0) - Number(policy.effective ?? policy.price ?? sku.price ?? 0)) * ratio) : Number(l.discount);
    const afterDiscount = Math.max(0, gross - discount), vat = Number(l.vat || 0);
    const net = header.taxInclusive ? round(afterDiscount / (1 + vat / 100)) : afterDiscount;
    const tax = header.taxInclusive ? afterDiscount - net : round(net * vat / 100);
    return { key: l.key, price, gross, discount, tax, total: net + tax };
  });
  const subtotal = estimates.reduce((sum, l) => sum + l.gross, 0), discount = estimates.reduce((sum, l) => sum + l.discount, 0), tax = estimates.reduce((sum, l) => sum + l.tax, 0);
  const estimateTotal = estimates.reduce((sum, l) => sum + l.total, 0) + Number(header.fee || 0);
  const total = saved ? Number(saved.total) : estimateTotal;
  const paid = saved ? Number(saved.paid) : Number(header.paid || 0);
  const remaining = Math.max(0, total - paid);
  const awaitingPrices = !saved && !!header.warehouseId && lines.length > 0 && prices.key !== priceKey && !priceError;

  return <div className="pos-layout" data-mobile-step={mobileStep}><nav className="mobile-pos-steps" aria-label="Các bước bán tại quầy">{[["products","Chọn hàng"],["cart","Giỏ hàng"],["payment","Thanh toán"]].map(([key,label])=><button key={key} type="button" aria-current={mobileStep===key?"step":undefined} onClick={()=>setMobileStep(key)}>{label}{key==="cart"?" ("+lines.length+")":""}</button>)}</nav>
    <section className="pos-catalog" aria-label="Chọn sản phẩm">
      <div className="pos-catalog-head"><div><h2>Sản phẩm</h2><small>{activeProducts.length} mặt hàng đang kinh doanh</small></div><span className="pos-live"><span /> Bán tại quầy</span></div>
      <fieldset disabled={locked} className="pos-catalog-controls">
        <form className="pos-search" onSubmit={e => {
          e.preventDefault(); const exact = activeProducts.filter(s => ["barcode", "code"].some(k => text(s, k) && text(s, k).toLowerCase() === query.trim().toLowerCase()));
          if (exact.length === 1) { addSku(exact[0]); setQuery(""); setScanMessage("Đã thêm " + text(exact[0], "name")); }
          else setScanMessage(exact.length > 1 ? "Mã trùng nhiều sản phẩm. Chọn đúng mặt hàng bên dưới." : "Chọn sản phẩm trong kết quả tìm kiếm.");
        }}><AppIcon name="search" size={20} /><input ref={searchRef} type="search" aria-label="Tìm tên, mã SKU hoặc quét barcode" placeholder="Tìm sản phẩm hoặc quét mã vạch…" value={query} onChange={e => { setQuery(e.target.value); setScanMessage(""); }} /><kbd>F2</kbd></form>
        <div className="pos-filters" role="group" aria-label="Lọc sản phẩm">{[["all", "Tất cả"], ["stock", "Còn hàng"], ["empty", "Hết hàng"]].map(([key, label]) => <button key={key} type="button" aria-pressed={filter === key} onClick={() => setFilter(key)}>{label}</button>)}<small>{matches.length} kết quả</small></div>
        {scanMessage && <p className="pos-scan-status" role="status">{scanMessage}</p>}
        <div className="pos-products">{matches.slice(0, 60).map((sku, index) => {
          const count = available(text(sku, "id")), inCart = lines.filter(l => l.skuId === text(sku, "id")).length;
          return <button type="button" className={`pos-product pos-tone-${index % 4}`} key={text(sku, "id")} onClick={() => { addSku(sku); setScanMessage(""); }} aria-label={"Thêm " + text(sku, "name") + (text(sku, "variant") ? " · " + text(sku, "variant") : "")}>
            <div className="pos-product-art"><AppIcon name={text(sku, "tracking") === "SERIAL" ? "cpu" : text(sku, "tracking") === "LOT" ? "layers" : "modules"} size={32} />{inCart > 0 && <span className="pos-in-cart">{inCart} dòng trong giỏ</span>}<span className="pos-product-add"><AppIcon name="plus" size={15} /></span></div>
            <div className="pos-product-copy"><small className="pos-sku-code">{text(sku, "code")}</small><strong>{text(sku, "name")}</strong><span className="pos-variant">{text(sku, "variant") || text(sku, "unit")}</span><div className="pos-product-bottom"><b>{money(Number(sku.price ?? 0))}</b><span className={count > 0 ? "pos-in-stock" : "pos-out-stock"}>{quantity(count)} {text(sku, "unit")}</span></div></div>
          </button>;
        })}</div>
        {!matches.length && <div className="pos-catalog-empty"><AppIcon name="search" size={36} /><h3>{activeProducts.length ? "Không tìm thấy sản phẩm" : "Danh mục đang trống"}</h3><p>{activeProducts.length ? "Thử tên, mã hàng hoặc đổi bộ lọc." : "Thiết lập hàng hóa tại module Sản phẩm để bắt đầu bán."}</p></div>}
      </fieldset>
      <footer className="pos-catalog-footer"><span><AppIcon name="shield" size={14} /> Giá và tồn được kiểm tra khi xác nhận</span><small>{matches.length > 60 ? "Hiển thị 60 kết quả. Tìm cụ thể để chọn nhanh." : "Giá niêm yết · Tồn tại kho đã chọn, có thể gồm lô hết hạn"}</small></footer>
    </section>

    <aside className="pos-ticket" aria-label="Giỏ hàng và thanh toán">
      <header className="pos-ticket-head"><div><AppIcon name="file" size={19} /><h2>{saved ? text(saved, "number") : "Phiếu bán hiện tại"}</h2></div><span>{lines.length} dòng</span></header>
      <fieldset className="pos-ticket-fields" disabled={locked}>
        <div className="pos-customer"><AppIcon name="users" size={18} /><select ref={customerRef} aria-label="Khách hàng" value={header.partnerId} onChange={e => updateHeader("partnerId", e.target.value)}><option value="">Khách vãng lai</option>{customers.map(c => <option value={text(c, "id")} key={text(c, "id")}>{text(c, "name")} · {text(c, "phone")}</option>)}</select><kbd>F4</kbd></div>
        <label hidden={catalog.warehouses.filter(w=>w.active).length===1} className="pos-warehouse"><AppIcon name="building" size={15} /><span>Kho bán</span><select aria-label="Kho thực hiện *" value={header.warehouseId} onChange={e => updateHeader("warehouseId", e.target.value)}><option value="">Chọn kho</option>{catalog.warehouses.filter(w => w.active).map(w => <option value={text(w, "id")} key={text(w, "id")}>{text(w, "name")}</option>)}</select></label>
        <div className="pos-cart" aria-label="Hàng trên phiếu">
          {!lines.length && <div className="pos-cart-empty"><span><AppIcon name="file" size={30} /></span><h3>Giỏ hàng chưa có sản phẩm</h3><p>Chọn hàng bên trái hoặc quét mã vạch để thêm vào phiếu.</p></div>}
          {lines.map((line, index) => {
            const sku = catalog.skus.find(s => text(s, "id") === line.skuId) ?? {}, estimate = estimates[index];
            const units = [...new Set([line.unit, text(sku, "unit"), ...Object.keys((sku.units ?? {}) as Row)])].filter(Boolean);
            return <article className="pos-cart-line" key={line.key}>
              <div className="pos-line-title"><span className="pos-line-index">{index + 1}</span><div><strong>{text(sku, "name")}</strong><small>{text(sku, "code")}{text(sku, "variant") && " · " + text(sku, "variant")}</small></div><button type="button" className="pos-remove" onClick={() => removeLine(line.key)} aria-label={"Bỏ " + text(sku, "name")}><AppIcon name="x" size={15} /></button></div>
              <div className="pos-line-main"><div className="pos-qty"><button type="button" aria-label={"Giảm số lượng " + text(sku, "name")} disabled={Number(line.qty) <= 1} onClick={() => updateLine(line.key, "qty", String(Math.max(0.0001, Number((Number(line.qty) - 1).toFixed(4)))))}>−</button><input aria-label="Số lượng" type="number" min="0.0001" step="0.0001" value={line.qty} onChange={e => updateLine(line.key, "qty", e.target.value)} /><button type="button" aria-label={"Tăng số lượng " + text(sku, "name")} onClick={() => updateLine(line.key, "qty", String(Number((Number(line.qty) + 1).toFixed(4))))}>+</button></div><span className="pos-line-price">{money(estimate.price)} / {line.unit}</span><strong>{money(estimate.total)}</strong></div>
              <details className="pos-line-details" open={text(sku, "tracking") === "SERIAL" ? true : undefined}><summary>Đơn vị, giá &amp; {text(sku, "tracking") === "SERIAL" ? "serial" : text(sku, "tracking") === "LOT" ? "lô hàng" : "ưu đãi"}</summary><div className="pos-detail-grid"><label>Đơn vị<select aria-label="Đơn vị" value={line.unit} onChange={e => updateLine(line.key, "unit", e.target.value)}>{units.map(unit => <option key={unit}>{unit}</option>)}</select></label><label>Đơn giá<MoneyInput aria-label="Đơn giá" placeholder="Theo bảng giá" value={line.price} onChange={value => updateLine(line.key, "price", value)} /></label><label>Giảm dòng (₫)<MoneyInput aria-label="Giảm dòng (₫)" placeholder="Theo ưu đãi" value={line.discount} onChange={value => updateLine(line.key, "discount", value)} /></label><label>VAT (%)<input aria-label="VAT (%)" type="number" min="0" max="100" step="0.0001" value={line.vat} onChange={e => updateLine(line.key, "vat", e.target.value)} /></label></div>
                {text(sku, "tracking") === "LOT" && <div className="pos-detail-grid"><label>Lô xuất<input aria-label="Lô xuất" placeholder="Tự chọn hạn sớm (FEFO)" value={line.lot} onChange={e => updateLine(line.key, "lot", e.target.value)} /></label><label>Hạn sử dụng<input aria-label="Hạn sử dụng" type="date" value={line.expiry} onChange={e => updateLine(line.key, "expiry", e.target.value)} /></label></div>}
                {text(sku, "tracking") === "SERIAL" && <label className="pos-serial">Serial *<input aria-label="Serial *" placeholder="SN001|SN002" value={line.serials} onChange={e => updateLine(line.key, "serials", e.target.value)} /><small>Mỗi đơn vị cơ sở một serial; ngăn bằng dấu |</small></label>}
              </details>
            </article>;
          })}
        </div>
        <details className="pos-note"><summary><AppIcon name="sliders" size={15} /> Ghi chú, phí &amp; thuế</summary><div className="pos-detail-grid"><label>Phí giao khách trả (₫)<MoneyInput aria-label="Phí giao khách trả (₫)" value={header.fee} onChange={value => updateHeader("fee", value)} /></label><label>Kênh bán<select aria-label="Kênh bán" value={header.channel} onChange={e => updateHeader("channel", e.target.value)}>{[["COUNTER", "Tại quầy"], ["ONLINE", "Online nhập tay"], ["WHOLESALE", "Bán buôn"], ["COLLABORATOR", "Cộng tác viên"]].map(([key, label]) => <option key={key} value={key}>{label}</option>)}</select></label></div><label className="pos-note-text">Ghi chú<textarea aria-label="Ghi chú" value={header.note} onChange={e => updateHeader("note", e.target.value)} placeholder="Ghi chú cho phiếu bán…" /></label><label className="pos-tax"><input type="checkbox" checked={header.taxInclusive} onChange={e => updateHeader("taxInclusive", e.target.checked)} /> Đơn giá đã bao gồm VAT</label></details>
      </fieldset>

      <div className="pos-checkout">
        {shortages.length > 0 && <section className="pos-stock-warning" aria-label="Hàng thiếu tồn" role="alert"><strong>Chưa đủ hàng tại kho đã chọn</strong>{shortages.map(s => <p key={s.skuId}>{s.name}: cần {quantity(s.requested)}, còn {quantity(s.available)}, thiếu <b>{quantity(s.missing)} {s.unit}</b>.</p>)}<small>{saved ? "Nháp chưa xuất hàng hoặc thu tiền. Có thể bổ sung tồn rồi tải lại, hoặc hủy nháp và chuyển toàn bộ giỏ sang đơn đặt trước." : "Chọn kho có hàng, giảm số lượng, nhập tồn đã được duyệt hoặc lập đơn đặt trước. Không ghi tồn âm."}</small>{saved && <button type="button" disabled={busy} onClick={recoverPreorder}>Hủy nháp &amp; chuyển giỏ đặt trước</button>}</section>}
        <div className="pos-totals"><div><span>Tiền hàng</span><span>{money(subtotal)}</span></div>{discount > 0 && <div><span>Giảm giá</span><span>−{money(discount)}</span></div>}{tax > 0 && <div><span>VAT {header.taxInclusive ? "(đã gồm trong giá)" : ""}</span><span>{money(tax)}</span></div>}{Number(header.fee) > 0 && <div><span>Phí giao hàng</span><span>{money(Number(header.fee))}</span></div>}<div className="pos-total"><span>{saved ? "Khách cần trả" : "Tổng tạm tính"}</span><strong>{money(total)}</strong></div></div>
        {!saved && <><fieldset disabled={locked || !!catalog.capabilities && !catalog.capabilities.pos?.includes("COLLECT_POS")} className="pos-payment"><div className="pos-methods" role="group" aria-label="Phương thức thanh toán">{methods.map(([key, label, icon]) => <button key={key} aria-pressed={header.method === key} type="button" onClick={() => updateHeader("method", key)}><AppIcon name={icon} size={17} />{label}</button>)}</div><div className="pos-paid"><label>Thu ngay (₫)<MoneyInput aria-label="Thu ngay (₫)" value={header.paid} onChange={value => updateHeader("paid", value)} /></label><button type="button" disabled={!lines.length || pricing || awaitingPrices} onClick={() => updateHeader("paid", String(Math.max(0, total)))}>Thu đủ</button></div><div className="pos-debt"><span>Còn phải thu</span><b>{money(remaining)}</b></div>{paid > total && <p className="pos-warning">Tiền thu vượt tổng tạm tính. Kiểm tra lại trước khi lưu.</p>}{remaining > 0 && !header.partnerId && <p className="pos-warning">Chọn khách hàng nếu bán còn nợ.</p>}</fieldset><button className="s-primary pos-pay-button" disabled={busy || !lines.length || !header.warehouseId || (!uncertain && shortages.length > 0)} onClick={save}><span>{busy ? "Đang xử lý…" : uncertain ? "Thử lại cùng lần lưu" : "Kiểm tra thanh toán"}</span><AppIcon name="chevron-right" size={19} /></button><button className="pos-preorder" disabled={locked || !lines.length} onClick={preorder}>Lập đơn đặt trước từ giỏ</button></>}
        {saved && <div className="pos-review"><span className="s-badge s-badge-draft">Đã lưu nháp</span><p>Thu ngay: <b>{money(paid)}</b> · Còn phải thu: <b>{money(remaining)}</b></p><label>Nội dung xác nhận / lý do ngoại lệ<input aria-label="Nội dung xác nhận / lý do ngoại lệ" disabled={busy} value={header.reason} onChange={e => updateHeader("reason", e.target.value)} /></label><button className="s-primary pos-pay-button" disabled={busy || shortages.length > 0} onClick={confirm}>{busy ? "Đang xác nhận…" : "Xác nhận bán hàng"}<AppIcon name="check-circle" size={19} /></button><button className="pos-preorder" disabled={busy} onClick={cancel}>Hủy nháp để lập lại</button></div>}
        <p className="pos-checkout-hint">{uncertain ? "Kết quả lưu chưa rõ. Thử lại cùng lần lưu để tránh tạo trùng phiếu." : saved ? "Tổng đã được máy chủ kiểm tra. Xác nhận để bán hoặc gửi duyệt ngoại lệ." : "Tổng tạm tính. Kiểm tra thanh toán để lưu nháp, sau đó xác nhận bán."}</p>
        {!saved && (pricing || awaitingPrices || priceError) && <p className="pos-pricing-status" role="status">{pricing || awaitingPrices ? "Đang tra giá theo khách hàng và số lượng…" : priceError}</p>}
      </div>
    </aside>
  <div className="mobile-pos-total"><div><span>{lines.length} dòng · Tổng tạm tính</span><strong>{money(total)}</strong></div><button type="button" onClick={()=>setMobileStep(mobileStep==="products"?"cart":"payment")}>{mobileStep==="products"?"Xem giỏ":"Thanh toán"}</button></div></div>;
}
