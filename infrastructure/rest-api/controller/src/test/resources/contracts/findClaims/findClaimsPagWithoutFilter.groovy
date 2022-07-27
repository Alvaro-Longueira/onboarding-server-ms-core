package contracts.findClaims

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Find Claims without filters"
    request {
        method GET()
        urlPath "/claims?page_number=1&page_size=10"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 200
    }
}
