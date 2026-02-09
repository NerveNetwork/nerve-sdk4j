package network.nerve.kit.model.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * DLMM 交易对流动性分布查询结果。
 * 通过 {@link #toString()} 可输出与原先控制台一致的表格字符串，便于调试或日志。
 *
 * @author PierreLuo
 */
public class DlmmLiquidityDistributionDto {

    private String tokenX;
    private String tokenY;
    private int binStep;
    private int activeId;
    private List<DlmmBinInfoDto> bins = new ArrayList<>();
    private BigInteger totalReserveX = BigInteger.ZERO;
    private BigInteger totalReserveY = BigInteger.ZERO;
    private BigInteger totalSupply = BigInteger.ZERO;

    private static final BigDecimal ONE_E18 = new BigDecimal("1000000000000000000");

    public String getTokenX() {
        return tokenX;
    }

    public void setTokenX(String tokenX) {
        this.tokenX = tokenX;
    }

    public String getTokenY() {
        return tokenY;
    }

    public void setTokenY(String tokenY) {
        this.tokenY = tokenY;
    }

    public int getBinStep() {
        return binStep;
    }

    public void setBinStep(int binStep) {
        this.binStep = binStep;
    }

    public int getActiveId() {
        return activeId;
    }

    public void setActiveId(int activeId) {
        this.activeId = activeId;
    }

    public List<DlmmBinInfoDto> getBins() {
        return bins;
    }

    public void setBins(List<DlmmBinInfoDto> bins) {
        this.bins = bins != null ? bins : new ArrayList<>();
    }

    public BigInteger getTotalReserveX() {
        return totalReserveX;
    }

    public void setTotalReserveX(BigInteger totalReserveX) {
        this.totalReserveX = totalReserveX != null ? totalReserveX : BigInteger.ZERO;
    }

    public BigInteger getTotalReserveY() {
        return totalReserveY;
    }

    public void setTotalReserveY(BigInteger totalReserveY) {
        this.totalReserveY = totalReserveY != null ? totalReserveY : BigInteger.ZERO;
    }

    public BigInteger getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(BigInteger totalSupply) {
        this.totalSupply = totalSupply != null ? totalSupply : BigInteger.ZERO;
    }

    /**
     * 输出与原先控制台一致的流动性分布表格字符串。
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("流动性分布查询\n");
        sb.append("========================================\n");
        sb.append(String.format("交易对: %s/%s\n", tokenX, tokenY));
        sb.append(String.format("Bin Step: %d\n", binStep));
        sb.append(String.format("Active ID: %d\n", activeId));
        sb.append("----------------------------------------\n\n");

        sb.append(String.format("%-10s %-35s %-35s %-50s %-20s\n",
                "Bin ID", "ReserveX", "ReserveY", "Total Supply", "Price"));
        sb.append("------------------------------------------------------------------------");
        sb.append("------------------------------------------------------------------------------------------------------\n");

        for (DlmmBinInfoDto bin : bins) {
            BigDecimal reserveXDecimal = new BigDecimal(bin.getReserveX()).divide(ONE_E18, 18, RoundingMode.HALF_UP);
            BigDecimal reserveYDecimal = new BigDecimal(bin.getReserveY()).divide(ONE_E18, 18, RoundingMode.HALF_UP);
            BigDecimal supplyDecimal = new BigDecimal(bin.getSupply()).divide(ONE_E18, 18, RoundingMode.HALF_UP);
            BigDecimal priceDecimal = new BigDecimal(bin.getPrice()).divide(ONE_E18, 18, RoundingMode.HALF_UP);
            String marker = bin.isActive() ? " <-- Active" : "";
            sb.append(String.format("%-10d %-35s %-35s %-50s %-20s%s\n",
                    bin.getBinId(),
                    reserveXDecimal.toPlainString(),
                    reserveYDecimal.toPlainString(),
                    supplyDecimal.toPlainString(),
                    priceDecimal.toPlainString(),
                    marker));
        }

        sb.append("------------------------------------------------------------------------");
        sb.append("------------------------------------------------------------------------------------------------------\n");
        BigDecimal totalReserveXDecimal = new BigDecimal(totalReserveX).divide(ONE_E18, 18, RoundingMode.HALF_UP);
        BigDecimal totalReserveYDecimal = new BigDecimal(totalReserveY).divide(ONE_E18, 18, RoundingMode.HALF_UP);
        BigDecimal totalSupplyDecimal = new BigDecimal(totalSupply).divide(ONE_E18, 18, RoundingMode.HALF_UP);
        sb.append(String.format("%-10s %-35s %-35s %-50s\n",
                "总计",
                totalReserveXDecimal.toPlainString(),
                totalReserveYDecimal.toPlainString(),
                totalSupplyDecimal.toPlainString()));
        sb.append(String.format("\n总 Bin 数量: %d\n", bins.size()));
        sb.append("========================================\n\n");
        return sb.toString();
    }

    /**
     * 单个 Bin 的储备与价格信息。
     */
    public static class DlmmBinInfoDto {
        private int binId;
        private BigInteger reserveX = BigInteger.ZERO;
        private BigInteger reserveY = BigInteger.ZERO;
        private BigInteger supply = BigInteger.ZERO;
        private BigInteger price = BigInteger.ZERO;
        private boolean active;

        public int getBinId() {
            return binId;
        }

        public void setBinId(int binId) {
            this.binId = binId;
        }

        public BigInteger getReserveX() {
            return reserveX;
        }

        public void setReserveX(BigInteger reserveX) {
            this.reserveX = reserveX != null ? reserveX : BigInteger.ZERO;
        }

        public BigInteger getReserveY() {
            return reserveY;
        }

        public void setReserveY(BigInteger reserveY) {
            this.reserveY = reserveY != null ? reserveY : BigInteger.ZERO;
        }

        public BigInteger getSupply() {
            return supply;
        }

        public void setSupply(BigInteger supply) {
            this.supply = supply != null ? supply : BigInteger.ZERO;
        }

        public BigInteger getPrice() {
            return price;
        }

        public void setPrice(BigInteger price) {
            this.price = price != null ? price : BigInteger.ZERO;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
