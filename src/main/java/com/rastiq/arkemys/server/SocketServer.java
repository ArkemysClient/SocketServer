package com.rastiq.arkemys.server;

import co.gongzh.procbridge.IDelegate;
import co.gongzh.procbridge.Server;
import com.rastiq.arkemys.server.user.User;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketServer {

    public static ArrayList<User> users = new ArrayList<>();
    public static int count = -1;

    public static void main(String[] args) throws FileNotFoundException {
        Server server = new Server(1337, new IDelegate() {
            @Override
            public @Nullable Object handleRequest(@Nullable String method, @Nullable Object payload) {
                switch (method) {
                    case "start":
                        String[] userProperties = payload.toString().split(":");
                        if (isClientInListByUsername(userProperties[0]) == true) {
                            System.out.println(userProperties[0] + " already on list, removing.");
                            getClientByUsername(userProperties[0]).remove();
                            count--;
                        }
                        users.add(new User(userProperties[0], Boolean.parseBoolean(userProperties[1])));
                        count++;
                        System.out.println(users.size());
                        System.out.println(users.get(count).getProperties());
                        return payload;
                    case "isUser":
                        for(User user : users) {
                            if(payload.toString().equals(user.getMcName())) {
                                return "true";
                            }
                        }
                        return "false";
                    case "keepAlive":
                        User client = null;
                        if(isClientInListByUsername(payload.toString())) {
                            client = getClientByUsername(payload.toString());
                        }
                        assert client != null;
                        client.resetKeepAliveTime();
                        return payload;
                }
                return payload;
            }
        });

        System.out.println("Started server on port " + server.getPort());
        server.start();

        Runnable verifyAlive = new Runnable() {
            public void run() {
                for (User client : users) {
                    if(client.getTimeSinceLastKeepalive() >= 50000) {
                        client.remove();
                        count--;
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(verifyAlive, 0, 5, TimeUnit.SECONDS);
    }

    public static boolean isClientInListByUsername(String username) {
        for (User client : users) {
            if(client.getMcName().equals(username)) return true;
        }
        return false;
    }

    public static User getClientByUsername(String username) {
        for (User client : users) {
            if(client.getMcName().equals(username)) return client;
        }
        return null;
    }

}