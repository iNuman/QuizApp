package i.numan.quizapp.viewModel_

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import i.numan.quizapp.db_.Dao_.onFireStoreTaskCompleted
import i.numan.quizapp.db_.entity_.QuizListModel
import i.numan.quizapp.repository_.QuizListRepository
import kotlinx.coroutines.launch


class QuizListViewModel : ViewModel(), onFireStoreTaskCompleted {
    private val quizListModelData =
        MutableLiveData<List<QuizListModel>>()

    /*
    *
    * This will be called inside User Interface
    * it'll return data of mutable list to this method below
     */
    fun getQuizListModelData(): LiveData<List<QuizListModel>> {
        return quizListModelData
    }

    private val firebaseRepository: QuizListRepository = QuizListRepository(this)

    // Getting data from this interface and setting it  to
    override fun quizListDataAdded(quizListModel: List<QuizListModel>) {
//        Since MutableLiveData is subclass of Live data here we had used MutableLiveData
//        because it contains some properties like set value and get value like i had used one below setValue
        quizListModelData.value = quizListModel
    }

    override fun getError(e: Exception) {
        TODO("Not yet implemented")
        println("ffnet: Error Here : ${e.localizedMessage}")
    }


/*
* Once our view model initializes this method inside init will be called
 */
    init {
        viewModelScope.launch {
            firebaseRepository.getQuizData()
        }

    }
}
