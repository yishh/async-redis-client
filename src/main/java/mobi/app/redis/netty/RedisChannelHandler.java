package mobi.app.redis.netty;

import mobi.app.redis.netty.command.Command;
import mobi.app.redis.netty.command.Commands;
import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: thor
 * Date: 14-5-26
 * Time: 下午4:48
 */
public class RedisChannelHandler extends SimpleChannelHandler {
    Logger logger = LoggerFactory.getLogger(NettyRedisClient.class);
    final static int DEFAULT_TIMEOUT = 5;
    Channel channel;

    public AtomicBoolean isAvailable = new AtomicBoolean(false);

    public RedisChannelHandler() {

    }

    public  Future sendCommand(Commands commands, Transcoder transcoder, Object... args) {
        @SuppressWarnings("unchecked") Command command = commands.getCommand(transcoder, args);
//        logger.debug("redis command is : {}", command.toString());
        channel.write(command);
        return command.getReply();
    }

    final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();


    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!e.getChannel().isWritable()) throw new RedisException("Channel not writable now!");
//        System.out.println(Thread.currentThread().getName());
        synchronized (commandQueue) {
        Command message = (Command) e.getMessage();
        logger.debug("send command [{}]", message.getName());
        Channels.write(ctx, e.getFuture(), CommandEncoder.encode(message));
        commandQueue.put(message);
        }
//        ctx.sendDownstream(e);
    }

    public void authAndSelect(String password, int db) {
        if (password != null) {
            logger.info("try auth command:");
            try {
                String authReply = (String) sendCommand(Commands.AUTH, null, password).get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                logger.info("auth db reply: {}", authReply);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                if(channel!=null)
                    channel.close();
                throw new RedisException(e);
            }
        }
        if (db > 0) {
            logger.debug("try to select db: {} ", db);
            try {
                String selectReply = (String) sendCommand(Commands.SELECT, null, db).get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                logger.info("select db reply: {}", selectReply);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                if(channel!=null)
                    channel.close();
                throw new RedisException(e);
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        logger.debug("messageReceived");
        Reply reply = (Reply) e.getMessage();
        Command command = commandQueue.take();
        command.setResult(reply);
        logger.debug("receive command [{}] 's reply", command.getName());
        ctx.sendUpstream(e);

    }

    @Override
    public synchronized void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = ctx.getChannel();
        commandQueue.clear();
        isAvailable.set(true);
    }

    @Override
    public synchronized void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        //连接断开后清空当前的commandQueue ,
        commandQueue.clear();
        isAvailable.set(false);
//        if (closedHandler != null) closedHandler.onClosed(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("exception : ", e.getCause());
        //TODO 是不是所有的异常都需要关闭连接？
        if (ctx.getChannel().isOpen())
            ctx.getChannel().close();

    }
}
