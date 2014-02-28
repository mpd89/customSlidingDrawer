package mil.darpa.mesa.sdk.android.widgets.drawer;

import java.util.ArrayList;
import java.util.List;

import com.example.animationtest.R;
import com.example.animationtest.R.id;
import com.example.animationtest.R.layout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;

/** @author michael.dempsey */
public class SlidingDrawer extends FrameLayout implements
        android.view.GestureDetector.OnGestureListener
{
    private List<Float> mStopPointList = new ArrayList<Float>();
    private List<Float> mDragDecisionsList = new ArrayList<Float>();
    private int mDrawerPosition;
    private int mSlideHeight;
    private int mLastStop;
    private float mDefaultHiddenStop;
    private Float mCustomStopPoints[] = null;
    private Float mCustomDragDecisionPoints[] = null;
    private float mCurrentPosition;
    private int mHandleWidth;
    private int mFlingThreshold;
    private int mSlideWidth;
    private float mTouchPosition;
    boolean scrollInProgress = false;
    boolean flingInProgress = false;
    boolean firstTouch = false;
    private Context mContext;
    private GestureDetector mDetector;
    private SlidingAdapter mAdapter;
    private ListEntry list_data[];
    private SlidingDrawer mSlide = null;
    private Handle mHandle;
    private ImageView mImage;
    float oldRawX = 0;
    float OFF_SET = 8;

    public SlidingDrawer(Context context)
    {
        super(context);
        init(context);
    }

    public SlidingDrawer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public SlidingDrawer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        mDrawerPosition = 0;
        mContext = context;
        mDetector = new GestureDetector(context, this);
        firstTouch = true;
        mFlingThreshold = 300;

        // Inflate Sliding Drawer
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) mContext
                .getSystemService(infService);
        li.inflate(R.layout.sliding_drawer_layout, this, true);

        // Instantiate drawer elements
        mSlide = (SlidingDrawer) findViewById(R.id.slider);
        mHandle = (Handle) findViewById(R.id.drawerHandle);
        mImage = (ImageView) findViewById(R.id.tapButton);

        // Retrieves screen width and handle width
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mSlideWidth = size.x;
        mSlideHeight = size.y;
        android.view.ViewGroup.LayoutParams layoutParams = mHandle
                .getLayoutParams();
        mHandleWidth = layoutParams.width;
        Log.i("touch2", "mHandleWidth is " + mHandleWidth);
        Log.i("touch2", "mSlide Height is" + mSlideHeight);
        // Sets Default Stop Points, consisting of 3 stop points at 20%,50%, and 80% of the screen size
        // All stop and decision points are held in two array lists
        mDefaultHiddenStop = -(mSlideWidth - mHandleWidth);
        mStopPointList.add(mDefaultHiddenStop);
        mStopPointList.add(calculateStopPointFromPercentage(.20f));
        mStopPointList.add(calculateStopPointFromPercentage(.50f));
        mStopPointList.add(calculateStopPointFromPercentage(.80f));

        mLastStop = 3;

        // sets drag decisions for the default stop points at 50% of the distance between them
        for (int i = 0; i < 3; i++)
        {
            mDragDecisionsList.add(calculateDragDecisionFromPercentage(
                    mStopPointList.get(i), mStopPointList.get(i + 1), .50f));
        }

    }

    // Stop point calculator determines position of the stop based on the screen width and percentage of a float value
    private float calculateStopPointFromPercentage(float percentage)
    {
        float stopPoint = mSlideWidth * percentage;
        stopPoint = -(mSlideWidth - stopPoint);
        return stopPoint;
    }

    // Drag decision calculator determines the distance between the two stop points times the percentage of the float value
    // and returns the drag decision point between them
    private float calculateDragDecisionFromPercentage(float firstPoint,
            float secondPoint, float dragPercent)
    {
        float dragDecisionPoint = (firstPoint - secondPoint) * dragPercent;
        dragDecisionPoint = firstPoint - dragDecisionPoint;
        return dragDecisionPoint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        float newRawX = me.getRawX();

        boolean returnValue = false;
        // If the touchEvent is handled by the gesture recognizer it returns
        // true and the touch is consumed
        if (mDetector.onTouchEvent(me))
            return true;

        if (firstTouch)
        {
            mSlide.requestLayout();
            firstTouch = false;
        }

        do
        {

            if (flingInProgress)
            {
                flingInProgress = false;
                returnValue = true;
                break;
            }

            /*
             * If a drag motion is detected from the onScroll gesture recognizer the dragging animations are used to handle the event. When an
             * ACTION_MOVE occurs the drawer is animated according to the movement. The positions for the animations are calculated from the drawers
             * position minus the handle. The position of the touch event is subtracted from the drawerMinusHandleWidth value to provide the new x
             * value for the animation
             */

            /**************** On Drag *******************************/
            if (scrollInProgress)
            {

                final int mDrawerWidthMinusHandle = -(mSlide.getWidth() - (mHandle
                        .getWidth() / 2));
                Log.i("fix", "mDrawerwidthMinusHandle "
                        + mDrawerWidthMinusHandle);
                if (me.getActionMasked() == MotionEvent.ACTION_MOVE)
                {

                    mTouchPosition = me.getX();
                    mCurrentPosition = mSlide.getX();

                    // Animating from the drawer without the handle eliminates the jumping motion from drag animations

                    // float offset = (mTouchPosition) + mDrawerWidthMinusHandle;
                    float offset = mTouchPosition + mDrawerWidthMinusHandle;

                    Log.i("fix", "raw x is " + me.getRawX());
                    Log.i("banzai", "mTouchPosition is " + mTouchPosition);
                    float translationX = mCurrentPosition + offset;
                    // float translationX = mSlideWidth - Math.abs(offset);
                    if (translationX < -(mSlide.getWidth() - mHandle.getWidth()))
                    {
                        Log.i("fix", "if in drag hit ");
                        returnValue = false;
                        break;
                    }
                    float rawX = me.getRawX();
                    if (mTouchPosition < 1100)
                    {

                        Log.i("banzai", "rawX is " + rawX);
                        float x = (1156 + mCurrentPosition);
                        float y = x - rawX;
                        if (oldRawX != 0)
                        {
                            if (oldRawX < newRawX)
                            {
                                rawX += y + 8;
                            }
                            else
                                rawX += y - OFF_SET;
                        }
                        Log.i("banzai", "y is " + OFF_SET);
                    }
                    // animate the drawer with the dragging motion

                    mSlide.setTranslationX(-(Math.abs(mDrawerWidthMinusHandle) - rawX));
                    returnValue = false;
                    oldRawX = newRawX;
                    break;
                }

                // When the user stops dragging the sliding drawer snaps to the closest stop point depending
                // on the decisionPoints that were set.
                else if (me.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    scrollInProgress = false;
                    mCurrentPosition = mSlide.getX();

                    // ensures that the handle is always exposed
                    if (mCurrentPosition <= mDrawerWidthMinusHandle)
                    {
                        mDrawerPosition = 0;
                        ObjectAnimator animateToStop0 = ObjectAnimator.ofFloat(
                                mSlide, "x", mSlide.getX(), mStopPointList
                                        .get(0));
                        animateToStop0.start();

                    }
                    // Handles a drag beyond the last stop
                    else if (mCurrentPosition >= mStopPointList.get(mLastStop))
                    {
                        ObjectAnimator animateToLastStop = ObjectAnimator
                                .ofFloat(mSlide, "x", mSlide.getX(),
                                        mStopPointList.get(mLastStop));
                        animateToLastStop.start();
                    }

                    // Handles a drag that occurs before the first dragDecision Point
                    else if ((mCurrentPosition > mDrawerWidthMinusHandle)
                            && (mCurrentPosition <= mDragDecisionsList.get(0)))
                    {
                        mDrawerPosition = 0;
                        ObjectAnimator animateDrag = ObjectAnimator.ofFloat(
                                mSlide, "x", mSlide.getX(), mStopPointList
                                        .get(0));
                        animateDrag.start();
                    }

                    else
                    {

                        int index = 1;
                        for (Float stopPoint : mStopPointList.subList(1,
                                mStopPointList.size()))
                        {
                            if (((mCurrentPosition <= stopPoint) && (mCurrentPosition > mDragDecisionsList
                                    .get(index - 1)))
                                    || ((mCurrentPosition >= stopPoint) && (mCurrentPosition <= mDragDecisionsList
                                            .get(index))))
                            {
                                mDrawerPosition = index;
                                ObjectAnimator animateDrag = ObjectAnimator
                                        .ofFloat(mSlide, "x", mSlide.getX(),
                                                mStopPointList.get(index));
                                animateDrag.start();

                            }
                            index++;
                        }

                    }
                    returnValue = false;
                    break;
                }// end ACTION UP
            }// end on Drag

            returnValue = true;
            break;
        } while (true);

        return returnValue;
    }

    // If the imageButton is tapped the animations are designed to move to the next stopPoint. If the drawer is fully extended then it returns back to
    // its starting position when tapped.
    /***************** On Tap ********************************/
    @Override
    public boolean onSingleTapUp(MotionEvent me)
    {
        boolean returnValue = false;
        int offset = (mSlide.getWidth() - mImage.getWidth());

        // If the touch event is within the handle imageButton the tap animations are performed
        if ((me.getY() >= mImage.getTop()) && (me.getY() <= mImage.getBottom())
                && (me.getX() <= mSlide.getWidth()) && (me.getX() >= offset))
        {
            Log.i("touch", "touch on slider hit");
            // If the drawer is in the last stop position, animate back to the first position
            if (mDrawerPosition == mLastStop)
            {
                ObjectAnimator animateTapBack = ObjectAnimator.ofFloat(mSlide,
                        "x", mSlide.getX(), mStopPointList.get(0));
                animateTapBack.start();
                mDrawerPosition = 0;
            }
            // Otherwise animate forward to the next stop point
            else
            {
                ObjectAnimator animateTapForward = ObjectAnimator.ofFloat(
                        mSlide, "x", mSlide.getX(), mStopPointList
                                .get(mDrawerPosition + 1));
                mDrawerPosition = mDrawerPosition + 1;
                animateTapForward.start();
            }
            flingInProgress = false;
            returnValue = true;
        }
        return returnValue;
    }

    /**************************** On Fling **********************************/
    // On fling animations
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {

        Log.i("fix", "motion event e2 is " + e2.getX());
        boolean returnValue = false;
        // Fling velocity sensitivity
        if (Math.abs(velocityX) > mFlingThreshold)
        {
            flingInProgress = true;

            // If A fling occurs beyond the last stop point the Drawer will snap to the last stop point
            if (mSlide.getX() > mStopPointList.get(mLastStop))
            {
                ObjectAnimator animateToLastStop = ObjectAnimator.ofFloat(
                        mSlide, "x", mSlide.getX(), mStopPointList
                                .get(mLastStop));
                animateToLastStop.start();
                mDrawerPosition = mLastStop;
            }
            // A fling from the starting position will go to the outermost stop point
            else if (mDrawerPosition == 0)
            {
                if (e1.getRawX() < e2.getRawX()) // Fling right
                {
                    mDrawerPosition = mLastStop;
                    ObjectAnimator animateToLastStop = ObjectAnimator.ofFloat(
                            mSlide, "x", mSlide.getX(), mStopPointList
                                    .get(mLastStop));
                    animateToLastStop.start();
                }
                else
                // fling left
                {
                    mDrawerPosition = 0;
                    ObjectAnimator animateToStop0 = ObjectAnimator.ofFloat(
                            mSlide, "x", mSlide.getX(), mStopPointList.get(0));
                    animateToStop0.start();
                }
            }
            else
            {
                if (e1.getRawX() < e2.getRawX()) // fling right
                {
                    ObjectAnimator animateFlingToLastStop = ObjectAnimator
                            .ofFloat(mSlide, "x", mSlide.getX(), mStopPointList
                                    .get(mLastStop));
                    mDrawerPosition = mLastStop;
                    animateFlingToLastStop.start();
                }
                else
                // fling left
                {
                    ObjectAnimator animateFlingToStop0 = ObjectAnimator
                            .ofFloat(mSlide, "x", mSlide.getX(), mStopPointList
                                    .get(0));
                    mDrawerPosition = 0;
                    animateFlingToStop0.start();
                }
            }
            returnValue = true;
        }
        return returnValue;
    }

    /** Set Handle Image Button
     * 
     * @param imageId - Allows the user to set the image for the handle of the drawer to a provided image resource id.
     * @return -void */

    public void setHandleImageButton(int imageId)
    {
        mImage.setImageResource(imageId);

    }

    /** Set Image Button Listener
     * 
     * @param textDescription - The string text description of the list item that the user is setting a listener for
     * @param listener - The new onClick listener that will be set to the imageButton
     * @return - false if the text description provided could not be found in the list - true if the listener was successfully applied to the
     * imageButton */

    public boolean setImageButtonListener(String textDescription,
            View.OnClickListener listener)
    {
        boolean returnValue = false;
        for (int i = 0; i < list_data.length; i++)
        {
            if (textDescription.equals(list_data[i].title))
            {
                list_data[i].listener = listener;
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }

    /** SetFlingVelocity - allows the user to customize the response sensitivity for fling gestures
     * 
     * @param minimumVelocity */
    public void setFlingThreshold(int minimumVelocity)
    {
        mFlingThreshold = minimumVelocity;
    }

    /** Set Animation Stop And Drag Points
     * 
     * @param Float [] stopPoints - The setAnimationStopPoints method takes an array of float values to customize the stopPoints of the sliding drawer
     * animations. The float values must be greater than zero and less than or equal to one. The value of each float is used as a percentage of the
     * screen that the user wants the sliding drawer to stop at. For example, if the user wants one of the stop points to take up 20 percent of the
     * screen then the float equivalent would be .20f.
     * 
     * @param Float [] dragPercentages - Custom drag decision points are calculated by calling dragDecisionCalculator which subtracts the width
     * between the two stopPoints and multiplies that value by the float values provided in the drag percentages array.
     * @return - void */

    public void setAnimationStopPoints(Float[] stopPoints)
    {
        // Set Animation Stop points
        mCustomStopPoints = stopPoints;
        mStopPointList.removeAll(mStopPointList);
        if (mCustomStopPoints[0] != 0) // The user has requested a custom starting position other than the default
        {
            if (mCustomStopPoints[0] < 0f || mCustomStopPoints[0] > 1.00f)
            {
                Log.e("error",
                        "In setAnimationStopPoints: custom stop point at 0 is greater than 1 or less than0. Default stop 0 has been set");
                mStopPointList.add(mDefaultHiddenStop);
            }
            mStopPointList
                    .add(calculateStopPointFromPercentage(mCustomStopPoints[0]));
        }
        else
            mStopPointList.add(mDefaultHiddenStop);

        // input validation
        for (int i = 1; i < mCustomStopPoints.length; i++)
        {
            if (mCustomStopPoints[i] > 1.00f || mCustomStopPoints[i] < 0f)
            {
                Log.e("error",
                        "In setAnimationStopPoints: custom stop point at index "
                                + i
                                + " is either less than 0 or greater than 1. Default value of .50f has been set");
                mCustomStopPoints[i] = .50f;
            }
            mStopPointList.add(i,
                    calculateStopPointFromPercentage(mCustomStopPoints[i]));
        }

        // Handles the case where the user has set custom stops without setting custom drag decision points
        mDragDecisionsList.removeAll(mDragDecisionsList);
        for (int i = 0; i < (mStopPointList.size() - 1); i++)
        {
            mDragDecisionsList.add(i, calculateDragDecisionFromPercentage(
                    mStopPointList.get(i), mStopPointList.get(i + 1), .50f));
        }

        mLastStop = mCustomStopPoints.length - 1; // used for calculating animations
    }

    public void setDragDecisionPoints(Float[] dragPercentages)
    {

        // Set Custom drag decision points
        mCustomDragDecisionPoints = dragPercentages;
        mDragDecisionsList.removeAll(mDragDecisionsList);

        int index = 0;
        for (float f : mCustomDragDecisionPoints)
        {

            if (f > 1.00f || f <= 0f)
            {
                Log.e("error",
                        "In setDragDecisionPoints: the float drag percentage at "
                                + index
                                + " is either less <=0 or > 1. Default decision percentage of .50f is used instead");
                f = .50f;
            }
            mDragDecisionsList.add(calculateDragDecisionFromPercentage(
                    mStopPointList.get(index), mStopPointList.get(index + 1),
                    mCustomDragDecisionPoints[index]));
            index++;
        }
    }

    /** Set Handle Image Position
     * 
     * @param float verticalPercentage - The float percentage value that sets the vertical position of the handle image relative to the sliding
     * drawers height. - The positioning of the image calculated is set relative to the bottom of the image. For example a float value of .25 will set
     * the bottom of the image at 25% of the screen. To align the image at the top or bottom the float values are 0 and 1.00 respectively. Any values
     * greater than 1 or less than 0 will not affect the screen position and the default position will be used. */
    public void setHandleImagePosition(float positionOfImagePercentage,
            final boolean stretchToFit, final float percentageOfDrawerOnScreen)
    {
        final float percentage = positionOfImagePercentage;

        // input validation
        if (positionOfImagePercentage < 0f || positionOfImagePercentage > 1.00f
                || percentageOfDrawerOnScreen < 0f
                || percentageOfDrawerOnScreen > 1.00f)
        {
            Log.e("error",
                    "In setHandleImagePosition: Percentage entered is either less than 0 or greater than 1. Default value of .50f has been set");
            positionOfImagePercentage = .50f;
        }

        mSlide.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        float drawerHeightBasedOnPercentage = (mSlide
                                .getHeight() * (1 - percentageOfDrawerOnScreen));
                        drawerHeightBasedOnPercentage = mSlide.getHeight()
                                - drawerHeightBasedOnPercentage;
                        if (stretchToFit)
                        {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                            mImage.setAdjustViewBounds(true);
                            Log.i("touch", "image width is "
                                    + mImage.getWidth());
                            mImage.setScaleType(ScaleType.FIT_XY);
                            //ViewGroup.LayoutParams params = mImage
                             //       .getLayoutParams();
                            
                            params.height = (int) drawerHeightBasedOnPercentage;
                            mImage.setLayoutParams(params);
                            mSlide.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);

                        }
                        else
                        {

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        
                           // params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                           // params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                       
                            
                         Log.i("banzai","height is " +mImage.getHeight());
                            int screenHeight = (int) drawerHeightBasedOnPercentage;
                            Log.i("touch", "screen height is " + screenHeight);
                            int imageHeight = Math.abs(mImage.getTop()
                                    - mImage.getBottom());
                            Log.i("touch", "image height is " + imageHeight);

                            int center = imageHeight / 2;
                            int upperBound = screenHeight - center;
                            int lowerBound = center;
                            int screenArea = upperBound - lowerBound;

                            Log.i("touch", "screen area is  " + screenArea);
                            float verticalPosition = percentage * screenArea;
                            Log.i("touch", "center is  " + center
                                    + " upperBound is " + upperBound
                                    + " lowerbound is " + lowerBound
                                    + " vertical Position is "
                                    + verticalPosition);
                            int imageTop = (int) (verticalPosition + lowerBound - center);
                            Log.i("touch", "image top is " + imageTop);

                             mImage.setTop(50);
                             mImage.setBottom(mImage.getTop()+imageHeight);
                            //mImage.setTranslationY(imageTop);
                             Log.i("banzai","image top is  " + mImage.getTop());
                             Log.i("banzai","image bottom is " + mImage.getBottom());
                             
                             params.topMargin = imageTop;
                             params.bottomMargin= imageTop+imageHeight;
                             
                               mImage.setLayoutParams(params);
                             
                        }
                        mSlide.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /** Set Drawer List
     * 
     * @param imageResource [] -An array of imageResource id’s which are used as the image buttons for the list view
     * @param text [] - an array of text describing the buttons */
    public void setDrawerList(final int imageResource[], final String text[])
    {
        // input validation
        if (imageResource.length != text.length)
        {
            Log.e("error",
                    "In setDrawerList, the imageResource[] and text[] are of different size");
        }
        list_data = new ListEntry[imageResource.length];
        for (int i = 0; i < imageResource.length; i++)
        {
            list_data[i] = new ListEntry(imageResource[i], text[i], null);
        }

        mAdapter = new SlidingAdapter(mContext, R.layout.list_row, list_data);
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(mAdapter);

    }

    public void setContainerView(View v)
    {
        ViewGroup layout = (ViewGroup) findViewById(R.id.container);
        layout.setBackgroundResource(R.drawable.surf);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // ImageView imageView = (ImageView) findViewById(viewId);
        v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        layout.addView(v);

    }

    @Override
    public boolean onDown(MotionEvent arg0)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0)
    {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
            float distanceX, float distanceY)
    {

        scrollInProgress = true;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0)
    {

    }

    @Override
    public void setPressed(boolean pressed)
    {

    }

}