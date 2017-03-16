var login = require("facebook-chat-api");
var fs = require('fs');
var prompt = require ('prompt');
var net = require ('net');
var client = net.connect(5001, 'localhost');
var FB = require('fb');

// This code is not ready for implementation yet.
//var fbToken = require('FacebookToken');


/*var io  = require('socket.io').listen(5001),
    dl  = require('delivery'),
    fs  = require('fs');*/

var fileName = ("test.txt");
//var fileText = ('');

	/**********************************************************
	* Starts a local node.js server
	*
    **********************************************************/
const http = require('http');

const hostname = '127.0.0.1';
const port = 3000;

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Test\n');
});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});

	/**********************************************************
	* Prompts user for login information
	* This will later be taken out, currently in-use
	* so testing developer doesn't have information saved
	*
    **********************************************************/
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

	/**********************************************************
	* Future functionality allowing for querying of
	* user Facebook data including online status and
	* user activity logs
	*
    **********************************************************/
/*          var queryOnFriends = "SELECT uid, name, online_presence, status FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = '" + console.log('user_id') + "')";
			var queryUserName = "select name, uid from user where uid={0}";

			var query = FB.Data.query(queryOnFriends, console.log('user_id'));
			query.wait(function(rows) {
    			document.getElementById('display').innerHTML =
    			'Your name is ' + rows[0].name;
			});*/

	FB.setAccessToken('access_token');
	FB.api('4', { fields: ['id', 'name'] }, function (res) {
  	if(!res || res.error) {
    	console.log(!res ? 'error occurred' : res.error);
    	return;
  	}
  	console.log(res.id);
  	console.log(res.name);

    var queryOnFriends = "SELECT uid, name, online_presence, status FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = '" + console.log('user_id') + "')";
	var queryUserName = "select name, uid from user where uid={0}";

	var query = FB.Data.query(queryOnFriends, console.log('user_id'));
	query.wait(function(rows) {
    	document.getElementById('display').innerHTML =
    	'Your name is ' + rows[0].name;
	});
});


/**********************************************************
	* Extracts friends list, updates as friends login
	* or logoff. Returns error statuses based on
	* actions taken and whether or not the action is
	* responded to or not (currently only listens to chat).
	* 
	*
    **********************************************************/
FB.setAccessToken('access_token');
 
var extractEtag;

//Takes list of all friends prioritizing those that are currently online
FB.api('', 'post', {
    batch: [
        { method: 'get', relative_url: '4' },
        { method: 'get', relative_url: 'me/friends?limit=50' },
        { method: 'get', relative_url: 'fql?q=' + encodeURIComponent('SELECT uid FROM user WHERE uid=me()' ) }, /* fql */
        { method: 'get', relative_url: 'fql?q=' + encodeURIComponent(JSON.stringify([
                    'SELECT uid FROM user WHERE uid=me()',
                    'SELECT name FROM user WHERE uid=me()'
                ])) }, /* fql multi-query */
        { method: 'get', relative_url: 'fql?q=' + encodeURIComponent(JSON.stringify({
                    id: 'SELECT uid FROM user WHERE uid=me()',
                    name: 'SELECT name FROM user WHERE uid IN (SELECT uid FROM #id)'
                })) }, /* named fql multi-query */
        { method: 'get', relative_url: '4', headers: { 'If-None-Match': '"7de572574f2a822b65ecd9eb8acef8f476e983e1"' } }, /* etags */
        { method: 'get', relative_url: 'me/friends?limit=1', name: 'one-friend' /* , omit_response_on_success: false */ },
        { method: 'get', relative_url: '{result=one-friend:$.data.0.id}/feed?limit=5'}
    ]
},

//
function(res) {
    var res0, res1, res2, res3, res4, res5, res6, res7,
        etag1;
 
    if(!res || res.error) {
        console.log(!res ? 'error occurred' : res.error);
        return;
    }



    res0 = JSON.parse(res[0].body);
    res1 = JSON.parse(res[1].body);
    res2 = JSON.parse(res[2].body);
    res3 = JSON.parse(res[3].body);
    res4 = JSON.parse(res[4].body);
    res5 = res[5].code === 304 ? undefined : JSON.parse(res[5].body);   // special case for not-modified responses 
                                                                        // set res5 as undefined if response wasn't modified. 
    res6 = res[6] === null ? null : JSON.parse(res[6].body);
    res7 = res6 === null ? JSON.parse(res[7].body) : undefined; // set result as undefined if previous dependency failed 
 
    if(res0.error) {
        console.log(res0.error);
    } else {
        console.log('Hi ' + res0.name);
        etag1 = extractETag(res[0]); // use this etag when making the second request. 
        console.log(etag1);
    }
 
    if(res1.error) {
        console.log(res1.error);
    } else {
        console.log(res1);
    }
 
    if(res2.error) {
        console.log(res2.error);
    } else {
        console.log(res2.data);
    }
    if(res3.error) {
        console.log(res3.error);
    } else {
        console.log(res3.data[0].fql_result_set);
        console.log(res3.data[1].fql_result_set);
    }
 
    if(res4.error) {
        console.log(res4.error);
    } else {
        console.log(res4.data[0].fql_result_set);
        console.log(res4.data[0].fql_result_set);
    }
 
    // check if there are any new updates 
    if(typeof res5 !== "undefined") {
        // make sure there was no error 
        if(res5.error) {
            console.log(error);
        } else {
            console.log('new update available');
            console.log(res5);
        }
    }
    else {
        console.log('no updates');
    }
    // check if dependency executed successfully 
    if(res[6] === null) {
        // then check if the result itself doesn't have any errors. 
        if(res7.error) {
            console.log(res7.error);
        } else {
            console.log(res7);
        }
    } else {
        console.log(res6.error);
    }
});
 
extractETag = function(res) {
    var etag, header, headerIndex;
    for(headerIndex in res.headers) {
        header = res.headers[headerIndex];
        if(header.name === 'ETag') {
            etag = header.value;
        }
    }
    return etag;
};
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
});
});