package com.example.mobdev2



import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mobdev2.ui.screens.book.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
@ComponentScan("com.example.mobdev2")
class AppModule {
    @Single
    fun provideFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

}