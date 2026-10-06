# j3d/objects/getters (Library)

__
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| get_tri_legA | [get_tri_legA](#get_tri_legA) |
| get_tri_firstLeg | [get_tri_firstLeg](#get_tri_legA) |
| get_tri_lineA | [get_tri_lineA](#get_tri_legA) |
| get_tri_legB | [get_tri_legB](#get_tri_legB) |
| get_tri_secondLeg | [get_tri_secondLeg](#get_tri_legB) |
| get_tri_lineB | [get_tri_lineB](#get_tri_legB) |
| get_tri_legC | [get_tri_legC](#get_tri_legC) |
| get_tri_thirdLeg | [get_tri_thirdLeg](#get_tri_legC) |
| get_tri_lineC | [get_tri_lineC](#get_tri_legC) |
| get_tri_windingA | [get_tri_windingA](#get_tri_windingA) |
| get_tri_firstPoint | [get_tri_firstPoint](#get_tri_windingA) |
| get_tri_pointA | [get_tri_pointA](#get_tri_windingA) |
| get_tri_windingB | [get_tri_windingB](#get_tri_windingB) |
| get_tri_secondPoint | [get_tri_secondPoint](#get_tri_windingB) |
| get_tri_pointB | [get_tri_pointB](#get_tri_windingB) |
| get_tri_windingC | [get_tri_windingC](#get_tri_windingC) |
| get_tri_thirdPoint | [get_tri_thirdPoint](#get_tri_windingC) |
| get_tri_pointC | [get_tri_pointC](#get_tri_windingC) |
| get_tri_doubleSided | [get_tri_doubleSided](#get_tri_doubleSided) |
| get_tri_isDoubleSided | [get_tri_isDoubleSided](#get_tri_doubleSided) |
| get_curve_start | [get_curve_start](#get_curve_start) |
| get_curve_pointA | [get_curve_pointA](#get_curve_start) |
| get_curve_control | [get_curve_control](#get_curve_control) |
| get_curve_pointB | [get_curve_pointB](#get_curve_control) |
| get_curve_end | [get_curve_end](#get_curve_end) |
| get_curve_pointC | [get_curve_pointC](#get_curve_end) |
| get_curve_amount | [get_curve_amount](#get_curve_amount) |
| get_line_start | [get_line_start](#get_line_start) |
| get_line_pointA | [get_line_pointA](#get_line_start) |
| get_line_end | [get_line_end](#get_line_end) |
| get_line_pointB | [get_line_pointB](#get_line_end) |
| get_vector3_x | [get_vector3_x](#get_vector3_x) |
| get_vector3_X | [get_vector3_X](#get_vector3_x) |
| get_vector3_left | [get_vector3_left](#get_vector3_x) |
| get_vector3_y | [get_vector3_y](#get_vector3_y) |
| get_vector3_Y | [get_vector3_Y](#get_vector3_y) |
| get_vector3_up | [get_vector3_up](#get_vector3_y) |
| get_vector3_z | [get_vector3_z](#get_vector3_z) |
| get_vector3_Z | [get_vector3_Z](#get_vector3_z) |
| get_vector3_forward | [get_vector3_forward](#get_vector3_z) |
| get_vector3_func | [get_vector3_func](#get_vector3_func) |
| get_vector3_asFunc | [get_vector3_asFunc](#get_vector3_func) |
| get_vector3_f | [get_vector3_f](#get_vector3_func) |
| get_vector3_F | [get_vector3_F](#get_vector3_func) |
| get_colour_red | [get_colour_red](#get_colour_red) |
| get_colour_r | [get_colour_r](#get_colour_red) |
| get_colour_green | [get_colour_green](#get_colour_green) |
| get_colour_g | [get_colour_g](#get_colour_green) |
| get_colour_blue | [get_colour_blue](#get_colour_blue) |
| get_colour_b | [get_colour_b](#get_colour_blue) |
| get_colour_alpha | [get_colour_alpha](#get_colour_alpha) |
| get_colour_a | [get_colour_a](#get_colour_alpha) |
| get_colour_func | [get_colour_func](#get_colour_func) |
| get_colour_asFunc | [get_colour_asFunc](#get_colour_func) |
| get_colour_f | [get_colour_f](#get_colour_func) |
| get_colour_F | [get_colour_F](#get_colour_func) |
| get_point_id | [get_point_id](#get_point_id) |
| get_point_pivot | [get_point_pivot](#get_point_pivot) |
| get_point_color | [get_point_color](#get_point_color) |
| get_point_colour | [get_point_colour](#get_point_color) |
| get_line_id | [get_line_id](#get_line_id) |
| get_line_pivot | [get_line_pivot](#get_line_pivot) |
| get_line_color | [get_line_color](#get_line_color) |
| get_line_colour | [get_line_colour](#get_line_color) |
| get_tri_id | [get_tri_id](#get_tri_id) |
| get_tri_pivot | [get_tri_pivot](#get_tri_pivot) |
| get_tri_color | [get_tri_color](#get_tri_color) |
| get_tri_colour | [get_tri_colour](#get_tri_color) |
| get_curve_id | [get_curve_id](#get_curve_id) |
| get_curve_pivot | [get_curve_pivot](#get_curve_pivot) |
| get_curve_color | [get_curve_color](#get_curve_color) |
| get_curve_colour | [get_curve_colour](#get_curve_color) |
| get_uuid_string | [get_uuid_string](#get_uuid_string) |
| get_uuid_of | [get_uuid_of](#get_uuid_string) |
| get_uuid_value | [get_uuid_value](#get_uuid_string) |
## Functions

### get_tri_legA

This symbol can be reached by the following aliases: _`get_tri_legA`_, _`get_tri_firstLeg`_, _`get_tri_lineA`_

_**Retrieves the first leg of the triangle**_


#### Definition

```jaiva
F~get_tri_legA(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_legB

This symbol can be reached by the following aliases: _`get_tri_legB`_, _`get_tri_secondLeg`_, _`get_tri_lineB`_

_**Retrieves the second leg of the triangle**_


#### Definition

```jaiva
F~get_tri_legB(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_legC

This symbol can be reached by the following aliases: _`get_tri_legC`_, _`get_tri_thirdLeg`_, _`get_tri_lineC`_

_**Retrieves the third leg of the triangle**_


#### Definition

```jaiva
F~get_tri_legC(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_windingA

This symbol can be reached by the following aliases: _`get_tri_windingA`_, _`get_tri_firstPoint`_, _`get_tri_pointA`_

_**Retrieves the first point of the triangle's winding**_


#### Definition

```jaiva
F~get_tri_windingA(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_windingB

This symbol can be reached by the following aliases: _`get_tri_windingB`_, _`get_tri_secondPoint`_, _`get_tri_pointB`_

_**Retrieves the second point of the triangle's winding**_


#### Definition

```jaiva
F~get_tri_windingB(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_windingC

This symbol can be reached by the following aliases: _`get_tri_windingC`_, _`get_tri_thirdPoint`_, _`get_tri_pointC`_

_**Retrieves the third point of the triangle's winding**_


#### Definition

```jaiva
F~get_tri_windingC(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_tri_doubleSided

This symbol can be reached by the following aliases: _`get_tri_doubleSided`_, _`get_tri_isDoubleSided`_

_**Retrieves the double sided property**_


#### Definition

```jaiva
F~get_tri_doubleSided(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_curve_start

This symbol can be reached by the following aliases: _`get_curve_start`_, _`get_curve_pointA`_

_**Retrieves the start point of the curve**_


#### Definition

```jaiva
F~get_curve_start(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_curve_control

This symbol can be reached by the following aliases: _`get_curve_control`_, _`get_curve_pointB`_

_**Retrieves the control point of the curve**_


#### Definition

```jaiva
F~get_curve_control(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_curve_end

This symbol can be reached by the following aliases: _`get_curve_end`_, _`get_curve_pointC`_

_**Retrieves the end point of the curve**_


#### Definition

```jaiva
F~get_curve_end(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_curve_amount

This symbol can be reached by the following aliases: _`get_curve_amount`_

_**Retrives the amount of lines tht this curve will decompose into.**_


#### Definition

```jaiva
F~get_curve_amount(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_line_start

This symbol can be reached by the following aliases: _`get_line_start`_, _`get_line_pointA`_

_**Retrieves the start point of the line**_


#### Definition

```jaiva
F~get_line_start(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_line_end

This symbol can be reached by the following aliases: _`get_line_end`_, _`get_line_pointB`_

_**Retrieves the control point of the line**_


#### Definition

```jaiva
F~get_line_end(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_vector3_x

This symbol can be reached by the following aliases: _`get_vector3_x`_, _`get_vector3_X`_, _`get_vector3_left`_

_**Returns the X property of the given Vector3 object**_


#### Definition

```jaiva
F~get_vector3_x(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_vector3_y

This symbol can be reached by the following aliases: _`get_vector3_y`_, _`get_vector3_Y`_, _`get_vector3_up`_

_**Returns the Y property of the given Vector3 object**_


#### Definition

```jaiva
F~get_vector3_y(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_vector3_z

This symbol can be reached by the following aliases: _`get_vector3_z`_, _`get_vector3_Z`_, _`get_vector3_forward`_

_**Returns the Z property of the given Vector3 object**_


#### Definition

```jaiva
F~get_vector3_z(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_vector3_func

This symbol can be reached by the following aliases: _`get_vector3_func`_, _`get_vector3_asFunc`_, _`get_vector3_f`_, _`get_vector3_F`_

_**v**_


#### Definition

```jaiva
F~get_vector3_func(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_colour_red

This symbol can be reached by the following aliases: _`get_colour_red`_, _`get_colour_r`_

_**Returns the Red property of the given Colour object**_


#### Definition

```jaiva
F~get_colour_red(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_colour_green

This symbol can be reached by the following aliases: _`get_colour_green`_, _`get_colour_g`_

_**Returns the Green property of the given Colour object**_


#### Definition

```jaiva
F~get_colour_green(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_colour_blue

This symbol can be reached by the following aliases: _`get_colour_blue`_, _`get_colour_b`_

_**Returns the Blue property of the given Colour object**_


#### Definition

```jaiva
F~get_colour_blue(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_colour_alpha

This symbol can be reached by the following aliases: _`get_colour_alpha`_, _`get_colour_a`_

_**Returns the Blue property of the given Colour object**_


#### Definition

```jaiva
F~get_colour_alpha(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_colour_func

This symbol can be reached by the following aliases: _`get_colour_func`_, _`get_colour_asFunc`_, _`get_colour_f`_, _`get_colour_F`_

_**v**_


#### Definition

```jaiva
F~get_colour_func(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---


### get_point_id

This symbol can be reached by the following aliases: _`get_point_id`_

_**Retrieves the id specified by the input GObject (point)**_


#### Definition

```jaiva
F~get_point_id(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [UUID] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak id <- get_point_id(object);

khuluma(id)!
```


---


### get_point_pivot

This symbol can be reached by the following aliases: _`get_point_pivot`_

_**Retrieves the pivot specified by the input GObject (point)**_


#### Definition

```jaiva
F~get_point_pivot(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Vector3] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak pivot <- get_point_pivot(object);

khuluma(pivot)!
```


---


### get_point_color

This symbol can be reached by the following aliases: _`get_point_color`_, _`get_point_colour`_

_**Retrieves the color specified by the input GObject (point)**_


#### Definition

```jaiva
F~get_point_color(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Colour] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak color <- get_point_color(object);

khuluma(color)!
```


---


### get_line_id

This symbol can be reached by the following aliases: _`get_line_id`_

_**Retrieves the id specified by the input GObject (line)**_


#### Definition

```jaiva
F~get_line_id(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [UUID] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak id <- get_line_id(object);

khuluma(id)!
```


---


### get_line_pivot

This symbol can be reached by the following aliases: _`get_line_pivot`_

_**Retrieves the pivot specified by the input GObject (line)**_


#### Definition

```jaiva
F~get_line_pivot(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Vector3] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak pivot <- get_line_pivot(object);

khuluma(pivot)!
```


---


### get_line_color

This symbol can be reached by the following aliases: _`get_line_color`_, _`get_line_colour`_

_**Retrieves the color specified by the input GObject (line)**_


#### Definition

```jaiva
F~get_line_color(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Colour] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak color <- get_line_color(object);

khuluma(color)!
```


---


### get_tri_id

This symbol can be reached by the following aliases: _`get_tri_id`_

_**Retrieves the id specified by the input GObject (tri)**_


#### Definition

```jaiva
F~get_tri_id(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [UUID] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak id <- get_tri_id(object);

khuluma(id)!
```


---


### get_tri_pivot

This symbol can be reached by the following aliases: _`get_tri_pivot`_

_**Retrieves the pivot specified by the input GObject (tri)**_


#### Definition

```jaiva
F~get_tri_pivot(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Vector3] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak pivot <- get_tri_pivot(object);

khuluma(pivot)!
```


---


### get_tri_color

This symbol can be reached by the following aliases: _`get_tri_color`_, _`get_tri_colour`_

_**Retrieves the color specified by the input GObject (tri)**_


#### Definition

```jaiva
F~get_tri_color(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Colour] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak color <- get_tri_color(object);

khuluma(color)!
```


---


### get_curve_id

This symbol can be reached by the following aliases: _`get_curve_id`_

_**Retrieves the id specified by the input GObject (curve)**_


#### Definition

```jaiva
F~get_curve_id(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [UUID] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak id <- get_curve_id(object);

khuluma(id)!
```


---


### get_curve_pivot

This symbol can be reached by the following aliases: _`get_curve_pivot`_

_**Retrieves the pivot specified by the input GObject (curve)**_


#### Definition

```jaiva
F~get_curve_pivot(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Vector3] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak pivot <- get_curve_pivot(object);

khuluma(pivot)!
```


---


### get_curve_color

This symbol can be reached by the following aliases: _`get_curve_color`_, _`get_curve_colour`_

_**Retrieves the color specified by the input GObject (curve)**_


#### Definition

```jaiva
F~get_curve_color(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

_**Returns:**_ **The [Colour] structured array**

Since Version: _1.0.0_


#### Example: 

```jaiva
tsea "j3d/objects/getters"!

@ If (object) holds the structured array

maak object!
maak color <- get_curve_color(object);

khuluma(color)!
```


---


### get_uuid_string

This symbol can be reached by the following aliases: _`get_uuid_string`_, _`get_uuid_of`_, _`get_uuid_value`_

_**Retrieves the value of the UUID as a string**_


#### Definition

```jaiva
F~get_uuid_string(array)
```
- **_array_**  **`<-`** _**[]**_
	 - _The array to extract the information from_

---

## Variables
