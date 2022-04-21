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

import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsObject, JsValue, Json, OWrites, Writes, __}
import play.api.libs.ws.WSResponse
import play.api.mvc.{AbstractController, Action, ControllerComponents, Result}
import uk.gov.hmrc.cipemail.config.AppConfig
import uk.gov.hmrc.cipemail.service.ValidateEmailProxyService

import java.util.concurrent.CompletionStage
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, future}

@Singleton
class ValidateEmailProxyController @Inject()(cc: ControllerComponents,
                                             config: AppConfig,
                                             ws: play.api.libs.ws.WSClient,
                                             validateEmailProxyService: ValidateEmailProxyService)
    extends AbstractController(cc) {

  private val logger = LoggerFactory.getLogger(getClass)

/* code in other git repo
 def validateFormat(): Action[JsValue] = Action(parse.json).async { implicit request =>
    withJsonBody[EmailAddress] { _ => Future.successful(Ok) }
  }*/

  /*
  match {
  case d: Double => d
  case _  => "NaN"
}
   */

  // TODO - ADD IN ERROR HANDLING
  def validateFormat(): Action[JsValue] = Action(parse.json).async { implicit request =>
    val futureWrapper = scala.concurrent.Future { validateEmailProxyService.callCipValidateEmailEndpoint(request) }
    futureWrapper
   /* futureWrapper match {
      case response: WSResponse => {
        val responseStatus = response.status
       /* val fResult: Future[Result] = futureWrapper.map { r =>
          r.whenCompleteAsync( w -> w.)
        }
        fResult*/
      /*  if (responseStatus == 200) {
          Future.successful(Ok)
        } else if (responseStatus == 400) {
          Future.successful(BadRequest(cc.messagesApi("error.invalid")(cc.langs.availables.head)))
        } else {
          Future.successful(response)
        }*/

      }
      //{Future[Result] = response.json }//Future.successful(response)
     // case _ => Future.successful(BadRequest(cc.messagesApi("error.invalid")(cc.langs.availables.head)))
    }*/


   /* val fResult: Future[Result] = futureWrapper.map { r =>
      Json.toJson(r)
    }
    fResult*/
  }

}
