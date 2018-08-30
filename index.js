// content of index.js
const http = require('http')
const port = 3000
const { parse } = require('querystring');

const requestHandler = (req , res) => {
  console.log("**********************")
  if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => {
          body += chunk.toString();
      });
      req.on('end', () => {
          let obj = JSON.parse(body);
          console.log( "Node with id " + obj.id + " has temperature " + obj.temperature)
          res.end('ok');
      });
  }
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})



var options = {
  host: 'localhost',
  path: '/notify',
  port: '9001',
  method: 'POST',
  headers: {
            'Content-Type': 'application/json',
            'Fiware-Service': 'demo',
            'Fiware-ServicePath': '/test'
        }
};
setInterval(function(){
    try {
        var number1 = Math.floor(Math.random() * 10000) + 1
        var number2 = Math.floor(Math.random() * 10000) + 1
        var temp1 = Math.floor(Math.random() * 60) + 1
        var temp2 = Math.floor(Math.random() * 60) + 1
        var data = JSON.stringify(JSON.parse(`{
            "data": [
                {
                    "id": "R1-76e94a6b18b54404b04650afc7aa46c3",
                    "type": "Node",
                    "co": {"type": "Float","value": 0,"metadata": {}},
                    "co2": {"type": "Float","value": 0,"metadata": {}},
                    "humidity": {"type": "Float","value": 40,"metadata": {}},
                    "pressure": {"type": "Float","value": ${number1},"metadata": {}},
                    "temperature": {"type": "Float","value": ${temp1} ,"metadata": {}},
                    "wind_speed": {"type": "Float","value": 1.06,"metadata": {}}
                },
                {
                    "id": "R2-2323123u2u2398490238402993fc8490",
                    "type": "Node",
                    "co": {"type": "Float","value": 0,"metadata": {}},
                    "co2": {"type": "Float","value": 0,"metadata": {}},
                    "humidity": {"type": "Float","value": 30,"metadata": {}},
                    "pressure": {"type": "Float","value": ${number2},"metadata": {}},
                    "temperature": {"type": "Float","value": ${temp2} ,"metadata": {}},
                    "wind_speed": {"type": "Float","value": 1.06,"metadata": {}}
                }
            ],
            "subscriptionId": "57458eb60962ef754e7c0998"
        }`))
        var req = http.request(options, callback);
        //This is the data we are posting, it needs to be a string or a buffer
        req.write(data);
        req.end();
        req.on('error', e=>{
        console.error(e)
    })
    } catch( e) {
        console.error(e)
    }

},1000)

callback = function(response) {
  var str = ''
  response.on('data', function (chunk) {
    str += chunk;
  });

  response.on('end', function () {
//    console.log(str);
  });
}


