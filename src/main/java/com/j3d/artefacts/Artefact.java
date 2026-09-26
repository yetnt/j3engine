package com.j3d.artefacts;

import com.j3d.Main;
import com.j3d.Startup;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.engine.J3DPanel;

/**
 * An {@link Artefact}, is an interface which describes any implementor to be something that initialises
 * the scene with objects either for debugging or testing purposes.
 * <p>
 *     Running an Artefact is functionally the exact same as opening the engine with no previous state.
 *     However only {@link EngineFrame} is responsible for running Artefacts passed into it,
 *     either via direct construction or through {@link com.j3d.Startup} or more broadly via
 *     {@link Main#test(Artefact)}
 * </p>
 * <p>
 *     As per the name, {@link J3DPanel} enforces that the artefact only runs once. Although i should probably
 *     move that enforcement to simply {@link EngineFrame#complete(Artefact)} but thats an issue for later
 * </p>
 * @see EngineFrame
 * @see Startup
 * @see J3DPanel
 * @see Executor
 *
 * @author Lehlogonolo Poole
 */
@FunctionalInterface
public interface Artefact {
    /**
     * The single side effects method to do whatever the hell you want in the scene.
     * @implSpec Keep in mind, nothing but the scene in an empty state is provided to you
     * so you will have to do a crap ton of plumbing.
     */
    void run();
}
