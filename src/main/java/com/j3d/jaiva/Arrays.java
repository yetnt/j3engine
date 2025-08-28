package com.j3d.jaiva;

import com.j3d.engine.Renderer;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.globals.BaseGlobals;
import com.jaiva.interpreter.globals.GlobalType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.Token;

import java.util.ArrayList;

public class Arrays extends BaseGlobals {
    public Arrays(IConfig<Renderer> config) {
        super(GlobalType.CONTAINER);

        vfs.put("aOf", new FArray());
    }

    static class FArray extends BaseFunction {
        public FArray() {
            super("aOf", new Token.TFunction("aOf", new String[] {}, null, -1, "Instant array creation"));
            freeze();
        }

        @Override
        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            ArrayList<Object> output = new ArrayList<>();
            for (Object something : params) {
                output.add(Primitives.toPrimitive(something, false, config, scope));
            }

            return output;
        }
    }
}
