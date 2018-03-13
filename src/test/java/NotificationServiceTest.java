import com.arangodb.ArangoDatabase;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.models.Notification;
import org.junit.*;
import com.linkedin.replica.notifier.services.NotificationService;
import com.linkedin.replica.notifier.config.Configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NotificationServiceTest {
    private static NotificationService notificationService;
    private static ArangoDatabase arangoDb;
    static Configuration config;

    @BeforeClass
    public static void init() throws IOException {
        String rootFolder = "src/main/resources/config/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config");
        DatabaseConnection.init();
        config = Configuration.getInstance();
        notificationService = new NotificationService();
        arangoDb = DatabaseConnection.getInstance().getArangoDriver().db(
                Configuration.getInstance().getArangoConfig("db.name")
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

        notificationService.serve("notifications.send", args);


        args.clear();
        args.put("userId", "1234");

        LinkedHashMap<String, Object> result = notificationService.serve("notifications.all", args);
        List<Notification> all = (List<Notification>) result.get("results");

        assertEquals("Expected 1 notification" ,1, all.size());

        notificationService.serve("notifications.mark.read",  args);


        args.put("text", "notification text 2");
        args.put("link", "notification link 2");
        notificationService.serve("notifications.send", args);

        args.clear();
        args.put("userId", "1234");

        result = notificationService.serve("notifications.all", args);
        all = (List<Notification>) result.get("results");

        result = notificationService.serve("notifications.unread", args);
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
        DatabaseConnection.getInstance().closeConnections();
    }
}
