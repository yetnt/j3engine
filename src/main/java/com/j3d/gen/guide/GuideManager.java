package com.j3d.gen.guide;

import com.j3d.gen.guide.steps.WelcomeStep;
import com.j3d.ui.engine.GuidePanel;

public class GuideManager {
    private GuidePanelAdapter adapter;
    private GuideFlow flow;

    public GuidePanelAdapter getAdapter() {
        return adapter;
    }

    public GuideManager(GuidePanel panel) {
        adapter = new GuidePanelAdapter(panel);
    }

    public void start() {

        if (flow != null && !flow.finished()) return;

        flow = new GuideFlow(
                () -> adapter
        );
        flow.add(() -> new WelcomeStep(this, flow));
        flow.start();
    }

    public GuideFlow getFlow() {
        return flow;
    }
}
