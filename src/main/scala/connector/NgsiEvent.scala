package connector

case class NgsiEvent(creationTime: Long, fiwareService: String ,fiwareServicePath: String, entityType: String,  entityId: String, attrs: Map[String,Attr] ) extends Serializable
case class DataObj( data: Seq[Map[String,Any]], subscriptionId: String ) extends Serializable
case class Attr(`type`: Any, value: Any, metadata:Any ) extends Serializable
