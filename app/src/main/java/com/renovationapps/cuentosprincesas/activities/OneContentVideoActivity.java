package com.renovationapps.cuentosprincesas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.renovationapps.cuentosprincesas.Config;
import com.renovationapps.cuentosprincesas.R;
import com.renovationapps.cuentosprincesas.utils.AppController;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.renovationapps.cuentosprincesas.Config.DIRECTION;

public class OneContentVideoActivity extends AppCompatActivity {
    private String buttonText;
    private String contentId;
    private String userUsername;
    private String contentTitle;
    private String categoryTitle;
    private String contentImage;
    private String contentUrl;
    private String contentDuration;
    private String contentViewed;
    private String contentPublishDate;
    private String contentTypeTitle;
    private String contentTypeId;
    private String contentPlayerTypeId;
    private String contentUserRoleId;
    private String contentOrientation;
    private String contentCached;
    private String urlComplement;
    private WebView wvOneContent;
    Context context = this;
    String contentBookmark;
    String contentDescription;
    String mobileUserLogin;
    String user_role_id;
    String userId;
    String total_rate, row_rate, rate_average;
    ImageView showContent;
    FloatingActionButton floatingActionButtonShowVideo;
    CoordinatorLayout coordinatorLayoutOneContentVideo;
    CardView cardViewAD;
    ImageButton imageButtonAdClose, btnArrowRating;
    CardView lnyComplement;
    TextView textVideo;
    private ProgressWheel progressWheelInterpolated;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu floating_action_menu_one_video;
    Animation show_main_fab;
    RatingBar ratingBarAverage;
    ProgressBar progressBarFiveStar, progressBarFourStar, progressBarThreeStar, progressBarTwoStar, progressBarOneStar;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_add_comment;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_show_comment;
    net.i2p.android.ext.floatingactionbutton.FloatingActionButton fab_bookmark;
    PlayerView playerView;
    SimpleExoPlayer player;

    //For Custom Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_content_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayoutOneContentVideo = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutOneContentVideo);

        ratingBarAverage = (RatingBar) findViewById(R.id.ratingBarAverage);
        progressBarFiveStar = (ProgressBar) findViewById(R.id.progressBarFiveStar);
        progressBarFourStar = (ProgressBar) findViewById(R.id.progressBarFourStar);
        progressBarThreeStar = (ProgressBar) findViewById(R.id.progressBarThreeStar);
        progressBarTwoStar = (ProgressBar) findViewById(R.id.progressBarTwoStar);
        progressBarOneStar = (ProgressBar) findViewById(R.id.progressBarOneStar);

        loadBannerAd();

