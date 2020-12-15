package net.hungermania.manialib.util;

import java.util.Arrays;

public final class Utils {
    public static void printCurrentStack() {
        System.out.println(Arrays.toString(new Throwable().getStackTrace()));
    }
}