package network.nerve.kit.model.event;


import network.nerve.kit.txdata.anybus.AnyBusBaseEvent;

/**
 * Event classes for LBPair contract
 * @author: PierreLuo
 * @date: 2025/12/24
 */
public class LBPairEvents {

    /**
     * Emitted when tokens are deposited to bins
     */
    public static class DepositedToBins implements AnyBusBaseEvent {
        private String sender;
        private String to;
        private Integer[] ids; // uint256[] in Solidity, but ids are uint24 (small values)
        private byte[][] amounts; // bytes32[]

        public DepositedToBins(String sender, String to, Integer[] ids, byte[][] amounts) {
            this.sender = sender;
            this.to = to;
            this.ids = ids;
            this.amounts = amounts;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, to, ids, amounts};
        }
    }

    /**
     * Emitted when tokens are withdrawn from bins
     */
    public static class WithdrawnFromBins implements AnyBusBaseEvent {
        private String sender;
        private String to;
        private Integer[] ids; // uint256[] in Solidity, but ids are uint24 (small values)
        private byte[][] amounts; // bytes32[]

        public WithdrawnFromBins(String sender, String to, Integer[] ids, byte[][] amounts) {
            this.sender = sender;
            this.to = to;
            this.ids = ids;
            this.amounts = amounts;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, to, ids, amounts};
        }
    }

    /**
     * Emitted when composition fees are charged
     */
    public static class CompositionFees implements AnyBusBaseEvent {
        private String sender;
        private Integer id; // uint24
        private byte[] totalFees; // bytes32
        private byte[] protocolFees; // bytes32

        public CompositionFees(String sender, Integer id, byte[] totalFees, byte[] protocolFees) {
            this.sender = sender;
            this.id = id;
            this.totalFees = totalFees;
            this.protocolFees = protocolFees;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, id, totalFees, protocolFees};
        }
    }

    /**
     * Emitted when protocol fees are collected
     */
    public static class CollectedProtocolFees implements AnyBusBaseEvent {
        private String feeRecipient;
        private byte[] protocolFees; // bytes32

        public CollectedProtocolFees(String feeRecipient, byte[] protocolFees) {
            this.feeRecipient = feeRecipient;
            this.protocolFees = protocolFees;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{feeRecipient, protocolFees};
        }
    }

    /**
     * Emitted when a swap occurs
     */
    public static class Swap implements AnyBusBaseEvent {
        private String sender;
        private String to;
        private Integer id; // uint24
        private byte[] amountsIn; // bytes32
        private byte[] amountsOut; // bytes32
        private Integer volatilityAccumulator; // uint24
        private byte[] totalFees; // bytes32
        private byte[] protocolFees; // bytes32

        public Swap(
                String sender,
                String to,
                Integer id,
                byte[] amountsIn,
                byte[] amountsOut,
                Integer volatilityAccumulator,
                byte[] totalFees,
                byte[] protocolFees
        ) {
            this.sender = sender;
            this.to = to;
            this.id = id;
            this.amountsIn = amountsIn;
            this.amountsOut = amountsOut;
            this.volatilityAccumulator = volatilityAccumulator;
            this.totalFees = totalFees;
            this.protocolFees = protocolFees;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, to, id, amountsIn, amountsOut,
                    volatilityAccumulator, totalFees, protocolFees};
        }
    }

    /**
     * Emitted when static fee parameters are set
     */
    public static class StaticFeeParametersSet implements AnyBusBaseEvent {
        private String sender;
        private Integer baseFactor; // uint16
        private Integer filterPeriod; // uint16
        private Integer decayPeriod; // uint16
        private Integer reductionFactor; // uint16
        private Integer variableFeeControl; // uint24
        private Integer protocolShare; // uint16
        private Integer maxVolatilityAccumulator; // uint24

        public StaticFeeParametersSet(
                String sender,
                Integer baseFactor,
                Integer filterPeriod,
                Integer decayPeriod,
                Integer reductionFactor,
                Integer variableFeeControl,
                Integer protocolShare,
                Integer maxVolatilityAccumulator
        ) {
            this.sender = sender;
            this.baseFactor = baseFactor;
            this.filterPeriod = filterPeriod;
            this.decayPeriod = decayPeriod;
            this.reductionFactor = reductionFactor;
            this.variableFeeControl = variableFeeControl;
            this.protocolShare = protocolShare;
            this.maxVolatilityAccumulator = maxVolatilityAccumulator;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, baseFactor, filterPeriod, decayPeriod,
                    reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator};
        }
    }

    /**
     * Emitted when hooks parameters are set
     */
    public static class HooksParametersSet implements AnyBusBaseEvent {
        private String sender;
        private byte[] hooksParameters; // bytes32

        public HooksParametersSet(String sender, byte[] hooksParameters) {
            this.sender = sender;
            this.hooksParameters = hooksParameters;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, hooksParameters};
        }
    }

    /**
     * Emitted when a flash loan occurs
     */
    public static class FlashLoan implements AnyBusBaseEvent {
        private String sender;
        private String receiver; // ILBFlashLoanCallback
        private Integer activeId; // uint24
        private byte[] amounts; // bytes32
        private byte[] totalFees; // bytes32
        private byte[] protocolFees; // bytes32

        public FlashLoan(
                String sender,
                String receiver,
                Integer activeId,
                byte[] amounts,
                byte[] totalFees,
                byte[] protocolFees
        ) {
            this.sender = sender;
            this.receiver = receiver;
            this.activeId = activeId;
            this.amounts = amounts;
            this.totalFees = totalFees;
            this.protocolFees = protocolFees;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, receiver, activeId, amounts, totalFees, protocolFees};
        }
    }

    /**
     * Emitted when oracle length is increased
     */
    public static class OracleLengthIncreased implements AnyBusBaseEvent {
        private String sender;
        private Integer oracleLength; // uint16

        public OracleLengthIncreased(String sender, Integer oracleLength) {
            this.sender = sender;
            this.oracleLength = oracleLength;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, oracleLength};
        }
    }

    /**
     * Emitted when forced decay occurs
     */
    public static class ForcedDecay implements AnyBusBaseEvent {
        private String sender;
        private Integer idReference; // uint24
        private Integer volatilityReference; // uint24

        public ForcedDecay(String sender, Integer idReference, Integer volatilityReference) {
            this.sender = sender;
            this.idReference = idReference;
            this.volatilityReference = volatilityReference;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{sender, idReference, volatilityReference};
        }
    }
}

