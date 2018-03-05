import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConfigReader;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by mostafa on 3/5/18.
 */
public class MessagesTest {
    private ConfigReader configReader = ConfigReader.getInstance();
    private final static String QUEUE_NAME = "SEND_NOTIFICATION";

    public MessagesTest() throws IOException {
    }

    public static void main(String[] argv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        JsonObject object = new JsonObject();
        object.addProperty("userId", "12314");
        object.addProperty("text", "message text");
        object.addProperty("link", "message link");
        String message = object.toString();
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    }
}
