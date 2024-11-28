package com.side.framework.core.tools;



import com.side.framework.core.constants.CodeEnum;
import com.side.framework.core.exception.BasicException;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 自定义字符工具类
 *
 * @author yxfl
 * @date 2023/12/14 22
 **/
public class StringHelper {
    static final List<String> CHINESE_NUMBER = List.of("零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百", "千", "万", "亿");
    static final List<Integer> NUMBER = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000, 100000000);

    /**
     * 线程安全的随机数生成器
     */
    private static final SecureRandom random = new SecureRandom();

    /**
     * 小写字母大写字母和数字的字面量
     */
    private static final String DATA_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public StringHelper() {
    }

    private static Map<Character, Character> element_map = new ConcurrentHashMap<Character, Character>() {{
        put('零', '0');
        put('一', '1');
        put('二', '2');
        put('三', '3');
        put('四', '4');
        put('五', '5');
        put('六', '6');
        put('七', '7');
        put('八', '8');
        put('九', '9');
        put('壹', '1');
        put('貳', '2');
        put('參', '3');
        put('肆', '4');
        put('伍', '5');
        put('陸', '6');
        put('柒', '7');
        put('捌', '8');
        put('玖', '9');
    }};

    private static Map<Character, Integer> unit_map = new ConcurrentHashMap<Character, Integer>() {{
        put('个', 1);
        put('十', 10);
        put('百', 100);
        put('千', 1000);
        put('万', 10000);
        put('亿', 100000000);
    }};

    /**
     * 获取UUID的字符串
     *
     * @return java.lang.String
     */
    public static String getUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 从字符串中提取整数字符串，支持汉字数字，不包含负数和小数
     * 示例：输入：123@456qwe789rr一.2中文
     * 输出：123 456 789 1 2
     *
     * @param data
     * @return
     */
    public static List<String> getIgnSymNumCol(String data) {
        if (StringUtils.isBlank(data)) {
            return Collections.emptyList();
        }

        List<String> outs = new ArrayList<>();
        char[] chars = data.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (Character character : chars) {
            if (element_map.keySet().contains(character) || element_map.values().contains(character)) {
                sb.append(element_map.getOrDefault(character, character));
            } else if (StringUtils.isNotEmpty(sb.toString())) {
                outs.add(sb.toString());
                //清除自身内容
                sb.setLength(0);
            }
        }
        if (StringUtils.isNotEmpty(sb.toString())) {
            outs.add(sb.toString());
        }
        return outs;
    }

    /**
     * 标准中文写法转数字
     * eg: 一万三千五百二十一  --> 13521
     * eg:
     *
     * @param data
     * @return
     */
    public static Integer stdChineseConvertInt(String data) {
        if (data == "" || data == null) {
            return null;
        }
        //判断是否十纯中文汉字，不是则直接返回00
        if (!isChineseNum(data)) {
            throw new BasicException(CodeEnum.HELPER_ERROR.getCode(), "输入的字符串不是标准的中文数字");
        }
        List<Integer> collect = data.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(CHINESE_NUMBER::indexOf)
                .map(NUMBER::get)
                .collect(Collectors.toList());
        // 临时变量，存储最终的值
        int temp = 0;
        // 下标，进行到了哪里
        int index = 0;
        // 下一个数字是否是单位数字 大于等10则为单位
        boolean isTemp2 = false;
        for (Integer integer : collect) {
            // 如果下一个是单位，则此时不使用当前数字直接跳过
            if (isTemp2) {
                // 重置判断条件
                isTemp2 = false;
                // 下标正常增加
                index++;
                continue;
            }
            if (integer >= 10) {
                if (index != 0) {
                    if (temp != 0) {
                        temp = temp * integer;
                    } else {
                        temp = integer;
                    }
                } else {
                    temp = integer;
                }
            } else {
                // 如果下一个数字是单位，下一个不是最后一个时，此时临时 变量直接 + 当前位数字 * 下一位单位
                if (index + 2 < collect.size() && collect.get(index + 1) >= 10) {
                    temp += integer * collect.get(index + 1);
                    isTemp2 = true;
                } else {
                    // 否则直接加当前数字即可
                    temp += integer;
                }
            }
            index++;
        }
        return temp;
    }

    /**
     * 判断传入的字符串是否全是汉字数字
     *
     * @param chineseStr 中文
     * @return 是否全是中文数字
     */
    private static boolean isChineseNum(String chineseStr) {
        char[] ch = chineseStr.toCharArray();
        for (char c : ch) {
            if (!CHINESE_NUMBER.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 去除字符串前后空格，如果为空返回null
     *
     * @param str 字符串
     * @return 去除前后空格的字符串，如果为空返回null
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return null;
        }
        return str;
    }

    /**
     * 生成包含大小写字母和数字的混淆字符串
     *
     * @param size 混淆字符串长度
     * @return
     */
    public static String generateConfuseStr(int size) {
        StringBuilder collect = random.ints(size, 0, DATA_STR.length())
                .collect(StringBuilder::new, (stringBuilder, value) -> stringBuilder.append(DATA_STR.charAt(value)), StringBuilder::append);
        return collect.toString();
    }

    /**
     * 替换null字符串为""
     *
     * @param str
     * @return
     */
    public static String replaceNull(String str) {
        return str == null ? "" : str;
    }
}
