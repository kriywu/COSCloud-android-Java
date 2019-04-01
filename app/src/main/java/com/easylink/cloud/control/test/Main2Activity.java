package com.easylink.cloud.control.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.easylink.cloud.R;
import com.easylink.cloud.control.MainActivity;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.web.Client;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import androidx.appcompat.app.AppCompatActivity;

import static com.easylink.cloud.modle.Constant.secretKey;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button = findViewById(R.id.btn);
        Client.getClient();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
