package i.numan.quizapp.fragments_

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import i.numan.quizapp.R
import i.numan.quizapp.adapters_.QuizListRecyclerView
import i.numan.quizapp.viewModel_.QuizListViewModel
import kotlinx.android.synthetic.main.fragment_quiz_details.*
import kotlinx.android.synthetic.main.fragment_quiz_list.*


class QuizDetailsFragment : Fragment(R.layout.fragment_quiz_details), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var quizListViewModel: QuizListViewModel
    private var position = -1
    var quizID: String = ""
    var totalQuestions = 0L
    var quizName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializations(view)
/*
*  // For Kotlin projects
    kotlinOptions {
        jvmTarget = "1.8"
    }
    * we should Add this line into our app level gradle file to make navArgs Support jvmTarget 1.8
 */
        val args: QuizDetailsFragmentArgs by navArgs()
        position = args.position
        println("ffnet: Position : ${position}")


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        quizListViewModel = ViewModelProvider(this@QuizDetailsFragment)
            .get(QuizListViewModel::class.java)
        quizListViewModel.getQuizListModelData().observe(viewLifecycleOwner, Observer {


            it[position].apply {
                quizID = quiz_id // passing the id
                totalQuestions = questions // passing the questions
                quizName = name
                Glide
                    .with(context!!)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(details_image)
                details_title.text = name
                details_desc.text = desc
                details_difficulty_text.text = level
                details_questions_text.text = "$questions" // Since it's long that why concatenated
            }

        })
    }

    private fun initializations(view: View) {
        navController = Navigation.findNavController(view) //now navController is instantiated
        details_start_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.details_start_btn -> {
                /*
                * Now to pass the position again we'll add arguments to navGraph of QuizFragment
                * And will Rebuild the project to Generate safeArguments
                 */
                val action =
                    QuizDetailsFragmentDirections
                        .actionQuizDetailsFragmentToQuizFragment(
                            totalQuestions = totalQuestions,
                            quizid = quizID,
                            quizName = quizName
                        )
//                navController.navigate(R.id.action_quizDetailsFragment_to_quizFragment)
                navController.navigate(action)
            }
        }

    }


}
