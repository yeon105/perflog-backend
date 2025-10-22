package com.perflog.domain.member.service

import com.perflog.domain.member.repository.MemberRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class LoginService(private val memberRepository: MemberRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val member = memberRepository.findByEmail(email)
            ?: throw UsernameNotFoundException(email)
        val grantedAuthorities: MutableList<GrantedAuthority> = ArrayList()
        grantedAuthorities.add(SimpleGrantedAuthority(member.role.toString()))

        return User(member.email, member.password, grantedAuthorities)
    }
}
