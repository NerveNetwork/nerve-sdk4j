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

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.core.exception.NulsException;
import network.nerve.kit.model.ParsedEventData;
import network.nerve.kit.model.SwapResult;
import network.nerve.kit.model.event.IToken1155;
import network.nerve.kit.model.event.LBFactoryEvents;
import network.nerve.kit.model.event.LBPairEvents;
import network.nerve.kit.txdata.anybus.AnyBusCallResult;
import network.nerve.kit.txdata.anybus.AnyBusEvent;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 从 SwapResult 列表中解析合约执行产生的事件。
 * business 字段为 AnyBusCallResult 的序列化 hex，反序列化后取 events，
 * 再根据事件名称将 payload 解析为具体事件类型（ERC1155 / LBFactory / LBPair）。
 *
 * <p>使用示例（按 eventName 分支处理所有事件类型）：
 * <pre>{@code
 * List<SwapResult> results = ...; // 合约执行结果
 * List<ParsedEventData> events = SwapResultEventParser.parseEvents(results);
 * for (ParsedEventData e : events) {
 *     String name = e.getEventName();
 *     Object data = e.getEventData();
 *     if ("TransferSingle".equals(name)) {
 *         IToken1155.TransferSingle ev = (IToken1155.TransferSingle) data;
 *         // 使用 ev 的 _operator, _from, _to, _id, _value
 *     } else if ("TransferBatch".equals(name)) {
 *         IToken1155.TransferBatch ev = (IToken1155.TransferBatch) data;
 *     } else if ("ApprovalForAll".equals(name)) {
 *         IToken1155.ApprovalForAll ev = (IToken1155.ApprovalForAll) data;
 *     } else if ("URI".equals(name)) {
 *         IToken1155.URI ev = (IToken1155.URI) data;
 *     } else if ("LBPairCreated".equals(name)) {
 *         LBFactoryEvents.LBPairCreated ev = (LBFactoryEvents.LBPairCreated) data;
 *     } else if ("FeeRecipientSet".equals(name)) {
 *         LBFactoryEvents.FeeRecipientSet ev = (LBFactoryEvents.FeeRecipientSet) data;
 *     } else if ("FlashLoanFeeSet".equals(name)) {
 *         LBFactoryEvents.FlashLoanFeeSet ev = (LBFactoryEvents.FlashLoanFeeSet) data;
 *     } else if ("LBPairImplementationSet".equals(name)) {
 *         LBFactoryEvents.LBPairImplementationSet ev = (LBFactoryEvents.LBPairImplementationSet) data;
 *     } else if ("LBPairIgnoredStateChanged".equals(name)) {
 *         LBFactoryEvents.LBPairIgnoredStateChanged ev = (LBFactoryEvents.LBPairIgnoredStateChanged) data;
 *     } else if ("PresetSet".equals(name)) {
 *         LBFactoryEvents.PresetSet ev = (LBFactoryEvents.PresetSet) data;
 *     } else if ("PresetOpenStateChanged".equals(name)) {
 *         LBFactoryEvents.PresetOpenStateChanged ev = (LBFactoryEvents.PresetOpenStateChanged) data;
 *     } else if ("PresetRemoved".equals(name)) {
 *         LBFactoryEvents.PresetRemoved ev = (LBFactoryEvents.PresetRemoved) data;
 *     } else if ("QuoteAssetAdded".equals(name)) {
 *         LBFactoryEvents.QuoteAssetAdded ev = (LBFactoryEvents.QuoteAssetAdded) data;
 *     } else if ("QuoteAssetRemoved".equals(name)) {
 *         LBFactoryEvents.QuoteAssetRemoved ev = (LBFactoryEvents.QuoteAssetRemoved) data;
 *     } else if ("DepositedToBins".equals(name)) {
 *         LBPairEvents.DepositedToBins ev = (LBPairEvents.DepositedToBins) data;
 *     } else if ("WithdrawnFromBins".equals(name)) {
 *         LBPairEvents.WithdrawnFromBins ev = (LBPairEvents.WithdrawnFromBins) data;
 *     } else if ("CompositionFees".equals(name)) {
 *         LBPairEvents.CompositionFees ev = (LBPairEvents.CompositionFees) data;
 *     } else if ("CollectedProtocolFees".equals(name)) {
 *         LBPairEvents.CollectedProtocolFees ev = (LBPairEvents.CollectedProtocolFees) data;
 *     } else if ("Swap".equals(name)) {
 *         LBPairEvents.Swap ev = (LBPairEvents.Swap) data;
 *         // 使用 ev 的 sender, to, id, amountsIn, amountsOut 等
 *     } else if ("StaticFeeParametersSet".equals(name)) {
 *         LBPairEvents.StaticFeeParametersSet ev = (LBPairEvents.StaticFeeParametersSet) data;
 *     } else if ("HooksParametersSet".equals(name)) {
 *         LBPairEvents.HooksParametersSet ev = (LBPairEvents.HooksParametersSet) data;
 *     } else if ("FlashLoan".equals(name)) {
 *         LBPairEvents.FlashLoan ev = (LBPairEvents.FlashLoan) data;
 *     } else if ("OracleLengthIncreased".equals(name)) {
 *         LBPairEvents.OracleLengthIncreased ev = (LBPairEvents.OracleLengthIncreased) data;
 *     } else if ("ForcedDecay".equals(name)) {
 *         LBPairEvents.ForcedDecay ev = (LBPairEvents.ForcedDecay) data;
 *     }
 *     // 公共信息: e.getContractAddress(), e.getBlockNumber()
 * }
 * }</pre>
 *
 * @author: PierreLuo
 * @date: 2025/2/9
 */
