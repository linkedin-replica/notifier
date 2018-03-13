import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.linkedin.replica.notifier.database.handlers.impl.ArangoNotificationsHandler;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.models.Notification;
import org.junit.*;
import com.linkedin.replica.notifier.config.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static ArangoNotificationsHandler arangoNotificationsHandler;
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
        arangoNotificationsHandler = new ArangoNotificationsHandler();
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
    public void testSendNotification() throws IOException {
        String collectionName = config.getArangoConfig("collection.notifications.name");
        long time = System.currentTimeMillis();
        Notification newNotification =
                new Notification("notification text",
                        "notification link",
                        time,
                        false);
        int userId = 12345;
        arangoNotificationsHandler.sendNotification(userId, newNotification);
        String query = "FOR t in " + collectionName + " RETURN t";
        ArangoCursor<Notification> allNotificationsCursor = arangoDb.query(query,
                new HashMap<String, Object>(),
                null,
                Notification.class);
        List<Notification> allNotifications = new ArrayList<>();
        while (allNotificationsCursor.hasNext())
            allNotifications.add(allNotificationsCursor.next());

        assertEquals("Expected to have one notification in com.linkedin.replica.database", 1, allNotifications.size());

        newNotification = allNotifications.get(0);
        assertEquals("Expected matching notification text", "notification text", newNotification.getNotificationText());
        assertEquals("Expected matching notification link", "notification link", newNotification.getLink());
        assertEquals("Expected matching notification time", time, newNotification.getTimeStamp());
        assertEquals("Expected notification to be unread", false, newNotification.isRead());
    }

    @Test
    public void testNotificationInteraction() {
        Notification n1 =
                new Notification("notification text",
                        "notification link",
                        System.currentTimeMillis(),
                        false);
        Notification n2 =
                new Notification("notification text",
                        "notification link",
                        System.currentTimeMillis(),
                        true);
        int userId = 1234;
        arangoNotificationsHandler.sendNotification(userId, n1);
        arangoNotificationsHandler.sendNotification(userId, n2);

        List<Notification> all = arangoNotificationsHandler.getAllNotifications(userId);
        assertEquals("Expected 2 notifications", 2, all.size());

        List<Notification> unread = arangoNotificationsHandler.getUnreadNotifications(userId);
        assertEquals("Expected 1 unread notification", 1, unread.size());

        arangoNotificationsHandler.markAllNotificationsAsRead(userId);
        all = arangoNotificationsHandler.getAllNotifications(userId);
        unread = arangoNotificationsHandler.getUnreadNotifications(userId);
        assertEquals("Expected 2 notifications", 2, all.size());
        assertEquals("Expected 0 unread notification", 0, unread.size());
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
