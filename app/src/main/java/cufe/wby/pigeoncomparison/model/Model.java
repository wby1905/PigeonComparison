package cufe.wby.pigeoncomparison.model;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Model {
    private static class SingletonHolder {
        private static final Model INSTANCE = new Model();
    }

    public static final Model getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public native void Init(AssetManager mgr);

    public class Obj
    {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int label;
        public float prob;

        public Obj(int x1, int y1, int x2, int y2, int label, float prob) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.label = label;
            this.prob = prob;
        }

        public Obj() {};



    }

    public native Obj[] Detect(Bitmap img);

    public native float Compare(Bitmap img1, Bitmap img2);

    //Mat转Bitmap
    public static Bitmap matToBitmap(Mat mat) {
        Bitmap resultBitmap = null;
        if (mat != null) {
            resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            if (resultBitmap != null)
                Utils.matToBitmap(mat, resultBitmap);
        }
        return resultBitmap;

    }


    //Bitmap转Mat
    public static Mat bitmapToMat(Bitmap bm) {
        Bitmap bmp32 = bm.copy(Bitmap.Config.RGB_565, true);
        Mat imgMat = new Mat ( bm.getHeight(), bm.getWidth(), CvType.CV_8UC2, new Scalar(0));
        Utils.bitmapToMat(bmp32, imgMat);
        bmp32.recycle();
        return imgMat;
    }


}