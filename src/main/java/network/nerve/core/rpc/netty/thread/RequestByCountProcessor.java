package network.nerve.core.rpc.netty.thread;

import network.nerve.core.log.Log;
import network.nerve.core.rpc.model.message.Response;
import network.nerve.core.rpc.netty.channel.ConnectData;
import network.nerve.core.rpc.netty.processor.RequestMessageProcessor;

/**
 * 订阅事件处理线程
 * Subscription event processing threads
 *
 * @author tag
 * 2019/2/25
 */
public class RequestByCountProcessor implements Runnable {
    private ConnectData connectData;

    public RequestByCountProcessor(ConnectData connectData) {
        this.connectData = connectData;
    }

    /**
     * 发送订阅的数据队列
     * Data queue for sending subscriptions
     */
    @Override
    public void run() {
        while (connectData.isConnected()) {
            try {
                Response response = connectData.getRequestEventResponseQueue().take();
                if (response.getRequestID() == null) {
                    continue;
                }
                RequestMessageProcessor.responseWithEventCount(connectData.getChannel(), response);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }
}
