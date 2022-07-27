package contracts.updateStatus

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim status - Success"
    request {
        method PUT()
        urlPath "/claims/10001/status"
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
        status 200
    }
}
