package network.nerve.kit.txdata;


import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.kit.model.NerveToken;

import java.io.IOException;

/**
 * @author Niels
 */
public class CreatePairData extends BaseNulsData {

    private NerveToken token0;
    private NerveToken token1;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeUint16(token0.getChainId());
        stream.writeUint16(token0.getAssetId());
        stream.writeUint16(token1.getChainId());
        stream.writeUint16(token1.getAssetId());
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.token0 = new NerveToken(byteBuffer.readUint16(), byteBuffer.readUint16());
        this.token1 = new NerveToken(byteBuffer.readUint16(), byteBuffer.readUint16());
    }

    @Override
    public int size() {
        return 8;
    }

    public NerveToken getToken0() {
        return token0;
    }

    public void setToken0(NerveToken token0) {
        this.token0 = token0;
    }

    public NerveToken getToken1() {
        return token1;
    }

    public void setToken1(NerveToken token1) {
        this.token1 = token1;
    }
}
