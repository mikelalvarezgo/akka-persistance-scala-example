package sample.persistence

//#persistent-actor-example
import akka.persistence._
import akka.actor._
import akka.persistence.fsm.PersistentFSM.State
import sample.persistence.account_states.StateAccount
import sample.persistence.database.StateAccountDAO
import org.joda.time.DateTime


sealed trait Command
case class GiveMe(coins: Int) extends Command
case class TakeMy(coins: Int) extends Command
case class SaveMe(timestamp: Long) extends Command
case class SnapMe() extends  Command
case class PrintMe() extends  Command

sealed trait Event
case class BalanceChangedBy(coins: Int) extends Event
case class BalanceSavedWhen(timestamp: Long, coins:Int) extends Event

case class Account(coins: Int) {
  def updated(diff: Int) = Account(coins + diff)
}

case class AccountStateSnapshot(coins:Int, time: Long)

class AccountPersistentActor extends PersistentActor {
  override def persistenceId = "account-id-1"

  val mongoHost = "127.0.0.1"
  val mongoPort = 27017
  val db = "accounts_db"
  val stateAccountDAO: StateAccountDAO = StateAccountDAO(mongoHost, mongoPort, db)

  var state = Account(coins = 0)

  def updateState(event: Event): Account = {
    event match {
      case BalanceChangedBy(coins) => {
        val state_up = state.updated(coins)
        state_up
      }
      case _ => state
    }
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: Account) =>
      println("offered state = " + s)
      state = s
    case BalanceChangedBy(coins) =>
      state = state.updated(coins)
    case BalanceSavedWhen(date, coins) =>
      val stateAccount = StateAccount("f" + date, coins, date)
      stateAccountDAO.create(stateAccount)
    case SnapMe() =>
      saveSnapshot(state)

  }

  def receiveCommand: Receive = {
    case TakeMy(coins) =>
      persist(BalanceChangedBy(coins)) { changed =>
        println("modified state : " + state.coins +"+ "+coins)

        state = updateState(changed)
      }
    case GiveMe(coins) =>
      persist(BalanceChangedBy(-coins)) { changed =>
        println("modified state : " + state.coins +"- "+coins)

        state = updateState(changed)
        sender() ! TakeMy(coins)
      }
    case SaveMe(timestamp) =>
      persist(BalanceSavedWhen(timestamp, state.coins)) { changed =>
        println("saved state = " + state)
        sender() ! SaveMe(timestamp)
      }
    case SnapshotOffer(_, a: Account) =>
      println("offered state = " + a)
      state = a
    case PrintMe() =>
      println(s"State of the account : ${state.coins} coins")

  }

   override def preStart(): Unit = {
    val tState = stateAccountDAO.getLatest
    val latestStateAccount =tState.getOrElse(StateAccount("",0,System.currentTimeMillis()))
   state = Account(latestStateAccount.amount)

  }

  override def postStop(): Unit = {
    val stateAccount = StateAccount("f" + System.currentTimeMillis(), state.coins, System.currentTimeMillis())
    stateAccountDAO.create(stateAccount)
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    context.children foreach { child ⇒
      context.unwatch(child)
      context.stop(child)
    }
    postStop()
  }

  override def postRestart(reason: Throwable): Unit = {
    preStart()
  }
}
//#persistent-actor-example

object PersistentActorExample extends App {

  val system = ActorSystem("example")
  val persistentActor = system.actorOf(Props[AccountPersistentActor], "persistentActor-2-scala")

  println("Initial status of actor")
  persistentActor ! PrintMe()
  persistentActor ! TakeMy(100)
  persistentActor ! SaveMe(System.currentTimeMillis())
  persistentActor ! TakeMy(100)
  persistentActor ! GiveMe(100)
  persistentActor ! SaveMe(System.currentTimeMillis())
  persistentActor ! GiveMe(100)
  persistentActor ! SnapMe
  Thread.sleep(10000)
  persistentActor ! GiveMe(100)
  persistentActor ! SaveMe(System.currentTimeMillis())
  println("Final status of actor")
  persistentActor ! PrintMe()
  persistentActor.tell(PoisonPill.getInstance,ActorRef.noSender)
}
