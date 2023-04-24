package network.nerve.kit.service;


import network.nerve.SDKContext;
import network.nerve.base.basic.AddressTool;
import network.nerve.base.basic.TransactionFeeCalculator;
import network.nerve.base.data.*;
import network.nerve.base.signture.MultiSignTxSignature;
import network.nerve.base.signture.P2PHKSignature;
import network.nerve.core.basic.Result;
import network.nerve.core.constant.ErrorCode;
import network.nerve.core.constant.TxType;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.model.BigIntegerUtils;
import network.nerve.core.model.StringUtils;
import network.nerve.kit.constant.AccountConstant;
import network.nerve.kit.constant.Constant;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.NerveToken;
import network.nerve.kit.model.NerveTokenAmount;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.txdata.*;
import network.nerve.kit.txdata.linkswap.StableLpSwapTradeData;
import network.nerve.kit.txdata.linkswap.SwapTradeStableRemoveLpData;
import network.nerve.kit.util.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static network.nerve.kit.constant.AccountConstant.ALIAS_FEE;
import static network.nerve.kit.error.TxErrorCode.INVALID_PATH;
import static network.nerve.kit.util.TxUtils.addressToLowerCase;
import static network.nerve.kit.util.ValidateUtil.validateChainId;

public class TransactionService {

    private TransactionService() {

    }

    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }

    public Result getTx(String txHash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/tx/" + txHash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            TransactionDto tx = TransactionDto.mapToPojo(map);
            result.setData(tx);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getTransaction(String txHash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/tx/" + txHash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            TransactionDto tx = TransactionDto.mapToPojo(map);
            restFulResult = RestFulUtil.get("api/block/header/height/" + tx.getBlockHeight());
            if (restFulResult.isSuccess()) {
                map = (Map<String, Object>) restFulResult.getData();
                tx.setBlockHash((String) map.get("hash"));
            }
            result.setData(tx);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result transfer(TransferForm transferForm) {
        validateChainId();

        Map<String, Object> params = new HashMap<>();
        params.put("address", transferForm.getAddress());
        params.put("toAddress", transferForm.getToAddress());
        params.put("password", transferForm.getPassword());
        params.put("amount", transferForm.getAmount());
        params.put("remark", transferForm.getRemark());
        RestFulResult restFulResult = RestFulUtil.post("api/accountledger/transfer", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result crossTransfer(CrossTransferForm form) {
        validateChainId();
        Map<String, Object> params = new HashMap<>();
        params.put("address", form.getAddress());
        params.put("toAddress", form.getToAddress());
        params.put("password", form.getPassword());
        params.put("assetChainId", form.getAssetChainId());
        params.put("assetId", form.getAssetId());
        params.put("amount", form.getAmount());
        params.put("remark", form.getRemark());

        RestFulResult restFulResult = RestFulUtil.post("api/accountledger/crossTransfer", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    /**
     * 计算转账交易手续费
     *
     * @param dto 请求参数
     * @return result
     */
    public BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
        }
        return TxUtils.calcTransferTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), dto.getPrice());
    }

    /**
     * 计算跨链交易手续费
     * 包含本链资产手续费与NULS链手续费
     *
     * @param dto
     * @return
     */
    public Map<String, BigInteger> calcCrossTransferTxFee(CrossTransferTxFeeDto dto) {
        return TxUtils.calcCrossTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark());
    }

    /**
     * 便捷版 组装在NERVE链内，转账非NVT资产的单账户对单账户普通转账(不能用于转NVT)。
     * 该方法会主动用fromAddress组装（NVT资产）打包手续费，
     * 如果from地址中没有足够的手续费，该交易不会成功。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NERVE地址）
     * @param toAddress   转入地址（NERVE地址）
     * @param assetId
     * @param amount
     * @return
     */
    public Result createTxSimpleTransferOfNonNvt(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return createTxSimpleTransferOfNonNvt(fromAddress, toAddress, assetChainId, assetId, amount, 0, null);
    }

    /**
     * 便捷版 组装在NERVE链内，转账非NVT资产的单账户对单账户普通转账(不能用于转NVT)。
     * 该方法会主动用fromAddress组装（NVT资产）打包手续费，
     * 如果from地址中没有足够的手续费，该交易不会成功。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NERVE地址）
     * @param toAddress    转入地址（NERVE地址）
     * @param assetChainId
     * @param assetId
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createTxSimpleTransferOfNonNvt(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NerveSDKTool.getAccountBalance(fromAddress, assetChainId, assetId);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
/*       不验证余额
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amount) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(assetChainId);
        from.setAssetId(assetId);
        from.setNonce(nonce);
        inputs.add(from);
/*      2021-4-25 取消手续费
        Result accountBalanceFeeR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountBalanceFeeR.getErrorCode()).setMsg(accountBalanceFeeR.getMsg());
        }
        Map balanceFee = (Map) accountBalanceFeeR.getData();
        BigInteger senderBalanceFee = new BigInteger(balanceFee.get("available").toString());

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(2);
        feeDto.setToLength(1);
        feeDto.setRemark(remark);
        BigInteger feeNeed = NerveSDKTool.calcTransferTxFee(feeDto);
        if (senderBalanceFee.compareTo(feeNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }
        String nonceFee = balanceFee.get("nonce").toString();
        //手续费资产
        CoinFromDto fromFee = new CoinFromDto();
        fromFee.setAddress(fromAddress);
        fromFee.setAmount(feeNeed);
        fromFee.setAssetChainId(SDKContext.main_chain_id);
        fromFee.setAssetId(SDKContext.main_asset_id);
        fromFee.setNonce(nonceFee);
        inputs.add(fromFee);*/

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(assetChainId);
        to.setAssetId(assetId);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createTransferTx(transferDto);
    }


    /**
     * 便捷版 组装在NERVE链内，转账NVT资产的单账户对单账户普通转账。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public Result createTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount) {
        return createTxSimpleTransferOfNvt(fromAddress, toAddress, amount, 0, null);
    }

    /**
     * 便捷版 组装在NERVE链内，转账NVT资产的单账户对单账户普通转账。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();

/*      2021-4-25 取消链内手续费
        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(2);
        feeDto.setToLength(1);
        feeDto.setRemark(remark);
        BigInteger feeNeed = NerveSDKTool.calcTransferTxFee(feeDto);
        BigInteger amountTotal = amount.add(feeNeed);*/
