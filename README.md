# J3Engine

<img src="./src/main/resources/art/logo/J3Engine.png" alt="J3Engine Logo"></img>

A (Work-In-Progress) 3D Graphics Engine written in Java from scratch using only 
the Java Standard Library and [Swing](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html)'s 
2D drawing capabilities.

all with no existing, external 3D library used. all code to project and transform
and others are handcrafted. [See Vector3](./src/main/java/com/j3d/engine/math/matrix/Vector3.java)

## Installation & Run

Man just run the `.jar` or `.msi` man with like Java 21 and you good.

## Screenshots & Features

![Screenshot showcasing a complex scene with
muliple prisms, a cone, some broken geometry
a curve and 3 triangles stacked along the Z axis. 
Also shows the "camera orbit" command feedback.](imgs/Screenshot%202026-09-13%20063011.png)

- Complex 3D scene with multiple geometry types
- Prisms, cone, curves, triangles
- Camera orbit command
- 3D rendering and spatial layering

![Screenshot showcasing the command palette's typing hints
and suggestions as the user types "prism" command which
expects a Vector3 object](imgs/Screenshot%202026-09-13%20063113.png)

- Command palette
- Live command suggestions
- Typed argument hints
- `Vector3`/`Color`/`UUID`(reoslve to referenced object) support

![Screenshot as a continution of the above, where the prism
is in a ghost form as the user can use the arrow keys to
increase the radius or side amount. The properties and layers
panels are also open](imgs/Screenshot%202026-09-13%20063219.png)

- Ghost/preview geometry
- Interactive prism creation
- Keyboard controlled radius/side count
- Properties panel
- Layers panel

![Screenshot as a continution of above where the current
scene is depicted in wireframe mode and is in the
process of being selected with a yellow square.
The layers panel now has the new prism from before and the
properties panel has generic properies which all
show (multiple values)](imgs/Screenshot%202026-09-13%20063249.png)

- Wireframe view mode
- Rectangle based selection
- Multi-object selection
- Layers panel
- Properties panel with generic/multiple types

![Screenshot showcasing the in-app documentation
along side having loaded a completely different engine
theme called "Espresso" Which changes the colours of UI
and the backgorund to be a warm brown as compared to the default
engine colour being a "corporate" teal hue. The user
also has a sub-menu open having clicked tje Geometry Tools
button from the Toolbox next to the properties button.](imgs/Screenshot%202026-09-13%20063520.png)

- In-app documentation. [See](./src/main/resources/docs/about.j3.md)
- Other engine themes.
- Geometry Tools toolbox submenu

![Screenshot showcasing the user having loaded another much
brighter blue theme, along side having typed "e" in the command palette
which gives the user all the command sstarting or containing "e"
along with the option to right-click to autocomplete.
There is also, a Grid2dPanel being showcased which acts as
a 2D drafting canvas for creating points and lines in a 
2D panel then projecting it by defining the plane the points
may live on. The projection is shown in the world and the position
of the mouse is shown too.](imgs/Screenshot%202026-09-13%20063733.png)

- Custom theme support
- Command palette suggestions and autocomplete (Right click)
- 2d drafting via `Grid2dPanel`
- Creating points and lines in 2D and projecting then into 3D
- Mouse-position feedback

![Screenshot showcasing the transform rotate subcommand working on a 
prism where the user has selected the X handle and a circle
and axis appear showing the user how the object selected will
rotate.](imgs/Screenshot%202026-09-13%20063901.png)

- `transform rotate` command
- interactive rotate handles
- Rotation axis visualization
- Object transformation preview

![Screenshot showcasing the properties panel.
A line is selected and by the object count, it can be deduced
its 2 points have also been selected however the combo box
within the panel shows "line", so the properties
panel will filter all other objects and only show the
properties of selected lines hence showing
all properties for the line that was
once hidden.](imgs/Screenshot%202026-09-13%20063938.png)

- Properties panel output
- Object-type filtering
- Generic properties
- Selection of dependent geometry (line, with it's points)