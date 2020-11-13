package network.nerve.core.rpc.util;

import network.nerve.core.exception.NulsException;
import network.nerve.core.log.Log;
import network.nerve.core.parse.JSONUtils;
import network.nerve.core.rpc.info.Constants;
import network.nerve.core.rpc.model.message.Response;
import network.nerve.core.rpc.netty.processor.ResponseMessageProcessor;

import java.util.Map;

public class RpcCall {
    public static Object request(String moduleCode, String cmd, Map params) throws NulsException {
        return request(moduleCode, cmd, params, null);
    }

    /**
     * 调用其他模块接口
     * Call other module interfaces
     */
    public static Object request(String moduleCode, String cmd, Map params, Long timeout) throws NulsException {
        try {
            params.put(Constants.VERSION_KEY_STR, "1.0");
            Response cmdResp;
            if (null == timeout) {
                cmdResp = ResponseMessageProcessor.requestAndResponse(moduleCode, cmd, params);
            } else {
                cmdResp = ResponseMessageProcessor.requestAndResponse(moduleCode, cmd, params, timeout);
            }
            Map resData = (Map) cmdResp.getResponseData();
            if (!cmdResp.isSuccess()) {
                Log.error("response error info is {}", cmdResp);
                String errorMsg;
                if (null == resData) {
                    errorMsg = String.format("Remote call fail. ResponseComment: %s ", cmdResp.getResponseComment());
                } else {
                    Map map = (Map) resData.get(cmd);
                    errorMsg = String.format("Remote call fail. msg: %s - code: %s - module: %s - interface: %s \n- params: %s ",
                            map.get("msg"), map.get("code"), moduleCode, cmd, JSONUtils.obj2PrettyJson(params));
                }
                throw new Exception(errorMsg);
            }
            return resData.get(cmd);
        } catch (Exception e) {
            Log.debug("cmd: {}", cmd);
            throw new NulsException(e);
        }
    }
}
