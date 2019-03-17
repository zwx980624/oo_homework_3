package differ.factors;

import differ.polys.Poly;
import differ.terms.Term;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseFactor {
    public abstract BaseFactor diff();

    public abstract boolean multMergeable(BaseFactor other);

    public static BaseFactor factorFactory(String str) {
        if (str.charAt(0) == 's') {
            return new Sinfactor(str);
        } else if (str.charAt(0) == 'c') {
            return new Cosfactor(str);
        } else if (str.charAt(0) == '(') {
            return new Poly(str.substring(1, str.length() - 1));
        } else {
            Pattern r = Pattern.compile("[\\+\\-]?\\d+");
            Matcher m = r.matcher(str);
            if (m.matches()) {
                return new Term(m.group(0));
            }
            return new Xfactor(str);
        }
    }
}
