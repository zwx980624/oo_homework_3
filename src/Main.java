import differ.polys.Poly;
import differ.terms.Term;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            String str = in.nextLine();
            Poly poly = new Poly(str);
            //System.out.println(poly);
            Term term = poly.diff();
            System.out.println(term);
        } catch (NumberFormatException | NoSuchElementException e) {
            System.out.println("WRONG FORMAT!");
        }
    }
}
