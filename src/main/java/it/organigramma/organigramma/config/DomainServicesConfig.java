package it.organigramma.organigramma.config;

import it.organigramma.organigramma.policy.RolePolicy;
import it.organigramma.organigramma.repository.RoleAssignmentRepository;
import it.organigramma.organigramma.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    @Bean
    public AllowedRolesService allowedRolesService() {
        return new AllowedRolesService();
    }

    @Bean
    public UnitMoveService unitMoveService() {
        return new UnitMoveService();
    }

    @Bean
    public OrgChartService orgChartService(RoleAssignmentRepository repo) {
        return new OrgChartService(repo);
    }

    @Bean
    public RoleAssignmentService roleAssignmentService(
            RoleAssignmentRepository repo,
            RolePolicy rolePolicy
    ) {
        return new RoleAssignmentService(repo, rolePolicy);
    }
}
