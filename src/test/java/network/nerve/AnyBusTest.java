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

import network.nerve.anybus.*;
import network.nerve.base.basic.AddressTool;
import network.nerve.base.data.CoinData;
import network.nerve.base.data.CoinFrom;
import network.nerve.base.data.CoinTo;
import network.nerve.base.data.Transaction;
import network.nerve.core.basic.Result;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.model.StringUtils;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.Account;
import network.nerve.kit.model.dto.RpcResult;
import network.nerve.kit.util.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static network.nerve.core.rpc.util.NulsDateUtils.getCurrentTimeSeconds;
import static network.nerve.kit.constant.Constant.PUBLIC_SERVER_URL;

/**
 * @author: PierreLuo
 * @date: 2026/1/4
 */
public class AnyBusTest {

    public static final int ANY_BUS = 89;
    public static final int chainId = 5;

    // TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA
    String userKey = "4594348E3482B751AA235B8E580EFEF69DB465B3A291C5662CEDA6459ED12E39";
    String userKey1 = "6de8506ce2f2291ea4bd5a8a4718d3179f5591e60eac6e8f6333a84004307b32";
    String from = "TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA";
    String from1 = "TNVTdTSPJJMGh7ijUGDqVZyucbeN1z4jqb1ad";
    String router = "TNVTdTSPZDjziAMTehLos5ypUiVxgbmWBX893";
    String factory = "TNVTdTSPbEECns5AFn8mvsWtNWYjJ28kCi1Wk";
    String token1155 = "TNVTdTSPkNQwwSd3UYZDpDYudLQLL2DwK5AAC";

    // 测试代币
    String tokenA = "5-4";  // 稳定币
    String tokenB = "5-2";   // ETH
    String tokenC = "5-3";   // BTC
    String pairAB = "TNVTdTSPXfsHBNyJGRhZquMnw5NPqGffPRjaQ";
    String pairBC = "TNVTdTSPfuef8sutXB6Fj8gJo9Mc3cMoRUf4x";

    @Before
    public void before() {
        NerveSDKBootStrap.init(chainId, 2, "TNVT","tNULS", "http://127.0.0.1:17004/");
    }

