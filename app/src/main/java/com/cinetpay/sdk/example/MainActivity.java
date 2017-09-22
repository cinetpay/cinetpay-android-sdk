package com.cinetpay.sdk.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.sample.R;
import com.cinetpay.sdk.ui.CinetPayUI;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private String API_KEY = "api_key";
    private String SITE_ID = "site_id";

    private MerchantService CINETPAY_MERCHANT = new MerchantService(API_KEY, SITE_ID);

    private Button mPayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPayView = findViewById(R.id.pay);

        mPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CinetPayUI cinetPayUI = CinetPayUI.getInstance(MainActivity.this, CINETPAY_MERCHANT);

                // Initier un paiement
                Purchase purchase = new Purchase();
                purchase.setMetaData("custom");
                purchase.setReference(String.valueOf(new Date().getTime()));
                purchase.setDescription("Ma description");
                purchase.setTitle("Test paiement");
                purchase.setAmount(5);

                cinetPayUI.beginPayment(purchase);
            }
        });

        // Voir le compte
        //cinetPayUI.showAccountDialog();
    }
}
