import java.util.ArrayList;
import java.util.List;

public class Schueler {
    private String name;

    private List<Kurs> kurse = new ArrayList<>();
    private List<List<String>> kNamen = new ArrayList<>();
    private List<List<String>> kLehrer = new ArrayList<>();
    private List<List<String>> kFaecher = new ArrayList<>();


    public Schueler(List<String> texte){
        name = texte.get(0).substring(0, texte.get(0).indexOf("Stand")-1);
        texte.remove(0);

        //Speichere den Stundenplan in den Listen
        //dabei steht der erste Index für die Stunde und der zweite für den Tag
        for (int i = 0; i < texte.size(); i++) {
            texte.set(i, formatTextLine(texte.get(i)));

            /*System.out.print(format(i, 2) + ". -> ");
            System.out.println(texte.get(i));*/

            if(i%2 == 0) {
                kNamen.add(new ArrayList<>());
                kFaecher.add(new ArrayList<>());
                List<String> t = new ArrayList<>(List.of(texte.get(i).split(" ")));
                for (int j = 0; j < t.size(); j++) {
                    if(j%2 == 0){
                        kFaecher.get(i/2).add(t.get(j));
                    }
                    else{
                        kNamen.get(i/2).add(t.get(j));
                    }
                }
            }
            else{
                kLehrer.add(new ArrayList<>(
                        List.of(texte.get(i).split(" "))
                ));
            }
        }
        synchPlan();
    }

    public void synchPlan(){
        //Synchronisiere die Listen mit den eigenen und allen Kursen
        for (int i = 0; i < 11; i++) {
            /*System.out.print(i + " --> ");
            for (String namen : kNamen.get(i)) {
                System.out.print(namen + " - ");
            }
            System.out.println();*/
            for (int j = 0; j < kNamen.get(i).size(); j++) {
                String f = kFaecher.get(i).get(j);
                String n = kNamen.get(i).get(j);
                String l = kLehrer.get(i).get(j);
                //System.out.println(f + " - " + n + " - " + l);
                Kurs k = Kurs.getKurs(f, n, l);
                if(!kurse.contains(k)){
                    kurse.add(k);
                    Kurs.sort(kurse);
                }

                if(kNamen.get(i).size() == 5){
                    k.addStunde(new Stunde(j, i));
                }
                //wenn alle fünf stunden in der Woche belegt sind => Mo, Di, Mi, Do, Fr
                if(kNamen.get(i).size() == 4 && i > 6 && i < 9){
                    int neuerTag = switch (j){
                        case 0 -> 0;
                        case 1 -> 1;
                        case 2 -> 3;
                        case 3 -> 4;
                        default -> -1;
                    };
                    k.addStunde(new Stunde(neuerTag, i));
                }
                //wenn vier stunden in der 8./9. belegt sind => Mo, Di, Do, Fr
                if(i == 6){
                    k.addStunde(new Stunde(2, 6));
                }
                //siebte stunde => Mi
                if(n.contains("/W")){
                    k.addStunde(new Stunde(3, i));
                }
                //WSeminar => Do
                if(n.contains("/P") && i == 10){
                    k.addStunde(new Stunde(3, i));
                }
                //PSeminar in der 11. Stunde => Do
                if(n.contains("/P") && j == 2 && kNamen.get(i).size() == 4){
                    k.addStunde(new Stunde(3, i));
                }
                //an vier Tagen 8./9. (wegen /P) und am dritten Tag PSeminar => Do
                if(i > 8 && kNamen.get(i).size() == 3){
                    int neuerTag = switch (j){
                        case 0 -> 0;
                        case 1 -> 2;
                        case 2 -> 3;
                        default -> -1;
                    };
                    k.addStunde(new Stunde(neuerTag, i));
                }
                //an drei Tagen 10./11. => Mo, Mi (Klettern), Do

                if(j == 0 && n.contains("smw") && kNamen.get(i).size() == 2 && i > 8){
                    k.addStunde(new Stunde(0, i));
                }
                else if(j == 0 && kNamen.get(i).size() == 2 && i > 8){
                    k.addStunde(new Stunde(2, i));
                }
            }
        }
    }

