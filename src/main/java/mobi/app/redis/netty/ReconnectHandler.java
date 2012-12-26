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
    private final Timer timer;
    private int attempts;
    private final NettyRedisClient client;

    public ReconnectHandler(NettyRedisClient client, Timer timer) {
        this.client = client;
        this.timer = timer;
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        attempts = 0;
        ctx.sendUpstream(e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        int timeout = 2 << attempts;
        if (attempts < 12) attempts++;
        logger.info("after " + timeout + " seconds will reconnect");
        timer.newTimeout(this, timeout, TimeUnit.SECONDS);
        ctx.sendUpstream(e);
    }



    @Override
    public void run(Timeout timeout) throws Exception {
        client.init();
    }
}
