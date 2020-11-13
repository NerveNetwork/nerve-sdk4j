package network.nerve.kit.model.dto;

import network.nerve.core.rpc.model.ApiModel;
import network.nerve.core.rpc.model.ApiModelProperty;
import network.nerve.core.rpc.model.TypeDescriptor;

import java.util.List;

@ApiModel
public class MultiSignWithDrawDto extends WithDrawDto {

    @ApiModelProperty(description = "公钥集合", type = @TypeDescriptor(value = List.class, collectionElement = String.class))
    private List<String> pubKeys;
    @ApiModelProperty(description = "最小签名数")
    private int minSigns;

    public MultiSignWithDrawDto() {
    }

    public List<String> getPubKeys() {
        return pubKeys;
    }

    public void setPubKeys(List<String> pubKeys) {
        this.pubKeys = pubKeys;
    }

    public int getMinSigns() {
        return minSigns;
    }

    public void setMinSigns(int minSigns) {
        this.minSigns = minSigns;
    }
}
