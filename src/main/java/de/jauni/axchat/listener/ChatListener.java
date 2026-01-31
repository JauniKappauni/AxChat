package de.jauni.axchat.listener;

import de.jauni.axchat.AxChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {
    AxChat reference;

    public ChatListener(AxChat reference) {
        this.reference = reference;
    }

    @EventHandler
    public void onChatMessage(PlayerChatEvent e) {
        Long timestamp = reference.getChatManager().getLastMessageTime().get(e.getPlayer().getUniqueId());
        if (timestamp != null && System.currentTimeMillis() - timestamp < reference.getConfig().getInt("chat_cooldown")) {
            Long remainingMillis = (reference.getConfig().getInt("chat_cooldown") - (System.currentTimeMillis() - timestamp));
            Long seconds = remainingMillis / 1000;
            Long displaySeconds = seconds + 1;
            e.getPlayer().sendMessage("Bitte warte noch" + " " + displaySeconds + " " + "," + " " + "bevor du wieder schreibst!");
            e.setCancelled(true);
            return;
        }
        List<String> forbiddenWords = reference.getConfig().getStringList("forbidden-words");
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
            String formatPlayer = reference.getMessage("chat.prefix");
            String formatSeparator = reference.getMessage("chat.separator");
            String formatMessage = reference.getMessage("chat.suffix");

            String formattedMessage = formatPlayer.replace("player", p.getName()) + " " + formatSeparator + " " + formatMessage.replace("message", message);
            reference.getChatManager().publishMessage("global_chat", formattedMessage);
            reference.getChatManager().getLastMessageTime().put(p.getUniqueId(), System.currentTimeMillis());
            e.setCancelled(true);
        }
    }
}
