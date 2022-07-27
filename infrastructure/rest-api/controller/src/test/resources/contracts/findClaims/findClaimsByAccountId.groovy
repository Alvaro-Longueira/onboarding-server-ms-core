package contracts.findClaims

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Find Claims - Fetch Claims by 'account_id'"
    request {
        method GET()
        urlPath "/claims?account_id=127757"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 200
    }
}
