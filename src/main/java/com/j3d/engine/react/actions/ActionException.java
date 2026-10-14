package com.j3d.engine.react.actions;

import com.j3d.engine.scene.SceneException;

public class ActionException extends SceneException {

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ActionException cleanupErr(Action<?> action, Throwable cause) {
        return (ActionException) new ActionException(
                "[" +action.getDescription() +"] could not be cleaned-up due to an exception being thrown.",
                cause
        ).code(201);
    }
}
