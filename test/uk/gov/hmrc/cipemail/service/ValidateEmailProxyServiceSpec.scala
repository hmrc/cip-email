/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cipemail.service

import mockws.MockWS
import mockws.MockWSHelpers.Action
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OWrites}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.mvc.Request
import play.api.mvc.Results.Ok
import play.api.test.Helpers.{POST, await, defaultAwaitTimeout}
import play.api.test.FakeRequest
import uk.gov.hmrc.cipemail.dto.EmailAddressDTO

import java.util.concurrent.CompletionStage

class ValidateEmailProxyServiceSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  private val fakeRequest = FakeRequest()
  private implicit val writes: OWrites[EmailAddressDTO] = Json.writes[EmailAddressDTO]
  private lazy val classUnderTest = app.injector.instanceOf[ValidateEmailProxyService]
/*
  "ValidateEmailProxyService#callCipValidateEmailEndpoint" should {
    "return response if the response from the cip platform is successful" in {

      val wsClient: WSClient = mock[WSClient]
      val mockWSRequest: WSRequest = mock[WSRequest]
      val mockResponse = mock[WSResponse]
      when(mockResponse.getStatus()).thenReturn(200)
      val mockCompletionStageResponse: CompletionStage[WSResponse] = mock[CompletionStage[WSResponse]]
      val completableFutureMock: CompletableFuture[WSResponse] = mock[CompletableFuture[WSResponse]]
      when(completableFutureMock.get(anyLong(), any())).thenReturn(mockResponse)
      when(mockCompletionStageResponse.toCompletableFuture).thenReturn(completableFutureMock)

      val request: Request[JsValue] = fakeRequest.withBody(Json.toJson(EmailAddressDTO("test@test.com")))

      when(mockWSRequest.post(anyString())).thenReturn(mockCompletionStageResponse)

      val actual = classUnderTest.callCipValidateEmailEndpoint(request)
      actual mustBe true
    }
  }*/

/*
  "ValidateEmailProxyService#callCipValidateEmailEndpoint" should {
    "return response if the response from the cip platform is successful" in {

      val ws = MockWS {
        case (POST, "/customer-insight-platform/email/validate-format")  => Action { Ok("")}
      }

      val mockResponse = await(ws.url("/customer-insight-platform/email/validate-format").get())
      val mockWSRequest: WSRequest = mock[WSRequest]
      val mockCompletionStageResponse: CompletionStage[WSResponse] = mock[CompletionStage[WSResponse]]
      when(mockWSRequest.post(anyString())).thenReturn(mockResponse)

      val request: Request[JsValue] = fakeRequest.withBody(Json.toJson(EmailAddressDTO("test@test.com")))

      val actual = classUnderTest.callCipValidateEmailEndpoint(request)
      actual mustBe true
    }
  }*/

}
