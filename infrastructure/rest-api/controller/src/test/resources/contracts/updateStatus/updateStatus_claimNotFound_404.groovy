package contracts.updateStatus

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim status - Claim not found"
    request {
        method PUT()
        urlPath "/claims/20000/status"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "status": "UNDER_REVIEW"
            }
        ''')
    }
    response {
        status 404
    }
}
