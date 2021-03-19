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

package network.nerve;

import network.nerve.core.model.StringUtils;
import network.nerve.core.parse.I18nUtils;

/**
 * @author: Loki
 * @date: 2020/11/6
 */
public class NerveSDKBootStrap {

    private static final String LANGUAGE = "en";
    private static final String LANGUAGE_PATH = "languages";

    /**
     * NULS-SDK工具初始化
     * 设置对接的链的ID和钱包NULS-SDK-Provider模块的url访问地址
     *
     * @param chainId 链ID
     * @param httpUrl 钱包url访问地址(ip + port)
     */
//    public static void init(int chainId, String addressPrefix, String httpUrl) {
//        initChainId(chainId);
//        if (httpUrl != null && !httpUrl.endsWith("/")) {
//            httpUrl += "/";
//        }
//        if (StringUtils.isNotBlank(httpUrl)) {
//            SDKContext.wallet_url = httpUrl;
//        }
//        SDKContext.addressPrefix = addressPrefix;
//    }

    /**
     * 初始化为 nerve 与 nuls 对接跨链关系的工具包
     * 初始化时必须按照以下方式来使用(默认已配置成主网对主网)
     *  nuls测试网对nerve测试网
     *  nuls主网对nerve主网
     *
     * @param chainId 当前链Id
     * @param nulsChainId nuls网链id
     * @param addressPrefix
     * @param addressPrefixNuls
     * @param httpUrl
     */
    public static void init(int chainId, int nulsChainId, String addressPrefix,String addressPrefixNuls, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.nuls_chain_id = nulsChainId;
        SDKContext.addressPrefix = addressPrefix;
        SDKContext.addressPrefixNuls = addressPrefixNuls;
    }

    /**
     * NULS-SDK工具连接NULS测试网钱包初始化
     * 设置测试网钱包NULS-SDK-Provider模块的url访问地址
     *
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void initTest(String httpUrl) {
        initChainId(5);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.nuls_chain_id = 2;
        SDKContext.addressPrefixNuls = "tNULS";
        SDKContext.addressPrefix = "TNVT";
    }

    /**
     * nuls sdk init
     *
     * @param chainId 运行链的id
     */
    private static void initChainId(int chainId) {
        if (chainId < 1 || chainId > 65535) {
            throw new RuntimeException("[defaultChainId] is invalid");
        }
        SDKContext.main_chain_id = chainId;
        I18nUtils.loadLanguage(NerveSDKBootStrap.class, LANGUAGE_PATH, LANGUAGE);
    }
}
