package contracts.updateClaim

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim - Business validation"
    request {
        method PUT()
        urlPath "/claims/10001"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "type": "DEFAULT",
              "event_date": "2121-02-24T12:43:03.340Z",
              "notification_date": "2021-02-24T12:43:03.340Z"
            }
        ''')
    }
    response {
        status 422
    }
}
