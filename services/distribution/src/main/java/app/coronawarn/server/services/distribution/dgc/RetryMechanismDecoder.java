package app.coronawarn.server.services.distribution.dgc;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class RetryMechanismDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();

  @Override
  public Exception decode(String s, Response response) {
    Exception exception = defaultErrorDecoder.decode(s, response);

    if (exception instanceof RetryableException) {
      return exception;
    }

    if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
      return new RetryableException(response.status(), "Server error", response.request().httpMethod(), null, null);
    }

    return exception;
  }
}
