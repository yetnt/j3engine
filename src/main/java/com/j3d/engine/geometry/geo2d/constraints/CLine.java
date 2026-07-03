package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CLine extends CObject implements HasParents<CTri> {

    public final CPoint pointA;
    public final CPoint pointB;
    private HashSet<CTri> parents = new HashSet<>();

    public CLine(GLine line) {
        super(line);
        pointA = line.getA().toConstraintObject();
        pointB = line.getB().toConstraintObject();
        parents = line.getParents().stream().map(GTri::toConstraintObject).collect(Collectors.toCollection(HashSet::new));
    }

    public CPoint pointB() {
        return pointB;
    }

    public CPoint pointA() {
        return pointA;
    }

    public double length() {
        return Math.sqrt(
                Math.pow(pointA.getPivot().getX()- pointB.getPivot().getX(), 2) + Math.pow(pointA.getPivot().getY()- pointB.getPivot().getY(), 2)
        );
    }

    public Stream<CPoint> getPointStream() {
        return Stream.of(pointA, pointB);
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
