var login = require("facebook-chat-api");
var fs = require('fs');
var prompt = require ('prompt');
var net = require ('net');
//var client = net.connect(5001, 'localhost');
var FB = require('fb');

// This code is not ready for implementation yet.
//var fbToken = require('FacebookToken');


/*var io  = require('socket.io').listen(5001),
    dl  = require('delivery'),
    fs  = require('fs');*/

var fileName = ("test.txt");

/*var loginFile = process.argv[2];
var user = ('');
var pass = ('');
//var fileText = ('');


if (process.argv.length < 3){
	console.log('Improper Usage, try: node.js ' + process.argv[1] + ' login.txt');
	process.exit(1);
}

var fr = require('fs');


fr.readFile(loginFile, 'utf8', function(err, data) {
	if(err) throw err;
	console.log('Login data successfully retrieved.');	
	user += data;
	var index = user.indexOf(' ');
		user = user.substring(0, index);
		pass = user.substring(index + 5);
		console.log(user);
		console.log(pass);
	
});*/

	/**********************************************************
	* Starts a local node.js server
	*
    **********************************************************/
const http = require('http');

const hostname = '127.0.0.1';
const port = 3000;
//const credsPort = 3002;

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Test\n');
});

  /*net.createServer(function(sock){
  	console.log('Connected: ' + sock.remoteAddress + ':' + sock.remotePort);
  	sock.on('data', function(data){
  		sock.write(data);
  	});
  	sock.on('close', function(data){
  		console.log('Closed: ' + sock.remoteAddress +' ' + sock.remotePort);
  	});
  }).listen(credsPort, hostname);

  console.log('Server listening on ' + hostname + ':' + credsPort);*/

	/**********************************************************
	* Prompts user for login information
	* This will later be taken out, currently in-use
	* so testing developer doesn't have information saved
	*
    **********************************************************/
prompt.start();
prompt.get(['email', 'password'], function (err, result) {
  console.log('Login Successful!');



  
server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});

	/**********************************************************
	* Logs into Facebook using previously povided credentials
	*
    **********************************************************/
login({email: result.email, password: result.password}, function callback (err, api) {
    if(err) return console.error(err);

    api.setOptions({
      logLevel: "silent" //Turns off messageID notification
    });



	/**********************************************************
	* Begins listening for user chat data. 
	* Current functionality only allows for chat history
	* to be retrieved when the user sends/receives a message
	*
    **********************************************************/
    var stopListening = api.listen(function(err, event) {
        if(err) return console.error(err); //Throws error

        switch(event.type) {
          case "message":
            if(event.body === '/stop') { //Ends the script
              	//api.sendMessage("Goodbye...", event.threadID);
              	return stopListening();
            }

            api.markAsRead(event.threadID, function(err) {
              	if(err) console.log(err);
            });


/*******************************************************
 *
 *******************************************************/
api.getThreadHistory(event.threadID, 0, 5, null, function(err, history){
            if (err) throw err;


            	console.log(history);
            	fs.writeFile(fileName, JSON.stringify(history), (err) => { //Writes retieved data to a file
              	if(err) throw err;
                console.log('It\'s saved!');

            	});
})
            sendPacket(fileName);

            break;

          case "event":
            console.log(event);
            break;
        }
    });

	/**********************************************************
	* Saves collected chat data
	*
    **********************************************************/
    function saveData(message, fileName, err){
    	fs.writeFile(fileName, message, (err) =>{
    		if(err) throw err;
    			console.log('Saved to ' + fileName);
    	})
    }

    /**********************************************************
	* Retrieves chat history based on parameters.
	* chatHistory(earliest message, furthest, input)
	*
    **********************************************************/
	function chatHistory(messageStart, messageEnd, event){
    	api.getThreadHistory(event, messageStart, messageEnd, null, function(err, history){
            if (err) throw err;

            for (var j = history.length - 2; j >= 0; j--){

            	console.log(history[j]);
            	fs.writeFile(fileName, history[j] + "\r\n", (err) => { //Writes retieved data to a file
              	if(err) throw err;
                console.log('It\'s saved!');
            });
            }
            console.log(event.body);

        })
    }

	/**********************************************************
	* Sends data packet once requested chat information
	* has been recorded.
	*
	* sendPacket(Recipient IP, Recipient Process port, 
	* Name to Save as, Path to Place in)
	*
    **********************************************************/
	function sendPacket(file){
		client.write(file);
		console.log("Successful");
		client.end();


		/*io.sockets.on('connection', function(socket){
  			var delivery = dl.listen(socket);
  			
  			delivery.on('receive.success',function(file){
    			var params = file.params;
   				
   				fs.writeFile(file.name,file.buffer, function(err){
   					if(err){
       					console.log('File could not be saved.');
   					}else{
     					console.log('File saved.');
   					};
   				});
  			});
		});*/
	};

	function receiveCreds(socket){

  		net.createServer(function(sock){
  			console.log('Connected: ' + sock.remoteAddress + ':' + sock.remotePort);

});	
};
});
});