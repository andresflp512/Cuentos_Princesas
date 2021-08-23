package com.innovapps.cuentosprincesas.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innovapps.cuentosprincesas.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Created on 26/11/2018.
 * @author Yulian Martinez
 * @version 1.0.1
 */
public class DialogPackage {

    public static boolean isShowing;
    private static final int ID_APP = 59; // id de la app
    private static final String URL_SERVER = "http://full-apps-org.com/Seguridad_Apps/"; // URL del servidor
    private static final int TITLE_DIALOG = R.string.title_dialo; // titulo del dialogo
    private static final int MESSAGE_DIALOG = R.string.Message_dialo; // mensaje del dialogo
    private static final int BUTTON_ACEPT = R.string.Button_accept; // texto del boton aceptar
    private static final int BUTTON_CANCEL = R.string.Button_Cancel; // texto del boton cancelar
    private static ListenerDialog listenerDialogOrig;

    /**
     * Inicializa el proceso de verificacion del Dialog de actualizacion
     * @param context - en donde se ejecuta
     */
    public static void show(Context context, ListenerDialog listenerDialog){
        listenerDialogOrig = listenerDialog;
        sendGet(context);
    }

    /**
     * Se encarga de realizar la peticion y obtener la respuesta del servicio
     * para decidir si muestra o no el Dialog
     * @param context - en donde se ejecuta
     */

    private static void sendGet(final Context context){

        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return getJSONString(params[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try{
                    JSONObject jsonObject = new JSONObject(result);
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    String versionAppName = pInfo.versionName;
                    if(!jsonObject.isNull("package_app")){
                        final String verifyPackage = jsonObject.getString("package_app");
                        final String versionSecure = jsonObject.getString("version_app");
                        if(!verifyPackage.equals(context.getPackageName()) || ( !versionSecure.equals("0") && !versionSecure.equals(versionAppName) ) ){


                            //dav
                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(1);
                            dialog.setContentView(R.layout.dialog_package_layout);
                            Objects.requireNonNull(dialog.getWindow()).setLayout(DisplayMetricsHandler.getScreenWidth() - 50, -2);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.accept);
                            LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.cancel);
                            TextView text_title = dialog.findViewById(R.id.text_title);
                            final int MESSAGE_DIALOG = ( !verifyPackage.equals(context.getPackageName()) ) ?
                                    R.string.Message_dialo : R.string.Message_dialo_version;
                            text_title.setText(MESSAGE_DIALOG);

                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    listenerDialogOrig.OnOk();
                                    downloadApp(context, verifyPackage);

                                }
                            });
                            linearLayout2.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    dialog.cancel();
                                    listenerDialogOrig.OnCancel();

                                }
                            });

                            //dav
//                            AlertDialog.Builder builder;
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
//                            } else {
//                                builder = new AlertDialog.Builder(context);
//                            }
//                            final int MESSAGE_DIALOG = ( !verifyPackage.equals(context.getPackageName()) ) ? R.string.Message_dialo : R.string.Message_dialo_version;
//                            builder.setTitle(TITLE_DIALOG)
//                                    .setMessage(MESSAGE_DIALOG)
//                                    .setPositiveButton(BUTTON_ACEPT, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            listenerDialogOrig.OnOk();
//                                            downloadApp(context, verifyPackage);
//
//
//                                        }
//                                    })
//                                    .setNegativeButton(BUTTON_CANCEL, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                            listenerDialogOrig.OnCancel();
//                                        }
//                                    })
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @Override
//                                        public void onDismiss(DialogInterface dialogInterface) {
//                                            listenerDialogOrig.OnCancel();
//                                        }
//                                    })
//                                    .show();
//                            isShowing = true;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        asyncTask.execute(URL_SERVER+"api.php?getPackage&id_app="+ID_APP);
    }

    /**
     * Encargado de realizar una peticion
     * @param url - url de la peticion
     * @return - respuesta del servicio
     */
    private static String getJSONString(String url) {
        String jsonString = null;
        HttpURLConnection urlConnection = null;
        try {
            URL linkurl = new URL(url);
            urlConnection = (HttpURLConnection) linkurl.openConnection();
            int code = urlConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream input = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j;
                while ((j = input.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonString;
    }

    private static void downloadApp(Context context, String packageName){
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id="+packageName)));
        } catch (ActivityNotFoundException var4) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id="+packageName)));
        }
    }

    public interface ListenerDialog{
        void OnOk();

        void OnCancel();
    }
}
