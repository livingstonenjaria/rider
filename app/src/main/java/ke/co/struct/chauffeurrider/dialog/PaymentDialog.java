package ke.co.struct.chauffeurrider.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;

import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.adapters.PaymentListAdapter;

/**
 * Created by STRUCT on 3/14/2018.
 */

public class PaymentDialog extends DialogFragment {
    private HashMap items = new HashMap<Integer,String>();
    private ListView lstMain;

    public interface NoticeDialogListener {
        public void onListItemClick(DialogFragment dialog,String itemName);
    }
    NoticeDialogListener mListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.payment_options);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.payment_options, null);
        lstMain = (ListView)view.findViewById(R.id.lstPayments);

        final String[] options = { getResources().getString(R.string.corporate), getResources().getString(R.string.cash), getResources().getString(R.string.visa)};
        Integer [] drawableIds = {R.mipmap.ico0,R.mipmap.ico1,R.mipmap.card};
        PaymentListAdapter adapter = new PaymentListAdapter(getContext(), options, drawableIds);
        lstMain.setAdapter(adapter);
        lstMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemValue = options[i];
                Log.d("Dialog", itemValue);
                mListener.onListItemClick(PaymentDialog.this , itemValue);
            }
        });
        builder.setView(view);
        return builder.create();
    }

}