public final class SwapResultEventParser {

    private SwapResultEventParser() {
    }

    /**
     * 解析 List&lt;SwapResult&gt; 中所有 business 里的事件，返回解析后的事件数据列表。
     *
     * @param swapResults 合约执行结果列表
     * @return 解析后的事件数据（顺序与原始事件一致；无法解析的 payload 时 eventData 为 null）
     */
    public static List<ParsedEventData> parseEvents(List<SwapResult> swapResults) {
        if (swapResults == null || swapResults.isEmpty()) {
            return Collections.emptyList();
        }
        List<ParsedEventData> out = new ArrayList<>();
        for (SwapResult sr : swapResults) {
            String business = sr != null ? sr.getBusiness() : null;
            if (business == null || business.isEmpty()) {
                continue;
            }
            try {
                AnyBusCallResult callResult = deserializeBusiness(business);
                if (callResult == null || callResult.getEvents() == null) {
                    continue;
                }
                for (AnyBusEvent ev : callResult.getEvents()) {
                    ParsedEventData parsed = toParsedEventData(ev);
                    if (parsed != null) {
                        out.add(parsed);
                    }
                }
            } catch (Exception ignored) {
                // 单条反序列化或解析失败时跳过，不中断整体
            }
        }
        return out;
    }

    /**
     * 将 business 的 hex 字符串反序列化为 AnyBusCallResult。
     */
    public static AnyBusCallResult deserializeBusiness(String businessHex) throws NulsException {
        if (businessHex == null || businessHex.isEmpty()) {
            return null;
        }
        byte[] bytes = AnyBusUtil.hexToBytes(businessHex);
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        NulsByteBuffer buffer = new NulsByteBuffer(bytes);
        AnyBusCallResult result = new AnyBusCallResult();
        result.parse(buffer);
        return result;
    }

    /**
     * 将 AnyBusEvent 转为 ParsedEventData，根据 event 名称解析 payload 为具体事件对象。
     */
    public static ParsedEventData toParsedEventData(AnyBusEvent ev) {
        if (ev == null) {
            return null;
        }
        List<String> payload = ev.getPayload() != null ? ev.getPayload() : Collections.emptyList();
        Object eventData = parseEventPayload(ev.getEvent(), payload);
        ParsedEventData parsed = new ParsedEventData();
        parsed.setContractAddress(ev.getContractAddress());
        parsed.setBlockNumber(ev.getBlockNumber());
        parsed.setEventName(ev.getEvent());
        parsed.setEventData(eventData);
        return parsed;
    }

    private static String get(List<String> payload, int index) {
        return (payload != null && index < payload.size()) ? payload.get(index) : "";
    }

