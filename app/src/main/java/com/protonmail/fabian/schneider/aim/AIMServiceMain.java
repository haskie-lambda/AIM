package com.protonmail.fabian.schneider.aim;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.os.Process;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.google.gson.Gson;

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
import java.util.Locale;

/**
 * Created by faebl on 08.11.16.
 */

public class AIMServiceMain extends Service {
    NotificationManager manager;
    Notification myNotification;


    //Setting specific stuff
    public sSetting configuration;

    ///Setting specific stuff

    private boolean silence = false;
    private SoundPool soundPool;
    private AudioManager audioManager;
    private static final int max_streams = 1;
    private static final int streamType = AudioManager.STREAM_MUSIC;
    boolean loaded;
    private float volume;

    //runtime Varialbess
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private int strengthSound[] = new int[5];


    //Handler for receiving messages from the thread
    private final class ServiceHandler extends Handler{
        public  ServiceHandler(Looper looper){
            super(looper);
        }


        @Override
        public void handleMessage(Message msg){

            //System.out.println("config_check in Message-Handler: " + Boolean.toString(configuration.reps==-1)); TODO: NPE with configuration.reps why??
            PhoneStateListener phoneStateListener= new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber){
                    if(state==TelephonyManager.CALL_STATE_RINGING){
                        //incoming call - stop service
                        silence = true;
                    } else if(state == TelephonyManager.CALL_STATE_IDLE){
                        //not in call - resume service
                        silence = false;
                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                        //call dialing
                        silence = true;
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

            //work here
            SharedPreferences prefs = getSharedPreferences("configuration",MODE_PRIVATE);
            System.out.println("actual configuration.reps:" + configuration.reps);
            if(configuration.reps==-1){
                while(prefs.getString("serviceStatus","stop").equals("run")) {
                    descTree();
                    System.out.println("While-loop");
                }
            } else {
                Integer i;
                for(i=0;i<=configuration.reps;i++){

                    descTree();
                }
            }
            stopSelf(msg.arg1);
            if (mgr != null){

            }
        }
    }

