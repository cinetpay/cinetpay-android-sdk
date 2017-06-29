package com.cinetpay.sdk.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.sample.R;
import com.cinetpay.sdk.ui.CinetPayUI;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private String API_KEY = "39955468c7a8c0cef1.68322505";
    private String SITE_ID = "709651";

    private MerchantService CINETPAY_MERCHANT = new MerchantService(API_KEY, SITE_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CinetPayUI cinetPayUI = CinetPayUI.getInstance(this, CINETPAY_MERCHANT);

        // Initier un paiement
        Purchase purchase= new Purchase();
        purchase.setMetaData("custom");
        purchase.setReference("REF001");
        purchase.setDescription("Ma description");
        purchase.setTitle("Le titre du don");
        purchase.setAmount(200);

        cinetPayUI.beginPayment(purchase);

        // Consulter le solde disponible
        //cinetPayUI.showAccountDialog();
    }
}
