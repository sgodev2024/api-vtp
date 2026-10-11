"use client";
import type {ModuleWorkspaceProps} from "../../module-contract";import SharedWorkspace from "../shared-workspace";import LocationWorkspace from "../location-workspace";
export default function Workspace(props:ModuleWorkspaceProps){if(props.view==="retail-warehouses")return <LocationWorkspace {...props}/>;return <SharedWorkspace {...props}/>;}
