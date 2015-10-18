package com.example.smartglass;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.opencv.core.Mat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //カメラの宣言
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //フルスクリーンの指定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //カメラプレビュー画面の設定
        SurfaceView cameraPreview= (SurfaceView)findViewById(R.id.preview);
        //サーフェイスホルダー生成
        cameraPreview.getHolder().addCallback(previewCallback);
        //サーフェイスホルダーのタイプを設定(外部バッファの使用)
        cameraPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //サーフェイスホルダーの「Callback()」メソッドを実装
    private SurfaceHolder.Callback previewCallback = new SurfaceHolder.Callback() {
        private Camera camera;
        // サーフェイス生成処理
        public void surfaceCreated(SurfaceHolder holder) {
            // カメラ初期化
                    try {
                        // カメラのオープン
                        camera = Camera.open();
                        // プレビューディスプレイのセット
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }
        // サーフェイス変更処理
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // プレビューを停止
            camera.stopPreview();
            // カメラのパラメータを取得
            Camera.Parameters params = camera.getParameters();
            // パラメータにプレビュー表示のサイズを設定
            params.setPreviewSize(params.getPreviewSize().width, params.getPreviewSize().height);
            // パラメータのセット
            camera.setParameters(params);
            // カメラプレビュー開始
            camera.startPreview();
        }
        // サーフェイス開放処理
        public void surfaceDestroyed(SurfaceHolder holder) {
            // カメラプレビュー停止
            camera.stopPreview();
            // カメラリリース
            camera.release();
        }
    };
}
