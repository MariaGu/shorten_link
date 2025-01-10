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
    private static final int LINK_MIN_VISIT;


    static {
        Properties properties = loadProperties();
        LINK_EXPIRY_DAYS = Integer.parseInt(properties.getProperty(("link.days.max")));
        LINK_MIN_VISIT = Integer.parseInt(properties.getProperty(("link.min.visit")));
    }

    private Map<UUID, User> users = new HashMap<>();

    public UUID createUser() {
        User user = new User();
        users.put(user.getUuid(), user);
        return user.getUuid();
    }

    public void cleanupExpiredUrls() {
        System.out.println("Запуск шедулера");
        List<String> toDeleteUrls = new ArrayList<>();
        for (User user : users.values()) {
            for (Map.Entry<String, ShortenedUrl> entry : user.getUrls().entrySet()) {
                if (entry.getValue().isExpired() || entry.getValue().isLimitReached()) {
                    toDeleteUrls.add(entry.getKey());
                }
            }
            for (String shortUrl : toDeleteUrls) {
                user.removeUrl(shortUrl);
                System.out.println("Удалена недоступная ссылка: " + shortUrl);
            }
        }
    }

    public String shortenUrl(UUID userId, String longUrl, int visitLimit, int liveLimit) {
        String shortUrl = (BASE_URL + Math.abs(userId.hashCode())).substring(0, 12)
                + UUID.randomUUID().toString().substring(0, 4);

        int expiryLimit = liveLimit < LINK_EXPIRY_DAYS ? liveLimit : LINK_EXPIRY_DAYS;
        long expiryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiryLimit);

        int visitMin = visitLimit > LINK_MIN_VISIT ? visitLimit : LINK_MIN_VISIT;
        ShortenedUrl shortenedUrl = new ShortenedUrl(longUrl, shortUrl, visitMin, expiryTime);

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

    public String editCurrentVisits(UUID userId, String link, int browseNumber) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            if (user.getUrl(link) != null) {
                user.getUrl(link).setCurrentVisits(browseNumber);
                return "Текущее число переходов по ссылкe успешно изменено на " + browseNumber;
            } else {
                return "Данная ссылка удалена.";
            }
        } else {
            return "Пользователя с таким UUID не существует.";
        }
    }

    public String deleteLink(UUID userId, String link) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            if (user.getUrl(link) != null) {
                user.getUrls().remove(link);
                return "Коротка ссылка успешно удалена " + link;
            } else {
                return "Данная ссылка уже удалена.";
            }
        } else {
            return "Пользователя с таким UUID не существует.";
        }
    }
}
