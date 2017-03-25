var net = require ('net');

const hostname = '127.0.0.1';
const credsPort = 3002;

  net.createServer(function(sock){
  	console.log('Connected: ' + sock.remoteAddress + ':' + sock.remotePort);
  	sock.on('data', function(data){
  		sock.write(data);
  	});
  	sock.on('close', function(data){
  		console.log('Closed: ' + sock.remoteAddress +' ' + sock.remotePort);
  	});
  }).listen(credsPort, hostname);

  console.log('Server listening on ' + hostname + ':' + credsPort); 