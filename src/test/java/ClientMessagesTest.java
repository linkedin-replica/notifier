import com.arangodb.ArangoDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.database.handlers.impl.ArangoNotificationsHandler;
import com.linkedin.replica.notifier.messaging.ClientMessagesReceiver;
import com.linkedin.replica.notifier.models.Notification;
import com.rabbitmq.client.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class ClientMessagesTest {
    private static Configuration config;
    private static String QUEUE_NAME;
    private static ClientMessagesReceiver messagesReceiver;
    private static ArangoDatabase arangoDb;
    private static ArangoNotificationsHandler arangoHandler;

    @BeforeClass
    public static void init() throws IOException, TimeoutException {
        String rootFolder = "src/main/resources/config/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config");
        DatabaseConnection.init();
        config = Configuration.getInstance();

        // init message receiver
        QUEUE_NAME = config.getAppConfig("rabbitmq.queue.client");
        messagesReceiver = new ClientMessagesReceiver();

        // init db
        arangoDb = DatabaseConnection.getInstance().getArangoDriver().db(
                Configuration.getInstance().getArangoConfig("db.name")
        );

        arangoDb.createCollection(
                config.getArangoConfig("collection.notifications.name")
        );
        arangoHandler = new ArangoNotificationsHandler();
        arangoHandler.sendNotification(12314, new Notification("Text", "Link", 0, false));
    }

    @Test
    public void testSendMessage() throws IOException, TimeoutException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String replyQueueName = channel.queueDeclare().getQueue();

        JsonObject object = new JsonObject();
        object.addProperty("commandName", "notifications.all");
        object.addProperty("userId", 12314);
        byte[] message = object.toString().getBytes();
        final String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", QUEUE_NAME, props, message);

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body));
                }
            }
        });

        String resMessage = response.take();
        JsonObject resObject = new JsonParser().parse(resMessage).getAsJsonObject();

        assertEquals("Response must contain one new notification", 1, resObject.get("results").getAsJsonArray().size());
    }

    @AfterClass
    public static void clean() throws IOException, TimeoutException {
        // close message queue connection
        messagesReceiver.closeConnection();

        // clean db
        arangoDb.collection(
                config.getArangoConfig("collection.notifications.name")
        ).drop();
        DatabaseConnection.getInstance().closeConnections();
    }
}
