package glue502.software.utils.PostTextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class PostEditView extends androidx.appcompat.widget.AppCompatEditText {

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private Paint mPaint = new Paint();

    public PostEditView(Context context, AttributeSet set)
    {
        super(context,set);
    }

    public PostEditView(Context mContext)
    {
        // TODO Auto-generated constructor stub
        super(mContext);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        mLeft = getPaddingLeft();
        mTop = getPaddingTop();
        mRight = canvas.getWidth() - getPaddingRight();
        mBottom = canvas.getHeight() - getPaddingBottom();


        String str = this.getText().toString();
        mPaint.setColor(getCurrentTextColor());
        mPaint.setTextSize(getTextSize());
        //canvas.drawText(str, left, right, paint);
        //ShowTextMutiLineUnicode(canvas, mLeft, mTop, mRight, mBottom, str.toCharArray(), (int)getTextSize()+3);
        //mTop+24:上部留24像素的空隙,getTextSize()+3:行高就是字体的高度加3
        ShowTextMutiLineUnicode(canvas, mLeft, mTop+24, mRight, mBottom, str.toCharArray(), (int)getTextSize()+3);
    }

    void ShowOneLineUnicode(Canvas hdc,int x,int y,char[] buff,int index, int len)
    {
        hdc.drawText(buff, index, len, x, y, mPaint);
    }

    int GetOneWidthFast(char[] p, int index)
    {
        return (int)mPaint.measureText(p,index,1);
    }
    /*
    *********************************************************************
    * 功能  :获取一个单词(宽字符)
    * 参数  :入口:p-单词地址
                  width-显示宽度
             出口:返回单词字符个数
    * 返回值:单词长度
    * 注意  :无
    *********************************************************************
    */
    int GetOneWordW(char[] p,int index, int width)
    {
        int w = 0;
        int pos = index;
        int word_len = 0;

        while(true)
        {
            if(pos >= p.length || p[pos] <= 0x20 || p[pos] >= 0x80)
            {
                break;
            }
            //w += freetype2_GetUnicodeWidth(*p);
            w += GetOneWidthFast(p, pos);
            if(w > width)
            {
                break;
            }
            pos++;
        }
        word_len = pos - index;

        return w|(word_len<<16);
    }
    /*
     *********************************************************************
     * 功能  :显示多行文字(Unicode编码)
     * 参数  :hdc-DC;x,y-起始位置,xEnd,yEnd-结束位置,buff-字符,line_height-行高
     * 返回值:无
     * 注意  :无
     *********************************************************************
     */
    void ShowTextMutiLineUnicode(Canvas hdc,int x,int y,int xEnd,int yEnd, char[] buff,int line_height)
    {
        int j;
        char p[] = buff;
        int len;
        int xtemp;
        int index = 0;;
        //int with = 20;

        if(line_height<16)
        {
            line_height=16;
        }

        while(true)
        {
            xtemp=x;
            for(j=0;j<1024;)//认为一行最多不会超过1024个字符
            {
                if(index+j >= p.length)
                {
                    ShowOneLineUnicode(hdc,x,y,p,index,j);
                    return;
                }
                if(p[index+j]==0x0d || p[index+j]==0x0a )//回车
                {
                    j++;
                    if(p[index+j]==0x0a)
                    {
                        j++;
                    }
                    break;
                }
                else
                {
                    if(p[index+j] > 0x20 && p[index+j] < 0x80)//英文字符
                    {
                        int ref = GetOneWordW(p, index+j ,xEnd - xtemp);
                        xtemp += ref&0xffff;
                        len = ref>>16;
                        if(xtemp > xEnd)
                        {
                            if(j == 0)//一个单词超过一行
                            {
                                j += len;
                            }
                            break;
                        }
                        j += len;
                    }
                    else//其他
                    {
                        xtemp += GetOneWidthFast(p, index+j);
                        if(xtemp > xEnd)
                            break;
                        j++;
                    }
                }
            }
            if(y+2*line_height > yEnd)
            {
                char[] pnew = new char[j+3];

                System.arraycopy(p, index, pnew, 0, j);
                if(index+j < p.length)
                {
                    int i;
                    int dot_w = (int)mPaint.measureText(".",0,1);
                    for(i = 0; i <= 3; i++)
                    {
                        if((int)mPaint.measureText(p,index,j-i)+dot_w*3 < xEnd - x)
                        {
                            break;
                        }
                    }
                    j += 3-i;
                    if(j >= 3)
                    {
                        pnew[j-3] = '.';
                        pnew[j-2] = '.';
                        pnew[j-1] = '.';
                    }
                }
                ShowOneLineUnicode(hdc,x,y,pnew,0,j);
                break;
            }
            ShowOneLineUnicode(hdc,x,y,p,index,j);
            index+=j;
            y+=line_height;
        }
    }
}