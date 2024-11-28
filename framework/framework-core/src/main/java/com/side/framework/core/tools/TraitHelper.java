package com.side.framework.core.tools;


import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TraitHelper {

    /**
     * 前256个质数集合
     */
    private final static List<Integer> PRIME_NUMBERS = getPrimeNumberSortList256();

    private TraitHelper() {
    }

    /**
     * 忽略集合顺序的情况下获取参数的特征
     * 计算方式：
     * 递归获取基础数据类型的hashCode，然后拼接成字符串，最后计算字符串的hashCode
     * 为了防止碰撞，需要对参数进行排序，且来源于同一对象的参数，与质数运算
     *
     * @param args
     * @return
     */
    public static String getArgsTrait(Object... args) {
        if (Objects.isNull(args)) {
            throw new IllegalArgumentException("args is null");
        }
        return String.valueOf(Objects.hash(getArgsHashList(args).toArray()));
    }

    private static List<Integer> getArgsHashList(Object... args) {
        List<Integer> hashList = Collections.synchronizedList(new ArrayList<>());
        int index = 0;
        for (Object obj : args) {
            if (canConvertToPrimitive(obj)) {
                hashList.add(getPrimitiveHash(obj, index));
                continue;
            }
            if (canConvertToCollection(obj)) {
                Collection<?> collection = (Collection<?>) obj;
                hashList.addAll(getArgsHashList(collection.toArray()));
                continue;
            }
            //obj转为map
            HashMap hashMap = MapHelper.convertToMap(obj, HashMap.class);
            for (Object entry : hashMap.entrySet()) {
                hashList.add(PRIME_NUMBERS.get(index) * getEntryPrimitiveHash(entry));
                if ((int) index == 255) {
                    index = 0;
                }
            }
        }
        //排序
        Collections.sort(hashList);
        return hashList;
    }

    /**
     * 获取原始数据类型的特征hash
     *
     * @param obj
     * @param index
     * @return
     */
    private static int getPrimitiveHash(Object obj, Integer index) {
        if (Objects.isNull(obj)) {
            return index++;
        }
        if (canConvertToPrimitive(obj.toString())) {
            return Objects.hash(obj);
        }
        throw new IllegalArgumentException("仅支持基础数据类型");
    }

    /**
     * 获取map的entry数据类型的特征hash
     *
     * @param entry
     * @return
     */
    private static int getEntryPrimitiveHash(Object entry) {
        if (entry instanceof Map.Entry<?, ?> entryMap) {
            if (!canConvertToPrimitive(entryMap.getValue())) {
                //value不是基础数据类型
                return Integer.parseInt(getArgsTrait(entryMap.getValue()));
            }
            return Objects.hash(entryMap.getKey(), entryMap.getValue());
        } else {
            throw new IllegalArgumentException("非法参数");
        }
    }


    /**
     * 是否支持基础类型hash运算
     *
     * @param element
     * @return
     */
    public static boolean canConvertToPrimitive(Object element) {
        if (Objects.isNull(element)) {
            return Boolean.TRUE;
        }
        String input = element.toString();
        try {
            return canParseBoolean(input) || isNumber(input);
        } catch (Exception e) {
            // 如果抛出异常，则表示无法转换
            return Boolean.FALSE;
        }
    }


    /**
     * 是否可以转成集合
     *
     * @param obj
     * @return
     */
    public static boolean canConvertToCollection(Object obj) {
        if (obj instanceof Collection) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否可以转成boolean
     *
     * @param str
     * @return
     */
    private static boolean canParseBoolean(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }

    /**
     * 获取前256位size位数的质数集合
     *
     * @return
     */
    private static List<Integer> getPrimeNumberSortList256() {
        List<Integer> primeNumberList = new ArrayList<>();
        for (int i = 2; true; i++) {
            boolean isPrime = true;
            for (int j = 0; j < primeNumberList.size(); j++) {
                if (i % primeNumberList.get(j) == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primeNumberList.add(i);
                if (primeNumberList.size() == 256) {
                    break;
                }
            }
        }
        return primeNumberList;
    }

    /**
     * 判断是否为数字
     * 参考hutool的NumberUtil.isNumber方法
     *
     * @param str
     * @return
     */
    public static boolean isNumber(CharSequence str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        char[] chars = str.toString().toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (false == foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return false == allowSigns && foundDigit;
    }
}
