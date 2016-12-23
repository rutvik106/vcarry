package viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apimodels.TripsByDriverMail;
import extra.CountDownTimer;
import extra.Utils;
import io.fusionbit.vcarry.ActivityTripDetails;
import io.fusionbit.vcarry.Constants;
import io.fusionbit.vcarry.R;

/**
 * Created by rutvik on 11/27/2016 at 4:05 PM.
 */

public class VHSingleTrip extends RecyclerView.ViewHolder implements CountDownTimer.OnCountDownTickListener
{

    final Context context;

    TripsByDriverMail model;

    TextView tvTripFrom, tvTripTo, tvTripTime, tvCurrentTripStatus, tvTripTimeCountdown, tvTimeLeft;

    LinearLayout llContainer;

    final SimpleDateFormat sdf;

    final Handler handler;

    CountDownTimer countDownTimer;


    public VHSingleTrip(final Context context, View itemView)
    {
        super(itemView);
        this.context = context;

        handler = new Handler();

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        tvTripFrom = (TextView) itemView.findViewById(R.id.tv_tripFrom);
        tvTripTo = (TextView) itemView.findViewById(R.id.tv_tripTo);
        tvTripTime = (TextView) itemView.findViewById(R.id.tv_tripTime);
        tvCurrentTripStatus = (TextView) itemView.findViewById(R.id.tv_currentTripStatus);
        tvTripTimeCountdown = (TextView) itemView.findViewById(R.id.tv_tripTimeCountdown);
        tvTimeLeft = (TextView) itemView.findViewById(R.id.tv_timeLeft);

        llContainer = (LinearLayout) itemView.findViewById(R.id.ll_singleTripContainer);

        llContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(context, ActivityTripDetails.class);
                i.putExtra(Constants.INTENT_EXTRA_TRIP_ID, model.getTripId());
                context.startActivity(i);
            }
        });

    }

    public static VHSingleTrip create(final Context context, final ViewGroup parent)
    {
        return new VHSingleTrip(context, LayoutInflater.from(context)
                .inflate(R.layout.single_trip_view, parent, false));
    }

    public static void bind(final VHSingleTrip vh, final TripsByDriverMail model)
    {
        vh.model = model;
        vh.tvTripFrom.setText(model.getFromShippingLocation());
        vh.tvTripTo.setText(model.getToShippingLocation());
        vh.tvCurrentTripStatus.setText(model.getStatus());

        vh.tvTripTime.setText(Utils.convertDateToRequireFormat(model.getTripDatetime()));

        try
        {
            Date tripDate = vh.sdf.parse(model.getTripDatetime());
            final Date currentDate = new Date();
            if (!currentDate.after(tripDate))
            {
                vh.setupCountDown(tripDate);
                vh.tvTimeLeft.setVisibility(View.VISIBLE);
            } else
            {
                vh.removeCountDown();
                vh.tvTimeLeft.setVisibility(View.GONE);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        final int tripStatus = Integer.valueOf(model.getTripStatus());
        final int tripStatusStarted = Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED);
        final int tripStarted = Integer.valueOf(Constants.TRIP_STATUS_TRIP_STARTED);

        if (tripStatus < tripStatusStarted)
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#66bb6a"));
        } else if (tripStatus == tripStarted)
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#ffa726"));
        } else
        {
            vh.tvCurrentTripStatus.setTextColor(Color.parseColor("#666666"));
        }
    }


    public void setupCountDown(final Date tripDate)
    {
        if (countDownTimer == null)
        {
            countDownTimer = new CountDownTimer(handler, tripDate, this);
        }
    }

    public void removeCountDown()
    {
        if (countDownTimer != null)
        {
            handler.removeCallbacks(countDownTimer);
            countDownTimer = null;
        }
    }


    @Override
    public void onCountDownTick(long days, long hours, long minutes, long seconds)
    {
        final String d = days != 0 ? days + "d " : "";

        final String hr = days > 0 && hours == 0 ? hours + "hr " :
                (hours != 0 ? hours + "hr " : "");

        final String m = days > 0 && minutes == 0 ? minutes + "m " :
                (hours > 0 && minutes == 0 ? minutes + "m " :
                        (minutes != 0 ? minutes + "m " : ""));

        final String s = days > 0 && seconds == 0 ? seconds + "s " :
                (hours > 0 && seconds == 0 ? seconds + "s " :
                        (minutes > 0 && seconds == 0 ? seconds + "s " :
                                (seconds != 0 ? seconds + "s " : "")));

        tvTripTimeCountdown.setText(d + hr + m + s);

    }

}
