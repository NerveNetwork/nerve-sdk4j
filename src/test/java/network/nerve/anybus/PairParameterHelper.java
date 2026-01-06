package network.nerve.anybus;

import java.math.BigInteger;


/**
 * Pair Parameter Helper
 * Java implementation of PairParameterHelper.sol
 *
 * This library contains functions to encode and decode parameters of a pair
 * The parameters are stored in a single bytes32 variable (32 bytes / 256 bits)
 *
 * Bit layout:
 * [0-16[: base factor (16 bits) - offset 0
 * [16-28[: filter period (12 bits) - offset 16
 * [28-40[: decay period (12 bits) - offset 28
 * [40-54[: reduction factor (14 bits) - offset 40
 * [54-78[: variable fee control (24 bits) - offset 54
 * [78-92[: protocol share (14 bits) - offset 78
 * [92-112[: max volatility accumulator (20 bits) - offset 92
 * [112-132[: volatility accumulator (20 bits) - offset 112
 * [132-152[: volatility reference (20 bits) - offset 132
 * [152-176[: index reference (24 bits) - offset 152
 * [176-216[: time of last update (40 bits) - offset 176
 * [216-232[: oracle index (16 bits) - offset 216
 * [232-256[: active index (24 bits) - offset 232
 *
 * @author: PierreLuo
 * @date: 2025/10/10
 */
public class PairParameterHelper {

