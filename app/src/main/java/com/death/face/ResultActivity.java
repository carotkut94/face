package com.death.face;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.yongjhih.mismeter.MisMeter;
import com.pixelcan.emotionanalysisapi.EmotionRestClient;
import com.pixelcan.emotionanalysisapi.ResponseCallback;
import com.pixelcan.emotionanalysisapi.models.FaceAnalysis;
import com.pixelcan.emotionanalysisapi.models.Scores;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ResultActivity extends AppCompatActivity {


    ImageView userView;
    RelativeLayout layout;
    ProgressDialog pDialog;
    Bitmap bmp;
    MisMeter anger, happiness, sadness, contempt, fear, neutral, disgust, surprise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        layout = (RelativeLayout) findViewById(R.id.indicator_controller);
        anger = (MisMeter) findViewById(R.id.anger);
        happiness = (MisMeter) findViewById(R.id.happiness);
        sadness = (MisMeter) findViewById(R.id.sadness);
        contempt = (MisMeter) findViewById(R.id.contempt);
        fear = (MisMeter) findViewById(R.id.fear);
        pDialog = new ProgressDialog(ResultActivity.this);
        pDialog.setMessage("Please wait while we calculate");
        neutral = (MisMeter) findViewById(R.id.neutral);
        disgust = (MisMeter) findViewById(R.id.disgust);
        surprise = (MisMeter) findViewById(R.id.surprise);
        userView = (ImageView) findViewById(R.id.profile_image);


        layout.setVisibility(View.INVISIBLE);

        Bundle b = this.getIntent().getBundleExtra("CONTAINER");
        byte[] image = b.getByteArray("IMAGE");
        Log.e("BYTE ARRAY", image.toString());
        bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        pDialog.show();
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        userView.setImageBitmap(bmp);
        getEmotion(userView);
    }
    public void getEmotion(ImageView view) {
        // run the GetEmotionCall class in the background
        GetEmotionCall emotionCall = new GetEmotionCall(view);
        emotionCall.execute();
    }
    public byte[] toBase64(ImageView imgPreview) {
        Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }
    // asynchronous class which makes the api call in the background
    private class GetEmotionCall extends AsyncTask<Void, Void, String> {

        private final ImageView img;

        GetEmotionCall(ImageView img) {
            this.img = img;
        }

        // this function is called before the api call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // this function is called when the api call is made
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpclient = HttpClients.createDefault();
            try {
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                // enter you subscription key here
                request.setHeader("Ocp-Apim-Subscription-Key", "0476f88d50f446b6a229dfc3e986b469");

                // Request body.The parameter of setEntity converts the image to base64
                request.setEntity(new ByteArrayEntity(toBase64(img)));

                // getting a response and assigning it to the string res
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);
                return res;
            }
            catch (Exception e){
                return "null";
            }
        }

        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray = null;
            pDialog.hide();
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";
                // get the scores object from the results
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject scores = jsonObject.getJSONObject("scores");
                    Log.e("SCores", scores.toString());
                    double max = 0;
                    String emotion = "";
                    for (int j = 0; j < scores.names().length(); j++) {
                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = scores.getDouble(scores.names().getString(j));
                            emotion = scores.names().getString(j);
                        }
                    }
                    emotions += emotion + "\n";
                }
                Log.e("Emotions", emotions);
            } catch (JSONException e) {
            }
        }
    }
}
