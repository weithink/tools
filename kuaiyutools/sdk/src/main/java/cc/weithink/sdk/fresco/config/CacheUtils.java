package cc.weithink.sdk.fresco.config;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import com.facebook.common.util.ByteConstants;

import java.io.File;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * Created by jarson on 2017/6/21.
 */

public class CacheUtils {
    public static final int MIN_DISK_CACHE_SIZE = 32 * ByteConstants.MB;
    public static final int MAX_DISK_CACHE_SIZE = 64 * ByteConstants.MB;

    public static final float MAX_AVAILABLE_SPACE_USE_FRACTION = 0.9f;
    public static final float MAX_TOTAL_SPACE_USE_FRACTION = 0.25f;

    private static final String TAG = "CacheUtils";

    /**
     * Calculates bonded min max cache size. Min value is {@link #MIN_DISK_CACHE_SIZE}
     *
     * @param dir cache dir
     * @return disk space in bytes
     */

    public static long calculateDiskCacheSize(File dir) {
        long size = Math.min(calculateAvailableCacheSize(dir), MAX_DISK_CACHE_SIZE);
        return Math.max(size, MIN_DISK_CACHE_SIZE);
    }

    /**
     * Calculates minimum of available or total fraction of disk space
     *
     * @param dir
     * @return space in bytes
     */
    @SuppressLint("NewApi")
    public static long calculateAvailableCacheSize(File dir) {
        long size = 0;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            int sdkInt = Build.VERSION.SDK_INT;
            long totalBytes;
            long availableBytes;
            if (sdkInt < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int blockSize = statFs.getBlockSize();
                availableBytes = ((long) statFs.getAvailableBlocks()) * blockSize;
                totalBytes = ((long) statFs.getBlockCount()) * blockSize;
            } else {
                availableBytes = statFs.getAvailableBytes();
                totalBytes = statFs.getTotalBytes();
            }
            // Target at least 90% of available or 25% of total space
            size = (long) Math.min(availableBytes * MAX_AVAILABLE_SPACE_USE_FRACTION, totalBytes
                    * MAX_TOTAL_SPACE_USE_FRACTION);
        } catch (IllegalArgumentException ignored) {
            // ignored
        }
        return size;
    }

    public static int calculateMemoryCacheSize(Context context, int proportion) {
        ActivityManager am = getService(context, Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        Log.d(TAG, "===============calculateMemoryCacheSize = " + memoryClass / proportion + " M");
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES
                .HONEYCOMB) {
            memoryClass = am.getLargeMemoryClass();
        }
        // Target ~15% of the available heap.
        Log.d(TAG, "===============calculateMemoryCacheSize large = " + memoryClass / proportion +
                " M");
        return 1024 * 1024 * memoryClass / proportion;
    }


    public static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    public static File createDefaultCacheDir(Context context, String path) {
        File cacheDir = getBaseDirectoryPath(context);
        File cache = new File(cacheDir, path);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    public static File createDefaultCacheDir(File baseFile, String path) {
        File cache = new File(baseFile, path);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    public static File getBaseDirectoryPath(Context context) {
        File cacheDir = context.getApplicationContext().getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getApplicationContext().getCacheDir();
        return cacheDir;
    }


    public static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
}
