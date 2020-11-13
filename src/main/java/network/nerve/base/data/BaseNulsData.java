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
package network.nerve.base.data;

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.core.basic.NulsData;
import network.nerve.core.constant.ToolsConstant;
import network.nerve.core.crypto.UnsafeByteArrayOutputStream;
import network.nerve.core.exception.NulsException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Eva
 */
public abstract class BaseNulsData implements NulsData, Serializable, Cloneable {

    @Override
    public final byte[] serialize() throws IOException {
        int size = size();
        try (ByteArrayOutputStream bos = new UnsafeByteArrayOutputStream(size)){
            NulsOutputStreamBuffer buffer = new NulsOutputStreamBuffer(bos);
            if (size == 0) {
                bos.write(ToolsConstant.PLACE_HOLDER);
            } else {
                serializeToStream(buffer);
            }
            byte[] bytes = bos.toByteArray();
            if (bytes.length != size) {
                throw new RuntimeException();
            }
            return bytes;
        }
    }

    protected abstract void serializeToStream(NulsOutputStreamBuffer stream) throws IOException;


    @Override
    public final void parse(byte[] bytes, int cursor) throws NulsException {
        if (bytes == null || bytes.length == 0 || ((bytes.length == 4) && Arrays.equals(ToolsConstant.PLACE_HOLDER, bytes))) {
            return;
        }
        NulsByteBuffer byteBuffer = new NulsByteBuffer(bytes);
        byteBuffer.setCursor(cursor);
        this.parse(byteBuffer);
    }

    public abstract void parse(NulsByteBuffer byteBuffer) throws NulsException;

}
