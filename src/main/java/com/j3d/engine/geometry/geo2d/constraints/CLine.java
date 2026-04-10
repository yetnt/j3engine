package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CLine extends CObject implements HasParents<CTri> {

    public final CPoint startPoint;
    public final CPoint endPoint;
    private HashSet<CTri> parents = new HashSet<>();

    public CLine(GLine line) {
        super(line);
        startPoint = line.getStart().toConstraintObject();
        endPoint = line.getEnd().toConstraintObject();
        parents = line.getParents().stream().map(GTri::toConstraintObject).collect(Collectors.toCollection(HashSet::new));
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
    public HashSet<CTri> getParents() {
        return parents;
    }

    @Override
    public void addParent(CTri parent) {
        parents.add(parent);
    }

    @Override
    public void removeParent(CTri parent) {
        parents.remove(parent);
    }
}
