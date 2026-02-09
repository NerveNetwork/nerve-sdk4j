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
package network.nerve.kit.util;


import network.nerve.base.basic.AddressTool;
import network.nerve.core.constant.BaseConstant;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.crypto.Sha256Hash;
import network.nerve.core.model.ArraysTool;
import network.nerve.core.parse.SerializeUtils;
import network.nerve.kit.model.NerveToken;
import network.nerve.kit.txdata.anybus.ProgramTransfer;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static network.nerve.core.model.StringUtils.isBlank;
import static network.nerve.kit.util.TxUtils.parseTokenStr;

/**
 * @author: PierreLuo
 * @date: 2025/12/4
 */
public class AnyBusUtil {

    static final String STRING = "String";
    public static String[][] twoDimensionalArray(Object[] args, String[] types) {
        if (args == null) {
            return null;
        } else {
            int length = args.length;
            String[][] two = new String[length][];
            Object arg;
            for (int i = 0; i < length; i++) {
                arg = args[i];
                if (arg == null) {
                    two[i] = new String[0];
                    continue;
                }
                if (arg instanceof String) {
                    String argStr = (String) arg;
                    // 非String类型参数，若传参是空字符串，则赋值为空一维数组，避免数字类型转化异常 -> 空字符串转化为数字
                    if (types != null && isBlank(argStr) && !STRING.equalsIgnoreCase(types[i])) {
                        two[i] = new String[0];
                    } else {
                        two[i] = new String[]{argStr};
                    }
                } else if (arg.getClass().isArray()) {
                    int len = Array.getLength(arg);
                    String[] result = new String[len];
                    for (int k = 0; k < len; k++) {
                        result[k] = valueOf(Array.get(arg, k));
                    }
                    two[i] = result;
                } else if (arg instanceof List) {
                    List resultArg = (List) arg;
                    int size = resultArg.size();
                    String[] result = new String[size];
                    for (int k = 0; k < size; k++) {
                        result[k] = valueOf(resultArg.get(k));
                    }
                    two[i] = result;
                } else {
                    two[i] = new String[]{valueOf(arg)};
                }
            }
            return two;
        }
    }

