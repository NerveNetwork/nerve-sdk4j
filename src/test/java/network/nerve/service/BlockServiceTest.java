package network.nerve.service;

import network.nerve.NerveSDKBootStrap;
import network.nerve.core.basic.Result;
import network.nerve.kit.model.dto.BlockDto;
import network.nerve.kit.model.dto.BlockHeaderDto;
import network.nerve.kit.util.NerveSDKTool;
import org.junit.Before;
import org.junit.Test;

public class BlockServiceTest {

    @Before
    public void before() {
        NerveSDKBootStrap.initTest("http://beta.api.nerve.network/");
    }

    @Test
    public void testGetHeaderHeight() {
        long height = 1;
        Result<BlockHeaderDto> result = NerveSDKTool.getBlockHeader(height);
        BlockHeaderDto dto = result.getData();
        System.out.println(dto.getHash());
    }

    @Test
    public void testGetHeaderHash() {
        String hash = "63516e4b16530cc1bf4de51bc39abfdebeaec5fced287f015842043e2fb4dce6";
        Result result = NerveSDKTool.getBlockHeader(hash);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBlock() {
        long height = 1L;
        Result<BlockDto> result = NerveSDKTool.getBlock(height);
        BlockDto dto = result.getData();
        System.out.println(dto.getHeader().getHash());
    }

    @Test
    public void testGetBlockHash() {
        String hash = "7fcbd32ffcbaefd8e1cad77140cead4fd50d9beb01fe388328e615f5b03c4462";
        Result result = NerveSDKTool.getBlock(hash);
        System.out.println(result.getData());
    }

    @Test
    public void testGetBestHeader() {
        Result result = NerveSDKTool.getBestBlockHeader();
        System.out.println(result.getData());
    }

    @Test
    public void testGetBestBlock() {
        Result result = NerveSDKTool.getBestBlock();
        System.out.println(result.getData());
    }

    @Test
    public void testInfo() {
        Result result = NerveSDKTool.getInfo();
        System.out.println(result.getData());
    }
}
