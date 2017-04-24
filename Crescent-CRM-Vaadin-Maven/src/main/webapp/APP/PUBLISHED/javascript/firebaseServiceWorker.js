/**
 * Javascript service worker file, for use with Firebase Cloud Messaging.
 */

self.addEventListener('push', function(event) {

var apiPath = '<apiPath>';
event.waitUntil(registration.pushManager.getSubscription().then(function (subscription){

    return fetch(apiPath).then(function(response){
        if(response.status !== 200){
            throw new Error();
        }

        return response.json().then(function(data){
            var title = data.title;
            var message = data.body;
            var icon = data.icon;
            var tag = data.tag;
            var url = data.url;
            return self.registration.showNotification(title,{
               body: message,
               icon: icon,
               tag: tag,
               data: url
            });
        })
    }).catch(function(err){

    })

}));
return;
});