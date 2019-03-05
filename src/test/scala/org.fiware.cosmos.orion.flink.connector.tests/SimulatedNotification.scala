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
final val defaultTemperature = 20;
final val defaultPressure = 40;
def notification(temperature: Float = defaultTemperature, pressure: Float = defaultPressure) : String = {
  s"""{
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
var maxTempVal = 0.0
var maxPresVal = 0.0
}
