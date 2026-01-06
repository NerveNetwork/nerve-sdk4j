package network.nerve.anybus;


import java.math.BigInteger;

/**
 * Preset Configuration
 * 
 * 预设配置，用于设置交易对的费用参数
 * 对应 Solidity 的 BipsConfig.FactoryPreset
 * 
 * @author: PierreLuo
 * @date: 2025/12/04
 */
public class PresetConfig {
    
    /**
     * binStep - 价格步长（基点，1 = 0.01%）
     */
    private int binStep;
    
    /**
     * baseFactor - 基础费用因子
     */
    private int baseFactor;
    
    /**
     * filterPeriod - 过滤周期（秒）
     */
    private int filterPeriod;
    
    /**
     * decayPeriod - 衰减周期（秒）
     */
    private int decayPeriod;
    
    /**
     * reductionFactor - 减少因子
     */
    private int reductionFactor;
    
    /**
     * variableFeeControl - 可变费用控制
     */
    private int variableFeeControl;
    
    /**
     * protocolShare - 协议费用份额（基点）
     */
    private int protocolShare;
    
    /**
     * maxVolatilityAccumulated - 最大波动率累积
     */
    private int maxVolatilityAccumulated;
    
    /**
     * isOpen - 是否开放给普通用户使用
     */
    private boolean isOpen;
    
    public PresetConfig() {
    }
    
    public PresetConfig(int binStep, int baseFactor, int filterPeriod, int decayPeriod,
                        int reductionFactor, int variableFeeControl, int protocolShare,
                        int maxVolatilityAccumulated, boolean isOpen) {
        this.binStep = binStep;
        this.baseFactor = baseFactor;
        this.filterPeriod = filterPeriod;
        this.decayPeriod = decayPeriod;
        this.reductionFactor = reductionFactor;
        this.variableFeeControl = variableFeeControl;
        this.protocolShare = protocolShare;
        this.maxVolatilityAccumulated = maxVolatilityAccumulated;
        this.isOpen = isOpen;
    }
    
    // Getters and Setters
    
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
    
    public int getMaxVolatilityAccumulated() {
        return maxVolatilityAccumulated;
    }
    
    public void setMaxVolatilityAccumulated(int maxVolatilityAccumulated) {
        this.maxVolatilityAccumulated = maxVolatilityAccumulated;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public void setOpen(boolean open) {
        isOpen = open;
    }
    
    /**
     * 转换为编码的 bytes32 (BigInteger)
     */
    public BigInteger encode() {
        BigInteger result = BigInteger.ZERO;
        
        // 使用 PairParameterHelper 的静态方法编码
        result = PairParameterHelper.setBaseFactor(result, baseFactor);
        result = PairParameterHelper.setFilterPeriod(result, filterPeriod);
        result = PairParameterHelper.setDecayPeriod(result, decayPeriod);
        result = PairParameterHelper.setReductionFactor(result, reductionFactor);
        result = PairParameterHelper.setVariableFeeControl(result, variableFeeControl);
        result = PairParameterHelper.setProtocolShare(result, protocolShare);
        result = PairParameterHelper.setMaxVolatilityAccumulator(result, maxVolatilityAccumulated);
        
        // isOpen 标志位在最高位（bit 255）
        if (isOpen) {
            result = result.setBit(255);
        }
        
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("PresetConfig{binStep=%d, baseFactor=%d, filterPeriod=%d, " +
                        "decayPeriod=%d, reductionFactor=%d, variableFeeControl=%d, " +
                        "protocolShare=%d, maxVolatilityAccumulated=%d, isOpen=%s}",
                binStep, baseFactor, filterPeriod, decayPeriod, reductionFactor,
                variableFeeControl, protocolShare, maxVolatilityAccumulated, isOpen);
    }
}

