package com.example.core;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.PrintInitException;

public class PrintActivity extends AppCompatActivity {

    TextView date, request, receipt, name, number, detail, quantity, amt, bill, rate;

    public String tag = "PrintActivity";

    final int PRINT_BMP = 2;
    final int PRINT_OPEN = 8;
    final int PRINT_UNICODE = 1;
    //    private RadioGroup rg = null;
    private int BatteryV;
    private int voltage_level;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    SharedPreferences sp;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //    private Button gb_test;
//    private Button btnBmp;
    private int cycle_num = 0;
    private final static int ENABLE_RG = 10;
    private final static int DISABLE_RG = 11;

    TextView textViewMsg = null;
    int ret = -1;
    private boolean m_bThreadFinished = true;

    private int RESULT_CODE = 0;
    int IsWorking = 0;

    PosApiHelper posApiHelper = PosApiHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        date = (TextView) findViewById(R.id.date);
        request = (TextView) findViewById(R.id.request);
        receipt = (TextView) findViewById(R.id.receit);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        detail = (TextView) findViewById(R.id.details);
        quantity = (TextView) findViewById(R.id.quan);
        amt = (TextView) findViewById(R.id.amt);
        bill = (TextView) findViewById(R.id.bill);
        rate = (TextView) findViewById(R.id.rate);

        Intent i = getIntent();

        saveData("mdate", i.getStringExtra("date"));
        saveData("mrequest", i.getStringExtra("request_id"));
        saveData("mreceipt", i.getStringExtra("receipt_id"));
        saveData("mname", i.getStringExtra("customer_name"));
        saveData("mnumber", i.getStringExtra("number_group"));
        saveData("mdetail", i.getStringExtra("details"));
        saveData("mquantity", i.getStringExtra("quantity"));
        saveData("mamount", i.getStringExtra("amount"));
        saveData("mbill", i.getStringExtra("bill_due"));
        saveData("mrate", i.getStringExtra("rate"));

