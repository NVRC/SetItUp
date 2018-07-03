package threeblindmice.setitup.viewmodel;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.widget.ImageView;

import threeblindmice.setitup.R;

public final class ImageBinder {

    private ImageBinder() {
    }



    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView iv, Bitmap bitmap) {
        if (bitmap == null){
            iv.setImageResource(R.drawable.close_icon);
        } else {
            iv.setImageBitmap(bitmap);
        }
    }
}
