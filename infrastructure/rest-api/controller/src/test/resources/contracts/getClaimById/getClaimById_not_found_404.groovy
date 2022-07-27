package contracts.getClaimById

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get Claim by id - Not found"
    request {
        method GET()
        urlPath "/claims/20000"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 404
    }
}
