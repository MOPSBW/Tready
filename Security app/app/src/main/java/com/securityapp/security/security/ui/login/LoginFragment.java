package com.securityapp.security.security.ui.login;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * LoginFragment displays user with fields to login and remembering credentials, and ensures that
 * the user is authenticated before launching the HomeActivity
 */

//TODO: Add loading indicator and error messages as well (viewmodel)
public class LoginFragment extends Fragment implements Injectable {

    @BindView(R.id.txt_username) TextView username;
    @BindView(R.id.txt_password) TextView password;
    @BindView(R.id.btn_login) Button loginButton;
    @BindView(R.id.checkbox_remember) CheckBox rememberMe;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel mLoginViewModel;
    private boolean isUsernameFilled = false;
    private boolean isPasswordFilled = false;

    public LoginFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLoginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);

        setupUsername();

        setupPassword();

        mLoginViewModel.getUser().observe(this, user -> {
            if(user.isUserValid()){
                startActivity(new Intent(getActivity(), MainActivity.class));
            }else{
                Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                clearUserInputs();
            }
        });
    }

    private void setupUsername(){
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /**
             * Override to ensure login button is enabled only when field is filled out
             */
            @Override
            public void afterTextChanged(Editable s) {
                isUsernameFilled = s.length() > 0;
                evaluateButtonVisibility();
            }
        });
    }

    private void setupPassword(){
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /**
             * Override to ensure login button is enabled only when field is filled out
             */
            @Override
            public void afterTextChanged(Editable s) {
                isPasswordFilled = s.length() > 0;
                evaluateButtonVisibility();
            }
        });
    }

    /**
     * Enables and disables the login button based on
     * whether or not both the username and password field have been filled out
     */
    private void evaluateButtonVisibility(){
        if(isUsernameFilled && isPasswordFilled){
            loginButton.setEnabled(true);
        }else{
            loginButton.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_login)
    public void loginButtonClicked(){
        mLoginViewModel.loginUser(username.getText().toString(), password.getText().toString(), rememberMe.isChecked());
        hideKeyboard();
    }

    private void clearUserInputs(){
        username.setText(null);
        password.setText(null);
    }

    private void hideKeyboard(){
        if(getView() != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null){
                imm.hideSoftInputFromInputMethod(getView().getWindowToken(),0);
            }
        }
    }
}
