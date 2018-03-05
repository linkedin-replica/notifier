import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import database.ArangoHandler;
import database.DatabaseConnection;
import models.Notification;
import org.junit.*;
import utils.ConfigReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArangoHandlerTest {
    private static ArangoHandler arangoHandler;
    private static ArangoDatabase arangoDb;
    static ConfigReader config;

    @BeforeClass
    public static void init() throws IOException {
        ConfigReader.isTesting = true;
        config = ConfigReader.getInstance();
        arangoHandler = new ArangoHandler();
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
    public void testSendNotification() throws IOException {
        String collectionName = config.getArangoConfig("collection.notifications.name");
        long time = System.currentTimeMillis();
        Notification newNotification =
                new Notification("notification text",
                        "notification link",
                        time,
                        false);
        int userId = 12345;
        arangoHandler.sendNotification(userId, newNotification);
        String query = "FOR t in " + collectionName + " RETURN t";
        ArangoCursor<Notification> allNotificationsCursor = arangoDb.query(query,
                new HashMap<String, Object>(),
                null,
                Notification.class);
        List<Notification> allNotifications = new ArrayList<>();
        while (allNotificationsCursor.hasNext())
            allNotifications.add(allNotificationsCursor.next());

        assertEquals("Expected to have one notification in database", 1, allNotifications.size());

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
        arangoHandler.sendNotification(userId, n1);
        arangoHandler.sendNotification(userId, n2);

        List<Notification> all = arangoHandler.getAllNotifications(userId);
        assertEquals("Expected 2 notifications", 2, all.size());

        List<Notification> unread = arangoHandler.getUnreadNotifications(userId);
        assertEquals("Expected 1 unread notification", 1, unread.size());

        arangoHandler.markAllNotificationsAsRead(userId);
        all = arangoHandler.getAllNotifications(userId);
        unread = arangoHandler.getUnreadNotifications(userId);
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
        ConfigReader.isTesting = false;
        DatabaseConnection.getDBConnection().closeConnections();
    }
}
