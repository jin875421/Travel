package glue502.software.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;

import glue502.software.R;


/**
 * Created by Administrator on 2017/9/4.
 * 自定义View.使用Canvas、Paint等来实现图片编辑功能（包括普通涂鸦、画圆、画矩形、画箭头、写字）
 */

public class EditImageView extends androidx.appcompat.widget.AppCompatImageView {

    private Paint paint;  //画笔
    private Bitmap originalBitmap;  //源图
    private Bitmap newBitmap;
    private Bitmap finalBitmap;  //最终保存时的图片
    private Bitmap finalOvalBitmap;
    private Bitmap finalRectBitmap;
    private Bitmap finalArrowBitmap;
    private Bitmap finalTextBitmap;

    private float clickX = 0;  //触摸时的x坐标
    private float clickY = 0;  //触摸时的y坐标
    private float startX = 0;  //每次绘制起点的x坐标
    private float startY = 0;  //每次绘制起点的y坐标

    private boolean isEdit = false;  //是否允许对图片进行编辑
    private boolean isMove = true;  //是否进行绘制
    private boolean isClear = false;  //是否进行清空
    private int drawType = 0;  //绘制类型：0---线条；1---圆；2---矩形；3---箭头；4---文字
    private String text;

    private int color = Color.RED;  //画笔颜色
    private float strokeWidth = 4.0f;  //涂鸦线条的宽度
    private int textSize = 18;  //字体大小
    private int textColor = getResources().getColor(R.color.blue);  //文字颜色

    private int mScreenWidth;
    private int mScreenHeight;

    private String filePath;


    public EditImageView(Context context) {
        super(context);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        //根据原位图的大小创建一个新位图，得到的位图是可变的
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        originalBitmap = BitmapFactory.decodeFile(filePath).copy(Bitmap.Config.ARGB_8888, true);
        //        new1Bitmap = Bitmap.createBitmap(originalBitmap);  //得到一个不可变位图
        finalBitmap = Bitmap.createScaledBitmap(originalBitmap, mScreenWidth, mScreenHeight, true);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (drawType) {
            case 0:
                canvas.drawBitmap(handWriting(), 0, 0, null);
                break;
            case 1:
                canvas.drawBitmap(drawOval(), 0, 0, null);
                break;
            case 2:
                canvas.drawBitmap(drawRect(), 0, 0, null);
                break;
            case 3:
                canvas.drawBitmap(drawArrow(), 0, 0, null);
                break;
            case 4:
                canvas.drawBitmap(writeText(), 0, 0, null);
                break;
        }
    }

    /**
     * 画线条
     * @return
     */
    public Bitmap handWriting() {
        if (isClear) {
            isClear = false;
            finalBitmap = newBitmap;
            return finalBitmap;
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(finalBitmap, mScreenWidth, mScreenHeight, true);
            Canvas canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStrokeWidth(strokeWidth);
            if (isMove) {  //当移动时绘制
                canvas.drawLine(startX, startY, clickX, clickY, paint);  //在画布上画线（通过起点、终点和画笔）
            }
            //起点需要不断变化，否则画出来的不是线而是面，因为起点一直不变
            startX = clickX;
            startY = clickY;
            finalBitmap = bitmap;
            return finalBitmap;
        }
    }

    /**
     * 画椭圆（包括圆，圆是一种特殊的椭圆）
     * @return
     */
    public Bitmap drawOval() {
        if (isClear) {
            isClear = false;
            finalBitmap = newBitmap;
            return finalBitmap;
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(finalBitmap, mScreenWidth, mScreenHeight, true);
            Canvas canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStrokeWidth(strokeWidth);
            if (isMove) {
                canvas.drawOval(new RectF(startX, startY, clickX, clickY), paint);
            }
            finalOvalBitmap = bitmap;
            return finalOvalBitmap;
        }
    }

    /**
     * 画矩形
     * @return
     */
    public Bitmap drawRect() {
        if (isClear) {
            isClear = false;
            finalBitmap = newBitmap;
            return finalBitmap;
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(finalBitmap, mScreenWidth, mScreenHeight, true);
            Canvas canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStrokeWidth(strokeWidth);
            if (isMove) {
                canvas.drawRect(startX, startY, clickX, clickY, paint);
            }
            finalRectBitmap = bitmap;
            return finalRectBitmap;
        }
    }

