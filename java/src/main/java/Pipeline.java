import dependencies.Config;
import dependencies.Emailer;
import dependencies.Logger;
import dependencies.Project;

public class Pipeline {
  private final Config config;
  private final Emailer emailer;
  private final Logger log;

  public Pipeline(Config config, Emailer emailer, Logger log) {
    this.config = config;
    this.emailer = emailer;
    this.log = log;
  }

  private boolean test(Project project) {
    if (!project.hasTests()) {
      log.info("No tests");
      return true;
    }
    if ("success".equals(project.runTests())) {
      log.info("Tests passed");
      return true;
    }
    log.error("Tests failed");
    return false;

  }

  private boolean isDeploySuccessFull(Project project) {
    return "success".equals(project.deploy());
  }

  private String deploy(Project project) {
    if (!test(project)) {
      return "Tests failed";
    }

    if (!isDeploySuccessFull(project)) {
      log.error("Deployment failed");
      return "Deployment failed";
    }

    log.info("Deployment successful");
    return "Deployment completed successfully";
  }

  private void sendEmail(String report) {
    if (!config.sendEmailSummary()) {
      log.info("Email disabled");
      return;
    }
    log.info("Sending email");
    emailer.send(report);
  }

  public void run(Project project) {
    sendEmail(deploy(project));
  }

}
