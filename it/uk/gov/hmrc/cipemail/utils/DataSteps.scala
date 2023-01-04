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

package uk.gov.hmrc.cipemail.utils


import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.cipemail.models.EmailAndPasscode
import uk.gov.hmrc.mongo.cache.DataKey
import uk.gov.hmrc.mongo.play.PlayMongoModule

import scala.concurrent.Future

trait DataSteps {
  this: GuiceOneServerPerSuite =>

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .bindings(new PlayMongoModule)
      .configure("mongodb.uri" -> "mongodb://localhost:27017/cip-email-verification")
      .configure("cache.expiry" -> 1)
      .build()

  private val repository = app.injector.instanceOf[PasscodeCacheRepository]

  protected val wsClient: WSClient = app.injector.instanceOf[WSClient]
  protected val baseUrl = s"http://localhost:$port"

  //mimics user reading email
  def retrievePasscode(email: String): Future[Option[EmailAndPasscode]] = {
    repository.get[EmailAndPasscode](email)(DataKey("cip-email-verification"))
  }

  def verify(email: String): Future[WSResponse] = {
    wsClient
      .url(s"$baseUrl/customer-insight-platform/email/verify")
      .withRequestFilter(AhcCurlRequestLogger())
      .withHttpHeaders(("Authorization", "fake-token"))
      .post(Json.parse {
        s"""{"email": "$email"}""".stripMargin
      })
  }
}
