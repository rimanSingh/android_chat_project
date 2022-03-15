package com.example.arcmessenger.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.R;
import com.example.arcmessenger.adapter.ContactAdapter;
import com.example.arcmessenger.databinding.ActivityContactBinding;
import com.example.arcmessenger.model.UserDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ActivityContactBinding binding;

    private static final String TAG = "ContactsActivity";
    private List<UserDetail> list = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    public static final int REQUEST_READ_CONTACTS = 79;
    private ListView contactList;
    private ArrayList mobileArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null){
            getContactFromPhone();
        }

        if (mobileArray != null) {
            getContact();
        }

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getContactFromPhone() {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)

                == PackageManager.PERMISSION_GRANTED) {

            mobileArray = getAllPhoneContacts();

        }
        else {

            requestPermission();

        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    mobileArray = getAllPhoneContacts();

                }
                else {

                    finish();

                }

                return;

            }
        }
    }

    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }

    private void getContact() {
        firebaseFirestore.collection("Users")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                    String userID = snapshots.getString("userID");
                    String userName = snapshots.getString("userName");
                    String imageURL = snapshots.getString("imgProfile");
                    String userPhone = snapshots.getString("userPhone");

                    UserDetail users = new UserDetail();
                    users.setUserID(userID);
                    users.setUserName(userName);
                    users.setImgProfile(imageURL);
                    users.setUserPhone(userPhone);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        if (mobileArray.contains(users.getUserPhone())){
                            list.add(users);
                        }
                    }
                }

//                for (UserDetail users : list){
//                    if (mobileArray.contains(users.getUserPhone())){
//                        Log.d(TAG, "getContactList: true " +users.getUserPhone());
//                    }
//                    else {
//                        Log.d(TAG, "getContactList: false" +users.getUserPhone());
//                    }
//                }
                contactAdapter = new ContactAdapter(list, ContactActivity.this);
                binding.recycleView.setAdapter(contactAdapter);
            }
        });
    }
}