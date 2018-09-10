package com.usersinformation.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.usersinformation.R;
import java.io.File;
public class ImageDisplaying {

    /*
     * Image displayer using glide library to load the image and cache
     * */

    public static void loadCircleImageFromURL(final Context ctx, String url, final ImageView image, int pic) {
        try {
            if (ctx == null || image==null) {
                return;
            }
            image.setTag(image.getId(), url);
            if (!url.startsWith("http")) {
                File url_file = new File(url);
                if (url_file != null && url_file.exists()) {
                    Glide.with(ctx).load(url_file).asBitmap().centerCrop().error(R.mipmap.user_default).into(new BitmapImageViewTarget(image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            image.invalidate();
                            image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }
            } else {
                Glide.with(ctx).load(url).asBitmap().centerCrop().error(R.mipmap.user_default).into(new BitmapImageViewTarget(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
//						image.requestFocus();
                        image.invalidate();
                        image.setImageDrawable(circularBitmapDrawable);
//						resource.recycle();
                    }
                });
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadImageFromURL_Rect(Context ctx, String url, ImageView image, int pic) {
        try {
            image.setTag(image.getId(), url);
            Glide.with(ctx)
                    .load(url)
                    .crossFade()
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //	String SDCARD_INTERNALPATH =null;
    public void setImageInDisplayer(Activity activity ,final String URLss, final ImageView imageView, final int defaultpic, final String filename) {
        try {
           Glide.clear(imageView);
            imageView.setTag(imageView.getId(), URLss);
            Glide.with(activity)
                    .load(URLss)
                    .error(defaultpic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.invalidate();
                            return false;
                        }
                    }).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}