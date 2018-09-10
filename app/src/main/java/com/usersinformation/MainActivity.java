package com.usersinformation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.persistence.room.ColumnInfo;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.userdatautilities.DatabaseHelper.User;
import com.userdatautilities.DatabaseHelper.UserDatabaseQueryDao;
import com.userdatautilities.NetworkRestApi.NetworkClassHandler;
import com.userdatautilities.NetworkRestApi.OkHttpInterface;
import com.userdatautilities.UserFingerPrint.FingerPrintChecker;
import com.userdatautilities.UserFingerPrint.FingerPrintDialog;
import com.userdatautilities.UserFingerPrint.FingerprintHelper;
import com.usersinformation.Adapter.UserListAdapter;
import com.usersinformation.Utils.APIRequest;
import com.usersinformation.Utils.OnLoadMoreListener;
import com.usersinformation.Utils.UtilityMainClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by Ajay on 06/09/18.
 * Modified by Ajay on 07-08/09/18.
 * Display the users list
 * Added side menu to perform the required fucntionalit like Query User, Exit from the App
 * Requested Page 1 number of results 20 and implemented paginationation based on page number 1, 2,3 so on
 * Added floating button also to clear and re-load all content from the server if user is connected to the internet
 * Inserted into the Room Database
 * Get All record from database
 * Filter list based on Gender
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener , OkHttpInterface {
    ProgressDialog dialog;
    RecyclerView recyclerView;
    UserListAdapter adapter;
    DrawerLayout drawer;
    int results=20;
    List<User> users_list=new ArrayList<>();
    int page= 1;
    static {
        System.loadLibrary("native-lib");
    }
    public native static String stringFromJNI();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("User List");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dialog.isShowing()){
                    if (UtilityMainClass.internetConnectionAvailable(APIRequest.API_REQUEST_TIMEOUT))
                    {
                        queryRequestonServer(results,APIRequest.REQUEST_API);
                    }else{
                        AlertDialog alertDialog = UtilityMainClass.showAlert(MainActivity.this);
                        alertDialog.show();
                    }

                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view);
        dialog= UtilityMainClass.onCreateDialog(this);
        dialog.cancel();
        /**
         * Check the connection or timeout first
         * if user not connected then look the local stored information and display accordingly on the listview
         * */


        // Get an instance of the fingerprint manager through the getSystemService method
        final FingerprintManager fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        FingerPrintDialog mFingerPrintDialog =new FingerPrintDialog();
        // Our fingerprint checker
        final FingerPrintChecker checker = new FingerPrintChecker(MainActivity.this, fingerprintManager);
        if (checker.isAbleToUseFingerPrint()) {
            mFingerPrintDialog.generateAuthenticationKey(new String(stringFromJNI()));
            if (mFingerPrintDialog.isCipherInitialized(new String(stringFromJNI()))) {
                // A wrapper for the crypto objects supported by the FingerprintManager
                final FingerprintManager.CryptoObject cryptoObject =  new FingerprintManager.CryptoObject(mFingerPrintDialog. getCipher());
                // Our fingerprint callback helper
                final FingerprintHelper fingerprintHelper = new FingerprintHelper(this);
                fingerprintHelper.authenticate(fingerprintManager, cryptoObject);
            }
        }
        queryRequestonServer(results ,APIRequest.REQUEST_API);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {

        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.male) {
            page= 1;
            new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao() ,"male").execute();
            return true;
        }
        if (id == R.id.female) {
            page= 1;
            new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao(),"female" ).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_query:
            Intent intent = new Intent(MainActivity.this, QueryUser.class);
            startActivityForResult(intent, APIRequest.REQUEST_API);
            break;
            case R.id.nav_view:
                if (drawer!=null &&  drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                page= 1;
                new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao() ,"").execute();
                break;
            case R.id.nav_exit:
                finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            switch (requestCode) {
//                case APIRequest.REQUEST_API:
//                    new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao(),"" ).execute();
//                    break;
//            }
            new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao(),"" ).execute();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onResponse(String s, int i, Map<String, String> map) {

        try {
            switch (i) {
                case APIRequest.REQUEST_API:
                    new insertAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao() ,s).execute();
                    break;
                case APIRequest.LOAD_MORE:
                    new insertAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao() ,s).execute();
                    break;
            }
        }catch (Exception e){
            dialog.cancel();
            e.printStackTrace();}

    }

    /*
    * RequestBulkDataFromServer
    * */
    public void queryRequestonServer(int resultCount ,int requestcode) {
        if (UtilityMainClass.internetConnectionAvailable(APIRequest.API_REQUEST_TIMEOUT)) {
            if(requestcode!=APIRequest.LOAD_MORE)
            dialog.show();
            HashMap<String, String> params = new HashMap<>();
            params.put("ostype", "Android");
            params.put("IS_DEBUG", "true");
            params.put("REQUEST_TYPE", "GET");
            params.put("results", String.valueOf(resultCount));
            new NetworkClassHandler().onRequest(MainActivity.this,requestcode, APIRequest.MULTIPLE_RESULT_USER+"?page="+page+"&results="+String.valueOf(resultCount), params);
        } else {
            dialog.show();
            new getAllAsyncTask(MainActivity.this,MainApplication.getDatabaseInstace(MainActivity.this).userDao(),"" ).execute();
        }
    }

    private class getAllAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDatabaseQueryDao mAsyncTaskDao;
