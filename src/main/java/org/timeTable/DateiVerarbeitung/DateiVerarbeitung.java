package org.timeTable.DateiVerarbeitung;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DateiVerarbeitung {
    private List<List<String>> listen;

    public DateiVerarbeitung(String inputfile) throws IOException {
        String txt = getTextOfTxtFile(inputfile);
        List<String> ls = new FileToStringList(txt).getList();
        listen = listenProSchüler(ls);
        /*
        for (List<String> l : listen) {
            for (String s : l) {
                System.out.println(s);
            }
            System.out.println("------");
        }
        */
    }
    public List<List<String>> getListen(){
        return listen;
    }

    public static List<List<String>> listenProSchüler (List<String> liste){
        List<List<String>> ausgabe = new ArrayList<>();
        for (String s : liste) {
            if(s.contains("Stand: 23.09.2022")){
                ausgabe.add(new ArrayList<>());
            }
            ausgabe.get(ausgabe.size()-1).add(s);
        }
        return ausgabe;
    }
    public static String getTextOfTxtFile(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        String everything = sb.toString();
        br.close();
        return everything;
    }
}
