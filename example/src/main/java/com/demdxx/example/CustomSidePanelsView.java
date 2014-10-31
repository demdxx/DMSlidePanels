package com.demdxx.example;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.demdxx.ui.DMSlideAnimator;
import com.demdxx.ui.DMSlidePanelsView;

public class CustomSidePanelsView extends DMSlidePanelsView {

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Constructors
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public CustomSidePanelsView(Context context) {
    super(context);
  }

  public CustomSidePanelsView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomSidePanelsView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Animators
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected PanelTranslation createPanelTranslation() {
    return new PanelTranslation();
  }

  @Override
  public void onAnimationStart(Animation animation) {
    super.onAnimationStart(animation);
  }

  @Override
  public void onAnimationEnd(Animation animation) {
    if (Math.abs(translation.centerPanelTranslation.left) < 10.f) {
      translation.centerPanelTranslation = translation.getCenterPanelTranslation(true, false);
    }
    super.onAnimationEnd(animation);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Display panels actions
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public int getLeftPanelWidth() {
    int w = Math.min(getMeasuredWidth(), getMeasuredHeight());
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      w *= 0.8f;
    }
    return w;
  }

  public int getRightPanelWidth() {
    return getLeftPanelWidth();
  }

  public int getPanelWidth() {
    return (int)(Math.min(getMeasuredWidth(), getMeasuredHeight())*0.8f);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Item Translation
  //////////////////////////////////////////////////////////////////////////////////////////////////

  class ItemTranslation extends DMSlideAnimator.Translation
  {
    float scale = 1.f;

    @Override
    protected DMSlideAnimator.Translation update(View v) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        if (null != v) {
          v.setScaleX(scale);
          v.setScaleY(scale);
        }
      }
      return super.update(v);
    }

    @Override
    protected DMSlideAnimator.Translation update(DMSlideAnimator.Translation from, DMSlideAnimator.Translation to, float interpolatedTime) {
      super.update(from, to, interpolatedTime);
      if (from.left < 0 || to.left < 0) {
        interpolatedTime = ((float) getRightPanelWidth() - (float) right) / (float) getPanelWidth();
        if (from.left < to.left) {
          interpolatedTime = 1.f - interpolatedTime;
        }
      } else {
        interpolatedTime = (float) left / (float) getPanelWidth();
        if (from.left > to.left) {
          interpolatedTime = 1.f - interpolatedTime;
        }
      }
      if (from instanceof ItemTranslation && to instanceof ItemTranslation) {
        final float f = ((ItemTranslation) from).scale;
        final float t = ((ItemTranslation) to).scale;
        scale = f + (t - f) * interpolatedTime;
      } else {
        scale = 1.f - 0.2f * interpolatedTime;
      }
      return this;
    }

    public DMSlideAnimator.Translation set(int left, int top, int right, int bottom, float scale) {
      this.scale = scale;
      return super.set(left, top, right, bottom);
    }

    @Override
    public DMSlideAnimator.Translation copy() {
      return new ItemTranslation().set(left, top, right, bottom, scale);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Scale Translation
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public class PanelTranslation extends DMSlidePanelsView.PanelTranslation {

    public PanelTranslation() {
      super();
      centerPanelTranslation = new ItemTranslation();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /// Move to sidebar
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get end position at
     *
     * @param front boolean
     * @param right boolean
     * @return translation
     */
    @Override
    public DMSlideAnimator.Translation getCenterPanelTranslation(boolean front, boolean right) {
      ItemTranslation c = (ItemTranslation) centerPanelTranslation.copy();
      c.top = getCenterPanel().getTop();
      c.bottom = c.top + getMeasuredHeight();
      if (front) {
        c.left = 0;
        c.scale = 1.f;
      } else if (right) {
        c.left = -getPanelWidth();
        c.scale = 0.8f;
      } else {
        c.left = getPanelWidth();
        c.scale = 0.8f;
      }
      c.right = getMeasuredWidth() + c.left;
      return c;
    }
  }
}
