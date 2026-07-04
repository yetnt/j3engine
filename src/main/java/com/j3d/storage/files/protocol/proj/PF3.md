# ProjectFileV3 (Changes to V2)

Structure

Bold - Changes from V2

Italics - V3 addition

> "Padding" is anything that is used to separate
> either data from a list, or elements within
> a list.

> - "Padding A": Single UTF-8 Char `?` that
> signifies the
> start of a numbered list where the next
> element is the amount of data to expect
> within the list.
> - "Padding B": full byte of 1s between
> list elements.
> - "Padding C": Single UTF-8 String `!` that
> signifies the end of a numbered list.

> The last section being the actual Thing
> serialization, within PF1 and PF2 is stored
> as a list of layer indexes with its own
> list of Things. Instead, PF3 makes it consistent
> with other serialization by storing the layer's
> identifier as a property of the Thing to simplify
> the logic

- J3D Header `UTF-8` `(J3D)`
- **J3D Version `short` `(1)`** 
- PROJECT Header `UTF-8` `PROJECT`
- PROJECT Header Version `int` `(3)`
- _`Padding A`_
- Number of Layers `int`
  - _`Padding B`_
  - Layer Identifier `UTF-8`
  - Layer Hidden `boolean`
  - ...
- _`Padding C`_
- _`Padding A`_
- Number of GPoints `int`
  - _`Padding B`_
  - Point UUID `UTF-8`
  - Parent Thing UUID `UTF-8`
  - X `double`
  - Y `double`
  - Z `double`
  - Colour `#AARRGGBB signed int32`
  - ...
- _`Padding C`_
- _`Padding A`_
- Number of GLines `int`
  - _`Padding B`_
  - Line UUID `UTF-8`
  - Parent THing UUID `UTF-8`
  - Colour `#AARRGGBB signed in32`
  - Point A UUID `UTf-8`
  - Point B UUID `UTF-8`
  - ...
- _`Padding C`_
- _`Padding A`_
- Number of GTri `int`
  - _`Padding B`_
  - Triangle UUID `UTF-8`
  - Parent Thing UUID `UTF-8`
  - _isDoubleSided `boolean`_
  - Colour `#AARRGGBB signed int32`
  - Line 1 UUID `UTF-8`
  - Line 2 UUID `UTF-8`
  - Line 3 UUID `UTF-8`
  - _Winding Point A UUID `UTF-8`_
  - _Winding Point B UUID `UTF-8`_
  - _Winding Point C UUID `UTF-8`_
  - ...
- _`Padding C`_
- _`Padding A`_
- Number of Things `UTF-8`
  - _`Padding B`_
  - Thing UUID `UTF-8`
  - Thing Name `UTF-8`
  - _Parent Layer Identifier `UTF-8`_
  - Thing Visibility `boolean`
  - _Solid `boolean`_
  - ...
- _`Padding C`_