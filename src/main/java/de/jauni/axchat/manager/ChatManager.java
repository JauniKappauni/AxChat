package de.jauni.axchat.manager;

import de.jauni.axchat.AxChat;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class ChatManager {
    AxChat reference;
    public ChatManager(AxChat reference){
        this.reference = reference;
    }
    Jedis publisher;

    public ChatManager(String host, int port) {
        publisher = new Jedis(host, port);
    }

    public void publishMessage(String channel, String message) {
        publisher.publish(channel, message);
    }

    public void subscribe(String channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Jedis subscriber = new Jedis(reference.getConfig().getString("redis.host"), reference.getConfig().getInt("redis.port"))) {
                    subscriber.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String ch, String message) {
                            for (var p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(message);
                            }
                        }
                    }, channel);
                }
            }
        }).start();

    }
}
