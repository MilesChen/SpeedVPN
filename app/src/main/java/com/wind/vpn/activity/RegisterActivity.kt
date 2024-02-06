package com.wind.vpn.activity

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.github.kr328.clash.R
import com.wind.vpn.widget.InputView
import org.w3c.dom.Text

class RegisterActivity : BaseActivity(), TextWatcher {
    private lateinit var inputName: InputView
    private lateinit var inputPwd1: InputView
    private lateinit var inputPwd2: InputView
    private lateinit var btn: TextView
    private lateinit var changeRegister: TextView
    private lateinit var llAgree: View;
    private lateinit var cbAgree: CheckBox
    private var isRegister = false
    private var isShowPwd1 = false
    private var isShowPwd2 = false
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
        inputName = findViewById(R.id.input_username)
        inputPwd1 = findViewById(R.id.input_pwd_1)
        inputPwd2 = findViewById(R.id.input_pwd_2)
        btn = findViewById(R.id.btn_register)
        changeRegister = findViewById(R.id.tv_change_register)
        llAgree = findViewById(R.id.ll_agree)
        cbAgree = findViewById(R.id.cb_agreement)
        changeRegister.setOnClickListener {
            updateRegisterState(true)
        }
        inputName.suffixIcon.setImageResource(R.drawable.icon_clear_text)
        inputName.prefixIcon.setImageResource(R.drawable.icon_people)
        inputName.inputText.setHint(R.string.hint_name)
        inputPwd1.suffixIcon.setImageResource(R.drawable.icon_pw_hide)
        inputPwd1.prefixIcon.setImageResource(R.drawable.icon_key)
        inputPwd1.inputText.setHint(R.string.hint_pwd1)
        inputPwd2.suffixIcon.setImageResource(R.drawable.icon_pw_hide)
        inputPwd2.prefixIcon.setImageResource(R.drawable.icon_key)
        inputPwd2.inputText.setHint(R.string.hint_pwd2)
        setShowPwd(inputPwd1.inputText, inputPwd1.suffixIcon, isShowPwd1)
        setShowPwd(inputPwd2.inputText, inputPwd2.suffixIcon, isShowPwd2)
        updateRegisterState(false)
        addWatcher()
        addClick()
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
        val enable: Boolean =
            inputName.inputText.text?.isNotEmpty() ?: false && inputPwd1.inputText.text?.isNotEmpty() ?: false && (inputPwd2.inputText.text?.isNotEmpty() ?: false || !isRegister)
        btn.isEnabled = enable;
    }

    private fun updateRegisterState(state: Boolean) {
        isRegister = state
        changeRegister.visibility = if (isRegister) View.GONE else View.VISIBLE
        inputPwd2.visibility = if (isRegister) View.VISIBLE else View.GONE
        btn.setText(if (isRegister) R.string.btn_register else R.string.btn_login)
        llAgree.visibility = if (isRegister) View.VISIBLE else View.GONE
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
}