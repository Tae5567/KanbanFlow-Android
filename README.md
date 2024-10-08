# KanbanFlow-Android
Android app Project Management tool with Kanban boards and Firebase-authenticated real-time user collaboration. Built with Android Studio and Kotlin, the app features Kanban boards for task management and integrates Firebase for real-time updates and user authentication.

# Key Features
Real-Time Collaboration: Multiple users can collaborate on projects with real-time updates across devices.
Kanban Board Interface: An intuitive interface that allows users to add, edit, move, and delete tasks within a Kanban board structure.
User Authentication: Firebase Authentication ensures secure login and user management.
Cross-Platform Notifications: Instant notifications of changes made by collaborators.

# Technical Details
## Frontend (Android)

Developed using Android Studio with Kotlin to ensure a smooth and responsive user experience.
Material Design elements for an intuitive user interface.
Drag-and-Drop functionality for tasks on the Kanban board, making task management easy.
## Backend (Firebase)

Firebase Authentication: Manages user registration and login securely.
Firebase Realtime Database: Ensures seamless and instantaneous data updates across multiple users.
Cloud Functions: Used for backend logic, such as sending notifications when task statuses change.

# How to Run
Clone the repository:
### How to Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/KanbanFlow-Android.git
   cd KanbanFlow-Android

2. **Set up Firebase**:

Create a Firebase project and add your app to it.
Download the google-services.json file and add it to your app's app/ directory.

3. **Run the app**:

Open the project in Android Studio and run it on an Android device or emulator.
