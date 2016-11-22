package modules;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

/**
 * Created by Hp on 11/20/2016.
 */
public class RightManager {
    private String tag=getClass().getSimpleName();
    RightManagerInteraction rightManagerInteraction;
    Activity activity;

    public RightManager(RightManagerInteraction rightManagerInteraction, Activity activity) {
        this.rightManagerInteraction = rightManagerInteraction;
        this.activity = activity;
    }

    public boolean isAccessGranted(){
        int hasPermission = ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA);
        return hasPermission == PackageManager.PERMISSION_GRANTED;
    }

    public void getRight() {
        int currentVersion = 0;
        try {
            currentVersion = android.os.Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentVersion > 0 && currentVersion >= 23) {
            Ask.on(activity)
                    .forPermissions(Manifest.permission.CAMERA)
                    .withRationales("In order for this app to work allow it to access camera's flash light") //optional
                    .go();
        } else {
            return;
        }
    }
    //optional
    @AskGranted(Manifest.permission.CAMERA)
    public void cameraAccessGranted() {
        Log.i(tag, "PHONE SATE GRANTED");
        rightManagerInteraction.onRightManagerInteraction(1, "Camera access granted", null);
    }

    //optional
    @AskDenied(Manifest.permission.CAMERA)
    public void cameraAccessDenied() {
        Log.i(tag, "PHONE SATE DENIED");
        Toast.makeText(activity, "Sorry without this permission flash light can not work", Toast.LENGTH_LONG).show();
        rightManagerInteraction.onRightManagerInteraction(0, "Sorry without this permission flash light can not work", null);
    }
    public interface RightManagerInteraction{
        void onRightManagerInteraction(int statusCode, String message, Object object);
    }
}
