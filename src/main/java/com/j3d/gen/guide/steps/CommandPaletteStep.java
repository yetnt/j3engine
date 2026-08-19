package com.j3d.gen.guide.steps;

import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.commands.camera.orbit.OrbitCmd;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.StatefulCommandCompletedPayload;
import com.j3d.gen.guide.Anchor;
import com.j3d.gen.guide.GuideInfo;
import com.j3d.gen.guide.GuidePanelAdapter;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;

public class CommandPaletteStep extends GuideInfo {
    public CommandPaletteStep() {
        super(
                CommandsManager.commands
        );
    }

    @Override
    public void build(GuidePanelAdapter adapter) {
        guideCounter(adapter);
        genericText(adapter, "(Type \"camera orbit\" in the command palette and hit Enter to continue)");

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(
                                "The command palette (the input field below) is a powerful way to use the engine"
                        )
                                .add(
                                        " other than just it's UI. A command by itself can either be \"stateless\" meaning"
                                )
                                .add(
                                        " it just shows output and nothing more, or \"stateful\", where it requires you to do "
                                ).add(
                                        "something like move your mouse or click your arrow keys. Each command will tell you"
                                ).add(
                                        " when your input is needed."
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "You can start typing letters into the palette to discover commands. once you finish a valid "
                                ).add(
                                        "command name, it will display the next input you should type to successfully enter the command. "
                                )
                                .add(
                                        "Once you have your command, hit ENTER to execute it. "
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "(You can further more, use the up and down arrow "
                                )
                                .add(
                                        "keys to go back to commands you executed before, and if you're in the palette and need to exit you can hit ESC "
                                ).add(
                                        "so you stop typing. You can also use the right arrow key to autocomplete a command name or argument string)"
                                )
//                                .addLn(JLabelRichText.LINE_BREAK)
                                .wrapDiv(280)
                                .bold()
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "4"
                                )
                                .wrapHTML()
                ),
                Anchor.WEST | Anchor.CENTRE
                , Anchor.offsetRight(50), Anchor.offsetUp(10)
        );

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(
                                "The input a command can take usually looks like this (refer to Documentation for more thorough details on Command Arguments) "
                        )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(new JLabelRichText("<vector3>", true).bold())
                                .add(" - A position or direction. This is in the form of (number, number, number). like ")
                                .add(new JLabelRichText("(10, 3, -24)").bold().italic())
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(new JLabelRichText("<string>", true).bold())
                                .add(" - simply input that is between a pair of double quotes. like ")
                                .add(new JLabelRichText("\"my string\"").bold().italic())
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(new JLabelRichText("<number>", true).bold())
                                .add(" - simply a number. (if it needs a whole number, it will say integer) like ")
                                .add(new JLabelRichText("10").bold().italic())
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(new JLabelRichText("<boolean>", true).bold())
                                .add(" - either true or false. like ")
                                .add(new JLabelRichText("true").bold().italic())
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(new JLabelRichText("<point>/<line>/<tri>/<curve>/<thing>", true).bold())
                                .add(" - an object that is currently selected in the scene. These you need to type out the UUID id of the object. these can be found within the properties panel. an Exmaple would be ")
                                .add(new JLabelRichText("a7e58b1c-2d3f-4a5b-6c7d-8e9f0a1b2c3d").bold().italic())
                                .add(" without the quotes, this isn't a string")
                                .addLn(JLabelRichText.LINE_BREAK)
                                .addLn("And alot more input")
                                .addLn(JLabelRichText.LINE_BREAK)
                                .wrapDiv(280)
//                                .bold()
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "4"
                                )
                                .wrapHTML()
                ),
                Anchor.EAST | Anchor.CENTRE,
                Anchor.offsetRight(100), Anchor.offsetDown(40)
        );

        System.out.println("d");
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.STATEFUL_COMMAND_COMPLETED) {
            StatefulCommandCompletedPayload
                    payload =(StatefulCommandCompletedPayload) properties;
            if (payload.emitter instanceof OrbitCmd) {
                close();
            }
        }
    }
}
