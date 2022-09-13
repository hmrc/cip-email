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

package uk.gov.hmrc.cipemail

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.{Json, OWrites}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.cipemail.controllers.ValidateController

class ValidateIntegrationSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  private val fakeRequest = FakeRequest()
  private lazy val controller = app.injector.instanceOf[ValidateController]

  "POST /" should {
    "return 200 with valid email address" in new SetUp {
      val result = controller.validate()(
        fakeRequest.withBody(Json.toJson(EmailAddress("test@test.com"))))
      status(result) shouldBe Status.OK
    }

    "return 400 with invalid email address" in new SetUp {
      val result = controller.validate()(
        fakeRequest.withBody(Json.toJson(EmailAddress("test.com"))))
      status(result) shouldBe Status.BAD_REQUEST
      (contentAsJson(result) \ "message" ).as[String] shouldBe "Enter a valid email address"
    }
  }

  trait SetUp {
    implicit val writes: OWrites[EmailAddress] = Json.writes[EmailAddress]
    case class EmailAddress(email: String)
    object EmailAddress {
      implicit val format = Json.format[EmailAddress]
    }
  }
}