    public static final BigInteger MASK128 = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);
    public static final BigInteger MASK112 = BigInteger.ONE.shiftLeft(112).subtract(BigInteger.ONE);


    // Bit offsets
    public static final int OFFSET_BASE_FACTOR = 0;
    public static final int OFFSET_FILTER_PERIOD = 16;
    public static final int OFFSET_DECAY_PERIOD = 28;
    public static final int OFFSET_REDUCTION_FACTOR = 40;
    public static final int OFFSET_VAR_FEE_CONTROL = 54;
    public static final int OFFSET_PROTOCOL_SHARE = 78;
    public static final int OFFSET_MAX_VOL_ACC = 92;
    public static final int OFFSET_VOL_ACC = 112;
    public static final int OFFSET_VOL_REF = 132;
    public static final int OFFSET_ID_REF = 152;
    public static final int OFFSET_TIME_LAST_UPDATE = 176;
    public static final int OFFSET_ORACLE_ID = 216;
    public static final int OFFSET_ACTIVE_ID = 232;

    // Bit masks
    public static final long MASK_UINT12 = 0xfffL;
    public static final long MASK_UINT14 = 0x3fffL;
    public static final long MASK_UINT16 = 0xffffL;
    public static final long MASK_UINT20 = 0xfffffL;
    public static final long MASK_UINT24 = 0xffffffL;
    public static final long MASK_UINT40 = 0xffffffffffL;

    /**
     * Decode a value from BigInteger at specified offset with mask
     * @param encoded The encoded BigInteger (represents bytes32)
     * @param mask The bit mask
     * @param offset The bit offset
     * @return The decoded value
     */
    private static long decode(BigInteger encoded, long mask, int offset) {
        // Right shift and apply mask
        return encoded.shiftRight(offset).longValue() & mask;
    }

    /**
     * Encode a value into BigInteger at specified offset with mask
     * @param encoded The current encoded value
     * @param value The value to encode
     * @param mask The bit mask
     * @param offset The bit offset
     * @return The new encoded value
     */
    private static BigInteger encode(BigInteger encoded, long value, long mask, int offset) {
        // Clear the bits at offset: encoded & ~(mask << offset)
        BigInteger maskShifted = BigInteger.valueOf(mask).shiftLeft(offset);
        BigInteger cleared = encoded.andNot(maskShifted);

        // Set the new value: cleared | ((value & mask) << offset)
        BigInteger valueShifted = BigInteger.valueOf(value & mask).shiftLeft(offset);
        return cleared.or(valueShifted);
    }

    // ==================== Decode Functions ====================

    /**
     * Get the base factor from the encoded parameters
     * Range: 16 bits (0-65535)
     */
    public static int getBaseFactor(BigInteger params) {
        return (int) decode(params, MASK_UINT16, OFFSET_BASE_FACTOR);
    }

    /**
     * Get the filter period from the encoded parameters
     * Range: 12 bits (0-4095)
     */
    public static int getFilterPeriod(BigInteger params) {
        return (int) decode(params, MASK_UINT12, OFFSET_FILTER_PERIOD);
    }

    /**
     * Get the decay period from the encoded parameters
     * Range: 12 bits (0-4095)
     */
    public static int getDecayPeriod(BigInteger params) {
        return (int) decode(params, MASK_UINT12, OFFSET_DECAY_PERIOD);
    }

    /**
     * Get the reduction factor from the encoded parameters
     * Range: 14 bits (0-16383)
     */
    public static int getReductionFactor(BigInteger params) {
        return (int) decode(params, MASK_UINT14, OFFSET_REDUCTION_FACTOR);
    }

    /**
     * Get the variable fee control from the encoded parameters
     * Range: 24 bits (0-16777215)
     */
    public static int getVariableFeeControl(BigInteger params) {
        return (int) decode(params, MASK_UINT24, OFFSET_VAR_FEE_CONTROL);
    }

    /**
     * Get the protocol share from the encoded parameters
     * Range: 14 bits (0-16383)
     */
    public static int getProtocolShare(BigInteger params) {
        return (int) decode(params, MASK_UINT14, OFFSET_PROTOCOL_SHARE);
    }

    /**
     * Get the max volatility accumulator from the encoded parameters
     * Range: 20 bits (0-1048575)
     */
    public static int getMaxVolatilityAccumulator(BigInteger params) {
        return (int) decode(params, MASK_UINT20, OFFSET_MAX_VOL_ACC);
    }

    /**
     * Get the volatility accumulator from the encoded parameters
     * Range: 20 bits (0-1048575)
     */
    public static int getVolatilityAccumulator(BigInteger params) {
        return (int) decode(params, MASK_UINT20, OFFSET_VOL_ACC);
    }

    /**
     * Get the volatility reference from the encoded parameters
     * Range: 20 bits (0-1048575)
     */
    public static int getVolatilityReference(BigInteger params) {
        return (int) decode(params, MASK_UINT20, OFFSET_VOL_REF);
    }

    /**
     * Get the index reference from the encoded parameters
     * Range: 24 bits (0-16777215)
     */
    public static int getIdReference(BigInteger params) {
        return (int) decode(params, MASK_UINT24, OFFSET_ID_REF);
    }

    /**
     * Get the time of last update from the encoded parameters
     * Range: 40 bits (0-1099511627775)
     */
    public static long getTimeOfLastUpdate(BigInteger params) {
        return decode(params, MASK_UINT40, OFFSET_TIME_LAST_UPDATE);
    }

    /**
     * Get the oracle id from the encoded parameters
     * Range: 16 bits (0-65535)
     */
    public static int getOracleId(BigInteger params) {
        return (int) decode(params, MASK_UINT16, OFFSET_ORACLE_ID);
    }

    /**
     * Get the active id from the encoded parameters
     * Range: 24 bits (0-16777215)
     */
    public static int getActiveId(BigInteger params) {
        return (int) decode(params, MASK_UINT24, OFFSET_ACTIVE_ID);
    }

    // ==================== Fee Calculation Functions ====================

    /**
     * Calculates the base fee, with 18 decimals
     * Formula: baseFee = baseFactor * binStep * 1e10
     * @param params The encoded pair parameters
     * @param binStep The bin step (in basis points)
     * @return baseFee The base fee (in 1e18 format)
     */
    public static BigInteger getBaseFee(BigInteger params, int binStep) {
        int baseFactor = getBaseFactor(params);
        // baseFee = baseFactor * binStep * 1e10
        return BigInteger.valueOf(baseFactor)
                .multiply(BigInteger.valueOf(binStep))
                .multiply(new BigInteger("10000000000")); // 1e10
    }

    /**
     * Calculates the variable fee
     * Formula: variableFee = (volAcc * binStep)^2 * variableFeeControl / 100
     * @param params The encoded pair parameters
     * @param binStep The bin step (in basis points)
     * @return variableFee The variable fee (in 1e18 format)
     */
    public static BigInteger getVariableFee(BigInteger params, int binStep) {
        int variableFeeControl = getVariableFeeControl(params);
        if (variableFeeControl == 0) {
            return BigInteger.ZERO;
        }

        int volAcc = getVolatilityAccumulator(params);
        // variableFee = (volAcc * binStep)^2 * variableFeeControl / 100
        BigInteger prod = BigInteger.valueOf(volAcc).multiply(BigInteger.valueOf(binStep));
        BigInteger prodSquared = prod.multiply(prod);
        BigInteger result = prodSquared.multiply(BigInteger.valueOf(variableFeeControl));
        // Round up: add 99 before dividing by 100
        return result.add(BigInteger.valueOf(99)).divide(BigInteger.valueOf(100));
    }

    /**
     * Calculates the total fee, which is the sum of the base fee and the variable fee
     * @param params The encoded pair parameters
     * @param binStep The bin step (in basis points)
     * @return totalFee The total fee (in 1e18 format)
     */
    public static BigInteger getTotalFee(BigInteger params, int binStep) {
        BigInteger baseFee = getBaseFee(params, binStep);
        BigInteger variableFee = getVariableFee(params, binStep);
        BigInteger totalFee = baseFee.add(variableFee);

        // Cap at 128 bits (uint128 max)
        if (totalFee.bitLength() > 128) {
            // Return max uint128 value
            return MASK128;
        }
        return totalFee;
    }

    // ==================== Encode Functions ====================

    /**
     * Set the base factor in the encoded parameters
     */
    public static BigInteger setBaseFactor(BigInteger params, int baseFactor) {
        return encode(params, baseFactor, MASK_UINT16, OFFSET_BASE_FACTOR);
    }

    /**
     * Set the filter period in the encoded parameters
     */
    public static BigInteger setFilterPeriod(BigInteger params, int filterPeriod) {
        return encode(params, filterPeriod, MASK_UINT12, OFFSET_FILTER_PERIOD);
    }

    /**
     * Set the decay period in the encoded parameters
     */
    public static BigInteger setDecayPeriod(BigInteger params, int decayPeriod) {
        return encode(params, decayPeriod, MASK_UINT12, OFFSET_DECAY_PERIOD);
    }

    /**
     * Set the reduction factor in the encoded parameters
     */
    public static BigInteger setReductionFactor(BigInteger params, int reductionFactor) {
        return encode(params, reductionFactor, MASK_UINT14, OFFSET_REDUCTION_FACTOR);
    }

    /**
     * Set the variable fee control in the encoded parameters
     */
    public static BigInteger setVariableFeeControl(BigInteger params, int variableFeeControl) {
        return encode(params, variableFeeControl, MASK_UINT24, OFFSET_VAR_FEE_CONTROL);
    }

    /**
     * Set the protocol share in the encoded parameters
     */
    public static BigInteger setProtocolShare(BigInteger params, int protocolShare) {
        return encode(params, protocolShare, MASK_UINT14, OFFSET_PROTOCOL_SHARE);
    }

    /**
     * Set the max volatility accumulator in the encoded parameters
     */
    public static BigInteger setMaxVolatilityAccumulator(BigInteger params, int maxVolatilityAccumulator) {
        return encode(params, maxVolatilityAccumulator, MASK_UINT20, OFFSET_MAX_VOL_ACC);
    }

    /**
     * Set the volatility accumulator in the encoded parameters
     */
    public static BigInteger setVolatilityAccumulator(BigInteger params, int volatilityAccumulator) {
        return encode(params, volatilityAccumulator, MASK_UINT20, OFFSET_VOL_ACC);
    }

    /**
     * Set the volatility reference in the encoded parameters
     */
    public static BigInteger setVolatilityReference(BigInteger params, int volatilityReference) {
        return encode(params, volatilityReference, MASK_UINT20, OFFSET_VOL_REF);
    }

    /**
     * Set the index reference in the encoded parameters
     */
    public static BigInteger setIdReference(BigInteger params, int idReference) {
        return encode(params, idReference, MASK_UINT24, OFFSET_ID_REF);
    }

    /**
     * Set the time of last update in the encoded parameters
     */
    public static BigInteger setTimeOfLastUpdate(BigInteger params, long timeOfLastUpdate) {
        return encode(params, timeOfLastUpdate, MASK_UINT40, OFFSET_TIME_LAST_UPDATE);
    }

    /**
     * Set the oracle id in the encoded parameters
     */
    public static BigInteger setOracleId(BigInteger params, int oracleId) {
        return encode(params, oracleId, MASK_UINT16, OFFSET_ORACLE_ID);
    }

    /**
     * Set the active id in the encoded parameters
     */
    public static BigInteger setActiveId(BigInteger params, int activeId) {
        return encode(params, activeId, MASK_UINT24, OFFSET_ACTIVE_ID);
    }

    /**
     * Set all static fee parameters at once
     * This is equivalent to setStaticFeeParameters in Solidity
     */
    public static BigInteger setStaticFeeParameters(
            BigInteger params,
            int baseFactor,
            int filterPeriod,
            int decayPeriod,
            int reductionFactor,
            int variableFeeControl,
            int protocolShare,
            int maxVolatilityAccumulator) {

        BigInteger result = BigInteger.ZERO;
        result = setBaseFactor(result, baseFactor);
        result = setFilterPeriod(result, filterPeriod);
        result = setDecayPeriod(result, decayPeriod);
        result = setReductionFactor(result, reductionFactor);
        result = setVariableFeeControl(result, variableFeeControl);
        result = setProtocolShare(result, protocolShare);
        result = setMaxVolatilityAccumulator(result, maxVolatilityAccumulator);

        // Preserve non-static parameters (bits 112-256) from original params
        // Mask for static parameters: bits 0-112
        BigInteger staticMask = MASK112;
        BigInteger staticPart = result.and(staticMask);
        BigInteger dynamicPart = params.andNot(staticMask);

        return staticPart.or(dynamicPart);
    }

    /**
     * Update index reference to current active id
     */
    public static BigInteger updateIdReference(BigInteger params) {
        int activeId = getActiveId(params);
        return setIdReference(params, activeId);
    }

    /**
     * Convert BigInteger to byte array (32 bytes, big-endian)
     */
    public static byte[] toBytes32(BigInteger value) {
        byte[] result = new byte[32];
        byte[] valueBytes = value.toByteArray();

        // Handle negative values or values with leading sign byte
        int srcPos = 0;
        int length = valueBytes.length;

        if (length > 32) {
            // If there's a sign byte, skip it
            srcPos = length - 32;
            length = 32;
        }

        // Copy to right side (big-endian)
        int destPos = 32 - length;
        System.arraycopy(valueBytes, srcPos, result, destPos, length);

        return result;
    }

    /**
     * Convert byte array (32 bytes) to BigInteger
     */
    public static BigInteger fromBytes32(byte[] bytes) {
        if (bytes.length != 32) {
            throw new IllegalArgumentException("bytes must be 32 bytes long");
        }
        // Use positive BigInteger (unsigned)
        return new BigInteger(1, bytes);
    }

    /**
     * Update volatility accumulator based on active id change
     * @param params The encoded pair parameters
     * @param activeId The current active id
     * @return Updated parameters with new volatility accumulator
     */
    public static BigInteger updateVolatilityAccumulator(BigInteger params, int activeId) {
        int idReference = getIdReference(params);
        int deltaId = Math.abs(activeId - idReference);
        int volRef = getVolatilityReference(params);
        int maxVolAcc = getMaxVolatilityAccumulator(params);

        // volAcc = volRef + deltaId * BASIS_POINT_MAX
        int volAcc = volRef + deltaId * 10000;

        // Cap at max volatility accumulator
        volAcc = Math.min(volAcc, maxVolAcc);

        return setVolatilityAccumulator(params, volAcc);
    }

    /**
     * Update references based on time passage
     * @param params The encoded pair parameters
     * @param timestamp Current timestamp
     * @return Updated parameters
     */
    public static BigInteger updateReferences(BigInteger params, long timestamp) {
        long timeOfLastUpdate = getTimeOfLastUpdate(params);
        long dt = timestamp - timeOfLastUpdate;

        if (dt >= getFilterPeriod(params)) {
            // Update id reference to current active id
            params = updateIdReference(params);

            // Update volatility reference based on decay period
            if (dt < getDecayPeriod(params)) {
                params = updateVolatilityReference(params);
            } else {
                params = setVolatilityReference(params, 0);
            }
        }

        return updateTimeOfLastUpdate(params, timestamp);
    }

    /**
     * Update volatility reference based on current volatility accumulator and reduction factor
     * Formula: volRef = volAcc * reductionFactor / BASIS_POINT_MAX
     * @param params The encoded pair parameters
     * @return Updated parameters with new volatility reference
     */
    public static BigInteger updateVolatilityReference(BigInteger params) {
        int volAcc = getVolatilityAccumulator(params);
        int reductionFactor = getReductionFactor(params);

        // volRef = volAcc * reductionFactor / BASIS_POINT_MAX
        int volRef = (int)((long)volAcc * reductionFactor / 10000L);

        return setVolatilityReference(params, volRef);
    }

    /**
     * Update time of last update
     * @param params The encoded pair parameters
     * @param timestamp New timestamp
     * @return Updated parameters
     */
    public static BigInteger updateTimeOfLastUpdate(BigInteger params, long timestamp) {
        return setTimeOfLastUpdate(params, timestamp);
    }
}



