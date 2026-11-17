package com.example.quizimages

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Représente une question du quiz.
 *
 * @param texte le texte affiché sous l'image.
 * @param imageRes l'identifiant du drawable associé (R.drawable.triangle, etc.).
 * @param choix les trois réponses proposées, dans l'ordre des boutons.
 * @param bonneReponse l'indice (0, 1 ou 2) du choix correct.
 * @param descriptionImage description utilisée pour l'accessibilité (TalkBack).
 */
data class Question(
    val texte: String,
    val imageRes: Int,
    val choix: Array<String>,
    val bonneReponse: Int,
    val descriptionImage: String
)

/**
 * Unique activité du TP : elle gère l'affichage des questions, la validation
 * des réponses, le score, la progression, le résultat final et le redémarrage.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var txtScore: TextView
    private lateinit var txtProgression: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var txtNumeroQuestion: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var txtQuestion: TextView
    private lateinit var txtFeedback: TextView
    private lateinit var layoutChoix: LinearLayout
    private lateinit var boutonsChoix: List<Button>
    private lateinit var btnNavigation: Button
    private lateinit var layoutResultat: LinearLayout
    private lateinit var txtResultat: TextView
    private lateinit var btnRejouer: Button

    /** Les cinq questions statiques : pas de base de données, ni de téléchargement. */
    private val questions = arrayOf(
        Question(
            texte = "Combien de côtés possède cette forme ?",
            imageRes = R.drawable.triangle,
            choix = arrayOf("Quatre", "Trois", "Cinq"),
            bonneReponse = 1,
            descriptionImage = "Un triangle rouge avec trois côtés"
        ),
        Question(
            texte = "Quelle couleur remplit cette forme ?",
            imageRes = R.drawable.carre,
            choix = arrayOf("Vert", "Rouge", "Bleu"),
            bonneReponse = 2,
            descriptionImage = "Un carré bleu"
        ),
        Question(
            texte = "Combien de sommets possède cette forme ?",
            imageRes = R.drawable.cercle,
            choix = arrayOf("Aucun", "Un", "Deux"),
            bonneReponse = 0,
            descriptionImage = "Un cercle vert sans sommet"
        ),
        Question(
            texte = "Combien de pointes compte cette étoile ?",
            imageRes = R.drawable.etoile,
            choix = arrayOf("Quatre", "Cinq", "Six"),
            bonneReponse = 1,
            descriptionImage = "Une étoile jaune à cinq pointes"
        ),
        Question(
            texte = "Quel est le nom de cette forme allongée ?",
            imageRes = R.drawable.rectangle,
            choix = arrayOf("Un carré", "Un triangle", "Un rectangle"),
            bonneReponse = 2,
            descriptionImage = "Un rectangle orange allongé"
        )
    )

    /** État de la partie. */
    private var indexCourant = 0
    private var score = 0
    private var reponsesDonnees = 0
    private var reponseDonnee = false
    private var choixSelectionne = -1
    private var partieTerminee = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trouverLesVues()
        progressQuiz.max = questions.size

        // Chaque bouton transmet son propre indice (0, 1 ou 2) au gestionnaire de clic.
        boutonsChoix.forEachIndexed { index, bouton ->
            bouton.setOnClickListener { validerReponse(index) }
        }
        btnNavigation.setOnClickListener { avancer() }
        btnRejouer.setOnClickListener { rejouer() }

        // Restauration facultative après une rotation de l'écran.
        if (savedInstanceState != null) {
            restaurerEtat(savedInstanceState)
        } else {
            afficherQuestion()
        }
        mettreAJourBandeau()
    }

    /** Récupère les références des widgets déclarés dans activity_main.xml. */
    private fun trouverLesVues() {
        txtScore = findViewById(R.id.txt_score)
        txtProgression = findViewById(R.id.txt_progression)
        progressQuiz = findViewById(R.id.progress_quiz)
        txtNumeroQuestion = findViewById(R.id.txt_numero_question)
        imgQuestion = findViewById(R.id.img_question)
        txtQuestion = findViewById(R.id.txt_question)
        txtFeedback = findViewById(R.id.txt_feedback)
        layoutChoix = findViewById(R.id.layout_choix)
        boutonsChoix = listOf(
            findViewById<Button>(R.id.btn_choix1),
            findViewById<Button>(R.id.btn_choix2),
            findViewById<Button>(R.id.btn_choix3)
        )
        btnNavigation = findViewById(R.id.btn_navigation)
        layoutResultat = findViewById(R.id.layout_resultat)
        txtResultat = findViewById(R.id.txt_resultat)
        btnRejouer = findViewById(R.id.btn_rejouer)
    }

    /** Affiche la question courante : image, texte, trois choix et état initial. */
    private fun afficherQuestion() {
        val question = questions[indexCourant]

        txtNumeroQuestion.text = getString(R.string.numero_question, indexCourant + 1, questions.size)
        imgQuestion.setImageResource(question.imageRes)
        imgQuestion.contentDescription = question.descriptionImage
        txtQuestion.text = question.texte

        boutonsChoix.forEachIndexed { index, bouton ->
            bouton.text = question.choix[index]
            bouton.isEnabled = true
        }

        txtFeedback.text = ""
        layoutChoix.visibility = View.VISIBLE
        layoutResultat.visibility = View.GONE
        btnNavigation.visibility = View.GONE

        reponseDonnee = false
        choixSelectionne = -1
        partieTerminee = false
    }

    /**
     * Valide le choix cliqué. Le drapeau [reponseDonnee] garantit qu'une question
     * ne peut rapporter qu'un seul point, même en cas de clics multiples.
     */
    private fun validerReponse(indexChoix: Int) {
        if (reponseDonnee || partieTerminee) return

        reponseDonnee = true
        choixSelectionne = indexChoix
        reponsesDonnees++

        if (indexChoix == questions[indexCourant].bonneReponse) {
            score++
        }

        appliquerEtatApresReponse()
        mettreAJourBandeau()
    }

    /** Désactive les choix, affiche la correction et prépare la navigation. */
    private fun appliquerEtatApresReponse() {
        desactiverChoix()
        afficherCorrection()
        btnNavigation.setText(
            if (estDerniereQuestion()) R.string.voir_resultat else R.string.question_suivante
        )
        btnNavigation.visibility = View.VISIBLE
    }

    private fun desactiverChoix() {
        boutonsChoix.forEach { it.isEnabled = false }
    }

    /** Message lisible (pas seulement une couleur) indiquant si la réponse est juste. */
    private fun afficherCorrection() {
        val question = questions[indexCourant]
        if (choixSelectionne == question.bonneReponse) {
            txtFeedback.text = getString(R.string.bonne_reponse)
            txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.correct))
        } else {
            txtFeedback.text = getString(R.string.mauvaise_reponse, question.choix[question.bonneReponse])
            txtFeedback.setTextColor(ContextCompat.getColor(this, R.color.incorrect))
        }
    }

    /** Met à jour le score et la barre de progression (réponses données, pas bonnes réponses). */
    private fun mettreAJourBandeau() {
        txtScore.text = getString(R.string.score, score, questions.size)
        progressQuiz.progress = reponsesDonnees
        txtProgression.text = getString(R.string.progression, reponsesDonnees, questions.size)
    }

    /** Passe à la question suivante ou affiche le résultat après la cinquième réponse. */
    private fun avancer() {
        if (!reponseDonnee || partieTerminee) return

        if (estDerniereQuestion()) {
            afficherResultat()
        } else {
            indexCourant++
            afficherQuestion()
        }
    }

    private fun estDerniereQuestion(): Boolean = indexCourant == questions.lastIndex

    /** Affiche le score final dans la même activité et masque les choix de réponse. */
    private fun afficherResultat() {
        partieTerminee = true
        layoutChoix.visibility = View.GONE
        btnNavigation.visibility = View.GONE
        layoutResultat.visibility = View.VISIBLE

        val message = when {
            score == questions.size -> getString(R.string.resultat_parfait)
            score >= questions.size / 2 -> getString(R.string.resultat_bien)
            else -> getString(R.string.resultat_encourage)
        }
        txtResultat.text = getString(R.string.resultat_final, score, questions.size) + "\n" + message
    }

    /** Remet à zéro le quiz, le score et la progression. */
    private fun rejouer() {
        indexCourant = 0
        score = 0
        reponsesDonnees = 0
        reponseDonnee = false
        choixSelectionne = -1
        partieTerminee = false
        afficherQuestion()
        mettreAJourBandeau()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, indexCourant)
        outState.putInt(KEY_SCORE, score)
        outState.putInt(KEY_REPONSES, reponsesDonnees)
        outState.putBoolean(KEY_REPONSE_DONNEE, reponseDonnee)
        outState.putInt(KEY_CHOIX, choixSelectionne)
        outState.putBoolean(KEY_PARTIE_TERMINEE, partieTerminee)
    }

    /** Restaure l'état après une rotation, sans fragment ni activité supplémentaire. */
    private fun restaurerEtat(etat: Bundle) {
        indexCourant = etat.getInt(KEY_INDEX, 0).coerceIn(questions.indices)
        score = etat.getInt(KEY_SCORE, 0).coerceIn(0, questions.size)
        reponsesDonnees = etat.getInt(KEY_REPONSES, 0).coerceIn(0, questions.size)
        reponseDonnee = etat.getBoolean(KEY_REPONSE_DONNEE, false)
        choixSelectionne = etat.getInt(KEY_CHOIX, -1)
        partieTerminee = etat.getBoolean(KEY_PARTIE_TERMINEE, false)

        if (partieTerminee) {
            afficherResultat()
        } else {
            afficherQuestion()
            if (reponseDonnee) appliquerEtatApresReponse()
        }
    }

    private companion object {
        const val KEY_INDEX = "index_courant"
        const val KEY_SCORE = "score"
        const val KEY_REPONSES = "reponses_donnees"
        const val KEY_REPONSE_DONNEE = "reponse_donnee"
        const val KEY_CHOIX = "choix_selectionne"
        const val KEY_PARTIE_TERMINEE = "partie_terminee"
    }
}
