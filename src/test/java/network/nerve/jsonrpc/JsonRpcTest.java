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
package network.nerve.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.model.dto.RestFulResult;
import network.nerve.kit.model.dto.RpcResult;
import network.nerve.kit.util.JsonRpcUtil;
import network.nerve.kit.util.ListUtil;
import network.nerve.kit.util.OkHttpClientUtil;
import network.nerve.kit.util.RestFulUtil;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class JsonRpcTest {

    @Test
    public void test() throws JsonProcessingException {
        List<Object> params = new LinkedList<>();
        params.add(2);
        params.add("tNULSeBaN9n5FJ3EYXENEuYwC2ZmnRE1agJffz");
        RpcResult result = JsonRpcUtil.request("getContract", params);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }

    static List<String> stablePairs = new ArrayList<>();
    static {
        stablePairs.add("NERVEepb7WDfEU4ZKsEFmwCfGwCaYWgdHgk5tW");
        stablePairs.add("NERVEepb7athLAHZF35D9r2fv1v2cCwctgjdK1");
        stablePairs.add("NERVEepb7SvfFaBRSGxmSGHdn8ogzvQ64g9vr5");
        stablePairs.add("NERVEepb7SruM6Ao4cSwcDom35ZK5UbehJ2gCj");
        stablePairs.add("NERVEepb7PKTD5FSTWxMC2JX3ggiRiMXYVchaN");
        stablePairs.add("NERVEepb7MoKvCKh2sn3fGCazinLiMr1XCEQNd");
        stablePairs.add("NERVEepb7Q29bz3VMLV59Ff4vdT15q1kRk1G9L");
        stablePairs.add("NERVEepb7Qf8yyH5N5wgugXqSA8ukSdqrJbJBY");
        stablePairs.add("NERVEepb7Nudia4cz6rdg7jiqd2VZa6yTeszqu");
        stablePairs.add("NERVEepb7NcH9smiRzAbGP3JxrTPXozKwoxPJy");
        stablePairs.add("NERVEepb7WKC26ivYqbEpbu3BMfQJbqwT1vvxA");
        stablePairs.add("NERVEepb7cQatcZ36EHiv4qKTK3GAWmGympYUL");
        stablePairs.add("NERVEepb7UFEPTHEDv53zd7QmaGXoehWYqkyd4");
        stablePairs.add("NERVEepb7bmwb11fRep69GRdGPktLfAjwSYabq");
        stablePairs.add("NERVEepb7XXViheVZG7tgAxhjr47KpSGi9cNbY");
        stablePairs.add("NERVEepb7Qo1in1eSzi74ysy69HNZraXuwxBcu");
        stablePairs.add("NERVEepb7bcjmByWbDKg2xtozQq9ukUEzoeaUD");
        stablePairs.add("NERVEepb7SJbQqGiv7vgwvvAmYMeqvJpMYtFA4");
        stablePairs.add("NERVEepb7SEjq39pA91ergTgPhGGcdzXzSrrbu");
        stablePairs.add("NERVEepb7SCM9FBza8mBA5u9oeJRVLXWa2ASbF");
        stablePairs.add("NERVEepb7bwprYuQT89yJN6uV7ZeQ3r2NNaNS7");
        stablePairs.add("NERVEepb7T4HWkURn3Kvy9YAA8U9VuqzrVW8gX");
        stablePairs.add("NERVEepb7SrR288tiiqciv8vzbQ2AE91CwRBGi");
    }

    @Test
    public void test1() throws Exception {
        for (String p : stablePairs) {
            RpcResult rpcResult = JsonRpcUtil.request("getStableSwapPairInfo", ListUtil.of(9, p));
            Map map = (Map) rpcResult.getResult();
            BigInteger lp = new BigInteger((String) map.get("totalLP"));
            if (lp.compareTo(BigInteger.ZERO) == 0) {
                System.out.println("empty pair: " + p);
                continue;
            }
            Map map1 = (Map) map.get("po");
            String lpKey = map1.get("tokenLP").toString().trim();
            String resultStr = OkHttpClientUtil.getInstance().getData("https://assets.nabox.io/api/price/" + lpKey, null);
            System.out.println(String.format("pair: %s, lpkey: %s, price: %s", p, lpKey, resultStr));
        }
    }

    @Test
    public void test2() throws Exception {
        int i = 0;
        for (String p : stablePairs) {
            if (i > 0) break;
            i++;
            RpcResult rpcResult = JsonRpcUtil.request("getStableSwapPairInfo", ListUtil.of(9, p));
            Map map = (Map) rpcResult.getResult();
            BigInteger lp = new BigInteger((String) map.get("totalLP"));
            if (lp.compareTo(BigInteger.ZERO) == 0) {
                System.out.println("empty pair: " + p);
                continue;
            }
            System.out.println(map);
        }
    }
}
