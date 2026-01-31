package de.jauni.axchat.manager;

import de.jauni.axchat.AxChat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {
    private String host;
    private int port;

    Jedis publisher;

    Map<UUID, Long> lastMessageTime = new HashMap<>();

    public ChatManager(String host, int port) {
        this.host = host;
        this.port = port;
        publisher = new Jedis(host, port);
    }

    public void publishMessage(String channel, String message) {
        publisher.publish(channel, message);
    }

    public void subscribe(String channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Jedis subscriber = new Jedis(host, port)) {
                    subscriber.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String message) {
                            for (var p : Bukkit.getOnlinePlayers()) {
                                String parsed = PlaceholderAPI.setPlaceholders(p, message);
                                p.sendMessage(parsed);
                            }
                        }
                    }, channel);
                }
            }
        }).start();
    }

    public void subscribePrivateMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Jedis subscriber = new Jedis(host, port)) {
                    subscriber.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String message) {
                            String[] messageParts = message.split(";");
                            String sourcePlayer = messageParts[0];
                            String targetName = messageParts[1];
                            String msg = messageParts[2];
                            Player targetPlayer = Bukkit.getPlayerExact(targetName);
                            if (targetPlayer != null) {
                                // Actual message
                                targetPlayer.sendMessage(sourcePlayer + " " + "-" + " " + targetPlayer.getName() + " " + ":" + " " + msg);
                            }
                        }
                    }, "private_messages");
                }
            }
        }).start();
    }

    public Map<UUID, Long> getLastMessageTime(){
        return lastMessageTime;
    }
}
