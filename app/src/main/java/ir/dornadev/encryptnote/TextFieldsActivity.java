package ir.dornadev.encryptnote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;

import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ir.dornadev.encrypt.R;

import ir.dornadev.encryptnote.Handler.DbHandler;
import ir.dornadev.encryptnote.Model.DbHelper;


public class TextFieldsActivity extends AppCompatActivity {
    ListView listView;

    Animation bounce,bouncereverse,fabanim;
    EditText txtmessage,txtpassword;
    FloatingActionButton btnencrypt,fabdecrypt;
    ClipboardManager clipboardManager;
   EditText edtsearch;
    ClipData clipData;
    String aes="AES",output;
   ImageView setting,notfound,rate,encdec,copy,paste;
    FloatingActionButton fab,fabsearch,close;
    public int[] id;
    DbHelper dbm;
    ArrayAdapter<String> adapter;
    private DbHandler db;
    String cafe="com.farsitel.bazaar";


    @Override
    protected void onResume() {
        super.onResume();
        refreshlist();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(TextFieldsActivity.this);
        dialog.setMessage("آیا مایل به خروج از هستید؟");
        dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
        dialog.show();
    }
        //
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_fields);
        dbm=new DbHelper(this);

        dbm.getWritableDatabase();
        db=new DbHandler(this);
        initviews();
        refreshlist();
        fabanim=AnimationUtils.loadAnimation(TextFieldsActivity.this,R.anim.fabadanim);

        if (adapter.getCount()==0)
        {
            listView.setVisibility(View.GONE);
            fabsearch.setVisibility(View.GONE);
            notfound.setVisibility(View.VISIBLE);
            rate.setVisibility(View.GONE);
            fab.startAnimation(fabanim);
            encdec.setVisibility(View.GONE);
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        }else {
            listView.setVisibility(View.VISIBLE);
            encdec.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
            notfound.setVisibility(View.GONE);
            fabsearch.setVisibility(View.VISIBLE);
            fabanim.cancel();

        }
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = packageManager.getApplicationInfo(cafe, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (applicationInfo == null) {
                    Toast.makeText(getBaseContext(),"نرم افزار کافه بازار نصب نمیباشد",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("bazaar://details?id=" + "ir.dornadev.encrypt"));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(TextFieldsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_main);
                TextView backup=(TextView)dialog.findViewById(R.id.backup);
                TextView resotre=(TextView)dialog.findViewById(R.id.restore);
                TextView email=(TextView)dialog.findViewById(R.id.email);
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL,"dornadev@gmail.com");
                        intent.setType("message/rfc822");
                        startActivity(Intent.createChooser(intent, "انتخاب نرم افزار جهت ارسال ایمیل"));
                    }
                });
                backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder alert=new AlertDialog.Builder(TextFieldsActivity.this);
                        alert.setTitle("هشدار");
                        alert.setMessage("در صورتی که اطلاعات مهم خود را رمزنگاری نکرده اید اطلاعات خود را رمزنگاری کنید زیرا دسترسی به اطلاعات نسخه پشتیبان آسان است. شما میتوانید نسخه پشتیبان خود را با نام backupnote واقع در حافظه داخلی دستگاه را در تلگرام،گوگل درایو و یا غیره ذخیره کنید");
                        alert.setPositiveButton("پشتیبان گیری", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                exportDB();
                            }
                        });
                        alert.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                });
                resotre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        importDB();
                        dialog.dismiss();
                        refreshlist();
                    }
                });
                dialog.show();
            }
        });
        encdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encdecdialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Intent additem=new Intent(TextFieldsActivity.this,Showitem.class);
               Bundle bn=new Bundle();
                bn.putString("id", String.valueOf(id[i]));
                additem.putExtras(bn);
               startActivity(additem);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long ids) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(TextFieldsActivity.this);
                dialog.setMessage("ایا میخواهید متن مورد نظر حذف شود");
                dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.open();
                        db.delete(id[position]);
                        db.close();
                        refreshlist();

                    }
                });
                dialog.show();


                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add=new Intent(TextFieldsActivity.this,EditActivity.class);
              startActivity(add);
            }

        });
        edtsearch.setVisibility(View.GONE);
        fabsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bounce= AnimationUtils.loadAnimation(TextFieldsActivity.this,R.anim.bounce);
                bouncereverse=AnimationUtils.loadAnimation(TextFieldsActivity.this,R.anim.bouncereverse);

                if (edtsearch.getVisibility()==View.GONE) {
                    edtsearch.setVisibility(View.VISIBLE);
                    edtsearch.requestFocus();
                    edtsearch.startAnimation(bounce);
                    edtsearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            (TextFieldsActivity.this).adapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                }else if (edtsearch.getVisibility()==View.VISIBLE){
                    edtsearch.startAnimation(bouncereverse);
                    listView.setAdapter(adapter);
                    edtsearch.setText("");
                    edtsearch.setVisibility(View.GONE);
                }
            }
        });







    }

    private void encdecdialog() {
        final Dialog enc=new Dialog(TextFieldsActivity.this);
        enc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        enc.setContentView(R.layout.encrypt);
        enc.show();
        btnencrypt=(FloatingActionButton)enc.findViewById(R.id.btnende);
        fabdecrypt=(FloatingActionButton)enc.findViewById(R.id.btndeen);
        txtpassword=(EditText)enc.findViewById(R.id.txtpass);
        txtmessage=(EditText)enc.findViewById(R.id.txtmessege);
        close=(FloatingActionButton)enc.findViewById(R.id.close);
        copy=(ImageView)enc.findViewById(R.id.copyenc);

        paste=(ImageView)enc.findViewById(R.id.pasteenc);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt=txtmessage.getText().toString();
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipData= ClipData.newPlainText("Text",txt);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(TextFieldsActivity.this,"متن کپی شد",Toast.LENGTH_SHORT).show();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = clipboardManager.getPrimaryClip();
                if(clipboardManager.hasPrimaryClip()== true) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    String text = item.getText().toString();
                    txtmessage.setText(text);

                    Toast.makeText(TextFieldsActivity.this, "متن چسبانده شد", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(TextFieldsActivity.this, "متنی در کلیپ بورد وجود ندارد", Toast.LENGTH_SHORT).show();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enc.dismiss();
            }
        });
        btnencrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtpassword.getText().toString().trim())){
                    txtpassword.setError("لطفا رمز را وارد کنید");
                    txtpassword.requestFocus();


                }else if (TextUtils.isEmpty(txtmessage.getText().toString())){
                    txtmessage.setError("لطفا متن خود را وارد کنید");
                    txtmessage.requestFocus();
                }
                else {
                    try {

                        output = encrypt(txtmessage.getText().toString(), txtpassword.getText().toString());
                        txtmessage.setText(output);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "رمزنگاری با مشکل مواجه شده است مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        fabdecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtpassword.getText().toString().trim())){
                    txtpassword.setError("لطفا رمز را وارد کنید");
                    txtpassword.requestFocus();


                }else if (TextUtils.isEmpty(txtmessage.getText().toString())){
                    txtmessage.setError("لطفا متن خود را وارد کنید");
                    txtmessage.requestFocus();

                }else {
                    try {
                        output = decrypt(txtmessage.getText().toString(), txtpassword.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "رمز اشتباه است", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    txtmessage.setText(output);
                }

            }
        });
    }

    private void refreshlist() {
        db.open();

        int count=db.count();
        final String[] title = new String[count];
        id=new int[count];
        for (int i=0;i<count;i++){
            title[i]=db.display(i,1);
            id[i]=Integer.parseInt(db.display(i,0));
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,title);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
    }


    private void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "ir.dornadev.encrypt"
                        + "//databases//" + "DataBEnc";
                String backupDBPath = "Backupnote";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "پشتیبان گیری با موفقیت انجام شد",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "پشتیبان گیری با مشکل مواجه شده است", Toast.LENGTH_SHORT)
                    .show();

        }
    }
    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "ir.dornadev.encrypt"
                        + "//databases//" + "DataBEnc";
                String backupDBPath = "Backupnote"; // From SD directory.
                File backupDB = new File(sd, backupDBPath);
                File currentDB = new File(data, currentDBPath);
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "بازیابی نسخه پشتیبان با موفقیت انجام شد",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "بازیابی نسخه پشتیبان با مشکل مواجه شده است", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private void initviews() {
        listView=(ListView)findViewById(R.id.listview);
        edtsearch=(EditText)findViewById(R.id.edtsearch);
        fabsearch=(FloatingActionButton)findViewById(R.id.fabsearch);
        fab=(FloatingActionButton) findViewById(R.id.fab);
        setting=(ImageView)findViewById(R.id.setting);
        rate=(ImageView)findViewById(R.id.rate);
        notfound=(ImageView)findViewById(R.id.notfound);
        encdec=(ImageView)findViewById(R.id.encdec);



    }
    private String decrypt(String output, String password)throws Exception {
        SecretKeySpec key=generatekey(password);
        Cipher cipher=Cipher.getInstance(aes);
        cipher.init(Cipher.DECRYPT_MODE,key);
        byte[] decodeval= Base64.decode(output,Base64.DEFAULT);
        byte[] deval=cipher.doFinal(decodeval);
        String decryptedvalue=new String(deval);

        return decryptedvalue;
    }

    private String encrypt(String data, String password)throws Exception {
        SecretKeySpec key=generatekey(password);
        Cipher cipher=Cipher.getInstance(aes);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] encval=cipher.doFinal(data.getBytes());
        String encryptedvalue= Base64.encodeToString(encval,Base64.DEFAULT);
        return encryptedvalue;

    }

    private SecretKeySpec generatekey(String password) throws Exception{
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }


}
