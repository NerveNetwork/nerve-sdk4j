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
import network.nerve.core.parse.JSONUtils;
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
        int tokenDecimals = 18;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        // 2-1, 5-9
        Result<Map> result =
                NerveSDKTool.createTxSimpleTransferOfNonNvt(fromAddress, toAddress, 5, 9, amount,
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
        String fromAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String withdrawalTxHash = "2dae6440f691e08552dd707a22412a6474c91e8f10810fd0dbddac301b167dff";
        BigInteger amount = new BigInteger("1000000000");
        String remark = null;
        Result<Map> result = NerveSDKTool.withdrawalAdditionalFeeTx(fromAddress, withdrawalTxHash, amount, 0, remark);

        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
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

}
