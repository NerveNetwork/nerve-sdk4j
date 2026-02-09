package network.nerve.kit.service;

import network.nerve.SDKContext;
import network.nerve.base.basic.AddressTool;
import network.nerve.base.data.CoinData;
import network.nerve.base.data.CoinFrom;
import network.nerve.base.data.CoinTo;
import network.nerve.base.data.Transaction;
import network.nerve.core.basic.Result;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.model.StringUtils;
import network.nerve.kit.model.Account;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.txdata.anybus.*;
import network.nerve.kit.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static network.nerve.core.rpc.util.NulsDateUtils.getCurrentTimeSeconds;

/**
 * DLMM 相关的链上查询和 AnyBus 调用服务。
 *
 * 从 {@code AnyBusTest} 中抽取出来，供 SDK 业务代码复用。
 *
 * @author PierreLuo
 */
public class DlmmService {

    /**
     * AnyBus 交易类型（89）
     */
    private static final int ANY_BUS = 89;

    private static final DlmmService INSTANCE = new DlmmService();

    private DlmmService() {
    }

    public static DlmmService getInstance() {
        return INSTANCE;
    }

    /**
     * 调用 AnyBus 合约的 view 方法。
     */
    @SuppressWarnings("rawtypes")
    public RpcResult callView(int chainId, String contract, String method, String[] types, Object args) throws Exception {
        return JsonRpcUtil.request("callAnyBusContract",
                ListUtil.of(chainId, contract, method, types, args));
    }

    /**
     * 调用 AnyBus 合约的 view 方法，不打印日志。
     */
    @SuppressWarnings("rawtypes")
    public RpcResult callViewSilent(int chainId, String contract, String method, String[] types, Object args) throws Exception {
        return JsonRpcUtil.request("callAnyBusContract",
                ListUtil.of(chainId, contract, method, types, args));
    }

