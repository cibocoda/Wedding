package com.gmf.wedding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Top2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top2);
        setTitle("管理者頁面");

        InitItems();
    }

    //每顆按鈕只有名稱及連結的頁面不同，使用同樣的初始化，所以程式碼使用引用的方式
    public void InitItems(){
        Button bt_setup = (Button)findViewById(R.id.BT_setup);
        InitButtons(bt_setup,"設定", 1);
        Button bt_accounting = (Button)findViewById(R.id.BT_accounting);
        InitButtons(bt_accounting,"會計", 2);
        Button bt_project = (Button)findViewById(R.id.BT_project);
        InitButtons(bt_project,"專案", 3);
        Button bt_management = (Button)findViewById(R.id.BT_management);
        InitButtons(bt_management,"管理", 4);
    }

    //將來四個頁面整合時，再填上另外三個連結的頁面名稱
    public void InitButtons(Button bt, final String str, final int page){
        bt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(Top2Activity.this, str, Toast.LENGTH_SHORT).show();
                switch (page){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent intent = new Intent(Top2Activity.this,ProjectActivity.class);
                        startActivity(intent);
                        Top2Activity.this.finish();
                        break;
                    case 4:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Top2Activity.this,AccountActivity.class);
        startActivity(intent);
        Top2Activity.this.finish();
    }
}
