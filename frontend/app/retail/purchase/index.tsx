"use client";
import type {ModuleWorkspaceProps} from "../../module-contract";import SharedWorkspace from "../shared-workspace";import PartnerMaster from "../partner-master";
export default function Workspace(props:ModuleWorkspaceProps){if(props.view==="retail-suppliers")return <PartnerMaster request={props.request} supplier context="master" onNavigate={props.onNavigate}/>;return <SharedWorkspace {...props}/>;}
