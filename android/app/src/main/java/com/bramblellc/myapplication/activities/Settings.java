package com.bramblellc.myapplication.activities;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.data.Globals;
import com.bramblellc.myapplication.layouts.CustomActionbar;
import com.bramblellc.myapplication.layouts.FullWidthButton;
import com.bramblellc.myapplication.services.ActionConstants;
import com.bramblellc.myapplication.services.AddContactService;
import com.bramblellc.myapplication.services.LogoutService;
import com.bramblellc.myapplication.services.RemoveContactService;

import java.util.HashSet;
import java.util.Set;

public class Settings extends Activity {

    private CustomActionbar settingsCustomActionbar;
    private FullWidthButton myDogsFullWidthButton;
    private FullWidthButton phoneServicesFullWidthButton;
    private FullWidthButton logoutFullWidthButton;
    private FullWidthButton testFullWidthButton;

    private IntentFilter addContactFilter;
    private IntentFilter removeContactFilter;

    private AddContactReceiver addContactReceiver;
    private RemoveContactReceiver removeContactReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        settingsCustomActionbar = (CustomActionbar) findViewById(R.id.settings_custom_actionbar);
        myDogsFullWidthButton = (FullWidthButton) findViewById(R.id.my_dogs_full_width_button);
        phoneServicesFullWidthButton = (FullWidthButton) findViewById(R.id.phone_aid_input_full_width_button);
        logoutFullWidthButton = (FullWidthButton) findViewById(R.id.logout_full_width_button);
        //testFullWidthButton = (FullWidthButton) findViewById(R.id.test_full_width_button);

        settingsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myDogsFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDogsPressed(v);
            }
        });

        phoneServicesFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneServicesPressed(v);
            }
        });

        logoutFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPressed(v);
            }
        });

        /*
        testFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFullWidthPressed(v);
            }
        });
        */
    }

    public void testFullWidthPressed(View view) {
        Intent intent = new Intent(Settings.this, TestEnvironment.class);
        startActivity(intent);
    }

    public void myDogsPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("ADD OR REMOVE A GUARD DOG")
                .positiveText("ADD")
                .negativeText("REMOVE")
                .neutralText("CANCEL")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addDog();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        removeDog();
                    }
                })
                .show();
    }

    public void addDog() {
        new MaterialDialog.Builder(this)
                .title("ADD A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to add to your kennel.")
                .positiveText("Add")
                .negativeText("Cancel")
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Intent addIntent = new Intent(Settings.this, AddContactService.class);
                        addIntent.putExtra("phone_number", input.toString());
                        addIntent.putExtra("token", Globals.getToken());
                        addContactFilter = new IntentFilter(ActionConstants.ADD_CONTACT_ACTION);
                        addContactReceiver = new AddContactReceiver();
                        LocalBroadcastManager.getInstance(Settings.this).registerReceiver(addContactReceiver, addContactFilter);
                        startService(addIntent);
                    }
                }).show();
    }

    public void removeDog() {
        new MaterialDialog.Builder(this)
                .title("REMOVE A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to remove from your kennel.")
                .positiveText("remove")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Intent removeIntent = new Intent(Settings.this, RemoveContactService.class);
                        removeIntent.putExtra("phone_number", input.toString());
                        removeIntent.putExtra("token", Globals.getToken());
                        removeContactFilter = new IntentFilter(ActionConstants.REMOVE_CONTACT_ACTION);
                        removeContactReceiver = new RemoveContactReceiver();
                        LocalBroadcastManager.getInstance(Settings.this).registerReceiver(removeContactReceiver, removeContactFilter);
                        startService(removeIntent);
                    }
                }).show();

    }

    public void phoneServicesPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("ADD OR REMOVE CONTACT FOR PHONE SERVICES")
                .positiveText("ADD")
                .negativeText("REMOVE")
                .neutralText("CANCEL")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addPhoneServiceContact();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        String contact = prefs.getString("phone", null);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        editor.putString("phone", null);
                        editor.apply();
                        if (contact != null)
                            Toast.makeText(Settings.this, "Your phone service provider has been removed as your contact.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Settings.this, "There was no phone service provider to remove.", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    public void addPhoneServiceContact() {
        new MaterialDialog.Builder(this)
                .title("ADD A PHONE SERVICE CONTACT")
                .content("Enter the phone number of the phone service provider you would like to add or replace as your contact.")
                .positiveText("Add")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        String contact = input.toString();
                        editor.putString("phone", contact);
                        editor.apply();
                    }
                }).show();
    }

    public void logoutPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("LOG OUT")
                .content("Are you sure you would like to log out?")
                .positiveText("Yes")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        logout();
                    }
                })
                .show();
    }

    public void logout() {
        Intent deauthenticateIntent = new Intent(Settings.this, LogoutService.class);
        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
        deauthenticateIntent.putExtra("token", prefs.getString("token", ""));
        editor.remove("token");
        editor.apply();
        Intent startIntent = new Intent(Settings.this, Landing.class);
        startIntent.putExtra("finish", true);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startService(deauthenticateIntent);
        startActivity(startIntent);
        finish();
    }

    private class AddContactReceiver extends BroadcastReceiver {

        private AddContactReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);

            if (!successful) {
                Toast.makeText(Settings.this, "An error occurred.", Toast.LENGTH_LONG).show();
            }

            LocalBroadcastManager.getInstance(Settings.this).unregisterReceiver(this);
        }

    }

    private class RemoveContactReceiver extends BroadcastReceiver {

        private RemoveContactReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);

            if (!successful) {
                Toast.makeText(Settings.this, "An error occurred.", Toast.LENGTH_LONG).show();
            }

            LocalBroadcastManager.getInstance(Settings.this).unregisterReceiver(this);
        }

    }

}