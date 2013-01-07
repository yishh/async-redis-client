package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;
import mobi.app.redis.transcoders.TranscoderUtils;

import java.util.Map;

/**
 * User: thor
 * Date: 12-12-26
 * Time: 下午2:50
 */
public class HmsetCommand<T> extends BaseCommand<T>{
    public HmsetCommand(Transcoder transcoder, String command, String key, Map<String, ?> map) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[map.size() * 2 + 1][];
        byteArgs[0] =  TranscoderUtils.encodeString(key);
        int i = 1;
        for(String field : map.keySet()){
            byteArgs[i] =  TranscoderUtils.encodeString(field);
            //noinspection unchecked
            byteArgs[i + 1] =  getTranscoder().encode(map.get(field));
            i += 2;
        }
        init(byteArgs);
    }
}
