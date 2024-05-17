package com.example.mobdev2.repo

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var instance: AuthRepositoryImpl

    @Before
    fun provideInstance() {
        instance = AuthRepositoryImpl()
    }

    @Test
    fun test_NotValidEmail_false() {
        val actual = instance.checkInputEmailPassword(
            "duc",
            "123456",
            "123456"
        )
        assertFalse(actual)
    }

    @Test
    fun test_EmailEmpty_false() {
        val actual = instance.checkInputEmailPassword(
            "duc",
            "",
            ""
        )
        assertFalse(actual)
    }

    @Test
    fun test_PasswordDifference_false() {
        val actual = instance.checkInputEmailPassword(
            "duc@gmail.com",
            "123456",
            "654321"
        )
        assertFalse(actual)
    }

    @Test
    fun test_ValidInput_true() {
        val actual = instance.checkInputEmailPassword(
            "duc@gmail.com",
            "123456",
            "123456"
        )
        assertTrue(actual)
    }
}