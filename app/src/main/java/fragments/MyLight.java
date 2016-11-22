package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amordivino.aubain.mylight.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import modules.LightManager;
import modules.RightManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyLightInteraction} interface
 * to handle interaction events.
 * Use the {@link MyLight#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyLight extends Fragment implements LightManager.LightManagerInteraction, RightManager.RightManagerInteraction {

    private MyLightInteraction mListener;
    @BindView(R.id.btnSwitch)
    ImageButton btnSwitch;

    Unbinder unbinder;
    boolean isFlashOn=false;
    private LightManager lightManager;
    private RightManager rightManager;
    private Camera camera;

    public MyLight() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment MyLight.
     */
    public static MyLight newInstance() {
        MyLight fragment = new MyLight();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mylight_ui, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        try{
            rightManager = new RightManager(this, getActivity());
            if(!rightManager.isAccessGranted())
                rightManager.getRight();
            else {
                camera = Camera.open();
                lightManager=new LightManager(this, getActivity(), camera);
            }
        }catch (Exception e){
            e.printStackTrace();
            uiPopUp(e.getLocalizedMessage());
        }
        toggleButtonImage();

        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    try{
                        turnOffFlash();
                        toggleButtonImage();
                    }catch (Exception e){
                        uiPopUp(e.getMessage()+"");
                    }
                } else {
                    // turn on flash
                    try{
                        turnOnFlash();
                        toggleButtonImage();
                    }catch (Exception e){
                        uiPopUp(e.getLocalizedMessage()+"");
                    }
                }
            }
        });
    }

    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.btn_switch_on);
        }else{
            btnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }

    private void turnOffFlash(){
        if(lightManager != null)
            lightManager.turnOffFlash();
        else
            uiPopUp("Something went wrong");
    }

    private void turnOnFlash(){
        if(lightManager != null)
            lightManager.turnOnFlash();
        else
            uiPopUp("Something went wrong");
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyLightInteraction) {
            mListener = (MyLightInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(rightManager.isAccessGranted())
            if(camera != null){

                try{
                camera.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onLightManagerInteraction(int statusCode, String message, Object object) {
        if(statusCode == 1)
            isFlashOn = true;
        else if( statusCode == 0)
            isFlashOn = false;
        else
            uiPopUp(message);
    }

    private void uiPopUp(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setTitle("Error");
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRightManagerInteraction(int statusCode, String message, Object object) {
        if(statusCode == 0)
            uiPopUp(message);
        else{
            camera = Camera.open();
            lightManager=new LightManager(this, getActivity(), camera);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MyLightInteraction {
        void onMyLightInteraction(int statusCode, String message, Object object);
    }
}
