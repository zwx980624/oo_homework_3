package differ.factors;

import differ.terms.Term;

import java.math.BigInteger;
import java.util.ArrayList;

public class Cosfactor extends Factor {
    private BaseFactor innerFactor;

    public Cosfactor(BigInteger idx, BaseFactor infact) {
        super(idx);
        innerFactor = infact;
    }

    public Cosfactor() {
        super();
    }

    //必须传入合法的，在此不做合法性检查
    public Cosfactor(String str) {
        super(str);
        int i;
        int stk = 0;
        for (i = 3; i < str.length(); ++i) {
            if (str.charAt(i) == '(') {
                stk++;
            } else if (str.charAt(i) == ')') {
                stk--;
                if (stk == 0) {
                    break;
                }
            }
        }
        innerFactor = BaseFactor.factorFactory(str.substring(4, i));
    }

    public Term diff() {
        ArrayList<BaseFactor> fl = new ArrayList<>();
        BigInteger idx = this.getIndex();
        if (!idx.equals(BigInteger.ZERO)) {
            fl.add(new Sinfactor(BigInteger.ONE, innerFactor));
            if (!idx.equals(BigInteger.ONE)) {
                fl.add(new Cosfactor(idx.subtract(BigInteger.ONE),
                        innerFactor));
            }
            fl.add(innerFactor.diff());
        }
        return new Term(idx.negate(), fl);
    }

    public boolean multMergeable(BaseFactor other) {
        if (this.getClass() == other.getClass()) {
            if (innerFactor.equals(((Cosfactor) other).innerFactor)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        if (this.getIndex().equals(BigInteger.ZERO)) {
            return "1";
        } else if (this.getIndex().equals(BigInteger.ONE)) {
            return "cos(" + innerFactor.toString() + ")";
        } else {
            return "cos(" + innerFactor.toString() + ")^" +
                    getIndex().toString();
        }
    }

    public Factor merge(Factor f) throws ClassCastException {
        if (this.getClass() == f.getClass()) {
            return new Cosfactor(basedMerge(f), innerFactor);
        } else {
            throw new ClassCastException("try to mearge " +
                    f.getClass().getName() + " to " + getClass().getName());
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        Cosfactor other = (Cosfactor) otherObject;
        return getIndex().equals(other.getIndex())
                && innerFactor.equals(other.innerFactor);
    }
}
