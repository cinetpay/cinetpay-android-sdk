package com.cinetpay.sdk.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.sample.R;
import com.cinetpay.sdk.ui.CinetPayUI;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String API_KEY = "api_key";
    private String SITE_ID = "site_id";

    private MerchantService CINETPAY_MERCHANT = new MerchantService(API_KEY, SITE_ID);
    private MerchantService CINETPAY_MERCHANT_WITH_URL_NOTIFICATION =
            new MerchantService(API_KEY, SITE_ID, "https://www.url_notification.com/index.php", true);

    private EditText mAmountView;
    private Button mPayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAmountView = findViewById(R.id.amount);
        mPayView = findViewById(R.id.pay);

        mPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = mAmountView.getText().toString();

                if (amount.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Veuillez entrer un montant SVP",
                            Toast.LENGTH_LONG).show();
                } else {
                    CinetPayUI cinetPayUI = CinetPayUI.getInstance(MainActivity.this, CINETPAY_MERCHANT);

                    Purchase purchase = new Purchase();
                    purchase.setMetaData("custom");
                    purchase.setReference(String.valueOf(new Date().getTime()));
                    purchase.setDescription("Ma description");
                    purchase.setTitle("Test paiement");
                    purchase.setAmount(Integer.valueOf(amount));

                    cinetPayUI.beginPayment(purchase, new CinetPay.PayCallBack() {
                        @Override
                        public void onPurchaseComplete(PaymentResponse response, Purchase purchase) {
                            if (response.getCode().equals("00") && response.hasBeenAccepted()
                                    && response.hasBeenConfirmed()) {
                                // Paiement effectué avec succès
                            }
                        }
                    });
                }
            }
        });
    }
}
