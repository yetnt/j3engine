package com.j3d.gen.guide.steps;

import com.j3d.gen.guide.Anchor;
import com.j3d.gen.guide.GuidePanelAdapter;
import com.j3d.gen.guide.generic.DoubleClickStep;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;

public class UIExplainerStep extends DoubleClickStep {

    @Override
    public void build(GuidePanelAdapter adapter) {
        guideCounter(adapter);
        genericText(adapter, "(Double click to continue)");

        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(
                                "The top panel with buttons, called the Toolbox, has buttons for executing quick actions"
                        )
                                .add(
                                        " (mostly on a selection of objects or other things. You can hover over the button to find out)"
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "There is also the Context Menu (right click the scene to open it) which shows actions you can do "
                                ).add(
                                        "but only when you're doing some specific action."
                                )
                                .addLn(JLabelRichText.LINE_BREAK)
                                .add(
                                        "All of these buttons are just abstractions over the bottom text field input, called the command palette. "
                                )
                                .add(
                                        "That will be discussed in the next help section, but essentially the engine strives to make everything"
                                ).add(
                                        " in terms of \"executable commands\" which you can type yourself in the command palette. The Toolbox "
                                ).add(
                                        "and Context Menu are just user friendly ways to access the command palette."
                                )
                                .wrapDiv(280)
                                .font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.WEST | Anchor.CENTRE,
                0, Anchor.offsetDown(40)
        );
    }
}
