package differ.factors;

import differ.terms.Term;

import java.math.BigInteger;
import java.util.ArrayList;

public class Xfactor extends Factor {
    public Xfactor(BigInteger idx) {
        super(idx);
    }

    public Xfactor() {
        super();
    }

    //必须传入合法的，在此不做合法性检查
    public Xfactor(String str) {
        super(str);
    }

    public Term diff() {
        ArrayList<BaseFactor> fl = new ArrayList<>();
        BigInteger idx = this.getIndex();
        //指数为1和0时无xfactor项
        if (!idx.equals(BigInteger.ZERO) && !idx.equals(BigInteger.ONE)) {
            fl.add(new Xfactor(this.getIndex().subtract(BigInteger.ONE)));
        }
        return new Term(this.getIndex(), fl);
    }

    public boolean multMergeable(BaseFactor other) {
        if (this.getClass() == other.getClass() &&
                getIndex().equals(((Factor) other).getIndex())) {
            return true;
        }
        return false;
    }

    public String toString() {
        if (this.getIndex().equals(BigInteger.ONE)) {
            return "x";
        } else if (this.getIndex().equals(BigInteger.ZERO)) {
            return "1";
        } else {
            return "x^" + this.getIndex().toString();
        }
    }

    public Factor merge(Factor f) throws ClassCastException {
        if (this.getClass() == f.getClass()) {
            return new Xfactor(basedMerge(f));
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
        Xfactor other = (Xfactor) otherObject;
        return getIndex().equals(other.getIndex());
    }

}
