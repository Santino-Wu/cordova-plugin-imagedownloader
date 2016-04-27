#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>
#import "ImageDownloader.h"

@implementation ImageDownloader

NSString* const SUCCESS_MESSAGE = @"success";
NSString* const FAILURE_MESSAGE = @"failure";

- (void) download:(CDVInvokedUrlCommand*)command
{
    _callbackId = command.callbackId;

    [self.commandDelegate runInBackground:^{
        @try {
            NSString* url = [command.arguments objectAtIndex:0];
            NSString* encodedUrl = [url
                stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];

            NSLog(@"Downloading image from URL: %@", url);

            NSURL* imageUrl = [NSURL URLWithString:encodedUrl];
            NSData* imageData = [NSData dataWithContentsOfURL:imageUrl];
            UIImage* image = [UIImage imageWithData:imageData];
            UIImageWriteToSavedPhotosAlbum(image,
                self,
                @selector(image:didFinishSavingWithError:contextInfo:), nil);
        } @catch (NSException *exception) {
            CDVPluginResult* pluginResult = [CDVPluginResult
                resultWithStatus:CDVCommandStatus_ERROR
                messageAsString:FAILURE_MESSAGE];

            [self.commandDelegate
                sendPluginResult:pluginResult
                callbackId:_callbackId];
        }
    }];
}

- (void) image:(UIImage *)image didFinishSavingWithError:(NSError *)error
    contextInfo:(void *)contextInfo
{
    CDVPluginResult* pluginResult = nil;

    if (error != nil)
    {
        NSLog(@"Fail to save photo into album: %@", error);

		pluginResult = [CDVPluginResult
            resultWithStatus:CDVCommandStatus_ERROR
            messageAsString:FAILURE_MESSAGE];
    }
    else
    {
        NSLog(@"Photo was saved into album");

		pluginResult = [CDVPluginResult
            resultWithStatus:CDVCommandStatus_OK
            messageAsString:SUCCESS_MESSAGE];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
}

@end
