/*
 * Copyright 2023 HM Revenue & Customs
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

import akka.stream.Materializer
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, stubFor, urlEqualTo}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.cipemail.config.CircuitBreakerConfig
import uk.gov.hmrc.cipemail.utils.TestActorSystem
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

class CircuitBreakerWrapperSpec extends AnyWordSpec
  with Matchers
  with WireMockSupport
  with ScalaFutures
  with HttpClientV2Support
  with TestActorSystem {

  val verificationUrl: String = "/customer-insight-platform/email/verify"
  val notificationsUrl: String = s"/v2/notifications/test-test"

  "Circuit Breakers" should {
    "not be triggered when call is successful" in new SetUp {

      stubFor(post(urlEqualTo(verificationUrl)).willReturn(aResponse()))

      val result = circuitBreakers.withCircuitBreaker(
        httpClientV2
          .post(url"http://$wireMockHost:$wireMockPort/customer-insight-platform/email/verify")
          .withBody(Json.obj("email" -> "test@test.com"))
          .transform(_.withRequestFilter(AhcCurlRequestLogger()))
          .execute[HttpResponse]
      )

      await(result).status shouldBe OK
      circuitBreakers.circuitBreaker.isOpen shouldBe false
      circuitBreakers.circuitBreaker.isClosed shouldBe true
      circuitBreakers.circuitBreaker.isHalfOpen shouldBe false
    }
  }

  trait SetUp {
    protected implicit val hc: HeaderCarrier = HeaderCarrier()
    val circuitBreakers = new CircuitBreakerWrapper {
      override def configCB: CircuitBreakerConfig = CircuitBreakerConfig("Cip Verification", 2, 60.seconds, 60.seconds, 60.seconds, 1, 0)

      override def materializer: Materializer = Materializer(TestActorSystem.system)
    }

    implicit val connectionFailure: Try[HttpResponse] => Boolean = {
      case Success(_) => false
      case Failure(_) => true
    }
  }
}
