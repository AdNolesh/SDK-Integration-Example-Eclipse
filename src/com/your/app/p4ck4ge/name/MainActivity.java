package com.your.app.p4ck4ge.name;

import com.nolesh.ads.AdNoleshSDK;
import com.nolesh.ads.IInitSDKEvents;
import com.nolesh.ads.NotificationScheduler;
import com.nolesh.ads.OfferwallAdapter;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private static final String TAG = "AdNolesh";
	// Default sample's package name to check if you changed it
    private static final String DEFAULT_PACKAGE_PREFIX = "com.your.app";
	
	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);

		if (getPackageName().startsWith(DEFAULT_PACKAGE_PREFIX)) {
            throw new RuntimeException("Please change the sample's package name!");
        }
		
		if(!checkApiToken(this)){
			throw new RuntimeException("Please check your API token in the Manifest.xml file!");
		}
		
		//Initialize SDK 
		AdNoleshSDK.initialize(this, false);
		AdNoleshSDK.initialize(this, false, new IInitSDKEvents() {	
			@Override
			public void onSuccess() {
				Log.d(TAG, "AdNoleshSDK is initialized");
			}			
			@Override
			public void onError(final String errorMsg) {
				Log.e(TAG, "Error: "+errorMsg);
				MainActivity.this.runOnUiThread(new Runnable() {
					  public void run() {						 
						  Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
					  }
				});
			}
		});
		
		
//		AdNoleshSDK.setPersistentVideoRequestMode(true);
		AdNoleshSDK.enableInterstitials(true);
				
		//Customize offerwall
		OfferwallAdapter.setTitleColor("#ff9900");
		OfferwallAdapter.setDescriptionColor("#77ccff");
		OfferwallAdapter.setBackgroundColor("#ffffff");
		
		//Customizes ad units in the offerwall and wrapped ListAdapter.
//		OfferwallAdapter.customizeAdUnits(R.layout.ad_listview_item, R.id.adIcon, R.id.adTitle, R.id.adDesc);
		
		
		OfferwallAdapter.setHeaderCloseButtonColor("#ff0000");
		OfferwallAdapter.setHeaderTitleColor("#333333");
		OfferwallAdapter.setHeaderBackgroundColors("#35b9ea", "#3290a8");
		
		//Set the handle and arrow color of the hidden offerwall
		OfferwallAdapter.setHandleColor("#33b5e5", "#ffffff");
		
		
		
		//Customize notification
		NotificationScheduler.setTitle(this, "My Awesome App");
		NotificationScheduler.setContentText(this, "Pull it down to see the content");
		
		//Embed hidden offerwall
//		AdNoleshSDK.embedHiddenOfferwall(this);
//		AdNoleshSDK.embedHiddenOfferwall(this, true);
		AdNoleshSDK.embedHiddenOfferwall(this, false, "popular apps");
		
				
		TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

		Tab = (ViewPager) findViewById(R.id.pager);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar = getActionBar();
				actionBar.setSelectedNavigationItem(position);
			}
		});
		Tab.setAdapter(TabAdapter);

		actionBar = getActionBar();
		actionBar.setDisplayOptions(Window.FEATURE_NO_TITLE);
		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {				
			}

			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				
				//close hidden offerwall if it's opened
				AdNoleshSDK.hiddenOfferwallSetState(false, true);
				
				Tab.setCurrentItem(tab.getPosition());				
			}

			@Override
			public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {				
			}
		}; 
		// Add New Tab
		actionBar.addTab(actionBar.newTab().setText(getBaseContext().getResources().getString(R.string.banner_tab)).setTabListener(tabListener));		
		actionBar.addTab(actionBar.newTab().setText(getBaseContext().getResources().getString(R.string.main_tab)).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getBaseContext().getResources().getString(R.string.offerwall_tab)).setTabListener(tabListener));
		
		actionBar.setSelectedNavigationItem(1);		
		
	}

	public class TabPagerAdapter extends FragmentStatePagerAdapter {
	    public TabPagerAdapter(FragmentManager fm) {
	        super(fm);
	        // TODO Auto-generated constructor stub
	    }
	 
	    @Override
	    public Fragment getItem(int i) {
	        switch (i) {
	        case 0:	           
	            return new BannerTab();
	        case 1:	           
	            return new MainTab();
	        case 2:	           
	            return new OfferwallTab();
	        }
	        return null;
	 
	    }
	 
	    @Override
	    public int getCount() {	       
	        return 3; //Number of Tabs
	    }	 
	}
		
	boolean checkApiToken(Context ctx){

        String token = null;

        try {

            ApplicationInfo app = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            if(bundle!=null) {
                token = bundle.getString("com.nolesh.ads.API_TOKEN");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if(token==null) return false;
        if(token.equals("YOUR_API_KEY")) return false;
        
        return true;
    }
	
}
