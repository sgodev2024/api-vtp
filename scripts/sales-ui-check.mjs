// Browser UI checks. All retail writes are intercepted; the operational DB is never seeded.
// Requires Playwright supplied via AICRM_PLAYWRIGHT_PATH and an installed Chromium browser.
import assert from "node:assert/strict";
import { createRequire } from "node:module";
import { readFile, mkdir, writeFile } from "node:fs/promises";
import { fileURLToPath } from "node:url";
const require = createRequire(import.meta.url);
const { chromium } = require(process.env.AICRM_PLAYWRIGHT_PATH || "playwright");
const base = process.env.AICRM_UI_URL || "http://localhost:3205";
const api = process.env.AICRM_API_URL || "http://localhost:8185";
const localEnv = await readFile(new URL("../.env", import.meta.url), "utf8").catch(() => "");
const password = process.env.AICRM_TEST_PASSWORD || localEnv.match(/^AICRM_BOOTSTRAP_PASSWORD=([^\r\n]+)/m)?.[1]?.trim();
assert(password, "Provide the local test password via environment or local compose configuration.");
const authResponse = await fetch(api + "/api/v1/auth/login", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ email: process.env.AICRM_TEST_EMAIL || "admin@core.local", password, remember: false }) });
assert.equal(authResponse.status, 200, "Local login failed");
const auth = (await authResponse.json()).session;
assert(auth.accessToken, "Expected an access token, never logged");
const browser = await chromium.launch({ headless: true, ...(process.env.AICRM_BROWSER_PATH ? { executablePath: process.env.AICRM_BROWSER_PATH } : { channel: "msedge" }) });
const output = new URL("../docs/screenshots/sales/", import.meta.url);
await mkdir(output, { recursive: true });
const checks = [], consoleErrors = [];
async function context(viewport) { const ctx = await browser.newContext({ viewport }); await ctx.addInitScript(({ token }) => { sessionStorage.setItem("core-access-token", token); }, { token: auth.accessToken }); return ctx; }
const id = suffix => `00000000-0000-4000-8000-${String(suffix).padStart(12, "0")}`;
const sku = { id: id(1), code: "TEST-AO-DO-M", barcode: "893000000001", name: "Áo kiểm thử", variant: "Đỏ / M", unit: "cái", units: { hộp: 2 }, active: true, tracking: "NONE", price: 50000, vat: 0 };
const wh = { id: id(2), name: "Kho kiểm thử", branch_id: id(3), active: true };
const customer = { id: id(4), name: "Khách kiểm thử", phone: "0900000000", kind: "CUSTOMER", active: true };
const serialSku = { ...sku, id: id(6), code: "TEST-SERIAL", name: "Máy kiểm thử", barcode: "893000000002", tracking: "SERIAL" };
const lotSku = { ...sku, id: id(7), code: "TEST-LOT", name: "Hàng lô kiểm thử", barcode: "893000000003", tracking: "LOT" };
const catalog = { warehouses: [wh], branches: [], skus: [sku, serialSku, lotSku], partners: [customer], priceRules: [], promotions: [], permissions: ["READ", "SELL", "FINANCE", "APPROVE", "EXPORT"], accountId: id(5) };
let documents = [], writes = [], failCreateOnce = true, balances = [{ warehouse_id: wh.id, sku_id: sku.id, available: 20 }];
const mkDoc = (payload, key) => ({ id: id(100 + documents.length), number: `${payload.type}-TEST-${documents.length + 1}`, type: payload.type, status: "DRAFT", version: 1, warehouse_id: wh.id, branch_id: wh.branch_id, warehouse: wh.name, partner_id: payload.partnerId, partner: customer.name, creator_id: catalog.accountId, channel: payload.channel, source_id: payload.sourceId, metadata: { address: payload.address || "", validUntil: payload.validUntil }, paid: Number(payload.paid || 0), fee: Number(payload.fee || 0), total: payload.lines.reduce((sum, line) => sum + Number(line.qty) * 50000, 0) + Number(payload.fee || 0), method: payload.method, created_at: "2026-10-05T02:00:00Z", posted_at: null, note: payload.note, key, openItems: [], lines: payload.lines.map((l, index) => ({ id: id(1000 + documents.length * 20 + index), sku_id: l.skuId, sku_snapshot: sku, qty: Number(l.qty), base_qty: Number(l.qty), ratio: 1, unit: l.unit, price: 50000, discount: 0, tax: 0, vat: 0, net: Number(l.qty) * 50000, total: Number(l.qty) * 50000, progress: 0, closed_qty: 0, serials: [], lot: "" })) });
async function mock(route) {
  const req = route.request(), url = new URL(req.url()), path = url.pathname.replace("/api/v1/retail", "");
  const send = (body, status = 200) => route.fulfill({ status, contentType: "application/json", body: JSON.stringify(body), headers: { "Access-Control-Allow-Origin": base } });
  if (req.method() === "OPTIONS") return send({});
  if (req.method() === "POST") {
    const payload = req.postDataJSON(), key = req.headers()["idempotency-key"];
    if (path === "/price") return send({ price: 50000, priceAvailable: true, effective: Number(payload.qty) === 4 ? 45000 : 50000, vat: 0, promotions: [] });
    writes.push({ path, key, payload });
    if (path === "/documents") {
      if (failCreateOnce) { failCreateOnce = false; return send({ detail: "Phản hồi lưu nháp bị gián đoạn (kiểm thử cô lập)." }, 503); }
      const existing = documents.find(d => d.key === key); if (existing) return send(existing);
      const d = mkDoc(payload, key); documents.push(d); return send(d);
    }
    const command = path.match(/^\/documents\/([^/]+)\/(submit|cancel)$/);
    if (command) {
      const d = documents.find(d => d.id === command[1]); assert(d); d.version++;
      d.status = command[2] === "cancel" ? "CANCELLED" : ["SALE_ORDER", "QUOTE"].includes(d.type) ? "APPROVED" : "POSTED";
      if (d.status === "POSTED") { d.posted_at = "2026-10-05T02:05:00Z"; d.openItems = [{ id: id(5000), kind: "AR", amount: d.total, allocated: d.paid, credit: 0, remaining: d.total - d.paid }]; }
      return send(d);
    }
    return send({ detail: "Unexpected retail write blocked by fixture" }, 422);
  }
  if (path === "/catalog") return send(catalog);
  if (path === "/stock") return send(balances);
  if (path === "/deliveries") return send([]);
  if (path === "/finance") return send({ payments: [], openItems: documents.flatMap(d => d.openItems) });
  if (path === "/documents") { const type = url.searchParams.get("type"), state = url.searchParams.get("status"), q = (url.searchParams.get("q") || "").toLowerCase(), p = Number(url.searchParams.get("page") || 0); const rows = documents.filter(d => (!type || d.type === type) && (!state || d.status === state) && (d.number.toLowerCase().includes(q) || d.partner.toLowerCase().includes(q))); return send({ items: rows.slice(p * 50, (p + 1) * 50), page: p, total: rows.length }); }
  if (path.startsWith("/documents/")) { const d = documents.find(d => d.id === path.split("/")[2]); return send(d || { detail: "Not found" }, d ? 200 : 404); }
  return send({ detail: "Unexpected retail read in fixture" }, 404);
}
try {
  // Read-only check against real local data: no business command is executed.
  const live = await context({ width: 1440, height: 1000 });
  await live.route("**/api/v1/retail/**", route => route.request().method() === "GET" ? route.continue() : route.abort());
  const livePage = await live.newPage();
  await livePage.goto(base + "/business/retail/pos");
  await livePage.getByRole("heading", { name: "Bán tại quầy", exact: true }).waitFor();
  await livePage.getByRole("button", { name: "Kiểm tra thanh toán", exact: true }).waitFor();
  await livePage.screenshot({ path: fileURLToPath(new URL("pos-local.png", output)), fullPage: true });
  checks.push("Real local POS loads without any retail writes");
  await live.close();

  const ctx = await context({ width: 1440, height: 1050 }); await ctx.route("**/api/v1/retail/**", mock);
  const page = await ctx.newPage(); page.on("pageerror", e => consoleErrors.push(e.message));
  await page.goto(base + "/business/retail/pos");
  await page.getByLabel("Tìm tên, mã SKU hoặc quét barcode").fill(sku.barcode);
  await page.getByLabel("Tìm tên, mã SKU hoặc quét barcode").press("Enter");
  await page.getByLabel("Số lượng", { exact: true }).fill("1");
  await page.getByRole("button", { name: "Tăng số lượng Áo kiểm thử", exact: true }).click();
  assert.equal(await page.getByLabel("Số lượng", { exact: true }).inputValue(), "2");
  await page.getByRole("button", { name: "Giảm số lượng Áo kiểm thử", exact: true }).click();
  assert.equal(await page.getByLabel("Số lượng", { exact: true }).inputValue(), "1");
  await page.locator(".pos-cart-line summary").click();
  await page.getByLabel("Đơn vị", { exact: true }).selectOption("hộp");
  await page.locator(".pos-total").getByText("100.000 ₫", { exact: true }).waitFor();
  await page.getByLabel("Đơn vị", { exact: true }).selectOption("cái");
  await page.locator(".pos-cart-line summary").click();
  await page.getByRole("button", { name: "Hết hàng", exact: true }).click();
  assert.equal(await page.locator(".pos-product").count(), 2);
  await page.getByRole("button", { name: "Tất cả", exact: true }).click();
  await page.getByRole("button", { name: "Thêm Máy kiểm thử", exact: false }).click();
  await page.getByLabel("Serial *", { exact: true }).fill("SN-001");
  await page.getByRole("button", { name: "Bỏ Máy kiểm thử", exact: true }).click();
  await page.getByRole("button", { name: "Thêm Hàng lô kiểm thử", exact: false }).click();
  const lotLine = page.locator(".pos-cart-line").filter({ hasText: lotSku.name });
  await lotLine.locator("summary").click();
  await page.getByLabel("Lô xuất", { exact: true }).fill("LOT-01");
  await page.getByRole("button", { name: "Bỏ Hàng lô kiểm thử", exact: true }).click();
  await page.keyboard.press("F2");
  assert(await page.getByLabel("Tìm tên, mã SKU hoặc quét barcode").evaluate(el => el === document.activeElement));
  await page.keyboard.press("F4");
  assert(await page.getByLabel("Khách hàng", { exact: true }).evaluate(el => el === document.activeElement));
  await page.getByRole("button", { name: "Thu đủ", exact: true }).click();
  assert.equal(await page.getByLabel("Thu ngay (₫)", { exact: true }).inputValue(), "50000");
  checks.push("POS barcode, stock filters, quantity controls, serial/lot fields, shortcuts and exact-payment helper work");
  await page.screenshot({ path: fileURLToPath(new URL("pos-fixture-cart-desktop.png", output)), fullPage: true });
  await page.setViewportSize({ width: 390, height: 844 });
  await page.waitForFunction(() => document.querySelector(".sidebar").getBoundingClientRect().right <= 1);
  assert((await page.locator(".sales-workspace").evaluate(el => el.scrollWidth - el.clientWidth)) <= 2);
  await page.screenshot({ path: fileURLToPath(new URL("pos-fixture-cart-mobile.png", output)), fullPage: true });
  checks.push("POS product grid and checkout render at 390px without root overflow");
  await page.setViewportSize({ width: 1440, height: 1050 });
  await page.getByLabel("Số lượng", { exact: true }).fill("4");
  await page.getByLabel("Khách hàng", { exact: true }).selectOption(customer.id);
  await page.getByLabel("Thu ngay (₫)", { exact: true }).fill("150000");
  await page.locator(".pos-total").getByText("180.000 ₫", { exact: true }).waitFor();
  await page.getByRole("button", { name: "Kiểm tra thanh toán", exact: true }).click();
  await page.getByRole("button", { name: "Thử lại cùng lần lưu", exact: true }).waitFor();
  assert(await page.getByLabel("Số lượng", { exact: true }).isDisabled());
  await page.getByRole("button", { name: "Thử lại cùng lần lưu", exact: true }).click();
  await page.getByRole("button", { name: "Xác nhận bán hàng", exact: true }).waitFor();
  await page.locator(".pos-total").getByText("200.000 ₫", { exact: true }).waitFor();
  assert.equal(await page.locator(".pos-line-main > strong").textContent(), "200.000 ₫");
  assert.equal(writes.filter(w => w.path === "/documents").length, 2);
  assert.equal(writes[0].key, writes[1].key, "Retry must retain Idempotency-Key");
  assert.deepEqual(writes[0].payload, writes[1].payload);
  assert.equal(documents.length, 1); assert.equal(documents[0].total, 200000);
  checks.push("Ambiguous create freezes inputs and replays the same key/payload");
  checks.push("Unit conversion preview is scaled; draft total and line amounts replace estimated prices with server values");
  await page.getByRole("button", { name: "Xác nhận bán hàng", exact: true }).click();
  await page.getByText("Nợ còn mở hiện tại:", { exact: false }).waitFor();
  assert.equal(documents[0].status, "POSTED"); assert.equal(documents[0].openItems[0].remaining, 50000);
  checks.push("POS draft review -> submit uses server total, debt 50,000, and selected customer");
  await page.screenshot({ path: fileURLToPath(new URL("pos-fixture-desktop.png", output)), fullPage: true });
  await page.getByRole("button", { name: "+ Phiếu bán mới", exact: true }).click();
  await page.getByRole("button", { name: "Lịch sử phiếu", exact: true }).click();
  await page.getByRole("button", { name: "Chi tiết", exact: true }).click();
  await page.getByRole("region", { name: "Chi tiết chứng từ", exact: true }).waitFor();
  await page.getByRole("button", { name: "Đóng chi tiết", exact: true }).click();
  await page.getByRole("button", { name: "Ẩn lịch sử", exact: true }).click();
  checks.push("POS history opens an existing receipt while the new cart remains available");
  await page.getByRole("button", { name: "Thêm Áo kiểm thử", exact: false }).click();
  await page.getByRole("button", { name: "Lập đơn đặt trước từ giỏ", exact: true }).click();
  await page.getByLabel("Khách hàng *", { exact: true }).selectOption(customer.id);
  await page.waitForFunction(() => document.querySelector('.s-line input[aria-label="Đơn giá"]')?.value === "50000");
  await page.getByRole("button", { name: "Lưu nháp & kiểm tra tiền", exact: true }).click();
  await page.getByRole("button", { name: "Xác nhận đơn bán hàng", exact: true }).click();
  assert.equal(documents.at(-1).type, "SALE_ORDER"); assert.equal(documents.at(-1).paid, 0);
  await page.getByRole("button", { name: "Xuất phần còn lại", exact: true }).waitFor();
  assert(await page.getByRole("button", { name: "Yêu cầu hủy phần chưa xuất", exact: true }).isDisabled());
  checks.push("Preorder deliberately converts cart to order without posting stock; approval cancellation is disabled");
  await page.getByRole("button", { name: "+ Phiếu bán mới", exact: true }).click();
  balances = [];
  await page.getByRole("button", { name: "Làm mới", exact: true }).click();
  await page.getByRole("button", { name: "Thêm Áo kiểm thử", exact: false }).click();
  await page.getByRole("alert").filter({ hasText: "Chưa đủ hàng tại kho đã chọn" }).waitFor();
  assert(await page.getByRole("button", { name: "Kiểm tra thanh toán", exact: true }).isDisabled());
  assert.match(await page.locator(".pos-stock-warning").textContent(), /thiếu 1 cái/);
  checks.push("Zero stock warns before draft creation and disables POS checkout");
  balances = [{ warehouse_id: wh.id, sku_id: sku.id, available: 1 }];
  await page.getByRole("button", { name: "Làm mới", exact: true }).click();
  await page.getByRole("button", { name: "Kiểm tra thanh toán", exact: true }).waitFor({ state: "visible" });
  await page.getByLabel("Khách hàng", { exact: true }).selectOption(customer.id);
  await page.getByRole("button", { name: "Kiểm tra thanh toán", exact: true }).click();
  await page.getByRole("button", { name: "Xác nhận bán hàng", exact: true }).waitFor();
  const shortageDraft = documents.at(-1);
  const submitCalls = writes.filter(w => w.path.endsWith("/submit")).length;
  balances = [];
  await page.getByRole("button", { name: "Xác nhận bán hàng", exact: true }).click();
  await page.getByRole("alert").filter({ hasText: "Chưa đủ tồn tại" }).waitFor();
  assert.equal(writes.filter(w => w.path.endsWith("/submit")).length, submitCalls);
  assert.equal(shortageDraft.status, "DRAFT");
  assert(await page.getByRole("button", { name: "Xác nhận bán hàng", exact: true }).isDisabled());
  await page.getByRole("button", { name: "Hủy nháp & chuyển giỏ đặt trước", exact: true }).click();
  await page.getByRole("button", { name: "Lưu nháp & kiểm tra tiền", exact: true }).waitFor();
  assert.equal(shortageDraft.status, "CANCELLED");
  assert.equal(await page.getByLabel("Số lượng", { exact: true }).inputValue(), "1");
  await page.getByRole("button", { name: "Lưu nháp & kiểm tra tiền", exact: true }).click();
  await page.getByRole("button", { name: "Xác nhận đơn bán hàng", exact: true }).click();
  assert.equal(documents.at(-1).type, "SALE_ORDER");
  assert.equal(documents.at(-1).paid, 0);
  checks.push("Stock changes after draft are detected before submit; explicit cancellation preserves cart for preorder without payment");
  await page.goto(base + "/business/retail/orders");
  await page.getByRole("heading", { name: "Đơn bán hàng", exact: true }).waitFor();
  await page.getByRole("tab", { name: "Báo giá", exact: true }).click();
  await page.getByText("Chưa có chứng từ phù hợp.", { exact: false }).waitFor();
  checks.push("Sales lists are filtered on server by document type and display empty state");
  await page.setViewportSize({ width: 390, height: 844 });
  await page.getByRole("button", { name: "+ Tạo đơn bán", exact: true }).click();
  await page.screenshot({ path: fileURLToPath(new URL("orders-fixture-mobile.png", output)), fullPage: true });
  const overflow = await page.locator(".sales-workspace").evaluate(el => el.scrollWidth - el.clientWidth);
  assert(overflow <= 2, `Sales root overflows mobile by ${overflow}px`);
  checks.push("Mobile 390px sales workspace has no root horizontal overflow");
  assert.deepEqual(consoleErrors, [], "No React/browser runtime errors");
  await ctx.close();
  await writeFile(new URL("../docs/sales-ui-check-result.json", import.meta.url), JSON.stringify({ checkedAt: new Date().toISOString(), mode: "real read-only + browser-intercepted retail fixture", checks, runtimeErrors: consoleErrors, operationalRetailWrites: 0 }, null, 2));
  console.log(JSON.stringify({ passed: checks.length, checks, operationalRetailWrites: 0 }, null, 2));
} finally { await browser.close(); }
