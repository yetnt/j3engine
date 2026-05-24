package com.j3d.engine.react.actions;

/**
 * A specialisation of a {@link AbstractAction} and {@link CleanableAction} specifically for a class's constructor.
 * Usually a method provides an action such that whoever calls the method is responsible for executing and storing
 * the action within the history. However, an object creation within it's constructor naturally breaks this rule as the action
 * was already applied by the constructor. Which is where this class comes in.
 * @implSpec It is up to the constructor implementation of this to not only add the action to the history, but to make sure the
 * run and undo code are 1:1 with the constructor.
 * @see AbstractAction
 * @see CleanableAction
 * @see Action
 * @author Lehlogonolo Poole
 */
public abstract class ConstructorAction extends AbstractAction implements CleanableAction {

}
