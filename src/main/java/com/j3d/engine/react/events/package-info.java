/**
 * Holds all subclasses and interfaces related to the event system.
 * <h1>Events</h1>
 * <p>
 *     The engine, in most cases instead of trying to find a specific class instance and method to
 *     call, makes use of "events" which allow any parts of the engine to fire events to any other
 *     part at any time of execution.
 * </p>
 * <p>
 *     Any class which extends {@link com.j3d.engine.react.events.EventEmitter} inherits event emitting
 *     capabilities to any class which it attaches that implements {@link com.j3d.engine.react.events.EventListener}.
 *     Classes which cannot directly extends EventEmitter can instead implement {@link com.j3d.engine.react.events.EventEmitterInterface}
 *     and hence use the generic static utilities provided by {@link com.j3d.engine.react.events.EventEmitter}
 *     such as to still emit events.
 * </p>
 * <p>
 *     An event is broadcast with an {@link com.j3d.engine.react.events.EventPayload} to all attached listeners.
 *     This class can also be the superclass of concrete implementations, and it's up to the listener to
 *     decipher the payload. However, there is also a {@link com.j3d.engine.react.events.EventType} enum
 *     where all event types can be categorised by a single enum before performing type casting.
 * </p>
 * <h2>Classes and interfaces</h2>
 * <ul>
 *     <li>
 *         An {@link com.j3d.engine.react.events.EventEmitter} is a class that allows other classes
 *         to register as listeners and receive events.
 *         <p>
 *             The {@link com.j3d.engine.react.events.EventEmitterInterface} is an interface that
 *             allows classes that cannot extend {@link com.j3d.engine.react.events.EventEmitter} to
 *             still emit events using its static utility methods.
 *         </p>
 *     </li>
 *     <li>
 *         An {@link com.j3d.engine.react.events.EventListener} is an interface which defines any class
 *         as one that can listen for events and handle said events via {@link com.j3d.engine.react.events.EventListener#onEvent(com.j3d.engine.react.events.EventType, com.j3d.engine.react.events.EventPayload)}
 *         <p>
 *             An {@link com.j3d.engine.react.events.EventReactor} is a special type of {@link com.j3d.engine.react.events.EventListener}
 *             which is removed after it has received it's event. It "reacts" to the event once and is
 *             removed thereafter.
 *         </p>
 *         <p>
 *              An {@link com.j3d.engine.react.events.IdempotentEventListener} is a special type of
 *              {@link com.j3d.engine.react.events.EventListener} that guarantees that processing the same
 *              event multiple times will produce the same result as processing it once. This is useful for
 *              events that might be re-triggered or re-broadcast, ensuring consistent state.
 *         </p>
 *     </li>
 *     <li>
 *         An {@link com.j3d.engine.react.events.EventPayload} is a class that carries data related to an event.
 *         It can be extended to provide specific event data.
 *     </li>
 *     <li>
 *         An {@link com.j3d.engine.react.events.EventType} is an enum that categorises different types
 *         of events, allowing listeners to filter and handle specific event types.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.react.events;