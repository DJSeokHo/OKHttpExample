package com.swein.okhttpexample

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import com.swein.easypermissionmanager.EasyPermissionManager
import com.swein.easyphotox.camera.EasyPhotoXFragment
import com.swein.okhttpexample.constants.WebConstants
import com.swein.okhttpexample.framework.glide.GlideWrapper
import com.swein.okhttpexample.framework.thread.ThreadUtility
import com.swein.okhttpexample.modelserivce.ModelService
import com.swein.okhttpexample.okhttpwrapper.model.MemberModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val shapeableImageView: ShapeableImageView by lazy {
        findViewById(R.id.shapeableImageView)
    }
    private val editTextID: EditText by lazy {
        findViewById(R.id.editTextID)
    }
    private val editTextPassword: EditText by lazy {
        findViewById(R.id.editTextPassword)
    }
    private val editTextNickname: EditText by lazy {
        findViewById(R.id.editTextNickname)
    }
    private val buttonSignUp: Button by lazy {
        findViewById(R.id.buttonSignUp)
    }
    private val buttonSignIn: Button by lazy {
        findViewById(R.id.buttonSignIn)
    }
    private val buttonUpdate: Button by lazy {
        findViewById(R.id.buttonUpdate)
    }
    private val buttonSignOut: Button by lazy {
        findViewById(R.id.buttonSignOut)
    }
    private val shapeableImageViewInfo: ShapeableImageView by lazy {
        findViewById(R.id.shapeableImageViewInfo)
    }
    private val textViewInfo: TextView by lazy {
        findViewById(R.id.textViewInfo)
    }
    private val frameLayoutProgress: FrameLayout by lazy {
        findViewById(R.id.frameLayoutProgress)
    }

    private var tokenFromServer = ""
    private var profileImageFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListener()
    }

    private fun setListener() {

        shapeableImageView.setOnClickListener {

            EasyPermissionManager.requestPermission(this,
                9999,
                "Permission",
                "permissions are necessary",
                "setting",
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                startCamera()
            }

        }

        buttonSignUp.setOnClickListener {
            signUp()
        }
        buttonSignIn.setOnClickListener {
            signIn()
        }
        buttonSignOut.setOnClickListener {
            tokenFromServer = ""
            textViewInfo.text = ""
            shapeableImageViewInfo.setImageBitmap(null)
        }
        buttonUpdate.setOnClickListener {
            updateProfile()
        }
    }

    /**
     * sign up -> get token from server -> get member info from server with token
     * let's check response first
     */
    private fun signUp() {

        val id = editTextID.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val nickname = editTextNickname.text.toString().trim()

        var imageName = ""
        if (profileImageFilePath != "") {
            imageName = "profile.jpg"
        }

        if (id == "" || password == "" || nickname == "") {
            return
        }

        showProgress()

        if (profileImageFilePath != "") {
            // sign up with image file by form data
            // form data
            ModelService.signUp(id, password, nickname, profileImageFilePath, imageName, object : ModelService.ModelServiceDelegate {
                override fun onResponse(response: String) {
                    Log.d("???", response)
                    val jsonObject = WebConstants.getJSONObjectFromResponse(response)

                    if (WebConstants.isResponseSuccess(jsonObject)) {
                        // we need get value in response json object
                        val token = WebConstants.getValueFromResponse(jsonObject)

                        if (token != "") {

                            // request member info from server with token
                            getProfileInfo(token)
                        }
                    }

                    ThreadUtility.startUIThread(0) {
                        hideProgress()
                    }
                }

                override fun onException(e: Exception) {
                    Log.d("???", "$e")

                    ThreadUtility.startUIThread(0) {
                        hideProgress()
                    }
                }

            })
        }
        else {
            // sign up without image file by json body
            // json body, can not post file
            ModelService.signUp(id, password, nickname, object : ModelService.ModelServiceDelegate {

                override fun onResponse(response: String) {
                    Log.d("???", response)
                    val jsonObject = WebConstants.getJSONObjectFromResponse(response)

                    if (WebConstants.isResponseSuccess(jsonObject)) {
                        // we need get value in response json object
                        val token = WebConstants.getValueFromResponse(jsonObject)

                        if (token != "") {

                            // request member info from server with token
                            getProfileInfo(token)
                        }
                    }

                    ThreadUtility.startUIThread(0) {
                        hideProgress()
                    }
                }

                override fun onException(e: Exception) {
                    Log.d("???", "$e")

                    ThreadUtility.startUIThread(0) {
                        hideProgress()
                    }
                }

            })
        }

    }

    /**
     * sign in: sign in with id and password to get token -> get member info from server with token
     */
    private fun signIn() {
        val id = editTextID.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (id == "" || password == "") {
            return
        }

        lifecycleScope.launch {

            showProgress()

            try {

                coroutineScope {

                    val tokenResult = async {
                        // in thread here
                        ModelService.signIn(id, password)
                    }

                    // back to main thread here
                    val token = tokenResult.await()

                    hideProgress()
                    if (token != "") {

                        // request member info from server with token
                        getProfileInfo(token)
                    }
                }

            }
            catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                hideProgress()
            }
        }

