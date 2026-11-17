# Quiz en images — TP Android (Kotlin + XML)

Jeu de quiz en images composé de **cinq questions statiques** sur le thème des **formes géométriques**.
L'utilisateur observe une image, choisit parmi **trois boutons**, gagne un point par bonne réponse,
suit sa progression puis consulte son score final. Tout se passe dans **une seule activité** `MainActivity`.

## Contenu du dépôt

| Dossier / fichier | Description |
| --- | --- |
| `app/src/main/java/com/example/quizimages/MainActivity.kt` | Logique du quiz, `data class Question`, tableau des 5 questions, gestion des clics, score, progression, résultat et `Rejouer`. |
| `app/src/main/res/layout/activity_main.xml` | Interface XML : bandeau titre/score, `ProgressBar`, `ImageView`, textes, 3 boutons de choix, navigation et zone de résultat. |
| `app/src/main/res/drawable/` | Les cinq images vectorielles : `triangle.xml`, `carre.xml`, `cercle.xml`, `etoile.xml`, `rectangle.xml` (plus `ic_launcher.xml`). |
| `app/src/main/res/values/` | Chaînes, couleurs et thème. |
| `captures/` | Quatre captures d'écran réelles : question, bonne réponse, mauvaise réponse, résultat final. |
| `compte-rendu/compte-rendu.md` | Compte rendu du travail et réponses aux questions du TP. |
| `gradle/wrapper/` et `gradlew*` | Gradle Wrapper : aucun Gradle local n'est nécessaire. |

Les dossiers `build/`, `.gradle/` et le fichier `local.properties` sont exclus du dépôt via `.gitignore`.

## Ouvrir et lancer le projet

1. Installer **Android Studio** (avec le SDK Android et un JDK 17).
2. Cloner ce dépôt, puis **File → Open…** et sélectionner la racine du projet (`settings.gradle.kts`).
3. Laisser Android Studio télécharger les dépendances Gradle (première synchronisation).
4. Lancer sur un émulateur ou un appareil physique avec **Run ▶** (`app`).

Ligne de commande (équivalent) :

```powershell
.\gradlew assembleDebug          # construire l'APK
.\gradlew :app:installDebug      # installer sur l'appareil/émulateur connecté
```

L'APK généré se trouve dans `app/build/outputs/apk/debug/app-debug.apk` (dossier non versionné).

## Règles du jeu

- Au départ : question 1/5, score 0/5, progression 0/5.
- Un clic valide immédiatement : **+1 point** si la réponse est correcte, **0 point** sinon.
- Dans les deux cas, la progression avance d'une unité et les trois boutons sont désactivés.
- Cliquer plusieurs fois sur un choix ne rapporte jamais plus d'un point.
- Le bouton **Question suivante** devient **Voir le résultat** à la dernière question.
- Le score final (0 à 5) s'affiche dans la même activité ; les choix sont masqués.
- **Rejouer** remet à zéro la question, le score et la progression.
- La partie est conservée lors d'une rotation de l'écran (`onSaveInstanceState`).

> La progression compte les **réponses données**, pas les bonnes réponses : une erreur fait avancer la barre sans augmenter le score.

## Liens utiles

- [Captures d'écran](captures/README.md)
- [Compte rendu](compte-rendu/compte-rendu.md)
