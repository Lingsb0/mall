package com.lingsb.oauth.service;

import com.lingsb.oauth.pojo.User;
import com.lingsb.oauth.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.queryByUserName(username);
        if(user != null){
            return new org.springframework.security.core.userdetails.User(user.getUserName(),
                    user.getPasswd(),
                    AuthorityUtils.createAuthorityList(user.getPasswd()));
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
