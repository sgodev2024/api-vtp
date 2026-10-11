"use client";
import SharedWorkspace from "../shared-workspace";import PartnerMaster from "../partner-master";
export default function Workspace(props:Parameters<typeof SharedWorkspace>[0]){return <PartnerMaster key={props.view} request={props.request} customerOnly={props.view!=="retail-master-partners"} context={"master"} onNavigate={props.onNavigate}/>;}
