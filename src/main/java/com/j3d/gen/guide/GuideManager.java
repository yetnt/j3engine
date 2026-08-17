package com.j3d.gen.guide;

import com.j3d.gen.guide.steps.SceneExplStep;
import com.j3d.gen.guide.steps.SelectionStep;
import com.j3d.gen.guide.steps.WelcomeStep;
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
        flow.add(new SelectionStep());

        flow.start();
    }

    public GuideFlow getFlow() {
        return flow;
    }
}
