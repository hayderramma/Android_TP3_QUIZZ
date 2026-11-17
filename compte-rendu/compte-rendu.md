# Compte rendu — Quiz en images

**Étudiant / binôme :** à compléter avant la remise  
**Thème choisi :** les formes géométriques (triangle, carré, cercle, étoile, rectangle)  
**Développement :** Kotlin, interface XML, une seule activité `MainActivity`, sans fragment ni Compose

## 1. Présentation de l’application

Au lancement, l’écran affiche le titre et le score en haut à droite, une barre de progression horizontale, le numéro de la question, l’image de la forme, la question et trois boutons de réponse. Après un clic, un message indique si la réponse est juste ou fausse, les trois boutons sont désactivés et un bouton de navigation apparaît. À la cinquième réponse, ce bouton affiche **Voir le résultat** : le score final, un message de synthèse et le bouton **Rejouer** s’affichent dans la même activité.

## 2. Interface et widgets utilisés

Le layout `activity_main.xml` organise les widgets suivants :

- `LinearLayout` : structure verticale, bandeau horizontal fixe et groupe des trois choix ;
- `TextView` : titre, score (`txt_score`), progression (`txt_progression`), numéro de question, question, retour sur la réponse (`txt_feedback`) et résultat final (`txt_resultat`) ;
- `ProgressBar` (`progress_quiz`) : barre horizontale de 0 à 5 ;
- `ImageView` (`img_question`) : image de la question, avec `scaleType` à `fitCenter` pour éviter toute déformation ;
- `Button` : `btn_choix1`, `btn_choix2`, `btn_choix3`, `btn_navigation` et `btn_rejouer` ;
- `ScrollView` : le contenu défile sur les petits écrans, tandis que le score et la progression restent visibles en haut.

Les ressources `values/strings.xml`, `values/colors.xml` et `values/themes.xml` centralisent textes, couleurs et thème. Les images sont des **drawable vectoriels XML** dans `res/drawable` : elles restent nettes à toute résolution et ne nécessitent aucun téléchargement. Chaque image possède une description destinée à l’accessibilité.

## 3. Structure du tableau des questions et association des images

Une `data class Question` regroupe les cinq informations demandées :

| Propriété | Type | Rôle |
| --- | --- | --- |
| `texte` | `String` | Question affichée sous l’image. |
| `imageRes` | `Int` | Identifiant `R.drawable.…` du vectoriel associé. |
| `choix` | `Array<String>` | Les trois réponses proposées. |
| `bonneReponse` | `Int` | Indice 0, 1 ou 2 du choix correct. |
| `descriptionImage` | `String` | Description pour l’accessibilité. |

Le tableau `questions` contient exactement cinq éléments statiques :

| Question | Image (`res/drawable`) | Indice de la bonne réponse |
| --- | --- | --- |
| Combien de côtés possède cette forme ? | `triangle.xml` (rouge) | 1 — Trois |
| Quelle couleur remplit cette forme ? | `carre.xml` (bleu) | 2 — Bleu |
| Combien de sommets possède cette forme ? | `cercle.xml` (vert) | 0 — Aucun |
| Combien de pointes compte cette étoile ? | `etoile.xml` (jaune) | 1 — Cinq |
| Quel est le nom de cette forme allongée ? | `rectangle.xml` (orange) | 2 — Un rectangle |

Les indices corrects sont volontairement répartis sur les trois boutons (1, 2, 0, 1, 2) afin d’éviter qu’une même position soit toujours la bonne réponse.

## 4. Événements, score et progression

Les trois boutons de choix reçoivent un `OnClickListener` avec leur indice grâce à `boutonsChoix.forEachIndexed`. Le clic appelle `validerReponse(index)` :

1. si `reponseDonnee` ou `partieTerminee` est vrai, la fonction retourne immédiatement : **un clic supplémentaire ne peut pas ajouter de point** ;
2. sinon, on mémorise l’indice choisi, on incrémente `reponsesDonnees` et on ajoute 1 à `score` uniquement si l’indice correspond à `bonneReponse` ;
3. les trois boutons sont désactivés, la correction est affichée (texte **et** couleur), puis le bandeau est actualisé ;
4. le bouton de navigation propose **Question suivante**, ou **Voir le résultat** sur la dernière question.

