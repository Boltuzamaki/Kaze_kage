package in.ac.iiitdmj.coola.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.gson.Gson;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

import in.ac.iiitdmj.coola.myapplication.CameraFiles.CameraConfig;
import in.ac.iiitdmj.coola.myapplication.CameraFiles.CameraError;
import in.ac.iiitdmj.coola.myapplication.CameraFiles.HiddenCameraActivity;
import in.ac.iiitdmj.coola.myapplication.CameraFiles.HiddenCameraUtils;
import in.ac.iiitdmj.coola.myapplication.Cameraconfig.CameraFacing;
import in.ac.iiitdmj.coola.myapplication.Cameraconfig.CameraFocus;
import in.ac.iiitdmj.coola.myapplication.Cameraconfig.CameraImageFormat;
import in.ac.iiitdmj.coola.myapplication.Cameraconfig.CameraResolution;
import in.ac.iiitdmj.coola.myapplication.Cameraconfig.CameraRotation;

public class MainActivity extends HiddenCameraActivity {


    Button manufacturingButton;
    Button bestBeforeButton;
    Button identifyObjectButton;
    Button textRecognizeButton;
    Button openCameraButton, autoCaptureButton;
    Button remindMeButton;
    TextView txt_view;

//    ---------String Values Stored--------

    String values = "";

    private String API_KEY = "97858fdaceab41d7a900634f8f78019b";
    private String API_LINK = "https://eastasia.api.cognitive.microsoft.com/vision/v1.0";


    VisionServiceClient visionServiceClient =  new VisionServiceRestClient(API_KEY,API_LINK);


    //  ------------------ Auto capture Feature -----------------

    private static final int REQ_CODE_CAMERA_PERMISSION = 1253;

    private CameraConfig mCameraConfig;

    //  ----  Text Recognition ----
    private String string = "";

    // ------------ Voice command initialization ----------
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech myTTS;
//    private SpeechRecognizer mySpeech;

    private float currentSpeed = 1.0f;
//*-***-**-**-**-*-**-*-*-*-*-*-*-*-*-*-**-*****--**-*-*-*-**-*-**-*-*-*-*-

//  ------------------ Custom Image Recognition -----------------

    private ImageView mImageView;
    private Bitmap mSelectedImage;
    //    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;


//    Name of the model file hosted with Firebase.

    private static final String HOSTED_MODEL_NAME = "cloud_model_2";
    private static final String LOCAL_MODEL_ASSET = "mobilenet_v1_1.0_224_quant.tflite";

    //     Name of the label file stored in Assets.
    private static final String LABEL_PATH = "labels.txt";

    //      Number of results to show in the UI.
    private static final int RESULTS_TO_SHOW = 3;

    //     Dimensions of inputs.
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;

    //     Labels corresponding to the output of the vision model
    private List<String> mLabelList;

    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float>
                                o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
    /* Preallocated buffers for storing image data. */
    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];


    /**
     * An instance of the driver class to run model inference with Firebase.
     */
    private FirebaseModelInterpreter mInterpreter;
    /**
     * Data configuration of input & output data of model.
     */
    private FirebaseModelInputOutputOptions mDataOptions;


// *-*-*-*-**-*-**-*-*-*-*-*-*--*-*-**-*-**-*--*--*-*-*-*-*---*-*-*-**--**-*-*-**-*---*

    // --- for selecting image from gallery  ---
    private int REQUEST_CODE = 1;

    // --- for directly capturing image from camera  ----
    private final int TEXT_RECO_CODE = 100;

    // for expiry date detection
    private boolean expiry, bestDuration;

// *-**-*-*-*-*-***-*-**-**-*-**-**-*-*-**-*-*-*-*-***-*-*-*-*-*--*-*-**-*-**-*--*--*-*-*-*-*---*-*-*-**--**-*-*-**-*---*-*-*-**--**-*-*-**-*---*


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//  ----- Custom Model Variables -----

        mImageView = findViewById(R.id.image_view);
        txt_view = findViewById(R.id.txt);

