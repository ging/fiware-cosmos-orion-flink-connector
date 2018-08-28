while true
do
    temp=$(shuf -i 1-100 -n 1)
    room=$(shuf -i 1-3 -n 1)
    echo $temp
    curl -d '{"room": "Room'$room'","temp": '$temp'}' -H "Content-Type: application/json" -X POST http://localhost:9001
    sleep 1
done