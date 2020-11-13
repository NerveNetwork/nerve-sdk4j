package network.nerve.kit.service;

import network.nerve.core.basic.Result;
import network.nerve.core.constant.ErrorCode;
import network.nerve.kit.model.dto.BlockDto;
import network.nerve.kit.model.dto.BlockHeaderDto;
import network.nerve.kit.model.dto.RestFulResult;
import network.nerve.kit.util.RestFulUtil;

import java.util.Map;

import static network.nerve.kit.util.ValidateUtil.validateChainId;


public class BlockService {

    private BlockService() {

    }

    private static BlockService instance = new BlockService();

    public static BlockService getInstance() {
        return instance;
    }

    public Result getBlockHeader(long height) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/header/height/" + height);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockHeaderDto dto = BlockHeaderDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getBlockHeader(String hash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/header/hash/" + hash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockHeaderDto dto = BlockHeaderDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getBestBlockHeader() {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/header/newest");
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockHeaderDto dto = BlockHeaderDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getBlock(long height) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/height/" + height);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockDto dto = BlockDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getBlock(String hash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/hash/" + hash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockDto dto = BlockDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getBestBlock() {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/block/newest");
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            BlockDto dto = BlockDto.mapToPojo(map);
            result.setData(dto);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getInfo() {
        RestFulResult restFulResult = RestFulUtil.get("api/info");
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }
}
