package com.codedarts.joel.dspot;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class AccountSetupActivity extends AppCompatActivity {

    private AccountSetupUserInformationFragment accountSetupUserInformationFragment = new AccountSetupUserInformationFragment();
    private CategorySelectionFragment categorySelectionFragment = new CategorySelectionFragment();

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FrameLayout frameLayout;
    private Button nextButton, previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        nextButton = (Button)findViewById(R.id.button2);
        previousButton = (Button)findViewById(R.id.previousButton);

        fragmentManager.beginTransaction().replace(R.id.frameLayout, accountSetupUserInformationFragment).commit();
    }

    public void nextFragment (View view) {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, categorySelectionFragment).commit();
        if (fragmentManager.getBackStackEntryAt(0).getId() == categorySelectionFragment.getId()) {
            Toast.makeText(this, "categorySelectionFragment", Toast.LENGTH_SHORT).show();
        }
        else {
            if (fragmentManager.getBackStackEntryAt(0).getId() == accountSetupUserInformationFragment.getId()) {
                Toast.makeText(this, "accountSetupUserInformationFragment", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "My coding sucks...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void previousFragment(View view) {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, accountSetupUserInformationFragment).commit();
    }

    public void updateProfilePicture (View view) {
        accountSetupUserInformationFragment.getPhotoFile();
    }

}
