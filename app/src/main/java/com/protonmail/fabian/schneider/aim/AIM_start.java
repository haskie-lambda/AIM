package com.protonmail.fabian.schneider.aim;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class AIM_start extends AppCompatActivity {
    static String downURL = "http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt";
    static String errPatt = "-1.00e+05";
    public Context tThis;
    public TextView status;
    private Button startButton;
    public TextView output;
    private CheckBox adOp;
    private CheckBox adOut;
    private RadioButton audioOut;
    private RadioButton btOut;
    private CheckBox notify;
    private CheckBox deamon;
    private CheckBox dispInApp;

    start_AIM calc = new start_AIM();
    private boolean func = false;

    public static String strengthArr[] = new String[4];

    static {
        strengthArr[0] = "90,110,90,110";
        strengthArr[1] = "50,89,111,150";
        strengthArr[2] = "0,49,151,200";
        strengthArr[3] = "-50,-1,201,-250";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aim_start);
        tThis = this;
        calc.init();
        status = (TextView) findViewById(R.id.statusView);
        output = (TextView) findViewById(R.id.lbl_output);

        startButton = (Button) findViewById(R.id.start_aim);
        startButton.setOnClickListener(new View.OnClickListener (){
            public void onClick(View v) {
                if (!func) {
                    func = true;
                    startButton.setText("Stop AIM");
                    status.setText("AIM-Started");
                    calc = new start_AIM();
                    calc.execute();
                } else {
                    func = false;
                    startButton.setText("Start AIM");
                    calc.cancel(false);
                    status.setText("AIM-Stopped");
                }
            }
        });

        //options
        adOp = (CheckBox) findViewById(R.id.cbox_adOP);
        adOut = (CheckBox) findViewById(R.id.cbox_adOut);
        audioOut = (RadioButton) findViewById(R.id.rad_audio);
        btOut = (RadioButton) findViewById(R.id.rad_bt);
        notify = (CheckBox) findViewById(R.id.cbox_disp_mess);
        dispInApp = (CheckBox) findViewById(R.id.cbox_disp);
        deamon= (CheckBox) findViewById(R.id.cbox_cont);

        adOp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!adOp.isChecked()){
                    adOut.setVisibility(View.INVISIBLE);
                    audioOut.setVisibility(View.INVISIBLE);
                    btOut.setVisibility(View.INVISIBLE);
                    notify.setVisibility(View.INVISIBLE);
                    dispInApp.setVisibility(View.INVISIBLE);
                    deamon.setVisibility(View.INVISIBLE);
                } else {
                    adOut.setVisibility(View.VISIBLE);
                    audioOut.setVisibility(View.VISIBLE);
                    btOut.setVisibility(View.VISIBLE);
                    notify.setVisibility(View.VISIBLE);
                    dispInApp.setVisibility(View.VISIBLE);
                    deamon.setVisibility(View.VISIBLE);
                }
            }
        });




        ///options
    }



    class start_AIM extends AsyncTask<String, String, String> {
        private SoundPool soundPool;
        private AudioManager audioManager;
        private static final int max_streams = 1;
        private static final int streamType = AudioManager.STREAM_MUSIC;
        boolean loaded;

        private float volume;

        private int strengthSound[] = new int[5];

        protected void init(){
            System.out.println("started audio initialization");
            //AUDIO
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);
            float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);
            this.volume = currentVolumeIndex/maxVolumeIndex;
            //this.setVolumeControlStream(streamType);


            if(Build.VERSION.SDK_INT >= 21) {
                AudioAttributes audioAttrib = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setAudioAttributes(audioAttrib).setMaxStreams(max_streams);
                this.soundPool = builder.build();
            } else{
                this.soundPool = new SoundPool(max_streams, AudioManager.STREAM_MUSIC, 0);
            }
            //Sound pool load complete
            this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = true;
                    System.out.println("finished loading resources");
                    //publishProgress("finished loading resources");
                    //SoundPool.Builder(new SoundPool.Builder()).play(strengthSound[0],volume,volume,1,0,1f);
                    MediaPlayer mediaPlayer = MediaPlayer.create(tThis, R.raw.strength0);
                    mediaPlayer.start();
                }
            });
            System.out.println("starting tone init");
            this.strengthSound[0] = this.soundPool.load(tThis, R.raw.strength0,1);
            this.strengthSound[1] = this.soundPool.load(tThis, R.raw.strength1,1);
            this.strengthSound[2] = this.soundPool.load(tThis, R.raw.strength2,1);
            this.strengthSound[3] = this.soundPool.load(tThis, R.raw.strength3,1);
            this.strengthSound[4] = this.soundPool.load(tThis, R.raw.strength4,1);
            System.out.println("fin tone init");

        }

        public String doInBackground(String... params){

            while (!this.isCancelled()) {

                System.out.println("before publishProgress Update");
                //publishProgress("Calculation Started");

                dataImport dI;
                try {
                    dI = new dataImport(downURL);
                    String calcData;
                    calcData = dI.download();

                    System.out.println(calcData);
                    preAnalysis pA;
                    pA = new preAnalysis(calcData);
                    boolean dateOk;
                    dateOk = pA.preanalyse();
                    if (dateOk) {
                        System.out.println("Data Date is okay");
                        //call last line downloader
                        dI = new dataImport(downURL);
                        String lastLine;
                        lastLine = dI.downloadLast();
                        if (!lastLine.contains(errPatt)) {
                            //data is ok
                            System.out.println("Current data is okay");
                            System.out.println("Current data: " + lastLine);
                            System.out.println("Starting analysis");

                            analyse an = new analyse(lastLine);
                            int strength;
                            strength = an.analyseData();
                            if (strength != -1) {
                                System.out.println("Strength: " + strength);
                                //outputText(Integer.toString(strength));
                                publishProgress(Integer.toString(strength));
                                //return Integer.toString(strength);


                            } else {
                                System.out.println("Strength not found");
                                //send signal for out of scope to output
                                publishProgress("-1");
                                //return "-1";
                            }
                        } else {
                            System.out.println("Current data is not okay");
                            publishProgress("Current data is not okay");
                            //send signal for satellite down to output
                            publishProgress("-1");
                            //return "-1";
                        }

                    } else {
                        System.out.println("Data Date not okay");
                        publishProgress("Data Date not okay");
                        //send signal for satellite down to output
                        publishProgress("-1");
                        //return "-1";
                    }


                    //catch statements
                } catch (MalformedURLException e) {
                    System.out.println("MalformedUrlException for dI allocation");
                } catch (IOException e) {
                    System.out.println("IOException in dI alloc");

                    //check inet conn/send satellite down to output
                    return "Connection expired";
                }
                //return "ERROR";
            }
            return null;
        }
        @Override protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            output.setText(values[0]);

            if (values[0].equals("Current data is not okay")) {
                status.setText(values[0]);
            } else if (values[0].equals("finished loading resources")) {
                status.setText(values[0]);
            } else {
                //audioOut aO = new audioOut();
                //aO.exec();[
                System.out.println("before BoolCheck: " + loaded);
                //if(true){ //this.loaded
                    System.out.println("before audioOut");
                    int tmpStrength = Integer.parseInt(values[0]);
                    int streamId = audioOutput(tmpStrength);
                //}
            }
        }

        protected int audioOutput(int tmpStrength) {
            System.out.println("audioOut called");
            int actRes;
            //return this.soundPool.play(this.strengthSound[tmpStrength], leftVolumn, rightVolumn, 1, 0, 1f);
            if(tmpStrength == 0) {
                actRes = R.raw.strength1s;
            } else if (tmpStrength == 1) {
                actRes = R.raw.strength2s;
            } else if(tmpStrength == 2) {
                actRes = R.raw.strength3s;
            } else if(tmpStrength == 3) {
                actRes = R.raw.strength4s;
            } else {
                actRes = R.raw.strength0s;
            }
            System.out.println("actRes: " + actRes);
            MediaPlayer mediaPlayerOut = MediaPlayer.create(tThis, actRes);
            mediaPlayerOut.start();
            try{
                Thread.sleep(800);
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception: " + e);
            }
            return 0;
        }

        protected void outputText(String result) {
            output.setText(result);
        }

        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            Log.d("MyApp", "finished");
            outputText(result);
        }


        protected void onPreExecute(String text) {
            status.setText(text);
        }

        protected void onProgressUpdate(String text) {
            super.onProgressUpdate(text);
            Log.d("MyApp", "onProgressUpdate called");


        }


        final class dataImport {
            private String downData = "";
            private URL url = null;
            private String lastLine = "";

            dataImport(String url) throws MalformedURLException {
                URL allocUrl = new URL(url);
                this.url = allocUrl;
            }

            String download() throws IOException {
                URLConnection con = url.openConnection();
                InputStream ins = con.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(ins));

                String line;

                while ((line = br.readLine()) != null) {
                    downData += line;
                    lastLine = line;
                }
                return downData;
            }

            String downloadLast() throws IOException {
                this.download();
                return lastLine;
            }
        }

        class preAnalysis{
            private String data;
            private String dataDate;
            private String sDate;
            private Calendar dateToCheck = Calendar.getInstance();
            private Calendar timeToCheck = Calendar.getInstance();

            preAnalysis(String data){
                this.data = data;
            }

            boolean preanalyse(){
                dataDate = this.getDataDate(data);
                sDate = this.getDataTime(dataDate);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HHmm");


                //TODO make date convert right
                try {
                    dateToCheck.setTime(formatter.parse(dataDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formatter = new SimpleDateFormat("HHmm");
                try {
                    timeToCheck.setTime(formatter.parse(sDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //convert from UT
                Calendar currentDate = Calendar.getInstance();
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.HOUR, -2);
                currentTime.add(Calendar.MINUTE, -5);
                //currentTime.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,Calendar.HOUR -2, Calendar.MINUTE -5);

                System.out.println("dateToCheck: " + dateToCheck + "; current Date: " + currentDate);
                System.out.println("timeToCheck: " + timeToCheck + "; current Time: " + currentTime);
                System.out.println("timeToCheck Time:" + timeToCheck.HOUR + ":" + timeToCheck.MINUTE);
                System.out.println("time checked: " + currentTime.HOUR + ":" + currentTime.MINUTE);
                if(dateToCheck.YEAR == currentDate.YEAR && dateToCheck.MONTH == currentDate.MONTH &&
                        dateToCheck.DAY_OF_MONTH == currentDate.DAY_OF_MONTH &&
                        timeToCheck.HOUR == currentTime.HOUR && timeToCheck.MINUTE == currentTime.MINUTE){      //&& !timeToCheck.before(currentTime.getTime())
                    return true;
                } else {
                    return false;
                }
            }
            private String getDataDate(final String data){
                return data.substring(35,51);
            }

            private String getDataTime(final String dataDate){
                return dataDate.substring(12);
            }
        }


        final class analyse{
            private String data;
            private String[] splittedData = new String[4];
            private Double[] splittedStrength = new Double[4];
            analyse(String data){
                this.data = data;
            }

            int analyseData(){
                data = this.getCalcData();
                this.splitCalcData();
                String temp = splittedData[3];
                double tempSplit = Double.parseDouble(temp);
                int counter = 0;
                for (String i : strengthArr){
                    this.splitStrength(i);
                    if ((tempSplit >= splittedStrength[0] &&
                            tempSplit <= splittedStrength[1]) ||
                            (tempSplit >= splittedStrength[2] &&
                                    tempSplit <= splittedStrength[3])){
                        return counter;
                    }
                    counter += 1;
                }

                return -1;
            }

            private void splitStrength(String strength){
                String[] temp;
                temp = strength.split(",");
                for(int b = 0; b < temp.length; b++){
                    splittedStrength[b] = Double.parseDouble(temp[b]);
                }
            }


            private String getCalcData(){
                return data.substring(37);
            }

            private void splitCalcData(){
                splittedData[0] = data.substring(0,8);
                splittedData[1] = data.substring(13,21);
                splittedData[2] = data.substring(25,33);
                splittedData[3] = data.substring(35);
            }
        }

    } //fin start aim


} //fin main