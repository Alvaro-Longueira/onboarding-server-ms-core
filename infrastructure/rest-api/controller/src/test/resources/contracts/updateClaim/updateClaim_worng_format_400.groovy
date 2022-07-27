package contracts.updateClaim

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim - Wrong notification date format"
    request {
        method PUT()
        urlPath "/claims/notFound"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "type": "DEFAULT",
              "event_date": "2021-02-24T12:43:03.340Z",
              "notification_date": "notificationDateBadFormat"
            }
        ''')
    }
    response {
        status 400
    }
}
