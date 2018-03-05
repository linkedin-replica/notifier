package messaging;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import services.NotificationService;
import utils.ConfigReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MessagesReceiver {
    private ConfigReader configReader = ConfigReader.getInstance();
    private NotificationService notificationService = new NotificationService();
    private final String QUEUE_NAME = configReader.getAppConfig("rabbitmq.queue");
    private final String RABBIT_MQ_IP = configReader.getAppConfig("rabbitmq.ip");;

    private ConnectionFactory factory;
    private Channel channel;
    private Connection connection;

    public MessagesReceiver() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ_IP);
        connection = factory.newConnection();
        channel = connection.createChannel();
        // declare the queue if it does not exist
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println("Started notification receiver successfully.");

        // Create the consumer (listener) for the new messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                // extract notification info from body and send it
                JsonObject object = new JsonParser().parse(new String(body)).getAsJsonObject();
                String userId = object.get("userId").getAsString();
                String text = object.get("text").getAsString();
                String link = object.get("link").getAsString();
                HashMap<String, String> args = new HashMap<>();
                args.put("userId", userId);
                args.put("text", text);
                args.put("link", link);

                String commandName = "send.notification";
                try {
                    notificationService.serve(commandName, args);
                    System.out.println("Sent a new notification to user with id " + userId);
                } catch (Exception e) {
                    // TODO write error to a log
                }
            }
        };

        // attach the consumer
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
