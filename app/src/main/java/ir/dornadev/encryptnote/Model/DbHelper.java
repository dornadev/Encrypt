package ir.dornadev.encryptnote.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class DbHelper extends SQLiteOpenHelper{

    public static final String dbname="DataBEnc";
    public static final String tbltext="tbltext";
    public static final String id="id";
    public static final String title="title";
    public static final String text="text";
    public static final String date="date";



    public static final String creaattable="CREATE TABLE "+tbltext+"("+id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            title+" TEXT,"+text+" TEXT,"+date+");";

    public DbHelper(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(creaattable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("UPDATE "+tbltext);


    }
}
