package com.common.akka

import akka.actor.Actor
import AggregateActor.GetState

/**
  * An [[AggregateActor]] knows how to
  * update certain state given an event.
  */
trait AggregateActor[S] extends Actor {

  type StateUpdate = PartialFunction[(S, Any),S]

  var state: S

  val onEvent: StateUpdate

  override def receive = {
    case GetState => sender ! state
    case event =>
      state = onEvent.orElse[(S, Any),S]{
        case other =>
          unhandled(other)
          state
      }.apply(state -> event)
  }

}

object AggregateActor {

  /** Message to be sent when */
  case object GetState

}
