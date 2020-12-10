/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.nerve.kit.constant;

import network.nerve.core.crypto.HexUtil;

/**
 * @author: Loki
 * @date: 2020/11/6
 */
public interface Constant {
    /**
     * 提现黑洞公钥
     */
    byte[] WITHDRAWAL_BLACKHOLE_PUBKEY = HexUtil.decode("000000000000000000000000000000000000000000000000000000000000000000");

    /**
     * 手续费汇集分发公钥
     */
    byte[] FEE_PUBKEY = HexUtil.decode("111111111111111111111111111111111111111111111111111111111111111111");

    String PUBLIC_SERVER_URL = "https://scan.nerve.network/api/";
}
