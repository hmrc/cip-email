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
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import uk.gov.hmrc.cipemail.config.AppConfig
import uk.gov.hmrc.cipemail.service.ValidateEmailProxyService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

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

  // TODO - ADD IN ERROR HANDLING
  def validateFormat(): Action[JsValue] = Action(parse.json).async { implicit request =>
    val futureWrapper = scala.concurrent.Future { validateEmailProxyService.callCipValidateEmailEndpoint() }
    futureWrapper.map(i => Ok("Got result: " + i))

   /* validateEmailProxyService.callCipValidateEmailEndpoint().map {
      case resp: WSResponse if resp.getStatus == 200 => Ok
      case _ => BadRequest("")
    }
*/
  }

}
