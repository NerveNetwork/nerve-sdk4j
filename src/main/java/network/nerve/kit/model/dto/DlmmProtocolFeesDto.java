package network.nerve.kit.model.dto;

import java.math.BigInteger;

/**
 * LBPair.getProtocolFees 返回结构 DTO（feeX, feeY）。
 *
 * @author PierreLuo
 */
public class DlmmProtocolFeesDto {

    private BigInteger feeX;
    private BigInteger feeY;

    public DlmmProtocolFeesDto() {
    }

    public DlmmProtocolFeesDto(BigInteger feeX, BigInteger feeY) {
        this.feeX = feeX;
        this.feeY = feeY;
    }

    public BigInteger getFeeX() {
        return feeX;
    }

    public void setFeeX(BigInteger feeX) {
        this.feeX = feeX;
    }

    public BigInteger getFeeY() {
        return feeY;
    }

    public void setFeeY(BigInteger feeY) {
        this.feeY = feeY;
    }
}
