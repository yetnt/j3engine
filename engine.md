# J3Engine Documentation

## Terms

These are some terms used either within the codebase itself or otherwise here in the docs.

- **J3Engine**: is actually short for "Jaiva 3D Engine". Although the engine itself is not written within Jaiva
and has nothing to do with it. The name was simply the first that came to mind fresh off writing Jaiva.
The engine may also be referred to as "J3DEngine" but thats before i removed the D from the name for simplicity.
- **Swing**: A GUI toolkit included in the Java Standard Library for building graphical user interfaces.
    > See [Swing (Java SE Documentation)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html) for more information.
- **Jaiva**: My custom scripting language which is not related to J3Engine however is imported as a dependency for it's CLI utilities
and scripting capabilities.
    > See [Jaiva GitHub Repository](https://github.com/yetnt/jaiva) for more information.
- **Thing**: A _Thing_ is an object in the 3D world. It holds multiple triangles (2d objects) which are rendered to the screen.
- **GObject**: A _GObject_ (short for Graphical Object) is a base 2d object which can be drawn to the screen. A GObject itself
is a base class for other 2D objects (triangles, lines and points)
- **Action**: An _Action_ is any operation which modifies the state of the engine and can be stored within the history.
Not all operation are considered Actions. For example, rendering the scene is not an Action as it does not modify
the state of the engine.
- **History**: The _History_ is a stack of Actions which have been performed in the engine. It allows for undoing and redoing
of Actions.

## Architecture

The main architecture of J3Engine is that every component is modular and can be used independently of each other.
This allows for easy testing and development of each component without affecting the others.

However the engine itself cannot handle multiple "scenes" at once. Much like Blender. With this in mind a lot of 
static references can be found within `Main` class which hold the current state of the engine.

How this will be explained is in sections of the base engine components and how they interact with each other.

The main components of the engine are:

- **Engine**: The core of the engine which manages the overall state and functionality.
- **Rendering**: The system responsible for rendering the 3D scene to the screen
- **Interactions**: The system responsible for handling user input (keyboard and mouse)
- **GUI**: The graphical user interface which wraps around the CLI and provides a visual interface for the user.
    > **CLI**: The command line interface although it's part of the GUI, it can also be used standalone for scripting and automation.

Some components may have a singleton class to manage state and functionality globally, 
while others are spread across multiple classes each with their own responsibilities.

### Engine

Being a project in 2D graphics, the core of the engine is built around the question of how to project 
3D objects onto a 2D plane (the screen).

#### Base Classes

```mermaid
classDiagram
    class BasePoint~T extends Number~ {
        +x: T
        +y: T
        Constructor(x: T, y: T)
        +areCollinear(p1: CartesianPoint, p2: CartesianPoint, p3: CartesianPoint) boolean
    }
    
    note for BasePoint "Base class for points in 2D space."
    
    class CartesianPoint {
        +fromList(arr : ArrayList~? extends Number~)$ CartesianPoint
        +x: double
        +y: double
        Constructor(x: double, y: double)
        Constructor()
        +isNotEmpty() boolean
        +toScreen() ScreenPoint
        +distanceTo(other: CartesianPoint) double
        +distanceSquaredTo(other: CartesianPoint) double
        +toArray() ArrayList~Double~
        +equals(obj: Object) boolean
        +hashCode() int
        +toString() string
    }
    
    note for CartesianPoint "A point in 2D Cartesian coordinate system."
    CartesianPoint --|> BasePoint~Double~
    
    class ScreenPoint {
        +x: int
        +y: int
        Constructor(x: int, y: int)
        +toPoint(renderer: Renderer) CartesianPoint
        +toSwingPoint() java.awt.Point
        +toString() string
    }
    
    note for ScreenPoint "A point in screen coordinates (pixels)."
    ScreenPoint --|> BasePoint~Integer~
    ScreenPoint <.. CartesianPoint : used to convert from CartesianPoint
    
    class Vector3 {
        -X: double
        -Y: double
        -Z: double
        +Constructor(x: double, y: double, z: double)
        +Constructor()
        +getX() double
        +getY() double
        +getZ() double
        +dot(v: Vector3) double
        +normalize() Vector3
        +add(v: Vector3) Vector3
        +magnitude() double
        +mult(scalar: double) Vector3
        +mult(B: Vector3) Vector3
        +div(scalar: double) Vector3
        +cross(B: Vector3) Vector3
        +sub(B: Vector3) Vector3
        +distance(B: Vector3) double
        +random(low: Vector3, high: Vector3)$ Vector3
        +copy() Vector3
        +toPoint(cam: Camera) CartesianPoint
        +distanceSquaredTo(mousePos: CartesianPoint) double
        +reduce(vectors: ArrayList~Vector3~, reducer: BiFunction~Vector3, Vector3, Vector3~)$ Vector3
        +equals(obj: Object) boolean
        +hashCode() int
        +toString() string
    }
    
    Vector3 ..> CartesianPoint : Converts to a 2d Point via Perspective. Using Camera Angle Projection and other stuff (TODO: link the wikipedia article)
    note for Vector3 "A point in 3D space with length height and depth"
```