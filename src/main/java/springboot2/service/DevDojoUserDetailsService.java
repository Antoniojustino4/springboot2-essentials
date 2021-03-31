package springboot2.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import springboot2.repository.DevDojoUserRepository;

@Service
@RequiredArgsConstructor
public class DevDojoUserDetailsService implements UserDetailsService{
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return Optional.ofNullable(devDojoUserRepository.findByUsername(username))
				.orElseThrow(()-> new UsernameNotFoundException("User not found"));
	}

}
