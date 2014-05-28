package mobi.app.redis.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 12-12-25
 * Time: 上午11:31
 */
public class ReconnectHandler extends SimpleChannelHandler implements TimerTask {
    private final static Logger logger = LoggerFactory.getLogger(ReconnectHandler.class);
    private  Timer timer;
    private int attempts;
    private  NettyRedisClient client;
    RedisChannelHandler handler;

    public ReconnectHandler() {
//        this.client = client;
//        this.timer = timer;
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        attempts = 0;
        ctx.sendUpstream(e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        int timeout = 2 << attempts;
        if (attempts < 12) attempts++;
        handler = (RedisChannelHandler) ctx.getChannel().getPipeline().getLast();
        logger.info("after " + timeout + " seconds will reconnect");
        timer.newTimeout(this, timeout, TimeUnit.SECONDS);
        ctx.sendUpstream(e);
    }



    @Override
    public void run(Timeout timeout) throws Exception {
        client.reconnect(handler);
    }

    public void setClient(NettyRedisClient client) {
        this.client = client;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
