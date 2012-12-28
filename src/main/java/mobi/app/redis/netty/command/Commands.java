package mobi.app.redis.netty.command;

import mobi.app.redis.ZEntity;
import mobi.app.redis.transcoders.Transcoder;

import java.util.ArrayList;
import java.util.Collections;
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
    }, HSETNX {
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
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{key}, fields));
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
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{key}, fields));
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
    },
    //sets
    SADD {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], (Object[]) args[1]);
        }
    }, SCARD {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, SDIFF {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String[] fields = ((String[]) args[1]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{key}, fields));
        }
    }, SDIFFSTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String destination = (String) args[0];
            String key = (String) args[1];
            String[] fields = ((String[]) args[2]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{destination, key}, fields));
        }
    }, SINTER {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String[] fields = ((String[]) args[1]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{key}, fields));
        }
    }, SINTERSTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String destination = (String) args[0];
            String key = (String) args[1];
            String[] fields = ((String[]) args[2]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{destination, key}, fields));
        }
    }, SISMEMBER {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    }, SMEMBERS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, SMOVE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1]}, args[2]);
        }
    }, SPOP {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, SRANDMEMBER {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args.length > 1)
                //noinspection RedundantArrayCreation
                return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
            else
                return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, SREM {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], (Object[]) args[1]);
        }
    }, SUNION {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String[] fields = ((String[]) args[1]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{key}, fields));
        }
    }, SUNIONSTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String destination = (String) args[0];
            String key = (String) args[1];
            String[] fields = ((String[]) args[2]);
            return new StringArgsCommand<T>(transcoder, name(), combineString(new String[]{destination, key}, fields));
        }
    }
    //sorted sets
    , ZADD {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new ZaddCommand<T>(transcoder, name(), (String) args[0], (Double) args[1], args[2], (ZEntity<?>[]) args[3]);
        }
    }, ZCARD {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, ZCOUNT {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1], (String) args[2]});
        }
    }, ZINCRBY {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new ZincrbyCommand<T>(transcoder, name(), (String) args[0], (Double) args[1], args[2]);
        }
    }, ZINTERSTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            List<String> results = new ArrayList<String>();
            String destination = (String) args[0];
            results.add(destination);
            String keyNumbers = String.valueOf(args[1]);
            results.add(keyNumbers);
            String[] keys = ((String[]) args[2]);
            Collections.addAll(results, keys);
            if (args[3] != null) {
                results.add("WEIGHTS");
                int[] weights = (int[]) args[3];
                for (int weight : weights) {
                    results.add(String.valueOf(weight));
                }
            }
            String aggregate = (String) args[4];
            results.add("AGGREGATE");
            results.add(aggregate);
            String[] strings = new String[results.size()];
            results.toArray(strings);
            return new StringArgsCommand<T>(transcoder, name(), strings);
        }
    }, ZUNIONSTORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            List<String> results = new ArrayList<String>();
            String destination = (String) args[0];
            results.add(destination);
            String keyNumbers = String.valueOf(args[1]);
            results.add(keyNumbers);
            String[] keys = ((String[]) args[2]);
            Collections.addAll(results, keys);
            if (args[3] != null) {
                results.add("WEIGHTS");
                int[] weights = (int[]) args[3];
                for (int weight : weights) {
                    results.add(String.valueOf(weight));
                }
            }
            String aggregate = (String) args[4];
            results.add("AGGREGATE");
            results.add(aggregate);
            String[] strings = new String[results.size()];
            results.toArray(strings);
            return new StringArgsCommand<T>(transcoder, name(), strings);
        }
    }, ZRANGE {
        @SuppressWarnings("RedundantArrayCreation")
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args.length == 4)
                return new ZWithScoreCommand<T>(transcoder, name(), (String) args[0], new String[]{String.valueOf(args[1]), String.valueOf(args[2]), (String) args[3]});
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1]), String.valueOf(args[2])});
        }
    }, ZRANGEBYSCORE {
        @SuppressWarnings("RedundantArrayCreation")
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String min = (String) args[1];
            String max = (String) args[2];
            String offset = null;
            String count = null;
            if (args.length == 6) {
                offset = String.valueOf(args[4]);
                count = String.valueOf(args[5]);
            }
            if (args[3] != null) {
                String[] arguments;
                if (offset != null) {
                    arguments = new String[]{min, max, "WITHSCORES", "LIMIT", offset, count};
                } else {
                    arguments = new String[]{min, max, "WITHSCORES"};
                }
                return new ZWithScoreCommand<T>(transcoder, name(), key, arguments);
            } else {
                String[] arguments;
                if (offset != null) {
                    arguments = new String[]{key, min, max, "LIMIT", offset, count};
                } else {
                    arguments = new String[]{key, min, max};
                }
                return new StringArgsCommand<T>(transcoder, name(), arguments);
            }
        }
    }, ZRANK {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    }, ZREM {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], (Object[]) args[1]);
        }
    }, ZREMRANGEBYRANK {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1]), String.valueOf(args[2])});
        }
    }, ZREMRANGEBYSCORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], (String) args[1], (String) args[2]});
        }
    }, ZREVRANGE {
        @SuppressWarnings("RedundantArrayCreation")
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args.length == 4)
                return new ZWithScoreCommand<T>(transcoder, name(), (String) args[0], new String[]{String.valueOf(args[1]), String.valueOf(args[2]), (String) args[3]});
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1]), String.valueOf(args[2])});
        }
    }, ZREVRANGEBYSCORE {
        @SuppressWarnings("RedundantArrayCreation")
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            String key = (String) args[0];
            String max = (String) args[1];
            String min = (String) args[2];
            String offset = null;
            String count = null;
            if (args.length == 6) {
                offset = String.valueOf(args[4]);
                count = String.valueOf(args[5]);
            }
            if (args[3] != null) {
                String[] arguments;
                if (offset != null) {
                    arguments = new String[]{max, min, "WITHSCORES", "LIMIT", offset, count};
                } else {
                    arguments = new String[]{max, min, "WITHSCORES"};
                }
                return new ZWithScoreCommand<T>(transcoder, name(), key, arguments);
            } else {
                String[] arguments;
                if (offset != null) {
                    arguments = new String[]{key, max, min, "LIMIT", offset, count};
                } else {
                    arguments = new String[]{key, max, min};
                }
                return new StringArgsCommand<T>(transcoder, name(), arguments);
            }
        }
    }, ZREVRANK {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    }, ZSCORE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new ZScoreCommand<T>(transcoder, name(), (String) args[0], args[1]);
        }
    },
    //scripts
    EVAL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args[2] != null)
                return new EvalCommand(transcoder, name(), (byte[]) args[0], (String[]) args[1], (byte[][]) args[2]);
            return new EvalCommand(transcoder, name(), (byte[]) args[0], (String[]) args[1]);
        }
    }, EVALSHA {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args[2] != null)
                return new EvalCommand(transcoder, name(), (byte[]) args[0], (String[]) args[1], (byte[][]) args[2]);
            return new EvalCommand(transcoder, name(), (byte[]) args[0], (String[]) args[1]);
        }
    }, SCRIPT_LOAD {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, "SCRIPT", new String[]{"LOAD", (String) args[0]});
        }
    }, SCRIPT_EXISTS {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            if (args[0] instanceof String[])
                //noinspection RedundantArrayCreation
                return new StringArgsCommand<T>(transcoder, "SCRIPT", combineString(new String[]{"EXISTS"}, (String[]) args[0]));
            else
                //noinspection RedundantArrayCreation
                return new StringArgsCommand<T>(transcoder, "SCRIPT", new String[]{"EXISTS", (String) args[0]});
        }
    }, SCRIPT_FLUSH {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, "SCRIPT", "FLUSH");
        }
    }, SCRIPT_KILL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, "SCRIPT", "KILL");
        }
    },
    //servers
    BGREWRITEAOF {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, BGSAVE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, CLIENT_KILL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, "CLIENT", new String[]{"KILL", String.format("%s:%s", args[0], args[1])});
        }
    }, CLIENT_LIST {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, "CLIENT", "LIST");
        }
    }, CONFIG_GET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, "CONFIG", new String[]{"GET", (String) args[0]});
        }
    }, CONFIG_RESETSTAT {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, "CONFIG", "RESETSTAT");
        }
    }, CONFIG_SET {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, "CONFIG", new String[]{"SET", (String) args[0], (String) args[1]});
        }
    }, DBSIZE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, FLUSHALL {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, FLUSHDB {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, INFO {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, LASTSAVE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, SAVE {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name());
        }
    }, SHUTDOWN {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            return new StringArgsCommand<T>(transcoder, name(), (String) args[0]);
        }
    }, SLAVEOF {
        @Override
        public <T> Command getCommand(Transcoder<T> transcoder, Object... args) {
            //noinspection RedundantArrayCreation
            return new StringArgsCommand<T>(transcoder, name(), new String[]{(String) args[0], String.valueOf(args[1])});
        }
    };

    private static String[] combineString(String[] keys, Object[] strs) {

        String[] results = new String[strs.length + keys.length];
        System.arraycopy(keys, 0, results, 0, keys.length);
        for (int i = 0; i < strs.length; i++) {
            results[i + keys.length] = String.valueOf(strs[i]);
        }
        return results;
    }

    public abstract <T> Command getCommand(Transcoder<T> transcoder, Object... args);
}
