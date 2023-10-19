package network.nerve.kit.txdata;


import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;
import network.nerve.kit.model.NerveToken;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Niels
 */
public class AddLiquidityData extends BaseNulsData {

    private NerveToken tokenA;
    private NerveToken tokenB;
    private byte[] to;
    private long deadline;
    private BigInteger amountAMin;
    private BigInteger amountBMin;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeNulsData(tokenA);
        stream.writeNulsData(tokenB);
        stream.write(to);
        stream.writeUint32(deadline);
        stream.writeBigInteger(amountAMin);
        stream.writeBigInteger(amountBMin);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.tokenA = byteBuffer.readNulsData(new NerveToken());
        this.tokenB = byteBuffer.readNulsData(new NerveToken());
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.deadline = byteBuffer.readUint32();
        this.amountAMin = byteBuffer.readBigInteger();
        this.amountBMin = byteBuffer.readBigInteger();
    }

    @Override
    public int size() {
        int size = 0;
        size += tokenA.size();
        size += tokenB.size();
        size += Address.ADDRESS_LENGTH;
        size += SerializeUtils.sizeOfUint32();
        size += SerializeUtils.sizeOfBigInteger();
        size += SerializeUtils.sizeOfBigInteger();
        return size;
    }

    public NerveToken getTokenA() {
        return tokenA;
    }

    public void setTokenA(NerveToken tokenA) {
        this.tokenA = tokenA;
    }

    public NerveToken getTokenB() {
        return tokenB;
    }

    public void setTokenB(NerveToken tokenB) {
        this.tokenB = tokenB;
    }

    public byte[] getTo() {
        return to;
    }

    public void setTo(byte[] to) {
        this.to = to;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public BigInteger getAmountAMin() {
        return amountAMin;
    }

    public void setAmountAMin(BigInteger amountAMin) {
        this.amountAMin = amountAMin;
    }

    public BigInteger getAmountBMin() {
        return amountBMin;
    }

    public void setAmountBMin(BigInteger amountBMin) {
        this.amountBMin = amountBMin;
    }
}
