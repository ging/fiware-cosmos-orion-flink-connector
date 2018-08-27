while true
do 
    temp=$(shuf -i 1-100 -n 1)
    echo $temp
    curl -d '{"temp": '$temp'}' -H "Content-Type: application/json" -X POST http://localhost:9001

    sleep 1
done
