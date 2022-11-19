package com.gms.app.ewhabarcode;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gms.app.ewhabarcode.domain.CustomerSimpleVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MassActionDialog {
    private Context context;
    String[] items;
    ArrayList<String> listItems;
    ArrayList<String> listItemsTemp;
    ArrayAdapter<String> adapter;
    ListView listView ;

    SharedPreferences sharedPreferences ;
    private String shared = "file";
    String bottleType = "";
    String buttonType = "";
    String bottles;
    String customerId="";
    String userId = "";
    String host ="";
    String strBottleType="공병";
    String value ="" ;
    String value1 ="";
    boolean isUpdate = true;

    public MassActionDialog(Context context, String bType) {
        this.context = context;
        this.buttonType = bType;
        sharedPreferences = context.getSharedPreferences(shared, 0);
        host = context.getString(R.string.host_name);

        value = sharedPreferences.getString("clist", "");

        if(value ==null || value.length() <= 10)
            new HttpAsyncTask().execute(host + "api/customerAllList.do");

    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String bottle , String id ){

        bottles= bottle;
        userId = id;

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.charge_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView title = (TextView) dlg.findViewById(R.id.title);
        final EditText message = (EditText) dlg.findViewById(R.id.mesgase1);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        final RadioGroup rg_gender = dlg.findViewById(R.id.rg_gender);
        final RadioButton rb_man = dlg.findViewById(R.id.rb_man);
        final RadioButton rb_woman = dlg.findViewById(R.id.rb_woman);

        title.setText(buttonType);
        listView = (ListView) dlg.findViewById(R.id.listview1);

        items = value.split("#");
        //Log.d("CustomDialog ","items.length=="+items.length);
        listItems = new ArrayList<>(Arrays.asList(items));
        listItemsTemp  = new ArrayList<>(Arrays.asList(items));

        adapter = new ArrayAdapter(context, R.layout.item_customer, R.id.tv_customer, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, "click item", Toast.LENGTH_SHORT).show();
                String text = (String)parent.getAdapter().getItem(position);
                message.setText(text);
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_man) {
                    Toast.makeText(context,"실병을 선택했습니다", Toast.LENGTH_SHORT).show();
                    bottleType = "F";
                    strBottleType = "실병";
                }else if(checkedId == R.id.rb_woman){
                    Toast.makeText(context,"공병을 선택했습니다", Toast.LENGTH_SHORT).show();
                    bottleType = "E";
                }
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                //main_label.setText(message.getText().toString()+"--"+str_result);
                customerId = message.getText().toString();

                Toast.makeText(context, String.format("\"%s으로 %s\" 처리를 하였습니다.", strBottleType, buttonType), Toast.LENGTH_SHORT).show();

                //작업한 용기목록저장
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("previousProduct",bottles);
                editor.commit();

                // 서버 전송
                new HttpAsyncTask1().execute(host+context.getString(R.string.api_controlMassAction)+ "userId="+userId+"&bottles="+bottles+"&customerNm="+ URLEncoder.encode(customerId)+"&bottleType="+bottleType+"&bottleWorkCd="+buttonType);

                //MainActivity List 제거
                MassActivity.clearArrayList();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

    // 검색을 수행하는 메소드
    public void filter(String str) {

        listItems.clear();
        Iterator it = this.listItemsTemp.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str2.toString().toLowerCase(Locale.getDefault()).contains(str)) {
                //this.mAccountList.add(str2);
                listItems.add(str2);
            }
        }

        this.adapter.notifyDataSetChanged();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, List<CustomerSimpleVO>> {
        private final String TAG = HttpAsyncTask.class.getSimpleName();
        // int REQUEST_CODE =
        // OkHttp 클라이언트
        OkHttpClient client = new OkHttpClient();

        @Override
        protected List<CustomerSimpleVO> doInBackground(String... params) {
            List<CustomerSimpleVO> customerList = new ArrayList<>();
            String strUrl = params[0];
            try {
                // 요청
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                // 응답
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();

                // import java.lang.reflect.Type
                Type listType = new TypeToken<ArrayList<CustomerSimpleVO>>() {
                }.getType();
                customerList = gson.fromJson(response.body().string(), listType);

                //Log.d(TAG, "onCreate: " + customerList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return customerList;
        }

        @Override
        protected void onPostExecute(List<CustomerSimpleVO> customerList) {
            super.onPostExecute(customerList);

            StringBuffer sb = new StringBuffer();
            items = new String[customerList.size()];
            for (int i = 0; i < customerList.size(); i++) {
                items[i] = customerList.get(i).getCustomerNm().toString();
                sb.append(customerList.get(i).getCustomerNm().toString());
                sb.append("#");
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            int cCount = sharedPreferences.getInt("clistCount", 0);

            if(cCount > 0 || cCount == customerList.size()) isUpdate = false;
            else isUpdate = true;
            //String value = id.getText().toString();
            editor.putString("clist", sb.toString());
            editor.putInt("clistCount",customerList.size());
            editor.commit();

            if(isUpdate) {
                //Log.d("isUpdate ture", "ture ");
                listItems = new ArrayList<>(Arrays.asList(items));
                adapter = new ArrayAdapter(context, R.layout.item_customer, R.id.tv_customer, listItems);
                listView.setAdapter(adapter);
            }
        }
    }
    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {

        private final String TAG = HttpAsyncTask.class.getSimpleName();
        // int REQUEST_CODE =
        // OkHttp 클라이언트
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            //List<CustomerSimpleVO> customerList = new ArrayList<>();
            String strUrl = params[0];
            String result= "";
            try {
                // 요청
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                // 응답
                Response response = client.newCall(request).execute();
                result = response.body().string();

                if(result.equals("fail")){
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            Toast.makeText(MassActionDialog.this.context, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }else if(result.equals("noUser")){
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            Toast.makeText(MassActionDialog.this.context, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }
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
}
