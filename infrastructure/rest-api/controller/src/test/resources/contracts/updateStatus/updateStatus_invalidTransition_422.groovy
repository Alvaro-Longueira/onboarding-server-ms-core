package contracts.updateStatus

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Update Claim status - Invalid status transition"
    request {
        method PUT()
        urlPath "/claims/10001/status"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "status": "REJECTED"
            }
        ''')
    }
    response {
        status 422
    }
}
