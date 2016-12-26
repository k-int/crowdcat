package crowdcat

class BootStrap {

  def sysusers = [
    [name:'admin',pass:'admin',display:'Admin',email:'admin@k-int.com', roles:['ROLE_ADMIN','ROLE_USER', 'ROLE_INST_ADM']],
    [name:'fred',pass:'fred',display:'Volunteer Fred',email:'fred@k-int.com', roles:['ROLE_VOLUNTEER','ROLE_USER']]
  ]

  def init = { servletContext ->

    setUpUserAccounts()
 
    def default_uni = SourceCollection.findByName('default') ?: new ElasticSearchSourceCollection(name:'default', 
                                                                                                  esUrl:'http://151.252.2.199:9200/ra/_search?q=object.multimedia.processed.zoom.location:*').save(flush:true, failOnError:true);
  }

  def destroy = {
  }

  def setUpUserAccounts() {
    sysusers.each { su ->
      log.debug("test ${su.name} ${su.pass} ${su.display} ${su.roles}");
      def user = User.findByUsername(su.name)
      if ( user ) {
        if ( user.password != su.pass ) {
          log.debug("Hard change of user password from config ${user.password} -> ${su.pass}");
          user.password = su.pass;
          user.save(failOnError: true)
        }
        else {
          log.debug("${su.name} present and correct");
        }
      }
      else {
        log.debug("Create user...");
        user = new User(
                      username: su.name,
                      password: su.pass,
                      display: su.display,
                      email: su.email,
                      enabled: true).save(failOnError: true)
      }

      log.debug("Add roles for ${su.name} (${su.roles})");
      su.roles.each { r ->

        def role = Role.findByAuthority(r) ?: new Role(authority:r).save(flush:true, failOnError:true)

        if ( ! ( user.authorities.contains(role) ) ) {
          log.debug("  -> adding role ${role} (${r})");
          UserRole.create user, role
        }
        else {
          log.debug("  -> ${role} already present");
        }
      }
    }
  }

}
