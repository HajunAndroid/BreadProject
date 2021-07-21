package kr.co.hajun.breadproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //HTTP 통신을 위한 Retrofit2 사용합니다.
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    //서울 열린 데이터 광장 - 서울특별시 강남구 제과 영업점 인허가 정보 OpenAPI Key입니다.
    private String API_KEY = "75677541726f30633938614b664150";
    //RecyclerView 사용합니다.
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    //전화 기능 권한 얻습니다.
    public boolean phoneCallPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //전화 기능 권한 있는지 확인하고 없다면 요청합니다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            phoneCallPermission=true;
        }
        if(!phoneCallPermission){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},200);
        }
        //recyclerView 얻고 layoutManger 설정합니다.
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //OpenAPI로 제과점 정보를 얻을 때 난수를 발생시켜 해당 번호부터 100개만 얻습니다.(즉, 랜덤하게 일부 제과점만 받옵니다.)
        int starting = (int) (Math.random() * 1000);
        int ending = starting+100;
        //받은 데이터를 저장하기 위해 SQLiteDatabase를 이용합니다.
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        //retrofitClient를 사용해 HTTP 통신을 합니다.
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        //RetrofitInterface에 정의한 대로 인자 값을 줍니다.
        retrofitInterface.getLocaldata(API_KEY, "json", "LOCALDATA_072218_GN",
                starting, ending).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                //해당 OpenAPI의 JSON 형식에 맞게 데이터를 추출합니다.
                Example example = response.body();
                Localdata072218Gn localdata = example.getLocaldata072218Gn();
                List<Row> row = localdata.getRow();
                //rowStillSave는 "영업"하는 제과점만 뽑아낸 list입니다.
                List<Row> rowStillSave = new ArrayList<>();
                for (int i = 0; i < row.size(); i++) {
                    //영업한다면 SQLiteDatabase에 저장하고 rowStillSave에 담습니다.
                    if (row.get(i).getDtlstatenm().equals("영업")) {
                        db.execSQL("insert into tb_store(storeName,storeAddress,storePhone) values (?,?,?)",
                                new String[]{row.get(i).getBplcnm(), row.get(i).getSitewhladdr(), row.get(i).getSitetel()});
                        rowStillSave.add(row.get(i));
                    }
                }
                //recyclerView를 위해 만들어둔 StoreAdapter를 생성하고 인자 값으로 rowStillSave를 줍니다.
                mAdapter = new StoreAdapter(MainActivity.this, rowStillSave);
                recyclerView.setAdapter(mAdapter);
                //사용이 완료된 SQLiteDatabase는 닫습니다.
                db.close();
            }
            //HTTP 통신 실패 시 Log과 Toast를 띄웁니다.
            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("errorFromHttp", "error");
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //MainActivity가 종료될 때 SQLiteDatabase의 데이터를 지웁니다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM tb_store");
        db.execSQL("VACUUM");
    }
    //초기 앱이 뜨고 전화 권한이 없었을 경우, 권한을 요청합니다. 이후 권한을 받았는지 재확인하는 함수입니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                phoneCallPermission=true;
            }
        }
    }
}