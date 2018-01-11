package com.sanganan.app.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


//import com.sun.jersey.core.util.Base64;

public class Utility {

    final public static String JSONPOSTING = "json";
    final public static String IMAGEPOSTING = "image";
    final public static String NAMEVALUEPAIRPOSTING = "namevalue";
    final public static String NORMALPOSTING = "none";
    final public static String DOWNLOADIMAGE = "downloadimage";

    public static String message;
    private static SharedPreferences sharedPreferences;
    /// private static String baseUrl = "http://www.trendcues.com/AreaAlert/";
    private static String baseUrl = "http://43.251.85.66:90/area-alert/service/";
    //private static String baseUrl1 = "192.168.1.217:8080/AreaAlert/";

    //private static final String MODE_PRIVATE = null;
    int PRIVATE_MODE = 0;

    // https://www.imsasllc.com/api/v3/data/

    // Context cnt;

    private Context _context;



    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }



    public static void showOkAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



//import com.sun.jersey.core.util.Base64;


        {
            if (message != null && message.trim().length() > 0) {
                baseUrl = message + "/";
                System.out.println("message is in static block :" + message);
                System.out.println("message is in static block :" + baseUrl);
            }

        }

        public Utility() {

        }


        public Utility(Context context) {
            this._context = context;

        }

        public static String writeData(Context con) {
            try {
                sharedPreferences = con.getSharedPreferences("CHANGEURL", Context.MODE_PRIVATE);
                if (sharedPreferences.contains("message")) {
                    message = sharedPreferences.getString("message", "There is no data availiale ...");
                    Log.v("Saved Value is:::: ", message);
                } else {
                    Log.v("Saved Value Error", "There is no data availiale ....");
                }
            } catch (Exception ex) {
                System.out.println("Exception in WriteData methos in Utility Class :" + ex);
            }
            return message;
        }

        public static String readUrlResponseAsString(HttpResponse response) {
            String data = "";
            try {
                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));

                System.out.println("rd is in readurlrespose :" + rd);
                String line;

                while ((line = rd.readLine()) != null) {
                    data += line;
                    //System.out.println("read url in data line :" + line);
                    //System.out.println("read url in data :" + data);

                }
            } catch (Exception ex) {
                data = null;
                System.out.println("Exception in readUrlResponseAsString method :"
                        + ex);
            }

            return data;
        }


        public static HttpResponse postDataOnUrl(String url, List<NameValuePair> nameValuePairs) {
            HttpResponse response;
            try {
                System.out.println(" url  :" + url);
                HttpHandler handler = HttpHandler.getInstance();
                HttpClient httpclient = handler.getHttpClient();
                BasicCookieStore cookieStore = handler.getBasicCookieStore();
                HttpContext httpContext = handler.getHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                //url = "http://192.168.1.39:8080/Rltix/Iphone?operation=authenticate&user=subrato&password=subrato";

                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost, httpContext);
                //response = httpclient.execute(httppost);
                return response;

                //System.out.println("user name response as url :" + response.toString());
            } catch (Exception ex) {
                //response = null;
                System.out.println("Exception in postDataOnUrl : " + ex);
                return null;
            }

        }

        public static HttpResponse postDataOnUrl(String url, String jsonString) {
            HttpResponse response;
            try {

                HttpHandler handler = HttpHandler.getInstance();
                HttpClient httpclient = handler.getHttpClient();
                BasicCookieStore cookieStore = handler.getBasicCookieStore();
                HttpContext httpContext = handler.getHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                HttpPost httppost = new HttpPost(url);
                //System.out.println("json string is in :" +jsonString);
                StringEntity entity = new StringEntity(jsonString);
                entity.setContentType("application/json;charset=UTF-8");
                entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httppost.setEntity(entity);

                response = httpclient.execute(httppost, httpContext);
                //System.out.println("response as url :" + response.toString());
            } catch (Exception ex) {
                response = null;
                System.out
                        .println("Exception in postDataOnUrl(String url, JSONObject obj) : "
                                + ex);
            }
            return response;
        }

        public static HttpResponse readDataFromUrl(String url) {
            HttpResponse response;
            try {
                HttpHandler handler = HttpHandler.getInstance();
                HttpClient client = handler.getHttpClient();
                BasicCookieStore cookieStore = handler.getBasicCookieStore();
                HttpContext httpContext = handler.getHttpContext();

                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                HttpGet request = new HttpGet(url);
                response = client.execute(request, httpContext);
            } catch (Exception ex) {
                response = null;
                System.out.println("Exception in readDataFromUrl : " + ex);
            }
            return response;
        }

        public static String getBaseUrl() {
            return baseUrl;
        }

        public static HttpResponse uploadFile(String url, String jsonString,
                                              String contentType) {
            HttpResponse response;
            try {
                HttpHandler handler = HttpHandler.getInstance();
                HttpClient httpclient = handler.getHttpClient();
                BasicCookieStore cookieStore = handler.getBasicCookieStore();
                HttpContext httpContext = handler.getHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                HttpPost httppost = new HttpPost(url);
                httppost.setHeader("Content-Type", contentType); // contentType =
                // "image/png"
                httppost.setHeader("Content-Transfer-Encoding", "base64");

                response = httpclient.execute(httppost, httpContext);
                System.out.println("response as url :" + response.toString());
            } catch (Exception ex) {
                response = null;
                System.out
                        .println("Exception in postDataOnUrl(String url, JSONObject obj) : "
                                + ex);
            }
            return response;
        }

        public static Bitmap downloadImage(String urll) {
            Bitmap bitmap = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                HttpResponse response;
                HttpHandler handler = HttpHandler.getInstance();
                HttpClient httpclient = handler.getHttpClient();
                BasicCookieStore cookieStore = handler.getBasicCookieStore();
                HttpContext httpContext = handler.getHttpContext();
                httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                HttpPost httppost = new HttpPost(urll);

                response = httpclient.execute(httppost, httpContext);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
                InputStream in = bufHttpEntity.getContent();
                bitmap = BitmapFactory.decodeStream(in);
                if (in != null) {
                    in.close();
                } else {
                    System.out.println("input stream is null :");
                }
            } catch (Exception e1) {
                System.out.println("Exception in download bitmap image :" + e1);
            }
            return bitmap;
        }

        public static String getBase64String(String baseFileUri) {
            String encodedImageData = "";
            JSONObject jobj = new JSONObject();

            System.out.println("getBase64String method is called :" + baseFileUri);


            try {

                //get to base64
                Bitmap bm = BitmapFactory.decodeFile(baseFileUri);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); //bm is the bitmap object
                bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] b = baos.toByteArray();
                //encodedImageData = Base64.encode(baseFileUri);
                encodedImageData = Base64.encodeToString(b, Base64.DEFAULT);
                ArrayList<NameValuePair> imagearraylistvalue = new
                        ArrayList<NameValuePair>();
                imagearraylistvalue.add(new BasicNameValuePair("media",
                        encodedImageData));


                //System.out.println("encode data in upload file :"+ encodedImageData);
            } catch (Exception ex) {
                System.out.println("Exception in getBase64String method in Utility class :" + ex);

            }
            return encodedImageData;

        }

	/*public static String getBase64VideoString(String baseFileUri) {
		
		String encodedVideoData = "";
		JSONObject jobj = new JSONObject();
		
		System.out.println("getBase64String encodedVideoData method is called :"	+ baseFileUri);
		         
		       
			try {
				 FileInputStream fileInputStream = new FileInputStream(new File(baseFileUri) );
				 int bytesRead, bytesAvailable, bufferSize;
			        byte[] buffer;
			        int maxBufferSize = 1*1024*1024;
			        bytesAvailable = fileInputStream.available();
			         bufferSize = Math.min(bytesAvailable, maxBufferSize);
			         buffer = new byte[bufferSize];
			         // read file and write it into form...
			         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			         System.out.println("byteread is in :" +bytesRead);
			         
			         while (bytesRead > 0)
			         {
			         
			          bytesAvailable = fileInputStream.available();
			          bufferSize = Math.min(bytesAvailable, maxBufferSize);
			          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			          
			          System.out.println("byteread is in i:" +bytesRead);
			          
			          //encodedVideoData = Integer.parseInt(bytesRead);
			          
			         }
			         // send multipart form data necesssary after file data...
			       
			         // close streams
			         Log.e("Debug","File is written");
			         fileInputStream.close();

				    //get to base64
		
			//System.out.println("encode data in upload file :"+ encodedImageData);
		} 
			
		        
		    
		
		        catch (Exception ex) 
		        {
			System.out.println("Exception in getBase64VideoString method in Utility class :"+ ex);
		
		}
		return encodedVideoData;
		
	}*/


        public static int uploadFile(String sourceFileUri, String upLoadServerUri) {
            // upLoadServerUri = "http://api.assettracker.co/user/upload";
            int serverResponseCode = 0;

            try {
                Bitmap bm = BitmapFactory.decodeFile(sourceFileUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); // bm is the
                // bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                // ArrayList<NameValuePair> imagearraylistvalue = new
                // ArrayList<NameValuePair>();
                // imagearraylistvalue.add(new BasicNameValuePair("image",
                // encodedImage));

                System.out.println("encode data in upload file :" + encodedImage);
            } catch (Exception ex) {
                System.out.println("Exception in uploa d file in Utility:" + ex);
            }


            return serverResponseCode;
        }

        public boolean isConnectingToInternet() {
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
            return false;
        }
   /* public static void showOkAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }*/
    }

