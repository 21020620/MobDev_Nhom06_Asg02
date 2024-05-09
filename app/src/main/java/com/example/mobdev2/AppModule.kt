package com.example.mobdev2


//import android.content.Context
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
//import com.example.mobdev2.ui.screens.book.UserPreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

//val Context.dataStore by preferencesDataStore(name = "settings")

@Module
@ComponentScan("com.example.mobdev2")
class AppModule {
    @Single
    fun provideFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

//    @Single
//    fun provideDataStore(context: Context) = context.dataStore
//
//    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
//        return UserPreferencesRepository(dataStore)
//    }
}