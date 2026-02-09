package network.nerve.kit.model.event;


import network.nerve.kit.txdata.anybus.AnyBusBaseEvent;

import java.math.BigInteger;

/**
 * Event classes for LBFactory contract
 * @author: PierreLuo
 * @date: 2025/12/24
 */
public class LBFactoryEvents {

    /**
     * Emitted when a new LBPair is created
     */
    public static class LBPairCreated implements AnyBusBaseEvent {
        private String tokenX;
        private String tokenY;
        private Integer binStep; // uint256 indexed, but value is small
        private String lbPair;
        private Integer pid; // uint256, but pair index is small

        public LBPairCreated(String tokenX, String tokenY, Integer binStep, String lbPair, Integer pid) {
            this.tokenX = tokenX;
            this.tokenY = tokenY;
            this.binStep = binStep;
            this.lbPair = lbPair;
            this.pid = pid;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{tokenX, tokenY, binStep, lbPair, pid};
        }
    }

    /**
     * Emitted when the fee recipient is set
     */
    public static class FeeRecipientSet implements AnyBusBaseEvent {
        private String oldRecipient;
        private String newRecipient;

        public FeeRecipientSet(String oldRecipient, String newRecipient) {
            this.oldRecipient = oldRecipient;
            this.newRecipient = newRecipient;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{oldRecipient, newRecipient};
        }
    }

    /**
     * Emitted when the flash loan fee is set
     */
    public static class FlashLoanFeeSet implements AnyBusBaseEvent {
        private BigInteger oldFlashLoanFee;
        private BigInteger newFlashLoanFee;

        public FlashLoanFeeSet(BigInteger oldFlashLoanFee, BigInteger newFlashLoanFee) {
            this.oldFlashLoanFee = oldFlashLoanFee;
            this.newFlashLoanFee = newFlashLoanFee;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{oldFlashLoanFee, newFlashLoanFee};
        }
    }

    /**
     * Emitted when the LBPair implementation is set
     */
    public static class LBPairImplementationSet implements AnyBusBaseEvent {
        private String oldLBPairImplementation;
        private String lbPairImplementation;

        public LBPairImplementationSet(String oldLBPairImplementation, String lbPairImplementation) {
            this.oldLBPairImplementation = oldLBPairImplementation;
            this.lbPairImplementation = lbPairImplementation;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{oldLBPairImplementation, lbPairImplementation};
        }
    }

    /**
     * Emitted when the ignored state of an LBPair is changed
     */
    public static class LBPairIgnoredStateChanged implements AnyBusBaseEvent {
        private String lbPair;
        private Boolean ignored;

        public LBPairIgnoredStateChanged(String lbPair, Boolean ignored) {
            this.lbPair = lbPair;
            this.ignored = ignored;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{lbPair, ignored};
        }
    }

    /**
     * Emitted when a preset is set
     */
    public static class PresetSet implements AnyBusBaseEvent {
        private Integer binStep; // uint256 indexed, but value is small
        private Integer baseFactor; // uint256, but uint16 in Solidity
        private Integer filterPeriod; // uint256, but uint16 in Solidity
        private Integer decayPeriod; // uint256, but uint16 in Solidity
        private Integer reductionFactor; // uint256, but uint16 in Solidity
        private Integer variableFeeControl; // uint256, but uint24 in Solidity
        private Integer protocolShare; // uint256, but uint16 in Solidity
        private Integer maxVolatilityAccumulator; // uint256, but uint24 in Solidity

        public PresetSet(
                Integer binStep,
                Integer baseFactor,
                Integer filterPeriod,
                Integer decayPeriod,
                Integer reductionFactor,
                Integer variableFeeControl,
                Integer protocolShare,
                Integer maxVolatilityAccumulator
        ) {
            this.binStep = binStep;
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
            return new Object[]{binStep, baseFactor, filterPeriod, decayPeriod,
                    reductionFactor, variableFeeControl, protocolShare, maxVolatilityAccumulator};
        }
    }

    /**
     * Emitted when the preset open state is changed
     */
    public static class PresetOpenStateChanged implements AnyBusBaseEvent {
        private Integer binStep; // uint256 indexed, but value is small
        private Boolean isOpen;

        public PresetOpenStateChanged(Integer binStep, Boolean isOpen) {
            this.binStep = binStep;
            this.isOpen = isOpen;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{binStep, isOpen};
        }
    }

    /**
     * Emitted when a preset is removed
     */
    public static class PresetRemoved implements AnyBusBaseEvent {
        private Integer binStep; // uint256 indexed, but value is small

        public PresetRemoved(Integer binStep) {
            this.binStep = binStep;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{binStep};
        }
    }

    /**
     * Emitted when a quote asset is added to the whitelist
     */
    public static class QuoteAssetAdded implements AnyBusBaseEvent {
        private String quoteAsset;

        public QuoteAssetAdded(String quoteAsset) {
            this.quoteAsset = quoteAsset;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{quoteAsset};
        }
    }

    /**
     * Emitted when a quote asset is removed from the whitelist
     */
    public static class QuoteAssetRemoved implements AnyBusBaseEvent {
        private String quoteAsset;

        public QuoteAssetRemoved(String quoteAsset) {
            this.quoteAsset = quoteAsset;
        }

        @Override
        public Object[] indexes() {
            return new Object[]{quoteAsset};
        }
    }
}