    public static String[][] twoDimensionalArray(Object[] args) {
        return twoDimensionalArray(args, null);
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    public static String[] _sortTokens(String tokenA, String tokenB) {
        NerveToken token0 = parseTokenStr(tokenA);
        NerveToken token1 = parseTokenStr(tokenB);
        if (token0.getChainId() == token1.getChainId() && token0.getAssetId() == token1.getAssetId()) {
            throw new RuntimeException("same token error");
        }
        boolean positiveSequence = token0.getChainId() < token1.getChainId() || (token0.getChainId() == token1.getChainId() && token0.getAssetId() < token1.getAssetId());
        if (positiveSequence) {
            return new String[]{tokenA, tokenB};
        }
        return new String[]{tokenB, tokenA};
    }

    public static String _generateToken1155Address(int chainId, byte[] owner, byte[] nonce) {
        byte[] all = ArraysTool.concatenate(
                Sha256Hash.hash(owner),
                Sha256Hash.hash("Token1155".getBytes(StandardCharsets.UTF_8)),
                Sha256Hash.hash(nonce)
        );
        byte[] addressBytes = AddressTool.getAddress(Sha256Hash.hash(all), chainId, BaseConstant.CONTRACT_ADDRESS_TYPE);
        return AddressTool.getStringAddressByBytes(addressBytes);
    }

    public static String _generateLBFactoryAddress(int chainId, byte[] owner, byte[] nonce) {
        byte[] all = ArraysTool.concatenate(
                Sha256Hash.hash(owner),
                Sha256Hash.hash("LBFactory".getBytes(StandardCharsets.UTF_8)),
                Sha256Hash.hash(nonce)
        );
        byte[] addressBytes = AddressTool.getAddress(Sha256Hash.hash(all), chainId, BaseConstant.CONTRACT_ADDRESS_TYPE);
        return AddressTool.getStringAddressByBytes(addressBytes);
    }

    public static String _generateLBRouterAddress(int chainId, byte[] owner, byte[] nonce) {
        byte[] all = ArraysTool.concatenate(
                Sha256Hash.hash(owner),
                Sha256Hash.hash("LBRouter".getBytes(StandardCharsets.UTF_8)),
                Sha256Hash.hash(nonce)
        );
        byte[] addressBytes = AddressTool.getAddress(Sha256Hash.hash(all), chainId, BaseConstant.CONTRACT_ADDRESS_TYPE);
        return AddressTool.getStringAddressByBytes(addressBytes);
    }

    /**
     * Generate a deterministic pair address
     * In real implementation, this would be a contract deployment
     */
    public static String _generatePairAddress(int chainId, byte[] owner, String tokenX, String tokenY, int binStep) {
        // Sort tokens for storage efficiency
        String[] sorted = _sortTokens(tokenX, tokenY);
        NerveToken tokenA = parseTokenStr(sorted[0]);
        NerveToken tokenB = parseTokenStr(sorted[1]);

        byte[] all = ArraysTool.concatenate(
                Sha256Hash.hash(owner),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(tokenA.getChainId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(tokenA.getAssetId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(tokenB.getChainId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(tokenB.getAssetId())),
                Sha256Hash.hash(SerializeUtils.int32ToBytes(binStep))
        );
        byte[] addressBytes = AddressTool.getAddress(Sha256Hash.hash(all), chainId, BaseConstant.CONTRACT_ADDRESS_TYPE);
        return AddressTool.getStringAddressByBytes(addressBytes);
    }

    /**
     * Converts field values to strings.
     * Supported types: String, BigInteger, Boolean, array types, byte[] (converted to hex), etc.
     */
    public static String convertValueToString(Object value) {
        if (value == null) {
            return "";
        }

        // 优先处理 byte[]，直接转换为 hex 字符串
        if (value instanceof byte[]) {
            return bytesToHex((byte[]) value);
        }

        // 处理其他数组类型（如 Integer[], byte[][] 等）
        if (value.getClass().isArray()) {
            return convertArrayToString(value);
        }

        // 处理基本类型
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Integer || value instanceof Long) {
            return value.toString();
        } else {
            // 其他类型转换为字符串
            return value.toString();
        }
    }

    /**
     * Convert byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return HexUtil.encode(bytes);
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return null;
        }
        return HexUtil.decode(hex);
    }

    /**
     * Convert the array to a string (comma-separated).
     * Handles nested arrays like byte[][] by converting each byte[] to hex.
     */
    private static String convertArrayToString(Object array) {
        if (array == null) {
            return "";
        }

        int length = Array.getLength(array);
        if (length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            Object element = Array.get(array, i);
            // If element is byte[], convert to hex; otherwise use convertValueToString
            if (element instanceof byte[]) {
                sb.append(bytesToHex((byte[]) element));
            } else {
                sb.append(convertValueToString(element));
            }
        }
        return sb.toString();
    }

    /**
     * 合并 ProgramTransfer 列表，计算每个地址的净余额
     *
     * 计算规则：
     * 1. 使用 地址+chainId+assetId 作为 key
     * 2. to 地址累加 value（接收资产）
     * 3. from 地址累减 value（支出资产）
     * 4. 最终 map 中：
     *    - value > 0：该地址接收了资产（净收入）
     *    - value < 0：该地址支出了资产（净支出）
     *    - value == 0：该地址该资产净余额为0（收支平衡）
     *
     * @param transfers 转账列表
     * @return Map<String, BigInteger>，key 格式：地址_hex|chainId|assetId，value 为净余额
     */
    public static Map<String, BigInteger> mergeTransfersByBalance(List<ProgramTransfer> transfers) {
        Map<String, BigInteger> balanceMap = new HashMap<>();

        if (transfers == null || transfers.isEmpty()) {
            return balanceMap;
        }

        for (ProgramTransfer transfer : transfers) {
            if (transfer == null) {
                continue;
            }

            BigInteger value = transfer.getValue();
            if (value == null || value.compareTo(BigInteger.ZERO) == 0) {
                continue;
            }

            int chainId = transfer.getAssetChainId();
            int assetId = transfer.getAssetId();

            // to 地址累加 value（接收资产）
            if (transfer.getTo() != null) {
                String toKey = generateBalanceKey(transfer.getTo(), chainId, assetId);
                balanceMap.put(toKey, balanceMap.getOrDefault(toKey, BigInteger.ZERO).add(value));
            }

            // from 地址累减 value（支出资产）
            if (transfer.getFrom() != null) {
                String fromKey = generateBalanceKey(transfer.getFrom(), chainId, assetId);
                balanceMap.put(fromKey, balanceMap.getOrDefault(fromKey, BigInteger.ZERO).subtract(value));
            }
        }

        // 移除净余额为0的项（可选，根据需求决定是否保留）
        balanceMap.entrySet().removeIf(entry -> entry.getValue().compareTo(BigInteger.ZERO) == 0);

        return balanceMap;
    }

    /**
     * 生成余额键
     * 格式：地址_hex|chainId|assetId
     */
    public static String generateBalanceKey(byte[] address, int chainId, int assetId) {
        String addressHex = bytesToHex(address);
        return addressHex + "|" + chainId + "|" + assetId;
    }
}
