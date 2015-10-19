package com.example.smartglass;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    private CameraBridgeViewBase mCameraView;
    private Mat mOutputFrame;
    private Mat Frame0,Frame;
    private Mat dif,difb0,difb;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //プログラム開始時に呼ばれる
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCameraView = (CameraBridgeViewBase)findViewById(R.id.camera_view);
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        //プログラム終了時に呼ばれる
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // カメラプレビュー開始時に呼ばれる
        // Mat(int rows, int cols, int type)
        // rows(行): height, cols(列): width
        mOutputFrame = new Mat(height, width, CvType.CV_8UC1);
        Frame = new Mat(height, width, CvType.CV_8UC1);
        Frame0 = new Mat(height, width, CvType.CV_8UC1);
        difb0 = new Mat(height, width, CvType.CV_8UC1);
        difb = new Mat(height, width, CvType.CV_8UC1);
        dif = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        // カメラプレビュー終了時に呼ばれる
        mOutputFrame.release();
        Frame0.release();
        Frame.release();
        difb0.release();
        difb.release();
        dif.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // フレームをキャプチャする毎(30fpsなら毎秒30回)に呼ばれる
        Frame=inputFrame.gray();
        // 差分計算
        Core.absdiff(Frame0, Frame, dif);
        //差分画像の二値化
        Imgproc.threshold(dif, difb, 30, 255, Imgproc.THRESH_BINARY);
        //二値化された差分画像の共通部分を取得
        Core.bitwise_and(difb0, difb, mOutputFrame);
        new Point();
        //クロージング・オープニング処理
        Imgproc.morphologyEx(mOutputFrame, mOutputFrame,  Imgproc.MORPH_OPEN, new Mat(),new Point(-1,-1),2);
        Imgproc.morphologyEx(mOutputFrame, mOutputFrame,  Imgproc.MORPH_CLOSE, new Mat(),new Point(-1,-1),2);
        //フレーム更新
        Frame0=Frame;
        difb0=difb;
        //出力
        return mOutputFrame;
    }
}