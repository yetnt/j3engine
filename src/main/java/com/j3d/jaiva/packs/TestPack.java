package com.j3d.jaiva.packs;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GObjectRegistry;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;
import java.util.UUID;

public class TestPack extends BaseLibrary {
    public static String path = "j3d";
    public TestPack() {
        super(LibraryType.LIB, "j3d");

        vfs.put("echo", new FEcho());
        vfs.put("find", new FFind());
        vfs.put("isValid", new FValidate());
    }

    public static class FEcho extends BaseFunction {
        public FEcho() {
            super(FunctionBuilder.start()
                    .name("echo")
            );
            freeze(); // freeze so user cant edit
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope); // will do parameter checking for me

            return TypeConverter.toJaivaReadable(
                            StaticRefs.getSceneManager().finder()
                                    .findFirst(GTri.class, (s, v) -> s.getId().toString().contains(v), "3")
                                    .getgObject()
                    );
        }
    }

    public static class FFind extends BaseFunction {
        public FFind() {
            super(FunctionBuilder.start()
                    .name("find")
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument("id", "the id to find", false, Argument.Type.STRING))
                    )
            );
            freeze();
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);

            Object id = Primitives.toPrimitive(params.getFirst(), false, config, scope);

            if (id instanceof String string) {
                UUID uuid =  UUID.fromString(string);

                FindResult result = StaticRefs.getSceneManager().finder().findFirst(
                        GObject.class, Finder.idQuery(), uuid
                );

                if (result.containsGObject()) {
                    return TypeConverter.toJaivaReadable(result.getgObject());
                }
            }
            return Token.voidValue(tFuncCall.lineNumber);
        }
    }

    public static class FValidate extends BaseFunction {
        public FValidate() {
            super(FunctionBuilder.start()
                    .name("isValid")
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument("array", "The array object to validate", false, Argument.Type.ARRAY))
                    )
            );
            freeze();
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);

            Object possibleArr = Primitives.toPrimitive(params.getFirst(),  false, config, scope);

            if (!(possibleArr instanceof ArrayList<?> array))
                throw new IllegalArgumentException("An input array was not given");

            EngineObject out = TypeConverter.fromArr(array);

            if (GObjectRegistry.isGObject(out.getType())) {
                GObject t = TypeConverter.getGObject(out);
                return t != null;
            }
            return false;
        }
    }
}
