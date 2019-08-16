package com.example.serviskontrol;

import android.app.AlertDialog;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;

import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import java.util.UUID;

import java.io.InputStream;


public  class ServisActivity extends AppCompatActivity {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    String address = null;
    boolean kontrolOtur=false;
    boolean kontrolKemer=false;
    String otur,kemer;
    Button btBt;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    ImageView img;
    Handler h;
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private StringBuilder sb = new StringBuilder();
    TextView txtArduino;
    Context context =this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servis);
        img= findViewById(R.id.imageView4);

        Intent newint = getIntent();
        address = newint.getStringExtra(BtActivity.EXTRA_ADDRESS);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        new ConnectBT().execute();

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear

                            if ( Integer.parseInt(sbprint)%10==1 )
                            {
                                otur=sbprint;
                                kontrolOtur=true;
                                otur(sbprint);


                            }else if(Integer.parseInt(sbprint)%10==2){
                                kemer=sbprint;
                                kontrolKemer=true;
                                kemer(sbprint);
                            }

                            // update TextView
                        }
                        Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");

                        break;



                }
            }



            private void kemer(String sbprint) {

                if(kontrolOtur==true && Integer.parseInt(sbprint)<500) {
                    Drawable myDrawable1 = getResources().getDrawable(R.drawable.guvenli);
                    img.setImageDrawable(myDrawable1);

                }
                else if(kontrolOtur==true && Integer.parseInt(sbprint)>500){
                    Drawable myDrawable = getResources().getDrawable(R.drawable.dolu2);
                    img.setImageDrawable(myDrawable);

                }
            }

            private void otur(String sbprint) {

                if( Integer.parseInt(sbprint)>500 && kontrolKemer==false){
                    Drawable myDrawable2 = getResources().getDrawable(R.drawable.dolu2);
                    img.setImageDrawable(myDrawable2);
                    kontrolOtur=true;
                }
                else if(Integer.parseInt(sbprint)<500){
                    kontrolOtur=false;
                    Drawable myDrawable3 = getResources().getDrawable(R.drawable.bos1);
                    img.setImageDrawable(myDrawable3);
                }
            }

            ;
        };

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {//Blutooth Bağlantı Kontrolu yapılıyor yapılıyor gerekli uyarılar kullanıcıya yapılıyor
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice address = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if(Integer.parseInt(otur)>500) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // alert dialog başlığını tanımlıyoruz.
                    alertDialogBuilder.setTitle("ARAÇTA ÖĞRENCİ BULUNMAKTADIR");

                    // alert dialog özelliklerini oluşturuyoruz.
                    alertDialogBuilder
                            .setMessage(" ARAÇ KİŞİ KONTROLÜ GERÇEKLEŞTİRİLDİ Mİ? !")
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher_round)
                            // Evet butonuna tıklanınca yapılacak işlemleri buraya yazıyoruz.
                            .setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            // İptal butonuna tıklanınca yapılacak işlemleri buraya yazıyoruz.
                            .setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    // alert dialog nesnesini oluşturuyoruz
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // alerti gösteriyoruz
                    alertDialog.show();
                    //Toast.makeText(getApplicationContext(), otur, Toast.LENGTH_LONG).show();

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // alert dialog başlığını tanımlıyoruz.
                    alertDialogBuilder.setTitle("DİKKAT");

                    // alert dialog özelliklerini oluşturuyoruz.
                    alertDialogBuilder
                            .setMessage(" BLUETOOTH BAĞLANTINIZ KOPMUŞTUR LÜTFEN TEKRAR BAĞLANMAYI DENEYİN")
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher_round)
                            // Evet butonuna tıklanınca yapılacak işlemleri buraya yazıyoruz.
                            .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            // İptal butonuna tıklanınca yapılacak işlemleri buraya yazıyoruz.
                            .setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    // alert dialog nesnesini oluşturuyoruz
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // alerti gösteriyoruz
                    alertDialog.show();

                }
            }
        }
    };





    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;


        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;


            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();


            } catch (IOException e) {
            }

            mmInStream = tmpIn;

        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream

                    bytes = mmInStream.read(buffer);// Get number of bytes and message in "buffer"

                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler


                } catch (IOException e) {
                    Log.d(TAG, "Giriş akışı bağlantısı kesildi", e);
                    break;
                }
            }
        }


    }



    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }






    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;


        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ServisActivity.this, "Bağlanıyor...", "Lütfen Bekleyin!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();

                }

            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("\n" +
                        "Bağlantı Başarısız!!. Tekrar deneyin.");
                finish();
            } else {
                msg("Bağlandı");
                isBtConnected = true;
            }



            progress.dismiss();
        }

    }



}
