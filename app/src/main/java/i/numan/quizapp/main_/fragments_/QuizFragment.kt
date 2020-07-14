package i.numan.quizapp.fragments_

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.ViewUtils
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import i.numan.quizapp.R
import i.numan.quizapp.db_.entity_.QuestionsListModel
import kotlinx.android.synthetic.main.fragment_quiz.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


const val COLLECTION_NAME = "QuizList"
const val QUESTIONS_COLLECTION_NAME = "Questions"

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class QuizFragment : Fragment(R.layout.fragment_quiz), View.OnClickListener {


    var position = -1
    var quizID = ""
    lateinit var allQuestionsList: MutableList<QuestionsListModel>
    var totalQuestionsToAnswer = 10L
    var quizName = ""
    lateinit var questionsToAnswer: MutableList<QuestionsListModel>
    lateinit var countDownTimer: CountDownTimer
    var currentQuestion = 0

    // to check if the answer is true or false
    var canAnswer: Boolean = false


    // Variables for storing total number of correct and wrong answers
    // we'll increment them inside verifyAnswer method
    var correctAnswers = 0
    var wrongAnswers = 0

    // this will be called in onFinish Timer method
    var notAnswered = 0

    // get the Current user id by initializing firebaseAuth
    var currentUserID = ""
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    //    lateInit var collectionReference: CollectionReference
    private lateinit var navController: NavController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view) //now navController is instantiated
        getQuizIDandTotalQuestionsToAnswerUsingNavigationArgs()
        authenticationCheck()
        initializations()

    }

    private fun getQuizIDandTotalQuestionsToAnswerUsingNavigationArgs() {
        val args: QuizFragmentArgs by navArgs()
        args.apply {
            this@QuizFragment.totalQuestionsToAnswer = totalQuestions
            this@QuizFragment.quizID = quizid
            this@QuizFragment.quizName = quizName
        }
    }

    private fun authenticationCheck() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let {
            firebaseAuth.currentUser?.apply {
                currentUserID = uid
                println("ffnet: Current User ID: $currentUserID")
            }

        }
    }

    private fun initializations() {
        allQuestionsList = ArrayList()
        questionsToAnswer = ArrayList()
        firestore = FirebaseFirestore.getInstance()
        firestore.collection(COLLECTION_NAME)
            .document(quizID)
            .collection(QUESTIONS_COLLECTION_NAME)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    allQuestionsList = it.result!!.toObjects(QuestionsListModel::class.java)
//                    println("ffnet: Data is : ${allQuestionsList[0].question}")
                    pickQuestions()
                    loadUI()
                } else {
                    quiz_title.text = getString(R.string.data_loading_error)
                }
            }
        /*
        * Setting onClickListeners for options buttons
         */
        quiz_option_one.setOnClickListener(this)
        quiz_option_two.setOnClickListener(this)
        quiz_option_three.setOnClickListener(this)
        quiz_next_btn.setOnClickListener(this)


    }

    private fun loadUI() {

//      Quiz data loaded, Load the UI
        quiz_title.text = quizName
        quiz_question.text = "Load First Question "

        enableOptions()
        loadQuestions(questionNum = 0)


    }

    private fun loadQuestions(questionNum: Int) {
        quiz_question_number.text = questionNum.toString()
        quiz_question.text = questionsToAnswer[questionNum].question
        questionsToAnswer[questionNum].apply {
            quiz_option_one.text = option_a
            quiz_option_two.text = option_b
            quiz_option_three.text = option_c
        }
//      Questions Loaded set the canAnswer to true
        canAnswer = true
        currentQuestion = questionNum

        /*
        * Since time for each property can be different So, we can't provide fix time to question
        * So, let the method pick the time to for answer from the question object itself
         */
        startTimer(questionNum)


    }

    private fun startTimer(questionNumber: Int) {

        val timeToAnswer = questionsToAnswer[questionNumber].timer
//        Show timer Progress Value
        quiz_question_progress.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(timeToAnswer * 1000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                quiz_question_time?.let {
                    quiz_question_time.text = "${millisUntilFinished / 1000}"
                }
                // Now to set the progress we'll convert the time left to percentage value
//                Progress in percent
                val percent = millisUntilFinished / (timeToAnswer * 10)
                /*
                * Since time is in seconds
                * So, We don't need to multiply it with thousand and then divide by hundred
                * to convert to percent
                 */
                quiz_question_progress?.let {
                    quiz_question_progress.progress = percent.toInt()
                }
            }

            override fun onFinish() {
//             Once the timer finished Answer will be false
                println("ffnet: Quiz Session Expired")
                canAnswer = false

                // this'll show when we fail to answer in given time
                quiz_question_feedback.text = "Times Up!"
                quiz_question_feedback.setTextColor(resources.getColor(R.color.colorPrimary, null))
                notAnswered++
                showNextButton()
            }
        }
        countDownTimer.start()


    }

    private fun enableOptions() {

//      Show All Option Buttons
        View.VISIBLE.apply {
            quiz_option_one
            quiz_option_two
            quiz_option_three
        }
//      Enable All Option Buttons
        quiz_option_one.isEnabled = true
        quiz_option_two.isEnabled = true
        quiz_option_three.isEnabled = true

//      Hiding feedback and next button
        quiz_question_feedback.visibility = View.INVISIBLE
        quiz_next_btn.apply {
            visibility = View.INVISIBLE
            isEnabled = false
        }

    }

    /*
    * Picking up random question using below method
     */
    private fun pickQuestions() {

        for (i in 0..totalQuestionsToAnswer) {

            val randomNumber = getRandomInteger(allQuestionsList.size, 0)
            questionsToAnswer.add(allQuestionsList[randomNumber])
            allQuestionsList.removeAt(randomNumber)
//            println("ffnet: QuestionsToAnswer ${i}: ${questionsToAnswer[i.toInt()].question}")
        }
    }

    companion object {
        fun getRandomInteger(maximum: Int, minimun: Int): Int {
            return (((Math.random() * (maximum - minimun))) + minimun).toInt()

        }
    }

    /*
    * onClick Listeners
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.quiz_option_one -> {
//                answerSelected(quiz_option_one.text) instead i'll pass button itself and will change the method name to
//                verifyAnswer
                verifyAnswer(quiz_option_one)

            }
            R.id.quiz_option_two -> {
                verifyAnswer(quiz_option_two)
            }
            R.id.quiz_option_three -> {
                verifyAnswer(quiz_option_three)
            }
            // when we answer wrong next button will move to next question in list
            R.id.quiz_next_btn -> {
                // if we came to an end of quiz this is the check
                println("ffnet: Clicking..")
                if (currentQuestion == totalQuestionsToAnswer.toInt()) {
                    submitResults()
                } else {
                    currentQuestion++
                    loadQuestions(currentQuestion)
                    resetOptions()

                }
            }

        }
    }


    /*
    * Submitting Results to Firebase by making new document with current Login user id
     */
    private fun submitResults() {
        /*
      * Submitting Results to Firebase by making new document with current Login user id
       */
        val resultMap = HashMap<String, Int>()
        resultMap.apply {
            put("correct", correctAnswers)
            put("wrong", wrongAnswers)
            put("unanswered", notAnswered)
        }

        firestore.collection("QuizList")
            .document(quizID)
            .collection("Results")
            .document(currentUserID)
            .set(resultMap).addOnCompleteListener {
                if (it.isSuccessful) {
//               Got to Result Page
                    /*
                    * Now to pass the position again we'll add arguments to navGraph of QuizFragment
                    * And will Rebuild the project to Generate safeArguments
                     */
                    val action =
                        QuizFragmentDirections.actionQuizFragmentToQuizResultFragment(
                            quizId = quizID,
                            totalQuestions = totalQuestionsToAnswer
                        )
                    navController.navigate(action)
                } else quiz_title.text = it.exception?.message
            }
    }

    private fun resetOptions() {
        quiz_option_one.apply {
            background = resources.getDrawable(R.drawable.outline_btn_bg, null)
            setTextColor(resources.getColor(R.color.colorLightText, null))
        }
        quiz_option_two.apply {
            background = resources.getDrawable(R.drawable.outline_btn_bg, null)
            setTextColor(resources.getColor(R.color.colorLightText, null))
        }
        quiz_option_three.apply {
            background = resources.getDrawable(R.drawable.outline_btn_bg, null)
            setTextColor(resources.getColor(R.color.colorLightText, null))
        }

        quiz_question_feedback.visibility = View.INVISIBLE
        quiz_next_btn.apply {
            visibility = View.INVISIBLE
            isEnabled = false
        }


    }

    /*
    * Either Correct or Wrong
     */
    private fun verifyAnswer(selectedAnswerBtn: Button) {

//     Check Answers
        if (canAnswer) {
            selectedAnswerBtn.setTextColor(resources.getColor(R.color.colorDark, null))
            if (questionsToAnswer[currentQuestion].answer == selectedAnswerBtn.text) {
//                Correct
                println("ffnet: Correct Answer")
                correctAnswers++
                selectedAnswerBtn.background =
                    resources.getDrawable(R.drawable.correct_answer_bg, null)
                quiz_question_feedback.apply {
                    text = "Correct Answer"
                    setTextColor(resources.getColor(R.color.colorPrimary, null))
                }

            } else {
//                Wrong
                println("ffnet: Wrong Answer")
                wrongAnswers++
                selectedAnswerBtn.background = resources.getDrawable(R.drawable.wrong_answer_bg, null)
                quiz_question_feedback.apply {
                    text =
                        "Wrong Answer \n Correct Answer: ${questionsToAnswer[currentQuestion].answer}"
                    setTextColor(resources.getColor(R.color.colorAccent, null))
                }
            }
            canAnswer = false
            countDownTimer.cancel() // after the false answer timer should be stopped

            // Show next button
            showNextButton()
        }
    }

    /*
    * Next Button Visibility
     */
    private fun showNextButton() {
        if (currentQuestion == totalQuestionsToAnswer.toInt()) {
            quiz_next_btn.text = "Submit Results"
        }
        quiz_question_feedback.visibility = View.VISIBLE
        quiz_next_btn.apply {
            visibility = View.VISIBLE
            isEnabled = true
        }
    }
}
