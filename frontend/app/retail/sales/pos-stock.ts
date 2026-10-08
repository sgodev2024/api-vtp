type Row = Record<string, unknown>;
type StockLine = { skuId: string; qty: string; unit: string; baseQty?: number };
export type StockShortage = { skuId: string; name: string; unit: string; requested: number; available: number; missing: number };

// Compare the combined demand of all lines in base units, including repeated serial rows.
// This is an early warning; the server still locks and validates stock when posting.
export function stockShortages(skus: Row[], stock: Row[], warehouseId: string, lines: StockLine[]): StockShortage[] {
  const requested = new Map<string, number>();
  for (const line of lines) {
    const sku = skus.find(s => String(s.id) === line.skuId);
    if (!sku) continue;
    const ratio = line.unit === String(sku.unit) ? 1 : Number(((sku.units ?? {}) as Row)[line.unit]);
    const baseQty = line.baseQty ?? Number(line.qty) * ratio;
    if (!Number.isFinite(baseQty) || baseQty <= 0) continue;
    requested.set(line.skuId, (requested.get(line.skuId) ?? 0) + Math.round(baseQty * 10000));
  }
  const result: StockShortage[] = [];
  for (const [skuId, scaledQty] of requested) {
    const sku = skus.find(s => String(s.id) === skuId)!;
    const balance = stock.filter(s => String(s.warehouse_id) === warehouseId && String(s.sku_id) === skuId).reduce((sum, s) => sum + Number(s.available ?? 0), 0);
    const scaledAvailable = Math.max(0, Math.round(balance * 10000));
    if (scaledQty > scaledAvailable) result.push({ skuId, name: String(sku.name), unit: String(sku.unit), requested: scaledQty / 10000, available: scaledAvailable / 10000, missing: (scaledQty - scaledAvailable) / 10000 });
  }
  return result;
}
