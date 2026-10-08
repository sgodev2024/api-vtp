package vn.coreplatform.retail;
import org.junit.jupiter.api.Test;
import vn.coreplatform.AbstractApiTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
class CatalogMergeTest extends AbstractApiTest {
  @Test void generatedSkuIsIdempotent() throws Exception {
    var token=adminToken(); var key="sku-"+suffix();
    var body="{\"name\":\"Merge test\",\"unit\":\"cái\",\"code\":null}";
    var first=mvc.perform(post("/api/v1/retail/catalog/skus").with(bearer(token)).header("Idempotency-Key",key).contentType(APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    var again=mvc.perform(post("/api/v1/retail/catalog/skus").with(bearer(token)).header("Idempotency-Key",key).contentType(APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    assertEquals(json.readTree(first),json.readTree(again));
    assertTrue(json.readTree(first).path("code").asText().matches("SP-[A-F0-9]{6}"));
  }
  @Test void generatedConfigCanBeUpdated() throws Exception {
    var token=adminToken();
    var response=mvc.perform(post("/api/v1/retail/warehouse-config").with(bearer(token)).header("Idempotency-Key","group-"+suffix()).contentType(APPLICATION_JSON).content("{\"kind\":\"GROUP\",\"name\":\"Test group\",\"code\":null}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    var code=json.readTree(response).path("code").asText(); assertTrue(code.matches("NSP-[A-F0-9]{6}"));
    mvc.perform(post("/api/v1/retail/warehouse-config").with(bearer(token)).header("Idempotency-Key","update-"+suffix()).contentType(APPLICATION_JSON).content(json.createObjectNode().put("kind","GROUP").put("code",code).put("name","Updated group").toString())).andExpect(status().isOk());
    assertEquals(1,jdbc.queryForObject("select count(*) from retail.warehouse_config where code=? and name='Updated group'",Integer.class,code));
  }
  @Test void oversizedCodesAreRejected() throws Exception {
    var token=adminToken();
    for(var path:new String[]{"catalog/skus","warehouse-config"}) {
      var body=json.createObjectNode().put("code","X".repeat(61)).put("name","Test").put("unit","cái").put("kind","GROUP");
      mvc.perform(post("/api/v1/retail/"+path).with(bearer(token)).header("Idempotency-Key","invalid-"+suffix()).contentType(APPLICATION_JSON).content(body.toString())).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.title").value("INVALID_FIELD"));
    }
  }
  @Test void plainAccountCannotCreateCatalogEntries() throws Exception {
    var email="merge-"+suffix()+"@test.local"; seedDefaultTenantAccount(email,"PlainUser@2026"); var token=login(email,"PlainUser@2026");
    for(var path:new String[]{"catalog/skus","warehouse-config"}) mvc.perform(post("/api/v1/retail/"+path).with(bearer(token)).header("Idempotency-Key","denied-"+suffix()).contentType(APPLICATION_JSON).content("{\"name\":\"Denied\",\"unit\":\"cái\",\"kind\":\"GROUP\"}")).andExpect(status().isForbidden());
  }
}