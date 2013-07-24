package com.visiors.visualstage.stage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.transform.Transformer;

public class Ruler {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;
    public static final int CENTIMETER = 0;
    public static final int INCH       = 1;

    /* centimeter unit */

    private final int       size;
    private final int       style;
    private final Color     bkColor;
    private final Color     lineColor;
    private final Font      font       = new Font("SansSerif", Font.PLAIN, 10);
    private final Transformer transformer;

    public Ruler(int style, Transformer transformer, int size) {

        this.transformer = transformer;
        this.style = style;
        this.size = size;

        bkColor = new Color(0xF1F8F8); // UIManager.getColor("MinuetLnF.Ruler.Background");
        lineColor = new Color(0x4b5d6f); // UIManager.getColor("MinuetLnF.Ruler.Line.Color");
    }

    public void draw(Device device, Rectangle r, double pixelsPreUnit, int outlineStep, String unitName) {

        final int sublineHeight = 3 * size / 4;
        double transUnit = pixelsPreUnit * transformer.getScale();
        boolean outline;
        Rectangle rTrans = new Rectangle(r);

        double fCompress = transUnit;
        while (transUnit < pixelsPreUnit / 2) {
            transUnit *= 2;
        }

        while (transUnit > pixelsPreUnit * 2.5) {
            transUnit /= 2.5;
        }
        fCompress = fCompress / transUnit;

        device.setFont(font);
        if (style == Ruler.HORIZONTAL) {
            device.setColor(bkColor);
            device.fillRect(r.x, r.y, r.width, size);
            device.setColor(lineColor);

            rTrans.x += transformer.getTranslateX();

            for (double x = rTrans.x, n = 0; x < rTrans.width; x += transUnit, n++) {
                outline = n % outlineStep == 0;
                device.drawLine((int) x, rTrans.y + size, (int) x, rTrans.y + (outline ? 0 : sublineHeight));
                if (outline) {
                    final String text = (int) Math.round((x - rTrans.x) / transUnit / fCompress) + " " + unitName;
                    device.drawString(text, (int) x + 3, rTrans.y + size - 6);
                }
            }

            for (double x = rTrans.x, n = 0; x > 0; x -= transUnit, n++) {
                outline = n % outlineStep == 0;
                device.drawLine((int) x, rTrans.y + size, (int) x, rTrans.y + (outline ? 0 : sublineHeight));
                if (outline) {
                    final String text = (int) Math.round((x - rTrans.x) / transUnit / fCompress) + " " + unitName;
                    device.drawString(text, (int) x + 3, rTrans.y + size - 6);

                }
            }
            // ruler border
            device.drawLine(0, size, r.width, size);
        } else {
            device.setColor(bkColor);
            device.fillRect(r.x, r.y, size, r.height);
            device.setColor(lineColor);

            rTrans.y += transformer.getTranslateY();

            for (double y = rTrans.y, n = 0; y < rTrans.height; y += transUnit, n++) {
                outline = n % outlineStep == 0;
                device.drawLine(rTrans.x + size, (int) y, rTrans.x + (outline ? 0 : sublineHeight), (int) y);
                if (outline) {
                    final String text = (int) Math.round((y - rTrans.y) / transUnit / fCompress) + " " + unitName;
                    device.drawString(text, rTrans.x + 4, (int) y + 3, 90);

                }
            }
            for (double y = rTrans.y, n = 0; y > 0; y -= transUnit, n++) {
                outline = n % outlineStep == 0;
                device.drawLine(rTrans.x + size, (int) y, rTrans.x + (outline ? 0 : sublineHeight), (int) y);
                if (outline) {
                    final String text = (int) Math.round((y - rTrans.y) / transUnit / fCompress) + " " + unitName;
                    device.drawString(text, rTrans.x + 4, (int) y + 3, 90);
                }
            }

            // ruler border
            device.drawLine(r.x + size, r.y, r.x + size, r.height);
        }
    }
}
