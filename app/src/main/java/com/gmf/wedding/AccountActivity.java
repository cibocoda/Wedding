package com.gmf.wedding;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        InitItems();
    }

    //初始化按鈕
    private void InitItems(){
        Button bt_login_login = (Button)findViewById(R.id.BT_login_login);
        bt_login_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CheckUsers(); //按下登入鍵，進入確認帳號密碼程序
            }
        });

        Button bt_cancel_login = (Button)findViewById(R.id.BT_cancel_login);
        bt_cancel_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GoGuest();
            }
        });
    }

    /* 按下登入時，檢查帳號跟密碼是否符合設定，
	 * 兩組帳號/密碼分別是 boss/boss 以及 emilia/emilia，
	 * 兩組帳號密碼之外按下登入鍵則出現提示訊息*/
    private void CheckUsers(){
        EditText et_user_login, et_pw_login;
        String username_login, pw_login;
        et_user_login = (EditText)findViewById(R.id.ET_user_login);
        username_login = et_user_login.getText().toString();
        et_pw_login = (EditText)findViewById(R.id.ET_pw_login);
        pw_login = et_pw_login.getText().toString();

        if("boss".equals(username_login) && "boss".equals(pw_login)){
            GoBoss();
        }
        else if("emilia".equals(username_login) && "emilia".equals(pw_login)){
            GoGuest();
        }
        else{
            Toast.makeText(this, "請輸入帳號密碼或按取消", Toast.LENGTH_SHORT).show();
        }
    }

    //進入管理者選項頁面top2
    private void GoBoss(){
        Intent intent = new Intent(AccountActivity.this,Top2Activity.class);
        startActivity(intent);
        AccountActivity.this.finish(); //關閉activity
    }

    //進入一般登入或無帳號按取消，進入頁面top1
    private void GoGuest(){
        Intent intent = new Intent(AccountActivity.this,Top1Activity.class);
        startActivity(intent);
        AccountActivity.this.finish();
    }

    //使用系統的倒退鍵，會顯示退出程式的提示視窗
    @Override
    public void onBackPressed(){
        AlertDialog.Builder ad=new AlertDialog.Builder(AccountActivity.this); //建立訊息方塊
        ad.setTitle("離開Emilia Hsiao");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() { //按"是"退出程式
            public void onClick(DialogInterface dialog, int i) {
                AccountActivity.this.finish();
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        ad.show();//顯示訊息視窗
    }

}
