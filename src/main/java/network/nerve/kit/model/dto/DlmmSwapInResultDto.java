package network.nerve.kit.model.dto;

import java.math.BigInteger;

/**
 * LBPair.getSwapIn 返回结构 DTO。
 *
 * @author PierreLuo
 */
public class DlmmSwapInResultDto {

    private BigInteger amountIn;
    private BigInteger amountOutLeft;
    private BigInteger fee;

    public DlmmSwapInResultDto() {
    }

    public DlmmSwapInResultDto(BigInteger amountIn, BigInteger amountOutLeft, BigInteger fee) {
        this.amountIn = amountIn;
        this.amountOutLeft = amountOutLeft;
        this.fee = fee;
    }

    public BigInteger getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(BigInteger amountIn) {
        this.amountIn = amountIn;
    }

    public BigInteger getAmountOutLeft() {
        return amountOutLeft;
    }

    public void setAmountOutLeft(BigInteger amountOutLeft) {
        this.amountOutLeft = amountOutLeft;
    }

    public BigInteger getFee() {
        return fee;
    }

    public void setFee(BigInteger fee) {
        this.fee = fee;
    }
}
