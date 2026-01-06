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
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;

public class AnyBusTxData extends BaseNulsData {
    /**
     * @AnyBusType
     */
    private int type;
    private byte[] data;

    public AnyBusTxData() {
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeUint16(type);
        stream.writeBytesWithLength(data);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.type = byteBuffer.readUint16();
        this.data = byteBuffer.readByLengthByte();
    }

    @Override
    public int size() {
        int size = SerializeUtils.sizeOfUint16();
        size += SerializeUtils.sizeOfBytes(this.data);
        return size;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        builder.append(String.format("\ttype: %s", this.type)).append(lineSeparator);
        if (this.data != null) {
            builder.append(String.format("\tdata: %s", HexUtil.encode(this.data))).append(lineSeparator);
        }
        return builder.toString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
