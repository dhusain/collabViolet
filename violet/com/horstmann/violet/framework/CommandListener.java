package com.horstmann.violet.framework;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.EventObject;

/**
 * Custom listener for commands to send to server
 */
public interface CommandListener {
    public void graphMod(EventObject event);
}
