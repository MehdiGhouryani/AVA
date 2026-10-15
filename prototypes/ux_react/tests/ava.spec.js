import {test,expect} from "@playwright/test";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
const here = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(here, "..", "..", "..");
const qaDir = path.join(projectRoot, ".artifacts", "prototype_qa");
fs.mkdirSync(qaDir, { recursive: true });
const registry=JSON.parse(fs.readFileSync(new URL("../src/generated/screens.json", import.meta.url),"utf8"));
const results=[],states=[];
for(const s of registry)test(`render ${s.id}: base, width checks and registered states`,async({page})=>{
 const errors=[];page.on("pageerror",e=>errors.push(e.message));
 await page.setViewportSize({width:1440,height:1000});await page.goto("/");
 await page.waitForFunction(()=>typeof window.avaSelect==="function");await page.evaluate(()=>document.fonts.ready);
 const frameStyle=await page.addStyleTag({content:".preview-grid{grid-template-columns:360px 330px!important}.child-device{width:360px!important;max-width:360px!important}.reduce-motion *{animation:none!important}"});
 await page.evaluate(()=>window.avaSetReduced(true));
  await page.evaluate(id=>window.avaSelect(id),s.id);
  const frame=page.locator("[data-screen]");
  await expect(frame).toHaveAttribute("data-screen",s.id);
  await page.waitForTimeout(s.id==="S13"?550:30);
  const rect=await frame.boundingBox();expect(rect.width).toBeGreaterThan(250);
  const data=await frame.evaluate(el=>({width:el.clientWidth,height:el.clientHeight,scrollWidth:el.scrollWidth,text:el.innerText.slice(0,120),controls:[...el.querySelectorAll("button,[role=slider]")].map(b=>({label:b.getAttribute("aria-label")||b.innerText,width:Math.round(b.getBoundingClientRect().width),height:Math.round(b.getBoundingClientRect().height)}))}));
  expect(data.scrollWidth).toBeLessThanOrEqual(data.width+1);
  expect(data.text).not.toContain("والد عزیز");
  const min=s.id.startsWith("CMS")||s.id.startsWith("UX")||["S03a","S03b","S03c","S03d","S03e","S18","S19","S20","S24","S27"].includes(s.id)?48:64;
  expect(data.controls.filter(c=>c.width<min||c.height<min),s.id+" touch target").toEqual([]);
  results.push({id:s.id,viewport:s.id.startsWith("CMS")?"desktop":360,...data});
  if(!s.id.startsWith("CMS")){
   for(const width of [320,412]){
    await page.evaluate(w=>{document.querySelector(".child-device").style.setProperty("width",w+"px","important");document.querySelector(".child-device").style.setProperty("max-width",w+"px","important");document.querySelector(".preview-grid").style.gridTemplateColumns=w+"px 330px"},width);
    const dims=await frame.evaluate(el=>({width:el.clientWidth,scrollWidth:el.scrollWidth,height:el.clientHeight}));
    expect(dims.scrollWidth,`${s.id} at ${width}`).toBeLessThanOrEqual(dims.width+1);
    results.push({id:s.id,viewport:width,...dims});
   }
   await page.evaluate(()=>{document.querySelector(".child-device").style.removeProperty("width");document.querySelector(".child-device").style.removeProperty("max-width")});
  }
  for(const st of s.states){
   await page.evaluate(state=>window.avaSetState(state),st);await page.waitForTimeout(8);
   expect(await frame.getAttribute("data-state")).toBe(st);
   const dimensions=await frame.evaluate(el=>({width:el.clientWidth,scrollWidth:el.scrollWidth}));
   expect(dimensions.scrollWidth).toBeLessThanOrEqual(dimensions.width+1);
   states.push({id:s.id,state:st,...dimensions,rendered:true});
   if(["near_miss","meaning","low_storage","offline","pending","denied","hash_mismatch","question","saving"].includes(st)){
   }
  }
 expect(errors).toEqual([]);
 fs.writeFileSync(path.join(qaDir, "render_receipt.json"),JSON.stringify({base_count:registry.length,geometry_checks:results.length,state_render_checks:states.length,results,states,errors,limits:"Browser CSS-pixel rendering, not native Android dp, screen-reader verification, educational validation or child pilot. Selector rendering does not certify production behavior."},null,2));
});

