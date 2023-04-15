package br.com.alimentadao.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TimeAdapter.OnTimeRemoveListener {

    private List<String> timeCache;
    private TimeAdapter timeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerViewTimes = findViewById(R.id.recyclerViewTimes);
        recyclerViewTimes.setLayoutManager(new LinearLayoutManager(this));

        timeCache = new ArrayList<>();
        timeAdapter = new TimeAdapter(timeCache, this);
        recyclerViewTimes.setAdapter(timeAdapter);

        Button buttonAddTime = findViewById(R.id.btn_add_time);
        buttonAddTime.setOnClickListener(view -> showDialogToAddTime());
    }

    @Override
    public void onTimeRemoved(String time) {
        int position = timeCache.indexOf(time);
        timeCache.remove(time);
        timeAdapter.notifyItemRemoved(position);
    }

    private void showDialogToAddTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_time, null);
        EditText etTime = dialogView.findViewById(R.id.etTime);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Adicionar", (dialog1, which) -> {
            String time = etTime.getText().toString();

            if (time.trim().length() == 0) {
                Toast.makeText(
                        getApplicationContext(),
                        "Insira um horário válido (HH:MM)",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            timeCache.add(time);
            timeAdapter.notifyItemInserted(timeCache.size() - 1);
            dialog1.dismiss();

            Toast.makeText(
                    getApplicationContext(),
                    "Horário adicionar com sucesso",
                    Toast.LENGTH_SHORT
            ).show();
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", (dialog12, which) -> dialog12.dismiss());

        dialog.show();
    }
}