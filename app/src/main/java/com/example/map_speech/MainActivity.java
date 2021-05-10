package com.example.map_speech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import Model.CountryDataSource;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SPEAK_REQUEST =10;
    TextView txt_value;
    TextView txt_value2;
    Button btn_voice_intent;

    public static CountryDataSource countryDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_value=findViewById(R.id.txtVlue);
        txt_value2=findViewById(R.id.txtValue2);
        btn_voice_intent=findViewById(R.id.btnVoiceIntent);

        btn_voice_intent.setOnClickListener(MainActivity.this);

        Hashtable<String,String> countriesAndMessage = new Hashtable<>();
        countriesAndMessage.put("Iran","Welcome To Iran");
        countriesAndMessage.put("Canada","Welcome To Canada");
        countriesAndMessage.put("France","Welcome To France");
        countriesAndMessage.put("Brazil","Welcome To Brazil");
        countriesAndMessage.put("Japan","Welcome To Japan");
        countriesAndMessage.put("United States","Welcome To United States");


        countryDataSource = new CountryDataSource(countriesAndMessage);
//آیا صدا را ساپورت میکند؟
        PackageManager packageManager = this.getPackageManager();

        List<ResolveInfo> listOfInformation=
                packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        Log.e("listOfInformation : ",String.valueOf(listOfInformation));

        if (listOfInformation.size() > 0){

            Toast.makeText(this, "Support Recognition", Toast.LENGTH_SHORT).show();
            listenToTheUsersVoice();
        } else {

            Toast.makeText(this, "Dose Not Support Recognition", Toast.LENGTH_SHORT).show();
        }

    }

    private void listenToTheUsersVoice() {

        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"TALK TO ME!!");
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);
        startActivityForResult(voiceIntent,SPEAK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SPEAK_REQUEST && resultCode==RESULT_OK){
            ArrayList<String> voiceWords=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confideLevels = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

          /* int index=0;
            for (String userWord : voiceWords){
                if (confideLevels!=null && index<confideLevels.length){
                    txt_value.setText(userWord+" - "+ confideLevels[index]);
                    txt_value2.setText(voiceWords +" - "+ confideLevels);
                }
            }*/
            String countryMachedWithUserWord = countryDataSource.matchWithMinimumConfidenceLevelOfUserWords(voiceWords,confideLevels);

            Intent myMapActivity = new Intent(MainActivity.this, MapsActivity2.class);
            myMapActivity.putExtra(CountryDataSource.COUNTRY_KEY,countryMachedWithUserWord);
            startActivity(myMapActivity);
        }
    }

    @Override
    public void onClick(View v) {

        listenToTheUsersVoice();

    }
}