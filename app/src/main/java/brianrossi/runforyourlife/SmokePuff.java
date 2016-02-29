package brianrossi.runforyourlife;

/**
 * Created by Brian on 2/26/2016.
 */
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class SmokePuff extends GameObject{
    public int r;
    public SmokePuff(int x, int y)
    {
        r = 5;
        super.x = x;
        super.y = y;
    }
    public void update()
    {
        x-=10;
    }
    public void draw(Canvas canvas)
    {
        Paint white = new Paint();
        Paint black = new Paint();
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.FILL);
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-r, y-r, r, black);
        canvas.drawCircle(x-r+2, y-r-2,r,white);
        canvas.drawCircle(x-r+4, y-r+1, r,black);
    }

}
