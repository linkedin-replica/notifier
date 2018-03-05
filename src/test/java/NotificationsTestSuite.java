import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ArangoHandlerTest.class,
        NotificationServiceTest.class,
        MessagesTest.class
})
public class NotificationsTestSuite {}
