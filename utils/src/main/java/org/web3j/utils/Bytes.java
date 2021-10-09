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

import java.util.Arrays;

/** Byte array utility functions.
 *
 * 字节数组处理
 *
 * */
public class Bytes {

    private Bytes() {}


    /**
     * 去掉指定前面byte
     *
     * @param bytes
     * @param b
     * @return
     */
    public static byte[] trimLeadingBytes(byte[] bytes, byte b) {
        int offset = 0;
        for (; offset < bytes.length - 1; offset++) {
            if (bytes[offset] != b) {
                break;
            }
        }
        // 返回数组范围 [offset,bytes.length)
        return Arrays.copyOfRange(bytes, offset, bytes.length);
    }

    /**
     * 去掉byte数组前面的0
     * <p> 例如 [0,0,3,4,5] ，返回 [3,4,5]</p>
     */
    public static byte[] trimLeadingZeroes(byte[] bytes) {
        return trimLeadingBytes(bytes, (byte) 0);
    }
}
