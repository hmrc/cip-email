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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.{BadRequest, Ok}
import play.api.mvc.{Request, Result}
import play.libs.ws.{WSClient, WSRequest, WSResponse}
import uk.gov.hmrc.cipemail.config.AppConfig

import java.util.concurrent.CompletionStage
import javax.inject.Inject
import scala.concurrent.Future

class ValidateEmailProxyService @Inject()(config: AppConfig,
                                          ws: WSClient) {

  def callCipValidateEmailEndpoint(request: Request[JsValue]): Future[Result] = {
    val incomingPayload: JsValue = request.body
    val phoneNumberJsonStr = (incomingPayload \ "email").as[String]

    val requestToCip: WSRequest = ws.url(config.cipValidateEmailEndpoint).addHeader("Accept", "application/json")

    val payload = Json.obj(
      "email" -> phoneNumberJsonStr
    ).toString()

    val future: CompletionStage[WSResponse] = requestToCip.post(payload)
    future.whenComplete { (result, error) => {
      if (result != null && result.getStatus == 200) {
        // Future.successful(Ok)
        val futureToReturn = Future.successful(Ok)
        //futureToReturn
        return futureToReturn
      }
      if (result != null && result.getStatus == 400) {
        //  Future.successful(BadRequest(result.getBody))
        val futureToReturn = Future.successful(BadRequest(result.getBody))
        futureToReturn
        return futureToReturn
      }
      if (error != null) {
        //  Future.failed(error)
        val futureToReturn = Future.failed(error)
        futureToReturn
        return futureToReturn
      }
    }

    }
    val futureToReturn: Future[Result] = Future.failed(new Throwable)
    futureToReturn
  }

}
