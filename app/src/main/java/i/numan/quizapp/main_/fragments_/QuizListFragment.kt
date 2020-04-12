package i.numan.quizapp.fragments_

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import i.numan.quizapp.R
import i.numan.quizapp.adapters_.QuizListRecyclerView
import i.numan.quizapp.db_.entity_.QuizListModel
import i.numan.quizapp.viewModel_.QuizListViewModel
import kotlinx.android.synthetic.main.fragment_quiz_list.*

/*
  *     app:popUpToInclusive="true"
  *     app:popUpTo="@id/quizAppSplashScreen"
  *     Adding these two in nav_graph will prevent the back press to splash instead back press
  *     will close the app
*/
class QuizListFragment : Fragment(R.layout.fragment_quiz_list), QuizListRecyclerView.Interaction {

    override fun onItemSelected(position: Int, item: QuizListModel?) {
        /*
        * Link to Understand Navigation Safe Arguments
        * https://developer.android.com/guide/navigation/navigation-pass-data
        * We'll pass action here as it performs action in navigation graph
        * And the details fragment will have arguments as arguments defined here
         */
        val action =
            QuizListFragmentDirections
                .actionQuizListFragmentToQuizDetailsFragment(position = position)
        navController.navigate(action)
    }

    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var quizListRecyclerAdapter: QuizListRecyclerView
    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializations(view)
        initRecyclerView()

    }

    private fun initializations(view: View) {
        navController = Navigation.findNavController(view) //now navController is instantiated
        quizListRecyclerAdapter = QuizListRecyclerView(this@QuizListFragment)
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*
        * Once viewModel Initialized it's gona fire up the override method
        */
        quizListViewModel = ViewModelProvider(this@QuizListFragment)
            .get(QuizListViewModel::class.java)
        quizListViewModel.getQuizListModelData().observe(viewLifecycleOwner, Observer {
            recycler_view.animation = fadeInAnimation
            list_progress.animation = fadeOutAnimation
            quizListRecyclerAdapter.submitList(it)
        })
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            adapter = quizListRecyclerAdapter
            layoutManager = LinearLayoutManager(context!!)
        }

    }


}
