package org.gi.gICore.util;

import org.bukkit.entity.Player;
import org.gi.gICore.manager.MessageManager;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    private final MessageManager messageManager;
    private String key;
    private final Map<String, String> placeholders;
    private String locale;

    public MessageBuilder(MessageManager messageManager) {
        this.messageManager = messageManager;
        this.placeholders = new HashMap<>();
    }

    public static MessageBuilder create(MessageManager messageManager) {
        return new MessageBuilder(messageManager);
    }

    public MessageBuilder key(String key) {
        this.key = key;
        return this;
    }

    public MessageBuilder placeholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    public MessageBuilder placeholders(Map<String, String> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    public MessageBuilder locale(String locale) {
        this.locale = locale;
        return this;
    }

    public String build() {
        if (locale != null) {
            return messageManager.getMessage(locale, key, placeholders);
        } else {
            return messageManager.getMessage("ko_kr", key, placeholders);
        }
    }

    public void send(Player player) {
        messageManager.sendMessage(player, key, placeholders);
    }

    public void broadcast() {
        messageManager.broadcast(key, placeholders);
    }
}
