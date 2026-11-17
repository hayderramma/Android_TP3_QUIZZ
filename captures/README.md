# Captures d'écran

Ce dossier doit contenir les captures **réelles** de l'application (une fois celle-ci lancée sur un
émulateur ou un appareil). Les fichiers attendus sont :

| Fichier | Situation à capturer |
| --- | --- |
| `01_question.png` | Une question avec son image, ses trois boutons, le score en haut à droite et la barre de progression. |
| `02_bonne_reponse.png` | Une bonne réponse : message vert « Bonne réponse ! +1 point » et score mis à jour. |
| `03_mauvaise_reponse.png` | Une mauvaise réponse : message rouge indiquant la bonne réponse, score inchangé, progression avancée. |
| `04_resultat_final.png` | L'écran final avec le score obtenu et le bouton **Rejouer**. |

## Comment les obtenir

1. Lancer l'application sur un émulateur ou un appareil connecté en USB.
2. Avec Android Studio : **Device Explorer → Screen Capture** ou bouton **Screenshot** de la barre d'appareil.
3. En ligne de commande :

```powershell
adb shell screencap -p /sdcard/quiz_capture.png
adb pull /sdcard/quiz_capture.png captures\01_question.png
```

4. Répéter pour les quatre situations, puis vérifier que les images sont lisibles dans le dépôt GitHub.

> Important : ne pas exclure ce dossier dans `.gitignore` ; les captures font partie du travail à rendre.
