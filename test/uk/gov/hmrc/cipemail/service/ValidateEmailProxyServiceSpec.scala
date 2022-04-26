package uk.gov.hmrc.cipemail.service

import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.{PlaySpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OWrites}
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.libs.ws.{WSClient, WSRequest}
import play.libs.ws.WSResponse
import uk.gov.hmrc.cipemail.dto.EmailAddressDTO

import java.util.concurrent.CompletionStage

class ValidateEmailProxyServiceSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest()
  private implicit val writes: OWrites[EmailAddressDTO] = Json.writes[EmailAddressDTO]
  private lazy val classUnderTest = app.injector.instanceOf[ValidateEmailProxyService]
  // val config: Configuration = app.configuration
  // val servicesConfig: ServicesConfig = new ServicesConfig(config)
  // val appConfig: AppConfig = new AppConfig(config, servicesConfig)
  //val classUnderTest = new ValidateEmailProxyService(appConfig, wsClient)

  "ValidateEmailProxyService#callCipValidateEmailEndpoint" should {
    "return response if the response from the cip platform is successful" in {

      val wsClient: WSClient = mock[WSClient]
      val mockWSRequest: WSRequest = mock[WSRequest]
      val futureResponse: CompletionStage[WSResponse] = mock[CompletionStage[WSResponse]]

      val request: Request[JsValue] = fakeRequest.withBody(Json.toJson(EmailAddressDTO("test@test.com")))

      when(mockWSRequest.post(anyString())).thenReturn(futureResponse)

      val actual = classUnderTest.callCipValidateEmailEndpoint(request)
      actual mustBe true
    }
  }

}
