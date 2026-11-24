# Anniversaire — Widget de compte à rebours

Application Android (Kotlin) qui affiche un widget d'écran d'accueil avec le compte à
rebours (jours / heures / minutes) jusqu'au prochain anniversaire.

## Fonctionnement

- `MainActivity` permet de choisir sa date de naissance (DatePickerDialog), de
  l'enregistrer et de prévisualiser le compte à rebours. Un bouton permet de
  demander l'épinglage du widget (`requestPinAppWidget`, API 26+).
- `BirthdayWidgetProvider` met à jour le widget à chaque minute (`TIME_TICK`) et
  au changement de date, de fuseau horaire, de locale ou au redémarrage de
  l'appareil. Un appui sur le widget rouvre l'application.
- `BirthdayCalculator` calcule la prochaine occurrence de l'anniversaire
  (y compris le cas du 29 février : repli sur le 28 février les années non bissextiles).
- La date de naissance est stockée dans les `SharedPreferences` (`BirthdayPrefs`).

## Compilation

```bash
./gradlew assembleDebug
```

Puis installer l'APK sur un appareil/émulateur (API 26+) :

```bash
./gradlew installDebug
```

Ajoute ensuite le widget « Anniversaire » depuis le sélecteur de widgets de l'écran
d'accueil, ou via le bouton de l'application.

## Structure

```
app/src/main/java/com/example/birthdaycountdown/
├── MainActivity.kt               # saisie de la date + aperçu
├── BirthdayWidgetProvider.kt     # widget (mise à jour toutes les minutes)
├── BirthdayCalculator.kt         # calcul du prochain anniversaire
└── BirthdayPrefs.kt              # persistance de la date de naissance
```
