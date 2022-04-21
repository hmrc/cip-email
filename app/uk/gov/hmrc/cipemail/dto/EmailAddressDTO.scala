package uk.gov.hmrc.cipemail.dto

import play.api.libs.functional.syntax.toApplicativeOps
import play.api.libs.json.{JsPath, Reads}

case class EmailAddressDTO(email: String)

object EmailAddress {
  val MAX_LENGTH = 256

  implicit val emailAddressDTOReads: Reads[EmailAddressDTO] =
    (JsPath \ "email").read[String](Reads.email.keepAnd(Reads.maxLength[String](MAX_LENGTH))).map(EmailAddressDTO.apply)
}