    String getBalanceAndNonce(String addr, int chainId, int assetId) {
        Result result = NerveSDKTool.getAccountBalance(addr, chainId, assetId);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.toString());
        }
        Map data = (Map) result.getData();
        String nonce = data.get("nonce").toString();
        BigInteger available = new BigInteger(data.get("available").toString());
        return nonce;
    }


    @Test
    public void createToken1155() throws Exception {
        String from = this.from;
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CREATE_1155.type());
        Create1155 create = new Create1155();
        create.setName("TikTok");
        create.setSymbol("TTK");
        create.setUri("tiktok.com");
        create.setMinters(new String[]{from});
        txData.setData(create.serialize());
        this.createContract(userKey, txData);
    }

    @Test
    public void createLBFactory() throws Exception {
        String from = this.from;
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CREATE_LB_FACTORY.type());
        CreateLBFactory create = new CreateLBFactory();
        create.setFeeRecipient(from);
        txData.setData(create.serialize());
        this.createContract(userKey, txData);
    }

    @Test
    public void createLBRouter() throws Exception {
        String factory = this.factory;
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CREATE_LB_ROUTER.type());
        CreateLBRouter create = new CreateLBRouter();
        create.setFactory(factory);
        txData.setData(create.serialize());
        this.createContract(userKey, txData);
    }

    @Test
    public void callSetPreset() throws Exception {
        PresetConfig config = DefaultPresets.getPreset(10);
        String contractAddr = this.factory;
        String method = "setPreset";
        String[] types = new String[]{"int", "int", "int", "int", "int", "int", "int", "int", "boolean"};
        Object[] args = new Object[]{
                config.getBinStep(),
                config.getBaseFactor(),
                config.getFilterPeriod(),
                config.getDecayPeriod(),
                config.getReductionFactor(),
                config.getVariableFeeControl(),
                config.getProtocolShare(),
                config.getMaxVolatilityAccumulated(),
                true
        };
        this.call(userKey, contractAddr, method, types, args);
    }

    @Test
    public void callAddQuoteAsset() throws Exception {
        String contractAddr = this.factory;
        String method = "addQuoteAsset";
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{
                tokenC
        };
        this.call(userKey, contractAddr, method, types, args);
    }

    @Test
    public void callCreateLBPair() throws Exception {
        int activeId = 8388608; // 中间价格点
        String contractAddr = this.factory;
        String method = "createLBPair";
        String[] types = new String[]{"String", "String", "int", "int"};
        Object[] args = new Object[]{
                tokenB, tokenC, activeId, 10
        };
        this.call(userKey, contractAddr, method, types, args);
    }
    @Test
    public void getPreset() throws Exception {
        String contract = this.factory;
        String method = "getPreset";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{10};
        this.callView(contract, method, types, args);
    }

    @Test
    public void getLBPairInformation() throws Exception {
        String contract = this.factory;
        String method = "getLBPairInformation";
        String[] types = new String[]{"String", "String", "BigInteger"};
        Object[] args = new Object[]{tokenB, tokenC, 10};
        this.callView(contract, method, types, args);
    }

    @Test
    public void isQuoteAsset() throws Exception {
        String contract = this.factory;
        String method = "isQuoteAsset";
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{tokenC};
        this.callView(contract, method, types, args);
    }



    @Test
    public void mintToken1155() throws Exception {
        String contractAddr = this.token1155;
        String method = "mint";
        String[] types = new String[]{"String", "BigInteger", "BigInteger"};
        String[][] args = new String[][]{
                new String[]{"TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA"},
                new String[]{"0"},
                new String[]{"123"},
        };
        this.call(userKey, contractAddr, method, types, args);
    }

    @Test
    public void transferToken1155() throws Exception {
        String contractAddr = this.token1155;
        String methodName = "safeTransferFrom";
        String[] paramTypeNames = new String[]{"String", "String", "BigInteger", "BigInteger", "String"};
        String from = "TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA";  // 拥有123个tokenId=0
        String to = "TNVTdTSPKy4iLwK6XC52VNqVSnk1vncF5Z2mu";    // 测试地址33
        String[][] args = new String[][]{
                new String[]{from},
                new String[]{to},
                new String[]{"0"},
                new String[]{"10"},
                new String[]{""},
        };
        this.call(userKey, contractAddr, methodName, paramTypeNames, args);
    }

    @Test
    public void desTest() throws Exception {
        String hex = "004036313931653834323235633936313434386330303331663134636335323432656332313165653235626332393534306630643236313461383334623435336534000125544e5654645453506b4e51777753643355595a4470445975644c514c4c3244774b3541414373740000000000000e5472616e7366657253696e676c650525544e565464545350526e586b446961677937656e7469314b4c37354e55354178433973514125544e565464545350526e586b446961677937656e7469314b4c37354e55354178433973514125544e5654645453504b7934694c774b3658433532564e7156536e6b31766e6346355a326d75013003313030";
        AnyBusCallResult result = new AnyBusCallResult();
        result.parse(HexUtil.decode(hex), 0);
        System.out.println("result: " + result);

    }

    @Test
    public void balanceOf() throws Exception {
        String contract = this.token1155;
        String method = "balanceOf";
        String[] types = new String[]{"String", "BigInteger"};
        String[][] args = new String[][]{
                new String[]{"TNVTdTSPKy4iLwK6XC52VNqVSnk1vncF5Z2mu"},
                new String[]{"0"},
        };
        this.callView(contract, method, types, args);
    }

    private void callView(String contract, String method, String[] types, Object args) throws Exception {
        RpcResult<Map> rpcResult = JsonRpcUtil.request("callAnyBusContract", ListUtil.of(chainId, contract, method, types, args));
        System.out.println(JSONUtils.obj2PrettyJson(rpcResult));
    }

    private void call(String userKey, String contractAddr, String method, String[] paramTypes, Object[] args) throws Exception {
        Account account = AccountTool.createAccount(chainId, userKey);
        byte[] fromBytes = account.getAddress().getAddressBytes();
        String from = account.getAddress().getBase58();
        String nonce = getBalanceAndNonce(from, SDKContext.main_chain_id, SDKContext.main_asset_id);
        String remark = "call Token1155 test";
        Transaction tx = new Transaction(ANY_BUS);
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CALL.type());
        Call call = new Call();
        call.setContractAddress(AddressTool.getAddress(contractAddr));
        call.setMethodName(method);
        call.setParamTypeNames(paramTypes);
        call.setArgs(TxUtils.twoDimensionalArray(args, paramTypes));
        txData.setData(call.serialize());

        tx.setTxData(TxUtils.nulsData2HexBytes(txData));
        tx.setTime(getCurrentTimeSeconds());
        tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

        CoinData coinData = new CoinData();
        List<CoinFrom> froms = coinData.getFrom();
        List<CoinTo> tos = coinData.getTo();

        froms.add(new CoinFrom(
                fromBytes,
                SDKContext.main_chain_id,
                SDKContext.main_asset_id,
                BigInteger.ZERO,
                HexUtil.decode(nonce),
                (byte) 0));
        tos.add(new CoinTo(
                AddressTool.getAddress(contractAddr),
                SDKContext.main_chain_id,
                SDKContext.main_asset_id,
                BigInteger.ZERO));
        tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));

        String txHex = HexUtil.encode(TxUtils.nulsData2HexBytes(tx));
        // 私钥签名交易
        Result<Map> result = NerveSDKTool.sign(txHex, from, userKey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        System.out.println(String.format("交易序列化Hex字符串: %s", txHex));
        System.out.println(String.format("交易hash: %s", txHash));

        // 广播交易
        result = NerveSDKTool.broadcast(txHex);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }

    private void createContract(String userKey, AnyBusTxData txData) throws Exception {
        Account account = AccountTool.createAccount(chainId, userKey);
        byte[] fromBytes = account.getAddress().getAddressBytes();
        String from = account.getAddress().getBase58();
        String nonce = getBalanceAndNonce(from, SDKContext.main_chain_id, SDKContext.main_asset_id);
        String remark = "create contract test";
        Transaction tx = new Transaction(ANY_BUS);

        tx.setTxData(TxUtils.nulsData2HexBytes(txData));
        tx.setTime(getCurrentTimeSeconds());
        tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

        CoinData coinData = new CoinData();
        List<CoinFrom> froms = coinData.getFrom();
        List<CoinTo> tos = coinData.getTo();

        froms.add(new CoinFrom(
                fromBytes,
                SDKContext.main_chain_id,
                SDKContext.main_asset_id,
                BigInteger.ZERO,
                HexUtil.decode(nonce),
                (byte) 0));
        tos.add(new CoinTo(
                fromBytes,
                SDKContext.main_chain_id,
                SDKContext.main_asset_id,
                BigInteger.ZERO));
        tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));

        String txHex = HexUtil.encode(TxUtils.nulsData2HexBytes(tx));
        // 私钥签名交易
        Result<Map> result = NerveSDKTool.sign(txHex, from, userKey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        System.out.println(String.format("交易序列化Hex字符串: %s", txHex));
        System.out.println(String.format("交易hash: %s", txHash));

        // 广播交易
        result = NerveSDKTool.broadcast(txHex);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }
}
