package Consumer;

import Shapes.LiftRideEvent;
import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final int THREADCNT = 100;
    private static final String  requestQueueName = "rq_queue";

    // private String  replyQueueName = "rp_queue";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Map<Integer, List<LiftRideEvent>> map = new ConcurrentHashMap<>();
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setHost("35.87.183.246");

        Connection connection = factory.newConnection();

//        Channel channel = connection.createChannel();
//        channel.queueDeclare(requestQueueName, false, false, false, null);
//        channel.queuePurge(requestQueueName);
//        channel.close();

        ConsumerRunnable runnable = new ConsumerRunnable(map,connection);
        for(int i=0; i<THREADCNT; i++){
            //new Thread(new ConsumerRunnable(map,connection)).start();
            new Thread(runnable).start();
        }

        System.out.println(" [x] Awaiting lift ride event POST requests");

    }
}
