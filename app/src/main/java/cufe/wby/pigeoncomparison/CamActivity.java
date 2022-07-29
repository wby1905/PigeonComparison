package cufe.wby.pigeoncomparison;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

import cufe.wby.pigeoncomparison.model.Model;

public class CamActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    CameraBridgeViewBase cam;
    BaseLoaderCallback baseLoaderCallback;

    Mat eye = null;

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat img = inputFrame.rgba();
        Bitmap bitmap = Model.matToBitmap(img);
        Model.Obj[] boxes = Model.getInstance().Detect(bitmap);
        Rect rect = null;
        for (Model.Obj box : boxes) {
            Point lt = new Point(box.x1, box.y1);
            Point rb = new Point(box.x2, box.y2);
            Log.i("Detect:", img.cols() + " " + img.rows() + " " + lt + rb);
            Imgproc.rectangle(img, lt, rb, new Scalar(255, 255, 0), 5);
//            Imgproc.putText(img, String.valueOf(box.prob), lt, 0, 2.8, new Scalar(255, 255, 255));

            if (box.x1 < 0 || box.x2 < 0 || box.y1 < 0 || box.y2 < 0
                    || box.x1 > img.cols() || box.x2 > img.cols() || box.y1 > img.rows() || box.y2 > img.rows())
                continue;
            rect = new Rect(lt, rb);

        }
        if (rect != null)
            eye = img.submat(rect);
        else {
            eye = null;
        }
        bitmap.recycle();
        return img;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cam = findViewById(R.id.cam);
        cam.setVisibility(SurfaceView.VISIBLE);
        cam.setCvCameraViewListener(this);


        //create camera listener callback
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        Log.v("Cam", "Loader interface success");
                        cam.enableView();
                        cam.enableFpsMeter();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        Button camBtn = findViewById(R.id.cam_button);
        camBtn.setOnClickListener(v -> {
            Intent i = new Intent();
            if (eye != null) {
                float scale = Math.max(352f / eye.width(), 352f / eye.height());
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap tmp = Model.matToBitmap(eye);

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.JPEG, 50, bs);

                i.putExtra("img", bs.toByteArray());
                setResult(RESULT_OK, i);
                finish();


            } else {
                Toast.makeText(this, "没有检测到目标！", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eye != null) {
            eye.release();
        }
        if (cam != null) {
            cam.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Toast.makeText(getApplicationContext(), "Problem Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eye != null)
            eye.release();
        if (cam != null)
            cam.disableView();
    }
}
