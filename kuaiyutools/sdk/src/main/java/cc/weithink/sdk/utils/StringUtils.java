package cc.weithink.sdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weithink on 2017/12/14.
 */

public class StringUtils {
    /**
     * 判断空字符串
     */
    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 校验文字 只能是数字,英文字母和中文
     */
    public static boolean  isString (String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 给一个字符串返回一个数字
     * @param s
     * @return
     */
    public static int  parsNumber (String s) {
        if (isEmpty(s)) return 0;
        Pattern p = Pattern.compile("^[0-9]+$");
        Matcher m = p.matcher(s);
        return m.matches()?Integer.valueOf(s):0;
    }

    /**
     * 截取字符串中的数字，并转为int
     * @param s
     * @return
     */
    public static int getNumber(String s){
        String pattern = "\\D*(\\d+).*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(s);
        if (m.find()) {
            return Integer.valueOf(m.group(1));
        }else {
            return 0;
        }
    }
}
