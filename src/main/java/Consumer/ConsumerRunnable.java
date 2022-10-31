package Consumer;

import Shapes.LiftRideEvent;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConsumerRunnable implements Runnable{
    private static final String  requestQueueName = "rq_queue";
    Map<Integer, List<LiftRideEvent>> map;
    Connection connection;
    Channel channel;
    public ConsumerRunnable(Map map, Connection connection){
        this.map =map;
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
            //throw new RuntimeException(e);
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
//                    .Builder()
//                    .correlationId(delivery.getProperties().getCorrelationId())
//                    .build();
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            //String response = "";
            Gson gson = new Gson();
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                //System.out.println(message);
                LiftRideEvent event = (LiftRideEvent)gson.fromJson(message,LiftRideEvent.class);
                map.putIfAbsent(event.getSkierID(), new ArrayList<LiftRideEvent>());
                map.get(event.getSkierID()).add(event);
                //response = "200";
            } catch (RuntimeException e) {
                //response = "400";
                e.printStackTrace();
            } finally {
                // System.out.println("Response: "+response);
                // channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                // channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                //int total = 0;
                //for(List v : map.values()) total+= v.size();
                //System.out.println("size of map: "+map.size());
                //System.out.println("total: "+total);
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