        lnyComplement = findViewById(R.id.lny_complement);
        textVideo = (TextView) findViewById(R.id.textVideo);
        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.one_item_progress_wheel);

        //Check user is login
        SharedPreferences prefs = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        mobileUserLogin = prefs.getString("mobile", null);

        //Set to RTL if true
        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        //Get Intent Data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("contentId")) {
                contentId = extras.getString("contentId");
                buttonText = getString(R.string.txt_loading);
                sendJsonArrayRequest();
                buttonText = extras.getString("buttonText");
            }
        }

        btnArrowRating = (ImageButton) findViewById(R.id.btn_arrow_rating);
        btnArrowRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowComment.class);
                intent.putExtra("contentId", contentId);
                intent.putExtra("contentTitle", contentTitle);
                startActivity(intent);
            }
        });

        //Set ActionBar Title
        setTitle("");

        //Get userID from local variable
        userId = ((AppController) this.getApplication()).getUserId();

        wvOneContent = (WebView) findViewById(R.id.wv_one_content);
        String text = "<html dir='"+DIRECTION+"'><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                + "</style></head>"
                + "<body>"
                + getString(R.string.txt_loading)
                + "</body></html>";
        wvOneContent.loadDataWithBaseURL(null,text, "text/html; charset=UTF-8", "utf-8", null);

        //Total View + 1 //
        totalContentView();

        floatingActionButtonShowVideo = (FloatingActionButton) findViewById(R.id.fabShowVideo);
        floatingActionButtonShowVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send contentURL to ShowWebViewContentActivity
                Intent intent = new Intent(context, ShowWebViewContentActivity.class);
                intent.putExtra("contentTitle", contentTitle);
                intent.putExtra("contentUrl", contentUrl);
                intent.putExtra("contentOrientation", contentOrientation);
                startActivity(intent);
            }
        });

        showContent = findViewById(R.id.btn_show_content);

        //Floating Action Menu
        floating_action_menu_one_video = findViewById(R.id.floating_action_menu_one_video);
        fab_add_comment = findViewById(R.id.fab_add_comment);
        fab_add_comment.setImageResource(R.drawable.ic_rate_review_white_24dp);
        fab_show_comment = findViewById(R.id.fab_show_comment);
        fab_show_comment.setImageResource(R.drawable.ic_insert_comment_white_24dp);
        fab_bookmark = findViewById(R.id.fab_bookmark);
        fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

        //Main Fab Animation
        show_main_fab = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_anim);
        floating_action_menu_one_video.startAnimation(show_main_fab);

        //Add CommentModel
        fab_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileUserLogin != null) {
                    //contentId userId
                    Intent intent = new Intent(getBaseContext(), AddCommentActivity.class);
                    intent.putExtra("contentId", contentId);
                    intent.putExtra("contentTitle", contentTitle);
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                }else{
                    //Please login first
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutOneContentVideo, R.string.txt_please_login_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.txt_bookmark_login, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //dav
//                                    startActivity(new Intent(OneContentVideoActivity.this, LoginActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
                    snackbar.show();
                }
            }
        });

        //Show Comments
        fab_show_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowComment.class);
                intent.putExtra("contentId", contentId);
                intent.putExtra("contentTitle", contentTitle);
                startActivity(intent);
            }
        });

        //Get bookmark status
        if (mobileUserLogin != null) {
            getBookmarkStatus();

        }else{
            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Please login first then click on the bookmark
                    Snackbar snackbar = Snackbar.make(coordinatorLayoutOneContentVideo, R.string.txt_please_login_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.txt_bookmark_login, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //dav
//                                    startActivity(new Intent(OneContentVideoActivity.this, LoginActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
                    snackbar.show();
                }
            });
        }
        //dav
        //Get Rating Average
