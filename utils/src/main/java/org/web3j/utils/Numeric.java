/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.web3j.exceptions.MessageDecodingException;
import org.web3j.exceptions.MessageEncodingException;

/**
 * Message codec functions.
 *
 * 数值处理
 *
 * <p>Implementation as per https://github.com/ethereum/wiki/wiki/JSON-RPC#hex-value-encoding
 */
public final class Numeric {

    //hex串前缀
    private static final String HEX_PREFIX = "0x";
    //hex 字符数组
    private static final char[] HEX_CHAR_MAP = "0123456789abcdef".toCharArray();

    private Numeric() {}


    /**
     * 编码转换成16进制 带前缀0x
     * <p>16  得到 0x10</p>
     */
    public static String encodeQuantity(BigInteger value) {
        if (value.signum() != -1) {
            return HEX_PREFIX + value.toString(16);
        } else {
            throw new MessageEncodingException("Negative values are not supported");
        }
    }

    /**
     * 16进制数据 解码转换
     *
     */
    public static BigInteger decodeQuantity(String value) {
        //是否为long类型
        if (isLongValue(value)) {
            return BigInteger.valueOf(Long.parseLong(value));
        }
        // hex 串验证 0x开头
        if (!isValidHexQuantity(value)) {
            throw new MessageDecodingException("Value must be in format 0x[1-9]+[0-9]* or 0x0");
        }
        try {
            //去掉前0x开头
            return new BigInteger(value.substring(2), 16);
        } catch (NumberFormatException e) {
            throw new MessageDecodingException("Negative ", e);
        }
    }
    //是否为long值
    private static boolean isLongValue(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidHexQuantity(String value) {
        if (value == null) {
            return false;
        }

        if (value.length() < 3) {
            return false;
        }
        //0x开头
        if (!value.startsWith(HEX_PREFIX)) {
            return false;
        }

        // If TestRpc resolves the following issue, we can reinstate this code
        // https://github.com/ethereumjs/testrpc/issues/220
        // if (value.length() > 3 && value.charAt(2) == '0') {
        //    return false;
        // }

        return true;
    }
    //去掉 0x 前缀
    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }
    //补充 0x 前缀
    public static String prependHexPrefix(String input) {
        if (!containsHexPrefix(input)) {
            return HEX_PREFIX + input;
        } else {
            return input;
        }
    }
    //验证是否包含 0x 前缀
    public static boolean containsHexPrefix(String input) {
        return !Strings.isEmpty(input)
                && input.length() > 1
                && input.charAt(0) == '0'
                && input.charAt(1) == 'x';
    }
    //byte数组转换BigInteger（截取）
    public static BigInteger toBigInt(byte[] value, int offset, int length) {
        return toBigInt((Arrays.copyOfRange(value, offset, offset + length)));
    }
    //byte数组转换BigInteger
    public static BigInteger toBigInt(byte[] value) {
        //正数
        return new BigInteger(1, value);
    }
    //hex转BigInteger
    public static BigInteger toBigInt(String hexValue) {
        String cleanValue = cleanHexPrefix(hexValue);
        return toBigIntNoPrefix(cleanValue);
    }

    //hex（无前缀 0x）转BigInteger
    public static BigInteger toBigIntNoPrefix(String hexValue) {
        return new BigInteger(hexValue, 16);
    }
    //转hex（含前缀 0x）
    public static String toHexStringWithPrefix(BigInteger value) {
        return HEX_PREFIX + value.toString(16);
    }
    //转hex（无前缀）
    public static String toHexStringNoPrefix(BigInteger value) {
        return value.toString(16);
    }
    //byte数组 转hex（无前缀 0x）
    public static String toHexStringNoPrefix(byte[] input) {
        return toHexString(input, 0, input.length, false);
    }
    //补0转hex（含前缀 0x）
    public static String toHexStringWithPrefixZeroPadded(BigInteger value, int size) {
        return toHexStringZeroPadded(value, size, true);
    }
    //安全转换hex
    public static String toHexStringWithPrefixSafe(BigInteger value) {
        String result = toHexStringNoPrefix(value);
        if (result.length() < 2) {
            result = Strings.zeros(1) + result;
        }
        return HEX_PREFIX + result;
    }
    //补0转hex（无前缀 0x）
    public static String toHexStringNoPrefixZeroPadded(BigInteger value, int size) {
        return toHexStringZeroPadded(value, size, false);
    }

    /**
     * 补0
     * @param value  值
     * @param size 长度
     * @param withPrefix 是否带前缀 0x 。true：是
     */
    private static String toHexStringZeroPadded(BigInteger value, int size, boolean withPrefix) {
        String result = toHexStringNoPrefix(value);

        int length = result.length();
        if (length > size) {
            throw new UnsupportedOperationException(
                    "Value " + result + "is larger then length " + size);
        } else if (value.signum() < 0) {
            throw new UnsupportedOperationException("Value cannot be negative");
        }

        if (length < size) {
            //补0
            result = Strings.zeros(size - length) + result;
        }

        if (withPrefix) {
            return HEX_PREFIX + result;
        } else {
            return result;
        }
    }


    /**
     * byte 数组填充,在value之前补0
     * <p>Numeric.toBytesPadded : [0, 0, 0, 0, 30] </p>
     * @return
     */
    public static byte[] toBytesPadded(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }
    //hex字符串转byte数组
    public static byte[] hexStringToByteArray(String input) {
        //去掉前缀0x
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] =
                    (byte)
                            ((Character.digit(cleanInput.charAt(i), 16) << 4)
                                    + Character.digit(cleanInput.charAt(i + 1), 16));
        }
        return data;
    }

    //截取字节数组转hex
    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        final String output = new String(toHexCharArray(input, offset, length, withPrefix));
        return withPrefix ? new StringBuilder(HEX_PREFIX).append(output).toString() : output;
    }
    //hex数组转换
    private static char[] toHexCharArray(byte[] input, int offset, int length, boolean withPrefix) {
        final char[] output = new char[length << 1];
        for (int i = offset, j = 0; i < length; i++, j++) {
            final int v = input[i] & 0xFF;
            output[j++] = HEX_CHAR_MAP[v >>> 4];
            output[j] = HEX_CHAR_MAP[v & 0x0F];
        }
        return output;
    }
    //数组 转换 hex
    public static String toHexString(byte[] input) {
        return toHexString(input, 0, input.length, true);
    }

    public static byte asByte(int m, int n) {
        return (byte) ((m << 4) | n);
    }

    //是否integer
    public static boolean isIntegerValue(BigDecimal value) {
        return value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0;
    }
}
