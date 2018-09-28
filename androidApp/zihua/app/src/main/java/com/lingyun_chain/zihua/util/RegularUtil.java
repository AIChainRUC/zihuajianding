package com.lingyun_chain.zihua.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: anapodoton
 * created on: 2018/3/26 15:36
 * description:正则表达式工具类
 */
public class RegularUtil {
    // 判断邮箱
    public static boolean isEmail(String email) {

        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";

        Pattern p = Pattern.compile(str);

        Matcher m = p.matcher(email);

        return m.matches();
    }

    // 判断日期格式:yyyy-mm-dd
    public static boolean isValidDate(String sDate) {
        String datePattern = "\\d{4}\\d{2}\\d{2}";
        if ((sDate != null)) {
            Pattern pattern = Pattern.compile(datePattern);
            Matcher match = pattern.matcher(sDate);
            if (match.matches()) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }
}
