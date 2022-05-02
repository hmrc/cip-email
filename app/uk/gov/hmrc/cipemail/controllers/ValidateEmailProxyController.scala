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
import play.api.libs.json.{JsValue, __}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import uk.gov.hmrc.cipemail.service.ValidateEmailProxyService

import javax.inject.{Inject, Singleton}

@Singleton
class ValidateEmailProxyController @Inject()(cc: ControllerComponents,
                                             validateEmailProxyService: ValidateEmailProxyService)
    extends AbstractController(cc) {

  private val logger = LoggerFactory.getLogger(getClass)
/*

  implicit def Response2Result(response: Future[WSResponse]): Future[Result] = {
    response map {
      response =>
        val headers = response.allHeaders map {
          h => (h._1, h._2.head)
        }
        val result = Result(ResponseHeader(response.status, headers), HttpEntity.Strict(ByteString(response.body), Some("application/json")))
        result
    }
  }

  def validateFormat(): Action[JsValue] = Action(parse.json).async { implicit request =>
    val futureWrapper = scala.concurrent.Future {
      validateEmailProxyService.callCipValidateEmailEndpoint(request)
    }
    futureWrapper match {
      case response: WSResponse => {
        val futureWrapperForWSResponse = scala.concurrent.Future {
          response
        }
        val result = Response2Result(futureWrapperForWSResponse)
        result
      }
    }
  }
*/

  def validateFormat(): Action[JsValue] = Action(parse.json).async { implicit request =>
    logger.debug("cip-email: validating email")
    validateEmailProxyService.callCipValidateEmailEndpoint(request)
  }

}
