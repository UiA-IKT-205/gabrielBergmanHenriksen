package com.example.huskeliste2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huskis.data.Todo
import com.example.huskis.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

private var USER_ID: String = "Anonymous"
private var DISPLAY_NAME: String = "Anon"
private var EMAIL: String = "anonymous@email.com"
private val TAG: String = "Huskis:MainActivity"
private val firebaseURL = "https://europe-west.firebasedatabase.app/"
lateinit var auth: FirebaseAuth
lateinit var gso: GoogleSignInOptions
lateinit var mGoogleSignInClient: GoogleSignInClient


class ListHolder {

    companion object {
        var PickedTodo: Todo? = null

    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardListing.layoutManager = LinearLayoutManager(this)
        binding.cardListing.adapter = ListRecyclerAdapter(emptyList<Todo>(), this::onListClicked)


        //Signing in with google user, using firebase with realtime database
        signInGoogle()

        ListDepositoryManager.instance.onList = {
            (binding.cardListing.adapter as ListRecyclerAdapter).updateCollection(it)
            if (it.isEmpty())
                binding.noDataText.visibility = (View.VISIBLE)
            else
                binding.noDataText.visibility = (View.INVISIBLE)
        }

        //Header list spacing
        binding.cardListing.addItemDecoration(HeaderDecoration(50, 50))

        //Login in button push show option
        binding.signInCard.setOnClickListener{
            switchAccount()
        }

        //Floating action button
        binding.fabAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.input))
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_input, null)
            val inputText = dialogLayout.findViewById<EditText>(R.id.inputEditText)
            builder.setView(dialogLayout)
            builder.setNeutralButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
                addList(
                    Todo(
                        inputText.text.toString(),
                        mutableListOf()
                    )
                )
            }
            builder.show()
        }
    }

    private fun switchAccount() {
        //Signing out of firebase and google client
        auth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this){ task ->
            if (task.isSuccessful) {
                //Sign out success
                Log.d(TAG, "Sign out : Success")
                binding.loginStatus.text = getString(R.string.noUserLogged)

            } else {
                // Sign out failed
                Toast.makeText(applicationContext,getString(R.string.logoutFailedToast), Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Sign out : Failure", task.exception)

            }


        };

        //Initializin new sign-in
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private fun signInGoogle() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 200)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent();
        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        auth = Firebase.auth
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "Sign In With Credential : Success")
                    USER_ID = auth.currentUser?.uid.toString()
                    EMAIL = auth.currentUser?.email.toString()
                    DISPLAY_NAME = auth.currentUser?.displayName.toString()
                    binding.loginStatus.text = "Logged in as '$DISPLAY_NAME' \n($EMAIL)"
                    ListDepositoryManager.instance.load(USER_ID, EMAIL, firebaseURL, this)

                } else {
                    // Sign in failed
                    Toast.makeText(applicationContext,getString(R.string.loginFailedToast), Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Sign In With Credential : Failure", task.exception)

                }
            }
    }

    private fun addList(item: Todo) {
        ListDepositoryManager.instance.addTodo(item)

    }
    override fun onResume() {
        super.onResume()
        binding.cardListing.adapter?.notifyDataSetChanged()

    }

    private fun onListClicked(todo: Todo): Unit {
        ListHolder.PickedTodo = todo
        Log.e(TAG, "Pushed list : >${todo.id}")
        val intent = Intent(this, DetailsActivity::class.java)
        startActivity(intent)
    }

}