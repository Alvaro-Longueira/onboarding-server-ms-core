package contracts.getClaimById

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get Claim by id - Success"
    request {
        method GET()
        urlPath "/claims/10001"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 200
    }
}
