package configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LogbackConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LogbackConfig.class);

    public static void initLogback(String configFilepathName) {
        File file = new File(configFilepathName);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            joranConfigurator.doConfigure(file);
        } catch (Exception e) {
            LOG.error("Load logback config file error. Message: "+ e.getMessage(),e);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }
}
