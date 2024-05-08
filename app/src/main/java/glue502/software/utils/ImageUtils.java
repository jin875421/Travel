package glue502.software.utils;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
