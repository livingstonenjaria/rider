package ke.co.struct.chauffeurrider.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.struct.chauffeurrider.R;

/**
 * Created by STRUCT on 3/14/2018.
 */

public class PaymentListAdapter extends ArrayAdapter<String> {


    private Context context;
    private String[] title;
    private Integer[] image;

    public PaymentListAdapter(@NonNull Context context, String[] title,  Integer[] image) {
        super(context, R.layout.payment_options, title);
        this.context = context;
        this.image = image;
        this.title = title;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.payment_item, null, true);
        TextView txtTitle =  rowView.findViewById(R.id.txtPaymentOption);
        ImageView imageView =  rowView.findViewById(R.id.imgPaymentOption);
        txtTitle.setText(title[position]);
        imageView.setImageResource(image[position]);
        return rowView;
    }
}