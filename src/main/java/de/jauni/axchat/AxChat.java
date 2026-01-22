package de.jauni.axchat;

import de.jauni.axchat.manager.ChatManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxChat extends JavaPlugin {
    ChatManager chatManager;

    public ChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        String host = getConfig().getString("redis.host");
        int port = getConfig().getInt("redis.port");
        chatManager = new ChatManager(host, port);
        getServer().getPluginManager().registerEvents(new de.jauni.axchat.listener.ChatListener(this), this);
        chatManager.subscribe("global_chat");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
