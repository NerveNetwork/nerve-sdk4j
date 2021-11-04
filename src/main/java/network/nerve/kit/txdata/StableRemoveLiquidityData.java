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
public class StableRemoveLiquidityData extends BaseNulsData {

    private byte[] indexs;
    private byte[] to;


    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBytesWithLength(indexs);
        stream.write(to);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.indexs = byteBuffer.readByLengthByte();
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
    }

    @Override
    public int size() {
        int size = SerializeUtils.sizeOfBytes(indexs);
        size += Address.ADDRESS_LENGTH;
        return size;
    }

    public byte[] getIndexs() {
        return indexs;
    }

    public void setIndexs(byte[] indexs) {
        this.indexs = indexs;
    }

    public byte[] getTo() {
        return to;
    }

    public void setTo(byte[] to) {
        this.to = to;
    }

}
