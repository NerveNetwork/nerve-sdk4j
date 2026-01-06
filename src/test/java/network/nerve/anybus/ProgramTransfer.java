/*
 * MIT License
 *
 * Copyright (c) 2017-2019 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package network.nerve.anybus;

import network.nerve.base.basic.AddressTool;
import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class ProgramTransfer extends BaseNulsData {

    private byte[] from;

    private byte[] to;

    private BigInteger value;

    private int assetChainId;

    private int assetId;

    private long lockedTime;

    public ProgramTransfer() {}

    public ProgramTransfer(byte[] from, byte[] to, BigInteger value, int assetChainId, int assetId, long lockedTime) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.assetChainId = assetChainId;
        this.assetId = assetId;
        this.lockedTime = lockedTime;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        // from (byte[], fixed length 23)
        stream.write(from != null ? from : new byte[Address.ADDRESS_LENGTH]);

        // to (byte[], fixed length 23)
        stream.write(to != null ? to : new byte[Address.ADDRESS_LENGTH]);

        // value (BigInteger)
        stream.writeBigInteger(value != null ? value : BigInteger.ZERO);

        // assetChainId (int, uint16)
        stream.writeUint16(assetChainId);

        // assetId (int, uint16)
        stream.writeUint16(assetId);

        // lockedTime (long, int64)
        stream.writeInt64(lockedTime);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        // from (byte[], fixed length 23)
        this.from = byteBuffer.readBytes(Address.ADDRESS_LENGTH);

        // to (byte[], fixed length 23)
        this.to = byteBuffer.readBytes(Address.ADDRESS_LENGTH);

        // value (BigInteger)
        this.value = byteBuffer.readBigInteger();

        // assetChainId (int, uint16)
        this.assetChainId = byteBuffer.readUint16();

        // assetId (int, uint16)
        this.assetId = byteBuffer.readUint16();

        // lockedTime (long, int64)
        this.lockedTime = byteBuffer.readInt64();
    }

    @Override
    public int size() {
        int size = 0;
        size += Address.ADDRESS_LENGTH; // from
        size += Address.ADDRESS_LENGTH; // to
        size += SerializeUtils.sizeOfBigInteger(); // value
        size += SerializeUtils.sizeOfUint16(); // assetChainId
        size += SerializeUtils.sizeOfUint16(); // assetId
        size += SerializeUtils.sizeOfInt64(); // lockedTime
        return size;
    }

    public void setFrom(byte[] from) {
        this.from = from;
    }

    public void setTo(byte[] to) {
        this.to = to;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public byte[] getFrom() {
        return from;
    }

    public byte[] getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public int getAssetChainId() {
        return assetChainId;
    }

    public void setAssetChainId(int assetChainId) {
        this.assetChainId = assetChainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public long getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(long lockedTime) {
        this.lockedTime = lockedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramTransfer that = (ProgramTransfer) o;

        if (assetChainId != that.assetChainId) return false;
        if (assetId != that.assetId) return false;
        if (lockedTime != that.lockedTime) return false;
        if (!Arrays.equals(from, that.from)) return false;
        if (!Arrays.equals(to, that.to)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(from);
        result = 31 * result + Arrays.hashCode(to);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + assetChainId;
        result = 31 * result + assetId;
        result = 31 * result + (int) (lockedTime ^ (lockedTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"from\":")
                .append(AddressTool.getStringAddressByBytes(from));
        sb.append(",\"to\":")
                .append(AddressTool.getStringAddressByBytes(to));
        sb.append(",\"value\":")
                .append(value);
        sb.append(",\"assetChainId\":")
                .append(assetChainId);
        sb.append(",\"assetId\":")
                .append(assetId);
        sb.append(",\"lockedTime\":")
                .append(lockedTime);
        sb.append('}');
        return sb.toString();
    }
}
