package mil.darpa.mesa.sdk.android.widgets.drawer;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.example.animationtest.R;
import com.example.animationtest.R.id;
import com.example.animationtest.R.layout;


public class Handle extends FrameLayout  {
    private Context mContext;
    SlidingDrawer mSlide = (SlidingDrawer) findViewById(R.id.slider);
    

    public Handle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) mContext
                .getSystemService(infService);
        li.inflate(R.layout.handle_layout, this, true);
    }

    public Handle(Context context)
    {
        super(context);
    }
    
    public Handle(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
    }
    
    
}
