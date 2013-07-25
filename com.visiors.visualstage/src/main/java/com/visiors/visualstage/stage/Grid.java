package com.visiors.visualstage.stage;

import java.awt.Color;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.transform.Transform;

public class Grid {

    public enum GridStyle {
        Line, Dot, Cross
    };

    /* Grid line color */
    private final Color lineColor;
    private final Color sublineColor;

    /* Centimetre unit */
    private static final int CROSS_SIZE = 4;
    private final Transform transformer;

    public Grid(Transform transformer) {

        this.transformer = transformer;
        lineColor = new Color(0xD8E5E5);// UIManager.getColor("MinuetLnF.Ruler.Line.Color");
        sublineColor = new Color(0xEBF2F2); // UIManager.getColor("MinuetLnF.Ruler.Subline.Color");
    }

    public void draw(Canvas canvas, Rectangle r, double pixelsPreUnit, GridStyle style) {

        double transUnit = transformer.getScale() * pixelsPreUnit;

        while (transUnit < pixelsPreUnit / 2) {
            transUnit *= 2;
        }

        while (transUnit > pixelsPreUnit * 2) {
            transUnit /= 2;
        }

        double xOffset = Math.abs(transformer.getTranslateX() - r.x) % transUnit - transUnit;
        double yOffset = Math.abs(transformer.getTranslateY() - r.y) % transUnit - transUnit;

        if ((int) xOffset < 0) {
            xOffset += transUnit;
        }
        if ((int) yOffset < 0) {
            yOffset += transUnit;
        }

        canvas.setStroke(1.0f);
        switch (style) {
            case Line:

                canvas.setColor(sublineColor);
                for (double x = r.x + xOffset + transUnit / 2; x < r.x + r.width; x += transUnit) {
                    canvas.drawLine((int) x, r.y, (int) x, r.y + r.height);
                }

                for (double y = r.y + yOffset + transUnit / 2; y < r.y + r.height; y += transUnit) {
                    canvas.drawLine(r.x, (int) y, r.x + r.width, (int) y);
                }

                canvas.setColor(lineColor);
                int test = 0;
                for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit, test++) {
                    canvas.drawLine((int) x, r.y, (int) x, r.y + r.height);
                    // System.err.println("Number of drawn lines: " + test);
                }

                for (double y = r.y + yOffset; y < r.y + r.height; y += transUnit) {
                    canvas.drawLine(r.x, (int) y, r.x + r.width, (int) y);
                }

                break;
            case Dot:
                canvas.setColor(lineColor);
                for (double y = r.y + yOffset; y < r.y + r.height; y += transUnit) {
                    for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
                        canvas.drawRect((int) x, (int) y, 1, 1);
                    }
                }
                break;
            case Cross:
                canvas.setColor(lineColor);
                for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
                    for (double y = r.y + yOffset - Grid.CROSS_SIZE; y < r.y + r.height; y += transUnit) {
                        canvas.drawLine((int) x, (int) y - Grid.CROSS_SIZE, (int) x, (int) y + Grid.CROSS_SIZE);
                    }
                }
                for (double y = r.y + yOffset - Grid.CROSS_SIZE; y < r.y + r.height; y += transUnit) {
                    for (double x = r.x + xOffset; x < r.x + r.width; x += transUnit) {
                        canvas.drawLine((int) x - Grid.CROSS_SIZE, (int) y, (int) x + Grid.CROSS_SIZE, (int) y);
                    }
                }
        }

        canvas.setColor(lineColor);
        canvas.drawRect(r.x, r.y, r.width, r.height);
    }

}
