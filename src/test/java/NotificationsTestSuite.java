import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ArangoHandlerTest.class,
        NotificationServiceTest.class
})
public class NotificationsTestSuite {}
