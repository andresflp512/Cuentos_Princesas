package com.renovationapps.cuentosprincesas.activities;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.renovationapps.cuentosprincesas.BuildConfig;
import com.renovationapps.cuentosprincesas.utils.DisplayMetricsHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.gms.ads.InterstitialAd;
import com.renovationapps.cuentosprincesas.Config;
import com.renovationapps.cuentosprincesas.fragments.AboutFragment;
import com.renovationapps.cuentosprincesas.fragments.CategoryFragment;
import com.renovationapps.cuentosprincesas.fragments.MainFragment;
import com.renovationapps.cuentosprincesas.fragments.SearchFragment;
import com.renovationapps.cuentosprincesas.utils.AppController;
import com.renovationapps.cuentosprincesas.utils.DialogExit;
import com.renovationapps.cuentosprincesas.utils.Mcallback;
import com.renovationapps.cuentosprincesas.utils.PrefManager;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import com.renovationapps.cuentosprincesas.R;

import com.renovationapps.cuentosprincesas.fragments.WebViewFragment;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //Create object for FrameLayout and Fragments
    private FrameLayout frmMain;
    private DrawerLayout drawerLayout;
    private MainFragment mainFragment = new MainFragment();
    private CategoryFragment categoryFragment = new CategoryFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private WebViewFragment webViewFragmentAbout = new WebViewFragment();
    private WebViewFragment webViewFragmentContact = new WebViewFragment();
    private AboutFragment aboutAppFragment = new AboutFragment();
    //dav
