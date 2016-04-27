cordova-plugin-imagedownloader
==============================

The cordova plugin for downloading image into gallery.

Supported platforms
-------------------

* iOS
* Android

Installation
------------

```
cordova plugin add https://github.com/Santino-Wu/cordova-plugin-imagedownloader.git
```

Usage
-----

Downloads an images from URL.

```js
window.imagedownloader
    .download(
        'http://cordova.apache.org/static/img/cordova_bot.png',
        function successFn() {
            alert('Image was downloaded');
        },
        function failureFn() {
            alert('Fail to download image');
        }
    );
```

License
-------

[MIT](/LICENSE)
