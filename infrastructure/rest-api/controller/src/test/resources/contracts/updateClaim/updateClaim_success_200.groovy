package contracts.updateClaim

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim - Success"
    request {
        method PUT()
        urlPath "/claims/10001"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "type": "DEFAULT",
              "event_date": "2021-02-24T12:43:03.340Z",
              "notification_date": "2021-02-24T12:43:03.340Z",
              "description": "Describes something",
              "place_of_event": "Barcelona" 
            }
        ''')
    }
    response {
        status 200
    }
}
