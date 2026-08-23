package com.j3d.jaiva.packs.getters;

import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.j3d.utility.generic.func.TriFunction;
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
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;
import java.util.List;

public class Vector3Getters extends BaseLibrary {

    public Vector3Getters() {
        super(LibraryType.CONTAINER);

        TriFunction<GettersPack.CallProperties, EngineObject, String, Object> g = (cp, eo, str) -> {
            Vector3 v = Vector3.fromObject(cp, eo);
            return switch (str) {
                case "x" -> v.getX();
                case "y" -> v.getY();
                case "z" -> v.getZ();
                default -> Token.voidValue(cp.call().lineNumber); // shouldn't happen
            };
        };

        GettersPack.putAliases(
                vfs, "vector3",
                JDoc.builder()
                        .addDesc("Returns the X property of the given Vector3 object"),
                (cp, eo) -> g.apply(cp, eo, "x"),
                "x", "X", "left"
        );
        GettersPack.putAliases(
                vfs, "vector3",
                JDoc.builder()
                        .addDesc("Returns the Y property of the given Vector3 object"),
                (cp, eo) -> g.apply(cp, eo, "y"),
                "y", "Y", "up"
        );
        GettersPack.putAliases(
                vfs, "vector3",
                JDoc.builder()
                        .addDesc("Returns the Z property of the given Vector3 object"),
                (cp, eo) -> g.apply(cp, eo, "z"),
                "z", "Z", "forward"
        );


        GettersPack.putAliases(
                vfs, "vector3",
                JDoc.builder()
                        .addDesc("v"),
                (cp, eo) -> {
                    Vector3 v3 = Vector3.fromObject(cp, eo);
                    return new Vector3Function(v3);
                },
                "func", "asFunc", "f", "F"
        );
    }

    public static class Vector3Function extends BaseFunction {
        Vector3 vector;
        public Vector3Function(Vector3 vector) {
            super(FunctionBuilder.start()
                    .name("v3")
                    .arguments(
                            Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                                    "property",
                                                    "The property to extract out the Vector3 object",
                                                    true, Argument.Type.ANY
                                            )
                                    )
                    )
                    .docs(
                            JDoc.builder()
                                    .addDesc("Allows the getting of Vector3 object properties")
                                    .addExample("""
                                            @ however the hell the imports looks
                                            
                                            maak vector3Array <- {wherever the Vector3 object array comes from)!
                                            
                                            maak lambda <- f~(x, y, z) : x + y + z!
                                            
                                            maak v3 <- get_vector3_asFunc(vector3Array)!
                                            
                                            maak x <- v3("x")! @ get the X component (or use 0)
                                            maak y <- v3("y")! @ get the Y component (or use 1)
                                            maak z <- v3("z")! @ get the Z component (or use 2)
                                            
                                            maak sum <- v3(lamda)! @ Applies the given function to each input.
                                            
                                            maak vector3 <- v3()! @ returns a simple array of [x, y, z]
                                            """)
                    )
            );
            this.vector = vector;
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            ArrayList<Object> list = new ArrayList<>(List.of(vector.getX(), vector.getY(), vector.getZ()));

            if (params.isEmpty() || (params.size() == 1 && params.getFirst() == null))
                return list;

            Object token = params.getFirst();

            Object value = Primitives.toPrimitive(token, false, config, scope);

            return switch (value) {
                case String s -> {
                    s = s.toLowerCase();
                    switch (s) {
                        case "x": yield vector.getX();
                        case "y": yield vector.getY();
                        case "z": yield vector.getZ();
                        default: yield Token.voidValue(tFuncCall.lineNumber);
                    }
                }
                case Integer i -> {
                    switch (i) {
                        case 0: yield vector.getX();
                        case 1: yield vector.getY();
                        case 2: yield vector.getZ();
                        default: yield Token.voidValue(tFuncCall.lineNumber);
                    }
                }
                case BaseFunction function -> function.call(
                        list, config, scope, tFuncCall
                );
                default -> Token.voidValue(tFuncCall.lineNumber);
            };
        }
    }
}
