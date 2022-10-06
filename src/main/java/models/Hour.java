package models;

import static Strings.Format.format;

public class Hour implements Comparable{
    public int tag;
    public int stunde;

    public Hour(int tag, int stunde) {
        this.tag = tag;
        this.stunde = stunde;
    }

    public static String getTag (int tag){
        return switch (tag){
            case 0 -> "Montag";
            case 1 -> "Dienstag";
            case 2 -> "Mittwoch";
            case 3 -> "Donnerstag";
            case 4 -> "Freitag";
            default -> "-!FEHLER!-";
        };
    }

    public static String getTagKurz(int tag){
        return getTag(tag).substring(0, 2).toUpperCase();
    }

    @Override
    public int compareTo(Object o) {
        Hour s = (Hour) o;
        if(s.tag != tag){
            return Integer.compare(tag, s.tag);
        }
        return Integer.compare(stunde, s.stunde);
    }

    @Override
    public String toString() {
        return "[" +
                getTagKurz(tag) + "|" + format(stunde+1, 2) +
                "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hour stunde1 = (Hour) o;
        return tag == stunde1.tag && stunde == stunde1.stunde;
    }
}
