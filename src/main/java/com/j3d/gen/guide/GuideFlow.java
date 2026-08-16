package com.j3d.gen.guide;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class GuideFlow extends EventEmitter implements EventListener {
    ArrayList<GuideInfo> guides = new ArrayList<>();
    private int totalSize = 0;
    LinkedHashSet<GuideInfo> allGuides = new LinkedHashSet<>();
    Supplier<GuidePanelAdapter> adapterSupplier;

    /**
     * Constructs a new GuideFlow with the given adapter supplier.
     *
     * @param adapterSupplier A supplier that provides instances of {@link GuidePanelAdapter}.
     */
    public GuideFlow(Supplier<GuidePanelAdapter> adapterSupplier) {
        this.adapterSupplier = adapterSupplier;
    }

    /**
     * Adds a {@link GuideInfo} to the flow.
     *
     * @param guideInfo The guide information to add.
     * @return This GuideFlow instance for method chaining.
     */
    public GuideFlow add(GuideInfo guideInfo) {
        guides.add(guideInfo);
        totalSize++;
        return this;
    }

    /**
     * Starts the guide flow by displaying the first guide in the queue.
     * If no guides are present, it shows a message dialog.
     * It attaches listeners to the current guide and builds its UI.
     */
    public void start() {
        GuidePanelAdapter gpa = adapterSupplier.get();
        if (guides.isEmpty()) {
            JOptionPane.showMessageDialog(
                    StaticRefs.getMainFrame(),
                    "No more guides to show!"
            );
            gpa.repaint();
            return;
        }
        GuideInfo g = guides.getFirst();
        // Keep track of all guides ever shown for indexinggg
        allGuides.add(g);

        g.attachListeners(this);
        g.build(gpa);

        System.out.println(g.getClass().getSimpleName());

        gpa.repaint();
    }

    /**
     * Checks if all guides in the flow have been processed.
     *
     * @return {@code true} if there are no more guides to show, {@code false} otherwise.
     */
    public boolean finished() {
        return guides.isEmpty();
    }

    /**
     * Returns the index of a given {@link GuideInfo} within the set of all guides ever processed.
     *
     * @param i The {@link GuideInfo} to find the index of.
     * @return The zero-based index of the guide, or -1 if the guide is not found.
     */
    public int indexOf(GuideInfo i) {
        return new ArrayList<>(allGuides).indexOf(i);
    }

    /**
     * Returns the total number of guides that were initially added to this flow.
     *
     * @return The total count of guides.
     */
    public int totalSize() {
        return totalSize;
    }

    /** The debounce period in seconds to prevent rapid guide closing events. */
    private final int DEB = 5;
    /** The last time a guide closing event was processed, used for debouncing. */
    private LocalTime lastTime = LocalTime.now();

    /**
     * Handles events, specifically {@link EventType#GUIDE_CLOSING} to advance the guide flow.
     *
     * @param event The type of the event.
     * @param properties The payload containing event-specific data.
     */
    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.GUIDE_CLOSING) {
            GuideInfoClosingEvent
                    event1 = (GuideInfoClosingEvent) properties;
            if (event1.getId().equals(guides.getFirst().getId())) {
                 if (lastTime.plusSeconds(DEB).isAfter(LocalTime.now())) return;
                lastTime = LocalTime.now();
                GuideInfo g = guides.removeFirst();
                g.breakdown();
                start();
            }
        }
    }
}
