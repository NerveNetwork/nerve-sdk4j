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
public class SwapTradeData extends BaseNulsData {

    private BigInteger amountOutMin;
    private byte[] to;
    /**
     * 手续费接收地址
     */
    private byte[] feeTo;
    private long deadline;
    private NerveToken[] path;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBigInteger(amountOutMin);
        stream.write(to);
        stream.writeBytesWithLength(feeTo);
        stream.writeUint32(deadline);
        short length = (short) path.length;
        stream.writeUint8(length);
        for (int i = 0; i < length; i++) {
            stream.writeNulsData(path[i]);
        }

    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.amountOutMin = byteBuffer.readBigInteger();
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.feeTo = byteBuffer.readByLengthByte();
        this.deadline = byteBuffer.readUint32();
        short length = byteBuffer.readUint8();
        this.path = new NerveToken[length];
        for (int i = 0; i < length; i++) {
            path[i] = byteBuffer.readNulsData(new NerveToken());
        }
    }

    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfBigInteger();
        size += Address.ADDRESS_LENGTH;
        size += SerializeUtils.sizeOfBytes(feeTo);
        size += SerializeUtils.sizeOfUint32();
        size += SerializeUtils.sizeOfUint8();
        size += SerializeUtils.sizeOfNulsData(new NerveToken()) * path.length;
        return size;
    }

    public BigInteger getAmountOutMin() {
        return amountOutMin;
    }

    public void setAmountOutMin(BigInteger amountOutMin) {
        this.amountOutMin = amountOutMin;
    }

    public byte[] getTo() {
        return to;
    }

    public void setTo(byte[] to) {
        this.to = to;
    }

    public byte[] getFeeTo() {
        return feeTo;
    }

    public void setFeeTo(byte[] feeTo) {
        this.feeTo = feeTo;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public NerveToken[] getPath() {
        return path;
    }

    public void setPath(NerveToken[] path) {
        this.path = path;
    }
}
