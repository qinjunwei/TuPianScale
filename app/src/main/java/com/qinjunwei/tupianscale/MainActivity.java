package com.qinjunwei.tupianscale;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //设置手指个数
    final int NONE = 0;
    final int DROG = 1;
    final int ZOOM = 2;
    int mode = NONE;
    //
    Matrix oldMatrix = new Matrix();
    Matrix newMatrix = new Matrix();
    private ImageView img;

    //第一个手指按下的点
    PointF startPoint = new PointF();
    //两个手指按下的中心点
    PointF midPoint = new PointF();
    //初始两个手指按下的触摸点的距离
    float oriDis = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找控件
        img = (ImageView)findViewById(R.id.imag);
        //设置Ontouch事件
        img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView)v;

                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        //第一个手指按下事件
                        oldMatrix.set(view.getImageMatrix());
                        newMatrix.set(oldMatrix);
                        startPoint.set(event.getX(),event.getY());
                        mode = DROG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oriDis = distance(event);//获得两点间的就离
                        if(oriDis>10f){
                            midPoint = centre(event);
                            newMatrix.set(oldMatrix);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //判断是否为一个手指
                        if(mode == DROG){
                            oldMatrix.set(newMatrix);
                            oldMatrix.postTranslate(event.getX()-startPoint.x,event.getY()-startPoint.y);
                        }else if(mode == ZOOM){
                            //两手指滑动后的距离
                            float newDist = distance(event);
                            if(newDist>10f){
                                oldMatrix.set(newMatrix);
                                float scale = newDist/oriDis;
                                oldMatrix.postScale(scale,scale,midPoint.x,midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(oldMatrix);
                return true;
            }
        });
    }

    //计算两点之间的距离
    public float distance(MotionEvent event){
        float x = event.getX(0)-event.getX(1);
        float y = event.getY(0)-event.getY(1);
        return (float) Math.sqrt(x*x+y*y);
    }
    //计算两点中心店
    public PointF centre(MotionEvent event){
        float x = event.getX(0)+event.getX(1);
        float y = event.getY(0)+event.getY(1);
        return new PointF(x/2,y/2);
    }
}