**Différence entre score et progression :** le `score` compte les bonnes réponses (0 à 5), tandis que la `ProgressBar` compte les réponses données, qu’elles soient justes ou fausses. Une mauvaise réponse fait donc avancer la barre sans modifier le score.

**Détection de la fin :** `estDerniereQuestion()` compare `indexCourant` à `questions.lastIndex`. Lors du clic sur **Voir le résultat**, `afficherResultat()` masque les choix, affiche le score final dans la même activité et révèle **Rejouer**. `rejouer()` remet l’indice, le score et la progression à zéro. Les bornes du tableau sont protégées : l’indice n’est incrémenté que si la question courante n’est pas la dernière.

La rotation de l’écran est gérée avec `onSaveInstanceState` et `restaurerEtat()` : l’indice, le score, le nombre de réponses et l’état de la réponse courante sont conservés, sans fragment ni activité supplémentaire.

## 5. Tests réalisés

| Scénario | Résultat attendu | Résultat observé |
| --- | --- | --- |
| Lancer une nouvelle partie | Question 1, trois choix, score 0/5, progression 0/5 | Conforme |
| Donner une bonne réponse | Score +1, progression +1 | Conforme |
| Donner une mauvaise réponse | Score inchangé, progression +1 | Conforme |
| Appuyer plusieurs fois sur un choix | Aucun point supplémentaire, boutons désactivés | Conforme |
| Passer à la question suivante | Image, texte et choix actualisés, score conservé | Conforme |
| Répondre juste aux cinq questions | Score final 5/5, progression 5/5 | Conforme |
| Répondre faux aux cinq questions | Score final 0/5, progression 5/5 | Conforme |
| Mélanger bonnes et mauvaises réponses | Score égal au nombre de bonnes réponses | Conforme |
| Utiliser Rejouer | Retour à la question 1, score et progression remis à zéro | Conforme |
| Rotation de l’écran | État de la partie conservé | Conforme |
| Petit écran | Choix accessibles par défilement, textes lisibles | Conforme |

Les cas limites ont été vérifiés en particulier : le bouton de navigation reste masqué tant qu’aucune réponse n’est donnée, et la progression ne dépasse jamais 5.

## 6. Difficultés rencontrées

La principale difficulté a été de bien séparer le **score** de la **progression** : la barre doit avancer même lors d’une erreur. La gestion des clics multiples a été résolue avec le drapeau `reponseDonnee`, et la conservation de l’état lors de la rotation avec `onSaveInstanceState`. Sur les petits écrans, le `ScrollView` évite que le troisième choix soit coupé tout en gardant le score visible en haut.

## 7. Réponses aux questions du compte rendu

1. **Comment représentez-vous une question et sa bonne réponse ?**  
   Une `data class Question` contient le texte, l’identifiant du drawable, les trois choix dans un tableau et l’indice `bonneReponse` (0, 1 ou 2). Le tableau `questions` contient cinq instances de cette classe.

2. **Comment le clic sur un bouton permet-il de savoir quel choix a été sélectionné ?**  
   Chaque bouton reçoit un écouteur avec son propre indice via `forEachIndexed`. L’indice transmis à `validerReponse(index)` est comparé à `questions[indexCourant].bonneReponse`.

3. **Comment empêchez-vous l’attribution de plusieurs points pour une question ?**  
   Le booléen `reponseDonnee` passe à `true` au premier clic ; les appels suivants sortent immédiatement de `validerReponse`. Les trois boutons sont en plus désactivés.

4. **Quelle différence faites-vous entre le score et la progression ?**  
   Le score compte uniquement les bonnes réponses ; la progression compte toutes les réponses données, justes ou fausses.

5. **Comment détectez-vous la dernière question et affichez-vous le résultat ?**  
   `estDerniereQuestion()` compare l’indice courant à `questions.lastIndex`. Sur la dernière question, le bouton porte **Voir le résultat** et appelle `afficherResultat()`, qui affiche le score final et le bouton **Rejouer** dans la même activité.
