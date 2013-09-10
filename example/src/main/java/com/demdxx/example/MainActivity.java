package com.demdxx.example;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.demdxx.ui.DMSlidePanelsView;

public class MainActivity extends Activity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final DMSlidePanelsView panels = (DMSlidePanelsView) findViewById(R.id.panels);

    findViewById(R.id.button_slide).setOnClickListener(this);
    findViewById(R.id.button_slide_right).setOnClickListener(this);
    findViewById(R.id.button2).setOnClickListener(this);

    ((RadioGroup) findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (R.id.anim1 == i) {
          panels.setSwipe(0);
        } else if (R.id.anim2 == i) {
          panels.setSwipe(1);
        } else if (R.id.anim3 == i) {
          panels.setSwipe(2);
        }
      }
    });

    ((CheckBox) findViewById(R.id.fixed)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        panels.setSidebarFixed(b);
      }
    });

    ((EditText) findViewById(R.id.duration)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        // ...
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        try {
          panels.setSlideAnimationDuration(Integer.valueOf(charSequence.toString()));
        } catch (Exception e) {
          // ...
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
        // ...
      }
    });
  }

  @Override
  public void onClick(View view) {
    if (R.id.button2 == view.getId()) {
      ((DMSlidePanelsView) findViewById(R.id.panels)).toggleLeft(true);
    } else if (R.id.button_slide == view.getId()) {
      ((DMSlidePanelsView) findViewById(R.id.panels)).toggleLeft(true);
    } else if (R.id.button_slide_right == view.getId()) {
      ((DMSlidePanelsView) findViewById(R.id.panels)).toggleRight(true);
    }
  }

}
