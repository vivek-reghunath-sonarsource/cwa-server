package app.coronawarn.server.services.distribution.dgc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.coronawarn.server.services.distribution.dgc.client.DigitalCovidCertificateClient;
import app.coronawarn.server.services.distribution.dgc.exception.DigitalCovidCertificateException;
import app.coronawarn.server.services.distribution.objectstore.integration.BaseS3IntegrationTest;
import feign.Response;
import feign.RetryableException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;



public class DigitalCovidCertificateRetryMechanismTest extends BaseS3IntegrationTest {

  @MockBean
  private DigitalGreenCertificateToCborMapping digitalGreenCertificateToCborMapping;

  @Mock
  private DigitalCovidCertificateClient digitalCovidCertificateClient;


  @Test
  public void testCustomRetryConfigByErrorDecoder() throws IOException, DigitalCovidCertificateException {

    when(digitalCovidCertificateClient.getCountryRuleByHash(any(), any())).thenReturn(
        Response.builder().status(503).build(),
        Response.builder().status(200).build());

    class RetryOn503ConflictStatus extends RetryMechanismDecoder.Default {

      @Override
      public Exception decode(final String methodKey, final Response response) {
        if (503 == response.status()) {
          return new RetryableException(response.status(), "getting conflict and retry", null, null, null);
        } else
          return super.decode(methodKey, response);
      }
    }
}
