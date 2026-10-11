export const businessModules={
  "aicrm-retail": {
    "dependencies": [],
    "screens": [
      "permissions"
    ]
  },
  "aicrm-products": {
    "dependencies": [
      "aicrm-retail"
    ],
    "screens": [
      "products",
      "product-groups",
      "product-brands",
      "product-units",
      "uom-conversions",
      "product-attributes"
    ]
  },
  "aicrm-partners": {
    "dependencies": [
      "aicrm-products",
      "aicrm-retail"
    ],
    "screens": [
      "master-customers",
      "master-partners"
    ]
  },
  "aicrm-operations": {
    "dependencies": [
      "aicrm-products",
      "aicrm-retail"
    ],
    "screens": [
      "locations",
      "setup",
      "approvals"
    ]
  },
  "aicrm-accounting": {
    "dependencies": [
      "aicrm-operations",
      "aicrm-retail",
      "aicrm-partners"
    ],
    "screens": [
      "cashflow",
      "debt"
    ]
  },
  "aicrm-stock": {
    "dependencies": [
      "aicrm-retail",
      "aicrm-products",
      "aicrm-operations",
      "aicrm-accounting"
    ],
    "screens": [
      "stock",
      "warehouses",
      "warehouse-config",
      "transfer"
    ]
  },
  "aicrm-sales": {
    "dependencies": [
      "aicrm-retail",
      "aicrm-stock",
      "aicrm-partners",
      "aicrm-accounting"
    ],
    "screens": [
      "pos",
      "orders",
      "sales-customers",
      "returns"
    ]
  },
  "aicrm-purchase": {
    "dependencies": [
      "aicrm-retail",
      "aicrm-stock",
      "aicrm-partners",
      "aicrm-accounting"
    ],
    "screens": [
      "purchase",
      "suppliers"
    ]
  },
  "aicrm-crm": {
    "dependencies": [
      "aicrm-operations",
      "aicrm-products",
      "aicrm-retail",
      "aicrm-partners"
    ],
    "screens": [
      "customers"
    ]
  },
  "aicrm-overview": {
    "dependencies": [
      "aicrm-retail",
      "aicrm-accounting",
      "aicrm-products"
    ],
    "screens": [
      "overview"
    ]
  },
  "aicrm-reports": {
    "dependencies": [
      "aicrm-operations",
      "aicrm-products",
      "aicrm-retail"
    ],
    "screens": [
      "reports"
    ]
  }
} as const;
export function screenModule(screen:string){return Object.entries(businessModules).find(([,m])=>(m.screens as readonly string[]).includes(screen))?.[0]||"aicrm-retail";}
