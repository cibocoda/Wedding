package com.gmf.wedding;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * A simple {@link Fragment} subclass.
 */
public class D_ConfirmFragment extends Fragment {
    private View rootView;
    private ImageView iv_selected, iv_stats;
    private DisplayMetrics dm;
    private Bitmap bm;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF mid = new PointF();
    private PointF start = new PointF();
    private int mode = 0;
    private static int DRAG = 2;
    private static int ZOOM = 1;
    private static int NONE = 0;
    private float oldDist = 1f;
    private static float MINSCALER = 0.3f;
    private static float MAXSCALER = 3.0f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_d_confirm, container, false);

        ((ProjectActivity)getActivity()).drawerLock(true);

        Bundle bundle = getArguments();
        String imgUrl = bundle.getString("selected_dress");
        //int imgId = bundle.getInt("selected_dress");

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm); //獲取分辨率

        iv_selected = (ImageView)rootView.findViewById(R.id.IV_dc_dress);
        iv_stats = (ImageView)rootView.findViewById(R.id.IV_dc_stats);

        Picasso.with(getActivity())
                .load(imgUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                        /* Save the bitmap or do something with it here */

                        //Set it in the ImageView
                        bm = bitmap;
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {}
                });
        //bm = BitmapFactory.decodeResource(getResources(), imgId);

        savedMatrix.setTranslate((dm.widthPixels - bm.getWidth())/2 , (dm.heightPixels - bm.getHeight()) / 2);
        iv_selected.setImageMatrix(savedMatrix);
        iv_selected.setImageBitmap(bm);
        iv_selected.setOnTouchListener(new TouchEvent());

        InitItems();

        return rootView;
    }

    class TouchEvent implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch(event.getActionMasked()){

                //單擊觸控，用於拖動
                case MotionEvent.ACTION_DOWN :
                    matrix.set(iv_selected.getImageMatrix());
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;

                //多點觸控，按下時
                case MotionEvent.ACTION_POINTER_DOWN :
                    oldDist = getSpacing(event);
                    savedMatrix.set(matrix);
                    getMidPoint(mid,event);
                    mode = ZOOM;
                    break;
                //多點觸控，擡起時
                case MotionEvent.ACTION_POINTER_UP :
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE :
                    if(mode == DRAG){
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }else if(mode == ZOOM){//縮放
                        //取得多指移動的直徑，如果大於10，則認為是縮放手勢
                        float newDist = getSpacing(event);
                        if(newDist > 10){
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }
            iv_selected.setImageMatrix(matrix);
            controlScale();
            center();
            return true;
        }

    }

    //求距離
    private float getSpacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);

    }

    //求中點
    private void getMidPoint(PointF mid,MotionEvent event){
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        mid.set(x / 2, y / 2);
    }

    //控制縮放比例
    private void controlScale(){
        float values[] = new float[9];
        matrix.getValues(values);
        if(mode == ZOOM){
            if(values[0] < MINSCALER)
                matrix.setScale(MINSCALER, MINSCALER);
            else if(values[0] > MAXSCALER)
                matrix.setScale(MAXSCALER, MAXSCALER);
        }
    }

    //自動居中 左右及上下都居中
    protected void center(){
        center(true,true);
    }

    private void center(boolean horizontal, boolean vertical){
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical){
            int screenHeight = dm.heightPixels; //手機屏幕分辨率的高度
            if (height < screenHeight){
                deltaY = (screenHeight - height)/2 - rect.top;
            }else if (rect.top > 0){
                deltaY = -rect.top;
            }else if (rect.bottom < screenHeight){
                deltaY = screenHeight - rect.bottom;
            }
        }

        if (horizontal){
            int screenWidth = dm.widthPixels; //手機屏幕分辨率的寬度
            if (width < screenWidth){
                deltaX = (screenWidth - width)/2 - rect.left;
            }else if (rect.left > 0){
                deltaX = -rect.left;
            }else if (rect.right < screenWidth){
                deltaX = screenWidth - rect.right;
            }
        }

        matrix.postTranslate(deltaX, deltaY);
    }

    public void InitItems(){
        Button bt_dc_buy = (Button)rootView.findViewById(R.id.BT_dc_buy);
        bt_dc_buy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                iv_stats.setImageResource(R.drawable.dc_buy);
            }
        });

        Button bt_dc_rent = (Button)rootView.findViewById(R.id.BT_dc_rent);
        bt_dc_rent.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                iv_stats.setImageResource(R.drawable.dc_rent);
            }
        });

        Button bt_dc_both = (Button)rootView.findViewById(R.id.BT_dc_both);
        bt_dc_both.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                iv_stats.setImageResource(R.drawable.dc_both);
            }
        });
    }

}
