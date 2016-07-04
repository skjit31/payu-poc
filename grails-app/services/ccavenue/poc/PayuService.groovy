package ccavenue.poc

import com.pg.Transaction
import grails.transaction.Transactional
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.json.JSONException
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import com.pg.util.ConfigHelper
import util.Encryption
import com.pg.util.RestApiUtil
import com.pg.util.SHA512Encryption
import groovyx.net.http.ContentType
import groovyx.net.http.Method

@Transactional
class PayuService {

    def grailsLinkGenerator

    def logRequestData(GrailsParameterMap params) {

        log.info("params : ${params}")
        String text = "eCwWELxi|${params.status}||||||${params.ud5 ?: ''}|${params.ud4 ?: ''}|${params.ud3 ?: ''}|${params.ud2 ?: ''}|" +
                "${params.ud1 ?: ''}|${params.email}|${params.firstname}|${params.productinfo}" +
                "|${params.amount}|${params.txnid}|${params.key}"
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${text}"
        String hash = SHA512Encryption.encryptTextInSHA512(text)
        println ">>>>>>>>>>>>>>>>>>>>>>>hash1>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${hash}"
        println ">>>>>>>>>>>>>>>>>>>>>>>hash2>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${params.hash}"
        if (hash == params.hash) {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>success"
        } else {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Hash is not matched."
        }
    }

    def makeApiCall() {
        String testUrl = ConfigHelper.payuApiUrl;
        String method = "verify_payment";

        //Salt of the merchant
        String salt = ConfigHelper.payuSalt;

        //Key of the merchant
        String key = ConfigHelper.payuKey;

        String var1 = "4039937155099450102345678901"; // transaction id


        String toHash = key + "|" + method + "|" + var1 + "|" + salt;
        String Hashed = Encryption.hashCal(toHash);
        String Poststring = "key=" + key + "&command=" + method + "&hash=" + Hashed + "&var1=" + var1;
        //String Poststring = "key=" + key +  "&command=" + method +  "&hash=" + Hashed + "&var1=" + var1 + "&var2=" + var2 + "&var3=" + var3 ;
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${Poststring}"
        RestApiUtil restApiUtil = new RestApiUtil()
        Map requestMap = [key: key, method: method, hash: Hashed, var1: var1]
        restApiUtil.makeApiCall("", ContentType.URLENC, Method.POST, requestMap)
    }


    Map makePaymentVerificationCall(String transactionId) {
        makeUrlConnectionCall("verify_payment", transactionId)
    }

    Map makeUrlConnectionCall(String command, String transactionId) {
        String testUrl = ConfigHelper.payuApiUrl;
        String method = command;

        //Salt of the merchant
        String salt = ConfigHelper.payuSalt;

        //Key of the merchant
        String key = ConfigHelper.payuKey;

        String var1 = transactionId; // transaction id
        String response = ""
        Map map = [:]

        String toHash = key + "|" + method + "|" + var1 + "|" + salt;
        String Hashed = Encryption.hashCal(toHash);
        String Poststring = "key=" + key + "&command=" + method + "&hash=" + Hashed + "&var1=" + var1;
        //String Poststring = "key=" + key +  "&command=" + method +  "&hash=" + Hashed + "&var1=" + var1 + "&var2=" + var2 + "&var3=" + var3 ;
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${Poststring}"
        // Create connection

        try {
            URL url = new URL(testUrl);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(Poststring);
            wr.flush();

            conn.connect()
            if (conn.responseCode == 200) {
                response = conn.content.text
                println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${response}"
                try {
                    def jsonSlurper = new JsonSlurper()
                    map = jsonSlurper.parseText(response) as Map
                    println(map)
                } catch (JSONException e) {
                }

            } else {
                println "An error occurred:"
                println connection.responseCode
                println connection.responseMessage
            }
            // Get the response
        } catch (IOException e) {

        }
        return map
    }

    Map nonSeamlessRequest() {
        log.info("non seamless method called")
        String key, txnid, amount, productinfo, firstname, email, salt, surl, furl, curl,mobileNumber
        key = ConfigHelper.payuKey
        salt = ConfigHelper.payuSalt
        amount = "100"
        productinfo = ConfigHelper.payuProductInfo
        firstname = "sanjeev"
        email = "sanjeev.jha@tothenew.com"
        mobileNumber="8802668433"
        Transaction transaction = Transaction.last()
        txnid = "TNX00001"

//format key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT
        surl = grailsLinkGenerator.link(controller: 'payu', action: 'success',absolute: true)
        furl = grailsLinkGenerator.link(controller: 'payu', action: 'failure',absolute: true)
        curl = grailsLinkGenerator.link(controller: 'payu', action: 'cancel',absolute: true)
        String paramSequence = "${key}|${txnid}|${amount}|${productinfo}|${firstname}|${email}|||||||||||${salt}"
        String hash1 = SHA512Encryption.encryptTextInSHA512(paramSequence)
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${hash1}"
        transaction = new Transaction(id: 1, status: "dsf").save(failOnError: true, flush: true)
        log.info("logging request")
        [firstname: firstname, key: key, hash: hash1, txnid: txnid, amount: amount, email: email,
         productInfo: productinfo,surl:surl,furl:furl,curl:curl,mobileNumber:mobileNumber,url:ConfigHelper.payuUrl]
    }
}
