package com.visiors.visualstage.system;

import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.util.PrinterUtil;

public class DefaultSystemUnitService implements SystemUnit {

    private Unit   unit;
    private double pixelsPreUnit;

    public DefaultSystemUnitService() {

        setUnit(Unit.cm);
    }

    @Override
    public void setUnit(Unit unit) {

        if (this.unit != unit) {
            switch (unit) {
                case mm:
                    pixelsPreUnit = PrinterUtil.PRINT_DPI / PrinterUtil.mmPerInch;
                    break;
                case cm:
                    pixelsPreUnit = PrinterUtil.PRINT_DPI / PrinterUtil.mmPerInch * 10.0;
                    break;
                case inch:
                    pixelsPreUnit = PrinterUtil.PRINT_DPI;
                    break;
                case pixel:
                    pixelsPreUnit = 1;
                    break;
            }
            fireUnitChanged();
        }
    }

    @Override
    public Unit getUnit() {

        return unit;
    }

    @Override
    public int mmToDPI(int mmValue) {

        return (int) (mmValue / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI);
    }

    @Override
    public double getPixelsPerUnit() {

        return pixelsPreUnit;
    }

    // //////////////////////////////////////////////////////////////////////////
    // Notifications - sending notification to listener

    protected List<SystemUnitChangeListener> systemUnitChangeListener = new ArrayList<SystemUnitChangeListener>();

    @Override
    public void addUnitChangeListener(SystemUnitChangeListener listener) {

        if (!systemUnitChangeListener.contains(listener)) {
            systemUnitChangeListener.add(listener);
        }
    }

    @Override
    public void removeUnitChangeListener(SystemUnitChangeListener listener) {

        systemUnitChangeListener.remove(listener);
    }

    private void fireUnitChanged() {

        for (SystemUnitChangeListener l : systemUnitChangeListener) {
            l.unitChanged();
        }
    }
}
