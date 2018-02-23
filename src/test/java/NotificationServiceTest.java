import com.arangodb.ArangoDatabase;
import database.ArangoHandler;
import database.DatabaseConnection;
import models.Notification;
import org.junit.*;
import services.NotificationService;
import utils.ConfigReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NotificationServiceTest {
    private static NotificationService notificationService;
    private static ArangoDatabase arangoDb;
    static ConfigReader config;

    @BeforeClass
    public static void init() throws IOException {
        ConfigReader.isTesting = true;
        config = ConfigReader.getInstance();
        notificationService = new NotificationService();
        arangoDb = DatabaseConnection.getDBConnection().getArangoDriver().db(
                ConfigReader.getInstance().getArangoConfig("db.name")
        );
    }

    @Before
    public void initBeforeTest() throws IOException {
        arangoDb.createCollection(
                config.getArangoConfig("collection.notifications.name")
        );
    }

    @Test
    public void testNotificationService() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HashMap<String, String> args = new HashMap<>();
        args.put("userId", "1234");
        args.put("text", "notification text");
        args.put("link", "notification link");

        notificationService.serve("send.notification", args);


        args.clear();
        args.put("userId", "1234");

        LinkedHashMap<String, Object> result = notificationService.serve("all.notifications", args);
        List<Notification> all = (List<Notification>) result.get("results");

        assertEquals("Expected 1 notification" ,1, all.size());

        notificationService.serve("mark.read",  args);


        args.put("text", "notification text 2");
        args.put("link", "notification link 2");
        notificationService.serve("send.notification", args);

        args.clear();
        args.put("userId", "1234");

        result = notificationService.serve("all.notifications", args);
        all = (List<Notification>) result.get("results");

        result = notificationService.serve("unread.notifications", args);
        List<Notification> unread = (List<Notification>) result.get("results");

        assertEquals("Expected 2 notifications" , 2, all.size());
        assertEquals("Expected 1 unread notification", 1, unread.size());
    }

    @After
    public void cleanAfterTest() throws IOException {
        arangoDb.collection(
                config.getArangoConfig("collection.notifications.name")
        ).drop();
    }

    @AfterClass
    public static void clean() throws IOException {
        ConfigReader.isTesting = false;
        DatabaseConnection.getDBConnection().closeConnections();
    }
}
