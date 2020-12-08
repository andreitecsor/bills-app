package ie.dam.project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputLayout;

import ie.dam.project.R;

public class ProfileFragment extends Fragment {


    private ImageView imageView;
    private ToggleButton changePass;
    private EditText nickname;
    private EditText email;
    private EditText pass;
    private EditText confirmPass;
    private TextInputLayout passLayout;
    private TextInputLayout confirmPassLayout;
    private EditText oldpass;
    private Button saveButton;

    public ProfileFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialiseComponents(view);
        changePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(changePass.isChecked()){

                    enableChangePass(view);
                }
                else disableChangePass(view);
            }
        });
        return view;
    }



    private void enableChangePass(View view) {
        pass.setVisibility(view.VISIBLE);
        confirmPass.setVisibility((view.VISIBLE));
        pass.setEnabled(true);
        confirmPass.setEnabled(true);
        passLayout.setVisibility(View.VISIBLE);
        confirmPassLayout.setVisibility(View.VISIBLE);

    }


    private void initialiseComponents(View view) {
        imageView=view.findViewById(R.id.frg_profile_imgv);
        nickname=view.findViewById(R.id.frg_profile_nickname_edit_text);
        email=view.findViewById(R.id.frg_profile_email_edit_text);
        pass=view.findViewById(R.id.frg_profile_new_pass_edit_text);
        confirmPass=view.findViewById(R.id.frg_profile_confirm_pass_edit_text);
        oldpass=view.findViewById(R.id.frg_profile_old_pass_edit_text);
        changePass=view.findViewById(R.id.frg_profile_btn_change_pass);
        saveButton=view.findViewById(R.id.frg_profile_btn_save);
        passLayout=view.findViewById(R.id.frg_profile_new_pass_layout);
        confirmPassLayout=view.findViewById(R.id.frg_profile_confirm_pass_layout);
        disableChangePass(view);
    }

    private void disableChangePass(View view) {
        pass.setEnabled(false);
        confirmPass.setEnabled(false);
        pass.setVisibility(view.GONE);
        confirmPass.setVisibility((view.GONE));
        passLayout.setVisibility(View.GONE);
        confirmPassLayout.setVisibility(View.GONE);

    }
}