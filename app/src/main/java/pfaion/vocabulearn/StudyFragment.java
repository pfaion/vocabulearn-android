package pfaion.vocabulearn;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class StudyFragment extends Fragment {

    private OnStudyFragmentInteractionListener mListener;

    public StudyFragment() {}

    public static StudyFragment newInstance() {
        StudyFragment fragment = new StudyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);

//        View smart_study = view.findViewById(R.id.smart_study);
//        ((TextView)smart_study.findViewById(R.id.text_field)).setText("Smart training");
//        ((ImageView)smart_study.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
//
//        View old_study = view.findViewById(R.id.old_study);
//        ((TextView)old_study.findViewById(R.id.text_field)).setText("Long time no see");
//        ((ImageView)old_study.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_access_time_black_24dp);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStudyFragmentInteractionListener) {
            mListener = (OnStudyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnStudyFragmentInteractionListener {
        void onSmartStudy();
    }
}
