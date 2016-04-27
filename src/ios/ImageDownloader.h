#import <Cordova/CDVPlugin.h>

@interface ImageDownloader : CDVPlugin
{
    NSString* _callbackId;
}

- (void) download:(CDVInvokedUrlCommand*)command;

@end
