package com.accessibility.photolabeller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
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
        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        
        // draw borders
        p.setColor(Color.rgb(192, 192, 192));
        p.setStrokeWidth(7);
        _canvas.drawRect(0, 7, _width, _height / 3 - 7, p);
        _canvas.drawRect(0, _height / 3 + 7, _width, _height * 2 / 3 - 7, p);
        _canvas.drawRect(0, _height * 2 / 3 + 7, _width, _height - 7, p);
        p.setStrokeWidth(0);
        
        // draw gradient rectangles
        LinearGradient gradient = new LinearGradient(7, 14, _width - 7, _height / 3 - 14, Color.RED, Color.rgb(155, 0, 0), Shader.TileMode.MIRROR);
        p.setShader(gradient);
        _canvas.drawRect(7, 14, _width - 7, _height / 3 - 14, p);
        gradient = new LinearGradient(7, _height / 3 + 14, _width - 7, _height * 2 / 3 - 14, Color.BLUE, Color.rgb(0, 0, 110), Shader.TileMode.MIRROR);
        p.setShader(gradient);
        _canvas.drawRect(7, _height / 3 + 14, _width - 7, _height * 2 / 3 - 14, p);
        gradient = new LinearGradient(7, _height * 2 / 3 + 14, _width - 7, _height - 14, Color.MAGENTA, Color.rgb(78, 0, 78), Shader.TileMode.MIRROR);
        p.setShader(gradient);
        _canvas.drawRect(7, _height * 2 / 3 + 14, _width - 7, _height - 14, p);
    	
        // draw texts
		_paint.setStyle(Paint.Style.FILL);
		_paint.setAntiAlias(true);
		_paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size));
		
		Rect rectangle = new Rect();
		_paint.getTextBounds("Capture", 0, 7, rectangle);
		float textHeight = rectangle.centerY();
		float startPositionX = (_width) / 2;

		_paint.setTextAlign(Paint.Align.CENTER);
		_canvas.drawText("Capture", startPositionX, (_height / 3) / 2 - textHeight, _paint);
		_canvas.drawText("Browse", startPositionX, (_height / 2) - textHeight, _paint);
		_canvas.drawText("Options", startPositionX, _height - (_height / 3) / 2 - textHeight, _paint);
		
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

			if ((y < height / 3) && (mFocusedButton != Button.CAPTURE)) {
					mFocusedButton = Button.CAPTURE;
					mRowListener.onRowOver();
			} else if ((y > height / 3 && y < height * 2 / 3) && (mFocusedButton != Button.BROWSE)) {
					mFocusedButton = Button.BROWSE;
					mRowListener.onRowOver();
			} else if ((y > height * 2 / 3) && (mFocusedButton != Button.OPTIONS)) {
					mFocusedButton = Button.OPTIONS;
					mRowListener.onRowOver();
			} else {
				// if none of the above, DO NOTHING
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
