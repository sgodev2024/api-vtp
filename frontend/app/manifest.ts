import type {MetadataRoute} from "next";
export default function manifest():MetadataRoute.Manifest{
 return {id:"/",name:"AICRM — Quản lý bán hàng",short_name:"AICRM",description:"Bán hàng, kho và mua hàng",lang:"vi",start_url:"/home",scope:"/",display:"standalone",background_color:"#f2f7f4",theme_color:"#0b382f",icons:[{src:"/app-icon-192.png",sizes:"192x192",type:"image/png",purpose:"any"},{src:"/app-icon-512.png",sizes:"512x512",type:"image/png",purpose:"any"},{src:"/app-icon-maskable.png",sizes:"512x512",type:"image/png",purpose:"maskable"}]};
}
