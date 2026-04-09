package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.geo2d.HasParent;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;

import java.util.stream.Stream;

public class CLine extends CObject implements HasParent<CTri> {

    public final CPoint startPoint;
    public final CPoint endPoint;
    private CTri parent;

    public CLine(GLine line) {
        super(line);
        startPoint = line.getStart().toConstraintObject();
        endPoint = line.getEnd().toConstraintObject();
        parent = line.getParent().toConstraintObject();
    }

    public CPoint getEnd() {
        return endPoint;
    }

    public CPoint getStart() {
        return startPoint;
    }

    public double length() {
        return Math.sqrt(
                Math.pow(startPoint.getPivot().getX()- endPoint.getPivot().getX(), 2) + Math.pow(startPoint.getPivot().getY()- endPoint.getPivot().getY(), 2)
        );
    }

    public Stream<CPoint> getPointStream() {
        return Stream.of(startPoint, endPoint);
    }

    @Override
    public CTri getParent() {
        return parent;
    }

    @Override
    public void setParent(CTri parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }
}
