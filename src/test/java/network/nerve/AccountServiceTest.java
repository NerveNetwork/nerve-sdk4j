package network.nerve;

import com.fasterxml.jackson.core.JsonProcessingException;
import network.nerve.core.basic.Result;
import network.nerve.core.parse.JSONUtils;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple SDKContext.
 */
public class AccountServiceTest {

    static String address = "tNULSeBaMkm6c3ShAFMzfDX8RKdapZdUcseSw8";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "6df381435098e47b685cdc00fa1d7c66fa2ba9cc441179c6dd1a5686153fb0ee";
    static String encryptedPrivateKey = "0c8e925d27660dbd04104455c001efe7a5d4cba8fc484d06506c8ff4baa653be2d69e31c971243e2185782cabbbe265a";
    static String password = "nuls123456";


    @Before
    public void before() {
        NerveSDKBootStrap.init(5, 2, "TNVT","tNULS", "http://127.0.0.1:17004/");
    }

    @Test
    public void testCreateAccount() {
        int count = 5;
        Result<List<String>> result = NerveSDKTool.createAccount(count, password);
        for (String address : result.getData()) {
            System.out.println(address);
        }
    }

    @Test
    public void testCreateOfflineAccount() {
        while (true) {
            int count = 100;
            Result<List<AccountDto>> result = NerveSDKTool.createOffLineAccount(count, password);

            for (AccountDto accountDto : result.getData()) {
                try {
                    if(accountDto.getAddress().toUpperCase().endsWith("VIVI")) {
                        System.out.println(JSONUtils.obj2json(accountDto));
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testResetPassword() {
        Result result = NerveSDKTool.resetPassword("GJbpb6GNSvkBNoNa3YH2skPLSEVvEYMfS99", password, "abcd4321");
        System.out.println(result);

    }

    @Test
    public void testImportPriKey() {
        Result result = NerveSDKTool.importPriKey("57b65cefbfcf73ec000158f3e6a98cfcac0ff36b70d68171955b87522360ddbf", password);
        System.out.println(result.getData());
        System.out.println(result);
    }

    @Test
    public void testGetPriKey() {
        Result result = NerveSDKTool.getPriKey("tNULSeBaMhUxmEFAiHj1ysd9UXYbFRnZ5yknq1", password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testGetPriKeyOffline() {
        String address = "tNULSeBaMoRp6QhNYSF8xjiwBFYnvCoCjkQzvU";
        String encryptedPrivateKey = "c15450ff689c55f4faf773e51caefa95649ae8983e75e6a473329f620613c700e735909ad61a5f54469e821e286dda8e";
        Result result = NerveSDKTool.getPriKeyOffline(address, encryptedPrivateKey, password);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void importKeystore() {
        AccountKeyStoreDto dto = new AccountKeyStoreDto();
        dto.setAddress("tNULSeBaMhZzUJghK8G8pQDwjVHwmgDSK2698F");
        dto.setPubKey("02f8fdf297dfdb2d4dc92698d2cb8988e15f9a63b39f1db32bf8c74071b7ee2462");
        dto.setEncryptedPrivateKey("87c7946d48e4e056aee42baa1fecab604620d55b9c44dd73b33b1bc16a12a10799cf7fc844116c6f9317c988bbcf32e2");
        Result result = NerveSDKTool.importKeystore(dto, password);
        System.out.println(result.getData());
    }

    @Test
    public void testExportKeystore() {
        String address = "GJbpb61kDNUMipDrRrPeqRRdidDY9SuXeED";
        String filePath = "D:";
        Result result = NerveSDKTool.exportKeyStore(address, password, filePath);
        System.out.println(result.getData());
    }


    @Test
    public void testSign() {
        String txHex = "030080d8435d0026170200013b191fcfe919eda35432f7ba2ad508e5c224e1310d736466617364666173646673648c01170200013b191fcfe919eda35432f7ba2ad508e5c224e131020001004023050600000000000000000000000000000000000000000000000000000000080000000000000000000117020001e2f297763765bc154afaac7aec5e7899a729fed20200010000e1f50500000000000000000000000000000000000000000000000000000000000000000000000000";

        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress("tNULSeBaMidSH7amSTjaNvVkL9VDdFEc9rUztf");
        signDto.setPriKey("aa7a8d46f9e685de7e9b9c859e6386997633041aab172929689ded7c2f49c7d8");
//        signDto.setEncryptedPrivateKey("298d69f9a7ed29d734769945a1788beecc0498d596da622e1d89909af29c07629ccd8a9df1b60196a4659e0e3c6cf9ce");
//        signDto.setPassword(password);
        signDtoList.add(signDto);

        Result result = NerveSDKTool.sign(signDtoList, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testMultiSign() {
        String txHex = "02002ef2435d0672656d61726b008c0117020003f6231825aa05e4d25b4772909a15c9ba3c0b6fe202000100402a86481700000000000000000000000000000000000000000000000000000008e2a45f6068c4bb7a00011702000191866cefc8c9e1181b4e1e068b64fa288405b3e60200010000e87648170000000000000000000000000000000000000000000000000000000000000000000000460202210377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db2103f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1";
        SignDto signDto = new SignDto();
        signDto.setAddress("tNULSeBaMoDwD9pvR4cYMvBdJYPs9LmTnHyq8y");
        signDto.setEncryptedPrivateKey("298d69f9a7ed29d734769945a1788beecc0498d596da622e1d89909af29c07629ccd8a9df1b60196a4659e0e3c6cf9ce");
        signDto.setPassword(password);

        Result result = NerveSDKTool.multiSign(signDto, txHex);
        Map map = (Map) result.getData();
        System.out.println(map);
    }

    @Test
    public void testBalance() {
        Result result = NerveSDKTool.getAccountBalance("tNULSeBaMshNPEnuqiDhMdSA4iNs6LMgjY6tcL", 2, 1);
        System.out.println(result);
    }

    @Test
    public void testAliasTx() {
        String address = "tNULSeBaMoDwD9pvR4cYMvBdJYPs9LmTnHyq8y";
        String alias = "testalias";
        AliasDto aliasDto = new AliasDto();
        aliasDto.setAddress(address);
        aliasDto.setAlias(alias);
        aliasDto.setNonce("0000000000000000");

        Result result = NerveSDKTool.createAliasTxOffline(aliasDto);
        System.out.println(result.getData());
    }

    @Test
    public void testMultiSignAliasTx() {
        String address = "tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy";
        String alias = "aaddbbees";
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        MultiSignAliasDto aliasDto = new MultiSignAliasDto();
        aliasDto.setPubKeys(pubKeys);
        aliasDto.setMinSigns(2);
        aliasDto.setAddress(address);
        aliasDto.setAlias(alias);
        aliasDto.setNonce("e2a45f6068c4bb7a");

        Result result = NerveSDKTool.createMultiSignAliasTxOffline(aliasDto);
        System.out.println(result.getData());
    }

    @Test
    public void testAlias() {
        String address = "tNULSeBaMk4YTkZaUXrLXbUtaHeTWF1Bx6aiBm";
        String alias = "ddaab";
        Result result = NerveSDKTool.setAlias(address, alias, password);
        System.out.println(result.getData());
    }

    @Test
    public void testValidateAddress() {
        String address = "NERVEepb6AcV55NzyXsAP8KKmZNApAE3JS3gvA";
        Result result = NerveSDKTool.validateAddress(9, address);
        System.out.println(result);
    }

}
