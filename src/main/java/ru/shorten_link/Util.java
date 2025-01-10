package ru.shorten_link;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Util {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        String name = "app.properties";
        try (InputStream in = new FileInputStream(name)) {
            properties.load(in);
        } catch (Exception e) {
            try (InputStream in = Util.class.getClassLoader().getResourceAsStream(name)) {
                properties.load(in);
            } catch (Exception ex) {
                throw new RuntimeException("Ошибка загрузки параметров: " + ex.getMessage());
            }
        }
        return properties;
    }
}
