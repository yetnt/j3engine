# Implementation Notes and Ideas.

This file is meant to document my progress within this PAT.

After unwrapping and revealing the entire engine, it is wrapped around `Vector3`, `ScreenPoint` and `CartesianPoint`

Where `BasePoint` (should be) an abstract class that holds an X and Y value, which `ScreenPoint` and `CartesianPoint` extend off.

```mermaid
---
title: Base Coordinate classes
---
classDiagram
    note for ScreenPoint "ScreenPoint defines a point using the screen's X and Y coordinates (0, 0 is the top left)"
    note for BasePoint "The Base class for any other class which has only an X and a Y value."
    note for CartesianPoint "Defines a point using the Cartesian Coordinates (0, 0 is the centre of the screen)"
    ScreenPoint --|> BasePoint~T extends Number~: Extends from (Type = Integer)
    CartesianPoint --|> BasePoint~T extends Number~: Extends from (Type = Double)
    class BasePoint~T extends Number~ {
        +y: T
        +x: T
        Constructor(xcord: T, ycord: T)
        +areCollinear(p1: CartesianPoint, p2: CartesianPoint, p3: CartesianPoint)) boolean$
    }
    CartesianPoint ..> ScreenPoint: converts to
    ScreenPoint ..> CartesianPoint: can convert to (unused)
    Vector3 ..> CartesianPoint: converts to
    class ScreenPoint {
        Constructor(X: integer, Y: integer)
        +toPoint(renderer: Renderer) CartesianPoint
    }
    class CartesianPoint {
        -EPSILON: number = 0.01$
        Constructor()
        Constructor(X: number, Y: number)
        +isNotEmpty() boolean
        +distanceTo(other: CartesianPoint) number
        +distanceSquaredTo(other: CartesianPoint) number
        +toScreen(renderer: Renderer) ScreenPoint
        +hashCode() integer
        +equals(obj: Object) boolean
        +toString() string
    }
    class Dimension {
        +width: integer
        +height: integer
        Constructor(width: integer, height: integer)
        +toString() string
    }
    class Vector3 {
        -X: number
        -Y: number
        -Z: number
        Constructor()
        Constructor(x: number, y: number, z: number)
        +isNotEmpty() boolean
        +getX() number
        +getY() number
        +getZ() number
        +normalize(v : Vector3) Vector3$
        +toPoint(cam : Camera) CartesianPoint
        +equals(o : Object) boolean
        +toString() string
        +hashCode() int
        +distanceSquaredTo(mousePos : CartesianPoint) number
        +add(v: Vector3) Vector3
        +magnitude() number
        +mult(scalar: number) Vector3
        +mult(B: Vector3) Vector3
        +div(scalar: number) Vector3
        +cross(B: Vector3) Vector3
        +sub(B: Vector3) Vector3
        +distance(B: Vector3) Vector3
        +random(low: Vector3, high: Vector3) Vector3$
        +reduce(vectors: ArrayLisr~Vector3~, reducer: BiFunction~Vector3, Vector3, Vector3~) Vector3
    }
```

The core of 3D Graphics is projecting a 3D object onto a 2D screen. How `Vector3` becomes a `CartesianPoint` is not simple.

