package org.timeTable.DateiVerarbeitung;

import java.util.ArrayList;
import java.util.List;

import static org.timeTable.Strings.CharIs.isNumber;

public class FileToStringList {
    private List<String> list;

    public FileToStringList(String txt){
        list = new ArrayList<>(List.of(txt.split("\n")));
        list.removeIf(i->i.startsWith(" "));
        list.removeIf(i->i.startsWith("Klasse 11"));
        for (int i = 0; i < list.size(); i++) {
            //System.out.println(list.get(i));
            if(list.get(i).contains(":") && !list.get(i).contains("Stand")){
                int dpI = list.get(i).lastIndexOf(":");
                if(dpI > 5){
                    //System.out.println("dpI: " + dpI);
                    String s = list.get(i);
                    if(isNumber(s.charAt(dpI-2))
                        && isNumber(s.charAt(dpI-1))
                        && isNumber(s.charAt(dpI+1))
                        && isNumber(s.charAt(dpI+2))){
                        //System.out.println("uhrzeit an der falschen stelle");
                        List<String> after = new ArrayList<>(list.subList(i+1, list.size()));
                        list = list.subList(0, i);
                        list.add(s.substring(0, dpI-2));
                        list.add(s.substring(dpI-2));
                        list.addAll(after);
                    }
                }
            }
        }
    }

    public List<String> getList() {
        return list;
    }
}
