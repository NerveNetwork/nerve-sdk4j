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

package network.nerve;

import network.nerve.base.basic.AddressTool;
import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.signture.P2PHKSignature;
import network.nerve.base.signture.TransactionSignature;
import network.nerve.core.basic.Result;
import network.nerve.core.crypto.ECKey;
import network.nerve.core.crypto.HexUtil;
import network.nerve.kit.model.dto.TransactionDto;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: Charlie
 * @date: 2019/12/24
 */
public class TransactionAnalyzeTest {
    String url = "https://api.nuls.io/";

    @Before
    public void before() {
    }

    @Test
    public void getTransaction() throws Exception {
        String hash = "568533e38c472f9e0740f318038b49ebe6b59817ce374c6816bf645744beb6ed";
        Result result = NerveSDKTool.getTransaction(hash);
        TransactionDto txDto = (TransactionDto) result.getData();
        String sign = txDto.getTransactionSignature();
        TransactionSignature txSign = new TransactionSignature();
        txSign.parse(new NulsByteBuffer(HexUtil.decode(sign)));
        for(P2PHKSignature p2PHKSignature :  txSign.getP2PHKSignatures()){
           String address = AddressTool.getStringAddressByBytes( AddressTool.getAddress(p2PHKSignature.getPublicKey(), 1));

           boolean rs = ECKey.verify(HexUtil.decode(hash), p2PHKSignature.getSignData().getSignBytes(), p2PHKSignature.getPublicKey());
            System.out.println(address + " - " + rs );
        }
    }
}
