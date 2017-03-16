var extension = require('ExtendToken');

/**********************************************************
	* Retrieve Facebook Application Token
	*
    **********************************************************/

FB.api('oauth/access_token', {
    client_id: 'app_id',
    client_secret: 'app_secret',
    redirect_uri: 'http://yoururl.com/callback',
    code: 'code'
}, function (err) {
    if(!err || err.error) {
        console.log(!err ? 'error occurred' : err.error);
        return;
    }
 
    var accessToken = err.access_token;
    var expires = err.expires ? err.expires : 0;
});

/**********************************************************
	* This is optional and its use will depend on
	* future code. Extracts and exchanges code from
	* URL.
	*
    **********************************************************/
/*var url = require('url');
 
var urlToParse = 'http://yoururl.com/callback?code=.....#_=_';
var result = url.parse(urlToParse, true);
if(result.query.error) {
    if(result.query.error_description) {
        console.log(result.query.error_description);
    } else {
        console.log(result.query.error);
    }
    return;
} else if (!result.query.code) {
    console.log('not a oauth callback');
    return;
}
var code = result.query.code;

FB.api('oauth/access_token', {
    client_id: 'app_id',
    client_secret: 'app_secret',
    redirect_uri: 'http://yoururl.com/callback',
    code: 'code'
}, function (res) {
    if(!res || res.error) {
        console.log(!res ? 'error occurred' : res.error);
        return;
    }
 
    var accessToken = res.access_token;
    var expires = res.expires ? res.expires : 0;
});*/