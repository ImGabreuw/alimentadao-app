package br.com.alimentadao.app.time;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.alimentadao.app.R;

public class TimeViewHolder extends RecyclerView.ViewHolder {
    public final TextView textViewTime;
    public final ImageButton buttonRemove;

    TimeViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewTime = itemView.findViewById(R.id.txtTime);
        buttonRemove = itemView.findViewById(R.id.btnRemove);
    }
}
