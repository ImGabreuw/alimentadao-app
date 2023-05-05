package br.com.alimentadao.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleDragUpButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleDragUpButton() {
        ImageButton button = findViewById(R.id.drag_up_button);

        button.setOnTouchListener((view, event) -> {
            float startY = 0;
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                startY = event.getY();
                return true;
            }

            float endY = event.getY();
            float deltaY = endY - startY;

            if (!(deltaY < 0) || !(Math.abs(deltaY) > 100)) return false;

            Transition transition = new Slide(Gravity.TOP);
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(
                    (ViewGroup) getWindow().getDecorView().getRootView(),
                    transition
            );

            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.nothing);

            return true;
        });
    }

}