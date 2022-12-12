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

package uk.gov.hmrc.cipemail

import org.apache.commons.lang3.RandomStringUtils
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger

class VerifyIntegrationSpec extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"

  "POST /" should {
    "return 202 with valid email address" in {
      val emailPrefix = RandomStringUtils.randomAlphabetic(4)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse(s"""{"email" : "$emailPrefix@test.com"}"""))
          .futureValue

      response.status shouldBe 202
    }

    "return 400 with invalid email address" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse("""{"email" : "incorrect_email_test"}"""))
          .futureValue

      response.status shouldBe 400
      (response.json \ "message").as[String] shouldBe "Enter a valid email"
    }
  }
}
