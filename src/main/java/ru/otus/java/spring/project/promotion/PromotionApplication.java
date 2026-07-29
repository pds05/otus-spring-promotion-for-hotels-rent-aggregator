package ru.otus.java.spring.project.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PromotionApplication  {

    public static void main(String[] args) {

        SpringApplication.run(PromotionApplication.class, args);

        System.out.println("Для доступа к приложению перейди по ссылке:" + "http://localhost:8088/ha_promotion/");

        System.out.println("Для работы Telegram бота нужно запустить приложение с аргументом '--botToken={TOKEN}'");

        System.out.println("Для входа в систему использую учетную запись user/password или manager/password");

    }
}
