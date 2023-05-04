package br.com.alimentadao.app;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.alimentadao.app.bluetooth.BluetoothService;
import br.com.alimentadao.app.time.TimeAdapter;

public class HomeActivity extends AppCompatActivity {

    private final BluetoothService bluetoothService = ConnectionActivity.getInstance().getBluetoothService();

    private TimeAdapter timeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        RecyclerView recyclerViewTimes = findViewById(R.id.timeList);
        recyclerViewTimes.setLayoutManager(new LinearLayoutManager(this));

        timeAdapter = new TimeAdapter();
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

    private void showAddTimeDialog() {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Selecione um horário")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(view -> {
            int hour = materialTimePicker.getHour();
            int minute = materialTimePicker.getMinute();

            timeAdapter.add(String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hour, minute
            ));
        });

        materialTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
}