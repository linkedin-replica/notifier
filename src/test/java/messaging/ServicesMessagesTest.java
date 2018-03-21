package messaging;

import com.arangodb.ArangoDatabase;
import com.google.gson.JsonObject;
import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.messaging.ServicesMessagesReceiver;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;
import org.junit.*;

public class ServicesMessagesTest {
    private static Configuration config;
    private static String QUEUE_NAME;
    private static ServicesMessagesReceiver messagesReceiver;
    private static ArangoDatabase arangoDb;

    @BeforeClass
    public static void init() throws IOException, TimeoutException {
        String rootFolder = "src/main/resources/config/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config",
                rootFolder + "controller.config");
        DatabaseConnection.init();
        config = Configuration.getInstance();

        // init message receiver
        QUEUE_NAME = config.getAppConfigProp("rabbitmq.queue.services");
        messagesReceiver = new ServicesMessagesReceiver();

        // init db
        arangoDb = DatabaseConnection.getInstance().getArangoDriver().db(
                Configuration.getInstance().getArangoConfigProp("db.name")
        );

        arangoDb.createCollection(
                config.getArangoConfigProp("collection.notifications.name")
        );
    }

    @Test
    public void testSendMessage() throws IOException, TimeoutException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // encode notification as json
        JsonObject object = new JsonObject();
        object.addProperty("userId", "1234");
        object.addProperty("text", "message text");
        object.addProperty("link", "message link");
        String message = object.toString();

        // send message
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    @AfterClass
    public static void clean() throws IOException, TimeoutException {
        // close message queue connection
        messagesReceiver.closeConnection();

        // clean db
        arangoDb.collection(
                config.getArangoConfigProp("collection.notifications.name")
        ).drop();
        DatabaseConnection.getInstance().closeConnections();
    }
}
