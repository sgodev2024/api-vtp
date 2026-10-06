// Real navigation/read-only screens plus browser-only fixture edits; no retail writes to local DB.
import assert from "node:assert/strict";
import {createRequire} from "node:module";
import {readFile,writeFile,mkdir} from "node:fs/promises";
import {fileURLToPath} from "node:url";
const {chromium}=createRequire(import.meta.url)(process.env.AICRM_PLAYWRIGHT_PATH||"playwright");
const password=(await readFile(new URL("../.env",import.meta.url),"utf8")).match(/^AICRM_BOOTSTRAP_PASSWORD=(.+)$/m)?.[1]?.trim();
const login=await fetch("http://localhost:8185/api/v1/auth/login",{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({email:"admin@core.local",password})});assert.equal(login.status,200);
const token=(await login.json()).session.accessToken;
const nav=await (await fetch("http://localhost:8185/api/v1/navigation/me",{headers:{Authorization:"Bearer "+token}})).json();
const matrix=[
 ["retail-partner-master","master-customers","Khách hàng"],
 ["retail-partner-master","master-partners","Đối tác"],
 ["retail-crm","customers","Khách hàng"],
 ["retail","sales-customers","Khách hàng"],
 ["retail-purchase","suppliers","Nhà cung cấp"]
];
for(const [section,route,label] of matrix){const group=nav.sections.find(s=>s.key===section);assert(group,section);const item=group.items.find(i=>i.route==="/business/retail/"+route);assert.equal(item?.label,label);assert(!item.parentKey,"Page must be a direct child of its module");}
assert.equal(nav.sections.find(s=>s.key==="retail-partner-master").label,"Khách hàng / Đối tác");
const checks=["Navigation API returns standalone master module with two children and separate customer pages under CRM and Sales"],errors=[];
const browser=await chromium.launch({channel:"msedge",headless:true});
const output=new URL("../docs/screenshots/customer-master/",import.meta.url);await mkdir(output,{recursive:true});
const rows=[
 {id:"10000000-0000-4000-8000-000000000001",code:"DUAL",name:"Đối tác dùng chung",roles:{CUSTOMER:true,SUPPLIER:true},kind:"CUSTOMER",entity_type:"BUSINESS",active:true,version:1,phone:"0900000001"},
 {id:"10000000-0000-4000-8000-000000000002",code:"SUP",name:"Nhà cung cấp riêng",roles:{SUPPLIER:true},kind:"SUPPLIER",entity_type:"BUSINESS",active:true,version:1},
 {id:"10000000-0000-4000-8000-000000000003",code:"CTV",name:"CTV riêng",roles:{COLLABORATOR:true},kind:"COLLABORATOR",entity_type:"PERSON",active:true,version:1}
];
const requests=[];
try{
 const ctx=await browser.newContext({viewport:{width:1440,height:1000}});await ctx.addInitScript(t=>sessionStorage.setItem("core-access-token",t),token);
 await ctx.route("**/api/v1/retail/**",route=>route.request().method()==="GET"?route.continue():route.abort());
 const page=await ctx.newPage();page.on("pageerror",e=>errors.push(e.message));
 for(const [,route,title] of matrix){await page.goto("http://localhost:3205/business/retail/"+route);await page.getByRole("heading",{name:title,exact:true}).waitFor();await page.getByRole("button",{name:"+ Thêm đối tác",exact:true}).waitFor();assert(page.url().endsWith(route));}
 await page.goto("http://localhost:3205/business/retail/master-partners");await page.getByRole("heading",{name:"Đối tác",exact:true}).waitFor();await page.screenshot({path:fileURLToPath(new URL("master-menu-local.png",output)),fullPage:true});checks.push("All five real routes load correct headings without redirect or retail writes");
 await ctx.unroute("**/api/v1/retail/**");
 await ctx.route("**/api/v1/retail/**",async route=>{
  const req=route.request(),url=new URL(req.url()),path=url.pathname.replace("/api/v1/retail","");
  const reply=data=>route.fulfill({status:200,contentType:"application/json",headers:{"access-control-allow-origin":"http://localhost:3205"},body:JSON.stringify(data)});
  if(req.method()==="OPTIONS")return route.fulfill({status:204,headers:{"access-control-allow-origin":"http://localhost:3205","access-control-allow-headers":"authorization,content-type,idempotency-key","access-control-allow-methods":"GET,POST,OPTIONS"}});
  if(path==="/catalog")return reply({permissions:["READ","SELL","PURCHASE","CRM","SETUP","FINANCE"],branches:[]});
  if(path==="/partner-owners")return reply([]);
  if(path==="/partners"){assert.equal(req.method(),"GET");const role=url.searchParams.get("role");requests.push(role);const items=rows.filter(p=>!role||Object.hasOwn(p.roles,role));return reply({items,total:items.length,page:0});}
  if(path.startsWith("/partners/")){const p=rows.find(r=>path.endsWith(r.id));assert(p);if(req.method()==="POST"){const body=req.postDataJSON();assert.equal(body.version,p.version);p.name=body.name;p.version++;}return reply({...p,history:[],notes:[],balances:[],warnings:[]});}
  throw new Error("Unexpected retail request "+path);
 });
 for(const [,route,title] of matrix){await page.goto("http://localhost:3205/business/retail/"+route);await page.getByRole("heading",{name:title,exact:true}).waitFor();const expectedRole=route==="master-partners"?"":route==="suppliers"?"SUPPLIER":"CUSTOMER";await page.getByRole("row").filter({hasText:"DUAL"}).waitFor();assert.equal(requests.at(-1),expectedRole);assert.equal(await page.getByRole("row").filter({hasText:"SUP"}).count(),expectedRole==="CUSTOMER"?0:1);assert.equal(await page.getByLabel("Lọc vai trò",{exact:true}).count(),route==="master-partners"?1:0);}
 checks.push("Three customer screens request CUSTOMER, supplier screen requests SUPPLIER, master partners can list all roles");
 await page.goto("http://localhost:3205/business/retail/sales-customers");await page.getByRole("row").filter({hasText:"DUAL"}).getByRole("button",{name:"Sửa",exact:true}).click();await page.getByLabel("Tên đối tác",{exact:true}).fill("Tên sửa từ Bán hàng");await page.getByRole("button",{name:"Lưu đối tác",exact:true}).click();await page.getByText("Đã lưu hồ sơ đối tác dùng chung.",{exact:true}).waitFor();
 for(const route of ["master-customers","master-partners","customers","suppliers"]){await page.goto("http://localhost:3205/business/retail/"+route);await page.getByRole("row").filter({hasText:"DUAL"}).getByText("Tên sửa từ Bán hàng",{exact:true}).waitFor();}
 checks.push("Edit from Sales updates one master ID, reflected in master/customer/CRM/supplier views (browser fixture only)");
 await page.goto("http://localhost:3205/business/retail/customers");await page.getByRole("button",{name:"Mở Customer Master",exact:true}).click();await page.getByRole("heading",{name:"Đối tác",exact:true}).waitFor();assert(page.url().endsWith("master-partners"));checks.push("Context screens link back to standalone Customer Master");
 await page.setViewportSize({width:390,height:844});assert((await page.locator(".partner-master").evaluate(el=>el.scrollWidth-el.clientWidth))<=2);assert.deepEqual(errors,[]);checks.push("Mobile master has no root overflow or runtime errors");await ctx.close();
 const result={checkedAt:new Date().toISOString(),checks,runtimeErrors:errors,operationalRetailWrites:0,fixture:"Browser interception only"};await writeFile(new URL("../docs/customer-master-menu-check-result.json",import.meta.url),JSON.stringify(result,null,2));console.log(JSON.stringify(result,null,2));
}finally{await browser.close();}
