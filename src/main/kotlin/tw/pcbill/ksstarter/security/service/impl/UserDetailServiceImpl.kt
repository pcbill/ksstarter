package tw.pcbill.ksstarter.security.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import tw.pcbill.ksstarter.repository.UserRepository
import tw.pcbill.ksstarter.security.JwtUserFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException



@Service
class UserDetailServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username)

        return if (user == null) {
            throw UsernameNotFoundException(String.format("No user found with username '%s'.", username))
        } else {
            JwtUserFactory.create(user)
        }
    }
}

