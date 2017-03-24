/**
 * 
 * Import Script with:
<script src="https://www.gstatic.com/firebasejs/3.7.3/firebase.js"></script>

 */


  // Initialize Firebase
  var config = {
    apiKey: "AIzaSyD5ql23ahqNYACaIM2J1arGsAM2OXCiCQ0",
    authDomain: "styleconnect-87be0.firebaseapp.com",
    databaseURL: "https://styleconnect-87be0.firebaseio.com",
    storageBucket: "styleconnect-87be0.appspot.com",
    messagingSenderId: "576036093363"
  };
  firebase.initializeApp(config);
  
  const messaging = firebase.messaging();
  
  var permissionRequested = false;
  
  function permRequest() {
	  messaging.requestPermission().then(function() {
		  console.log('Notification permission granted.');
		  // TODO(developer): Retrieve an Instance ID token for use with FCM.
		  this.permissionRequested=true;
	  	})
	  	.catch(function(err) {
	  		console.log('Unable to get permission to notify.', err);
	  	})
  }
  
  function tokenAquire() {
	  // Get Instance ID token. Initially this makes a network call, once retrieved
	  // subsequent calls to getToken will return from cache.
	  messaging.getToken()
	  .then(function(currentToken) {
	    if (currentToken) {
	      serverTokenFunction(currentToken);
	    } else {
	      // Show permission request.
	      console.log('No Instance ID token available. Request permission to generate one.');
	      
	    }
	  })
	  .catch(function(err) {
	    console.log('An error occurred while retrieving token. ', err);
	    showToken('Error retrieving Instance ID token. ', err);
	    setTokenSentToServer(false);
	  });
	}