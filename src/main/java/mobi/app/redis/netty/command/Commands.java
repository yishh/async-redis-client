package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;

import java.util.List;
import java.util.Map;

/**
 * User: thor
 * Date: 12-12-21
 * Time: 下午1:17
 */
public enum Commands {
    //Connection
    AUTH {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, PING {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name());
        }
    }, SELECT {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), String.valueOf(args[0]));
        }
    }, ECHO {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new EchoCommand(name(), (String) args[0]);
        }
    }, QUIT {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name());
        }
    },

    //keys
    DEL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String[] keys = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                keys[i] = String.valueOf(args[i]);
            }
            return new StringArgsCommand<String>(transcoder, name(), keys);
        }
    }, DUMP {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, RESTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new RestoreCommand(transcoder, name(), (String) args[0], (Integer) args[1], (byte[]) args[2]);
        }
    }, EXISTS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, EXPIRE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, EXPIREAT {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, PEXPIRE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, PEXPIREAT {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, TTL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, PTTL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, KEYS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<List<String>>(transcoder, name(), (String) args[0]);
        }
    }, MIGRATE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<List<String>>(transcoder, name(), new String[]{
                    (String) args[0], String.valueOf(args[1]), (String) args[2], String.valueOf(args[3]), String.valueOf(args[4])});
        }
    }, MOVE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, PERSIST {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }, RANDOMKEY {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name());
        }
    }, RENAME {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], (String) args[1]});
        }
    }, RENAMENX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), new String[]{(String) args[0], (String) args[1]});
        }
    }, TYPE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0]);
        }
    }
    //strings
    , SET {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0], args[1]);
        }
    }, GETSET {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0], args[1]);
        }
    }, MSET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection unchecked
            return new MsetCommand(transcoder, name(), (Map<String, ?>) args[0]);
        }
    }, SETNX {
        @Override
        public Command getCommand(Transcoder transcoder, Object... args) {
            return new StringArgsCommand<String>(transcoder, name(), (String) args[0], args[1]);
        }
    }, MSETNX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection unchecked
            return new MsetCommand(transcoder, name(), (Map<String, ?>) args[0]);
        }
    }, SETEX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new SetexCommand<String>(transcoder, name(), (String) args[0], (Integer) args[1], args[2]);
        }
    }, GET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, MGET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String[]) args);
        }
    }, INCR {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, INCRBY {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, DECR {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, DECRBY {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }
    //hashes
    , HSET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new HashCommand<String>(transcoder, name(), (String) args[0], (String) args[1], args[2]);
        }
    }, HGET {
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1]});
        }
    }, HDEL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String[] fields = ((String[]) args[1]);
            String[] keys = new String[fields.length + 1];
            keys[0] = key;
            for (int i = 0; i < fields.length; i++) {
                keys[i + 1] = String.valueOf(fields[i]);
            }
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), keys);
        }
    }, HEXISTS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1]});
        }
    }, HGETALL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new HgetallCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, HINCRBY {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1], String.valueOf(args[2])});
        }
    }, HKEYS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, HLEN {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, HMGET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String[] fields = ((String[]) args[1]);
            String[] keys = new String[fields.length + 1];
            keys[0] = key;
            for (int i = 0; i < fields.length; i++) {
                keys[i + 1] = String.valueOf(fields[i]);
            }
            return new StringArgsCommand<T>(transcoder, name(), (String[]) keys);
        }
    }, HMSET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection unchecked
            return new HmsetCommand(transcoder, name(), (String) args[0], (Map<String, ?>) args[1]);
        }
    }, HVALS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    },
    // lists
    LPUSH {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], (Object[]) args[1]);
        }
    }, LPUSHX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    }, LINDEX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    }, LPOP {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, LINSERT {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new LinsertCommand<T>(transcoder, name(), (String) args[0], (String) args[1], args[2], args[3]);
        }
    }, LLEN {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, LRANGE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1]), String.valueOf(args[2])});
        }
    }, LREM {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new LremOrSetCommand<T>(transcoder, name(), (String) args[0], (Integer) args[1], args[2]);
        }
    }, LSET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new LremOrSetCommand<T>(transcoder, name(), (String) args[0], (Integer) args[1], args[2]);
        }
    }, LTRIM {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1]), String.valueOf(args[2])});
        }
    }, RPOP {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, RPUSH {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], (Object[]) args[1]);
        }
    }, RPUSHX {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    }, RPOPLPUSH {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1]});
        }
    };

    public abstract <T> Command getCommand(Transcoder<T> transcoder, Object... args);
}
