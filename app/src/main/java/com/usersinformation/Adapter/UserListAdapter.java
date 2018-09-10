package com.usersinformation.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.userdatautilities.DatabaseHelper.User;
import com.usersinformation.ImageLoader.ImageDisplaying;
import com.usersinformation.R;
import com.usersinformation.UserDetails;
import com.usersinformation.Utils.APIRequest;
import com.usersinformation.Utils.OnLoadMoreListener;
import com.usersinformation.Utils.RecyclerViewHolder;
import com.usersinformation.Utils.RecyclerViewPositionHelper;
import com.usersinformation.Utils.UtilityMainClass;

import org.json.JSONObject;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    List<User> mUser;
    RecyclerView recyclerView;
    ImageDisplaying imageDownloader;
    private boolean isLoading;
    private Activity context;
    int loaded_size=0;
    Handler refreshHandler;
    RecyclerViewPositionHelper mRecyclerViewHelper;
    int lastVisibleItem=0;
    private OnLoadMoreListener mOnLoadMoreListener;

    /*
    * Display here the lsit of user using recyclerView adapter
    * Decrypted email id and display along with the user details
    * */
    public UserListAdapter(Activity context, List<User> mUsers, RecyclerView recyclerView) {
        try {
            this.context = context;
            imageDownloader=new ImageDisplaying();
            this.mUser = mUsers;
            this.recyclerView=recyclerView;

            this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                    lastVisibleItem = mRecyclerViewHelper.findLastVisibleItemPosition();
                    if ((lastVisibleItem == mUser.size() - 1)) {
                        loadMoreItems();
                    }
                }
            });
        }catch(Exception e){e.printStackTrace();}

    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    };
    public void setLoaded() {
        isLoading = false;
    }
    public synchronized void loadMoreItems() {
        if(!isLoading) {
            if (mOnLoadMoreListener != null) {
                isLoading=true;
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }
    public synchronized void notifi_list(final int prev, final int last) {
        try {
            loaded_size=prev;
            recyclerView.getAdapter().notifyItemChanged(prev - 1);
            if(refreshHandler!=null)
            {
                refreshHandler.removeCallbacks(refreshRun);
                refreshHandler = null;
            }
            refreshHandler =   new Handler();
            refreshHandler.postDelayed(refreshRun,300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Runnable refreshRun =new Runnable() {
        @Override
        public void run() {
            setLoaded();
        }
    };
    @Override
    public int getItemCount() {
        return (null != mUser ? mUser.size(): 0);
    }

    @Override
    public synchronized void onBindViewHolder(RecyclerViewHolder holder,  final int position) {
        try {
            final RecyclerViewHolder mainHolder = (RecyclerViewHolder) holder;
            final User mSellerSingleObject = mUser.get(position);
            mainHolder.name.setText(mSellerSingleObject.getName());
            mainHolder.gender.setText(mSellerSingleObject.getGender());
            mainHolder.email.setText(mSellerSingleObject.getEmail());
            mainHolder.email.setText(UtilityMainClass.decryptText(context,mSellerSingleObject.getEmail()));
//            mainHolder.email.setText(mSellerSingleObject.getEmail());
            mainHolder.user_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bndl= new Bundle();
                    bndl.putSerializable("user_data",mSellerSingleObject);
                    Intent intent = new Intent(context, UserDetails.class);
                    intent.putExtra("user_data" ,bndl );
                    context.startActivityForResult(intent, APIRequest.REQUEST_DETAILS);
                }
            });
            imageDownloader.loadCircleImageFromURL(context, mSellerSingleObject.getThumbnail(), mainHolder.user_photo, R.mipmap.user_default);
        }catch(Exception e){e.printStackTrace();}
    }
    @Override
    public synchronized RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_row, viewGroup, false);
        return new RecyclerViewHolder(view);
    }
}