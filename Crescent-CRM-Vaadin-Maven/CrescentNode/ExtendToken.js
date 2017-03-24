/**********************************************************
    * Extends the expiration time of an access token
    *
    **********************************************************/

FB.api('oauth/access_token', {
    client_id: 'client_id',
    client_secret: 'client_secret',
    grant_type: 'fb_exchange_token', //Grants a new token to be generated and retires the old one
    fb_exchange_token: 'existing_access_token' //Token soon to expire
}, function (err) {
    if(!err || err.error) {
        console.log(!err ? 'error occurred' : err.error);
        return;
    }
 
    var accessToken = err.access_token;
    var expires = err.expires ? err.expires : 0;
});