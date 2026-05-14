# 📱 Présentation

**LINGO-TV** est une application Android inspirée de Netflix, développée dans le cadre d'un projet universitaire. Elle permet de découvrir, rechercher et explorer des films et séries en utilisant l'API TMDB (The Movie Database).

---

## ✨ Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| 🏠 **Accueil** | Affichage des films populaires et tendances |
| 🔍 **Recherche** | Recherche de films et séries en temps réel |
| 🎬 **Détails** | Fiche complète : synopsis, note, casting |
| ❤️ **Favoris** | Sauvegarder vos films préférés |
| 📂 **Catégories** | Navigation par genre (Action, Comédie, Horreur...) |
| 🌙 **Mode sombre** | Interface dark style Netflix |
| 🤖 **IA Gemini** | Assistant intelligent intégré (Google Gemini) |
| 🖼️ **Avatar** | Upload et personnalisation de photo de profil |

---

## 🛠️ Technologies utilisées

- **Language** : Java
- **IDE** : Android Studio
- **API** : [TMDB API v3](https://www.themoviedb.org/documentation/api)
- **Réseau** : Retrofit2 + OkHttp
- **Images** : Glide / Picasso
- **Architecture** : MVC / MVVM
- **Base de données** : [Supabase](https://supabase.com) (Backend as a Service)
- **Conteneurisation** : Docker
- **Intelligence Artificielle** : [Gemini API](https://ai.google.dev) (Google AI)
- **Avatar** : Upload de photo depuis la galerie du téléphone
- **Min SDK** : API 21 (Android 5.0)
- **Target SDK** : API 34 (Android 14)

---

## 🚀 Installation

### Prérequis
- Android Studio (version récente)
- JDK 11 ou supérieur
- Clé API TMDB (gratuite sur [themoviedb.org](https://www.themoviedb.org/))

### Étapes

```bash
# 1. Cloner le repository
git clone https://github.com/Abir-Raghad-NABIH/LINGO-TV-Android.git

# 2. Ouvrir dans Android Studio
File → Open → Sélectionner le dossier

# 3. Ajouter ta clé API TMDB dans :
# app/src/main/java/.../utils/Constants.java
API_KEY = "ta_clé_api_ici"

# 4. Lancer l'application
Run → Run 'app'
```

---

## 📁 Structure du projet

```
LINGO-TV-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/.../
│   │   │   │   ├── activities/      # Écrans principaux
│   │   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   │   ├── models/          # Modèles de données
│   │   │   │   ├── api/             # Appels API TMDB
│   │   │   │   └── utils/           # Constantes & helpers
│   │   │   └── res/
│   │   │       ├── layout/          # Fichiers XML
│   │   │       ├── drawable/        # Images & icônes
│   │   │       └── values/          # Couleurs, strings, styles
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── README.md
```

---

## 👩‍💻 Auteur

**Abir Raghad NABIH**
- GitHub : [@Abir-Raghad-NABIH](https://github.com/Abir-Raghad-NABIH)

---

## 📄 Licence

Projet réalisé dans un cadre universitaire — usage éducatif uniquement.

---


 ⭐ N'oublie pas de mettre une étoile si tu aimes le projet !</sub>

