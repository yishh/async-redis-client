package mobi.app.redis.netty;

import mobi.app.redis.netty.reply.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 下午2:42
 */
public class ReplyDecoder extends ReplayingDecoder {
    Logger logger = LoggerFactory.getLogger(ReplyDecoder.class);
    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    public static final byte PLUS_BYTE = '+';
    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';
    private static final byte CR = '\r';
    private static final byte LF = '\n';
    private static final int MAX_LINE_LENGTH = 10 * 1024 * 1024;

    private MultiBulkReply multiBulkReply;


    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, Enum state) throws Exception {
//        logger.debug("decode reply start");
        byte marker = buffer.readByte();
        switch (marker) {
            case PLUS_BYTE:
                return new SingleReply(readStringLine(buffer));
            case MINUS_BYTE:
                return new ErrorReply(readStringLine(buffer));
            case COLON_BYTE:
                return new IntegerReply(readInteger(buffer));
            case DOLLAR_BYTE:
                if(multiBulkReply != null){
                    multiBulkReply.add(readBulk(buffer));
                    return checkAndReturn();
                }
                return new BulkReply(readBulk(buffer));
            case ASTERISK_BYTE:
                multiBulkReply = new MultiBulkReply(readInteger(buffer));
                return checkAndReturn();
            default:
                buffer.clear();
                throw new RedisException("Unknown start byte");

        }
    }

    private Reply checkAndReturn(){
        if(multiBulkReply.isReady()){
            MultiBulkReply reply = multiBulkReply;
            multiBulkReply = null;
            return reply;
        }
        checkpoint();
        return  null;
    }

    private byte[] readBulk(ChannelBuffer buffer) throws TooLongFrameException {
        int length = readInteger(buffer);
        if(length < 0) return null;
        ChannelBuffer input = ChannelBuffers.buffer(length);
        input.writeBytes(buffer, length);
        int cr = buffer.readByte();
        int lf = buffer.readByte();
        if (cr != CR || lf != LF) {
            throw new RedisException("Improper line ending: " + cr + ", " + lf);
        }
        return input.array();
    }

    private int readInteger(ChannelBuffer buffer) throws TooLongFrameException {
        String intString = readStringLine(buffer);
        return Integer.parseInt(intString);

    }

    private String readStringLine(ChannelBuffer buffer) throws TooLongFrameException {
        ChannelBuffer lineBuffer = readLine(buffer, MAX_LINE_LENGTH);
        return new String(lineBuffer.copy().array());
    }

    private ChannelBuffer readLine(ChannelBuffer buffer, int maxLineLength) throws TooLongFrameException {
        ChannelBuffer out = ChannelBuffers.dynamicBuffer();

        int lineLength = 0;
        while (true) {
            byte nextByte = buffer.readByte();
            if (nextByte == CR) {
                nextByte = buffer.readByte();
                if (nextByte == LF) {
                    return out;
                }
            } else {
                if (lineLength >= maxLineLength) {
                    // TODO: Respond with Bad Request and discard the traffic
                    //    or close the connection.
                    //       No need to notify the upstream handlers - just log.
                    //       If decoding a response, just throw an exception.
                    throw new TooLongFrameException(
                            "An redis  line is larger than " + maxLineLength +
                                    " bytes.");
                }
                lineLength++;
                out.writeByte(nextByte);
            }
        }
    }
}
