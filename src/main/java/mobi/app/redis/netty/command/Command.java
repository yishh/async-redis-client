package mobi.app.redis.netty.command;

import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;

import java.util.List;
import java.util.concurrent.Future;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 上午10:18
 */
public interface Command<T> {
    List<byte[]> getArgs();
    String getName();
    void setTranscoder(Transcoder transcoder);
    Transcoder getTranscoder();

    void setResult(Reply reply);
//    boolean decodeBySelf();
//    T decode(Reply reply);
    Future<T> getReply();
}
