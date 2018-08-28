while true
do
    timestamp=$(shuf -i 1-100000000 -n 1)
    temp1=$(shuf -i 18-53 -n 1)
    temp2=$(shuf -i 1-33 -n 1)
    echo $temp1
    echo $temp2
    echo
    curl -d '{ "creationTime": '$timestamp',  "fiwareService": "rooms",  "fiwareServicePath": "unknown",  "timestamp": '$timestamp', "entityType": "room", "entityPattern": "1", "entityId": "1", "attrs": [{"name": "room1", "attType": "temperature", "value": '$temp1'},{"name": "room2", "attType": "temperature", "value": '$temp2'}], "count":2 }' -H "Content-Type: application/json" -X POST http://localhost:9001

    sleep 1
done
