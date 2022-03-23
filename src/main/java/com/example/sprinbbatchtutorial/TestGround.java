package com.example.sprinbbatchtutorial;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TestGround {
    public static void main(String[] args) {
        System.out.println(ZoneId.systemDefault());
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        System.out.println(localDateTime);
        long l = localDateTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println(l);
    }
}
