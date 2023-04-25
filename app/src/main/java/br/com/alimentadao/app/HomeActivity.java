package br.com.alimentadao.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.alimentadao.app.bluetooth.BluetoothService;

public class HomeActivity extends AppCompatActivity implements TimeAdapter.OnTimeRemoveListener {

    private List<String> timeCache;
    private TimeAdapter timeAdapter;

    private BluetoothService bluetoothService = MainActivity.getInstance().getBluetoothService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        RecyclerView recyclerViewTimes = findViewById(R.id.timeList);
        recyclerViewTimes.setLayoutManager(new LinearLayoutManager(this));

        timeCache = new ArrayList<>();
        timeAdapter = new TimeAdapter(timeCache, this);
        recyclerViewTimes.setAdapter(timeAdapter);

        Button buttonAddTime = findViewById(R.id.btn_add_time);
        buttonAddTime.setOnClickListener(view -> showAddTimeDialog());

        // TODO: 19/04/2023 Fetch times from arduino

        Button buttonFedNow = findViewById(R.id.btn_fed_now);
        buttonFedNow.setOnClickListener(view -> {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);

            bluetoothService.sendTime(formatter.format(now));
        });
    }

    @Override
    public void onTimeRemoved(String time) {
        int position = timeCache.indexOf(time);
        timeCache.remove(time);
        timeAdapter.notifyItemRemoved(position);
    }

    private void showAddTimeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_time, null);
        EditText etTime = dialogView.findViewById(R.id.inputTime);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Adicionar", (addTimeDialog, which) -> {
            String time = etTime.getText().toString();

            if (TextUtils.isEmpty(time)) {
                Toast.makeText(
                        getApplicationContext(),
                        "Insira um horário válido (HH:MM)",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            timeCache.add(time);
            timeAdapter.notifyItemInserted(timeCache.size() - 1);
            addTimeDialog.dismiss();
        });
        dialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                "Cancelar",
                (cancelDialog, which) -> cancelDialog.dismiss()
        );

        dialog.show();
    }
}