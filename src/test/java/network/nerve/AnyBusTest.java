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
import network.nerve.core.io.IoUtils;
import network.nerve.core.model.StringUtils;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.Account;
import network.nerve.kit.model.dto.RpcResult;
import network.nerve.kit.util.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static network.nerve.core.rpc.util.NulsDateUtils.getCurrentTimeSeconds;
import static network.nerve.kit.constant.Constant.PUBLIC_SERVER_URL;

/**
 * @author: PierreLuo
 * @date: 2026/1/4
 */
public class AnyBusTest {

    public static final int ANY_BUS = 89;
    public static final int chainId = 5;

    Map<String, Object> pMap;

    // TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA
    String userKey;
    String userKey1;
    String from;
    String from1;
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

        try {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/env.json");
            String pData = IoUtils.readBytesToString(resourceAsStream);
            pMap = JSONUtils.json2map(pData);
            from = "TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA";
            from1 = "TNVTdTSPJJMGh7ijUGDqVZyucbeN1z4jqb1ad";
            userKey = pMap.get(from).toString();
            userKey1 = pMap.get(from1).toString();
            System.out.println("init done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void callAddLiquidity() throws Exception {
        String contractAddr = this.router;
        String method = "addLiquidity";
        String[] types = new String[]{"String",
                "String",
                "int",
                "BigInteger",
                "BigInteger",
                "BigInteger",
                "BigInteger",
                "int",
                "int",
                "int[]",
                "BigInteger[]",
                "BigInteger[]",
                "String",
                "String",
                "long"};
        // 准备流动性参数
        int activeId = 8388608;

        BigInteger amountX = TxUtils.parse18("1000");
        BigInteger amountY = TxUtils.parse18("20");

        // 分布
        /*int[] deltaIds = {-2, -1, 0, 1, 2}; // 在 activeId 附近的5个 bins
        BigInteger[] distributionX = new BigInteger[]{
                BigInteger.ZERO,
                BigInteger.ZERO,
                new BigDecimal("0.4").movePointRight(18).toBigInteger(),
                new BigDecimal("0.3").movePointRight(18).toBigInteger(),
                new BigDecimal("0.3").movePointRight(18).toBigInteger()
        };
        BigInteger[] distributionY = new BigInteger[]{
                new BigDecimal("0.3").movePointRight(18).toBigInteger(),
                new BigDecimal("0.3").movePointRight(18).toBigInteger(),
                new BigDecimal("0.4").movePointRight(18).toBigInteger(),
                BigInteger.ZERO,
                BigInteger.ZERO
        };*/
        int[] deltaIds = {0}; // 在 activeId
        BigInteger[] distributionX = new BigInteger[]{
                new BigDecimal("1").movePointRight(18).toBigInteger()
        };
        BigInteger[] distributionY = new BigInteger[]{
                new BigDecimal("1").movePointRight(18).toBigInteger()
        };
        long deadline = System.currentTimeMillis()/1000 + 3600; // 1小时后
        Object[] args = new Object[]{
                tokenA,
                tokenB,
                10,  // binStep
                amountX,
                amountY,
                amountX.multiply(BigInteger.valueOf(95)).divide(BigInteger.valueOf(100)), // 5% slippage
                amountY.multiply(BigInteger.valueOf(95)).divide(BigInteger.valueOf(100)),
                activeId,
                5,  // idSlippage
                deltaIds,
                distributionX,
                distributionY,
                from1,
                from1,
                deadline
        };
        Map<String, BigInteger> msgValue = new HashMap<>();
        msgValue.put(tokenA, amountX);
        msgValue.put(tokenB, amountY);
        this.call(userKey1, contractAddr, method, types, args, msgValue);
    }

    @Test
    public void testGetNextNonEmptyBin() throws Exception {
        int bin = getNextNonEmptyBin(this.pairAB, false, 8388608);
        System.out.println(bin);
        System.out.println(Arrays.toString(getBin(this.pairAB, 8388606)));
        System.out.println(Arrays.toString(getBin(this.pairAB, 8388607)));
        System.out.println(Arrays.toString(getBin(this.pairAB, 8388608)));
        System.out.println(Arrays.toString(getBin(this.pairAB, 8388609)));
        System.out.println(Arrays.toString(getBin(this.pairAB, 8388610)));
    }

    @Test
    public void getLiquidityDistribution() throws Exception {
        String pair = this.pairAB;
        printLiquidityDistribution(pair);
    }

    /**
     * 查询并打印流动性分布
     */
    public void printLiquidityDistribution(String pair) throws Exception {
        // 获取基本信息
        String tokenX = getTokenX(pair);
        String tokenY = getTokenY(pair);
        int activeId = getActiveId(pair);
        int binStep = getBinStep(pair);

        System.out.println("\n========================================");
        System.out.println("流动性分布查询");
        System.out.println("========================================");
        System.out.println(String.format("交易对: %s/%s", tokenX, tokenY));
        System.out.println(String.format("Bin Step: %d", binStep));
        System.out.println(String.format("Active ID: %d", activeId));
        System.out.println("----------------------------------------\n");

        // 收集所有有流动性的 bin
        java.util.Set<Integer> allBins = new java.util.HashSet<>();
        allBins.add(activeId); // 先添加 activeId

        // 向左遍历（使用 getNextNonEmptyBin(false, id)，找 < id 的最大 bin）
        int currentId = activeId;
        int maxIterations = 1000; // 防止无限循环
        int iterations = 0;
        while (iterations < maxIterations) {
            int nextId = getNextNonEmptyBin(pair, true, currentId);
            if (nextId == 0 || nextId == 0xFFFFFF || nextId == currentId) {
                break;
            }
            allBins.add(nextId);
            currentId = nextId;
            iterations++;
        }

        // 向右遍历（使用 getNextNonEmptyBin(true, id)，找 > id 的最小 bin）
        currentId = activeId;
        iterations = 0;
        while (iterations < maxIterations) {
            int nextId = getNextNonEmptyBin(pair, false, currentId);
            if (nextId == 0 || nextId == 0xFFFFFF || nextId == currentId) {
                break;
            }
            allBins.add(nextId);
            currentId = nextId;
            iterations++;
        }

        // 按 binId 排序
        java.util.List<Integer> sortedBins = new java.util.ArrayList<>(allBins);
        sortedBins.sort(Integer::compareTo);

        // 计算总流动性
        BigInteger totalReserveX = BigInteger.ZERO;
        BigInteger totalReserveY = BigInteger.ZERO;
        BigInteger totalSupply = BigInteger.ZERO;

        // 打印表头
        System.out.println(String.format("%-10s %-30s %-30s %-30s %-20s",
                "Bin ID", "ReserveX", "ReserveY", "Total Supply", "Price"));
        System.out.println("------------------------------------------------------------------------" +
                "------------------------------------------------------------------------");

        // 打印每个 bin 的信息
        for (int binId : sortedBins) {
            BigInteger[] binReserves = getBin(pair, binId);
            BigInteger binSupply = totalSupply(pair, binId);
            BigInteger price = getPriceFromId(pair, binId);

            // 格式化显示（除以 1e18）
            java.math.BigDecimal reserveXDecimal = new java.math.BigDecimal(binReserves[0])
                    .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal reserveYDecimal = new java.math.BigDecimal(binReserves[1])
                    .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal supplyDecimal = new java.math.BigDecimal(binSupply)
                    .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal priceDecimal = new java.math.BigDecimal(price)
                    .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);

            String marker = (binId == activeId) ? " <-- Active" : "";
            System.out.println(String.format("%-10d %-30s %-30s %-30s %-20s%s",
                    binId,
                    reserveXDecimal.toPlainString(),
                    reserveYDecimal.toPlainString(),
                    supplyDecimal.toPlainString(),
                    priceDecimal.toPlainString(),
                    marker));

            totalReserveX = totalReserveX.add(binReserves[0]);
            totalReserveY = totalReserveY.add(binReserves[1]);
            totalSupply = totalSupply.add(binSupply);
        }

        // 打印汇总信息
        System.out.println("------------------------------------------------------------------------" +
                "------------------------------------------------------------------------");
        java.math.BigDecimal totalReserveXDecimal = new java.math.BigDecimal(totalReserveX)
                .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal totalReserveYDecimal = new java.math.BigDecimal(totalReserveY)
                .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal totalSupplyDecimal = new java.math.BigDecimal(totalSupply)
                .divide(new java.math.BigDecimal("1000000000000000000"), 6, java.math.RoundingMode.HALF_UP);

        System.out.println(String.format("%-10s %-30s %-30s %-30s",
                "总计",
                totalReserveXDecimal.toPlainString(),
                totalReserveYDecimal.toPlainString(),
                totalSupplyDecimal.toPlainString()));
        System.out.println(String.format("\n总 Bin 数量: %d", sortedBins.size()));
        System.out.println("========================================\n");
    }