    /**
     * 获取下一个非空 bin。
     */
    public int getNextNonEmptyBin(int chainId, String contract, boolean swapForY, int id) throws Exception {
        String method = "getNextNonEmptyBin";
        String[] types = new String[]{"boolean", "int"};
        Object[] args = new Object[]{swapForY, id};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 tokenX。
     */
    public String getTokenX(int chainId, String contract) throws Exception {
        String method = "getTokenX";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return rpcResult.getResult().toString();
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 tokenY。
     */
    public String getTokenY(int chainId, String contract) throws Exception {
        String method = "getTokenY";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return rpcResult.getResult().toString();
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 activeId。
     */
    public int getActiveId(int chainId, String contract) throws Exception {
        String method = "getActiveId";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 binStep。
     */
    public int getBinStep(int chainId, String contract) throws Exception {
        String method = "getBinStep";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return Integer.parseInt(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 bin 的 reserves。
     * 合约返回格式为 "reserveX,reserveY" 的字符串。
     */
    public BigInteger[] getBin(int chainId, String contract, int id) throws Exception {
        String method = "getBin";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{id};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
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
     * 获取 bin 的 totalSupply。
     */
    public BigInteger totalSupply(int chainId, String contract, int id) throws Exception {
        String method = "totalSupply";
        String[] types = new String[]{"BigInteger"};
        Object[] args = new Object[]{BigInteger.valueOf(id)};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 获取 binId 对应价格。
     */
    public BigInteger getPriceFromId(int chainId, String contract, int id) throws Exception {
        String method = "getPriceFromId";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{id};
        RpcResult rpcResult = this.callViewSilent(chainId, contract, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    /**
     * 查询指定账户在指定 bin 下的 LP Token 余额。
     */
    public BigInteger getBalanceOf(int chainId, String pair, String account, int binId) throws Exception {
        String method = "balanceOf";
        String[] types = new String[]{"String", "BigInteger"};
        Object[] args = new Object[]{account, BigInteger.valueOf(binId)};
        RpcResult rpcResult = this.callViewSilent(chainId, pair, method, types, args);
        if (rpcResult.getResult() != null) {
            return new BigInteger(rpcResult.getResult().toString());
        } else {
            throw new RuntimeException(rpcResult.getError().toString());
        }
    }

    // ======================== 解析 view 返回为 DTO 的辅助方法 ========================
    @SuppressWarnings("unchecked")
    private static DlmmLBPairInformationDto parseLBPairInformation(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            DlmmLBPairInformationDto dto = new DlmmLBPairInformationDto();
            dto.setBinStep(getInt(m, "binStep"));
            dto.setLbPair(getStr(m, "lbPair"));
            dto.setCreatedByOwner(getBool(m, "createdByOwner"));
            dto.setIgnoredForRouting(getBool(m, "ignoredForRouting"));
            return dto;
        }
        if (result instanceof String) {
            String s = (String) result;
            String[] parts = s.split(",");
            if (parts.length >= 4) {
                return new DlmmLBPairInformationDto(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        Boolean.parseBoolean(parts[2].trim()),
                        Boolean.parseBoolean(parts[3].trim()));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static DlmmPresetConfigDto parsePresetConfig(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            DlmmPresetConfigDto dto = new DlmmPresetConfigDto();
            dto.setBinStep(getInt(m, "binStep"));
            dto.setBaseFactor(getInt(m, "baseFactor"));
            dto.setFilterPeriod(getInt(m, "filterPeriod"));
            dto.setDecayPeriod(getInt(m, "decayPeriod"));
            dto.setReductionFactor(getInt(m, "reductionFactor"));
            dto.setVariableFeeControl(getInt(m, "variableFeeControl"));
            dto.setProtocolShare(getInt(m, "protocolShare"));
            dto.setMaxVolatilityAccumulator(getInt(m, "maxVolatilityAccumulator"));
            dto.setOpen(getBool(m, "open"));
            return dto;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static DlmmStaticFeeParametersDto parseStaticFeeParameters(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            return new DlmmStaticFeeParametersDto(
                    getInt(m, "baseFactor"),
                    getInt(m, "filterPeriod"),
                    getInt(m, "decayPeriod"),
                    getInt(m, "reductionFactor"),
                    getInt(m, "variableFeeControl"),
                    getInt(m, "protocolShare"),
                    getInt(m, "maxVolatilityAccumulator"));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static DlmmVariableFeeParametersDto parseVariableFeeParameters(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            return new DlmmVariableFeeParametersDto(
                    getInt(m, "volatilityAccumulator"),
                    getInt(m, "volatilityReference"),
                    getInt(m, "idReference"),
                    getLong(m, "timeOfLastUpdate"));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static DlmmSwapInResultDto parseSwapInResult(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            return new DlmmSwapInResultDto(
                    getBigInt(m, "amountIn"),
                    getBigInt(m, "amountOutLeft"),
                    getBigInt(m, "fee"));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static DlmmSwapOutResultDto parseSwapOutResult(Object result) {
        if (result == null) return null;
        if (result instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) result;
            return new DlmmSwapOutResultDto(
                    getBigInt(m, "amountInLeft"),
                    getBigInt(m, "amountOut"),
                    getBigInt(m, "fee"));
        }
        return null;
    }

    private static int getInt(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(String.valueOf(v));
    }

    private static long getLong(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(String.valueOf(v));
    }

    private static boolean getBool(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return false;
        if (v instanceof Boolean) return (Boolean) v;
        return Boolean.parseBoolean(String.valueOf(v));
    }

    private static String getStr(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static BigInteger getBigInt(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return BigInteger.ZERO;
        if (v instanceof BigInteger) return (BigInteger) v;
        return new BigInteger(String.valueOf(v));
    }

    // ======================== LBFactory view ========================

    /**
     * LBFactory.getOwner
     */
    public String getFactoryOwner(int chainId, String factoryAddress) throws Exception {
        String method = "getOwner";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getMinBinStep
     */
    public BigInteger getFactoryMinBinStep(int chainId, String factoryAddress) throws Exception {
        String method = "getMinBinStep";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return new BigInteger(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getFeeRecipient
     */
    public String getFactoryFeeRecipient(int chainId, String factoryAddress) throws Exception {
        String method = "getFeeRecipient";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getLBPairImplementation
     */
    public String getFactoryLBPairImplementation(int chainId, String factoryAddress) throws Exception {
        String method = "getLBPairImplementation";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.isQuoteAsset
     */
    public boolean isQuoteAsset(int chainId, String factoryAddress, String token) throws Exception {
        String method = "isQuoteAsset";
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{token};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return Boolean.parseBoolean(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getLBPairInformation，返回 DTO，不存在时可能为 null（视链上序列化而定）。
     */
    public DlmmLBPairInformationDto getLBPairInformation(int chainId, String factoryAddress, String tokenA, String tokenB, BigInteger binStep) throws Exception {
        String method = "getLBPairInformation";
        String[] types = new String[]{"String", "String", "BigInteger"};
        Object[] args = new Object[]{tokenA, tokenB, binStep};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseLBPairInformation(rpcResult.getResult());
        if (rpcResult.getError() != null) throw new RuntimeException(rpcResult.getError().toString());
        return null;
    }

    /**
     * LBFactory.getPreset，返回 DTO，无预设时可能为 null。
     */
    public DlmmPresetConfigDto getPreset(int chainId, String factoryAddress, int binStep) throws Exception {
        String method = "getPreset";
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{binStep};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return parsePresetConfig(rpcResult.getResult());
        if (rpcResult.getError() != null) throw new RuntimeException(rpcResult.getError().toString());
        return null;
    }

    /**
     * LBFactory.getNumberOfLBPairs
     */
    public BigInteger getNumberOfLBPairs(int chainId, String factoryAddress) throws Exception {
        String method = "getNumberOfLBPairs";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return new BigInteger(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getLBPairAtIndex
     */
    public String getLBPairAtIndex(int chainId, String factoryAddress, BigInteger index) throws Exception {
        String method = "getLBPairAtIndex";
        String[] types = new String[]{"BigInteger"};
        Object[] args = new Object[]{index};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getNumberOfQuoteAssets
     */
    public BigInteger getNumberOfQuoteAssets(int chainId, String factoryAddress) throws Exception {
        String method = "getNumberOfQuoteAssets";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return new BigInteger(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getQuoteAssetAtIndex
     */
    public String getQuoteAssetAtIndex(int chainId, String factoryAddress, BigInteger index) throws Exception {
        String method = "getQuoteAssetAtIndex";
        String[] types = new String[]{"BigInteger"};
        Object[] args = new Object[]{index};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBFactory.getAllBinSteps
     */
    @SuppressWarnings("unchecked")
    public List<BigInteger> getAllBinSteps(int chainId, String factoryAddress) throws Exception {
        String method = "getAllBinSteps";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() == null) throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
        Object res = rpcResult.getResult();
        if (res instanceof List) {
            List<Object> list = (List<Object>) res;
            List<BigInteger> out = new ArrayList<>();
            for (Object o : list) {
                if (o != null) out.add(new BigInteger(o.toString()));
            }
            return out;
        }
        return new ArrayList<>();
    }

    /**
     * LBFactory.getOpenBinSteps
     */
    @SuppressWarnings("unchecked")
    public List<BigInteger> getOpenBinSteps(int chainId, String factoryAddress) throws Exception {
        String method = "getOpenBinSteps";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() == null) throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
        Object res = rpcResult.getResult();
        if (res instanceof List) {
            List<Object> list = (List<Object>) res;
            List<BigInteger> out = new ArrayList<>();
            for (Object o : list) {
                if (o != null) out.add(new BigInteger(o.toString()));
            }
            return out;
        }
        return new ArrayList<>();
    }

    /**
     * LBFactory.getAllLBPairs(tokenX, tokenY)，返回 DTO 列表。
     */
    @SuppressWarnings("unchecked")
    public List<DlmmLBPairInformationDto> getAllLBPairs(int chainId, String factoryAddress, String tokenX, String tokenY) throws Exception {
        String method = "getAllLBPairs";
        String[] types = new String[]{"String", "String"};
        Object[] args = new Object[]{tokenX, tokenY};
        RpcResult rpcResult = callViewSilent(chainId, factoryAddress, method, types, args);
        if (rpcResult.getResult() == null) throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
        Object res = rpcResult.getResult();
        if (res instanceof List) {
            List<Object> list = (List<Object>) res;
            List<DlmmLBPairInformationDto> out = new ArrayList<>();
            for (Object o : list) {
                DlmmLBPairInformationDto dto = parseLBPairInformation(o);
                if (dto != null) out.add(dto);
            }
            return out;
        }
        return new ArrayList<>();
    }

    // ======================== LBFactory 写方法（仅组装交易） ========================

    /**
     * LBFactory.createLBPair：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> createLBPairTx(int chainId, String factoryAddress, String fromAddress,
                                       String tokenX, String tokenY, int activeId, int binStep) throws Exception {
        String[] types = new String[]{"String", "String", "int", "int"};
        Object[] args = new Object[]{tokenX, tokenY, activeId, binStep};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "createLBPair", types, args, null);
    }

    /**
     * LBFactory.addQuoteAsset：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> addQuoteAssetTx(int chainId, String factoryAddress, String fromAddress, String quoteAsset) throws Exception {
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{quoteAsset};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "addQuoteAsset", types, args, null);
    }

    /**
     * LBFactory.removeQuoteAsset：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> removeQuoteAssetTx(int chainId, String factoryAddress, String fromAddress, String quoteAsset) throws Exception {
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{quoteAsset};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "removeQuoteAsset", types, args, null);
    }

    /**
     * LBFactory.setPreset：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> setPresetTx(int chainId, String factoryAddress, String fromAddress,
                                   int binStep, int baseFactor, int filterPeriod, int decayPeriod,
                                   int reductionFactor, int variableFeeControl, int protocolShare,
                                   int maxVolatilityAccumulator, boolean isOpen) throws Exception {
        String[] types = new String[]{"int", "int", "int", "int", "int", "int", "int", "int", "boolean"};
        Object[] args = new Object[]{binStep, baseFactor, filterPeriod, decayPeriod, reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator, isOpen};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "setPreset", types, args, null);
    }

    /**
     * LBFactory.setPresetOpenState：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> setPresetOpenStateTx(int chainId, String factoryAddress, String fromAddress, int binStep, boolean isOpen) throws Exception {
        String[] types = new String[]{"int", "boolean"};
        Object[] args = new Object[]{binStep, isOpen};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "setPresetOpenState", types, args, null);
    }

    /**
     * LBFactory.removePreset：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> removePresetTx(int chainId, String factoryAddress, String fromAddress, int binStep) throws Exception {
        String[] types = new String[]{"int"};
        Object[] args = new Object[]{binStep};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "removePreset", types, args, null);
    }

    /**
     * LBFactory.setLBPairIgnored：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> setLBPairIgnoredTx(int chainId, String factoryAddress, String fromAddress,
                                           String tokenX, String tokenY, int binStep, boolean ignored) throws Exception {
        String[] types = new String[]{"String", "String", "int", "boolean"};
        Object[] args = new Object[]{tokenX, tokenY, binStep, ignored};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "setLBPairIgnored", types, args, null);
    }

    /**
     * LBFactory.setFeesParametersOnPair：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> setFeesParametersOnPairTx(int chainId, String factoryAddress, String fromAddress,
                                                 String tokenX, String tokenY, int binStep,
                                                 int baseFactor, int filterPeriod, int decayPeriod,
                                                 int reductionFactor, int variableFeeControl, int protocolShare,
                                                 int maxVolatilityAccumulator) throws Exception {
        String[] types = new String[]{"String", "String", "int", "int", "int", "int", "int", "int", "int", "int"};
        Object[] args = new Object[]{tokenX, tokenY, binStep, baseFactor, filterPeriod, decayPeriod, reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "setFeesParametersOnPair", types, args, null);
    }

    /**
     * LBFactory.setFeeRecipient：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> setFeeRecipientTx(int chainId, String factoryAddress, String fromAddress, String feeRecipient) throws Exception {
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{feeRecipient};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "setFeeRecipient", types, args, null);
    }

    /**
     * LBFactory.forceDecay(pairAddress)：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> forceDecayTx(int chainId, String factoryAddress, String fromAddress, String pairAddress) throws Exception {
        String[] types = new String[]{"String"};
        Object[] args = new Object[]{pairAddress};
        return assembleCallTx(chainId, fromAddress, factoryAddress, "forceDecay", types, args, null);
    }

    // ======================== LBPair view（补充） ========================

    /**
     * LBPair.getFactory
     */
    public String getFactory(int chainId, String pairAddress) throws Exception {
        String method = "getFactory";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getReserves，返回 [reserveX, reserveY]（已扣除 protocol fees 的净储备）。
     */
    public BigInteger[] getReserves(int chainId, String pairAddress) throws Exception {
        String method = "getReserves";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) {
            String value = rpcResult.getResult().toString();
            String[] split = value.split(",");
            if (split.length >= 2) {
                return new BigInteger[]{new BigInteger(split[0].trim()), new BigInteger(split[1].trim())};
            }
        }
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getProtocolFees，返回 DTO（feeX, feeY）。链上可能返回 "feeX,feeY" 或 Map。
     */
    @SuppressWarnings("unchecked")
    public DlmmProtocolFeesDto getProtocolFees(int chainId, String pairAddress) throws Exception {
        String method = "getProtocolFees";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() == null) throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
        Object res = rpcResult.getResult();
        if (res instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) res;
            return new DlmmProtocolFeesDto(getBigInt(m, "feeX"), getBigInt(m, "feeY"));
        }
        String s = res.toString();
        String[] parts = s.split(",");
        if (parts.length >= 2) {
            return new DlmmProtocolFeesDto(new BigInteger(parts[0].trim()), new BigInteger(parts[1].trim()));
        }
        return new DlmmProtocolFeesDto(BigInteger.ZERO, BigInteger.ZERO);
    }

    /**
     * LBPair.getStaticFeeParameters，返回 DTO。
     */
    public DlmmStaticFeeParametersDto getStaticFeeParameters(int chainId, String pairAddress) throws Exception {
        String method = "getStaticFeeParameters";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseStaticFeeParameters(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getVariableFeeParameters，返回 DTO。
     */
    public DlmmVariableFeeParametersDto getVariableFeeParameters(int chainId, String pairAddress) throws Exception {
        String method = "getVariableFeeParameters";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseVariableFeeParameters(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getIdFromPrice
     */
    public int getIdFromPrice(int chainId, String pairAddress, BigInteger price) throws Exception {
        String method = "getIdFromPrice";
        String[] types = new String[]{"BigInteger"};
        Object[] args = new Object[]{price};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return Integer.parseInt(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getSwapIn(amountOut, swapForY)，返回 DTO。
     */
    public DlmmSwapInResultDto getSwapIn(int chainId, String pairAddress, BigInteger amountOut, boolean swapForY) throws Exception {
        String method = "getSwapIn";
        String[] types = new String[]{"BigInteger", "boolean"};
        Object[] args = new Object[]{amountOut, swapForY};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseSwapInResult(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBPair.getSwapOut(amountIn, swapForY)，返回 DTO。
     */
    public DlmmSwapOutResultDto getSwapOut(int chainId, String pairAddress, BigInteger amountIn, boolean swapForY) throws Exception {
        String method = "getSwapOut";
        String[] types = new String[]{"BigInteger", "boolean"};
        Object[] args = new Object[]{amountIn, swapForY};
        RpcResult rpcResult = callViewSilent(chainId, pairAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseSwapOutResult(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    // ======================== LBPair 写方法（仅组装交易） ========================

    /**
     * LBPair.mint：仅组装交易。需在 msgValue 中转入 tokenX/tokenY，此处由调用方传入 msgValue。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> mintTx(int chainId, String pairAddress, String fromAddress,
                              String to, int[] binIds, BigInteger[] distributionX, BigInteger[] distributionY, String refundTo,
                              Map<String, BigInteger> msgValue) throws Exception {
        String[] types = new String[]{"String", "int[]", "BigInteger[]", "BigInteger[]", "String"};
        Object[] args = new Object[]{to, binIds, distributionX, distributionY, refundTo};
        return assembleCallTx(chainId, fromAddress, pairAddress, "mint", types, args, msgValue);
    }

    /**
     * LBPair.burn：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> burnTx(int chainId, String pairAddress, String fromAddress,
                              String from, String to, int[] ids, BigInteger[] amountsToBurn) throws Exception {
        String[] types = new String[]{"String", "String", "int[]", "BigInteger[]"};
        Object[] args = new Object[]{from, to, ids, amountsToBurn};
        return assembleCallTx(chainId, fromAddress, pairAddress, "burn", types, args, null);
    }

    /**
     * LBPair.swap(swapForY, to)：仅组装交易。需在 msgValue 中转入输入代币。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> swapTx(int chainId, String pairAddress, String fromAddress, boolean swapForY, String to, Map<String, BigInteger> msgValue) throws Exception {
        String[] types = new String[]{"boolean", "String"};
        Object[] args = new Object[]{swapForY, to};
        return assembleCallTx(chainId, fromAddress, pairAddress, "swap", types, args, msgValue);
    }

    /**
     * LBPair.collectProtocolFees：仅组装交易（仅 feeRecipient 可调）。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> collectProtocolFeesTx(int chainId, String pairAddress, String fromAddress) throws Exception {
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        return assembleCallTx(chainId, fromAddress, pairAddress, "collectProtocolFees", types, args, null);
    }

    // ======================== LBRouter view 补充 ========================

    /**
     * LBRouter.getFactoryAddress
     */
    public String getRouterFactoryAddress(int chainId, String routerAddress) throws Exception {
        String method = "getFactoryAddress";
        String[] types = new String[]{};
        Object[] args = new Object[]{};
        RpcResult rpcResult = callViewSilent(chainId, routerAddress, method, types, args);
        if (rpcResult.getResult() != null) return rpcResult.getResult().toString();
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBRouter.getIdFromPrice(pairAddress, price)
     */
    public int getRouterIdFromPrice(int chainId, String routerAddress, String pairAddress, BigInteger price) throws Exception {
        String method = "getIdFromPrice";
        String[] types = new String[]{"String", "BigInteger"};
        Object[] args = new Object[]{pairAddress, price};
        RpcResult rpcResult = callViewSilent(chainId, routerAddress, method, types, args);
        if (rpcResult.getResult() != null) return Integer.parseInt(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBRouter.getPriceFromId(pairAddress, id)
     */
    public BigInteger getRouterPriceFromId(int chainId, String routerAddress, String pairAddress, int id) throws Exception {
        String method = "getPriceFromId";
        String[] types = new String[]{"String", "int"};
        Object[] args = new Object[]{pairAddress, id};
        RpcResult rpcResult = callViewSilent(chainId, routerAddress, method, types, args);
        if (rpcResult.getResult() != null) return new BigInteger(rpcResult.getResult().toString());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBRouter.getSwapIn(pairAddress, amountOut, swapForY)，返回 DTO。
     */
    public DlmmSwapInResultDto getRouterSwapIn(int chainId, String routerAddress, String pairAddress, BigInteger amountOut, boolean swapForY) throws Exception {
        String method = "getSwapIn";
        String[] types = new String[]{"String", "BigInteger", "boolean"};
        Object[] args = new Object[]{pairAddress, amountOut, swapForY};
        RpcResult rpcResult = callViewSilent(chainId, routerAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseSwapInResult(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    /**
     * LBRouter.getSwapOut(pairAddress, amountIn, swapForY)，返回 DTO。
     */
    public DlmmSwapOutResultDto getRouterSwapOut(int chainId, String routerAddress, String pairAddress, BigInteger amountIn, boolean swapForY) throws Exception {
        String method = "getSwapOut";
        String[] types = new String[]{"String", "BigInteger", "boolean"};
        Object[] args = new Object[]{pairAddress, amountIn, swapForY};
        RpcResult rpcResult = callViewSilent(chainId, routerAddress, method, types, args);
        if (rpcResult.getResult() != null) return parseSwapOutResult(rpcResult.getResult());
        throw new RuntimeException(rpcResult.getError() != null ? rpcResult.getError().toString() : "null error");
    }

    // ======================== LBRouter 写方法（仅组装交易）补充 ========================

    /**
     * LBRouter.createLBPair：通过 Router 调 Factory 创建交易对，仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> createLBPairViaRouterTx(int chainId, String routerAddress, String fromAddress,
                                                String tokenX, String tokenY, int activeId, int binStep) throws Exception {
        String[] types = new String[]{"String", "String", "int", "int"};
        Object[] args = new Object[]{tokenX, tokenY, activeId, binStep};
        return assembleCallTx(chainId, fromAddress, routerAddress, "createLBPair", types, args, null);
    }

    /**
     * LBRouter.sweep(token, to, amount)：仅组装交易。需在 msgValue 中转入 token。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> sweepTx(int chainId, String routerAddress, String fromAddress, String token, String to, BigInteger amount, Map<String, BigInteger> msgValue) throws Exception {
        String[] types = new String[]{"String", "String", "BigInteger"};
        Object[] args = new Object[]{token, to, amount};
        return assembleCallTx(chainId, fromAddress, routerAddress, "sweep", types, args, msgValue);
    }

    /**
     * LBRouter.sweepLBToken(lbTokenAddress, to, ids, amounts)：仅组装交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> sweepLBTokenTx(int chainId, String routerAddress, String fromAddress,
                                       String lbTokenAddress, String to, int[] ids, BigInteger[] amounts) throws Exception {
        String[] types = new String[]{"String", "String", "int[]", "BigInteger[]"};
        Object[] args = new Object[]{lbTokenAddress, to, ids, amounts};
        return assembleCallTx(chainId, fromAddress, routerAddress, "sweepLBToken", types, args, null);
    }

    /**
     * 查询账户的某个 token 余额（基于 NerveSDKTool.getAccountBalance）。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public BigInteger getTokenBalance(String account, String token) throws Exception {
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
     * 将 18 精度的整数格式化为字符串。
     */
    public String format18(BigInteger value) {
        if (value == null || value.compareTo(BigInteger.ZERO) == 0) {
            return "0";
        }
        BigDecimal decimal = new BigDecimal(value)
                .divide(new BigDecimal("1000000000000000000"), 6, RoundingMode.HALF_UP);
        return decimal.toPlainString();
    }

    /**
     * 查询某个交易对的流动性分布，返回数据 DTO。可通过 {@link DlmmLiquidityDistributionDto#toString()} 输出表格字符串。
     */
    public DlmmLiquidityDistributionDto getLiquidityDistribution(int chainId, String pair) throws Exception {
        String tokenX = getTokenX(chainId, pair);
        String tokenY = getTokenY(chainId, pair);
        int activeId = getActiveId(chainId, pair);
        int binStep = getBinStep(chainId, pair);

        java.util.Set<Integer> allBins = new java.util.HashSet<>();
        allBins.add(activeId);

        int currentId = activeId;
        int maxIterations = 1000;
        int iterations = 0;
        while (iterations < maxIterations) {
            int nextId = getNextNonEmptyBin(chainId, pair, true, currentId);
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
            int nextId = getNextNonEmptyBin(chainId, pair, false, currentId);
            if (nextId == 0 || nextId == 0xFFFFFF || nextId == currentId) {
                break;
            }
            allBins.add(nextId);
            currentId = nextId;
            iterations++;
        }

        java.util.List<Integer> sortedBins = new ArrayList<>(allBins);
        sortedBins.sort(Integer::compareTo);

        DlmmLiquidityDistributionDto dto = new DlmmLiquidityDistributionDto();
        dto.setTokenX(tokenX);
        dto.setTokenY(tokenY);
        dto.setBinStep(binStep);
        dto.setActiveId(activeId);

        BigInteger totalReserveX = BigInteger.ZERO;
        BigInteger totalReserveY = BigInteger.ZERO;
        BigInteger totalSupply = BigInteger.ZERO;
        List<DlmmLiquidityDistributionDto.DlmmBinInfoDto> binList = new ArrayList<>();

        for (int binId : sortedBins) {
            BigInteger[] binReserves = getBin(chainId, pair, binId);
            BigInteger binSupply = totalSupply(chainId, pair, binId);
            BigInteger price = getPriceFromId(chainId, pair, binId);

            DlmmLiquidityDistributionDto.DlmmBinInfoDto binInfo = new DlmmLiquidityDistributionDto.DlmmBinInfoDto();
            binInfo.setBinId(binId);
            binInfo.setReserveX(binReserves[0]);
            binInfo.setReserveY(binReserves[1]);
            binInfo.setSupply(binSupply);
            binInfo.setPrice(price);
            binInfo.setActive(binId == activeId);
            binList.add(binInfo);

            totalReserveX = totalReserveX.add(binReserves[0]);
            totalReserveY = totalReserveY.add(binReserves[1]);
            totalSupply = totalSupply.add(binSupply);
        }

        dto.setBins(binList);
        dto.setTotalReserveX(totalReserveX);
        dto.setTotalReserveY(totalReserveY);
        dto.setTotalSupply(totalSupply);
        return dto;
    }

    /**
     * 仅组装 AnyBus CALL 交易，不签名、不广播。
     *
     * @param chainId      链ID
     * @param fromAddress  发起人地址
     * @param contractAddr 合约地址（如 LBRouter）
     * @param method       方法名
     * @param paramTypes   参数类型
     * @param args         参数值
     * @param msgValue     转入合约的资产 tokenId -> amount，可为 null 表示只扣主链主资产手续费
     * @return Result 成功时 data 为 Map，包含 "txHex"（未签名交易 hex）、"hash"（交易 hash）
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> assembleCallTx(int chainId, String fromAddress, String contractAddr, String method,
                                      String[] paramTypes, Object[] args, Map<String, BigInteger> msgValue) throws Exception {
        byte[] fromBytes = AddressTool.getAddress(fromAddress);
        byte[] contractBytes = AddressTool.getAddress(contractAddr);
        String remark = "call AnyBus contract";

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
                int assetChainId = Integer.parseInt(split[0]);
                int assetId = Integer.parseInt(split[1]);
                String nonce = getBalanceAndNonce(fromAddress, assetChainId, assetId);
                froms.add(new CoinFrom(
                        fromBytes,
                        assetChainId,
                        assetId,
                        value,
                        HexUtil.decode(nonce),
                        (byte) 0));
                tos.add(new CoinTo(
                        contractBytes,
                        assetChainId,
                        assetId,
                        value));
            }
        } else {
            String nonce = getBalanceAndNonce(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
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

        String txHex = HexUtil.encode(tx.serialize());
        String hash = tx.getHash().toHex();
        Map<String, Object> data = new HashMap<>();
        data.put("txHex", txHex);
        data.put("hash", hash);
        return Result.getSuccess(data);
    }

    /**
     * LBRouter 添加流动性：仅组装未签名交易，不签名、不广播。
     *
     * @param tokenX         tokenX 标识，如 "5-4"
     * @param tokenY         tokenY 标识
     * @param binStep        binStep
     * @param amountX        amountX
     * @param amountY        amountY
     * @param amountXMin     最小 amountX（滑点）
     * @param amountYMin     最小 amountY（滑点）
     * @param activeId       activeId
     * @param idSlippage     idSlippage
     * @param deltaIds       deltaIds
     * @param distributionX  distributionX
     * @param distributionY  distributionY
     * @param to             接收 LP 的地址
     * @param deadline       过期时间戳（秒）
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> addLiquidityTx(int chainId, String routerAddress, String fromAddress,
                                      String tokenX, String tokenY, int binStep,
                                      BigInteger amountX, BigInteger amountY,
                                      BigInteger amountXMin, BigInteger amountYMin,
                                      int activeId, int idSlippage,
                                      int[] deltaIds, BigInteger[] distributionX, BigInteger[] distributionY,
                                      String to, long deadline) throws Exception {
        String[] types = new String[]{
                "String", "String", "int", "BigInteger", "BigInteger",
                "BigInteger", "BigInteger", "int", "int", "int[]",
                "BigInteger[]", "BigInteger[]", "String", "String", "long"
        };
        Object[] args = new Object[]{
                tokenX, tokenY, binStep, amountX, amountY,
                amountXMin, amountYMin, activeId, idSlippage,
                deltaIds, distributionX, distributionY, to, to, deadline
        };
        Map<String, BigInteger> msgValue = new HashMap<>();
        msgValue.put(tokenX, amountX);
        msgValue.put(tokenY, amountY);
        return assembleCallTx(chainId, fromAddress, routerAddress, "addLiquidity", types, args, msgValue);
    }

    /**
     * LBRouter 移除流动性：仅组装未签名交易，不签名、不广播。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> removeLiquidityTx(int chainId, String routerAddress, String fromAddress,
                                         String tokenX, String tokenY, int binStep,
                                         BigInteger amountXMin, BigInteger amountYMin,
                                         int[] ids, BigInteger[] amounts,
                                         String to, long deadline) throws Exception {
        String[] types = new String[]{
                "String", "String", "int", "BigInteger", "BigInteger",
                "int[]", "BigInteger[]", "String", "long"
        };
        Object[] args = new Object[]{
                tokenX, tokenY, binStep, amountXMin, amountYMin,
                ids, amounts, to, deadline
        };
        return assembleCallTx(chainId, fromAddress, routerAddress, "removeLiquidity", types, args, null);
    }

    /**
     * LBRouter 精确输入兑换 swapExactTokensForTokens：仅组装未签名交易，不签名、不广播。
     *
     * @param amountIn     输入数量（18 精度）
     * @param amountOutMin 最小输出数量（滑点）
     * @param pairBinSteps 路径上每对的 binStep，如 [10]
     * @param tokenPath    代币路径，如 [tokenA, tokenB]
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> swapExactTokensForTokensTx(int chainId, String routerAddress, String fromAddress,
                                                  BigInteger amountIn, BigInteger amountOutMin,
                                                  int[] pairBinSteps, String[] tokenPath,
                                                  String to, long deadline) throws Exception {
        String[] types = new String[]{
                "BigInteger", "BigInteger", "int[]", "String[]", "String", "long"
        };
        Object[] args = new Object[]{amountIn, amountOutMin, pairBinSteps, tokenPath, to, deadline};
        Map<String, BigInteger> msgValue = new HashMap<>();
        if (tokenPath != null && tokenPath.length > 0) {
            msgValue.put(tokenPath[0], amountIn);
        }
        return assembleCallTx(chainId, fromAddress, routerAddress, "swapExactTokensForTokens", types, args, msgValue);
    }

    /**
     * LBRouter 精确输出兑换 swapTokensForExactTokens：仅组装未签名交易，不签名、不广播。
     *
     * @param amountOut   期望输出数量（18 精度）
     * @param amountInMax 最大输入数量（18 精度）
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> swapTokensForExactTokensTx(int chainId, String routerAddress, String fromAddress,
                                                  BigInteger amountOut, BigInteger amountInMax,
                                                  int[] pairBinSteps, String[] tokenPath,
                                                  String to, long deadline) throws Exception {
        String[] types = new String[]{
                "BigInteger", "BigInteger", "int[]", "String[]", "String", "long"
        };
        Object[] args = new Object[]{amountOut, amountInMax, pairBinSteps, tokenPath, to, deadline};
        Map<String, BigInteger> msgValue = new HashMap<>();
        if (tokenPath != null && tokenPath.length > 0) {
            msgValue.put(tokenPath[0], amountInMax);
        }
        return assembleCallTx(chainId, fromAddress, routerAddress, "swapTokensForExactTokens", types, args, msgValue);
    }

    /**
     * 组装并广播 AnyBus CALL 交易（需私钥，会签名并广播）。
     * 返回广播结果。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result callContract(int chainId, String userKey, String contractAddr, String method,
                               String[] paramTypes, Object[] args, Map<String, BigInteger> msgValue) throws Exception {
        Account account = AccountTool.createAccount(chainId, userKey);
        String from = account.getAddress().getBase58();
        Result<Map> assembleResult = assembleCallTx(chainId, from, contractAddr, method, paramTypes, args, msgValue);
        if (!assembleResult.isSuccess()) {
            return assembleResult;
        }
        String txHex = (String) assembleResult.getData().get("txHex");
        Result<Map> signResult = NerveSDKTool.sign(txHex, from, userKey);
        txHex = (String) signResult.getData().get("txHex");
        Result<Map> broadcastResult = NerveSDKTool.broadcast(txHex);
        return Result.getSuccess(broadcastResult.getData());
    }

    /**
     * 仅组装 AnyBus CREATE 合约交易，不签名、不广播。
     *
     * @param chainId     链ID
     * @param fromAddress 发起地址
     * @param txData      AnyBusTxData（如 CREATE_LB_FACTORY / CREATE_LB_ROUTER）
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> assembleCreateContractTx(int chainId, String fromAddress, AnyBusTxData txData) throws Exception {
        byte[] fromBytes = AddressTool.getAddress(fromAddress);
        String nonce = getBalanceAndNonce(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        String remark = "create AnyBus contract";

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

        String txHex = HexUtil.encode(tx.serialize());
        String hash = tx.getHash().toHex();
        Map<String, Object> data = new HashMap<>();
        data.put("txHex", txHex);
        data.put("hash", hash);
        return Result.getSuccess(data);
    }

    /**
     * 组装创建 LBFactory 的 AnyBus 交易（CREATE_LB_FACTORY），返回未签名 txHex 和 hash。
     *
     * @param chainId      链ID
     * @param fromAddress  发起地址
     * @param feeRecipient 手续费接收地址
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> createFactoryTx(int chainId, String fromAddress, String feeRecipient) throws Exception {
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CREATE_LB_FACTORY.type());
        CreateLBFactory create = new CreateLBFactory();
        create.setFeeRecipient(feeRecipient);
        txData.setData(create.serialize());
        return assembleCreateContractTx(chainId, fromAddress, txData);
    }

    /**
     * 组装创建 LBRouter 的 AnyBus 交易（CREATE_LB_ROUTER），返回未签名 txHex 和 hash。
     *
     * @param chainId       链ID
     * @param fromAddress   发起地址
     * @param factoryAdress LBFactory 合约地址
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result<Map> createRouterTx(int chainId, String fromAddress, String factoryAdress) throws Exception {
        AnyBusTxData txData = new AnyBusTxData();
        txData.setType(AnyBusType.CREATE_LB_ROUTER.type());
        CreateLBRouter create = new CreateLBRouter();
        create.setFactory(factoryAdress);
        txData.setData(create.serialize());
        return assembleCreateContractTx(chainId, fromAddress, txData);
    }

    /**
     * 组装并广播 AnyBus CREATE 合约交易。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Result createContract(int chainId, String userKey, AnyBusTxData txData) throws Exception {
        Account account = AccountTool.createAccount(chainId, userKey);
        byte[] fromBytes = account.getAddress().getAddressBytes();
        String from = account.getAddress().getBase58();
        String nonce = getBalanceAndNonce(from, SDKContext.main_chain_id, SDKContext.main_asset_id);
        String remark = "create AnyBus contract";

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
        Result<Map> signResult = NerveSDKTool.sign(txHex, from, userKey);
        txHex = (String) signResult.getData().get("txHex");

        // 广播交易
        Result<Map> broadcastResult = NerveSDKTool.broadcast(txHex);
        return Result.getSuccess(broadcastResult.getData());
    }

    /**
     * 查询账户某资产的 nonce。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getBalanceAndNonce(String addr, int chainId, int assetId) {
        Result result = NerveSDKTool.getAccountBalance(addr, chainId, assetId);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.toString());
        }
        Map data = (Map) result.getData();
        return data.get("nonce").toString();
    }

    /**
     * 将 32 字节的 DLMM bin 编码解码成 [X, Y]。
     */
    public BigInteger[] decodeBinXY(byte[] data) {
        if (data == null || data.length != 32) {
            return new BigInteger[]{BigInteger.ZERO, BigInteger.ZERO};
        }
        byte[] yBytes = new byte[16];
        byte[] xBytes = new byte[16];
        System.arraycopy(data, 0, yBytes, 0, 16);
        System.arraycopy(data, 16, xBytes, 0, 16);
        return new BigInteger[]{
                new BigInteger(1, xBytes),
                new BigInteger(1, yBytes)
        };
    }
}

