package Consumer;

import Shapes.LiftRideEvent;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConsumerRunnable implements Runnable{
    private static final String  requestQueueName = "rq_queue";
    //Map<Integer, List<LiftRideEvent>> map;
    Connection connection;
    Channel channel;
    JedisPool pool;
    public ConsumerRunnable(JedisPool pool, Connection connection){
        this.pool = pool;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
             channel = connection.createChannel();
             channel.queueDeclare(requestQueueName, false, false, false, null);
             channel.queuePurge(requestQueueName);
             channel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            Gson gson = new Gson();
            LiftRideEvent event = null;
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                event = (LiftRideEvent)gson.fromJson(message,LiftRideEvent.class);

            } catch (RuntimeException e) {
                //response = "400";
                e.printStackTrace();
            } finally {
            }

            try (Jedis jedis = pool.getResource()) {
                if(event!=null){
                    //Stores lift rides a skiers used on a specific day
                    String skierAndDay = "Skier"+"-"+event.getSkierID()+"-"+event.getDayID();
                    String newLift = event.getLiftID()+"-"+event.getLiftTime()+"-"+event.getResortID()+"-"+event.getDayID()+"-"+event.getSeasonID();
                    jedis.sadd(skierAndDay,newLift);
                    System.out.println(skierAndDay+": "+newLift);
                    //Stores dayIDs that a skiers has skied
                    jedis.sadd(event.getSkierID().toString(),event.getDayID());
                    //Stores skiers that visited resort X on day N in a set
                    String resortAndDay = "Resort"+"-"+event.getResortID()+"-"+event.getDayID();
                    jedis.sadd(resortAndDay,event.getSkierID().toString());
                    System.out.println(resortAndDay);
                    System.out.println("End");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        };


        try {
            channel.basicConsume(requestQueueName, false, deliverCallback, (consumerTag -> {}));
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }
}

/**
 * hlen()
 * jedis.scard()
 */
//
//String newLift = event.getLiftID()+"-"+event.getLiftTime()+"-"+event.getResortID()+"-"+event.getDayID()+"-"+event.getSeasonID();
//String liftList = jedis.hget(event.getSkierID().toString(),event.getDayID());
//String  newLiftList = "";
//if(liftList!=null) newLiftList = liftList + " " + newLift;
//else newLiftList = newLift;
//jedis.hset(event.getSkierID().toString(),event.getDayID(),newLiftList);