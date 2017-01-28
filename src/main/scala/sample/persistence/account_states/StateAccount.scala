package sample.persistence.account_states
import spray.json._
case class StateAccount(
                                identifier: String,
                                amount: Int,
                                date: Long)

object StateAccount {

  import DefaultJsonProtocol._

  implicit val format = jsonFormat3(StateAccount.apply)
  type IdAccount = String
  type Amount = String
  type  Identifier = String

}
