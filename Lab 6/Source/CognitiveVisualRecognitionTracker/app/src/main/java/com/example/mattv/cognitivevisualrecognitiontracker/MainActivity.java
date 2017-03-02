package com.example.mattv.cognitivevisualrecognitiontracker;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.widget.ImageButton;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.R.attr.bitmap;
import static com.example.mattv.cognitivevisualrecognitiontracker.R.id.imageView;

public class MainActivity extends Activity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static String question = "";
    ImageButton imageButton;
    ImageButton imageButton2;
    final Random rnd = new Random();
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    //t1.setPitch(90);

                }
            }
        });
        ImageView img = (ImageView) findViewById(imageView);
        String str = "img_" + rnd.nextInt(5);
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );
        voiceRecorderButton();
        skipButton();
    }
    public void voiceRecorderButton() {

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                promptSpeechInput();
            }
        });
    }

    public void skipButton() {

        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ImageView img = (ImageView) findViewById(imageView);
                String str = "img_" + rnd.nextInt(6); //possibly make 5?
                img.setImageDrawable
                        (
                                getResources().getDrawable(getResourceID(str, "drawable",
                                        getApplicationContext()))
                        );
                String question = "This is a test";
                System.out.println(question);
                //Test Clarifai API call
                ClarifaiAPI();
                //Send to HTTP Server Test
                httpPostTest();
            }
        });
    }
    public void ClarifaiAPI()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);//REMOVE for async process
        ImageView img = (ImageView) findViewById(imageView);
        Bitmap icon=((BitmapDrawable)img.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream); //bm is the bitmap object
        byte[] byteArray = stream.toByteArray();

        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            final ClarifaiClient client = new ClarifaiBuilder("KKQIegBW9uOl_3vaMSzqq4QCfPNyNBvB7XNBz1vE", "xsY48eiDhhsFo5M7HE3F71ZYkB_tEQmemlWekTgG")
                    .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                    .buildSync(); // or use .build() to get a Future<ClarifaiClient>
            client.getToken();
            ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                    .withInputs(
                            //ClarifaiInput.forImage(ClarifaiImage.of(encodedImage)) //PASS BYTES
                            ClarifaiInput.forImage(ClarifaiImage.of(byteArray))
                    )
                    .executeSync();
            List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
            if (predictions.isEmpty()) {
                System.out.println("No Predictions");
            }
            List<Concept> data = predictions.get(0).data();
            for (int i = 0; i < data.size(); i++) {
                System.out.println(data.get(i).name() + " - " + data.get(i).value());
                //image.drawText(data.get(i).name(), (int)Math.floor(Math.random()*x), (int) Math.floor(Math.random()*y), HersheyFont.ASTROLOGY, 20, RGBColour.RED);
            }
            data.clear();
            predictions.clear();
            byteArray = null;
            icon = null;
            stream = null;
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}


    }
    private void httpPostTest()
    {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding

                // Read data on the worker thread
                final String responseData = response.body().string();
                System.out.println(responseData);
                //Toast.makeText(MainActivity.this, responseData, Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "What is your question?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this,
                    "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                   /* Toast.makeText(MainActivity.this,
                            "You said '" + result.get(0) + "'.", Toast.LENGTH_LONG).show();
                    }*/
                    question = result.get(0);
                    String toSpeak = question;
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            }
        }
    }

    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }

}
