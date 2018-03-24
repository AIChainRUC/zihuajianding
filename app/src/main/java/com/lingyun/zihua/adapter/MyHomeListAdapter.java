package com.lingyun.zihua.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingyun.zihua.R;

/**
 * 主页的ListView的Adapter
 */
public class MyHomeListAdapter extends BaseAdapter{
    private Context context;
    public MyHomeListAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView=View.inflate(context, R.layout.fragement_main_list_item,null);
            //holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        switch (position){
            case 0:
                holder.tv_name.setText("字画存链");
            break;
            case 1:
                holder.tv_name.setText("字画鉴定");
                break;
            case 2:
                holder.tv_name.setText("证书生成");
                break;
            default:
                holder.tv_name.setText("证书使用");
                break;
        }
        return convertView;
    }
    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
    }
}
