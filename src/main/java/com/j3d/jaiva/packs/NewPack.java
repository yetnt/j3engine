package com.j3d.jaiva.packs;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GPolyline;
import com.j3d.jaiva.TypeConverter;
import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PublicLibrary(path = "j3d/obj/new", description = "new")
public class NewPack extends BaseLibrary {

    public NewPack() {
        add(new FNewPoint(), new FJoin());
    }

    static class FNewPoint extends BaseFunction {
        public FNewPoint() {
            super(FunctionBuilder.start()
                    .name("new_point")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("v3", "The vector3 position. Either a J3Engine Vector3 array or a 3 length array of doubles", false, Argument.Type.ARRAY))
                            .add(new AArgument("thingId", "The Id of the thing to add this to. Either a UUID object or string.", false, Argument.Type.ANY))
                            .add(new AArgument("col", "The colour. Either a J3Engine colour array or a at minimum 3 length, and max 4 length array of integers. If not set defualts to black", true, Argument.Type.ARRAY))
                    )
                    .setLib("j3d/obj/new")
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            Object pObj = callProperties.params().getFirst();
            Object iObj = callProperties.params().get(1);
            Object cObj = callProperties.params().size() > 2 ? callProperties.params().get(2) : null;

            Vector3 position = BasePack.parseV3(pObj, callProperties, this, 1);
            UUID thingId = BasePack.parseId(iObj, callProperties, this, 2);
            Color color = cObj instanceof ArrayList<?>
                    ? BasePack.parseCol(cObj, callProperties, this, 3)
                    : Color.BLACK;

            Thing thing =
                    StaticRefs.throwIfNull(StaticRefs.getSceneManager())
                            .finder()
                            .findFirst(
                                    Thing.class,
                                    Finder.idQuery(),
                                    thingId
                            ).getThing();
            if (thing == null)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "The id you put... does not exist",
                        callProperties.getLineNumber()
                );

            GPoint point = new GPoint(position);
            point.setColour(color);
            thing.addObjs(point);

            return TypeConverter.toJaivaReadable(point);
        }
    }

    static class FJoin extends BaseFunction {
        public FJoin() {
            super(FunctionBuilder.start()
                    .name("join")
                    .arguments(Arguments.getInstance()
                            .add(new AArgument("pointList", "The list of points", false, Argument.Type.ARRAY))
                            .add(new AArgument("target", "The Id of the thing to add this to. Either a UUID object or string.", false, Argument.Type.ANY))
                    )
                    .setLib("j3d/obj/new")
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            Object arr =  callProperties.params().getFirst();
            ArrayList<GPoint> points = new ArrayList<>();
            if (!(arr instanceof ArrayList<?> arrList))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", arr, ArrayList.class, callProperties.getLineNumber()
                );

            if (arrList.size() < 2)
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "Yeah Join less than 2 points bro.", callProperties.getLineNumber());

            for (Object o : arrList) {
                UUID idPointA = BasePack.parseId(o, callProperties, this, 1);

                GPoint A = StaticRefs.throwIfNull(StaticRefs.getSceneManager())
                        .finder().findFirst(GPoint.class, Finder.idQuery(), idPointA)
                        .getgObjectAs(GPoint.class);
                if (A == null)
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(),
                            "The id you put for one of the points in the list... does not exist",
                            callProperties.getLineNumber()
                    );

                points.add(A);
            }


            UUID thingIdA = BasePack.parseId(callProperties.params().get(1), callProperties, this, 2);

            Thing target = StaticRefs.throwIfNull(StaticRefs.getSceneManager())
                    .finder().findFirst(Thing.class, Finder.idQuery(), thingIdA)
                    .getThing();
            if (target == null)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "The id you put for the target thing... does not exist",
                        callProperties.getLineNumber()
                );

            GPolyline line = new GPolyline(points);
            target.addObjs(line);

            Thing.moveObjects((ArrayList) points, target);

            return TypeConverter.toJaivaReadable(line);
        }
    }
}
