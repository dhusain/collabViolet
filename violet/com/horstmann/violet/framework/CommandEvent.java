package com.horstmann.violet.framework;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.EventObject;

/**
 * Event that contains new command info
 */
public class CommandEvent extends EventObject {
    private Point2D point2D1;
    private Point2D point2D2;
    private Commands command;
    private Node n1;
    private Node n2;
    private Edge e;


    /**
     * creates a command event
     * @param point2D1 location
     * @param command type of command
     * @param n1 first node
     * @param n2 second node
     */
    public CommandEvent(Object source, Point2D point2D1, Point2D point2D2, Commands command, Node n1, Node n2, Edge e) {
        super(source);
        this.point2D1 = point2D1;
        this.point2D2 = point2D2;
        this.command = command;
        this.n1 = n1;
        this.n2 = n2;
        this.e = e;
    }

    /**
     * gets the edge
     * @return the edge
     */
    public Edge getE() {
        return e;
    }

    /**
     * sets the edge
     * @param e new edge
     */
    public void setE(Edge e) {
        this.e = e;
    }

    /**
     * gets point
     * @return point
     */
    public Point2D getPoint2D1() {
        return point2D1;
    }

    /**
     * gets second point
     * @return second point
     */
    public Point2D getPoint2D2() {
        return point2D2;
    }

    /**
     * sets second point
     * @param point2D2 new second point
     */
    public void setPoint2D2(Point2D point2D2) {
        this.point2D2 = point2D2;
    }

    /**
     * sets point
     * @param point2D the point to be set
     */
    public void setPoint2D1(Point2D point2D) {
        this.point2D1 = point2D;
    }

    /**
     * gets command
     * @return the command
     */
    public Commands getCommand() {
        return command;
    }

    /**
     * sets command
     * @param command new command
     */
    public void setCommand(Commands command) {
        this.command = command;
    }

    /**
     * gets the first node
     * @return the first node
     */
    public Node getN1() {
        return n1;
    }

    /**
     * sets the first node
     * @param n1 new node1
     */
    public void setN1(Node n1) {
        this.n1 = n1;
    }

    /**
     * gets the second node
     * @return
     */
    public Node getN2() {
        return n2;
    }

    /**
     * sets second node
     * @param n2 new second node
     */
    public void setN2(Node n2) {
        this.n2 = n2;
    }
}
