package network.nerve.service;

import network.nerve.NerveSDKBootStrap;
import network.nerve.core.basic.Result;
import network.nerve.kit.model.dto.CrossTransferForm;
import network.nerve.kit.model.dto.TransferForm;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class AccountTxTest {

    static String address = "GJbpb6AeSbA68tyPe67CeTaZZeJjWqiw7Wd";

    static String toAddress = "GJbpb656PMFKnYVH9eLgvgtfD3VWbG86iqB";

    static String password = "nuls123456";

    @Before
    public void before() {
        NerveSDKBootStrap.initTest("http://beta.api.nerve.network/");
    }


    @Test
    public void testTransfer() {
        TransferForm form = new TransferForm();
        form.setAddress(address);
        form.setToAddress(toAddress);
        form.setPassword(password);
        form.setAmount(new BigInteger("100000000"));

        Result result = NerveSDKTool.transfer(form);
        System.out.println(result.getData());
    }

    @Test
    public void testCrossTransfer() {
        CrossTransferForm form = new CrossTransferForm();
        form.setAddress("tNULSeBaMoRp6QhNYSF8xjiwBFYnvCoCjkQzvU");
        form.setToAddress("TNVTdTSPFnCMgr9mzvgibQ4hKspVSGEc6XTKE");
        form.setPassword(password);
        form.setAmount(new BigInteger("10000000"));
        form.setAssetChainId(2);
        form.setAssetId(1);
        Result result = NerveSDKTool.crossTransfer(form);
        System.out.println(result.getData());
    }


    @Test
    public void testTx() {
        Result result = NerveSDKTool.getTx("49dc5c6db817d98ad4bc5152bff7cc26827fc659271057938586a1a24b7c733b");
        System.out.println(result.getData());
    }

    @Test
    public void testa() {
        double d = 100;

        for(int i = 0;i <3650;i++) {
            d = xxx(d);
        }
        System.out.println(d);
    }

    private double xxx(double d) {
        return d * 1.01;
    }

}
