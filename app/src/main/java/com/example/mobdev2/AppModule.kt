package com.example.mobdev2

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
