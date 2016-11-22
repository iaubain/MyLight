package modules;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.amordivino.aubain.mylight.R;
import com.pddstudio.easyflashlight.EasyFlashlight;
import com.vistrav.ask.Ask;

/**
 * Created by Hp on 11/20/2016.
 */
public class LightManager implements RightManager.RightManagerInteraction {
    private LightManagerInteraction lightManagerInteraction;
    private Activity activity;
    private MediaPlayer mp;
    private boolean isFlashOn=false;
    private RightManager rightManager;

    private Camera camera;
    private Camera.Parameters p;

    public LightManager(LightManagerInteraction lightManagerInteraction, Activity activity, Camera camera) {
        this.lightManagerInteraction = lightManagerInteraction;
        this.activity = activity;
        rightManager =new RightManager(this, activity);
        if(!rightManager.isAccessGranted())
            rightManager.getRight();

        this.camera=camera;
    }

    private boolean isDeviceFlashEnabled(){
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void uiPopUp(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle("Error");
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void turnOnFlash(){
        if(isDeviceFlashEnabled()){
            if(!isFlashOn){
                p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
//
//                EasyFlashlight.init(activity);
//                EasyFlashlight.getInstance().turnOn();
                isFlashOn=true;
                lightManagerInteraction.onLightManagerInteraction(1, "Turned on", null);
            }
            playSound();
        }else{
            uiPopUp("Sorry, your device doesn't support flash light!");
        }
    }
    public void turnOffFlash(){
        if(isFlashOn){

            p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
//
//            EasyFlashlight.init(activity);
//            EasyFlashlight.getInstance().turnOff();
            isFlashOn=false;
            lightManagerInteraction.onLightManagerInteraction(0, "Turned off", null);
        }
        playSound();
    }

    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(activity, R.raw.light_switch_off);
        }else{
            mp = MediaPlayer.create(activity, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    @Override
    public void onRightManagerInteraction(int statusCode, String message, Object object) {
        if(statusCode == 0)
            uiPopUp(message);
    }

    public interface LightManagerInteraction{
        void onLightManagerInteraction(int statusCode, String message, Object object);
    }
}
