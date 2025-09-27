package com.j3d.jaiva;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.GLine;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.globals.BaseGlobals;
import com.jaiva.interpreter.globals.GlobalType;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.tokenizer.Token;

import java.util.ArrayList;

public class Testing extends BaseGlobals {
    public Testing(IConfig<Renderer> config) {
        super(GlobalType.LIB, "j3d-core");

//        vfs.put("circ", new FCirc());
//        vfs.put("draw", new FDraw());
        vfs.put("line", new FLine());
        vfs.put("point", new FPoint());
        vfs.put("zf", new FZoomFactor());
        vfs.put("axis", new FAxis());
        vfs.putAll(new Arrays(config).vfs);
    }

//    static class FDraw extends BaseFunction {
//        public FDraw() {
//            super("draw", new Token.TFunction("draw", new String[] {}, null, -1, "cool"));
//            freeze();
//        }
//
//        @Override
//        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
//            ((Renderer)config.object).draw();
//            return Token.voidValue(tFuncCall.lineNumber);
//        }
//    }

//    static class FCirc extends BaseFunction {
//        public FCirc() {
//            super("circ", new Token.TFunction("circ", new String[] {"x", "y", "r"}, null, -1, "cool"));
//            freeze();
//        }
//
//        @Override
//        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig config, Scope scope) throws Exception {
//            checkParams(tFuncCall, scope);
//            int x = (int) Primitives.toPrimitive(params.getFirst(), false, config, scope);
//            int y = (int) Primitives.toPrimitive(params.get(1), false, config, scope);
//            int radius = (int) Primitives.toPrimitive(params.getLast(), false, config, scope);
//            Renderer.circ(new CartesianPoint(x, y), radius);
//            return Token.voidValue(tFuncCall.lineNumber);
//        }
//    }

    static class FZoomFactor extends BaseFunction {
        public FZoomFactor() {
            super("zf", new Token.TFunction("zf", new String[] {"factor"}, null, -1, "zoom factor"));
            freeze();
        }

        @Override
        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);
            ((Renderer)config.object).SCALE = (int) Primitives.toPrimitive(params.getFirst(), false, config, scope);
            return Token.voidValue(tFuncCall.lineNumber);
        }
    }


    static class FLine extends BaseFunction {
        public FLine() {
            super("line", new Token.TFunction("line", new String[] {"a", "b"}, null, -1, "cool"));
            freeze();
        }

        @Override
        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);
            Object arrA = Primitives.toPrimitive(params.getFirst(), false, config, scope);
            if (!(arrA instanceof ArrayList<?>))
                throw new RuntimeException("point 1 was not given!");
            CartesianPoint A = CartesianPoint.fromArray((ArrayList<Integer>) arrA);
            Object arrB = Primitives.toPrimitive(params.getLast(), false, config, scope);
            if (!(arrB instanceof ArrayList<?>))
                throw new RuntimeException("point 2 was not given!");
            CartesianPoint B = CartesianPoint.fromArray((ArrayList<Integer>) arrB);
//            GLine line = ((Renderer)config.object).line(A, B);
//            return line.toArray();
            return arrA;
        }
    }


    static class FPoint extends BaseFunction {
        public FPoint() {
            super("point", new Token.TFunction("point", new String[] {"x", "y"}, null, -1, "cool"));
            freeze();
        }

        @Override
        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);
            Object objX = Primitives.toPrimitive(params.getFirst(), false, config, scope);
            if (!(objX instanceof Integer) && !(objX instanceof Double))
                throw new RuntimeException("x coordinate is not a number");
            Object objY = Primitives.toPrimitive(params.getLast(), false, config, scope);
            if (!(objY instanceof Integer) && !(objY instanceof Double))
                throw new RuntimeException("y coordinate is not a number");
//            ((Renderer)config.object).point(new CartesianPoint(
//                    objX instanceof Integer ? (int)objX : (double)objX,
//                    objY instanceof Integer ? (int)objY : (double)objY));
            return new ArrayList<>(java.util.Arrays.asList(objX, objY));
        }
    }


    static class FAxis extends BaseFunction {
        public FAxis() {
            super("axis", new Token.TFunction("axis", new String[] {}, null, -1, "draw axis."));
            freeze();
        }

        @Override
        public Object call(Token.TFuncCall tFuncCall, ArrayList<Object> params, IConfig<Object> config, Scope scope) throws Exception {
            checkParams(tFuncCall, scope);
//            ((Renderer)config.object).axis();
            return Token.voidValue(tFuncCall.lineNumber);
        }
    }
}
