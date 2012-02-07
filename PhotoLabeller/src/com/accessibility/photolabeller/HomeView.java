package com.accessibility.photolabeller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
 
public class HomeView extends View {
 
    public enum Button {
        NOTHING(0),
        CAPTURE(1),
        BROWSE(2),
        OPTIONS(3);

        private int mValue;

        private Button(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static Button fromInt(int i) {
            for (Button s : values()) {
                if (s.getValue() == i) {
                    return s;
                }
            }
            return NOTHING;
        }
    }
    
    private Button mFocusedButton = Button.NOTHING;
    private Button mInitialPush = Button.NOTHING;
    
    private int _height;
    private int _width;
    private Bitmap _bitmap;
    private Canvas _canvas;
    private Paint _paint;
    private Point[] _firstHorizontalLine;
    private Point[] _secondHorizontalLine;
    
    private RowListener mRowListener;
    
    public interface RowListener {
        abstract void onRowOver();
        abstract void focusChanged();
    }
    
    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        requestFocus();
        
        _paint = new Paint();
        _paint.setColor(Color.WHITE);
        _paint.setStyle(Paint.Style.STROKE);
    }
     
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        _height = View.MeasureSpec.getSize(heightMeasureSpec);
        _width = View.MeasureSpec.getSize(widthMeasureSpec);
     
        setMeasuredDimension(_width, _height);
     
        _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
        _canvas = new Canvas(_bitmap);
     
        calculateLinePlacements();
        drawBoard();
    }

    private void calculateLinePlacements() {
        int splitHeight = _height / 3;
     
        _firstHorizontalLine = new Point[2];
        Point p1 = new Point(0, splitHeight);
        Point p2 = new Point(_width, splitHeight);
        _firstHorizontalLine[0] = p1;
        _firstHorizontalLine[1] = p2;
     
        _secondHorizontalLine = new Point[2];
        p1 = new Point(0, 2 * splitHeight);
        p2 = new Point(_width, 2 * splitHeight);
        _secondHorizontalLine[0] = p1;
        _secondHorizontalLine[1] = p2;
    }
    
    private void drawBoard() {
        _canvas.drawLine(_firstHorizontalLine[0].x, _firstHorizontalLine[0].y,
                _firstHorizontalLine[1].x, _firstHorizontalLine[1].y, _paint);
     
        _canvas.drawLine(_secondHorizontalLine[0].x,
                _secondHorizontalLine[0].y, _secondHorizontalLine[1].x,
                _secondHorizontalLine[1].y, _paint);
     
		_paint.setStyle(Paint.Style.FILL);
		_paint.setAntiAlias(true);
		_paint.setTextSize(60);
		float canvasWidth = _canvas.getWidth();
		float text1Width = _paint.measureText("Capture");
		float startPositionX = (canvasWidth - text1Width) / 2;

		_paint.setTextAlign(Paint.Align.LEFT);
		_canvas.translate(0, 80);
		_canvas.drawText("Capture", startPositionX, _height * 3 / 30, _paint);
		_canvas.drawText("Browse", startPositionX, _height * 12 / 30, _paint);
		_canvas.drawText("Options", startPositionX, _height * 22 / 30, _paint);
		
        invalidate();
    }
    
    public void setRowListener(RowListener rowListener) {
        mRowListener = rowListener;
    }
    
    public Button getFocusedButton() {
        return mFocusedButton;
    }

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(_bitmap, 0, 0, _paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {	
			int y = (int) event.getY();
			int height = this.getHeight();

			boolean repeated = false;

			if (y < height / 3) {
				if (mFocusedButton == Button.CAPTURE)
					repeated = true;
				else
					mFocusedButton = Button.CAPTURE;
			} else if (y > height / 3 && y < height * 2 / 3) {
				if (mFocusedButton == Button.BROWSE)
					repeated = true;
				else
					mFocusedButton = Button.BROWSE;
			} else if (y > height * 2 / 3) {
				if (mFocusedButton == Button.OPTIONS)
					repeated = true;
				else
					mFocusedButton = Button.OPTIONS;
			}

			if (mRowListener != null && !repeated) {
				mRowListener.onRowOver();
			}
			
			if (action == MotionEvent.ACTION_DOWN)
				mInitialPush = mFocusedButton;
			
			return true;
		} else if (action == MotionEvent.ACTION_UP) {
			if (mInitialPush != mFocusedButton)
				mRowListener.focusChanged();
			mFocusedButton = Button.NOTHING;
			mInitialPush = Button.NOTHING;
			return true;
		}

		return false;
	}
}
