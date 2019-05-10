package com.white.progressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author White
 * Date 2017/4/23
 * Time 14:21
 */

public class KevenProgressView1 extends ProgressBar {

  private static final String TAG = KevenProgressView1.class.getSimpleName();

  @IntDef({TOP, CENTRE, BOTTOM})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Position {

  }

  public static final int TOP = 1;
  public static final int CENTRE = 0;
  public static final int BOTTOM = -1;

  private static final String STATE = "state";
  private static final String NORMAL_BAR_SIZE = "normal_bar_size";
  private static final String NORMAL_BAR_COLOR = "normal_bar_color";
  private static final String REACH_BAR_SIZE = "reach_bar_size";
  private static final String REACH_BAR_COLOR = "reach_bar_color";
  private static final String TEXT_COLOR = "text_color";
  private static final String TEXT_SIZE = "text_size";
  private static final String TEXT_SUFFIX = "text_suffix";
  private static final String TEXT_PREFIX = "text_prefix";
  private static final String TEXT_OFFSET = "text_offset";
  private static final String TEXT_POSITION = "text_position";
  private static final String TEXT_VISIBLE = "text_visible";
  private static final String TEXT_SKEW_X = "text_skew_x";

  private int mNormalBarSize = Utils.dp2px(getContext(), 2);
  private int mNormalBarColor = Color.parseColor("#FFD3D6DA");
  private int mReachBarSize = Utils.dp2px(getContext(), 2);
  private int mReachBarColor = Color.parseColor("#108ee9");
  private int mTextSize = Utils.sp2px(getContext(), 14);
  private int mTextColor = Color.parseColor("#108ee9");
  private int mTextOffset = Utils.dp2px(getContext(), 6);
  private int mProgressPosition = CENTRE;
  private boolean mTextVisible = true;
  private float mTextSkewX;
  private String mTextPrefix = "";
  private String mTextSuffix = "%";

  private Paint mTextPaint;
  private Paint mNormalPaint;
  private Paint mReachPaint;

//  范围 -180~ 180
  private float degree = 0;
//  float degree = -50;
  /**
   * 经过测量后得到的需要绘制的总宽度
   */
  private int mDrawWidth;

  public KevenProgressView1(Context context) {
    this(context, null);
  }

