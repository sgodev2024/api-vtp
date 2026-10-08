"use client";
import PermissionBoundary from "./permission-boundary";import LocationWorkspace from "./location-workspace";import PermissionMatrix from "./permission-matrix";import PartnerMaster from "./partner-master";import SharedWorkspace from "./shared-workspace";import SalesWorkspace from "./sales/sales-workspace";import PurchaseWorkspace from "./purchase";import InventoryWorkspace from "./inventory";
type Request=<T>(path:string,init?:RequestInit)=>Promise<T>;
export default function RetailWorkspace(props:{view:string;request:Request;apiUrl:string;onNavigate:(v:string)=>void}){if(props.view==="retail-permissions")return <PermissionMatrix request={props.request}/>;return <PermissionBoundary view={props.view} request={props.request}><Content {...props}/></PermissionBoundary>;}
function Content(props:Parameters<typeof RetailWorkspace>[0]){
 if(["retail-locations","retail-warehouses"].includes(props.view))return <LocationWorkspace {...props}/>;
 if(props.view==="retail-setup")return <LocationWorkspace {...props}><SharedWorkspace {...props}/></LocationWorkspace>;
 if(["retail-pos","retail-orders"].includes(props.view))return <SalesWorkspace key={props.view} view={props.view} request={props.request} onNavigate={props.onNavigate}/>;
 if(["retail-customers","retail-suppliers","retail-master-customers","retail-master-partners","retail-sales-customers"].includes(props.view))return <PartnerMaster key={props.view} request={props.request} supplier={props.view==="retail-suppliers"} customerOnly={["retail-customers","retail-master-customers","retail-sales-customers"].includes(props.view)} context={props.view==="retail-customers"?"crm":props.view==="retail-sales-customers"?"sales":"master"} onNavigate={props.onNavigate}/>;
 if(props.view==="retail-purchase")return <PurchaseWorkspace {...props}/>;
 if(["retail-stock","retail-transfer","retail-warehouse-config"].includes(props.view))return <InventoryWorkspace {...props}/>;
 return <SharedWorkspace {...props}/>;
}
