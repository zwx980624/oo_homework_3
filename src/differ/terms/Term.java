package differ.terms;

import differ.factors.BaseFactor;
import differ.factors.Factor;
import differ.polys.Poly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Term extends BaseFactor {
    private BigInteger coef;
    private ArrayList<BaseFactor> factList;
    //保证只要构造出Term一定是最简，无指数为0因子 且不可能有term因子

    public Term(BigInteger c, ArrayList<BaseFactor> fl) //new一个fl然后合并同类
    {
        coef = c;
        factList = new ArrayList<BaseFactor>();
        ListIterator<BaseFactor> iter = fl.listIterator();
        while (iter.hasNext()) {
            BaseFactor f = iter.next();
            // 判断是不是只有一项的Poly
            if (f instanceof Poly)
            {
                Poly p = (Poly) f;
                if (p.getTermList().size() == 1)
                {
                    iter.add(p.getTermList().get(0));
                    iter.previous();
                    continue;
                }
                else if (p.getTermList().size() == 0)
                {
                    coef = coef.multiply(BigInteger.ZERO);
                    continue;
                }
            }
            // 先判断是不是Term
            if (f instanceof Term) {
                Term t = (Term) f;
                coef = coef.multiply(t.getCoef());
                for (BaseFactor ff : t.getFactList()) {
                    iter.add(ff);
                    iter.previous();
                }
                continue;
            }
            // 若不是Term判断能否与之前的合并
            int pos = findSameFact(factList, f);
            if (pos != -1) {
                // 如果有幸进入这里，一定是Factor类型的，可以放心转换
                Factor ftemp = ((Factor) (factList.get(pos))).merge((Factor) f);
                if (ftemp.getIndex().equals(BigInteger.ZERO)) {
                    factList.remove(pos);
                } else {
                    factList.set(pos, ftemp);
                }
            } else {
                if (f instanceof Poly || f instanceof Factor &&
                        !((Factor) f).getIndex().equals(BigInteger.ZERO)) {
                    factList.add(f);
                }
            }
        }
    }

    //必须传入合法的，在此不做合法性检查
    public Term(String str1) {
        String str = str1;
        coef = BigInteger.ONE;
        factList = new ArrayList<>();
        str = str.replaceAll("\\s+", "");
        String[] factstrs = splitbymult(str);
        // 处理第一非常数项带正负号,去掉第一个符号，并将影响其移入coef中
        Pattern r = Pattern.compile("^[\\-\\+][^\\d]");
        Matcher m = r.matcher(factstrs[0]);
        if (m.find()) {
            if (factstrs[0].charAt(0) == '-') {
                coef = coef.negate();
            }
            factstrs[0] = factstrs[0].substring(1);
        }
        // 遍历构造每一个factor
        for (int i = 0; i < factstrs.length; ++i) {
            //先看是不是常数
            r = Pattern.compile("[\\+\\-]?\\d+");
            m = r.matcher(factstrs[i]);
            if (m.matches()) {
                coef = coef.multiply(new BigInteger(factstrs[i]));
                continue;
            }
            //不是常数时，一定是变量因子
            BaseFactor f = Factor.factorFactory(factstrs[i]); //调用静态工厂方法
            int pos = findSameFact(factList, f);
            if (pos != -1) {
                // 如果有幸进入这里，一定是Factor类型的，可以放心转换
                Factor ftemp = ((Factor) (factList.get(pos))).merge((Factor) f);
                if (ftemp.getIndex().equals(BigInteger.ZERO)) {
                    factList.remove(pos);
                } else {
                    factList.set(pos, ftemp);
                }
            } else {
                if (f instanceof Poly || f instanceof Factor &&
                        !((Factor) f).getIndex().equals(BigInteger.ZERO)) {
                    factList.add(f);
                }
            }
        }
    }

    private String[] splitbymult(String str) {
        ArrayList<String> sl = new ArrayList<>();
        int begin = 0;
        int stk = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                stk++;
            } else if (str.charAt(i) == ')') {
                stk--;
            } else if (str.charAt(i) == '*' && stk == 0) {
                sl.add(str.substring(begin, i));
                begin = i + 1;
            }
        }
        sl.add(str.substring(begin));
        return (sl.toArray(new String[0]));
    }

    public BigInteger getCoef() {
        return coef;
    }

    public ArrayList<BaseFactor> getFactList() {
        return (ArrayList<BaseFactor>) factList.clone();
    }

    public Poly diff() {
        ArrayList<Term> tl = new ArrayList<>();
        for (int i = 0; i < factList.size(); i++) {
            ArrayList<BaseFactor> fl = (ArrayList<BaseFactor>) factList.clone();
            BigInteger c = coef;
            fl.remove(i);
            Term temp = (Term) (factList.get(i).diff());
            c = c.multiply(temp.coef);
            fl.addAll(temp.factList);
            tl.add(new Term(c, fl));
        }
        return new Poly(tl);
    }

    private int findSameFact(ArrayList<BaseFactor> fl, BaseFactor f) {
        for (int i = 0; i < fl.size(); i++) {
            if (fl.get(i).multMergeable(f)) {
                return i;
            }
        }
        return -1;
    }

    public boolean multMergeable(BaseFactor other) {
        return false;
    }

    public Term merge(Term t) throws ClassCastException {
        if (this.addMergeable(t)) {
            return new Term(coef.add(t.coef), factList);
        } else {
            throw new ClassCastException("two term are not Mergeable");
        }
    }

    public boolean addMergeable(Term t) {
        if (this.factList.size() != t.factList.size()) {
            return false;
        }
        ArrayList<BaseFactor> templ =
                (ArrayList<BaseFactor>) t.getFactList().clone();
        for (BaseFactor f : factList) {
            boolean findflag = false;
            for (BaseFactor tf : templ) {
                if (f.equals(tf)) {
                    findflag = true;
                    templ.remove(tf);
                    break;
                }
            }
            if (!findflag) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        String ret = "";
        if (coef.equals(BigInteger.ZERO)) {
            return "0";
        } else {
            for (BaseFactor f : factList) {
                ret = ret + f.toString() + "*";
            }
            if (coef.equals(new BigInteger("-1"))) {
                if (ret.equals("")) {
                    ret = "-1";
                } else {
                    ret = "-" + ret;
                    ret = ret.substring(0, ret.length() - 1); //去掉结尾乘号
                }
            } else if (coef.equals(BigInteger.ONE)) {
                if (ret.equals("")) {
                    ret = "1";
                } else {
                    ret = ret.substring(0, ret.length() - 1); //去掉结尾乘号
                }
            } else {
                if (ret.equals("")) {
                    ret = coef.toString();
                } else {
                    ret = coef.toString() + "*" + ret;
                    ret = ret.substring(0, ret.length() - 1); //去掉结尾乘号
                }
            }
        }
        return ret;
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
        Term other = (Term) otherObject;
        return coef.equals(other.coef) && addMergeable(other);
    }
}
