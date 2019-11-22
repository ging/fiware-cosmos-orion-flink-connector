package org.fiware.cosmos.orion.flink.connector

/**
  * NgsiEvent
  * @param creationTime Time the event was received (milliseconds since 1970)
  * @param fiwareService FIWARE service header
  * @param fiwareServicePath FIWARE service path header
  * @param entities List of entities
  * @param subscriptionId Subscription id to the Context Broker
  */
case class NgsiEvent(creationTime: Long, fiwareService: String ,fiwareServicePath: String, entities: Seq[Entity], subscriptionId: String) extends scala.Serializable

/**
  * NgsiEventLD
  * @param creationTime Time the event was received (milliseconds since 1970)
  * @param fiwareService FIWARE service header
  * @param fiwareServicePath FIWARE service path header
  * @param entities List of entities
  * @param subscriptionId Subscription id to the Context Broker
  */
case class NgsiEventLD(creationTime: Long, fiwareService: String ,fiwareServicePath: String, entities: Seq[EntityLD], subscriptionId: String) extends scala.Serializable

/**
  * Entity
  * @param id Identification of the entity
  * @param `type` Entity type
  * @param attrs List of attributes
  */
case class Entity(id: String,  `type`: String, attrs: Map[String, Attribute]) extends scala.Serializable

/**
  * Entity
  *
  * @param id     Identification of the entity
  * @param `type` Entity type
  * @param context Entity type
  * @param attrs  List of attributes
  */
case class EntityLD(id: String,  `type`: String,context: Any, attrs: Map[String, AttributeLD]) extends scala.Serializable

/**
  * HttpBody
  * @param data Data array
  * @param subscriptionId Subscription id
  */
case class HttpBody( data: Seq[Map[String,Any]], subscriptionId: String ) extends scala.Serializable

/**
  * Attribute of an entity
  * @param `type` Type of attribute
  * @param value Value of the attribute
  * @param metadata Metadata map
  */
case class Attribute(`type`: Any, value: Any, metadata:Any ) extends scala.Serializable

/**
  * Attribute of an entity
  * @param `type` Type of attribute
  * @param value Value of the attribute
  * @param metadata Metadata map
  */
case class AttributeLD(`type`: Any, value: Any, metadata: Map[String,Any] ) extends scala.Serializable


/**
  * Helper for converting a map to an attribute object
  */
object MapToAttributeConverter{

  /**
    * Helper for converting a map to an attribute object
    * @param values Map in which the keys are the attribute names and the values are maps with the type, the metadata and the actual value of the attribute
    * @return Attribute object constructed from the map values
    */
  def unapply(values: Map[String,Any]) : Attribute =  {
     Attribute(
       values.get("type").orNull,
       values.get("value").orNull,
       values.get("metadata").orNull)
  }
  def unapplyLD(values: Map[String,Any]) : AttributeLD =  {
    val metadata = values.filterKeys(x => x != "value" & x!= "type")


    AttributeLD(
      values.get("type").orNull,
      values.get("value").orNull,
      metadata
    )
  }

}

