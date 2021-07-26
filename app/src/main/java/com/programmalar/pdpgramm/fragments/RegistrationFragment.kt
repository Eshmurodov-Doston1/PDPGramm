package com.programmalar.pdpgramm.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.FragmentRegistrationBinding
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var fragmentRegistrationBInding:FragmentRegistrationBinding
    lateinit var root:View
    var startTime=60000
    var millis = startTime
    lateinit var storedVerificationId:String
    var a=false
    var isHave=false
    lateinit var countDownTimer: CountDownTimer
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    lateinit var auth:FirebaseAuth
    lateinit var googleSignInClient:GoogleSignInClient
    private  val TAG = "RegistrationFragment"
    var RC_SIGN_IN=1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRegistrationBInding = FragmentRegistrationBinding.inflate(inflater, container, false)
        root = fragmentRegistrationBInding.root
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("uz")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(root.context, gso)
        if (auth.currentUser!=null){
            var bundle = Bundle()
            var navOptions = NavOptions.Builder()
            navOptions.setEnterAnim(R.anim.enter)
            navOptions.setExitAnim(R.anim.exite)
            navOptions.setPopEnterAnim(R.anim.pop_enter)
            navOptions.setPopExitAnim(R.anim.pop_exite)
            findNavController().navigate(R.id.chatsFragment,bundle,navOptions.build())
        }else {
            fragmentRegistrationBInding.google.setOnClickListener {
                signIn()
            }
            countDownTimer = object : CountDownTimer(millis.toLong(), 1000) {
                override fun onFinish() {
                    isHave = true
                    fragmentRegistrationBInding.timer.visibility = View.INVISIBLE
                }

                override fun onTick(millisUntilFinished: Long) {
                    millis = millisUntilFinished.toInt()
                    updateCountDown()
                }

            }
            fragmentRegistrationBInding.sms.setOnClickListener {
                val phoneNumber = fragmentRegistrationBInding.phoneNumber.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    val phone = phoneNumber.substring(1)
                    val substring = phone.substring(0, 3)
                    val substring0 = phone.substring(4, 7)
                    var a = "$substring$substring0"
                    val substring1 = phone.substring(9, 12)
                    val substring2 = phone.substring(13, 15)
                    val substring3 = phone.substring(16, 18)
                    var phoneNumber1 = "$a$substring1$substring2$substring3"
                    if (substring0 != "8__" && substring1 != "___" && substring2 != "__" && substring3 != "__") {
                        sendVerification(phoneNumber1)
                        fragmentRegistrationBInding.timer.visibility = View.VISIBLE
                        closeKerBoard()
                        countDownTimer.start()
                        // fragmentRegistrationBInding.phoneNumber.setText("(+99 8__) ___-__-__")
                    } else {
                        Toast.makeText(
                            root.context,
                            "Iltimos Telefon raqamni to`liq kiriting",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        root.context,
                        "Iltimos Telefon raqamingizni kiriting",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            fragmentRegistrationBInding.parol.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    verifyCode()
                    verifyCode()
                    var view = activity?.currentFocus
                    if (view != null) {
                        var imm: InputMethodManager =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                true
            }
            fragmentRegistrationBInding.parol.addTextChangedListener {
                if (it!!.length == 6) {
                    verifyCode()
                }
            }

            fragmentRegistrationBInding.refreshCode.setOnClickListener {
                if (a) {
                    resendCode(fragmentRegistrationBInding.phoneNumber.text.toString())
                    millis = startTime
                    countDownTimer.start()
                    updateCountDown()
                    fragmentRegistrationBInding.timer.visibility = View.VISIBLE
                } else {
                    sendVerification(fragmentRegistrationBInding.phoneNumber.text.toString())
                }
            }
        }


        var calback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as AppCompatActivity).finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,calback)
        return root
    }

    fun updateCountDown(){
        var minut = (millis/1000)/60
        var second = (millis/1000)%60
        var timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minut,second)
        fragmentRegistrationBInding.timer.text = timeLeftFormatted
    }
    fun resendCode(phoneNumber:String){
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .setForceResendingToken(resendToken)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(){
        var code = fragmentRegistrationBInding.parol.text.toString()
        if (code.length==6){
            if (this::storedVerificationId.isInitialized) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                signInWithPhoneAuthCredential(credential)
            }
        }
    }

    private fun closeKerBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
    fun sendVerification(phoneNumber:String){
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            if (!isHave) {
                fragmentRegistrationBInding.parol.setText(credential.smsCode)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(root.context, "Vaqt o`tib ketdi Qayta jo`nating", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            a=true
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user
                        var bundle = Bundle()
                        bundle.putString("user_email", user!!.email)
                        var navOptions = NavOptions.Builder()
                        navOptions.setEnterAnim(R.anim.enter)
                        navOptions.setExitAnim(R.anim.exite)
                        navOptions.setPopEnterAnim(R.anim.pop_enter)
                        navOptions.setPopExitAnim(R.anim.pop_exite)
                        findNavController().navigate(R.id.chatsFragment,bundle,navOptions.build())
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                        // Update UI
                    }
                }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        Navigation.findNavController(fragmentRegistrationBInding.root).previousBackStackEntry!!.savedStateHandle.set("key", "str");
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        var bundle = Bundle()
                        var navOptions = NavOptions.Builder()
                        navOptions.setEnterAnim(R.anim.enter)
                        navOptions.setExitAnim(R.anim.exite)
                        navOptions.setPopEnterAnim(R.anim.pop_enter)
                        navOptions.setPopExitAnim(R.anim.pop_exite)
                        findNavController().navigate(R.id.chatsFragment,bundle,navOptions.build())
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        //updateUI(null)
                    }
                }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}