//        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        manufacturingButton = findViewById(R.id.mfg);
        bestBeforeButton = findViewById(R.id.bestBefore);
        textRecognizeButton = findViewById(R.id.textRecognize);
        identifyObjectButton = findViewById(R.id.identifyObject);
        autoCaptureButton = findViewById(R.id.autoCapture);
        openCameraButton = findViewById(R.id.openCamera);
        remindMeButton = findViewById(R.id.remindMe);
//   ----------------------------- Button Functions ------------------------


        manufacturingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    expiry = true;
                    runTextRecognition();
                } catch (Exception e) {
                    // No Image to deal with
                    speak("No image Found. PLease choose image first");
                }
            }
        });

        bestBeforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bestDuration = true;
                    runTextRecognition();
                } catch (Exception e) {
                    // No Image to deal with
                    speak("No image Found. PLease choose image first");
                }
            }
        });

        identifyObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //identify object
                try {
                    // describe image
                    describeImage();
                } catch (Exception e) {
                    // No Image to deal with
                    speak("No image Found. PLease choose image first");
                }


            }
        });

        textRecognizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // recognize text in image
                try {
                    runTextRecognition();
                } catch (Exception e) {
                    // No Image to deal with
                    speak("No image Found. PLease choose image first");
                }

            }
        });

        autoCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        remindMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int period = 0;
                // reminds when expiry date of product is near

              // ToDo:- TEMP CODE NEED TO MODIFIED ACCORDING TO MANUFACTURING DATE AS STARTDATE
                Calendar startDate = Calendar.getInstance();
                addEvent(startDate, period, "Expiry Expiry", " Expiry of this product within " + values);
            }
        });

//        ----------------------------------- Check Permission -----------------------------------
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Intent startVoiceChat = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            startVoiceChat.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startVoiceChat.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
//            mySpeech.startListening(startVoiceChat);

        }


// -----------------------------------Voice Chat-------------------------------

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


// -----------------------------------  Auto Capture -------------------------------


        mCameraConfig = new CameraConfig()
                .getBuilder(this)
                .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_90)
                .setCameraFocus(CameraFocus.CONTINUOUS_PICTURE)
                .build();


        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQ_CODE_CAMERA_PERMISSION);
        }


// *-**-*-*-*-*-***-*-**-**-*-**-**-*-*-**-*-*-*-*-***-*-*-*-*-*--*-*-**-*-**-*--*--*-*-*-*-*---*-*-*-**--**-*-*-**-*---*-*-*-**--**-*-*-**-*---*


        initializeTTS();
        initCustomModel();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    //  -------------------------------- Notification ----------------------------------
    private void addNotification(String messageText, String title) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void addEvent(Calendar startDate, int period, String title, String content) {



        try {

            long calID = 3; // Make sure to which calender you want to add event
            long startMillis = 0;
            long endMillis = 0;
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(startDate.YEAR, startDate.MONTH, startDate.DATE, startDate.HOUR_OF_DAY+1, 00);
            startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(startDate.YEAR , startDate.MONTH , startDate.DATE, 11, 59);
            endMillis = endTime.getTimeInMillis();


            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.DESCRIPTION, content);
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());

        } catch (Exception e) {

        }
    }

//    -----------------------------********************************-------------------------------


    @Override
    public void onPause() {
        super.onPause();
        myTTS.stop();
    }

    private void initializeTTS() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if (myTTS.getEngines().size() == 0)
                {
                    Toast.makeText(MainActivity.this, "NO engine available", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    speak("Hello! I am ready");
                }
            }
        });
    }

    private void speak(String message) {

        if (Build.VERSION.SDK_INT >= 21){
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else{
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null);
        }
    }


    private void increaseSpeed() {
        currentSpeed = currentSpeed + 0.1f;
        myTTS.setSpeechRate(currentSpeed);
        speak("Now Current speech rate is " + currentSpeed );
    }

    private void decreaseSpeed() {
        currentSpeed = currentSpeed - 0.1f;
        speak("Now Current speech rate is " + currentSpeed );

    }

