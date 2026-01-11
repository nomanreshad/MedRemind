**Name:** Md. Noman Hassan Reshad  
**Background:** Computer Science Student & Self Taught Android Developer  
**Project:** MedRemind - Kotlin Multiplatform Medication Reminder App (Android & iOS)

---

### Background and Motivation

I am a computer science student with around three years of coding experience. My journey into programming did not start with mobile development. My first exposure to programming was through Python, which I learned on my own. Python helped me build a strong foundation in programming concepts, but most of what I built lived only on my local machine. I wanted to create something interactive that I could easily show to others and that real users could interact with.

This curiosity led me to explore web development, where I learned how to build user facing applications and gained a better understanding of how software is experienced by end users. Over time, I became increasingly interested in mobile applications because of their accessibility and real world impact. Eventually, I moved into Android development, and it quickly became the area I enjoyed the most. Since then, my primary focus has been mobile development, especially building applications that solve practical, everyday problems.

As I grew as a developer, I became interested not just in writing code, but in building reliable, well structured applications that people can depend on.

---

### Project Idea and Problem Statement

Medication management is one such real world problem that I noticed around me. Many people, especially those who take multiple medications, struggle to remember doses, schedules, and important details related to their prescriptions. Missing or mistiming medication can have serious consequences, yet many existing solutions are either overly complex or unreliable without constant internet access.

This motivated me to build **MedRemind**, a medication reminder application designed to help users manage their medications in a simple and dependable way. The core goal of the project is to make medication tracking easier and more reliable for everyday use.

MedRemind allows users to add and manage detailed medication information, schedule dose reminders, attach prescription and medication images, and receive notifications at the right time. Everything is designed to reduce friction and help users stay consistent with their medication routines.

---

### Kotlin Multiplatform Approach

MedRemind is built as a **Kotlin Multiplatform** project targeting both Android and iOS. I chose Kotlin Multiplatform because it allows sharing business logic, data handling, and UI code while still delivering native experiences on each platform.

Using **Compose Multiplatform**, I implemented a largely shared UI layer so that both Android and iOS versions behave consistently and follow the same design principles. At the same time, platform specific features such as notifications, alarms, and system level integrations are implemented separately for each operating system, ensuring native behavior where it matters.

This approach significantly reduced duplicated work while keeping the codebase clean and maintainable. It also allowed me to focus more on application logic and user experience rather than rewriting the same features for each platform.

---

### Offline First Design Philosophy

A key design decision in MedRemind is its **offline first architecture**. All core functionality, including adding medications, scheduling reminders, viewing schedules, and receiving notifications, works entirely without an internet connection. This is especially important for a health related application, where reliability is more important than advanced online features.

Internet access is only required for an optional AI powered prescription scanning feature, which helps users extract medication details from prescription images. Even without this feature, the app remains fully functional. This ensures that users can depend on MedRemind in real world conditions where connectivity may be limited or unavailable.

---

### Architecture and Technologies Used

From a technical perspective, MedRemind follows a clean and scalable **MVI (Model View Intent)** architecture to ensure predictable state management and easier testing. The project is structured into clear layers such as domain, data, and presentation, which are shared across platforms where possible.

Key technologies used in the project include Kotlin Multiplatform for shared logic, Compose Multiplatform for UI, Kotlin Coroutines and Flows for asynchronous programming, SQLDelight for a multiplatform local database, Koin for dependency injection, and Kotlinx Serialization for data handling. Material 3 is used to provide a modern and accessible user interface.

Platform specific implementations handle notifications, alarms, and system integrations on Android and iOS, while exposing a unified API to the shared code.

---

### Impact and Learning Outcomes

Building MedRemind helped me deepen my understanding of Kotlin Multiplatform beyond simple examples. I learned how to structure a real production style multiplatform project, manage shared and platform specific code effectively, and design an application with real users and constraints in mind.

The project also reinforced my interest in mobile development and strengthened my belief that Kotlin Multiplatform is a practical solution for building reliable cross platform applications without compromising user experience or code quality.

---

### Conclusion

MedRemind represents my journey from learning programming fundamentals with Python, to building user facing applications on the web, and finally to focusing on mobile development with Kotlin Multiplatform. It combines real world problem solving, thoughtful architecture, and modern multiplatform technologies into a single cohesive application.

Through this project, I aimed to demonstrate how Kotlin Multiplatform can be used to build meaningful, offline first, multiplatform applications that are both maintainable and user focused. I see MedRemind not only as a contest submission, but as a strong foundation for building impactful mobile applications in the future.
