package mobi.app.redis.netty.command;

import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午2:34
 */
public class ZincrbyCommand<T> extends BaseCommand<T> {
    public ZincrbyCommand(Transcoder transcoder, String command, String key, double increment, Object member) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[3][];
        byteArgs[0] = tu.encodeString(key);
        byteArgs[1] = tu.encodeString(String.valueOf(increment));
        //noinspection unchecked
        byteArgs[2] = getTranscoder().encode(member);
        init(byteArgs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResult(Reply reply) {
        Double result = (Double) reply.decode(Transcoder.DOUBLE_TRANSCODER);
        future.setResultAndAWake((T) result);
    }

}
