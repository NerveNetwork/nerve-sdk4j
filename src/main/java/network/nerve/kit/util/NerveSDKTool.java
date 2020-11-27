package network.nerve.kit.util;


import network.nerve.SDKContext;
import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.data.Transaction;
import network.nerve.core.basic.Result;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.exception.NulsException;
import network.nerve.core.rpc.model.*;
import network.nerve.kit.model.annotation.ApiOperation;
import network.nerve.kit.model.dto.*;
import network.nerve.kit.service.AccountService;
import network.nerve.kit.service.BlockService;
import network.nerve.kit.service.ConsensusService;
import network.nerve.kit.service.TransactionService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class NerveSDKTool {

    private static AccountService accountService = AccountService.getInstance();

    private static TransactionService transactionService = TransactionService.getInstance();

    private static BlockService blockService = BlockService.getInstance();

    private static ConsensusService consensusService = ConsensusService.getInstance();


    @ApiOperation(description = "获取本链相关信息,其中共识资产为本链创建共识节点交易和创建委托共识交易时，需要用到的资产", order = 001)
    @ResponseData(name = "返回值", description = "返回本链信息", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "chainId", description = "本链的ID"),
            @Key(name = "assetId", description = "本链默认主资产的ID"),
            @Key(name = "inflationAmount", description = "本链默认主资产的初始数量"),
            @Key(name = "agentChainId", description = "本链共识资产的链ID"),
            @Key(name = "agentAssetId", description = "本链共识资产的ID")
    }))
    public static Result<Map> getInfo() {
        return blockService.getInfo();
    }

    @ApiOperation(description = "批量创建账户", order = 101, detailDesc = "创建的账户存在于本地钱包内")
    @Parameters(value = {
            @Parameter(parameterName = "count", requestType = @TypeDescriptor(value = int.class), parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = String.class)
    )
    public static Result<List<String>> createAccount(int count, String password) {
        return accountService.createAccount(count, password);
    }

    @ApiOperation(description = "修改账户密码", order = 102)
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "oldPassword", parameterDes = "原密码"),
            @Parameter(parameterName = "newPassword", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = Boolean.class, description = "是否修改成功")
    }))
    public static Result resetPassword(String address, String oldPassword, String newPassword) {
        return accountService.resetPassword(address, oldPassword, newPassword);
    }

    @ApiOperation(description = "导出账户私钥", order = 103, detailDesc = "只能导出本地钱包已存在账户的私钥")
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "私钥")
    }))
    public static Result getPriKey(String address, String password) {
        return accountService.getPriKey(address, password);
    }

    @ApiOperation(description = "根据私钥导入账户", order = 104, detailDesc = "导入私钥时，需要输入密码给明文私钥加密")
    @Parameters({
            @Parameter(parameterName = "priKey", parameterDes = "账户明文私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importPriKey(String priKey, String password) {
        return accountService.importPriKey(priKey, password);
    }

    @ApiOperation(description = "根据keystore导入账户", order = 105)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "pubKey", parameterDes = "公钥"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importKeystore(AccountKeyStoreDto keyStore, String password) {
        return accountService.importKeystore(keyStore, password);
    }

    @ApiOperation(description = "导出keystore到指定文件目录", order = 106)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterDes = "密码"),
            @Parameter(parameterName = "filePath", parameterDes = "文件目录")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "path", description = "导出的文件路径")
    }))
    public static Result exportKeyStore(String address, String password, String filePath) {
        return accountService.exportKeyStore(address, password, filePath);
    }

    @ApiOperation(description = "查询账户余额", order = 107, detailDesc = "根据资产链ID和资产ID，查询本链账户对应资产的余额与nonce值")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址"),
            @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "资产的链ID"),
            @Parameter(parameterName = "assetsId", requestType = @TypeDescriptor(value = int.class), parameterDes = "资产ID")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = AccountBalanceDto.class))
    public static Result getAccountBalance(String address, int chainId, int assetsId) {
        return accountService.getAccountBalance(address, chainId, assetsId);
    }

    @ApiOperation(description = "设置账户别名", order = 108, detailDesc = "别名格式为1-20位小写字母和数字的组合，设置别名会销毁1个NULS")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址"),
            @Parameter(parameterName = "alias", requestType = @TypeDescriptor(value = String.class), parameterDes = "别名"),
            @Parameter(parameterName = "password", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "设置别名交易的hash")
    }))
    public static Result setAlias(String address, String alias, String password) {
        return accountService.setAlias(address, alias, password);
    }

    @ApiOperation(description = "验证地址格式是否正确", order = 109, detailDesc = "验证本链地址格式是否正确")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class))
    public static Result validateAddress(String address) {
        return accountService.validateAddress(SDKContext.main_chain_id, address);
    }

    @ApiOperation(description = "验证地址格式是否正确", order = 110, detailDesc = "根据chainId验证地址格式是否正确")
    @Parameters({
            @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "链ID"),
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class))
    public static Result validateAddress(int chainId, String address) {
        return accountService.validateAddress(chainId, address);
    }


    @ApiOperation(description = "离线 - 批量创建账户", order = 150, detailDesc = "创建的账户不会保存到钱包中,接口直接返回账户的keystore信息")
    @Parameters(value = {
            @Parameter(parameterName = "count", requestType = @TypeDescriptor(value = int.class), parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class)
    )
    public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, password);
    }

    @ApiOperation(description = "离线 - 批量创建地址带固定前缀的账户", order = 151, detailDesc = "创建的账户不会保存到钱包中,接口直接返回账户的keystore信息")
    @Parameters(
            value = {
                    @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(int.class), parameterDes = "地址对应的链Id"),
                    @Parameter(parameterName = "count", requestType = @TypeDescriptor(int.class), parameterDes = "创建数量"),
                    @Parameter(parameterName = "prefix", requestType = @TypeDescriptor(String.class), canNull = true, parameterDes = "地址前缀"),
                    @Parameter(parameterName = "password", parameterDes = "密码")
            }
    )
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class))
    public static Result<List<AccountDto>> createOffLineAccount(int chainId, int count, String prefix, String password) {
        return accountService.createOffLineAccount(chainId, count, prefix, password);
    }

    @ApiOperation(description = "离线修改账户密码", order = 152)
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "oldPassword", parameterDes = "原密码"),
            @Parameter(parameterName = "newPassword", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "重置密码后的加密私钥")
    }))
    public static Result resetPasswordOffline(String address, String encryptedPriKey, String password, String newPassword) {
        return accountService.resetPasswordOffline(address, encryptedPriKey, password, newPassword);
    }

    @ApiOperation(description = "离线获取账户明文私钥", order = 153)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "明文私钥")
    }))
    public static Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        return accountService.getPriKeyOffline(address, encryptedPriKey, password);
    }

    @ApiOperation(description = "多账户摘要签名", order = 154, detailDesc = "用于签名离线组装的多账户转账交易，调用接口时，参数可以传地址和私钥，或者传地址和加密私钥和加密密码")
    @Parameters({
            @Parameter(parameterName = "signDtoList", parameterDes = "摘要签名表单", requestType = @TypeDescriptor(value = SignDto.class)),
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(List<SignDto> signDtoList, String txHex) {
        return accountService.sign(signDtoList, txHex);
    }

    public static Result sign(int chainId, String prefix, List<SignDto> signDtoList, String txHex) {
        return accountService.sign(chainId, prefix, signDtoList, txHex);
    }

    @ApiOperation(description = "多签账户摘要签名", order = 155, detailDesc = "用于签名离线组装的多签账户转账交易，每次调用接口时，只能传入一个账户的私钥进行签名，签名成功后返回的交易字符串再交给第二个账户签名，依次类推")
    @Parameters({
            @Parameter(parameterName = "signDto", parameterDes = "摘要签名表单", requestType = @TypeDescriptor(value = SignDto.class)),
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result multiSign(SignDto signDto, String txHex) {
        return accountService.multiSign(signDto, txHex);
    }

    @ApiOperation(description = "明文私钥摘要签名", order = 156)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串"),
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "privateKey", parameterDes = "账户明文私钥")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(String txHex, String address, String privateKey) {
        return transactionService.signTx(txHex, address, privateKey);
    }

    @ApiOperation(description = "密文私钥摘要签名", order = 157)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串"),
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPrivateKey", parameterDes = "账户密文私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(String txHex, String address, String encryptedPrivateKey, String password) {
        return transactionService.signTx(txHex, address, encryptedPrivateKey, password);
    }

    public static Result sign(String txHex, int chainId, String prefix, String address, String encryptedPrivateKey, String password) {
        return accountService.sign(txHex, chainId, prefix, address, encryptedPrivateKey, password);
    }

    public Result multiSign(int chainId, String prefix, String address, String encryptedPrivateKey, String password, String txHex) {
        return accountService.multiSign(chainId, prefix, address, encryptedPrivateKey, password, txHex);
    }

    @ApiOperation(description = "创建多签账户", order = 158, detailDesc = "根据多个账户的公钥创建多签账户，minSigns为多签账户创建交易时需要的最小签名数")
    @Parameters(value = {
            @Parameter(parameterName = "pubKeys", requestType = @TypeDescriptor(value = List.class, collectionElement = String.class), parameterDes = "账户公钥集合"),
            @Parameter(parameterName = "minSigns", requestType = @TypeDescriptor(value = int.class), parameterDes = "最小签名数")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户的地址")
    }))
    public static Result createMultiSignAccount(List<String> pubKeys, int minSigns) {
        return accountService.createMultiSignAccount(pubKeys, minSigns);
    }

    @ApiOperation(description = "根据私钥获取地址", order = 159, detailDesc = "根据传入的私钥，生成对应的地址，私钥不会存储在钱包里")
    @Parameters(value = {
            @Parameter(parameterName = "priKey", parameterDes = "原始私钥")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户的地址")
    }))
    public static Result getAddressByPriKey(String priKey) {
        return accountService.getAddressByPriKey(priKey);
    }


    @ApiOperation(description = "根据区块高度查询区块头", order = 201)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(long height) {
        return blockService.getBlockHeader(height);
    }

    @ApiOperation(description = "根据区块hash查询区块头", order = 202)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(String hash) {
        return blockService.getBlockHeader(hash);
    }

    @ApiOperation(description = "根据区块高度查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 203)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(long height) {
        return blockService.getBlock(height);
    }

    @ApiOperation(description = "根据区块hash查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 204)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(String hash) {
        return blockService.getBlock(hash);
    }

    @ApiOperation(description = "查询最新区块头信息", order = 205)
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBestBlockHeader() {
        return blockService.getBestBlockHeader();
    }

    @ApiOperation(description = "查询最新区块", order = 206)
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockDto.class))
    public static Result getBestBlock() {
        return blockService.getBestBlock();
    }

    @ApiOperation(description = "根据hash查询交易详情", order = 301)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "交易hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = TransactionDto.class))
    public static Result getTx(String txHash) {
        return transactionService.getTx(txHash);
    }

    public static Result getTransaction(String txHash) {
        return transactionService.getTransaction(txHash);
    }

    @ApiOperation(description = "验证交易", order = 302, detailDesc = "验证离线组装的交易,验证成功返回交易hash值,失败返回错误提示信息")
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result validateTx(String txHex) {
        return transactionService.validateTx(txHex);
    }

    @ApiOperation(description = "广播交易", order = 303, detailDesc = "广播离线组装的交易,成功返回true,失败返回错误提示信息")
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = boolean.class, description = "是否成功"),
            @Key(name = "hash", description = "交易hash")
    }))
    public static Result broadcast(String txHex) {
        return transactionService.broadcastTx(txHex);
    }


    @ApiOperation(description = "单笔转账", order = 304, detailDesc = "发起单账户单资产的转账交易(在线)")
    @Parameters({
            @Parameter(parameterName = "transferForm", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result transfer(TransferForm transferForm) {
        return transactionService.transfer(transferForm);
    }

    @ApiOperation(description = "单笔跨链转账", order = 305, detailDesc = "发起单账户单资产的跨链转账交易(在线)")
    @Parameters({
            @Parameter(parameterName = "transferForm", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = CrossTransferForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result crossTransfer(CrossTransferForm transferForm) {
        return transactionService.crossTransfer(transferForm);
    }

    /**
     * 转账交易
     */

    @ApiOperation(description = "通用离线组装转账交易", order = 310, detailDesc = "根据inputs和outputs离线组装转账交易，用于单账户或多账户的转账交易。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTransferTxOffline(TransferDto transferDto) {
        return transactionService.createTransferTx(transferDto);
    }

    /**
     * 便捷版 组装在NULS链内，转账非NVT资产的单账户对单账户普通转账(不能用于转NVT)。
     * 该方法会主动用fromAddress组装（NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NERVE地址）
     * @param toAddress    转入地址（NERVE地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       到账数量
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内非NVT资产转账交易", order = 311, detailDesc = "组装在NULS链内，转账非NVT资产的单账户对单账户普通转账。" +
            "该方法会主动用fromAddress组装(NVT资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NERVE地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NERVE地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNonNvt(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return transactionService.createTxSimpleTransferOfNonNvt(fromAddress, toAddress, assetChainId, assetId, amount);
    }

    /**
     * 便捷版 组装在NULS链内，转账非NVT资产的单账户对单账户普通转账(不能用于转NVT)。
     * 该方法会主动用fromAddress组装（NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NERVE地址）
     * @param toAddress    转入地址（NERVE地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       到账数量
     * @param time         交易时间
     * @param remark       备注
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内非NVT资产转账交易", order = 312, detailDesc = "组装在NULS链内，转账非NVT资产的单账户对单账户普通转账。" +
            "该方法会主动用fromAddress组装(NVT资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NERVE地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NERVE地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNonNvt(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        return transactionService.createTxSimpleTransferOfNonNvt(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
    }

    /**
     * 便捷版 组装在NULS链内，转账NVT资产的单账户对单账户普通转账(只能用于转NVT)。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NERVE地址）
     * @param toAddress   转入地址（NERVE地址）
     * @param amount      到账数量（不含手续费）
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内NVT资产转账交易", order = 313, detailDesc = "组装在NULS链内，转账NVT资产的单账户对单账户普通转账。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NERVE地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NERVE地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount) {
        return transactionService.createTxSimpleTransferOfNvt(fromAddress, toAddress, amount);
    }

    /**
     * 便捷版 组装在NULS链内，转账NVT资产的单账户对单账户普通转账(只能用于转NVT)。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NERVE地址）
     * @param toAddress   转入地址（NERVE地址）
     * @param amount      到账数量（不含手续费）
     * @param time        交易时间
     * @param remark      备注
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内NVT资产转账交易", order = 314, detailDesc = "组装在NULS链内，转账NVT资产的单账户对单账户普通转账。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NERVE地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NERVE地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        return transactionService.createTxSimpleTransferOfNvt(fromAddress, toAddress, amount, time, remark);
    }


    /**
     * 跨链交易
     */

    @ApiOperation(description = "离线组装跨链转账交易", order = 320, detailDesc = "根据inputs和outputs离线组装转账交易，用于单账户或多账户的转账交易。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTransferTxOffline(TransferDto transferDto) {
        return transactionService.createCrossTransferTx(transferDto);
    }

    /**
     * 跨链交易
     * 便捷版 组装跨链转账非[NULS和NVT]资产的单账户对单账户普通跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网。
     * 该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（当前链地址）
     * @param toAddress    转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @param time         交易时间
     * @param remark       备注
     * @return
     */
    @ApiOperation(description = "离线组装跨链非[NULS和NVT]资产转账交易", order = 321, detailDesc = "组装跨链转账非[NULS和NVT]资产的单账户对单账户普通跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网，" +
            "该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNonNvtNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        return transactionService.createCrossTxSimpleTransferOfNonNvtNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
    }

    /**
     * 跨链交易
     * 便捷版 组装跨链转账非[NULS和NVT]资产的单账户对单账户普通跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网。
     * 该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（当前链地址）
     * @param toAddress    转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @return
     */
    @ApiOperation(description = "离线组装跨链非[NULS和NVT]资产转账交易", order = 322, detailDesc = "组装跨链转账非[NULS和NVT]资产的单账户对单账户普通跨链转账(不能用于转NULS和NVT)，用于将资产转入NULS主网，" +
            "该方法会主动用fromAddress组装（NULS和NVT资产）打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNonNvtNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return transactionService.createCrossTxSimpleTransferOfNonNvtNuls(fromAddress, toAddress, assetChainId, assetId, amount);
    }

    /**
     * 跨链交易
     * 便捷版 组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网（只能用于转NULS）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取（NULS和NVT资产）手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    @ApiOperation(description = "离线组装跨链NULS资产转账交易", order = 323, detailDesc = "组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网（只能用于转NULS）。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        return transactionService.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount, time, remark);
    }


    /**
     * 跨链交易
     * 便捷版 组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网（只能用于转NULS）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取（NULS和NVT资产）手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @return
     */
    @ApiOperation(description = "离线组装跨链NULS资产转账交易", order = 324, detailDesc = "组装跨链转账NULS资产的单账户对单账户跨链转账，用于将NULS资产转入NULS主网（只能用于转NULS）。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return transactionService.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount);
    }


    /**
     * 跨链交易
     * 便捷版 组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网（只能用于转NVT）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取（NULS和NVT资产）手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    @ApiOperation(description = "离线组装跨链NVT资产转账交易", order = 323, detailDesc = "组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网（只能用于转NVT）。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址（NULS地址）"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        return transactionService.createCrossTxSimpleTransferOfNvt(fromAddress, toAddress, amount, time, remark);
    }


    /**
     * 跨链交易
     * 便捷版 组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网（只能用于转NVT）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取（NULS和NVT资产）手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（当前链地址）
     * @param toAddress   转入地址（NULS地址）
     * @param amount
     * @return
     */
    @ApiOperation(description = "离线组装跨链NVT资产转账交易", order = 324, detailDesc = "组装跨链转账NVT资产的单账户对单账户跨链转账，用于将NVT资产转入NULS主网（只能用于转NVT）。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNvt(String fromAddress, String toAddress, BigInteger amount) {
        return transactionService.createCrossTxSimpleTransferOfNvt(fromAddress, toAddress, amount);
    }

    /**
     * 异构链提现交易
     *
     * @param withdrawalTxDto
     * @return
     */
    @ApiOperation(description = "异构链提现交易", order = 330, detailDesc = "组装异构链提现交易")
    @Parameters({
            @Parameter(parameterName = "withdrawalTxDto", parameterDes = "提现交易参数", requestType = @TypeDescriptor(value = WithdrawalTxDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createWithdrawalTx(WithdrawalTxDto withdrawalTxDto) {
        return transactionService.createWithdrawalTx(withdrawalTxDto);
    }

    /**
     * 追加异构提现手续费
     * 1.不能为已完成的提现交易追加手续费
     * 2.提现交易与追加手续费交易必须由相同的地址发起（相同私钥签名）
     *
     * @param fromAddress 转出地址（支付手续费地址）
     * @param txHash      要追加手续费的提现交易hash
     * @param amount      追加手续费数量
     * @param time        时间
     * @param remark      备注
     * @return
     */
    @ApiOperation(description = "追加异构提现手续费", order = 331, detailDesc = "支付NVT来为提现交易追加手续费(加速)，不能为已完成的提现交易追加手续费，" +
            "提现交易与追加手续费交易必须由相同的地址发起（相同私钥签名）")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(当前链地址)"),
            @Parameter(parameterName = "txHash", requestType = @TypeDescriptor(value = String.class), parameterDes = "要追加手续费的提现交易hash"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "追加手续费数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result withdrawalAdditionalFeeTx(String fromAddress, String txHash, BigInteger amount, long time, String remark) {
        return transactionService.withdrawalAdditionalFeeTx(fromAddress, txHash, amount, time, remark);
    }


    @ApiOperation(description = "计算离线创建转账交易所需手续费", order = 340)
    @Parameters({
            @Parameter(parameterName = "TransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = TransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        return transactionService.calcTransferTxFee(dto);
    }

    @ApiOperation(description = "计算离线创建跨链转账交易所需手续费", order = 341, detailDesc = "向NULS网跨链需要同时收取当前网络主资产和NULS来分别支付两个网络的手续费")
    @Parameters({
            @Parameter(parameterName = "CrossTransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = CrossTransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = Map.class))
    @Deprecated
    public static Map<String, BigInteger> calcCrossTransferTxFee(CrossTransferTxFeeDto dto) {
        return transactionService.calcCrossTransferTxFee(dto);
    }


    @ApiOperation(description = "离线组装多签账户转账交易", order = 352, detailDesc = "根据inputs和outputs离线组装转账交易，用于单个多签账户转账。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = MultiSignTransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignTransferTxOffline(MultiSignTransferDto transferDto) {
        return transactionService.createMultiSignTransferTx(transferDto);
    }

    @ApiOperation(description = "计算离线创建多签账户转账交易所需手续费", order = 333)
    @Parameters({
            @Parameter(parameterName = "MultiSignTransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = MultiSignTransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger calcMultiSignTransferTxFee(MultiSignTransferTxFeeDto dto) {
        return transactionService.calcMultiSignTransferTxFee(dto);
    }

    @ApiOperation(description = "离线创建设置别名交易", order = 354)
    @Parameters({
            @Parameter(parameterName = "AliasDto", parameterDes = "创建别名交易表单", requestType = @TypeDescriptor(value = AliasDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createAliasTxOffline(AliasDto dto) {
        return transactionService.createAliasTx(dto);
    }

    @ApiOperation(description = "离线创建多签账户设置别名交易", order = 355)
    @Parameters({
            @Parameter(parameterName = "MultiSignAliasDto", parameterDes = "多签账户创建别名交易表单", requestType = @TypeDescriptor(value = MultiSignAliasDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignAliasTxOffline(MultiSignAliasDto dto) {
        return transactionService.createMultiSignAliasTx(dto);
    }

    /**
     * 根据交易的hex ,反序列化成交易实体类
     *
     * @param txHex
     * @return
     */
    public static Result deserializeTxHex(String txHex) {
        Transaction tx = new Transaction();
        try {
            tx.parse(new NulsByteBuffer(HexUtil.decode(txHex)));
            return Result.getSuccess(tx);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    //todo  查询资产信息


}
