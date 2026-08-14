# Trello Clone (Android)

A fully-featured, highly-polished Trello-inspired task management application built with **Kotlin** and **Jetpack Compose**. The app allows users to manage boards, create lists, and organize cards with fluid animations, drag-and-drop gestures, and a stunning UI.

## 🚀 Key Features

* **Advanced Drag & Drop:** Seamlessly drag cards between lists or reorder entire lists horizontally.
* **Smart Edge Scrolling:** Dragging a card to the edge of the screen automatically scrolls the board for effortless organization.
* **Rich Card Management:** 
  * **Swipe-to-Delete:** Swiftly remove cards with smooth, intuitive swipe gestures and visual feedback.
  * **Rich Metadata:** Assign colored labels, vibrant cover headers, and due dates to cards.
  * **Task Tracking:** Mark cards as complete or change their priority level.
* **Custom Board Backgrounds:** Choose from a vibrant color palette to paint the entire background of your boards, complete with a transparent top app bar that blends perfectly into the theme.
* **Favorites System:** Pin your most important boards to a dedicated Favorites screen for quick access.
* **Smooth Animations:** Buttery-smooth layout transitions powered by Compose's `animateItem()` when lists and cards are added, removed, or reordered.
* **Robust Architecture:** Built using a strict **MVI (Model-View-Intent)** pattern, ensuring predictable state management and clean UI separation.

## 📸 Screenshots



| My Boards | Board View | Card Details |
| --- | --- | --- |
| <img width="720" height="1600" alt="WhatsApp Image 2026-08-14 at 3 42 29 AM" src="https://github.com/user-attachments/assets/eed77193-07d6-468b-92f8-bd1bd19cb922" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-08-14 at 3 43 23 AM" src="https://github.com/user-attachments/assets/015d352b-7fb5-4cb4-b8b0-a66504fa77a2" /> | <img width="720" height="1600" alt="WhatsApp Image 2026-08-14 at 3 47 58 AM" src="https://github.com/user-attachments/assets/027dbebf-c78f-4eb0-8a61-95d5ba92fb1f" />
 |

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose & Material 3
* **Architecture:** MVI (Model-View-Intent)
* **Dependency Injection:** Dagger Hilt
* **Database:** Room Database (Offline-first local persistence)
* **Navigation:** Jetpack Navigation Compose
* **Concurrency:** Kotlin Coroutines & StateFlow
