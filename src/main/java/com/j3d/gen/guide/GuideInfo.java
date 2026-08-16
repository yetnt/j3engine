package com.j3d.gen.guide;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.*;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class GuideInfo extends EventEmitter implements EventListener {

    private final ArrayList<Component> components = new ArrayList<>();
    private EventEmitterInterface listeningTo;
    private final UUID id = UUID.randomUUID();

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {

    }

    public GuideInfo(GuideFlow f) {
        this.attachListener(f);
    }

    public GuideInfo(GuideFlow f, EventEmitterInterface listeningTo) {
        this(f);
        this.listeningTo = listeningTo;
        listeningTo.attachListener(this);
    }

    public void build(GuidePanelAdapter adapter) {
        // build help contents here.
    }

    public void close() {
        // remove components
        components.forEach(StaticRefs.getMainFrame().getGuideManager().getAdapter()::remove);
        // remove listener
        if (listeningTo != null)
            listeningTo.detachListener(this);
        // fire event
        broadcast(EventType.GUIDE_CLOSING, new GuideInfoClosingEvent(this));
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public UUID getId() {
        return id;
    }

    // small ui

    public void guideCounter(GuidePanelAdapter adapter) {
        String index = StaticRefs.getMainFrame().getGuideManager().getFlow().indexOf(this) + "";
        String total = "/" + StaticRefs.getMainFrame().getGuideManager().getFlow().totalSize();

        JLabel label = new JLabel(
                new JLabelRichText(index + total)
                        .wrapUsing(adapter.readableTextStyle)
                        .italic()
                        .wrapHTML()
        );

        addCompAt(
                adapter,
                label,
                Anchor.SOUTH | Anchor.EAST,
                20, 20
        );
    }

    // methods to help build the UI

    public void addCompAtCentre(GuidePanelAdapter adapter, Component l, int offX, int offY) {
        components.add(l);
        adapter.addCentreOffset(l, offX, offY);
    }
    public void addCompAtCentre(GuidePanelAdapter adapter, Component l) {
        addCompAtCentre(adapter, l, 0, 0);
    }

    public void addCompAt(GuidePanelAdapter adapter, Component l, int anchor, int offX, int offY) {
        int x = 0;
        int y = 0;
        int pX = adapter.getWidth();
        int pY = adapter.getHeight();
        int lX = l.getPreferredSize().width/2;
        int lY = l.getPreferredSize().height/2;

        if (Anchor.has(anchor, Anchor.CENTRE)) {
            x = (pX /2) - lX;
            y = (pY /2) - lY;
        }
        if (Anchor.hasVertical(anchor)) {
            if (Anchor.has(anchor, Anchor.NORTH)) {
                y = 0;
            } else if (Anchor.has(anchor, Anchor.SOUTH)) {
                y = (y == 0 ? pY : y + y) - lY;
            }
        }
        if (Anchor.hasHorizontal(anchor)) {
            if (Anchor.has(anchor, Anchor.WEST)) {
                x = 20;
            } else if (Anchor.has(anchor, Anchor.EAST)) {
                x = (x == 0 ? pX : x + x) - lX;
            }
        }

        components.add(l);
        adapter.addComponentAt(l, x - offX , y - offY);
    }

    public void addCompAt(GuidePanelAdapter adapter, Component l, int anchor) {
        addCompAt(adapter, l, anchor, 0 ,0);
    }
}
