package com.protonmail.fabian.schneider.aim;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
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

/**
 * Created by faebl on 08.11.16.
 */

public class AIMServiceMain extends Service {

    private SoundPool soundPool;
    private AudioManager audioManager;
    private static final int max_streams = 1;
    private static final int streamType = AudioManager.STREAM_MUSIC;
    boolean loaded;
    public static String strengthArr[] = new String[4];

    static String downURL = "http://services.swpc.noaa.gov/text/goes-magnetometer-primary.txt";
    static String errPatt = "-1.00e+05";

    private float volume;

    private int strengthSound[] = new int[5];
    static {
        strengthArr[0] = "90,110,90,110";
        strengthArr[1] = "50,89,111,150";
        strengthArr[2] = "0,49,151,200";
        strengthArr[3] = "-50,-1,201,-250";
    }
    //runtime Varialbess
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;





    //Handler for receiving messages from the thread
    private final class ServiceHandler extends Handler{
        public  ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            //work here


            while (true) {

                System.out.println("before publishProgress Update");
                //publishProgress("Calculation Started");

                AIMServiceMain.dataImport dI;
                try {
                    dI = new AIMServiceMain.dataImport(downURL);
                    String calcData;
                    calcData = dI.download();

                    System.out.println(calcData);
                    AIMServiceMain.preAnalysis pA;
                    pA = new AIMServiceMain.preAnalysis(calcData);
                    boolean dateOk;
                    dateOk = pA.preanalyse();
                    if (dateOk) {
                        System.out.println("Data Date is okay");
                        //call last line downloader
                        dI = new AIMServiceMain.dataImport(downURL);
                        String lastLine;
                        lastLine = dI.downloadLast();
                        if (!lastLine.contains(errPatt)) {
                            //data is ok
                            System.out.println("Current data is okay");
                            System.out.println("Current data: " + lastLine);
                            System.out.println("Starting analysis");

                            AIMServiceMain.analyse an = new AIMServiceMain.analyse(lastLine);
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
                    return;
                }
                //return "ERROR";
                try{
                    Thread.sleep(60*1000);
                } catch (InterruptedException e){
                    System.out.println("Interrupted Exception thrown while sleep");
                }
            }




            //stopSelf(msg.arg1); stops the service
        }
    }
    private void aimNotify() {
        NotificationManager manager;
        Notification myNotification;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent("com.protonmail.fabian.schneider.aim.SERVICE");
        PendingIntent pendingIntent = PendingIntent.getActivity(AIMServiceMain.this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(AIMServiceMain.this);
        builder.setAutoCancel(false);
        builder.setTicker("TICKER");
        builder.setContentTitle("AIM-Service");
        builder.setContentText("AIM-Service-Text - Strength");
        //builder.setSmallIcon(R.drawable.myImageFile);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setNumber(100);
        builder.build();

        myNotification = builder.getNotification();
        manager.notify(11, myNotification);

    }

    @Override
    public void onCreate(){
        //start Thread running the service... (background task creation)

        //NOTIFICATION_SERVICE
        //this.aimNotify();
        ///NOTIFICATION

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "AIM starting", Toast.LENGTH_SHORT).show();

        //for each start request send message to start a job and deliver the
        // start ID so we know whitch request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        // if we get killed return after from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        //no binding so null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "AIM shutdown", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }




    //AIM Methods
    protected void publishProgress(String... values) {
        //output.setText(values[0]);

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
        System.out.println("actRes: " + actRes);
        MediaPlayer mediaPlayerOut = MediaPlayer.create(this, actRes);
        mediaPlayerOut.start();
        return 0;
    }

    protected void outputText(String result) {
        //output.setText(result);
        // if app is active, set outputview to the current strength
    }


    final class dataImport {
        private String downData = "";
        private URL url = null;
        private String lastLine = "";

        dataImport(String url) throws MalformedURLException {
            this.url = new URL (url);
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

}
