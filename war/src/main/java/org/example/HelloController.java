package org.example;

import io.micrometer.common.util.StringUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Paolo")
public class HelloController {

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    String hello() throws ClassNotFoundException {
        Class<?> testClass = Class.forName("org.example.Test");
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        XMLRunListener xmlRunListener = new XMLRunListener();
        LauncherDiscoveryRequest request = getLauncherDiscoveryRequest(testClass, "test");
        launcher.registerTestExecutionListeners(xmlRunListener, summaryGeneratingListener);
        launcher.execute(request);
        summaryGeneratingListener.getSummary().getFailures().forEach(e -> logger.error("s", e.getException()));
        processTestResults(testClass, summaryGeneratingListener);
        //Mockito.mock(Object.class);
        return "Hi";
    }

    private void processTestResults(Class<?> testClass, SummaryGeneratingListener listener) {
        TestExecutionSummary summary = listener.getSummary();
        long testsFound = summary.getTestsFoundCount();
        long testsFailed = summary.getTotalFailureCount();
        long testsSkipped = summary.getTestsSkippedCount();

        if (testsFailed > 0) {
            logFailedJUnit5Tests(summary);
            System.out.println(testsFound + " tests: " + testsFailed + " failures, " + testsSkipped + " skipped (" + testClass.getName() + ").");
        } else {
            System.out.println(testsFound + " tests completed successfully (" + testClass.getName() + ").");
        }
    }

    private void logFailedJUnit5Tests(TestExecutionSummary summary) {
        summary.getFailures().forEach(failure -> System.out.println(failure.getTestIdentifier().getDisplayName() + " " + failure.getException()));
    }

    private LauncherDiscoveryRequest getLauncherDiscoveryRequest(Class<?> testClass, String methodName) {
        DiscoverySelector selector = StringUtils.isBlank(methodName)
                ? DiscoverySelectors.selectClass(testClass)
                : DiscoverySelectors.selectMethod(testClass, methodName);

        LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request()
                .selectors(selector);
        requestBuilder = addParallelExecutionParameters(requestBuilder);
        return requestBuilder.build();
    }

    private LauncherDiscoveryRequestBuilder addParallelExecutionParameters(LauncherDiscoveryRequestBuilder requestBuilder) {
        return requestBuilder
                .configurationParameter(PARALLEL_EXECUTION_ENABLED, "false")
                .configurationParameter(PARALLELISM_MODE_FOR_MULTIPLE_CLASSES, CONCURRENT);
    }

    public static final String PARALLEL_EXECUTION_ENABLED            = "junit.jupiter.execution.parallel.enabled";

    /*
     * Default: same_thread. To enable parallelism, use "concurrent".
     */
    public static final String PARALLELISM_MODE_FOR_MULTIPLE_CLASSES = "junit.jupiter.execution.parallel.mode.classes.default";

    public static final String CONCURRENT                            = "concurrent";
}