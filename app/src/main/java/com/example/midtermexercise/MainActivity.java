package com.example.midtermexercise;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định hiển thị trang chủ
        loadFragment(new ContactsFragment());

        // Xử lý chuyển giữa các tab
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_contacts) {
                selectedFragment = new ContactsFragment();
            } else if (id == R.id.nav_groups) {
                selectedFragment = new GroupFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }else if (id == R.id.nav_dialpad) {
                selectedFragment = new DialpadFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Tùy chỉnh kích thước item trong BottomNavigationView
        bottomNavigationView.post(() -> {
            ViewGroup menuView = (ViewGroup) bottomNavigationView.getChildAt(0);
            if (menuView != null) {
                int count = menuView.getChildCount();
                for (int i = 0; i < count; i++) {
                    View item = menuView.getChildAt(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            0,
                            dpToPx(64),
                            1f
                    );
                    item.setLayoutParams(params);
                    item.setPadding(0, dpToPx(6), 0, dpToPx(6));
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Chuyển đổi dp sang pixel
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
