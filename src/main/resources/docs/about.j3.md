# About

J3Engine (or J3D) is a custom-built, CPU-based 3D Geometry Editor and Visualisation Engine
capable of creating, editing and visualising 3D objects only using Swing 2D Graphics.

<img alt="j3engine logo" src="../art/logo/J3Engine.png" scale="0.09"></img>

---

## Why?

I wanted to challenge myself. This project came mid-way through [Jaiva](https://github.com/yetnt/jaiva)
as the thought of making my own custom engine didn't sound as daunting.

---
[Arbitary Link](https://google.com)

It absolutely is. But here is the app either way!

## Design

J3Engine is a Swing app, which uses [2D Swing Graphics](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html). 
Meaning the only core rendering API not made by J3Engine which it utilises, is drawing 2D lines, points, triangles and other shapes.
Everything else like prisms, cubes, 3D curves is all done by J3Engine itself with no outside classes.

---

Most of the features like editing through J3Engine are features which are accessible via the command palette. The entire philosophy
of J3Engine is that everything you should be able to do is just a command in the command palette. This means most of the UI. Be it
the Toolbox, the Context Menu or menu bars just execute commands that you can type yourself into the command palette.