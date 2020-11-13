package network.nerve.kit.model.dto;

import network.nerve.core.rpc.model.ApiModel;
import network.nerve.core.rpc.model.ApiModelProperty;

@ApiModel
public class AliasDto {
    @ApiModelProperty(description = "账户地址")
    private String address;
    @ApiModelProperty(description = "别名")
    private String alias;
    @ApiModelProperty(description = "资产nonce值")
    private String nonce;
    @ApiModelProperty(description = "交易备注", required = false)
    private String remark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
