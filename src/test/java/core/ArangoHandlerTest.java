package core;

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
    private static Configuration config;

    @BeforeClass
    public static void init() throws IOException {
        String rootFolder = "src/main/resources/config/";
        Configuration.init(rootFolder + "app.config",
                rootFolder + "arango.test.config",
                rootFolder + "commands.config",
                rootFolder + "controller.config");
        DatabaseConnection.init();
        config = Configuration.getInstance();
        arangoNotificationsHandler = new ArangoNotificationsHandler();
        arangoDb = DatabaseConnection.getInstance().getArangoDriver().db(
                Configuration.getInstance().getArangoConfigProp("db.name")
        );
    }

    @Before
    public void initBeforeTest() throws IOException {
        arangoDb.createCollection(
                config.getArangoConfigProp("collection.notifications.name")
        );
    }

    @Test
    public void testSendNotification() throws IOException {
        String collectionName = config.getArangoConfigProp("collection.notifications.name");
        long time = System.currentTimeMillis();
        Notification newNotification =
                new Notification("1,",
                        "notification text",
                        "notification link",
                        "12345",
                        time,
                        false);
        arangoNotificationsHandler.sendNotification(newNotification);
        String query = "FOR t in " + collectionName + " RETURN t";
        ArangoCursor<Notification> allNotificationsCursor = arangoDb.query(query,
                new HashMap<>(),
                null,
                Notification.class);
        List<Notification> allNotifications = new ArrayList<>();
        while (allNotificationsCursor.hasNext())
            allNotifications.add(allNotificationsCursor.next());

        assertEquals("Expected to have one notification in com.linkedin.replica.database", 1, allNotifications.size());

        newNotification = allNotifications.get(0);
        assertEquals("Expected matching notification text", "notification text", newNotification.getText());
        assertEquals("Expected matching notification link", "notification link", newNotification.getLink());
        assertEquals("Expected matching notification time", time, newNotification.getTimestamp());
        assertEquals("Expected notification to be unread", false, newNotification.isRead());
    }

    @Test
    public void testNotificationInteraction() {
        String userId = "1234";
        Notification n1 =
                new Notification("2",
                        "notification text",
                        "notification link",
                        userId,
                        System.currentTimeMillis(),
                        false);
        Notification n2 =
                new Notification("3",
                        "notification text",
                        "notification link",
                        userId,
                        System.currentTimeMillis(),
                        true);
        arangoNotificationsHandler.sendNotification(n1);
        arangoNotificationsHandler.sendNotification(n2);

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
                config.getArangoConfigProp("collection.notifications.name")
        ).drop();
    }

    @AfterClass
    public static void clean() throws IOException {
        DatabaseConnection.getInstance().closeConnections();
    }
}
