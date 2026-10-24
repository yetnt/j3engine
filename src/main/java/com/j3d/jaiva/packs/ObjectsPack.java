package com.j3d.jaiva.packs;

import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.tokenizer.jdoc.JDoc;

@Exports(GettersPack.class)
@PublicLibrary(
        path = "j3d/objects",
        description = "Objects pack which just holds the array type constants" +
                " along with all the functions to do stuff"
)
public class ObjectsPack extends BaseLibrary {

    public ObjectsPack() {
        super();

        for (EngineObject.Type value : EngineObject.Type.values()) {
            String v = value.toString();
            String name = "J3D_" + v;
            BaseVariable variable = new BaseVariable(
                    VariableBuilder.start()
                            .name(name)
                            .value(v)
                            .docs(JDoc.builder()
                                    .addDesc("Type constant")
                                    .sinceVersion("1.0.0")
                            )
            );
            add(variable);
        }
    }
}
