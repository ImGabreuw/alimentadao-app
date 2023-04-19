package br.com.alimentadao.app;

import static android.content.Intent.ACTION_PICK;
import static br.com.alimentadao.app.bluetooth.BluetoothGateway.REQUEST_ENABLE_BT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentadao.app.bluetooth.BluetoothGateway;

public class MainActivity extends AppCompatActivity implements TimeAdapter.OnTimeRemoveListener {

    private static final int REQUEST_PICK_IMAGE = 1;

    private List<String> timeCache;
    private TimeAdapter timeAdapter;

    private BluetoothGateway bluetoothGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerViewTimes = findViewById(R.id.timeList);
        recyclerViewTimes.setLayoutManager(new LinearLayoutManager(this));

        timeCache = new ArrayList<>();
        timeAdapter = new TimeAdapter(timeCache, this);
        recyclerViewTimes.setAdapter(timeAdapter);

        Button buttonAddTime = findViewById(R.id.btnAddTime);
        buttonAddTime.setOnClickListener(view -> showDialogToAddTime());

        ImageButton buttonChangeImgProfile = findViewById(R.id.btnChangeImgProfile);
        buttonChangeImgProfile.setOnClickListener(view -> openGallery());

        // TODO: 19/04/2023 Fetch times from arduino

        Button buttonFedNow = findViewById(R.id.btn_fed_now);
        buttonFedNow.setOnClickListener(view -> bluetoothGateway = new BluetoothGateway(this));
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
        EditText etTime = dialogView.findViewById(R.id.inputTime);

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

    private void openGallery() {
        Intent intent = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_PICK_IMAGE || resultCode != RESULT_OK || data == null) {
            return;
        }

        Uri imageUri = data.getData();

        ImageView meuImageButton = findViewById(R.id.dogImage);
        meuImageButton.setImageURI(imageUri);

        Toast.makeText(
                getApplicationContext(),
                "Foto atualizada com sucesso",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_ENABLE_BT) return;

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.deny_bluetooth_permission);
        }
    }
}