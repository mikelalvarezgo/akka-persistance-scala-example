package com.common.mongodb

trait MongodbComponent {

  import com.mongodb.casbah.Imports._

  val mongoHost: String
  val mongoPort: Int
  val db: String

  lazy private[wyred] val mongoClient = MongoClient(mongoHost, mongoPort)
  lazy val database = mongoClient(db)

}

object MongoContext {
  type MongoHost = String
  type MongoPort = Int
  type Db = String
  type MongoCtxt = (MongoHost, MongoPort, Db)

}
