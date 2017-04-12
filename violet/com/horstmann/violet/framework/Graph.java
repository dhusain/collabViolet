/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import com.horstmann.violet.ClassNode;
import com.horstmann.violet.InterfaceNode;
import com.horstmann.violet.NoteNode;
import com.horstmann.violet.PackageNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.prefs.PreferenceChangeEvent;


/**
   A graph consisting of selectable nodes and edges.
*/
public abstract class Graph implements Serializable
{
   /**
      Constructs a graph with no nodes or edges.
   */
   public Graph()
   {
      commandListeners = new ArrayList();
      nodes = new ArrayList();
      edges = new ArrayList();
      nodesToBeRemoved = new ArrayList();
      edgesToBeRemoved = new ArrayList();
      needsLayout = true;
   }

   /**
    * adds a commandlistener
    * @param commandListener litener to add
    */
   public void addCommandListener(CommandListener commandListener){
      commandListeners.add(commandListener);
   }
   /**
    Notifies all listeners of a state change.
    @param event the event to propagate
    */
   private void sendCommand(CommandEvent event)
   {
      for (int i = 0; i < commandListeners.size(); i++)
      {
         CommandListener listener = (CommandListener)commandListeners.get(i);
         //ChangeListener listener = (ChangeListener)changeListeners.get(i);
         listener.graphMod(event);
      }
   }
   /**
      Adds an edge to the graph that joins the nodes containing
      the given points. If the points aren't both inside nodes,
      then no edge is added.
      @param e the edge to add
      @param p1 a point in the starting node
      @param p2 a point in the ending node
   */
   public boolean connect(Edge e, Point2D p1, Point2D p2)
   {
      Node n1 = findNode(p1);
      Node n2 = findNode(p2);
      if (n1 != null)
      {
         e.connect(n1, n2);
         if (n1.addEdge(e, p1, p2) && e.getEnd() != null)
         {
            edges.add(e);
            if (!nodes.contains(e.getEnd()))
               nodes.add(e.getEnd());
            needsLayout = true;
            return true;
         }
      }
      return false;
   }
   /**
    Adds an edge to the graph that joins the nodes containing
    the given points. If the points aren't both inside nodes,
    then no edge is added.
    @param e the edge to add
    @param id1 the id of the starting node
    @param id2 the id of the ending node
    */
   public boolean connectById(Edge e, String id1, String id2)
   {
      Node n1 = findNodeById(id1);
      Node n2 = findNodeById(id2);
      if (n1 != null)
      {
         e.connect(n1, n2);
         if (n1.addEdge(e, new Point2D.Double(n1.getBounds().getX(),n1.getBounds().getY()), new Point2D.Double(n2.getBounds().getX(),n2.getBounds().getY())) && e.getEnd() != null)
         {
            edges.add(e);
            if (!nodes.contains(e.getEnd()))
               nodes.add(e.getEnd());
            needsLayout = true;
            return true;
         }
      }
      return false;
     // return this.connect(e, new Point2D.Double(n1.getBounds().getX(),n1.getBounds().getY()), new Point2D.Double(n2.getBounds().getX(),n2.getBounds().getY()));
   }

   /**
      Adds a node to the graph so that the top left corner of
      the bounding rectangle is at the given point.
      @param n the node to add
      @param p the desired location
   */
   public boolean add(Node n, Point2D p)
   {
      Rectangle2D bounds = n.getBounds();
      n.translate(p.getX() - bounds.getX(), 
         p.getY() - bounds.getY()); 

      boolean accepted = false;
      boolean insideANode = false;
      for (int i = nodes.size() - 1; i >= 0 && !accepted; i--)
      {
         Node parent = (Node)nodes.get(i);
         if (parent.contains(p)) 
         {
            insideANode = true;
            if (parent.addNode(n, p)) accepted = true;
         }
      }
      if (insideANode && !accepted) 
         return false;

      nodes.add(n);
      needsLayout = true;
      return true;
   }

   /**
    * increments counter by 1
    * @return incrememnted counter
    */
   public int incCounter() {
      this.counter++;
      return this.counter;
   }

   /**
    * gets current node ID
    * @return node ID
    */
   public int getCurrentId() {
      return counter;
   }

