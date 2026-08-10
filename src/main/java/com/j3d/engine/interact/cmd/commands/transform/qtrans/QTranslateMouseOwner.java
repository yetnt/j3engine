package com.j3d.engine.interact.cmd.commands.transform.qtrans;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.scene.nodes.geometry.GPoint;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * A {@link MouseOwner} responsible for handling "quick translate" functionality.
 * This class allows for displacing a group of selected {@link GPoint}s by a mouse movement delta,
 * projecting the 2D mouse movement onto a 3D plane for intuitive translation.
 * <p>
 * It calculates the center of the selected points, establishes a projection plane based on the camera's view,
 * and translates the points in 3D space according to the mouse's 2D movement.
 * </p>
 * @author Lehlogonolo Poole
 * @see MOwner#QTRANS
 * @see MouseOwner
 * @see com.j3d.engine.interact.cmd.commands.transform.qtrans.QuickTranslateCmd
 * @implSpec It's kinda funky. and math heavy
 */
public class QTranslateMouseOwner extends MouseOwner {

    ArrayList<GPoint> pointsToTransform = new ArrayList<>();
    AxisPlane cachedPlane = null;
    Vector3 cachedCentre = null;


    public QTranslateMouseOwner() {
        super(MOwner.QTRANS, 0);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isNotOwner()) return;
        wrap(e);

        if (pointsToTransform.isEmpty()) return;

        // get centre
        Vector3 centre = calculateCentre();
        if (cachedPlane == null || cachedCentre == null) {
            cachedPlane = AxisPlane.usingOrigin(
                    centre,
                    StaticRefs
                            .getCamera()
                            .viewPlane() // view plane (NormalPlane)
                            .toAxisPlane()
            ); // set the axis plane origin to the selection origin
            cachedCentre = centre;
        }

        // mouse location in cartesian terms. (using global properties)
        CartesianPoint mouseLocCart = getMouseLocFromPhysical().toPoint();

        double abs = Math.abs(cachedCentre.distance(centre));
        double m = abs == 0 ? 1 : Math.log1p(abs);
        mouseLocCart = new CartesianPoint(
                -mouseLocCart.x*m,
                -mouseLocCart.y*m
        );

        // convert back to world
        Vector3 worldMouseLoc = cachedPlane.toWorld(mouseLocCart);

        mouseLoc = worldMouseLoc;

        // get the vector difference between the original centre and da new mouseLocation
        Vector3 diff = worldMouseLoc.sub(centre);

        // apply to all stuff.
        pointsToTransform.forEach(p -> p.setPivot(p.getPivot().add(diff)));

        StaticRefs.getMainPanel().repaint();
    }

    private Vector3 calculateCentre() {
            return Vector3.reduceToVector3(
                    pointsToTransform.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new))
                    , Vector3::add).div(pointsToTransform.size());
    }

    @Override
    public void clear() {
        super.clear();
        pointsToTransform.clear();
        cachedPlane = null;
        cachedCentre = null;
        setOldPoint(new Point(0, 0));
    }

    public void using(ArrayList<GPoint> pointsToTransform) {
        this.pointsToTransform = pointsToTransform;
    }

    Vector3 mouseLoc;

    public Vector3 mouseLoc() {
        return mouseLoc;
    }
}
