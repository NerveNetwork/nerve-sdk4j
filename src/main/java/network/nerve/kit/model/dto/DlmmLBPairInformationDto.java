package network.nerve.kit.model.dto;

/**
 * LBFactory.getLBPairInformation 返回结构 DTO，便于 JSON-RPC 返回结构化数据。
 *
 * @author PierreLuo
 */
public class DlmmLBPairInformationDto {

    private int binStep;
    private String lbPair;
    private boolean createdByOwner;
    private boolean ignoredForRouting;

    public DlmmLBPairInformationDto() {
    }

    public DlmmLBPairInformationDto(int binStep, String lbPair, boolean createdByOwner, boolean ignoredForRouting) {
        this.binStep = binStep;
        this.lbPair = lbPair;
        this.createdByOwner = createdByOwner;
        this.ignoredForRouting = ignoredForRouting;
    }

    public int getBinStep() {
        return binStep;
    }

    public void setBinStep(int binStep) {
        this.binStep = binStep;
    }

    public String getLbPair() {
        return lbPair;
    }

    public void setLbPair(String lbPair) {
        this.lbPair = lbPair;
    }

    public boolean isCreatedByOwner() {
        return createdByOwner;
    }

    public void setCreatedByOwner(boolean createdByOwner) {
        this.createdByOwner = createdByOwner;
    }

    public boolean isIgnoredForRouting() {
        return ignoredForRouting;
    }

    public void setIgnoredForRouting(boolean ignoredForRouting) {
        this.ignoredForRouting = ignoredForRouting;
    }
}
