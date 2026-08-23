
package com.j3d.jaiva.packs.getters;

import com.j3d.engine.math.matrix.Vector3;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColourGetters extends BaseLibrary {

    public ColourGetters() {
        super(LibraryType.CONTAINER);

        TriFunction<GettersPack.CallProperties, EngineObject, String, Object> g = (cp, eo, str) -> {
            Color v = TypeConverter.colorFromObject(cp, eo);
            return switch (str) {
                case "red" -> v.getRed();
                case "green" -> v.getGreen();
                case "blue" -> v.getBlue();
                case "alpha" -> v.getAlpha();
                default -> Token.voidValue(cp.call().lineNumber); // shouldn't happen
            };
        };

        GettersPack.putAliases(
                vfs, "colour",
                JDoc.builder()
                        .addDesc("Returns the Red property of the given Colour object"),
                (cp, eo) -> g.apply(cp, eo, "red"),
                "red", "r"
        );
        GettersPack.putAliases(
                vfs, "colour",
                JDoc.builder()
                        .addDesc("Returns the Green property of the given Colour object"),
                (cp, eo) -> g.apply(cp, eo, "green"),
                "green", "g"
        );
        GettersPack.putAliases(
                vfs, "colour",
                JDoc.builder()
                        .addDesc("Returns the Blue property of the given Colour object"),
                (cp, eo) -> g.apply(cp, eo, "blue"),
                "blue", "b"
        );
        GettersPack.putAliases(
                vfs, "colour",
                JDoc.builder()
                        .addDesc("Returns the Blue property of the given Colour object"),
                (cp, eo) -> g.apply(cp, eo, "alpha"),
                "alpha", "a"
        );


        GettersPack.putAliases(
                vfs, "colour",
                JDoc.builder()
                        .addDesc("v"),
                (cp, eo) -> {
                    Color v3 = TypeConverter.colorFromObject(cp, eo);
                    return new ColourFunction(v3);
                },
                "func", "asFunc", "f", "F"
        );
    }

    public static class ColourFunction extends BaseFunction {
        Color colour;
        public ColourFunction(Color col) {
            super(FunctionBuilder.start()
                    .name("col")
                    .arguments(
                            Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                                    "property", "The property to extract out the Vector3 object",
                                                    true, Argument.Type.ANY
                                            )
                                    )
                    )
                    .docs(
                            JDoc.builder()
                                    .addDesc("Allows the getting of Vector3 object properties")
                                    .addExample("""
                                            @ however the hell the imports looks
                                            
                                            maak colourArray <- {wherever the Colour object array comes from)!
                                            
                                            maak lambda <- f~(r, g, b, a) : r + g + b + a !
                                            
                                            maak v3 <- get_colour_asFunc(colourArray)!
                                            
                                            maak r <- v3("R")! @ get the Red component (or use 0)
                                            maak g <- v3("G")! @ get the Green component (or use 1)
                                            maak b <- v3("B")! @ get the Blue component (or use 2)
                                            maak a <- v3("A")! @ get the Alpha component (or use 3)
                                            
                                            maak sum <- v3(lamda)! @ Applies the given function to each input.
                                            
                                            maak colour <- v3()! @ returns a simple array of [r, g, b, a]
                                            """)
                    )
            );
            this.colour = col;
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            ArrayList<Object> list = new ArrayList<>(List.of(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha()));

            if (params.isEmpty() || (params.size() == 1 && params.getFirst() == null))
                return list;

            Object token = params.getFirst();

            Object value = Primitives.toPrimitive(token, false, config, scope);

            return switch (value) {
                case String s -> {
                    s = s.toLowerCase();
                    switch (s) {
                        case "r", "red": yield colour.getRed();
                        case "g", "green": yield colour.getGreen();
                        case "b", "blue": yield colour.getBlue();
                        case "a", "alpha": yield colour.getAlpha();
                        default: yield Token.voidValue(tFuncCall.lineNumber);
                    }
                }
                case Integer i -> {
                    switch (i) {
                        case 0: yield colour.getRed();
                        case 1: yield colour.getGreen();
                        case 2: yield colour.getBlue();
                        case 3: yield colour.getAlpha();
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
