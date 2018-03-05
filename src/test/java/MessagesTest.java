import com.arangodb.ArangoDatabase;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import database.DatabaseConnection;
import messaging.MessagesReceiver;
import utils.ConfigReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;
import org.junit.*;

public class MessagesTest {
    private static ConfigReader config;
    private static String QUEUE_NAME;
    private static MessagesReceiver messagesReceiver;
    private static ArangoDatabase arangoDb;

    @BeforeClass
    public static void init() throws IOException, TimeoutException {
        ConfigReader.isTesting = true;
        config = ConfigReader.getInstance();

        // init message receiver
        QUEUE_NAME = config.getAppConfig("rabbitmq.queue");
        messagesReceiver = new MessagesReceiver();

        // init db
        arangoDb = DatabaseConnection.getDBConnection().getArangoDriver().db(
                ConfigReader.getInstance().getArangoConfig("db.name")
        );

        arangoDb.createCollection(
                config.getArangoConfig("collection.notifications.name")
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
        ConfigReader.isTesting = false;

        // close message queue connection
        messagesReceiver.closeConnection();

        // clean db
        arangoDb.collection(
                config.getArangoConfig("collection.notifications.name")
        ).drop();
        DatabaseConnection.getDBConnection().closeConnections();
    }
}
