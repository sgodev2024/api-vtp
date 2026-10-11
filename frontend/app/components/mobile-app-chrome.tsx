"use client";
import {useEffect,useState,useRef} from "react";
import {AppIcon} from "./app-icon";

type Tab={label:string;icon:string;active:boolean;onSelect:()=>void};
type DirectoryEntry={key:string;label:string;icon:string;section:string;sectionLabel:string;keywords:string[];active:boolean;onSelect:()=>void};
type InstallPrompt=Event&{prompt:()=>Promise<void>;userChoice:Promise<{outcome:string}>};
export default function MobileAppChrome({tabs,entries,moreOpen,onMore}:{tabs:Tab[];entries:DirectoryEntry[];moreOpen:boolean;onMore:()=>void}){
 const [query,setQuery]=useState(""),[expanded,setExpanded]=useState("");
 const directoryRef=useRef<HTMLElement>(null),moreRef=useRef<HTMLButtonElement>(null);
 const closeDirectory=()=>{setQuery("");setExpanded("");onMore();};
 useEffect(()=>{
  if(!moreOpen)return;
  const trigger=moreRef.current;
  const media=window.matchMedia("(max-width:820px)"),previous=document.body.style.overflow;
  const sync=()=>{document.body.style.overflow=media.matches?"hidden":previous;if(media.matches)directoryRef.current?.querySelector<HTMLButtonElement>("button")?.focus();};
  sync();media.addEventListener("change",sync);
  const trap=(event:KeyboardEvent)=>{if(!media.matches||event.key!=="Tab"||document.querySelector(".mobile-help-backdrop"))return;const controls=Array.from(directoryRef.current?.querySelectorAll<HTMLElement>("button,input,[tabindex=\"0\"]")??[]).filter(e=>e.getClientRects().length&&!e.hasAttribute("disabled"));const first=controls[0],last=controls.at(-1);if(!first)return;if(event.shiftKey&&document.activeElement===first){event.preventDefault();last?.focus();}else if(!event.shiftKey&&document.activeElement===last){event.preventDefault();first.focus();}};
  document.addEventListener("keydown",trap);return()=>{document.body.style.overflow=previous;media.removeEventListener("change",sync);document.removeEventListener("keydown",trap);if(media.matches)trigger?.focus();};
 },[moreOpen]);
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
 const selectEntry=(entry:DirectoryEntry)=>{setQuery("");setExpanded("");entry.onSelect();};
 const normalize=(value:string)=>value.normalize("NFD").replace(/[\u0300-\u036f]/g,"").toLowerCase().replace(/đ/g,"d");
 const visible=entries.filter(e=>normalize(`${e.label} ${e.sectionLabel} ${e.keywords.join(" ")}`).includes(normalize(query)));
 const categories=[{label:"Nghiệp vụ",match:(e:DirectoryEntry)=>!['home','system-administration','retail-general','retail-reports'].includes(e.section)}, {label:"Vận hành",match:(e:DirectoryEntry)=>e.section==='retail-general'}, {label:"Báo cáo & Quản trị",match:(e:DirectoryEntry)=>['home','system-administration','retail-reports'].includes(e.section)}];
 return <><nav className="mobile-app-nav" aria-label="Điều hướng nhanh" hidden={moreOpen} style={{gridTemplateColumns:`repeat(${tabs.length+1},minmax(0,1fr))`}}>{tabs.map(t=><button key={t.label} aria-current={t.active?"page":undefined} onClick={t.onSelect}><AppIcon name={t.icon} size={22}/><span>{t.label}</span></button>)}<button ref={moreRef} aria-current={!tabs.some(t=>t.active)?"page":undefined} aria-expanded={moreOpen} aria-controls="mobile-directory" aria-label="Thêm chức năng" onClick={onMore}><AppIcon name="apps" size={22}/><span>Thêm</span></button></nav>
 {moreOpen&&<section ref={directoryRef} id="mobile-directory" className="mobile-directory" role="dialog" aria-modal="true" aria-labelledby="mobile-directory-title">
 <header><div><span className="directory-brand">AICRM</span><h2 id="mobile-directory-title">Chức năng</h2></div><button aria-label="Đóng danh mục chức năng" onClick={closeDirectory}><AppIcon name="x" size={22}/></button></header>
 <div className="directory-search"><AppIcon name="search" size={20}/><input aria-label="Tìm chức năng trong danh mục" placeholder="Tìm chức năng..." value={query} onChange={e=>setQuery(e.target.value)}/>{query&&<button aria-label="Xóa tìm kiếm" onClick={()=>setQuery("")}><AppIcon name="x" size={18}/></button>}</div>
 <div className="directory-content">{categories.map(category=>{const list=visible.filter(category.match);if(!list.length)return null;const sectionKeys=[...new Set(list.map(e=>e.section))];return <section className="directory-category" key={category.label}><h3>{category.label}</h3>{sectionKeys.map(key=>{const pages=list.filter(e=>e.section===key);const open=!!query||expanded===key;return <div className="directory-module" key={key}>{pages.length===1?<button className="directory-page" aria-current={pages[0].active?"page":undefined} onClick={()=>selectEntry(pages[0])}><span className="directory-icon"><AppIcon name={pages[0].icon} size={20}/></span><span>{pages[0].sectionLabel===pages[0].label||pages[0].section==="home"?pages[0].label:pages[0].sectionLabel}{pages[0].sectionLabel!==pages[0].label&&pages[0].section!=="home"&&<small>{pages[0].label}</small>}</span><AppIcon name="chevron-right" size={18}/></button>:<><button className="directory-module-trigger" aria-expanded={open} aria-controls={`directory-${key}`} onClick={()=>setExpanded(open?"":key)}><span className="directory-icon"><AppIcon name={pages[0].icon} size={20}/></span><span>{pages[0].sectionLabel}<small>{pages.length} chức năng</small></span><AppIcon name={open?"chevron-down":"chevron-right"} size={18}/></button>{open&&<div className="directory-pages" id={`directory-${key}`}>{pages.map(e=><button className="directory-page" key={e.key} aria-current={e.active?"page":undefined} onClick={()=>selectEntry(e)}><AppIcon name={e.icon} size={18}/><span>{e.label}</span><AppIcon name="chevron-right" size={16}/></button>)}</div>}</>}</div>})}</section>})}{visible.length===0&&<p className="directory-empty">Không tìm thấy chức năng được cấp quyền phù hợp.</p>}
 {!standalone&&<button className="directory-install" onClick={()=>{if(install)void install.prompt().then(()=>install.userChoice).then(()=>setInstall(null));else setHelp(true);}}><AppIcon name="download" size={20}/> Thêm vào màn hình chính</button>}</div>
 </section>}
 {help&&<div className="mobile-help-backdrop"><section role="dialog" aria-modal="true" aria-label="Thêm AICRM vào màn hình chính" onKeyDown={e=>{if(e.key==="Escape"){e.stopPropagation();setHelp(false);}if(e.key==="Tab"){e.preventDefault();e.currentTarget.querySelector("button")?.focus();}}}><h2>AICRM trên điện thoại</h2><p>Trên iPhone: mở bằng Safari, chọn Chia sẻ → Thêm vào Màn hình chính.</p><p>Trên Android: mở menu trình duyệt, chọn Cài đặt ứng dụng hoặc Thêm vào màn hình chính, nếu có.</p><button autoFocus onClick={()=>setHelp(false)}>Đã hiểu</button></section></div>}</>;
}
