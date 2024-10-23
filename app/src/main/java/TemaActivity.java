
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test2.MainActivity;

public class TemaActivity extends AppCompatActivity {  // AppCompatActivity 상속

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tema);  // tema.xml과 연결

        // 테마 버튼 설정
        ImageButton temaHomeButton = findViewById(R.id.imageButton);
        ImageButton temaAirportButton = findViewById(R.id.imageButton2);

        temaHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThemePreference("home");
                goToMainActivity();
            }
        });

        temaAirportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThemePreference("airport");
                goToMainActivity();
            }
        });
    }

    private void saveThemePreference(String theme) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedTheme", theme);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(TemaActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
