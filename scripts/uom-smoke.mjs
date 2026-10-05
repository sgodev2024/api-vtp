import assert from "node:assert/strict";
const base="http://127.0.0.1:8185";
const password=process.env.AICRM_ADMIN_PASSWORD;if(!password)throw Error("Set AICRM_ADMIN_PASSWORD");
let token="";async function req(path,body,status=200){const res=await fetch(base+"/api/v1/"+path,{method:body?"POST":"GET",headers:{Authorization:"Bearer "+token,"Content-Type":"application/json","Idempotency-Key":crypto.randomUUID()},body:body?JSON.stringify(body):undefined});const result=await res.json();assert.equal(res.status,status,JSON.stringify(result));return result;}
token=(await req("auth/login",{email:"admin@core.local",password})).session.accessToken;
const key=crypto.randomUUID().slice(0,8);const cat=await req("retail/catalog");const before=await req("retail/uom-conversions");assert(before.conversions.length>0,"Legacy conversions migrated");
await req("retail/warehouse-config",{kind:"UNIT",code:"UOM-"+key,name:"Hộp "+key,value:"",active:true});
const unit=(await req("retail/uom-conversions")).units.find(u=>u.name==="Hộp "+key);assert(unit);
const products=[];for(let i=0;i<2;i++){products.push(await req("retail/catalog/skus",{code:"UOM-"+key+"-"+i,name:"Kiểm thử quy đổi "+i,unit:"chiếc",price:100,tracking:"NONE"}));await req("retail/uom-conversions",{productId:products[i].id,uomId:unit.id,factor:i?12:10});}
const quote=async(id)=>req("retail/documents",{type:"QUOTE",warehouseId:cat.warehouses[0].id,lines:[{skuId:id,qty:2,unit:unit.name,price:1000,discount:0,vat:0}]});
const q1=await quote(products[0].id);const q2=await quote(products[1].id);assert.equal(Number(q1.lines[0].base_qty),20);assert.equal(Number(q2.lines[0].base_qty),24);
await req("retail/uom-conversions",{productId:products[0].id,uomId:unit.id,factor:15});assert.equal(Number((await req("retail/documents/"+q1.id)).lines[0].ratio),10);assert.equal(Number((await quote(products[0].id)).lines[0].base_qty),30);
const invalid=await fetch(base+"/api/v1/retail/uom-conversions",{method:"POST",headers:{Authorization:"Bearer "+token,"Content-Type":"application/json","Idempotency-Key":crypto.randomUUID()},body:JSON.stringify({productId:products[0].id,uomId:unit.id,factor:0})});assert(invalid.status>=400);
console.log("PASS legacy migration, UOM catalogue, per-product factors, document base quantities, unchanged snapshots, reject zero factor. Fixture: "+key);
