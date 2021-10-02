package org.ezequiel.shortlink;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static org.ezequiel.shortlink.FirstFragment.generateQrCode;


public class ShareQrCode {


    public ShareQrCode(String url, Activity context) {

        Bitmap bitmap = generateQrCode(url,context);

        File file = new File(context.getCacheDir(), "qrcode" + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri imageUri = FileProvider.getUriForFile(
                context,
                context.getBaseContext().
                        getApplicationContext().getPackageName() +".org.ezequiel.shortlink.Provider",
                file);

        Intent shareIntent = new Intent();
        shareIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "Qr Code"));


    }


}
