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

import org.slf4j.LoggerFactory
import play.api.http.HttpEntity
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, ResponseHeader, Result}
import play.libs.ws.{WSClient, WSRequest, WSResponse}
import uk.gov.hmrc.cipemail.config.AppConfig

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateEmailProxyService @Inject()(config: AppConfig,
                                          ws: WSClient) {

  private val logger = LoggerFactory.getLogger(getClass)

  def callCipValidateEmailEndpoint(request: Request[JsValue]): Future[Result] = {
    val incomingPayload: JsValue = request.body
    val phoneNumberJsonStr = (incomingPayload \ "email").as[String]

    val requestToCip: WSRequest = ws.url(config.cipValidateEmailEndpoint).addHeader("Accept", "application/json")

    val payload = Json.obj(
      "email" -> phoneNumberJsonStr
    ).toString()

    val futureWrapper: Future[WSResponse] = scala.concurrent.Future {
      requestToCip.post(payload).toCompletableFuture.get()
    }
    convertFutureToFutureResult(futureWrapper)
  }

  private def convertFutureToFutureResult(f: Future[WSResponse]): Future[Result] = {
    f.flatMap { result =>
      Future.successful(Response2Result(result))
    }
      .recoverWith {
        case e: Exception =>
          logger.error(s"Something went wrong, ${e.getMessage}")
          Future.failed(e)
      }
  }

  implicit def FutureResponse2FutureResult(response: Future[WSResponse]): Future[Result] = {
    response map {
      response =>
        Result(ResponseHeader(response.getStatus, Map.empty), HttpEntity.Strict(response.getBodyAsBytes, None))
    }
  }

  implicit def Response2Result(response: WSResponse): Result = {
    Result(ResponseHeader(response.getStatus, Map.empty), HttpEntity.Strict(response.getBodyAsBytes, None))
  }

}
