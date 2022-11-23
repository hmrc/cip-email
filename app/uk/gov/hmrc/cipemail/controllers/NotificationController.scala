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

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cipemail.connectors.VerifyConnector
import uk.gov.hmrc.cipemail.metrics.MetricsService
import uk.gov.hmrc.cipemail.models.api.ErrorResponse
import uk.gov.hmrc.cipemail.models.api.ErrorResponse.{Codes, Message}
import uk.gov.hmrc.cipemail.utils.ResultBuilder.processHttpResponse
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton()
class NotificationController @Inject()(cc: ControllerComponents, verifyConnector: VerifyConnector, metricsService: MetricsService )
                                      (implicit executionContext: ExecutionContext)
  extends BackendController(cc)
  with Logging {

  def status(notificationId: String): Action[AnyContent] = Action.async { implicit request =>
    callVerificationService(notificationId)
  }

  private def callVerificationService(notificationId: String)(implicit hc: HeaderCarrier): Future[Result] = {
    verifyConnector.callCheckStatusEndpoint(notificationId).transformWith {
      case Success(response) => Future.successful(processHttpResponse(response))
      case Failure(e) =>
        metricsService.recordMetric("cip-notification-status-failure")
        logger.error(s"An unexpected error has occurred")
        Future.successful(GatewayTimeout(Json.toJson(ErrorResponse(Codes.SERVER_CURRENTLY_UNAVAILABLE.id, Message.SERVER_CURRENTLY_UNAVAILABLE))))
    }
  }
}
