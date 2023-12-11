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

import network.nerve.base.data.Transaction;
import network.nerve.core.basic.Result;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.dto.WithdrawalTxDto;
import network.nerve.kit.txdata.WithdrawalAdditionalFeeTxData;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 测试网测试
 *
 * @author: Loki
 * @date: 2020/11/4
 */
public class NaboxCollectionTest {

    @Before
    public void before() {
        NerveSDKBootStrap.initTest("http://beta.api.nerve.network/");
    }

    /**
     * 链内普通转账
     * 非NVT资产
     */
    @Test
    public void createTxSimpleTransferOfNonNvt() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String toAddress = "TNVTdTSPMv8w4t1VaZ2ExNJdUUoihz4AocTQb";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        // 2-1, 5-9
        Result<Map> result =
                NerveSDKTool.createTxSimpleTransferOfNonNvt(fromAddress, toAddress, 2, 1, amount,
                        1605064055, "createTxSimpleTransferOfNonNvt");
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");

        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        //广播
        result = NerveSDKTool.broadcast(txHex);
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(JSONUtils.obj2json(result));
    }

    /**
     * 链内普通转账
     * NVT
     */
    @Test
    public void createTxSimpleTransferOfNvt() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String toAddress = "TNVTdTSPMv8w4t1VaZ2ExNJdUUoihz4AocTQb";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NerveSDKTool.createTxSimpleTransferOfNvt(fromAddress, toAddress, amount,
                1605064055, "createTxSimpleTransferOfNvt");
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);

        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));
    }

    /**
     * 跨链转账
     * 非NULS
     */
    @Test
    public void createCrossTxSimpleTransferOfNonNvtNuls() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String toAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String value = "2.5";
        int tokenDecimals = 18;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NerveSDKTool.createCrossTxSimpleTransferOfNonNvtNuls(fromAddress, toAddress, 5, 9, amount,
                1605064055, "createCrossTxSimpleTransferOfNonNvtNuls");
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));
    }

    /**
     * 跨链转账
     * NULS
     */
    @Test
    public void createCrossTxSimpleTransferOfNuls() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String toAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String value = "2.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NerveSDKTool.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount,
                1605064055, "createCrossTxSimpleTransferOfNuls");
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));
    }


    /**
     * 跨链转账
     * NVT
     */
    @Test
    public void createCrossTxSimpleTransferOfNvt() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String toAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String value = "2.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NerveSDKTool.createCrossTxSimpleTransferOfNvt(fromAddress, toAddress, amount,
                1605064055, "createCrossTxSimpleTransferOfNvt");
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));
    }

    /**
     * 提现
     */
    @Test
    public void createWithdrawalTx() throws Exception {
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        WithdrawalTxDto withdrawalTxDto = new WithdrawalTxDto();
        withdrawalTxDto.setFromAddress(fromAddress);
        // 5-1(8) 5-3(6) 5-4(18)
        withdrawalTxDto.setAssetChainId(5);
        withdrawalTxDto.setAssetId(2);
        // 101   102
        withdrawalTxDto.setHeterogeneousChainId(101);
        withdrawalTxDto.setHeterogeneousAddress("0xfa27c84eC062b2fF89EB297C24aaEd366079c684");
        // 提现金额
        String amount = "0.1";
        int decimal = 18; //小数位
        BigDecimal am = new BigDecimal(amount).movePointRight(decimal);
        withdrawalTxDto.setAmount(am.toBigInteger());

        // 手续费
        String feeStr = "20";
        int decimalFee = 8; //小数位
        BigDecimal fee = new BigDecimal(feeStr).movePointRight(decimalFee);
        withdrawalTxDto.setDistributionFee(fee.toBigInteger());

        withdrawalTxDto.setRemark(null);
        Result<Map> result = NerveSDKTool.createWithdrawalTx(withdrawalTxDto);

        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));
    }


    @Test
    public void withdrawalAdditionalFeeTx() throws Exception {
        NerveSDKBootStrap.initMain("https://api.nerve.network/");
        String fromAddress = "NERVEepb65mFoxeXfQN5KgeKxRTfufKekBAn3C";
        String withdrawalTxHash = "1eb250b29fa08ea9f381b83fe746a5284405989bd154b65b56751bd86e3e10d3";
        BigInteger amount = new BigInteger("100000000000");
        String remark = null;
        Result<Map> result = NerveSDKTool.withdrawalAdditionalFeeTx(fromAddress, withdrawalTxHash, amount, 0, remark);

        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "???";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format(WithdrawalAdditionalFeeTxData.class));
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));

    }

    /**
     * 提现
     */
    @Test
    public void createWithdrawalTxOffline() throws Exception {
        String fromAddress = "TNVTdTSPMcyC8e7jz8f6ngX5yTmK6S8CXEGva";
        String prikey = "17c50c6f7f18e7afd37d39f92c1d48054b6b3aa2373a70ecf2d6663eace2a7d6";
        // 声明提现资产ID
        int withdrawalAssetChainId = 5;
        int withdrawalAssetId = 9;
        // 声明异构链网络信息
        int heterogeneousChainId = 103;
        // 声明提现接收地址
        String toAddress = "0xc11D9943805e56b630A401D4bd9A29550353EFa1";

        /**************************************** 以下需要在线请求的接口 ****************************************/
        // 获取账户提现资产、NVT资产的nonce值
        String withdrawalAssetNonce;
        String nvtFeeAssetNonce;
        Result withdrawalAssetBalance = NerveSDKTool.getAccountBalance(fromAddress, withdrawalAssetChainId, withdrawalAssetId);
        if (!withdrawalAssetBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.RPC_REQUEST_FAILD, withdrawalAssetBalance.toString());
        }
        Map withdrawalAssetBalanceData = (Map) withdrawalAssetBalance.getData();
        withdrawalAssetNonce = withdrawalAssetBalanceData.get("nonce").toString();

        Result nvtFeeAssetBalance = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!nvtFeeAssetBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.RPC_REQUEST_FAILD, nvtFeeAssetBalance.toString());
        }
        Map nvtFeeAssetBalanceData = (Map) nvtFeeAssetBalance.getData();
        nvtFeeAssetNonce = nvtFeeAssetBalanceData.get("nonce").toString();

        /**************************************** 以下是完全离线方式组装提现交易 ****************************************/
        WithdrawalTxDto withdrawalTxDto = new WithdrawalTxDto();
        // 设置转出账户
        withdrawalTxDto.setFromAddress(fromAddress);
        // 设置提现资产ID
        withdrawalTxDto.setAssetChainId(withdrawalAssetChainId);
        withdrawalTxDto.setAssetId(withdrawalAssetId);
        // 设置异构链网络ID
        withdrawalTxDto.setHeterogeneousChainId(heterogeneousChainId);
        // 设置异构链提现接收地址
        withdrawalTxDto.setHeterogeneousAddress(toAddress);
        // 设置提现金额
        String amount = "0.001";
        // 设置小数位
        int decimals = 18; //
        BigDecimal am = new BigDecimal(amount).movePointRight(decimals);
        withdrawalTxDto.setAmount(am.toBigInteger());

        // 设置提现的NVT手续费
        String feeStr = "2";
        int decimalsFee = 8; //小数位
        BigDecimal fee = new BigDecimal(feeStr).movePointRight(decimalsFee);
        withdrawalTxDto.setDistributionFee(fee.toBigInteger());
        withdrawalTxDto.setRemark(null);

        // 组装离线交易
        Result<Map> result = NerveSDKTool.createWithdrawalTx(withdrawalTxDto, withdrawalAssetNonce, nvtFeeAssetNonce);
        String txHex = (String) result.getData().get("txHex");
        // 离线交易签名
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        /***************************************************************************************************/

        // 在线接口，广播交易
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format());
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));

    }


    @Test
    public void withdrawalAdditionalFeeTxOffline() throws Exception {
        // 设置追加手续费的账户，必须与提现账户一致
        String fromAddress = "TNVTdTSPMcyC8e7jz8f6ngX5yTmK6S8CXEGva";
        String prikey = "17c50c6f7f18e7afd37d39f92c1d48054b6b3aa2373a70ecf2d6663eace2a7d6";
        // 发出的提现交易hash
        String withdrawalTxHash = "2dae6440f691e08552dd707a22412a6474c91e8f10810fd0dbddac301b167dff";
        // 追加的NVT手续费，此处设置为追加10个NVT
        BigInteger amount = new BigInteger("1000000000");
        String remark = null;

        /**************************************** 以下需要在线请求的接口 ****************************************/
        // 获取账NVT资产的nonce值
        String nvtFeeAssetNonce;
        Result nvtFeeAssetBalance = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!nvtFeeAssetBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.RPC_REQUEST_FAILD, nvtFeeAssetBalance.toString());
        }
        Map nvtFeeAssetBalanceData = (Map) nvtFeeAssetBalance.getData();
        nvtFeeAssetNonce = nvtFeeAssetBalanceData.get("nonce").toString();

        /**************************************** 以下是完全离线方式组装提现交易 ****************************************/
        // 组装离线交易
        Result<Map> result = NerveSDKTool.withdrawalAdditionalFeeTx(fromAddress, withdrawalTxHash, amount, 0, remark, nvtFeeAssetNonce);
        String txHex = (String) result.getData().get("txHex");
        // 离线交易签名

        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        /***************************************************************************************************/
        // 在线接口，广播交易
        result = NerveSDKTool.broadcast(txHex);
        Transaction tx = Transaction.getInstance(HexUtil.decode(txHex));
        System.out.println(tx.format(WithdrawalAdditionalFeeTxData.class));
        System.out.println(String.format("hash: %s", txHash));
        System.out.println(String.format("hash: %s", JSONUtils.obj2json(result)));

    }
}
