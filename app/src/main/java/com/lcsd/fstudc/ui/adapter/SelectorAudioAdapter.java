package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.AudioInfo;
import com.lcsd.fstudc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/13.
 */
public class SelectorAudioAdapter extends BaseAdapter {
    private Context mContext;
    private List<AudioInfo> list;
    private LayoutInflater layoutInflater;
    private int mSelectorPosition = -1;
    private int duration;

    public SelectorAudioAdapter(Context mContext, List<AudioInfo> list, int duration) {
        this.mContext = mContext;
        this.list = list;
        this.duration = duration;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<AudioInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodle holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_select_audio, parent, false);
            holder = new ViewHodle(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodle) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).getDisplayName());
        holder.tv_path.setText(list.get(position).getPath());
        if (list.get(position).getDuration() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            holder.tv_druation.setText(formatter.format(list.get(position).getDuration()));
        }

        holder.tv_date.setText(Utils.timeStampDate(list.get(position).getDate()));

        if (position == mSelectorPosition) {
            holder.iv_select.setBackground(mContext.getResources().
                    getDrawable(R.drawable.img_select_yes));
        } else if (position != mSelectorPosition && list.get(position).getDuration() >= duration) {
            holder.iv_select.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.img_select_no));
        } else if (position != mSelectorPosition && list.get(position).getDuration() < duration) {
            holder.iv_select.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.img_select_defult));
        }
        return convertView;
    }

    /**
     * 设置选中哪一个
     *
     * @param position
     */
    public void setSelectorPosition(int position) {
        if (mSelectorPosition == position) {
            mSelectorPosition = -1;
        } else {
            mSelectorPosition = position;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取被选中的position
     *
     * @return
     */
    public int getSelectorPosition() {
        return mSelectorPosition;
    }

    class ViewHodle {
        @BindView(R.id.item_iv_select)
        ImageView iv_select;
        @BindView(R.id.item_tv_audio_name)
        TextView tv_name;
        @BindView(R.id.item_tv_audio_path)
        TextView tv_path;
        @BindView(R.id.item_tv_audio_date)
        TextView tv_date;
        @BindView(R.id.item_tv_audio_druation)
        TextView tv_druation;

        public ViewHodle(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
