package tw.pcbill.ksstarter.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Qualifier("userDetailServiceImpl")
    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    @Throws(Exception::class)
    fun configureAuthentication(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationTokenFilterBean(): JwtAuthenticationTokenFilter {
        return JwtAuthenticationTokenFilter()
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // using JWT，csrf is not necessary.
                .csrf().disable()

                // base on token，does not need session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // access static resource without permit
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                // get token API can access without auth.
                .antMatchers("/auth/**").permitAll()
                // others need to auth
                .anyRequest().authenticated()

        // no cache
        httpSecurity.headers().cacheControl()

        httpSecurity.addFilterBefore(authenticationTokenFilterBean(),
                UsernamePasswordAuthenticationFilter::class.java)
    }
}