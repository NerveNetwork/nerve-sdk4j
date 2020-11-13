package network.nerve.kit.service;


import network.nerve.core.basic.Result;
import network.nerve.core.constant.ErrorCode;
import network.nerve.core.exception.NulsException;
import network.nerve.kit.error.AccountErrorCode;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.util.CommonValidator;
import network.nerve.kit.util.RestFulUtil;
import network.nerve.kit.util.ValidateUtil;

import java.util.HashMap;
import java.util.Map;

import static network.nerve.kit.util.ValidateUtil.validateChainId;

public class ConsensusService {

    private ConsensusService() {

    }

    private static ConsensusService instance = new ConsensusService();

    public static ConsensusService getInstance() {
        return instance;
    }

    public Result createAgent(CreateAgentForm form) {
        validateChainId();

        try {
            CommonValidator.validateCreateAgentForm(form);
            Map<String, Object> map = new HashMap<>();
            map.put("agentAddress", form.getAgentAddress());
            map.put("packingAddress", form.getPackingAddress());
            map.put("rewardAddress", form.getRewardAddress());
            map.put("commissionRate", form.getCommissionRate());
            map.put("deposit", form.getDeposit());
            map.put("password", form.getPassword());
            RestFulResult restFulResult = RestFulUtil.post("api/consensus/agent", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result stopAgent(StopAgentForm form) {
        validateChainId();

        try {
            CommonValidator.validateStopAgentForm(form);

            Map<String, Object> map = new HashMap<>();
            map.put("address", form.getAgentAddress());
            map.put("password", form.getPassword());

            RestFulResult restFulResult = RestFulUtil.post("api/consensus/agent/stop", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result depositToAgent(DepositForm form) {
        validateChainId();

        try {
            CommonValidator.validateDepositForm(form);
            Map<String, Object> map = new HashMap<>();
            map.put("address", form.getAddress());
            map.put("agentHash", form.getAgentHash());
            map.put("deposit", form.getDeposit());
            map.put("password", form.getPassword());

            RestFulResult restFulResult = RestFulUtil.post("api/consensus/deposit", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result withdraw(WithdrawForm form) {
        validateChainId();

        try {
            CommonValidator.validateWithDrawForm(form);
            Map<String, Object> map = new HashMap<>();
            map.put("address", form.getAddress());
            map.put("txHash", form.getTxHash());
            map.put("password", form.getPassword());

            RestFulResult restFulResult = RestFulUtil.post("api/consensus/withdraw", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result getDepositList(String agentHash) {
        if (!ValidateUtil.validHash(agentHash)) {
            return Result.getFailed(AccountErrorCode.PARAMETER_ERROR);
        }
        RestFulResult restFulResult = RestFulUtil.getList("api/consensus/list/deposit/" + agentHash, null);
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
