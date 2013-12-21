package edu.fcu.selab.inappbillingtest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iab.IabHelper;
import com.android.iab.IabResult;
import com.android.iab.Inventory;
import com.android.iab.Purchase;

public class MainActivity extends Activity {
	ArrayList<String> additionalSkuList;
	Inventory inAppInventory;
	IabHelper mHelper;
	Button btn1;
	Button btn2;
	Button btn3;
	Button btn4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		
		//Google Play Developer Console 產生的 public license key
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj11WmafKWVBNBLu1IFVB6GQhEO8+TZlkjEqkZZuvbfqTNw2CHlB74z4NMIsJZGY/X5lBlzVerT9BLSC3zu0o0kpyh0y3RptNywMv6vdW8L5CrQQ8IjbsOL40tZhtEuO4nqmQe4JM+NmpIfmz5uIMwtiok9afN2uv8ElAlE+EEipC4WKYfMQr0TIroJoNSoPwzdX+jRK38BgjziEGenDBjA9voOSUeFDDWzIPD85caXr2XoBj6D+c9SCeQe6z9d6jtnfkv2Fw87jlEtHT7q6vITcwbIXPtukDtClJtsT7E3JU1Vu8cew9SIY6RbtZBq5DHmQVF1PjS0gfTrirr0ItrwIDAQAB";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override //初始化
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d("iab", "Problem setting up In-app Billing: " + result);
				}
				// 要查詢的商品清單，編號在Google Play Developer Console定義
				additionalSkuList = new ArrayList<String>();
				additionalSkuList.add("sku_song1");
				additionalSkuList.add("sku_song2");
				additionalSkuList.add("sku_song3");
				// 查詢商品資訊
				mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
			}
		});
		// --------------------------------------------------------
		btn1.setOnClickListener(purchaseListenerA);
		btn2.setOnClickListener(purchaseListenerB);
		btn3.setOnClickListener(purchaseListenerC);
		btn4.setOnClickListener(refundListener);
	}

	IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override // 查詢商品資訊後與UI做同步的動作
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if (result.isFailure()) {
				Log.d("iab", "Problem setting up In-app Billing: " + result);
				return;
			}
			inAppInventory = inventory; // 同步Google與App的商品資訊
			// 查詢價格，其他也可查詢名稱等等...
			String songA_price = inventory.getSkuDetails("sku_song1").getPrice();
			String songB_price = inventory.getSkuDetails("sku_song2").getPrice();
			String songC_price = inventory.getSkuDetails("sku_song3").getPrice();
			TextView tv4 = (TextView) findViewById(R.id.textView4);
			TextView tv5 = (TextView) findViewById(R.id.textView5);
			TextView tv6 = (TextView) findViewById(R.id.textView6);
			tv4.setText("Price: " + songA_price);
			tv5.setText("Price: " + songB_price);
			tv6.setText("Price: " + songC_price);
			
			// 判斷該商品是否已被使用者購買
			boolean isSongAPurchased = inventory.hasPurchase("sku_song1");
			boolean isSongBPurchased = inventory.hasPurchase("sku_song2");
			boolean isSongCPurchased = inventory.hasPurchase("sku_song3");
			// update UI accordingly
			if (isSongAPurchased) {
				btn1.setText("Purchased");
				btn1.setEnabled(false);
			}
			if (isSongBPurchased) {
				btn2.setText("Purchased");
				btn2.setEnabled(false);
			}
			if (isSongCPurchased) {
				btn3.setText("Purchased");
				btn3.setEnabled(false);
			}
		}
	};

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override //當使用者購買商品之後，更新UI
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			if (result.isFailure()) {
				Log.d("iab", "Error purchasing: " + result);
				return;
			} else if (info.getSku().equals("sku_song1")) {
				btn1.setText("Purchased");
				btn1.setEnabled(false);
			} else if (info.getSku().equals("sku_song2")) {
				btn2.setText("Purchased");
				btn2.setEnabled(false);
			} else if (info.getSku().equals("sku_song3")) {
				btn3.setText("Purchased");
				btn3.setEnabled(false);
			}
			//同步Google與App內商品的資訊
			mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (result.isSuccess()) {
				btn1.setText("Buy");
				btn1.setEnabled(true);
			} else {
				Log.d("iab", "Error Consuming: " + result);
			}
		}
	};

	OnClickListener purchaseListenerA = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 購買商品
			mHelper.launchPurchaseFlow(MainActivity.this, "sku_song1", 10001,
					mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
		}
	};
	OnClickListener purchaseListenerB = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 購買商品
			mHelper.launchPurchaseFlow(MainActivity.this, "sku_song2", 10002,
					mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
		}
	};
	OnClickListener purchaseListenerC = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 購買商品
			mHelper.launchPurchaseFlow(MainActivity.this, "sku_song3", 10003,
					mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
		}
	};
	OnClickListener refundListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 註銷使用者對該商品的擁有權
			if(inAppInventory.hasPurchase("sku_song1")){
				mHelper.consumeAsync(inAppInventory.getPurchase("sku_song1"), mConsumeFinishedListener);
			} else {				
				Toast.makeText(MainActivity.this, "You don't have this item", Toast.LENGTH_SHORT).show();
			}
			
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("iab", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null) return;
		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d("iab", "onActivityResult handled by IABUtil.");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
