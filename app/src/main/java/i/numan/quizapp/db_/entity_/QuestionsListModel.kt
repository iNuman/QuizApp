package i.numan.quizapp.db_.entity_

import com.google.firebase.firestore.DocumentId

class QuestionsListModel(
    @DocumentId // this feature of getting Document id is only available in def firebase_firestore = "21.4.0"
    val question_id: String = "",
    val question: String = "",
    val option_a: String = "",
    val option_b: String = "",
    val option_c: String = "",
    val answer: String = "",
    val timer: Long = 0L
)