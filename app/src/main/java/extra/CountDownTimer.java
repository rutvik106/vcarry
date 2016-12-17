package extra;

import android.os.Handler;

import java.util.Date;

/**
 * Created by rutvik on 12/16/2016 at 10:09 PM.
 */

public class CountDownTimer implements Runnable
{

    final Handler handler;

    final Date tripDate;

    final OnCountDownTickListener onCountDownTickListener;

    public CountDownTimer(final Handler handler, final Date tripDate,
                          final OnCountDownTickListener onCountDownTickListener)
    {
        this.handler = handler;
        this.tripDate = tripDate;
        this.onCountDownTickListener = onCountDownTickListener;
        handler.postDelayed(this, 0);
    }

    @Override
    public void run()
    {
        handler.postDelayed(this, 1000);
        final Date currentDate = new Date();
        if (!currentDate.after(tripDate))
        {
            long diff = tripDate.getTime()
                    - currentDate.getTime();

            long days = diff / (24 * 60 * 60 * 1000);
            diff -= days * (24 * 60 * 60 * 1000);
            long hours = diff / (60 * 60 * 1000);
            diff -= hours * (60 * 60 * 1000);
            long minutes = diff / (60 * 1000);
            diff -= minutes * (60 * 1000);
            long seconds = diff / 1000;

            onCountDownTickListener.onCountDownTick(days,hours,minutes,seconds);
        } else
        {
            handler.removeCallbacks(this);
        }
    }

    public interface OnCountDownTickListener
    {
        void onCountDownTick(long days, long hours, long minutes, long seconds);
    }

}
