package ccavenue.poc

import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.client.HttpResponseException

@Transactional
class DemoService {

    def grailsApplication

    Map getCCAvenueConfig() {
        return grailsApplication.config.ccavenue
    }

    List makeUrlConnectionCall(String url) {
        List l =[]
        String responseText
        def urlConnect = new URL(url)
        def connection = urlConnect.openConnection()
//Set all of your needed headers
        connection.setRequestProperty("Referer", "http://localhost:8080/")

        if (connection.responseCode == 200) {
            responseText = connection.content.text
            String parsedText = responseText.replaceFirst("processData\\(", "").replaceAll("]\\)", "]").replaceAll("\"\\[\\{", "[{").replaceAll("}]\"", "}]")
            String mainText = parsedText.replaceAll("\"", "'").replace("\\", "").replace("'","\"")
            def jsonSlurper = new groovy.json.JsonSlurper()
            l = jsonSlurper.parseText(mainText)
//            l << lt.data.OPTCRDC
        } else {
            println "An error occurred:"
            println connection.responseCode
            println connection.responseMessage
        }
        l
    }
}
