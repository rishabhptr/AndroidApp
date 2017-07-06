package com.example.rish.androidapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Objects;

import static java.util.logging.Logger.global;

public class ProfileActivity extends AppCompatActivity {
    final String pathtofirebase="gs://androidapp-6745a.appspot.com/MathsOlympiad";
    FirebaseAuth firebaseAuth;
    StorageReference storagereference;
    ListView listview;
    ArrayAdapter<String> arraypdf;
    Button btnlogout;
    TextView textView;
    ProgressDialog progressDialog;     //To see the progress when logging out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnlogout=(Button)findViewById(R.id.logout);                                 //the logout button
        textView=(TextView)findViewById(R.id.showemail);                            //shows email to screen
        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        Listview();
        btnlogout.setOnClickListener(click);
    }

    private void Listview() {
        storagereference= FirebaseStorage.getInstance().getReferenceFromUrl(pathtofirebase);

       listview=(ListView)findViewById(R.id.theListView);
        String fav[]={"Hello","World","How","Are","You"};
        arraypdf=new ArrayAdapter<String>(ProfileActivity.this,android.R.layout.simple_list_item_1,fav);
        listview.setAdapter(arraypdf);
        listview.setOnItemClickListener(itemobject);
    }
    private AdapterView.OnItemClickListener itemobject=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    private HashMap<String,Object> testing(){
        final HashMap<String,Object> hashmaparray =new HashMap<>();
        final HashMap<String,Object>hashmap=new HashMap<>();
        StorageReference storagereference =FirebaseStorage.getInstance().getReference(pathtofirebase);
         final int i[]=new int[2];
        i[1]=0;
        for( i[0]=1;;i[0]++){
            String path=i+".pdf";
            final StringBuilder build=new StringBuilder();
            StorageReference sref=storagereference.child(path);
            sref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {

                 hashmap.put("Name",storageMetadata.getName());
                    hashmap.put("StorageReference",storageMetadata.getReference());
                    hashmap.put("Check",storageMetadata.getCustomMetadata("Check"));
                    hashmaparray.put(String.valueOf(i[0]),hashmap);
                    if(storageMetadata.getCustomMetadata("Check").toString()=="final")
                        i[1]=1;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            if(i[1]==1){
              break;
            }
        }
    return  hashmaparray;}

    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressDialog.setMessage("Signing out...");
            progressDialog.show();                                              // show the loading sign
            firebaseAuth.signOut();        //Sign out the firebase
            finish();                     //End the current activity
            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));           //Start the activity LoginActivity     go to login page
        }
    };

    private void itemdownload(String dfrom, String dto){
       final String from=dfrom;
        final String to=dto;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Download file ...", Toast.LENGTH_SHORT).show();
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        File file = new File(to + from.substring(from.lastIndexOf('.')));
                        if (file.exists()) file.delete();
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);

                            ProfileActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "File successfully downloaded.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                th.start();
            }
        });
    }

 private void updatemetadata( String mto, String mkey,String mvalue){
     final String to=mto,key=mkey,value=mvalue;
     StorageReference sref=FirebaseStorage.getInstance().getReference();
     StorageReference ref=sref.child(to);
     StorageMetadata smeta=new StorageMetadata.Builder().setCustomMetadata(key,value).build();
    ref.updateMetadata(smeta).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
        @Override
        public void onSuccess(StorageMetadata storageMetadata) {
         Toast.makeText(getApplicationContext(),"Updating metadata successful",Toast.LENGTH_SHORT).show();
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    });

 }
 private ContentValues[] stringaraaybuilder(int number,String partialpath){
     //final int no=number;
     ContentValues[] contentvalues=new ContentValues[number];
     for(int i=0;i<number;i++){
         String path=partialpath+i;
      contentvalues[i]=listbuilder(path);

     }
     return contentvalues;
 }

 private ContentValues listbuilder(String mto){
     final String to=mto;
//     final StringBuilder sbf=new StringBuilder();
//     final StringBuilder sf=new StringBuilder();
     final ContentValues content=new ContentValues();
     String st;
     StorageReference sref=FirebaseStorage.getInstance().getReference();
     StorageReference ref=sref.child(to);
     ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {

         @Override
         public void onSuccess(StorageMetadata storageMetadata) {
           String string=storageMetadata.getName()+".pdf";
             content.put("Name",string);
             content.put("url",storageMetadata.getPath());
         };
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
         }
     });


  return content;
 }
 private StorageReference listitemdetector(ContentValues itemname) {
     String s=itemname.get("url").toString();
     return FirebaseStorage.getInstance().getReferenceFromUrl(s);
 }

    }


