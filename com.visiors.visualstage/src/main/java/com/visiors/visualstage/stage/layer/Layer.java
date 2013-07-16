package com.visiors.visualstage.stage.layer;

import java.awt.Color;

public interface Layer {

    int getID();

    void setID(int id);

    boolean isVisible();

    void setVisible(boolean visible);

    Color getBackgroundColor();

    void setBackgroundColor(Color bkc);

    int getOrder();

    void setOrder(int order);

    VisualGraph getGraphView();
}
