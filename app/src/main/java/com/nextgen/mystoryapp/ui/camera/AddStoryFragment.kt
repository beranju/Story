package com.nextgen.mystoryapp.ui.camera

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.common.util.ResponseWrapper
import com.nextgen.mystoryapp.databinding.FragmentAddStoryBinding
import com.nextgen.mystoryapp.infra.utils.createCustomTempFile
import com.nextgen.mystoryapp.infra.utils.reduceFileImage
import com.nextgen.mystoryapp.infra.utils.uriToFile
import com.nextgen.mystoryapp.ui.common.extention.gone
import com.nextgen.mystoryapp.ui.common.extention.showAlertDialog
import com.nextgen.mystoryapp.ui.common.extention.showToast
import com.nextgen.mystoryapp.ui.common.extention.visible
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@WithFragmentBindings
@AndroidEntryPoint
class AddStoryFragment : Fragment() {
    private lateinit var binding: FragmentAddStoryBinding
    private lateinit var currentPhotoPath: String
    var getFile: File? = null
    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val getCameraImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivPreview.setImageBitmap(result)
        }
    }

    private val getGaleryImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)

        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val requestPermissionLauncer = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                postStory()
            }
            permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                postStory()
            }

        }
    }

    private fun checkPermission(permissions: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            permissions
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGrandted()) {
                Toast.makeText(requireContext(),
                    getString(R.string.text_no_permission),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGrandted() = Companion.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionGrandted()) {
            ActivityCompat.requestPermissions(requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        binding.galery.setOnClickListener { openGalery() }
        binding.btnTakePic.setOnClickListener { startTakePic() }
        binding.btnUnggah.setOnClickListener {
            postStory()
        }

        observer()
    }

    private fun observer() {
        viewModel.mState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddStoryState.Init -> Unit
                is AddStoryState.IsLoading -> handleLoading(result.isLoading)
                is AddStoryState.ShowToast -> context?.showToast(result.message)
                is AddStoryState.SuccessAdd -> goToHome()
                is AddStoryState.ErrorAdd -> handleError(result.rawResponse)
            }
        }
    }

    private fun handleError(rawResponse: ResponseWrapper) {
        context?.showAlertDialog(rawResponse.message)
    }

    private fun goToHome() {
        findNavController().navigate(R.id.action_addStoryFragment_to_homeFragment)
    }

    private fun handleLoading(loading: Boolean) {
        binding.btnUnggah.isEnabled = !loading
        binding.btnTakePic.isEnabled = !loading
        binding.galery.isEnabled = !loading
        if (!loading) {
            binding.pbAddstori.progress = 0
            binding.pbAddstori.gone()
        } else {
            binding.pbAddstori.visible()
        }
    }

    private fun postStory() {
        if (getFile != null) {

            val file = reduceFileImage(getFile as File)
            val desc = binding.editText.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude.toFloat()
                        val lon = location.longitude.toFloat()
                        viewModel.addStory(imageMultipart, desc, lat, lon)
                    } else {
                        requireContext().showToast("Location is not found")
                    }
                }
            } else {
                requestPermissionLauncer.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.error_no_picture),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        getGaleryImage.launch(chooser)
    }

    private fun startTakePic() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(activity?.packageManager!!)

        createCustomTempFile(requireContext()).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.nextgen.mystoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            getCameraImage.launch(intent)
        }
    }

}