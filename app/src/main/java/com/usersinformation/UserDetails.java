package com.usersinformation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.userdatautilities.DatabaseHelper.User;
import com.userdatautilities.DatabaseHelper.UserDatabaseQueryDao;
import com.usersinformation.ImageLoader.ImageDisplaying;
import com.usersinformation.Utils.APIRequest;
import com.usersinformation.Utils.UtilityMainClass;
/**
 * Created by Ajay on 07/09/18.
 * Modified by Ajay on 08/09/18.
 * Display details
 * Deleted from the local database
 * Decrypted email id and display along with the user details
 */

public class UserDetails extends AppCompatActivity {
    TextView name,user_id,gender,age,dob,email,query_delete;
    ImageView user_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User Details");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
try {
   final User fullUserDetails = (User) getIntent().getBundleExtra("user_data").getSerializable("user_data");
    user_photo = (ImageView) findViewById(R.id.user_photo);
    gender = (TextView) findViewById(R.id.gender);
    name = (TextView) findViewById(R.id.name);
    user_id = (TextView) findViewById(R.id.user_id);
    age = (TextView) findViewById(R.id.age);
    dob = (TextView) findViewById(R.id.dob);
    email = (TextView) findViewById(R.id.email);
    query_delete= (TextView) findViewById(R.id.query_delete);
    name.setText( fullUserDetails.getName());
    user_id.setText(fullUserDetails.getId_name()+" ("+fullUserDetails.getValue()+")");
    if(fullUserDetails.getId_name().length()<1 && fullUserDetails.getValue().length()<1 )
        user_id.setVisibility(View.GONE);
    age.setText(fullUserDetails.getAge());
     dob.setText(fullUserDetails.getDate().split("T")[0]);
    gender.setText(fullUserDetails.getGender());
    email.setText(UtilityMainClass.decryptText(UserDetails.this,fullUserDetails.getEmail()));
    new ImageDisplaying().loadCircleImageFromURL(UserDetails.this, fullUserDetails.getLarge(), user_photo, R.mipmap.user_default);

    query_delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(UserDetails.this);
            builder.setTitle("Warning");
            builder.setMessage("Are you sure want to delete?");
// Add the buttons
            builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    new deleteRecordAsyncTask(UserDetails.this,MainApplication.getDatabaseInstace(UserDetails.this).userDao()).execute(fullUserDetails);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    });
}catch(Exception e){e.printStackTrace();}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private class deleteRecordAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDatabaseQueryDao mAsyncTaskDao;
        Activity mActivity;
        deleteRecordAsyncTask(Activity mActivity, UserDatabaseQueryDao dao) {
            mAsyncTaskDao = dao;
            this.mActivity=mActivity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(final User... params) {
            try {
                mAsyncTaskDao.delete(params[0]);
            }catch(Exception e){e.printStackTrace();}
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            AlertDialog alertDialog = UtilityMainClass.showAlert(UserDetails.this ,"Deleted Successfully");
//            alertDialog.show();
            Toast.makeText(UserDetails.this ,"Deleted Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}

