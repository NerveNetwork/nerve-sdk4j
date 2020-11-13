package network.nerve;

public class SDKContext {

    public static String default_encoding = "UTF-8";
    /**
     * 本链id
     */
    public static int main_chain_id = 9;
    /**
     * 本链主资产id
     */
    public static int main_asset_id = 1;

    /**
     * NULS链网络 chain Id
     */
    public static int nuls_chain_id = 1;

    /**
     * NULS链网络 主资产 Id
     */
    public static int nuls_asset_id = 1;


    public static String addressPrefixNuls = "NULS";
    /**
     * nerve 网络地址前缀
     */
    public static String addressPrefix = "NERVE";

//    /**
//     * 默认转账交易手续费单价
//     */
//    public static BigInteger DEFAULT_NORMAL_TX_FEE_PRICE = new BigInteger("100000");
//    /**
//     * 默认业务交易手续费单价
//     */
//    public static BigInteger DEFAULT_OTHER_TX_FEE_PRICE = new BigInteger("100000");

    /**
     * 注销共识节点，保证金锁定时间
     */
    public static int stop_agent_lock_time = 259200;
    /**
     * 访问钱包的http接口url地址
     */
    public static String wallet_url = "https://api.nerve.network/";
//    public static String wallet_url = "http://127.0.0.1:17004/";

}
