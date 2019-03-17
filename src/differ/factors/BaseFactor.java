package differ.factors;

import differ.polys.Poly;

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
            return new Xfactor(str);
        }
    }
}
