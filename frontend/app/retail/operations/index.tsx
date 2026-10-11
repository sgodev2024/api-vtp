"use client";
import SharedWorkspace from "../shared-workspace";import LocationWorkspace from "../location-workspace";
export default function Workspace(props:Parameters<typeof SharedWorkspace>[0]){if(props.view==="retail-locations")return <LocationWorkspace {...props}/>;if(props.view==="retail-setup")return <LocationWorkspace {...props}><SharedWorkspace {...props}/></LocationWorkspace>;return <SharedWorkspace {...props}/>;}
