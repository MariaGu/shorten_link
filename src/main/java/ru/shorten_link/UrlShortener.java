package ru.shorten_link;

import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.shorten_link.Util.loadProperties;

public class UrlShortener {
    private static final String BASE_URL = "click.ru/";
    private static final int LINK_EXPIRY_DAYS;


    static {
        Properties properties = loadProperties();
        LINK_EXPIRY_DAYS = Integer.parseInt(properties.getProperty(("link.days.max")));
    }

    private Map<UUID, User> users = new HashMap<>();

    public UUID createUser() {
        User user = new User();
        users.put(user.getUuid(), user);
        return user.getUuid();
    }

    public void cleanupExpiredUrls() {
        System.out.println("Запуск шедулера");
        List<String> expiredUrls = new ArrayList<>();
        for (User user : users.values()) {
            for (Map.Entry<String, ShortenedUrl> entry : user.getUrls().entrySet()) {
                if (entry.getValue().isExpired()) {
                    expiredUrls.add(entry.getKey());
                }
            }
            for (String shortUrl : expiredUrls) {
                user.removeUrl(shortUrl);
                System.out.println("Удалена просроченная ссылка: " + shortUrl);
            }
        }
    }

    public String shortenUrl(UUID userId, String longUrl, int visitLimit) {
        String shortUrl = (BASE_URL + Math.abs(userId.hashCode())).substring(0,12)
                + UUID.randomUUID().toString().substring(0, 4);
        long expiryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(LINK_EXPIRY_DAYS);
        ShortenedUrl shortenedUrl = new ShortenedUrl(longUrl, shortUrl, visitLimit, expiryTime);

        users.get(userId).addUrl(shortenedUrl);
        return shortUrl;
    }

    public String visitShortUrl(String shortUrl) {
        for (User user : users.values()) {
            ShortenedUrl url = user.getUrl(shortUrl);
            if (url != null) {
                if (url.isExpired()) {
                    return "Ссылка просрочена.";
                }

                if (url.isLimitReached()) {
                    return "Лимит переходов по ссылке исчерпан.";
                }

                url.visit();
                try {
                    Desktop.getDesktop().browse(new URI(url.getOriginalUrl()));
                } catch (Exception e) {
                    return "Ссылка недоступна";
                }
                return "Открываю...";
            }
        }
        return "Коротка ссылка не найдена.";
    }
}
