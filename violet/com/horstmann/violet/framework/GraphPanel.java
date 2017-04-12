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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.*;
import java.io.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Base64;

import java.util.*;
import java.util.Timer;

/**
 * A panel to draw a graph
 */
public class GraphPanel extends JPanel
{
   /**
    * Constructs a graph.
    * @param aToolBar the tool bar with the node and edge tools
    */
   public GraphPanel(ToolBar aToolBar)
   {
      serverLink = new ServerSync("1","1", serverAddress);
      JTextField userIdText = new JTextField("UserID");
      JTextField sessionIdText = new JTextField("SessionID");
      JButton address = new JButton("Enter address");
      address.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent mouseEvent) {
            serverAddress = JOptionPane.showInputDialog("Enter address");
            if (serverAddress != null && serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length()-1)=='/') {
               serverAddress = serverAddress.substring(0, serverAddress.length()-1);
            }
            serverLink.setServerAddress(serverAddress);
         }
      });
      this.add(address);
      JButton serverHandle = new JButton("ConnectToServer?");
      serverHandle.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent mouseEvent) {
            //Custom button text
            Object[] options = {"New Session",
                    "Join Existing"};
            int n = JOptionPane.showOptionDialog(frame,
                    "New Session or Join Existing?",
                    "Connection Options",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if(n==0){
               try {
                  if(serverAddress == "") {
                     serverAddress = JOptionPane.showInputDialog("Enter address");
                     if (serverAddress != null && serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length()-1)=='/') {
                        serverAddress = serverAddress.substring(0, serverAddress.length()-1);
                     }
                     serverLink.setServerAddress(serverAddress);
                  }
                  String urlString = serverAddress+"/new";
                  URL url = new URL(urlString);
                  url.openConnection();
                  Scanner in = new Scanner(url.openStream());
                  String[] inputs = in.nextLine().split(" ");
                  sessionId = inputs[inputs.length - 1];
                  URL user = new URL(serverAddress+"/connect/"+sessionId);
                  in = new Scanner(user.openStream());
                  inputs = in.nextLine().split(" ");
                  userId = inputs[inputs.length - 1];
                  //new URL("http://localhost:9000/add?id=" + sessionId + "&name=testing").openStream();
                  serverLink = new ServerSync(sessionId, userId, serverAddress);
                  sessionIdText.setText("SessionID: "+sessionId);
                  userIdText.setText("UserID: "+userId);
               }catch (Exception e){
                  e.printStackTrace();
               }
            }else if(n==1){
               if(serverAddress == "") {
                  serverAddress = JOptionPane.showInputDialog("Enter address");
                  if (serverAddress != null && serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length()-1)=='/') {
                     serverAddress = serverAddress.substring(0, serverAddress.length()-1);
                  }
                  serverLink.setServerAddress(serverAddress);
               }
               sessionId = JOptionPane.showInputDialog("Enter session id");
               serverLink.setSessionId(sessionId);
               try {
                  URL url = new URL(serverAddress+"/connect/" + sessionId);
                  Scanner in = new Scanner(url.openStream());
                  String[] inputs = in.nextLine().split(" ");
                  userId = inputs[inputs.length - 1];
                  serverLink.setUserId(userId);
                  sessionIdText.setText("SessionID: "+sessionId);
                  userIdText.setText("UserID: "+userId);
               }catch(Exception e){
                  e.printStackTrace();
               }
            }

         }
      });
      this.add(serverHandle);
      this.add(sessionIdText);
      this.add(userIdText);


      timer = new Timer();
      TimerTask myTask = new TimerTask() {
         public void run() {
            ArrayList<EventObject> events = serverLink.receiveChanges();
            int tCounter = 0;
            for(int i = commandCount;i<events.size();i++) {
               tCounter++;
               if (events.get(i) instanceof PropertyChangeEvent) {
                  graph.modifyNode((PropertyChangeEvent) events.get(i));
                  graph.layout();
                  setModified(true);
                  revalidate();
                  repaint();
                  //firePropertyChanged((PropertyChangeEvent) events.get(i));
               } else if (events.get(i) instanceof CommandEvent) {
                  CommandEvent commandEvent = (CommandEvent) events.get(i);
                  if (commandEvent.getCommand() == Commands.ADD) {
                     //temp.setId(commandEvent.getN1().getId().getText());
                     boolean add = graph.add((Node) commandEvent.getN1().clone(), commandEvent.getPoint2D1());
                     if (add) {
                        setModified(true);
                        setSelectedItem(commandEvent.getN1());
                        repaint();
                     }
                  } else if (commandEvent.getCommand() == Commands.REMOVENODE) {
                     graph.removeNode(graph.findNodeById(commandEvent.getN1().getId().getText()));
                     repaint();
                  }else if(commandEvent.getCommand() == Commands.REMOVEEDGE){
                     //  graph.findRemoveEdgeById(commandEvent.getE().getId().getText());
                     repaint();
                     graph.removeEdge(graph.findEdgeById(commandEvent.getE().getId().getText()));
                  } else if (commandEvent.getCommand() == Commands.MOVE) {
                     Node node = graph.findNodeById(commandEvent.getN1().getId().getText());
                     double dx, dy;
                     dx = commandEvent.getN1().getBounds().getX() - node.getBounds().getX();
                     dy = commandEvent.getN1().getBounds().getY() - node.getBounds().getY();
                     //Node node = graph.findNode(new Point2D.Double(commandEvent.getN1().getBounds().getX(), commandEvent.getN1().getBounds().getY()));
                     node.translate(dx, dy);
                     repaint();
                  } else if (commandEvent.getCommand() == Commands.CONNECT) {
                     Node n1 = graph.findNodeById(commandEvent.getN1().getId().getText());
                     Node n2 = graph.findNodeById(commandEvent.getN2().getId().getText());
                     Point2D.Double p1 = new Point2D.Double(n1.getBounds().getX(), n1.getBounds().getY());
                     Point2D.Double p2 = new Point2D.Double(n2.getBounds().getX(), n2.getBounds().getY());

                     graph.connectById(commandEvent.getE(), commandEvent.getN1().getId().getText(), commandEvent.getN2().getId().getText());
                     repaint();
                  }
               }
            }
            commandCount += tCounter;
         }
      };
      timer.schedule(myTask, 2000, 2000);
            JButton receive = new JButton("Force Update");
      receive.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent mouseEvent) {
            myTask.run();
         }
      });
      this.add(receive);
      grid = new Grid();
      gridSize = GRID;
      grid.setGrid((int) gridSize, (int) gridSize);
      zoom = 1;
      toolBar = aToolBar;
      setBackground(Color.WHITE);

      selectedItems = new HashSet();

      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent event)
         {
            requestFocus();
            final Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
                  event.getY() / zoom);
            boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
            Node n = graph.findNode(mousePoint);
            Edge e = graph.findEdge(mousePoint);
            Object tool = toolBar.getSelectedTool();
            if (event.getClickCount() > 1
                  || (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
            // double/right-click
            {
               if (e != null)
               {
                  setSelectedItem(e);
                  editSelected();
               }
               else if (n != null)
               {
                  setSelectedItem(n);
                  editSelected();
               }
               else
               {
                  toolBar.showPopup(GraphPanel.this, mousePoint,
                        new ActionListener()
                        {
                           public void actionPerformed(ActionEvent event) 
                           {
                             
                              Object tool = toolBar.getSelectedTool();
                              if (tool instanceof Node)
                              {
                                 Node prototype = (Node) tool;
                                 Node newNode = (Node) prototype.clone();
//                                 if(newNode instanceof ClassNode) {
                                 if(newNode instanceof ClassNode) {
                                    ((ClassNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                                    nodecounter++;
                                 } else  if(newNode instanceof InterfaceNode) {
                                    ((InterfaceNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                                    nodecounter++;
                                 }else  if(newNode instanceof PackageNode) {
                                    ((PackageNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                                    nodecounter++;
                                 }else  if(newNode instanceof NoteNode) {
                                    ((NoteNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                                    nodecounter++;
                                 }
                                 boolean added = graph.add(newNode,  mousePoint);
                                 if (added)
                                 {
                                    setModified(true);
                                    setSelectedItem(newNode);
                                    notifyServer(new CommandEvent(this, mousePoint, null,Commands.ADD, newNode, null,null));
                                 }
                              }
                           }
                        });
               }
            }
            else if (tool == null) // select
            {
               if (e != null)
               {
                  setSelectedItem(e);
               }
               else if (n != null)
               {
                  if (isCtrl)
                     addSelectedItem(n);
                  else if (!selectedItems.contains(n))
                     setSelectedItem(n);
                  dragMode = DRAG_MOVE;
               }
               else
               {
                  if (!isCtrl)
                     clearSelection();
                  dragMode = DRAG_LASSO;
               }
            }
            else if (tool instanceof Node)
            {
               Node prototype = (Node) tool;
               Node newNode = (Node) prototype.clone();
               if(newNode instanceof ClassNode) {
                  ((ClassNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                  nodecounter++;
               }else  if(newNode instanceof InterfaceNode) {
                  ((InterfaceNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                  nodecounter++;
               }else  if(newNode instanceof PackageNode) {
                  ((PackageNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                  nodecounter++;
               }else  if(newNode instanceof NoteNode) {
                  ((NoteNode) newNode).setId(userId + "_" + Integer.toString(nodecounter));
                  nodecounter++;
               }
               boolean added = graph.add(newNode, mousePoint);
               if (added)
               {
                  setModified(true);
                  setSelectedItem(newNode);
                  notifyServer(new CommandEvent(this, mousePoint, null,Commands.ADD, newNode, null,null));
                  dragMode = DRAG_MOVE;
               }
               else if (n != null)
               {
                  if (isCtrl)
                     addSelectedItem(n);
                  else if (!selectedItems.contains(n))
                     setSelectedItem(n);
                  dragMode = DRAG_MOVE;
               }
            }
            else if (tool instanceof Edge)
            {
               if (n != null) dragMode = DRAG_RUBBERBAND;
            }

            lastMousePoint = mousePoint;
            mouseDownPoint = mousePoint;
            repaint();
         }

         public void mouseReleased(MouseEvent event)
         {

            Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
                  event.getY() / zoom);
            Object tool = toolBar.getSelectedTool();
            if (dragMode == DRAG_RUBBERBAND)
            {
               Edge prototype = (Edge) tool;
               Edge newEdge = (Edge) prototype.clone();
               newEdge.setId(userId + "_" + Integer.toString(edgecounter));
               edgecounter++;
               if (mousePoint.distance(mouseDownPoint) > CONNECT_THRESHOLD
                     && graph.connect(newEdge, mouseDownPoint, mousePoint))
               {
                  notifyServer(new CommandEvent(this, mouseDownPoint, mousePoint, Commands.CONNECT, graph.findNode(mouseDownPoint), graph.findNode(mousePoint),newEdge));
                  setModified(true);
                  setSelectedItem(newEdge);
               }
            }
            else if (dragMode == DRAG_MOVE)
            {
               graph.layout();
               setModified(true);
            }
            dragMode = DRAG_NONE;

            revalidate();
            repaint();
         }
      });

      addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent event)
         {
            Point2D mousePoint = new Point2D.Double(event.getX() / zoom, 
                  event.getY() / zoom);
            boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 

            if (dragMode == DRAG_MOVE && lastSelected instanceof Node)
            {               
               Node lastNode = (Node) lastSelected;
               Rectangle2D bounds = lastNode.getBounds();
               double dx = mousePoint.getX() - lastMousePoint.getX();
               double dy = mousePoint.getY() - lastMousePoint.getY();
                            
               // we don't want to drag nodes into negative coordinates
               // particularly with multiple selection, we might never be 
               // able to get them back.
               Iterator iter = selectedItems.iterator();
               while (iter.hasNext())
               {
                  Object selected = iter.next();                 
                  if (selected instanceof Node)
                  {
                     Node n = (Node) selected;
                     bounds.add(n.getBounds());
                  }
               }
               dx = Math.max(dx, -bounds.getX());
               dy = Math.max(dy, -bounds.getY());
               
               iter = selectedItems.iterator();
               while (iter.hasNext())
               {
                  Object selected = iter.next();                 
                  if (selected instanceof Node)
                  {
                     Node n = (Node) selected;
//                     notifyServer(new CommandEvent(this, new Point2D.Double(dx,dy), new Point2D.Double(n.getBounds().getX(), n.getBounds().getY()),
//                             Commands.MOVE, n, null));
                     n.translate(dx, dy);
                     notifyServer(new CommandEvent(this, new Point2D.Double(dx,dy), new Point2D.Double(n.getBounds().getX(), n.getBounds().getY()),
                             Commands.MOVE, n, null,null));
                  }
               }
               // we don't want continuous layout any more because of multiple selection
               // graph.layout();
            }            
            else if (dragMode == DRAG_LASSO)
            {
               double x1 = mouseDownPoint.getX();
               double y1 = mouseDownPoint.getY();
               double x2 = mousePoint.getX();
               double y2 = mousePoint.getY();
               Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
                     Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
               Iterator iter = graph.getNodes().iterator();
               while (iter.hasNext())
               {
                  Node n = (Node) iter.next();
                  Rectangle2D bounds = n.getBounds();
                  if (!isCtrl && !lasso.contains(n.getBounds())) 
                  {
                     removeSelectedItem(n);
                  }
                  else if (lasso.contains(n.getBounds())) 
                  {
                     addSelectedItem(n);
                  }
               }
            }
            
            lastMousePoint = mousePoint;
            repaint();
         }
      });

   }

   public boolean notifyServer(EventObject eventObject){
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream( baos );
         oos.writeObject(eventObject);
         oos.flush();
         oos.close();
         //serverLink.sendChange(Base64.getEncoder().encode(baos.toByteArray()));
         String change = URLEncoder.encode(new String(Base64.getEncoder().encode(baos.toByteArray()), "UTF-8"), "UTF-8");
         serverLink.notifyServer(change);
         commandCount++;
         return true;

      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }
   /**
    * Edits the properties of the selected graph element.
    */
   public void editSelected()
   {
      Object edited = lastSelected;
      if (lastSelected == null)
      {
         if (selectedItems.size() == 1)
            edited = selectedItems.iterator().next();
         else
            return;
      }

      PropertySheet sheet = new PropertySheet(edited, this, sessionId);
//      sheet.addChangeListener(new ChangeListener()
//      {
//         public void stateChanged(ChangeEvent event)
      sheet.addPropertyChangeListener(new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent event)
         {

            notifyServer(event);
            graph.layout();
            repaint();
         }
      });
      JOptionPane.showInternalMessageDialog(this, sheet, 
            ResourceBundle.getBundle("com.horstmann.violet.framework.EditorStrings").getString("dialog.properties"),            
            JOptionPane.QUESTION_MESSAGE);
      setModified(true);
   }
   /**
    * sets session id
    * @param sessionId makes this the sessionId
    */
   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

    /**
     * gets sessionId
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
    * Removes the selected nodes or edges.
    */
   public void removeSelected()
   {
      Iterator iter = selectedItems.iterator();
      while (iter.hasNext())
      {
         Object selected = iter.next();                 
         if (selected instanceof Node)
         {
            Node remove = (Node) selected;
            Point2D.Double point = new Point2D.Double(remove.getBounds().getX(),remove.getBounds().getY());
            notifyServer(new CommandEvent(this, point, null,Commands.REMOVENODE, remove, null,null));
            graph.removeNode((Node) selected);

         }
         else if (selected instanceof Edge)
         {
            notifyServer(new CommandEvent(this , null, null,Commands.REMOVEEDGE, null, null,(Edge) selected));
            graph.removeEdge((Edge) selected);
         }
      }
      if (selectedItems.size() > 0) setModified(true);
      repaint();
   }

   /**
    * Set the graph in the panel
    * @param aGraph the graph to be displayed and edited
    */
   public void setGraph(Graph aGraph)
   {
      graph = aGraph;
      setModified(false);
      revalidate();
      repaint();
   }

   /**
    * paints the graph panel
    * @param g graphics on which panel is to be drawn
    */
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.scale(zoom, zoom);
      Rectangle2D bounds = getBounds();
      Rectangle2D graphBounds = graph.getBounds(g2);
      if (!hideGrid) grid.draw(g2, new Rectangle2D.Double(0, 0, 
            Math.max(bounds.getMaxX() / zoom, graphBounds.getMaxX()), 
            Math.max(bounds.getMaxY() / zoom, graphBounds.getMaxY())));
      graph.draw(g2, grid);

      Iterator iter = selectedItems.iterator();
      Set toBeRemoved = new HashSet();
      while (iter.hasNext())
      {
         Object selected = iter.next();                 
      
         if (!graph.getNodes().contains(selected)
               && !graph.getEdges().contains(selected)) 
         {
            toBeRemoved.add(selected);
         }
         else if (selected instanceof Node)
         {
            Rectangle2D grabberBounds = ((Node) selected).getBounds();
            drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
            drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
            drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
            drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
         }
         else if (selected instanceof Edge)
         {
            Line2D line = ((Edge) selected).getConnectionPoints();
            drawGrabber(g2, line.getX1(), line.getY1());
            drawGrabber(g2, line.getX2(), line.getY2());
         }
      }

      iter = toBeRemoved.iterator();
      while (iter.hasNext())      
         removeSelectedItem(iter.next());                 
      
      if (dragMode == DRAG_RUBBERBAND)
      {
         Color oldColor = g2.getColor();
         g2.setColor(PURPLE);
         g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
         g2.setColor(oldColor);
      }      
      else if (dragMode == DRAG_LASSO)
      {
         Color oldColor = g2.getColor();
         g2.setColor(PURPLE);
         double x1 = mouseDownPoint.getX();
         double y1 = mouseDownPoint.getY();
         double x2 = lastMousePoint.getX();
         double y2 = lastMousePoint.getY();
         Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
               Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
         g2.draw(lasso);
         g2.setColor(oldColor);
      }      
   }

   /**
    * Draws a single "grabber", a filled square
    * @param g2 the graphics context
    * @param x the x coordinate of the center of the grabber
    * @param y the y coordinate of the center of the grabber
    */
   public static void drawGrabber(Graphics2D g2, double x, double y)
   {
      final int SIZE = 5;
      Color oldColor = g2.getColor();
      g2.setColor(PURPLE);
      g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
      g2.setColor(oldColor);
   }

   /**
    * gets preferred size
    * @return the preferred size
    */
   public Dimension getPreferredSize()
   {
      Rectangle2D bounds = graph.getBounds((Graphics2D) getGraphics());
      return new Dimension((int) (zoom * bounds.getMaxX()),
            (int) (zoom * bounds.getMaxY()));
   }

   /**
    * Changes the zoom of this panel. The zoom is 1 by default and is multiplied
    * by sqrt(2) for each positive stem or divided by sqrt(2) for each negative
    * step.
    * @param steps the number of steps by which to change the zoom. A positive
    * value zooms in, a negative value zooms out.
    */
   public void changeZoom(int steps)
   {
      final double FACTOR = Math.sqrt(2);
      for (int i = 1; i <= steps; i++) {
         zoom *= FACTOR;
      }
      for (int i = 1; i <= -steps; i++) {
         zoom /= FACTOR;
      }
      revalidate();
      repaint();
   }

   /**
    * Changes the grid size of this panel. The zoom is 10 by default and is
    * multiplied by sqrt(2) for each positive stem or divided by sqrt(2) for
    * each negative step.
    * @param steps the number of steps by which to change the zoom. A positive
    * value zooms in, a negative value zooms out.
    */
   public void changeGridSize(int steps)
   {
      final double FACTOR = Math.sqrt(2);
      for (int i = 1; i <= steps; i++) {
         gridSize *= FACTOR;
      }
      for (int i = 1; i <= -steps; i++) {
         gridSize /= FACTOR;
      }
      grid.setGrid((int) gridSize, (int) gridSize);
      graph.layout();
      repaint();
   }

   /**
    * selects next item
    * @param n number of next item
    */
   public void selectNext(int n)
   {
      ArrayList selectables = new ArrayList();
      selectables.addAll(graph.getNodes());
      selectables.addAll(graph.getEdges());
      if (selectables.size() == 0) return;
      java.util.Collections.sort(selectables, new java.util.Comparator()
      {
         public int compare(Object obj1, Object obj2)
         {
            double x1;
            double y1;
            if (obj1 instanceof Node)
            {
               Rectangle2D bounds = ((Node) obj1).getBounds();
               x1 = bounds.getX();
               y1 = bounds.getY();
            }
            else
            {
               Point2D start = ((Edge) obj1).getConnectionPoints().getP1();
               x1 = start.getX();
               y1 = start.getY();
            }
            double x2;
            double y2;
            if (obj2 instanceof Node)
            {
               Rectangle2D bounds = ((Node) obj2).getBounds();
               x2 = bounds.getX();
               y2 = bounds.getY();
            }
            else
            {
               Point2D start = ((Edge) obj2).getConnectionPoints().getP1();
               x2 = start.getX();
               y2 = start.getY();
            }
            if (y1 < y2) return -1;
            if (y1 > y2) return 1;
            if (x1 < x2) return -1;
            if (x1 > x2) return 1;
            return 0;
         }
      });
      int index;
      if (lastSelected == null) index = 0;
      else index = selectables.indexOf(lastSelected) + n;
      while (index < 0)
         index += selectables.size();
      index %= selectables.size();
      setSelectedItem(selectables.get(index));
      repaint();
   }

   /**
    * Checks whether this graph has been modified since it was last saved.
    * @return true if the graph has been modified
    */
   public boolean isModified()
   {
      return modified;
   }

   /**
    * Sets or resets the modified flag for this graph
    * @param newValue true to indicate that the graph has been modified
    */
   public void setModified(boolean newValue)
   {
      modified = newValue;

      if (frame == null)
      {
         Component parent = this;
         do
         {
            parent = parent.getParent();
         }
         while (parent != null && !(parent instanceof GraphFrame));
         if (parent != null) frame = (GraphFrame) parent;
      }
      if (frame != null)
      {
         String title = frame.getFileName();
         if (title != null)
         {
            if (modified)
            {
               if (!frame.getTitle().endsWith("*")) frame.setTitle(title + "*");
            }
            else frame.setTitle(title);
         }
      }
   }

   private void addSelectedItem(Object obj)
   {

      lastSelected = obj;
      selectedItems.add(obj);


   }

   /**
    * adds a command listener
    * @param listener listener to add
    */
   public void addCommandListener(CommandListener listener){
      commandListeners.add(listener);
   }

   /**
    * notifies all listeners ofa  command received
    * @param event event to be used
    */
   private void fireCommandReceived(CommandEvent event){
      for (int i = 0; i < commandListeners.size(); i++)
      {
         CommandListener listener = (CommandListener) commandListeners.get(i);
         //ChangeListener listener = (ChangeListener)propertyChangeListeners.get(i);
         listener.graphMod(event);
      }
   }
   /**
    Adds a property change listener to the list of listeners.
    @param listener the listener to add
    */
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      propertyChangeListeners.add(listener);
   }

   /**
    Notifies all listeners of a state change.
    @param event the event to propagate
    */
   private void firePropertyChanged(PropertyChangeEvent event)
   {
      for (int i = 0; i < propertyChangeListeners.size(); i++)
      {
         PropertyChangeListener listener = (PropertyChangeListener)propertyChangeListeners.get(i);
         //ChangeListener listener = (ChangeListener)propertyChangeListeners.get(i);
         listener.propertyChange(event);
      }
   }
   private void removeSelectedItem(Object obj)
   {
      if (obj == lastSelected)
         lastSelected = null;
      selectedItems.remove(obj);
   }
   
   private void setSelectedItem(Object obj)
   {
      selectedItems.clear();
      lastSelected = obj;
      if (obj != null) selectedItems.add(obj);
   }
   
   private void clearSelection()
   {
      selectedItems.clear();
      lastSelected = null;
   }
   
   /**
    * Sets the value of the hideGrid property
    * @param newValue true if the grid is being hidden
    */
   public void setHideGrid(boolean newValue)
   {
      hideGrid = newValue;
      repaint();
   }

   /**
    * Gets the value of the hideGrid property
    * @return true if the grid is being hidden
    */
   public boolean getHideGrid()
   {
      return hideGrid;
   }

   /**
    * returns user id
    * @return user id
    */
   public String getUserId() {
      return userId;
   }

   private String serverAddress = "http://104.196.246.226";
   private Timer timer;
   private JTextField sessionIdText;
   private JTextField userIdText;
   private int commandCount = 0;
   private ArrayList commandListeners = new ArrayList();
   private ArrayList propertyChangeListeners = new ArrayList();
   private int nodecounter= 0 ;
   private int edgecounter = 0;
   private String sessionId;
   private String userId;
   private ServerSync serverLink;
   private Graph graph;
   private Grid grid;
   private GraphFrame frame;
   private ToolBar toolBar;


   private double zoom;
   private double gridSize;
   private boolean hideGrid;
   private boolean modified;

   private Object lastSelected;
   private Set selectedItems;

   private Point2D lastMousePoint;
   private Point2D mouseDownPoint;   
   private int dragMode;
      
   private static final int DRAG_NONE = 0;
   private static final int DRAG_MOVE = 1;
   private static final int DRAG_RUBBERBAND = 2;
   private static final int DRAG_LASSO = 3;
   
   private static final int GRID = 10;

   private static final int CONNECT_THRESHOLD = 8;

   private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
}
