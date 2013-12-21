Android-IAB
===========

Google play In-App Billing example.

If you want to sell something in your app through google play, this may help you.

To deploy this app,
Step 1. You must need a google developer account, it may take you 25 US dollars.
Step 2. You must upload the apk file of this app to google developer console and do not publish.
Step 3. You must need to regist a google checkout merchant account.
Step 4. Adding three item ids under the app detail, sku_song1, sku_song2, sku_song3.
Step 5. Adding the public license key in InAppBillingTest\src\edu\fcu\selab\inappbillingtest\MainActivity.java, you can find the public license key under the app detail in developer console. 

Now, the seller is ready. We need the buyer as well.

Step 1. Prepare another google account that differ from the seller.
Step 2. Seller add the buyer's google account to the debugger list in developer console.
Step 3. Buyer need to have a credit card in his/her google wallet account.

After all of above steps, you can test the In-App Billing through google play without the payment in actually.
