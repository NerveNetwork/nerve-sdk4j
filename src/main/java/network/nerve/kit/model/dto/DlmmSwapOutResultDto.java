package network.nerve.kit.model.dto;

import java.math.BigInteger;

/**
 * LBPair.getSwapOut 返回结构 DTO。
 *
 * @author PierreLuo
 */
public class DlmmSwapOutResultDto {

    private BigInteger amountInLeft;
    private BigInteger amountOut;
    private BigInteger fee;

    public DlmmSwapOutResultDto() {
    }

    public DlmmSwapOutResultDto(BigInteger amountInLeft, BigInteger amountOut, BigInteger fee) {
        this.amountInLeft = amountInLeft;
        this.amountOut = amountOut;
        this.fee = fee;
    }

    public BigInteger getAmountInLeft() {
        return amountInLeft;
    }

    public void setAmountInLeft(BigInteger amountInLeft) {
        this.amountInLeft = amountInLeft;
    }

    public BigInteger getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(BigInteger amountOut) {
        this.amountOut = amountOut;
    }

    public BigInteger getFee() {
        return fee;
    }

    public void setFee(BigInteger fee) {
        this.fee = fee;
    }
}
