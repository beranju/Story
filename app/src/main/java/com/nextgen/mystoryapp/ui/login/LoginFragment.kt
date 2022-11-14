package com.nextgen.mystoryapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.login.remote.dto.LoginRequest
import com.nextgen.mystoryapp.data.login.remote.dto.LoginResponse
import com.nextgen.mystoryapp.databinding.FragmentLoginBinding
import com.nextgen.mystoryapp.domain.login.entity.LoginEntity
import com.nextgen.mystoryapp.infra.utils.SharedPrefs
import com.nextgen.mystoryapp.ui.common.extention.isEmail
import com.nextgen.mystoryapp.ui.common.extention.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject

@WithFragmentBindings
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var prefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login()
        observe()
        toSignUp()
    }

    private fun toSignUp() {
        binding.btnRegister.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observe() {
        viewModel.mState.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: LoginState) {
        when (state) {
            is LoginState.IsLoading -> handleStateLoading(state.isLoading)
            is LoginState.Init -> Unit
            is LoginState.SuccessLogin -> handleStateSuccess(state.loginEntity)
            is LoginState.ErrorLogin -> handleStateError(state.rawResponse)
        }
    }

    private fun handleStateError(rawResponse: LoginResponse) {
        context?.showAlertDialog(rawResponse.message.toString())
    }

    private fun handleStateSuccess(loginEntity: LoginEntity) {
        prefs.saveToken(loginEntity.token)
        goToHome()
    }

    private fun goToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun handleStateLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.btnRegister.isEnabled = !loading
        binding.pbLogin.apply {
            isIndeterminate = loading
            if (!loading) {
                progress = 0
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
        }

    }

    private fun login() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validate(email, password)) {
                viewModel.login(LoginRequest(email, password))
            }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        resetAllError()
        if (!email.isEmail()) {
            setEmailError(getString(R.string.email_error))
            return false
        }
        if (password.length < 6) {
            setPasswordError(getString(R.string.password_error))
            return false
        }
        return true
    }

    private fun resetAllError() {
        setEmailError(null)
        setPasswordError(null)
    }

    private fun setPasswordError(s: String?) {
        binding.etPassword.error = s
    }

    private fun setEmailError(s: String?) {
        binding.etEmail.error = s
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}