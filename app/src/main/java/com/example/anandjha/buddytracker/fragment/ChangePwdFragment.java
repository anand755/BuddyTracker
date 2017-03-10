package com.example.anandjha.buddytracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.firebase.client.Firebase;

/**
 * Created by anandjha on 08/03/17.
 */

public class ChangePwdFragment extends Fragment {

    private EditText mEtCurrentPwd, mEtNewPwd, mEtConfirmPwd;
    private Button mBtChangePwd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_pwd_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

        mBtChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    Constant.CurrentUser.setPassword(mEtNewPwd.getText().toString());
                    Constant.FIREBASE_REF.child("buddy").child(Constant.CurrentUser.getMobile()).setValue(Constant.CurrentUser);
                    Toast.makeText(getActivity(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    private void initializeViews(View view) {
        mEtCurrentPwd = (EditText) view.findViewById(R.id.et_current_pwd);
        mEtNewPwd = (EditText) view.findViewById(R.id.et_new_pwd);
        mEtConfirmPwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
        mBtChangePwd = (Button) view.findViewById(R.id.btn_change_pwd);

    }

    private boolean validate() {
        boolean status = false;
        String regexPass = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (mEtCurrentPwd.getText().toString().isEmpty()) {
            mEtCurrentPwd.setError("Please enter Current password");
        } else if (!mEtCurrentPwd.getText().toString().equals(Constant.CurrentUser.getPassword())) {
            mEtCurrentPwd.setError("Current password doesn't match");
        } else if (mEtNewPwd.getText().toString().isEmpty()) {
            mEtNewPwd.setError("Please enter New password");
        } else if (!mEtNewPwd.getText().toString().matches(regexPass)) {
            mEtNewPwd.setError("Password Policy: \nAt least 8 chars\n" +
                    "Contains at least one digit\n" +
                    "Contains at least one lower alpha char and one upper alpha char\n" +
                    "Contains at least one char within a set of special chars (@#%$^ etc.)\n" +
                    "Does not contain space, tab, etc.");
        } else if (!mEtConfirmPwd.getText().toString().equals(mEtNewPwd.getText().toString())) {
            mEtConfirmPwd.setError("Password doesn't match");
        } else status = true;
        return status;

    }

}
