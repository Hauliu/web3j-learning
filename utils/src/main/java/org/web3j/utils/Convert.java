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

/**
 * Ethereum unit conversion functions.
 *
 *  以太坊单位转换处理
 *
 *
 */
public final class Convert {
    private Convert() {}
    /**
     *  将number从wei 转换成以 Unit为单位 (去掉0）
     */
    public static BigDecimal fromWei(String number, Unit unit) {
        return fromWei(new BigDecimal(number), unit);
    }

    public static BigDecimal fromWei(BigDecimal number, Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    /**
     *  将number 转换成以 wei为单位 (补0）
     */
    public static BigDecimal toWei(String number, Unit unit) {
        return toWei(new BigDecimal(number), unit);
    }

    public static BigDecimal toWei(BigDecimal number, Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }


    /**
     * 单位枚举，eth最小单位 wei，
     * <p>1 ether=10^18 wei</p>
     * <p>1 ether=10^9 gwei</p>
     */
    public enum Unit {
        WEI("wei", 0),
        KWEI("kwei", 3),
        MWEI("mwei", 6),
        GWEI("gwei", 9),
        SZABO("szabo", 12),
        FINNEY("finney", 15),
        ETHER("ether", 18),
        KETHER("kether", 21),
        METHER("mether", 24),
        GETHER("gether", 27);

        private String name;
        private BigDecimal weiFactor;

        Unit(String name, int factor) {
            this.name = name;
            this.weiFactor = BigDecimal.TEN.pow(factor);
        }


        /**
         * 返回wei值
         * <p>Convert.Unit.ETHER.getWeiFactor()  : 1000000000000000000</p>
         * <p>Convert.Unit.GWEI.getWeiFactor()  : 1000000000</p>
         * <p>Convert.Unit.WEI.getWeiFactor()  : 1</p>
         * <p>Convert.Unit.fromString("ether").getWeiFactor() : 1000000000000000000</p>
         */
        public BigDecimal getWeiFactor() {
            return weiFactor;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * 根据name 获取 Unit
         * @param name  Unit.name
         */
        public static Unit fromString(String name) {
            if (name != null) {
                for (Unit unit : Unit.values()) {
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }
            return Unit.valueOf(name);
        }
    }
}
