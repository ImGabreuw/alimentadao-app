package br.com.alimentadao.app;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import br.com.alimentadao.app.bluetooth.ArduinoBluetoothConnection;
import br.com.alimentadao.app.database.TimeSQLiteRepository;
import br.com.alimentadao.app.time.TimeAdapter;
import br.com.alimentadao.app.time.TimeItem;

public class HomeActivity extends AppCompatActivity {

    private final ArduinoBluetoothConnection bluetoothConnection = ConnectionActivity.getInstance().getBluetoothConnection();

    private TimeSQLiteRepository timeRepository;
    private TimeAdapter timeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        RecyclerView recyclerViewTimes = findViewById(R.id.timeList);
        recyclerViewTimes.setLayoutManager(new LinearLayoutManager(this));

        timeRepository = new TimeSQLiteRepository(this);

        timeAdapter = new TimeAdapter(timeRepository.findAll());
        recyclerViewTimes.setAdapter(timeAdapter);

        handleAddTimeButton();
        handleFedNowButton();

        timeRepository
                .findAll()
                .stream()
                .map(TimeItem::getFormattedTime)
                .forEach(bluetoothConnection::sendData);
    }

    @Override
    protected void onPause() {
        super.onPause();

        timeRepository.deleteAll();
        timeAdapter.getTimeCache().forEach(time -> timeRepository.save(time));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeRepository.close();
    }

    private void handleAddTimeButton() {
        Button buttonAddTime = findViewById(R.id.add_time_button);

        buttonAddTime.setOnClickListener(view -> showAddTimeDialog());
    }

    private void handleFedNowButton() {
        Button buttonFedNow = findViewById(R.id.fed_now_button);

        buttonFedNow.setOnClickListener(view -> bluetoothConnection.sendData("a"));
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

            timeAdapter.add(TimeItem.create(hour, minute));
        });

        materialTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
}