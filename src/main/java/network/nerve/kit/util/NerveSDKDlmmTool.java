package network.nerve.kit.util;

import network.nerve.core.basic.Result;
import network.nerve.core.rpc.model.*;
import network.nerve.kit.model.dto.DlmmLiquidityDistributionDto;
import network.nerve.kit.service.DlmmService;

import java.math.BigInteger;
import java.util.Map;

/**
 * DLMM 相关 SDK 封装工具类。
 *
 * 说明：
 * - 原本挂在 {@link NerveSDKTool} 下的 dlmm* 系列方法，迁移到此类中并去掉前缀；
 * - 只负责 DLMM（LBFactory / LBRouter / LBPair）相关的查询与交易组装。
 */
public class NerveSDKDlmmTool {

    private static final DlmmService dlmmService = DlmmService.getInstance();

    // ============================= LBPair 视图方法 =============================

    @ApiOperation(description = "查询下一个非空BinId", order = 400, detailDesc = "根据交易对和当前BinId，查询下一个有流动性的BinId")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "swapForY", parameterDes = "方向标识，true向左查找，false向右查找", requestType = @TypeDescriptor(value = boolean.class)),
            @Parameter(parameterName = "id", parameterDes = "起始BinId", requestType = @TypeDescriptor(value = int.class))
    })
    @ResponseData(name = "返回值", description = "下一个非空BinId", responseType = @TypeDescriptor(value = int.class))
    public static int getNextNonEmptyBin(int chainId, String pairAddress, boolean swapForY, int id) throws Exception {
        return dlmmService.getNextNonEmptyBin(chainId, pairAddress, swapForY, id);
    }

    @ApiOperation(description = "查询交易对TokenX", order = 401)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", description = "TokenX 资产标识", responseType = @TypeDescriptor(value = String.class))
    public static String getTokenX(int chainId, String pairAddress) throws Exception {
        return dlmmService.getTokenX(chainId, pairAddress);
    }

    @ApiOperation(description = "查询交易对TokenY", order = 402)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", description = "TokenY 资产标识", responseType = @TypeDescriptor(value = String.class))
    public static String getTokenY(int chainId, String pairAddress) throws Exception {
        return dlmmService.getTokenY(chainId, pairAddress);
    }

    @ApiOperation(description = "查询交易对ActiveId", order = 403)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", description = "当前活跃BinId", responseType = @TypeDescriptor(value = int.class))
    public static int getActiveId(int chainId, String pairAddress) throws Exception {
        return dlmmService.getActiveId(chainId, pairAddress);
    }

    @ApiOperation(description = "查询交易对BinStep", order = 404)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", description = "BinStep", responseType = @TypeDescriptor(value = int.class))
    public static int getBinStep(int chainId, String pairAddress) throws Exception {
        return dlmmService.getBinStep(chainId, pairAddress);
    }

    @ApiOperation(description = "查询指定Bin的储备量", order = 405)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binId", parameterDes = "BinId", requestType = @TypeDescriptor(value = int.class))
    })
    @ResponseData(name = "返回值", description = "[reserveX, reserveY]", responseType = @TypeDescriptor(value = BigInteger[].class))
    public static BigInteger[] getBin(int chainId, String pairAddress, int binId) throws Exception {
        return dlmmService.getBin(chainId, pairAddress, binId);
    }

    @ApiOperation(description = "查询指定Bin的LP总量", order = 406)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binId", parameterDes = "BinId", requestType = @TypeDescriptor(value = int.class))
    })
    @ResponseData(name = "返回值", description = "该Bin下的LP总量", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger getTotalSupply(int chainId, String pairAddress, int binId) throws Exception {
        return dlmmService.totalSupply(chainId, pairAddress, binId);
    }

    @ApiOperation(description = "查询指定Bin的价格", order = 407)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binId", parameterDes = "BinId", requestType = @TypeDescriptor(value = int.class))
    })
    @ResponseData(name = "返回值", description = "价格（18精度整数）", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger getPriceFromId(int chainId, String pairAddress, int binId) throws Exception {
        return dlmmService.getPriceFromId(chainId, pairAddress, binId);
    }

    @ApiOperation(description = "查询账户在指定Bin的LP余额", order = 408)
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "account", parameterDes = "账户地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binId", parameterDes = "BinId", requestType = @TypeDescriptor(value = int.class))
    })
    @ResponseData(name = "返回值", description = "LP余额（18精度整数）", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger getBalanceOf(int chainId, String pairAddress, String account, int binId) throws Exception {
        return dlmmService.getBalanceOf(chainId, pairAddress, account, binId);
    }

    @ApiOperation(description = "查询交易对流动性分布", order = 409, detailDesc = "返回数据 DTO，可通过 toString() 得到表格字符串")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", description = "流动性分布 DTO", responseType = @TypeDescriptor(value = DlmmLiquidityDistributionDto.class))
    public static DlmmLiquidityDistributionDto getLiquidityDistribution(int chainId, String pairAddress) throws Exception {
        return dlmmService.getLiquidityDistribution(chainId, pairAddress);
    }

    @ApiOperation(description = "打印交易对流动性分布到控制台", order = 410, detailDesc = "获取流动性分布后输出 DTO 的 toString()，便于调试")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "pairAddress", parameterDes = "交易对地址", requestType = @TypeDescriptor(value = String.class))
    })
    public static void printLiquidityDistribution(int chainId, String pairAddress) throws Exception {
        System.out.println(dlmmService.getLiquidityDistribution(chainId, pairAddress).toString());
    }

    // ============================= AnyBus CREATE：LBFactory / LBRouter =============================

    @ApiOperation(description = "组装创建 LBFactory 交易（CREATE_LB_FACTORY）", order = 420, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "feeRecipient", parameterDes = "手续费接收地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result createFactoryTx(int chainId, String fromAddress, String feeRecipient) throws Exception {
        return dlmmService.createFactoryTx(chainId, fromAddress, feeRecipient);
    }

    @ApiOperation(description = "组装创建 LBRouter 交易（CREATE_LB_ROUTER）", order = 421, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "factoryAddress", parameterDes = "LBFactory 合约地址", requestType = @TypeDescriptor(value = String.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result createRouterTx(int chainId, String fromAddress, String factoryAddress) throws Exception {
        return dlmmService.createRouterTx(chainId, fromAddress, factoryAddress);
    }

    // ============================= LBRouter 交易组装（仅组装，不签名不广播） =============================

    @ApiOperation(description = "组装添加流动性交易", order = 430, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "routerAddress", parameterDes = "LBRouter 合约地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起人地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "tokenX", parameterDes = "TokenX 如 5-4", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "tokenY", parameterDes = "TokenY 如 5-2", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binStep", parameterDes = "binStep", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "amountX", parameterDes = "amountX", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountY", parameterDes = "amountY", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountXMin", parameterDes = "最小 amountX", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountYMin", parameterDes = "最小 amountY", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "activeId", parameterDes = "activeId", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "idSlippage", parameterDes = "idSlippage", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "deltaIds", parameterDes = "deltaIds", requestType = @TypeDescriptor(value = int[].class)),
            @Parameter(parameterName = "distributionX", parameterDes = "distributionX", requestType = @TypeDescriptor(value = BigInteger[].class)),
            @Parameter(parameterName = "distributionY", parameterDes = "distributionY", requestType = @TypeDescriptor(value = BigInteger[].class)),
            @Parameter(parameterName = "to", parameterDes = "接收 LP 地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "deadline", parameterDes = "过期时间戳秒", requestType = @TypeDescriptor(value = long.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result addLiquidityTx(int chainId, String routerAddress, String fromAddress,
                                        String tokenX, String tokenY, int binStep,
                                        BigInteger amountX, BigInteger amountY,
                                        BigInteger amountXMin, BigInteger amountYMin,
                                        int activeId, int idSlippage,
                                        int[] deltaIds, BigInteger[] distributionX, BigInteger[] distributionY,
                                        String to, long deadline) throws Exception {
        return dlmmService.addLiquidityTx(chainId, routerAddress, fromAddress,
                tokenX, tokenY, binStep, amountX, amountY, amountXMin, amountYMin,
                activeId, idSlippage, deltaIds, distributionX, distributionY, to, deadline);
    }

    @ApiOperation(description = "组装移除流动性交易", order = 431, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "routerAddress", parameterDes = "LBRouter 合约地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起人地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "tokenX", parameterDes = "TokenX", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "tokenY", parameterDes = "TokenY", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "binStep", parameterDes = "binStep", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "amountXMin", parameterDes = "amountXMin", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountYMin", parameterDes = "amountYMin", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "ids", parameterDes = "要移除的 bin id 数组", requestType = @TypeDescriptor(value = int[].class)),
            @Parameter(parameterName = "amounts", parameterDes = "各 bin 移除的 LP 数量", requestType = @TypeDescriptor(value = BigInteger[].class)),
            @Parameter(parameterName = "to", parameterDes = "接收代币地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "deadline", parameterDes = "过期时间戳秒", requestType = @TypeDescriptor(value = long.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result removeLiquidityTx(int chainId, String routerAddress, String fromAddress,
                                           String tokenX, String tokenY, int binStep,
                                           BigInteger amountXMin, BigInteger amountYMin,
                                           int[] ids, BigInteger[] amounts,
                                           String to, long deadline) throws Exception {
        return dlmmService.removeLiquidityTx(chainId, routerAddress, fromAddress,
                tokenX, tokenY, binStep, amountXMin, amountYMin, ids, amounts, to, deadline);
    }

    @ApiOperation(description = "组装精确输入兑换交易 swapExactTokensForTokens", order = 432, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "routerAddress", parameterDes = "LBRouter 合约地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起人地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "amountIn", parameterDes = "输入数量 18精度", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountOutMin", parameterDes = "最小输出数量", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "pairBinSteps", parameterDes = "path 每对 binStep 如 [10]", requestType = @TypeDescriptor(value = int[].class)),
            @Parameter(parameterName = "tokenPath", parameterDes = "代币路径 如 [tokenA, tokenB]", requestType = @TypeDescriptor(value = String[].class)),
            @Parameter(parameterName = "to", parameterDes = "接收代币地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "deadline", parameterDes = "过期时间戳秒", requestType = @TypeDescriptor(value = long.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result swapExactTokensForTokensTx(int chainId, String routerAddress, String fromAddress,
                                                    BigInteger amountIn, BigInteger amountOutMin,
                                                    int[] pairBinSteps, String[] tokenPath,
                                                    String to, long deadline) throws Exception {
        return dlmmService.swapExactTokensForTokensTx(chainId, routerAddress, fromAddress,
                amountIn, amountOutMin, pairBinSteps, tokenPath, to, deadline);
    }

    @ApiOperation(description = "组装精确输出兑换交易 swapTokensForExactTokens", order = 433, detailDesc = "返回未签名的 txHex 和 hash，不签名、不广播")
    @Parameters({
            @Parameter(parameterName = "chainId", parameterDes = "链ID", requestType = @TypeDescriptor(value = int.class)),
            @Parameter(parameterName = "routerAddress", parameterDes = "LBRouter 合约地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "fromAddress", parameterDes = "发起人地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "amountOut", parameterDes = "期望输出数量 18精度", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "amountInMax", parameterDes = "最大输入数量 18精度", requestType = @TypeDescriptor(value = BigInteger.class)),
            @Parameter(parameterName = "pairBinSteps", parameterDes = "path 每对 binStep", requestType = @TypeDescriptor(value = int[].class)),
            @Parameter(parameterName = "tokenPath", parameterDes = "代币路径", requestType = @TypeDescriptor(value = String[].class)),
            @Parameter(parameterName = "to", parameterDes = "接收代币地址", requestType = @TypeDescriptor(value = String.class)),
            @Parameter(parameterName = "deadline", parameterDes = "过期时间戳秒", requestType = @TypeDescriptor(value = long.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHex", description = "未签名交易 hex"),
            @Key(name = "hash", description = "交易 hash")
    }))
    public static Result swapTokensForExactTokensTx(int chainId, String routerAddress, String fromAddress,
                                                    BigInteger amountOut, BigInteger amountInMax,
                                                    int[] pairBinSteps, String[] tokenPath,
                                                    String to, long deadline) throws Exception {
        return dlmmService.swapTokensForExactTokensTx(chainId, routerAddress, fromAddress,
                amountOut, amountInMax, pairBinSteps, tokenPath, to, deadline);
    }
}

