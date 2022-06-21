package org.reactnative.maskedview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import com.facebook.react.views.view.ReactViewGroup;

public class RNCMaskedView extends ReactViewGroup {
  private static final String TAG = "RNCMaskedView";

  private Bitmap mBitmapMask = null;
  private boolean mBitmapMaskInvalidated = false;
  private Paint mPaint;
  private PorterDuffXfermode mPorterDuffXferMode;

  public RNCMaskedView(Context context) {
    super(context);
    
    // Default to hardware rendering, androidRenderingMode prop will override
    setRenderingMode("hardware");
    
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPorterDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    System.out.println("Constructor called");
    Log.d(TAG, "Constructor called");
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {

    super.dispatchDraw(canvas);

    if (mBitmapMaskInvalidated) {
      System.out.println("dispatchDraw: mBitmapMaskInvalidated");
      Log.d(TAG, "dispatchDraw: mBitmapMaskInvalidated");
      // redraw mask element to support animated elements
      updateBitmapMask();

      mBitmapMaskInvalidated = false;
    }

    // draw the mask
    if (mBitmapMask != null) {
      System.out.println("dispatchDraw: mBitmapMask!= null");
      Log.d(TAG, "dispatchDraw: mBitmapMask != null");
      mPaint.setXfermode(mPorterDuffXferMode);
      canvas.drawBitmap(mBitmapMask, 0, 0, mPaint);
      mPaint.setXfermode(null);
    }
  }

  @Override
  public void onDescendantInvalidated(View child, View target) {
    super.onDescendantInvalidated(child, target);
    System.out.println("onDescendantInvalidated");
    Log.d(TAG, "onDescendantInvalidated");

    if (!mBitmapMaskInvalidated) {
      System.out.println("onDescendantInvalidated: !mBitmapMaskInvalidated");
      Log.d(TAG, "onDescendantInvalidated: !mBitmapMaskInvalidated");

      View maskView = getChildAt(0);
      if (maskView != null) {
        System.out.println("onDescendantInvalidated: maskView!=null");
        Log.d(TAG, "onDescendantInvalidated: maskView!=null");
        if (maskView.equals(child)) {
          System.out.println("onDescendantInvalidated: maskView!.equals(child)");
          Log.d(TAG, "onDescendantInvalidated: maskView!.equals(child)");
          mBitmapMaskInvalidated = true;
        }
      }
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    System.out.println("onLayout");
    Log.d(TAG, "onLayout");

    if (changed) {
      mBitmapMaskInvalidated = true;
      System.out.println("onLayout: changed");
      Log.d(TAG, "onLayout: changed");
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mBitmapMaskInvalidated = true;
    System.out.println("onAttachedToWindow");
    Log.d(TAG, "onAttachedToWindow");
  }

  private void updateBitmapMask() {
    System.out.println("updateBitmapMask");
    Log.d(TAG, "updateBitmapMask");

    if (this.mBitmapMask != null) {
      this.mBitmapMask.recycle();
      System.out.println("updateBitmapMask: != null 1");
      Log.d(TAG, "updateBitmapMask: != null 1");
    }

    View maskView = getChildAt(0);
    if (maskView != null) {
      System.out.println("updateBitmapMask: != null 2");
      Log.d(TAG, "updateBitmapMask: != null 2");
      maskView.setVisibility(View.VISIBLE);
      this.mBitmapMask = getBitmapFromView(maskView);
      maskView.setVisibility(View.INVISIBLE);
    } else{
      System.out.println("updateBitmapMask: == null");
      Log.d(TAG, "updateBitmapMask: == null");
      this.mBitmapMask = null;
    }
  }

  public static Bitmap getBitmapFromView(final View view) {
    System.out.println("getBitmapFromView");
    Log.d(TAG, "getBitmapFromView");
    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

    if (view.getMeasuredWidth() <= 0 || view.getMeasuredHeight() <= 0) {
      return null;
    }

    final Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
            view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

    final Canvas canvas = new Canvas(bitmap);

    view.draw(canvas);

    return bitmap;
  }

  public void setRenderingMode(String renderingMode) {
    System.out.println("setRenderingMode");
    Log.d(TAG, "setRenderingMode");
    if (renderingMode.equals("software")) {
      setLayerType(LAYER_TYPE_SOFTWARE, null);
    } else {
      setLayerType(LAYER_TYPE_HARDWARE, null);
    }
  }
}
