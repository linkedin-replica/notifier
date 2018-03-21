package controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RequestProcessingHandlerTest.class,
        RequestDecoderHandlerTest.class,
        ResponseEncoderHandlerTest.class
})
public class ControllerTestSuite {}
