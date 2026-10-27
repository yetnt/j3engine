# j3d/v3 (Library)

_v3_
## Exports
This library exports the following libraries: 
## Table of Contents
| Alias | Link |
| --- | --- |
| v3_add | [v3_add](#v3_add) |
| v3_sub | [v3_sub](#v3_sub) |
| v3_mult | [v3_mult](#v3_mult) |
| v3_cross | [v3_cross](#v3_cross) |
| v3_dot | [v3_dot](#v3_dot) |
| v3_dist | [v3_dist](#v3_dist) |
| v3_norm | [v3_norm](#v3_norm) |
| v3_mag | [v3_mag](#v3_mag) |
| v3_equal | [v3_equal](#v3_equal) |
| v3_isNotZero | [v3_isNotZero](#v3_isNotZero) |
| v3_zeroClamp | [v3_zeroClamp](#v3_zeroClamp) |
| v3_rot | [v3_rot](#v3_rot) |
## Functions

### v3_add

This symbol can be reached by the following aliases: _`v3_add`_

_**Adds the 2 given vectors' components**_


#### Definition

```jaiva
F~v3_add(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_sub

This symbol can be reached by the following aliases: _`v3_sub`_

_**Subtracts the 2 given vectors' components**_


#### Definition

```jaiva
F~v3_sub(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_mult

This symbol can be reached by the following aliases: _`v3_mult`_

_**Multiplies the 2 given vectors' components**_


#### Definition

```jaiva
F~v3_mult(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_cross

This symbol can be reached by the following aliases: _`v3_cross`_

_**Calculates the cross product between the 2 given vectors**_


#### Definition

```jaiva
F~v3_cross(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_dot

This symbol can be reached by the following aliases: _`v3_dot`_

_**Calculates the dot product between the 2 given vectors**_


#### Definition

```jaiva
F~v3_dot(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_dist

This symbol can be reached by the following aliases: _`v3_dist`_

_**Calculates the distance between the 2 given vectors' components**_


#### Definition

```jaiva
F~v3_dist(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_norm

This symbol can be reached by the following aliases: _`v3_norm`_

_**Normalizes the given vector**_


#### Definition

```jaiva
F~v3_norm(vec3)
```
- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_mag

This symbol can be reached by the following aliases: _`v3_mag`_

_**Returns the magnitude of the given vector**_


#### Definition

```jaiva
F~v3_mag(vec3)
```
- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_equal

This symbol can be reached by the following aliases: _`v3_equal`_

_**Gives whether the 2 given vectors' components are equal**_


#### Definition

```jaiva
F~v3_equal(vec3, secondVec3)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_secondVec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_isNotZero

This symbol can be reached by the following aliases: _`v3_isNotZero`_

_**Returns true if any property of this vector is not zero.**_


#### Definition

```jaiva
F~v3_isNotZero(vec3)
```
- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_zeroClamp

This symbol can be reached by the following aliases: _`v3_zeroClamp`_

_**Clamps the given vector by comparing it's components to an epsilon of 10^-10. If a component is less than that it gets clamped to 0**_


#### Definition

```jaiva
F~v3_zeroClamp(vec3)
```
- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_

Since Version: _1.3.0_


---


### v3_rot

This symbol can be reached by the following aliases: _`v3_rot`_

_**Uses  Rodrigues' rotation formula magic**_


#### Definition

```jaiva
F~v3_rot(vec3, axis, angleDeg)
```

- **_vec3_**  **`<-`** _**[]**_
	 - _The vector3 list, either a J3Engine Object or 3 element array_
- **_axis_**  **`<-`** _**[]**_
	 - _The vector3 axis to rotate around_
- **_angleDeg_**  **`<-`** _**number**_
	 - _angle in degrees_

Since Version: _1.3.0_


---

## Variables