//        private List<User> users_list;
        Activity mActivity;
        String mFilter;
        getAllAsyncTask(Activity mActivity, UserDatabaseQueryDao dao , String mFilter ) {
            mAsyncTaskDao = dao;
            this.mActivity=mActivity;
            this.mFilter =mFilter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(users_list!=null)
            users_list.clear();

        }

        @Override
        protected Void doInBackground(final User... params) {
            if(mFilter.length()>0){
                users_list =   mAsyncTaskDao.getAllUsersBasedOnGender(mFilter);
            }else{
                users_list =   mAsyncTaskDao.getAll();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.cancel();
            inItListView(users_list);
        }
    }


    private class insertAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDatabaseQueryDao mAsyncTaskDao;
        Activity mActivity;
        String SERVER_RESPONSE;
        boolean isReInitiateList=true;
        int previousesize=0;
        insertAsyncTask(Activity mActivity, UserDatabaseQueryDao dao , String RESPONSE) {
            mAsyncTaskDao = dao;
            this.mActivity=mActivity;
            SERVER_RESPONSE = RESPONSE;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            previousesize=users_list.size();
            if(users_list.size()>0)
            isReInitiateList=false;
        }
        @Override
        protected Void doInBackground(final User... params) {
        try {
        JSONObject response = new JSONObject(SERVER_RESPONSE);
        JSONArray resultArray = response.getJSONArray("results");
        Log.d("resultArray",""+resultArray.length());
        if(resultArray.length()>0){
            if(isReInitiateList == true)
            mAsyncTaskDao.nukeTable();
            for (int j = 0; j < resultArray.length(); j++) {
                JSONObject user_info = resultArray.getJSONObject(j);
                User mUser=UtilityMainClass.creatUserForInsert(MainActivity.this,j,user_info);
                if(mUser!=null) {
                    mUser.setSeed(response.getJSONObject("info").getString("seed"));
                    users_list.add(mUser);
                    mAsyncTaskDao.insertAll(mUser);
                }
            }
        }else{
            page =page-1;
        }

        }catch(Exception e){e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.cancel();
            if(isReInitiateList == true && users_list.size()>0)
            {
                inItListView(users_list);
            }else if(adapter!=null){
                adapter.notifi_list(previousesize-1 , users_list.size()-1);
            }

        }
    }
    public void inItListView(List<User> musers_list ){
        if(musers_list.size()>0) {
            this.users_list = musers_list;
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
            adapter = new UserListAdapter(MainActivity.this, users_list, recyclerView);
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    page = page + 1;
                    queryRequestonServer(results, APIRequest.LOAD_MORE);
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.setLoaded();
        }
    }

}
