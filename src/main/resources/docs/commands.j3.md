# Commands

Commands are a powerful way to use the engine. A command invokes some operation/state when it itself is invoked and can
be invoked by the user directly through the command palette or by the user via a GUI abstraction.

## Command Definition Syntax

Commands, have


# Key Terms

## Alias

A different label for the exact same command. Technically, the name of the command is defined in the code
as it's first alias but it functions the exact same.

---

Aliases provide a short convenient way to refer to a command without having to remember it's full name.

---

e.g. the command **`transform`** can be invoked via that name or one of it's aliases: 
**`trans`**, **`tr`**

---

below is an example of the [typing hints](#typing-hints) showing all the aliases for the same **`transform`** command

<img alt="Transform command typing hints" src="../art/docs/cmd/alias.png" scale="1"> </img>

## Stateful vs Stateless

### Stateless

A _stateless_ command is any command which is purely immediately side effects. In 
that it will run and then finishes, not retaining any state for future invocations.

---

An example would be a **`print`** command, which simply prints its arguments to the console and then closes.

---

Here is an example after executing the following command

```cmd
debug echo "hello"
```

<img alt="An output of hello" src="../art/docs/cmd/stateless.png"> </img>

Simply prints hello and continues allowing user input.

### Stateful

A _stateful_ command on the other hand, takes over user input and is usually noticeable due to the fact that 
most other operations or commands cannot execute until the current one has finished. These are identifiable
as they usually tell you to give input via **_`ESC`_** to exit the command, or **_`ENTER`_** to commit whatever
the command is showing.

# Typing Hints


