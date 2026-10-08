export const businessModules={
 "aicrm-retail":{dependencies:[],screens:[]},
 "aicrm-stock":{dependencies:["aicrm-retail"],screens:["stock","warehouses","warehouse-config","transfer"]},
 "aicrm-sales":{dependencies:["aicrm-retail","aicrm-stock"],screens:["pos","orders","sales-customers","returns"]},
 "aicrm-purchase":{dependencies:["aicrm-retail","aicrm-stock"],screens:["purchase","suppliers"]},
} as const;
export function screenModule(screen:string){return Object.entries(businessModules).find(([,m])=>(m.screens as readonly string[]).includes(screen))?.[0]||"aicrm-retail";}
