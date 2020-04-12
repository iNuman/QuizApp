package i.numan.quizapp.db_.Dao_

import i.numan.quizapp.db_.entity_.QuizListModel


interface onFireStoreTaskCompleted {

    fun quizListDataAdded(quizListModel: List<QuizListModel>) // it'll return a list of items
    fun getError(e: Exception)
}