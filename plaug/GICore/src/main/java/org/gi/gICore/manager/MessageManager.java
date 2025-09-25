package org.gi.gICore.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.util.ModuleLogger;
import org.gi.gICore.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final ModuleLogger logger;
    private final Map<String, Map<String, String>> messages; // locale -> key -> message
    private final String defaultLocale;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
        this.logger = new ModuleLogger(null, "MessageManager");
        this.messages = new ConcurrentHashMap<>();
        this.defaultLocale = "ko_kr";

    }

    private void initialize() {
        // 지원 언어 목록
        List<String> supportedLanguages = List.of("ko_kr", "en_us", "ja_jp");

        for (String language : supportedLanguages) {
            loadLanguageMessages(language);
        }

        logger.info("Loaded messages for %d languages", supportedLanguages.size());
    }

    private void loadLanguageMessages(String language) {
        Map<String, String> langMessages = new HashMap<>();

        // messages/언어코드/ 디렉터리에서 모든 .yml 파일 로드
        File langDir = new File(plugin.getDataFolder(), "messages/" + language);
        if (!langDir.exists()) {
            langDir.mkdirs();

            // 기본 메시지 파일들 생성
            createDefaultMessageFiles(language);
        }

        File[] messageFiles = langDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (messageFiles != null) {
            for (File file : messageFiles) {
                String fileName = file.getName();
                Map<String, String> fileMessages = loadMessagesFromFile(language + "/" + fileName);
                langMessages.putAll(fileMessages);
            }
        }

        messages.put(language, langMessages);
        logger.debug("Loaded %d messages for language: %s", langMessages.size(), language);
    }
    private Map<String, String> loadMessagesFromFile(String filePath) {
        Map<String, String> fileMessages = new HashMap<>();

        var config = configManager.getConfig(filePath);
        if (config != null) {
            for (String key : config.getKeys(true)) {
                if (config.isString(key)) {
                    String message = config.getString(key);
                    if (message != null) {
                        fileMessages.put(key, StringUtil.colorize(message));
                    }
                }
            }
        }

        return fileMessages;
    }

    /**
     * 기본 메시지 파일들 생성
     */
    private void createDefaultMessageFiles(String language) {
        // common.yml 생성
        plugin.saveResource("messages/" + language + "/common.yml", false);
        plugin.saveResource("messages/" + language + "/error.yml", false);
        plugin.saveResource("messages/" + language + "/economy.yml", false);
    }

    /**
     * 메시지 가져오기 (플레이어 로케일 사용)
     */
    public String getMessage(Player player, String key) {
        String locale = getPlayerLocale(player);
        return getMessage(locale, key);
    }

    /**
     * 메시지 가져오기 (로케일 지정)
     */
    public String getMessage(String locale, String key) {
        return getMessage(locale, key, new HashMap<>());
    }

    /**
     * 메시지 가져오기 (플레이스홀더 포함)
     */
    public String getMessage(Player player, String key, Map<String, String> placeholders) {
        String locale = getPlayerLocale(player);
        return getMessage(locale, key, placeholders);
    }

    /**
     * 메시지 가져오기 (로케일 및 플레이스홀더 지정)
     */
    public String getMessage(String locale, String key, Map<String, String> placeholders) {
        Map<String, String> langMessages = messages.get(locale);

        // 해당 언어가 없으면 기본 언어 사용
        if (langMessages == null) {
            langMessages = messages.get(defaultLocale);
        }

        // 기본 언어도 없으면 키 반환
        if (langMessages == null) {
            return key;
        }

        String message = langMessages.get(key);
        if (message == null) {
            // 기본 언어에서 찾아보기
            if (!locale.equals(defaultLocale)) {
                Map<String, String> defaultMessages = messages.get(defaultLocale);
                if (defaultMessages != null) {
                    message = defaultMessages.get(key);
                }
            }

            // 그래도 없으면 키 반환
            if (message == null) {
                return key;
            }
        }

        // 플레이스홀더 치환
        return StringUtil.replacePlaceholders(message, placeholders);
    }

    /**
     * 메시지 목록 가져오기
     */
    public List<String> getMessageList(Player player, String key) {
        String locale = getPlayerLocale(player);
        return getMessageList(locale, key);
    }

    public List<String> getMessageList(String locale, String key) {
        // 구현 생략 - 필요시 ConfigManager에서 getStringList 사용
        return List.of(getMessage(locale, key));
    }

    /**
     * 메시지 전송
     */
    public void sendMessage(Player player, String key) {
        sendMessage(player, key, new HashMap<>());
    }

    public void sendMessage(Player player, String key, Map<String, String> placeholders) {
        String message = getMessage(player, key, placeholders);
        player.sendMessage(message);
    }

    public void sendMessage(CommandSender sender, String locale, String key, Map<String, String> placeholders) {
        String message = getMessage(locale, key, placeholders);
        sender.sendMessage(message);
    }

    /**
     * 브로드캐스트 메시지
     */
    public void broadcast(String key) {
        broadcast(key, new HashMap<>());
    }

    public void broadcast(String key, Map<String, String> placeholders) {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            sendMessage(player, key, placeholders);
        });
    }

    /**
     * 권한별 브로드캐스트
     */
    public void broadcast(String permission, String key, Map<String, String> placeholders) {
        plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> sendMessage(player, key, placeholders));
    }

    /**
     * 플레이어 로케일 가져오기
     */
    private String getPlayerLocale(Player player) {
        if (player == null) {
            return defaultLocale;
        }

        String locale = player.getLocale().toLowerCase();

        // 지원하는 로케일로 변환
        switch (locale) {
            case "ko_kr":
            case "korean":
                return "ko_kr";
            case "en_us":
            case "english":
                return "en_us";
            case "ja_jp":
            case "japanese":
                return "ja_jp";
            default:
                return defaultLocale;
        }
    }

    /**
     * 메시지 리로드
     */
    public void reload() {
        messages.clear();
        initialize();
        logger.info("Messages reloaded");
    }

    /**
     * 지원하는 언어 목록 가져오기
     */
    public List<String> getSupportedLanguages() {
        return List.copyOf(messages.keySet());
    }

    /**
     * 메시지 존재 여부 확인
     */
    public boolean hasMessage(String locale, String key) {
        Map<String, String> langMessages = messages.get(locale);
        return langMessages != null && langMessages.containsKey(key);
    }

    /**
     * 메시지 개수 가져오기
     */
    public int getMessageCount(String locale) {
        Map<String, String> langMessages = messages.get(locale);
        return langMessages != null ? langMessages.size() : 0;
    }
}