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