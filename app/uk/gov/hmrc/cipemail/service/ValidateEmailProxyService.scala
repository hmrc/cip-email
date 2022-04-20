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

import play.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.cipemail.config.AppConfig

import java.util.concurrent.CompletionStage
import javax.inject.Inject

class ValidateEmailProxyService @Inject()(config: AppConfig,
                                          ws: WSClient) {

  // TODO - PUT IN CONFIG FOR ACTUAL POST BODY
  def callCipValidateEmailEndpoint(): CompletionStage[WSResponse] = {
    val result: CompletionStage[WSResponse] = ws.url(config.cipValidateEmailEndpoint).post("content")
    result
  }

}
