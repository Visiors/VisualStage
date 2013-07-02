package com.visiors.visualstage.system;


public interface SystemUnit {

    public enum Unit {
        mm, cm, inch, pixel
    }

    public void setUnit(Unit unit);

    public double getPixelsPerUnit();

    public Unit getUnit();

    public int mmToDPI(int mmValue);

    public void addUnitChangeListener(SystemUnitChangeListener listener);

    public void removeUnitChangeListener(SystemUnitChangeListener listener);
}
