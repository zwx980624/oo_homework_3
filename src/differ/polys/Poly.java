package differ.polys;

import differ.factors.BaseFactor;
import differ.terms.Term;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poly extends BaseFactor {
    private ArrayList<Term> termList;

    public Poly(ArrayList<Term> tl) {
        termList = new ArrayList<Term>();
        for (Term t : tl) {
            int pos = findSameTerm(termList, t);
            if (pos != -1) {
                termList.set(pos, termList.get(pos).merge(t));
                if (termList.get(pos).getCoef().equals(BigInteger.ZERO)) {
                    termList.remove(pos);
                }
            } else {
                if (!t.getCoef().equals(BigInteger.ZERO)) {
                    termList.add(t);
                }
            }
        }
    }

    // 可输入非法字符串，抛出异常
    public Poly(String str1) throws NumberFormatException {
        String str = str1;
        termList = new ArrayList<>();
        //先去掉空格干扰，并检测非法字符
        if (!checkSpaceLegal(str)) {
            throw new NumberFormatException();
        }
        str = str.replaceAll("[ \\t]+", "");
        if (str.equals("")) {
            throw new NumberFormatException();
        }
        // 首项之前保证有符号，统一好处理
        if (str.charAt(0) != '+' && str.charAt(0) != '-') {
            str = "+" + str;
        }
        if (!checkLegalPoly(str)) {
            throw new NumberFormatException();
        }

        str = str.replaceAll("(\\+\\+)|(\\-\\-)", "\\+");
        str = str.replaceAll("(\\+\\-)|(\\-\\+)", "\\-");
        str = str.replaceAll("(\\+\\+)|(\\-\\-)", "\\+");
        str = str.replaceAll("(\\+\\-)|(\\-\\+)", "\\-");
        str = str.replaceAll("\\-", "\\+\\-");
        str = str.replaceAll("\\^\\+", "\\^");
        if (str.charAt(0) == '+') {
            str = str.replaceFirst("\\+", "");
        }
        // 从左到右处理，找到每一个项
        while (!str.equals("")) {
            if (str.charAt(0) == '+') {
                str = str.substring(1);
            }
            String temp = getFirstTerm(str);
            str = str.substring(temp.length());
            Term t = new Term(temp);
            int pos = findSameTerm(termList, t);
            if (pos != -1) {
                termList.set(pos, termList.get(pos).merge(t));
                if (termList.get(pos).getCoef().equals(BigInteger.ZERO)) {
                    termList.remove(pos);
                }
            } else {
                if (!t.getCoef().equals(BigInteger.ZERO)) {
                    termList.add(t);
                }
            }
        }
    }

    private String getFirstTerm(String str1) {
        String str = str1;
        int stk = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '(') {
                stk++;
            } else if (c == ')') {
                stk--;
            } else if (stk == 0 && c == '+') {
                return str.substring(0, i);
            }
        }
        return str;
    }

    private boolean checkLegalPoly(String str1) {
        if (str1.equals("")) {
            return false;
        }
        String str = str1;
        while (!str.equals("")) {
            String temp = getOriginLegalTerm(str);
            if (!temp.equals("")) {
                str = str.substring(temp.length());
            } else {
                int stk = 0;
                int scflag = 0;
                int begin = 0;
                int end = 0;
                int flag = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '(') {
                        stk++;
                        if (stk == 1) {
                            begin = i;
                            if (i > 0 && (str.charAt(i - 1) == 'n' ||
                                    str.charAt(i - 1) == 's')) {
                                scflag = 1;
                            }
                        }
                    } else if (str.charAt(i) == ')') {
                        stk--;
                        if (stk == 0) {
                            end = i;
                            if (scflag == 0) { //检查括号内是不是表达式
                                String newstr = str.substring(begin + 1, end);
                                if (!newstr.equals("") &&
                                        (newstr.charAt(0) != '+' &&
                                                newstr.charAt(0) != '-')) {
                                    newstr = "+" + newstr;
                                }
                                if (checkLegalPoly(newstr)) { //把括号去掉
                                    str = str.substring(0, begin) + "+1" +
                                            str.substring(end + 1);
                                    flag = 1;
                                    break;
                                } else {
                                    return false;
                                } //检查括号内是不是因子 //把sin(f(x))换成x
                            } else if (checkLegalFactor(str.substring(
                                    begin + 1, end))) {
                                str = str.substring(0, begin - 3) +
                                        "x" + str.substring(end + 1);
                                flag = 1;
                                break;
                            } else {
                                return false;
                            }
                        }
                    }
                }
                if (flag == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkLegalFactor(String str1) {
        if (str1.equals("")) {
            return false;
        }
        String str = str1;
        if (str.charAt(0) == '(') {
            int stk = 0;
            int i;
            for (i = 0; i < str.length(); ++i) {
                if (str.charAt(i) == '(') {
                    stk++;
                } else if (str.charAt(i) == ')') {
                    stk--;
                    if (stk == 0) {
                        break;
                    }
                }
            }
            if (i == str.length() - 1) //只能是表达式因子
            {
                String newstr = str.substring(1, str.length() - 1);
                if (!newstr.equals("") && (newstr.charAt(0) != '+' &&
                        newstr.charAt(0) != '-')) {
                    newstr = "+" + newstr;
                }
                return checkLegalPoly(newstr);
            }
        }
        //是其他因子
        String numStr = "[\\+\\-]?\\d+";
        String powerFunStr = "x(\\^" + numStr + ")?";
        String combineStr = "(" + numStr + ")|(" + powerFunStr + ")";
        Pattern r = Pattern.compile(combineStr);
        Matcher m = r.matcher(str);
        if (m.matches()) {
            return true;
        } else //sin() cos()
        {
            String pattern = "(sin\\(.*\\))|(cos\\(.*\\))";
            r = Pattern.compile(pattern);
            m = r.matcher(str);
            if (m.matches()) {
                return checkLegalFactor(str.substring(4, str.length() - 1));
            } else {
                return false;
            }
        }
    }

    public ArrayList<Term> getTermList() {
        return (ArrayList<Term>) termList.clone();
    }

    public String getOriginLegalTerm(String str) {
        String numStr = "[\\+\\-]?\\d+";
        String powerFunStr = "x(\\^" + numStr + ")?";
        String sinFunStr = "sin\\(x\\)(\\^" + numStr + ")?";
        String cosFunStr = "cos\\(x\\)(\\^" + numStr + ")?";
        String factorStr = "(" + numStr + ")|(" + powerFunStr + ")|(" +
                sinFunStr + ")|(" + cosFunStr + ")";
        String termStr = "^[\\+\\-]{1,2}(\\d+\\*)?((" + factorStr +
                ")\\*)*+(" + factorStr + ")";
        //System.out.println(termStr);
        Pattern r = Pattern.compile(termStr);
        Matcher m = r.matcher(str);
        if (m.find()) {
            //System.out.println("First");
            return m.group(0);
        } else {
            //System.out.println("Second");
            return "";
        }
    }

    private boolean checkSpaceLegal(String str1) {
        String str = str1;
        //首先看数字间有无空格
        String spaceInNum = "(\\d[ \\t]+\\d)" +
                "|([\\*\\^][ \\t]*[\\+\\-][ \\t]+\\d)" +
                "|(([\\+\\-][ \\t]*){2}[\\+\\-][ \\t]+\\d)";
        Pattern r = Pattern.compile(spaceInNum);
        Matcher m = r.matcher(str);
        if (m.find()) {
            return false;
        }
        //去掉sin cos后看看有无其余非法字符
        str = str.replaceAll("sin", "");
        str = str.replaceAll("cos", "");
        r = Pattern.compile("[^x \\t\\d\\^\\+\\-\\*\\(\\)]");
        m = r.matcher(str);
        if (m.find()) {
            return false;
        }

        return true;
    }

    private int findSameTerm(ArrayList<Term> tl, Term t) {
        for (int i = 0; i < tl.size(); ++i) {
            if (tl.get(i).addMergeable(t)) {
                return i;
            }
        }
        return -1;
    }

    public Term diff() {
        ArrayList<Term> tl = new ArrayList<>();
        for (Term t : termList) {
            tl.addAll(t.diff().termList);
        }
        Poly p = new Poly(tl);
        ArrayList<BaseFactor> fl = new ArrayList<>();
        fl.add(p);
        return new Term(BigInteger.ONE, fl);
    }

    public boolean multMergeable(BaseFactor other) {
        return false;
    }

    public String toString() {
        ArrayList<String> termStrs = new ArrayList<>();
        StringBuilder ret = new StringBuilder();
        int ppos = -1;
        for (int i = 0; i < termList.size(); i++) {
            String temp = termList.get(i).toString();
            //System.out.println(temp);
            if (!temp.equals("0")) {
                if (temp.charAt(0) != '-') {
                    temp = "+" + temp;
                    if (ppos == -1) {
                        ppos = termStrs.size();
                    }
                }
                termStrs.add(temp);
            }
        }
        if (ppos != -1) {
            //String temp = termStrs.get(ppos);
            //termStrs.remove(ppos);
            //termStrs.add(0,temp);
            Collections.swap(termStrs, 0, ppos);
            termStrs.set(0, termStrs.get(0).replaceFirst("\\+", ""));
        }
        for (String e : termStrs) {
            ret.append(e);
        }
        if (ret.toString().equals("")) {
            return "(0)";
        } else {
            return "(" + ret.toString() + ")";
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
        Poly other = (Poly) otherObject;
        if (this.termList.size() != other.termList.size()) {
            return false;
        }
        ArrayList<Term> templ = other.getTermList();
        for (Term t : termList) {
            boolean findflag = false;
            for (Term tt : templ) {
                if (t.equals(tt)) {
                    findflag = true;
                    templ.remove(tt);
                    break;
                }
            }
            if (!findflag) {
                return false;
            }
        }
        return true;
    }
}