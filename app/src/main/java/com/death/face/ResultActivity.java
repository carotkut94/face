package com.death.face;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

        final Bitmap bitmap = ((BitmapDrawable) userView.getDrawable()).getBitmap();
        layout.setVisibility(View.VISIBLE);
        EmotionRestClient.init(this, "7816f2e151a04e209ff5248233fa69cf");
        EmotionRestClient.getInstance().detect(bitmap, new ResponseCallback() {
            @Override
            public void onError(String errorMessage) {
                pDialog.dismiss();
                Log.e("LOG", "Error");
            }

            @Override
            public void onSuccess(FaceAnalysis[] response) {
                for (int i = 0; i < response.length; i++) {
                    pDialog.dismiss();
                    Scores scores = response[i].getScores();
                    disgust.setProgress((float) scores.getDisgust());
                    anger.setProgress((float) scores.getAnger());
                    happiness.setProgress((float) scores.getHappiness());
                    sadness.setProgress((float) scores.getSadness());
                    surprise.setProgress((float) scores.getSurprise());
                    contempt.setProgress((float) scores.getContempt());
                    fear.setProgress((float) scores.getFear());
                    neutral.setProgress((float) scores.getNeutral());
                }
            }
        });
    }
}
