package com.example.analysisxml.util;

public class ChineseUtil {
    /**
     * 判断一个字符是否是中文
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        ) return true;
        return false;
    }

    /**
     * 判断一个字符串是否包含中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c))
                return true;
        }
        return false;
    }
}
