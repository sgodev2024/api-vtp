"use client";

import { useId, useRef, useState } from "react";
import { AppIcon } from "../components/app-icon";
import MoneyInput from "./money-input";
import type { Catalog, Doc, Header, Line, Row } from "./sales-workspace";
import "./order-editor.css";

type Props = {
  catalog: Catalog; customers: Row[]; header: Header; lines: Line[]; source: Doc | null;
  locked: boolean; prices: Record<string, Row>; pricesReady: boolean;
  available: (id: string) => number; addSku: (sku: Row) => void; removeLine: (key: string) => void;
  updateHeader: <K extends keyof Header>(key: K, value: Header[K]) => void;
  updateLine: (key: string, field: keyof Line, value: string) => void;
  addCustomer: () => void;
};
const text = (row: Row, key: string) => String(row[key] ?? "");
const number = (row: Row, key: string) => Number(row[key] ?? 0);
const money = (value: number) => new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 0 }).format(value) + " ₫";
const quantity = (value: number) => new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 4 }).format(value);

function ProductSelect({ skus, locked, available, addSku }: Pick<Props, "locked" | "available" | "addSku"> & { skus: Row[] }) {
  const id = useId(), input = useRef<HTMLInputElement>(null);
  const [query, setQuery] = useState(""), [open, setOpen] = useState(false), [active, setActive] = useState(-1), [notice, setNotice] = useState("");
  const search = query.trim().toLocaleLowerCase("vi-VN");
  const matching = skus.filter(sku => sku.active && ["name", "code", "barcode", "variant"].some(key => text(sku, key).toLocaleLowerCase("vi-VN").includes(search)));
  const rows = matching.slice(0, 60);
  const choose = (sku: Row) => { if (locked) return; addSku(sku); setQuery(""); setOpen(false); setActive(-1); setNotice(""); input.current?.focus(); };
  return <div className="oe-product-select" onBlur={event => { if (!event.currentTarget.contains(event.relatedTarget as Node | null)) { setOpen(false); setActive(-1); } }}>
    <label htmlFor={id}>Chọn sản phẩm</label>
    <div className="oe-search-control"><AppIcon name="search" size={17} /><input ref={input} id={id} role="combobox" aria-label="Tìm tên, mã SKU hoặc quét barcode" aria-expanded={open && !locked} aria-controls={id + "-results"} aria-autocomplete="list" aria-activedescendant={open && active >= 0 && rows[active] ? id + "-option-" + active : undefined} disabled={locked} autoComplete="off" value={query} placeholder="Tìm tên, mã SKU hoặc quét barcode…" onFocus={() => { if (!locked) setOpen(true); }} onChange={e => { setQuery(e.target.value); setOpen(true); setActive(-1); setNotice(""); }} onKeyDown={event => {
      if (event.key === "Escape") { event.preventDefault(); setOpen(false); setActive(-1); }
      if (event.key === "ArrowDown" || event.key === "ArrowUp") { event.preventDefault(); setOpen(true); setActive(index => event.key === "ArrowDown" ? Math.min(index + 1, rows.length - 1) : index <= 0 ? rows.length - 1 : index - 1); }
      if (event.key === "Enter") {
        event.preventDefault();
        if (open && active >= 0 && rows[active]) { choose(rows[active]); return; }
        const exact = skus.filter(sku => sku.active && ["barcode", "code"].some(key => text(sku, key) && text(sku, key).toLocaleLowerCase("vi-VN") === search));
        if (search && exact.length === 1) choose(exact[0]);
        else if (exact.length > 1) { setOpen(true); setNotice("Có nhiều sản phẩm cùng mã. Chọn đúng sản phẩm trong danh sách."); }
        else if (search && matching.length === 1) choose(matching[0]);
        else setOpen(true);
      }
    }} /><button type="button" disabled={locked} aria-label="Mở danh sách sản phẩm" aria-expanded={open && !locked} onClick={() => { input.current?.focus(); setOpen(!open); }}><AppIcon name="chevron-down" size={17} /></button></div>
    {open && !locked && <div className="oe-product-dropdown"><div role="listbox" aria-label="Kết quả tìm sản phẩm" id={id + "-results"}>{rows.map((sku, index) => <button role="option" aria-selected={index === active} id={id + "-option-" + index} className="s-product-result" tabIndex={-1} type="button" key={text(sku, "id")} onMouseDown={e => e.preventDefault()} onClick={() => choose(sku)}><span><strong>{text(sku, "name")}</strong><small>{text(sku, "code")} · {text(sku, "variant") || text(sku, "unit")}</small></span><span><b>{number(sku, "price") > 0 ? money(number(sku, "price")) : "Tra giá khi chọn"}</b><small>Tồn: {quantity(available(text(sku, "id")))} {text(sku, "unit")}</small></span></button>)}</div>{!rows.length && <p role="status">Không tìm thấy sản phẩm phù hợp.</p>}<small className="oe-dropdown-note">{notice || (matching.length > 60 ? "Hiển thị 60 kết quả. Nhập thêm để tìm chính xác." : "↑ ↓ chọn sản phẩm · Enter thêm vào phiếu · Esc đóng")}</small></div>}
  </div>;
}

