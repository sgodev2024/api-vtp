"use client";
import SharedWorkspace from "../shared-workspace";import PartnerMaster from "../partner-master";
import Pipeline from "./pipeline";
export default function Workspace(props:Parameters<typeof SharedWorkspace>[0]){return ["retail-leads","retail-opportunities"].includes(props.view)?<Pipeline key={props.view} view={props.view} request={props.request}/>:<PartnerMaster key={props.view} request={props.request} customerOnly={true} context={"crm"} onNavigate={props.onNavigate}/>;}
