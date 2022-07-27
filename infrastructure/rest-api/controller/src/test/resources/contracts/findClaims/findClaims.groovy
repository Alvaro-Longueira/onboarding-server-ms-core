package contracts.findClaims

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Find Claims"
    request {
        method GET()
        urlPath "/claims"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 200
    }
}
