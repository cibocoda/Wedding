package com.gmf.wedding;


import android.content.Context;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class DressesFragment extends Fragment {
    private ViewPager viewPager;
    private LinearLayout dresses_list, dresses;
    private int[] imgIdArray, iconIdArray, cdressIdArray;
    private int[] dressesList = new ItemGroupDB().dresses_list; //新增陣列引用ItemGroudDB的禮服種類
    private int opreDressList = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dresses, container, false);

        String project = getResources().getString(R.string.project_dresses);
        getActivity().setTitle(project);

        viewPager = (ViewPager) rootView.findViewById(R.id.VP_dresses);
        dresses = (LinearLayout) rootView.findViewById(R.id.LL_dresses);
        dresses_list = (LinearLayout) rootView.findViewById(R.id.LL_dresses_list);

        InitViewPager(opreDressList);
        InitDressesList();

        return rootView;
    }

    public void InitDressesList(){
        for(int i=0; i<dressesList.length; i++){
            Button button = new Button(getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT); //設定值的layout_width跟layout_height
            button.setLayoutParams(layoutParams1); //將上面的設定值，設定入新增的按鈕裡
            button.setBackgroundResource(R.drawable.button_style_2); //設定背景
            String str = getResources().getString(dressesList[i]); //從禮服種類裡面取出文字
            button.setText(str); //設定按鈕文字為取出的文字
            button.setGravity(Gravity.CENTER); //設定文字位置置中
            dresses_list.addView(button); //將按鈕新增到 dresses_list 這個 LinearLayout 裡
            InitButtons(button, i);
        }
    }

    //初始化禮服種類按鈕，每次按下按鈕，就更改資料重新載入大小圖
    public void InitButtons(Button bt,final int i){
        bt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InitViewPager(i);
            }
        });
    }

    public void InitViewPager(int dress){

        //當按下禮服種類時，傳送按下的代號，用switch case判斷選擇的項目
        switch(dress){
            case 0:
                imgIdArray = null;
                iconIdArray = null;
                cdressIdArray = null;
                imgIdArray = new ItemGroupDB().dress_wedding; //載入禮服大圖陣列
                iconIdArray = new ItemGroupDB().dress_wedding_icon; //載入禮服小圖陣列
                cdressIdArray = new ItemGroupDB().dress_cwedding; //載入禮服c圖陣列
                break;
            case 1:
                imgIdArray = null;
                iconIdArray = null;
                cdressIdArray = null;
                imgIdArray = new ItemGroupDB().dress_maxi;
                iconIdArray = new ItemGroupDB().dress_maxi_icon;
                cdressIdArray = new ItemGroupDB().dress_cmaxi;
                break;
            case 2:
                imgIdArray = null;
                iconIdArray = null;
                cdressIdArray = null;
                imgIdArray = new ItemGroupDB().dress_cocktail;
                iconIdArray = new ItemGroupDB().dress_cocktail_icon;
                cdressIdArray = new ItemGroupDB().dress_ccocktail;
                break;
            case 3:
                imgIdArray = null;
                iconIdArray = null;
                cdressIdArray = null;
                imgIdArray = new ItemGroupDB().dress_white;
                iconIdArray = new ItemGroupDB().dress_white_icon;
                cdressIdArray = new ItemGroupDB().dress_cwhite;
                break;
            case 4:
                imgIdArray = null;
                iconIdArray = null;
                cdressIdArray = null;
                imgIdArray = new ItemGroupDB().dress_black;
                iconIdArray = new ItemGroupDB().dress_black_icon;
                cdressIdArray = new ItemGroupDB().dress_cblack;
                break;
        }

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(getContext(), imgIdArray, cdressIdArray, dress);
        viewPager.setAdapter(mCustomPagerAdapter);

        //以下利用交換adapter的方式，在資料更新之後能更新禮服大圖，否則禮服大圖依然是舊畫面
        PagerAdapter adapter = viewPager.getAdapter();
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //===============================================
        InitDresses();
        viewPager.setCurrentItem(0);
    }

    //初始化禮服小圖
    public void InitDresses(){
        dresses.removeAllViews(); //將舊的禮服小圖全部刪除，不然會新增到舊圖組的後方

        for(int i=0; i<iconIdArray.length; i++){
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT);  //將寬度設為100dp
            layoutParams1.setMargins(2, 4, 2, 0); // 讓每個小圖產生間隔，左2dp、上4dp、右2dp、下0dp
            iv.setLayoutParams(layoutParams1);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE); //ScaleType.CENTER_INSIDE會讓圖符合邊框的長或寬，整張納入並且置中
            iv.setImageResource(iconIdArray[i]); //設置 android:src
            iv.setBackgroundResource(R.color.white); //設置 android:background
            iv.setClickable(true); //設置android:clickable="true"，讓小圖能夠點選
            dresses.addView(iv);
            InitDressClick(iv,i);
        }
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
        int[] mResources, mcdresses;
        int preDressList;

        CustomPagerAdapter(Context context, int[] dresses, int[] cdresses, int pdl) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mResources = dresses;
            mcdresses = cdresses;
            preDressList = pdl;
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.items_dresses, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.IV_dresses);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams1);
            imageView.setBackgroundResource(mResources[position]);
            imageView.setClickable(true);
            imageView.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    opreDressList = preDressList;
                    D_ConfirmFragment frg = new D_ConfirmFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("selected_dress", mcdresses[position]);
                    frg.setArguments(bundle);

                    FragmentManager fragMgr = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragMgr.beginTransaction();
                    transaction.setCustomAnimations(R.anim.fragment_in_from_right_side, R.anim.fragment_out_from_left_side, R.anim.fragment_in_from_left_side, R.anim.fragment_out_from_right_side).replace(R.id.FL_dresses, frg, frg.getClass().getName()).addToBackStack(null).commit();
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
