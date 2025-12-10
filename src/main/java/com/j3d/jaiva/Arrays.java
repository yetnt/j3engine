package com.j3d.jaiva;

import com.j3d.engine.Renderer;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TFunction;

import java.util.ArrayList;

public class Arrays extends BaseLibrary {
    public Arrays(IConfig<Renderer> config) {
        super(LibraryType.CONTAINER);

        vfs.put("aOf", new FArray());
    }

    static class FArray extends BaseFunction {
        public FArray() {
            super("aOf", new TFunction("aOf", new String[] {}, null, -1, JDoc.from("Instant array creation")));
            freeze();
        }

        @Override
        public Object call(TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            ArrayList<Object> output = new ArrayList<>();
            for (Object something : params) {
                output.add(Primitives.toPrimitive(something, false, config, scope));
            }

            return output;
        }
    }
}
