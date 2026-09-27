package com.j3d.jaiva.packs;

import com.j3d.jaiva.EngineObject;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TStringVar;

@PublicLibrary(path = "j3d/objects")
public class ObjectsPack extends BaseLibrary {

    public ObjectsPack() {
        super();

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
