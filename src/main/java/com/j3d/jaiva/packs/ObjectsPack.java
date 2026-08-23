package com.j3d.jaiva.packs;

import com.j3d.jaiva.EngineObject;
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
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TStringVar;

import java.util.ArrayList;

public class ObjectsPack extends BaseLibrary {
    public static String path = "j3d/objects";
    public ObjectsPack() {
        super(LibraryType.LIB, "j3d/objects");

        for (EngineObject.Type value : EngineObject.Type.values()) {
            String v = value.toString();
            String name = "J3D_" + v;
            BaseVariable variable = new BaseVariable(name, new TStringVar(name, v, -1,
                    JDoc.builder()
                            .addDesc("Type constant")
                            .sinceVersion("1.0.0")
                            .build()
                    ), v);
            vfs.put(name, variable);
        }
    }
}
