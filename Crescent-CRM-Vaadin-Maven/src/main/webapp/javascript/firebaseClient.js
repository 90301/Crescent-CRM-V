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