        date.setText(getData("mdate"));
        request.setText(getData("mrequest"));
        receipt.setText(getData("mreceipt"));
        name.setText(getData("mname"));
        number.setText(getData("mnumber"));
        detail.setText(getData("mdetail"));
        quantity.setText(getData("mquantity"));
        amt.setText(getData("mamount"));
        bill.setText(getData("mbill"));
        rate.setText(getData("mrate"));




    }

    private void setValue(int val){
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("value", val);
        editor.commit();
    }

    private void saveData(String name,String val){
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, val);
        editor.commit();
    }

    private int getValue() {
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        int value = sp.getInt("value", 2);
        return value;
    }


    private String getData(String name) {
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        String value = sp.getString(name, "");
        return value;
    }

    public void onClickBmp(View view) {
        if (printThread != null && printThread.isThreadFinished()) {
            Log.e(tag, "Thread is still running...");

          return;
        }

        printThread = new Print_Thread(PRINT_BMP);
        printThread.start();


    }

    Print_Thread printThread = null;

    public class Print_Thread extends Thread {
        int type;

        public boolean isThreadFinished() {
            return !m_bThreadFinished;
        }

        public Print_Thread(int type) {
            this.type = type;

        }

        public void run() {
            Log.d("Print_Thread[ run ]", "run() begin");
            Message msg = Message.obtain();
            Message msg1 = new Message();

            synchronized (this) {

                m_bThreadFinished = false;
                try {
                    ret = posApiHelper.PrintInit();
                } catch (PrintInitException e) {
                    e.printStackTrace();
                    int initRet = e.getExceptionCode();
                    Log.e(tag, "initRer : " + initRet);
                }

                Log.e(tag, "init code:" + ret);

                ret = getValue();
                Log.e(tag, "getValue():" + ret);

                posApiHelper.PrintSetGray(ret);


                switch (type) {

                    case PRINT_BMP:
                        SendMsg("PRINT_BMP");
                        msg.what = DISABLE_RG;
                        handler.sendMessage(msg);

                        //0 left，1 middle ，2 right
//                        Print.Lib_PrnSetAlign(0);
                        Bitmap bmp = BitmapFactory.decodeResource(PrintActivity.this.getResources(), R.mipmap.fclogo);
                        ret = posApiHelper.PrintBmp(bmp);
                        posApiHelper.PrintStr("\n");
                        posApiHelper.PrintStr("            Forestry Commission\n");
                        posApiHelper.PrintStr("          Forest Services Division\n");
                        posApiHelper.PrintStr("                 Achimota Park\n");
                        posApiHelper.PrintStr("-------------------------------------");
                        posApiHelper.PrintStr("Date: "+getData("mdate")+"\n\n");
                        posApiHelper.PrintStr("Request ID:  "+getData("mrequest")+"\n\n");
                        posApiHelper.PrintStr("Receipt No.: "+getData("mreceipt")+"\n\n");
                        posApiHelper.PrintStr("Customer Name: "+getData("mname")+"\n\n");
                        posApiHelper.PrintStr("Number in Group:  "+getData("mnumber")+"\n\n");
                        posApiHelper.PrintStr("Details     Qty      Rate    Amt\n\n");
                        posApiHelper.PrintStr(""+getData("mdetail")+"    "+getData("mquantity")+"           "+getData("mrate")+"          "+getData("mamount")+"   \n\n");
                        posApiHelper.PrintStr("                      Bill Due:   "+getData("mbill")+"\n\n");
                        posApiHelper.PrintStr("\n");

                        posApiHelper.PrintStr("-------------------------------------");
                        posApiHelper.PrintStr("      Thank you for the visit.\n");
                        posApiHelper.PrintStr(" Ticket sold are Not Returnable\n");
                        posApiHelper.PrintStr("       ICT Directorate, FC.\n\n");
                        posApiHelper.PrintStr("\n\n");
                        if (ret == 0) {
                            posApiHelper.PrintStr("\n\n\n");
                            posApiHelper.PrintStr("                                         \n");
                            posApiHelper.PrintStr("                                         \n");

                            SendMsg("Printing... ");
                            ret = posApiHelper.PrintStart();

                            msg1.what = ENABLE_RG;
                            handler.sendMessage(msg1);

                            Log.d("", "Lib_PrnStart ret = " + ret);
                            if (ret != 0) {
                                RESULT_CODE = -1;
                                Log.e("Lazer", "Lib_PrnStart fail bmp, ret = " + ret);
                                if (ret == -1) {
                                    SendMsg("No Print Paper ");
                                } else if(ret == -2) {
                                    SendMsg("too hot ");
                                }else if(ret == -3) {
                                    SendMsg("low voltage ");
                                }else{
                                    SendMsg("Print fail ");
                                }
                            } else {
                                RESULT_CODE = 0;
                                SendMsg("Print Finish ");
                            }
                        } else {
                            RESULT_CODE = -1;
                            SendMsg("Lib_PrnBmp Failed");
                        }
                        break;

                    default:
                        break;
                }
                m_bThreadFinished = true;

                Log.e(tag, "goToSleep2...");
            }
        }
    }


    public void SendMsg(String strInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("MSG", strInfo);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DISABLE_RG:
                    IsWorking = 1;
//                    rb_high.setEnabled(false);
//                    rb_middle.setEnabled(false);
//                    rb_low.setEnabled(false);
//                    radioButton_4.setEnabled(false);
//                    radioButton_5.setEnabled(false);
                    break;

                case ENABLE_RG:
                    IsWorking = 0;
//                    rb_high.setEnabled(true);
//                    rb_middle.setEnabled(true);
//                    rb_low.setEnabled(true);
//                    radioButton_4.setEnabled(true);
//                    radioButton_5.setEnabled(true);

                    break;
                default:
                    Bundle b = msg.getData();
                    String strInfo = b.getString("MSG");
//                    textViewMsg.setText(strInfo);

                    break;
            }
        }
    };

    public class BatteryReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            voltage_level = intent.getExtras().getInt("level");// ��õ�ǰ����
            Log.e("wbw", "current  = " + voltage_level);
            BatteryV = intent.getIntExtra("voltage", 0);  //电池电压
            Log.e("wbw", "BatteryV  = " + BatteryV);
            Log.e("wbw", "V  = " + BatteryV * 2 / 100);
            //	m_voltage = (int) (65+19*voltage_level/100); //放大十倍
            //   Log.e("wbw","m_voltage  = " + m_voltage );
        }
    }


}




