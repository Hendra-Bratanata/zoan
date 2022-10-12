package com.zoan;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;



public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    int hasilCode = 100;
    String imei;
    static String MQTTHOST = "tcp://zoan.online:1883";
    static String TOPIC = "###########";
    int qos = 1;
    SwitchCompat aSwitch1;
    SwitchCompat aSwitch2;
    TextView tvNitrogen;
    TextView tvPhosphor;
    TextView tvKalium;
    TextView tvPh;
    TextView tvSoilHumi;
    TextView tvSoilTemp;
    TextView tvAirHumi;
    TextView tvAirTemp;
    TextView tvLux;
    TextView tvID;
    TextView tvTime;
    TextView tvStatus;


    ImageButton btnVoice;

    MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        tvNitrogen = findViewById(R.id.tv_nitrogen);
        tvPhosphor = findViewById(R.id.tv_phosphor);
        tvKalium = findViewById(R.id.tv_kalium);
        tvPh = findViewById(R.id.tv_ph);
        tvAirHumi = findViewById(R.id.tv_airHumi);
        tvAirTemp = findViewById(R.id.tv_airTemp);
        tvSoilHumi = findViewById(R.id.tv_soilHumi);
        tvSoilTemp = findViewById(R.id.tv_soilTemp);
        tvLux = findViewById(R.id.tv_lux);
        tvID = findViewById(R.id.tvId);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);

        btnVoice = findViewById(R.id.btn_voice);
        aSwitch1 = findViewById(R.id.switch1);
        aSwitch2 = findViewById(R.id.switch2);
        imei = getIntent().getStringExtra("imei");
        Log.d("TAG", "onCreate: " + imei);

        TOPIC = imei;
        tvID.setText(String.format(": %s", TOPIC));
        tvStatus.setText(": Offline");
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
        if (aSwitch1 != null) {
            aSwitch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    publish("ssr1on");
                } else {
                    publish("ssr1off");
                }
            });
        }
        if (aSwitch2 != null) {
            aSwitch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    publish("ssr2on");
                } else {
                    publish("ssr2off");
                }
            });
        }
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voice();
            }
        });

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
                clientId);
        options = new MqttConnectOptions();
//        options.setConnectionTimeout(240000);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);


        connectMqtt();

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("TAG", "connectionLost: " + cause);
                Log.d("TAG", "connectionLost: eroor putus jaringan ");
                tvStatus.setText(": Offline");
                tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                connectMqtt();

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                DecimalFormat format = new DecimalFormat("#.##");
                DecimalFormat formatsingel = new DecimalFormat("#");

                String dataHumi = message.toString();
                String[] dataSub = dataHumi.split("/");
                Log.d("TAG", "messageArrived: " + message);
                if (dataSub.length > 2) {
                    String currentDateandTime = sdf.format(new Date());
                    tvStatus.setText("Online");
                    tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    tvTime.setText(String.format(": %s", currentDateandTime));

                    Log.d("TAG", "messageArrived: " + currentDateandTime);


                    tvNitrogen.setText(dataSub[0]);
                    tvPhosphor.setText(dataSub[1]);
                    tvKalium.setText(dataSub[2]);

                    tvSoilTemp.setText(dataSub[4]);
//                tvSoilHumi.setText(dataSub[9]);

                    double airhumi_meter = Double.parseDouble(dataSub[6]);
                    double airtemp_meter = Double.parseDouble(dataSub[7]);
                    double lux_meter = Double.parseDouble(dataSub[8]);
                    tvAirHumi.setText(formatsingel.format(airhumi_meter));
                    tvAirTemp.setText(formatsingel.format(airtemp_meter));
                    tvLux.setText(formatsingel.format(lux_meter));

                    double ph_meter = Double.parseDouble(dataSub[3]);
                    double ph = 7.0;
//                    Log.d("TAG", String.valueOf(ph_meter));

//                    if (ph_meter < 250) {
//                        ph = 3.0;
//                    }
//                    if (ph_meter > 250 && ph_meter <= 300) {
//                        ph = 3.5;
//                    }
//                    if (ph_meter > 300 && ph_meter <= 350) {
//                        ph = 4.0;
//                    }
//                    if (ph_meter > 350 && ph_meter <= 380) {
//                        ph = 4.5;
//                    }
//                    if (ph_meter > 380 && ph_meter <= 420) {
//                        ph = 5.0;
//                    }
//                    if (ph_meter > 420 && ph_meter <= 450) {
//                        ph = 5.5;
//                    }
//                    if (ph_meter > 450 && ph_meter <= 480) {
//                        ph = 6.0;
//                    }
//                    if (ph_meter > 480 && ph_meter <= 495) {
//                        ph = 6.5;
//                    }
//                    if (ph_meter > 495 && ph_meter <= 520) {
//                        ph = 7.0;
//                    }
//                    if (ph_meter > 520 && ph_meter <= 540) {
//                        ph = 7.5;
//                    }
//                    if (ph_meter > 540 && ph_meter <= 560) {
//                        ph = 8.0;
//                    }
//                    if (ph_meter > 560 && ph_meter <= 580) {
//                        ph = 8.5;
//                    }
//                    if (ph_meter > 580 && ph_meter <= 600) {
//                        ph = 9.0;
//                    }

                    //Soil Humidity
                    double SoilHumi = Double.parseDouble(dataSub[9]);
                    double countSoilHumi = (SoilHumi / 250.0) * 100.0;


                    String status = "";
                    if (SoilHumi < 50) {
                        status = "Sangat Kering";
                    }
                    if (SoilHumi >= 50 && SoilHumi < 100) {
                        status = "Kering";
                    }
                    if (SoilHumi >= 100 && SoilHumi <= 150) {
                        status = "Normal";
                    }
                    if (SoilHumi > 150 && SoilHumi <= 180) {
                        status = "Basah";
                    }
                    if (SoilHumi > 180) {
                        status = "Sangat Basah";
                    }
                    //set TextView Ph
                    tvPh.setText(String.valueOf(ph_meter));
                    //Set TextView Soil Hhhumidity
//                    tvSoilHumi.setText(format.format(countSoilHumi));
                    tvSoilHumi.setText(formatsingel.format(SoilHumi));
                }
                if (dataSub[0].equalsIgnoreCase("ssr1off")) {
                    aSwitch1.setChecked(false);
                }
                if (dataSub[0].equalsIgnoreCase("ssr1on")) {
                    aSwitch1.setChecked(true);
                }
                if (dataSub[0].equalsIgnoreCase("ssr2off")) {
                    aSwitch2.setChecked(false);
                }
                if (dataSub[0].equalsIgnoreCase("ssr2on")) {
                    aSwitch2.setChecked(true);
                }
                if (dataSub[0].equalsIgnoreCase("ssrallon")) {
                    aSwitch2.setChecked(true);
                    aSwitch1.setChecked(true);
                }
                if (dataSub[0].equalsIgnoreCase("ssralloff")) {
                    aSwitch1.setChecked(false);
                    aSwitch2.setChecked(false);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

        });


