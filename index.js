// content of index.js
const http = require('http')
const port = 3000

const requestHandler = (request, response) => {
  console.log(request.url)
  console.log(request)
  console.log(request.body)
  console.log("**********************")
  response.end("msg")
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})