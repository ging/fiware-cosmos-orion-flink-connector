while true
do
    timestamp=$(shuf -i 1-100000000 -n 1)
    temp1=$(shuf -i 18-53 -n 1)
    temp2=$(shuf -i 1-33 -n 1)
    number=$(shuf -i 1-3113 -n 1)
    # echo $temp1
    # echo $temp2
    # echo
    curl -d '{ "data": [{"id": "R1-76e94a6b18b54404b04650afc7aa46c3","type": "Node","co": {"type": "Float","value": 0,"metadata": {}},"co2": {"type": "Float","value": 0,"metadata": {}},"humidity": {"type": "Float","value": 40,"metadata": {}},"pressure": {"type": "Float","value": '$number',"metadata": {}},"temperature": {"type": "Float","value": 305.738,"metadata": {}},"wind_speed": {"type": "Float","value": 1.06,"metadata": {}}} ],"subscriptionId": "57458eb60962ef754e7c0998"}' -H "Content-Type: application/json" -X POST http://localhost:9001
    sleep 1
done
