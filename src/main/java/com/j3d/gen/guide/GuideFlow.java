package com.j3d.gen.guide;

import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class GuideFlow extends EventEmitter implements EventListener {
    ArrayList<Supplier<GuideInfo>> guides = new ArrayList<>();
    private int totalSize = 0;
    LinkedHashSet<GuideInfo> allGuides = new LinkedHashSet<>();
    Supplier<GuidePanelAdapter> adapterSupplier;

    public GuideFlow(Supplier<GuidePanelAdapter> adapterSupplier) {
        this.adapterSupplier = adapterSupplier;
    }

    public GuideFlow add(Supplier<GuideInfo> guideInfo) {
        guides.add(guideInfo);
        totalSize++;
        return this;
    }

    public void start() {
        GuidePanelAdapter gpa = adapterSupplier.get();
        if (guides.isEmpty()) {
            System.out.println("No guides left!");
            gpa.repaint();
            return;
        }
        GuideInfo g = guides.getFirst().get();
        allGuides.add(g);
        g.build(gpa);

        gpa.repaint();
    }

    public boolean finished() {
        return guides.isEmpty();
    }

    public int indexOf(GuideInfo i) {
        return new ArrayList<>(allGuides).indexOf(i);
    }

    public int totalSize() {
        return totalSize;
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.GUIDE_CLOSING) {
            GuideInfoClosingEvent
                    event1 = (GuideInfoClosingEvent) properties;
            guides.removeFirst();

            start();

        }
    }
}