export default function OrderEditor({ catalog, customers, header, lines, source, locked, prices, pricesReady, available, addSku, removeLine, updateHeader, updateLine, addCustomer }: Props) {
  const customerId = useId();
  const rows = lines.map(line => {
    const sku = catalog.skus.find(sku => text(sku, "id") === line.skuId) ?? {};
    const original = source?.lines.find(item => text(item, "id") === line.sourceLineId);
    const product = (original?.sku_snapshot as Row | undefined) ?? sku;
    const ratio = original ? number(original, "ratio") : line.unit === text(sku, "unit") ? 1 : Number(((sku.units ?? {}) as Row)[line.unit]);
    const automatic = !source && pricesReady && !!prices[line.key]?.priceAvailable;
    const unitPrice = automatic ? Math.round(number(prices[line.key], "price") * ratio * 10000) / 10000 : line.price;
    const ready = source ? line.price !== "" : pricesReady && Object.hasOwn(prices[line.key] ?? {}, "priceAvailable") && (automatic || line.price.trim() !== "");
    const gross = Math.round(Number(line.qty) * Number(unitPrice));
    const discount = automatic ? Math.round(Number(line.qty) * (number(prices[line.key], "price") - number(prices[line.key], "effective")) * ratio) : 0;
    const net = header.taxInclusive ? Math.round((gross - discount) / (1 + Number(line.vat || 0) / 100)) : gross - discount;
    const tax = header.taxInclusive ? gross - discount - net : Math.round(net * Number(line.vat || 0) / 100);
    const total = original ? Math.round(number(original, "net") * Number(line.qty) * ratio / number(original, "base_qty")) + Math.round(number(original, "tax") * Number(line.qty) * ratio / number(original, "base_qty")) : net + tax;
    const valid = ready && Number.isFinite(total) && Number(line.qty) > 0 && Number(unitPrice) >= 0 && Number(line.vat) >= 0 && Number(line.vat) <= 100;
    return { line, sku, product, unitPrice, automatic, valid, total, discount };
  });
  const estimate = rows.reduce((sum, row) => sum + (row.valid ? row.total : 0), 0);
  const complete = rows.length > 0 && rows.every(row => row.valid);
  return <div className="oe-editor">
    <div className="oe-header-grid"><label hidden={catalog.warehouses.filter(w=>w.active).length===1} className="oe-field">Kho thực hiện *<select aria-label="Kho thực hiện *" disabled={locked} value={header.warehouseId} onChange={e => updateHeader("warehouseId", e.target.value)}><option value="">Chọn kho</option>{catalog.warehouses.filter(w => w.active).map(w => <option key={text(w, "id")} value={text(w, "id")}>{text(w, "name")}</option>)}</select></label>
      <div className="oe-field"><label htmlFor={customerId}>Khách hàng *</label><div className="oe-customer-control"><select id={customerId} aria-label="Khách hàng *" disabled={locked || !!source} value={header.partnerId} onChange={e => updateHeader("partnerId", e.target.value)}><option value="">Chọn khách hàng</option>{customers.map(c => <option key={text(c, "id")} value={text(c, "id")}>{text(c, "name")}{text(c, "phone") ? " · " + text(c, "phone") : ""}</option>)}</select><AppIcon className="oe-customer-chevron" name="chevron-down" size={15} />{!source && (!catalog.capabilities || catalog.capabilities.customer?.includes("CREATE")) && <button type="button" aria-label="+ Thêm khách hàng" title="Thêm khách hàng" disabled={locked} onClick={addCustomer}><AppIcon name="plus" size={18} /></button>}</div></div>
      <label className="oe-field">Kênh bán<select aria-label="Kênh bán" disabled={locked || !!source} value={header.channel} onChange={e => updateHeader("channel", e.target.value)}>{[["COUNTER", "Tại quầy"], ["ONLINE", "Online nhập tay"], ["WHOLESALE", "Bán buôn"], ["COLLABORATOR", "Cộng tác viên"]].map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></label>
    </div>
    {!source && <ProductSelect skus={catalog.skus} locked={locked} available={available} addSku={addSku} />}
    <section className="oe-sheet" aria-label="Nội dung đơn bán"><div className="oe-sheet-heading"><h3>Hàng trên phiếu</h3><small>{lines.length} dòng</small></div>
      <div className="oe-table-scroll"><table className="oe-table" aria-label="Hàng trên phiếu"><colgroup>{["index", "product", "unit", "qty", "price", "vat", "total", "action"].map(key => <col key={key} className={"oe-col-" + key} />)}</colgroup><thead><tr>{["STT", "Sản phẩm", "ĐVT", "Số lượng", "Đơn giá", "VAT %", "Thành tiền", ""].map((heading, index) => <th key={index} scope="col">{heading || <span className="oe-sr-only">Thao tác</span>}</th>)}</tr></thead><tbody>
        {!rows.length && <tr><td colSpan={8} className="oe-empty">Tìm hoặc quét sản phẩm để thêm vào phiếu.</td></tr>}
        {rows.map(({ line, sku, product, unitPrice, automatic, valid, total, discount }, index) => <tr className="s-line oe-line" key={line.key}><td>{index + 1}</td><td><strong>{text(product, "name")}</strong><small>{text(product, "code")}{text(product, "variant") && " · " + text(product, "variant")}</small>{discount > 0 && <small className="oe-price-note">Ưu đãi: {money(discount)}</small>}{["LOT", "SERIAL"].includes(text(product, "tracking")) && <details className="oe-tracking"><summary>{text(product, "tracking") === "LOT" ? "Lô / hạn sử dụng" : "Serial"}</summary>{text(product, "tracking") === "LOT" ? <><label>Lô xuất<input aria-label="Lô xuất" disabled={locked} value={line.lot} placeholder="Tự chọn FEFO" onChange={e => updateLine(line.key, "lot", e.target.value)} /></label><label>Hạn sử dụng<input aria-label="Hạn sử dụng" disabled={locked} type="date" value={line.expiry} onChange={e => updateLine(line.key, "expiry", e.target.value)} /></label></> : <label>Serial<input aria-label="Serial *" disabled={locked} value={line.serials} placeholder="SN001|SN002" onChange={e => updateLine(line.key, "serials", e.target.value)} /></label>}</details>}</td>
          <td><select aria-label="Đơn vị" disabled={locked || !!source} value={line.unit} onChange={e => updateLine(line.key, "unit", e.target.value)}>{[...new Set([line.unit, text(sku, "unit"), ...Object.keys((sku.units ?? {}) as Row)])].filter(Boolean).map(unit => <option key={unit}>{unit}</option>)}</select></td>
          <td><input aria-label="Số lượng" disabled={locked} type="number" min="0.0001" step="0.0001" value={line.qty} onChange={e => updateLine(line.key, "qty", e.target.value)} /></td>
          <td><MoneyInput aria-label="Đơn giá" disabled={locked || !!source || !pricesReady || automatic} value={unitPrice} placeholder="Nhập giá" onChange={value => updateLine(line.key, "price", value)} /><small className="oe-price-note">{source ? "Theo nguồn" : !pricesReady ? "Đang tra giá…" : automatic ? "Tự động" : "Nhập thủ công"}</small></td>
          <td><input aria-label="VAT (%)" disabled={locked || !!source} type="number" min="0" max="100" step="0.0001" value={line.vat} onChange={e => updateLine(line.key, "vat", e.target.value)} /></td><td className="oe-amount">{valid ? money(total) : "—"}</td><td><button type="button" className="oe-remove" disabled={locked} aria-label={"Bỏ " + text(product, "name")} title="Bỏ dòng" onClick={() => removeLine(line.key)}><AppIcon name="x" size={15} /></button></td></tr>)}
      </tbody><tfoot><tr><td colSpan={6}>Tổng tạm tính <small>Đã gồm VAT và ưu đãi tự động</small></td><td className="oe-amount">{complete ? money(estimate) : "—"}</td><td /></tr></tfoot></table></div>
      <div className="oe-sheet-notes"><label className="oe-field">Địa chỉ giao<input aria-label="Địa chỉ giao" disabled={locked} value={header.address} onChange={e => updateHeader("address", e.target.value)} /></label><label className="oe-field">Ghi chú<input aria-label="Ghi chú" disabled={locked} value={header.note} onChange={e => updateHeader("note", e.target.value)} /></label></div>
      {!source && <label className="s-checkbox oe-tax"><input disabled={locked} type="checkbox" checked={header.taxInclusive} onChange={e => updateHeader("taxInclusive", e.target.checked)} /> Đơn giá đã bao gồm VAT</label>}
    </section>
  </div>;
}
