"use client";
import {useEffect,useState} from "react";
import {AppIcon} from "./app-icon";

type Tab={label:string;icon:string;active:boolean;onSelect:()=>void};
type InstallPrompt=Event&{prompt:()=>Promise<void>;userChoice:Promise<{outcome:string}>};
export default function MobileAppChrome({tabs,moreOpen,onMore}:{tabs:Tab[];moreOpen:boolean;onMore:()=>void}){
 const [install,setInstall]=useState<InstallPrompt|null>(null),[help,setHelp]=useState(false);
 useEffect(()=>{
  const root=document.documentElement;
  // Labels preserve the original cells and controls; CSS changes only their mobile presentation.
  const labelTables=()=>document.querySelectorAll<HTMLTableElement>("main table").forEach(table=>{
   const headers=Array.from(table.tHead?.rows[0]?.cells??[]).map(c=>c.textContent?.trim()??"");
   for(const body of table.tBodies)for(const row of body.rows)Array.from(row.cells).forEach((cell,i)=>{
    if(cell.colSpan===1&&headers[i])cell.dataset.mobileLabel=headers[i];else delete cell.dataset.mobileLabel;
   });
  });
  let frame=0;const schedule=()=>{cancelAnimationFrame(frame);frame=requestAnimationFrame(labelTables);};
  const observer=new MutationObserver(schedule);const main=document.querySelector("main");if(main)observer.observe(main,{childList:true,subtree:true,characterData:true});schedule();
  const viewport=window.visualViewport;
  let fullHeight=window.innerHeight;
  const keyboard=()=>{
   const field=document.activeElement;
   const editing=field instanceof HTMLTextAreaElement||(field instanceof HTMLInputElement&&!['checkbox','radio','button','submit','file'].includes(field.type));
   if(!editing)fullHeight=window.innerHeight;
   root.dataset.mobileKeyboard=String(window.innerWidth<=820&&editing&&!!viewport&&Math.max(fullHeight,window.innerHeight)-viewport.height>120);
  };
  viewport?.addEventListener("resize",keyboard);document.addEventListener("focusin",keyboard);document.addEventListener("focusout",keyboard);
  const offer=(e:Event)=>{e.preventDefault();setInstall(e as InstallPrompt);};const installed=()=>setInstall(null);
  window.addEventListener("beforeinstallprompt",offer);window.addEventListener("appinstalled",installed);
  return()=>{observer.disconnect();cancelAnimationFrame(frame);viewport?.removeEventListener("resize",keyboard);document.removeEventListener("focusin",keyboard);document.removeEventListener("focusout",keyboard);delete root.dataset.mobileKeyboard;window.removeEventListener("beforeinstallprompt",offer);window.removeEventListener("appinstalled",installed);};
 },[]);
 const standalone=typeof window!=="undefined"&&window.matchMedia("(display-mode: standalone)").matches;
 return <><nav className="mobile-app-nav" aria-label="Điều hướng nhanh" style={{gridTemplateColumns:`repeat(${tabs.length+1},minmax(0,1fr))`}}>{tabs.map(t=><button key={t.label} aria-current={t.active&&!moreOpen?"page":undefined} onClick={t.onSelect}><AppIcon name={t.icon} size={22}/><span>{t.label}</span></button>)}<button aria-expanded={moreOpen} aria-label="Thêm chức năng" onClick={onMore}><AppIcon name="apps" size={22}/><span>Thêm</span></button></nav>
 {moreOpen&&!standalone&&<button className="mobile-install" onClick={()=>{if(install)void install.prompt().then(()=>install.userChoice).then(()=>setInstall(null));else setHelp(true);}}><AppIcon name="download" size={18}/> Thêm vào màn hình chính</button>}
 {help&&<div className="mobile-help-backdrop"><section role="dialog" aria-modal="true" aria-label="Thêm AICRM vào màn hình chính"><h2>AICRM trên điện thoại</h2><p>Trên iPhone: mở bằng Safari, chọn Chia sẻ → Thêm vào Màn hình chính.</p><p>Trên Android: mở menu trình duyệt, chọn Cài đặt ứng dụng hoặc Thêm vào màn hình chính, nếu có.</p><button onClick={()=>setHelp(false)}>Đã hiểu</button></section></div>}</>;
}
