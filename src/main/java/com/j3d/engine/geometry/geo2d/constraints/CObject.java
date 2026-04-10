package com.j3d.engine.geometry.geo2d.constraints;

import com.j3d.engine.geometry.constraints.ConstraintMirror;
import com.j3d.engine.geometry.geo2d.BaseObject;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.awt.*;
import java.util.UUID;

/**
 * Constraints Object is a baseclass snapshot of any {@link GObject}
 * @author Lehlogonolo Poole
 * @see GObject
 * @see BaseObject
 */
public class CObject implements BaseObject, ConstraintMirror {
    protected Color col = Color.BLACK;
    private Vector3 pivot;
    private UUID Id;
    private final GObject parent;
    private boolean isStale = false;

    /**
     * Default Constructor.
     * @param ref The parent GObject
     */
    public CObject(GObject ref)  {
        this.parent = ref;
        this.pivot = ref.getPivot();
        this.col = ref.getColour();
        this.Id = ref.getId();
    }

    @Override
    public Vector3 getPivot() {
        return pivot;
    }

    @Override
    public void setPivot(Vector3 pivot) {
        this.pivot = pivot;
    }

    @Override
    public Color getColour() {
        return col;
    }

    @Override
    public void setColour(Color colour) {
        col = colour;
    }

    @Override
    public UUID getId() {
        return Id;
    }

    @Override
    public void dispose() {
        deleteSelf();
    }

    @Override
    public void setId(UUID id) {
        Id = id;
    }

    @Override
    public boolean deleteSelf() {
        parent.detachConstraint();
        parent.toConstraintObject();
        // make a new constraint object that isnt this.
        isStale = true;
        return true;
    }

    @Override
    public boolean isStale() {
        return isStale;
    }
}
