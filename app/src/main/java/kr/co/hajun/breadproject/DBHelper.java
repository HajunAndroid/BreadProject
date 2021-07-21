package kr.co.hajun.breadproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public DBHelper(Context context){
        super(context,"storedb",null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String storeSQL = "create table tb_store"+
                "(_id integer primary key autoincrement,"+
                "storeName,"+
                "storeAddress,"+
                "storePhone)";
        sqLiteDatabase.execSQL(storeSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
