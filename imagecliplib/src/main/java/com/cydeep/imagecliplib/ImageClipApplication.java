package com.cydeep.imagecliplib;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.cydeep.imagecliplib.cache.LruMemoryCache;
import com.cydeep.imagecliplib.cache.MemoryCache;

public class ImageClipApplication extends Application {

    private static ImageClipApplication imageClipApplication;

    private LruMemoryCache lruMemoryCache = null;

    public static ImageClipApplication getInstance() {
        return imageClipApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        imageClipApplication = this;

    }

    public MemoryCache getMemoryCache() {
        Context context = ImageClipApplication.getInstance();
        int memoryCacheSize = 20 * 1024 * 1024;
        if (lruMemoryCache == null) {
            if (memoryCacheSize == 0) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                int memoryClass = am.getMemoryClass();
                if (hasHoneycomb() && isLargeHeap(context)) {
                    memoryClass = getLargeMemoryClass(am);
                }
                memoryCacheSize = 1024 * 1024 * memoryClass / 8;
            }
            lruMemoryCache = new LruMemoryCache(memoryCacheSize);
        }
        return lruMemoryCache;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
    }

    private static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
