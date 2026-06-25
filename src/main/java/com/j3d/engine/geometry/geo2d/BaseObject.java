package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.storage.files.protocol.proj.ProjectFileV1;
import com.j3d.ui.dialog.Spinner;

import java.awt.*;
import java.util.UUID;

public interface BaseObject {
    /**
     * Returns the pivot point.
     * @implSpec Child-classes need to guarantee that this returns the
     * exact mathematical pivot point of this object. Which if it does
     * not define as some special point, nees to be it's centre.
     * @return a CartesianPoint
     */
    Vector3 getPivot();
    /**
     * Sets the pivot point.
     * @param pivot The new pivot point.
     */
    void setPivot(Vector3 pivot);
    /**
     * Returns this geometry's color
     * @return The Color
     */
    Color getColour();
    /**
     * Sets the colour
     * @param colour The new colour
     */
    void setColour(Color colour);
    /**
     * Returns this geometry's unique identifier
     * @return The UUID
     */
    UUID getId();
    /**
     * Sets this geometry's unique identifier
     * @implSpec This is intended to be used when a child is created from
     * file loading or anything where it hasnt had a UUID attached to it already.
     * Otherwise the UUID is treated as immutable.
     * @param id The new UUID
     * @see ProjectFile#readFile(String, String, Spinner)
     * @see GPoint#fromRaw(String, Vector3)
     * @see GLine#fromRaw(String, GPoint, GPoint)
     * @see GTri#fromRaw(String, Color, GLine, GLine, GLine)
     */
    void setId(UUID id);
    /**
     * Deletes itself
     * @return true if the object was deleted
     * @implNote This is meant to be overriden by inheritors.
     */
    boolean deleteSelf();
}