    /**
     * 获取下一个非空 bin
     */
    public int getNextNonEmptyBin(String contract, boolean swapForY, int id) throws Exception {
        String method = "getNextNonEmptyBin";
        String[] types = new String[]{"boolean", "int"};
        Object[] args = new Object[]{swapForY, id};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 tokenX
     */
    private String getTokenX(String contract) throws Exception {
        String method = "getTokenX";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return rpcResult.getResult().toString();
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 tokenY
     */
    private String getTokenY(String contract) throws Exception {
        String method = "getTokenY";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return rpcResult.getResult().toString();
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 activeId
     */
    private int getActiveId(String contract) throws Exception {
        String method = "getActiveId";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 binStep
     */
    private int getBinStep(String contract) throws Exception {
        String method = "getBinStep";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 bin 的 reserves
     */
    private BigInteger[] getBin(String contract, int id) throws Exception {
        String method = "getBin";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{id};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            // 假设返回的是数组，需要解析
            String value = (String) rpcResult.getResult();
            String[] split = value.split(",");
            BigInteger[] result = new BigInteger[2];
            result[0] = new BigInteger(split[0].trim());
            result[1] = new BigInteger(split[1].trim());
            return result;
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 bin 的 totalSupply
     */
    private BigInteger totalSupply(String contract, int id) throws Exception {
        String method = "totalSupply";
        String[] types = new String[]{"BigInteger"};
        Object[] args = new Object[]{BigInteger.valueOf(id)};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 bin 的价格
     */
    private BigInteger getPriceFromId(String contract, int id) throws Exception {
        String method = "getPriceFromId";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{id};
        RpcResult rpcResult = this.callViewSilent(contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
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
        String hex = "fd3c014164644c6971756964697479526573756c747b616d6f756e745841646465643d31303030303030303030303030303030303030302c20616d6f756e745941646465643d353030303030303030303030303030303030302c20616d6f756e74584c6566743d302c20616d6f756e74594c6566743d302c206465706f7369744964733d383338383630362c383338383630372c383338383630382c383338383630392c383338383631302c206c69717569646974794d696e7465643d32323539323535353139383134383936323235363139353639353137332c32323539323535353139383134383936323235363139353639353137332c32363038373633353635303636353536343432343737353830383238332c313733323931363631363537343439363335302c313733333738323835383337363434363137307d403130303233316165393563356263623237303637666661393264646332373833346234333262663837346564386363333238386139643631373635636463613102050002327f65b2a5657ef2aa396363e2b50db4fc008fe1050002194c50b6fd6e6c1d1ddd034fafb6a3ebf1d999630000e8890423c78a000000000000000000000000000000000000000000000000050004000000000000000000050002327f65b2a5657ef2aa396363e2b50db4fc008fe1050002194c50b6fd6e6c1d1ddd034fafb6a3ebf1d999630000f444829163450000000000000000000000000000000000000000000000000500020000000000000000000625544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000e5472616e7366657253696e676c650525544e5654645453505a446a7a69414d5465684c6f733579705569567867626d5742583839330025544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a716231616407383338383630361d323235393235353531393831343839363232353631393536393531373325544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000e5472616e7366657253696e676c650525544e5654645453505a446a7a69414d5465684c6f733579705569567867626d5742583839330025544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a716231616407383338383630371d323235393235353531393831343839363232353631393536393531373325544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000e5472616e7366657253696e676c650525544e5654645453505a446a7a69414d5465684c6f733579705569567867626d5742583839330025544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a716231616407383338383630381d323630383736333536353036363535363434323437373538303832383325544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000e5472616e7366657253696e676c650525544e5654645453505a446a7a69414d5465684c6f733579705569567867626d5742583839330025544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a71623161640738333838363039133137333239313636313635373434393633353025544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000e5472616e7366657253696e676c650525544e5654645453505a446a7a69414d5465684c6f733579705569567867626d5742583839330025544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a71623161640738333838363130133137333337383238353833373634343631373025544e56546454535058667348424e794a4752685a71754d6e77354e507147666650526a6151bcae0100000000000f4465706f7369746564546f42696e730425544e5654645453505a446a7a69414d5465684c6f733579705569567867626d57425838393325544e5654645453504a4a4d476837696a55474471565a79756362654e317a346a716231616427383338383630362c383338383630372c383338383630382c383338383630392c38333838363130fd4401303030303030303030303030303030303134643131323064376231363030303030303030303030303030303030303030303030303030303030303030303030302c303030303030303030303030303030303134643131323064376231363030303030303030303030303030303030303030303030303030303030303030303030302c303030303030303030303030303030303162633136643637346563383030303030303030303030303030303030303030333738326461636539643930303030302c303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030323961323234316166363263303030302c30303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303032396132323431616636326330303030";
        AnyBusCallResult result = new AnyBusCallResult();
        result.parse(HexUtil.decode(hex), 0);
        System.out.println("result: " + result);

    }

    @Test
    public void balanceOf() throws Exception {
        String contract = this.pairAB;
        String method = "balanceOf";
        String[] types = new String[]{"String", "BigInteger"};
        String[][] args = new String[][]{
                new String[]{from1},
                new String[]{"8388608"},
        };
        this.callView(contract, method, types, args);
    }

    /**
     * 移除流动性测试用例
     */
    @Test
    public void callRemoveLiquidity() throws Exception {
        String contractAddr = this.router;
        String method = "removeLiquidity";
        String[] types = new String[]{
                "String",      // tokenX
                "String",      // tokenY
                "int",         // binStep
                "BigInteger",  // amountXMin
                "BigInteger",  // amountYMin
                "int[]",       // ids
                "BigInteger[]", // amounts
                "String",      // to
                "long"         // deadline
        };

        // 先查询用户在各个 bin 的 LP token 余额
        int activeId = 8388608;
        int[] bins = {activeId}; // 只移除 activeId 的流动性
        BigInteger[] amounts = new BigInteger[bins.length];

        System.out.println("\n========================================");
        System.out.println("查询移除前的 LP Token 余额");
        System.out.println("========================================");
        for (int i = 0; i < bins.length; i++) {
            BigInteger balance = getBalanceOf(this.pairAB, from1, bins[i]);
            amounts[i] = balance.divide(BigInteger.valueOf(2)); // 移除一半
            System.out.println(String.format("Bin %d: 总余额=%s, 移除数量=%s",
                    bins[i], format18(balance), format18(amounts[i])));
        }

        // 查询移除前的代币余额
        System.out.println("\n移除前的代币余额:");
        BigInteger balanceXBefore = getTokenBalance(from1, tokenA);
        BigInteger balanceYBefore = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s", tokenA, format18(balanceXBefore)));
        System.out.println(String.format("TokenB (%s): %s", tokenB, format18(balanceYBefore)));

        // 查询移除前的 bin reserves
        System.out.println("\n移除前的 Bin Reserves:");
        for (int binId : bins) {
            BigInteger[] binReserves = getBin(this.pairAB, binId);
            System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                    binId, format18(binReserves[0]), format18(binReserves[1])));
        }

        long deadline = System.currentTimeMillis() / 1000 + 3600; // 1小时后
        Object[] args = new Object[]{
                tokenA,
                tokenB,
                10,  // binStep
                BigInteger.ZERO,  // amountXMin (允许任何数量)
                BigInteger.ZERO,  // amountYMin (允许任何数量)
                bins,
                amounts,
                from1,  // to: 接收代币的地址
                deadline
        };

        System.out.println("\n========================================");
        System.out.println("执行移除流动性");
        System.out.println("========================================");
        this.call(userKey1, contractAddr, method, types, args);

        // 等待交易确认后查询结果
        System.out.println("\n等待交易确认...");
        Thread.sleep(3000);

        // 查询移除后的代币余额
        System.out.println("\n移除后的代币余额:");
        BigInteger balanceXAfter = getTokenBalance(from1, tokenA);
        BigInteger balanceYAfter = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s (变化: %s)",
                tokenA, format18(balanceXAfter), format18(balanceXAfter.subtract(balanceXBefore))));
        System.out.println(String.format("TokenB (%s): %s (变化: %s)",
                tokenB, format18(balanceYAfter), format18(balanceYAfter.subtract(balanceYBefore))));

        // 查询移除后的 LP token 余额
        System.out.println("\n移除后的 LP Token 余额:");
        for (int binId : bins) {
            BigInteger balance = getBalanceOf(this.pairAB, from1, binId);
            System.out.println(String.format("Bin %d: %s", binId, format18(balance)));
        }

        // 查询移除后的 bin reserves
        System.out.println("\n移除后的 Bin Reserves:");
        for (int binId : bins) {
            BigInteger[] binReserves = getBin(this.pairAB, binId);
            System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                    binId, format18(binReserves[0]), format18(binReserves[1])));
        }

        // 打印流动性分布
        System.out.println("\n========================================");
        System.out.println("当前流动性分布");
        System.out.println("========================================");
        printLiquidityDistribution(this.pairAB);
    }

    /**
     * Swap 测试用例 - 精确输入交换
     */
    @Test
    public void callSwapExactTokensForTokens() throws Exception {
        String contractAddr = this.router;
        String method = "swapExactTokensForTokens";
        String[] types = new String[]{
                "BigInteger",  // amountIn
                "BigInteger",  // amountOutMin
                "int[]",       // path.pairBinSteps
                "String[]",    // path.tokenPath
                "String",      // to
                "long"         // deadline
        };

        // Swap: tokenA -> tokenB
        BigInteger amountIn = TxUtils.parse18("2.1");
        BigInteger amountOutMin = TxUtils.parse18("0.4"); // 至少 0.4 tokenB

        int[] binSteps = {10};
        String[] tokenPath = {tokenA, tokenB};

        // 查询交换前的余额
        System.out.println("\n========================================");
        System.out.println("查询交换前的余额");
        System.out.println("========================================");
        BigInteger balanceABefore = getTokenBalance(from1, tokenA);
        BigInteger balanceBBefore = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s", tokenA, format18(balanceABefore)));
        System.out.println(String.format("TokenB (%s): %s", tokenB, format18(balanceBBefore)));

        // 查询交换前的 bin reserves 和 activeId
        int activeId = getActiveId(this.pairAB);
        System.out.println(String.format("\nActive ID: %d", activeId));
        System.out.println("交换前的 Bin Reserves:");
        BigInteger[] binReservesBefore = getBin(this.pairAB, activeId);
        System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                activeId, format18(binReservesBefore[0]), format18(binReservesBefore[1])));

        long deadline = System.currentTimeMillis() / 1000 + 3600; // 1小时后
        Object[] args = new Object[]{
                amountIn,
                amountOutMin,
                binSteps,
                tokenPath,
                from1,  // to: 接收代币的地址
                deadline
        };

        // 准备 msgValue: 需要将 tokenA 转入 router
        Map<String, BigInteger> msgValue = new HashMap<>();
        msgValue.put(tokenA, amountIn);

        System.out.println("\n========================================");
        System.out.println("执行 Swap");
        System.out.println("========================================");
        System.out.println(String.format("输入: %s TokenA", format18(amountIn)));
        System.out.println(String.format("最小输出: %s TokenB", format18(amountOutMin)));
        this.call(userKey1, contractAddr, method, types, args, msgValue);

        // 等待交易确认后查询结果
        System.out.println("\n等待交易确认...");
        Thread.sleep(3000);

        // 查询交换后的余额
        System.out.println("\n交换后的代币余额:");
        BigInteger balanceAAfter = getTokenBalance(from1, tokenA);
        BigInteger balanceBAfter = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s (变化: %s)",
                tokenA, format18(balanceAAfter), format18(balanceAAfter.subtract(balanceABefore))));
        System.out.println(String.format("TokenB (%s): %s (变化: %s)",
                tokenB, format18(balanceBAfter), format18(balanceBAfter.subtract(balanceBBefore))));

        // 计算实际交换率
        BigInteger actualAmountIn = balanceABefore.subtract(balanceAAfter);
        BigInteger actualAmountOut = balanceBAfter.subtract(balanceBBefore);
        if (actualAmountOut.compareTo(BigInteger.ZERO) > 0 && actualAmountIn.compareTo(BigInteger.ZERO) > 0) {
            BigDecimal price = new BigDecimal(actualAmountOut)
                    .divide(new BigDecimal(actualAmountIn), 18, RoundingMode.HALF_UP);
            System.out.println(String.format("\n实际交换率: 1 TokenA = %s TokenB", price.toPlainString()));
        }

        // 查询交换后的 bin reserves
        System.out.println("\n交换后的 Bin Reserves:");
        BigInteger[] binReservesAfter = getBin(this.pairAB, activeId);
        System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                activeId, format18(binReservesAfter[0]), format18(binReservesAfter[1])));
        System.out.println(String.format("ReserveX 变化: %s",
                format18(binReservesAfter[0].subtract(binReservesBefore[0]))));
        System.out.println(String.format("ReserveY 变化: %s",
                format18(binReservesAfter[1].subtract(binReservesBefore[1]))));

        // 打印流动性分布
        System.out.println("\n========================================");
        System.out.println("当前流动性分布");
        System.out.println("========================================");
        printLiquidityDistribution(this.pairAB);
    }

    /**
     * Swap 测试用例 - 精确输出交换
     * 指定要获得的输出数量，计算需要的输入数量
     */
    @Test
    public void callSwapTokensForExactTokens() throws Exception {
        String contractAddr = this.router;
        String method = "swapTokensForExactTokens";
        String[] types = new String[]{
                "BigInteger",  // amountOut
                "BigInteger",  // amountInMax
                "int[]",       // path.pairBinSteps
                "String[]",    // path.tokenPath
                "String",      // to
                "long"         // deadline
        };

        // Swap: tokenA -> tokenB
        // 想要获得 0.5 tokenB，最多愿意支付 3.0 tokenA
        BigInteger amountOut = TxUtils.parse18("0.5");
        BigInteger amountInMax = TxUtils.parse18("3.0");

        int[] binSteps = {10};
        String[] tokenPath = {tokenA, tokenB};

        // 查询交换前的余额
        System.out.println("\n========================================");
        System.out.println("查询交换前的余额");
        System.out.println("========================================");
        BigInteger balanceABefore = getTokenBalance(from1, tokenA);
        BigInteger balanceBBefore = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s", tokenA, format18(balanceABefore)));
        System.out.println(String.format("TokenB (%s): %s", tokenB, format18(balanceBBefore)));

        // 查询交换前的 bin reserves 和 activeId
        int activeId = getActiveId(this.pairAB);
        System.out.println(String.format("\nActive ID: %d", activeId));
        System.out.println("交换前的 Bin Reserves:");
        BigInteger[] binReservesBefore = getBin(this.pairAB, activeId);
        System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                activeId, format18(binReservesBefore[0]), format18(binReservesBefore[1])));

        long deadline = System.currentTimeMillis() / 1000 + 3600; // 1小时后
        Object[] args = new Object[]{
                amountOut,
                amountInMax,
                binSteps,
                tokenPath,
                from1,  // to: 接收代币的地址
                deadline
        };

        // 注意：swapTokensForExactTokens 需要先计算需要的输入数量
        // 但实际执行时，我们需要先估算一个输入数量并转入 router
        // 这里使用 amountInMax 作为初始估算值
        Map<String, BigInteger> msgValue = new HashMap<>();
        msgValue.put(tokenA, amountInMax);

        System.out.println("\n========================================");
        System.out.println("执行 Swap (精确输出)");
        System.out.println("========================================");
        System.out.println(String.format("期望输出: %s TokenB", format18(amountOut)));
        System.out.println(String.format("最大输入: %s TokenA", format18(amountInMax)));
        this.call(userKey1, contractAddr, method, types, args, msgValue);

        // 等待交易确认后查询结果
        System.out.println("\n等待交易确认...");
        Thread.sleep(3000);

        // 查询交换后的余额
        System.out.println("\n交换后的代币余额:");
        BigInteger balanceAAfter = getTokenBalance(from1, tokenA);
        BigInteger balanceBAfter = getTokenBalance(from1, tokenB);
        System.out.println(String.format("TokenA (%s): %s (变化: %s)",
                tokenA, format18(balanceAAfter), format18(balanceAAfter.subtract(balanceABefore))));
        System.out.println(String.format("TokenB (%s): %s (变化: %s)",
                tokenB, format18(balanceBAfter), format18(balanceBAfter.subtract(balanceBBefore))));

        // 验证实际输出是否达到期望值
        BigInteger actualAmountOut = balanceBAfter.subtract(balanceBBefore);
        System.out.println(String.format("\n期望输出: %s TokenB", format18(amountOut)));
        System.out.println(String.format("实际输出: %s TokenB", format18(actualAmountOut)));
        if (actualAmountOut.compareTo(amountOut) >= 0) {
            System.out.println("✅ 实际输出达到或超过期望值");
        } else {
            System.out.println("⚠️ 实际输出低于期望值");
        }

        // 计算实际输入和交换率
        BigInteger actualAmountIn = balanceABefore.subtract(balanceAAfter);
        System.out.println(String.format("实际输入: %s TokenA", format18(actualAmountIn)));
        System.out.println(String.format("最大允许输入: %s TokenA", format18(amountInMax)));
        if (actualAmountIn.compareTo(amountInMax) <= 0) {
            System.out.println("✅ 实际输入在允许范围内");
        } else {
            System.out.println("⚠️ 实际输入超过最大允许值");
        }

        if (actualAmountOut.compareTo(BigInteger.ZERO) > 0 && actualAmountIn.compareTo(BigInteger.ZERO) > 0) {
            BigDecimal price = new BigDecimal(actualAmountOut)
                    .divide(new BigDecimal(actualAmountIn), 18, RoundingMode.HALF_UP);
            System.out.println(String.format("\n实际交换率: 1 TokenA = %s TokenB", price.toPlainString()));
        }

        // 查询交换后的 bin reserves
        System.out.println("\n交换后的 Bin Reserves:");
        BigInteger[] binReservesAfter = getBin(this.pairAB, activeId);
        System.out.println(String.format("Bin %d: ReserveX=%s, ReserveY=%s",
                activeId, format18(binReservesAfter[0]), format18(binReservesAfter[1])));
        System.out.println(String.format("ReserveX 变化: %s",
                format18(binReservesAfter[0].subtract(binReservesBefore[0]))));
        System.out.println(String.format("ReserveY 变化: %s",
                format18(binReservesAfter[1].subtract(binReservesBefore[1]))));

        // 打印流动性分布
        System.out.println("\n========================================");
        System.out.println("当前流动性分布");
        System.out.println("========================================");
        printLiquidityDistribution(this.pairAB);
    }

    /**
     * 查询用户的 LP Token 余额
     */
    private BigInteger getBalanceOf(String pair, String account, int binId) throws Exception {
        String method = "balanceOf";
        String[] types = new String[]{"String", "BigInteger"};
        Object[] args = new Object[]{account, BigInteger.valueOf(binId)};
        RpcResult rpcResult = this.callViewSilent(pair, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 查询用户的代币余额
     */
    private BigInteger getTokenBalance(String account, String token) throws Exception {
        String[] split = token.split("-");
        int chainId = Integer.parseInt(split[0]);
        int assetId = Integer.parseInt(split[1]);
        Result result = NerveSDKTool.getAccountBalance(account, chainId, assetId);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.toString());
        }
        Map data = (Map) result.getData();
        return new BigInteger(data.get("available").toString());
    }

    /**
     * 格式化显示（除以 1e18）
     */
    private String format18(BigInteger value) {
        if (value == null || value.compareTo(BigInteger.ZERO) == 0) {
            return "0";
        }
        BigDecimal decimal = new BigDecimal(value)
                .divide(new BigDecimal("1000000000000000000"), 6, RoundingMode.HALF_UP);
        return decimal.toPlainString();
    }

    /**
     * 查询并验证移除流动性后的状态
     */
    @Test
    public void verifyRemoveLiquidityState() throws Exception {
        String pair = this.pairAB;
        String account = from1;
        int activeId = 8388608;

        System.out.println("\n========================================");
        System.out.println("验证移除流动性后的状态");
        System.out.println("========================================");

        // 1. 查询 LP Token 余额
        System.out.println("\n1. LP Token 余额:");
        for (int binId : new int[]{activeId - 1, activeId, activeId + 1}) {
            BigInteger balance = getBalanceOf(pair, account, binId);
            System.out.println(String.format("  Bin %d: %s", binId, format18(balance)));
        }

        // 2. 查询代币余额
        System.out.println("\n2. 代币余额:");
        BigInteger balanceA = getTokenBalance(account, tokenA);
        BigInteger balanceB = getTokenBalance(account, tokenB);
        System.out.println(String.format("  TokenA: %s", format18(balanceA)));
        System.out.println(String.format("  TokenB: %s", format18(balanceB)));

        // 3. 查询 Bin Reserves
        System.out.println("\n3. Bin Reserves:");
        for (int binId : new int[]{activeId - 1, activeId, activeId + 1}) {
            BigInteger[] reserves = getBin(pair, binId);
            BigInteger supply = totalSupply(pair, binId);
            System.out.println(String.format("  Bin %d: ReserveX=%s, ReserveY=%s, Supply=%s",
                    binId, format18(reserves[0]), format18(reserves[1]), format18(supply)));
        }

        // 4. 打印流动性分布
        printLiquidityDistribution(pair);
    }

    /**
     * 查询并验证 Swap 后的状态
     */
    @Test
    public void verifySwapState() throws Exception {
        String pair = this.pairAB;
        String account = from1;

        System.out.println("\n========================================");
        System.out.println("验证 Swap 后的状态");
        System.out.println("========================================");

        // 1. 查询代币余额
        System.out.println("\n1. 代币余额:");
        BigInteger balanceA = getTokenBalance(account, tokenA);
        BigInteger balanceB = getTokenBalance(account, tokenB);
        System.out.println(String.format("  TokenA: %s", format18(balanceA)));
        System.out.println(String.format("  TokenB: %s", format18(balanceB)));

        // 2. 查询 Active Bin 状态
        int activeId = getActiveId(pair);
        System.out.println(String.format("\n2. Active Bin (ID: %d):", activeId));
        BigInteger[] reserves = getBin(pair, activeId);
        BigInteger price = getPriceFromId(pair, activeId);
        System.out.println(String.format("  ReserveX: %s", format18(reserves[0])));
        System.out.println(String.format("  ReserveY: %s", format18(reserves[1])));
        System.out.println(String.format("  Price: %s", format18(price)));

        // 3. 计算价格比率
        if (reserves[0].compareTo(BigInteger.ZERO) > 0 && reserves[1].compareTo(BigInteger.ZERO) > 0) {
            BigDecimal priceRatio = new BigDecimal(reserves[1])
                    .divide(new BigDecimal(reserves[0]), 18, RoundingMode.HALF_UP);
            System.out.println(String.format("  Price Ratio (Y/X): %s", priceRatio.toPlainString()));
        }

        // 4. 打印流动性分布
        printLiquidityDistribution(pair);
    }

    /**
     * 查询交易对的总体状态
     */
    @Test
    public void queryPairState() throws Exception {
        String pair = this.pairAB;

        System.out.println("\n========================================");
        System.out.println("交易对总体状态查询");
        System.out.println("========================================");

        // 基本信息
        String tokenX = getTokenX(pair);
        String tokenY = getTokenY(pair);
        int activeId = getActiveId(pair);
        int binStep = getBinStep(pair);

        System.out.println(String.format("交易对地址: %s", pair));
        System.out.println(String.format("TokenX: %s", tokenX));
        System.out.println(String.format("TokenY: %s", tokenY));
        System.out.println(String.format("Bin Step: %d", binStep));
        System.out.println(String.format("Active ID: %d", activeId));

        // 查询总储备量
        BigInteger totalReserveX = BigInteger.ZERO;
        BigInteger totalReserveY = BigInteger.ZERO;

        // 遍历所有有流动性的 bin
        java.util.Set<Integer> allBins = new java.util.HashSet<>();
        allBins.add(activeId);

        int currentId = activeId;
        int maxIterations = 100;
        int iterations = 0;
        while (iterations < maxIterations) {
            int nextId = getNextNonEmptyBin(pair, true, currentId);
            if (nextId == 0 || nextId == 0xFFFFFF || nextId == currentId) {
                break;
            }
            allBins.add(nextId);
            currentId = nextId;
            iterations++;
        }

        currentId = activeId;
        iterations = 0;
        while (iterations < maxIterations) {
            int nextId = getNextNonEmptyBin(pair, false, currentId);
            if (nextId == 0 || nextId == 0xFFFFFF || nextId == currentId) {
                break;
            }
            allBins.add(nextId);
            currentId = nextId;
            iterations++;
        }

        for (int binId : allBins) {
            BigInteger[] binReserves = getBin(pair, binId);
            totalReserveX = totalReserveX.add(binReserves[0]);
            totalReserveY = totalReserveY.add(binReserves[1]);
        }

        System.out.println(String.format("\n总储备量:"));
        System.out.println(String.format("  Total ReserveX: %s", format18(totalReserveX)));
        System.out.println(String.format("  Total ReserveY: %s", format18(totalReserveY)));

        if (totalReserveX.compareTo(BigInteger.ZERO) > 0 && totalReserveY.compareTo(BigInteger.ZERO) > 0) {
            BigDecimal totalPriceRatio = new BigDecimal(totalReserveY)
                    .divide(new BigDecimal(totalReserveX), 18, RoundingMode.HALF_UP);
            System.out.println(String.format("  总价格比率 (Y/X): %s", totalPriceRatio.toPlainString()));
        }

        System.out.println(String.format("\n有流动性的 Bin 数量: %d", allBins.size()));

        // 打印详细流动性分布
        printLiquidityDistribution(pair);
    }

    private RpcResult callView(String contract, String method, String[] types, Object args) throws Exception {
        RpcResult<Map> rpcResult = JsonRpcUtil.request("callAnyBusContract", ListUtil.of(chainId, contract, method, types, args));
        System.out.println(JSONUtils.obj2PrettyJson(rpcResult));
        return rpcResult;
    }

    /**
     * 调用 view 方法但不打印日志（用于批量查询）
     */
    private RpcResult callViewSilent(String contract, String method, String[] types, Object args) throws Exception {
        RpcResult<Map> rpcResult = JsonRpcUtil.request("callAnyBusContract", ListUtil.of(chainId, contract, method, types, args));
        return rpcResult;
    }

    private void call(String userKey, String contractAddr, String method, String[] paramTypes, Object[] args) throws Exception {
        this.call(userKey, contractAddr, method, paramTypes, args, null);
    }

    private void call(String userKey, String contractAddr, String method, String[] paramTypes, Object[] args, Map<String, BigInteger> msgValue) throws Exception {
        Account account = AccountTool.createAccount(chainId, userKey);
        byte[] fromBytes = account.getAddress().getAddressBytes();
        byte[] contractBytes = AddressTool.getAddress(contractAddr);
        String from = account.getAddress().getBase58();
        String remark = "call Token1155 test";
        Transaction tx = new Transaction(ANY_BUS);
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CALL.type());
        Call call = new Call();
        call.setContractAddress(contractBytes);
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

        if (msgValue != null && !msgValue.isEmpty()) {
            Set<Map.Entry<String, BigInteger>> entries = msgValue.entrySet();
            for (Map.Entry<String, BigInteger> entry : entries) {
                String key = entry.getKey();
                BigInteger value = entry.getValue();
                String[] split = key.split("-");
                int chainId = Integer.parseInt(split[0]);
                int assetId = Integer.parseInt(split[1]);
                String nonce = getBalanceAndNonce(from, chainId, assetId);
                froms.add(new CoinFrom(
                        fromBytes,
                        chainId,
                        assetId,
                        value,
                        HexUtil.decode(nonce),
                        (byte) 0));
                tos.add(new CoinTo(
                        contractBytes,
                        chainId,
                        assetId,
                        value));
            }
        } else {
            String nonce = getBalanceAndNonce(from, SDKContext.main_chain_id, SDKContext.main_asset_id);
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
        }
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
