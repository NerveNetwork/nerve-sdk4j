package network.nerve.kit.model.dto;

/**
 * LBPair.getVariableFeeParameters 返回结构 DTO。
 *
 * @author PierreLuo
 */
public class DlmmVariableFeeParametersDto {

    private int volatilityAccumulator;
    private int volatilityReference;
    private int idReference;
    private long timeOfLastUpdate;

    public DlmmVariableFeeParametersDto() {
    }

    public DlmmVariableFeeParametersDto(int volatilityAccumulator, int volatilityReference,
                                        int idReference, long timeOfLastUpdate) {
        this.volatilityAccumulator = volatilityAccumulator;
        this.volatilityReference = volatilityReference;
        this.idReference = idReference;
        this.timeOfLastUpdate = timeOfLastUpdate;
    }

    public int getVolatilityAccumulator() {
        return volatilityAccumulator;
    }

    public void setVolatilityAccumulator(int volatilityAccumulator) {
        this.volatilityAccumulator = volatilityAccumulator;
    }

    public int getVolatilityReference() {
        return volatilityReference;
    }

    public void setVolatilityReference(int volatilityReference) {
        this.volatilityReference = volatilityReference;
    }

    public int getIdReference() {
        return idReference;
    }

    public void setIdReference(int idReference) {
        this.idReference = idReference;
    }

    public long getTimeOfLastUpdate() {
        return timeOfLastUpdate;
    }

    public void setTimeOfLastUpdate(long timeOfLastUpdate) {
        this.timeOfLastUpdate = timeOfLastUpdate;
    }
}
