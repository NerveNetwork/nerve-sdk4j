package network.nerve.kit.util;

import network.nerve.core.constant.CommonCodeConstanst;
import network.nerve.core.log.Log;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.model.dto.RpcResult;
import network.nerve.kit.model.dto.RpcResultError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static network.nerve.SDKContext.wallet_url;

/**
 * JSON-RPC 请求工具
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class JsonRpcUtil {

    private static final String ID = "id";
    private static final String JSONRPC = "jsonrpc";
    private static final String METHOD = "method";
    private static final String PARAMS = "params";
    private static final String DEFAULT_ID = "1";
    private static final String JSONRPC_VERSION = "2.0";

    public static RpcResult request(String method, List<Object> params) {
        String url = wallet_url + JSONRPC;
        return request(url, method, params);

    }

    public static RpcResult request(String requestURL, String method, List<Object> params) {
        RpcResult rpcResult;
        try {
            Map<String, Object> map = new HashMap<>(8);
            map.put(ID, DEFAULT_ID);
            map.put(JSONRPC, JSONRPC_VERSION);
            map.put(METHOD, method);
            map.put(PARAMS, params);
            String param = JSONUtils.obj2json(map);
            String resultStr = OkHttpClientUtil.getInstance().postJson(requestURL, param);
            rpcResult = JSONUtils.json2pojo(resultStr, RpcResult.class);
        } catch (Exception e) {
            Log.error(e);
            rpcResult = RpcResult.failed(new RpcResultError(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null));
        }
        return rpcResult;
    }

}
