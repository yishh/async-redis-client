package mobi.app.redis.netty.command;

import mobi.app.redis.ZEntity;
import mobi.app.redis.transcoders.Transcoder;

import java.util.Map;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午2:07
 */
public class ZaddCommand<T> extends BaseCommand<T> {
    public ZaddCommand(Transcoder transcoder, String command, String key, double score, Object member, ZEntity<?>... entities) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[(entities.length + 1) * 2 + 1][];
        byteArgs[0] = tu.encodeString(String.valueOf(key));
        byteArgs[1] = tu.encodeString(String.valueOf(score));
        //noinspection unchecked
        byteArgs[2] = getTranscoder().encode(member);
        int i = 3;
        for (ZEntity entity : entities) {
            byteArgs[i] = tu.encodeString(String.valueOf(entity.score));
            //noinspection unchecked
            byteArgs[i + 1] = getTranscoder().encode(entity.member);
            i += 2;
        }
        init(byteArgs);
    }
}