/*        不验证余额
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amountTotal) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonce);
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createTransferTx(transferDto);
    }

    /**
     * 创建转账交易(离线)
     * create transfer transaction(off-line)
     *
     * @param transferDto 转账请求参数
     * @return
     */
    public Result createTransferTx(TransferDto transferDto) {
        validateChainId();
        try {
            CommonValidator.checkTransferDto(transferDto);

            for (CoinFromDto fromDto : transferDto.getInputs()) {
                if (fromDto.getAssetChainId() == 0) {
                    fromDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (fromDto.getAssetId() == 0) {
                    fromDto.setAssetId(SDKContext.main_asset_id);
                }
            }
            for (CoinToDto toDto : transferDto.getOutputs()) {
                if (toDto.getAssetChainId() == 0) {
                    toDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (toDto.getAssetId() == 0) {
                    toDto.setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.TRANSFER);
            if (transferDto.getTime() != 0) {
                tx.setTime(transferDto.getTime());
            } else {
                tx.setTime(getCurrentTimeSeconds());
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto.getInputs(), transferDto.getOutputs(), tx.getSize());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装转账交易的coinData数据
     * Assemble the coinData for the transfer transaction
     *
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : inputs) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : outputs) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        //txSize = txSize + getSignatureSize(coinFroms);
        //TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 便捷版 组装跨链转账非[NULS和NVT]资产的单账户对单账户普通跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网。
     * 该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（当前链地址）
     * @param toAddress    转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @return
     */
    public Result createCrossTxSimpleTransferOfNonNvtNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return createCrossTxSimpleTransferOfNonNvtNuls(fromAddress, toAddress, assetChainId, assetId, amount, 0, null);
    }

    /**
     * 便捷版 组装跨链转账非[NULS和NVT]资产的单账户对单账户跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网。
     * 该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（当前链地址）
     * @param toAddress    转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @param time         交易时间
     * @param remark       备注
     * @return
     */
    public Result createCrossTxSimpleTransferOfNonNvtNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NerveSDKTool.getAccountBalance(fromAddress, assetChainId, assetId);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
/*        不验证余额
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amount) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(assetChainId);
        from.setAssetId(assetId);
        from.setNonce(nonce);
        inputs.add(from);

        CrossTransferTxFeeDto crossFeeDto = new CrossTransferTxFeeDto();
        crossFeeDto.setAddressCount(1);
        crossFeeDto.setFromLength(2);
        crossFeeDto.setToLength(1);
        crossFeeDto.setRemark(remark);
        Map<String, BigInteger> feeMap = calcCrossTransferTxFee(crossFeeDto);

        BigInteger feeNvtNeed = feeMap.get("LOCAL");
        BigInteger feeNulsNeed = feeMap.get("NULS");

        /* NVT手续费 资产信息**/
        Result accountNvtBalanceFeeR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountNvtBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountNvtBalanceFeeR.getErrorCode()).setMsg(accountNvtBalanceFeeR.getMsg());
        }
        Map balanceNvtFee = (Map) accountNvtBalanceFeeR.getData();
       /* 不验证余额
       BigInteger senderNvtBalanceFee = new BigInteger(balanceNvtFee.get("available").toString());
        if (senderNvtBalanceFee.compareTo(feeNvtNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }*/
        String nonceNvtFee = balanceNvtFee.get("nonce").toString();

        /* NULS手续费 资产信息**/
        Result accountNulsBalanceFeeR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.nuls_chain_id, SDKContext.nuls_asset_id);
        if (!accountNulsBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountNulsBalanceFeeR.getErrorCode()).setMsg(accountNulsBalanceFeeR.getMsg());
        }
        Map balanceNulsFee = (Map) accountNulsBalanceFeeR.getData();
      /*  不验证余额
        BigInteger senderNulsBalanceFee = new BigInteger(balanceNulsFee.get("available").toString());
        if (senderNulsBalanceFee.compareTo(feeNulsNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }*/
        String nonceNulsFee = balanceNulsFee.get("nonce").toString();

        //NVT手续费资产
        CoinFromDto fromNvtFee = new CoinFromDto();
        fromNvtFee.setAddress(fromAddress);
        fromNvtFee.setAmount(feeNvtNeed);
        fromNvtFee.setAssetChainId(SDKContext.main_chain_id);
        fromNvtFee.setAssetId(SDKContext.main_asset_id);
        fromNvtFee.setNonce(nonceNvtFee);
        inputs.add(fromNvtFee);

        //NULS手续费资产
        CoinFromDto fromNulsFee = new CoinFromDto();
        fromNulsFee.setAddress(fromAddress);
        fromNulsFee.setAmount(feeNulsNeed);
        fromNulsFee.setAssetChainId(SDKContext.nuls_chain_id);
        fromNulsFee.setAssetId(SDKContext.nuls_asset_id);
        fromNulsFee.setNonce(nonceNulsFee);
        inputs.add(fromNulsFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(assetChainId);
        to.setAssetId(assetId);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createCrossTransferTx(transferDto);
    }


    /**
     * 便捷版 组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取[NULS与NVT]手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @return
     */
    public Result createCrossTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount) {
        return createCrossTxSimpleTransferOfNvt(fromAddress, toAddress, amount, 0, null);
    }

    /**
     * 便捷版 组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取[NULS与NVT]手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createCrossTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();


        CrossTransferTxFeeDto crossFeeDto = new CrossTransferTxFeeDto();
        crossFeeDto.setAddressCount(1);
        crossFeeDto.setFromLength(2);
        crossFeeDto.setToLength(1);
        crossFeeDto.setRemark(remark);
        Map<String, BigInteger> feeMap = calcCrossTransferTxFee(crossFeeDto);

        BigInteger feeNvtNeed = feeMap.get("LOCAL");
        BigInteger feeNulsNeed = feeMap.get("NULS");

        BigInteger amountTotal = amount.add(feeNvtNeed);
/*         不验证余额
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amountTotal) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amountTotal);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonce);
        inputs.add(from);

        /* NULS手续费 资产信息**/
        Result accountNulsBalanceFeeR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.nuls_chain_id, SDKContext.nuls_asset_id);
        if (!accountNulsBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountNulsBalanceFeeR.getErrorCode()).setMsg(accountNulsBalanceFeeR.getMsg());
        }
        Map balanceNulsFee = (Map) accountNulsBalanceFeeR.getData();
      /* 不验证余额
        BigInteger senderNulsBalanceFee = new BigInteger(balanceNulsFee.get("available").toString());
        if (senderNulsBalanceFee.compareTo(feeNulsNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }*/
        String nonceNulsFee = balanceNulsFee.get("nonce").toString();

        //NULS手续费资产
        CoinFromDto fromNulsFee = new CoinFromDto();
        fromNulsFee.setAddress(fromAddress);
        fromNulsFee.setAmount(feeNulsNeed);
        fromNulsFee.setAssetChainId(SDKContext.nuls_chain_id);
        fromNulsFee.setAssetId(SDKContext.nuls_asset_id);
        fromNulsFee.setNonce(nonceNulsFee);
        inputs.add(fromNulsFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createCrossTransferTx(transferDto);
    }

    /**
     * 便捷版 组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取[NULS与NVT]手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @return
     */
    public Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount, 0, null);
    }

    /**
     * 便捷版 组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取[NULS与NVT]手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        Result accountNvtBalanceR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountNvtBalanceR.isSuccess()) {
            return Result.getFailed(accountNvtBalanceR.getErrorCode()).setMsg(accountNvtBalanceR.getMsg());
        }
        Map balanceNvtFee = (Map) accountNvtBalanceR.getData();


        CrossTransferTxFeeDto crossFeeDto = new CrossTransferTxFeeDto();
        crossFeeDto.setAddressCount(1);
        crossFeeDto.setFromLength(2);
        crossFeeDto.setToLength(1);
        crossFeeDto.setRemark(remark);
        Map<String, BigInteger> feeMap = calcCrossTransferTxFee(crossFeeDto);

        BigInteger feeNvtNeed = feeMap.get("LOCAL");
        BigInteger feeNulsNeed = feeMap.get("NULS");
/*        不验证余额
        BigInteger senderNvtBalance = new BigInteger(balanceNvtFee.get("available").toString());
        if (senderNvtBalance.compareTo(feeNvtNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        String nonceNvtFee = balanceNvtFee.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //NVT手续费
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(feeNvtNeed);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonceNvtFee);
        inputs.add(from);

        /* NULS手续费 资产信息**/
        Result accountNulsBalanceFeeR = NerveSDKTool.getAccountBalance(fromAddress, SDKContext.nuls_chain_id, SDKContext.nuls_asset_id);
        if (!accountNulsBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountNulsBalanceFeeR.getErrorCode()).setMsg(accountNulsBalanceFeeR.getMsg());
        }
        Map balanceNulsFee = (Map) accountNulsBalanceFeeR.getData();
        BigInteger senderNulsBalanceFee = new BigInteger(balanceNulsFee.get("available").toString());
        BigInteger amountTotal = amount.add(feeNulsNeed);
      /*  不验证余额
       if (senderNulsBalanceFee.compareTo(amountTotal) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }*/
        String nonceNulsFee = balanceNulsFee.get("nonce").toString();

        //NULS手续费+转账数量
        CoinFromDto fromNulsFee = new CoinFromDto();
        fromNulsFee.setAddress(fromAddress);
        fromNulsFee.setAmount(amountTotal);
        fromNulsFee.setAssetChainId(SDKContext.nuls_chain_id);
        fromNulsFee.setAssetId(SDKContext.nuls_asset_id);
        fromNulsFee.setNonce(nonceNulsFee);
        inputs.add(fromNulsFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.nuls_chain_id);
        to.setAssetId(SDKContext.nuls_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createCrossTransferTx(transferDto);
    }

    /**
     * 创建跨链转账交易
     *
     * @param transferDto
     * @return
     */
    public Result createCrossTransferTx(TransferDto transferDto) {
        validateChainId();
        try {
            CommonValidator.checkCrossTransferDto(transferDto);
            Transaction tx = new Transaction(TxType.CROSS_CHAIN);
            if (transferDto.getTime() != 0) {
                tx.setTime(transferDto.getTime());
            } else {
                tx.setTime(getCurrentTimeSeconds());
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = createCrossTxCoinData(transferDto.getInputs(), transferDto.getOutputs());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));

            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }

    }

    public CoinData createCrossTxCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs) {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : inputs) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : outputs) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);

        return coinData;
    }


    /**
     * 提现
     * 异构跨链转出 收取nvt作为手续费
     *
     * @param withdrawalTxDto
     * @return
     */
    public Result createWithdrawalTx(WithdrawalTxDto withdrawalTxDto, String withdrawalAssetNonce, String nvtFeeAssetNonce) {
        validateChainId();
        try {
            CommonValidator.checkWithdrawalTxDto(withdrawalTxDto);
            WithdrawalTxData txData = new WithdrawalTxData(addressToLowerCase(withdrawalTxDto.getHeterogeneousAddress()));
            txData.setHeterogeneousChainId(withdrawalTxDto.getHeterogeneousChainId());

            byte[] txDataBytes = null;
            try {
                txDataBytes = txData.serialize();
            } catch (IOException e) {
                throw new NulsException(AccountErrorCode.SERIALIZE_ERROR);
            }

            Transaction tx = new Transaction(TxType.WITHDRAWAL);
            tx.setTxData(txDataBytes);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.isBlank(withdrawalTxDto.getRemark()) ? null : StringUtils.bytes(withdrawalTxDto.getRemark()));
            byte[] coinData = assembleWithdrawalCoinData(withdrawalTxDto, withdrawalAssetNonce, nvtFeeAssetNonce);
            tx.setCoinData(coinData);
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 追加异构提现手续费
     *
     * @param fromAddress
     * @param txHash
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result withdrawalAdditionalFeeTx(String fromAddress, String txHash, BigInteger amount, long time, String remark, String nonce) {
        try {
            //转账交易转出地址必须是本链地址
            if (!AddressTool.validAddress(SDKContext.main_chain_id, fromAddress)) {
                throw new NulsException(AccountErrorCode.IS_NOT_CURRENT_CHAIN_ADDRESS);
            }
            if (StringUtils.isBlank(txHash)) {
                throw new NulsException(AccountErrorCode.NULL_PARAMETER);
            }
            if (null == amount || BigIntegerUtils.isLessThan(amount, BigInteger.ZERO)) {
                throw new NulsException(AccountErrorCode.DATA_ERROR);
            }
            WithdrawalAdditionalFeeTxData txData = new WithdrawalAdditionalFeeTxData(txHash);
            byte[] txDataBytes = null;
            try {
                txDataBytes = txData.serialize();
            } catch (IOException e) {
                throw new NulsException(AccountErrorCode.SERIALIZE_ERROR);
            }
            Transaction tx = new Transaction(TxType.WITHDRAWAL_ADDITIONAL_FEE);
            tx.setTxData(txDataBytes);
            tx.setTime(time);
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));
            byte[] coinData;
            if (nonce == null) {
                coinData = assembleFeeCoinData(fromAddress, amount);
            } else {
                coinData = assembleFeeCoinData(fromAddress, amount, nonce);
            }
            tx.setCoinData(coinData);
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * Stable-Swap稳定币兑换交易
     *
     * @param from              用户地址
     * @param to                接收地址
     * @param tokenAmountIns    卖出的资产数量列表
     * @param nonces            卖出的资产nonce列表
     * @param tokenOutIndex     买进的资产索引
     * @param pairAddress       交易对地址
     * @param feeTo             交易手续费取出一部分给指定的接收地址（当前未收取手续费）
     * @param remark            交易备注
     * @return
     */
    public Result stableSwapTradeTx(String from, String to,
                                    NerveTokenAmount[] tokenAmountIns, String[] nonces,
                                    int tokenOutIndex, String pairAddress,
                                    String feeTo, NerveTokenAmount feeTokenAmount, String remark) {
        try {
            byte[] pairAddressBytes = AddressTool.getAddress(pairAddress);
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] feeToBytes = feeTo != null ? AddressTool.getAddress(feeTo) : null;
            boolean hasFee = false;
            if (StringUtils.isNotBlank(feeTo) && feeTokenAmount != null) {
                hasFee = true;
            }
            // 组装交易
            StableSwapTradeData data = new StableSwapTradeData();
            data.setTo(AddressTool.getAddress(to));
            data.setTokenOutIndex((byte) tokenOutIndex);
            data.setFeeTo(feeToBytes);

            Transaction tx = new Transaction(TxType.SWAP_TRADE_STABLE_COIN);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();
            int length = tokenAmountIns.length;
            nonces = this.checkNonces(from, tokenAmountIns, nonces);
            for (int i = 0; i < length; i++) {
                NerveTokenAmount token = tokenAmountIns[i];
                BigInteger amount = token.getAmount();
                String nonce = nonces[i];
                froms.add(new CoinFrom(
                        fromBytes,
                        token.getChainId(),
                        token.getAssetId(),
                        amount,
                        HexUtil.decode(nonce),
                        (byte) 0));
                if (hasFee && token.getChainId() == feeTokenAmount.getChainId() && token.getAssetId() == feeTokenAmount.getAssetId()) {
                    amount = amount.subtract(feeTokenAmount.getAmount());
                    tos.add(new CoinTo(
                            feeToBytes,
                            token.getChainId(),
                            token.getAssetId(),
                            feeTokenAmount.getAmount()));
                }
                tos.add(new CoinTo(
                        pairAddressBytes,
                        token.getChainId(),
                        token.getAssetId(),
                        amount));
            }
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result stableSwapAddLiquidity(String from, BigInteger amount,
                                         NerveToken token, String pairAddress,
                                         Long deadline, String to, String remark) {
        try {
            long currentTimeSeconds = getCurrentTimeSeconds();
            if (deadline == null || deadline.longValue() <= 0) {
                deadline = currentTimeSeconds + 300;
            }
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] pairAddressBytes = AddressTool.getAddress(pairAddress);
            // 组装交易
            StableAddLiquidityData data = new StableAddLiquidityData();
            data.setTo(AddressTool.getAddress(to));

            Transaction tx = new Transaction(TxType.SWAP_ADD_LIQUIDITY_STABLE_COIN);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(currentTimeSeconds);
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();

            String nonce = this.checkNonce(from, token, null);

            froms.add(new CoinFrom(
                    fromBytes,
                    token.getChainId(),
                    token.getAssetId(),
                    amount,
                    HexUtil.decode(nonce),
                    (byte) 0));
            tos.add(new CoinTo(
                    pairAddressBytes,
                    token.getChainId(),
                    token.getAssetId(),
                    amount));
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result stableSwapRemoveLiquidity(String from, BigInteger amountLP, NerveToken tokenLP,
                                            Integer[] receiveOrderIndexs, String pairAddress, Long deadline, String to, String remark) {
        try {
            long currentTimeSeconds = getCurrentTimeSeconds();
            if (deadline == null || deadline.longValue() <= 0) {
                deadline = currentTimeSeconds + 300;
            }
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] pairAddressBytes = AddressTool.getAddress(pairAddress);
            // 组装交易
            int length = receiveOrderIndexs.length;
            byte[] indexs = new byte[length];
            for (int i = 0; i < length; i++) {
                indexs[i] = receiveOrderIndexs[i].byteValue();
            }
            StableRemoveLiquidityData data = new StableRemoveLiquidityData();
            data.setIndexs(indexs);
            data.setTo(AddressTool.getAddress(to));

            Transaction tx = new Transaction(TxType.SWAP_REMOVE_LIQUIDITY_STABLE_COIN);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(currentTimeSeconds);
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();

            String nonce = this.checkNonce(from, tokenLP, null);

            froms.add(new CoinFrom(
                    fromBytes,
                    tokenLP.getChainId(),
                    tokenLP.getAssetId(),
                    amountLP,
                    HexUtil.decode(nonce),
                    (byte) 0));
            tos.add(new CoinTo(
                    pairAddressBytes,
                    tokenLP.getChainId(),
                    tokenLP.getAssetId(),
                    amountLP));
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    private String[] checkNonces(String address, NerveTokenAmount[] tokens, String[] nonces) throws NulsException {
        if (nonces != null) {
            return nonces;
        }
        int length = tokens.length;
        nonces = new String[length];
        for (int i = 0; i < length; i++) {
            NerveTokenAmount token = tokens[i];
            Result accountBalance = NerveSDKTool.getAccountBalance(address, token.getChainId(), token.getAssetId());
            if (!accountBalance.isSuccess()) {
                throw new NulsException(AccountErrorCode.NOT_FOUND_NONCE);
            }
            Map balance = (Map) accountBalance.getData();
            nonces[i] = balance.get("nonce").toString();
        }
        return nonces;
    }

    private String checkNonce(String address, NerveToken token, String nonce) throws NulsException {
        if (nonce != null) {
            return nonce;
        }
        Result accountBalance = NerveSDKTool.getAccountBalance(address, token.getChainId(), token.getAssetId());
        if (!accountBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.NOT_FOUND_NONCE);
        }
        Map balance = (Map) accountBalance.getData();
        nonce = balance.get("nonce").toString();
        return nonce;
    }

    /**
     * 组装提现交易CoinData
     *
     * @param withdrawalTxDto
     * @return
     * @throws NulsException
     */
    private byte[] assembleWithdrawalCoinData(WithdrawalTxDto withdrawalTxDto, String withdrawalAssetNonce, String nvtFeeAssetNonce) throws NulsException {
        int withdrawalAssetId = withdrawalTxDto.getAssetId();
        int withdrawalAssetChainId = withdrawalTxDto.getAssetChainId();

        int chainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        BigInteger amount = withdrawalTxDto.getAmount();
        String address = withdrawalTxDto.getFromAddress();
        //提现资产from
        CoinFrom withdrawalCoinFrom;
        if (withdrawalAssetNonce == null) {
            withdrawalCoinFrom = getWithdrawalCoinFrom(address, amount, withdrawalAssetChainId, withdrawalAssetId, withdrawalTxDto.getDistributionFee());
        } else {
            withdrawalCoinFrom = getWithdrawalCoinFrom(address, amount, withdrawalAssetChainId, withdrawalAssetId, withdrawalTxDto.getDistributionFee(), withdrawalAssetNonce);
        }
        List<CoinFrom> listFrom = new ArrayList<>();
        listFrom.add(withdrawalCoinFrom);
        if (withdrawalAssetChainId != chainId || assetId != withdrawalAssetId) {
            // 只要不是当前链主资产 都要组装额外的coinFrom
            CoinFrom withdrawalFeeCoinFrom;
            //手续费from 包含异构链补贴手续费
            if (nvtFeeAssetNonce == null) {
                withdrawalFeeCoinFrom = getWithdrawalFeeCoinFrom(address, withdrawalTxDto.getDistributionFee());
            } else {
                withdrawalFeeCoinFrom = getWithdrawalFeeCoinFrom(address, withdrawalTxDto.getDistributionFee(), nvtFeeAssetNonce);
            }
            listFrom.add(withdrawalFeeCoinFrom);
        }
        //组装to
        List<CoinTo> listTo = new ArrayList<>();
        CoinTo withdrawalCoinTo = new CoinTo(
                AddressTool.getAddress(Constant.WITHDRAWAL_BLACKHOLE_PUBKEY, chainId),
                withdrawalAssetChainId,
                withdrawalAssetId,
                amount);

        listTo.add(withdrawalCoinTo);
        // 判断组装异构链补贴手续费暂存to
        CoinTo withdrawalFeeCoinTo = new CoinTo(
                AddressTool.getAddress(Constant.FEE_PUBKEY, chainId),
                chainId,
                assetId,
                withdrawalTxDto.getDistributionFee());
        listTo.add(withdrawalFeeCoinTo);
        CoinData coinData = new CoinData(listFrom, listTo);
        try {
            return coinData.serialize();
        } catch (IOException e) {
            throw new NulsException(AccountErrorCode.SERIALIZE_ERROR);
        }
    }

    /**
     * 组装提现资产CoinFrom
     *
     * @param address
     * @param amount
     * @param withdrawalAssetChainId
     * @param withdrawalAssetId
     * @return
     * @throws NulsException
     */
    private CoinFrom getWithdrawalCoinFrom(
            String address,
            BigInteger amount,
            int withdrawalAssetChainId,
            int withdrawalAssetId,
            BigInteger withdrawalHeterogeneousFeeNvt) throws NulsException {

        Result accountBalance = NerveSDKTool.getAccountBalance(address, withdrawalAssetChainId, withdrawalAssetId);
        if (!accountBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.NOT_FOUND_NONCE);
        }
        Map balance = (Map) accountBalance.getData();
/*      不验证余额是否足够够 保证可以提前组装交易
        BigInteger withdrawalAssetBalance = new BigInteger(balance.get("available").toString());
        if (BigIntegerUtils.isLessThan(withdrawalAssetBalance, amount)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/

        if (withdrawalAssetChainId == SDKContext.main_chain_id && SDKContext.main_asset_id == withdrawalAssetId) {
            // 异构转出链内主资产, 直接合并到一个coinFrom
            // 总手续费 = 链内打包手续费 + 异构链转账(或签名)手续费[都以链内主资产结算]
            BigInteger totalFee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(withdrawalHeterogeneousFeeNvt);
            amount = totalFee.add(amount);
/*          不验证余额是否足够够 保证可以提前组装交易
            if (BigIntegerUtils.isLessThan(withdrawalAssetBalance, amount)) {
                throw new NulsException(AccountErrorCode.INSUFFICIENT_BALANCE);
            }*/
        }
        String nonce = balance.get("nonce").toString();
        return new CoinFrom(
                AddressTool.getAddress(address),
                withdrawalAssetChainId,
                withdrawalAssetId,
                amount,
                HexUtil.decode(nonce),
                (byte) 0);
    }

    private CoinFrom getWithdrawalCoinFrom(
            String address,
            BigInteger amount,
            int withdrawalAssetChainId,
            int withdrawalAssetId,
            BigInteger withdrawalHeterogeneousFeeNvt,
            String withdrawalAssetNonce) throws NulsException {
        if (withdrawalAssetChainId == SDKContext.main_chain_id && SDKContext.main_asset_id == withdrawalAssetId) {
            // 异构转出链内主资产, 直接合并到一个coinFrom
            // 总手续费 = 链内打包手续费 + 异构链转账(或签名)手续费[都以链内主资产结算]
            BigInteger totalFee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(withdrawalHeterogeneousFeeNvt);
            amount = totalFee.add(amount);
        }
        return new CoinFrom(
                AddressTool.getAddress(address),
                withdrawalAssetChainId,
                withdrawalAssetId,
                amount,
                HexUtil.decode(withdrawalAssetNonce),
                (byte) 0);
    }

    /**
     * 组装提现交易手续费(包含链内打包手续费, 异构链补贴手续费)
     *
     * @param address
     * @param withdrawalHeterogeneousFeeNvt
     * @return
     * @throws NulsException
     */
    private CoinFrom getWithdrawalFeeCoinFrom(String address, BigInteger withdrawalHeterogeneousFeeNvt) throws NulsException {
        int chainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;

        Result accountBalance = NerveSDKTool.getAccountBalance(address, chainId, assetId);
        if (!accountBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.NOT_FOUND_NONCE);
        }
        Map balanceMap = (Map) accountBalance.getData();
        // 本链资产余额
        BigInteger balance = new BigInteger(balanceMap.get("available").toString());

        // 总手续费 = 链内打包手续费 + 异构链转账(或签名)手续费[都以链内主资产结算]
        BigInteger totalFee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(withdrawalHeterogeneousFeeNvt);
/*        不验证余额
        if (BigIntegerUtils.isLessThan(balance, totalFee)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        // 查询账本获取nonce值
        String nonce = balanceMap.get("nonce").toString();
        return new CoinFrom(AddressTool.getAddress(address), chainId, assetId, totalFee, HexUtil.decode(nonce), (byte) 0);
    }

    private CoinFrom getWithdrawalFeeCoinFrom(String address, BigInteger withdrawalHeterogeneousFeeNvt,
                                              String nvtFeeAssetNonce) throws NulsException {
        int chainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        // 总手续费 = 链内打包手续费 + 异构链转账(或签名)手续费[都以链内主资产结算]
        BigInteger totalFee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(withdrawalHeterogeneousFeeNvt);
        return new CoinFrom(AddressTool.getAddress(address), chainId, assetId, totalFee, HexUtil.decode(nvtFeeAssetNonce), (byte) 0);
    }


    /**
     * 组装追加提现手续费（CoinData）
     *
     * @param address
     * @param extraFee 向公共手续费收集地址 支付额外的业务费用(例如提案费用等), 用于后续费用的补偿
     * @return
     * @throws NulsException
     */
    private byte[] assembleFeeCoinData(String address, BigInteger extraFee) throws NulsException {

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        Result accountBalance = NerveSDKTool.getAccountBalance(address, assetChainId, assetId);
        if (!accountBalance.isSuccess()) {
            throw new NulsException(AccountErrorCode.NOT_FOUND_NONCE);
        }
        Map balanceMap = (Map) accountBalance.getData();
        BigInteger amount = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(extraFee);
/*        不验证余额
        BigInteger balance = new BigInteger(balanceMap.get("available").toString());
        if (BigIntegerUtils.isLessThan(balance, amount)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        //查询账本获取nonce值
        String nonce = balanceMap.get("nonce").toString();
        CoinFrom coinFrom = new CoinFrom(
                AddressTool.getAddress(address),
                assetChainId,
                assetId,
                amount,
                HexUtil.decode(nonce),
                (byte) 0);
        CoinData coinData = new CoinData();
        List<CoinFrom> froms = new ArrayList<>();
        froms.add(coinFrom);

        List<CoinTo> tos = new ArrayList<>();
/*      不验证余额
        if (BigIntegerUtils.isLessThan(balance, TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(extraFee))) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_BALANCE);
        }*/
        CoinTo extraFeeCoinTo = new CoinTo(
                AddressTool.getAddress(Constant.FEE_PUBKEY, assetChainId),
                assetChainId,
                assetId,
                extraFee);
        tos.add(extraFeeCoinTo);

        coinData.setFrom(froms);
        coinData.setTo(tos);
        try {
            return coinData.serialize();
        } catch (IOException e) {
            throw new NulsException(AccountErrorCode.SERIALIZE_ERROR);
        }
    }

    private byte[] assembleFeeCoinData(String address, BigInteger extraFee, String nonce) throws NulsException {

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        BigInteger amount = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES.add(extraFee);
        CoinFrom coinFrom = new CoinFrom(
                AddressTool.getAddress(address),
                assetChainId,
                assetId,
                amount,
                HexUtil.decode(nonce),
                (byte) 0);
        CoinData coinData = new CoinData();
        List<CoinFrom> froms = new ArrayList<>();
        froms.add(coinFrom);

        List<CoinTo> tos = new ArrayList<>();
        CoinTo extraFeeCoinTo = new CoinTo(
                AddressTool.getAddress(Constant.FEE_PUBKEY, assetChainId),
                assetChainId,
                assetId,
                extraFee);
        tos.add(extraFeeCoinTo);

        coinData.setFrom(froms);
        coinData.setTo(tos);
        try {
            return coinData.serialize();
        } catch (IOException e) {
            throw new NulsException(AccountErrorCode.SERIALIZE_ERROR);
        }
    }


    public Result createMultiSignTransferTx(MultiSignTransferDto transferDto) {
        validateChainId();
        try {
            CommonValidator.checkMultiSignTransferDto(transferDto);
            for (CoinFromDto fromDto : transferDto.getInputs()) {
                if (fromDto.getAssetChainId() == 0) {
                    fromDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (fromDto.getAssetId() == 0) {
                    fromDto.setAssetId(SDKContext.main_asset_id);
                }
            }
            for (CoinToDto toDto : transferDto.getOutputs()) {
                if (toDto.getAssetChainId() == 0) {
                    toDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (toDto.getAssetId() == 0) {
                    toDto.setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.TRANSFER);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto, tx.getSize());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) transferDto.getMinSigns());

            List<byte[]> list = new ArrayList<>();
            for (String pubKey : transferDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }


    private CoinData assemblyCoinData(MultiSignTransferDto transferDto, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : transferDto.getInputs()) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : transferDto.getOutputs()) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        //txSize = txSize + getMultiSignSignatureSize(transferDto.getPubKeys().size());
        //TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 计算转账交易手续费
     *
     * @param dto 请求参数
     * @return result
     */
    public BigInteger calcMultiSignTransferTxFee(MultiSignTransferTxFeeDto dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
        }
        return TxUtils.calcTransferTxFee(dto.getPubKeyCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), dto.getPrice());
    }

    /**
     * 通过coinFroms计算签名数据的size
     * 如果coinFroms有重复地址则只计算一次
     * Calculate the size of the signature data by coinFroms
     * if coinFroms has duplicate addresses, it will only be evaluated once
     *
     * @param coinFroms 交易输入
     * @return int size
     */
    private int getSignatureSize(List<CoinFrom> coinFroms) {
        int size = 0;
        Set<String> commonAddress = new HashSet<>();
        for (CoinFrom coinFrom : coinFroms) {
            String address = AddressTool.getStringAddressByBytes(coinFrom.getAddress());
            commonAddress.add(address);
        }
        size += commonAddress.size() * P2PHKSignature.SERIALIZE_LENGTH;
        return size;
    }

    private int getMultiSignSignatureSize(int signNumber) {
        int size = signNumber * P2PHKSignature.SERIALIZE_LENGTH;
        return size;
    }

    public Result createAliasTx(AliasDto aliasDto) {
        validateChainId();
        try {
            CommonValidator.checkAliasDto(aliasDto);

            Transaction tx = new Transaction(TxType.ACCOUNT_ALIAS);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(aliasDto.getRemark()));

            Alias alias = new Alias(AddressTool.getAddress(aliasDto.getAddress()), aliasDto.getAlias());
            tx.setTxData(alias.serialize());

            CoinData coinData = assemblyCoinData(aliasDto);
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(AliasDto dto) {
        byte[] address = AddressTool.getAddress(dto.getAddress());
        byte[] nonce = HexUtil.decode(dto.getNonce());

        List<CoinFrom> coinFroms = new ArrayList<>();
        CoinFrom coinFrom = new CoinFrom();
        coinFrom.setAddress(address);
        coinFrom.setNonce(nonce);
        coinFrom.setAmount(ALIAS_FEE.add(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES));
        coinFrom.setAssetsChainId(SDKContext.main_chain_id);
        coinFrom.setAssetsId(SDKContext.main_asset_id);
        coinFroms.add(coinFrom);

        String prefix = AccountTool.getPrefix(dto.getAddress());
        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo();
        coinTo.setAddress(AddressTool.getAddress(AccountConstant.DESTORY_PUBKEY, SDKContext.main_chain_id, prefix));
        coinTo.setAmount(ALIAS_FEE);
        coinTo.setAssetsChainId(SDKContext.main_chain_id);
        coinTo.setAssetsId(SDKContext.main_asset_id);
        coinTos.add(coinTo);

        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 组装创建共识节点交易
     * Assemble to create consensus node transactions
     *
     * @param consensusDto 创建共识节点请求参数
     * @return result
     */
    public Result createConsensusTx(ConsensusDto consensusDto) {
        validateChainId();
        try {
            if (StringUtils.isBlank(consensusDto.getRewardAddress())) {
                consensusDto.setRewardAddress(consensusDto.getAgentAddress());
            }
            CommonValidator.validateConsensusDto(consensusDto);

            if (consensusDto.getInput().getAssetChainId() == 0) {
                consensusDto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (consensusDto.getInput().getAssetId() == 0) {
                consensusDto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.REGISTER_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            Agent agent = new Agent();
            agent.setAgentAddress(AddressTool.getAddress(consensusDto.getAgentAddress()));
            agent.setPackingAddress(AddressTool.getAddress(consensusDto.getPackingAddress()));
            agent.setRewardAddress(AddressTool.getAddress(consensusDto.getRewardAddress()));
            agent.setDeposit((consensusDto.getDeposit()));
            tx.setTxData(agent.serialize());

            CoinData coinData = assemblyCoinData(consensusDto.getInput(), agent.getDeposit(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * Create a proxy consensus transaction
     * 创建委托共识交易
     *
     * @param dto 委托共识请求参数
     * @return result
     */
    public Result createDepositTx(DepositDto dto) {
        validateChainId();
        try {
            CommonValidator.validateDepositDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());
            Deposit deposit = new Deposit();
            deposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            deposit.setDeposit(dto.getDeposit());
            tx.setTxData(deposit.serialize());

            CoinData coinData = assemblyCoinData(dto.getInput(), dto.getDeposit(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(CoinFromDto from, BigInteger amount, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        byte[] nonce = HexUtil.decode(from.getNonce());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), amount, -1);
        coinTos.add(coinTo);

        //txSize = txSize + getSignatureSize(coinFroms);
        //TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 创建取消委托交易
     *
     * @param dto 取消委托交易参数
     * @return result
     */
    public Result createWithdrawDepositTx(WithDrawDto dto) {
        validateChainId();

        try {
            if (dto.getPrice() == null) {
                dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
            }
            CommonValidator.validateWithDrawDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.CANCEL_DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());

            CancelDeposit cancelDeposit = new CancelDeposit();
            cancelDeposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            cancelDeposit.setJoinTxHash(NulsHash.fromHex(dto.getDepositHash()));
            tx.setTxData(cancelDeposit.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);

        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装退出共识交易coinData
     *
     * @param dto    请求参数
     * @param txSize 交易大小
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(WithDrawDto dto, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        CoinFromDto from = dto.getInput();
        byte[] address = AddressTool.getAddress(from.getAddress());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(dto.getDepositHash());
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), from.getAmount().subtract(dto.getPrice()), 0);
        coinTos.add(coinTo);

        //txSize = txSize + getSignatureSize(coinFroms);
        //TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 创建注销共识节点交易
     *
     * @param dto 注销节点参数请求
     * @return result
     */
    public Result createStopConsensusTx(StopConsensusDto dto) {
        validateChainId();

        try {
            if (dto.getPrice() == null) {
                dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
            }
            CommonValidator.validateStopConsensusDto(dto);
            for (StopDepositDto depositDto : dto.getDepositList()) {
                if (depositDto.getInput().getAssetChainId() == 0) {
                    depositDto.getInput().setAssetChainId(SDKContext.main_chain_id);
                }
                if (depositDto.getInput().getAssetId() == 0) {
                    depositDto.getInput().setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.STOP_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            StopAgent stopAgent = new StopAgent();
            NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
            stopAgent.setCreateTxHash(nulsHash);
            tx.setTxData(stopAgent.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装注销节点交易coinData
     *
     * @param dto    参数
     * @param txSize 交易大小
     * @return
     * @throws NulsException
     */
    private CoinData assemblyCoinData(StopConsensusDto dto, int txSize) throws NulsException {
        //获取当前链注册共识资产的chainId和assetId
        int chainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;

        List<CoinFrom> coinFromList = new ArrayList<>();
        //组装创建节点交易的coinFrom
        byte[] addressBytes = AddressTool.getAddress(dto.getAgentAddress());
        CoinFrom coinFrom = new CoinFrom(addressBytes, chainId, assetId, dto.getDeposit(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFromList.add(coinFrom);

        Map<String, CoinFromDto> dtoMap = new HashMap<>();
        CoinFromDto fromDto;
        //组装所有委托的coinFrom
        for (StopDepositDto depositDto : dto.getDepositList()) {
            CoinFromDto input = depositDto.getInput();
            byte[] address = AddressTool.getAddress(input.getAddress());
            CoinFrom coinFrom1 = new CoinFrom(address, input.getAssetChainId(), input.getAssetId(), input.getAmount(), (byte) -1);
            NulsHash nulsHash1 = NulsHash.fromHex(depositDto.getDepositHash());
            coinFrom1.setNonce(TxUtils.getNonce(nulsHash1.getBytes()));
            coinFromList.add(coinFrom1);
            //将相同账户的多次委托的金额存放在一起
            String key = input.getAddress() + input.getAssetChainId() + input.getAssetId();
            fromDto = dtoMap.get(key);
            if (fromDto == null) {
                dtoMap.put(key, input);
            } else {
                fromDto.setAmount(fromDto.getAmount().add(input.getAmount()));
            }
        }
        //通过dtoMap组装交易输出
        List<CoinTo> coinToList = new ArrayList<>();
        for (CoinFromDto input : dtoMap.values()) {
            byte[] address = AddressTool.getAddress(input.getAddress());
            CoinTo coinTo = new CoinTo(address, input.getAssetChainId(), input.getAssetId(), input.getAmount(), 0L);
            coinToList.add(coinTo);
        }
        //计算手续费
        BigInteger fee = TxUtils.calcStopConsensusTxFee(coinFromList.size(), coinToList.size() + 1, dto.getPrice());
        //组装退回保证金的coinTo
        CoinTo coinTo = new CoinTo(addressBytes, coinFrom.getAssetsChainId(), coinFrom.getAssetsId(), coinFrom.getAmount().subtract(fee), getCurrentTimeSeconds() + SDKContext.stop_agent_lock_time);
        coinToList.add(0, coinTo);

        //txSize = txSize + P2PHKSignature.SERIALIZE_LENGTH;
        //TxUtils.calcTxFee(coinFromList, coinToList, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFromList);
        coinData.setTo(coinToList);
        return coinData;
    }

    /**
     * 密文私钥签名交易(单签)
     *
     * @param address
     * @param txHex
     * @return
     */
    public Result signTx(String txHex, String address, String encryptedPrivateKey, String password) {
        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
        signDto.setEncryptedPrivateKey(encryptedPrivateKey);
        signDto.setPassword(password);
        signDtoList.add(signDto);
        return NerveSDKTool.sign(signDtoList, txHex);
    }

    /**
     * 明文私钥签名交易(单签)
     *
     * @param address
     * @param txHex
     * @return
     */
    public Result signTx(String txHex, String address, String privateKey) {
        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
        signDto.setPriKey(privateKey);
        signDtoList.add(signDto);
        return NerveSDKTool.sign(signDtoList, txHex);
    }

    /**
     * 广播交易
     *
     * @param txHex
     * @return
     */
    public Result broadcastTx(String txHex) {
        RpcResult<Map> balanceResult = JsonRpcUtil.request("broadcastTx", ListUtil.of(SDKContext.main_chain_id, txHex));
        RpcResultError rpcResultError = balanceResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        Map result = balanceResult.getResult();
        return Result.getSuccess(result);
    }

    /**
     * 验证交易
     *
     * @param txHex
     * @return
     */
    public Result validateTx(String txHex) {
        validateChainId();
        try {
            if (StringUtils.isBlank(txHex)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "form is empty");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("txHex", txHex);

            RestFulResult restFulResult = RestFulUtil.post("api/accountledger/transaction/validate", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result createMultiSignConsensusTx(MultiSignConsensusDto consensusDto) {
        validateChainId();
        try {
            if (StringUtils.isBlank(consensusDto.getRewardAddress())) {
                consensusDto.setRewardAddress(consensusDto.getAgentAddress());
            }
            CommonValidator.validateMultiSignConsensusDto(consensusDto);
            if (consensusDto.getInput().getAssetChainId() == 0) {
                consensusDto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (consensusDto.getInput().getAssetId() == 0) {
                consensusDto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.REGISTER_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            Agent agent = new Agent();
            agent.setAgentAddress(AddressTool.getAddress(consensusDto.getAgentAddress()));
            agent.setPackingAddress(AddressTool.getAddress(consensusDto.getPackingAddress()));
            agent.setRewardAddress(AddressTool.getAddress(consensusDto.getRewardAddress()));
            agent.setDeposit((consensusDto.getDeposit()));
            tx.setTxData(agent.serialize());

            CoinData coinData = assemblyCoinData(consensusDto.getInput(), agent.getDeposit(), consensusDto.getPubKeys().size(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) consensusDto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : consensusDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(CoinFromDto from, BigInteger amount, int pubKeyCount, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        byte[] nonce = HexUtil.decode(from.getNonce());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), amount, -1);
        coinTos.add(coinTo);

        //txSize = txSize + getMultiSignSignatureSize(pubKeyCount);
        //TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    public Result createMultiSignDepositTx(MultiSignDepositDto dto) {
        validateChainId();
        try {
            CommonValidator.validateMultiSignDepositDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());
            Deposit deposit = new Deposit();
            deposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            deposit.setDeposit(dto.getDeposit());
            tx.setTxData(deposit.serialize());

            CoinData coinData = assemblyCoinData(dto.getInput(), dto.getDeposit(), dto.getPubKeys().size(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 创建取消委托交易
     *
     * @param dto 取消委托交易参数
     * @return result
     */
    public Result createMultiSignWithdrawDepositTx(MultiSignWithDrawDto dto) {
        validateChainId();
        try {
            if (dto.getPrice() == null) {
                dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
            }
            CommonValidator.validateMultiSignWithDrawDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.CANCEL_DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());

            CancelDeposit cancelDeposit = new CancelDeposit();
            cancelDeposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            cancelDeposit.setJoinTxHash(NulsHash.fromHex(dto.getDepositHash()));
            tx.setTxData(cancelDeposit.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);

        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public Result createMultiSignStopConsensusTx(MultiSignStopConsensusDto dto) {
        validateChainId();
        try {
            if (dto.getPrice() == null) {
                dto.setPrice(TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES);
            }
            CommonValidator.validateMultiSignStopConsensusDto(dto);
            for (StopDepositDto depositDto : dto.getDepositList()) {
                if (depositDto.getInput().getAssetChainId() == 0) {
                    depositDto.getInput().setAssetChainId(SDKContext.main_chain_id);
                }
                if (depositDto.getInput().getAssetId() == 0) {
                    depositDto.getInput().setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.STOP_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            StopAgent stopAgent = new StopAgent();
            NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
            stopAgent.setCreateTxHash(nulsHash);
            tx.setTxData(stopAgent.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public Result createMultiSignAliasTx(MultiSignAliasDto aliasDto) {
        validateChainId();
        try {
            CommonValidator.validateMultiSignAliasDto(aliasDto);

            Transaction tx = new Transaction(TxType.ACCOUNT_ALIAS);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(aliasDto.getRemark()));

            Alias alias = new Alias(AddressTool.getAddress(aliasDto.getAddress()), aliasDto.getAlias());
            tx.setTxData(alias.serialize());

            CoinData coinData = assemblyCoinData(aliasDto);
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) aliasDto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : aliasDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }


    public Result swapTradeTx(String from, BigInteger amountIn, NerveToken[] tokenPath, BigInteger amountOutMin, String feeTo, Long deadline, String to, String remark) {
        try {
            int chainId = SDKContext.main_chain_id;
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] feeToBytes = feeTo != null ? AddressTool.getAddress(feeTo) : null;

            int length = tokenPath.length;
            if (length < 2) {
                return Result.getFailed(INVALID_PATH);
            }
            byte[] firstPairAddress = TxUtils.getPairAddress(chainId, tokenPath[0], tokenPath[1]);

            // 组装交易
            NerveToken tokenIn = tokenPath[0];
            SwapTradeData data = new SwapTradeData();
            data.setAmountOutMin(amountOutMin);
            data.setTo(AddressTool.getAddress(to));
            data.setFeeTo(feeTo != null ? AddressTool.getAddress(feeTo) : null);
            data.setDeadline(deadline);
            data.setPath(tokenPath);

            Transaction tx = new Transaction(TxType.SWAP_TRADE);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();
            String nonce = this.checkNonce(from, tokenIn, null);
            froms.add(new CoinFrom(
                    fromBytes,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn,
                    HexUtil.decode(nonce),
                    (byte) 0));
            tos.add(new CoinTo(
                    firstPairAddress,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn));
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));
            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result stableLpSwapTrade(String from, String stablePairAddress, BigInteger amountIn, NerveToken[] tokenPath, BigInteger amountOutMin, String feeTo, Long deadline, String to, String remark) {
        try {
            long currentTimeSeconds = getCurrentTimeSeconds();
            if (deadline == null || deadline.longValue() <= 0) {
                deadline = currentTimeSeconds + 300;
            }
            int chainId = SDKContext.main_chain_id;
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] feeToBytes = feeTo != null ? AddressTool.getAddress(feeTo) : null;

            int length = tokenPath.length;
            if (length < 2) {
                return Result.getFailed(INVALID_PATH);
            }
            byte[] stablePairAddressBytes = AddressTool.getAddress(stablePairAddress);

            // 组装交易
            NerveToken tokenIn = tokenPath[0];
            StableLpSwapTradeData data = new StableLpSwapTradeData();
            data.setAmountOutMin(amountOutMin);
            data.setTo(AddressTool.getAddress(to));
            data.setFeeTo(feeTo != null ? AddressTool.getAddress(feeTo) : null);
            data.setDeadline(deadline);
            data.setPath(tokenPath);

            Transaction tx = new Transaction(TxType.SWAP_STABLE_LP_SWAP_TRADE);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();
            String nonce = this.checkNonce(from, tokenIn, null);
            froms.add(new CoinFrom(
                    fromBytes,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn,
                    HexUtil.decode(nonce),
                    (byte) 0));
            tos.add(new CoinTo(
                    stablePairAddressBytes,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn));
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));
            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result swapTradeStableRemoveLp(String from, BigInteger amountIn, NerveToken[] tokenPath, BigInteger amountOutMin, String feeTo, Long deadline, String to, NerveToken targetToken, String remark) {
        try {
            long currentTimeSeconds = getCurrentTimeSeconds();
            if (deadline == null || deadline.longValue() <= 0) {
                deadline = currentTimeSeconds + 300;
            }
            int chainId = SDKContext.main_chain_id;
            byte[] fromBytes = AddressTool.getAddress(from);
            byte[] feeToBytes = feeTo != null ? AddressTool.getAddress(feeTo) : null;

            int length = tokenPath.length;
            if (length < 2) {
                return Result.getFailed(INVALID_PATH);
            }
            byte[] firstPairAddress = TxUtils.getPairAddress(chainId, tokenPath[0], tokenPath[1]);

            // 组装交易
            NerveToken tokenIn = tokenPath[0];
            SwapTradeStableRemoveLpData data = new SwapTradeStableRemoveLpData();
            data.setAmountOutMin(amountOutMin);
            data.setTo(AddressTool.getAddress(to));
            data.setFeeTo(feeTo != null ? AddressTool.getAddress(feeTo) : null);
            data.setDeadline(deadline);
            data.setPath(tokenPath);
            data.setTargetToken(targetToken);

            Transaction tx = new Transaction(TxType.SWAP_TRADE_SWAP_STABLE_REMOVE_LP);
            tx.setTxData(TxUtils.nulsData2HexBytes(data));
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.isBlank(remark) ? null : StringUtils.bytes(remark));

            CoinData coinData = new CoinData();
            List<CoinFrom> froms = coinData.getFrom();
            List<CoinTo> tos = coinData.getTo();
            String nonce = this.checkNonce(from, tokenIn, null);
            froms.add(new CoinFrom(
                    fromBytes,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn,
                    HexUtil.decode(nonce),
                    (byte) 0));
            tos.add(new CoinTo(
                    firstPairAddress,
                    tokenIn.getChainId(),
                    tokenIn.getAssetId(),
                    amountIn));
            tx.setCoinData(TxUtils.nulsData2HexBytes(coinData));
            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(TxUtils.nulsData2HexBytes(tx)));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }
}

