import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        List<String> liste1 = new ArrayList<>(
                List.of(new String[]{"ab", "bc", "cd", "de"})
        );
        List<String> liste2 = new ArrayList<>(
                List.of(new String[]{"aa", "bc", "cc", "de"})
        );
        System.out.println("liste1: " + liste1);
        System.out.println("liste2: " + liste2);
        List<String> matches = new ArrayList<>(allMatches(liste1, liste2));
        System.out.println("matches: " + matches);
    }
    public static <E> List<E> allMatches (List<E> list1, List<E> list2){
        List<E> copy = new ArrayList<>(list1);
        copy.removeIf(i->!list2.contains(i));
        return copy;
    }

}
