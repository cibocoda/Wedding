package com.gmf.wedding;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DressesFragment extends Fragment {
    private ViewPager viewPager;
    private LinearLayout dresses_list, dresses;
    private ArrayList<String> categoryArray = new ArrayList<>();
    private Map<String,ArrayList<String>> productsArray = new HashMap<String,ArrayList<String>>();
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private int opreDressList = 0, opreDress = 0, cdressCount, cdressListSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dresses, container, false);

        String project = getResources().getString(R.string.project_dresses);
        getActivity().setTitle(project);

        viewPager = (ViewPager) rootView.findViewById(R.id.VP_dresses);
        dresses = (LinearLayout) rootView.findViewById(R.id.LL_dresses);
        dresses_list = (LinearLayout) rootView.findViewById(R.id.LL_dresses_list);

        ((ProjectActivity)getActivity()).drawerLock(false);

        new AsyncProducts().execute("true");

        return rootView;
    }

    public class AsyncProducts extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                // Enter URL address where your php file resides http://zoptest.esy.es/
                url = new URL("http://192.168.1.103/wedding_management/DBproducts.php");

            }catch (MalformedURLException e){
                e.printStackTrace();
                return "exception";
            }

            try{
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
				conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("ask_server", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }

            try{
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK){
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());
                }else {
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }finally{
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result){
            //this method will be running on UI thread

            if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(getActivity(), "連線問題", Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    String dupeWord = null;
                    int temp_num = 0;
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        String keyWord = jsonData.getString("category");
                        if(!keyWord.equals(dupeWord)){
                            dupeWord = keyWord;
                            categoryArray.add(keyWord);
                            InitDressesList(categoryArray.get(temp_num),temp_num);
                            temp_num++;
                        }
                    }

                    for(int i=0; i<categoryArray.size();i++){
                        productsArray.put(categoryArray.get(i), new ArrayList<String>());
                        for(int j = 0; j < jsonArray.length(); j++){
                            JSONObject jsonData = jsonArray.getJSONObject(j);
                            if(categoryArray.get(i).equals(jsonData.getString("category"))){
                                productsArray.get(categoryArray.get(i)).add(jsonData.getString("name"));
                            }
                        }
                    }
                    pdLoading.dismiss();
                    InitDresses(opreDressList);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "json問題", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    public void InitDressesList(String category, final int i){
        Button button = new Button(getActivity());
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT); //設定值的layout_width跟layout_height
        button.setLayoutParams(layoutParams1); //將上面的設定值，設定入新增的按鈕裡
        button.setBackgroundResource(R.drawable.button_style_2); //設定背景
        button.setText(category); //設定按鈕文字
        button.setGravity(Gravity.CENTER); //設定文字位置置中
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InitDresses(i);
            }
        });
        dresses_list.addView(button); //將按鈕新增到 dresses_list 這個 LinearLayout 裡
    }

    public void InitViewPager(int cdressList){

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(getActivity(), cdressList);
        viewPager.setAdapter(mCustomPagerAdapter);

        //以下利用交換adapter的方式，在資料更新之後能更新禮服大圖，否則禮服大圖依然是舊畫面
        PagerAdapter adapter = viewPager.getAdapter();
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //===============================================
        viewPager.setCurrentItem(opreDress);

    }

    //初始化禮服小圖
    public void InitDresses(int cdressList){
        dresses.removeAllViews(); //將舊的禮服小圖全部刪除，不然會新增到舊圖組的後方

        cdressCount = productsArray.get(categoryArray.get(cdressList)).size();
        cdressListSize = cdressCount;

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("禮服準備中");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(cdressListSize);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        for(int i=0; i<cdressListSize; i++){
            ImageView iv = new ImageView(getActivity());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT);  //將寬度設為100dp
            layoutParams1.setMargins(2, 4, 2, 0); // 讓每個小圖產生間隔，左2dp、上4dp、右2dp、下0dp
            iv.setLayoutParams(layoutParams1);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE); //ScaleType.CENTER_INSIDE會讓圖符合邊框的長或寬，整張納入並且置中
            String imageUrl = "http://192.168.1.103/wedding_management/pictures/photo/"+productsArray.get(categoryArray.get(cdressList)).get(i)+".png";
            Picasso.with(getActivity())
                    .load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder( R.drawable.progress_animation )
                    .into(iv, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            cdressCount--;
                            mProgressDialog.setProgress(cdressListSize - cdressCount);
                            if(cdressCount == 0){
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
            iv.setBackgroundResource(R.color.white); //設置 android:background
            iv.setClickable(true); //設置android:clickable="true"，讓小圖能夠點選
            dresses.addView(iv);
            InitDressClick(iv,i);
        }

        InitViewPager(cdressList);
    }

    //初始化小圖點選
    public void InitDressClick(ImageView iv, final int i){
        iv.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                viewPager.setCurrentItem(i,false); //setCurrentItem(i)預設是滑動換圖，增加false則會立刻跳圖
            }
        });
    }

    public class CustomPagerAdapter extends PagerAdapter{
        Context mContext;
        LayoutInflater mLayoutInflater;
        int mcdressList;

        CustomPagerAdapter(Context context, int cdressList) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mcdressList = cdressList;
        }

        @Override
        public int getCount() {
            return productsArray.get(categoryArray.get(mcdressList)).size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.items_dresses, container, false);

            final ImageView imageView = (ImageView) itemView.findViewById(R.id.IV_dresses);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams1);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            final String imageUrl = "http://192.168.1.103/wedding_management/pictures/photo/"+productsArray.get(categoryArray.get(mcdressList)).get(position)+".png";
            Picasso.with(getActivity())
                    .load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setClickable(true);
                            imageView.setOnClickListener(new ImageView.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    opreDressList = mcdressList;
                                    opreDress = position;
                                    D_ConfirmFragment frg = new D_ConfirmFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("selected_dress", imageUrl);
                                    frg.setArguments(bundle);

                                    FragmentManager fragMgr = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = fragMgr.beginTransaction();
                                    transaction.setCustomAnimations(R.anim.fragment_in_from_right_side, R.anim.fragment_out_from_left_side, R.anim.fragment_in_from_left_side, R.anim.fragment_out_from_right_side).replace(R.id.FL_dresses, frg, frg.getClass().getName()).addToBackStack(null).commit();
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (FragmentUtils.sDisableFragmentAnimations) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

}