//    private ProfileFragment profileFragment = new ProfileFragment();
    private PrefManager prefManager;
    String mobileUserLogin;
    RequestQueue rqGetAllAfterLogin;
    String settingVersionCode;
    private InterstitialAd mInterstitialAd;
    public static String login_user_id;
    public static String user_role_id;
    public static String setting_email;
    public static String setting_website;
    TextView tvNavTitle, tvNavSubTitle;
    LinearLayout navLinearLayout;
    ImageView navImageView;
    //dav
    private BottomSheetDialog mBottomSheetDialog;
    private static final String TAG = "MainActivity";


    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Verificacion de seguridad
        /*DialogPackage.show(this, new DialogPackage.ListenerDialog() {
            @Override
            public void OnOk() {
                finish();
            }
            @Override
            public void OnCancel() {
                finish();
            }
        });*/

        // Firebase notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        //dav
        loadInterstitialAd();

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        setting_email = ((AppController) this.getApplication()).getSettingEmail();
        setting_website = ((AppController) this.getApplication()).getSettingWebsite();

        //Access the FrameLayout
        frmMain = (FrameLayout) findViewById(R.id.frmMain);

        //Load Default and Main fragment in MainActivity
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.frmMain, mainFragment);
        transaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Check user login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        //Hide login nav menu if user logged in
        Menu nav_Menu = navigationView.getMenu();
        if (mobileUserLogin != null) { //User is Login
            //Set menu
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_account_upgrade).setVisible(false);
            login_user_id = ((AppController) this.getApplication()).getUserId();
            user_role_id = ((AppController) this.getApplication()).getUserRoleID();


            //Set Navigation Menu title and sub title and background color
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
            tvNavTitle = (TextView) header.findViewById(R.id.navTitle);
            tvNavTitle.setText(((AppController) this.getApplication()).getUserUserName());
            tvNavSubTitle = (TextView) header.findViewById(R.id.tvNavSubTitle);
            tvNavSubTitle.setText(((AppController) this.getApplication()).getUserEmail());
            navImageView = (ImageView) header.findViewById(R.id.navImageView);
            try {
                Glide.with(MainActivity.this)
                        .load(Config.USER_IMG_URL + ((AppController) this.getApplication()).getUserImage())
                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                                .placeholder(R.drawable.pre_loading)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate())
                        .into(navImageView);
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Tag","Error Bitmap # 1");
            }
            //navLinearLayout = (LinearLayout) header.findViewById(R.id.navLinearLayout);
            //navLinearLayout.setBackgroundColor(Color.parseColor(colorPrimary));

        }else { //User Not Login
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_Menu.findItem(R.id.nav_bookmark).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_account_upgrade).setVisible(false);
            login_user_id = "Not Login";
            user_role_id = "Not Login";

            //Set Navigation Menu title and sub title and background color
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
            tvNavTitle = (TextView) header.findViewById(R.id.navTitle);
            tvNavTitle.setText(R.string.app_name);
            tvNavSubTitle = (TextView) header.findViewById(R.id.tvNavSubTitle);
            tvNavSubTitle.setText(R.string.app_sub_name);
            navImageView = (ImageView) header.findViewById(R.id.navImageView);
            try {
                Glide.with(MainActivity.this)
                        .load(R.mipmap.ic_launcher)
                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(115)) //Rounded Image
                                .placeholder(R.drawable.pre_loading)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate())
                        .into(navImageView);
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Tag","Error #2");
            }
        }

        // SUBSCRIBE TOPIC
        FirebaseMessaging.getInstance().subscribeToTopic(Config.FCM_TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // Notifications push firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        fcmUrlReceived(getIntent());


    }

    @Override
    protected void onResume() {
        super.onResume();
        // verify link received
        fcmUrlReceived(getIntent());
    }

    public void fcmUrlReceived(Intent intent){
        String url = intent.getStringExtra("link");
        //System.out.println("REC_URL:"+url);
        if(url != null && !url.equals("")){
            intent.putExtra("link","");
            Intent e = new Intent(Intent.ACTION_VIEW);
            e.setData(Uri.parse(url));
            this.startActivity(e);
        }
    }


    @Override
    public void onBackPressed() {
            //To fix title bug.
            if (mainFragment != null && mainFragment.isVisible()) {
                //dav
//                setTitle(R.string.app_name);
                exitDialogP();
            }else {
                super.onBackPressed();
            }

//        }
    }
    //dav
    private void exitDialog() {
        DialogExit dialogExit = new DialogExit();
        dialogExit.setOnExitClickListener(new DialogExit.OnExitClickListener() {
            @Override
            public void onExit() {
                finish();
            }
        });
        dialogExit.show(getSupportFragmentManager(), "");
    }
    //dav
    public void exitDialogP() {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.dialog_exit_p);
            dialog.getWindow().setLayout(DisplayMetricsHandler.getScreenWidth() - 50, -2);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.shareapp);
            LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.nothnks);

            ((LinearLayout) dialog.findViewById(R.id.rateus)).setOnClickListener(new View.OnClickListener() {
                public void onClick(android.view.View r5) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }

                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(android.view.View r4) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.share_content) + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            });
            linearLayout2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dialog.cancel();
                    //super.onBackPressed();
                    finish();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }


    //==========================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //==========================================================================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //dav
            showBottomSheetDialog();

        }if (id == R.id.action_search) {
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","");
            bundle.putString("showTitle", getString(R.string.menu_search));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment, "SEARCH_FRAGMENT")
                    .addToBackStack(null)
                    .commit();


            //To refresh searchFragment if clicked on the search toolbar again to show search form
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(searchFragment).attach(searchFragment).commit();
                //Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","");
                bundle.putString("showTitle", getString(R.string.menu_search));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }else if(id == R.id.action_filter){
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","");
            bundle.putString("showFilter","1");
            bundle.putString("showTitle", getString(R.string.menu_search));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment, "SEARCH_FRAGMENT")
                    .addToBackStack(null)
                    .commit();


            //To refresh searchFragment if clicked on the search toolbar again to show search form
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(searchFragment).attach(searchFragment).commit();
                //Bundle bundle = new Bundle();
                bundle.putString("showWhichContent","");
                bundle.putString("showFilter","1");
                bundle.putString("showTitle", getString(R.string.menu_search));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    //==========================================================================//
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, mainFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (mainFragment != null && mainFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(mainFragment).attach(mainFragment).commit();
            }

        } else if (id == R.id.nav_category) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            /*Bundle bundle = new Bundle();
            String myMessage = "Armin MSG";
            bundle.putString("message", myMessage);
            categoryFragment.setArguments(bundle);*/

            transaction.replace(R.id.frmMain, categoryFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (categoryFragment != null && categoryFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(categoryFragment).attach(categoryFragment).commit();
            }

        }
        //dav
        else if (id == R.id.nav_recomended){
            Intent intent = new Intent(MainActivity.this, RecomendedActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_bookmark) {
            //Pass data from Fragment to Fragment
            Bundle bundle = new Bundle();
            bundle.putString("showWhichContent","BookmarkContent");
            bundle.putString("showTitle", getString(R.string.nav_bookmark));
            searchFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                    .replace(R.id.frmMain, searchFragment)
                    .addToBackStack(null)
                    .commit();

            //To refresh current fragment if clicked
            if (searchFragment != null && searchFragment.isVisible()) {
                //getSupportFragmentManager().beginTransaction().detach(bookmarkFragment).attach(bookmarkFragment).commit();
                bundle.putString("showWhichContent","BookmarkContent");
                bundle.putString("showTitle", getString(R.string.nav_bookmark));
                searchFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                        .detach(searchFragment)
                        .attach(searchFragment)
                        .addToBackStack(null)
                        .commit();
            }

        } else if (id == R.id.nav_contact) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_contact);
            String theSubTitle = getString(R.string.txt_ways_to_contact_us);
            String theUrl = Config.PAGE_CONTACT_US + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("sub_title", theSubTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        } else if (id == R.id.nav_help) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation

            //Pass variable to fragment
            Bundle bundle = new Bundle();
            String theTitle = getString(R.string.nav_reward_coin);
            String theSubTitle = getString(R.string.txt_how_to_reward_coin);
            String theUrl = Config.PAGE_HELP + "?api_key=" + Config.API_KEY;
            bundle.putString("title", theTitle);
            bundle.putString("sub_title", theSubTitle);
            bundle.putString("url", theUrl);
            webViewFragmentContact.setArguments(bundle);

            transaction.replace(R.id.frmMain, webViewFragmentContact);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (webViewFragmentContact != null && webViewFragmentContact.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(webViewFragmentContact).attach(webViewFragmentContact).commit();
            }

        }
        //dav
