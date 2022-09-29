package network.nerve.kit.util;

import network.nerve.SDKContext;
import network.nerve.base.basic.AddressTool;
import network.nerve.base.basic.TransactionFeeCalculator;
import network.nerve.base.data.BaseNulsData;
import network.nerve.base.data.CoinFrom;
import network.nerve.base.data.CoinTo;
import network.nerve.base.signture.P2PHKSignature;
import network.nerve.core.constant.CommonCodeConstanst;
import network.nerve.core.crypto.Sha256Hash;
import network.nerve.core.exception.NulsException;
import network.nerve.core.exception.NulsRuntimeException;
import network.nerve.core.model.ArraysTool;
import network.nerve.core.model.BigIntegerUtils;
import network.nerve.core.model.StringUtils;
import network.nerve.core.parse.SerializeUtils;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.NerveToken;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static network.nerve.SDKContext.*;

public class TxUtils {

    private static final String HEX_REGEX="^[A-Fa-f0-9]+$";
    private static final byte PAIR_ADDRESS_TYPE = 4;

    public static boolean isMainAsset(int chainId, int assetId) {
        return chainId == main_chain_id && assetId == SDKContext.main_asset_id;
    }

    public static boolean isNulsAsset(int chainId, int assetId) {
        return chainId == nuls_chain_id && assetId == nuls_asset_id;
    }

