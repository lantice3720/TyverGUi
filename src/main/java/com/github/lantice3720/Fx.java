package com.github.lantice3720;

import java.util.regex.Pattern;

/**
 * 단순한 메서드가 있는 클래스
 */
public class Fx {

    static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     * 입력받은 문자열이 수인지 확인합니다.
     *
     * @param string
     * @return
     */
    public static boolean isNumeric(String string) {
        if (string == null) {
            return false;
        }

        return pattern.matcher(string).matches();
    }
}
