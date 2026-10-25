package com.j3d.jaiva.packs;

import com.j3d.engine.math.matrix.Vector3;
import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.yetnt.utils.functional.function.ThrowableBiFunction;
import com.yetnt.utils.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

@PublicLibrary(
        path = "j3d/v3", description = "v3"
)
public class Vec3Pack extends BaseLibrary {
    static interface Vec3Func extends BiFunction<Vector3, Vector3, Object> {
    }
    static interface Custom extends ThrowableBiFunction<CallProperties, BaseFunction, Vector3, InterpreterException> {}
    static interface DocBuilder extends Function<JDocBuilder, JDocBuilder> {};

    public Vec3Pack() {
        add(
                v3(Vector3::add, "add", (d) -> d.addDesc("Adds the 2 given vectors' components")),
                v3(Vector3::sub, "sub", (d) -> d.addDesc("Subtracts the 2 given vectors' components")),
                v3(Vector3::mult, "mult", (d) -> d.addDesc("Multiplies the 2 given vectors' components")),
                v3(Vector3::cross, "cross", (d) -> d.addDesc(
                        "Calculates the cross product between the 2 given vectors"
                )),
                v3(Vector3::dot, "dot", (d) -> d.addDesc(
                        "Calculates the dot product between the 2 given vectors"
                )),
                v3(Vector3::distance, "dist", (d) -> d.addDesc(
                        "Calculates the distance between the 2 given vectors' components"
                )),
                v3(Vector3::normalize, "norm", (d) -> d.addDesc("Normalizes the given vector"),true),
                v3(Vector3::magnitude, "mag", (d) -> d.addDesc("Returns the magnitude of the given vector"), true),
                v3(Vector3::equals, "equal",  (d) -> d.addDesc("Gives whether the 2 given vectors' components are equal")),
                v3(Vector3::isNotZero, "isNotZero",  (d) -> d.addDesc("Returns true if any property of this vector is not zero."),true),
                v3(Vector3::zeroClamp, "zeroClamp",  (d) -> d.addDesc(
                        "Clamps the given vector by comparing it's components to an epsilon of 10^-10. If a " +
                                "component is less than that it gets clamped to 0"),true
                ),
                v3((callProperties, func) -> {
                    Vector3 v1 = BasePack.parseV3(
                            callProperties.params().getFirst(),
                            callProperties,
                            func,
                            1
                    );
                    Vector3 v2 = BasePack.parseV3(
                            callProperties.params().get(1),
                            callProperties,
                            func,
                            2
                    );

                    if (!(callProperties.params().get(2) instanceof Number num))
                        throw new InterpreterException.WtfAreYouDoingException(
                                callProperties.scope(),
                                "So you dont input a number for rotating?",
                                callProperties.getLineNumber()
                        );

                    return v1.rotateAroundAxis(v2, num.doubleValue());
                }, "rot",
                        (d) -> d.addDesc("Uses  Rodrigues' rotation formula magic"),
                        new AArgument("axis", "The vector3 axis to rotate around", false, Argument.Type.ARRAY),
                        new AArgument("angleDeg", "angle in degrees", false, Argument.Type.NUMBER))
        );
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private static BaseFunction v3(Vec3Func func, String name, DocBuilder docBuilder) {
        return v3AsTwo(func, null, null, docBuilder, name, false);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private static BaseFunction v3(Function<Vector3, Object> func, String name, DocBuilder docBuilder) {
        return v3AsTwo(null, func, null, docBuilder, name, false);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private static BaseFunction v3(Function<Vector3, Object> func, String name, DocBuilder docBuilder, boolean noSecondParam) {
        return v3AsTwo(null, func, null, docBuilder, name, noSecondParam);
    }


    @NotNull
    @Contract("_, _, _, _ -> new")
    private static <T> BaseFunction v3(Custom func, String name, DocBuilder docBuilder, AArgument ...aArgument) {
        return v3AsTwo(null, null, new Pair<>(func, aArgument), docBuilder, name, false);
    }

    @NotNull
    @Contract("_, _, _, _, _, _ -> new")
    private static <T> BaseFunction v3AsTwo(
            Vec3Func func,
            Function<Vector3, Object> func2,
            Pair<Custom, AArgument[]> func3,
            @NotNull DocBuilder builder,
            String name,
            boolean noSecondParam) {

        JDocBuilder jDocBuilder = builder.apply(JDoc.builder()
                .sinceVersion("1.3.0"));


        Arguments arguments = Arguments.getInstance()
                .add(new AArgument("vec3", "The vector3 list, either a J3Engine Object or 3 element array", false, Argument.Type.ARRAY));

        if (!noSecondParam) {
            if (func3 == null)
                arguments.add(new AArgument("secondVec3", "The vector3 list, either a J3Engine Object or 3 element array", false, Argument.Type.ARRAY));
            else {
                for (AArgument arg : func3.getSecond()) {
                    arguments.add(arg);
                }
            }
        }
        return new BaseFunction(FunctionBuilder.start()
                .name("v3_" + name)
                .arguments(arguments)
                .docs(jDocBuilder)
        ) {
            @Override
            public Object call(CallProperties callProperties) throws Exception {
                checkParams(callProperties.toCheckParams());


                if (func3 != null) {
                    Object apply = func3.getFirst().apply(callProperties, this);
                    return apply instanceof Vector3 vv ? vv.toTriple().toArrayList() : apply;
                }

                Vector3 v = BasePack.parseV3(
                        callProperties.params().getFirst(),
                        callProperties,
                        this,
                        1
                );

                if (func != null) {
                    Vector3 v2 = BasePack.parseV3(
                            callProperties.params().get(1),
                            callProperties,
                            this,
                            2
                    );

                    Object apply = func.apply(v, v2);
                    return apply instanceof Vector3 vv ? vv.toTriple().toArrayList() : apply;
                } else {
                    Object apply = func2.apply(v);
                    return apply instanceof Vector3 vv ? vv.toTriple().toArrayList() : apply;
                }
            }
        };
    }
}
