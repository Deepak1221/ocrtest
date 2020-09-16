package com.example.ocrtest


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainViewModel(repo: MainRepo) :ViewModel() {
    var regex = "^[m|M|w|W]+[0-9]{1,}\$"
    var ocrTxtLivedata = MutableLiveData<AppResource<ProcessedData>>()

    fun analyseImage(bitmap: Bitmap?) {
        ocrTxtLivedata.postValue(AppResource.loading(null))
        analyzeImage(bitmap)
    }

    private fun analyzeImage(image: Bitmap?) {
        if (image == null) {
             return
        }

        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        textRecognizer.processImage(firebaseVisionImage)
            .addOnSuccessListener {
                val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)

           val result= recognizeText(it, mutableImage)
                if (result != null) ocrTxtLivedata.postValue(AppResource.success(result)) else ocrTxtLivedata.postValue(
                    AppResource.error("There was some error", null)
                )

            }
            .addOnFailureListener {
                ocrTxtLivedata.postValue(AppResource.error("There was some error",null))

            }
    }

    private fun recognizeText(result: FirebaseVisionText?, image: Bitmap?) :ProcessedData? {

        if (result == null || image == null) {
             return null
        }
      Log.w("abh_text",result.text)
        var txtList:ArrayList<TextRecognitionModel>?= arrayListOf()
        val canvas = Canvas(image)
        val rectPaint = Paint()
        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 4F
        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 40F

        var index = 0
        for (block in result.textBlocks) {
            for (line in block.lines) {
                canvas.drawRect(line.boundingBox!!, rectPaint)
                canvas.drawText(index.toString(), line.cornerPoints!![2].x.toFloat(), line.cornerPoints!![2].y.toFloat(), textPaint)

                for (word in line.elements) {
                    Log.w("abh_text:3-", word.text)
                    if(isValidSeries(word.text)){
                        txtList?.add(TextRecognitionModel(index++, word.text))
                    }
                }
            }
        }
        return ProcessedData(txtList,image)
    }

//    fun getPatternString(str: String): String {
//        if (str.isNullOrBlank()) return ""
//        var resutStr = ""
//        if (str.startsWith("MW",true) && str.length > 2 && !str.contains("-") && !str.contains("+")
//        ) {
//            val item = str.substring(2)
//            try {
//                val secChar: Int = item.toInt()
//                resutStr = str
//            } catch (e: NumberFormatException) {
//                resutStr = ""
//            }
//        } else {
//            resutStr = ""
//        }
//
//        return resutStr
//
//    }

    fun isValidSeries(str:String):Boolean{
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(str)
        return m.matches()
    }
}