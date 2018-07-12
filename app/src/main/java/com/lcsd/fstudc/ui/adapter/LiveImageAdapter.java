package com.lcsd.fstudc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcsd.fstudc.R;
import com.lcsd.fstudc.ui.moudle.LiveImage;
import com.lcsd.fstudc.utils.Utils;
import com.lcsd.fstudc.view.RoundImageView;

import java.util.List;

/**
 * Created by jie on 2018/5/28.
 */
public class LiveImageAdapter extends BaseListAdapter<LiveImage> {
    private Context mContext;

    public LiveImageAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_live_list, null);
            holder.image = convertView.findViewById(R.id.riv_fm);
            holder.tv_title=convertView.findViewById(R.id.tv_title);
            holder.tv_time=convertView.findViewById(R.id.tv_time);
            holder.iv_sh=convertView.findViewById(R.id.iv_sh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mList.get(position).getThumb()).fitCenter().into(holder.image);
        holder.tv_title.setText(mList.get(position).getTitle());
        holder.tv_time.setText(Utils.timeStampDate2(mList.get(position).getDateline()));
        if(mList.get(position).getStatus()!=null&&mList.get(position).getStatus().equals("1")){
            holder.iv_sh.setImageResource(R.mipmap.img_wsh);
        }else {
            holder.iv_sh.setImageResource(R.mipmap.img_ysh);
        }
        return convertView;
    }

    class ViewHolder {
        RoundImageView image;
        TextView tv_title,tv_time;
        ImageView iv_sh;
    }
}
