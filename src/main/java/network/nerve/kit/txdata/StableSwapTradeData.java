package network.nerve.kit.txdata;

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;

/**
 * @author Niels
 */
public class StableSwapTradeData extends BaseNulsData {

    private byte[] to;
    private byte tokenOutIndex;
    /**
     * 手续费接收地址
     */
    private byte[] feeTo;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.write(to);
        stream.writeByte(tokenOutIndex);
        if (feeTo != null) {
            stream.write(feeTo);
        }
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.tokenOutIndex = byteBuffer.readByte();
        if (!byteBuffer.isFinished()) {
            this.feeTo = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        }
    }

    @Override
    public int size() {
        int size = 0;
        size += Address.ADDRESS_LENGTH;
        size += 1;
        if (feeTo != null) {
            size += Address.ADDRESS_LENGTH;
        }
        return size;
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

    public byte getTokenOutIndex() {
        return tokenOutIndex;
    }

    public void setTokenOutIndex(byte tokenOutIndex) {
        this.tokenOutIndex = tokenOutIndex;
    }
}
