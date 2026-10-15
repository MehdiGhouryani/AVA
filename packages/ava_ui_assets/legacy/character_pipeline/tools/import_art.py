#!/usr/bin/env python3
"""Imports generated Ava art into the package: strips white glow halos, trims, caps size, saves WebP with the exact asset name.
Usage: python3 tools/import_art.py <source.png> <ava/body/peek.webp>   (target is relative to assets/characters/)"""
import sys, os
import numpy as np
from PIL import Image, ImageFilter
from scipy import ndimage
MAXSIDE=1280; MARGIN=6
def clean_halo(im):
    a=np.array(im.convert("RGBA")).astype(np.float32); al=a[...,3]; rgb=a[...,:3]
    core=al>=240; near=ndimage.binary_dilation(core,iterations=3)
    luma=rgb@np.array([.299,.587,.114]); whitish=(luma>195)&(rgb.max(2)-rgb.min(2)<60)
    kill=(~core)&(whitish|(~near))&(al<240)          # white glow, or anything far from the body that is not solid
    keep_shadow=(~core)&(~whitish)&(luma<175)&(al<200)&(al>8)   # grey/blue ground shadows stay
    al=np.where(kill&~keep_shadow,0,al)
    out=Image.fromarray(np.dstack([rgb,al]).astype(np.uint8),"RGBA")
    A=out.getchannel("A").filter(ImageFilter.GaussianBlur(0.6)); out.putalpha(A); return out
def remove_checker(im):
    """For JPG/PNG images that have a checkerboard PAINTED on them (no real alpha): flood-fill the neutral light background from the
    border, keep the largest object, decontaminate edge colours (no white fringe), feather the alpha."""
    rgb=np.array(im.convert("RGB")); a=rgb.astype(np.float32)
    mx,mn=a.max(2),a.min(2); sat=(mx-mn)/np.maximum(mx,1); luma=a@np.array([.299,.587,.114])
    cand=(sat<0.20)&(luma>95)                                   # white/grey checker squares, smudges and grey floor shadows
    lab,n=ndimage.label(cand); border=set(np.unique(np.concatenate([lab[0],lab[-1],lab[:,0],lab[:,-1]])))-{0}
    bg=np.isin(lab,list(border)); fg=~bg
    fg=ndimage.binary_opening(fg,iterations=1)
    lab2,n2=ndimage.label(fg); sizes=ndimage.sum(fg,lab2,range(1,n2+1)); fg=lab2==(1+int(np.argmax(sizes)))      # one object only
    # feet zone: enclosed checker gaps (between the legs) and soft floor shadows are background regardless of connectivity
    ys=np.where(fg.any(1))[0]; y0,y1=ys.min(),ys.max(); zone=np.zeros_like(fg); zone[int(y1-0.20*(y1-y0)):,:]=True
    shadowish=(sat<0.30)&(luma>60)                                 # grey-teal floor shadow right under the feet
    fg=fg&~(zone&(cand|shadowish))
    fg=ndimage.binary_opening(fg,iterations=1)
    lab2,n2=ndimage.label(fg); sizes=ndimage.sum(fg,lab2,range(1,n2+1)); fg=lab2==(1+int(np.argmax(sizes)))
    holes,nh=ndimage.label(~fg)                                   # fill only SMALL enclosed holes (JPEG specks), never the gap between legs
    for i in range(1,nh+1):
        m=holes==i
        if m.sum()<150 and not (m[0].any() or m[-1].any() or m[:,0].any() or m[:,-1].any()): fg|=m
    inter=ndimage.binary_erosion(fg,iterations=3)
    dist,(iy,ix)=ndimage.distance_transform_edt(~inter,return_indices=True)
    fixed=rgb[iy,ix]; band=(dist>0)&(dist<=6); rgb2=np.where(band[...,None],fixed,rgb)
    alpha=ndimage.binary_erosion(fg,iterations=1).astype(np.float32)
    alpha=np.array(Image.fromarray((alpha*255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.9)))
    return Image.fromarray(np.dstack([rgb2,alpha]).astype(np.uint8),"RGBA")
def run(src,dst_rel,root,checker=False):
    im=remove_checker(Image.open(src)) if checker else clean_halo(Image.open(src)); bb=im.getchannel("A").point(lambda v:255 if v>10 else 0).getbbox(); im=im.crop(bb)
    pad=Image.new("RGBA",(im.width+2*MARGIN,im.height+2*MARGIN),(0,0,0,0)); pad.paste(im,(MARGIN,MARGIN)); im=pad
    s=min(1.0,MAXSIDE/max(im.size)); im=im.resize((round(im.width*s),round(im.height*s)),Image.LANCZOS)
    dst=os.path.join(root,"assets","characters",dst_rel); os.makedirs(os.path.dirname(dst),exist_ok=True)
    im.save(dst,"WEBP",quality=90,alpha_quality=100,method=6); return dst,im.size,os.path.getsize(dst)
if __name__=="__main__":
    root=os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
    d,sz,b=run(sys.argv[1],sys.argv[2],root,checker="--checker" in sys.argv); print(f"{sys.argv[2]}  {sz[0]}x{sz[1]}  {b//1024} KB")
