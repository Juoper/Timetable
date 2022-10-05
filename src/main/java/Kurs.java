import java.util.*;

import static Strings.Format.format;


public class Kurs {
    private static List<Kurs> alleKurse = new ArrayList<>();

    private String fach;
    private String name;
    private String lehrer;
    private List<Stunde> stunden = new ArrayList<>();

    public Kurs (String name){
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

    public Kurs(String fach, String name, String lehrer) {
        this.fach = fach;
        this.name = name;
        this.lehrer = lehrer;
    }

    public void addStunde (Stunde s){
        if(!stunden.contains(s)) {
            stunden.add(s);
            Collections.sort(stunden);
        }
    }

    public List<Stunde> getStunden() {
        return stunden;
    }

    public boolean schonStundenDefiniert(){
        return stunden.size() != 0;
    }

    public static Kurs getKurs(String fach, String name, String lehrer){
        if(alleKurse.stream().map(i->i.name).toList().contains(name)
            && alleKurse.stream().map(i->i.fach).toList().contains(fach)){
            int i = alleKurse.stream().map(
                    k->k.fach + k.name).toList().indexOf(fach+name);
            return alleKurse.get(i);
        }
        Kurs k = new Kurs(fach, name, lehrer);
        alleKurse.add(k);
        sort(alleKurse);
        return k;
    }

    public static List<Kurs> getAlleKurse() {
        return alleKurse;
    }

    public static void sort(List<Kurs> eingabe){
        eingabe.sort(Comparator.comparing(o -> o.name));
    }

    public static void alleKurseAusgeben(){
        for (Kurs k : alleKurse) {
            System.out.println(k);
        }
    }

    @Override
    public String toString() {
        if(stunden.size() == 0) {
            return "Kurs{ " +
                    format(fach, 3) + " | " +
                    format(name, 10) + " | " +
                    format(lehrer, 3) + " | xxx}";
        }
        String ausgabe = "Kurs{ " +
                format(fach, 3) + " | " +
                format(name, 10) + " | " +
                format(lehrer, 3) + " | ";
        for (Stunde s : stunden) {
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
        Kurs kurs = (Kurs) o;
        return Objects.equals(fach, kurs.fach)
                && Objects.equals(name, kurs.name)
                && Objects.equals(lehrer, kurs.lehrer)
                && Objects.equals(stunden, kurs.stunden);
    }
}
