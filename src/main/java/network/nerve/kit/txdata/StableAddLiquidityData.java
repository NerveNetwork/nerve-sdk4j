package network.nerve.kit.txdata;


import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;

import java.io.IOException;

/**
 * @author Niels
 */
public class StableAddLiquidityData extends BaseNulsData {

    private byte[] to;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.write(to);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
    }

    @Override
    public int size() {
        int size = 0;
        size += Address.ADDRESS_LENGTH;
        return size;
    }

    public byte[] getTo() {
        return to;
    }

    public void setTo(byte[] to) {
        this.to = to;
    }

}
