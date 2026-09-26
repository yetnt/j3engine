# Building J3Engine

Requirements:

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) (needed for `jpackage`, which ships with the JDK from Java 14+, so you're covered)
- Maven 3.8+ (or an IDE with Maven support)
- [WiX Toolset v3](https://wixtoolset.org/releases/) — **required on Windows** for the `.msi` build via `jpackage-maven-plugin`. If it's not installed/on PATH, the `package` phase will fail at the `jpackage` goal.
- Git
- An IDE, although you can build this purely in a terminal. Just more pain.

No local setup needed for dependencies — `jaiva` and `ytils` both resolve automatically via JitPack, as declared in the POM.

## Steps

Clone the repo:

```shell
git clone https://github.com/yetnt/j3engine
cd j3engine
```

Make sure you're on `main`:

```shell
git checkout main
git pull
```

Build the project using Maven:

```shell
mvn clean package
```

This will:

- Pull `jaiva` and `ytils` from JitPack
- Compile the project
- Shade everything into a fat jar via `maven-shade-plugin` (`target/J3Engine-1.3.0.jar`, runnable standalone with `com.j3d.Main` as the entrypoint)
- Run `jpackage` to produce a Windows `.msi` installer under `target/jpackage/`, bundling the icon, file associations (`.j3d` project files), Start Menu shortcuts, and a per-user install

If you only want the shaded jar and don't care about the `.msi` (e.g. you're not on Windows, or don't have WiX installed), you can skip the `jpackage` execution:

```shell
mvn clean package -Djpackage.skip=true
```

(If `jpackage-maven-plugin` respects that flag — if not, comment out its `<execution>` block in the POM temporarily, or just accept the failure once `jpackage` finishes and the jar is already sitting in `target/`.)

## Building the Javadoc site locally

If you want to preview what the GitHub Pages Javadoc build produces, without waiting on CI:

```shell
mvn javadoc:javadoc
```

Output lands in `target/site/apidocs/`. Note this runs independently of `package` — it won't build the jar or the `.msi`, and it won't run tests either.

## Running tests

```shell
mvn test
```

Heads up: some tests depend on engine-global state via `StaticRefs` (through the `J3DTest` fixture) — these aren't yet wired into CI, so running them locally before you push is on you for now..