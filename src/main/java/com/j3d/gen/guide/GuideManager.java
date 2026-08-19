package com.j3d.gen.guide;

import com.j3d.gen.guide.steps.*;
import com.j3d.ui.engine.GuidePanel;

public class GuideManager {
    private final GuidePanelAdapter adapter;
    private GuideFlow flow;

    public GuidePanelAdapter getAdapter() {
        return adapter;
    }

    public GuideManager(GuidePanel panel) {
        adapter = new GuidePanelAdapter(panel);
    }

    // each GuideFlow() should be reinstantiated from nothing.

    public void start() {

        if (flow != null && !flow.finished()) return;

        flow = new GuideFlow(
                () -> adapter
        );
        flow.add(new WelcomeStep());
        flow.add(new SceneExplStep());
        flow.add(new ObjectExplainerStep());
        flow.add(new SelectionStep());
        flow.add(new UIExplainerStep());
        flow.add(new CommandPaletteStep());

        flow.start();
    }

    public GuideFlow getFlow() {
        return flow;
    }
}
