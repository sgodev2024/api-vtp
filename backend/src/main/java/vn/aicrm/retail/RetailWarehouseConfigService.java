package vn.aicrm.retail;

import static vn.aicrm.retail.RetailAccess.problem;
import static vn.aicrm.retail.RetailAccess.required;
import static vn.aicrm.retail.RetailAccess.text;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class RetailWarehouseConfigService {

    private static final Set<String> VALID_KINDS =
        Set.of("INVENTORY", "UNIT", "VARIANT", "BRAND", "ATTRIBUTE", "GROUP");

    private static final Map<String, String> CODE_PREFIX = Map.of(
        "GROUP",     "NSP-",
        "BRAND",     "TH-",
        "UNIT",      "DVT-",
        "ATTRIBUTE", "TT-",
        "VARIANT",   "CHBT-"
    );

    private static final Set<String> AUTO_GEN_KINDS = CODE_PREFIX.keySet();
    private final RetailAccess retailAccess;

    public RetailWarehouseConfigService(RetailAccess retailAccess) {
        this.retailAccess = retailAccess;
    }

    public  Object doSetupConfig(Authentication a, JsonNode x) {

        retailAccess.require(a, "SETUP");

        var kind = required(x, "kind", 30);
        if (!VALID_KINDS.contains(kind))
            throw problem("INVALID_CONFIG", "Loại cấu hình không hợp lệ");

        var code  = text(x, "code", "").trim();
        var name  = required(x, "name", 160);
        var value = text(x, "value", "");

        if (value.length() > 1000)
            throw problem("INVALID_CONFIG", "Nội dung tối đa 1000 ký tự");

        if (code.isEmpty() && AUTO_GEN_KINDS.contains(kind))
            code = generateCode(kind, retailAccess.tenant(a));

        if (code.isEmpty())
            throw problem("CODE_REQUIRED", "Mã không được để trống");

        if (code.length() > 60) throw problem("INVALID_FIELD", "Mã cấu hình tối đa 60 ký tự");
        if (kind.equals("INVENTORY")
                && (!code.equals("defaultTracking")
                    || !Set.of("NONE", "LOT", "SERIAL").contains(value)))
            throw problem("INVALID_CONFIG", "Kiểu theo dõi không hợp lệ");

        if (kind.equals("UNIT")) {
            if (name.length() > 40)
                throw problem("INVALID_UOM", "Tên đơn vị tối đa 40 ký tự");
            retailAccess.db.update(
                "insert into retail.uom(tenant_id,name) values(?,?) on conflict do nothing",
                retailAccess.tenant(a), name);
        }

        retailAccess.db.update(
            "insert into retail.warehouse_config(id,tenant_id,kind,code,name,value,active) "
          + "values(?,?,?,?,?,?,?) "
          + "on conflict(tenant_id,kind,code) do update "
          + "set name=excluded.name, value=excluded.value, active=excluded.active",
            UUID.randomUUID(), retailAccess.tenant(a), kind, code, name, value,
            x.path("active").asBoolean(true));

        return Map.of("ok", true, "code", code);
    }

    private String generateCode(String kind, UUID tenantId) {
        String prefix = CODE_PREFIX.get(kind);
        if (prefix == null) return null;

        for (int i = 0; i < 5; i++) {
            String raw  = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String code = prefix + raw.substring(0, 6);

            Integer exists = retailAccess.db.queryForObject(
                "select count(*) from retail.warehouse_config "
              + "where tenant_id=? and kind=? and code=?",
                Integer.class, tenantId, kind, code);

            if (exists == null || exists == 0) return code;
        }
        throw problem("CODE_GEN_FAILED", "Không sinh được mã duy nhất, thử lại");
    }
}