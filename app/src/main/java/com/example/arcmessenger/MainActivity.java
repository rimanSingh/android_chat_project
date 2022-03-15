package com.example.arcmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.contacts.ContactActivity;
import com.example.arcmessenger.databinding.ActivityMainBinding;
import com.example.arcmessenger.menu.ChatFragment;
import com.example.arcmessenger.setting.SettingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private NestedScrollView nestedScrollView;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton floatingActionButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        textView = findViewById(R.id.welcomeText);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButton = findViewById(R.id.fabButton);
        nestedScrollView = findViewById(R.id.nestedView);

        setUpWithViewPager(binding.viewPager);

        if (firebaseUser!=null)
        {
            getUserInformation();
        }

        onClick();

    }

    private void setUpWithViewPager(ViewPager viewPager) {
        MainActivity.SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(),"");
        //textView.setVisibility(View.INVISIBLE);
        viewPager.setAdapter(adapter);
    }

    private static class SectionPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position){ return mFragmentList.get(position);}

        @Override
        public int getCount(){ return mFragmentList.size();}

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){ return mFragmentTitleList.get(position);}
    }

    private void onClick(){
        binding.moreSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSettingOpen();
            }
        });
        binding.fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            }
        });
    }

    private void onClickSettingOpen() {
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
    }

    private void getUserInformation(){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("userName");
                String userImage = documentSnapshot.getString("imgProfile");

                binding.userName.setText(username);
                Glide.with(MainActivity.this).load(userImage).into(binding.userImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data", "onFailure: "+e.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }
}