test("onboarding, recovery, individual avatar persistence, rewards, CMS review and mobile shell",async({page})=>{
 await page.setViewportSize({width:1280,height:1000});await page.goto("/");await page.waitForFunction(()=>window.avaSelect);
 const frame=page.locator(".child-device");
 await page.evaluate(()=>window.avaSelect("S02"));
 await frame.getByRole("button",{name:"بعدی",exact:true}).click();await expect(frame.getByRole("heading",{name:"هر صدا، یک کشف"})).toBeVisible();
 await frame.getByRole("button",{name:"بعدی",exact:true}).click();await frame.getByRole("button",{name:"آماده شویم",exact:true}).click();
 await expect(frame).toHaveAttribute("data-screen","S03a");
 await expect(frame.getByRole("button",{name:"نمایش مسیر موبایل"})).toBeDisabled();
 await frame.getByRole("switch",{name:"پذیرش نمایشی قوانین"}).click();await frame.getByRole("button",{name:"نمایش مسیر موبایل"}).click();
 await expect(frame).toHaveAttribute("data-screen","S03b");
 await frame.getByRole("button",{name:"نمایش تأیید موفق"}).click();await expect(frame).toHaveAttribute("data-screen","S04");
 await page.evaluate(()=>window.avaSelect("S10"));await frame.getByRole("radio",{name:"آ",exact:true}).click();await expect(frame.getByText("با هم دوباره گوش بدهیم",{exact:true})).toBeVisible();
 await page.evaluate(()=>window.avaSelect("S09"));await frame.getByRole("button",{name:"مکث و خروج"}).click();await expect(page.getByRole("dialog")).toBeVisible();await page.getByRole("button",{name:"ادامهٔ بازی",exact:true}).click();
 await page.evaluate(()=>window.avaSelect("S27"));await frame.getByRole("radio",{name:"دوست باغ",exact:true}).click();await frame.getByRole("button",{name:"ویرایش همراه"}).click();
 await expect(frame).toHaveAttribute("data-screen","S15");await frame.getByRole("radio",{name:"بنفش",exact:true}).click();await frame.getByRole("button",{name:"این همراه من است"}).click();
 const profile=await page.evaluate(()=>JSON.parse(localStorage.getItem("ava_profiles_v18")));expect(profile.demo_child_2.avatar.color).toBe("plum");
 await page.evaluate(()=>window.avaSelect("S27"));await frame.getByRole("radio",{name:"دوست قصه",exact:true}).click();await frame.getByRole("button",{name:"ویرایش همراه"}).click();expect(await frame.locator(".child-avatar").getAttribute("aria-label")).toContain("mint");
 await page.evaluate(()=>window.avaSelect("S13"));await page.waitForTimeout(600);
 const ledgerBefore=await page.evaluate(()=>localStorage.getItem("ava_reward_ledger_v18"));expect(JSON.parse(ledgerBefore)["demo_child_1:demo_node_b"].committed).toBe(true);
 await page.evaluate(()=>window.avaSelect("S06-B"));await page.evaluate(()=>window.avaSelect("S13"));await page.waitForTimeout(600);expect(await page.evaluate(()=>localStorage.getItem("ava_reward_ledger_v18"))).toBe(ledgerBefore);
 await page.evaluate(()=>window.avaSelect("CMS-02"));await page.locator(".cms-main").getByRole("button",{name:"بازبینی",exact:true}).first().click();
 await expect(page.getByRole("dialog")).toBeVisible();await page.getByRole("radio",{name:"بازبینی",exact:true}).click();await page.getByLabel("دلیل تصمیم نمونه").fill("بازبینی آموزشی لازم است");await page.getByRole("button",{name:"پیشنهاد اصلاح"}).click();await expect(page.getByText("دلیل رد نمونه ثبت شد، بدون تغییر داده")).toBeVisible();await page.getByRole("button",{name:"بستن پرونده"}).click();
 await page.setViewportSize({width:390,height:844});await page.evaluate(()=>window.avaSelect("S15"));
 expect(await page.evaluate(()=>document.documentElement.scrollWidth)).toBeLessThanOrEqual(390);
 await page.evaluate(()=>window.avaSetReduced(true));expect(await frame.locator(".child-avatar").evaluate(e=>getComputedStyle(e).animationName)).toBe("none");
 await page.evaluate(()=>window.avaSetQuiet(true));await expect(frame.getByRole("button",{name:"بازپخش راهنمای صوتی"})).toBeVisible();
 fs.writeFileSync(path.join(qaDir, "interaction_receipt.json"),JSON.stringify({passed:["child-first onboarding","consent demo gate","wrong-answer recovery","pause/resume dialog","per-profile avatar persistence","profile avatar independence","reward commit before reveal","idempotent reward demo","CMS reason-required review","390px shell without horizontal overflow","reduced motion","quiet visual cue"],limits:"No real audio, credential collection, downloads, payments, backend, TalkBack/device or child pilot."},null,2));
});