//        showProgress()
//
//        ModelService.signIn(id, password, object : ModelService.ModelServiceDelegate {
//            override fun onResponse(response: String) {
//                Log.d("???", response)
//                val jsonObject = WebConstants.getJSONObjectFromResponse(response)
//
//                if (WebConstants.isResponseSuccess(jsonObject)) {
//                    // we need get value in response json object
//                    val token = WebConstants.getValueFromResponse(jsonObject)
//
//                    if (token != "") {
//
//                        // request member info from server with token
//                        getProfileInfo(token)
//                    }
//                }
//
//                ThreadUtility.startUIThread(0) {
//                    hideProgress()
//                }
//            }
//
//            override fun onException(e: Exception) {
//                Log.d("???", "$e")
//
//                ThreadUtility.startUIThread(0) {
//                    hideProgress()
//                }
//            }
//
//        })

    }

    private fun updateProfile() {

        if (tokenFromServer == "") {
            return
        }

        val nickname = editTextNickname.text.toString().trim()

        var imageName = ""
        if (profileImageFilePath != "") {
            imageName = "profile.jpg"
        }

        if (nickname == "") {
            return
        }

        showProgress()

        // form data
        ModelService.updateProfile(tokenFromServer, nickname, profileImageFilePath, imageName, object : ModelService.ModelServiceDelegate {

            override fun onResponse(response: String) {
                Log.d("???", response)
                // update will return the member info after updated.
                // so just parsing member object, we don't need token here
                val jsonObject = WebConstants.getJSONObjectFromResponse(response)

                if (WebConstants.isResponseSuccess(jsonObject)) {

                    val memberObject = WebConstants.getDictionaryFromResponse(jsonObject)

                    val memberModel = MemberModel()
                    memberModel.initWithJSONObject(memberObject)

                    // update here
                    ThreadUtility.startUIThread(0) {
                        // update UI must in UI thread
                        updateUI(memberModel)
                    }
                }

                ThreadUtility.startUIThread(0) {
                    hideProgress()
                }
            }

            override fun onException(e: Exception) {
                Log.d("???", "$e")

                ThreadUtility.startUIThread(0) {
                    hideProgress()
                }
            }

        })
    }

    private fun getProfileInfo(token: String) {

        tokenFromServer = token
        if (tokenFromServer == "") {
            return
        }

        showProgress()

        ModelService.profileInfo(tokenFromServer, object : ModelService.ModelServiceDelegate {
            override fun onResponse(response: String) {
                Log.d("???", response)
                // we should update UI here, let's check the member info response
                // member info key is "dictionary"
                val jsonObject = WebConstants.getJSONObjectFromResponse(response)

                if (WebConstants.isResponseSuccess(jsonObject)) {

                    val memberObject = WebConstants.getDictionaryFromResponse(jsonObject)

                    val memberModel = MemberModel()
                    memberModel.initWithJSONObject(memberObject)

                    // update here
                    ThreadUtility.startUIThread(0) {
                        // update UI must in UI thread
                        updateUI(memberModel)
                    }
                }

                ThreadUtility.startUIThread(0) {
                    hideProgress()
                }
            }

            override fun onException(e: Exception) {
                Log.d("???", "$e")

                ThreadUtility.startUIThread(0) {
                    hideProgress()
                }
            }

        })

    }

    private fun updateUI(memberModel: MemberModel) {
        // you should get image static file domain from your server developer
        if (memberModel.profileImageUrl != "") {
            GlideWrapper.setImageUrl("${WebConstants.IMAGE_DOMAIN}${memberModel.profileImageUrl}", shapeableImageViewInfo,
                shapeableImageViewInfo.width, shapeableImageViewInfo.height)
        }

        val memberInfo = "${memberModel.id}\n${memberModel.nickname}\n"

        textViewInfo.text = memberInfo
    }


    private fun startCamera() {

        EasyPhotoXFragment.startFragment(this, R.id.container, 1, onImageSelected = {

            if (it.isNotEmpty()) {
                Log.d("???", "${it[0]}")

                profileImageFilePath = it[0]

                GlideWrapper.setImage(profileImageFilePath, imageView = shapeableImageView,
                    width = shapeableImageView.width,
                    height = shapeableImageView.height,
                    rate = 0.8f
                )

                // close camera
                EasyPhotoXFragment.destroyFragment(this)
            }

        }, onCloseCamera = {
            EasyPhotoXFragment.destroyFragment(this)
        })

    }

    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyPermissionManager.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {

        if (EasyPhotoXFragment.destroyFragment(this)) {
            return
        }

        finish()
    }

}