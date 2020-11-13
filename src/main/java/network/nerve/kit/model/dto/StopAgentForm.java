package network.nerve.kit.model.dto;

import network.nerve.core.rpc.model.ApiModel;
import network.nerve.core.rpc.model.ApiModelProperty;

@ApiModel
public class StopAgentForm {

    @ApiModelProperty(description = "节点创建地址")
    private String agentAddress;
    @ApiModelProperty(description = "密码")
    private String password;

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
