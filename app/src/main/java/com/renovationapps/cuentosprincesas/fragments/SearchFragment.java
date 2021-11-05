package com.renovationapps.cuentosprincesas.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.renovationapps.cuentosprincesas.models.TypeContentModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.renovationapps.cuentosprincesas.Config;
import com.renovationapps.cuentosprincesas.models.ContentModel;
import com.renovationapps.cuentosprincesas.utils.AppController;
import com.renovationapps.cuentosprincesas.utils.Tools;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.renovationapps.cuentosprincesas.R;
import com.renovationapps.cuentosprincesas.adapters.ContentAdapter;

public class SearchFragment extends Fragment {
    public SearchFragment() { }
    CoordinatorLayout searchCoordinatorLayout;
    LinearLayout searchFormLinearLayout;
    LinearLayout linearLayout_top_bar;
    CardView cardView_search_form;
    TextInputEditText etSearchKeyword;
    Button btnSearch;
    //Items Variable for volley
    RecyclerView contentRecyclerView;
    ContentAdapter cAdapter;
    LinearLayoutManager cLayoutManager;
    List<ContentModel> contentModelsList;
    RequestQueue rqContent;
    private ProgressWheel progressWheelInterpolated;
    String showWhichContent;
    String showTitle;
    String CONTENT_URL;
    String userId;
    String showFilter;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private  ArrayList<TypeContentModel> listTypeContent;
    //dav
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //Set ActionBar Title
        getActivity().setTitle(R.string.menu_search);

        //dav
        loadBannerAd(view);

        searchCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.searchCoordinatorLayout);
        searchFormLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_fs_search_form);
        linearLayout_top_bar = (LinearLayout) view.findViewById(R.id.linearLayout_top_bar);
        cardView_search_form = (CardView) view.findViewById(R.id.cardView_search_form);

        //Check internet connection start
        if (!Tools.isNetworkAvailable(getActivity())) {
            Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_internet, Snackbar.LENGTH_LONG)
                    .setAction(R.string.txt_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Refresh fragment
                            /*getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frmMain, new BookmarkFragment())
                                    .commit();*/
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorYellow));
            snackbar.show();
        }

        //Material ProgressWheel
        progressWheelInterpolated = (ProgressWheel) view.findViewById(R.id.search_progress_wheel);

        //contentRecyclerView start
        //List items by category
        rqContent = Volley.newRequestQueue(getActivity());
        contentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_search_content);
        contentRecyclerView.setHasFixedSize(true);
        //dav
        cLayoutManager = new LinearLayoutManager(getActivity());

        contentRecyclerView.setLayoutManager(cLayoutManager);
        contentModelsList = new ArrayList<>();
        cAdapter = new ContentAdapter(getActivity(), contentModelsList);
        contentRecyclerView.setAdapter(cAdapter);

        //Search Form
        etSearchKeyword = (TextInputEditText)view.findViewById(R.id.et_search_keyword);

        //Show search button on phone keyboard
        etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchFormLinearLayout.setVisibility(View.INVISIBLE);
                    linearLayout_top_bar.setVisibility(View.INVISIBLE);
                    cardView_search_form.setVisibility(View.INVISIBLE);
                    // Hidden the keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchCoordinatorLayout.getWindowToken(), 0);

                    performSearch();
                    return true;
                }
                return false;
            }
        });

        btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFormLinearLayout.setVisibility(View.INVISIBLE);
                linearLayout_top_bar.setVisibility(View.INVISIBLE);
                cardView_search_form.setVisibility(View.INVISIBLE);
                // Hidden the keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchCoordinatorLayout.getWindowToken(), 0);

                performSearch();
            }
        });

        //showContentList
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            showWhichContent = bundle.getString("showWhichContent", "");
            showTitle = bundle.getString("showTitle", "");
            if(bundle.containsKey("showFilter")){
                showFilter = bundle.getString("showFilter", "");
            }
        }
        if(showWhichContent != null)
        {
            if(showWhichContent.equals("FeaturedContent")) {
                getActivity().setTitle(showTitle);
                CONTENT_URL = Config.GET_FEATURED_CONTENT_URL+"?limit=" + Config.LOAD_LIMIT + "&api_key=" + Config.API_KEY;
                //Show gamebox for first time
                showContentListFirstTime();

                //For Load More
                contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            // Recycle view scrolling downwards...
                            // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                                // remember "!" is the same as "== false"
                                // here we are now allowed to load more, but we need to be careful
                                // we must check if itShouldLoadMore variable is true [unlocked]
                                if (itShouldLoadMore) {
                                    //Show gamebox for load more
                                    showContentListLoadMore();
                                }
                            }
                        }
                    }
                });

            }else if(showWhichContent.equals("BestRatedContent")) {
                getActivity().setTitle(showTitle);
                CONTENT_URL = Config.GET_BEST_RATED_CONTENT_URL+"?limit=" + Config.LOAD_LIMIT + "&api_key=" + Config.API_KEY;
                //Show gamebox for first time
                showContentListFirstTime();

                //For Load More
                contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            // Recycle view scrolling downwards...
                            // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                                // remember "!" is the same as "== false"
                                // here we are now allowed to load more, but we need to be careful
                                // we must check if itShouldLoadMore variable is true [unlocked]
                                if (itShouldLoadMore) {
                                    showContentListLoadMore();
                                }
                            }
                        }
                    }
                });

            }else if(showWhichContent.equals("LatestContent")) {
                getActivity().setTitle(showTitle);
                CONTENT_URL = Config.GET_LATEST_CONTENT_URL+"?limit=" + Config.LOAD_LIMIT + "&api_key=" + Config.API_KEY;
                //Show gamebox for first time
                showContentListFirstTime();

                //For Load More
                contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            // Recycle view scrolling downwards...
                            // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                                // remember "!" is the same as "== false"
                                // here we are now allowed to load more, but we need to be careful
                                // we must check if itShouldLoadMore variable is true [unlocked]
                                if (itShouldLoadMore) {
                                    showContentListLoadMore();
                                }
                            }
                        }
                    }
                });

            }else if(showWhichContent.equals("SpecialContent")) {
                getActivity().setTitle(showTitle);
                CONTENT_URL = Config.GET_SPECIAL_CONTENT_URL+"?limit=" + Config.LOAD_LIMIT + "&api_key=" + Config.API_KEY;
                //Show gamebox for first time
                showContentListFirstTime();

                //For Load More
                contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            // Recycle view scrolling downwards...
                            // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                                // remember "!" is the same as "== false"
                                // here we are now allowed to load more, but we need to be careful
                                // we must check if itShouldLoadMore variable is true [unlocked]
                                if (itShouldLoadMore) {
                                    showContentListLoadMore();
                                }
                            }
                        }
                    }
                });

            }else if(showWhichContent.equals("BookmarkContent")) {
                getActivity().setTitle(showTitle);
                //Get userID from local variable
                userId = ((AppController) getActivity().getApplication()).getUserId();
                CONTENT_URL = Config.GET_BOOKMARK_CONTENT_URL+"/?user_id=" + userId + "&limit=" + Config.LOAD_LIMIT + "&api_key=" + Config.API_KEY;

                //Show gamebox for first time
                showContentListFirstTime();

                //For Load More
                contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            // Recycle view scrolling downwards...
                            // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                            if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                                // remember "!" is the same as "== false"
                                // here we are now allowed to load more, but we need to be careful
                                // we must check if itShouldLoadMore variable is true [unlocked]
                                if (itShouldLoadMore) {
                                    showContentListLoadMore();
                                }
                            }
                        }
                    }
                });
            }
        }

        //validate modal filter
        if(showFilter != null && !showFilter.isEmpty()){
            showModalFilter();
        }

        return view;
    }

    // open modal with list view content type
    public void showModalFilter(){
        showFilter="";
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_TYPE_CONTENT_AVAILABLE + "?api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                listTypeContent = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        TypeContentModel objTypeContent = new TypeContentModel(jsonObject.getString("content_type_id"),jsonObject.getString("name_content_type"));
                        listTypeContent.add(objTypeContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                progressWheelInterpolated.setVisibility(View.GONE);
                // abrir ventana modal
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle("Selecciona el tipo de contenido:");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                for(int i = 0; i < listTypeContent.size(); i++){
                    arrayAdapter.add(listTypeContent.get(i).getName_content_type());
                }
                // boton cancelar
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // al seleccionar
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String content_type_id = listTypeContent.get(which).getContent_type_id();
                        getInfoTypeContent(content_type_id);

                    }
                });
                builderSingle.show();
                // finalizar ventana modal

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }

    //==========================================================================//
    public void getInfoTypeContent(String content_type_id) {
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free

        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_CONTENTTYPE_BY_SEARCH_URL + "?content_type_id="+content_type_id+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                    cardView_search_form.setVisibility(View.GONE);
                }

                    /*cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                    contentRecyclerView.setAdapter(cAdapter);
                    progressWheelInterpolated.setVisibility(View.GONE);*/
                showFilter="";

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                //searchFormLinearLayout.setVisibility(View.VISIBLE);
                //linearLayout_top_bar.setVisibility(View.VISIBLE);
                cardView_search_form.setVisibility(View.VISIBLE);
                itShouldLoadMore = true;
                //dav
                /*Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_more_result, Snackbar.LENGTH_LONG);
                snackbar.show();*/

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }

    //==========================================================================//
    public void performSearch() {
        final String keyword = etSearchKeyword.getText().toString();
        if (keyword.equals("")) {
            Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_please_enter_keyword, Snackbar.LENGTH_LONG);
            snackbar.show();
            searchFormLinearLayout.setVisibility(View.VISIBLE);
            linearLayout_top_bar.setVisibility(View.VISIBLE);
            cardView_search_form.setVisibility(View.VISIBLE);

        }else if (keyword.length() < 3) {
            Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_keyword_too_short, Snackbar.LENGTH_LONG);
            snackbar.show();
            searchFormLinearLayout.setVisibility(View.VISIBLE);
            linearLayout_top_bar.setVisibility(View.VISIBLE);
            cardView_search_form.setVisibility(View.VISIBLE);

        }else {

            //Show gamebox for first time
            performSearchFirstTime(keyword);

            //For Load More
            contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        // Recycle view scrolling downwards...
                        // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                        if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                            // remember "!" is the same as "== false"
                            // here we are now allowed to load more, but we need to be careful
                            // we must check if itShouldLoadMore variable is true [unlocked]
                            if (itShouldLoadMore) {
                                performSearchLoadMore(keyword);
                            }
                        }
                    }
                }
            });

        }
    }


    //==========================================================================//
    public void performSearchFirstTime(String keyword) {
        lastId = "0";
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free

        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_CONTENT_BY_SEARCH_URL + "?keyword="+keyword+"&last_id="+lastId+"&limit="+Config.LOAD_LIMIT+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

                    /*cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                    contentRecyclerView.setAdapter(cAdapter);
                    progressWheelInterpolated.setVisibility(View.GONE);*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                searchFormLinearLayout.setVisibility(View.VISIBLE);
                linearLayout_top_bar.setVisibility(View.VISIBLE);
                cardView_search_form.setVisibility(View.VISIBLE);
                itShouldLoadMore = true;
                Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_result, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void performSearchLoadMore(String keyword) {
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free

        progressWheelInterpolated.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.GET_CONTENT_BY_SEARCH_URL + "?keyword="+keyword+"&last_id="+lastId+"&limit="+Config.LOAD_LIMIT+"&api_key="+ Config.API_KEY, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

                    /*cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                    contentRecyclerView.setAdapter(cAdapter);
                    progressWheelInterpolated.setVisibility(View.GONE);*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                //searchFormLinearLayout.setVisibility(View.VISIBLE);
                //linearLayout_top_bar.setVisibility(View.VISIBLE);
                //cardView_search_form.setVisibility(View.VISIBLE);
                itShouldLoadMore = true;
                /*Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_more_result, Snackbar.LENGTH_LONG);
                snackbar.show();*/

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void showContentListNoLoadMore() {
        progressWheelInterpolated.setVisibility(View.VISIBLE);
        searchFormLinearLayout.setVisibility(View.INVISIBLE);
        linearLayout_top_bar.setVisibility(View.INVISIBLE);
        cardView_search_form.setVisibility(View.INVISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CONTENT_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);

                }

                cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                contentRecyclerView.setAdapter(cAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                //searchFormLinearLayout.setVisibility(View.VISIBLE);
                //linearLayout_top_bar.setVisibility(View.VISIBLE);
                //cardView_search_form.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_result, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void showContentListFirstTime() {
        lastId = "0";
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free

        progressWheelInterpolated.setVisibility(View.VISIBLE);
        searchFormLinearLayout.setVisibility(View.INVISIBLE);
        linearLayout_top_bar.setVisibility(View.INVISIBLE);
        cardView_search_form.setVisibility(View.INVISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CONTENT_URL+"&last_id="+lastId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getActivity(), CONTENT_URL+"&last_id="+lastId, Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");
                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

                //cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                //contentRecyclerView.setAdapter(cAdapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                //searchFormLinearLayout.setVisibility(View.VISIBLE);
                //linearLayout_top_bar.setVisibility(View.VISIBLE);
                //cardView_search_form.setVisibility(View.VISIBLE);
                itShouldLoadMore = true;
                Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_result, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }


    //==========================================================================//
    public void showContentListLoadMore() {
        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure, user will not load more when volley is processing another request only load more when  volley is free

        progressWheelInterpolated.setVisibility(View.VISIBLE);
        searchFormLinearLayout.setVisibility(View.INVISIBLE);
        linearLayout_top_bar.setVisibility(View.INVISIBLE);
        cardView_search_form.setVisibility(View.INVISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CONTENT_URL+"&last_id="+lastId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itShouldLoadMore = true;
                if (response.length() == 0) {
                    //No result found!
                    //Toast.makeText(getActivity(), R.string.txt_no_result, Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    ContentModel contentModel = new ContentModel();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString("content_id");
                        contentModel.setContent_id(jsonObject.getString("content_id"));
                        contentModel.setContent_title(jsonObject.getString("content_title"));
                        contentModel.setContent_image(jsonObject.getString("content_image"));
                        contentModel.setContent_publish_date(jsonObject.getString("content_publish_date"));
                        contentModel.setCategory_title(jsonObject.getString("category_title"));
                        contentModel.setContent_duration(jsonObject.getString("content_duration"));
                        contentModel.setContent_viewed(jsonObject.getString("content_viewed"));
                        contentModel.setContent_url(jsonObject.getString("content_url"));
                        contentModel.setContent_type_title(jsonObject.getString("content_type_title"));
                        contentModel.setContent_type_id(jsonObject.getString("content_type_id"));
                        contentModel.setContent_user_role_id(jsonObject.getString("content_user_role_id"));
                        contentModel.setContent_orientation(jsonObject.getString("content_orientation"));
                        contentModel.setUser_role_title(jsonObject.getString("user_role_title"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contentModelsList.add(contentModel);
                    cAdapter.notifyDataSetChanged();
                    progressWheelInterpolated.setVisibility(View.GONE);
                }

                /*cAdapter = new ContentAdapter(getActivity(), contentModelsList);
                contentRecyclerView.setAdapter(cAdapter);
                progressWheelInterpolated.setVisibility(View.GONE);*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressWheelInterpolated.setVisibility(View.GONE);
                Log.i("BlueDev Volley Error: ", error+"");
                //Toast.makeText(getActivity(), R.string.txt_error, Toast.LENGTH_SHORT).show();
                //searchFormLinearLayout.setVisibility(View.VISIBLE);
                //linearLayout_top_bar.setVisibility(View.VISIBLE);
                //cardView_search_form.setVisibility(View.VISIBLE);
                itShouldLoadMore = true;
                /*Snackbar snackbar = Snackbar.make(searchCoordinatorLayout, R.string.txt_no_more_result, Snackbar.LENGTH_LONG);
                snackbar.show();*/

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqContent.add(jsonArrayRequest);
    }
    //dav
    //============================================================================//
    private void loadBannerAd(View view){
        try {
            //dav
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
            MobileAds.initialize(getContext(), ((AppController) getContext().getApplicationContext()).getAdmobSettingAppId());
            View adContainer = view.findViewById(R.id.adMobBannerView);
            mAdView = new AdView(getContext());
            //dav
            AdSize adSize = getAdSize((Activity) getContext());
            mAdView.setAdSize(adSize);

            mAdView.setAdUnitId(((AppController) getContext().getApplicationContext()).getAdmobSettingBannerUnitId());
            ((RelativeLayout) adContainer).addView(mAdView);
            //dav
//        cardViewAD.setVisibility(View.VISIBLE);
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
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }


    //============================================================================//

}