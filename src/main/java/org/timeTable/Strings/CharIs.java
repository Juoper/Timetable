package org.timeTable.Strings;

public class CharIs {
    public static boolean isUpperCase(char c){
        if((65 <= c && c <= 90)||(97 <= c && c <= 122)) {
            return String.valueOf(c).toUpperCase().equals(String.valueOf(c));
        }
        return false;
    }
    public static boolean isNumber(char c){
        return 48 <= c && c <= 57;
    }
    public static boolean isBuchstabe(char c){
        return (65 <= c && c <= 90)||(97 <= c && c <= 122);
    }
}
