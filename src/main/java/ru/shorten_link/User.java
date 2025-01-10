package ru.shorten_link;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class User {
    private final UUID uuid;
    private Map<String, ShortenedUrl> urls = new HashMap<>();

    public User() {
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, ShortenedUrl> getUrls() {
        return urls;
    }

    public void addUrl(ShortenedUrl url) {
        urls.put(url.getShortUrl(), url);
    }

    public void removeUrl(String url) {
        urls.remove(url);
    }

    public ShortenedUrl getUrl(String shortUrl) {
        return urls.get(shortUrl);
    }
}
