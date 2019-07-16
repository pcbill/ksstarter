package tw.pcbill.ksstarter.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(@Id private val id: String,
                var username: String,
                var password: String,
                var roles: String,
                var lastPasswordResetDate: Date)
