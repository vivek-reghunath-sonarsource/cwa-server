package app.coronawarn.server.services.distribution.dgc;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryCustom implements Retryer {

  private int retryMaxAttempt;

  private long retryInterval;

  private int attempt = 1;

  private static final Logger log = LoggerFactory.getLogger(RetryCustom.class);

  public RetryCustom(int retryMaxAttempt, Long retryInterval) {
    this.retryMaxAttempt = retryMaxAttempt;
    this.retryInterval = retryInterval;
  }

  @Override
  public void continueOrPropagate(RetryableException e) {
    log.info("Feign retry attempt {} due to {} ", attempt, e.getMessage());

    if (attempt++ == retryMaxAttempt) {
      throw e;
    }
    try {
      Thread.sleep(retryInterval);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public Retryer clone() {
    return new RetryCustom(6, 2000L);
  }
}
