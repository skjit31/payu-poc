package ccavenue.poc

import grails.converters.JSON

class PayuController {

    def payuService

    def seamless() {
        payuService.nonSeamlessRequest()
    }

    def nonSeamless() {
       payuService.nonSeamlessRequest()
    }

    def success() {
        payuService.logRequestData(params)
        List emptyKeys = params.findAll{!it.value}.collect{it.key}
        [emptyKeys:emptyKeys,keys: (params.keySet() - emptyKeys),map:params,responsePage:"Success Page"]
    }

    def failure() {
        payuService.logRequestData(params)
        List emptyKeys = params.findAll{!it.value}.collect{it.key}
        render(view:'success',model:[emptyKeys:emptyKeys,keys: (params.keySet() - emptyKeys),map:params,responsePage:"Failure Page"])
    }

    def cancel() {
        payuService.logRequestData(params)
        List emptyKeys = params.findAll{!it.value}.collect{it.key}
        render(view:'success',model:[emptyKeys:emptyKeys,keys: (params.keySet() - emptyKeys),map:params,responsePage:"Failure Page"])
    }

    def makeApiCall(){
    }

    def verifyPaymentApi(String transactionId){
        Map map = payuService.makePaymentVerificationCall(transactionId)
        render map as JSON
    }
}
