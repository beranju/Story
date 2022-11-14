package com.nextgen.mystoryapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.data.signup.remote.dto.SignupRequest
import com.nextgen.mystoryapp.databinding.FragmentRegisterBinding
import com.nextgen.mystoryapp.ui.common.extention.gone
import com.nextgen.mystoryapp.ui.common.extention.isEmail
import com.nextgen.mystoryapp.ui.common.extention.showAlertDialog
import com.nextgen.mystoryapp.ui.common.extention.visible
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@WithFragmentBindings
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register()
        observer()
        binding.btnToLogin.setOnClickListener {
            goToLogin()
        }
    }

    private fun observer() {
        viewModel.mState.observe(viewLifecycleOwner) { result ->
            handleState(result)
        }
    }

    private fun handleState(result: RegisterState) {
        when (result) {
            is RegisterState.Init -> Unit
            is RegisterState.IsLoading -> handleLoading(result.isLoading)
            is RegisterState.SuccessRegister -> goToLogin()
            is RegisterState.ErrorRegister -> handleError(result.rawResponse)
        }
    }

    private fun register() {
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validate(name, email, password)) {
                viewModel.signup(SignupRequest(name, email, password))
            }
        }
    }

    private fun handleError(rawResponse: ResponseWrapper) {
        context?.showAlertDialog(rawResponse.message)
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun handleLoading(loading: Boolean) {
        binding.btnSignup.isEnabled = !loading
        binding.btnToLogin.isEnabled = !loading
        binding.pbRegister.isIndeterminate = loading
        if (!loading) {
            binding.pbRegister.progress = 0
            binding.pbRegister.gone()
        } else {
            binding.pbRegister.visible()
        }
    }

    private fun validate(name: String, email: String, password: String): Boolean {
        resetAllError()
        if (name.isEmpty()) {
            setNameError(getString(R.string.error_name))
            return false
        }
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
        setNameError(null)
        setEmailError(null)
        setPasswordError(null)
    }

    private fun setPasswordError(e: String?) {
        binding.etPassword.error = e
    }

    private fun setEmailError(e: String?) {
        binding.edtEmail.error = e
    }

    private fun setNameError(e: String?) {
        binding.etName.error = e
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