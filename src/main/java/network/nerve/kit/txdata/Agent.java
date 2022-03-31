/*
 * *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017-2018 nuls.io
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
package network.nerve.kit.txdata;

import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.Address;
import network.nerve.base.data.BaseNulsData;
import network.nerve.base.data.NulsHash;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;
import network.nerve.core.rpc.model.ApiModel;
import network.nerve.core.rpc.model.ApiModelProperty;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * 节点信息类
 * Node information class
 *
 * @author tag
 * 2018/11/6
 */
@ApiModel(name = "节点信息")
public class Agent extends BaseNulsData {

    /**
     * 节点地址
     * agent address
     **/
    @ApiModelProperty(description = "节点地址")
    private byte[] agentAddress;

    /**
     * 打包地址
     * packing address
     **/
    @ApiModelProperty(description = "出块地址")
    private byte[] packingAddress;

    /**
     * 奖励地址
     * reward address
     */
    @ApiModelProperty(description = "奖励地址")
    private byte[] rewardAddress;

    /**
     * 保证金
     * deposit
     */
    @ApiModelProperty(description = "保证金")
    private BigInteger deposit;

    /**
     * 创建时间
     * create time
     **/
    @ApiModelProperty(description = "创建时间")
    private transient long time;

    /**
     * 所在区块高度
     * block height
     */
    @ApiModelProperty(description = "所在区块高度")
    private transient long blockHeight = -1L;

    /**
     * 该节点注销所在区块高度
     * Block height where the node logs out
     */
    @ApiModelProperty(description = "节点注销高度")
    private transient long delHeight = -1L;

    /**
     * 0:待共识 unConsensus, 1:共识中 consensus
     */
    @ApiModelProperty(description = "状态，0:待共识 unConsensus, 1:共识中 consensus")
    private transient int status;

    /**
     * 信誉值
     * credit value
     */
    @ApiModelProperty(description = "信誉值")
    private transient double creditVal;

    /**
     * 交易HASH
     * transaction hash
     */
    @ApiModelProperty(description = "创建该节点的交易HASH")
    private transient NulsHash txHash;

    /**
     * 别名不序列化
     * Aliases not serialized
     */
    @ApiModelProperty(description = "节点别名")
    private transient String alias;

    /**
     * 出块地址公钥
     * Aliases not serialized
     */
    @ApiModelProperty(description = "节点别名")
    private transient byte[] pubKey;

    private transient String packingAddressStr;
    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfBigInteger();
        size += this.agentAddress.length;
        size += this.rewardAddress.length;
        size += this.packingAddress.length;
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBigInteger(deposit);
        stream.write(agentAddress);
        stream.write(packingAddress);
        stream.write(rewardAddress);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.deposit = byteBuffer.readBigInteger();
        this.agentAddress = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.packingAddress = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
        this.rewardAddress = byteBuffer.readBytes(Address.ADDRESS_LENGTH);
    }


    public byte[] getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(byte[] agentAddress) {
        this.agentAddress = agentAddress;
    }

    public byte[] getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(byte[] packingAddress) {
        this.packingAddress = packingAddress;
    }

    public byte[] getRewardAddress() {
        return rewardAddress;
    }

    public void setRewardAddress(byte[] rewardAddress) {
        this.rewardAddress = rewardAddress;
    }

    public BigInteger getDeposit() {
        return deposit;
    }

    public void setDeposit(BigInteger deposit) {
        this.deposit = deposit;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getDelHeight() {
        return delHeight;
    }

    public void setDelHeight(long delHeight) {
        this.delHeight = delHeight;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCreditVal() {
        return creditVal;
    }

    public void setCreditVal(double creditVal) {
        this.creditVal = creditVal;
    }

    public NulsHash getTxHash() {
        return txHash;
    }

    public void setTxHash(NulsHash txHash) {
        this.txHash = txHash;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public String getPackingAddressStr() {
        return packingAddressStr;
    }

    public void setPackingAddressStr(String packingAddressStr) {
        this.packingAddressStr = packingAddressStr;
    }

    @Override
    public Agent clone() throws CloneNotSupportedException {
        return (Agent) super.clone();
    }

    public Set<byte[]> getAddresses() {
        Set<byte[]> addressSet = new HashSet<>();
        addressSet.add(this.agentAddress);
        return addressSet;
    }

}
