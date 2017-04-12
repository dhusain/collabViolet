package com.horstmann.violet.framework;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EventObject;
import java.util.Scanner;

/**
 * handles sending and receiving updates from the server
 */
public class ServerSync {
    private String sessionId;
    private String userId;
    private URL url;
    private String serverAddress;

    /**
     * gets server address
     * @return address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * sets address
     * @param serverAddress new addreess
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * creates a serversync object
     * @param sessionId servers session id
     */
    public ServerSync(String sessionId, String userId, String serverAddress) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.serverAddress = serverAddress;
    }

    /**
     * gets user id
     * @return user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * sets user id
     * @param userId new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * getter
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * sets session id
     * @param sessionId makes this the sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void sendCommand(CommandEvent event){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject(event);
            oos.flush();
            oos.close();
            //serverLink.sendChange(Base64.getEncoder().encode(baos.toByteArray()));
            String change = URLEncoder.encode(new String(Base64.getEncoder().encode(baos.toByteArray()), "UTF-8"), "UTF-8");
            notifyServer(change);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * sends encoded serialized objects
     * @param propertyValue encoded object
     * @return the byte sequence
     */
    public String sendChange(byte[] propertyValue){
        //DataOutputStream dOut = new DataOutputStream();
        String  s1 = new String(propertyValue);
        try {
            new URL(serverAddress+"/add?id=" + sessionId + "&name=" + s1).openStream();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Sends text to server for testing
     * @param e event to be sent
     */
    public void notifyServer(String e){
        try {
            new URL(serverAddress+"/add?id=" + sessionId + "&name=" + e).openStream();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Receives changes
     * @return the changes event
     */
    public ArrayList<EventObject> receiveChanges(){
        String inputLine="";
        ArrayList<EventObject> events = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        try {
            String urlString = serverAddress+"/connect/" + sessionId;
            URL url = new URL(urlString);
            Scanner in = new Scanner(
                    new InputStreamReader(url.openConnection().getInputStream()));
            in.nextLine();
            while (in.hasNextLine()) {
                inputLine = in.nextLine();
                strings.add(inputLine);
            }
            in.close();


        }catch (IOException e){

        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            for(String s: strings) {
                byte[] data = Base64.getDecoder().decode(s);
                //byte [] data = Base64.getDecoder().decode(inputLine.replaceAll("\\s+",""));
                ObjectInputStream ois = new ObjectInputStream(
                        new ByteArrayInputStream(data));
                Object o = ois.readObject();
                ois.close();
                EventObject event = (EventObject) o;
                events.add(event);
            }
            return events;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
