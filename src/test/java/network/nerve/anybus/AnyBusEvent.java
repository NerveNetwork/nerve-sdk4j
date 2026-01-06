/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package network.nerve.anybus;

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: PierreLuo
 * @date: 2025/12/24
 */
public class AnyBusEvent extends BaseNulsData {
    private String contractAddress;
    private long blockNumber;
    private String event;
    private List<String> payload;

    public AnyBusEvent() {
        this.payload = new ArrayList<>();
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        // contractAddress (String)
        stream.writeString(contractAddress);

        // blockNumber (long, int64)
        stream.writeInt64(blockNumber);

        // event (String)
        stream.writeString(event);

        // payload (List<String>)
        if (payload == null || payload.isEmpty()) {
            stream.writeVarInt(0);
        } else {
            stream.writeVarInt(payload.size());
            for (String item : payload) {
                stream.writeString(item);
            }
        }
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        // contractAddress (String)
        this.contractAddress = byteBuffer.readString();

        // blockNumber (long, int64)
        this.blockNumber = byteBuffer.readInt64();

        // event (String)
        this.event = byteBuffer.readString();

        // payload (List<String>)
        long payloadCount = byteBuffer.readVarInt();
        this.payload = new ArrayList<>((int) payloadCount);
        for (int i = 0; i < payloadCount; i++) {
            this.payload.add(byteBuffer.readString());
        }
    }

    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfString(contractAddress); // contractAddress
        size += SerializeUtils.sizeOfInt64(); // blockNumber
        size += SerializeUtils.sizeOfString(event); // event

        // payload (List<String>)
        size += SerializeUtils.sizeOfVarInt(payload == null ? 0 : payload.size());
        if (payload != null) {
            for (String item : payload) {
                size += SerializeUtils.sizeOfString(item);
            }
        }

        return size;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<String> getPayload() {
        return payload;
    }

    public void setPayload(List<String> payload) {
        this.payload = payload != null ? payload : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "AnyBusEvent{" +
                "contractAddress='" + contractAddress + '\'' +
                ", blockNumber=" + blockNumber +
                ", event='" + event + '\'' +
                ", payload=" + payload +
                '}';
    }
}
