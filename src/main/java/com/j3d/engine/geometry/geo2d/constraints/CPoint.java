package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.geo2d.HasParents;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CPoint extends CObject implements HasParents<CLine> {
    private HashSet<CLine> parents;

    public CPoint(GPoint p) {
        super(p);
        parents = p.getParents().stream().map(GLine::toConstraintObject).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public HashSet<CLine> getParents() {
        return parents;
    }

    @Override
    public void addParent(CLine parent) {
        parents.add(parent);
    }

    @Override
    public void removeParent(CLine parent) {
        parents.remove(parent);
    }
}
