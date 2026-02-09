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
package network.nerve.kit.model;

/**
 * 解析后的事件数据：包含合约地址、区块高度、事件名称以及解析后的具体事件对象。
 * eventData 为实现了 AnyBusBaseEvent 的具体类型（如 IToken1155.TransferSingle、LBPairEvents.Swap 等）。
 *
 * @author: PierreLuo
 * @date: 2025/2/9
 */
public class ParsedEventData {

    private String contractAddress;
    private long blockNumber;
    private String eventName;
    private Object eventData;

    public ParsedEventData() {
    }

    public ParsedEventData(String contractAddress, long blockNumber, String eventName, Object eventData) {
        this.contractAddress = contractAddress;
        this.blockNumber = blockNumber;
        this.eventName = eventName;
        this.eventData = eventData;
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getEventData() {
        return eventData;
    }

    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }

    @Override
    public String toString() {
        return "ParsedEventData{" +
                "contractAddress='" + contractAddress + '\'' +
                ", blockNumber=" + blockNumber +
                ", eventName='" + eventName + '\'' +
                ", eventData=" + eventData +
                '}';
    }
}
