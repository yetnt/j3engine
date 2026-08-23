package com.j3d.jaiva.packs.getters;

import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Scope;

/**
 * This is not a J3Engine error which would make use of J3DError and those, this is an error of malformed input into J3D from Jaiva
 * but it's the jaiva script's fault, hence its a jaiva facing error
 */
public class J3DGetterException extends InterpreterException.WtfAreYouDoingException {

    public J3DGetterException(Scope ct, EngineObject.Type expected, EngineObject.Type got, int lineNumber) {
        super(ct, "Expected to receive an object of type " + expected + " instead got " + got, lineNumber);
    }
}
