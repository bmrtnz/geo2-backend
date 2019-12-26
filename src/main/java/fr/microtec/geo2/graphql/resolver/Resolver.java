package fr.microtec.geo2.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import fr.microtec.geo2.persistance.entity.GeoUser;
import fr.microtec.geo2.persistance.repository.GeoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Root Query Resolver
 */
@Component
public class Resolver implements GraphQLQueryResolver {

	private GeoUserRepository userRepository;

	@Autowired
	public Resolver(GeoUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<GeoUser> getUsers() {
		return (List<GeoUser>) this.userRepository.findAll();
	}

}
