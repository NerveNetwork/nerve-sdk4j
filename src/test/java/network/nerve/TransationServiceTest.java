package network.nerve;

import com.fasterxml.jackson.core.JsonProcessingException;
import network.nerve.base.data.Transaction;
import network.nerve.core.basic.Result;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.model.NerveToken;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.txdata.StableSwapTradeData;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransationServiceTest {

    static String address = "8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "6df381435098e47b685cdc00fa1d7c66fa2ba9cc441179c6dd1a5686153fb0ee";
    static String encryptedPrivateKey = "0c8e925d27660dbd04104455c001efe7a5d4cba8fc484d06506c8ff4baa653be2d69e31c971243e2185782cabbbe265a";
    static String password = "abcd1234";

    static String packingAddress = "8CPcA7kag6XT1a2yoiTijYaJGY7jceebYWFFq";


    @Before
    public void before() {
        NerveSDKBootStrap.init(5, 2, "TNVT","tNULS", "http://beta.api.nerve.network/");
        //NerveSDKBootStrap.init(5, 2, "TNVT","tNULS", "http://127.0.0.1:17004/");
    }

    @Test
    public void testStableSwapTradeTx() throws Exception {
        String from = "TNVTdTSPVcqUCdfVYWwrbuRtZ1oM6GpSgsgF5";
        String to = "TNVTdTSPNEpLq2wnbsBcD8UDTVMsArtkfxWgz";
        String pairAddress = "TNVTdTSPRyJgExG4HQu5g1sVxhVVFcpCa6fqw";
        String feeTo = "TNVTdTSPUR5vYdstWDHfn5P8MtHB6iZZw3Edv";
        NerveToken[] tokensIn = new NerveToken[]{new NerveToken(5, 8), new NerveToken(5, 9)};
        BigInteger[] amountsIn = new BigInteger[]{new BigInteger("800000000"), new BigInteger("300000000")};
        String[] nonces = new String[]{"02003c812b5f0672", "050001f7ec6473df"};
        //String[] nonces = null;
        int tokenOutIndex = 2;
        String remark = "swap test";
        Map map = (Map) NerveSDKTool.stableSwapTradeTxOffline(from, to, tokensIn, amountsIn, nonces, tokenOutIndex, pairAddress, feeTo, remark).getData();
        System.out.println(JSONUtils.obj2PrettyJson(map));
    }

    @Test
    public void testBroadTx() {
        String txHex = "02003c812b5f0672656d61726b008c0117050001f7ec6473df12e751d64cf20a8baa7edd50810f810500010000e18a79c2480000000000000000000000000000000000000000000000000000089cd92b91c5e536540001170500017fe9a685e43b3124e00fd9c8e4e59158baea63450200010000009573c24800000000000000000000000000000000000000000000000000000000000000000000692103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e346304402205e335bf49a5e1d963df18b5349ebc4642e1db9dc3c6a876fa318dc375f10e18502206511b6bbffb77a40bc966cb725d7835b4122b4d5ccbd5fddf37e5c4d0a161874";
        //广播
        Result result = NerveSDKTool.broadcast(txHex);
        Map map = (Map) result.getData();
        String hash = (String) map.get("value");
        System.out.println(hash);

        txHex = "020059812b5f0672656d61726b008c0117050001f7ec6473df12e751d64cf20a8baa7edd50810f810500010000e18a79c2480000000000000000000000000000000000000000000000000000089cd92b91c5e53654000117050001bc9cf2a09f0d1dbe7ab0a7dca2ccb87d12da6a990200010000009573c24800000000000000000000000000000000000000000000000000000000000000000000692103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e346304402207bb75ebb571f4ad8ade1e2b726de9dd854ca203a272e149bc9bd3ec5f9c18a0402201ab59a5762c5c30bff9db33e6e6d8b66ba8e5abfc58835a0b0eeb76583fac61c";
        result = NerveSDKTool.broadcast(txHex);
        map = (Map) result.getData();
        if (result.isSuccess()) {
            hash = (String) map.get("value");
            System.out.println(hash);
        } else {
            map.get("msg");
        }

    }

    @Test
    public void testCreateTransferTx() {
        String fromAddress = "TNVTdTSPLmP6SKyn2RigSA8Lr9bMTgjUhnve4";
        String toAddress = "tNULSeBaMsEfHKEXvaFPPpQomXipeCYrru6t81";

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NerveSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(new BigInteger("10000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("0000000000000000");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("10000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result<Map> result = NerveSDKTool.createTransferTxOffline(transferDto);
        String txHex = (String) result.getData().get("txHex");

        //签名
        String prikey = "";
        result = NerveSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");

        String txHash = (String) result.getData().get("hash");
        //广播
        result = NerveSDKTool.broadcast(txHex);
    }



    @Test
    public void testCreateMultiSignTx() {
        String multiSignAddress = "tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy";
        String toAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";

        MultiSignTransferDto transferDto = new MultiSignTransferDto();
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        transferDto.setPubKeys(pubKeys);
        transferDto.setMinSigns(2);

        MultiSignTransferTxFeeDto feeDto = new MultiSignTransferTxFeeDto();
        feeDto.setPubKeyCount(2);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NerveSDKTool.calcMultiSignTransferTxFee(feeDto);

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(multiSignAddress);
        from.setAmount(new BigInteger("100000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("0000000000000000");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("100000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result result = NerveSDKTool.createMultiSignTransferTxOffline(transferDto);
        System.out.println(result.getData());
    }
/*

    @Test
    public void testCreateAgentTx() {
        //创建节点保证金
        BigInteger deposit = new BigInteger("2000000000000");
        BigInteger fee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;

        ConsensusDto dto = new ConsensusDto();
        dto.setAgentAddress(address);
        dto.setPackingAddress(packingAddress);
        dto.setCommissionRate(10);
        dto.setDeposit(deposit);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("0000000000000000");

        dto.setInput(fromDto);

        Result result = NerveSDKTool.createConsensusTxOffline(dto);
        System.out.println(result.getData());
        //04002ad2155d006600204aa9d101000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d640001cf22472370724906e94f72faa361dcb24bee125864000115423f8fc2f9f62496cb98d43e3347bd7996327d0a8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100406259a9d101000000000000000000000000000000000000000000000000000008000000000000000000011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d1010000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd
    }

    @Test
    public void testCreateDepositTx() {
        //委托共识金额
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger fee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;

        DepositDto depositDto = new DepositDto();
        depositDto.setAddress(address);
        depositDto.setDeposit(deposit);
        depositDto.setAgentHash("584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd");

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("63b6e201aa9af5f0");

        depositDto.setInput(fromDto);

        Result result = NerveSDKTool.createDepositTxOffline(depositDto);
        System.out.println(result.getData());
        //05003ad9155d005700d0ed902e00000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d640001004012fd902e00000000000000000000000000000000000000000000000000000008827dfaef75714ebd00011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f
    }

    @Test
    public void testWithDrawDepositTx() {
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger price = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;

        WithDrawDto drawDto = new WithDrawDto();
        drawDto.setAddress(address);
        drawDto.setDepositHash("f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f");
        drawDto.setPrice(price);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit);

        drawDto.setInput(fromDto);

        Result result = NerveSDKTool.createWithdrawDepositTxOffline(drawDto);
        System.out.println(result.getData());
        //txHex：06005ae5155d0020f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e00000000000000000000000000000000000000000000000000000008622f795c2196d64fff011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100c08dde902e000000000000000000000000000000000000000000000000000000000000000000000000
        //hash：84fd9e76616f6ff6ac82628bc99d20ebff20da7478df79743724bf007e4c2805
    }

    @Test
    public void testStopConsensusTx() {
        StopConsensusDto dto = new StopConsensusDto();
        dto.setAgentHash("584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd");
        dto.setAgentAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        dto.setDeposit(new BigInteger("2000000000000"));
        dto.setPrice(new BigInteger("100000"));
        List<StopDepositDto> list = new ArrayList<>();

        StopDepositDto depositDto1 = new StopDepositDto();
        depositDto1.setDepositHash("8ada2c25024ee0559b3d78c8e7695184b1c73b42b3a0ba586db83fdd14f6f233");
        CoinFromDto fromDto1 = new CoinFromDto();
        fromDto1.setAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        fromDto1.setAssetChainId(SDKContext.main_chain_id);
        fromDto1.setAssetId(SDKContext.main_asset_id);
        fromDto1.setAmount(new BigInteger("200000000000"));
        depositDto1.setInput(fromDto1);
        list.add(depositDto1);

        StopDepositDto depositDto2 = new StopDepositDto();
        depositDto2.setDepositHash("02d6b74d99c8406e30f9267c8e79f69b318f9a12a162063d63b6e201aa9af5f0");
        CoinFromDto fromDto2 = new CoinFromDto();
        fromDto2.setAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        fromDto2.setAssetChainId(SDKContext.main_chain_id);
        fromDto2.setAssetId(SDKContext.main_asset_id);
        fromDto2.setAmount(new BigInteger("200000000000"));
        depositDto2.setInput(fromDto2);
        list.add(depositDto2);

        dto.setDepositList(list);

        Result result = NerveSDKTool.createStopConsensusTxOffline(dto);
        System.out.println(result.getData());

        //txHex：090081fd165d0020584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebdfd5c01031764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d101000000000000000000000000000000000000000000000000000008827dfaef75714ebdff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000086db83fdd14f6f233ff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e0000000000000000000000000000000000000000000000000000000863b6e201aa9af5f0ff021764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100609948a9d1010000000000000000000000000000000000000000000000000000990b175d000000001764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000a0db215d000000000000000000000000000000000000000000000000000000000000000000000000
        //txHash：d86502a23ab0e18ab78a43fce3fc302dc70d93467bc52922eb96ecf00d630f58
    }

    @Test
    public void testGetTx() {
        String hash = "8e4e864b2345518163c3dd46c08b2f9a66a496ed65840a5bc12be2335ca524e9";
        Result<TransactionDto> result = NerveSDKTool.getTx(hash);
        TransactionDto tx = result.getData();
        System.out.println(tx.getInBlockIndex());
    }

    @Test
    public void getTransaction() {
        String hash = "8e4e864b2345518163c3dd46c08b2f9a66a496ed65840a5bc12be2335ca524e9";
        Result<TransactionDto> result = NerveSDKTool.getTransaction(hash);
        TransactionDto tx = result.getData();
        System.out.println(tx.getBlockHash());
    }

    @Test
    public void testMultiCreateAgentTx() {
        //创建节点保证金
        BigInteger deposit = new BigInteger("2000000000000");
        BigInteger fee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        MultiSignConsensusDto dto = new MultiSignConsensusDto();
        dto.setAgentAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        dto.setPackingAddress("tNULSeBaMowgMLTbRUngAuj2BvGy2RmVLt3okv");
        dto.setCommissionRate(10);
        dto.setDeposit(deposit);
        dto.setPubKeys(pubKeys);
        dto.setMinSigns(2);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("fd5de2b08d42bd69");

        dto.setInput(fromDto);

        Result result = NerveSDKTool.createMultiSignConsensusTx(dto);
        System.out.println(result.getData());
        //04002ad2155d006600204aa9d101000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d640001cf22472370724906e94f72faa361dcb24bee125864000115423f8fc2f9f62496cb98d43e3347bd7996327d0a8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100406259a9d101000000000000000000000000000000000000000000000000000008000000000000000000011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d1010000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd
    }

    @Test
    public void testMultiCreateDepositTx() {
        //委托共识金额
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger fee = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        MultiSignDepositDto depositDto = new MultiSignDepositDto();
        depositDto.setPubKeys(pubKeys);
        depositDto.setMinSigns(2);
        depositDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        depositDto.setDeposit(deposit);
        depositDto.setAgentHash("e67ed0f09cea8bd4e2ad3b4b6d83a39841f9f83dd2a9e5737b73b4d5ad203537");

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("7d81947431ba90ad");

        depositDto.setInput(fromDto);

        Result result = NerveSDKTool.createMultiSignDepositTxOffline(depositDto);
        System.out.println(result.getData());
        //05003ad9155d005700d0ed902e00000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d640001004012fd902e00000000000000000000000000000000000000000000000000000008827dfaef75714ebd00011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f
    }

    @Test
    public void testMultiSignWithDrawDepositTx() {
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger price = TransactionFeeCalculator.NORMAL_PRICE_PRE_1024_BYTES;
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        MultiSignWithDrawDto drawDto = new MultiSignWithDrawDto();
        drawDto.setPubKeys(pubKeys);
        drawDto.setMinSigns(2);
        drawDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        drawDto.setDepositHash("c395d6a03a58b7efc50916f80db17e200999ded125249769e2a45f6068c4bb7a");
        drawDto.setPrice(price);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit);

        drawDto.setInput(fromDto);

        Result result = NerveSDKTool.createMultiSignWithdrawDepositTxOffline(drawDto);
        System.out.println(result.getData());
        //txHex：06005ae5155d0020f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e00000000000000000000000000000000000000000000000000000008622f795c2196d64fff011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100c08dde902e000000000000000000000000000000000000000000000000000000000000000000000000
        //hash：84fd9e76616f6ff6ac82628bc99d20ebff20da7478df79743724bf007e4c2805
    }

    @Test
    public void testMultiSignStopConsensusTx() {
        MultiSignStopConsensusDto dto = new MultiSignStopConsensusDto();
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        dto.setPubKeys(pubKeys);
        dto.setMinSigns(2);
        dto.setAgentHash("e67ed0f09cea8bd4e2ad3b4b6d83a39841f9f83dd2a9e5737b73b4d5ad203537");
        dto.setAgentAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        dto.setDeposit(new BigInteger("2000000000000"));
        dto.setPrice(new BigInteger("100000"));
        List<StopDepositDto> list = new ArrayList<>();

        StopDepositDto depositDto1 = new StopDepositDto();
        depositDto1.setDepositHash("d4a9404a823ea533d1c7fba34470970ac499a974f35172bb8a717b0d6c4d4cbe");
        CoinFromDto fromDto1 = new CoinFromDto();
        fromDto1.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto1.setAssetChainId(SDKContext.main_chain_id);
        fromDto1.setAssetId(SDKContext.main_asset_id);
        fromDto1.setAmount(new BigInteger("200000000000"));
        depositDto1.setInput(fromDto1);
        list.add(depositDto1);

        StopDepositDto depositDto2 = new StopDepositDto();
        depositDto2.setDepositHash("7a735d44d9551a06c3d0bf8107c0ff75ba6921a78d932fc37d81947431ba90ad");
        CoinFromDto fromDto2 = new CoinFromDto();
        fromDto2.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto2.setAssetChainId(SDKContext.main_chain_id);
        fromDto2.setAssetId(SDKContext.main_asset_id);
        fromDto2.setAmount(new BigInteger("200000000000"));
        depositDto2.setInput(fromDto2);
        list.add(depositDto2);

        dto.setDepositList(list);
        Result result = NerveSDKTool.createMultiSignStopConsensusTx(dto);
        System.out.println(result.getData());

        //txHex：090081fd165d0020584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebdfd5c01031764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d101000000000000000000000000000000000000000000000000000008827dfaef75714ebdff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000086db83fdd14f6f233ff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e0000000000000000000000000000000000000000000000000000000863b6e201aa9af5f0ff021764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100609948a9d1010000000000000000000000000000000000000000000000000000990b175d000000001764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000a0db215d000000000000000000000000000000000000000000000000000000000000000000000000
        //txHash：d86502a23ab0e18ab78a43fce3fc302dc70d93467bc52922eb96ecf00d630f58
    }
*/

    @Test
    public void testTx() {
        String txHex = "4800a55caf60097377617020746573742f0500017fe9a685e43b3124e00fd9c8e4e59158baea634502050001e45dc2d5ea7c4216baa277e5a7cee4ef6eb70c34fd16010217050001f7ec6473df12e751d64cf20a8baa7edd50810f81050008000008af2f0000000000000000000000000000000000000000000000000000000008474dccf734b498bc0017050001f7ec6473df12e751d64cf20a8baa7edd50810f810500090000a3e1110000000000000000000000000000000000000000000000000000000008cd94f9d0fa1132d4000217050001bc9cf2a09f0d1dbe7ab0a7dca2ccb87d12da6a99050008000008af2f00000000000000000000000000000000000000000000000000000000000000000000000017050001bc9cf2a09f0d1dbe7ab0a7dca2ccb87d12da6a990500090000a3e11100000000000000000000000000000000000000000000000000000000000000000000000000";

        try {
            Result result = NerveSDKTool.deserializeTxHex(txHex);
            Transaction tx = (Transaction) result.getData();
            tx.getCoinDataInstance();
            NerveSDKTool.validateTx(txHex);
            StableSwapTradeData txData = new StableSwapTradeData();
            txData.parse(tx.getTxData(), 0);
            System.out.println();
//            CoinData coinData = tx.getCoinDataInstance();
//            for (CoinFrom from : coinData.getFrom()) {
//                String fromAddress = AddressTool.getStringAddressByBytes(from.getAddress());
//                System.out.println(fromAddress);
//                System.out.println(from.getAmount().toString());
//            }
//            for (CoinTo to : coinData.getTo()) {
//                String toAddress = AddressTool.getStringAddressByBytes(to.getAddress());
//                System.out.println(toAddress);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
