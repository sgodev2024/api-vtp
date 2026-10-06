"use client";

import { useEffect, useRef, useState } from "react";
import { AppIcon } from "../components/app-icon";

type Row = Record<string, unknown>;
type Props = { form: Row; owners: Row[]; busy: boolean; error: string; finance: boolean; update: (key: string, value: unknown) => void; onSave: () => void; onClose: () => void };
const text = (row: Row, key: string) => String(row[key] ?? "");
const tabs = [["general", "Thông tin chung"], ["additional", "Thông tin bổ sung"], ["payment", "Thông tin thanh toán"]];
const requiredFields = [["code", "Mã NCC"], ["name", "Tên NCC"], ["entityType", "Loại đối tượng"], ["phone", "Điện thoại"], ["email", "Email"], ["address", "Địa chỉ"]];

export default function SupplierDialog({ form, owners, busy, error, finance, update, onSave, onClose }: Props) {
  const [tab, setTab] = useState("general"), [validation, setValidation] = useState("");
  const dialogRef = useRef<HTMLDivElement>(null), invalidRef = useRef<string | null>(null);
  useEffect(() => {
    const prior = document.activeElement as HTMLElement | null;
    dialogRef.current?.querySelector<HTMLInputElement>('input[name="code"]')?.focus();
    return () => prior?.focus();
  }, []);
  useEffect(() => {
    if (invalidRef.current) { dialogRef.current?.querySelector<HTMLElement>(`[name="${invalidRef.current}"]`)?.focus(); invalidRef.current = null; }
  }, [tab, validation]);
  const input = (key: string, label: string, required = false, max = 160, type = "text") => <label key={key}><span>{label}{required && <span className="ncc-required"> *</span>}</span><input name={key} aria-label={key === "companyAddress" ? "Địa chỉ công ty" : label} required={required} maxLength={max} type={type} min={type === "number" ? 0 : undefined} max={type === "number" ? 3650 : undefined} step={type === "number" ? 1 : undefined} value={text(form, key)} onChange={e => { update(key, e.target.value); setValidation(""); }} /></label>;
  const validate = () => {
    const empty = requiredFields.find(([key]) => !text(form, key).trim());
    if (empty) { invalidRef.current = empty[0]; setTab("general"); setValidation("Vui lòng nhập " + empty[1] + "."); return false; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(text(form, "email").trim())) { invalidRef.current = "email"; setTab("general"); setValidation("Email không hợp lệ."); return false; }
    const days = text(form, "purchaseDays");
    if (days && (!/^\d{1,4}$/.test(days) || Number(days) > 3650)) { invalidRef.current = "purchaseDays"; setTab("payment"); setValidation("Thời gian công nợ phải là số ngày từ 0 đến 3650, hoặc để trống."); return false; }
    return true;
  };

  return <div className="ncc-backdrop" onKeyDown={e => {
    if (e.key === "Escape" && !busy) { e.stopPropagation(); onClose(); }
    if (e.key === "Tab") {
      const elements = Array.from(dialogRef.current?.querySelectorAll<HTMLElement>('button:not([disabled]),input:not([disabled]),select:not([disabled]),[tabindex="0"]') ?? []).filter(el => el.getClientRects().length > 0);
      if (!elements.length) { e.preventDefault(); return; }
      const first = elements[0], last = elements[elements.length - 1];
      if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    }
  }}><div ref={dialogRef} className="ncc-dialog" role="dialog" aria-modal="true" aria-labelledby="ncc-dialog-title" tabIndex={-1}>
    <header className="ncc-dialog-head"><div><small>MUA HÀNG · NHÀ CUNG CẤP</small><h2 id="ncc-dialog-title">{form.id ? "Sửa NCC" : "Thêm NCC"}</h2><p>Thông tin chung bắt buộc. Có thể bổ sung các thông tin khác sau.</p></div><button type="button" aria-label="Đóng popup NCC" disabled={busy} onClick={onClose}><AppIcon name="x" size={19} /></button></header>
    <div className="ncc-tabs" role="tablist" aria-label="Thông tin NCC" onKeyDown={e => {
      if (!["ArrowRight", "ArrowLeft", "Home", "End"].includes(e.key) || busy) return;
      e.preventDefault(); const index = tabs.findIndex(([key]) => key === tab);
      const next = e.key === "Home" ? 0 : e.key === "End" ? tabs.length - 1 : (index + (e.key === "ArrowRight" ? 1 : -1) + tabs.length) % tabs.length;
      setTab(tabs[next][0]); dialogRef.current?.querySelector<HTMLButtonElement>(`#ncc-tab-${tabs[next][0]}`)?.focus();
    }}>{tabs.map(([key, label]) => <button key={key} type="button" id={"ncc-tab-" + key} role="tab" aria-selected={tab === key} aria-controls={"ncc-panel-" + key} tabIndex={tab === key ? 0 : -1} disabled={busy} onClick={() => setTab(key)}>{label}</button>)}</div>
    <form noValidate onSubmit={e => { e.preventDefault(); if (validate()) onSave(); }}>
      <div className="ncc-dialog-body">{(validation || error) && <div className="pm-error ncc-error" role="alert">{validation || error}</div>}<fieldset disabled={busy}>
        <div role="tabpanel" id="ncc-panel-general" aria-labelledby="ncc-tab-general" hidden={tab !== "general"}><div className="ncc-grid">{input("code", "Mã NCC", true, 60)}{input("name", "Tên NCC", true)}<label><span>Loại đối tượng<span className="ncc-required"> *</span></span><select name="entityType" aria-label="Loại đối tượng" required value={text(form, "entityType")} onChange={e => update("entityType", e.target.value)}><option value="">Chọn loại đối tượng</option><option value="PERSON">Cá nhân</option><option value="BUSINESS">Doanh nghiệp</option></select></label>{input("phone", "Điện thoại", true, 60, "tel")}{input("email", "Email", true, 160, "email")}{input("address", "Địa chỉ", true, 2000)}</div></div>
        <div role="tabpanel" id="ncc-panel-additional" aria-labelledby="ncc-tab-additional" hidden={tab !== "additional"}><p className="ncc-optional">Các trường trong tab này không bắt buộc.</p><div className="ncc-grid">{input("companyName", "Tên công ty")}{input("companyAddress", "Địa chỉ", false, 2000)}{input("taxCode", "Mã số thuế", false, 30)}{input("contactName", "Người liên hệ")}<label>Người quản lý<select name="ownerId" aria-label="Người quản lý" value={text(form, "ownerId")} onChange={e => update("ownerId", e.target.value)}><option value="">Chưa phân công</option>{owners.map(owner => <option key={text(owner, "id")} value={text(owner, "id")}>{text(owner, "display_name")}</option>)}</select></label></div></div>
        <div role="tabpanel" id="ncc-panel-payment" aria-labelledby="ncc-tab-payment" hidden={tab !== "payment"}><p className="ncc-optional">Các trường trong tab này không bắt buộc. Thời gian công nợ tính bằng ngày.</p>{!finance && <p className="ncc-permission">Cần quyền Kế toán để xem hoặc thay đổi thông tin ngân hàng.</p>}<fieldset disabled={!finance || busy}><div className="ncc-grid">{input("bankName", "Ngân hàng")}{input("bankAccount", "Số tài khoản", false, 80)}{input("bankHolder", "Chủ tài khoản")}</div></fieldset><div className="ncc-grid">{input("purchaseDays", "Thời gian công nợ", false, 4, "number")}</div></div>
      </fieldset></div>
      <footer className="ncc-dialog-footer"><small>Lưu thông tin NCC chưa phát sinh công nợ.</small><div><button type="button" disabled={busy} onClick={onClose}>Đóng</button><button type="submit" className="pm-primary" disabled={busy}>{busy ? "Đang lưu…" : "Lưu NCC"}</button></div></footer>
    </form>
  </div></div>;
}
