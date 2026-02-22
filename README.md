# J3Engine

<img src="./src/main/resources/art/logo/J3Engine.png" alt="J3Engine Logo"></img>


A (Work-In-Progress) 3D Graphics Engine written in Java from scratch using only 
the Java Standard Library and [Swing](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html)'s 
2D drawing capabilities.

This is a project for my 2026 Matric Practical Assessment Task in Information Technology.

## Features

- 3D rendering using software rasterization
- Camera movement and rotation
- History of transformations for objects
- Command Line Interface
- GUI (wrapper around CLI) using Swing
- Real time rendering*
- And more to come!

> Real time rendering means there is not a "rendering loop" like in most 3D engines.
> Instead, the scene is re-rendered only when there is a change (e.g., camera movement, object transformation).
> Why? Ask the souls who made Java's Swing library... It repaints when it feels like it.

## Installation

While in development, the engine has no official release yet.
To use the engine, clone this repository and compile the Java files using your preferred IDE or command line tools.

(This is a Maven project, so you can also use Maven to handle dependencies and build the project.)

```bash
git clone https://github.com/yetnt/j3engine.git
cd j3engine
mvn compile
mvn exec:java -Dexec.mainClass="j3engine.Main"
```

