package differ.factors;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import differ.terms.Term;

public abstract class Factor extends BaseFactor {

    private BigInteger index;

    public Factor(BigInteger idx) {
        index = idx;
    }

    public Factor() {
        index = BigInteger.ZERO;
    }

    public Factor(String str) {
        index = parseString(str);
    }

    public abstract Term diff();

    public abstract Factor merge(Factor f);

    protected BigInteger parseString(String str1) throws NumberFormatException {
        String str = str1;
        str = str.replaceAll("\\s+", "");
        BigInteger idx = BigInteger.ONE;
        Pattern r = Pattern.compile(".*\\^([\\+\\-]?\\d+)");
        Matcher m = r.matcher(str);
        if (m.matches()) {
            idx = new BigInteger(m.group(1));
        }
        if (idx.compareTo(BigInteger.valueOf(10000)) > 0) {
            throw new NumberFormatException();
        }
        return idx;
    }

    //不可变
    protected BigInteger basedMerge(Factor f) {
        return this.getIndex().add(f.getIndex());
    }

    //不可变
    public BigInteger getIndex() {
        return index;
    }
}
