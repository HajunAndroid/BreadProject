package kr.co.hajun.breadproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    String storeName; //제과점 이름
    String storeAddress; //제과점 주소
    String[] splitAddress; //제과점 주소 중 '번지'라는 단어 앞까지 자르기 위해 선언합니다.
    String storePhone; //제과점 번호
    GoogleMap mMap; //구글 지도
    float latitude; // 주소를 위도와 경로도 변환하기 위해 선언합니다.
    float longitude;
    TextView text1,text2; //text1은 제과점 이름, text2는 제과점 주소를 뷰에 보이기 위해 사용합니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        text1=findViewById(R.id.main2Name);
        text2=findViewById(R.id.main2Address);
        //Intent를 통해 MainActivity에서 선택된 제과점 이름을 얻습니다.
        Intent intent = getIntent();
        storeName = intent.getStringExtra("position");
        //제과점 이름으로 해당 제과점의 주소와 번호를 SQLiteDatabase에서 찾습니다.
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select storeAddress, storePhone from tb_store where storeName=? limit 1", new String[]{storeName});
        while (cursor.moveToNext()) {
            storeAddress = cursor.getString(0);
            storePhone = cursor.getString(1);
            //찾은 정보를 뷰에 나타냅니다.
            text1.setText(storeName);
            text2.setText(storeAddress);
            //주소 중에서 '번지','-0'은 지도 검색 시 오류를 발생시켜 제외합니다.
            splitAddress = storeAddress.split("번지");
            splitAddress[0]=splitAddress[0].replaceAll("-0","");
        }
        //사용이 완료된 SQLiteDatabase는 닫습니다.
        db.close();
        //구글 지도를 사용합니다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //주소를 위도, 경도로 변환합니다.
        if(mMap !=null){
            MyGeocoder reverse = new MyGeocoder(storeAddress);
            reverse.start();
        }
    }

    class MyGeocoder extends Thread{
        String address;
        public MyGeocoder(String address){
            this.address = address;
        }
        //Geocoder는 구글 서버 콘텐츠를 이용하는 구조입니다.
        //따라서 thread를 작성해서 thread 내부에서 구현합니다.
        @Override
        public void run() {
            Geocoder geocoder = new Geocoder(MainActivity2.this);
            try{
                List<Address> results = geocoder.getFromLocationName(address,1);
                Address resultAddress = results.get(0);
                LatLng latLng = new LatLng(resultAddress.getLatitude(),
                        resultAddress.getLongitude());
                //Message객체를 이용해 handler에게 전달합니다.
                Message msg = new Message();
                msg.what = 200;
                msg.obj = latLng;
                handler.sendMessage(msg);
                latitude = (float) latLng.latitude;
                longitude = (float) latLng.longitude;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //map에 해당 위도,경도 위치에 마커를 표시합니다.
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 200:
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                    markerOptions.position((LatLng)msg.obj);
                    markerOptions.title(storeName);
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng)msg.obj));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
    };

    //전화 걸기 버튼을 누르면 전화 앱을 이용해 해당 번호로 전화걸 수 있습니다.
    public void btnCall(View view){
        //권한이 있을 경우에만 전화를 겁니다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
            //전화 번호가 있을 경우에만 전화를 겁니다.
            if (storePhone.length() >= 9) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + storePhone));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,"전화걸기 권한이 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    //길찾기 버튼으르 누르면 구글 지도 앱에 제과점 주소를 보내 길찾기 서비스를 제공합니다.
    public void btnCall2(View view){
        Uri uri = Uri.parse("http://maps.google.com/maps?f=d&daddr="+splitAddress[0]+"&hl=ko");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(it);
    }
}