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

/**
 * @author: PierreLuo
 * @date: 2025/9/24
 */
public class Create1155 extends BaseNulsData {

    private String name;
    private String symbol;
    private String uri;
    private String[] minters;


    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeString(name);
        stream.writeString(symbol);
        stream.writeString(uri);
        if (minters != null) {
            stream.writeVarInt(minters.length);
            for (String minter : minters) {
                stream.writeString(minter);
            }
        } else {
            stream.writeVarInt(0);
        }

    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.name = byteBuffer.readString();
        this.symbol = byteBuffer.readString();
        this.uri = byteBuffer.readString();
        int length = (int) byteBuffer.readVarInt();
        if (length > 0) {
            this.minters = new String[length];
            for (int i = 0; i < length; i++) {
                this.minters[i] = byteBuffer.readString();
            }
        }
    }

    @Override
    public int size() {
        int size = SerializeUtils.sizeOfString(this.name);
        size += SerializeUtils.sizeOfString(this.symbol);
        size += SerializeUtils.sizeOfString(this.uri);
        String[] minters1 = this.minters;
        if (minters1 != null) {
            size += SerializeUtils.sizeOfVarInt(minters1.length);
            for (String minter : minters1) {
                size += SerializeUtils.sizeOfString(minter);
            }
        } else {
            size += SerializeUtils.sizeOfVarInt(0);
        }
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String[] getMinters() {
        return minters;
    }

    public void setMinters(String[] minters) {
        this.minters = minters;
    }
}