    /**
     * 画箭头
     */
    public Bitmap drawArrow() {
        if (isClear) {
            isClear = false;
            finalBitmap = newBitmap;
            return finalBitmap;
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(finalBitmap, mScreenWidth, mScreenHeight, true);
            Canvas canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStrokeWidth(strokeWidth);
            if (isMove) {
                drawArrow(startX, startY, clickX, clickY, canvas, paint);
            }
            finalArrowBitmap = bitmap;
            return finalArrowBitmap;
        }
    }

    public void drawArrow(float startX, float startY, float endX, float endY, Canvas canvas, Paint paint) {
        double H = 10;  //箭头高度
        double L = 6;  //底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H);  //箭头角度
        double arraow_len = Math.sqrt(L * L + H * H);  //箭头的长度
        double[] arrXY_1 = rotateVec(endX - startX, endY - startY, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(endX - startX, endY - startY, -awrad, true, arraow_len);
        double x_3 = endX - arrXY_1[0];  //(x3,y3)是第一端点
        double y_3 = endY - arrXY_1[1];
        double x_4 = endX - arrXY_2[0];  //(x4,y4)是第二端点
        double y_4 = endY - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        //画线
        canvas.drawLine(startX, startY, endX, endY, paint);
        Path triangle = new Path();
        triangle.moveTo(endX, endY);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint);
    }

    public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        //矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    /**
     * 在图片上写字
     * @param text
     */
    public void writeTextToBitmap(String text) {
        drawType = 4;
        this.text = text;
        invalidate();
    }

    public Bitmap writeText() {
        if (isClear) {
            isClear = false;
            finalBitmap = newBitmap;
            finalTextBitmap = finalBitmap;
            return finalBitmap;
        } else {
            Bitmap bitmap = Bitmap.createScaledBitmap(finalBitmap, mScreenWidth, mScreenHeight, true);
            Canvas canvas = new Canvas(bitmap);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize * getResources().getDisplayMetrics().density);
            textPaint.setAntiAlias(true);
            StaticLayout staticLayout = new StaticLayout(text, textPaint, mScreenWidth * 3 / 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.translate(clickX, clickY);
            staticLayout.draw(canvas);
            finalTextBitmap = bitmap;
            return finalTextBitmap;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当按下并不移动时，刷新并记下按下时的坐标点但不绘制
            startX = clickX;
            startY = clickY;
            isMove = false;
            invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //当移动时，进行绘制，从按下的坐标点起
            if (isEdit) {
                isMove = true;
            }
            invalidate();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isMove = false;
            switch (drawType) {
                case 1:
                    finalBitmap = finalOvalBitmap;
                    break;
                case 2:
                    finalBitmap = finalRectBitmap;
                    break;
                case 3:
                    finalBitmap = finalArrowBitmap;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置画的类型
     * @param drawType
     */
    public void setDrawType(int drawType) {
        if (this.drawType == 4) {
            finalBitmap = finalTextBitmap;
        }
        this.drawType = drawType;
    }

    /**
     * 设置是否允许对图片进行编辑
     * @param isEdit
     */
    public void setIdEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    /**
     * 清空
     */
    public void clear() {
        isClear = true;
        newBitmap = Bitmap.createScaledBitmap(originalBitmap, mScreenWidth, mScreenHeight, true);
        invalidate();
    }

    /**
     * 设置画笔颜色
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 设置画笔的宽度
     * @param strokeWidth
     */
    public void setStyle(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * 设置字体大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置字体颜色
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 保存图片
     */
    public void saveBitmap(Handler handler) {
        if (drawType == 4) {
            finalBitmap = finalTextBitmap;
        }
        if (!filePath.equals(Environment.getExternalStorageDirectory().getPath() + "/JCamera/image.jpeg")) {
            int bitmapWidth = originalBitmap.getWidth();
            int bitmapHeight = originalBitmap.getHeight();
            finalBitmap = Bitmap.createScaledBitmap(finalBitmap, bitmapWidth, bitmapHeight, true);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(Message.obtain());
        }
    }
}