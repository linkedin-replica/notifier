package com.linkedin.replica.notifier.messaging;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.exceptions.BadRequestException;
import com.linkedin.replica.notifier.services.NotificationService;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeoutException;

public class ClientMessagesReceiver {
    private Configuration configuration = Configuration.getInstance();
    private NotificationService notificationService = new NotificationService();
    private final String QUEUE_NAME = configuration.getAppConfig("rabbitmq.queue.client");
    private final String RABBIT_MQ_IP = configuration.getAppConfig("rabbitmq.ip");

    private ConnectionFactory factory;
    private Channel channel;
    private Connection connection;
    private Gson gson = new Gson();

    public ClientMessagesReceiver() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ_IP);
        connection = factory.newConnection();
        channel = connection.createChannel();

        // Create queue if not exists
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // set unacknowledged limit to 1 message
        channel.basicQos(1);

        // Create the messages consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                // Create the response message properties
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();

                // Extract the request arguments
                JsonObject object = new JsonParser().parse(new String(body)).getAsJsonObject();
                String commandName = object.get("commandName").getAsString();
                HashMap<String, String> args = new HashMap<>();
                for(String key: object.keySet())
                    if(!key.equals("commandName"))
                        args.put(key, object.get(key).getAsString());

                // Call the service and form the response
                LinkedHashMap<String, Object> response = new LinkedHashMap<>();
                try {
                    Object results = notificationService.serve(commandName, args);
                    if(results != null)
                        response.put("results", results);
                    response.put("statusCode", 200);
                }
                catch (BadRequestException e) {
                    // set status code to 400
                    response.put("statusCode", "400");
                    response.put("error", e.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    // set status code to 500
                    response.put("statusCode", "500");
                    response.put("error", "Internal server error.");

                    // TODO write the error to a log
                }

                // publish the response to the "replyTo" queue
                byte[] jsonResponse = gson.toJson(response).getBytes();
                channel.basicPublish( "", properties.getReplyTo(), replyProps, jsonResponse);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
