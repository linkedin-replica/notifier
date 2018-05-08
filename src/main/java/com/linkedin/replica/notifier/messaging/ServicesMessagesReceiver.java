package com.linkedin.replica.notifier.messaging;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.services.NotificationService;
import com.linkedin.replica.notifier.services.Workers;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ServicesMessagesReceiver {
    private Configuration configuration = Configuration.getInstance();
    private NotificationService notificationService = new NotificationService();
    private final String QUEUE_NAME = configuration.getAppConfigProp("rabbitmq.queue.services");
    private final String RABBIT_MQ_IP = configuration.getAppConfigProp("rabbitmq.ip");
    private final String RABBIT_MQ_USERNAME = configuration.getAppConfigProp("rabbitmq.username");
    private final String RABBIT_MQ_PASSWORD = configuration.getAppConfigProp("rabbitmq.password");

    private ConnectionFactory factory;
    private Channel channel;
    private Connection connection;

    public ServicesMessagesReceiver() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setUsername(RABBIT_MQ_USERNAME);
        factory.setPassword(RABBIT_MQ_PASSWORD);
        factory.setHost(RABBIT_MQ_IP);
        connection = factory.newConnection();
        channel = connection.createChannel();
        // declare the queue if it does not exist
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Create the consumer (listener) for the new messages
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                Runnable messageProcessorRunnable = () -> {
                    // extract notification info from body and send it
                    JsonObject object = new JsonParser().parse(new String(body)).getAsJsonObject();
                    String userId = object.get("userId").getAsString();
                    String text = object.get("text").getAsString();
                    String link = object.get("link").getAsString();
                    HashMap<String, String> args = new HashMap<>();
                    args.put("userId", userId);
                    args.put("text", text);
                    args.put("link", link);

                    String commandName = "notifications.send";
                    try {
                        System.out.println(commandName + " " + args);
                        notificationService.serve(commandName, args);
                        System.out.println("Sent a new notification to user with id " + userId);
                    } catch (Exception e) {
                        // TODO write error to a log
                    }
                };

                Workers.getInstance().submit(messageProcessorRunnable);
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
