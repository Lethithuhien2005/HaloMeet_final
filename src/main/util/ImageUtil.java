package main.util;

import com.sun.javafx.util.Utils;
import javafx.scene.image.Image;

import java.net.URL;

public class ImageUtil {
    public static Image loadAvatarSafe(String path) {
        try {
            return new Image(path, true); // load async, fallback tự xử lý
        } catch (Exception e) {
            return new Image("images/default_avatar.png");
        }
    }
}
