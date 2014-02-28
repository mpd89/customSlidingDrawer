package mil.darpa.mesa.sdk.android.widgets.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageButton extends ImageView 
{

    public AspectRatioImageButton(Context context)
    {
        super(context);
    }
    public AspectRatioImageButton(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public AspectRatioImageButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
    }

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
   int measuredHeight = measureHeight(heightMeasureSpec);
   int measuredWidth = measureWidth(widthMeasureSpec);
   setMeasuredDimension(measuredHeight,measuredWidth);
}   
   private int measureHeight(int measureSpec) {
       int specMode = MeasureSpec.getMode(measureSpec);
       int specSize = MeasureSpec.getSize(measureSpec);
       int result = 500;
       if(specMode == MeasureSpec.AT_MOST)
       {
           
           result = specSize; 
           
       }
       else if (specMode == MeasureSpec.EXACTLY) {
           result = specSize;
       }
       return result;
   }
   
   private int measureWidth(int measureSpec){
       int specMode = MeasureSpec.getMode(measureSpec);
       int specSize = MeasureSpec.getSize(measureSpec);
       int result = 500;
       if(specMode==MeasureSpec.AT_MOST) {
           result=specSize;
       }
       else if (specMode==MeasureSpec.EXACTLY)
       {
           result = specSize;
       }
       return result;
   }
   
   
}

