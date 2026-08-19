package com.j3d.gen.guide;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.*;
import com.j3d.engine.react.events.payloads.GuideInfoClosingPayload;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class GuideInfo extends EventEmitter implements EventListener {

    private final ArrayList<Component> components = new ArrayList<>();
    private EventEmitterInterface listeningTo;
    private final UUID id = UUID.randomUUID();

    /**
     * Handles incoming events.
     * @implSpec Implementors, are expected to override this method to handle the specific event they have
     * passed into {@link #GuideInfo(EventEmitterInterface)}) specifically to call {@link #close()} when
     * they are satisfied.
     *
     * @param <K> The type of the emitter associated with the event.
     * @param event The type of event that occurred.
     * @param properties The payload containing data related to the event.
     */
    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {

    }

    /**
     * Constructs a new GuideInfo instance, setting up the event emitter it should listen to.
     * @param listeningTo The EventEmitterInterface instance to listen for events from.
     */
    public GuideInfo(EventEmitterInterface listeningTo) {
        this.listeningTo = listeningTo;
    }

    /**
     * Attaches listeners for this guide. It attaches itself to the provided GuideFlow
     * and also attaches itself as a listener to the `listeningTo` EventEmitterInterface, if set.
     * @param f The GuideFlow to which this GuideInfo should attach its listener.
     */
    void attachListeners(GuideFlow f) {
        this.attachListener(f);
        if (listeningTo != null)
            listeningTo.attachListener(this);
    }

    /**
     * Builds the UI components for this guide step using the provided adapter.
     * Subclasses should override this method to define their specific guide content.
     * @param adapter The GuidePanelAdapter used to add and manage UI components.
     */
    public void build(GuidePanelAdapter adapter) {
        // build help contents here.
    }

    /**
     * Initiates the closing process for this guide step.
     * It broadcasts a {@link EventType#GUIDE_CLOSING} event with a {@link GuideInfoClosingPayload}.
     */
    public void  close() {
        // fire event
        broadcast(EventType.GUIDE_CLOSING, new GuideInfoClosingPayload(this));
    }
    /**
     * Cleans up the guide step by removing all associated UI components
     * from the adapter and detaching itself as a listener from the `listeningTo`
     * EventEmitterInterface, if it was attached.
     */
    public void breakdown() {
        // remove components
        components.forEach(StaticRefs.getMainFrame().getGuideManager().getAdapter()::remove);
        // remove listener
        if (listeningTo != null)
            listeningTo.detachListener(this);
    }

    /**
     * Returns the list of UI components associated with this guide step.
     * @return An ArrayList of Component objects managed by this guide step.
     */
    public ArrayList<Component> getComponents() {
        return components;
    }

    /**
     * Returns the unique identifier for this guide step.
     * @return The UUID of this GuideInfo instance.
     */
    public UUID getId() {
        return id;
    }

    // small ui
    /**
     * Displays a guide counter (e.g., "1 of 5") on the guide panel.
     * The counter indicates the current guide step's position within the overall flow.
     * @param adapter The GuidePanelAdapter to which the counter label will be added.
     */
    public void guideCounter(GuidePanelAdapter adapter) {
        int index =(StaticRefs.getMainFrame().getGuideManager().getFlow().indexOf(this)+1);
        int total = StaticRefs.getMainFrame().getGuideManager().getFlow().totalSize();

        JButton back = new JButton("(go back)");
        back.addActionListener((e) -> {
            StaticRefs.getMainFrame().getGuideManager().getFlow().back();
        });
        JLabel label = new JLabel(
                new JLabelRichText(index + " of " + total)
                        .wrapUsing(adapter.readableTextStyle)
                        .italic()
                        .wrapHTML()
        );

        addCompAt(
                adapter,
                back,
                Anchor.SOUTH | Anchor.EAST,
                150, 20
        );
        addCompAt(
                adapter,
                label,
                Anchor.SOUTH | Anchor.EAST,
                60, 20
        );
    }

    /**
     * Creates and returns a JLabel containing an image loaded from the given URL,
     * scaled by the specified factor.
     * @param url The URL of the image to load.
     * @param scale The scaling factor to apply to the image (e.g., 0.5 for half size).
     * @return A JLabel with the scaled image icon.
     * @throws RuntimeException if there is a {@link URISyntaxException} when converting the URL to a URI.
     */
    public JLabel image(URL url, double scale) {
        JLabel label = new JLabel("");
        File imagePath;
        try {
            imagePath = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // set the image
        ImageIcon icon = new ImageIcon(imagePath.getAbsolutePath());

        Image scaled = icon.getImage().getScaledInstance(
                (int) (icon.getIconWidth() * scale),
                (int) (icon.getIconHeight() * scale),
                Image.SCALE_SMOOTH
        );

        label.setIcon(new ImageIcon(scaled));
        return label;
    }

    /**
     * Adds a generic text label to the guide panel at a predefined position (SOUTH | WEST).
     * The text is styled using {@link JLabelRichText} with a specific font color and size.
     * @param adapter The GuidePanelAdapter to which the text label will be added.
     * @param text The string content to display in the label.
     */
    public void genericText(GuidePanelAdapter adapter, String text) {
        addCompAt(
                adapter,
                new JLabel(
                        new JLabelRichText(text)
                                .wrapDiv(200).font(
                                        J3DTheme.TEXT_PRIMARY.color(),
                                        "5"
                                )
                                .wrapHTML()
                ),
                Anchor.SOUTH | Anchor.WEST,
                Anchor.offsetRight(50), Anchor.offsetUp(35)
        );
    }

    // methods to help build the UI

    /**
     * Adds a component to the center of the guide panel with specified X and Y offsets.
     * The component is also added to this guide's internal list of components.
     * @param adapter The GuidePanelAdapter to which the component will be added.
     * @param l The Component to add.
     * @param offX The horizontal offset from the center.
     * @param offY The vertical offset from the center.
     */
    public void addCompAtCentre(GuidePanelAdapter adapter, Component l, int offX, int offY) {
        components.add(l);
        adapter.addCentreOffset(l, offX, offY);
    }
    /**
     * Adds a component to the center of the guide panel with no offset.
     * The component is also added to this guide's internal list of components.
     * @param adapter The GuidePanelAdapter to which the component will be added.
     * @param l The Component to add.
     */
    public void addCompAtCentre(GuidePanelAdapter adapter, Component l) {
        addCompAtCentre(adapter, l, 0, 0);
    }
    /**
     * Adds a component to the guide panel at a position determined by an anchor and offsets.
     * The component is also added to this guide's internal list of components.
     * @param adapter The GuidePanelAdapter to which the component will be added.
     * @param l The Component to add.
     * @param anchor A bitmask representing the desired anchor point (e.g., {@link Anchor#NORTH}, {@link Anchor#SOUTH}, {@link Anchor#EAST}, {@link Anchor#WEST}, {@link Anchor#CENTRE}).
     * @param offX The horizontal offset from the calculated anchor position.
     * @param offY The vertical offset from the calculated anchor position.
     */
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

    /**
     * Adds a component to the guide panel at a position determined by an anchor with no offsets.
     * The component is also added to this guide's internal list of components.
     * @param adapter The GuidePanelAdapter to which the component will be added.
     * @param l The Component to add.
     * @param anchor A bitmask representing the desired anchor point (e.g., {@link Anchor#NORTH}, {@link Anchor#SOUTH}, {@link Anchor#EAST}, {@link Anchor#WEST}, {@link Anchor#CENTRE}).
     */
    public void addCompAt(GuidePanelAdapter adapter, Component l, int anchor) {
        addCompAt(adapter, l, anchor, 0 ,0);
    }

}
