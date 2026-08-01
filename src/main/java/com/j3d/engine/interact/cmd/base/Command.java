package com.j3d.engine.interact.cmd.base;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.*;
import com.j3d.ui.SafeJLabel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@code Command} encapsulates all the information needed to define and execute
 * a user-callable action. This includes its name, aliases, description, and a
 * structured list of accepted {@link Argument}s.
 * <p>
 * Key responsibilities of this class include:
 * <ul>
 *     <li>Defining the command's identity and structure.</li>
 *     <li>Providing a {@link #run} method for subclasses to implement their specific logic.</li>
 *     <li>Supporting a hierarchy of commands through subcommand dispatching.</li>
 *     <li>A mechanism ({@link #parseUsages}) to automatically generate
 *         detailed usage strings based on its arguments, which is crucial for
 *         providing dynamic help and command suggestions to the user.</li>
 * </ul>
 *
 * @author Lehlogonolo Poole
 * @see Argument
 * @see Subcommand
 * @see CommandsManager
 */
public class Command {
    /**
     * A list of all names this command can be called by, including the primary name.
     */
    public ArrayList<String> aliases = new ArrayList<>();
    /**
     * A user-friendly description of what the command does.
     */
    public String description;
    public ArrayList<Argument> args = new ArrayList<>();
    private boolean hasNoArgUsage = false;
    /**
     * A flag indicating whether this command accepts a variable number of tagged arguments (e.g., key:value pairs).
     */
    private boolean variadicTaggedArgs = true;

    protected ArrayList<String> usages = new ArrayList<>();

    /**
     * Constructs a new Command.
     *
     * @param name The primary name of the command. This will also be its first alias.
     * @param d    A user-friendly description of the command.
     */
    public Command(String name, String d) {
        aliases.add(name);
        description = d;
    }

    /**
     * Adds one or more aliases to this command.
     *
     * @param a A varargs array of alias strings.
     * @return This Command instance for method chaining.
     */
    public Command aliases(String... a) {
        aliases.addAll(Arrays.asList(a));
        return this;
    }

    /**
     * Adds one or more arguments to this command's definition.
     *
     * @param a A varargs array of {@link Argument} objects.
     * @return This Command instance for method chaining.
     */
    public Command args(Argument... a) {
        args.addAll(Arrays.asList(a));
        return this;
    }

    public Command addNoArgUsage() {
        hasNoArgUsage = true;
        usages.add(
                "...key:value"
        );
        return this;
    }

    /**
     * The method to be overridden by subclasses to implement command functionality.
     *
     * @param logLabel   The JLabel to display log messages.
     * @param aliasUsed  The alias of the command that was used to invoke it.
     * @param args       The arguments passed to the command.
     * @param taggedArgs Tagged arguments passed to the command.
     */
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        // To be overridden by subclasses
        StaticRefs.getLog().cmdPrintln(
                (this instanceof Subcommand ? "Subcommand" : "Command") +
                " invoked: \"" + aliasUsed + "\" ("+aliases.getFirst()+"), " + "with an args length of " + args.length + " and " + taggedArgs.size() + " tagged arguments."
        );
    }

    /**
     * Dispatches the command to the appropriate subcommand based on the first argument.
     *
     * @param subcommandName The name of the subcommand to dispatch to.
     * @param args           The raw arguments passed to the main command, including the subcommand name as the first argument.
     * @param taggedArgs
     */
    protected void dispatchToSubcommands(String subcommandName, SafeJLabel logLabel, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        for (Argument arg : this.args) {
            if (!(arg instanceof Subcommand subcommand)) continue;
            if (subcommand.aliases.contains(subcommandName.toLowerCase())) {
                Object[] subArgs = new Object[args.length - 1];
                String alias = (String) args[0];
                System.arraycopy(args, 1, subArgs, 0, args.length - 1);
                subcommand.run(logLabel, alias, subArgs, taggedArgs);
                return;
            }
        }
    }

    /**
     * Returns a stream of all the aliases of the command. (Including it's own name)
     * @return A stream of all the aliases of the command.
     */
    public Stream<String> aliasStream() {
        return aliases.stream();
    }

    /**
     * Parses the usages of the command based on its arguments.
     * This method populates the usages list with all possible usages of the command,
     * taking into account subcommands and typed arguments.
     * @implSpec
     *     If an inheriting class does some special shenanigans with its arguments.
     *     It should handle the parsing itself.
     * @return The Command instance with populated usages.
     */
    public Command parseUsages() {
        // This method is going to be long as hell I can already feel it.
        // For each returned usage, don't prefix the command name, just the arguments.
        // So later the suggestions can prefix the command name or the given alias for said command.
        ArrayList<StringBuilder> usageAccumulator = new ArrayList<>();
        for (Argument arg : args) {
            if (arg instanceof Subcommand sub) {
                // Step 1: If the argument is a Subcommand, get all it's usages and add
                // them to this command's usages.
                for (var entry : sub.getUsages()) {
                    String value = sub.aliases.getFirst() + " " + entry;
                    usages.add(value);
                }
                continue; // If a command has subcommands, it can't have anything else. So exit early.
                // Not bad. That was simple its just recursion.
            }
            // separated from the above if for clarity.
            if (arg instanceof TypedArg tArg) {
                // Step 2: If the argument is a TypedArg, we need to unfortunately handle
                // multiple types.
                // For something that is defined by it's UUID,
                //      use <ClassName> (Goes for Thing, GObject, GPoint, GLine and GTri)
                // For something that is a direct type
                //      use (Type) (Goes for Vector3, String, Double)
                // Special cases:
                // Color: use #Color#
                // Next problem: a TypedArg can take multiple types. And we need to
                // create a usage for each type.
                for (int i = 0; i < tArg.getType().size(); i++) {
                    Class<?> cls = tArg.getType().get(i);
                    // If the accumulators are empty at the index, we need to add a new entry.
                    if (usageAccumulator.size() <= i) {
                        usageAccumulator.add(new StringBuilder());
                    }
                    StringBuilder usageAccumulatorEntry = usageAccumulator.get(i);
                    switch (cls.getSimpleName()) {
                        case "Thing" -> usageAccumulatorEntry.append("<thing").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GPoint" -> usageAccumulatorEntry.append("<point").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GLine" -> usageAccumulatorEntry.append("<line").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GTri" -> usageAccumulatorEntry.append("<tri").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Color" -> usageAccumulatorEntry.append("<#color").append(tArg.isOptional() ? "?" : "").append("#> ");
                        case "Vector3" -> usageAccumulatorEntry.append("<vector3").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "String" -> usageAccumulatorEntry.append("<string").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Integer" -> usageAccumulatorEntry.append("<int").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Double" -> usageAccumulatorEntry.append("<number").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Any" -> usageAccumulatorEntry.append("<any").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Boolean" -> usageAccumulatorEntry.append("<boolean").append(tArg.isOptional() ? "?" : "").append("> ");
                        default -> throw new IllegalStateException("Unexpected value: " + cls.getSimpleName());
                    }
                }
            }
//            else if (arg instanceof TaggedArgUtil tagArg) {
            // All commands by default tag a variadic amount of tagged args
//            }
            else if (arg instanceof ArgSet setArg) {
                // Another simple one, An ArgSet is always a set of predefined strings.
                // However, we need to add this arg to every usage within the accumulator and typeAccumulator
                // If the accumulators are empty, we need to add a new entry.
                if (usageAccumulator.isEmpty()) {
                    usageAccumulator.add(new StringBuilder("[" + String.join("|", setArg.getAllowedValues()) + "] "));
                } else {
                    for (StringBuilder usage : usageAccumulator) {
                        usage.append("[").append(String.join("|", setArg.getAllowedValues())).append(setArg.isOptional() ? "?" : "").append("] ");
                    }
                }
            } else {
                throw new IllegalStateException("Unknown argument type: " + arg.getClass().getSimpleName());
            }
        }

        // Now we need to combine the usageAccumulator into the usages map.
        for (StringBuilder stringBuilder : usageAccumulator) {
            String value = stringBuilder.toString().trim()
                    + (variadicTaggedArgs ? " ...key:value" : "");
            usages.add(value);
        }

        return this;
    }

    public ArrayList<String> getUsages() {
        return new ArrayList<>(usages); // i tend to mutate this arry...
    }

    public ArrayList<String> usages(String alias) {
//        if (noArgs) return new String[]{alias + " ...key:value"};
        return usages
                .stream()
                .map(s -> alias + " " + s)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean varTaggedArgs() {
        return variadicTaggedArgs;
    }

    public boolean hasNoArgs() {
        return hasNoArgUsage;
    }
}
