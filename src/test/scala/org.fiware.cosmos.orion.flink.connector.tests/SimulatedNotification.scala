package object simulatedNotification {
final val notificationVal =
  """{
       "data": [
         {
          "id": "R1",
          "type": "Node",
          "co": {"type": "Float","value": 0,"metadata": {}},
          "co2": {"type": "Float","value": 0,"metadata": {}},
          "humidity": {"type": "Float","value": 40,"metadata": {}},
          "pressure": {"type": "Float","value": 40,"metadata": {}},
          "temperature": {"type": "Float","value": 20,"metadata": {}},
          "wind_speed": {"type": "Float","value": 1.06,"metadata": {}}
          }
       ],
       "subscriptionId": "57458eb60962ef754e7c0998"
     }""".stripMargin
final val defaultTemperature = 20
  final val defaultPressure = 40

  def notification(temperature: Float = defaultTemperature, pressure: Float = defaultPressure) : String = {
    """{
        "data": [
           {
            "id": "R1",
            "type": "Node",
            "co": {"type": "Float","value": 0,"metadata": {}},
            "co2": {"type": "Float","value": 0,"metadata": {}},
            "humidity": {"type": "Float","value": 40,"metadata": {}},
            "pressure": {"type": "Float","value": ${pressure},"metadata": {}},
            "temperature": {"type": "Float","value": ${temperature},"metadata": {}},
            "wind_speed": {"type": "Float","value": 1.06,"metadata": {}}
            }
        ],
        "subscriptionId": "57458eb60962ef754e7c0998"
        }""".stripMargin
  }
    def notificationLD(temperature: Float = defaultTemperature, pressure: Float = defaultPressure) : String = {
      """{
        "data": [
           {
            "id": "R1",
            "type": "Node",
            "co": {"type": "Propert","value": 0,"metadata": {}},
            "co2": {"type": "Property","value": 0,"metadata": {}},
            "humidity": {"type": "Property","value": 40,"metadata": {}},
            "pressure": {"type": "Property","value": ${pressure},"metadata": {}},
            "temperature": {"type": "Property","value": ${temperature},"metadata": {}},
            "wind_speed": {"type": "Property","value": 1.06,"metadata": {}},
            "@context": ["https://schema.lab.fiware.org/ld/context","https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"]
            }
        ],
        "subscriptionId": "57458eb60962ef754e7c0998"
        }""".stripMargin

    }

var maxTempVal = 0.0
var maxPresVal = 0.0
};

