package com.j3d.gen.help.properties;

import com.j3d.gen.help.HelpProperties;
import com.j3d.utility.generators.JLabelRichText;

public class HelpText extends JLabelRichText {
    public HelpText(String cont) {
        super(cont);
    }

    public HelpText(String cont, boolean esc) {
        super(cont, esc);
    }

    public HelpText asSubheading() {
        super.italic().underline().heading(Heading.H3);
        return this;
    }
}
