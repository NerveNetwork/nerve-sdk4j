package network.nerve.kit.model;


import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Niels
 */
public class NerveTokenAmount  {

    private int chainId;
    private int assetId;
    private BigInteger amount;

    public NerveTokenAmount() {
    }

    public NerveTokenAmount(int chainId, int assetId, BigInteger amount) {
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

}