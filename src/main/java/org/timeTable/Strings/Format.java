package org.timeTable.Strings;

public class Format {
    public static String format(int eingabe, int stellen){
        return format(String.valueOf(eingabe), stellen).replace(" ", "0");
    }

    public static String format(String eingabe, int stellen){
        String f = "%" + stellen + "." + stellen + "s";
        return String.format(f, eingabe);
    }
}
