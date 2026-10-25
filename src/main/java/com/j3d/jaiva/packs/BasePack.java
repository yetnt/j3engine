package com.j3d.jaiva.packs;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.SceneObject;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.yetnt.utils.tuple.Triple;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@PublicLibrary(
        path = "j3d",
        description = "d"
)
@Exports({ObjectsPack.class, TestPack.class, Vec3Pack.class})
public class BasePack extends BaseLibrary {
    public BasePack() {
        add(new FLayers());
    }

    public static UUID parseId(Object obj, CallProperties callProperties, BaseFunction instance, int paramPos) throws InterpreterException {
        if (obj instanceof String st) {
            try {
                return UUID.fromString(st);
            } catch (IllegalArgumentException e) {
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "... that string does not parse toa  valid uuid",
                        callProperties.getLineNumber()
                );
            }
        }

        if (!(obj instanceof ArrayList<?> obj2))
            throw new InterpreterException.FunctionParametersException(
                    callProperties.scope(),
                    instance,
                    paramPos + "",
                    obj,
                    ArrayList.class,
                    callProperties.getLineNumber()
            );
        Object innerObj = obj2.getFirst();

        if (!(innerObj instanceof ArrayList<?>))
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(),
                    "idk wtf " + innerObj + " is doing in a J3Engine id list",
                    callProperties.getLineNumber()
            );


        EngineObject object;
        try {
            object = EngineObject.from(obj2);
        } catch (IllegalArgumentException e) {
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(),
                    "Okay so like either put a valid uuid string or a uuid J3Engine list.",
                    callProperties.getLineNumber()
            );
        }
        Object idObj = TypeConverter.from(object);

        if (idObj instanceof SceneObject sceneObject)
            return sceneObject.getId();


        if (!(idObj instanceof UUID uuid)) {
            throw new InterpreterException.WtfAreYouDoingException(
                    callProperties.scope(),
                    "Bro put in a " + object.getType() + " in the palce of a uuid",
                    callProperties.getLineNumber()
            );
        }

        return uuid;

    }

    public static Vector3 parseV3(Object obj, CallProperties callProperties, BaseFunction instance, int paramPos) throws InterpreterException {
        if (!(obj instanceof ArrayList<?> obj2))
            throw new InterpreterException.FunctionParametersException(
                    callProperties.scope(),
                    instance,
                    paramPos + "",
                    obj,
                    ArrayList.class,
                    callProperties.getLineNumber()
            );
        Object innerObj = obj2.getFirst();

        if (!(innerObj instanceof Number)) {
            // assume EngineObject
            EngineObject object;
            try {
                object = EngineObject.from(obj2);
            } catch (IllegalArgumentException e) {
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "Okay so like either put a valid vector3 list (3 numbers) or a vector3 J3Engine list.",
                        callProperties.getLineNumber()
                );
            }
            Object v3Obj = TypeConverter.from(object);

            if (!(v3Obj instanceof Vector3 vector3)) {
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "Bro put in a " + object.getType() + " in the palce of a Vector3",
                        callProperties.getLineNumber()
                );
            }

            return vector3;
        } else {
            // length of 3
            if (obj2.size() != 3)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "Does have a Vector3 have " + obj2.size() + " elements? i dont think so.",
                        callProperties.getLineNumber()
                );
            ArrayList<Double> v3List = new ArrayList<>();
            for (Object obj3 : obj2) {
                if (obj3 instanceof Number num)
                    v3List.add(num.doubleValue());
                else
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(), "Vector3's have " + obj3 + " in them now?",
                            callProperties.getLineNumber()
                    );
            }
            return Triple.from(v3List).mapTo(Vector3::new);
        }
    }

    public static Color parseCol(Object obj, CallProperties callProperties, BaseFunction instance, int paramPos) throws InterpreterException {
        if (!(obj instanceof ArrayList<?> obj2))
            throw new InterpreterException.FunctionParametersException(
                    callProperties.scope(),
                    instance,
                    paramPos + "",
                    obj,
                    ArrayList.class,
                    callProperties.getLineNumber()
            );

        Object innerObj = obj2.getFirst();

        if (!(innerObj instanceof Number)) {
            // assume EngineObject
            EngineObject object;
            try {
                object = EngineObject.from(obj2);
            } catch (IllegalArgumentException e) {
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "Okay so like either put a valid colour list (3 numbers) or a colour J3Engine list.",
                        callProperties.getLineNumber()
                );
            }
            Object v3Obj = TypeConverter.from(object);

            if (!(v3Obj instanceof Color vector3)) {
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),
                        "Bro put in a " + object.getType() + " in the palce of a colour",
                        callProperties.getLineNumber()
                );
            }

            return vector3;
        } else {
            // length of 3 or 4
            if (obj2.size() != 3 && obj2.size() != 4)
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "Does have a colour have " + obj2.size() + " elements? i dont think so.",
                        callProperties.getLineNumber()
                );
            ArrayList<Integer> colList = new ArrayList<>();
            for (Object obj3 : obj2) {
                if (obj3 instanceof Number num)
                    colList.add(num.intValue());
                else
                    throw new InterpreterException.WtfAreYouDoingException(
                            callProperties.scope(), "Colours have " + obj3 + " in them now?",
                            callProperties.getLineNumber()
                    );
            }
            return obj2.size() == 3 ?
                    Triple.from(colList).mapTo(Color::new)
                    : new Color(colList.getFirst(), colList.get(1), colList.get(2), colList.get(3));
        }
    }

    static class FLayers extends BaseFunction {
        public FLayers() {
            super(FunctionBuilder.start()
                    .setLib("j3d")
                    .name("layers")
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            return StaticRefs.throwIfNull(StaticRefs.getSceneManager())
                    .layers
                    .stream()
                    .map(TypeConverter::toJaivaReadable)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }
}