//        getRatingAverage();
    }

    //============================================================================//
    private void getBookmarkStatus()
    {
        StringRequest requestPostResponse = new StringRequest(Request.Method.GET, Config.GET_BOOKMARK_STATUS+"?user_id="+userId+"&content_id="+contentId+"&api_key="+ Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        if (getAnswer.equals("ContentIsBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsBookmark",Toast.LENGTH_LONG).show();
                            fab_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                            fab_bookmark.setTitle(getString(R.string.txt_remove_bookmark));
                            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to unBookmark
                                    fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                                    fab_bookmark.setTitle(getString(R.string.txt_add_bookmark));
                                    removeFromBookmark();
                                }
                            });

                        }else if (getAnswer.equals("ContentIsNotBookmark")) {
                            //Toast.makeText(getApplicationContext(),"ItemIsNotBookmark",Toast.LENGTH_LONG).show();
                            fab_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                            fab_bookmark.setTitle(getString(R.string.txt_add_bookmark));
                            fab_bookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change status to Bookmark
                                    fab_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                    fab_bookmark.setTitle(getString(R.string.txt_remove_bookmark));
                                    addToBookmark();
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //============================================================================//
    private void removeFromBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.REMOVE_FROM_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);
    }


    //============================================================================//
    private void addToBookmark()
    {
        //Remove From Bookmark
        progressWheelInterpolated.setVisibility(View.VISIBLE);

        StringRequest requestPostResponse = new StringRequest(Request.Method.POST, Config.ADD_TO_BOOKMARK_URL + "?api_key=" + Config.API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String getAnswer = response.toString();
                        Toast.makeText(getApplicationContext(), getAnswer ,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),R.string.txt_error,Toast.LENGTH_LONG).show();
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
        ){
            //To send our parametrs
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_id",userId);
                params.put("content_id",contentId);

                return params;
            }
        };

        //To avoid send twice when internet speed is slow
        requestPostResponse.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestPostResponse);

    }


    /*============================================================================*/
    private void sendJsonArrayRequest()
    {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        userUsername = object.getString("user_username");
                        contentDescription = object.getString("content_description");
                        contentBookmark = object.getString("content_description");
                        contentTitle = object.getString("content_title");
                        categoryTitle = object.getString("category_title");
                        contentImage = object.getString("content_image");
                        contentUrl = object.getString("content_url");
                        contentDuration = object.getString("content_duration");
                        contentViewed = object.getString("content_viewed");
                        contentPublishDate = object.getString("content_publish_date");
                        contentTypeTitle = object.getString("content_title");
                        contentTypeId = object.getString("content_type_id");
                        contentPlayerTypeId = object.getString("content_player_type_id");
                        contentUserRoleId = object.getString("content_user_role_id");
                        contentOrientation = object.getString("content_orientation");
                        contentCached = object.getString("content_cached");
                        urlComplement = object.getString("url_complement");

                        String contentImageURL = Config.CONTENT_IMG_URL+contentImage;
                        ImageView contentImageView = (ImageView) findViewById(R.id.iv_one_content_image);
                        Glide
                                .with(OneContentVideoActivity.this)
                                .load(contentImageURL)
                                .apply(new RequestOptions()
                                        .fitCenter()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(contentImageView);


                        TextView oneContentTitle = (TextView)findViewById(R.id.tv_one_content_title);
                        oneContentTitle.setText(contentTitle);

                        TextView oneContentCategory = (TextView)findViewById(R.id.tv_one_content_category);
                        oneContentCategory.setText(categoryTitle);

                        TextView oneContentDate = (TextView)findViewById(R.id.tv_one_content_date);
                        oneContentDate.setText(Config.TimeAgo(contentPublishDate));

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String contentViewed_formatted = formatter.format(Integer.parseInt(contentViewed));
                        TextView oneContentViewed = (TextView)findViewById(R.id.tv_one_content_viewed);
                        oneContentViewed.setText(contentViewed_formatted);

                        //dav
//                        showContent.setText(buttonText);
                        showContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                // validar que no se este ejecutando el audio
                                if(player != null && player.isPlaying())
                                {
                                    player.setPlayWhenReady(false);
                                    player.getPlaybackState();
                                }

                                if(contentPlayerTypeId.equals("1")) {
                                    //Go to ExoPlayerActivity
                                    exoPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("2")) {
                                    //Go to JzPlayerActivity
                                    jzPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("3")) {
                                    //Go to WebViewPlayerActivity
                                    webViewPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("4")) {
                                    //Go to YouTubePlayerActivity
                                    youtubePlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("5")) {
                                    //Go to WebViewPlayerActivity and select Youtube Embed
                                    webViewPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("6")) {
                                    //Go to WebViewPlayerActivity and select Vimeo Embed
                                    webViewPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("7")) {
                                    //Go to HLSExoPlayerActivity
                                    hlsExoPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else if(contentPlayerTypeId.equals("8")) {
                                    //Go to NativePlayerActivity
                                    nativePlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);

                                }else {
                                    //Go to ExoPlayerActivity
                                    exoPlayer(userId, contentId, contentTitle, contentImage, contentUrl, contentTypeId, contentPlayerTypeId);
                                }
                            }
                        });

                        // validar si tiene audio complemetario
                        if(!urlComplement.isEmpty()){
                            textVideo.setVisibility(View.VISIBLE);
                            lnyComplement.setVisibility(View.VISIBLE);
                            //ExoPlayer
                            //Exo Player New
                            playerView = (PlayerView) findViewById(R.id.exoPlayerComplement);
                            //dav
//                        View view = LayoutInflater.from(getApplicationContext())
//                                .inflate(R.layout.cumstom_layout_exoplayer, null, false);
//                        playerView = (PlayerView) view.getRootView();

                            player = new SimpleExoPlayer.Builder(context).build();
                            playerView.setPlayer(player);
                            // Produces DataSource instances through which media data is loaded.
                            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                                    Util.getUserAgent(context, getString(R.string.app_name)));
                            // This is the MediaSource representing the media to be played.
                            MediaSource videoSource =
                                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(Uri.parse(urlComplement));
                            // Prepare the player with the source.
                            player.prepare(videoSource);
                            player.setPlayWhenReady(false);
                        }

                        //To format as HTML
                        String formattedPageContent = android.text.Html.fromHtml(contentDescription).toString(); //Just for ckEditor

                        wvOneContent.getSettings().setJavaScriptEnabled(true);
                        wvOneContent.setFocusableInTouchMode(false);
                        wvOneContent.setFocusable(false);
                        WebSettings webSettings = wvOneContent.getSettings();
                        webSettings.setDefaultFontSize(Config.Font_Size);
                        wvOneContent.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html; charset=UTF-8";
                        String encoding = "utf-8";

                        String text = "<html dir='"+DIRECTION+"'><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #424242; text-align:justify; direction:"+DIRECTION+"; line-height:23px;}"
                                + "</style></head>"
                                + "<body>"
                                //+ formattedPageContent
                                + contentDescription
                                + "</body></html>";
                        wvOneContent.loadDataWithBaseURL(null,text, mimeType, encoding, null);
                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressWheelInterpolated.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.txt_error, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_ONE_CONTENT_URL+contentId+"/?api_key=" + Config.API_KEY, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }


    //============================================================================//
    private void totalContentView(){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Total itemView + 1
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.TOTAL_CONTENT_VIEWED_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("content_id",contentId);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(9000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


   /*============================================================================*/
    private void getRatingAverage()
    {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject object = response.getJSONObject(i);
                        total_rate = object.getString("total_rate");
                        row_rate = object.getString("row_rate");
                        rate_average = object.getString("rate_average");
                        String one_star_average = object.getString("one_star_average");
                        String two_star_average = object.getString("two_star_average");
                        String three_star_average = object.getString("three_star_average");
                        String four_star_average = object.getString("four_star_average");
                        String five_star_average = object.getString("five_star_average");

                        ratingBarAverage.setRating(Float.parseFloat(rate_average));
                        progressBarFiveStar.setProgress(Integer.parseInt(five_star_average));
                        progressBarFourStar.setProgress(Integer.parseInt(four_star_average));
                        progressBarThreeStar.setProgress(Integer.parseInt(three_star_average));
                        progressBarTwoStar.setProgress(Integer.parseInt(two_star_average));
                        progressBarOneStar.setProgress(Integer.parseInt(one_star_average));

                        TextView rateAverage = (TextView)findViewById(R.id.txt_rate_average);
                        rateAverage.setText(rate_average);

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String total_rate_formated = formatter.format(Integer.parseInt(row_rate));

                        TextView rowRate = (TextView)findViewById(R.id.txt_row_rate);
                        rowRate.setText(total_rate_formated);

                        progressWheelInterpolated.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    progressWheelInterpolated.setVisibility(View.GONE);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressWheelInterpolated.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), R.string.txt_error, Toast.LENGTH_LONG).show();
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Config.GET_RATING_AVERAGE+"?content_id="+contentId+"&api_key="+ Config.API_KEY, null, listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(25000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }


    //============================================================================//
    private void loadBannerAd(){
        //dav
        try {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
            AdRequest adRequest = new AdRequest.Builder().build();

            // Banner Ad
            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this, ((AppController) this.getApplication()).getAdmobSettingAppId());
            View adContainer = findViewById(R.id.adMobBannerView);
            mAdView = new AdView(OneContentVideoActivity.this);

            //dav
            AdSize adSize = getAdSize((Activity) context);
            mAdView.setAdSize(adSize);

            mAdView.setAdUnitId(((AppController) this.getApplication()).getAdmobSettingBannerUnitId());
            ((RelativeLayout) adContainer).addView(mAdView);
            //dav
            adContainer.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //dav
    private AdSize getAdSize(Activity activity) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


    //============================================================================//
    private void updateUserCoin(final String theUserId, final String theUserCoin, final String updateCoinType, final String expirationTime){
        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Total user_coin + user_coin
                if(!response.toString().equals(""))
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest requestView = new StringRequest(Request.Method.POST, Config.UPDATE_USER_COIN_URL + "?api_key=" + Config.API_KEY,listener,errorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("user_id",theUserId);
                params.put("user_coin",theUserCoin);
                params.put("update_coin_type",updateCoinType);
                params.put("expiration_time",expirationTime);
                return params;
            }
        };
        requestView.setRetryPolicy(new DefaultRetryPolicy(10000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestView);
    }


    //============================================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }


    //============================================================================//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            /*if(player.isPlaying()) {
                player.stop();
            }*/
            //jzPlayer.releaseAllVideos();
            //dav
            // Show Interstitial Ad
//            if (mInterstitialAd.isLoaded()) {
//                if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1")) {
//                    if(login_user_id.equals("Not Login")) {
//                        mInterstitialAd.show();
//
//                    }else{
//                        if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
//                        {
//                            mInterstitialAd.show();
//                        }
//                    }
//                }
//            } else {
//                Log.d("TAG", "The interstitial wasn't loaded yet.");
//            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //============================================================================//
    public void onBackPressed() {
        if(player != null && player.isPlaying())
        {
            player.stop();
        }
        //jzPlayer.releaseAllVideos();
        /*if(player.isPlaying()) {
            player.stop();
        }*/
        // Show Interstitial Ad
        /*if (mInterstitialAd.isLoaded()) {
            if (((AppController) this.getApplication()).getAdmobSettingInterstitialStatus().equals("1"))
            {
                if(login_user_id.equals("Not Login")) {
                    mInterstitialAd.show();

                }else{
                    if (((AppController) this.getApplication()).getUserHideInterstitialAd().equals("0")) //Check user hide ads or not
                    {
                        mInterstitialAd.show();
                    }
                }
            }
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }*/

        finish();
    }


    //============================================================================//
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    //============================================================================//
    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }


    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    //============================================================================//
    public void exoPlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, ExoPlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }


    //============================================================================//
    public void hlsExoPlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, HLSExoPlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }


    //============================================================================//
    public void jzPlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, JzPlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }


    //============================================================================//
    public void webViewPlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, WebViewPlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }


    //============================================================================//
    public void youtubePlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, YouTubePlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }


    //============================================================================//
    public void nativePlayer(final String userId, final String contentId, final String contentTitle, final String contentImage, final String contentUrl, final String contentTypeId, final String contentPlayerTypeId) {

        Intent intentPlayer = new Intent(context, NativeVideoPlayerActivity.class);
        intentPlayer.putExtra("userId", userId);
        intentPlayer.putExtra("contentId", contentId);
        intentPlayer.putExtra("contentTitle", contentTitle);
        intentPlayer.putExtra("contentImage", contentImage);
        intentPlayer.putExtra("contentUrl", contentUrl);
        intentPlayer.putExtra("contentTypeId", contentTypeId);
        intentPlayer.putExtra("contentPlayerTypeId", contentPlayerTypeId);
        startActivity(intentPlayer);
    }
}