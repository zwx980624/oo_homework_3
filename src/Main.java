import differ.polys.Poly;
import differ.terms.Term;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static String rmBrace(String str)
    {
        int stk = 0;
        if (str.charAt(0) == '(')
        {
            int i;
            for (i = 0; i < str.length(); ++i)
            {
                if (str.charAt(i) == '(')
                {
                    stk++;
                }
                else if (str.charAt(i) == ')')
                {
                    stk--;
                    if (stk == 0) {
                        break;
                    }
                }
            }
            if (i == str.length() - 1)
            {
                return str.substring(1, str.length() - 1);
            }
        }
        return str;
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            String str = in.nextLine();
            Poly poly = new Poly(str);
            //System.out.println(poly);
            Term term = poly.diff();
            //System.out.println(term.toString());
            System.out.println(rmBrace(term.toString()));
        } catch (NumberFormatException | NoSuchElementException e) {
            System.out.println("WRONG FORMAT!");
        }
    }
}
