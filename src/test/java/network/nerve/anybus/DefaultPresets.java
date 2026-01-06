package network.nerve.anybus;


import java.util.HashMap;
import java.util.Map;

/**
 * Default Presets Configuration
 * 
 * 默认预设配置，参考 config/bips-config.sol
 * 
 * @author: PierreLuo
 * @date: 2025/12/04
 */
public class DefaultPresets {
    
    /**
     * 获取所有默认预设配置
     * 参考 TraderJoe 的 bips-config.sol
     * 
     * @return Map<binStep, PresetConfig>
     */
    public static Map<Integer, PresetConfig> getAllPresets() {
        Map<Integer, PresetConfig> presets = new HashMap<>();
        
        // binStep = 1 (0.01%)
        presets.put(1, new PresetConfig(
                1,          // binStep
                20_000,     // baseFactor
                10,         // filterPeriod
                120,        // decayPeriod
                5_000,      // reductionFactor
                2_000_000,  // variableFeeControl
                0,          // protocolShare
                100_000,    // maxVolatilityAccumulated
                false       // isOpen (默认不开放，仅owner可用)
        ));
        
        // binStep = 2 (0.02%)
        presets.put(2, new PresetConfig(
                2,          // binStep
                15_000,     // baseFactor
                10,         // filterPeriod
                120,        // decayPeriod
                5_000,      // reductionFactor
                500_000,    // variableFeeControl
                0,          // protocolShare
                250_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        // binStep = 5 (0.05%)
        presets.put(5, new PresetConfig(
                5,          // binStep
                8_000,      // baseFactor
                30,         // filterPeriod
                600,        // decayPeriod
                5_000,      // reductionFactor
                120_000,    // variableFeeControl
                0,          // protocolShare
                300_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        // binStep = 10 (0.1%)
        presets.put(10, new PresetConfig(
                10,         // binStep
                10_000,     // baseFactor
                30,         // filterPeriod
                600,        // decayPeriod
                5_000,      // reductionFactor
                40_000,     // variableFeeControl
                0,          // protocolShare
                350_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        // binStep = 15 (0.15%)
        presets.put(15, new PresetConfig(
                15,         // binStep
                10_000,     // baseFactor
                30,         // filterPeriod
                600,        // decayPeriod
                5_000,      // reductionFactor
                30_000,     // variableFeeControl
                0,          // protocolShare
                350_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        // binStep = 20 (0.2%)
        presets.put(20, new PresetConfig(
                20,         // binStep
                10_000,     // baseFactor
                30,         // filterPeriod
                600,        // decayPeriod
                5_000,      // reductionFactor
                20_000,     // variableFeeControl
                0,          // protocolShare
                350_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        // binStep = 25 (0.25%)
        presets.put(25, new PresetConfig(
                25,         // binStep
                10_000,     // baseFactor
                30,         // filterPeriod
                600,        // decayPeriod
                5_000,      // reductionFactor
                15_000,     // variableFeeControl
                0,          // protocolShare
                350_000,    // maxVolatilityAccumulated
                false       // isOpen
        ));
        
        return presets;
    }
    
    /**
     * 获取单个预设配置
     * 
     * @param binStep binStep值 (1, 2, 5, 10, 15, 20, 25)
     * @return PresetConfig 或 null
     */
    public static PresetConfig getPreset(int binStep) {
        return getAllPresets().get(binStep);
    }
    
    /**
     * 获取推荐的预设配置用于不同的交易对类型
     */
    public static PresetConfig getRecommendedPreset(TokenPairType pairType) {
        switch (pairType) {
            case STABLE_PAIR:
                // 稳定币对，使用最小的binStep (1)
                return getPreset(1);
                
            case CORRELATED_PAIR:
                // 相关资产对（如ETH/wstETH），使用较小的binStep (2或5)
                return getPreset(2);
                
            case STANDARD_PAIR:
                // 标准交易对，使用中等binStep (10或15)
                return getPreset(10);
                
            case VOLATILE_PAIR:
                // 高波动性交易对，使用较大的binStep (20或25)
                return getPreset(20);
                
            default:
                return getPreset(10); // 默认使用10
        }
    }
    
    /**
     * 交易对类型枚举
     */
    public enum TokenPairType {
        STABLE_PAIR,       // 稳定币对 (如 USDT/USDC)
        CORRELATED_PAIR,   // 相关资产对 (如 ETH/wstETH)
        STANDARD_PAIR,     // 标准交易对 (如 ETH/USDT)
        VOLATILE_PAIR      // 高波动性交易对 (如 新币/ETH)
    }
    
    /**
     * 预设配置说明
     */
    public static String getPresetDescription(int binStep) {
        switch (binStep) {
            case 1:
                return "binStep=1 (0.01%) - 适用于稳定币对，最小价格波动";
            case 2:
                return "binStep=2 (0.02%) - 适用于相关资产对";
            case 5:
                return "binStep=5 (0.05%) - 适用于低波动性资产对";
            case 10:
                return "binStep=10 (0.1%) - 标准配置，适用于大多数交易对";
            case 15:
                return "binStep=15 (0.15%) - 适用于中等波动性资产对";
            case 20:
                return "binStep=20 (0.2%) - 适用于高波动性资产对";
            case 25:
                return "binStep=25 (0.25%) - 适用于极高波动性资产对";
            default:
                return "自定义 binStep";
        }
    }
}

