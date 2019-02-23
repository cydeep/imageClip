package com.cydeep.imagecliplib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cydeep.imagecliplib.utils.FileUtils;
import com.cydeep.imagecliplib.utils.ImageUtil;
import com.cydeep.imagecliplib.utils.ViewSizeUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import photoview.PhotoViewAttacher;

public class ImageClipActivity extends FragmentActivity {
    private PhotoViewAttacher mAttacher;
    private RelativeLayout image_container;
    private View clip_bounds_view;
    private int dp_360;
    private RectF baseRectF;
    private float baseScaleRate = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    private String path;


    public static void startImageClipActivity(Activity activity, int requestCode, String path) {
        Intent intent = new Intent(activity, ImageClipActivity.class);
        intent.putExtra("path", path);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        path = getIntent().getStringExtra("path");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter_clip);
        findViewById(R.id.bottom).getLayoutParams().height = ViewSizeUtil.getCustomDimen(44f);
        imageView = (ImageView) findViewById(R.id.clip_image_view);
        image_container = (RelativeLayout) findViewById(R.id.image_container);
        checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1, new CheckPermissionListener() {
            @Override
            public void hasPermission() {
                bitmap = ImageUtil.getSuitBitmap(path);
                initImageView();
            }
        });

        clip_bounds_view = findViewById(R.id.clip_bounds_view);
        dp_360 = ViewSizeUtil.getCustomDimen(360f);

        findViewById(R.id.photo_full_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap.getHeight() >= bitmap.getWidth()) {
                    if (mAttacher.getScale() < baseScaleRate) {
                        mAttacher.setScale(baseScaleRate);
                    } else {
                        float height = image_container.getHeight() * 1.0f > baseRectF.height() ? baseRectF.height() : image_container.getHeight() * 1.0f;
                        mAttacher.setScale(dp_360 * 1.0f / height);
                    }
                } else {
                    if (mAttacher.getScale() < baseScaleRate) {
                        mAttacher.setScale(baseScaleRate);
                    } else {
                        mAttacher.setScale(baseRectF.height() * 1.0f / dp_360 * 1.0f);
                    }
                }
            }
        });
        findViewById(R.id.comm_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAttacher.update();
//                if (bitmap.getHeight() < bitmap.getWidth()) {
//                    mAttacher.setScale(baseScaleRate);
//                }
                mAttacher.setScale(baseScaleRate);
            }
        });

        findViewById(R.id.comm_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        checkPermission(ImageClipActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 2, new CheckPermissionListener() {
                            @Override
                            public void hasPermission() {
                                final Bitmap bitmap = clip();
                                path = ImageUtil.saveClip(bitmap, FileUtils.File_CLIP);
                                findViewById(R.id.comm_done).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.putExtra("path", path);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            }
                        });


                    }
                });
            }
        });

        findViewById(R.id.comm_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initImageView() {
        imageView.setImageBitmap(bitmap);
        imageView.setTag(true);
        mAttacher = new PhotoViewAttacher(imageView);
        image_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int deltHeight = setCustomBounds();
                setMask(deltHeight);
                image_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mAttacher.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {

            @Override
            public void onMatrixChanged(RectF rect) {
                Object tag = imageView.getTag();
                if (tag != null) {
                    boolean flag = (boolean) tag;
                    baseRectF = new RectF(rect);
                    if (bitmap.getHeight() >= bitmap.getWidth()) {
                        if (flag) {
                            float bitmapRate = bitmap.getHeight() * 1.0f / bitmap.getWidth() * 1.0f;
                            float viewRate = image_container.getHeight() * 1.0f / image_container.getWidth() * 1.0f;
                            if (bitmapRate > viewRate) {//图片是否超过imageview的控件区域
                                imageView.setTag(false);
                                baseScaleRate = dp_360 * 1.0f / rect.width() * 1.0f;
                                if (baseScaleRate > 3.0f) {
                                    mAttacher.setMaximumScale(baseScaleRate + 2);
                                }
                                mAttacher.setScale(baseScaleRate);
                            } else {
                                imageView.setTag(null);
                            }
                        } else {
                            imageView.setTag(null);
                        }
                        float height = image_container.getHeight() * 1.0f > baseRectF.height() ? baseRectF.height() : image_container.getHeight() * 1.0f;
//                        mAttacher.setCustomMinScale(dp_360 * 1.0f / height);
                        mAttacher.setMinimumScale(dp_360 * 1.0f / height);
                    } else {
                        if (flag) {
                            imageView.setTag(false);
                            baseScaleRate = dp_360 * 1.0f / rect.height() * 1.0f;
                            if (baseScaleRate > 3.0f) {
                                mAttacher.setMaximumScale(baseScaleRate + 2);
                            }
                            mAttacher.setScale(baseScaleRate);
                            mAttacher.setMinimumScale(baseRectF.height() * 1.0f / dp_360 * 1.0f);
                        } else {
                            imageView.setTag(null);
                        }
                    }
                }

            }

        });
        mAttacher.setMediumScale(2.0f);
        mAttacher.setMaximumScale(3.0f);
    }

    private void setMask(int deltHeight) {
        View bottom = findViewById(R.id.clip_bounds_view_below);
        bottom.setBackgroundResource(R.color.clip_black_overlay);
        View above = findViewById(R.id.clip_bounds_view_above);
        above.setBackgroundResource(R.color.clip_black_overlay);
        above.getLayoutParams().height = bottom.getLayoutParams().height = deltHeight;
    }

    private int setCustomBounds() {
        int deltHeight = (image_container.getHeight() - dp_360) / 2;
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.right = dp_360;
        rectF.top = deltHeight;
        rectF.bottom = rectF.top + dp_360;
        mAttacher.setCustomBounds(rectF);
        return deltHeight;
    }

    private Bitmap clip() {
        RectF mClipBorderRectF = getClipBorder();
        final Drawable drawable = imageView.getDrawable();
        final float[] matrixValues = new float[9];
        Matrix displayMatrix = mAttacher.getDrawMatrix();
        displayMatrix.getValues(matrixValues);
        final float scale = matrixValues[Matrix.MSCALE_X] * drawable.getIntrinsicWidth() / bitmap.getWidth();
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final float cropX = (-transX + mClipBorderRectF.left) / scale;
        final float cropY = (-transY + mClipBorderRectF.top) / scale;
        final float cropWidth = mClipBorderRectF.width() / scale;
        final float cropHeight = mClipBorderRectF.height() / scale;
        return Bitmap.createBitmap(bitmap, (int) cropX, (int) cropY, (int) cropWidth, (int) cropHeight, null, false);
    }

    private RectF getClipBorder() {
        RectF mClipBorderViewRectF = new RectF(ViewSizeUtil.getViewRectInParent(clip_bounds_view, image_container));
        RectF displayRect = mAttacher.getDisplayRect();
        if (bitmap.getHeight() >= bitmap.getWidth()) {
            if (mAttacher.getScale() < baseScaleRate) {
                mClipBorderViewRectF.left = displayRect.left;
                mClipBorderViewRectF.right = displayRect.right;
            }
        } else {
            if (mAttacher.getScale() < baseScaleRate) {
                mClipBorderViewRectF.top = displayRect.top;
                mClipBorderViewRectF.bottom = displayRect.bottom;
            }
        }
        return mClipBorderViewRectF;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0) {
                    return;
                }
                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bitmap = ImageUtil.getSuitBitmap(path);
                    initImageView();
                } else {
                    //用户不同意，向用户展示该权限作用
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        return;
                    }
                }
                break;
            case 2:

                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final Bitmap bitmap = clip();
                    path = ImageUtil.saveClip(bitmap, FileUtils.File_CLIP);
                    findViewById(R.id.comm_done).post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("path", path);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                } else {
                    //用户不同意，向用户展示该权限作用
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        return;
                    }
                }
                break;
        }
    }

    public void checkPermission(final Activity activity, String permission, final int code, CheckPermissionListener listener) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity, permission);
        if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
            listener.hasPermission();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
        }
    }

    public interface CheckPermissionListener {
        void hasPermission();
    }

}
