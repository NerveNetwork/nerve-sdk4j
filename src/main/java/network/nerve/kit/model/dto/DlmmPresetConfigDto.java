package network.nerve.kit.model.dto;

/**
 * LBFactory.getPreset 返回结构 DTO，便于 JSON-RPC 返回结构化数据。
 *
 * @author PierreLuo
 */
public class DlmmPresetConfigDto {

    private int binStep;
    private int baseFactor;
    private int filterPeriod;
    private int decayPeriod;
    private int reductionFactor;
    private int variableFeeControl;
    private int protocolShare;
    private int maxVolatilityAccumulator;
    private boolean open;

    public DlmmPresetConfigDto() {
    }

    public DlmmPresetConfigDto(int binStep, int baseFactor, int filterPeriod, int decayPeriod,
                               int reductionFactor, int variableFeeControl, int protocolShare,
                               int maxVolatilityAccumulator, boolean open) {
        this.binStep = binStep;
        this.baseFactor = baseFactor;
        this.filterPeriod = filterPeriod;
        this.decayPeriod = decayPeriod;
        this.reductionFactor = reductionFactor;
        this.variableFeeControl = variableFeeControl;
        this.protocolShare = protocolShare;
        this.maxVolatilityAccumulator = maxVolatilityAccumulator;
        this.open = open;
    }

    public int getBinStep() {
        return binStep;
    }

    public void setBinStep(int binStep) {
        this.binStep = binStep;
    }

    public int getBaseFactor() {
        return baseFactor;
    }

    public void setBaseFactor(int baseFactor) {
        this.baseFactor = baseFactor;
    }

    public int getFilterPeriod() {
        return filterPeriod;
    }

    public void setFilterPeriod(int filterPeriod) {
        this.filterPeriod = filterPeriod;
    }

    public int getDecayPeriod() {
        return decayPeriod;
    }

    public void setDecayPeriod(int decayPeriod) {
        this.decayPeriod = decayPeriod;
    }

    public int getReductionFactor() {
        return reductionFactor;
    }

    public void setReductionFactor(int reductionFactor) {
        this.reductionFactor = reductionFactor;
    }

    public int getVariableFeeControl() {
        return variableFeeControl;
    }

    public void setVariableFeeControl(int variableFeeControl) {
        this.variableFeeControl = variableFeeControl;
    }

    public int getProtocolShare() {
        return protocolShare;
    }

    public void setProtocolShare(int protocolShare) {
        this.protocolShare = protocolShare;
    }

    public int getMaxVolatilityAccumulator() {
        return maxVolatilityAccumulator;
    }

    public void setMaxVolatilityAccumulator(int maxVolatilityAccumulator) {
        this.maxVolatilityAccumulator = maxVolatilityAccumulator;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
