async-redis-client
==================

基于netty实现的非阻塞redis客户端

 		
 		AsyncRedisClient client = new NettyRedisClient("172.16.3.213:6379", 1, null);
 		String result = client.set("TEST_KEY2", "CACHED").get(1, TimeUnit.SECONDS);
        String cached = (String) client.get("TEST_KEY2").get(1, TimeUnit.SECONDS);

        RedisClient client = new SyncRedisClient("172.16.3.213:6379", 1, null, 1, TimeUnit.SECONDS);
        String result = client.set("TEST_KEY2", "CACHED");
        String cached = (String) client.get("TEST_KEY2");
        

## mvn

mvn 仓库 :https://oss.sonatype.org/content/repositories/snapshots/

		<dependency>
  			<groupId>mobi.51app</groupId>
  			<artifactId>async-redis-client</artifactId>
  			<version>0.1.1-SNAPSHOT</version>
		</dependency>

## 优势

* 基于netty的非阻塞的实现。相比较jedis, 在多线程并发访问的情况下有更好的性能。
* 更简单的java api。jedis的api完全基于redis的command来抽象，只提供了基于string和byte[]的操作接口，在实际使用中并不方便。async-redis-client针对常用的操作做了更好的抽象，集成了序列化和反序列化的实现，使用上更符合java编程的习惯

## 限制

* 目前没有实现所有的redis命令。将实现并覆盖大部分常用的redis命令，包括strings，hashes， lists，sets， sort sets...。但是其中部分命令可能不会被实现。
* 由于默认采用了java 序列化方案，因此async-redis-client操作的数据可能无法和其他语言实现的redis客户端协同工作
* scritps的使用是受限的。由于redis的协议限制，很难判断eval script后返回的响应边界，所以目前使用eval命令时需要确保执行的script只返回一个reply，不能是多个reply。

## 其他

* 不打算实现pipeline功能。因为pipeline主要是为了解决在bio模式下io利用率较低的问题，使用async-redis-client不需要再使用pipeline的功能了。 如下的使用方式即可:


		Future<String> result = client.set("TEST_KEY2", "CACHED");
       	Future<String> cached = (Future<String>) client.get("TEST_KEY2");
       	String str = result.get();
       	String cachedValue = cached.get();
       	
* 为set和get方法提供了几个不同的覆盖实现，以区别数值类型和其他对象类型。对象类型默认会使用内建的序列化机制序列化存储。数值类型则按照redis的字符串方式存储，以保证incr和decr操作可以正常工作。如下：

		Future<String> set(String key, int o);
		Future<String> set(String key, long o);
		Future<String> set(String key, double o);
		Future<String> set(String key, Object o);
		
		Future<Object> get(String key);
		Future<Integer> getInt(String key);
		Future<Long> getLong(String key);
		Future<Double> getDouble(String key);
		
		
* 对象的序列化代码来自于 spymemcached 。默认有大对象的压缩机制。




