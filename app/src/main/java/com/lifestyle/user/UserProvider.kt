package com.lifestyle.user

/**
 * An interface *Fragment*s can reference to get the User they should manipulate.
 */
interface UserProvider {
    fun getUser(): UserData
}