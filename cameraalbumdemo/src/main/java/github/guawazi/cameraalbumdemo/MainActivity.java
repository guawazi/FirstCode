package github.guawazi.cameraalbumdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private Button mBtnTakePhoto;
    private Uri mImageUri;
    private static final int TAKE_PHOTO = 1;
    private ImageView mIvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnTakePhoto = findViewById(R.id.btn_take_photo);
        mIvImage = findViewById(R.id.iv_image);
        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void takePhoto() {
        // 1. 创建储存拍照结果的文件
        File imageFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "文件创建失败", Toast.LENGTH_SHORT).show();
        }

        // 2. 转换成 URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 大于 7.0 使用 fileProvider
            mImageUri = FileProvider.getUriForFile(MainActivity.this, "github.guawazi.cameraalbumdemo", imageFile);
        } else {
            mImageUri = Uri.fromFile(imageFile);
        }

        // 3. 启动Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO: // 4. 获取拍照结果
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));
                        mIvImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
