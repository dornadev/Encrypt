package ir.dornadev.encryptnote;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ir.dornadev.encrypt.R;
import ir.dornadev.encryptnote.Handler.DbHandler;

public class itShowitem extends AppCompatActivity {

    String aes="AES",output;
    EditText txtmessageup,txtpassword,txttitleup;
    FloatingActionButton fabsave,btnencrypt,fabdecrypt,btnedit;
    TextView txtmessage,txttitle,date;
    ImageView copy,share,delete,back,copyup,pasteup,shareup,deleteup,backup;
    ClipData clipData;
    ClipboardManager clipboardManager;
    String ids;
    private DbHandler db;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_item);
        initviews();

        db=new DbHandler(this);
        Intent intent=getIntent();
        Bundle bn=intent.getExtras();
        ids=bn.getString("id");
        db.open();
        txttitle.setText(db.display2(Integer.parseInt(ids),1));
        txtmessage.setText(db.display2(Integer.parseInt(ids),2));
        date.setText(db.display2(Integer.parseInt(ids),3));

        db.close();
        Imagefunctions();

    }

    private void Imagefunctions() {
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt=txtmessage.getText().toString();
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipData= ClipData.newPlainText("Text",txt);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Showitem.this,"متن کپی شد",Toast.LENGTH_LONG).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareintent=new Intent(Intent.ACTION_SEND);
                shareintent.setType("text/plain");
                shareintent.putExtra(android.content.Intent.EXTRA_TEXT,txtmessage.getText().toString());
                startActivity(Intent.createChooser(shareintent,"اشتراک گذاری "));

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(Showitem.this);
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
                        db.delete(Integer.parseInt(ids));
                        db.close();
                        Intent back=new Intent(Showitem.this,TextFieldsActivity.class);
                        startActivity(back);
                        finish();
                        Toast.makeText(Showitem.this,"متن مورد نظر حذف شد",Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back=new Intent(Showitem.this,TextFieldsActivity.class);
                startActivity(back);
                finish();
            }
        });
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_update);
                functions();
                db.open();
                txttitleup.setText(db.display2(Integer.parseInt(ids),1));
                txtmessageup.setText(db.display2(Integer.parseInt(ids),2));
                db.close();
            }
        });
    }

    private void initviews() {
        share=(ImageView)findViewById(R.id.share);
        copy=(ImageView)findViewById(R.id.copy);
        delete=(ImageView)findViewById(R.id.delete);
        back=(ImageView)findViewById(R.id.back);
        btnedit=(FloatingActionButton)findViewById(R.id.btnedit);
        txtmessage=(TextView)findViewById(R.id.showmessage);
        txttitle=(TextView)findViewById(R.id.showtitle);
        date=(TextView)findViewById(R.id.date);



    }

    public void functions() {
        upinitviews();
        btnencrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtpassword.getText().toString().trim())){
                    txtpassword.setError("لطفا رمز را وارد کنید");
                    txtpassword.requestFocus();


                }else if (TextUtils.isEmpty(txtmessageup.getText().toString())){
                    txtmessageup.setError("لطفا متن خود را وارد کنید");
                    txtmessageup.requestFocus();
                }
                else {
                    try {

                        output = encrypt(txtmessageup.getText().toString(), txtpassword.getText().toString());
                        txtmessageup.setText(output);
                        Toast.makeText(getBaseContext(), "متن شما رمزنگاری شد", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
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


                }else if (TextUtils.isEmpty(txtmessageup.getText().toString())){
                    txtmessageup.setError("لطفا متن خود را وارد کنید");
                    txtmessageup.requestFocus();
                }else {
                    try {
                        output = decrypt(txtmessageup.getText().toString(), txtpassword.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "رمز اشتباه است", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    txtmessageup.setText(output);
                }

            }
        });
        Imagefunctions();

        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txttitleup.getText().toString().trim())) {
                    txttitleup.setError("لطفا عنوان متن خود را وارد کنید");
                    txttitleup.requestFocus();


                } else if (TextUtils.isEmpty(txtmessageup.getText().toString())) {
                    txtmessageup.setError("لطفا متن خود را وارد کنید");
                    txtmessageup.requestFocus();
                } else {
                    String ttitle = txttitleup.getText().toString();
                    String mess = txtmessageup.getText().toString();
                    if (TextUtils.isEmpty(ttitle) && TextUtils.isEmpty(mess)) {
                        Toast.makeText(getBaseContext(), "لطفا عنوان و متن خود را وارد کنید", Toast.LENGTH_SHORT).show();
                    } else {
                        db.open();
                        db.update(ttitle,mess, Integer.parseInt(ids));
                        db.close();
                        final Dialog dialog=new Dialog(Showitem.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.done);
                        dialog.show();


                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                dialog.dismiss();
                                Intent back=new Intent(Showitem.this,TextFieldsActivity.class);
                                startActivity(back);
                            }}, 2700);
                    }





                }
            }
        });
        copyup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt=txtmessageup.getText().toString();
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipData= ClipData.newPlainText("Text",txt);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(Showitem.this,"متن کپی شد",Toast.LENGTH_SHORT).show();
            }
        });
        pasteup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = clipboardManager.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                String text = item.getText().toString();
                txtmessageup.setText(text);
                Toast.makeText(Showitem.this,"متن چسبانده شد",Toast.LENGTH_SHORT).show();
            }
        });

        shareup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareintent=new Intent(Intent.ACTION_SEND);
                shareintent.setType("text/plain");
                shareintent.putExtra(Intent.EXTRA_TEXT,txtmessageup.getText().toString());
                startActivity(Intent.createChooser(shareintent,"اشتراک گذاری "));

            }
        });

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(Showitem.this);
                dialog.setMessage("تغییرات ذخیره نمیشود آیا مایل به ادامه هستید؟");
                dialog.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent back=new Intent(Showitem.this,TextFieldsActivity.class);
                        startActivity(back);
                        finish();

                    }
                });
                dialog.show();

            }
        });
        deleteup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog=new AlertDialog.Builder(Showitem.this);
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
                        db.delete(Integer.parseInt(ids));
                        db.close();
                        Intent back=new Intent(Showitem.this,TextFieldsActivity.class);
                        startActivity(back);
                        finish();
                        Toast.makeText(Showitem.this,"متن مورد نظر حذف شد",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

            }
        });





    }

    private void upinitviews() {
        fabsave=(FloatingActionButton) findViewById(R.id.btnupdate);
        btnencrypt=(FloatingActionButton) findViewById(R.id.btnende);
        fabdecrypt=(FloatingActionButton) findViewById(R.id.btndeen);
        txtpassword=(EditText)findViewById(R.id.txtpassup);
        txtmessageup=(EditText)findViewById(R.id.txtmessegeup);
        txttitleup=(EditText)findViewById(R.id.txttitleup);
        shareup=(ImageView)findViewById(R.id.shareup);
        copyup=(ImageView)findViewById(R.id.copyup);
        deleteup=(ImageView)findViewById(R.id.deleteup);
        pasteup=(ImageView)findViewById(R.id.pasteup);
        backup=(ImageView)findViewById(R.id.backup);
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