    public void resynchPlan(){
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < kNamen.get(i).size(); j++) {
                String f = kFaecher.get(i).get(j);
                String n = kNamen.get(i).get(j);
                String l = kLehrer.get(i).get(j);
                Kurs k = Kurs.getKurs(f, n, l);
                if(j == kNamen.get(i).size() - 1 && kNamen.get(i).size() > 1
                        && (kNamen.get(i).get(j-1).contains("/P")
                            || kNamen.get(i).get(j-1).contains("/W"))){
                    /*
                    System.out.println("size: " + kNamen.get(i).size());
                    System.out.print(i + " | " + j + " - > ");
                    System.out.println(f + " - " + n + " - " + l);
                    */
                    Kurs.getKurs(f, n, l).addStunde(new Stunde(4, i));
                }
                else if(n.equals("QWU") && k.getStunden().size() < 2){
                    k.addStunde(new Stunde(1, i));
                }
                if(n.contains("/P1") && i == 10
                            && k.getStunden().size() == 1
                            && k.getStunden().get(0).tag == 3){
                    System.out.println("fall 3");
                    System.out.print(i + " | " + j + " - > ");
                    System.out.println(f + " - " + n + " - " + l);
                    k.addStunde(new Stunde(k.getStunden().get(0).tag, 8));
                }
                if(n.contains("WR/P1") && k.getStunden().size() == 1 &&  j == kNamen.get(i).size() - 1){
                    k.addStunde(new Stunde(4, i));
                }
                if(i > 6 && (n.equals("1smw7") || n.equals("5swm3") || n.equals("psy/ps2"))){
                    k.addStunde(new Stunde(2, i));
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void NameAusgeben(){
        System.out.println("Name: " + name);
    }

    public void KurseAusgeben(){
        System.out.println("Kurse: ");
        for (Kurs k : kurse) {
            System.out.println(k);
        }
    }

    public List<Kurs> getKurse() {
        return kurse;
    }

    public boolean inKurs(String fach, String name, String lehrer){
        boolean b = false;
        for (Kurs k : kurse) {
            if(k.getLehrer().equals(lehrer) && k.getFach().equals(fach) && k.getName().equals(name)){
                b = true;
                break;
            }
        }
        //System.out.println("schueler " + format(this.name, 20) + " ist " + format((b?"":" nicht "), 7) + " im Kurs " + name);
        return b;
    }

    public static String formatTextLine(String text){
        String ausgabe =  text
                .replace(" c ", " C ")
                .substring(6)
                .replace("| ", " ")
                .replace("  ", " ")
                .replace("g9e0", "geo")
                .replace("QAWU", "QWU")
                .replace("1m?", "1m7")
                .replace("Kw", "Ku/")
                .replace("9", "g")
                .replace("14", "1d")
                .replace(" 6", " C")
                .replace("/w", "/W")
                .replace("I/", "/")
                .replace("Cc", "C")
                .replace("Smwi", "Smw")
                .replace("Wi", "W1")
                .replace("Kuw", "Ku")
                .replace("WRI", "WR")
                .replace("Ww", "W")
                .replace("Kk", "k")
                .replace("kK", "k")
                .replace("1K3", "1k3")
                .replace("Twr", "wr")
                .replace("PA", "P1")
                .replace("/p", "/P")
                .replace("/0", "/O")
                .replace("KuP", "Ku/P")
                .replace("ge0", "geo")
                .replace("15-11", "1s-t1")
                .replace("MIW", "M/W")
                .replace("_psylps2", "psy/ps2")
                .replace("Pi", "P1")
                .replace("/Ps", "/ps")
                .replace("1smw7?", "1smw7")
                .replace("7?", "7")
                .replace("?", "")
                .replace("iwr", "1wr")
                .replace("185", "1e5")
                .replace("TurF", "Tuf")
                .replace("OO", "OE")
                .replace("]", "")
                .replace("voS", "vo5")
                .replace("GW1", "G/W1")
                .replace("Tuf", "TuF")
                .replace("Tur", "TuF")
                .replace("wS", "w5")
                .replace("//", "/")
                .replace("1smw ", "1smw7 ")
                .replace("/OE", "/O")
                .replace("HAl", "HA")
                .replace("HE)", "HE")
                .replace("nSm", "n Sm")
                ;
        if(ausgabe.startsWith(" ")){
            ausgabe = ausgabe.substring(1, ausgabe.length()-1);
        }
        if(ausgabe.startsWith("6 ")){
            ausgabe = "C " + ausgabe.substring(2);
        }
        /*
        for (int i = 1; i < ausgabe.length()-1; i++) {
            boolean b = isCap(ausgabe.charAt(i-1)) &&
                    isCap(ausgabe.charAt(i)) &&
                    !isCap(ausgabe.charAt(i+1)) &&
                            isBuchstabe(ausgabe.charAt(i+1));
            if(b){
                ausgabe = ausgabe.substring(0, i+1) + " " + ausgabe.substring(i+1);
            }
            //System.out.println(" --> " + ausgabe);
        }
        */
        return ausgabe;
    }

}
