package com.gms.app.daehanfactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gms.app.daehanfactory.dialog.StockReportDialog;
import com.gms.app.daehanfactory.dialog.StockReportOwnerDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private Context context;
    private String bottles;
    private String shared = "file";
    private String userId = "";

    private static ArrayList<MainData> arrayList;

    private Button btn_charge, btn_hole, btn_vacuum, btn_chargedt,
        btn_deleteAll,btn_report,btn_bottleTrans,btn_incar,
        btn_reportRent,btn_reportStock,btn_reportAgency, btn_version;


    public BottomSheetDialog(Context context) {
        this.context = context;
    }

    public BottomSheetDialog(Context context, String bottles) {
        this.context = context;
        this.bottles = bottles;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        //SharedPreferences 로그인 정보 유무 확인
        final SharedPreferences sharedPreferences = context.getSharedPreferences(shared,0);
        userId = sharedPreferences.getString("id", "");

        btn_charge = v.findViewById(R.id.btn_charge);
        btn_chargedt  = v.findViewById(R.id.btn_chargedt);
        btn_vacuum  = v.findViewById(R.id.btn_money);
        btn_hole  = v.findViewById(R.id.btn_hole);

        btn_deleteAll  = v.findViewById(R.id.btn_deleteAll);
        btn_report = v.findViewById(R.id.btn_report);
        btn_bottleTrans = v.findViewById(R.id.btn_bottleTrans);           // 용기차용
        btn_incar = v.findViewById(R.id.btn_ln2);             // 상차

        btn_reportRent = v.findViewById(R.id.btn_dummy);          // 대여현황
        btn_reportStock = v.findViewById(R.id.btn_reportStock);       // 재고현황
        btn_reportAgency = v.findViewById(R.id.btn_reportAgency);       // 대리점현황
        btn_version  = v.findViewById(R.id.btn_version);

        arrayList = MainActivity.getArrayList();

        btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size() <= 0){
                    Toast.makeText(context, "용기를 선택하세요", Toast.LENGTH_SHORT).show();
                }else {
                    ChargeDialog customDialog = new ChargeDialog(context, btn_charge.getText().toString());

                    String tempStr = "";
                    for (int i = 0; i < arrayList.size(); i++) {
                        tempStr += arrayList.get(i).getTv_bottleBarCd() + ",";
                    }

                    // 커스텀 다이얼로그를 호출한다.
                    customDialog.callFunction(tempStr, userId);
                    dismiss();
                }
            }
        });

        btn_chargedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size() <= 0){
                    Toast.makeText(context, "용기를 선택하세요", Toast.LENGTH_SHORT).show();
                }else {
                    ChargeDialog customDialog = new ChargeDialog(context, btn_chargedt.getText().toString());

                    String tempStr = "";
                    for (int i = 0; i < arrayList.size(); i++) {
                        tempStr += arrayList.get(i).getTv_bottleBarCd() + ",";
                    }
                    //Toast.makeText(MainActivity.this, tempStr, Toast.LENGTH_SHORT).show();

                    // 커스텀 다이얼로그를 호출한다.
                    customDialog.callFunction(tempStr, userId);
                    dismiss();
                }
            }
        });

        btn_hole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size() <= 0){
                    Toast.makeText(context, "용기를 선택하세요", Toast.LENGTH_SHORT).show();
                }else {
                    ChargeDialog customDialog = new ChargeDialog(context, btn_hole.getText().toString());

                    String tempStr = "";
                    for (int i = 0; i < arrayList.size(); i++) {
                        tempStr += arrayList.get(i).getTv_bottleBarCd() + ",";
                    }
                    //Toast.makeText(MainActivity.this, tempStr, Toast.LENGTH_SHORT).show();

                    // 커스텀 다이얼로그를 호출한다.
                    customDialog.callFunction(tempStr, userId);
                    dismiss();
                }
            }
        });

        btn_vacuum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size() <= 0){
                    Toast.makeText(context, "용기를 선택하세요", Toast.LENGTH_SHORT).show();
                }else {
                    ChargeDialog customDialog = new ChargeDialog(context, btn_vacuum.getText().toString());

                    String tempStr = "";
                    for (int i = 0; i < arrayList.size(); i++) {
                        tempStr += arrayList.get(i).getTv_bottleBarCd() + ",";
                    }
                    //Toast.makeText(MainActivity.this, tempStr, Toast.LENGTH_SHORT).show();

                    // 커스텀 다이얼로그를 호출한다.
                    customDialog.callFunction(tempStr, userId);
                    dismiss();
                }
            }
        });

        btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayList.size() > 0 ){

                    AlertDialog.Builder ad = new AlertDialog.Builder(context);
                    ad.setMessage("리스트를 삭제하시겠습니까?");

                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(context, "리스트를 삭제하였습니다", Toast.LENGTH_SHORT).show();
                            MainActivity.clearArrayList();
                            dialog.dismiss();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("notSaveArray");
                            editor.commit();
                            dialog.dismiss();
                        }
                    });

                    ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                    dismiss();
                }else{
                    Toast.makeText(context, "삭제할 용기목록이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,ReportActivity.class);
                // user_id 가져오기 0601 추가
                //intent.putExtra("uid",value);
                startActivity(intent);

            }
        });

        btn_incar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayList.size() <= 0){
                    Toast.makeText(context, "용기를 선택하세요", Toast.LENGTH_SHORT).show();
                }else {
                    CustomDialog customDialog = new CustomDialog(context, btn_incar.getText().toString());

                    String tempStr = "";
                    for (int i = 0; i < arrayList.size(); i++) {
                        tempStr += arrayList.get(i).getTv_bottleBarCd() + ",";
                    }

                    // 커스텀 다이얼로그를 호출한다.
                    customDialog.callFunction(tempStr, userId);
                }
            }
        });

        btn_bottleTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1
                        = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
                builder1 .setTitle("대한특수가스")
                        .setMessage("공사중입니다")
                        .setPositiveButton("확인", null);
                AlertDialog ad = builder1.create();

                ad.show();
                dismiss();
            }
        });

        btn_reportRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockReportDialog customDialog = new StockReportDialog(context,btn_reportRent.getText().toString());

                // 커스텀 다이얼로그를 호출한다.
                customDialog.callFunction();
            }
        });

        btn_reportStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StockReportOwnerDialog customDialog = new StockReportOwnerDialog(context,btn_reportStock.getText().toString());

                // 커스텀 다이얼로그를 호출한다.
                customDialog.callFunction();
            }
        });
        btn_reportAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockReportDialog customDialog = new StockReportDialog(context,btn_reportAgency.getText().toString());

                // 커스텀 다이얼로그를 호출한다.
                customDialog.callFunction();
            }
        });

        btn_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1
                        = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_DARK);
                builder1 .setTitle("대한특수가스")
                        .setMessage("대한특수가스 공장앱니다")
                        .setPositiveButton("확인", null);
                AlertDialog ad = builder1.create();

                ad.show();
                dismiss();
            }
        });


        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
