/*
 * *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017-2019 nuls.io
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */
package network.nerve.base.data;

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.core.constant.BaseConstant;
import network.nerve.core.exception.NulsException;
import network.nerve.core.log.Log;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;

/**
 * @author Eva
 */
public class BlockRoundData extends BaseNulsData {


    protected long roundIndex;

    protected int consensusMemberCount;

    protected long roundStartTime;

    protected int packingIndexOfRound;

    public long getRoundEndTime() {
        return roundStartTime + consensusMemberCount * BaseConstant.BLOCK_TIME_INTERVAL_SECOND;
    }

    public BlockRoundData() {
    }

    public BlockRoundData(byte[] extend) {
        try {
            this.parse(extend,0);
        } catch (NulsException e) {
            Log.error(e);
        }
    }

    public int getConsensusMemberCount() {
        return consensusMemberCount;
    }

    public void setConsensusMemberCount(int consensusMemberCount) {
        this.consensusMemberCount = consensusMemberCount;
    }

    public long getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(long roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public int getPackingIndexOfRound() {
        return packingIndexOfRound;
    }

    public void setPackingIndexOfRound(int packingIndexOfRound) {
        this.packingIndexOfRound = packingIndexOfRound;
    }

    public long getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(long roundIndex) {
        this.roundIndex = roundIndex;
    }


    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfUint32(); // roundIndex
        size += SerializeUtils.sizeOfUint16(); // consensusMemberCount
        size += SerializeUtils.sizeOfUint32();  //roundStartTime
        size += SerializeUtils.sizeOfUint16();  // packingIndexOfRound
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeUint32(roundIndex);
        stream.writeUint16(consensusMemberCount);
        stream.writeUint32(roundStartTime);
        stream.writeUint16(packingIndexOfRound);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.roundIndex = byteBuffer.readUint32();
        this.consensusMemberCount = byteBuffer.readUint16();
        this.roundStartTime = byteBuffer.readUint32();
        this.packingIndexOfRound = byteBuffer.readUint16();
    }
}
