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

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import uk.gov.hmrc.cipemail.utils.DataSteps

import scala.util.Random

class VerifyPasscodeIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with DataSteps {

  private val emailRandomizer = Random.alphanumeric.take(10).mkString

  "verify/passcode" should {
    "respond with 200 verified status with valid email and passcode" in {
      val email = s"$emailRandomizer@test.com"
      //generate passcode
      verify(email).futureValue

      //retrieve passcode
      val maybeEmailAndPasscode = retrievePasscode(email).futureValue

      //verify passcode (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify/passcode")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse {
            s"""{
               "email": "$email",
               "passcode": "${maybeEmailAndPasscode.get.passcode}"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "status").as[String] shouldBe "Verified"
    }

    // if there is a value for that input and its within 15 mins
    // then it either mathces or doesn't match
    // so 200 status: Verified or Not Verified
    // we know there is one for test@test.com
    // as the same email is used above
    "respond with 200 not verified status with non-matching passcode" in {
      val email = s"$emailRandomizer@test.com"
      //verify passcode (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify/passcode")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse {
            s"""{
               "email": "$email",
               "passcode": "123456"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "status").as[String] shouldBe "Not verified"
    }

    // if no passcode for that input i.e. not found in cache
    // then Enter a correct passcode should appear
    // therefore use a different input email that we know doesn't exists at all in the cache.
    "respond with 200 Enter a correct passcode error message with non existent passcode" in {
      //verify passcode (sut)
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify/passcode")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse {
            s"""{
               "email": "nopasscodefor@test.com",
               "passcode": "123456"
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 200
      (response.json \ "code").as[Int] shouldBe 1014
      (response.json \ "message").as[String] shouldBe "Enter a correct passcode"
    }

    // invalid request
    "respond with 400 status for invalid request" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/email/verify/passcode")
          .withRequestFilter(AhcCurlRequestLogger())
          .withHttpHeaders(("Authorization", "fake-token"))
          .post(Json.parse {
            s"""{
               "email": "test@test.com",
               "passcode": ""
               }""".stripMargin
          })
          .futureValue

      response.status shouldBe 400
      (response.json \ "code").as[Int] shouldBe 1002
      (response.json \ "message").as[String] shouldBe "Enter a valid passcode"
    }
  }
}
