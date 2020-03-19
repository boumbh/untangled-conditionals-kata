import dependencies.Config;
import dependencies.Emailer;
import dependencies.Project;

import java.util.logging.Logger;

public class Pipeline {
    private final Config config;
    private final Emailer emailer;
    private final Logger log;

    public Pipeline(Config config, Emailer emailer, Logger log) {
        this.config = config;
        this.emailer = emailer;
        this.log = log;
    }

    public void run(Project project) {
        boolean testsPassed;
        boolean buildSuccessful;

        if (project.hasTests()) {
            if ("success".equals(project.runTests())) {
                log.info("Tests passed");
                testsPassed = true;
            } else {
                log.severe("Tests failed");
                testsPassed = false;
            }
        } else {
            log.info("No tests");
            testsPassed = true;
        }

        if (testsPassed) {
            if ("success".equals(project.build())) {
                log.info("Deployment successful");
                buildSuccessful = true;
            } else {
                log.severe("Deployment failed");
                buildSuccessful = false;
            }
        } else {
            buildSuccessful = false;
        }

        if (config.sendEmailSummary()) {
            log.info("Sending email");
            if (testsPassed) {
                if (buildSuccessful) {
                    emailer.send("Deployment completed successfully");
                } else {
                    emailer.send("Deployment failed");
                }
            } else {
                emailer.send("Tests failed");
            }
        } else {
            log.info("Email disabled");
        }
    }
}