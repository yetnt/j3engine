```mermaid
classDiagram

class MyFavClass {
+field: string
-field: number
-getSomething() int
}

class B {
-another: string
}

class C {
-secretMethod() Object
}

A --> B
C --o B
```