//        else if (id == R.id.nav_account_upgrade) {
//            Intent intentUpgrade = new Intent(MainActivity.this, AccountUpgrade.class);
//            startActivity(intentUpgrade);
//
//        }
        else if (id == R.id.nav_about_app) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
            transaction.replace(R.id.frmMain, aboutAppFragment);
            //To support back to previous fragment
            transaction.addToBackStack(null);
            transaction.commit();

            //To refresh current fragment if clicked
            if (aboutAppFragment != null && aboutAppFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().detach(aboutAppFragment).attach(aboutAppFragment).commit();
            }

        } else if (id == R.id.nav_share_app) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.txt_share);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }
        //dav
//        else if (id == R.id.nav_login) {
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//
//        }
//        else if (id == R.id.nav_register) {
//            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
//
//        }
        //dav
//        else if (id == R.id.nav_profile) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
//
//            transaction.replace(R.id.frmMain, profileFragment);
//            //To support back to previous fragment
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//            //To refresh current fragment if clicked
//            if (profileFragment != null && profileFragment.isVisible()) {
//                getSupportFragmentManager().beginTransaction().detach(profileFragment).attach(profileFragment).commit();
//            }
//
//        } else if (id == R.id.nav_logout) {
//            SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
//            prefs.edit().clear().commit();
//            //SharedPreferences users = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
//            //users.edit().clear().commit();
//            Toast.makeText(getApplicationContext(),R.string.txt_logout_successfully,Toast.LENGTH_SHORT).show();
//
//            Intent refresh = new Intent(MainActivity.this, OneSplashActivity.class);
//            startActivity(refresh);
//            finish();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //dav===============================================================================//
    private void loadInterstitialAd(){
        //dav
        try {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
            // Interstitial Ad
            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this, ((AppController) this.getApplication()).getAdmobSettingAppId());
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(((AppController) this.getApplication()).getAdmobSettingInterstitialUnitId());
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //============================================================================//
    //dav
    public void interShow(Mcallback mcallback) {
        // InterstitialAd
        if (mInterstitialAd.isLoaded()) {
            if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1"))
            {
                if(login_user_id.equals("Not Login")) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            if (mcallback != null) {
                                mcallback.onAction();
                            }
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                    });

                }else{
                    if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
                    {
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                if (mcallback != null) {
                                    mcallback.onAction();
                                }
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        });
                    }else {
                        if (mcallback != null) {
                            mcallback.onAction();
                        }
                    }
                }
            }
        } else {
            if (mcallback != null) {
                mcallback.onAction();
            }
            loadInterstitialAd();
            Log.d("TAG", "The interstitial wasn't loaded yet.");

        }
    }
    //==============================================================================================//
    private void showBottomSheetDialog() {

        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);



        view.findViewById(R.id.btn_recomended).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecomendedActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setting_website));
                startActivity(browserIntent);
                /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit); //Start Animation
                transaction.replace(R.id.frmMain, aboutAppFragment);
                //To support back to previous fragment
                transaction.addToBackStack(null);
                transaction.commit();
                mBottomSheetDialog.cancel();*/



                //To refresh current fragment if clicked
                if (aboutAppFragment != null && aboutAppFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction().detach(aboutAppFragment).attach(aboutAppFragment).commit();
                }
            }
        });
        view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.txt_share);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }
}
