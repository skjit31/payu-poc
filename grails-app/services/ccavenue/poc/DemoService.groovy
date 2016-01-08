package ccavenue.poc

import grails.transaction.Transactional

@Transactional
class DemoService {

    def grailsApplication

    Map getCCAvenueConfig() {
        return grailsApplication.config.ccavenue
    }
}
