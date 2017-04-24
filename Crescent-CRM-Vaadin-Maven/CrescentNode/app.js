const login = require("facebook-chat-api");
const fs = require('fs');
var prompt = require ('prompt');
var net = require ('net');
//var client = net.connect(3002, 'localhost');
var FB = require('fb');

	/**********************************************************
	* Nodejs Server info
	*
    **********************************************************/
const hostname = '0.0.0.0';//0.0.0.0 will listen on all devices
const port = 3000;


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


/*server.listen(function (err, data) {
	if(err) return console.error(err);
	console.log('Waiting for threadID at Port: ' + threadIDPort);
	//JSON.stringify(data);
	threadID = JSON.stringify(data);
});*/





/**********************************************************
* 1.) Creates a listen instance, assigns random port.
* 
* 2.) Splits string into a size 2 array at a trigger point
* ":" and "/"(for user/pass). 
* 
* 3.) Reads code before ":" and performs a function
* assigned to that code.
*
**********************************************************/
var user;
var password;
var userHolder;
var passHolder;



net.createServer(function(socket) { //Start server, create socket variable


  console.log('CONNECTED: ' + socket.remoteAddress +':'+ socket.remotePort); //IP and Port auto-assigned

  var senderIp = socket.remoteAddress;

  socket.on('data', function(data) {
	  console.log("userHolder after socketon " + userHolder);
	  // data was received in the socket 
	  // Writes the received message back to the socket (echo)
	  //socket.write(data);
	  var code = '';
	  code = data.toString('utf8');
	  console.log(data.toString('utf8'));
	  
	  var string = code;
	  var array = string.split(":");
	  
	  //console.log(array[0]);
	  //console.log(array.length);
	  
	  
	  /***********************************************************************************************
	   * For some reason array[0] is still reading as login1 instead of threadID
	   * even though the console.log above this  (console.log(data.toString('utf8)); is
	   * spitting out the correct string (threadID:1234567).
	   **********************************************************************************************/
	  if(array[0] == "login1"){
		  //TODO make logging in a function
		  var holder = array[1];
		  var credentialsArray = holder.split("/"); //split user/pass
		  user = credentialsArray[0];
		  password = credentialsArray[1];
		  userHolder = user.replace(/(?:\r\n|\r|\n)/g, '')//removes new line characters;
		  passHolder = password.replace(/(?:\r\n|\r|\n)/g, '');//removes new line characters
		  
		  console.log(userHolder);
		  console.log(passHolder);
		  console.log("userHolder before first login " + userHolder);
		  login({email: userHolder, password: passHolder}, function callback (err, api) {
			    if(err) return console.error(err);

			    api.setOptions({
			      logLevel: "silent" //Turns off messageID notification
			    });


			    api.getThreadList(0, null, 'inbox', function(err, arr){
			    	if(err) return console.error(err);
			    	
			    	var javaC = net.connect(3002, senderIp);
			    	var buffer = new Buffer(JSON.stringify(arr))
			    	
			    	javaC.write(buffer);
			    	//console.log(arr);
			    	javaC.end();
			    	
			    	
			    }); 
			    
			   
			    
			    
			/*api.getFriendsList((err, data) => {
			        if(err) return console.error(err);
			       
			        var client = net.connect(3002, 'localhost');
			        let buffer1 = new Buffer(JSON.stringify(data));
			        
			        client.write(buffer1);
			        console.log(data);
			        client.end();
			    });*/
		  });
		  console.log(userHolder);
		  
		  
		  /**************************************************************************
		   * This is what we want for the second connection. It SHOULD be threadID
		   * and not login1. We just need to figure out why this is going wrong
		   *************************************************************************/
	  }else if(array[0] == "threadID"){ 
		  console.log("hi this is working");
		  console.log("userHolder after Hi this is working " + userHolder);
		  
		  
		  
		  login({email: userHolder, password: passHolder}, function callback (err, api) {
			    if(err) return console.error("ThreadID ERROR: " + err);

			    console.log("Logged into: " + userHolder);
			    api.setOptions({
			      logLevel: "silent" //Turns off messageID notification
			    });
			    
			
		  var threadID = array[1];
		  var threadIDClean = threadID.replace(/(?:\r\n|\r|\n)/g, '');
		  
		  console.log("thread id parsed: " + threadIDClean);
		  
			  api.getThreadHistory(threadIDClean, 0, 5, null, function(err, history){
		          if (err) throw err;

		          console.log("sending data from thread: " + threadIDClean);
		          
		          var javaC2 = net.connect(3002, senderIp);
		          var buffer2 = new Buffer(JSON.stringify(history))
		          javaC2.write(buffer2);
		          javaC2.end();
		          
		          //console.log(history);
		})// end of get history
		  
		  }) // end of login
	  
	  }
console.log("userHolder after else if " +userHolder);
	  
  });  
  
  function funcThreadID(element) {
	  return element = "threadID";
	}
  // Add a 'close' - "event handler" in this socket instance
  socket.on('close', function(data) {
	  // closed connection
	  console.log('CLOSED: ' + socket.remoteAddress +' '+ socket.remotePort);

  });
 

}).listen(port, hostname);

console.log('Server listening on ' + hostname +':'+ port);




/******************************************************************************************************************
* Everything below this point will be either:
* 
* 1.) Moved to above
* 
* 2.) Heavily edited
* 
* 3.) Deleted
*
******************************************************************************************************************/
prompt.start();
prompt.get(['email', 'password'], function (err, result) {
  console.log('Login Successful!');




	/**********************************************************
	* Logs into Facebook using previously povided credentials
	*
    **********************************************************/
login({email: result.email, password: result.password}, function callback (err, api) {
    if(err) return console.error(err);

    api.setOptions({
      logLevel: "silent" //Turns off messageID notification
    });


api.getFriendsList((err, data) => {
        if(err) return console.error(err);

        fs.writeFile('friendinfo.txt', JSON.stringify(data), (err) => { //Writes retrieved data to a file
              	if(err) throw err;
                console.log('It\'s saved!');

            	});

        console.log(data);
    });

/*api.getThreadHistory(threadID, 0, 5, null, function(err, history){
            if (err) throw err;

            	console.log(history);
            	fs.writeFile(fileName, JSON.stringify(history), (err) => { //Writes retieved data to a file
              	if(err) throw err;
                console.log('It\'s saved!');

            	});
})*/

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

   var clientID = getClientID();
/*******************************************************
 *event.threadID
 *******************************************************/
api.getThreadHistory(clientID, 0, 5, null, function(err, history){
            if (err) throw err;


            	console.log(history);
            	fs.writeFile(fileName, JSON.stringify(history), (err) => { //Writes retrieved data to a file
              	if(err) throw err;
                console.log('It\'s saved!');

            	});
})
            //sendPacket(fileName);

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
	function getClientID(){
	    var threadID = '';
	    server.listen(function (err, data) {
		if(err) return console.error(err);
		threadID = Stringify(data);
	    });	
		return threadID;	    
};

	function getUser(){
		var getUser = ' ';
		server.listen(function(err, user){
			if(err) return console.error(err);
			getUser = Stringify(user);
		})
		return getUser;
	};
	
	function getPass(){
		var getPass = ' ';
		server.listen(function(err, pass){
			if(err) return console.error(err);
			getPass = Stringify(pass);
		})
		return getPass
	};
});
});