//  *-*-*-*-*-*-*-**-*-*--*-***-*****-*-*---**-------***-***-*-*-*

    // Custom gallery image and camera option
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK )
        {

            try{
                Uri uri = data.getData();

                mSelectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                parseBitmap();

            }
            catch(Exception e)
            {
                Toast.makeText(this, "cannot decode imageView from bitmap", Toast.LENGTH_SHORT).show();
            }

        }
        else if (requestCode == TEXT_RECO_CODE){


            if (resultCode == RESULT_OK)
            {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    mSelectedImage = photo;
                    mImageView.setImageBitmap(photo);
                    parseBitmap();
                }
                catch (Exception e){
                    Toast.makeText(this, "Fail to get data from capure image", Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode ==RESULT_CANCELED)
            {
                Toast.makeText(this, "Operation cancelled by user", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Failed to Capture Image", Toast.LENGTH_SHORT).show();
            }


        }
        else{

            String s = String.valueOf(REQUEST_CODE+requestCode);
            String s1 = String.valueOf(RESULT_OK+resultCode);
            Toast.makeText(this, "Image path not found :-" + s + "   "+ s1, Toast.LENGTH_SHORT).show();

            return;



        }
    }



    public void captureImage(){


        Intent captureImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureImg,TEXT_RECO_CODE);
    }

    public void parseBitmap(){

        if (mSelectedImage != null) {

            Toast.makeText(this, "Image Displayed", Toast.LENGTH_SHORT).show();
            speak("Image captured");
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }


    }



//    ------------------------------------Custom Model Functions -------------------------------------


    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =  mImageView.getHeight();
        }

        return mImageMaxHeight;
    }


    private void initCustomModel() {
        mLabelList = loadLabelList(this);

        int[] inputDims = {DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE};
        int[] outputDims = {DIM_BATCH_SIZE, mLabelList.size()};
        try {
            mDataOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.BYTE, inputDims)
                            .setOutputFormat(0, FirebaseModelDataType.BYTE, outputDims)
                            .build();
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions
                    .Builder()
                    .requireWifi()
                    .build();
            FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder
                    (HOSTED_MODEL_NAME)
                    .enableModelUpdates(true)
                    .setInitialDownloadConditions(conditions)
                    .setUpdatesDownloadConditions(conditions)  // You could also specify
                    // different conditions
                    // for updates
                    .build();
            FirebaseLocalModel localModel =
                    new FirebaseLocalModel.Builder("asset")
                            .setAssetFilePath(LOCAL_MODEL_ASSET).build();
            FirebaseModelManager manager = FirebaseModelManager.getInstance();
            manager.registerRemoteModel(remoteModel);
            manager.registerLocalModel(localModel);
            FirebaseModelOptions modelOptions =
                    new FirebaseModelOptions.Builder()
                            .setRemoteModelName(HOSTED_MODEL_NAME)
                            .setLocalModelName("asset")
                            .build();
            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions);
        } catch (FirebaseMLException e) {
            showToast("Error while setting up the model");
            e.printStackTrace();
        }
    }




    private synchronized List<String> getTopLabels(byte[][] labelProbArray) {
        for (int i = 0; i < mLabelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(mLabelList.get(i), (labelProbArray[0][i] &
                            0xff) / 255.0f));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        List<String> result = new ArrayList<>();
        List<String> res = new ArrayList<>();
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            result.add(label.getKey() + ":" + label.getValue());
            res.add(label.getKey());

        }
        showToast("labels: " + result.toString());
        speak("I have find "+res.get(2));
        return result;
    }


//    Reads label list from Assets.

    private List<String> loadLabelList(Activity activity) {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(activity.getAssets().open
                             (LABEL_PATH)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
            showToast("Failed to read label list.");
        }
        return labelList;
    }


//     Writes Image data into a {@code ByteBuffer}.

    private synchronized ByteBuffer convertBitmapToByteBuffer(
            Bitmap bitmap, int width, int height) {
        ByteBuffer imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y,
                true);
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        // Convert the image to int points.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.put((byte) ((val >> 16) & 0xFF));
                imgData.put((byte) ((val >> 8) & 0xFF));
                imgData.put((byte) (val & 0xFF));
            }
        }
        return imgData;
    }


