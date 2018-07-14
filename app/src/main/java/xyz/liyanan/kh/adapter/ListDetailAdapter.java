package xyz.liyanan.kh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import xyz.liyanan.kh.R;
import xyz.liyanan.kh.bean.ListDetail;

public class ListDetailAdapter extends ArrayAdapter<ListDetail> {
    private int resourceId;

    public ListDetailAdapter(Context context, int textViewResourceId, List<ListDetail> object) {
        super(context, textViewResourceId, object);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ListDetail listDetail = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        //ImageView detailed_icon = view.findViewById(R.id.list_detail_icon);
        TextView detailed_um = view.findViewById(R.id.list_detail_um);
        TextView detailed_haveum = view.findViewById(R.id.list_detail_haveum);
        TextView detailed_datetime = view.findViewById(R.id.list_detail_datetime);
        detailed_um.setText(listDetail.getUsername1());
        detailed_datetime.setText(listDetail.getDatetime());
        detailed_haveum.setText(listDetail.getUsername2());
        return view;
    }
}
