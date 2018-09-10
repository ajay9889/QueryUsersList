package com.usersinformation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.userdatautilities.DatabaseHelper.User;
import com.userdatautilities.DatabaseHelper.UserDatabaseQueryDao;
import com.userdatautilities.NetworkRestApi.NetworkClassHandler;
import com.userdatautilities.NetworkRestApi.OkHttpInterface;
import com.usersinformation.Utils.APIRequest;
import com.usersinformation.Utils.UtilityMainClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ajay on 07/09/18.
 * Modified by Ajay on 08/09/18.
 * Get user from server if user is connected to the internet other will look on local database to show the result
 * Automatically navigate to the details screen
 * Seed or only selected Gender user can get the record
 */
public class QueryUser extends AppCompatActivity implements View.OnClickListener,OkHttpInterface {
   TextView query_request;
    TextView gender;
    TextView selectgender;
   EditText multiple_user_edit,user_id;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.query_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Query User");

        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        gender = (TextView)findViewById(R.id.gender);
        query_request = (TextView)findViewById(R.id.query_request);
        selectgender = (TextView)findViewById(R.id.selectgender);
        user_id = (EditText)findViewById(R.id.user_id);
        multiple_user_edit = (EditText)findViewById(R.id.multiple_user_edit);
        selectgender.setOnClickListener(this);
        query_request.setOnClickListener(this);
        dialog= UtilityMainClass.onCreateDialog(this);
        dialog.cancel();
        gender.setTypeface(UtilityMainClass.fontawesome(this));

        multiple_user_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                    queryRequestonServer();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void queryRequestonServer(){
//        if(Validation(user_id )){
            if(UtilityMainClass.internetConnectionAvailable(4000))
            {
               dialog.show();
                HashMap<String , String> params= new HashMap<>();
                params.put("ostype","Android");
                params.put("IS_DEBUG","true");
                params.put("REQUEST_TYPE","GET");
                params.put("results",multiple_user_edit.getText().toString());
                String url="";
                if(user_id.getText().toString().length()>0){
                    url= APIRequest. GETUSER_SEED+"?seed="+user_id.getText().toString()+"&gender="+selectgender.getText().toString().toLowerCase();
                }else{
                    url= APIRequest. GETUSER_SEED+"?gender="+selectgender.getText().toString().toLowerCase();
                }
                new NetworkClassHandler().onRequest(QueryUser.this, APIRequest.REQUEST_QUERY,url,params );
            }else {
                new getUserDetailsAsyncTask(QueryUser.this, MainApplication.getDatabaseInstace(QueryUser.this).userDao()).execute();
            }
    }
    private class getUserDetailsAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDatabaseQueryDao mAsyncTaskDao;
        private List<User> users_list;
        Activity mActivity;
        getUserDetailsAsyncTask(Activity mActivity, UserDatabaseQueryDao dao) {
            mAsyncTaskDao = dao;
            this.mActivity=mActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(final User... params) {
            if(user_id.getText().toString().length()>0){
                users_list =   mAsyncTaskDao.getAllUsers(selectgender.getText().toString().toLowerCase() ,user_id.getText().toString().toLowerCase() ,1);
            }else{
                users_list =   mAsyncTaskDao.getUsersBasedOnGender(selectgender.getText().toString().toLowerCase(), 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(users_list.size()>0){
                Intent intent = new Intent(QueryUser.this, UserDetails.class);
                Bundle bndl= new Bundle();
                bndl.putSerializable("user_data",users_list.get(0));
                intent.putExtra("user_data" ,bndl );
                startActivityForResult(intent, APIRequest.REQUEST_DETAILS);
            }else{
                AlertDialog alertDialog = UtilityMainClass.showAlert(QueryUser.this );
                alertDialog.show();
            }


        }
    }

    public boolean Validation(EditText user_id ) {
        user_id.setError(null);
        String msg="";
        if (TextUtils.isEmpty(user_id.getText().toString().trim())) {
            msg = getResources().getString(R.string.user_id);
            user_id.setError(msg);
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch ((view.getId()))
        {
            case R.id.selectgender:
                PopupMenu dropDownMenu = new PopupMenu(QueryUser.this, selectgender);
                dropDownMenu.getMenuInflater().inflate(R.menu.select_gender, dropDownMenu.getMenu());
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        selectgender.setText(menuItem.getTitle());
//                        queryRequestonServer();
                        return true;
                    }
                });
                dropDownMenu.show();

                break;
            case R.id.query_request:
                queryRequestonServer();
                break;
        }
    }

    @Override
    public void onResponse(final String s, int i, Map<String, String> map) {
        dialog.cancel();

            switch (i) {
                case APIRequest.REQUEST_QUERY:

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                            if(s!=null && s.contains("results")) {
                                new insertAsyncTask(QueryUser.this,MainApplication.getDatabaseInstace(QueryUser.this).userDao() ,s).execute();
                            }else{
                                AlertDialog alertDialog = UtilityMainClass.showAlert(QueryUser.this ,s);
                                alertDialog.show();
                            }
                            }catch (Exception e){e.printStackTrace();}
                        }
                    });

                    break;
            }


    }


    private class insertAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDatabaseQueryDao mAsyncTaskDao;
        Activity mActivity;
        String SERVER_RESPONSE;
        User mUser =null;
        insertAsyncTask(Activity mActivity, UserDatabaseQueryDao dao , String RESPONSE) {
            mAsyncTaskDao = dao;
            this.mActivity=mActivity;
            SERVER_RESPONSE=RESPONSE;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(final User... params) {
            try {
                if(SERVER_RESPONSE!=null) {
                    JSONObject response = new JSONObject(SERVER_RESPONSE);
                    JSONArray resultArray = response.getJSONArray("results");
                    Log.d("resultArray", "" + resultArray.length());
                    if (resultArray.length() > 0) {
                        for (int j = 0; j < resultArray.length(); j++) {
                            JSONObject user_info = resultArray.getJSONObject(j);
                            mUser = UtilityMainClass.creatUserForInsert(QueryUser.this, j, user_info);
                            if (mUser != null) {
                                mUser.setSeed(response.getJSONObject("info").getString("seed"));
                                mAsyncTaskDao.insertAll(mUser);
                            }
                        }
                    }
                }
            }catch(Exception e){e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mUser!=null){
                Intent intent = new Intent(QueryUser.this, UserDetails.class);
                Bundle bndl= new Bundle();
                bndl.putSerializable("user_data",mUser);
                intent.putExtra("user_data" ,bndl );
                startActivityForResult(intent, APIRequest.REQUEST_DETAILS);
            }
        }
    }
}

