package com.programmalar.pdpgramm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.programmalar.pdpgramm.R
import com.programmalar.pdpgramm.databinding.FragmentUserAccountBinding
import com.programmalar.pdpgramm.models.User
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserAccountFragment : Fragment() {
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
    lateinit var fragmentuserAccountBinding:FragmentUserAccountBinding
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var root:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       fragmentuserAccountBinding = FragmentUserAccountBinding.inflate(inflater,container,false)
        root = fragmentuserAccountBinding.root
        val user = arguments?.getSerializable("user") as User
        val countChat = arguments?.getInt("countChat",0)
        fragmentuserAccountBinding.email.text = "User emaili:\n${user.email}"
        fragmentuserAccountBinding.name.text = "User email nomi:\n${ user.displayName}"
        fragmentuserAccountBinding.countChat.text = "Yozishmalar soni:$countChat"
        Picasso.get().load(user.photoUrl).into(fragmentuserAccountBinding.userImage)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}