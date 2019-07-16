package tw.pcbill.ksstarter.security.service

import tw.pcbill.ksstarter.model.User

interface AuthService {

    fun register(userToAdd: User): User?
    fun login(username: String, password: String): String
    fun refresh(oldToken: String): String?

}