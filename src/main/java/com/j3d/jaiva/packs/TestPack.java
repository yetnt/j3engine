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
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

@PublicLibrary(path = "j3d/test", description = "tes")
public class TestPack extends BaseLibrary {

    public TestPack() {
        super();

        add(new FEcho());
        add(new FFind());
        add(new FValidate());
    }

    public static class FEcho extends BaseFunction {
        public FEcho() {
            super(FunctionBuilder.start()
                    .name("echo")
                    .setLib("j3d/test")
            );
            freeze(); // freeze so user cant edit
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams()); // will do parameter checking for me

            return TypeConverter.toJaivaReadable(
                            StaticRefs.throwIfNull(
                                    StaticRefs.throwIfNull(StaticRefs.getSceneManager()).finder()
                                    .findFirst(GTri.class, (s, v) -> s.getId().toString().contains(v), "3")
                                    .getgObject()
                            )
                    );
        }
    }

    public static class FFind extends BaseFunction {
        public FFind() {
            super(FunctionBuilder.start()
                    .name("find")
                    .setLib("j3d/test")
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument("id", "the id to find", false, Argument.Type.STRING))
                    )
            );
            freeze();
        }

        @Override
        public Object call(@NotNull CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            Object id = callProperties.params().getFirst();

            if (id instanceof String string) {
                UUID uuid =  UUID.fromString(string);

                FindResult result = StaticRefs.throwIfNull(StaticRefs.getSceneManager()).finder().findFirst(
                        GObject.class, Finder.idQuery(), uuid
                );

                if (result.containsGObject()) {
                    return TypeConverter.toJaivaReadable(StaticRefs.throwIfNull(result.getgObject()));
                }
            }
            return Token.voidValue(callProperties.getLineNumber());
        }
    }

    public static class FValidate extends BaseFunction {
        public FValidate() {
            super(FunctionBuilder.start()
                    .name("isValid")
                    .setLib("j3d/test")
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument("array", "The array object to validate", false, Argument.Type.ARRAY))
                    )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());

            Object possibleArr = callProperties.params().getFirst();

            if (!(possibleArr instanceof ArrayList<?> array))
                throw new IllegalArgumentException("An input array was not given");

            EngineObject out = TypeConverter.fromArr(array);

            if (GObjectRegistry.isGObject(out.getType())) {
                Object t = TypeConverter.getGObject(out, null);
                return t instanceof GObject;
            }
            return false;
        }
    }
}
