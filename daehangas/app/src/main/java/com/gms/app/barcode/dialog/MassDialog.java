package com.gms.app.barcode.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gms.app.barcode.MassActivity;
import com.gms.app.barcode.R;
import com.gms.app.barcode.adapter.MassAdapter;
import com.gms.app.barcode.adapter.MassData;
import com.gms.app.barcode.domain.SimpleBottleVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MassDialog {

    private Context context;
    private static ArrayList<MassData> arrayList;
    private static MassAdapter massAdapter;
    MassData massData;

    SharedPreferences sharedPreferences ;
    private String shared = "file";
    boolean isUpdate = true;
    private Spinner spinner, spinner1;

    List<SimpleBottleVO> productList = new ArrayList<SimpleBottleVO>();
    List<String> spinnerArray =  new ArrayList<String>();
    List<String> bottleIdArray =  new ArrayList<String>();
    List<String> arrayProductList = new ArrayList<String>();
    List<String> productPriceSeqArray =  new ArrayList<String>();

    String productType = "";
    String buttonType = "";
    String host ="";

    String productNm= "";
    String prdouctCapa = "";
    String bottleId = "";

    String itemB[] = null;

    public MassDialog(Context context, String bType) {
        this.context = context;
        this.buttonType = bType;

        sharedPreferences = context.getSharedPreferences(shared, 0);
        host = context.getString(R.string.host_name);

        new HttpAsyncTask2().execute(host + context.getString(R.string.api_getDummyBottle));
    }

    // ????????? ??????????????? ????????? ????????????.
    public void callFunction( ArrayList<MassData> arrayList1, MassAdapter massAdapter1 ){
        arrayList = arrayList1;
        massAdapter = massAdapter1;

        // ????????? ?????????????????? ?????????????????? Dialog???????????? ????????????.
        final Dialog dlg = new Dialog(context);

        // ??????????????? ??????????????? ?????????.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ????????? ?????????????????? ??????????????? ????????????.
        dlg.setContentView(R.layout.mass_dialog);

        // ????????? ?????????????????? ????????????.
        dlg.show();

        // ????????? ?????????????????? ??? ???????????? ????????????.
        final TextView title = (TextView) dlg.findViewById(R.id.title);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        final EditText et_productCount = (EditText) dlg.findViewById(R.id.et_productCount);

        title.setText("????????????");
        // Add Data to listView

        spinner = (Spinner)dlg.findViewById(R.id.spinner);
        spinner1 = (Spinner)dlg.findViewById(R.id.spinner1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productType = parent.getItemAtPosition(position).toString();
                productNm = parent.getItemAtPosition(position).toString();
                String pValue= "";
                String bValue= "";
                for(int i = 0 ; i < arrayProductList.size() ; i++){
//                    Log.d("MassDialog","size="+arrayProductList.get(i)+" position="+position+"---"+arrayProductList.get(position));
                    if(i==position){
                        pValue = arrayProductList.get(position);
                        bValue = bottleIdArray.get(position);
                    }
                }
                String item[] = pValue.split(";");
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, item);

                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter1);
                itemB = bValue.split(";");
                et_productCount.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prdouctCapa = parent.getItemAtPosition(position).toString();
                if(itemB!=null && itemB[position] !=null)
                    bottleId = itemB[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String iProductCount = "";
                //Log.d("MassDialog  ","bottleId--"+bottleId);
                if(et_productCount.getText().toString().equals("") || et_productCount.getText().toString() == null){
                    Toast.makeText(context, "????????? ???????????????", Toast.LENGTH_SHORT).show();
                }else {
                    iProductCount = et_productCount.getText().toString();

                    boolean updateFlag = true;
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getTv_mBottleId().equals(bottleId))
                            updateFlag = false;
                    }

                    if (updateFlag) {
                        massData = new MassData(productNm, bottleId, prdouctCapa, iProductCount);
                        //tv_result.setText(bottleBarCd+" "+s);
                        arrayList.add(massData);
                        massAdapter.notifyDataSetChanged();
                        et_productCount.setText("");
                        MassActivity.setTextBottleCount();
                    } else {
                        Toast.makeText(context, "????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                    // ????????? ??????????????????.????????????.
                    dlg.dismiss();
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "?????? ????????????.", Toast.LENGTH_SHORT).show();

                // ????????? ?????????????????? ????????????.
                dlg.dismiss();
            }
        });
    }



    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {

        private final String TAG = HttpAsyncTask1.class.getSimpleName();
        // int REQUEST_CODE =
        // OkHttp ???????????????
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            //List<CustomerSimpleVO> customerList = new ArrayList<>();
            String strUrl = params[0];
            String result= "";
            try {
                // ??????
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                // ??????
                Response response = client.newCall(request).execute();
                result = response.body().string();
                //Log.d(TAG, "response.body().string(): " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    // ???????????? ????????????
    private class HttpAsyncTask2 extends AsyncTask<String, Void, List<SimpleBottleVO>> {
        private final String TAG = HttpAsyncTask2.class.getSimpleName();
        // int REQUEST_CODE =
        // OkHttp ???????????????
        OkHttpClient client = new OkHttpClient();

        @Override
        protected List<SimpleBottleVO> doInBackground(String... params) {

            String strUrl = params[0];
            try {
                // ??????
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                // ??????
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();

                // import java.lang.reflect.Type
                Type listType = new TypeToken<ArrayList<SimpleBottleVO>>() {
                }.getType();
                productList = gson.fromJson(response.body().string(), listType);

                //Log.d(TAG, "onCreate: " + productList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return productList;
        }

        @Override
        protected void onPostExecute(List<SimpleBottleVO> productList) {
            super.onPostExecute(productList);

            StringBuffer sb = new StringBuffer();
            StringBuffer sb1 = new StringBuffer();

            for (int i = 0; i < productList.size(); i++) {
//                Log.d("MassDialog  ","productList.get(i).getProductNm()="+productList.get(i).getProductNm()+"=="+productList.get(i).getBottleCapa());
                if(i==0) {
                    spinnerArray.add(productList.get(i).getProductNm());

                    sb.append(productList.get(i).getBottleCapa()+";");
                    sb1.append(productList.get(i).getBottleId()+";");
                }

                if(i > 0 && productList.get(i).getProductId() > productList.get(i-1).getProductId()) {
                    spinnerArray.add(productList.get(i).getProductNm());
//                    Log.d("MassDialog  ","sb.toString()="+sb.toString());
                    arrayProductList.add(sb.toString());
                    bottleIdArray.add(sb1.toString());
                    sb = new StringBuffer();
                    sb1 = new StringBuffer();
                    sb.append(productList.get(i).getBottleCapa()+";");
                    sb1.append(productList.get(i).getBottleId()+";");

                }else if (i > 0 && i < productList.size()-1 && productList.get(i).getProductId() == productList.get(i-1).getProductId()) {
                    sb.append(productList.get(i).getBottleCapa()+";");
                    sb1.append(productList.get(i).getBottleId()+";");
                }
                if(i==productList.size()-1) {
                    sb.append(productList.get(i).getBottleCapa()+";");
                    sb1.append(productList.get(i).getBottleId()+";");

                    arrayProductList.add(sb.toString());
                    bottleIdArray.add(sb1.toString());
                }
            }

//            for(int i =0 ; i < arrayProductList.size() ; i++){
//                Log.d("MassDialog  ","arrayProductList.get(i="+i+" == "+arrayProductList.get(i));
////                Log.d("MassDialog  ","bottleIdArray.get(i="+i+" -=" bottleIdArray.get(i));
//            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Spinner sItems = (Spinner) findViewById(R.id.spinner1);
            spinner.setAdapter(adapter);
        }
    }

}
