package pfaion.vocabulearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import static pfaion.vocabulearn.Overview.TAG;

public class SettingsDialogFragment extends DialogFragment {


    private View.OnClickListener cb;
    public SettingsDialogFragment() {}

    public static SettingsDialogFragment newInstance(View.OnClickListener cb) {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        fragment.setCb(cb);
        return fragment;
    }

    private void setCb(View.OnClickListener cb) {
        this.cb = cb;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dialog, null);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        
        view.findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onClick(view);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}