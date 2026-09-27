#!/usr/bin/env python3
"""生成启动图标 PNG（adaptive icon 在 Android 8 以下的回退）。"""
import math
import os
from PIL import Image, ImageDraw

BG = (0, 0, 0, 255)
WHITE = (255, 255, 255, 255)

BASE = os.path.join(os.path.dirname(__file__), "app", "src", "main", "res")
SIZES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def draw_globe(img: Image.Image):
    s = img.size[0]
    d = ImageDraw.Draw(img)
    cx = cy = s / 2
    r = s * 0.26
    w = max(2, int(round(s * 0.032)))

    # 外圆
    d.ellipse([cx - r, cy - r, cx + r, cy + r], outline=WHITE, width=w)
    # 经线（椭圆）
    rw = r * 0.45
    d.ellipse([cx - rw, cy - r, cx + rw, cy + r], outline=WHITE, width=w)
    # 纬线：赤道 + 南北回归线
    for k in (0, 0.34, -0.34):
        y = cy + k * r
        half = r * math.sqrt(max(0.0, 1 - k * k))
        d.line([cx - half, y, cx + half, y], fill=WHITE, width=w)


def make_icon(size: int, round_icon: bool) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if round_icon:
        d.ellipse([0, 0, size - 1, size - 1], fill=BG)
    else:
        d.rectangle([0, 0, size, size], fill=BG)
    draw_globe(img)
    return img


def main():
    for qual, size in SIZES.items():
        out_dir = os.path.join(BASE, f"mipmap-{qual}")
        os.makedirs(out_dir, exist_ok=True)
        make_icon(size, False).save(os.path.join(out_dir, "ic_launcher.png"))
        make_icon(size, True).save(os.path.join(out_dir, "ic_launcher_round.png"))
        print(f"{qual}: {size}x{size}")


if __name__ == "__main__":
    main()
