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

package uk.gov.hmrc.cipemail.controllers

import org.mockito.ArgumentMatchers.{any}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Ok}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.cipemail.dto.EmailAddressDTO
import uk.gov.hmrc.cipemail.service.ValidateEmailProxyService

import scala.concurrent.Future
import scala.util.Random

class ValidateEmailProxyControllerSpec extends  AnyWordSpec with Matchers with MockitoSugar with GuiceOneAppPerSuite with BeforeAndAfterEach {

  val expectedErrorMessage: String = "Enter a valid email address"
  private val fakeRequest = FakeRequest()
  private implicit val writes: OWrites[EmailAddressDTO] = Json.writes[EmailAddressDTO]
  private val successResult: Future[Result] = Future.successful(Ok)
  private val errorResult: Future[Result] = Future.successful(BadRequest("Enter a valid email address"))

  private val mockValidateEmailProxyService: ValidateEmailProxyService = mock[ValidateEmailProxyService]
  override lazy val app = new GuiceApplicationBuilder()
    .overrides(bind[ValidateEmailProxyService].toInstance(mockValidateEmailProxyService))
    .build()
  private val controller = app.injector.instanceOf[ValidateEmailProxyController]

  override def beforeEach() {
    when(mockValidateEmailProxyService.callCipValidateEmailEndpoint(any())).thenReturn(errorResult)
  }

  override def afterEach() {
    reset(mockValidateEmailProxyService)
  }

  "POST /" should {
    "return 200 with valid email address" in {
      // override mock in beforeEach
      when(mockValidateEmailProxyService.callCipValidateEmailEndpoint(any())).thenReturn(successResult)
      val result = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO("test@test.com"))))
      status(result) shouldBe OK
    }

    "return 400 with email with no @" in {
      val actual = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO("invalid.email"))))
      status(actual) shouldBe BAD_REQUEST
      val bodyText: String = contentAsString(actual)
      bodyText shouldBe expectedErrorMessage
    }

    "return 400 with email address too long" in {
      val local = s"${Random.alphanumeric.take(248).mkString}"
      val domain = "test"
      val topLevelDomain = "com"
      val email = s"${local}@${domain}.${topLevelDomain}"
      val actual = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO(email))))
      status(actual) shouldBe BAD_REQUEST
      val bodyText: String = contentAsString(actual)
      bodyText shouldBe expectedErrorMessage
    }

    "return 400 with email address with spaces" in {
      val actual = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO("invalid email"))))
      status(actual) shouldBe BAD_REQUEST
      val bodyText: String = contentAsString(actual)
      bodyText shouldBe expectedErrorMessage
    }

    "return 400 with blank email" in {
      val actual = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO(""))))
      status(actual) shouldBe BAD_REQUEST
      val bodyText: String = contentAsString(actual)
      bodyText shouldBe expectedErrorMessage
    }

    "return 400 with blank email with spaces" in {
      val actual = controller.validateFormat()(
        fakeRequest.withBody(Json.toJson(EmailAddressDTO(" "))))
      status(actual) shouldBe BAD_REQUEST
      val bodyText: String = contentAsString(actual)
      bodyText shouldBe expectedErrorMessage
    }
  }

}