    private void descTree(){
        //System.out.println("before publishProgress Update");
        //publishProgress("Calculation Started");

        AIMServiceMain.dataImport dI;
        try {
            //get Data
            if(configuration.getTypeName().equals(constants.ST_CONF_TYPE_RT_DATA)){        //datatype == liveDataSource
                if(configuration.sourceConfig.parentSourceType.equals(constants.PST_ONLINE)){
                    dI = new AIMServiceMain.dataImport(configuration.sourceConfig.source);
                    String calcData = dI.download();

                    AIMServiceMain.preAnalysis pA;

                    long dateTimeRange;
                    if(configuration.validDateRange!=-1){                                                   //convert from date range like last 5 mins to a specific timestamp
                        dateTimeRange = System.currentTimeMillis() / 1000L - configuration.validDateRange;
                    } else {
                        dateTimeRange = configuration.validDateTimeFrom;
                    }
                    pA = new AIMServiceMain.preAnalysis(calcData, dateTimeRange, configuration.dateLocInFile, configuration.timeLocInFile);


                    boolean dateOk = true;
                    if(configuration.validRequired){
                        dateOk = pA.preanalyse();
                    }

                    System.out.println("dateOK: " + dateOk);
                    if(dateOk){
                        String lineToAnalyse;
                        if(configuration.specialLine.equals(constants.DATA_ANALYSIS_SPECIALLINE_LAST_LINE)){
                            lineToAnalyse = dI.downloadLast();
                        } else if(configuration.specialLine.equals(constants.DATA_ANALYSIS_SPECIALLINE_FIRST_LINE)){
                            lineToAnalyse = dI.downloadLast();
                        } else if(configuration.specialLine.equals(constants.DATA_ANALYSIS_SPECIALLINE_NO)){
                            lineToAnalyse = dI.download(configuration.lineOfFile);
                        } else {
                            lineToAnalyse = configuration.sourceConfig.errorPattern; //Error handling for broken object
                        }

                        System.out.println("lineToAnalyse: " + lineToAnalyse);
                        if(!lineToAnalyse.contains(configuration.sourceConfig.errorPattern)){
                            //TODO: get algorithm set and decide weather default or other...

                                //DEFAULT
                                int[] temp = new int[2];
                                temp[0] = configuration.restFrom;
                                temp[1] = configuration.restTo;

                                AIMServiceMain.analyse an = new AIMServiceMain.analyse(lineToAnalyse, temp);
                                int strength;
                                strength = an.analyseData();
                                ///DEFAULT

                            if (strength != -1) {
                                System.out.println("Strength: " + strength);
                                publishProgress(Integer.toString(strength));
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
                } else if(configuration.sourceConfig.parentSourceType.equals("offlineFile")){
                    System.out.println("            ParentSourceType == offlineFile");
                } else {//...
                    System.out.println("            ParentSourceType == unknown");
                }


            } else {                        //data Type == fileDataSource

                System.out.println("dataType == fileDataSource");

            }


            //catch statements
        } catch (MalformedURLException e) {
            System.out.println("MalformedUrlException for dI allocation");
        } catch (IOException e) {
            System.out.println("IOException in dI alloc");

            e.printStackTrace();

            boolean nA = networkAvaiable();
            if (!nA){
                //stop service publish no conn
                if(configuration.disableOnDisconnect) {
                    SharedPreferences prefs = getSharedPreferences("configuration", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString("serviceStatus", "stop").commit();

                }


            }
            return;
        }
        //return "ERROR";
        try{
            Thread.sleep(configuration.repTime);
        } catch (InterruptedException e){
            System.out.println("Interrupted Exception thrown while sleep");
        }
    }

    private boolean networkAvaiable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    };

    private void aimNotify() {

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent("com.protonmail.fabian.schneider.aim.SERVICE");
        PendingIntent pendingIntent = PendingIntent.getActivity(AIMServiceMain.this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(AIMServiceMain.this);
        builder.setAutoCancel(false);
        builder.setTicker("TICKER");
        builder.setContentTitle("AIM-Service");
        builder.setContentText("AIM-Service-Text - Strength");
        builder.setSmallIcon(R.drawable.rect);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setNumber(100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(constants.NOTIFICATION_ID, myNotification);

    }

    @Override
    public void onCreate(){
        //start Thread running the service... (background task creation)

        //NOTIFICATION_SERVICE
        this.aimNotify();
        ///NOTIFICATION

        System.out.println("started audio initialization");
        //AUDIO
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);
        this.volume = currentVolumeIndex/maxVolumeIndex;
        //this.setVolumeControlStream(streamType);


        setConfiguration();

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
                //Toast.makeText(this, "Finished loading resources", Toast.LENGTH_SHORT).show();
                /*MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.strength0);
                MediaPlayer mediaPlayer2 = MediaPlayer.create()
                mediaPlayer.start();*/
            }
        });
        System.out.println("starting tone init");
        this.strengthSound[0] = this.soundPool.load(this, R.raw.strength0,1);
        this.strengthSound[1] = this.soundPool.load(this, R.raw.strength1,1);
        this.strengthSound[2] = this.soundPool.load(this, R.raw.strength2,1);
        this.strengthSound[3] = this.soundPool.load(this, R.raw.strength3,1);
        this.strengthSound[4] = this.soundPool.load(this, R.raw.strength4,1);
        System.out.println("fin tone init");


        //System init
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        //get handlerthread's looper and use it for handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }


    protected void setConfiguration(){
        SharedPreferences prefs = getSharedPreferences(constants.SHAREDPREF_CONFIG, MODE_PRIVATE);
        String configName = prefs.getString(constants.SHAREDPREF_ACTUAL_CONFIG, "");
        if(!configName.contains(constants.CONF_PREFIX)){
            configName = constants.CONF_PREFIX + configName;
        }
        Gson gson = new Gson();
        String json;
        json = prefs.getString(configName, "");
        configuration = gson.fromJson(json, sSetting.class);
        System.out.println("config_check after alloc: " + String.valueOf(configuration.repTime));
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){   //Service Startup
        Toast.makeText(this, "AIM starting", Toast.LENGTH_SHORT).show();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        // if we get killed return after from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "AIM shutdown", Toast.LENGTH_SHORT).show();
        manager.cancel(constants.NOTIFICATION_ID);
        super.onDestroy();
    }




    //AIM Methods
    protected void publishProgress(String... values) {
        if (values[0].equals("Current data is not okay")) {
            //status.setText(values[0]);
            //play no sound so i know that satellites are down

        } else if (values[0].equals("finished loading resources")) {
            //status.setText(values[0]);
            //play no sound so i know that satellites are down

        } else {
            System.out.println("before BoolCheck: " + loaded);
            System.out.println("before audioOut");

            int tmpStrength = Integer.parseInt(values[0]);
            int streamId = audioOutput(tmpStrength);
            //keep variable for termination
        }
    }

    protected void updateMainUI(){

    }

    protected int audioOutput(int tmpStrength) {
        System.out.println("audioOut called");
        int actRes;
        //return this.soundPool.play(this.strengthSound[tmpStrength], leftVolumn, rightVolumn, 1, 0, 1f);
        if (tmpStrength == 1) {
            actRes = R.raw.strength1s;
        } else if(tmpStrength == 2) {
            actRes = R.raw.strength2s;
        } else if(tmpStrength == 3) {
            actRes = R.raw.strength3s;
        } else {
            actRes = R.raw.strength4s;
        }
        // UPDATE UI
        Intent intent = new Intent("STRENGTH");
        intent.putExtra("STRENGTH", (double) tmpStrength);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        ///UPDATE UI

        System.out.println("actRes: " + actRes);
        MediaPlayer mediaPlayerOut = MediaPlayer.create(this, actRes);
        if(!silence){mediaPlayerOut.start();}
        return 0;
    }




    //------------------------------DATA IMPORT ------------------------------------------

    final class dataImport {
        private String downData = "";
        private URL url = null;
        private String lastLine = "";
        private String firstLine = "";
        private String numberLine = "";
        private int lineNumber;

        dataImport(String url) throws MalformedURLException {
            this.url = new URL (url);
        }

        String download() throws IOException {
            URLConnection con = url.openConnection();
            InputStream ins = con.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(ins));

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if(i==lineNumber){
                    numberLine = line;
                } else {
                    firstLine = line;
                }
                downData += line;
                lastLine = line;
            }
            return downData;
        }

        String download(int lineNumber) throws IOException{
            this.lineNumber = lineNumber;
            this.download();
            return numberLine;
        }

        String downloadLast() throws IOException {
            this.download();
            return lastLine;
        }

        String downloadFirst() throws IOException {
            this.download();
            return firstLine;
        }

    }






