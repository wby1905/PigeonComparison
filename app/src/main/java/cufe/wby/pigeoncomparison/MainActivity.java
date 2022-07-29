package cufe.wby.pigeoncomparison;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import cufe.wby.pigeoncomparison.model.Model;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    static {
        System.loadLibrary("model");
    }

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Main", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Model.getInstance().Init(getAssets());

        setContentView(R.layout.activity_main);

        Button buttonCmp = findViewById(R.id.cmpBtn);
        buttonCmp.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainActivity.this, CmpActivity.class);
                    startActivity(intent);
                }
        );

        Button button2 = findViewById(R.id.more);
        button2.setOnClickListener(
                v -> Toast.makeText(this, "敬请期待...", Toast.LENGTH_LONG).show()
        );

//        intent = new Intent(MainActivity.this, BGM.class);
//        String action = BGM.ACTION_MUSIC;
//        // 设置action
//        intent.setAction(action);
//        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("Main", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("Main", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intent != null) {
            stopService(intent);
        }
    }
}