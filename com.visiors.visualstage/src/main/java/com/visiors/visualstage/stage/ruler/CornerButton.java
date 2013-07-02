package com.visiors.visualstage.stage.ruler;

import java.awt.Color;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.Device;

public class CornerButton {

    final Ruler       hRuler;
    final Ruler       vRuler;
    final Grid        grid;
    Color             bkCorner;
    Color             lineColor;
    private final int size;

    public CornerButton(Ruler hRler, Ruler vRuler, Grid grid, int size) {

        hRuler = hRler;
        this.vRuler = vRuler;
        this.grid = grid;
        this.size = size;
        bkCorner = new Color(0xE7F1F1);// UIManager.getColor("MinuetLnF.Ruler.Button");
        lineColor = new Color(0x4b5d6f);// UIManager.getColor("MinuetLnF.Ruler.Button");
    }

    public void draw(Device device, Rectangle r) {

        device.setColor(bkCorner);
        device.fillRect(r.x, r.y, size, size);

        device.setColor(lineColor);
        // device.drawRect(r.x, r.y, size, size);
        device.drawLine(r.x, r.y + size, r.x + size, r.y + size);
        device.drawLine(r.x + size / 2 - 3, r.y + size / 2 - 3, r.x + size / 2 + 3, r.y + size / 2 + 3);
    }

}