   /**
    Modifies node based on event
    @param modifyEvent contains info of node to be changed
    */
   public void modifyNode(PropertyChangeEvent modifyEvent)
   {
      for (int i = nodes.size() - 1; i >= 0; i--)
      {

         Node n = (Node)nodes.get(i);
        // if(n instanceof ClassNode) {
         if(n instanceof ClassNode ||n instanceof InterfaceNode ||n instanceof PackageNode ||n instanceof NoteNode) {
            if (n.getId().toString().equals(modifyEvent.getOldValue().toString())) {
               try {
                  BeanInfo info = Introspector.getBeanInfo(n.getClass());
                  for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                     if (descriptor.getName().equals(modifyEvent.getPropertyName())) {
                        if(modifyEvent.getNewValue() instanceof String){
                           Method setter = descriptor.getWriteMethod();
                           setter.invoke(n, new Object[]{modifyEvent.getNewValue()});
                        }else {
                           MultiLineString change = (MultiLineString) modifyEvent.getNewValue();
                           Method setter = descriptor.getWriteMethod();
                           setter.invoke(n, new Object[]{change.clone()});
                        }
                     }
                  }
               }catch (Exception e){
                  e.printStackTrace();

               }
            }

         }
      }
   }

   /**
    * gets node with given id
    * @param id node's id
    * @return node with given id
    */
   public Node findNodeById(String id){
      for (Object node: nodes){
         Node n = (Node) node;
         if (n.getId().getText().equals(id)) return n;
      }
      return null;
   }

   public Edge findEdgeById(String id){
      for (Object edge: edges){
         Edge e = (Edge) edge;
         if (e.getId().getText().equals(id)){
            return e ;
         }
      }
      return null;
   }
   /**
      Finds a node containing the given point.
      @param p a point
      @return a node containing p or null if no nodes contain p
   */
   public Node findNode(Point2D p)
   {
      for (int i = nodes.size() - 1; i >= 0; i--)
      {
         Node n = (Node)nodes.get(i);
         if (n.contains(p)) return n;
      }
      return null;
   }

   /**
      Finds an edge containing the given point.
      @param p a point
      @return an edge containing p or null if no edges contain p
   */
   public Edge findEdge(Point2D p)
   {
      for (int i = edges.size() - 1; i >= 0; i--)
      {
         Edge e = (Edge)edges.get(i);
         if (e.contains(p)) return e;
      }
      return null;
   }
   
   /**
      Draws the graph
      @param g2 the graphics context
   */
   public void draw(Graphics2D g2, Grid g)
   {
      layout(g2, g);

      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = (Node)nodes.get(i);
         n.draw(g2);
      }

      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = (Edge)edges.get(i);
         e.draw(g2);
      }
   }
   
   /**
      Removes a node and all edges that start or end with that node
      @param n the node to remove
   */
   public void removeNode(Node n)
   {
      if (nodesToBeRemoved.contains(n)) return;
      nodesToBeRemoved.add(n);
      // notify nodes of removals
      for (int i = 0; i < nodes.size(); i++)
      {
         Node n2 = (Node)nodes.get(i);
         n2.removeNode(this, n);
      }
      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = (Edge)edges.get(i);
         if (e.getStart() == n || e.getEnd() == n)
            removeEdge(e);
      }

      needsLayout = true;
   }

   /**
      Removes an edge from the graph.
      @param e the edge to remove
   */
   public void removeEdge(Edge e)
   {
      if (edgesToBeRemoved.contains(e)) return;
      edgesToBeRemoved.add(e);
      for (int i = nodes.size() - 1; i >= 0; i--)
      {
         Node n = (Node)nodes.get(i);
         n.removeEdge(this, e);
      }
      needsLayout = true;
   }

   /**
      Causes the layout of the graph to be recomputed.
   */
   public void layout()
   {
      needsLayout = true;
   }

   /**
      Computes the layout of the graph.
      If you override this method, you must first call 
      <code>super.layout</code>.
      @param g2 the graphics context
      @param g the grid to snap to
   */
   protected void layout(Graphics2D g2, Grid g)
   {
      if (!needsLayout) return;
      nodes.removeAll(nodesToBeRemoved);
      edges.removeAll(edgesToBeRemoved);
      nodesToBeRemoved.clear();
      edgesToBeRemoved.clear();

      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = (Node) nodes.get(i);
         n.layout(this, g2, g);
      }
      needsLayout = false;
   }

   /**
      Gets the smallest rectangle enclosing the graph
      @param g2 the graphics context
      @return the bounding rectangle
   */
   public Rectangle2D getBounds(Graphics2D g2)
   {
      Rectangle2D r = minBounds;
      for (int i = 0; i < nodes.size(); i++)
      {
         Node n = (Node)nodes.get(i);
         Rectangle2D b = n.getBounds();
         if (r == null) r = b;
         else r.add(b);
      }
      for (int i = 0; i < edges.size(); i++)
      {
         Edge e = (Edge)edges.get(i);
         r.add(e.getBounds(g2));
      }
      return r == null ? new Rectangle2D.Double() : new Rectangle2D.Double(r.getX(), r.getY(), 
            r.getWidth() + AbstractNode.SHADOW_GAP, r.getHeight() + AbstractNode.SHADOW_GAP);
   }
   
   public Rectangle2D getMinBounds() { return minBounds; }
   public void setMinBounds(Rectangle2D newValue) { minBounds = newValue; }

   /**
      Gets the node types of a particular graph type.
      @return an array of node prototypes
   */   
   public abstract Node[] getNodePrototypes();

   /**
      Gets the edge types of a particular graph type.
      @return an array of edge prototypes
   */   
   public abstract Edge[] getEdgePrototypes();
 
   /**
      Adds a persistence delegate to a given encoder that
      encodes the child nodes of this node.
      @param encoder the encoder to which to add the delegate
   */
   public static void setPersistenceDelegate(Encoder encoder)
   {
      encoder.setPersistenceDelegate(Graph.class, new
         DefaultPersistenceDelegate()
         {
            protected void initialize(Class type, 
               Object oldInstance, Object newInstance, 
               Encoder out) 
            {
               super.initialize(type, oldInstance, 
                  newInstance, out);
               Graph g = (Graph)oldInstance;
         
               for (int i = 0; i < g.nodes.size(); i++)
               {
                  Node n = (Node)g.nodes.get(i);
                  Rectangle2D bounds = n.getBounds();
                  Point2D p = new Point2D.Double(bounds.getX(),
                     bounds.getY());
                  out.writeStatement(
                     new Statement(oldInstance,
                        "addNode", new Object[]{ n, p }) );
               }
               for (int i = 0; i < g.edges.size(); i++)
               {
                  Edge e = (Edge)g.edges.get(i);
                  out.writeStatement(
                     new Statement(oldInstance,
                        "connect", 
                        new Object[]{ e, e.getStart(), e.getEnd() }) );            
               }
            }
         });
   }

   /**
      Gets the nodes of this graph.
      @return an unmodifiable collection of the nodes
   */
   public Collection getNodes() { return nodes; }

   /**
      Gets the edges of this graph.
      @return an unmodifiable collection of the edges
   */
   public Collection getEdges() { return edges; }

   /**
      Adds a node to this graph. This method should
      only be called by a decoder when reading a data file.
      @param n the node to add
      @param p the desired location
   */
   public void addNode(Node n, Point2D p)
   {
      Rectangle2D bounds = n.getBounds();
      n.translate(p.getX() - bounds.getX(), 
         p.getY() - bounds.getY()); 
      nodes.add(n); 
   }

   /**
      Adds an edge to this graph. This method should
      only be called by a decoder when reading a data file.
      @param e the edge to add
      @param start the start node of the edge
      @param end the end node of the edge
   */
   public void connect(Edge e, Node start, Node end)
   {
      e.connect(start, end);
      edges.add(e);
   }
   private int counter=0;
//   static
//   {
//      editors = new HashMap();
//      editors.put(String.class, PropertySheet.StringEditor.class);
//      editors.put(java.awt.Color.class, ColorEditor.class);
//   }

   private ArrayList commandListeners;

   private static Map editors;


   private ArrayList nodes;
   private ArrayList edges;
   private transient ArrayList nodesToBeRemoved;
   private transient ArrayList edgesToBeRemoved;
   private transient boolean needsLayout;
   private transient Rectangle2D minBounds;
}





