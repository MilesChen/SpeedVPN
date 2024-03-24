package com.wind.vpn.activity

import android.graphics.Rect
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.log.Log
import com.wind.vpn.WindGlobal
import com.wind.vpn.data.account.WindAccount
import com.wind.vpn.data.WindApi
import com.wind.vpn.util.buildSupperLinkSpan
import com.wind.vpn.util.dp2px
import com.wind.vpn.widget.InputView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
const val KEY_GO_RENEW = "key_go_renew"
class RegisterActivity : BaseActivity(), TextWatcher, OnGlobalLayoutListener,
    CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private lateinit var inputName: InputView
    private lateinit var inputPwd1: InputView
    private lateinit var inputPwd2: InputView
    private lateinit var btn: TextView
    private lateinit var changeRegister: TextView
    private lateinit var cbAgree: CheckBox
    private lateinit var title: TextView
    private lateinit var mainContent: View
    private var isRegister = false
    private var isShowPwd1 = false
    private var isShowPwd2 = false
    private var goRenew = false;
    override fun getLayoutResId(): Int {
        return R.layout.act_register_login
    }

    override fun getTopTitle(): Int {
        return 0;
    }

    override fun onTopBarIconClick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (isRegister) {
            updateRegisterState(false)
            return
        }
        super.onBackPressed()
    }

    override fun initView() {
        super.initView()
        mainContent = findViewById(R.id.main_content)
        goRenew = intent.getBooleanExtra(KEY_GO_RENEW, false)
        inputName = findViewById(R.id.input_username)
        inputPwd1 = findViewById(R.id.input_pwd_1)
        inputPwd2 = findViewById(R.id.input_pwd_2)
        title = findViewById(R.id.tv_header_bold)
        btn = findViewById(R.id.btn_register)
        changeRegister = findViewById(R.id.tv_change_register)
        cbAgree = findViewById(R.id.cb_agreement)
        cbAgree.setOnCheckedChangeListener{_, _ ->  updateBtn()}
//        cbAgree.text = "${getString(R.string.cb_agree_prefix)} ${getString(R.string.cb_agree_name)}"
        cbAgree.buildSupperLinkSpan("${getString(R.string.cb_agree_prefix)} ${getString(R.string.cb_agree_name)}", getString(R.string.cb_agree_name), "crisp://")
        changeRegister.setOnClickListener {
            updateRegisterState(true)
        }
        btn.setOnClickListener {
            loginOrRegister()
        }
        inputName.suffixIcon.setImageResource(R.drawable.icon_clear_text)
        inputName.prefixIcon.setImageResource(R.drawable.icon_people)
        inputName.inputText.setHint(R.string.hint_name)
        inputName.inputText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
        inputPwd1.suffixIcon.setImageResource(R.drawable.icon_pw_hide)
        inputPwd1.prefixIcon.setImageResource(R.drawable.icon_key)
        inputPwd1.inputText.setHint(R.string.hint_pwd1)
        inputPwd1.inputText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        inputPwd2.suffixIcon.setImageResource(R.drawable.icon_pw_hide)
        inputPwd2.prefixIcon.setImageResource(R.drawable.icon_key)
        inputPwd2.inputText.setHint(R.string.hint_pwd2)
        inputPwd2.inputText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        setShowPwd(inputPwd1.inputText, inputPwd1.suffixIcon, isShowPwd1)
        setShowPwd(inputPwd2.inputText, inputPwd2.suffixIcon, isShowPwd2)
        addWatcher()
        addClick()
        updateRegisterState(false)
        mainContent.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    private var mPreHeight = 0
    private var mKeyBorderHeight = 0
    private fun resizeWindow() {
        val rect = Rect()
        mainContent.getWindowVisibleDisplayFrame(rect)
        val screenHeight = mainContent.rootView.height
        val keyboardHeight = screenHeight - rect.bottom
        android.util.Log.d("chenchao", "screenHeight: $screenHeight, keyboardHeight: $keyboardHeight")
        if (keyboardHeight != mPreHeight) {
            mPreHeight = keyboardHeight
            android.util.Log.d("chenchao", "paddingBottom: ${mainContent.paddingBottom}")
            if (keyboardHeight > 200) {
                mKeyBorderHeight = keyboardHeight
//                mainContent.setPadding(mainContent.paddingLeft, mainContent.paddingTop, mainContent.paddingRight, mainContent.paddingBottom+mKeyBorderHeight)
                mainContent.translationY = -dp2px(40.0f).toFloat()
            } else {
//                mainContent.setPadding(mainContent.paddingLeft, mainContent.paddingTop, mainContent.paddingRight, mainContent.paddingBottom-mKeyBorderHeight)
                mainContent.translationY = 0f
            }
        }

    }

    private fun addClick() {
        inputName.suffixIcon.setOnClickListener { inputName.inputText.setText("") }
        inputPwd1.suffixIcon.setOnClickListener {
            isShowPwd1 = !isShowPwd1
            setShowPwd(inputPwd1.inputText, inputPwd1.suffixIcon, isShowPwd1)
        }

        inputPwd2.suffixIcon.setOnClickListener {
            isShowPwd2 = !isShowPwd2
            setShowPwd(inputPwd2.inputText, inputPwd2.suffixIcon, isShowPwd2)
        }

    }

    private fun addWatcher() {
        inputName.inputText.addTextChangedListener(this)
        inputPwd1.inputText.addTextChangedListener(this)
        inputPwd2.inputText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        updateBtn()
    }

    private fun updateBtn() {
        val isNameOk = inputName.inputText.text?.isNotEmpty() ?: false
        val isPwd1Ok = inputPwd1.inputText.text?.isNotEmpty() ?: false
        val isPwd2Ok = inputPwd2.inputText.text?.isNotEmpty()?:false
        val isCheckbox = cbAgree.isChecked
        val enable: Boolean = isNameOk && isPwd1Ok && ((isPwd2Ok/* && isCheckbox*/) || !isRegister)
        btn.isEnabled = enable;
    }

    private fun updateRegisterState(state: Boolean) {
        isRegister = state
        changeRegister.visibility = if (isRegister) View.GONE else View.VISIBLE
        inputPwd2.visibility = if (isRegister) View.VISIBLE else View.GONE
        btn.setText(if (isRegister) R.string.btn_register else R.string.btn_login)
        cbAgree.visibility = if (isRegister) View.VISIBLE else View.GONE
        title.setText(if (isRegister) R.string.act_title_register else R.string.act_title_login)
        updateBtn()
    }

    private fun setShowPwd(et: EditText, img: ImageView, show: Boolean) {
        if (show) {
            et.transformationMethod = HideReturnsTransformationMethod.getInstance()
            img.setImageResource(R.drawable.icon_pw_show)
        } else {
            et.transformationMethod = PasswordTransformationMethod.getInstance()
            img.setImageResource(R.drawable.icon_pw_hide)
        }
        if (et.text?.isNotEmpty() == true) {
            et.setSelection(et.text.length)
        }
    }

    private fun loginOrRegister() {
        if (isRegister) {
            val pwd1 = inputPwd1.inputText.text.toString()
            val pwd2 = inputPwd2.inputText.text.toString()
            if (pwd1 != pwd2) {
                showToast("two passwords not same")
                return
            }
        }
        login()
    }

    private fun login() {
        showLoading()
        launch(Dispatchers.IO) {
            val email = inputName.inputText.text.toString()
            val pwd = inputPwd1.inputText.text.toString()
            val loginResult = if (!isRegister) WindApi.login(email, pwd) else WindApi.register(email, pwd)
            withContext(Dispatchers.Main) {
                hideLoading()
                if (loginResult.isSuccess && loginResult.data != null) {
                    val account = WindAccount()
                    account.token = loginResult.data!!.token
                    account.email = email
                    account.pwd = pwd
                    account.auth_data = loginResult.data!!.auth_data
                    WindGlobal.account = account
                    if (goRenew) {
                        goRenew()
                    }
                    finish()
                } else if (!loginResult.message.isNullOrEmpty()) {
                    showToast(loginResult.message!!)
                } else {
                    showToast(getString(R.string.toast_error))
                }
            }
        }
    }

    override fun onGlobalLayout() {
        resizeWindow()
    }
}