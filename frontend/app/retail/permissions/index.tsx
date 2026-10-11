"use client";
import type {ModuleWorkspaceProps} from "../../module-contract";import SharedWorkspace from "../shared-workspace";import PermissionMatrix from "../permission-matrix";
export default function Workspace(props:ModuleWorkspaceProps){return <PermissionMatrix request={props.request}/>;}
