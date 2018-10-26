package ir.dornadev.encryptnote;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ir.dornadev.encrypt.R;
import ir.dornadev.encryptnote.Handler.DbHandler;

public class EditActivity extends AppCompatActivity {
    EditText txtmessage,txtpassword,txttitle;
    FloatingActionButton fabsave,btnencrypt,fabdecrypt;
    ImageView copy,share,delete,paste;
    ClipData clipData;
    String aes="AES",output;
    ClipboardManager clipboardManager;
    private DbHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        db=new DbHandler(this);
        initviews();
        functions();
        txttitle.requestFocus();
    }

    public void functions() {
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
                        Toast.makeText(getBaseContext(), "پیغام شما رمزنگاری شد", Toast.LENGTH_SHORT).show();

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


                }else if (TextUtils.isEmpty(txtmessage.getText().toString())){
                    txtmessage.setError("لطفا متن خود را وارد کنید");
                    txtmessage.requestFocus();
                }else {
                    try {
                        output = decrypt(txtmessage.getText().toString(), txtpassword.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "رمز اشتباه است", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    txtmessage.setText(output);
                }

            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt=txtmessage.getText().toString();
                clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                clipData= ClipData.newPlainText("Text",txt);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(EditActivity.this,"متن کپی شد",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditActivity.this, "متن چسبانده شد", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditActivity.this, "متنی در کلیپ بورد وجود ندارد", Toast.LENGTH_SHORT).show();
                }
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





        fabsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txttitle.getText().toString().trim())) {
                    txttitle.setError("لطفا عنوان متن خود را وارد کنید");
                    txttitle.requestFocus();


                } else if (TextUtils.isEmpty(txtmessage.getText().toString())) {
                    txtmessage.setError("لطفا متن خود را وارد کنید");
                    txtmessage.requestFocus();
                } else {
                    String ttitle = txttitle.getText().toString();
                    String mess = txtmessage.getText().toString();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
                    String currentDateandTime = sdf.format(new Date());

                    if (TextUtils.isEmpty(ttitle) && TextUtils.isEmpty(mess)) {
                        Toast.makeText(getBaseContext(), "لطفا عنوان و متن خود را وارد کنید", Toast.LENGTH_SHORT).show();
                    } else {
                        db.open();
                        db.insert(ttitle, mess,currentDateandTime);
                        db.close();
                        final Dialog dialog=new Dialog(EditActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.done);
                        dialog.show();


                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                dialog.dismiss();
                                Intent back=new Intent(EditActivity.this,TextFieldsActivity.class);
                                startActivity(back);
                            }}, 2700);

                    }





                }
            }
        });


    }

    private void initviews() {
        fabsave=(FloatingActionButton) findViewById(R.id.btnsave);
        btnencrypt=(FloatingActionButton) findViewById(R.id.btnende);
        fabdecrypt=(FloatingActionButton) findViewById(R.id.btndeen);
        txtpassword=(EditText)findViewById(R.id.txtpass);
        txtmessage=(EditText)findViewById(R.id.txtmessege);
        txttitle=(EditText)findViewById(R.id.txttitle);
        String message=txtmessage.getText().toString();
        String title=txttitle.getText().toString();

        share=(ImageView)findViewById(R.id.share);
        copy=(ImageView)findViewById(R.id.copy);

        paste=(ImageView)findViewById(R.id.paste);

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
