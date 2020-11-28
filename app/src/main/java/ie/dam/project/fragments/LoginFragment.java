package ie.dam.project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ie.dam.project.R;
import ie.dam.project.StartActivity;


public class LoginFragment extends Fragment {
    private Button registerNowButton;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initialiseComponents(view);
        registerNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity startActivity = (StartActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (startActivity != null) {
                    startActivity.getSupportFragmentManager().beginTransaction().replace(R.id.act_start_frame_layout,
                            new RegisterFragment()).commit();
                }
            }
        });
        return view;
    }

    private void initialiseComponents(View view) {
        registerNowButton = view.findViewById(R.id.frg_login_register_button);
    }
}