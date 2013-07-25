package com.visiors.visualstage.renderer.cache;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import com.visiors.visualstage.renderer.Context;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.DrawingContext.Subject;
import com.visiors.visualstage.svg.SVGDocumentBuilder;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.transform.Transform;

public class VisualObjectPreviewGenerator implements Runnable {

    private final VisualGraphObject vgo;
    private String[][]              svgAttributes;
    private Thread                  t;
    private DrawingSubject        context;
    private ImageObserver           observer;

    public VisualObjectPreviewGenerator(VisualGraphObject vgo) {

        this.vgo = vgo;
    }

    public synchronized void createPreview(DrawingSubject context, ImageObserver observer) {

        this.context = context;
        this.observer = observer;

        t = new Thread(this, "preview generator");
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public synchronized Image createPreview(Context context) {

        String desc = vgo.getSVGDescription(context, false);
        if (desc == null) {
            return null;
        }
        svgAttributes = vgo.getSVGDocumentAttributes();
        Transform transform = vgo.getTransform();
        final Rectangle viewBox = transform.transformToScreen(vgo.getExtendedBoundary());
        viewBox.x -= transform.getTranslateX();
        viewBox.y -= transform.getTranslateY();

        SVGDocumentBuilder doc = new SVGDocumentBuilder();
        doc.createDocument(viewBox, null, null, null, null);
        doc.addContent(desc);
        doc.addDocumentAttributes(svgAttributes);
        doc.closeDocument();
        Image img = SVGUtil.svgToImage(doc.getDocument());
        Graphics g = img.getGraphics();
        // g.setColor(Color.orange);
        // g.drawRect(1, 1, viewBox.width-2, viewBox.height-2);
        return img;
    }

    @Override
    public void run() {

        String desc = vgo.getSVGDescription(context, false);
        if (desc != null) {
            context.subject = Subject.OBJECT;
            Image img = createPreview(context);
            // System.err.println(" Off-Screen Image created -ID:" + vgo.getID());
            if (observer != null && img != null) {
                observer.imageUpdate(img, ImageObserver.ALLBITS, 0, 0, img.getWidth(null), img.getHeight(null));
            }
        }
    }
}
