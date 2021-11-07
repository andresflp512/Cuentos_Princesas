package com.renovationapps.cuentosprincesas.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.MediaView;
import com.renovationapps.cuentosprincesas.Config;
import com.renovationapps.cuentosprincesas.activities.MainActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentDownloadActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentLinkActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentMusicActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentProductActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentTextActivity;
import com.renovationapps.cuentosprincesas.activities.OneContentVideoActivity;
import com.renovationapps.cuentosprincesas.models.ContentModel;

import java.util.List;
import java.util.Random;

import com.renovationapps.cuentosprincesas.R;
import com.renovationapps.cuentosprincesas.utils.Mcallback;
import com.renovationapps.cuentosprincesas.utils.TemplateView;
import com.renovationapps.cuentosprincesas.utils.Tools;

import static com.renovationapps.cuentosprincesas.activities.MainActivity.user_role_id;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder>
{
    private Context context;
    private List<ContentModel> contentModel;
    Intent intentContent;


    public ContentAdapter(Context context, List<ContentModel> contentModel) {
        this.context = context;
        this.contentModel = contentModel;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_content_list, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.itemView.setTag(contentModel.get(position));

        ContentModel content = contentModel.get(position);

        final ContentViewHolder vItem = (ContentViewHolder) holder;
        if (holder.getAdapterPosition() % Config.NATIVO_INTERVALO == Config.NATIVO_INDEX) {
            vItem.bindAdMobNativeAdView();
        } else {
            vItem.admob_native_template_back.setVisibility(View.GONE);
        }

        holder.contentTitle.setText(content.getContent_title());
        Glide.with(context)
                .load(Config.CONTENT_IMG_URL+content.getContent_image())
                .apply(new RequestOptions().transforms(new RoundedCorners(8)) //Rounded and Circle Image
                        //.bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 0, 0))
                        .placeholder(R.drawable.pre_loading)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate())
                .into(holder.contentImage);
        holder.contentDuration.setText(content.getContent_duration());
        holder.contentViewed.setText(content.getContent_viewed());
        holder.categoryTitle.setText(content.getCategory_title());
        //dav
        //holder.contentPublishDate.setText(Config.TimeAgo(content.getContent_publish_date()));
        holder.contentTypeTitle.setText(content.getContent_type_title());
        holder.userRoleTitle.setText(content.getUser_role_title());
        //dav
        int[] androidColors = context.getResources().getIntArray(R.array.mdcolor_random);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        holder.card_color.setCardBackgroundColor(randomAndroidColor);

    }

    @Override
    public int getItemCount() {
        return contentModel.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder
    {
        public TextView contentId;
        public TextView contentTitle;
        public ImageView contentImage;
        public TextView contentUrl;
        public TextView contentPrice;
        public TextView contentTypeId;
        public TextView contentAccess;
        public TextView contentUserRoleId;
        public TextView contentRoleTitle;
        public TextView userRoleTitle;
        public TextView contentDuration;
        public TextView contentViewed;
        public TextView contentLiked;
        public TextView contentPublishDate;
        public TextView contentFeatured;
        public TextView contentSpecial;
        public TextView contentOrientation;
        public TextView categoryTitle;
        public TextView contentTypeTitle;
        //dav
        CardView card_color;
        TemplateView admob_native_template;
        MediaView admob_media_view;
        CardView admob_native_template_back;

        public ContentViewHolder(View itemView) {
            super(itemView);
            contentTitle = (TextView) itemView.findViewById(R.id.tv_content_list_title);
            contentImage = (ImageView) itemView.findViewById(R.id.iv_content_list_image);
            categoryTitle = (TextView) itemView.findViewById(R.id.tv_content_list_category);
            contentDuration = (TextView) itemView.findViewById(R.id.tv_content_list_duration);
            //dav
//            contentPublishDate = (TextView) itemView.findViewById(R.id.tv_content_list_date_time);
            contentViewed = (TextView) itemView.findViewById(R.id.tv_content_list_total_viewed);
            contentTypeTitle = (TextView) itemView.findViewById(R.id.tv_content_list_type_title);
            userRoleTitle = (TextView) itemView.findViewById(R.id.tv_user_role_title);
            //dav
            card_color = itemView.findViewById(R.id.card_color);
            admob_native_template = itemView.findViewById(R.id.native_template);
            admob_media_view = itemView.findViewById(R.id.media_view);
            admob_native_template_back = itemView.findViewById(R.id.admob_native_template_back);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dav
                    ((MainActivity) context).interShow(new Mcallback() {
                        @Override
                        public void onAction() {

                    ContentModel content = (ContentModel) view.getTag();

                    String contentId = content.getContent_id();
                    String contentTitle = content.getContent_title();
                    String categoryTitle = content.getCategory_title();
                    String contentImage = content.getContent_image();
                    String contentUrl = content.getContent_url();
                    String contentDuration = content.getContent_duration();
                    String contentViewed = content.getContent_viewed();
                    String contentPublishDate = content.getContent_publish_date();
                    String contentTypeId = content.getContent_type_id();
                    String contentTypeTitle = content.getContent_type_title();
                    String contentUserRoleId = content.getContent_user_role_id();
                    String userRoleTitle = content.getUser_role_title();
                    String contentOrientation = content.getContent_orientation();


                    if(contentTypeId.equals("1")) { //Send information to OneContentVideoActivity
                        intentContent = new Intent(context, OneContentVideoActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_video));

                    }else if(contentTypeId.equals("2")) { //Send information to OneContentMusicActivity
                        intentContent = new Intent(context, OneContentMusicActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_music));

                    }else if(contentTypeId.equals("3")) { //Send information to OneContentLinkActivity
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_play_game));

                    }else if(contentTypeId.equals("4")) { //Send information to OneContentTextActivity
                        intentContent = new Intent(context, OneContentTextActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));

                    }else if(contentTypeId.equals("5")) { //Send information to OneContentDownloadActivity
                        intentContent = new Intent(context, OneContentDownloadActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_download));

                    }else if(contentTypeId.equals("6")) { //Send information to OneContentLinkActivity
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));

                    }else if(contentTypeId.equals("7")) { //Send information to OneContentProductActivity
                        intentContent = new Intent(context, OneContentProductActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_show));

                    }else{
                        intentContent = new Intent(context, OneContentLinkActivity.class);
                        intentContent.putExtra("buttonText", context.getString(R.string.txt_button_open));
                    }

                    intentContent.putExtra("contentId", contentId);
                    intentContent.putExtra("contentTitle", contentTitle);
                    intentContent.putExtra("categoryTitle", categoryTitle);
                    intentContent.putExtra("contentImage", contentImage);
                    intentContent.putExtra("contentUrl", contentUrl);
                    intentContent.putExtra("contentDuration", contentDuration);
                    intentContent.putExtra("contentViewed", contentViewed);
                    intentContent.putExtra("contentPublishDate", contentPublishDate);
                    intentContent.putExtra("contentTypeId", contentTypeId);
                    intentContent.putExtra("contentTypeTitle", contentTypeTitle);
                    intentContent.putExtra("contentUserRoleId", contentUserRoleId);
                    intentContent.putExtra("contentOrientation", contentOrientation);

                    //Check content user role id
                    if(user_role_id.equals("Not Login")) {
                        //Guest user can't access VIP game with ID: 6
                        if(contentUserRoleId.equals("6")) {
                            //Toast.makeText(context, "Role: "+contentUserRoleId,Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(context);
                            builderCheckUpdate.setTitle(R.string.txt_access_permission);
                            builderCheckUpdate.setMessage(R.string.txt_this_content_is_for_vip_user_role);
                            builderCheckUpdate.setCancelable(false);

                            builderCheckUpdate.setPositiveButton(
                                    R.string.txt_upgrade_role,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //dav
//                                            Intent intentUpgrade = new Intent(context, AccountUpgrade.class);
//                                            context.startActivity(intentUpgrade);
                                        }
                                    });

                            builderCheckUpdate.setNegativeButton(
                                    R.string.txt_cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                            alert1CheckUpdate.show();

                        }else{
                            context.startActivity(intentContent);
                        }

                    }else{
                        if(contentUserRoleId.equals("6")) {
                            //Check if user is VIP or Regular
                            if(user_role_id.equals("6") || user_role_id.equals("1") || user_role_id.equals("2") || user_role_id.equals("3") || user_role_id.equals("4")) {
                                context.startActivity(intentContent);

                            }else{
                                //Toast.makeText(context, "Role: "+contentUserRoleId,Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builderCheckUpdate = new AlertDialog.Builder(context);
                                builderCheckUpdate.setTitle(R.string.txt_access_permission);
                                builderCheckUpdate.setMessage(R.string.txt_this_content_is_for_vip_user_role);
                                builderCheckUpdate.setCancelable(false);

                                builderCheckUpdate.setPositiveButton(
                                        R.string.txt_upgrade_role,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //dav
//                                                Intent intentUpgrade = new Intent(context, AccountUpgrade.class);
//                                                context.startActivity(intentUpgrade);
                                            }
                                        });

                                builderCheckUpdate.setNegativeButton(
                                        R.string.txt_cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert1CheckUpdate = builderCheckUpdate.create();
                                alert1CheckUpdate.show();
                            }

                        }else{
                            context.startActivity(intentContent);
                        }
                    }
                        }
                    });
                }
            });
        }
        private void bindAdMobNativeAdView() {
            AdLoader adLoader = new AdLoader.Builder(context, Config.AdMobNativeId)
                    .forNativeAd(NativeAd -> {
                        admob_media_view.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                        admob_native_template.setNativeAd(NativeAd);
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (getAdapterPosition() % Config.NATIVO_INTERVALO == Config.NATIVO_INDEX) {
                                admob_native_template_back.setVisibility(View.VISIBLE);
                            } else {
                                admob_native_template_back.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            admob_native_template_back.setVisibility(View.GONE);
                        }
                    })
                    .build();
            adLoader.loadAd(Tools.getAdRequest((Activity) context));

        }
    }

}
