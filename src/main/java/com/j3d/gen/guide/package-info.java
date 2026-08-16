/**
 * Guide
 * <h1>Guide</h1>
 * <p>
 *     The Guide or otherwise tutorial makes full use of EventEmitter and EventListeners. Every single interaction
 *     other than starting it uses this.
 * </p>
 * <p>
 *     Say we have the following "Guide Steps" we want to show to the user:
 *     <p>
 *         {@code WelcomeStep}, {@code SceneStep}, {@code SelectionStep}
 *     </p>
 *     And obviously they all cant be shown at the same time. but need to be shown in that order, this is how it works.
 *     <ol>
 *         <li>
 *             {@link com.j3d.gen.guide.GuideManager} instantiates a new {@link com.j3d.gen.guide.GuideFlow}
 *             which calls {@link com.j3d.gen.guide.GuideFlow#start()} which shows {@code WelcomeStep}
 *             (the first {@link com.j3d.gen.guide.GuideInfo} in the start of list)
 *         </li>
 *         <li>
 *             {@code WelcomeStep} displays its stuff over the entire engine using {@link com.j3d.gen.guide.GuidePanelAdapter}
 *         </li>
 *         <li>
 *             The user does some specific event that {@code WelcomeStep} listens for
 *         </li>
 *         <li>
 *             {@code WelcomeStep} fires an event to {@link com.j3d.gen.guide.GuideFlow}, specifically being
 *             {@link com.j3d.gen.guide.GuideInfoClosingEvent}
 *         </li>
 *         <li>
 *             {@link com.j3d.gen.guide.GuideFlow} makes sure that {@code WelcomeStep} can indeed close.
 *             If so, it calls its {@link com.j3d.gen.guide.GuideInfo#breakdown()} and removes it from the top
 *             of the list.
 *         </li>
 *         <li>
 *             {@link com.j3d.gen.guide.GuideFlow} calls {@link com.j3d.gen.guide.GuideFlow#start()} once again
 *             and repeats but now {@code SceneStep} is the head of the list
 *         </li>
 *     </ol>
 * </p>
 * <h1>Classes</h1>
 * <ul>
 *     <li>
 *        {@link com.j3d.gen.guide.GuidePanelAdapter}, a simple class which just adds helper methods on top
 *        of the {@link com.j3d.ui.engine.GuidePanel} which is nothing more than just a JPanel over the entire
 *        frame.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.guide.GuideInfo}, a singular step within the guide which only needs to know the engine
 *         event it wants to listen to that the user has to perform in order for this specific step to close itself
 *         and for the next one to proceed. It can build itself and stuff using {@link com.j3d.gen.guide.GuidePanelAdapter}
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.guide.GuideManager}, the orchestrator that starts the {@link com.j3d.gen.guide.GuideFlow}
 *         and holds the created {@link com.j3d.gen.guide.GuidePanelAdapter}.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.guide.GuideFlow}, the flowwwwerr that manages the sequence and progression of {@link com.j3d.gen.guide.GuideInfo} steps.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.guide.GuideInfoClosingEvent}, An event payload indicating that a
 *         {@link com.j3d.gen.guide.GuideInfo} step is requesting to close.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.guide.Anchor}, a utility class providing constants for positioning UI components within the guide panel
 *         components.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.gen.guide;