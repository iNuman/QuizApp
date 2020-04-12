package i.numan.quizapp.splash_

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import i.numan.quizapp.R
import kotlinx.android.synthetic.main.fragment_quiz_app_splash_screen.*


class QuizAppSplashScreen : Fragment(R.layout.fragment_quiz_app_splash_screen) {



    lateinit var firebaseAuth: FirebaseAuth
    lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializations(view)
        start_feedback.text = "Checking User Account"
    }

    private fun initializations(view: View) {
        navController = Navigation.findNavController(view) //now navController is instantiated
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /*
    * Check if the current user is not null in the onStart
     */
    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser == null) {
            // Create new User
            // First SignIn Anonamously
            start_feedback.text = "Create an Account"
            signInAnonamously()
        } else {
            // login if the user is allready logged in using navigation
            navController.navigate(R.id.action_quizAppSplashScreen_to_quizListFragment)
        }

    }

    /*
    * Anonamous SignIn method
     */
    private fun signInAnonamously() {
        firebaseAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                start_feedback.text = "Account Created Sucessfully"
                navController.navigate(R.id.action_quizAppSplashScreen_to_quizListFragment)
            } else {
                println("ffnet: Start Log: ${it.exception}")
            }

        }
    }
}
