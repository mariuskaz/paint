package my.android.paintbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PaintBook extends Activity {

	private GestureDetectorCompat mDetector;
	private static Toast toast = null;
	private Drawing	drawing;
	private int pageNo = 1;
    public static Bitmap bitmap = null;
    public static Paint paint;
    private Canvas canvas;
    private MaskFilter emboss;
    private MaskFilter blur;
    private int[] pens;
    private int pen;
    private int pmax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawing = new Drawing(this);
        setContentView(drawing);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        pens = new int[] { 10, 1, 4, 10 };
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        emboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },  0.4f, 6, 3.5f);
        blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);   //INNER, OUTER
        
        String page = "PAGE" + pageNo;
		Boolean saved = loadBitmap(page);
		if (saved) makeToast(getBaseContext(),R.drawable.emo_im_cool,"Tavo pieðinys",Toast.LENGTH_LONG).show();
    }

    public Boolean loadBitmap(String name) {		
    	Bitmap bmp = null;
    	FileInputStream fis;
        try {
        	fis = openFileInput(name);
        	bmp = BitmapFactory.decodeStream(fis);
        	fis.close();
        	bitmap =  Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
        	canvas = new Canvas(bitmap);
        	drawing.invalidate();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        	if (bitmap != null) bitmap.eraseColor(Color.WHITE);
        	drawing.invalidate();
        	return false;
        } catch (IOException e) {
        	e.printStackTrace();
        	return false;
        }
    	return true;
    }
	
    public boolean saveBitmap(String name) {
    	FileOutputStream fos;
    	try {
    		 fos = openFileOutput(name, Context.MODE_PRIVATE);
    		 bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    		 fos.close();  	   
    	   } catch (FileNotFoundException e) {
    		 e.printStackTrace();
    		 return false;
    	   } catch (IOException e) {
    		 e.printStackTrace();
    		 return false;
    	}
    	return true;
    }
    
    
    
    public class Drawing extends View {
    	
        private Path    mPath;
        private Paint   mBitmapPaint;

        public Drawing(Context c) {
            super(c);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }
        
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            pmax = h;
            if (w > h) pmax = w;
            if (bitmap == null) bitmap = Bitmap.createBitmap(pmax, pmax, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
        	canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, paint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            //undo[step] = Bitmap.createBitmap(bitmap);
        	mPath.reset();
        	mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
        	mPath.lineTo(mX, mY);
        	canvas.drawPath(mPath, paint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {   	
        	mDetector.onTouchEvent(event);
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                	touch_start(x, y);
                	invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                	touch_move(x, y);
                	invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                	touch_up();
                	invalidate();
                    break;
            }
            return true;
        }
        
    } // end of drawing view
    
    private static final int PREVIOUS_MENU_ID = Menu.FIRST ;
    private static final int SAVE_MENU_ID = Menu.FIRST + 1 ;
    private static final int NEXT_MENU_ID = Menu.FIRST + 2 ;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);    
        menu.add(0, PREVIOUS_MENU_ID, 0, "Previous").setIcon(R.drawable.ic_action_previous_item);
        menu.add(0, SAVE_MENU_ID, 0, "Save").setIcon(R.drawable.ic_action_save);
        menu.add(0, NEXT_MENU_ID, 0, "Next").setIcon(R.drawable.ic_action_next_item);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        paint.setXfermode(null);
        String page = "PAGE";
        switch (item.getItemId()) {
        	case NEXT_MENU_ID:
        		pageNo += 1;
                page += pageNo;
            	loadBitmap(page); 
        		makeToast(getBaseContext(), R.drawable.emo_im_cool, "Lapas " + pageNo, 5).show(); 
        		return true;
        	case PREVIOUS_MENU_ID:
        		if (pageNo > 1)	pageNo -= 1;
                page += pageNo;
            	loadBitmap(page); 
        		makeToast(getBaseContext(), R.drawable.emo_im_cool, "Lapas " + pageNo, 5).show(); 
        		return true;
        	case SAVE_MENU_ID:
            	String name = "PAGE" + pageNo;
        		boolean saved = saveBitmap(name);
        		if (saved) makeToast(getBaseContext(),R.drawable.emo_im_tongue_sticking_out,"Pieðinys iðsaugotas",Toast.LENGTH_LONG).show();
        		return true;

        }
        return super.onOptionsItemSelected(item);
    }
	
	public void setEraser(CharSequence title) {
		final SeekBar eraser = new SeekBar(this.getApplicationContext());
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle(title);
    	alert.setView(eraser);
    	alert.setPositiveButton("Trinti", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		int val = eraser.getProgress();
    		float size = (float) val;
      	  	paint.setStrokeWidth(size);
      	  	pens[0] = val;
    	  }
    	});
    	alert.show();
    	eraser.setProgress(pens[0]);
	}

	public void colorChanged(int color, int size) {
		pens[pen] = size;
		paint.setColor(color);
		paint.setStrokeWidth(size);
	}
	


	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {   
		
        @Override
        public boolean onDown(MotionEvent event) { 
            return true;
        }
        
        @Override
        public void onLongPress(MotionEvent event) {
        	Intent ring = new Intent(getBaseContext(), ActionsRing.class);
	    	startActivity(ring);
        }
                
    }

	
	public static Toast makeToast(Context context, int imageResId, CharSequence text, int length) {
	    String margin = "  ";	    
	    toast = Toast.makeText(context, text + margin, length);
	    toast.setGravity(Gravity.TOP, 0, 10);	    
	    View rootView = toast.getView();
	    LinearLayout linearLayout = null;
	    View messageTextView = null;
	    if (rootView instanceof LinearLayout) {
	        linearLayout = (LinearLayout) rootView;
	        if (linearLayout.getChildCount() == 1) {
	            View child = linearLayout.getChildAt(0);
	            if (child instanceof TextView) {
	                messageTextView = (TextView) child;
	            }
	        }
	    }
	    if (linearLayout == null || messageTextView == null) {
	        return toast;
	    }
	    ViewGroup.LayoutParams textParams = messageTextView.getLayoutParams();
	    ((LinearLayout.LayoutParams) textParams).gravity = Gravity.CENTER_VERTICAL;
	    float density = context.getResources().getDisplayMetrics().density;
	    int imageSize = (int) (density * 25 + 0.5f);
	    int imageMargin = (int) (density * 15 + 0.5f);
	    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
	    imageParams.setMargins(0, 0, imageMargin, 0);
	    imageParams.gravity = Gravity.CENTER_VERTICAL;
	    ImageView imageView = new ImageView(context);
	    imageView.setImageResource(imageResId);
	    imageView.setLayoutParams(imageParams);
	    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
	    linearLayout.addView(imageView, 0);
	    return toast;
	}
    
} // end of main class
