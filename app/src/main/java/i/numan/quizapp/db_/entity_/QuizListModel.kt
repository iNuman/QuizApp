package i.numan.quizapp.db_.entity_

import com.google.firebase.firestore.DocumentId

data class QuizListModel(
    @DocumentId // this feature of getting Document id is only available in def firebase_firestore = "21.4.0"
    val quiz_id: String = "",
    val name: String = "",
    val desc: String = "",
    val image: String = "",
    val level: String = "",
    val visibility: String = "",
    val questions: Long = 0L
)