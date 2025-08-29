package com.j3d.engine.events;

/**
 * Events is an enum that describes the possible Events that a listener can listen for.
 */
public enum EventType {
    /**
     * The position of the node coordinates have changed and parent
     * geometry needs to update itself.
     * <p></p>
     * e.g. Where a line (parent) has 2 points (2 nodes), If the points are moved, the line has to update to account for that.
     */
    NODE_UPDATED,
    /**
     * The node was deleted entirely, the parent geometry needs to update itself accordingly.
     * By either deleting itself, or turning itself into a bunch of nodes.
     */
    NODE_DELETED,
    /**
     * The parent of the given node was updated, the node should update itself accordingly
     * <p></p>
     * e.g. Where a line (parent) has in it's list own list of points, a point (node) in the midpoint of the given line. When the line's distance changes, the node needs to accomodate that.
     */
    PARENT_UPDATED,
    /**
     * The parent was deleted entirely, the node geometry needs to update itself accordingly.
     * By deleting itself or doing nothing.
     */
    PARENT_DELETED,
}
