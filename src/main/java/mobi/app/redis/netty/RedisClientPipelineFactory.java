package mobi.app.redis.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * User: thor
 * Date: 14-5-26
 * Time: 下午5:37
 */
public class RedisClientPipelineFactory implements ChannelPipelineFactory {
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return  Channels.pipeline(
                new ReconnectHandler(),
                new ReplyDecoder(),
                new RedisChannelHandler()
        );
    }
}
