package sample.persistence.database

import com.common.mongodb.{Converters, MongodbComponent}
import com.mongodb.casbah.commons.MongoDBObject
import Converters._
import sample.persistence.account_states.StateAccount

import scala.util.Try
case class StateAccountDAO(
                                     mongoHost: String,
                                     mongoPort: Int,
                                     db: String) extends AbsStateAccountDAO with MongodbComponent{

  import sample.persistence.account_states.StateAccount._


  lazy val state_accounts = database("state_accounts")

  override def getAll: Stream[StateAccount] = {
    state_accounts.find().toStream.map(to[StateAccount].apply)
  }

    def create(stateAcc: StateAccount): Try[Unit] = Try {
    val bulk = state_accounts.initializeOrderedBulkOperation
    bulk.insert(dbObject[StateAccount].apply(stateAcc))
    require(bulk.execute().isAcknowledged)
  }

   def update(stateAcc: StateAccount): Try[Unit] = ???

   def remove(idAccount: Id): Try[Unit] = Try {
    val bulk = state_accounts.initializeOrderedBulkOperation
    bulk.find(MongoDBObject("id" -> idAccount.value)).remove()
    require(bulk.execute().isAcknowledged)
  }

   def get(idAcc: Id): Try[StateAccount] = Try {
    state_accounts.findOne(MongoDBObject(
      "id" ->idAcc.value )).toStream.map(to[StateAccount].apply).head
  }

   def get(
                    page: Int,
                    pageSize: Int,
                    sortField: Option[String],
                    sortAsc: Option[Boolean]): Try[List[StateAccount]] = ???

    def getLatest: Try[StateAccount] = Try {
      state_accounts.find()
        .sort(MongoDBObject("date" -> -1))
        .map(obj => to[StateAccount].apply(obj))
        .toStream.head
    }
  override def latest[U](sorting: StateAccount => U)(implicit o: Ordering[U]): Try[StateAccount] = ???

}



trait AbsStateAccountDAO extends DAO[StateAccount] {


}
object StateAccountDAO {

  val IdAccount = "IdCampaign"
  val Amount = "Amount"
  val Date = "Date"

}
