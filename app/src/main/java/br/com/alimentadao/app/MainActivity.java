package br.com.alimentadao.app;

import static android.content.Intent.ACTION_PICK;
import static br.com.alimentadao.app.bluetooth.BluetoothGateway.REQUEST_ENABLE_BT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
        buttonAddTime.setOnClickListener(view -> showAddTimeDialog());

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

    private void openGallery() {
        Intent intent = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            ImageView meuImageButton = findViewById(R.id.dogImage);
            meuImageButton.setImageURI(imageUri);

            Toast.makeText(
                    getApplicationContext(),
                    "Foto atualizada com sucesso",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (requestCode == REQUEST_ENABLE_BT && resultCode == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(MainActivity.this, BTUsesActivity.class));
        }
    }
}