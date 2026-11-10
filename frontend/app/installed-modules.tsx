"use client";
import dynamic from "next/dynamic";
import type { InstalledFrontendModule, ModuleWorkspaceProps } from "./module-contract";
import PermissionBoundary from "./retail/permission-boundary";
import { bootstrap } from "./retail/bootstrap";
import OrderManagementPage from "./orders/OrderManagementPage";
import ShippingConfigPage from "./shipping/ShippingConfigPage";
import ApiKeyPage from "./api-keys/ApiKeyPage";

const Entry0 = dynamic(() => import("./retail/permissions"), { ssr: false });
function Workspace0(props: ModuleWorkspaceProps) { return <Entry0 {...props} />; }

const Entry1 = dynamic(() => import("./retail/products"), { ssr: false });
function Workspace1(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry1 {...props} /></PermissionBoundary>; }

const Entry2 = dynamic(() => import("./retail/partners"), { ssr: false });
function Workspace2(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry2 {...props} /></PermissionBoundary>; }

const Entry3 = dynamic(() => import("./retail/operations"), { ssr: false });
function Workspace3(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry3 {...props} /></PermissionBoundary>; }

const Entry4 = dynamic(() => import("./retail/accounting"), { ssr: false });
function Workspace4(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry4 {...props} /></PermissionBoundary>; }

const Entry5 = dynamic(() => import("./retail/inventory"), { ssr: false });
function Workspace5(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry5 {...props} /></PermissionBoundary>; }

const Entry6 = dynamic(() => import("./retail/sales"), { ssr: false });
function Workspace6(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry6 {...props} /></PermissionBoundary>; }

const Entry7 = dynamic(() => import("./retail/purchase"), { ssr: false });
function Workspace7(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry7 {...props} /></PermissionBoundary>; }

const Entry8 = dynamic(() => import("./retail/crm"), { ssr: false });
function Workspace8(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry8 {...props} /></PermissionBoundary>; }

const Entry9 = dynamic(() => import("./retail/overview"), { ssr: false });
function Workspace9(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry9 {...props} /></PermissionBoundary>; }

const Entry10 = dynamic(() => import("./retail/reports"), { ssr: false });
function Workspace10(props: ModuleWorkspaceProps) { return <PermissionBoundary view={props.view} request={props.request}><Entry10 {...props} /></PermissionBoundary>; }

const Entry11 = dynamic(() => import("./shipping/ShippingConfigPage"), { ssr: false });
function Workspace11(props: ModuleWorkspaceProps) { return <Entry11 />; }

export const installedModules: InstalledFrontendModule[] = [
  // Gộp chung cả 3 view vào 1 key module duy nhất
  { 
    key: "aicrm-viettelpost", 
    views: ["shipping", "order", "apikeys"], 
    Workspace: (props) => {
      if (props.view === "shipping") return <ShippingConfigPage />;
      if (props.view === "order") return <OrderManagementPage />;
      if (props.view === "apikeys") return <ApiKeyPage />;
      return null;
    }
  }
];