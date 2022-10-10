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

package uk.gov.hmrc.cipemail.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Results.Ok
import uk.gov.hmrc.cipemail.config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.concurrent.ExecutionContext.Implicits.global

class ValidateConnectorSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with GuiceOneAppPerSuite
  with ScalaFutures
  with HttpClientV2Support
{

  val url: String = "/customer-insight-platform/email/validate"

  "ValidatorConnector.callService" should {
    "return HttpResponse OK for valid input" in new Setup {
      val email = "test@test.com"
      stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse())
      )

      implicit val hc = HeaderCarrier()
      validateConnector.callService(Json.parse(s"""{"email" : "$email"}"""))
        .futureValue shouldBe Ok

      verify(
        postRequestedFor(urlEqualTo(url))
          .withRequestBody(equalToJson(s"""{"email": "test@test.com"}"""))
      )
    }

  }

  trait Setup {

    private val appConfig = new AppConfig(
      Configuration.from(Map(
        "microservice.services.cipemail.validation.host" -> wireMockHost,
        "microservice.services.cipemail.validation.port" -> wireMockPort,
        "microservice.services.cipemail.validation.protocol" -> "http"
      ))
    )

    val validateConnector = new ValidateConnector(
      httpClientV2,
      appConfig
    )
  }
}
