package ie.dam.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ie.dam.project.fragments.CurrencyFragment;
import ie.dam.project.fragments.FilterFragment;
import ie.dam.project.fragments.HomeFragment;
import ie.dam.project.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavMenu;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents(savedInstanceState);
        bottomNavMenu.setOnNavigationItemSelectedListener(selectMenuItem());
    }

    private void initialiseComponents(Bundle savedInstanceState) {
        bottomNavMenu = findViewById(R.id.act_main_menu);
        //Start-up fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_main_frame_layout,
                    new HomeFragment()).commit();
            bottomNavMenu.setSelectedItemId(R.id.menu_home);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectMenuItem() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = new HomeFragment();
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.menu_currency:
                        selectedFragment = new CurrencyFragment();
                        break;
                    case R.id.menu_add:
                        Intent intent = new Intent(getApplicationContext(), AddEditActivity.class);
                        startActivity(intent);
                    case R.id.menu_filter:
                        selectedFragment = new FilterFragment();
                        break;
                    case R.id.menu_profile:
                        selectedFragment = new ProfileFragment();


                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.act_main_frame_layout,
                        selectedFragment).commit();

                return true;
            }
        };
    }


}