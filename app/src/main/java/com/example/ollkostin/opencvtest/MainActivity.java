package com.example.ollkostin.opencvtest;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.core.Core.rectangle;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG = "MainActivity";

    JavaCameraView cameraView;
    Mat frame, grayFrame;
    File cascadeFile;
    CascadeClassifier eyeCascade;
    BaseLoaderCallback mloaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    cameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        if(cameraView!=null){
            cameraView.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cameraView!=null){
            cameraView.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()){
           try {
               InputStream is = getResources().openRawResource(R.raw.haarcascade_eye);
               File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
               cascadeFile = new File(cascadeDir, "haarcascade_eye.xml");
               FileOutputStream os = new FileOutputStream(cascadeFile);

               byte[] buffer = new byte[4096];
               int bytesRead;
               while ((bytesRead = is.read(buffer)) != -1) {
                   os.write(buffer, 0, bytesRead);
               }
               is.close();
               os.close();
               eyeCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
               mloaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
               Log.i(TAG, "OpenCV OK");
           } catch (IOException e){
               Log.i(TAG,"Failed to load file");
           }
        } else {
            Log.i(TAG,"OpenCV load failed");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this,mloaderCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat(height,width, CvType.CV_8UC4);
        eyeCascade = new CascadeClassifier();
        grayFrame = new Mat(height,width,CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frame = inputFrame.rgba();
        /*Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

            MatOfRect eyes = new MatOfRect();
            eyeCascade.detectMultiScale(grayFrame,eyes,1.1,2, Objdetect.CASCADE_SCALE_IMAGE,new Size(100,1000),new Size());
            Rect[] facesArray = eyes.toArray();
            for (Rect aFacesArray : facesArray)
                rectangle(frame, aFacesArray.tl(), aFacesArray.br(), new Scalar(0, 255, 0, 255), 3);

            */
        return frame;
    }
}
