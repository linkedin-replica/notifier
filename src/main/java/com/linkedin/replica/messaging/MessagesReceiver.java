package com.linkedin.replica.messaging;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import com.linkedin.replica.services.NotificationService;
import com.linkedin.replica.config.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MessagesReceiver {
    private Configuration configuration = Configuration.getInstance();
    private NotificationService notificationService = new NotificationService();
    private final String QUEUE_NAME = configuration.getAppConfig("rabbitmq.queue");
    private final String RABBIT_MQ_IP = configuration.getAppConfig("rabbitmq.ip");;

    public MessagesReceiver() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ_IP);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println("Started notification receiver successfully.");

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
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new MessagesReceiver();
    }
}
