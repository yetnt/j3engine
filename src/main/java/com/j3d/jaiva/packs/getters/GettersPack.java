package com.j3d.jaiva.packs.getters;

import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GObjectRegistry;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.j3d.utility.generic.func.ThrowableTriFunction;
import com.j3d.utility.generic.func.TriFunction;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.libBuilders.func.*;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.JDocBuilder;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GettersPack extends BaseLibrary {

    @FunctionalInterface
    public interface Getter {
        Object applyExc(CallProperties call, EngineObject object) throws JaivaException;
    }

    @FunctionalInterface
    public interface GetterOf<T> {
        T applyExc(CallProperties call, EngineObject object) throws JaivaException;
    }

    public record CallProperties (
            TFuncCall call,
            Scope scope,
            IConfig<Object> config
    ) {}

    public static final ThrowableTriFunction<GetterOf<EngineObject>, CallProperties, EngineObject, Object, JaivaException> referenceTransformer = (f, t, e) -> {
        EngineObject reference = f.applyExc(t, e);
        GObject obj = TypeConverter.getReference(reference);
        if (obj == null) return Token.voidValue(t.call.lineNumber);
        return TypeConverter.toJaivaReadable(obj);
    };
    public static String path = "j3d/objects/getters";
    public GettersPack() {
        super(LibraryType.LIB, "j3d/objects/getters");

        vfs.putAll(new TriGetters().vfs); // adds other tri getters like winding, legs and double-sided proper.
        vfs.putAll(new CurveGetters().vfs); // curve getters
        vfs.putAll(new LineGetters().vfs); // line getters
        vfs.putAll(new Vector3Getters().vfs);
        vfs.putAll(new ColourGetters().vfs);

        GObjectRegistry.forEach(
                (object) -> {
                    String namespace = object.toString().toLowerCase();
                    TriFunction<String, String, String, JDocBuilder> function = (s1, s2, s3) ->
                            JDoc.builder()
                                    .addDesc("Retrieves the "+s1+" specified by the input GObject (" + namespace + ")")
                                    .addReturns("The "+s2+" structured array")
                                    .sinceVersion("1.0.0")
                                    .addExample(
                                            String.format("""
                                            tsea "j3d/objects/getters"!
                                            
                                            @ If (object) holds the structured array
                                            
                                            maak object!
                                            maak %s <- get_%s_%s(object);
                                            
                                            khuluma(%s)!
                                            """, s1, namespace, s3, s1)
                                    );
                    put(vfs, namespace, "id", function.apply("id", "[UUID]", "id"), GObject.EngineObjectUtils::getUuid);
                    put(vfs, namespace, "pivot", function.apply("pivot", "[Vector3]", "pivot"), GObject.EngineObjectUtils::getPivot);
                    putAliases(vfs, namespace,
                            function.apply("color", "[Colour]", "color"),
                            GObject.EngineObjectUtils::getColour,
                            "color", "colour"
                            );
                }
        );

        putAliases(
                vfs, "uuid",
                JDoc.builder()
                        .addDesc("Retrieves the value of the UUID as a string"),
                (cp, eo) -> {

                    TypeConverter.expectObjectType(eo, EngineObject.Type.UUID, cp);
                    UUID id = TypeConverter.UUIDfromObject(eo);
                    return id.toString();
                },
                "string", "of", "value"
        );
    }

    public static void putAliases(Vfs vfs, String namespace, JDocBuilder jDocBuilder, Getter getter, String ...labels) {
        String name = "get_" + namespace + "_" + labels[0];
        BaseFunction bf = of(name, jDocBuilder, getter);
        for (String l : labels) {
            vfs.put("get_" + namespace + "_"  + l, bf);
        }
    }

    public static void put(Vfs vfs, String namespace, String label, JDocBuilder jDocBuilder, Getter getter) {
        String name = "get_" + namespace + "_" + label;
        BaseFunction bf = of(name, jDocBuilder, getter);
        vfs.put(name, bf);
    }

    public static BaseFunction of(String name, JDocBuilder docs, Getter getter) {
        return new AbstractFunction(
                name,
                docs,
                getter
        );
    }

    public static class AbstractFunction extends BaseFunction {
        private final Getter getter;
        public AbstractFunction(String name, JDocBuilder docs, Getter getter) {
            super(FunctionBuilder.start()
                    .name(name)
                    .arguments(
                            Arguments.getInstance()
                                    .add(new AArgument("array", "The array to extract the information from", false, Argument.Type.ARRAY))
                    )
                    .docs(docs)
            );
            this.getter = getter;
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);

            Object arr = Primitives.toPrimitive(params.getFirst(), false, config, scope);

            if(arr instanceof ArrayList<?> s) {
                return getter.applyExc(
                        new CallProperties(tFuncCall, scope, config)
                        , TypeConverter.fromArr(s));
            }

            return Token.voidValue(tFuncCall.lineNumber);
        }
    }
}
