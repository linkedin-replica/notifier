import controller.ControllerTestSuite;
import core.CoreTestSuite;
import messaging.MessagingTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ControllerTestSuite.class,
        CoreTestSuite.class,
        MessagingTestSuite.class
})
public class AllTestsSuite {}
