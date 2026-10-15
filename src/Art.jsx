import React from "react";

// Visual cleanup gate: old character/logo/garden artwork has been removed.
// These semantic slots keep the prototype functional until new approved art is supplied.
export const POSES={
  welcome:{label:"دعوت به بازی"},listen:{label:"گوش‌دادن"},think:{label:"فکرکردن"},help:{label:"راهنمایی آرام"},
  point:{label:"اشاره به کشف"},read:{label:"قصه‌خوانی"},celebrate:{label:"جشن کشف"},wait:{label:"انتظار همراهانه"},
  wave:{label:"خوش‌آمد دوباره"},demo:{label:"نمایش حرکت"}
};

function Slot({className="",label="جای تصویر جدید",children}){
  return <div className={"asset-slot "+className} role="img" aria-label={label}>{children||<span>{label}</span>}</div>;
}
export function Logo(){return <div className="ava-logo" aria-label="آوا"><strong>آوا</strong></div>}
export function Companion({pose="welcome",small=false}){return <Slot className={"companion "+(small?"small ":"")+"pose-"+pose} label={"جای تصویر همراه آوا: "+(POSES[pose]?.label||pose)}><span>تصویر همراه</span></Slot>}
export function ChildAvatar({config={},small=false}){return <Slot className={"child-avatar "+(small?"small":"")} label="جای تصویر آواتار کودک"><span>آواتار</span></Slot>}
export function Friend({kind="fox"}){const labels={fox:"دوست روباه",owl:"دوست جغد",bird:"دوست پرنده"};return <Slot className={"friend friend-"+kind} label={"جای تصویر "+(labels[kind]||"دوست")}><span>دوست</span></Slot>}
export function Garden({className=""}){return <Slot className={"garden-art "+className} label="جای تصویر محیط/باغ"><span>تصویر محیط</span></Slot>}
