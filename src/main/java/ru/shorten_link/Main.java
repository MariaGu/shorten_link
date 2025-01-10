package ru.shorten_link;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UrlShortener urlShortener = new UrlShortener();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(urlShortener::cleanupExpiredUrls, 1, 30, TimeUnit.MINUTES);

        printMenu();
        while (true) {
            String s = scanner.nextLine();
            if (s.equals("3")) break;
            else if (s.equals("1")) {
                browseLink(scanner, urlShortener);
            } else if (s.equals("2")) {
                createShortLink(scanner, urlShortener);
            } else {
                System.out.println("Операция не поддерживается");
                printMenu();
            }
        }
    }

    private static void createShortLink(Scanner scanner, UrlShortener urlShortener) {
        System.out.println("Введите ваш UUID, если имеется. Если нет  - нажмите Enter");

        String userUUID = scanner.nextLine();
        UUID userId = null;

        if (userUUID.isBlank()) {
            userId = urlShortener.createUser();
            System.out.println("Ваш UUID: " + userId);
        } else {
            userId = UUID.fromString(userUUID);
        }

        System.out.println("Введите длинную ссылку для преобразования:");
        String link = scanner.nextLine();
        System.out.println("Задайте мксимальное количество переходов по ссылке:");
        int browseLimit = Integer.parseInt(scanner.nextLine());

        String shortUrl = urlShortener.shortenUrl(userId, link, browseLimit);
        System.out.println("Ваша короткая ссылка: " + shortUrl);
    }

    private static void browseLink(Scanner scanner, UrlShortener urlShortener) {
        System.out.println("Введите ссылку");
        String shortLink = scanner.nextLine();
        System.out.println(urlShortener.visitShortUrl(shortLink));
    }

    private static void printMenu() {
        System.out.println("""
                Введите команды:
                '1' - для перехода по короткой ссылке;
                '2' - для создания короткой ссылки;
                '3' - для выхода из программы;
                """);
    }
}