(See [Wikipedia's Article on 3D to 2D Projection](https://en.wikipedia.org/wiki/3D_projection))

The method this engine uses is Perspective Projection, where objects further away from the camera appear to be smaller When in reality they could be the exact same size.

[This exact formula](https://en.wikipedia.org/wiki/3D_projection#Mathematical_formula) from Wikipedia is used for this conversion.

After converting it to a `CartesianPoint`, we need to convert it to Swing's coordinates. as a Cartesian Point's coordinates originate at the centre of the screen and
Swing's is the top left corner of the frame. This is done using the following formula (`ScreenPoint` represents the coordinates Swing uses.)

where $S$ is the scale used to define how far CartesianPoints are from each other. $c_{xy}$ is the cartesian point coordinates,
$s_{xy}$ is the screen coordinates, and $f_{height} \times f_{width}$ is 
the frame height and width

$$
s_x = S \cdot c_x + \frac{f_{width}}2
$$

$$
s_y = \frac{f_{height}}2 - S \cdot c_y
$$


```mermaid
---
title: Orchestrator Classes
---
classDiagram
    Main ..> Executor : Instantiates
    Executor ..> Renderer
    Main --> Renderer : Instantiates and passes into Executor
    Main --> JPanel : Extends from
    Renderer --o GObject : Holds a list of 
    note for Executor "A class used to draw objects once."
    note for Renderer "The class that manages how things get drawn and what gets drawn"
    note for Main "The Main class."
    
    class Executor {
        -renderer: Renderer
        Constructor(r: Renderer)
        +run()
    }
    class Main {
        +dimension: Dimension$
        +jBundler: JBundler$
        #paintComponent(g: Graphics)
        +main(args: String[])$
    }
    class Renderer {
        -graphics: Graphics2D
        -screenSize: Dimension
        -objectQueue: ArrayDeque~GObject~
        +SCALE: number
        +cameraOffset: Point
        Constructor(g: Graphics, dim: Dimension)
        +line(A: CartesianPoint, B: CartesianPoint) GLine
        +point(point: CartesianPoint) GPoint
        +axis()
        +clear()
        +findOrCreatePoint(CartesianPoint) GPoint
        +getGraphics() Graphics2D
    }
```

Which manage everything from rendering, redrawing. Then the shapes themselves

```mermaid
---
title: Core 2D Classes
---
classDiagram
    note for GObject "Base class for 2D shapes."
    note for GPoint "A single point. When drawn is a circle with radius 2"
    note for GLine "A line. When drawn, it's a line segment between 2 points."
    note for GTri "A Triangle, when drawn it fills a polygon using 3 specified points"
    GPoint --|> GObject
    GLine --|> GObject
    GTri --|> GObject
    class GObject {
        -pivot : CartesianPoint
        -Id: string
        Constructor()
        +getPivot() CartesianPoint
        +setPivot(pivot : CartesianPoint)
        +getId() string
    }
    class GPoint {
        -drawPoint(renderer : Renderer, cartesianPoint : CartesianPoint)
        Constructor(renderer : Renderer, cartesianPoint : CartesianPoint)
        +update(renderer : Renderer, cartesianPoint : CartesianPoint)
        +equals(obj : Object) boolean
    }
    class GLine {
        -startPoint: CartesianPoint
        -endPoint : CartesianPoint
        Constructor(renderer : Renderer, startPoint : GPoint, endPoint : GPoint)
        +setEndPoint(end : CartesianPoint)
        +setStartPoint(start : CartesianPoint)
        +getEndPoint() CartesianPoint
        +getStartPoint() CartesianPoint
        +length() number
    }
    class GTri {
        -LegA : GLine
        -LegB : GLine
        -LegC : GLine
        Constructor(renderer : Renderer, A : GPoint, B : GPoint, C : GPoint)
        Constructor(renderer : Renderer, A : GLine, B : GLine, C : GLine)
        +getLegA() GLine
        +getLegB() GLine
        +getLegC() GLine
        +setLegA(GLine legA)
        +setLegB(GLine legB)
        +setLegC(GLine legC)
        +area() number
    
    }
```

And other classes which aren't of importance to the implementation such as `Point3D` which
holds only a public x, y and z value. And `Dimension` which holds a width and height.

Classes such as `Arrays` and `Testing` came as a result of trying to embed 
[jaiva](https://github.com/yetnt/jaiva) (my custom programming language) into 
this engine. (Which is where the `JBundler` instance comes from) They aren't used in
the main run of the engine and probably won't be until the engine is stable enough.
Therefore until then, No class diagrams will be shown for them.

Also the `Frame` class, came as I thought i will maybe later save stuff as frames from
the engine, but then i realized i'm biting more than I can chew, so I deleted it in later commits.
As it's never used, it won't be documented here either.

Another class implemented for the first time is the `Events` enum, which serves
no purpose in the current commit and therefore will not be part of the class diagrams
(as yet).

---

The core idea before making any GUI was to have a set of base shapes to be able
to draw anytime.

Where `Renderer` holds the master-list of objects to draw anytime the frame needs
to be redrawn and `Main` handles the input and registers the classes. `Executor` is
nothing but debug class used to draw specific shapes at specific coordinates. It may
be removed when the engine is stable with proper GUI.

#### Add README commit

[Commit](https://github.com/yetnt/j3engine/commit/eb4dd7fefd8f142904b29ca8815614fb187c4d1d)

Added the basic readme

### 29 Aug

#### Add Documentation

[Commit](https://github.com/yetnt/j3engine/commit/94bfa4b6ed1fd3d1041aabc33185e9ff873e9745)

Started documentation on the core classes and deleted the 
`Engine` and `Frame` class as they served no use.

#### Event Management

[Commit](https://github.com/yetnt/j3engine/commit/835a05b6334ab763373ccb28eade8866ede342a2)

Started work on Events.

How this was thought out was: When drawing, a point may update and a line or triangle
needs to react to said update and update themselves. Or if a triangle gets deleted, 
the lines (nodes) need to delete themselves.

The main idea is that if an object, such as a `GLine` has 2 `GPoint`s, those points
are **nodes** to the `GLine`, while the `GLine` is the **parent** to each `GPoint`

`GPoint`, `GLine` and `GTri` each have their own `Event` class which 
extend `EventBroadcast` which other `G`Objects should listen for. 

```mermaid
---
title: Event Classes
---
classDiagram
    EventEmitter --* EventListener : Holds a list of EventListeners
    EventEmitter ..> EventBroadcast : Creates a new EventBroadcast when broadcasting.
    EventBroadcast --> EventEmitter : Holds the Event Originator
    EventBroadcast --> Renderer : Holds the Renderer Instance
    EventListener ..> EventType : To determine the type of Event that happened.
    GObject --|> EventEmitter : Extends
    GObject --|> EventListener : Implements
    note for ObjectType "An enum defining whether the object that we are listening to is a NODE or PARENT."
    class EventEmitter {
        <<abstract>>
        -registered: HashMap~ObjectType, ArrayList~EventListener~~
        +attach(event: EventListener, type: ObjectType)
        +detach(event: EventListener, type: ObjectType)
        +broadcast(type: EventType, type : ObjectType, properties: EventBroadcast)
    }
    class EventListener {
        <<interface>>
        +onEvent(event: EventType, properties: EventBroadcast)
    }
    class EventBroadcast {
        <<abstract>>
        +emitter: EventEmitter
        +renderer: Renderer
        Constructor(e: EventEmitter, r: Renderer)
    }
    class EventType {
        <<enumeration>>
        NODE_UPDATED
        NODE_DELETED
        PARENT_DELETED
        PARENT_UPDATED
    }
    class ObjectType {
        <<enumeration>>
        NODE
        PARENT
    }
    
    `GPoint.Event` --|> EventBroadcast
    `GLine.Event` --|> EventBroadcast
    `GTri.Event` --|> EventBroadcast
```

How The Event happens and propagates is the following:

Assuming you have the `GTri` **tri** with **3 lines** (`GLine`s) sharing
3 `GPoint`s respectively, such that:

**A** connects to **B** to form **Line1**, **B** connects to **C** to form **Line2**
and **C** connects to **A** to form **Line3**.

```mermaid
flowchart 
    User["User moves Point A"]
    PA["Point A calls broadcast(EventType.NODE, properties) to parents"]
    User --> PA
    L1["Line 1 receives the event"]
    L3["Line 3 receives the event"]
    PA --> L1
    PA --> L3
    U1["Line 1 updates it's properties"]
    U3["Line 3 updates it's properties"]
    L1 --> U1
    L3 --> U3
    Tri["Sends Event to the main triangle"]
    U1 --> Tri
    U3 --> Tri
    Tri --> C["Triangle updates and recalculates area, perimeter, etc"]
```

As Shown in the diagram, the main expected Event Propagation is:

```mermaid
flowchart LR
    GPoint <--> GLine <--> GTri
```

Where the only initiators of events, are the ends `GPoint` and `GTri`. A `GLine` being in the middle,
shouldn't be able to initiate events. They propagate events up and down, but never
start one.



### 30 Aug

[Events](https://github.com/yetnt/j3engine/commit/cf36183b65f78a818da71c8ae4f3a46dadd64f22)

This adds the `ObjectType` enum shown in the above diagrams.

### 19 Sept

#### Events and Stuff

[Commit](https://github.com/yetnt/j3engine/commit/bef120b329c27e3f9a03535da65daa17d4b02120)

Implements events into GObjects and adds `registeredNodes()`, 
`registeredParents()` and `detachAll()` to `EventEmitter`.

Also marks the `registered` HashMap as protected.

#### GUI

[Commit](https://github.com/yetnt/j3engine/commit/45b12ded306492d004c18b434263a8415ce2b498)

Begin implementing the Debug GUI with a basic `draw` and `clear` button.

