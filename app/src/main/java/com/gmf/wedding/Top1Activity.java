package com.gmf.wedding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Top1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top1);

        Button bt_go_project = (Button)findViewById(R.id.BT_go_project);
        bt_go_project.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Top1Activity.this,ProjectActivity.class);
                startActivity(intent);
                Top1Activity.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Top1Activity.this,AccountActivity.class);
        startActivity(intent);
        Top1Activity.this.finish();
    }

}
