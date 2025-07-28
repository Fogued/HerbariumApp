# HerbariumApp

Herbarium is a modern Android application built with Kotlin that enables users to effortlessly manage accounts, capture and organize plant photos with custom metadata, and seamlessly store everything in the cloud using Firebase. The app also features an intuitive interactive gallery with flexible view options for an enhanced user experience.

## Features

- **Secure User Authentication**  
    Sign up, sign in, and sign out smoothly using Firebase Authentication to protect your data.
    
- **Photo Capture**  
    Capture high-quality photos directly from your device’s camera with `CameraX`.
    
- **Metadata Input**  
    Each photo can be saved with custom details:
    
    - Name
        
    - Location (entered manually)
        
    - Description
        
- **Cloud Storage**  
    Images are uploaded to Firebase Storage, while their metadata is safely stored in Firebase Realtime Database.
    
- **Interactive Photo Gallery**  
    View and browse all your uploads (or everyone’s, as configured). Switch between:
    
    - List view 
        
    - Grid view 
        

## Technology Stack

- **Language:** Kotlin
    
- **Framework:** Android Jetpack (ViewModel, LiveData, Navigation)
    
- **Cloud Backend:** Firebase
    
    - Authentication
        
    - Realtime Database
        
    - Storage
        
- **Imaging:** CameraX, Glide (for image display)
    

## Future work

- AI-powered image recognition
    
- External login support (Google, Facebook, Apple)
    
- Image filters and basic editing before upload

- Location auto-filled with GPS
    
- Commenting and reactions on photos
    
- Tag integration for organizing photos
    
- Search plants by name, location, or description
