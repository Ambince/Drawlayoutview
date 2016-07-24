package com.example.amence_a.drawlayoutview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Amence_A on 2016/7/24.
 * 测量             摆放     绘制
 * measure   ->  layout  ->  draw
 * |           |          |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 * <p/>
 * View流程
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 * <p/>
 * ViewGroup流程
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */
public class SlideMenu extends ViewGroup {
    //按下的x坐标
    private float downX;
    //按下的y坐标
    private float downY;
    //移动坐标
    private float moveX;
    //显示主界面的状态
    private static final int MAIN_STATE = 0;
    //显示侧面板的状态
    private static final int MENU_STATE = 1;
    //当前模式
    private int currentState = MAIN_STATE;
    private Scroller scroller;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        //初始化滚动器，数值模拟器
        scroller = new Scroller(getContext());

    }

    /**
     * 测量并设置，所有子View的宽高
     *
     * @param widthMeasureSpec  当前控件的宽度测量规则
     * @param heightMeasureSpec 当前控件的高度测量规则
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //指定左面板的宽高
        View leftMenu = getChildAt(0);
        leftMenu.measure(leftMenu.getLayoutParams().width, heightMeasureSpec);

        //指定主面板的宽高
        View mainContent = getChildAt(1);
        mainContent.measure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * @param changed 当前控件的尺寸大小，位置，是否发生变化
     * @param l       当前控件的 左边据
     * @param t       当前控件 上边据
     * @param r       当前控件 右边距
     * @param b       当前控件 下边距
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //摆放内容
        View leftMenu = getChildAt(0);
        leftMenu.layout(-leftMenu.getMeasuredWidth(), 0, 0, b);
        //主面板
        getChildAt(1).layout(l, t, r, b);
    }


    /**
     * 处理触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取按下的X值
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                //将要发生的偏移量/变化量
                int scrollX = (int) (downX - moveX);

                //判断将要滚动到的位置，判断是否会超出，超出去了，不在ScrollBy
                //getScrollX() 获取滚动到的位置
                int newScrollPosition = getScrollX() + scrollX;
                if (newScrollPosition < -getChildAt(0).getMeasuredWidth()) {
                    //如果新的位置小于-240的时候直接滑动到左边边界
                    scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
                } else if (newScrollPosition > 0) {
                    //总的滑动距离如果大于零了，就不出现左边的抽屉
                    scrollTo(0, 0);
                } else {
                    //其他的情况如期滑动距离,scrollBy()原来的基础上移动，scrollTo直接到某个位置

                    scrollBy(scrollX, 0);
                }
                //为了不使直接移动的距离太大，一点点的移动，则将移动后的距离赋值给按下的距离
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                //根据当前滚动的位置和左面板的一半比较
                int leftCenter = (int) (-getChildAt(0).getMeasuredWidth() / 2.0f);
                if (getScrollX() < leftCenter) {
                    //打开，切换成菜单面板
                    currentState = MENU_STATE;
                    updateCurrentContent();
                } else {
                    //关闭，切换主面板
                    currentState = MAIN_STATE;
                    updateCurrentContent();
                }

                break;

        }


        //这个事件需要子view捕获
        return true;
    }

    /**
     * 根据当前的状态，执行， 关闭/开启 的动画
     */
    private void updateCurrentContent() {
        int startX = getScrollX();
        int dx = 0;
        //平滑滚动
        if (currentState == MENU_STATE) {
            dx = -getChildAt(0).getMeasuredWidth() - startX;
        } else {
            dx = 0 - startX;
        }
        // startX: 开始的x值
        // startY: 开始的y值
        // dx: 将要发生的水平变化量. 移动的x距离
        // dy: 将要发生的竖直变化量. 移动的y距离
        // duration : 数据模拟持续的时长
        int duration = Math.abs(dx *2);
        scroller.startScroll(startX,0,dx,0,duration);
        //重绘 导致 computeScroll执行
        invalidate();

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){
            // 直到duration事件以后, 结束
            //true 动画还没有结束
            //获取当前模拟器的数据，也就是要滚动的位置
            int currX = scroller.getCurrX();
            scrollTo(currX,0);
            // 重绘界面-> drawChild() -> computeScroll();循环
            invalidate();
        }
    }

    public void open(){
        currentState =MENU_STATE;
        updateCurrentContent();
    }
    public void close(){
        currentState = MAIN_STATE;
        updateCurrentContent();
    }
    public void switchState(){
        if(currentState ==MAIN_STATE){
            open();
        }else {
            close();
        }
    }
    public int getCurrentState(){
        return currentState;
    }

    /**
     * 拦截判断
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float xOffset = Math.abs(ev.getX()-downX);
                float yoffset = Math.abs(ev.getX()-downY);
                if(xOffset>yoffset &&xOffset>5){
                    // 水平方向超出一定距离时,才拦截
                    //拦截事件，不让父布局处理
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }














        return super.onInterceptTouchEvent(ev);
    }
}