//            getSensor();
    }

    private void connectMqtt() {
        try {
            IMqttToken token = client.connect(options);
//            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribe();
//
                    Log.d("TAG", "onSuccess mqtt");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TAG", "onFailure mqtt");
                    connectMqtt();

                }
            });
        } catch (Exception e) {
            Log.d("TAG", "onCreate: Jaringan Kurang Stabil ");
            e.printStackTrace();
        }
    }

    private void getSensor() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://zoan.online/getData.php?api_key=50bfbf83-76db-4cc8-9cc9-eaeb6d5a99b4";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String hasil = new String(bytes);
                Log.d("TAG", "onSuccess: " + hasil);
                try {
                    JSONObject jsonObject = new JSONObject(hasil);
                    JSONArray array = jsonObject.getJSONArray("data");
                    String ID = array.getJSONObject(0).getString("ID");
                    String Nitrogen = array.getJSONObject(0).getString("Nitrogen");
                    Log.d("TAG", "onSuccess: " + Nitrogen);
                    Log.d("TAG", "onSuccess: " + ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void voice() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sebutkan perintah");
        }
        try {
            startActivityForResult(intent, hasilCode);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, a.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> suara = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                suara = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            }
//            Toast.makeText(this, suara.get(0), Toast.LENGTH_SHORT).show();
            // isi semua peritah dengan if pada baris dibawah ini untuk exekusi publish
            String Voice = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                Voice = suara.get(0).toLowerCase(Locale.ROOT);
            }
            if (Voice.equalsIgnoreCase("nyalakan relay 1") || Voice.equalsIgnoreCase("nyalakan relay satu")) {
                publish("ssr1on");
            } else if (Voice.equalsIgnoreCase("matikan relay 1") || Voice.equalsIgnoreCase("Matikan relay satu")) {
                publish("ssr1off");
            } else if (Voice.equalsIgnoreCase("nyalakan relay 2") || Voice.equalsIgnoreCase("nyalakan relay dua")) {
                publish("ssr2on");
            } else if (Voice.equalsIgnoreCase("matikan relay 2") || Voice.equalsIgnoreCase("matikan relay dua")) {
                publish("ssr2off");
            } else if (Voice.equalsIgnoreCase("nyalakan pompa")) {
                publish("ssr1on");
            } else if (Voice.equalsIgnoreCase("matikan pompa")) {
                publish("ssr1off");
            } else if (Voice.equalsIgnoreCase("nyalakan pompa 1") || Voice.equalsIgnoreCase("nyalakan pompa satu")) {
                publish("ssr1on");
            } else if (Voice.equalsIgnoreCase("matikan pompa 1") || Voice.equalsIgnoreCase("matikan pompa satu")) {
                publish("ssr1off");
            } else if (Voice.equalsIgnoreCase("nyalakan pompa 2") || Voice.equalsIgnoreCase("nyalakan pompa dua")) {
                publish("ssr2on");
            } else if (Voice.equalsIgnoreCase("matikan pompa 2") || Voice.equalsIgnoreCase("matikan pompa dua")) {
                publish("ssr2off");
            } else if (Voice.equalsIgnoreCase("nyalakan kipas")) {
                publish("ssr2on");
            } else if (Voice.equalsIgnoreCase("matikan kipas")) {
                publish("ssr2off");
            } else if (Voice.equalsIgnoreCase("nyalakan kipas 1") || Voice.equalsIgnoreCase("nyalakan kipas satu")) {
                publish("ssr1on");
            } else if (Voice.equalsIgnoreCase("matikan kipas 1") || Voice.equalsIgnoreCase("matikan kipas satu")) {
                publish("ssr1off");
            } else if (Voice.equalsIgnoreCase("nyalakan kipas 2") || Voice.equalsIgnoreCase("nyalakan kipas dua")) {
                publish("ssr2on");
            } else if (Voice.equalsIgnoreCase("matikan kipas 2") || Voice.equalsIgnoreCase("matikan kipas dua")) {
                publish("ssr2off");
            } else if (Voice.equalsIgnoreCase("nyalakan semuanya")) {
                publish("ssrallon");
            } else if (Voice.equalsIgnoreCase("matikan semuanya")) {
                publish("ssralloff");
            }
        }
    }

    public void subscribe() {
        try {
            client.subscribe(TOPIC, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String data) {
        String topic = TOPIC;
        String payload = data;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, QR_activity.class);
        startActivity(intent);
        this.finish();
    }
}