//  -------------------------------Text Recognition --------------------------------------

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        string="";
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            speak("No Text Found");
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {

                    string = string +" "+ elements.get(k).getText();
                }
            }
        }
        showResult(string);
    }

    private void showResult(String string) {

        if(bestDuration) {
            try {
                String bestBefore = string;
                bestBefore = bestBefore.toLowerCase();
                int startValue = 0;

                int endValue = bestBefore.indexOf("months");
                if (bestBefore.contains("best before")) {
                    startValue = bestBefore.indexOf("best before");
                    values = bestBefore.substring(startValue, endValue);
                }
                if (bestBefore.contains("use before")) {
                    startValue = bestBefore.indexOf("use before");
                    values = bestBefore.substring(startValue, endValue);
                }

                if(values.length() >1 ) {
                    showToast(values + "months of use");
                    speak(values + "months of use");
                }
                else{
                    speak("Error in finding best before. Please try with different orientation");

                }
            } catch (Exception e) {
                showToast("Error in finding best before. Please try with different orientation");
                speak("Error in finding best before. Please try with different orientation");
            }
            bestDuration = false;
        }

        else if(expiry){
            try {
                String days = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|Januray|Feruary|March|April|June|July|August|September|October|November|December|Sept)";
                String years = "(2019|2020|19|20|18|17|16|2018|2017)";

                String pattern1, pattern2, pattern3, pattern4;
                pattern1 = "0[1-9][/-]" + years + "|";
                pattern2 = "\\d{2}[/.][a-zA-Z]{3}[/.]" + years + "|";
                pattern3 = "\\d{2}[/.]\\d{2}[/.]" + years + "|";
                pattern4 = days + "[-/]" + years;
                String regexDate = "(" + pattern1 + pattern2 + pattern3 + pattern4 + ")";
                Pattern pattern = Pattern.compile(regexDate);
                Matcher matcher = pattern.matcher(string);

                if (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String result = matcher.group(1);
                    showToast(result);
                    speak(result);
                }
                else{
                    speak("could not find manufacturing date. Please try with different orientation");
                }
            }
            catch(Exception e){
                speak("could not find expiry. Please try with different orientation");
            }

            expiry = false;

        }
        else{
            speak(string);
            showToast(string);
        }


    }


//  -------------------------------Auto Capture -----------------------------


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(mCameraConfig);
            } else {
                Toast.makeText(this, R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {

        // Convert file to bitmap.
        // Do something.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

//        mGraphicOverlay.clear();
        mSelectedImage = bitmap;
        parseBitmap();
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, R.string.error_cannot_open, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, R.string.error_cannot_write, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
                break;
        }
    }


//    -----------------------Cognitive Service Azure--------------------

    public void  describeImage(){

        mImageView.setImageBitmap(mSelectedImage);
        final Bitmap bitmap = imageView2Bitmap(mImageView);

        //Convert Bitmap to ByteArray
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        //use Async to request api
        AsyncTask<InputStream,String,String> visionTask = new AsyncTask<InputStream, String, String>() {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected String doInBackground(InputStream... inputStreams) {

                publishProgress("Recognizing....");
                String[] features = {"Description "};
                String[] details = {};
                String jsonResult = "";
                try {
                    AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0], features, details);
                    jsonResult = new Gson().toJson(result);
                    return jsonResult;
                }
                catch (Exception e){

                }


                return null;
            }

            @Override
            protected void onPostExecute(String s) {

                if(TextUtils.isEmpty(s))
                {
                    Toast.makeText(MainActivity.this,"API return Empty Result",Toast.LENGTH_LONG);
                }
                else {
                    progressDialog.dismiss();

                    AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                    StringBuilder result_Text = new StringBuilder();
                    for (Caption caption : result.description.captions)
                        result_Text.append(caption.text);
                    txt_view.setText(result_Text.toString());
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                progressDialog.setMessage(values[0]);
            }
        };

        visionTask.execute(inputStream);

    }

    private Bitmap imageView2Bitmap(ImageView view) {
        Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
        return bitmap;
    }


//    *-*-*-**-*-*-*-*--*-*-*-*-***-*-*-*---**-*-**-*---*-*-*---**-*-**-*---*-*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
