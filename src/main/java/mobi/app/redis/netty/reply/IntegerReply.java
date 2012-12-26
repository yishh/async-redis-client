package mobi.app.redis.netty.reply;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 下午4:30
 */
public class IntegerReply implements Reply<Long>{
    private final long value;
    public IntegerReply(long value){
       this.value = value;
    }


    @Override
    public Long get() {
        return value;
    }

    @Override
    public Long decode(Transcoder transcoder) {
        return value;
    }


}
