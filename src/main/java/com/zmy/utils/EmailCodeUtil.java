package com.zmy.utils;

import java.util.Random;

public class EmailCodeUtil {
    public static String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }
}
