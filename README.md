# J3Engine

<img src="./src/main/resources/art/logo/J3Engine.png" alt="J3Engine Logo"></img>

A (Work-In-Progress) CAD-inspired 3D Geometry Editor written in Java from scratch using only 
the Java Standard Library and [Swing](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html)'s 
2D drawing capabilities.

all with no existing, external 3D library used. all code to project and transform
and others are handcrafted. [See Vector3](./src/main/java/com/j3d/engine/math/matrix/Vector3.java)

J3Engine is a standalone desktop app.

(This project comes in 2 versions, this version the actual hobby
project development and then the 
[PAT version](https://github.com/yetnt/j3engine/tree/pat)
with the features being enar identical with only
a few minor differences)

## Links

- [Architecture](documents/Architecture.md)
- [Building J3Engine from scratch](documents/Building.md)
- [J3Engine API site (Generated from Javadoc)](https://yetnt.github.io/j3engine/)

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

## Highlight Commands

(See [Aliases](./src/main/resources/docs/commands.j3.md#alias) to see how command and subcommand aliases work)

(All links below link to the source code of the command/tool)

- `transform <subcommand>` - [Transform](./src/main/java/com/j3d/engine/interact/cmd/commands/transform/TransformCmd.java) a selection of objects
  - `transform translate [p|f|t]` : Translate tool
  - `transform rotate [p|f|t] <vector3?>` : Rotate tool
  - `transform scale [p|v|t]` : Scale tool
  - `transform qtrans` : Quick-Translate tool
- `camera <subcommand>` - [Camera](./src/main/java/com/j3d/engine/interact/cmd/commands/camera/CameraCmd.java) commands
  - `camera orbit` : Orbit tool (Change the camera yaw/pitch or orbit around world centre)
- `clipboard <subcommand>` - [Clipboard](./src/main/java/com/j3d/engine/interact/cmd/commands/clipboard/ClipboardCmd.java) related commands (keybinded to CTRL+C and CTRL+V)
  - `clipboard copy` : Copies the current selection to the J3Engine clipbaord
  - `clipboard paste` : Pastes whatever is in the J3Engine clipboard
- `extrude <triangle?>` : [Extrude](./src/main/java/com/j3d/engine/interact/cmd/commands/ExtrudeCmd.java) a given triangle into a solid (or a selection if no triangle input is given)
- `prism <vector3> <vector3> <vector3?> <vector3?> <vector3?> <vector3?>` : Creates a [prism](./src/main/java/com/j3d/engine/interact/cmd/commands/PrismCmd.java)
- `join <subcommand?>` : [Joins](./src/main/java/com/j3d/engine/interact/cmd/commands/join/JoinCmd.java) a selection of points or otherwise into new geometry
  - Depending on what's selected, the command may call the appropriate subcommand o your behalf.
  - `join line <point> <point> <point?>` : Joins 2 points into a line or 3 points into a curve.
- `measure <subcommand?>` : [Measures](./src/main/java/com/j3d/engine/interact/cmd/commands/measure/MeasureCmd.java) the given geometry
  - Similar selection based handling semantics as join
  - `measure line <vector3> <vector3>` : Measure the distance between 2 positions
  - `measure area <vector3> <Vector3> <vector3>` : Measure the area between 3 positions as a triangle
  - `measure volume <thing>` : Measure the volume of a given Thing.
- `explode <any>` : [Explodes](./src/main/java/com/j3d/engine/interact/cmd/commands/ExplodeCmd.java) all geometry into constituent points, decimating all relationships and not storing in history.
- `debug <subcommand>` : Commands for debugging mostly random engine internals. Such as echoing within the command palette or other stuff
- `ui <subcommand>` : [UI](./src/main/java/com/j3d/engine/interact/cmd/commands/uicmd/UICmd.java) related commands.
  - `ui toggle [history|grid2d|properties|...]` : Toggle the visibility of floating panel ui.
- `create [point|tri|prism|...]` : [Create](./src/main/java/com/j3d/engine/interact/cmd/commands/create/CreateCmd.java) new geometry on the fly
- `engine <subcommand>` : [Engine](./src/main/java/com/j3d/engine/interact/cmd/commands/engine/EngineCmd.java) related subcommands
  - `engine exit <boolean>` : Run the engine shutdown sequence
  - `engine files` : Open the location of J3Engine user files where things such as custom themes, preferences, log files, projects frame relating things are.
- `select <any>` : [Selects](./src/main/java/com/j3d/engine/interact/cmd/commands/SelectCmd.java) the given geometry.
- `help <any>` : Provides [help](./src/main/java/com/j3d/engine/interact/cmd/commands/HelpCmd.java) on the given command.