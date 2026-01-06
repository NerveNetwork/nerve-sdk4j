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
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;

/**
 * @author: PierreLuo
 * @date: 2025/9/24
 */
public class Call extends BaseNulsData {

    private byte[] contractAddress;
    private String methodName;
    private String[] paramTypeNames;
    private String[][] args;

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.write(contractAddress);
        stream.writeString(methodName);
        if (paramTypeNames != null) {
            stream.writeUint8((short) paramTypeNames.length);
            for (String paramTypeName : paramTypeNames) {
                stream.writeString(paramTypeName);
            }
        } else {
            stream.writeUint8((short) 0);
        }

        if (args != null) {
            stream.writeUint8((short) args.length);
            for (String[] arg : args) {
                if (arg == null) {
                    stream.writeUint8((short) 0);
                } else {
                    stream.writeUint8((short) arg.length);
                    for (String str : arg) {
                        stream.writeString(str);
                    }
                }
            }
        } else {
            stream.writeUint8((short) 0);
        }

    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.contractAddress = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.methodName = byteBuffer.readString();
        short length = byteBuffer.readUint8();
        if (length > 0) {
            this.paramTypeNames = new String[length];
            for (int i = 0; i < length; i++) {
                this.paramTypeNames[i] = byteBuffer.readString();
            }
        }
        short argsLength = byteBuffer.readUint8();
        for (short i = 0; i < argsLength; i++) {
            short argCount = byteBuffer.readUint8();
            if (argCount == 0) {
                args[i] = new String[0];
            } else {
                String[] arg = new String[argCount];
                for (short k = 0; k < argCount; k++) {
                    arg[k] = byteBuffer.readString();
                }
                args[i] = arg;
            }
        }
    }

    @Override
    public int size() {
        int size = Address.ADDRESS_LENGTH;
        size += SerializeUtils.sizeOfString(this.methodName);
        String[] paramTypeNames1 = this.paramTypeNames;
        size += 1;
        if (paramTypeNames1 != null) {
            for (String minter : paramTypeNames1) {
                size += SerializeUtils.sizeOfString(minter);
            }
        }
        size += 1;
        if (args != null) {
            for (String[] arg : args) {
                size += 1;
                if (arg != null) {
                    for (String str : arg) {
                        size += SerializeUtils.sizeOfString(str);
                    }
                }
            }
        }

        return size;
    }

    public byte[] getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(byte[] contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getParamTypeNames() {
        return paramTypeNames;
    }

    public void setParamTypeNames(String[] paramTypeNames) {
        this.paramTypeNames = paramTypeNames;
    }

    public String[][] getArgs() {
        return args;
    }

    public void setArgs(String[][] args) {
        this.args = args;
    }
}