    //-----------------------------------------PREANALYSIS------------------------------------

    class preAnalysis{
        private String data;
        private String dataDate;
        private String sDate;
        private Calendar dateToCheck = Calendar.getInstance();
        private Calendar timeToCheck = Calendar.getInstance();
        private Long dateTimeFrom = System.currentTimeMillis() / 1000L - 5*60;

        private int[] dateLocInFile;
        private int[] timeLocInFile;

        preAnalysis(String data, Long dateTimeFrom, int[] dateLocInFile, int[] timeLocInFile){
            this.data = data;
            this.dateTimeFrom = dateTimeFrom;
            this.dateLocInFile = dateLocInFile;
            this.timeLocInFile = timeLocInFile;
        }

        boolean preanalyse(){
            dataDate = this.getDataDate(data);
            sDate = this.getDataTime(dataDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMM dd HHmm", Locale.US);

            try {
                dateToCheck.setTime(formatter.parse(dataDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formatter = new SimpleDateFormat("HHmm", Locale.US);
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
            System.out.println("timeToCheck Time:" + timeToCheck.getTime());
            System.out.println("time checked: " + currentTime.getTime());

            if(dateToCheck.getTime().getYear() == currentDate.getTime().getYear() && dateToCheck.getTime().getMonth() == currentDate.getTime().getMonth() &&
                    dateToCheck.getTime().getDay() == currentDate.getTime().getDay() &&
                    timeToCheck.getTime().getHours() == currentTime.getTime().getHours() && timeToCheck.getTime().getMinutes() == currentTime.getTime().getMinutes()){      //&& !timeToCheck.before(currentTime.getTime())
                return true;
            } else {
                return false;
            }
        }
        private String getDataDate(final String data){
            return data.substring(dateLocInFile[0],dateLocInFile[1]);
        }

        private String getDataTime(final String dataDate){
            return dataDate.substring(timeLocInFile[0], timeLocInFile[1]);
        }
    }







    //---------------------------------------------------ANALYSE---------------------------------------------


    final class analyse{
        private String data;
        private int[] dataFromTo;
        private String[] splittedData = new String[4];
        private Double[] splittedStrength = new Double[4];
        analyse(String data, int[] dataFromTo){
            this.data = data;
            this.dataFromTo = dataFromTo;
        }

        int analyseData(){
            System.out.println("in c.Analyse>analyseData>v.data 01: " + data);
            data = this.getCalcData();
            //this.splitCalcData();
            System.out.println("in c.Analyse>analyseData>v.data: " + data);
            String temp = data; //splittedData[3];
            double tempSplit = Double.parseDouble(temp);
            int counter = 0;
            int[] checkLine;
            for (int i=0;i<=configuration.strengthArray.size();i++){
                checkLine = configuration.splitStrenthArray(i);
                if(tempSplit>=checkLine[0] && tempSplit<=checkLine[1]){
                    return checkLine[2];
                }
                counter++;
            }
            return -1;
        }

        private void splitDoubleStrengthArray(String strength){
            String[] temp;
            temp = strength.split(",");
            for(int b = 0; b < temp.length; b++){
                splittedStrength[b] = Double.parseDouble(temp[b]);
            }
        }


        private String getCalcData(){
            return data.substring(dataFromTo[0],dataFromTo[1]);
        }

        private void splitCalcData(){
            splittedData[0] = data.substring(0,8);
            splittedData[1] = data.substring(13,21);
            splittedData[2] = data.substring(25,33);
            splittedData[3] = data.substring(35);
        }
    }

}