  public KevenProgressView1(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public KevenProgressView1(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    obtainAttributes(attrs);
    // 初始化画笔
    initPaint();
  }

  protected void initPaint() {
    mTextPaint = new Paint();
    mTextPaint.setColor(mTextColor);
    mTextPaint.setStyle(Paint.Style.FILL);
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setTextSkewX(mTextSkewX);
    mTextPaint.setAntiAlias(true); // 抗锯齿

//    PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8}, 5);
    mNormalPaint = new Paint();
    mNormalPaint.setColor(mNormalBarColor);
    mNormalPaint.setStyle(Paint.Style.FILL);
    mNormalPaint.setAntiAlias(true);
    mNormalPaint.setStrokeWidth(mNormalBarSize);
//    mNormalPaint.setPathEffect(effects);

    mReachPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mReachPaint.setColor(mReachBarColor);
//    mReachPaint.setStyle(Paint.Style.FILL);
    mReachPaint.setStyle(Paint.Style.STROKE);
    mReachPaint.setAntiAlias(true);

//    mReachPaint.setPathEffect(effects);
    mReachPaint.setPathEffect(new DashPathEffect(new float[]{6, 10, 50, 10}, 0));
//    mReachPaint.setPathEffect(new DashPathEffect(new float[]{6, 2}, 1));
    mReachPaint.setStrokeWidth(mReachBarSize);
  }

  /**
   * 获取自定义属性值
   */
  protected void obtainAttributes(AttributeSet attrs) {
    TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.KevenProgressView);
    mNormalBarSize = (int) ta.getDimension(R.styleable.KevenProgressView_progressNormalSize,
        mNormalBarSize);
    mNormalBarColor =
        ta.getColor(R.styleable.KevenProgressView_progressNormalColor, mNormalBarColor);

    mReachBarSize =
        (int) ta.getDimension(R.styleable.KevenProgressView_progressReachSize, mReachBarSize);
    mReachBarColor =
        ta.getColor(R.styleable.KevenProgressView_progressReachColor, mReachBarColor);

    mTextSize =
        (int) ta.getDimension(R.styleable.KevenProgressView_progressTextSize, mTextSize);
    degree =
         ta.getFloat(R.styleable.KevenProgressView_progressDegree, degree);
    mTextColor = ta.getColor(R.styleable.KevenProgressView_progressTextColor, mTextColor);
    mTextSkewX = ta.getDimension(R.styleable.KevenProgressView_progressTextSkewX, 0);
    if (ta.hasValue(R.styleable.KevenProgressView_progressTextSuffix)) {
      mTextSuffix = ta.getString(R.styleable.KevenProgressView_progressTextSuffix);
    }
    if (ta.hasValue(R.styleable.KevenProgressView_progressTextPrefix)) {
      mTextPrefix = ta.getString(R.styleable.KevenProgressView_progressTextPrefix);
    }
    mTextOffset =
        (int) ta.getDimension(R.styleable.KevenProgressView_progressTextOffset, mTextOffset);

    mProgressPosition =
        ta.getInt(R.styleable.KevenProgressView_progressTextPosition, mProgressPosition);

    mTextVisible =
        ta.getBoolean(R.styleable.KevenProgressView_progressTextVisible, mTextVisible);
    ta.recycle();
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int textHeight = (int) (mTextPaint.descent() - mTextPaint.ascent());
    int heightSize = Math.max(Math.max(mNormalBarSize, mReachBarSize), Math.abs(textHeight * 2))
        + getPaddingTop()
        + getPaddingBottom();
    heightSize = resolveSize(heightSize, heightMeasureSpec);
    setMeasuredDimension(widthSize, heightSize);

    System.out.println("=====widthSize==="+widthSize+"    "+textHeight+"   "+heightSize );
    // 实际绘制宽度
//    mDrawWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
//    mDrawWidth = getMeasuredHeight() - getPaddingLeft() - getPaddingRight();


//    double pingf = Math.pow(getMeasuredHeight(),2)+Math.pow(getMeasuredWidth(),2);
////    mDrawWidth = (int)Math.abs((Math.sqrt(pingf)*Math.sin(Math.toRadians(degree)))) ;//- getPaddingLeft() - getPaddingRight();
//    mDrawWidth = (int)Math.abs((Math.sqrt(pingf))) ;//- getPaddingLeft() - getPaddingRight();

    double heit= Math.abs(getMeasuredHeight()/Math.sin(Math.toRadians(degree)));
    double weit= Math.abs(getMeasuredWidth()/Math.cos(Math.toRadians(degree)));

    mDrawWidth = (int)Math.min(heit,weit)- getPaddingLeft() - getPaddingRight();

    System.out.println("=====mDrawWidth==="+mDrawWidth+"   "+getMeasuredHeight()+"  "+getMeasuredWidth() );
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {

    canvas.save();

   if(degree<0&&degree>-90){
      int hei = getHeight();
      int wid = getWidth();
      double heit= Math.abs(hei/Math.sin(Math.toRadians(degree)));
      double weit= Math.abs(wid/Math.cos(Math.toRadians(degree)));
      if (weit>=heit){
      }else{
        hei= (int)Math.abs((wid*(float)Math.tan(Math.toRadians(degree))));
      }
      canvas.rotate(degree);
      canvas.translate(hei*(float)Math.sin(Math.toRadians(degree)), hei*(float)Math.cos(Math.toRadians(degree)));

   }else  if(degree<=-90&&degree>=-180) {
     canvas.rotate(degree);
     canvas.translate(-mDrawWidth, 0);

     if (degree ==-90){
       canvas.translate(0, getWidth() / 2);
     }
     if (degree ==-180){
       canvas.translate(0, -getHeight() / 2);
     }
   }

    if(degree<=90&&degree>=0) {
      canvas.rotate(degree);
    }else if(degree<=180&&degree>90) {


     float tempdegree = 180-degree;

      int hei = getHeight();
      int wid = getWidth();
      double heit= Math.abs(hei/Math.sin(Math.toRadians(tempdegree)));
      double weit= Math.abs(wid/Math.cos(Math.toRadians(tempdegree)));
      if (weit>=heit){
      }else{
        hei= (int)Math.abs((wid*(float)Math.tan(Math.toRadians(tempdegree))));
      }
      canvas.rotate(degree);


      canvas.translate(((-hei*(float)Math.cos(Math.toRadians(tempdegree)))/(float)Math.tan(Math.toRadians(tempdegree))), -hei*(float)Math.cos(Math.toRadians(tempdegree)));


//      canvas.rotate(degree);
//      canvas.translate(-mDrawWidth, 0);
    }



//    canvas.rotate(degree);
//    canvas.translate(-(getHeight()), 0);

//    canvas.translate((int)(mDrawWidth*Math.sin(Math.toRadians(degree))), (int)(mDrawWidth*Math.cos(Math.toRadians(degree))));
    drawHorizontalProgressView(canvas);
    canvas.restore();

  }

  private void drawHorizontalProgressView(Canvas canvas) {
//    canvas.translate(getPaddingLeft(), getHeight() / 2);




    boolean needDrawUnReachArea = true; //是否需要绘制未到达进度
    float textWidth = 0;
    String text = mTextPrefix + getProgress() + mTextSuffix;
    if (mTextVisible) {
      textWidth = mTextPaint.measureText(text);
    } else {
      mTextOffset = 0;
    }
    float textHeight = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
    float radio = getProgress() * 1.0f / getMax();
    float progressPosX = (int) (mDrawWidth - textWidth) * (radio);

    if (progressPosX + textWidth >= mDrawWidth) {
      progressPosX = mDrawWidth - textWidth;
      needDrawUnReachArea = false;
    }

    // 绘制已到达进度
//    float endX = progressPosX - mTextOffset / 2;


    switch (mProgressPosition) {
      case BOTTOM: //BOTTOM
      case TOP: // TOP
        float a = textWidth;
        int b = mTextOffset / 2;
        float endX = progressPosX + (a+b+b)*radio-b;
        if (endX > 0) {
          canvas.drawLine(0, 0, endX, 0, mReachPaint);
        }
        // 绘制未到达进度
        if (needDrawUnReachArea) {
//      float start = progressPosX + mTextOffset / 2 + textWidth;
          float start = endX;
          canvas.drawLine(start, 0, mDrawWidth, 0, mNormalPaint);
        }

        break;
      default: // CENTER



//        mReachPaint.setPathEffect(new DashPathEffect(new float[]{20f,10f,5f}, 0));
        // 绘制已到达进度
        endX = progressPosX - mTextOffset / 2;
        if (endX > 0) {
          Path mPath = new Path();
          mPath.moveTo(0, 0);
          mPath.lineTo(endX, 0);
          canvas.drawPath(mPath, mReachPaint);
//          canvas.drawPath(0, 0, endX, 0, mReachPaint);
//          canvas.drawLine(0, 0, endX, 0, mReachPaint);
        }

        // 绘制未到达进度
        if (needDrawUnReachArea) {
          float start = progressPosX + mTextOffset / 2 + textWidth;
          canvas.drawLine(start, 0, mDrawWidth, 0, mNormalPaint);
        }

        break;
    }

    // 绘制字体
    if (!mTextVisible) {
      return;
    }


    if (degree<-90&& degree>=-180){
      canvas.save();
//      float textX= textWidth.

      canvas.rotate(180,progressPosX+textWidth/2,-textHeight/2-mTextOffset/2 );

    }

    if (degree<180&& degree>90){
      canvas.save();
//      float textX= textWidth.

      canvas.rotate(180,progressPosX+textWidth/2,-textHeight/2-mTextOffset/2 );

    }

    switch (mProgressPosition) {
      case BOTTOM: //BOTTOM
        canvas.drawText(text, progressPosX, -textHeight * 2 + mTextOffset, mTextPaint);
        break;
      case TOP: // TOP
        canvas.drawText(text, progressPosX, 0 - mTextOffset, mTextPaint);
        break;
      default: // CENTER
        canvas.drawText(text, progressPosX, -textHeight, mTextPaint);
        break;
    }
    if (degree<-90&& degree>=-180) {
      canvas.restore();
    }
    if (degree<180&& degree>90) {
      canvas.restore();
    }
  }

  public int getNormalBarSize() {
    return mNormalBarSize;
  }

  public void setNormalBarSize(int normalBarSize) {
    mNormalBarSize = Utils.dp2px(getContext(), normalBarSize);
    invalidate();
  }

  public int getNormalBarColor() {
    return mNormalBarColor;
  }

  public void setNormalBarColor(int normalBarColor) {
    mNormalBarColor = normalBarColor;
    invalidate();
  }

  public int getReachBarSize() {
    return mReachBarSize;
  }

  public void setReachBarSize(int reachBarSize) {
    mReachBarSize = Utils.dp2px(getContext(), reachBarSize);
    invalidate();
  }

  public int getReachBarColor() {
    return mReachBarColor;
  }

  public void setReachBarColor(int reachBarColor) {
    mReachBarColor = reachBarColor;
    invalidate();
  }

  public int getTextSize() {
    return mTextSize;
  }

  public void setTextSize(int textSize) {
    mTextSize = Utils.sp2px(getContext(), textSize);
    invalidate();
  }

  public int getTextColor() {
    return mTextColor;
  }

  public void setTextColor(int textColor) {
    mTextColor = textColor;
    invalidate();
  }

  public int getTextOffset() {
    return mTextOffset;
  }

  public void setTextOffset(int textOffset) {
    mTextOffset = Utils.dp2px(getContext(), textOffset);
    invalidate();
  }

  @Position
  public int getProgressPosition() {
    return mProgressPosition;
  }

  public void setProgressPosition(@Position int progressPosition) {
    if (progressPosition > 1 || progressPosition < -1) {
      mProgressPosition = 0;
    } else {
      mProgressPosition = progressPosition;
    }
    invalidate();
  }

  public boolean isTextVisible() {
    return mTextVisible;
  }

  public void setTextVisible(boolean textVisible) {
    mTextVisible = textVisible;
    invalidate();
  }

  public float getTextSkewX() {
    return mTextSkewX;
  }

  public void setTextSkewX(float textSkewX) {
    mTextSkewX = textSkewX;
    invalidate();
  }

  public String getTextPrefix() {
    return mTextPrefix;
  }

  public void setTextPrefix(String textPrefix) {
    mTextPrefix = textPrefix;
    invalidate();
  }

  public String getTextSuffix() {
    return mTextSuffix;
  }

  public void setTextSuffix(String textSuffix) {
    mTextSuffix = textSuffix;
    invalidate();
  }

  public void runProgressAnim(long duration) {
    setProgressInTime(0, duration);
  }

  /**
   * @param progress 进度值
   * @param duration 动画播放时间
   */
  public void setProgressInTime(final int progress, final long duration) {
    setProgressInTime(progress, getProgress(), duration);
  }

  /**
   * @param startProgress 起始进度
   * @param progress 进度值
   * @param duration 动画播放时间
   */
  public void setProgressInTime(int startProgress, final int progress, final long duration) {
    ValueAnimator valueAnimator = ValueAnimator.ofInt(startProgress, progress);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

      @Override
      public void onAnimationUpdate(ValueAnimator animator) {
        //获得当前动画的进度值，整型，1-100之间
        int currentValue = (Integer) animator.getAnimatedValue();
        setProgress(currentValue);
      }
    });
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    valueAnimator.setInterpolator(interpolator);
    valueAnimator.setDuration(duration);
    valueAnimator.start();
  }

  @Override
  public Parcelable onSaveInstanceState() {
    final Bundle bundle = new Bundle();
    bundle.putParcelable(STATE, super.onSaveInstanceState());
    // 保存text信息
    bundle.putInt(TEXT_COLOR, getTextColor());
    bundle.putInt(TEXT_SIZE, getTextSize());
    bundle.putInt(TEXT_OFFSET, getTextOffset());
    bundle.putInt(TEXT_POSITION, getProgressPosition());
    bundle.putFloat(TEXT_SKEW_X, getTextSkewX());
    bundle.putBoolean(TEXT_VISIBLE, isTextVisible());
    bundle.putString(TEXT_SUFFIX, getTextSuffix());
    bundle.putString(TEXT_PREFIX, getTextPrefix());
    // 保存已到达进度信息
    bundle.putInt(REACH_BAR_COLOR, getReachBarColor());
    bundle.putInt(REACH_BAR_SIZE, getReachBarSize());

    // 保存未到达进度信息
    bundle.putInt(NORMAL_BAR_COLOR, getNormalBarColor());
    bundle.putInt(NORMAL_BAR_SIZE, getNormalBarSize());
    return bundle;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      final Bundle bundle = (Bundle) state;

      mTextColor = bundle.getInt(TEXT_COLOR);
      mTextSize = bundle.getInt(TEXT_SIZE);
      mTextOffset = bundle.getInt(TEXT_OFFSET);
      mProgressPosition = bundle.getInt(TEXT_POSITION);
      mTextSkewX = bundle.getFloat(TEXT_SKEW_X);
      mTextVisible = bundle.getBoolean(TEXT_VISIBLE);
      mTextSuffix = bundle.getString(TEXT_SUFFIX);
      mTextPrefix = bundle.getString(TEXT_PREFIX);

      mReachBarColor = bundle.getInt(REACH_BAR_COLOR);
      mReachBarSize = bundle.getInt(REACH_BAR_SIZE);
      mNormalBarColor = bundle.getInt(NORMAL_BAR_COLOR);
      mNormalBarSize = bundle.getInt(NORMAL_BAR_SIZE);

      initPaint();
      super.onRestoreInstanceState(bundle.getParcelable(STATE));
      return;
    }
    super.onRestoreInstanceState(state);
  }

  @Override
  public void invalidate() {
    initPaint();
    super.invalidate();
  }
}