    private static Object parseEventPayload(String eventName, List<String> payload) {
        if (eventName == null) {
            return null;
        }
        try {
            switch (eventName) {
                // ---------- IToken1155 (ERC1155) ----------
                case "TransferSingle": {
                    String op = get(payload, 0);
                    String from = get(payload, 1);
                    String to = get(payload, 2);
                    BigInteger id = parseBigInteger(get(payload, 3));
                    BigInteger value = parseBigInteger(get(payload, 4));
                    return new IToken1155.TransferSingle(op, from, to, id, value);
                }
                case "TransferBatch": {
                    String op = get(payload, 0);
                    String from = get(payload, 1);
                    String to = get(payload, 2);
                    BigInteger[] ids = parseBigIntegerArray(get(payload, 3));
                    BigInteger[] values = parseBigIntegerArray(get(payload, 4));
                    return new IToken1155.TransferBatch(op, from, to, ids, values);
                }
                case "ApprovalForAll": {
                    String owner = get(payload, 0);
                    String operator = get(payload, 1);
                    Boolean approved = parseBoolean(get(payload, 2));
                    return new IToken1155.ApprovalForAll(owner, operator, approved);
                }
                case "URI": {
                    String value = get(payload, 0);
                    BigInteger id = parseBigInteger(get(payload, 1));
                    return new IToken1155.URI(value, id);
                }
                // ---------- LBFactoryEvents ----------
                case "LBPairCreated": {
                    String tokenX = get(payload, 0);
                    String tokenY = get(payload, 1);
                    Integer binStep = parseInteger(get(payload, 2));
                    String lbPair = get(payload, 3);
                    Integer pid = parseInteger(get(payload, 4));
                    return new LBFactoryEvents.LBPairCreated(tokenX, tokenY, binStep, lbPair, pid);
                }
                case "FeeRecipientSet": {
                    String oldR = get(payload, 0);
                    String newR = get(payload, 1);
                    return new LBFactoryEvents.FeeRecipientSet(oldR, newR);
                }
                case "FlashLoanFeeSet": {
                    BigInteger oldF = parseBigInteger(get(payload, 0));
                    BigInteger newF = parseBigInteger(get(payload, 1));
                    return new LBFactoryEvents.FlashLoanFeeSet(oldF, newF);
                }
                case "LBPairImplementationSet": {
                    String oldImpl = get(payload, 0);
                    String impl = get(payload, 1);
                    return new LBFactoryEvents.LBPairImplementationSet(oldImpl, impl);
                }
                case "LBPairIgnoredStateChanged": {
                    String lbPair = get(payload, 0);
                    Boolean ignored = parseBoolean(get(payload, 1));
                    return new LBFactoryEvents.LBPairIgnoredStateChanged(lbPair, ignored);
                }
                case "PresetSet": {
                    Integer binStep = parseInteger(get(payload, 0));
                    Integer baseFactor = parseInteger(get(payload, 1));
                    Integer filterPeriod = parseInteger(get(payload, 2));
                    Integer decayPeriod = parseInteger(get(payload, 3));
                    Integer reductionFactor = parseInteger(get(payload, 4));
                    Integer variableFeeControl = parseInteger(get(payload, 5));
                    Integer protocolShare = parseInteger(get(payload, 6));
                    Integer maxVolatilityAccumulator = parseInteger(get(payload, 7));
                    return new LBFactoryEvents.PresetSet(binStep, baseFactor, filterPeriod, decayPeriod,
                            reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator);
                }
                case "PresetOpenStateChanged": {
                    Integer binStep = parseInteger(get(payload, 0));
                    Boolean isOpen = parseBoolean(get(payload, 1));
                    return new LBFactoryEvents.PresetOpenStateChanged(binStep, isOpen);
                }
                case "PresetRemoved": {
                    Integer binStep = parseInteger(get(payload, 0));
                    return new LBFactoryEvents.PresetRemoved(binStep);
                }
                case "QuoteAssetAdded": {
                    String quoteAsset = get(payload, 0);
                    return new LBFactoryEvents.QuoteAssetAdded(quoteAsset);
                }
                case "QuoteAssetRemoved": {
                    String quoteAsset = get(payload, 0);
                    return new LBFactoryEvents.QuoteAssetRemoved(quoteAsset);
                }
                // ---------- LBPairEvents ----------
                case "DepositedToBins": {
                    String sender = get(payload, 0);
                    String to = get(payload, 1);
                    Integer[] ids = parseIntegerArray(get(payload, 2));
                    byte[][] amounts = parseByteArrayArray(get(payload, 3));
                    return new LBPairEvents.DepositedToBins(sender, to, ids, amounts);
                }
                case "WithdrawnFromBins": {
                    String sender = get(payload, 0);
                    String to = get(payload, 1);
                    Integer[] ids = parseIntegerArray(get(payload, 2));
                    byte[][] amounts = parseByteArrayArray(get(payload, 3));
                    return new LBPairEvents.WithdrawnFromBins(sender, to, ids, amounts);
                }
                case "CompositionFees": {
                    String sender = get(payload, 0);
                    Integer id = parseInteger(get(payload, 1));
                    byte[] totalFees = parseByteArray(get(payload, 2));
                    byte[] protocolFees = parseByteArray(get(payload, 3));
                    return new LBPairEvents.CompositionFees(sender, id, totalFees, protocolFees);
                }
                case "CollectedProtocolFees": {
                    String feeRecipient = get(payload, 0);
                    byte[] protocolFees = parseByteArray(get(payload, 1));
                    return new LBPairEvents.CollectedProtocolFees(feeRecipient, protocolFees);
                }
                case "Swap": {
                    String sender = get(payload, 0);
                    String to = get(payload, 1);
                    Integer id = parseInteger(get(payload, 2));
                    byte[] amountsIn = parseByteArray(get(payload, 3));
                    byte[] amountsOut = parseByteArray(get(payload, 4));
                    Integer volatilityAccumulator = parseInteger(get(payload, 5));
                    byte[] totalFees = parseByteArray(get(payload, 6));
                    byte[] protocolFees = parseByteArray(get(payload, 7));
                    return new LBPairEvents.Swap(sender, to, id, amountsIn, amountsOut,
                            volatilityAccumulator, totalFees, protocolFees);
                }
                case "StaticFeeParametersSet": {
                    String sender = get(payload, 0);
                    Integer baseFactor = parseInteger(get(payload, 1));
                    Integer filterPeriod = parseInteger(get(payload, 2));
                    Integer decayPeriod = parseInteger(get(payload, 3));
                    Integer reductionFactor = parseInteger(get(payload, 4));
                    Integer variableFeeControl = parseInteger(get(payload, 5));
                    Integer protocolShare = parseInteger(get(payload, 6));
                    Integer maxVolatilityAccumulator = parseInteger(get(payload, 7));
                    return new LBPairEvents.StaticFeeParametersSet(sender, baseFactor, filterPeriod, decayPeriod,
                            reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator);
                }
                case "HooksParametersSet": {
                    String sender = get(payload, 0);
                    byte[] hooksParameters = parseByteArray(get(payload, 1));
                    return new LBPairEvents.HooksParametersSet(sender, hooksParameters);
                }
                case "FlashLoan": {
                    String sender = get(payload, 0);
                    String receiver = get(payload, 1);
                    Integer activeId = parseInteger(get(payload, 2));
                    byte[] amounts = parseByteArray(get(payload, 3));
                    byte[] totalFees = parseByteArray(get(payload, 4));
                    byte[] protocolFees = parseByteArray(get(payload, 5));
                    return new LBPairEvents.FlashLoan(sender, receiver, activeId, amounts, totalFees, protocolFees);
                }
                case "OracleLengthIncreased": {
                    String sender = get(payload, 0);
                    Integer oracleLength = parseInteger(get(payload, 1));
                    return new LBPairEvents.OracleLengthIncreased(sender, oracleLength);
                }
                case "ForcedDecay": {
                    String sender = get(payload, 0);
                    Integer idRef = parseInteger(get(payload, 1));
                    Integer volatilityRef = parseInteger(get(payload, 2));
                    return new LBPairEvents.ForcedDecay(sender, idRef, volatilityRef);
                }
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static BigInteger parseBigInteger(String s) {
        if (s == null || s.isEmpty()) {
            return BigInteger.ZERO;
        }
        try {
            return new BigInteger(s);
        } catch (NumberFormatException e) {
            return BigInteger.ZERO;
        }
    }

    private static Boolean parseBoolean(String s) {
        if (s == null || s.isEmpty()) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(s);
    }

    private static Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static byte[] parseByteArray(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        byte[] b = AnyBusUtil.hexToBytes(hex);
        return b != null ? b : new byte[0];
    }

    private static BigInteger[] parseBigIntegerArray(String s) {
        if (s == null || s.isEmpty()) {
            return new BigInteger[0];
        }
        String[] parts = s.split(",");
        BigInteger[] arr = new BigInteger[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = parseBigInteger(parts[i].trim());
        }
        return arr;
    }

    private static Integer[] parseIntegerArray(String s) {
        if (s == null || s.isEmpty()) {
            return new Integer[0];
        }
        String[] parts = s.split(",");
        Integer[] arr = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = parseInteger(parts[i].trim());
        }
        return arr;
    }

    private static byte[][] parseByteArrayArray(String s) {
        if (s == null || s.isEmpty()) {
            return new byte[0][];
        }
        String[] parts = s.split(",");
        byte[][] arr = new byte[parts.length][];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = parseByteArray(parts[i].trim());
        }
        return arr;
    }
}
