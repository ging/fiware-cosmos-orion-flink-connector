package org.fiware.cosmos.orion.flink.connector

package object simulatedNotificationLD {
  final val notificationVal =
    """{
       "data": [
         {
          "id": "R1",
          "type": "Node",
          "co": {"type": "Property","value": 0,"unitCode": ""},
          "co2": {"type": "Property","value": 0,"unitCode": ""},
          "humidity": {"type": "Property","value": 40,"unitCode": ""},
          "pressure": {"type": "Property","value": 40,"unitCode": ""},
          "temperature": {"type": "Property","value": 20,"unitCode": ""},
          "wind_speed": {"type": "Property","value": 1.06,"unitCode": ""},
           "@context": [
             "https://schema.lab.fiware.org/ld/context",
             "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
           ]
          }
       ],
       "subscriptionId": "57458eb60962ef754e7c0998"
     }""".stripMargin
  final val defaultTemperature = 20
  final val defaultPressure = 40

  def notification(temperature: Float = defaultTemperature, pressure: Float = defaultPressure) : String = {
    s"""{
        "data": [
           {
            "id": "R1",
            "type": "Node",
            "co": {"type": "Property","value": 0,"unitCode": ""},
            "co2": {"type": "Property","value": 0,"unitCode": ""},
            "humidity": {"type": "Property","value": 40,"unitCode": ""},
            "pressure": {"type": "Property","value": ${pressure},"unitCode": ""},
            "temperature": {"type": "Property","value": ${temperature},"unitCode": ""},
            "wind_speed": {"type": "Property","value": 1.06,"unitCode": ""},
            "@context": [
                  "https://schema.lab.fiware.org/ld/context",
                  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
             ]
            }
        ],
        "subscriptionId": "57458eb60962ef754e7c0998"
        }""".stripMargin

  }
  var maxTempVal = 0.0
  var maxPresVal = 0.0
}

