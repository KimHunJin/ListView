package com.example.list.myapplication;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by HunJin on 2015-09-11.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        init(getApplicationContext());
        super.onCreate();
    }

    public void init(Context ctx)
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.profile)
                .showImageForEmptyUri(R.drawable.profile2)
                .showImageOnFail(R.drawable.profile3)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .threadPriority(Thread.NORM_PRIORITY-2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(options).build();

        ImageLoader.getInstance().init(config);
    }
}
