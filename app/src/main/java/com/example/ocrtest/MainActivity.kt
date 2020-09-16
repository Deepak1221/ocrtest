package com.example.ocrtest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hitanshudhawan.firebasemlkitexample.textrecognition.TextRecognitionAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.content_text_recognition.*

class MainActivity : AppCompatActivity() {
    lateinit var  viewModel:MainViewModel
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(findViewById(R.id.bottom_sheet)!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(MainRepo() )
        ).get(MainViewModel::class.java)

        observeChanges()
    }

    private inline fun observeChanges() {
        viewModel.ocrTxtLivedata.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showProgress()
                }
                Status.SUCCESS -> {
                    hideProgress()
                  handleSuccessResult(it.data!!)
                }
                Status.ERROR -> {
                    hideProgress()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
     }

    private inline fun handleSuccessResult(data: ProcessedData) {
        data.txtList?.let {
           initRecyclerView(it)
        }
        data.image?.let {
            text_recognition_image_view.setImageBitmap(it)
        }
        hideProgress()
 //        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initViews() {

         bottom_sheet_button.setOnClickListener {
            CropImage.activity().start(this)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val imageUri = result.uri
                //analyzeImage(MediaStore.Images.Media.getBitmap(contentResolver, imageUri))
                processImageToText(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "There was some error : ${result.error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processImageToText(imageUri: Uri?) {
        text_recognition_image_view.setImageBitmap(null)
        viewModel.analyseImage(MediaStore.Images.Media.getBitmap(contentResolver, imageUri))

    }

    private fun showProgress() {
        bottom_sheet_button_image.visibility = View.GONE
        bottom_sheet_button_progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        bottom_sheet_button_image.visibility = View.VISIBLE
        bottom_sheet_button_progress.visibility = View.GONE
    }

    private fun initRecyclerView(mList: ArrayList<TextRecognitionModel>) {
      val madapter = TextRecognitionAdapter(this, mList)
        val mlayoutManager = LinearLayoutManager(this)
        bottom_sheet_recycler_view.apply {
            adapter = madapter
            layoutManager = mlayoutManager
            hasFixedSize()
        }
    }
}