package com.j3d.engine.interact.cmd.base;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.ui.util.SafeJLabel;

import java.util.*;
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
 *     <li>A sophisticated mechanism ({@link #parseUsages}) to automatically generate
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
    public String usage = "This will be auto-generated.";
    public ArrayList<Argument> args = new ArrayList<>();
    /**
     * A flag indicating whether this command accepts a variable number of tagged arguments (e.g., key:value pairs).
     */
    private boolean variadicTaggedArgs = true;

    /**
     * A map to hold different usages based on argument types.
     * Where for a command which may have multiple types for one argument,
     * the key is an ArrayList of Classes representing the types of the arguments,
     * and the value is a String representing the usage for that specific combination of argument types.
     * <p>
     *     For example, for a command that can take either an Integer or a String as its first argument,
     *     and a Double as its second argument, you might have two entries in the usages map:
     *     <ul>
     *         <li>Key: [Integer.class, Double.class], Value: "command &lt;int&gt; &lt;double&gt;"</li>
     *         <li>Key: [String.class, Double.class], Value: "command &lt;string&gt; &lt;double&gt;"</li>
     *     </ul>
     *     This allows the command to provide specific usage instructions based on the types of arguments provided
     * </p>
     */
    protected HashMap<ArrayList<Class>, String> usages = new HashMap<>();

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
        Static.getLog().println(
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
     * This method populates the usages map with all possible usages of the command,
     * taking into account subcommands and typed arguments.
     * @implSpec
     *     If an inheriting class does some special shenanigans with it's arguments.
     *     It should handle the parsing itself.
     * @return The Command instance with populated usages.
     */
    public Command parseUsages() {
        // This method is going to be long as hell I can already feel it.
        // For each returned usage, don't prefix the command name, just the arguments.
        // So later the suggestions can prefix the command name or the given alias for said command.
        ArrayList<ArrayList<Class>> typeAccumulator = new ArrayList<>();
        ArrayList<StringBuilder> usageAccumulator = new ArrayList<>();
        for (Argument arg : args) {
            if (arg instanceof Subcommand sub) {
                // Step 1: If the argument is a Subcommand, get all it's usages and add
                // them to this command's usages.
                for (var entry : sub.getUsages().entrySet()) {
                    ArrayList<Class> key = new ArrayList<>();
                    key.addFirst(String.class); // The first argument is always the subcommand name
                    key.addAll(entry.getKey());
                    String value = sub.aliases.getFirst() + " " + entry.getValue();
                    usages.put(key, value);
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
                    Class cls = tArg.getType().get(i);
                    // If the accumulators are empty at the index, we need to add a new entry.
                    if (typeAccumulator.size() <= i) {
                        typeAccumulator.add(new ArrayList<>());
                        usageAccumulator.add(new StringBuilder());
                    }
                    ArrayList<Class> clsList = typeAccumulator.get(i);
                    clsList.add(cls);
                    StringBuilder usageAccumulatorEntry = usageAccumulator.get(i);
                    switch (cls.getSimpleName()) {
                        case "Thing" -> usageAccumulatorEntry.append("<Thing").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GPoint" -> usageAccumulatorEntry.append("<point").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GLine" -> usageAccumulatorEntry.append("<line").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "GTri" -> usageAccumulatorEntry.append("<triangle").append(tArg.isOptional() ? "?" : "").append("> ");
                        case "Color" -> usageAccumulatorEntry.append("#color").append(tArg.isOptional() ? "?" : "").append("# ");
                        case "Vector3" -> usageAccumulatorEntry.append("(vector3").append(tArg.isOptional() ? "?" : "").append(") ");
                        case "String" -> usageAccumulatorEntry.append("(string").append(tArg.isOptional() ? "?" : "").append(") ");
                        case "Double" -> usageAccumulatorEntry.append("(number").append(tArg.isOptional() ? "?" : "").append(") ");
                        case "Any" -> usageAccumulatorEntry.append("<any").append(tArg.isOptional() ? "?" : "").append("> ");
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
                if (typeAccumulator.isEmpty()) {
                    typeAccumulator.add(new ArrayList<>(List.of(String.class)));
                    usageAccumulator.add(new StringBuilder("[" + String.join("|", setArg.getAllowedValues()) + "] "));
                } else {
                    for (ArrayList<Class> clsList : typeAccumulator) {
                        clsList.add(String.class);
                    }
                    for (StringBuilder usage : usageAccumulator) {
                        usage.append("[").append(String.join("|", setArg.getAllowedValues())).append(setArg.isOptional() ? "?" : "").append("] ");
                    }
                }
            } else {
                throw new IllegalStateException("Unknown argument type: " + arg.getClass().getSimpleName());
            }
        }

        // Now we need to combine the typeAccumulator and usageAccumulator into the usages map.
        for (int i = 0; i < typeAccumulator.size(); i++) {
            ArrayList<Class> key = typeAccumulator.get(i);
            String value = usageAccumulator.get(i).toString().trim()
                    + (variadicTaggedArgs ? " ...key:value" : "");
            usages.put(key, value);
        }

        return this;
    }

    /**
     * Retrieves the map of all parsed usages for this command.
     * The key is a list of argument types, and the value is the corresponding usage string.
     *
     * @return A {@link HashMap} containing all possible usage patterns.
     */
    public HashMap<ArrayList<Class>, String> getUsages() {
        return usages;
    }

    /**
     * Returns all usages that match or partially match the given argument types.
     * This is useful for providing dynamic usage suggestions based on the types of arguments provided.
     * @param alias The alias of the command to prefix the usage with.
     * @param types The argument types to match against.
     * @return An array of usage strings that match the given argument types.
     */
    public String[] returnUsagesWhere(String alias, Class ...types) {
        ArrayList<String> matchedUsages = new ArrayList<>();
        for (var entry : usages.entrySet()) {
            ArrayList<Class> key = entry.getKey();
            String value = entry.getValue();
            // Check if the key matches or partially matches the given types
            boolean matches = true;
            for (int i = 0; i < types.length; i++) {
                if (i >= key.size() || !key.get(i).isAssignableFrom(types[i])) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                matchedUsages.add(alias + " " +value);
            }
        }
        return matchedUsages.toArray(new String[0]);
    }
}
