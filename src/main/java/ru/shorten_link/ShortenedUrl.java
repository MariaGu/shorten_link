package ru.shorten_link;

class ShortenedUrl {
    private final String originalUrl;
    private final String shortUrl;
    private final int visitLimit;
    private int currentVisits;
    private final long expiryTime;

    public ShortenedUrl(String originalUrl, String shortUrl, int visitLimit, long expiryTime) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.visitLimit = visitLimit;
        this.currentVisits = 0;
        this.expiryTime = expiryTime;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setCurrentVisits(int currentVisits) {
        this.currentVisits = currentVisits;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public boolean canVisit() {
        return currentVisits < visitLimit && !isExpired();
    }

    public void visit() {
        if (canVisit()) {
            currentVisits++;
            if (isLimitReached()){
                System.out.println("Лимит переходов по ссылке исчерпан");
            }
        }
    }

    public boolean isLimitReached() {
        return currentVisits >= visitLimit;
    }
}
