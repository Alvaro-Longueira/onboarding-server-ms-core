package contracts.updateStatus

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim status - Wrong input format"
    request {
        method PUT()
        urlPath "/claims/10001/status"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "status": "something else"
            }
        ''')
    }
    response {
        status 400
    }
}
