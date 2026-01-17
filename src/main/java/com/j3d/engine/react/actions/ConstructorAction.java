package com.j3d.engine.react.actions;

/**
 * ConstructorAction is an action who does not (need to) implement {@link #run()} as
 * the action is executed in the constructor.
 * However, it's suggested to implement it anyway, in the case the action needs to be re-executed
 * (for example, in a redo operation).
 */
public abstract class ConstructorAction extends AbstractAction implements CleanableAction {

}
