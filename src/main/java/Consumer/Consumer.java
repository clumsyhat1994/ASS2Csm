package Consumer;

import Shapes.LiftRideEvent;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final int THREADCNT = 200;
    private static final String  requestQueueName = "rq_queue";

    // private String  replyQueueName = "rp_queue";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //test();


        Map<Integer, List<LiftRideEvent>> map = new ConcurrentHashMap<>();
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setHost("172.31.27.181");
        //factory.setHost("35.90.245.100");
        Connection connection = factory.newConnection();

        JedisPool pool = new JedisPool("172.31.11.208", 6379);
        //JedisPool pool = new JedisPool("34.221.230.20", 6379);
        ConsumerRunnable runnable = new ConsumerRunnable(pool,connection);
        for(int i=0; i<THREADCNT; i++){
            //new Thread(new ConsumerRunnable(map,connection)).start();
            new Thread(runnable).start();
        }

        System.out.println(" [x] Awaiting lift ride event POST requests");
    }
    private static void test(){
        JedisPool pool = new JedisPool("54.187.251.95", 6379);
        try (Jedis jedis = pool.getResource()) {

            //Stores lift rides a skiers used on a specific day
            String skierAndDay = "Skier"+"-"+"11001"+"-"+"40";
            String newLift = "004"+"-"+"12345"+"-"+"001"+"-"+"40"+"-"+"1";
            jedis.sadd(skierAndDay,newLift);

            //Stores skiers that visited resort X on day N in a set
            String resortAndDay = "Resort"+"-"+"007"+"-"+"40";
            jedis.sadd(resortAndDay,"123456");

        }
    }

}





//        try (Jedis jedis = pool.getResource()) {
//            jedis.set("hey", "World");
//
//        }
//        HostAndPort config = new HostAndPort("54.149.100.121", 6379);
//        PooledConnectionProvider provider = new PooledConnectionProvider(config);
//        UnifiedJedis client = new UnifiedJedis(provider);

//        Channel channel = connection.createChannel();
//        channel.queueDeclare(requestQueueName, false, false, false, null);
//        channel.queuePurge(requestQueueName);
//        channel.close();