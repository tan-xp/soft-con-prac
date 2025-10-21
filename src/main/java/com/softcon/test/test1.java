package com.softcon.test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class test1 {
    public static void main(String[] args) {
        int n1, n2;
        int i = 0;
        String result;

        Random random = new Random();

        Set all = new HashSet();
        while (i < 50) {
            if (random.nextBoolean()) {
                n1 = random.nextInt(100);
                n2 = random.nextInt(0, 100 - n1) + 1;

                result = String.format("%d+%d=%d%" + (20 - (n1 + "+" + n2 + "=" + (n1 + n2)).length()) + "s",
                        n1, n2, (n1 + n2), "");

                if (!all.add(new HashSet<>(List.of("+", n1, n2, (n1 + n2))))) {
                    continue;
                }
                System.out.printf(result);

            } else {
                n1 = random.nextInt(100) + 1;
                n2 = random.nextInt(0, n1);

                result = String.format("%d-%d=%d%" + (20 - (n1 + "-" + n2 + "=" + (n1 - n2)).length()) + "s",
                        n1, n2, (n1 - n2), "");

                if (!all.add(new HashSet<>(List.of("-", n1, n2, (n1 - n2))))) {
                    continue;
                }
                System.out.printf(result);
            }

            i++;
            if (i % 5 == 0) {
                System.out.println();
            }
        }
    }
}
