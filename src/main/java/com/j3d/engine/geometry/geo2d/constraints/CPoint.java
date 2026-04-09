package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.geo2d.HasParent;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

public class CPoint extends CObject implements HasParent<CLine> {
    private CLine parent;

    public CPoint(GPoint p) {
        super(p);
        parent = p.getParent().toConstraintObject();
    }

    @Override
    public CLine getParent() {
        return parent;
    }

    @Override
    public void setParent(CLine parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }
}
