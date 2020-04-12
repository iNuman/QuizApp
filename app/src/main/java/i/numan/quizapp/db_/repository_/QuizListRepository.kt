package i.numan.quizapp.repository_

import com.google.firebase.firestore.FirebaseFirestore
import i.numan.quizapp.db_.Dao_.onFireStoreTaskCompleted
import i.numan.quizapp.db_.entity_.QuizListModel
import i.numan.quizapp.fragments_.COLLECTION_NAME


class QuizListRepository(private val onFireStoreTaskCompleted: onFireStoreTaskCompleted) {


    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference = firebaseFirestore
                                        .collection(COLLECTION_NAME)
                                        .whereEqualTo("visibility","public")

    suspend fun getQuizData() {
        collectionReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
//                interfaces
                onFireStoreTaskCompleted.quizListDataAdded(
//                  result'll contain data and will be passed to ListModel class
                    it.result!!.toObjects(QuizListModel::class.java)
                )
            } else {
//                interfaces
                onFireStoreTaskCompleted.getError(it.exception!!)
            }

        }
    }


}

