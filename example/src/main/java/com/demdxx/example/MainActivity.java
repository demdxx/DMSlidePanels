package com.demdxx.example;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import com.demdxx.ui.DMSlidePanelsView;

public class MainActivity extends Activity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.button_slide).setOnClickListener(this);
    findViewById(R.id.button2).setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (R.id.button2 == view.getId()) {
      ((DMSlidePanelsView) findViewById(R.id.panels)).toggleLeft(true);
    } else {
      ((DMSlidePanelsView) findViewById(R.id.panels)).toggleLeft(true);
    }
  }

}