    public static void calcTxFee(List<CoinFrom> coinFroms, List<CoinTo> coinTos, int txSize) throws NulsException {
        BigInteger totalFrom = BigInteger.ZERO;
        boolean fromAddressIsMain = true;
        for (CoinFrom coinFrom : coinFroms) {
            if (fromAddressIsMain) {
                // 判断from地址是否是主网地址
                if (!AddressTool.validNormalAddress(coinFrom.getAddress(), main_chain_id)) {
                    fromAddressIsMain = false;
                }
            }
            txSize += coinFrom.size();
            if (TxUtils.isMainAsset(coinFrom.getAssetsChainId(), coinFrom.getAssetsId())) {
                totalFrom = totalFrom.add(coinFrom.getAmount());
            }
        }
        BigInteger totalTo = BigInteger.ZERO;
        boolean toAddressIsMain = true;
        for (CoinTo coinTo : coinTos) {
            if (toAddressIsMain) {
                // 判断from地址是否是主网地址
                if (!AddressTool.validNormalAddress(coinTo.getAddress(), main_chain_id)) {
                    toAddressIsMain = false;
                }
            }
            txSize += coinTo.size();
            if (TxUtils.isMainAsset(coinTo.getAssetsChainId(), coinTo.getAssetsId())) {
                totalTo = totalTo.add(coinTo.getAmount());
            }
        }
        if (fromAddressIsMain && toAddressIsMain) {
            // 2021-4-25 都是主网无需验证手续费, 不收手续费
            return;
        }
        //本交易预计收取的手续费
        BigInteger targetFee = TransactionFeeCalculator.getNormalTxFee(txSize);
        //实际收取的手续费, 可能自己已经组装完成
        BigInteger actualFee = totalFrom.subtract(totalTo);
        if (BigIntegerUtils.isLessThan(actualFee, BigInteger.ZERO)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_FEE);
        } else if (BigIntegerUtils.isLessThan(actualFee, targetFee)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_FEE);
        }
    }


    public static BigInteger calcTransferTxFee(int addressCount, int fromLength, int toLength, String remark, BigInteger price) {
        int size = 10;
        size += addressCount * P2PHKSignature.SERIALIZE_LENGTH;
        size += 70 * fromLength;
        size += 68 * toLength;
        if (StringUtils.isNotBlank(remark)) {
            size += StringUtils.bytes(remark).length;
        }
        size = size / 1024 + 1;
        return price.multiply(new BigInteger(size + ""));
    }

    public static Map<String, BigInteger> calcCrossTxFee(int addressCount, int fromLength, int toLength, String remark) {
        int size = 10;
        size += addressCount * P2PHKSignature.SERIALIZE_LENGTH;
        size += 70 * fromLength;
        size += 68 * toLength;
        if (StringUtils.isNotBlank(remark)) {
            size += StringUtils.bytes(remark).length;
        }
        size = size / 1024 + 1;
        BigInteger fee = TransactionFeeCalculator.getCrossTxFee(size);
        Map<String, BigInteger> map = new HashMap<>();
        BigInteger localFee = TransactionFeeCalculator.getCrossTxFee(size);
        map.put("LOCAL", localFee);
        map.put("NULS", fee);
        return map;
    }


    public static BigInteger calcStopConsensusTxFee(int fromLength, int toLength, BigInteger price) {
        int size = 152;
        size += 70 * fromLength;
        size += 68 * toLength;
        size = size / 1024 + 1;
        return price.multiply(new BigInteger(size + ""));
    }

    /**
     * 根据交易HASH获取NONCE（交易HASH后8位）
     * Obtain NONCE according to HASH (the last 8 digits of HASH)
     */
    public static byte[] getNonce(byte[] txHash) {
        byte[] targetArr = new byte[8];
        System.arraycopy(txHash, txHash.length - 8, targetArr, 0, 8);
        return targetArr;
    }

    public static byte[] nulsData2HexBytes(BaseNulsData nulsData) {
        try {
            return nulsData.serialize();
        } catch (IOException e) {
            throw new NulsRuntimeException(e);
        }
    }

    public static boolean isHexStr(String str) {
        if(StringUtils.isBlank(str)) {
            return false;
        }
        return str.matches(HEX_REGEX);
    }

    public static String addressToLowerCase(String address) {
        if (StringUtils.isBlank(address)) {
            return address;
        }
        String validAddress = cleanHexPrefix(address);
        if (isHexStr(validAddress)) {
            address = address.toLowerCase();
        }
        return address;
    }

    public static String cleanHexPrefix(String input) {
        return containsHexPrefix(input) ? input.substring(2) : input;
    }

    public static boolean containsHexPrefix(String input) {
        return !StringUtils.isBlank(input) && input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    public static String getStringPairAddress(int chainId, NerveToken token0, NerveToken token1) {
        return AddressTool.getStringAddressByBytes(getPairAddress(chainId, token0, token1));
    }

    public static String getStringPairAddress(int chainId, NerveToken token0, NerveToken token1, String prefix) {
        return AddressTool.getStringAddressByBytes(getPairAddress(chainId, token0, token1), prefix);
    }

    public static byte[] getPairAddress(int chainId, NerveToken token0, NerveToken token1) {
        return getSwapAddress(chainId, token0, token1, PAIR_ADDRESS_TYPE);
    }

    private static byte[] getSwapAddress(int chainId, NerveToken token0, NerveToken token1, byte addressType) {
        if (token0 == null || token1 == null) {
            throw new NulsRuntimeException(CommonCodeConstanst.NULL_PARAMETER);
        }
        NerveToken[] array = tokenSort(token0, token1);
        byte[] all = ArraysTool.concatenate(
                Sha256Hash.hash(SerializeUtils.int32ToBytes(array[0].getChainId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(array[0].getAssetId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(array[1].getChainId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(array[1].getAssetId()))
        );
        return AddressTool.getAddress(Sha256Hash.hash(all), chainId, addressType);
    }

    public static NerveToken[] tokenSort(NerveToken token0, NerveToken token1) {
        if (token0 == null || token1 == null) {
            throw new NulsRuntimeException(CommonCodeConstanst.NULL_PARAMETER);
        }
        if (token0.getChainId() == token1.getChainId() && token0.getAssetId() == token1.getAssetId()) {
            throw new NulsRuntimeException(CommonCodeConstanst.PARAMETER_ERROR);
        }
        boolean positiveSequence = token0.getChainId() < token1.getChainId() || (token0.getChainId() == token1.getChainId() && token0.getAssetId() < token1.getAssetId());
        if (positiveSequence) {
            return new NerveToken[]{token0, token1};
        }
        return new NerveToken[]{token1, token0};
    }
}
