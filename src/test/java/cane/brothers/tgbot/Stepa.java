package cane.brothers.tgbot;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Stepa {

    public static void main(String[] args) {
        for (int a = 0; a <= 10; a++) {
            System.out.print("a=" + a);
            var x = func(a);
            System.out.println(",\t x=" + round(x));
        }
    }

    private static double round(double x) {
        return new BigDecimal(x).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static double func(int a) {
        double tmp = a * 0.1d;
        if (tmp < 0.4d) {
            tmp += 7d / 3d;
            tmp *= 30d / 7d;
            tmp -= 38d / 7d;
        } else {
            tmp -= 2d / 9d;

            if (tmp >= 5d / 18d) {
                tmp *= 9d;
            } else {
                tmp += 47d / 45d;
            }
        }
        return tmp;
    }
}
