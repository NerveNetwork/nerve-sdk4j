package network.nerve.core.basic;


import network.nerve.core.log.Log;

public class DefaultVersionChangeInvoker implements VersionChangeInvoker{
    @Override
    public void process(int chainId) {
        Log.info("DefaultVersionChangeInvoker trigger. chainId-" + chainId);
    }
}
