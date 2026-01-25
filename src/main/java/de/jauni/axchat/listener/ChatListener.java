package de.jauni.axchat.listener;

import de.jauni.axchat.AxChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {
    String[] forbiddenWords = {"badword1", "badword2", "badlink"};
    AxChat reference;

    public ChatListener(AxChat reference) {
        this.reference = reference;
    }

    @EventHandler
    public void onChatMessage(PlayerChatEvent e) {
        String message = e.getMessage();
        Player p = e.getPlayer();
        boolean containsForbidden = false;
        for (String word : forbiddenWords) {
            if (message.toLowerCase().contains(word)) {
                containsForbidden = true;
                break;
            }
        }
        if (containsForbidden) {
            p.sendMessage("Deine Nachricht wurde blockiert!");
            e.setCancelled(true);
        } else {
            String msg = p.getName() + " " + ":" + " " + message;
            reference.getChatManager().publishMessage("global_chat", msg);
            e.setCancelled(true);
        }
    }
}
