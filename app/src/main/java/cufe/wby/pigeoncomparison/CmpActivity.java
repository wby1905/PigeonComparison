package cufe.wby.pigeoncomparison;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.FileNotFoundException;
import java.io.IOException;

import cufe.wby.pigeoncomparison.model.Model;

public class CmpActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 1, CAM_IMAGE = 2;

    private int flag;

    private Bitmap bitmap1;
    private Bitmap bitmap2;

    private ImageView img1 = null;
    private ImageView img2 = null;
    private TextView res = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = null;
                if (requestCode == SELECT_IMAGE) {
                    Uri selectedImage = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Mat tmp = Model.bitmapToMat(bitmap.copy(Bitmap.Config.ARGB_8888, true));
                    Rect rect = null;
                    Model.Obj[] boxes = Model.getInstance().Detect(bitmap);
                    for (Model.Obj box : boxes) {
                        Point lt = new Point(box.x1, box.y1);
                        Point rb = new Point(box.x2, box.y2);
                        rect = new Rect(lt, rb);
                    }
                    if (rect == null) {
                        Toast.makeText(this, "没有检测到目标！请重新选择", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tmp = tmp.submat(rect);
                    bitmap = Model.matToBitmap(tmp);
                    tmp.release();

                } else if (requestCode == CAM_IMAGE) {
                    byte[] tmp = data.getByteArrayExtra("img");
                    if(tmp == null) {
                        Toast.makeText(this, "没有图片！请重新尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                }
                if (bitmap == null) {
                    Toast.makeText(this, "没有图片！请重新尝试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (flag == 1) {
                    bitmap1 = bitmap.copy(bitmap.getConfig(), true);
                    img1.setImageBitmap(bitmap1);
                } else if (flag == 2) {
                    bitmap2 = bitmap.copy(bitmap.getConfig(), true);
                    img2.setImageBitmap(bitmap2);
                }

                bitmap.recycle();

            } catch (IOException e) {
                Log.e("CmpActivity", "FileNotFound");
                Toast.makeText(this, "没有图片！请重新尝试", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmp_view);

        res = findViewById(R.id.res_text);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);

        Button buttonCap1 = findViewById(R.id.cap1);
        buttonCap1.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            Intent i = new Intent(CmpActivity.this, CamActivity.class);
            flag = 1;
            startActivityForResult(i, CAM_IMAGE);
        });

        Button buttonCap2 = findViewById(R.id.cap2);
        buttonCap2.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
            Intent i = new Intent(CmpActivity.this, CamActivity.class);
            flag = 2;
            startActivityForResult(i, CAM_IMAGE);
        });

        Button buttonSel1 = findViewById(R.id.sel1);
        buttonSel1.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            flag = 1;
            i.setType("image/*");
            startActivityForResult(i, SELECT_IMAGE);
        });

        Button buttonSel2 = findViewById(R.id.sel2);
        buttonSel2.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            flag = 2;
            i.setType("image/*");
            startActivityForResult(i, SELECT_IMAGE);
        });

        Button buttonStart = findViewById(R.id.start);
        buttonStart.setOnClickListener(v -> {

            float prob = Model.getInstance().Compare(bitmap1, bitmap2);
            res.setText("相似血统的概率：" + prob);
        });

    }


}
