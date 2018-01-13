package pfaion.vocabulearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import java.io.Serializable;

public class SettingsDialogFragment extends DialogFragment {

    public interface SettingsTransmitListener {
        public void onTransmit(Settings settings);
    }

    private SettingsTransmitListener cb;
    public SettingsDialogFragment() {}

    public static SettingsDialogFragment newInstance(SettingsTransmitListener cb) {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        fragment.setCb(cb);
        return fragment;
    }

    private void setCb(SettingsTransmitListener cb) {
        this.cb = cb;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.settings_dialog, null);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        
        view.findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Settings.AMOUNT_5;
                if(((RadioButton)view.findViewById(R.id.rb_amount_10)).isChecked()) amount = Settings.AMOUNT_10;
                if(((RadioButton)view.findViewById(R.id.rb_amount_20)).isChecked()) amount = Settings.AMOUNT_20;
                if(((RadioButton)view.findViewById(R.id.rb_amount_30)).isChecked()) amount = Settings.AMOUNT_30;
                if(((RadioButton)view.findViewById(R.id.rb_amount_all)).isChecked()) amount = Settings.AMOUNT_ALL;
                int side = Settings.SIDE_FRONT_FIRST;
                if(((RadioButton)view.findViewById(R.id.rb_side_back)).isChecked()) side = Settings.SIDE_BACK_FIRST;
                if(((RadioButton)view.findViewById(R.id.rb_side_mixed)).isChecked()) side = Settings.SIDE_MIXED;
                int order = Settings.ORDER_SMART;
                if(((RadioButton)view.findViewById(R.id.rb_order_random)).isChecked()) order = Settings.ORDER_RANDOM;
                if(((RadioButton)view.findViewById(R.id.rb_order_hard)).isChecked()) order = Settings.ORDER_HARD;
                if(((RadioButton)view.findViewById(R.id.rb_order_old)).isChecked()) order = Settings.ORDER_OLD;
                if(((RadioButton)view.findViewById(R.id.rb_order_new)).isChecked()) order = Settings.ORDER_NEW;
                Settings settings = new Settings(amount, side, order);
                cb.onTransmit(settings);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}