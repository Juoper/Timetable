package org.timeTable.models;

import java.util.*;

import static org.timeTable.Strings.Format.format;


public class Kurs_old {
    private static List<Kurs_old> alleKurse = new ArrayList<>();

    private String fach;
    private String name;
    private String lehrer;
    private List<Hour> stunden = new ArrayList<>();

    public Kurs_old(String name){
        this.name = name;
    }

    public String getFach() {
        return fach;
    }

    public String getName() {
        return name;
    }

    public String getLehrer() {
        return lehrer;
    }

    public Kurs_old(String fach, String name, String lehrer) {
        this.fach = fach;
        this.name = name;
        this.lehrer = lehrer;
    }

    public void addStunde (Hour s){
        if(!stunden.contains(s)) {
            stunden.add(s);
            Collections.sort(stunden);
        }
    }

    public List<Hour> getStunden() {
        return stunden;
    }

    public boolean schonStundenDefiniert(){
        return stunden.size() != 0;
    }

    public static Kurs_old getKurs(String fach, String name, String lehrer){
        if(alleKurse.stream().map(i->i.name).toList().contains(name)
            && alleKurse.stream().map(i->i.fach).toList().contains(fach)){
            int i = alleKurse.stream().map(
                    k->k.fach + k.name).toList().indexOf(fach+name);
            return alleKurse.get(i);
        }
        Kurs_old k = new Kurs_old(fach, name, lehrer);
        alleKurse.add(k);
        sort(alleKurse);
        return k;
    }

    public static List<Kurs_old> getAlleKurse() {
        return alleKurse;
    }

    public static void sort(List<Kurs_old> eingabe){
        eingabe.sort(Comparator.comparing(o -> o.name));
    }

    public static void alleKurseAusgeben(){
        for (Kurs_old k : alleKurse) {
            System.out.println(k);
        }
    }

    @Override
    public String toString() {
        if(stunden.size() == 0) {
            return "org.timeTable.models.Kurs{ " +
                    format(fach, 3) + " | " +
                    format(name, 10) + " | " +
                    format(lehrer, 3) + " | xxx}";
        }
        String ausgabe = "org.timeTable.models.Kurs{ " +
                format(fach, 3) + " | " +
                format(name, 10) + " | " +
                format(lehrer, 3) + " | ";
        for (Hour s : stunden) {
            ausgabe += s + ",";
        }
        ausgabe = ausgabe.substring(0, ausgabe.length() - 1);
        ausgabe += "}";
        return ausgabe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kurs_old kurs = (Kurs_old) o;
        return Objects.equals(fach, kurs.fach)
                && Objects.equals(name, kurs.name)
                && Objects.equals(lehrer, kurs.lehrer)
                && Objects.equals(stunden, kurs.stunden);
    }
}
