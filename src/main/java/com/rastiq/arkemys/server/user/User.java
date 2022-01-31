package com.rastiq.arkemys.server.user;

import com.rastiq.arkemys.server.SocketServer;
import com.rastiq.arkemys.server.util.Timer;

public class User {

    public String mcName;
    public boolean isUser;
    public Timer timeSinceKeepalive;

    public User(String mcName, boolean isUser) {
        this.mcName = mcName;
        this.isUser = isUser;
        timeSinceKeepalive = new Timer();
    }

    public String getProperties() {
        return mcName + ":" + (isUser ? "true" : "false");
    }


    public String getMcName() {
        return mcName;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setMcName(String mcName) {
        this.mcName = mcName;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public long getTimeSinceLastKeepalive() {
        return timeSinceKeepalive.getTime();
    }

    public void resetKeepAliveTime() {
        timeSinceKeepalive.reset();
        System.out.println("Reset client " + getMcName());
    }

    public void remove(){
        SocketServer.users.remove(this);
        SocketServer.count--;
        System.out.println("Removed client " + getMcName());
    }
}
