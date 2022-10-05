import DateiVerarbeitung.DateiVerarbeitung;
import models.Kurs;
import models.Schueler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Strings.Format.format;

public class ManagementOverAll {
    public List<Schueler> schueler = new ArrayList<>();

    public ManagementOverAll(){
        try {
            DateiVerarbeitung dv = new DateiVerarbeitung("input.txt");
            List<List<String>> listen = dv.getListen();
            schueler = new ArrayList<>();
            for (int i = 0; i < listen.size(); i++) {
                schueler.add(new Schueler(listen.get(i)));
            }
            for (int i = 0; i < listen.size(); i++) {
                schueler.get(i).resynchPlan();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<String> getNamen(List<Schueler> schueler){
        return schueler.stream().map(Schueler::getName).toList();
    }

    private Schueler getSchueler(String name){
        for (Schueler s : schueler) {
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }

    private boolean KursExistiert(Kurs k){
        return false;
    }

    private void ListeNummeriertAusgeben(List<String> liste){
        for (int i = 0; i < liste.size(); i++) {
            System.out.print(format((i+1)+ "", 3) + ".: ");
            String s = liste.get(i);
            System.out.println(s);
        }
    }

    public Kurs getGrundkursVon(String schueler){
        Schueler s = getSchueler(schueler);
        if(s == null){
            System.out.println("Der Schüler '" + schueler + "' existiert nicht.");
            return null;
        }
        Kurs k = s.getKurse().get(s.getKurse().stream().map(Kurs::getFach).toList().indexOf("D"));
        System.out.println("Der Grundkurs des Schülers '" + schueler + "' " + k.getName() + ": ");
        return k;
    }

    public void AlleSchülerAusgeben(){
        ListeNummeriertAusgeben(getNamen(schueler));
    }

    public void KurslisteVon(Kurs k){
        String f = k.getFach();
        String n = k.getName();
        String l = k.getLehrer();
        List<Schueler> copyOfSchueler = new ArrayList<>(schueler);
        copyOfSchueler.removeIf(i->!i.inKurs(f, n, l));
        //System.out.println("insgesamt schueler: " + copyOfSchueler.size());
        ListeNummeriertAusgeben(getNamen(copyOfSchueler));
    }


}
